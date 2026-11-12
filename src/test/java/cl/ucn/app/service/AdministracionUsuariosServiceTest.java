package cl.ucn.app.service;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AdministracionUsuariosServiceTest {
    @Test void actualizacionSinClaveConservaHashYNormalizaCorreo() {
        UsuarioRepository repo=mock(UsuarioRepository.class);
        new AdministracionUsuariosService(repo).guardar(5L," Docente "," DOCENTE@SIGU.CL ","",2L,true,1L);
        verify(repo).administrar(5L,"Docente","docente@sigu.cl",null,2L,true,1L);
    }
    @Test void rechazaCorreoYClaveCortaAntesDeGuardar() {
        UsuarioRepository repo=mock(UsuarioRepository.class);var service=new AdministracionUsuariosService(repo);
        assertThrows(IllegalArgumentException.class,()->service.guardar(null,"Usuario","sin-correo","Clave123",2L,true,1L));
        assertThrows(IllegalArgumentException.class,()->service.guardar(null,"Usuario","u@example.test","abc",2L,true,1L));
        verifyNoInteractions(repo);
    }
}
