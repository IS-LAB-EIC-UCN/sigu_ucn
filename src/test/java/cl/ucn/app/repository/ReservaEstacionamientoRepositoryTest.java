
package cl.ucn.app.repository;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.ReservaEstacionamiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

class ReservaEstacionamientoRepositoryTest {

    private EntityManager em;
    private EntityTransaction transaction;
    private ReservaEstacionamientoRepository repository;

    @BeforeEach
    void setUp() {
        em = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);

        when(em.getTransaction()).thenReturn(transaction);

        Supplier<EntityManager> supplier = () -> em;
        repository = new ReservaEstacionamientoRepository(supplier);
    }

    @Test
    void guardaReservaCorrectamente() {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);

        repository.save(reserva);

        verify(transaction).begin();
        verify(em).persist(reserva);
        verify(transaction).commit();
        verify(em).close();
    }

    @ParameterizedTest
    @CsvSource({"excl_reservas_est_espacio_horario,El estacionamiento ya está reservado en ese horario.",
            "excl_reservas_est_vehiculo_horario,El vehículo ya tiene una reserva en ese horario."})
    void traduceConflictosAlConfirmar(String constraint, String mensaje) {
        RuntimeException failure = new RuntimeException(new ConstraintViolationException(
                "Conflicto", new SQLException("Cruce", "23P01"), constraint));
        when(transaction.isActive()).thenReturn(true);
        doThrow(failure).when(transaction).commit();
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> repository.save(mock(ReservaEstacionamiento.class)));
        assertEquals(mensaje, error.getMessage());
        assertSame(failure, error.getCause());
        verify(transaction).rollback();
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"excl_reservas_est_espacio_horario", "excl_reservas_est_vehiculo_horario"})
    void traduceConflictosAlInsertar(String constraint) {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);
        RuntimeException failure = new ConstraintViolationException(
                "Conflicto", new SQLException("Cruce", "23P01"), constraint);
        when(transaction.isActive()).thenReturn(true);
        doThrow(failure).when(em).persist(reserva);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> repository.save(reserva));
        assertSame(failure, error.getCause());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(em).close();
    }

    @ParameterizedTest
    @CsvSource({"23505,excl_reservas_est_vehiculo_horario", "23P01,otra_restriccion"})
    void propagaOtrasRestricciones(String state, String constraint) {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);
        RuntimeException failure = new ConstraintViolationException(
                "Otro error", new SQLException("Otro error", state), constraint);
        doThrow(failure).when(em).persist(reserva);
        assertSame(failure, assertThrows(RuntimeException.class, () -> repository.save(reserva)));
        verify(em).close();
    }

    @Test
    void conservaErrorOriginalSiFallaRollback() {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);
        RuntimeException failure = new RuntimeException("Persistencia");
        RuntimeException rollbackError = new RuntimeException("Rollback");
        when(transaction.isActive()).thenReturn(true);
        doThrow(failure).when(em).persist(reserva);
        doThrow(rollbackError).when(transaction).rollback();
        assertSame(failure, assertThrows(RuntimeException.class, () -> repository.save(reserva)));
        assertSame(rollbackError, failure.getSuppressed()[0]);
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"excl_reservas_est_espacio_horario", "excl_reservas_est_vehiculo_horario"})
    void usaNombreEstructuradoSiHibernateLoOmite(String constraint) {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);
        PSQLException postgres = new PSQLException(new ServerErrorMessage(
                "SERROR\u0000C23P01\u0000MConflicto\u0000n" + constraint + "\u0000\u0000"));
        RuntimeException failure = new ConstraintViolationException("Conflicto", postgres, null);
        doThrow(failure).when(em).persist(reserva);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> repository.save(reserva));
        assertSame(failure, error.getCause());
        assertTrue(error.getMessage().contains(constraint.contains("vehiculo") ? "vehículo" : "estacionamiento"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"40P01", "40001"})
    void comunicaConflictoTransitorioTrasRollback(String state) {
        RuntimeException failure = new RuntimeException(new SQLException("Conflicto", state));
        when(transaction.isActive()).thenReturn(true);
        doThrow(failure).when(transaction).commit();
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> repository.save(mock(ReservaEstacionamiento.class)));
        assertEquals("Otra solicitud coincidió con esta reserva. Revisa la disponibilidad e inténtalo nuevamente.",
                error.getMessage());
        assertSame(failure, error.getCause());
        verify(transaction).rollback();
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void consultaCrucesPorVehiculo(long cantidad) {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> query = mock(TypedQuery.class);
        String jpql = "SELECT COUNT(r) FROM ReservaEstacionamiento r "
                + "WHERE r.vehiculo.id = :vehiculoId "
                + "AND r.estado IN ('PENDIENTE', 'APROBADA') "
                + "AND r.fechaHoraInicio < :fin AND r.fechaHoraFin > :inicio";
        LocalDateTime inicio = LocalDateTime.of(2030, 1, 1, 10, 0);
        LocalDateTime fin = inicio.plusHours(1);
        when(em.createQuery(jpql, Long.class)).thenReturn(query);
        when(query.setParameter("vehiculoId", 2L)).thenReturn(query);
        when(query.setParameter("inicio", inicio)).thenReturn(query);
        when(query.setParameter("fin", fin)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(cantidad);
        assertEquals(cantidad > 0, repository.existsOverlappingReservationForVehicle(2L, inicio, fin));
        verify(em).close();
    }

    @Test
    void revierteTransaccionSiFallaElGuardado() {
        ReservaEstacionamiento reserva = mock(ReservaEstacionamiento.class);

        when(transaction.isActive()).thenReturn(true);

        doThrow(new RuntimeException("Error al guardar"))
                .when(em).persist(reserva);

        assertThrows(
                RuntimeException.class,
                () -> repository.save(reserva)
        );

        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(em).close();
    }

    @Test
    void buscaReservaPorId() {
        @SuppressWarnings("unchecked")
        TypedQuery<ReservaEstacionamiento> query =
                mock(TypedQuery.class);

        ReservaEstacionamiento esperada =
                mock(ReservaEstacionamiento.class);

        when(em.createQuery(anyString(), eq(ReservaEstacionamiento.class)))
                .thenReturn(query);

        when(query.setParameter("id", 1L)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(esperada);

        ReservaEstacionamiento resultado = repository.findById(1L);

        assertSame(esperada, resultado);
        verify(em).close();
    }

    @Test
    void devuelveNullCuandoNoEncuentraReservaPorId() {
        @SuppressWarnings("unchecked")
        TypedQuery<ReservaEstacionamiento> query =
                mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(ReservaEstacionamiento.class)))
                .thenReturn(query);

        when(query.setParameter("id", 99L)).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        ReservaEstacionamiento resultado = repository.findById(99L);

        assertNull(resultado);
        verify(em).close();
    }

    @Test
    void buscaReservasPorUsuarioId() {
        @SuppressWarnings("unchecked")
        TypedQuery<ReservaEstacionamiento> query =
                mock(TypedQuery.class);

        List<ReservaEstacionamiento> esperadas = List.of(
                mock(ReservaEstacionamiento.class),
                mock(ReservaEstacionamiento.class)
        );

        when(em.createQuery(anyString(), eq(ReservaEstacionamiento.class)))
                .thenReturn(query);

        when(query.setParameter("usuarioId", 1L)).thenReturn(query);
        when(query.getResultList()).thenReturn(esperadas);

        List<ReservaEstacionamiento> resultado =
                repository.findByUsuarioId(1L);

        assertEquals(2, resultado.size());
        assertEquals(esperadas, resultado);
        verify(em).close();
    }

    @Test
    void detectaReservaSolapada() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> query = mock(TypedQuery.class);

        LocalDateTime inicio = LocalDateTime.of(2026, 11, 24, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 24, 12, 0);

        when(em.createQuery(anyString(), eq(Long.class)))
                .thenReturn(query);

        when(query.setParameter(eq("estacionamientoId"), eq(1L)))
                .thenReturn(query);
        when(query.setParameter(eq("inicio"), eq(inicio)))
                .thenReturn(query);
        when(query.setParameter(eq("fin"), eq(fin)))
                .thenReturn(query);

        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado =
                repository.existsOverlappingReservation(1L, inicio, fin);

        assertTrue(resultado);
        verify(em).close();
    }

    @Test
    void noDetectaReservaSolapadaCuandoNoExistenCoincidencias() {
        @SuppressWarnings("unchecked")
        TypedQuery<Long> query = mock(TypedQuery.class);

        LocalDateTime inicio = LocalDateTime.of(2026, 11, 24, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 24, 12, 0);

        when(em.createQuery(anyString(), eq(Long.class)))
                .thenReturn(query);

        when(query.setParameter(eq("estacionamientoId"), eq(1L)))
                .thenReturn(query);
        when(query.setParameter(eq("inicio"), eq(inicio)))
                .thenReturn(query);
        when(query.setParameter(eq("fin"), eq(fin)))
                .thenReturn(query);

        when(query.getSingleResult()).thenReturn(0L);

        boolean resultado =
                repository.existsOverlappingReservation(1L, inicio, fin);

        assertFalse(resultado);
        verify(em).close();
    }
}
