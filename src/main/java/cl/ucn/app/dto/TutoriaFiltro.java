package cl.ucn.app.dto;

import java.time.LocalDate;

public record TutoriaFiltro(Long tutorId, Long asignaturaId, LocalDate fecha) {

    public String fechaTexto() {
        return fecha == null ? "" : fecha.toString();
    }
}
