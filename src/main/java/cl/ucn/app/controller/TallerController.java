package cl.ucn.app.controller;

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
        if ("ADMIN".equals(rolUsuario)) {
            docentes = tallerService.obtenerDocentes();
        }

        ctx.render("talleres.jte", Map.of(
                "title", "Talleres - SIGU-UCN",
                "usuarioNombre", nombreUsuario != null ? nombreUsuario : "Demo",
                "rol", rolUsuario,
                "talleres", talleres,
                "misInscripciones", misInscripciones != null ? misInscripciones : List.of(),
                "docentes", docentes != null ? docentes : List.of()
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
            Integer cupos = Integer.parseInt(ctx.formParam("cupos"));
            LocalDate inicio = LocalDate.parse(ctx.formParam("fechaInicio"));
            LocalDate fin = LocalDate.parse(ctx.formParam("fechaFin"));
            Character bloque = ctx.formParam("bloque").charAt(0);
            Long profesorId = Long.parseLong(ctx.formParam("profesorId"));

            tallerService.crearTaller(nombre, descripcion, cupos, inicio, fin, bloque, profesorId);
            ctx.redirect("/talleres?success=Taller+creado");
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + e.getMessage());
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
            ctx.redirect("/talleres?success=" + mensaje);
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + e.getMessage());
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
            ctx.redirect("/talleres?success=Inscripcion+cancelada+correctamente");
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + e.getMessage());
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
                    "inscripciones", inscripciones
            ));
        } catch (Exception e) {
            ctx.redirect("/talleres?error=" + e.getMessage());
        }
    }
}
