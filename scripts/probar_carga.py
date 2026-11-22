"""Prueba local reproducible: sesiones independientes y lecturas simultaneas, sin modificar eventos."""
import argparse
import concurrent.futures as futures
import datetime
import http.cookiejar
import json
import os
from pathlib import Path
import platform
import threading
import time
import urllib.parse
import urllib.request


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base",default="http://127.0.0.1:7000")
    parser.add_argument("--clientes",type=int,default=100)
    parser.add_argument("--rondas",type=int,default=3)
    parser.add_argument("--salida",default="target/carga_eventos.json")
    args=parser.parse_args()
    if not 1<=args.clientes<=100 or not 1<=args.rondas<=10: parser.error("Usa de 1 a 100 clientes y de 1 a 10 rondas")
    password=os.environ.get("SIGU_TEST_PASSWORD")
    if not password: parser.error("Define SIGU_TEST_PASSWORD con la clave de una cuenta de prueba")
    correo=os.environ.get("SIGU_TEST_CORREO","estudiante@sigu.cl")
    base=args.base.rstrip("/")
    rutas=["/eventos","/eventos?vista=agenda","/eventos?vista=mios"]
    def sesion(_):
        cliente=urllib.request.build_opener(urllib.request.ProxyHandler({}),urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        datos=urllib.parse.urlencode({"correo":correo,"password":password}).encode()
        with cliente.open(base+"/login",datos,timeout=30) as respuesta:
            respuesta.read()
            if not respuesta.geturl().endswith("/home"): raise RuntimeError("La cuenta de prueba no pudo iniciar sesion")
        return cliente
    print("Autenticando sesiones independientes; el login queda fuera de la medicion...",flush=True)
    with futures.ThreadPoolExecutor(max_workers=10) as pool: clientes=list(pool.map(sesion,range(args.clientes)))
    for ruta in rutas:
        with clientes[0].open(base+ruta,timeout=30) as respuesta: respuesta.read()
    muestras=[]
    for ronda in range(args.rondas):
        salida=threading.Barrier(args.clientes+1,timeout=30)
        def consultar(indice):
            ruta=rutas[(indice+ronda)%len(rutas)]
            salida.wait();inicio=time.perf_counter()
            try:
                with clientes[indice].open(base+ruta,timeout=10) as respuesta:
                    contenido=respuesta.read().decode("utf-8")
                    ok=respuesta.status==200 and "/login" not in respuesta.geturl() and "eventos-panel" in contenido
                    estado=respuesta.status
            except Exception as error: ok=False;estado=type(error).__name__
            return {"ronda":ronda+1,"ruta":ruta,"segundos":round(time.perf_counter()-inicio,6),"correcta":ok,"estado":estado}
        with futures.ThreadPoolExecutor(max_workers=args.clientes) as pool:
            trabajos=[pool.submit(consultar,i) for i in range(args.clientes)]
            salida.wait();muestras.extend(trabajo.result(timeout=20) for trabajo in trabajos)
        print("Ronda",ronda+1,"completada",flush=True)
    latencias=sorted(m["segundos"] for m in muestras)
    informe={"fecha":datetime.datetime.now().astimezone().isoformat(),"entorno":platform.platform(),"sesiones_independientes":args.clientes,
        "identidad":"Una cuenta de prueba, con cookies distintas por sesion","peticiones":len(muestras),
        "errores":sum(not m["correcta"] for m in muestras),"max_segundos":max(latencias),
        "p95_segundos":latencias[min(len(latencias)-1,int(len(latencias)*0.95))],
        "cumple_menos_de_2s":all(m["correcta"] and m["segundos"]<2 for m in muestras),"muestras":muestras}
    destino=Path(args.salida);destino.parent.mkdir(parents=True,exist_ok=True)
    destino.write_text(json.dumps(informe,ensure_ascii=False,indent=2),encoding="utf-8")
    print(json.dumps({k:v for k,v in informe.items() if k!="muestras"},ensure_ascii=False,indent=2),flush=True)
    return 0 if informe["cumple_menos_de_2s"] else 1

if __name__=="__main__":raise SystemExit(main())
