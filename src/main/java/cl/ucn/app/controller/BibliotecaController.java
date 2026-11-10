package cl.ucn.app.controller;

import cl.ucn.app.model.*;
import cl.ucn.app.service.*;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BibliotecaController {

    private static final Map<String, String> MENSAJES_EXITO = Map.of(
            "lector", "Lector registrado correctamente.",
            "libro", "Libro y ejemplares incorporados al catálogo.",
            "prestamo", "Préstamo registrado. Revisa la fecha límite en tus préstamos activos.",
            "devolucion", "Devolución registrada. Si hubo atraso, se generó la multa y se bloqueó al lector.",
            "pago", "Pago de multa registrado. Si no quedan multas pendientes, el lector fue habilitado."
    );

    private final BibliotecaService bibliotecaService;
    private final LectorService lectorService;

    public BibliotecaController(BibliotecaService bibliotecaService, LectorService lectorService) {
        this.bibliotecaService = bibliotecaService;
        this.lectorService = lectorService;
    }

    private void exigirSesion(Context ctx) {
        if (ctx.sessionAttribute("usuarioNombre") == null) {
            throw new AccesoDenegadoException(401, "Debe iniciar sesión para acceder al módulo de Biblioteca.");
        }
    }

    private void exigirRolBibliotecario(Context ctx) {
        exigirSesion(ctx);
        if (!esBibliotecario(ctx)) {
            throw new AccesoDenegadoException(403, "Acceso denegado: operación exclusiva para Bibliotecario.");
        }
    }

    private boolean esBibliotecario(Context ctx) {
        String rol = ctx.sessionAttribute("usuarioRol");
        return "ADMIN".equalsIgnoreCase(rol);
    }

    public void showIndex(Context ctx) {
        if (ctx.sessionAttribute("usuarioNombre") == null) {
            ctx.status(401);
            ctx.redirect("/login");
            return;
        }

        String q = ctx.queryParam("q");
        List<Libro> libros = bibliotecaService.buscarLibros(q);
        Map<String, Object> model = construirModeloBase(ctx, libros, q);
        cargarDatosActorEnModelo(ctx, model);

        ctx.render("biblioteca/index.jte", model);
    }

    private Map<String, Object> construirModeloBase(Context ctx, List<Libro> libros, String q) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Módulo de Biblioteca - SIGU-UCN");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("esBibliotecario", esBibliotecario(ctx));
        model.put("libros", libros);
        model.put("busqueda", q != null ? q : "");
        model.put("mensajeExito", obtenerMensajeExito(ctx.queryParam("success")));
        return model;
    }

    private String obtenerMensajeExito(String clave) {
        return clave == null ? null : MENSAJES_EXITO.get(clave);
    }

    private void cargarDatosActorEnModelo(Context ctx, Map<String, Object> model) {
        if (esBibliotecario(ctx)) {
            model.put("categorias", bibliotecaService.listarCategorias());
            model.put("autores", bibliotecaService.listarAutores());
            model.put("lectoresRegistrados", lectorService.listarTodos());
            return;
        }
        String correo = ctx.sessionAttribute("usuarioCorreo");
        if (correo != null) {
            Optional<Lector> lectorOpt = lectorService.buscarPorCorreo(correo);
            lectorOpt.ifPresent(lector -> {
                model.put("lector", lector);
                model.put("prestamosActivos", bibliotecaService.consultarPrestamosActivos(lector.getId()));
                model.put("multasPendientes", bibliotecaService.consultarMultasPendientes(lector.getId()));
            });
        }
    }

    public void registrarLector(Context ctx) {
        exigirRolBibliotecario(ctx);

        String rut = ctx.formParam("rut");
        String nombre = ctx.formParam("nombre");
        String correo = ctx.formParam("correo");
        TipoLector tipo = parsearTipoLector(ctx.formParam("tipo"));

        lectorService.registrarLector(rut, nombre, correo, tipo);
        ctx.redirect("/biblioteca?success=lector");
    }

    public void registrarLibro(Context ctx) {
        exigirRolBibliotecario(ctx);

        String isbn = ctx.formParam("isbn");
        String titulo = ctx.formParam("titulo");
        String editorial = ctx.formParam("editorial");
        int anio = parsearLongObligatorio(ctx.formParam("anio"), "anio").intValue();
        Long categoriaId = parsearLongObligatorio(ctx.formParam("categoriaId"), "categoriaId");
        Long autorId = parsearLongObligatorio(ctx.formParam("autorId"), "autorId");
        int cantidad = parsearLongObligatorio(ctx.formParam("cantidad"), "cantidad").intValue();
        String ubicacion = ctx.formParam("ubicacion");

        bibliotecaService.registrarLibroConEjemplares(
                isbn, titulo, editorial, anio, categoriaId, Collections.singletonList(autorId), cantidad, ubicacion);
        ctx.redirect("/biblioteca?success=libro");
    }

    public void solicitarPrestamo(Context ctx) {
        exigirSesion(ctx);

        String correo = ctx.sessionAttribute("usuarioCorreo");
        Lector lector = lectorService.buscarPorCorreo(correo)
                .orElseThrow(() -> new DatosInvalidosException("usuario", "El usuario actual no está registrado como lector."));

        Long ejemplarId = parsearLongObligatorio(ctx.formParam("ejemplarId"), "ejemplarId");
        bibliotecaService.solicitarPrestamo(lector.getId(), ejemplarId, LocalDate.now());
        ctx.redirect("/biblioteca?success=prestamo");
    }

    public void registrarDevolucion(Context ctx) {
        exigirRolBibliotecario(ctx);

        String codigoInventario = ctx.formParam("codigoInventario");
        if (codigoInventario == null || codigoInventario.isBlank()) {
            throw new DatosInvalidosException("codigoInventario", "Debe ingresar el código de inventario.");
        }
        boolean danado = "true".equalsIgnoreCase(ctx.formParam("danado"));
        String correo = ctx.sessionAttribute("usuarioCorreo");
        Long bibliotecarioId = bibliotecaService.buscarBibliotecarioPorCorreo(correo)
                .map(Bibliotecario::getId)
                .orElse(null);

        bibliotecaService.registrarDevolucion(codigoInventario.trim(), LocalDate.now(), danado, bibliotecarioId);
        ctx.redirect("/biblioteca?success=devolucion");
    }

    public void registrarPagoMulta(Context ctx) {
        exigirRolBibliotecario(ctx);

        Long multaId = parsearLongObligatorio(ctx.formParam("multaId"), "multaId");
        bibliotecaService.registrarPagoMulta(multaId, LocalDate.now());
        ctx.redirect("/biblioteca?success=pago");
    }

    public void manejarReglaNegocio(ReglaNegocioException e, Context ctx) {
        responderError(ctx, 409, "Operación rechazada", "Regla de negocio: " + e.getCodigoRegla(),
                e.getMessage(), "[" + e.getCodigoRegla() + "] " + e.getMessage());
    }

    public void manejarDatosInvalidos(DatosInvalidosException e, Context ctx) {
        responderError(ctx, 400, "Datos inválidos", "Campo inválido: " + e.getCampo(),
                e.getMessage(), "Error en campo [" + e.getCampo() + "]: " + e.getMessage());
    }

    public void manejarAccesoDenegado(AccesoDenegadoException e, Context ctx) {
        String titulo = e.getStatusCode() == 401 ? "Sesión requerida" : "Acceso denegado";
        responderError(ctx, e.getStatusCode(), titulo, null, e.getMessage(), e.getMessage());
    }

    private void responderError(Context ctx, int status, String titulo, String detalle,
                                String mensaje, String textoPlano) {
        ctx.status(status);
        if (!aceptaHtml(ctx)) {
            ctx.result(textoPlano);
            return;
        }
        Map<String, Object> model = new HashMap<>();
        model.put("title", titulo + " - SIGU-UCN");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("status", status);
        model.put("titulo", titulo);
        model.put("detalle", detalle);
        model.put("mensaje", mensaje);
        ctx.render("biblioteca/error.jte", model);
    }

    private boolean aceptaHtml(Context ctx) {
        String accept = ctx.header("Accept");
        return accept != null && accept.contains("text/html");
    }

    private TipoLector parsearTipoLector(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException("tipo", "El tipo de lector es obligatorio.");
        }
        try {
            return TipoLector.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatosInvalidosException("tipo", "Tipo de lector inválido.");
        }
    }

    private Long parsearLongObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(nombreCampo, "El campo " + nombreCampo + " es obligatorio.");
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(nombreCampo, "El campo " + nombreCampo + " debe ser numérico.");
        }
    }
}
