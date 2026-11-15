package cl.ucn.app.dto;

import cl.ucn.app.model.ModalidadTutoria;
import cl.ucn.app.model.Tutoria;

import java.time.format.DateTimeFormatter;

public record TutoriaDetalle(Tutoria tutoria, long inscritos) {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public Long id() {
        return tutoria.getId();
    }

    public String asignaturaTexto() {
        return tutoria.getAsignatura().getCodigo() + " - " + tutoria.getAsignatura().getNombre();
    }

    public String tutorNombre() {
        return tutoria.getTutor().getEstudiante().getNombre();
    }

    public String fechaTexto() {
        return tutoria.getInicio().format(FECHA);
    }

    public String horarioTexto() {
        return tutoria.getInicio().format(HORA) + " - " + tutoria.getFin().format(HORA);
    }

    public String modalidadTexto() {
        if (tutoria.getModalidad() == ModalidadTutoria.ONLINE) {
            return "Online";
        }
        return "Presencial (" + tutoria.getSala() + ")";
    }

    public String estadoTexto() {
        return tutoria.getEstado().name();
    }

    public int capacidad() {
        return tutoria.getCapacidad();
    }

    public long cuposDisponibles() {
        return Math.max(0, tutoria.getCapacidad() - inscritos);
    }

    public boolean sinCupos() {
        return cuposDisponibles() == 0;
    }
}
