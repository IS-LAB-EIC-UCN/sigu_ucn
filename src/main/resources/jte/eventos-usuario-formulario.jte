@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param Long id
@param java.util.Map<String,String> datos
@param java.util.List<cl.ucn.app.model.Rol> roles
@template.layout.main(title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><a class="enlace-suave" href="/eventos/usuarios">← Volver a usuarios</a><header class="eventos-cabecera"><div><p class="eyebrow">ACCESO AL SISTEMA</p><h1>${id==null ? "Crear usuario" : "Editar usuario"}</h1><p>El rol define las acciones permitidas. Desactivar conserva su historial.</p></div></header>
@if(!error.isBlank())<div class="evento-alerta error" role="alert">${error}</div>@endif
<form class="eventos-formulario" method="post" action="${id==null ? "/eventos/usuarios/crear" : "/eventos/usuarios/"+id+"/editar"}"><input type="hidden" name="csrfToken" value="${csrfToken}"><div class="eventos-form-grid">
<div class="eventos-campo"><label for="nombre">Nombre *</label><input id="nombre" name="nombre" maxlength="100" value="${datos.getOrDefault("nombre","")}" required></div>
<div class="eventos-campo"><label for="correo">Correo *</label><input id="correo" name="correo" type="email" maxlength="120" value="${datos.getOrDefault("correo","")}" required></div>
<div class="eventos-campo"><label for="rolId">Rol *</label><select id="rolId" name="rolId" required><option value="">Selecciona un rol</option>@for(var r:roles)<option value="${r.getId()}" selected="${r.getId().toString().equals(datos.get("rolId"))}">${r.getNombre()}</option>@endfor</select></div>
<div class="eventos-campo"><label for="activo">Estado *</label><select id="activo" name="activo"><option value="true" selected="${!"false".equals(datos.get("activo"))}">Activo</option><option value="false" selected="${"false".equals(datos.get("activo"))}">Inactivo</option></select></div>
<div class="eventos-campo eventos-campo-completo"><label for="clave">${id==null ? "Contraseña inicial *" : "Nueva contraseña (opcional)"}</label><input id="clave" name="clave" type="password" minlength="8" maxlength="1024" autocomplete="new-password" required="${id==null}"><small class="ayuda">Mínimo 8 caracteres. Al editar, déjala vacía para conservar la actual. No puedes desactivar tu propia cuenta administradora ni quitarte su rol.</small></div>
</div><div class="eventos-acciones"><button class="eventos-btn" type="submit">Guardar cuenta</button><a class="eventos-btn secundario" href="/eventos/usuarios">Cancelar</a></div></form></section>`)
