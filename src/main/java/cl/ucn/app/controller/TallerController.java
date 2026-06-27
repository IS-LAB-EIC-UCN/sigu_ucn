package cl.ucn.app.controller;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import io.javalin.http.Context;
import java.time.LocalDate;
import cl.ucn.app.model.Usuario;
import java.util.List;
import java.util.Map;
import cl.ucn.app.service.ITallerService;
import cl.ucn.app.service.TallerServiceImpl;

public class TallerController {

    private static final ITallerService tallerService = new TallerServiceImpl();

    public static void listarTalleres(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String nombreUsuario = ctx.sessionAttribute("usuarioNombre");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        // Obtener el catálogo general de talleres (el servicio ya filtra si es DOCENTE)
        List<Taller> talleres = tallerService.obtenerTalleres(usuarioId, rolUsuario);
        
        // Si es estudiante, traemos adicionalmente su lista de inscripciones personales
        List<Inscripcion> misInscripciones = null;
        if ("ESTUDIANTE".equals(rolUsuario)) {
            misInscripciones = tallerService.obtenerMisInscripciones(usuarioId);
        }

        List<Usuario> docentes = null;
        List<Espacio> espacios = null;
        if ("ADMIN".equals(rolUsuario)) {
            docentes = tallerService.obtenerDocentes();
            espacios = tallerService.obtenerEspacios();
        }

        String successMsg = ctx.queryParam("success");
        String errorMsg = ctx.queryParam("error");

        ctx.render("talleres.jte", Map.of(
                "title", "Talleres - SIGU-UCN",
                "usuarioNombre", nombreUsuario != null ? nombreUsuario : "Demo",
                "rol", rolUsuario,
                "talleres", talleres,
                "misInscripciones", misInscripciones != null ? misInscripciones : List.of(),
                "docentes", docentes != null ? docentes : List.of(),
                "espacios", espacios != null ? espacios : List.of(),
                "successMsg", successMsg != null ? successMsg : "",
                "errorMsg", errorMsg != null ? errorMsg : ""
        ));
    }

    public static void registrarTaller(Context ctx) {
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        if (rolUsuario == null || !rolUsuario.equals("ADMIN")) {
            ctx.status(403).result("Acceso denegado: Solo los Administradores pueden crear talleres.");
            return;
        }

        try {
            String nombre = ctx.formParam("nombre");
            String descripcion = ctx.formParam("descripcion");
            String categoria = ctx.formParam("categoria");
            Integer cupos = Integer.parseInt(ctx.formParam("cupos"));
            LocalDate inicio = LocalDate.parse(ctx.formParam("fechaInicio"));
            LocalDate fin = LocalDate.parse(ctx.formParam("fechaFin"));
            String bloqueParam = ctx.formParam("bloque");
            if (bloqueParam == null || bloqueParam.isBlank()) {
                throw new Exception("El bloque horario es obligatorio.");
            }
            Character bloque = bloqueParam.toUpperCase().charAt(0);
            if (bloque < 'A' || bloque > 'F') {
                throw new Exception("El bloque horario debe ser una letra entre A y F.");
            }
            Long profesorId = Long.parseLong(ctx.formParam("profesorId"));

            String espacioIdStr = ctx.formParam("espacioId");
            Long espacioId = (espacioIdStr != null && !espacioIdStr.isBlank()) ? Long.parseLong(espacioIdStr) : null;

            tallerService.crearTaller(nombre, descripcion, categoria, cupos, inicio, fin, bloque, profesorId, espacioId);
            ctx.redirect("/talleres?success=" + java.net.URLEncoder.encode("Taller creado con exito", java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    public static void inscribirAlumno(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        if (usuarioId == null || !"ESTUDIANTE".equals(rolUsuario)) {
            ctx.status(403).result("Solo los estudiantes pueden inscribirse a talleres.");
            return;
        }

        try {
            Long tallerId = Long.parseLong(ctx.pathParam("id"));
            String mensaje = tallerService.inscribirAlumno(tallerId, usuarioId);
            ctx.redirect("/talleres?success=" + java.net.URLEncoder.encode(mensaje, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    public static void cancelarInscripcion(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        if (usuarioId == null || !"ESTUDIANTE".equals(rolUsuario)) {
            ctx.status(403).result("Solo los estudiantes pueden cancelar inscripciones.");
            return;
        }

        try {
            Long tallerId = Long.parseLong(ctx.pathParam("id"));
            tallerService.cancelarInscripcion(tallerId, usuarioId);
            ctx.redirect("/talleres?success=" + java.net.URLEncoder.encode("Inscripcion cancelada correctamente", java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    public static void listarAlumnosPorTaller(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");
        String nombreUsuario = ctx.sessionAttribute("usuarioNombre");

        if (usuarioId == null || !"DOCENTE".equals(rolUsuario)) {
            ctx.status(403).result("Acceso denegado: Solo los docentes pueden ver los alumnos.");
            return;
        }

        try {
            Long tallerId = Long.parseLong(ctx.pathParam("id"));
            List<Inscripcion> inscripciones = tallerService.obtenerInscripcionesPorTaller(tallerId, usuarioId);
            
            ctx.render("alumnos_taller.jte", Map.of(
                    "title", "Alumnos del Taller",
                    "usuarioNombre", nombreUsuario != null ? nombreUsuario : "Demo",
                    "rol", rolUsuario,
                    "tallerId", tallerId,
                    "inscripciones", inscripciones
            ));
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + e.getMessage());
        }
    }

    public static void descargarCsvAlumnos(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        if (usuarioId == null || !"DOCENTE".equals(rolUsuario)) {
            ctx.status(403).result("Acceso denegado: Solo los docentes pueden descargar la lista de alumnos.");
            return;
        }

        try {
            Long tallerId = Long.parseLong(ctx.pathParam("id"));
            List<Inscripcion> inscripciones = tallerService.obtenerInscripcionesPorTaller(tallerId, usuarioId);

            StringBuilder csv = new StringBuilder();
            csv.append("Nombre Alumno,Correo Institucional,Estado\n");

            for (Inscripcion i : inscripciones) {
                String nombre = i.getUsuario().getNombre().replace(",", " ");
                String correo = i.getUsuario().getCorreo().replace(",", " ");
                String estado = i.getEstado();
                csv.append(nombre).append(",").append(correo).append(",").append(estado).append("\n");
            }

            ctx.header("Content-Type", "text/csv; charset=utf-8");
            ctx.header("Content-Disposition", "attachment; filename=\"alumnos_taller_" + tallerId + ".csv\"");
            ctx.result(csv.toString());
        } catch (Exception e) {
            ctx.status(500).result("Error al generar el archivo CSV: " + e.getMessage());
        }
    }

    public static void mostrarFormularioEdicion(Context ctx) {
        String rolUsuario = ctx.sessionAttribute("usuarioRol");
        String nombreUsuario = ctx.sessionAttribute("usuarioNombre");
        if (!"ADMIN".equals(rolUsuario)) {
            ctx.status(403).result("Solo los administradores pueden editar talleres.");
            return;
        }

        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Taller taller = tallerService.obtenerTallerPorId(id);
            List<Usuario> docentes = tallerService.obtenerDocentes();
            List<Espacio> espacios = tallerService.obtenerEspacios();

            ctx.render("editar_taller.jte", Map.of(
                    "title", "Editar Taller",
                    "taller", taller,
                    "docentes", docentes,
                    "espacios", espacios,
                    "usuarioNombre", nombreUsuario != null ? nombreUsuario : "Admin",
                    "rol", rolUsuario
            ));
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Error", java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    public static void procesarEdicionTaller(Context ctx) {
        String rolUsuario = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(rolUsuario)) {
            ctx.status(403).result("Solo los administradores pueden editar talleres.");
            return;
        }

        Long tallerId = Long.parseLong(ctx.pathParam("id"));

        try {
            String nombre = ctx.formParam("nombre");
            String descripcion = ctx.formParam("descripcion");
            String categoria = ctx.formParam("categoria");
            Integer cupos = Integer.parseInt(ctx.formParam("cupos_totales"));
            LocalDate inicio = LocalDate.parse(ctx.formParam("fecha_inicio"));
            LocalDate fin = LocalDate.parse(ctx.formParam("fecha_fin"));
            Character bloque = ctx.formParam("bloque_horario").charAt(0);
            Long profesorId = Long.parseLong(ctx.formParam("profesor_id"));
            String espacioIdStr = ctx.formParam("espacio_id");
            Long espacioId = (espacioIdStr != null && !espacioIdStr.isEmpty()) ? Long.parseLong(espacioIdStr) : null;

            tallerService.editarTaller(tallerId, nombre, descripcion, categoria, cupos, inicio, fin, bloque, profesorId, espacioId);

            ctx.redirect("/talleres?success=" + java.net.URLEncoder.encode("Taller actualizado exitosamente.", java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error al actualizar el taller";
            ctx.redirect("/talleres/" + tallerId + "/editar?error=" + java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    public static void eliminarTaller(Context ctx) {
        String rolUsuario = ctx.sessionAttribute("usuarioRol");
        if (!"ADMIN".equals(rolUsuario)) {
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode("Acceso denegado. Solo administradores pueden eliminar talleres.", java.nio.charset.StandardCharsets.UTF_8));
            return;
        }

        try {
            Long tallerId = Long.parseLong(ctx.pathParam("id"));
            tallerService.eliminarTaller(tallerId);
            ctx.redirect("/talleres?success=" + java.net.URLEncoder.encode("Taller eliminado con exito", java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Error al eliminar taller";
            ctx.redirect("/talleres?error=" + java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
