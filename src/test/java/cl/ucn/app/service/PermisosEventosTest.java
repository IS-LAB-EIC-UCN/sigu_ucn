package cl.ucn.app.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PermisosEventosTest {
    @Test void soloEstudiantesPuedenInscribirse() {
        assertTrue(PermisosEventos.participa("ESTUDIANTE"));
        assertFalse(PermisosEventos.participa("DOCENTE"));
        assertFalse(PermisosEventos.participa("ADMIN"));
        assertFalse(PermisosEventos.participa("OTRO"));
        assertFalse(PermisosEventos.participa(null));
    }
    @Test void estudianteNoOrganizaNiAdministraAunqueSeaCreadorAntiguo() {
        assertFalse(PermisosEventos.organiza("ESTUDIANTE"));
        assertFalse(PermisosEventos.administra("ESTUDIANTE",1L,1L));
    }
    @Test void asistenciaOficialRequiereSerOrganizadorInclusoSiEsAdmin() {
        assertTrue(PermisosEventos.registraAsistencia("ADMIN",1L,1L));
        assertTrue(PermisosEventos.registraAsistencia("DOCENTE",2L,2L));
        assertFalse(PermisosEventos.registraAsistencia("ADMIN",1L,2L));
        assertFalse(PermisosEventos.registraAsistencia("ESTUDIANTE",1L,1L));
    }
    @Test void docenteSoloAdministraSusEventos() {
        assertTrue(PermisosEventos.organiza("DOCENTE"));
        assertTrue(PermisosEventos.administra("DOCENTE",1L,1L));
        assertFalse(PermisosEventos.administra("DOCENTE",1L,2L));
    }
    @Test void adminSupervisaYRechazaAnonimosYRolesDesconocidos() {
        assertFalse(PermisosEventos.administra("ADMIN",1L,2L));
        assertTrue(PermisosEventos.administra("ADMIN",1L,1L));
        assertTrue(PermisosEventos.consultaInscritos("ADMIN",1L,2L,"PUBLICADO"));
        assertFalse(PermisosEventos.consultaInscritos("ADMIN",1L,2L,"BORRADOR"));
        assertFalse(PermisosEventos.consultaInscritos("ESTUDIANTE",1L,2L,"PUBLICADO"));
        assertFalse(PermisosEventos.administra("ADMIN",null,2L));
        assertFalse(PermisosEventos.organiza(null));assertFalse(PermisosEventos.organiza("OTRO"));
    }
}
