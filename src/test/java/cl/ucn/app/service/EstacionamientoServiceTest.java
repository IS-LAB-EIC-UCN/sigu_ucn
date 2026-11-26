package cl.ucn.app.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.TipoEstacionamiento;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EstacionamientoRepository;
import cl.ucn.app.repository.ReservaEstacionamientoRepository;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EstacionamientoServiceTest {

    private static final Long ADMIN = 1L;
    private static final Long ESPACIO = 2L;
    private static final LocalDateTime AHORA = LocalDateTime.of(2030, 1, 1, 10, 0);

    private EstacionamientoRepository estacionamientos;
    private ReservaEstacionamientoRepository reservas;
    private EntityManager em;
    private EstacionamientoService service;

    @BeforeEach
    void preparar() {
        estacionamientos = mock(EstacionamientoRepository.class);
        reservas = mock(ReservaEstacionamientoRepository.class);
        em = mock(EntityManager.class);
        ZoneId zona = ZoneId.of("America/Santiago");
        service = new EstacionamientoService(estacionamientos, reservas,
                Clock.fixed(AHORA.atZone(zona).toInstant(), zona));
        when(reservas.findUsuarioConRol(ADMIN)).thenReturn(usuario("ADMIN", true));
    }

    @Test
    void normalizaConLocaleTurcoYRespetaElEstadoInicial() {
        Locale anterior = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Estacionamiento espacio = service.registrar(ADMIN, "\t\u00a0 i2 \n", "DOCENTES", "false");
            assertEquals("I2", espacio.getCodigo());
            assertEquals(TipoEstacionamiento.DOCENTES, espacio.getTipo());
            assertFalse(espacio.getHabilitado());
            verify(estacionamientos).existsByCodigo("I2");
            verify(estacionamientos).save(espacio);
        } finally {
            Locale.setDefault(anterior);
        }
    }

    @Test
    void permiteDiezCaracteresYNoExigePrefijo() {
        Estacionamiento espacio = service.registrar(ADMIN, "123456789z", "COMUN", "true");
        assertEquals("123456789Z", espacio.getCodigo());
        assertEquals(TipoEstacionamiento.COMUN, espacio.getTipo());
        assertTrue(espacio.getHabilitado());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "X", "12345678901", "C 01", "C\t01", "C-01", "Á2", "ß1", "Ｃ1"})
    void rechazaCodigoInvalido(String codigo) {
        assertThrows(IllegalArgumentException.class, () -> service.registrar(ADMIN, codigo, "COMUN", "true"));
        verifyNoInteractions(estacionamientos);
    }

    @Test
    void exigeTipoYEstadoValidos() {
        assertThrows(IllegalArgumentException.class, () -> service.registrar(ADMIN, "A1", null, "true"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(ADMIN, "A1", "OTRO", "true"));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(ADMIN, "A1", "COMUN", null));
        assertThrows(IllegalArgumentException.class, () -> service.registrar(ADMIN, "A1", "COMUN", "no"));
        verifyNoInteractions(estacionamientos);
    }

    @Test
    void rechazaCodigoDuplicadoTrasNormalizar() {
        when(estacionamientos.existsByCodigo("C01")).thenReturn(true);
        var error = assertThrows(IllegalArgumentException.class,
                () -> service.registrar(ADMIN, " c01 ", "COMUN", "true"));
        assertEquals("Ya existe un estacionamiento con ese código.", error.getMessage());
        verify(estacionamientos, never()).save(any());
    }

    @Test
    void listaHabilitadosYDeshabilitados() {
        var catalogo = List.of(new Estacionamiento("C01", true),
                new Estacionamiento("D01", false, TipoEstacionamiento.DOCENTES));
        when(estacionamientos.findAll()).thenReturn(catalogo);
        assertEquals(catalogo, service.listar(ADMIN));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ESTUDIANTE", "DOCENTE", "ADMIN"})
    void impideAccesoDeUnaCuentaSinPrivilegiosVigentes(String rol) {
        when(reservas.findUsuarioConRol(ADMIN)).thenReturn(usuario(rol, !"ADMIN".equals(rol)));
        assertThrows(EstacionamientoService.AccesoDenegadoException.class, () -> service.listar(ADMIN));
        assertThrows(EstacionamientoService.AccesoDenegadoException.class,
                () -> service.registrar(ADMIN, "A1", "COMUN", "true"));
        assertThrows(EstacionamientoService.AccesoDenegadoException.class,
                () -> service.cambiarHabilitacion(ADMIN, ESPACIO, "false"));
        verifyNoInteractions(estacionamientos);
    }

    @Test
    void validaElIdYElEstadoAntesDeBloquear() {
        assertThrows(IllegalArgumentException.class, () -> service.cambiarHabilitacion(ADMIN, 0L, "false"));
        assertThrows(IllegalArgumentException.class, () -> service.cambiarHabilitacion(ADMIN, null, "false"));
        assertThrows(IllegalArgumentException.class, () -> service.cambiarHabilitacion(ADMIN, ESPACIO, "otro"));
        verifyNoInteractions(estacionamientos);
    }

    @Test
    void informaCuandoElEspacioNoExiste() {
        prepararEspacioBloqueado(null);
        var error = assertThrows(IllegalArgumentException.class,
                () -> service.cambiarHabilitacion(ADMIN, ESPACIO, "false"));
        assertEquals("El estacionamiento no existe.", error.getMessage());
    }

    @Test
    void impideDeshabilitarConReservasQueAunNoTerminan() {
        var espacio = new Estacionamiento("C01", true);
        prepararEspacioBloqueado(espacio);
        when(reservas.existenReservasVigentes(em, ESPACIO, AHORA)).thenReturn(true);

        var error = assertThrows(IllegalArgumentException.class,
                () -> service.cambiarHabilitacion(ADMIN, ESPACIO, "false"));
        assertEquals("El estacionamiento tiene reservas pendientes o aprobadas que aún no terminan.",
                error.getMessage());
        assertTrue(espacio.getHabilitado());
    }

    @Test
    void deshabilitaYHabilitaEnLaMismaFilaSinCambiarCodigoNiTipo() {
        var espacio = new Estacionamiento("D01", true, TipoEstacionamiento.DOCENTES);
        prepararEspacioBloqueado(espacio);

        assertFalse(service.cambiarHabilitacion(ADMIN, ESPACIO, "false").getHabilitado());
        assertFalse(service.cambiarHabilitacion(ADMIN, ESPACIO, "false").getHabilitado());
        assertTrue(service.cambiarHabilitacion(ADMIN, ESPACIO, "true").getHabilitado());
        assertEquals("D01", espacio.getCodigo());
        assertEquals(TipoEstacionamiento.DOCENTES, espacio.getTipo());
        verify(reservas).existenReservasVigentes(em, ESPACIO, AHORA);
    }

    private void prepararEspacioBloqueado(Estacionamiento espacio) {
        when(estacionamientos.conEstacionamientoBloqueado(eq(ESPACIO), any())).thenAnswer(invocacion -> {
            BiFunction<EntityManager, Estacionamiento, Estacionamiento> operacion = invocacion.getArgument(1);
            return operacion.apply(em, espacio);
        });
    }

    private Usuario usuario(String rol, boolean activo) {
        return new Usuario("Cuenta de prueba", "prueba@test.local", "test", activo, new Rol(rol));
    }
}
