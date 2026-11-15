package cl.ucn.app.dto;

import java.time.LocalDate;

public record TutoriaFiltro(Long asignaturaId, Long tutorId, LocalDate fecha) {

    public static TutoriaFiltro vacio() {
        return new TutoriaFiltro(null, null, null);
    }

    public static TutoriaFiltro desde(String asignaturaId, String tutorId, String fecha) {
        return new TutoriaFiltro(leerLong(asignaturaId), leerLong(tutorId), leerFecha(fecha));
    }

    public String fechaTexto() {
        return fecha == null ? "" : fecha.toString();
    }

    private static Long leerLong(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate leerFecha(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : LocalDate.parse(valor.trim());
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }
}
