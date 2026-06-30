package cl.ucn.app.service;

import cl.ucn.app.model.Expositor;
import cl.ucn.app.repository.ExpositorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// 3 TESTS PARA EXPOSITORES
@ExtendWith(MockitoExtension.class)
class ExpositorServiceTest {

    @Mock
    private ExpositorRepository expositorRepository;

    private ExpositorService expositorService;

    @BeforeEach
    void setUp() {
        expositorService = new ExpositorService(expositorRepository);
    }

    @Test
    void registrarExpositorValidoDebeGuardar() {
        Expositor expositor = new Expositor("Ana Pérez", "ana@ucn.cl", "UCN");

        when(expositorRepository.findByEmail("ana@ucn.cl")).thenReturn(null);
        when(expositorRepository.save(expositor)).thenReturn(expositor);

        Expositor resultado = expositorService.registrar(expositor);

        assertSame(expositor, resultado);
        verify(expositorRepository).save(expositor);
    }

    @Test
    void registrarExpositorSinNombreDebeLanzarExcepcion() {
        Expositor expositor = new Expositor("", "ana@ucn.cl", "UCN");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> expositorService.registrar(expositor)
        );

        assertEquals("El nombre del expositor es obligatorio.", ex.getMessage());
        verify(expositorRepository, never()).save(any());
    }

    @Test
    void registrarExpositorConEmailDuplicadoDebeLanzarExcepcion() {
        Expositor expositor = new Expositor("Ana Pérez", "ana@ucn.cl", "UCN");
        Expositor existente = new Expositor("Otra Ana", "ana@ucn.cl", "UCN");

        when(expositorRepository.findByEmail("ana@ucn.cl")).thenReturn(existente);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> expositorService.registrar(expositor)
        );

        assertTrue(ex.getMessage().contains("Ya existe un expositor registrado"));
        verify(expositorRepository, never()).save(any());
    }
}