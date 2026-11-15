package cl.ucn.app.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Pbkdf2PasswordHasherTest {

    // Pocas iteraciones para que las pruebas sean rápidas; el algoritmo es el mismo.
    private final PasswordHasher hasher = new Pbkdf2PasswordHasher(1_000);

    @Test
    void elHashNoContieneLaContrasenaEnTextoPlano() {
        String hash = hasher.hash("Secreta123");
        assertFalse(hash.contains("Secreta123"));
        assertTrue(hasher.esHash(hash));
    }

    @Test
    void verificaLaContrasenaCorrecta() {
        String hash = hasher.hash("Secreta123");
        assertTrue(hasher.verificar("Secreta123", hash));
    }

    @Test
    void rechazaUnaContrasenaIncorrecta() {
        String hash = hasher.hash("Secreta123");
        assertFalse(hasher.verificar("secreta123", hash));
        assertFalse(hasher.verificar("", hash));
    }

    @Test
    void laMismaContrasenaGeneraHashesDistintosPorLaSal() {
        assertNotEquals(hasher.hash("Secreta123"), hasher.hash("Secreta123"));
    }

    @Test
    void noReconoceTextoPlanoNiFormatosInvalidosComoHash() {
        assertFalse(hasher.esHash("admin123"));
        assertFalse(hasher.esHash(null));
        assertFalse(hasher.verificar("admin123", "admin123"));
        assertFalse(hasher.verificar("x", "pbkdf2_sha256$1000$no-es-base64$@@@"));
    }
}
