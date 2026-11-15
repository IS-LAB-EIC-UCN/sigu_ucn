package cl.ucn.app.dto;

/**
 * Datos ingresados en el formulario de registro (RF-01).
 * Las contraseñas nunca se devuelven a la vista.
 */
public record RegistroForm(String nombre, String correo, String password,
                           String confirmacion, String rol) {

    /** Formulario vacío para mostrar la pantalla por primera vez. */
    public static RegistroForm vacio() {
        return new RegistroForm("", "", "", "", "ESTUDIANTE");
    }

    public String nombreOVacio() {
        return nombre == null ? "" : nombre;
    }

    public String correoOVacio() {
        return correo == null ? "" : correo;
    }

    public String rolOVacio() {
        return rol == null ? "" : rol;
    }
}
