package cl.ucn.app.security;

/**
 * Abstracción para cifrar y verificar contraseñas (RNF-01).
 * Los servicios dependen de esta interfaz y no de un algoritmo concreto (DIP),
 * por lo que el algoritmo puede cambiarse sin modificar AuthService.
 */
public interface PasswordHasher {

    /** Genera el hash de una contraseña en texto plano (incluye sal aleatoria). */
    String hash(String passwordPlano);

    /** Verifica si la contraseña en texto plano corresponde al hash almacenado. */
    boolean verificar(String passwordPlano, String hashAlmacenado);

    /** Indica si el valor almacenado fue generado por este hasher. */
    boolean esHash(String valorAlmacenado);
}
