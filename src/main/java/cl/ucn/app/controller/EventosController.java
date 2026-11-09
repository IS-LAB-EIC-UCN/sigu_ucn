
package cl.ucn.app.controller;

import cl.ucn.app.model.Evento;
import cl.ucn.app.repository.EventoRepository;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventosController {

    private final EventoRepository eventoRepository = new EventoRepository();

    public void mostrarEventos(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        List<Evento> eventos = eventoRepository.findAll();

        ctx.render("eventos.jte", Map.of(
                "title", "Gestión de Eventos",
                "usuarioNombre", usuarioNombre,
                "eventos", eventos));
    }

    public void mostrarFormulario(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioNombre == null || usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        ctx.render("eventos-nuevo.jte", Map.of(
                "title", "Crear evento académico",
                "usuarioNombre", usuarioNombre));
    }

    public void crearEvento(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        try {
            String titulo = ctx.formParam("titulo");
            String descripcion = ctx.formParam("descripcion");
            String fechaTexto = ctx.formParam("fecha");
            String inicioTexto = ctx.formParam("horaInicio");
            String finTexto = ctx.formParam("horaFin");
            String lugar = ctx.formParam("lugar");
            String cupoTexto = ctx.formParam("cupoMaximo");

            if (titulo == null || titulo.isBlank()
                    || lugar == null || lugar.isBlank()) {
                ctx.status(400).result("Título y lugar son obligatorios");
                return;
            }

            LocalDate fecha = LocalDate.parse(fechaTexto);
            LocalTime inicio = LocalTime.parse(inicioTexto);
            LocalTime fin = LocalTime.parse(finTexto);
            int cupo = Integer.parseInt(cupoTexto);

            if (fecha.isBefore(LocalDate.now())
                    || !fin.isAfter(inicio)
                    || cupo <= 0
                    || titulo.length() > 150
                    || lugar.length() > 150) {
                ctx.status(400).result("Datos del evento inválidos");
                return;
            }

            UsuarioRepository usuarioRepository = new UsuarioRepository();

            Usuario organizador = usuarioRepository.findById(usuarioId);

            if (organizador == null) {
                ctx.status(403).result("Usuario no encontrado");
                return;
            }

            Evento evento = new Evento();

            evento.setTitulo(titulo.trim());
            evento.setDescripcion(descripcion);
            evento.setFecha(fecha);
            evento.setHoraInicio(inicio);
            evento.setHoraFin(fin);
            evento.setLugar(lugar.trim());
            evento.setCupoMaximo(cupo);
            evento.setEstado("BORRADOR");
            evento.setOrganizador(organizador);

            eventoRepository.save(evento);

            ctx.redirect("/eventos");

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Revisa los datos del formulario");
        }
    }

}
