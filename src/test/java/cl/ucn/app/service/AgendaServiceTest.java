package cl.ucn.app.service;

import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private InscripcionTutoriaRepository inscripcionRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Test
    void agendaEstudianteConsultaSoloElEstudianteAutenticado() {
        Estudiante estudiante = estudiante(7L);
        List<InscripcionTutoria> reservas = List.of(new InscripcionTutoria());
        when(estudianteRepository.findByUsuarioId(12L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository.findByEstudianteId(7L)).thenReturn(reservas);

        List<InscripcionTutoria> resultado = new AgendaService(
                estudianteRepository, inscripcionRepository, tutoriaRepository)
                .agendaEstudiante(12L);

        assertSame(reservas, resultado);
        verify(inscripcionRepository).findByEstudianteId(7L);
        verify(inscripcionRepository, never()).findByEstudianteId(99L);
    }

    @Test
    void agendaTutorConsultaElTutorAutenticado() {
        List<Tutoria> tutorias = List.of(new Tutoria());
        when(tutoriaRepository.findByTutorId(5L)).thenReturn(tutorias);

        assertSame(tutorias, new AgendaService(
                estudianteRepository, inscripcionRepository, tutoriaRepository)
                .agendaTutor(5L));
    }

    private Estudiante estudiante(Long id) {
        Estudiante estudiante = new Estudiante(
                "estudiante@alumnos.ucn.cl", "Estudiante", "Ingeniería", 1);
        try {
            Field field = Estudiante.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(estudiante, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return estudiante;
    }
}
