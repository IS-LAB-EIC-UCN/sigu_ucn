/**
 * parking-panel.js  —  Grupo 9 · SIGU-UCN
 * Gestión de estado local para el Panel Unificado de Estacionamientos.
 *
 * MÁQUINA DE ESTADO
 * ─────────────────
 *   estado "menu"   → muestra el grid de cards, oculta todas las vistas.
 *   estado "view"   → oculta el grid, muestra la vista elegida.
 *
 * GARANTÍAS DE NO PERSISTENCIA PREMATURA
 * ───────────────────────────────────────
 *  • Ninguna transición de estado dispara una petición HTTP.
 *  • Al volver al menú desde un formulario, este se limpia (reset).
 *  • La persistencia ocurre SOLO al hacer submit explícito del form.
 *  • Un refresh o retroceso en el navegador devuelve al menú raíz
 *    (hash "#menu" o sin hash), nunca a mitad de un formulario con datos.
 *
 * PERSISTENCIA DE VISTA EN URL
 * ─────────────────────────────
 *  La vista activa se refleja en el hash: /estacionamientos/panel#view-historial
 *  Esto permite que el backend redirija al panel con la vista correcta activa
 *  (ej. después de filtrar historial, redirigir a /panel#view-historial).
 */

$(function () {

    /* ═══════════════════════════════════════════════════
       REFERENCIAS DOM
       ═══════════════════════════════════════════════════ */
    const $menu  = $('#ep-menu');
    const $views = $('.ep-view');          // todos los div.ep-view

    /* ═══════════════════════════════════════════════════
       ESTADO — solo dos valores posibles: 'menu' | 'view'
       ═══════════════════════════════════════════════════ */
    let currentView = null;   // id de la vista activa, ej. 'view-historial'

    /* ═══════════════════════════════════════════════════
       FUNCIONES CENTRALES
       ═══════════════════════════════════════════════════ */

    /**
     * showMenu()
     * Transición → estado "menu".
     * Oculta la vista activa, limpia formularios, muestra el grid.
     */
    function showMenu() {

        // 1. Ocultar todas las vistas funcionales
        $views.attr('hidden', '').removeClass('ep-view--visible');

        // 2. Limpiar formularios de la vista que se abandona para
        //    evitar que datos residuales se envíen en sesiones futuras.
        if (currentView) {
            $('#' + currentView).find('form').each(function () {
                this.reset();
                // Quita también clases de error de validación
                $(this).find('.input-error').removeClass('input-error');
            });
        }

        // 3. Mostrar menú principal
        $menu.removeAttr('hidden');

        // 4. Actualizar estado interno y URL
        currentView = null;
        updateHash('menu');

        // 5. Scroll al inicio del módulo (UX en pantallas pequeñas)
        scrollToShell();
    }

    /**
     * showView(viewId)
     * Transición → estado "view".
     * @param {string} viewId — id del elemento div.ep-view (sin #)
     */
    function showView(viewId) {

        const $target = $('#' + viewId);
        if ($target.length === 0) {
            console.warn('[parking-panel] Vista no encontrada:', viewId);
            return;
        }

        // 1. Ocultar menú
        $menu.attr('hidden', '');

        // 2. Ocultar otras vistas (por si se navega entre vistas directamente)
        $views.attr('hidden', '').removeClass('ep-view--visible');

        // 3. Mostrar la vista seleccionada
        $target.removeAttr('hidden').addClass('ep-view--visible');

        // 4. Actualizar estado interno y URL
        currentView = viewId;
        updateHash(viewId);

        // 5. Scroll
        scrollToShell();
    }

    /**
     * updateHash(id)
     * Actualiza el hash de la URL sin forzar un salto de página.
     * Usa replaceState para no ensuciar el historial del navegador
     * con cada transición interna (solo pushState en acciones explícitas).
     */
    function updateHash(id) {
        if (history.replaceState) {
            const newHash = id === 'menu' ? ' ' : '#' + id;
            history.replaceState({ epView: id }, '', newHash.trim() || window.location.pathname);
        }
    }

    /**
     * scrollToShell()
     * Desplaza suavemente al inicio del módulo para que el usuario
     * siempre vea la cabecera al cambiar de vista (útil en mobile).
     */
    function scrollToShell() {
        const $shell = $('.ep-shell');
        if ($shell.length) {
            $('html, body').animate({ scrollTop: $shell.offset().top - 16 }, 200);
        }
    }

    /* ═══════════════════════════════════════════════════
       INICIALIZACIÓN — restaurar vista desde hash
       ═══════════════════════════════════════════════════ */
    (function init() {
        const hash = window.location.hash.replace('#', '');   // ej. 'view-historial'

        if (hash && hash !== 'menu' && $('#' + hash).length > 0) {
            // El backend redirigió con un hash válido: activar esa vista
            showView(hash);
        } else {
            // Estado por defecto: menú de cards
            showMenu();
        }
    }());

    /* ═══════════════════════════════════════════════════
       EVENTO: clic en una card del menú
       ═══════════════════════════════════════════════════ */
    $menu.on('click', '.ep-card[data-view]', function () {
        const viewId = $(this).data('view');
        showView(viewId);
    });

    /* ═══════════════════════════════════════════════════
       EVENTO: clic en cualquier botón "Volver al menú"
       ═══════════════════════════════════════════════════ */
    $(document).on('click', '[data-back]', function () {
        showMenu();
    });

    /* ═══════════════════════════════════════════════════
       SOPORTE: botón Atrás / Adelante del navegador
       ═══════════════════════════════════════════════════ */
    $(window).on('popstate', function (e) {
        const state = e.originalEvent.state;
        const viewId = state && state.epView ? state.epView : 'menu';

        if (viewId === 'menu') {
            showMenu();
        } else {
            showView(viewId);
        }
    });

    /* ═══════════════════════════════════════════════════
       SEGURIDAD: confirmación antes de navegar a eliminar
       Evita disparos accidentales de GET destructivos.
       ═══════════════════════════════════════════════════ */
    $(document).on('click', 'a[data-confirm]', function (e) {
        const msg = $(this).data('confirm') || '¿Confirmar esta acción?';
        if (!window.confirm(msg)) {
            e.preventDefault();
        }
    });

    /* ═══════════════════════════════════════════════════
       VALIDACIÓN: formulario de registro de usuario
       Solo previene submit con campos vacíos; no hace HTTP.
       ═══════════════════════════════════════════════════ */
    $(document).on('submit', '#formRegistrarUsuario', function () {
        const campos = [
            '#reg-nombre', '#reg-correo', '#reg-password',
            '#reg-patente', '#reg-marca', '#reg-modelo'
        ];
        let valido = true;

        $.each(campos, function (_, sel) {
            const $el = $(sel);
            if ($el.val().trim() === '') {
                $el.addClass('input-error');
                valido = false;
            } else {
                $el.removeClass('input-error');
            }
        });

        if (!valido) {
            alert('Por favor, complete todos los campos obligatorios antes de registrar.');
            return false;
        }
        // Si es válido, el form hace su POST normal → backend persiste → redirect
    });

    /* ═══════════════════════════════════════════════════
       UX: quitar clase de error al corregir el campo
       ═══════════════════════════════════════════════════ */
    $(document).on('input', '.input-error', function () {
        $(this).removeClass('input-error');
    });

});   // end $(function)
