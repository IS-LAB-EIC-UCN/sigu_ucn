package cl.ucn.app.service.soporte;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.Ticket;
import cl.ucn.app.model.soporte.TecnicoDisponible;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.soporte.JpaPermisosSoporteRepository;
import cl.ucn.app.repository.soporte.JpaTicketRepository;
import cl.ucn.app.repository.soporte.PermisosSoporteRepository;
import cl.ucn.app.repository.soporte.TicketRepository;

import java.util.Objects;
import java.util.List;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisosSoporteRepository permisosRepository;

    public TicketService() {
        this(new JpaTicketRepository(), new UsuarioRepository(), new JpaPermisosSoporteRepository());
    }

    public TicketService(TicketRepository ticketRepository, UsuarioRepository usuarioRepository, PermisosSoporteRepository permisos) {
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
        this.permisosRepository = Objects.requireNonNull(permisos);
    }

    public Ticket crearTicket(String titulo, String descripcion, Long usuarioId) {
        validarDatos(titulo, descripcion);

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Usuario no válido");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId);

        if(usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo.");
        }

        Ticket ticket = new Ticket(titulo.trim(), descripcion.trim(), usuario);
        return ticketRepository.guardar(ticket);
    }

    private void validarDatos(String titulo, String descripcion) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del ticket es obligatorio.");
        }

        if (titulo.trim().length() > 150) {
            throw new IllegalArgumentException("El título no puede superar los 150 caracteres.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del ticket es obligatoria.");
        }
    }

    public boolean esAdministrador(Long usuarioId) {
        return usuarioId != null && permisosRepository.esAdministrador(usuarioId);
    }

    public boolean esTecnico(Long usuarioId) {
        return usuarioId != null && permisosRepository.esTecnico(usuarioId);
    }

    public List<TecnicoDisponible> listarTecnicos(Long administradorId) {
        exigirAdministrador(administradorId);
        return permisosRepository.listarTecnicosActivos();
    }

    public List<Ticket> listarPendientes(Long administradorId) {
        exigirAdministrador(administradorId);
        return ticketRepository.listarPendientes();
    }

    public Ticket asignarTecnico(Long ticketId, Long tecnicoId, Long administradorId) {
        exigirAdministrador(administradorId);
        validarId(ticketId, "Ticket");
        validarId(tecnicoId, "Técnico");

        if (!permisosRepository.esTecnico(tecnicoId)) {
            throw new IllegalArgumentException("El usuario seleccionado no es un técnico activo.");
        }

        return ticketRepository.asignarTecnico(ticketId, tecnicoId, administradorId);
    }

    public List<Ticket> listarAsignados(Long tecnicoId) {
        exigirTecnico(tecnicoId);
        return ticketRepository.listarAsignados(tecnicoId);
    }

    public Ticket iniciarResolucion(Long ticketId, Long tecnicoId) {
        exigirTecnico(tecnicoId);
        validarId(ticketId, "Ticket");
        return ticketRepository.iniciarResolucion(ticketId, tecnicoId);
    }

    public List<Ticket> listarEnResolucion(Long tecnicoId) {
        exigirTecnico(tecnicoId);
        return ticketRepository.listarEnResolucion(tecnicoId);
    }

    public Ticket cerrarTicket(Long ticketId, Long tecnicoId, String resolucion) {
        exigirTecnico(tecnicoId);
        validarId(ticketId, "Ticket");

        if (resolucion == null || resolucion.isBlank()) {
            throw new IllegalArgumentException("La resolución es obligatoria.");
        }

        return ticketRepository.cerrarTicket(ticketId, tecnicoId, resolucion.trim());
    }

    private void exigirAdministrador(Long usuarioId) {
        if (!esAdministrador(usuarioId)) {
            throw new SecurityException("Solo un administrador puede asignar tickets.");
        }
    }

    private void exigirTecnico(Long usuarioId) {
        if (usuarioId == null || !permisosRepository.esTecnico(usuarioId)) {
            throw new SecurityException("Solo un técnico puede modificar el estado del ticket.");
        }
    }

    private void validarId(Long id, String tipo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(tipo + " no válido.");
        }
    }
}
