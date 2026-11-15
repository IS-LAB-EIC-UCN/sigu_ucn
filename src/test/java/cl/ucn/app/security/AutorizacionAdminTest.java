package cl.ucn.app.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutorizacionAdminTest {

    @Test
    void adminEsTrue() {
        assertTrue(AutorizacionAdmin.esAdmin("ADMIN"));
    }

    @Test
    void docenteEstudianteyNullYMinusculasSonFalse() {
        assertFalse(AutorizacionAdmin.esAdmin("DOCENTE"));
        assertFalse(AutorizacionAdmin.esAdmin("ESTUDIANTE"));
        assertFalse(AutorizacionAdmin.esAdmin(null));
        assertFalse(AutorizacionAdmin.esAdmin("admin"));
    }
}
