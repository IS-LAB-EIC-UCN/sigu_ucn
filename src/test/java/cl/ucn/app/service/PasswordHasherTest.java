package cl.ucn.app.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PasswordHasherTest {
    @Test void generaSalDistintaYVerificaSinNormalizarClave() {
        String uno=PasswordHasher.generar("Clave de prueba"),dos=PasswordHasher.generar("Clave de prueba");
        assertNotEquals(uno,dos);assertTrue(PasswordHasher.esHash(uno));assertTrue(PasswordHasher.verificar("Clave de prueba",uno));
        assertFalse(PasswordHasher.verificar("otra",uno));assertFalse(PasswordHasher.verificar(" Clave de prueba ",uno));
    }
    @Test void rechazaTextoPlanoHashesDanadosYParametrosExcesivos() {
        assertFalse(PasswordHasher.verificar("admin123","admin123"));
        assertFalse(PasswordHasher.verificar("clave","pbkdf2-sha256$999999999$AA==$AA=="));
        assertFalse(PasswordHasher.verificar("clave","pbkdf2-sha256$600000$AA==$AA=="));
        assertFalse(PasswordHasher.verificar("clave",null));assertThrows(IllegalArgumentException.class,()->PasswordHasher.generar(" "));
    }
}
