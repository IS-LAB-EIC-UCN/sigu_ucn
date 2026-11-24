
package cl.ucn.app.repository;

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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.Estacionamiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

class EstacionamientoRepositoryTest {

    private EntityManager em;
    private EntityTransaction transaction;
    private EstacionamientoRepository repository;

    @SuppressWarnings("unchecked")
    private TypedQuery<Estacionamiento> query;

    @BeforeEach
    void setUp() {
        em = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);
        query = mock(TypedQuery.class);

        when(em.getTransaction()).thenReturn(transaction);

        Supplier<EntityManager> supplier = () -> em;
        repository = new EstacionamientoRepository(supplier);
    }

    @Test
    void guardaEstacionamientoCorrectamente() {
        Estacionamiento estacionamiento =
                new Estacionamiento("E-01", true);

        repository.save(estacionamiento);

        verify(transaction).begin();
        verify(em).persist(estacionamiento);
        verify(transaction).commit();
        verify(em).close();
    }

    @Test
    void revierteTransaccionSiFallaElGuardado() {
        Estacionamiento estacionamiento =
                new Estacionamiento("E-01", true);

        when(transaction.isActive()).thenReturn(true);

        doThrow(new RuntimeException("Error al guardar"))
                .when(em).persist(estacionamiento);

        assertThrows(
                RuntimeException.class,
                () -> repository.save(estacionamiento)
        );

        verify(transaction).rollback();
        verify(em).close();
        verify(transaction, never()).commit();
    }

    @Test
    void buscaEstacionamientoPorId() {
        Estacionamiento esperado =
                new Estacionamiento("E-01", true);

        when(em.find(Estacionamiento.class, 1L))
                .thenReturn(esperado);

        Estacionamiento resultado = repository.findById(1L);

        assertSame(esperado, resultado);
        verify(em).close();
    }

    @Test
    void buscaEstacionamientoPorCodigo() {
        Estacionamiento esperado =
                new Estacionamiento("E-01", true);

        when(em.createQuery(
                "SELECT e FROM Estacionamiento e WHERE e.codigo = :codigo",
                Estacionamiento.class
        )).thenReturn(query);

        when(query.setParameter("codigo", "E-01"))
                .thenReturn(query);

        when(query.getSingleResult()).thenReturn(esperado);

        Estacionamiento resultado =
                repository.findByCodigo("E-01");

        assertSame(esperado, resultado);
        verify(em).close();
    }

    @Test
    void devuelveNullSiNoEncuentraCodigo() {
        when(em.createQuery(
                "SELECT e FROM Estacionamiento e WHERE e.codigo = :codigo",
                Estacionamiento.class
        )).thenReturn(query);

        when(query.setParameter("codigo", "E-99"))
                .thenReturn(query);

        when(query.getSingleResult())
                .thenThrow(new NoResultException());

        Estacionamiento resultado =
                repository.findByCodigo("E-99");

        assertNull(resultado);
        verify(em).close();
    }

    @Test
    void listaTodosLosEstacionamientos() {
        List<Estacionamiento> esperados = List.of(
                new Estacionamiento("E-01", true),
                new Estacionamiento("E-02", false)
        );

        when(em.createQuery(
                "SELECT e FROM Estacionamiento e ORDER BY e.codigo",
                Estacionamiento.class
        )).thenReturn(query);

        when(query.getResultList()).thenReturn(esperados);

        List<Estacionamiento> resultado = repository.findAll();

        assertEquals(2, resultado.size());
        assertEquals(esperados, resultado);
        verify(em).close();
    }

    @Test
    void compruebaSiExisteUnCodigo() {
        TypedQuery<Long> countQuery = mock(TypedQuery.class);

        when(em.createQuery(
                "SELECT COUNT(e) FROM Estacionamiento e WHERE e.codigo = :codigo",
                Long.class
        )).thenReturn(countQuery);

        when(countQuery.setParameter("codigo", "E-01"))
                .thenReturn(countQuery);

        when(countQuery.getSingleResult()).thenReturn(1L);

        boolean resultado = repository.existsByCodigo("E-01");

        assertTrue(resultado);
        verify(em).close();
    }

    @Test
    void compruebaSiNoExisteUnCodigo() {
        TypedQuery<Long> countQuery = mock(TypedQuery.class);

        when(em.createQuery(
                "SELECT COUNT(e) FROM Estacionamiento e WHERE e.codigo = :codigo",
                Long.class
        )).thenReturn(countQuery);

        when(countQuery.setParameter("codigo", "E-99"))
                .thenReturn(countQuery);

        when(countQuery.getSingleResult()).thenReturn(0L);

        boolean resultado = repository.existsByCodigo("E-99");

        assertFalse(resultado);
        verify(em).close();
    }
}
