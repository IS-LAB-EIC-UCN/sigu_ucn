package cl.ucn.app.service;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.model.Rol;
import cl.ucn.app.repository.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private RegistroRepository repository;

    private RegistroService service;

    @BeforeEach
    void setUp() {
        service = new RegistroService(repository);
    }

    @Test
    void rechazaCorreoDuplicado() {
        when(repository.existeUsuarioConCorreo("nuevo@alumnos.ucn.cl")).thenReturn(true);

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> service.registrar(formulario()));

        assertEquals("correo", error.getCampo());
        verify(repository, never()).guardar(any(), any());
    }

    @Test
    void rechazaContrasenaDebil() {
        RegistroForm form = new RegistroForm(
                "Nuevo Estudiante", "nuevo@alumnos.ucn.cl", "Ingeniería", "1", "corta", "corta");

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> service.registrar(form));

        assertEquals("password", error.getCampo());
        verifyNoInteractions(repository);
    }

    @Test
    void registraUsuarioYEstudianteConRolEstudiante() {
        when(repository.buscarRol("ESTUDIANTE")).thenReturn(new Rol("ESTUDIANTE"));

        service.registrar(formulario());

        verify(repository).guardar(argThat(usuario ->
                        usuario.getRol().getNombre().equals("ESTUDIANTE")
                                && usuario.getCorreo().equals("nuevo@alumnos.ucn.cl")),
                argThat(estudiante ->
                        estudiante.getCorreo().equals("nuevo@alumnos.ucn.cl")
                                && estudiante.getSemestre() == 1));
    }

    private RegistroForm formulario() {
        return new RegistroForm(
                "Nuevo Estudiante", "nuevo@alumnos.ucn.cl", "Ingeniería", "1",
                "password1", "password1");
    }
}
