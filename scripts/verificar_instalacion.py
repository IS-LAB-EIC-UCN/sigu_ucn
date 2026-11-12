"""Clona el commit local y verifica instalacion completa en PostgreSQL temporal, sin tocar la base habitual."""
from pathlib import Path
import datetime
import http.cookiejar
import json
import os
import re
import shutil
import socket
import subprocess
import time
import urllib.parse
import urllib.request
import uuid

ROOT=Path(__file__).resolve().parents[1]

def main():
    nombre="sigu-ucn-verificacion-"+uuid.uuid4().hex[:12]
    carpeta=ROOT/"target"/nombre;carpeta.mkdir(parents=True)
    clon=carpeta/"fuente"
    creado=False;proceso=None
    maven=shutil.which("mvn.cmd") or shutil.which("mvn")
    if not maven:raise RuntimeError("Se requiere Maven en PATH")
    informe={"fecha":datetime.datetime.now().astimezone().isoformat(),"contenedor_temporal":nombre}
    def ejecutar(args,**kwargs):return subprocess.check_output(args,cwd=ROOT,stderr=subprocess.STDOUT,**kwargs)
    def esperar(condicion,segundos,mensaje):
        fin=time.monotonic()+segundos
        while time.monotonic()<fin:
            if condicion():return
            time.sleep(.25)
        raise RuntimeError(mensaje)
    try:
        print("Clonando solo archivos confirmados...",flush=True)
        ejecutar(["git","clone","--local","--no-hardlinks","--single-branch","--branch","grupo-6",str(ROOT),str(clon)])
        informe["commit"]=ejecutar(["git","-C",str(clon),"rev-parse","HEAD"]).decode().strip()
        ejecutar(["docker","run","-d","--name",nombre,"-p","127.0.0.1::5432","-e","POSTGRES_DB=sigu_ucn","-e","POSTGRES_USER=postgres","-e","POSTGRES_PASSWORD=postgres","--mount","type=bind,source="+str((clon/"db/init").resolve())+",target=/docker-entrypoint-initdb.d,readonly","postgres:16"])
        creado=True
        def listo():
            logs=ejecutar(["docker","logs",nombre]).decode(errors="replace")
            return "PostgreSQL init process complete" in logs and subprocess.run(["docker","exec",nombre,"pg_isready","-U","postgres","-d","sigu_ucn"],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL).returncode==0
        esperar(listo,90,"PostgreSQL no termino de inicializarse")
        puerto=int(ejecutar(["docker","port",nombre,"5432/tcp"]).decode().strip().rsplit(":",1)[1])
        opciones=["-Ddb.url=jdbc:postgresql://127.0.0.1:"+str(puerto)+"/sigu_ucn","-Ddb.user=postgres","-Ddb.password=postgres"]
        print("Compilando y ejecutando todas las pruebas contra la base nueva...",flush=True)
        with (carpeta/"maven.log").open("wb") as log:
            subprocess.run([maven,"-B","test","-Deventos.integration=true",*opciones],cwd=clon,stdout=log,stderr=subprocess.STDOUT,check=True,timeout=240)
        with socket.socket() as s:s.bind(("127.0.0.1",0));web=s.getsockname()[1]
        logapp=(carpeta/"aplicacion.log").open("wb")
        proceso=subprocess.Popen([maven,"-B","exec:java","-Dexec.mainClass=cl.ucn.app.main.Main","-Dserver.port="+str(web),*opciones],cwd=clon,stdout=logapp,stderr=subprocess.STDOUT,creationflags=subprocess.CREATE_NO_WINDOW if os.name=="nt" else 0)
        base="http://127.0.0.1:"+str(web)
        def activa():
            if proceso.poll() is not None:raise RuntimeError("La aplicacion termino antes de iniciar")
            try:
                with urllib.request.urlopen(base+"/login",timeout=2) as r:return r.status==200
            except OSError:return False
        esperar(activa,90,"La aplicacion nueva no inicio")
        verificaciones=[]
        for correo,clave in [("admin@sigu.cl","admin123"),("docente@sigu.cl","docente123"),("estudiante@sigu.cl","estudiante123")]:
            c=urllib.request.build_opener(urllib.request.ProxyHandler({}),urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
            with c.open(base+"/login",urllib.parse.urlencode({"correo":correo,"password":clave}).encode(),timeout=15) as r:
                assert r.geturl().endswith("/home");r.read()
            with c.open(base+"/eventos",timeout=15) as r:assert r.status==200;r.read()
            # La administración global de usuarios no pertenece al módulo 6.
            try:c.open(base+"/admin/usuarios",timeout=15);raise AssertionError("El panel global de usuarios sigue expuesto")
            except urllib.error.HTTPError as ex:assert ex.code==404
            if not correo.startswith("docente"):
                try:c.open(base+"/eventos/nuevo",timeout=15);raise AssertionError("Solo el docente debe crear eventos")
                except urllib.error.HTTPError as ex:assert ex.code==403
            else:
                with c.open(base+"/eventos/nuevo",timeout=15) as r:assert r.status==200;r.read()
            verificaciones.append(correo)
        datos=ejecutar(["docker","exec",nombre,"psql","-U","postgres","-d","sigu_ucn","-At","-c","SELECT (SELECT count(*) FROM eventos)||'|'||(SELECT count(*) FROM usuarios)||'|'||(SELECT count(*) FROM usuarios WHERE password LIKE 'pbkdf2-sha256$%');"]).decode().strip()
        assert datos=="8|3|3",datos
        import xml.etree.ElementTree as ET
        tests=sum(int(ET.parse(f).getroot().attrib.get("tests",0)) for f in (clon/"target/surefire-reports").glob("TEST-*.xml"))
        informe.update({"correcta":True,"pruebas":tests,"eventos_iniciales":8,"usuarios":3,"passwords_protegidos":3,"cuentas_verificadas":verificaciones,"fuente":str(clon),"base_aislada":True})
        print("Instalacion limpia verificada:",tests,"pruebas, tres cuentas y ocho eventos iniciales",flush=True)
    finally:
        if proceso is not None and proceso.poll() is None:
            if os.name=="nt":subprocess.run(["taskkill","/PID",str(proceso.pid),"/T","/F"],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
            else:proceso.terminate();proceso.wait(timeout=15)
        if creado:
            assert re.fullmatch(r"sigu-ucn-verificacion-[0-9a-f]{12}",nombre)
            subprocess.run(["docker","rm","-f","-v",nombre],check=True,stdout=subprocess.DEVNULL)
        destino=ROOT/"target/instalacion-limpia.json";destino.write_text(json.dumps(informe,ensure_ascii=False,indent=2),encoding="utf-8")
        print("Logs e informe:",carpeta,flush=True)
    return 0

if __name__=="__main__":raise SystemExit(main())
