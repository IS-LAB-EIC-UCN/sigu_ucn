package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RolesSoporteTest {

    @Test
    void esTecnico_usuarioActivoConRolTecnico_retornaTrue() {
        Usuario tecnico = new Usuario("Tecnico", "t@sigu.cl", "clave", true, new Rol(RolesSoporte.ROL_TECNICO));

        assertTrue(RolesSoporte.esTecnico(tecnico));
        assertFalse(RolesSoporte.esAdministrador(tecnico));
    }

    @Test
    void esTecnico_usuarioInactivo_retornaFalse() {
        Usuario tecnico = new Usuario("Tecnico", "t@sigu.cl", "clave", false, new Rol(RolesSoporte.ROL_TECNICO));

        assertFalse(RolesSoporte.esTecnico(tecnico));
    }
}
