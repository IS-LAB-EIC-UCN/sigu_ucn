package cl.ucn.app.dto;

import cl.ucn.app.model.Asignatura;

public record AsignaturaForm(String codigo, String nombre, String carrera, String semestre) {

    public static AsignaturaForm vacio() {
        return new AsignaturaForm("", "", "", "");
    }

    public static AsignaturaForm desde(Asignatura asignatura) {
        return new AsignaturaForm(
                asignatura.getCodigo(),
                asignatura.getNombre(),
                asignatura.getCarrera(),
                String.valueOf(asignatura.getSemestre())
        );
    }
}
