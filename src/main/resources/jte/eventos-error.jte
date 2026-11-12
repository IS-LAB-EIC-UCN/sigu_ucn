@param boolean admin = false
@param String title
@param String usuarioNombre
@param String csrfToken
@param String error
@param String aviso
@template.layout.main(administrador=admin,title=title,usuarioNombre=usuarioNombre,content=@`
<section class="eventos-panel"><header class="eventos-cabecera"><div><p class="eyebrow">GESTIÓN DE EVENTOS</p><h1>Revisa la operación</h1></div></header><div class="evento-alerta error" role="alert">${error}</div><a class="eventos-btn" href="/eventos">Volver a eventos</a></section>`)
