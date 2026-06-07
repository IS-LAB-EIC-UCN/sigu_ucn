package cl.ucn.app.controller;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Evento;
import cl.ucn.app.model.Expositor;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ExpositorRepository;
import cl.ucn.app.service.EventoService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventoController {

    private final EventoService eventoService;
    private final EspacioRepository espacioRepository;
    private final ExpositorRepository expositorRepository;

    public EventoController() {
        this.eventoService = new EventoService();
        this.espacioRepository = new EspacioRepository();
        this.expositorRepository = new ExpositorRepository();
    }

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        List<Evento> eventos = eventoService.listarTodos();

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Eventos - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("eventos", eventos);
        model.put("error", ctx.queryParam("error"));
        model.put("success", ctx.queryParam("success"));

        ctx.render("eventos/lista.jte", model);
    }

    public void mostrarFormulario(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        List<Espacio> espacios = espacioRepository.findDisponibles();
        List<Expositor> expositores = expositorRepository.findAll();

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Registrar Evento - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("espacios", espacios);
        model.put("expositores", expositores);
        model.put("error", "");

        ctx.render("eventos/formulario.jte", model);
    }

    public void registrar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            String titulo = ctx.formParam("titulo");
            String descripcion = ctx.formParam("descripcion");
            String fechaStr = ctx.formParam("fecha");
            String horaInicioStr = ctx.formParam("horaInicio");
            String horaFinStr = ctx.formParam("horaFin");
            String tematica = ctx.formParam("tematica");
            String capacidadStr = ctx.formParam("capacidad");
            String espacioIdStr = ctx.formParam("espacioId");
            List<String> expositorIdStrs = ctx.formParams("expositorIds");

            if (titulo == null || titulo.isBlank()) {
                throw new IllegalArgumentException("El título es obligatorio.");
            }
            if (fechaStr == null || horaInicioStr == null || horaFinStr == null) {
                throw new IllegalArgumentException("Fecha y horas son obligatorias.");
            }
            if (capacidadStr == null || espacioIdStr == null) {
                throw new IllegalArgumentException("Capacidad y espacio son obligatorios.");
            }

            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);
            Integer capacidad = Integer.parseInt(capacidadStr);
            Long espacioId = Long.parseLong(espacioIdStr);

            Espacio espacio = espacioRepository.findById(espacioId);
            if (espacio == null) {
                throw new IllegalArgumentException("El espacio seleccionado no existe.");
            }

            Evento evento = new Evento(titulo, descripcion, fecha, horaInicio, horaFin, tematica, capacidad, espacio);

            if (expositorIdStrs != null && !expositorIdStrs.isEmpty()) {
                List<Expositor> expositores = new ArrayList<>();
                for (String idStr : expositorIdStrs) {
                    if (idStr == null || idStr.isBlank()) continue;
                    Long expositorId = Long.parseLong(idStr);
                    Expositor expositor = expositorRepository.findById(expositorId);
                    if (expositor == null) {
                        throw new IllegalArgumentException("El expositor con ID " + expositorId + " no existe.");
                    }
                    expositores.add(expositor);
                }
                evento.setExpositores(expositores);
            }

            eventoService.registrar(evento);

            ctx.redirect("/eventos?success=Evento registrado exitosamente.");

        } catch (IllegalArgumentException e) {
            List<Espacio> espacios = espacioRepository.findDisponibles();
            List<Expositor> expositores = expositorRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Registrar Evento - SIGU-UCN");
            model.put("usuarioNombre", usuarioNombre);
            model.put("espacios", espacios);
            model.put("expositores", expositores);
            model.put("error", e.getMessage());
            ctx.status(400);
            ctx.render("eventos/formulario.jte", model);
        } catch (Exception e) {
            List<Espacio> espacios = espacioRepository.findDisponibles();
            List<Expositor> expositores = expositorRepository.findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Registrar Evento - SIGU-UCN");
            model.put("usuarioNombre", usuarioNombre);
            model.put("espacios", espacios);
            model.put("expositores", expositores);
            model.put("error", "Error inesperado: " + e.getMessage());
            ctx.status(500);
            ctx.render("eventos/formulario.jte", model);
        }
    }
}
