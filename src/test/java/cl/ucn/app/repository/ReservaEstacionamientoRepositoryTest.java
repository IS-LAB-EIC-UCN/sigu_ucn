
package cl.ucn.app.repository;

import java.time.LocalDateTime;
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
