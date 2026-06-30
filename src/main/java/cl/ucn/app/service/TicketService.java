package cl.ucn.app.service;

import cl.ucn.app.model.Comentario;
import cl.ucn.app.model.Ticket;
import java.util.List;

/**
 * Contrato de negocio del módulo de soporte.
 * Interfaces específicas (I de SOLID) en lugar de un único servicio genérico.
 */
public interface TicketService {

    /** Crea un ticket nuevo con estado ABIERTO y fecha de creación automática. */
    Ticket crearTicket(String titulo, String descripcion, String prioridad,
                       Long categoriaId, Long solicitanteId);

    /** Asigna un técnico a un ticket; el ticket no puede estar CERRADO. */
    Ticket asignarTecnico(Long ticketId, Long tecnicoId);

    /** Cambia el estado; aplica reglas de negocio (ticket cerrado no modificable). */
    Ticket cambiarEstado(Long ticketId, String nuevoEstado, Long usuarioId);

    /** Cierra el ticket; exige que haya resolución registrada. */
    Ticket cerrarTicket(Long ticketId, String resolucion, Long tecnicoId);

    /** Agrega un comentario; el ticket no puede estar CERRADO. */
    Comentario agregarComentario(Long ticketId, String contenido, Long autorId);

    Ticket obtenerTicket(Long ticketId);

    List<Ticket> listarTickets();

    List<Ticket> filtrarTickets(String estado, String prioridad, Long tecnicoId);

    List<Comentario> obtenerComentarios(Long ticketId);
}
