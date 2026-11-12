@param String title
@param String usuarioNombre
@param String aviso
@param String q
@param java.util.List<cl.ucn.app.model.Usuario> usuarios
@template.layout.main(administrador=true,title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><header class="eventos-cabecera"><div><p class="eyebrow">ADMINISTRACIÓN DEL SISTEMA</p><h1>Usuarios y roles</h1><p>Gestiona el acceso sin eliminar el historial de participación.</p></div><a class="eventos-btn" href="/admin/usuarios/nuevo">Crear usuario</a></header>
<nav class="eventos-nav"><a class="activo" href="/admin/usuarios">Usuarios</a><a href="/admin/roles">Roles y permisos</a><a href="/eventos">Volver a eventos</a></nav>
@if(!aviso.isBlank())<div class="evento-alerta exito" role="status">${aviso}</div>@endif
<form class="eventos-filtros" method="get"><div><label for="q">Buscar por nombre o correo</label><input id="q" name="q" type="search" value="${q}" maxlength="120"></div><button class="eventos-btn" type="submit">Buscar</button><a class="enlace-suave" href="/admin/usuarios">Limpiar</a></form>
<div class="eventos-seccion"><h2>Cuentas del sistema</h2><span>${usuarios.size()} resultados</span></div><div class="eventos-tabla-contenedor"><table class="eventos-tabla"><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>@if(usuarios.isEmpty())<tr><td colspan="5">No se encontraron cuentas.</td></tr>@endif @for(var u:usuarios)<tr><td>${u.getNombre()}</td><td>${u.getCorreo()}</td><td><span class="badge tema">${u.getRol().getNombre()}</span></td><td>${Boolean.TRUE.equals(u.getActivo()) ? "Activo" : "Inactivo"}</td><td><a class="eventos-btn secundario" href="/admin/usuarios/${u.getId()}/editar">Editar cuenta</a></td></tr>@endfor</tbody></table></div></section>`)
