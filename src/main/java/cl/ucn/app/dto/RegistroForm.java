package cl.ucn.app.dto;

public record RegistroForm(
        String nombre,
        String correo,
        String carrera,
        String semestre,
        String password,
        String confirmacion
) {
}
