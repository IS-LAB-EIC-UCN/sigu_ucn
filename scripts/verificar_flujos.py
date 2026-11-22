"""Ensayo HTTP de entrega. Lo invoca verificar_instalacion sobre su base temporal."""
import datetime
import http.cookiejar
import re
import urllib.error
import urllib.parse
import urllib.request
import uuid


def verificar(base):
    def cliente(correo, clave):
        c = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        pedir(c, "/login", {"correo": correo, "password": clave})
        return c

    def pedir(c, ruta, datos=None, estado=200):
        datos = None if datos is None else urllib.parse.urlencode(datos, doseq=True).encode()
        try:
            with c.open(base + ruta, datos, timeout=20) as r:
                codigo, texto = r.status, r.read().decode()
        except urllib.error.HTTPError as ex:
            codigo, texto = ex.code, ex.read().decode()
        assert codigo == estado, (ruta, codigo, estado)
        return texto

    def token(html):
        return re.search(r'name="csrfToken" value="([^"]+)"', html)[1]

    def opcion(html, nombre):
        select = re.search(r'<select[^>]*name="' + nombre + r'".*?</select>', html, re.S)[0]
        return re.search(r'<option value="(\d+)"', select)[1]

    admin = cliente("admin@sigu.cl", "admin123")
    docente = cliente("docente@sigu.cl", "docente123")
    estudiante = cliente("estudiante@sigu.cl", "estudiante123")
    for c in [docente, estudiante]:
        pedir(c, "/eventos/usuarios", estado=403)
        pedir(c, "/eventos/roles", estado=403)
    pedir(estudiante, "/eventos/catalogos", estado=403)
    pedir(admin, "/eventos/nuevo", estado=403)
    pedir(estudiante, "/eventos/nuevo", estado=403)
    catalogo = pedir(admin, "/eventos/catalogos")
    assert '<h2>Expositores' not in catalogo and 'vista=mios' not in catalogo
    assert 'Salas y categorías' in catalogo
    assert 'Registrar expositor' in pedir(docente, "/eventos/catalogos")

    # Programación, publicación y edición de un evento temporal.
    formulario = pedir(docente, "/eventos/nuevo")
    titulo = "Ensayo de entrega " + uuid.uuid4().hex[:8]
    datos = dict(csrfToken=token(formulario), titulo=titulo, descripcion="Ensayo automático en base aislada",
                 categoriaId=opcion(formulario, "categoriaId"), espacioId=opcion(formulario, "espacioId"),
                 expositorIds=re.search(r'name="expositorIds" value="(\d+)"', formulario)[1],
                 fecha=str(datetime.date.today() + datetime.timedelta(days=60)),
                 horaInicio="10:00", horaFin="11:00", cupoMaximo="2")
    lista = pedir(docente, "/eventos/crear", datos)
    evento = re.search(r'href="/eventos/(\d+)/detalle">' + re.escape(titulo), lista)[1]
    ruta = "/eventos/" + evento
    datos["descripcion"] = "Descripción editada durante el ensayo"
    pedir(docente, ruta + "/editar", datos)
    pedir(docente, ruta + "/publicar", {"csrfToken": datos["csrfToken"]})
    assert "Descripción editada" in pedir(estudiante, ruta + "/detalle")
    pedir(admin, ruta + "/editar", estado=403)

    # Inscripción, duplicado, supervisión, asistencia y cancelación propia.
    t_estudiante = token(pedir(estudiante, ruta + "/detalle"))
    pedir(estudiante, ruta + "/inscribir", {"csrfToken": t_estudiante})
    pedir(estudiante, ruta + "/inscribir", {"csrfToken": t_estudiante}, estado=409)
    inscritos = pedir(docente, ruta + "/inscritos")
    asistencia = re.search(r'action="([^"]+/asistencia)"', inscritos)[1]
    pedir(docente, asistencia, {"csrfToken": token(inscritos), "asistencia": "true"})
    assert "Asistió" in pedir(docente, ruta + "/inscritos")
    assert 'action="' + asistencia + '"' not in pedir(admin, ruta + "/inscritos")
    pedir(admin, asistencia, {"csrfToken": token(catalogo), "asistencia": "true"}, estado=403)
    pedir(estudiante, ruta + "/cancelar-inscripcion", {"csrfToken": t_estudiante})
    assert "CANCELADO" in pedir(docente, ruta + "/inscritos")
    pedir(docente, ruta + "/cancelar", {"csrfToken": datos["csrfToken"]})
    assert "CANCELADO" in pedir(docente, ruta + "/detalle")

    # Cuenta temporal sobre las tablas compartidas; rol y estado de una sesión abierta.
    formulario = pedir(admin, "/eventos/usuarios/nuevo")
    roles = dict((nombre, id_) for id_, nombre in re.findall(r'<option value="(\d+)"[^>]*>(ADMIN|DOCENTE|ESTUDIANTE)</option>', formulario))
    correo = uuid.uuid4().hex + "@example.test"
    cuenta = dict(csrfToken=token(formulario), nombre="Cuenta de ensayo", correo=correo,
                  clave="Ensayo12345", rolId=roles["ESTUDIANTE"], activo="true")
    lista = pedir(admin, "/eventos/usuarios/crear", cuenta)
    fila = re.search(r'<tr><td>Cuenta de ensayo</td><td>' + re.escape(correo) + r'.*?</tr>', lista, re.S)[0]
    editar = re.search(r'href="([^"]+/editar)"', fila)[1]
    nueva = cliente(correo, "Ensayo12345")
    pedir(nueva, "/eventos/nuevo", estado=403)
    cuenta.update(clave="", rolId=roles["DOCENTE"])
    pedir(admin, editar, cuenta)
    pedir(nueva, "/eventos/nuevo")
    cuenta["activo"] = "false"
    pedir(admin, editar, cuenta)
    assert 'action="/login"' in pedir(nueva, "/eventos")
    return ["roles y catálogos", "crear, editar, publicar y cancelar evento", "inscripción y rechazo de duplicados",
            "supervisión sin edición", "lista y asistencia del docente", "cancelación de inscripción",
            "crear cuenta y asignar rol", "actualizar permisos de sesión", "desactivar cuenta y cerrar sesión"]
