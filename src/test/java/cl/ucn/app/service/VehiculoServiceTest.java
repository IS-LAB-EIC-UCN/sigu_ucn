package cl.ucn.app.service;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.VehiculoRepository;
import jakarta.persistence.PersistenceException;

class VehiculoServiceTest {

    private VehiculoRepository vehiculoRepository;
    private UsuarioRepository usuarioRepository;
    private VehiculoService service;
    private Usuario propietario;

    @BeforeEach
    void setUp() {
        vehiculoRepository = mock(VehiculoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new VehiculoService(vehiculoRepository, usuarioRepository);
        propietario = new Usuario();
    }

    @Test
    void normalizaYGuardaElVehiculoConSuPropietario() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);

        Vehiculo vehiculo = service.registrarVehiculo(" \tab12345678\n ", 7L);

        assertEquals("AB12345678", vehiculo.getPatente());
        assertSame(propietario, vehiculo.getPropietario());
        verify(vehiculoRepository).existsByPatente("AB12345678");
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    void normalizaSinDependerDelIdiomaDelServidor() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("IZ12", service.registrarVehiculo(" iz12 ", 7L).getPatente());
        } finally {
            Locale.setDefault(original);
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    void rechazaPatentesAusentesSinAccederALosRepositorios(String patente) {
        assertThrows(IllegalArgumentException.class, () -> service.registrarVehiculo(patente, 7L));

        verifyNoInteractions(vehiculoRepository, usuarioRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABCDEFGHIJK", "  abcdefghijk  ", "ßßßßßß"})
    void rechazaPatentesQueSuperanDiezCaracteresTrasNormalizar(String patente) {
        assertThrows(IllegalArgumentException.class, () -> service.registrarVehiculo(patente, 7L));

        verifyNoInteractions(vehiculoRepository, usuarioRepository);
    }

    @Test
    void conservaEspaciosInterioresSinImponerUnFormatoDePatente() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);

        assertEquals("AB 12", service.registrarVehiculo(" ab 12 ", 7L).getPatente());
    }

    @Test
    void rechazaUnPropietarioNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.registrarVehiculo("AB1234", null));

        verifyNoInteractions(vehiculoRepository, usuarioRepository);
    }

    @Test
    void rechazaUnUsuarioInexistenteSinGuardar() {
        assertThrows(IllegalArgumentException.class, () -> service.registrarVehiculo("AB1234", 99L));

        verify(usuarioRepository).findById(99L);
        verifyNoInteractions(vehiculoRepository);
    }

    @Test
    void rechazaUnaPatenteDuplicadaNormalizadaSinGuardar() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);
        when(vehiculoRepository.existsByPatente("AB1234")).thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.registrarVehiculo(" ab1234 ", 7L));

        assertEquals("La patente ya está registrada.", error.getMessage());
        verify(vehiculoRepository, never()).save(any());
    }

    @Test
    void permiteVariosVehiculosParaElMismoUsuario() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);

        Vehiculo primero = service.registrarVehiculo("AB1234", 7L);
        Vehiculo segundo = service.registrarVehiculo("CD5678", 7L);

        assertNotSame(primero, segundo);
        assertSame(propietario, primero.getPropietario());
        assertSame(propietario, segundo.getPropietario());
        verify(vehiculoRepository).save(primero);
        verify(vehiculoRepository).save(segundo);
    }

    @Test
    void propagaLosErroresDePersistenciaSinAnunciarUnRegistroExitoso() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);
        PersistenceException failure = new PersistenceException("Conexión interrumpida");
        doThrow(failure).when(vehiculoRepository).save(any());

        assertSame(failure, assertThrows(PersistenceException.class,
                () -> service.registrarVehiculo("AB1234", 7L)));
    }

    @Test
    void noGuardaSiFallaLaComprobacionDeDuplicados() {
        when(usuarioRepository.findById(7L)).thenReturn(propietario);
        PersistenceException failure = new PersistenceException("Consulta fallida");
        when(vehiculoRepository.existsByPatente("AB1234")).thenThrow(failure);

        assertSame(failure, assertThrows(PersistenceException.class,
                () -> service.registrarVehiculo("AB1234", 7L)));
        verify(vehiculoRepository, never()).save(any());
    }
}