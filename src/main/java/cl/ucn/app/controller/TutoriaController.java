package cl.ucn.app.controller;

import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.model.TutoriaReserva;
import cl.ucn.app.service.TutoriaService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutoriaController {

    private final TutoriaService tutoriaService = new TutoriaService();

    public void listar(Context ctx) {
        String rol = ctx.sessionAttribute("usuarioRol");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        String tutorParam = ctx.queryParam("tutorId");
        String asignaturaParam = ctx.queryParam("asignaturaId");
        String fechaParam = ctx.queryParam("fecha");

        Long filtroTutorId = null;
        Long filtroAsignaturaId = null;
        LocalDate filtroFecha = null;

        if (tutorParam != null && !tutorParam.isBlank()) {
            filtroTutorId = Long.parseLong(tutorParam);
        }

        if (asignaturaParam != null && !asignaturaParam.isBlank()) {
            filtroAsignaturaId = Long.parseLong(asignaturaParam);
        }

        if (fechaParam != null && !fechaParam.isBlank()) {
            filtroFecha = LocalDate.parse(fechaParam);
        }

        List<Tutoria> tutorias;
        List<TutoriaReserva> reservas;

        if ("ADMIN".equals(rol)) {
            tutorias = tutoriaService.filtrarTutorias(filtroTutorId, filtroAsignaturaId, filtroFecha, null);
            reservas = tutoriaService.listarReservas();

        } else if ("TUTOR".equals(rol)) {
            tutorias = tutoriaService.filtrarTutorias(usuarioId, filtroAsignaturaId, filtroFecha, null);
            reservas = tutoriaService.listarReservasPorTutor(usuarioId);

        } else if ("ESTUDIANTE".equals(rol)) {
            tutorias = tutoriaService.filtrarTutorias(filtroTutorId, filtroAsignaturaId, filtroFecha, "DISPONIBLE");
            reservas = tutoriaService.listarHistorialEstudiante(usuarioId);

        } else {
            tutorias = List.of();
            reservas = List.of();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Tutorías");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", rol);
        model.put("tutorias", tutorias);
        model.put("reservas", reservas);
        model.put("asignaturas", tutoriaService.listarAsignaturas());
        model.put("tutores", tutoriaService.listarTutores());

        model.put("filtroTutorId", tutorParam == null ? "" : tutorParam);
        model.put("filtroAsignaturaId", asignaturaParam == null ? "" : asignaturaParam);
        model.put("filtroFecha", fechaParam == null ? "" : fechaParam);

        ctx.render("lista.jte", model);
    }

    public void crear(Context ctx) {
        String rol = ctx.sessionAttribute("usuarioRol");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        List<Asignatura> asignaturas = tutoriaService.listarAsignaturas();

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Crear Tutoría");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", rol);
        model.put("asignaturas", asignaturas);

        ctx.render("crear.jte", model);
    }

    public void guardar(Context ctx) {
        try {
            String rol = ctx.sessionAttribute("usuarioRol");
            Long tutorId = ctx.sessionAttribute("usuarioId");

            if (!"TUTOR".equals(rol)) {
                ctx.status(403).result("Solo los tutores pueden publicar disponibilidad.");
                return;
            }

            Long asignaturaId = Long.parseLong(ctx.formParam("asignaturaId"));
            LocalDate fecha = LocalDate.parse(ctx.formParam("fecha"));
            LocalTime horaInicio = LocalTime.parse(ctx.formParam("horaInicio"));
            LocalTime horaFin = LocalTime.parse(ctx.formParam("horaFin"));

            tutoriaService.publicarDisponibilidad(tutorId, asignaturaId, fecha, horaInicio, horaFin);

            ctx.redirect("/tutorias");

        } catch (Exception e) {
            ctx.result("Error al crear tutoría: " + e.getMessage());
        }
    }

    public void reservar(Context ctx) {
        try {
            String rol = ctx.sessionAttribute("usuarioRol");
            Long estudianteId = ctx.sessionAttribute("usuarioId");

            if (!"ESTUDIANTE".equals(rol)) {
                ctx.status(403).result("Solo los estudiantes pueden reservar tutorías.");
                return;
            }

            Long tutoriaId = Long.parseLong(ctx.formParam("tutoriaId"));

            tutoriaService.reservarTutoria(tutoriaId, estudianteId);

            ctx.redirect("/tutorias");

        } catch (Exception e) {
            ctx.result("Error al reservar tutoría: " + e.getMessage());
        }
    }

    public void cancelar(Context ctx) {
        try {
            String rol = ctx.sessionAttribute("usuarioRol");

            if (!"ADMIN".equals(rol) && !"TUTOR".equals(rol)) {
                ctx.status(403).result("Solo administradores o tutores pueden cancelar tutorías.");
                return;
            }

            Long tutoriaId = Long.parseLong(ctx.formParam("tutoriaId"));

            tutoriaService.cancelarTutoria(tutoriaId);

            ctx.redirect("/tutorias");

        } catch (Exception e) {
            ctx.result("Error al cancelar tutoría: " + e.getMessage());
        }
    }

    public void registrarAsistencia(Context ctx) {
        try {
            String rol = ctx.sessionAttribute("usuarioRol");

            if (!"TUTOR".equals(rol)) {
                ctx.status(403).result("Solo los tutores pueden registrar asistencia.");
                return;
            }

            Long reservaId = Long.parseLong(ctx.formParam("reservaId"));
            boolean asistio = Boolean.parseBoolean(ctx.formParam("asistio"));

            tutoriaService.registrarAsistencia(reservaId, asistio);

            ctx.redirect("/tutorias");

        } catch (Exception e) {
            ctx.result("Error al registrar asistencia: " + e.getMessage());
        }
    }

    public void crearAsignatura(Context ctx) {
        try {
            String rol = ctx.sessionAttribute("usuarioRol");

            if (!"ADMIN".equals(rol)) {
                ctx.status(403).result("Solo el administrador puede crear asignaturas.");
                return;
            }

            String codigo = ctx.formParam("codigo");
            String nombre = ctx.formParam("nombre");

            tutoriaService.crearAsignatura(codigo, nombre);

            ctx.redirect("/tutorias");

        } catch (Exception e) {
            ctx.result("Error al crear asignatura: " + e.getMessage());
        }
    }
}