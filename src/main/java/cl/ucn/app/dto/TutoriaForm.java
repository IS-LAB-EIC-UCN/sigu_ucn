package cl.ucn.app.dto;

public record TutoriaForm(String asignaturaId, String fecha, String horaInicio, String horaFin,
                          String modalidad, String sala, String capacidad) {

    public static TutoriaForm vacio() {
        return new TutoriaForm("", "", "", "", "PRESENCIAL", "", "");
    }

    public boolean esAsignatura(Long id) {
        return asignaturaId != null && asignaturaId.equals(String.valueOf(id));
    }

    public boolean esModalidad(String valor) {
        return valor.equals(modalidad);
    }
}
