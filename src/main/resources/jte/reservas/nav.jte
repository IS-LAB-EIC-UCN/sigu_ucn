@param boolean esAdmin
@param String activo

<%-- Navegación interna del módulo de reservas (Grupo 1).
     El enlace al panel solo se muestra a ADMIN; el backend igual valida el rol en cada ruta. --%>
<nav class="nav-reservas" style="display:flex; flex-wrap:wrap; gap:8px 18px; margin:8px 0 16px;">
    <a href="/reservas" style="${"salas".equals(activo) ? "font-weight:bold;" : ""}">Salas</a>
    <a href="/reservas/disponibilidad" style="${"disponibilidad".equals(activo) ? "font-weight:bold;" : ""}">Disponibilidad</a>
    <a href="/reservas/mis-reservas" style="${"mis-reservas".equals(activo) ? "font-weight:bold;" : ""}">Mis reservas</a>
    @if(esAdmin)
        <a href="/reservas/admin/pendientes" style="${"admin".equals(activo) ? "font-weight:bold;" : ""}">Panel de administración</a>
    @endif
</nav>
