package cl.ucn.app.dto;

import java.util.List;

public record TutorForm(String correo, List<String> asignaturaIds) {

    public TutorForm {
        if (asignaturaIds == null) {
            asignaturaIds = List.of();
        }
    }

    public static TutorForm vacio() {
        return new TutorForm("", List.of());
    }

    public boolean incluye(Long asignaturaId) {
        return asignaturaIds.contains(String.valueOf(asignaturaId));
    }
}
