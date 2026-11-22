package cl.ucn.app.repository;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.service.VehiculoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TypedQuery;

class VehiculoRepositoryTest {

    private EntityManager em;
    private EntityTransaction transaction;
    private VehiculoRepository repository;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        em = mock(EntityManager.class);
        transaction = mock(EntityTransaction.class);
        repository = new VehiculoRepository(() -> em);
        vehiculo = new Vehiculo("AB1234", new Usuario());
    }

    @Test
    void confirmaLaEscrituraYCierraElEntityManager() {
        when(em.getTransaction()).thenReturn(transaction);

        repository.save(vehiculo);

        InOrder order = inOrder(transaction, em);
        order.verify(transaction).begin();
        order.verify(em).persist(vehiculo);
        order.verify(transaction).commit();
        order.verify(em).close();
        verify(transaction, never()).rollback();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void traduceElUniqueDePatenteTantoAlInsertarComoAlConfirmar(boolean fallaEnCommit) {
        when(em.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        PersistenceException failure = new RollbackException("Escritura rechazada",
                constraint("23505", "vehiculos_patente_key"));
        if (fallaEnCommit) {
            doThrow(failure).when(transaction).commit();
        } else {
            doThrow(failure).when(em).persist(vehiculo);
        }

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> repository.save(vehiculo));

        assertEquals("La patente ya está registrada.", error.getMessage());
        assertSame(failure, error.getCause());
        verify(transaction).rollback();
        verify(em).close();
    }

    @ParameterizedTest
    @CsvSource({"23503,fk_vehiculos_usuarios", "23505,vehiculos_pkey", "23514,otra_restriccion"})
    void noConfundeOtrasRestriccionesConUnaPatenteDuplicada(String state, String name) {
        when(em.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        PersistenceException failure = new PersistenceException("Restricción", constraint(state, name));
        doThrow(failure).when(em).persist(vehiculo);

        assertSame(failure, assertThrows(PersistenceException.class, () -> repository.save(vehiculo)));
        verify(transaction).rollback();
        verify(em).close();
    }

    @Test
    void noIntentaRevertirUnaTransaccionYaCerradaPorElProveedor() {
        when(em.getTransaction()).thenReturn(transaction);
        PersistenceException failure = new PersistenceException("Transacción cerrada");
        doThrow(failure).when(transaction).commit();

        assertSame(failure, assertThrows(PersistenceException.class, () -> repository.save(vehiculo)));
        verify(transaction, never()).rollback();
        verify(em).close();
    }

    @Test
    void cierraElEntityManagerSiNoSePuedeIniciarLaTransaccion() {
        when(em.getTransaction()).thenReturn(transaction);
        PersistenceException failure = new PersistenceException("No se pudo iniciar");
        doThrow(failure).when(transaction).begin();

        assertSame(failure, assertThrows(PersistenceException.class, () -> repository.save(vehiculo)));
        verify(em, never()).persist(any());
        verify(em).close();
    }

    @Test
    void conservaElErrorOriginalSiTambienFallaElRollback() {
        when(em.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        PersistenceException failure = new PersistenceException("Escritura fallida");
        PersistenceException rollbackFailure = new PersistenceException("Rollback fallido");
        doThrow(failure).when(em).persist(vehiculo);
        doThrow(rollbackFailure).when(transaction).rollback();

        assertSame(failure, assertThrows(PersistenceException.class, () -> repository.save(vehiculo)));
        assertArrayEquals(new Throwable[]{rollbackFailure}, failure.getSuppressed());
        verify(em).close();
    }

    @Test
    void buscaPorPatenteUsandoParametrosYCierraElEntityManager() {
        TypedQuery<Vehiculo> query = vehiculoQuery();
        String patente = "A' OR '1'='1";
        when(query.setParameter("patente", patente)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(vehiculo);

        assertSame(vehiculo, repository.findByPatente(patente));

        verify(em).createQuery(contains(":patente"), eq(Vehiculo.class));
        verify(query).setParameter("patente", patente);
        verify(em).close();
    }

    @Test
    void devuelveNullCuandoNoExisteLaPatente() {
        TypedQuery<Vehiculo> query = vehiculoQuery();
        when(query.setParameter("patente", "AB1234")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        assertNull(repository.findByPatente("AB1234"));
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void consultaLosVehiculosDeUnUsuarioIncluyendoUnaListaVacia(boolean tieneVehiculos) {
        TypedQuery<Vehiculo> query = vehiculoQuery();
        when(query.setParameter("usuarioId", 7L)).thenReturn(query);
        List<Vehiculo> expected = tieneVehiculos
                ? List.of(vehiculo, new Vehiculo("CD5678", vehiculo.getPropietario())) : List.of();
        when(query.getResultList()).thenReturn(expected);

        assertEquals(expected, repository.findByUsuarioId(7L));

        verify(em).createQuery(contains(":usuarioId"), eq(Vehiculo.class));
        verify(query).setParameter("usuarioId", 7L);
        verify(em, never()).getTransaction();
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L})
    void compruebaSiExisteUnaPatente(long cantidad) {
        TypedQuery<Long> query = countQuery(em);
        when(query.setParameter("patente", "AB1234")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(cantidad);

        assertEquals(cantidad > 0, repository.existsByPatente("AB1234"));
        verify(query).setParameter("patente", "AB1234");
        verify(em).close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"patente", "usuario", "existencia"})
    void propagaLosErroresDeConsultaYCierraLosRecursos(String consulta) {
        PersistenceException failure = new PersistenceException("Consulta fallida");
        if (consulta.equals("existencia")) {
            when(em.createQuery(anyString(), eq(Long.class))).thenThrow(failure);
        } else {
            when(em.createQuery(anyString(), eq(Vehiculo.class))).thenThrow(failure);
        }

        assertSame(failure, assertThrows(PersistenceException.class, () -> {
            switch (consulta) {
                case "patente" -> repository.findByPatente("AB1234");
                case "usuario" -> repository.findByUsuarioId(7L);
                default -> repository.existsByPatente("AB1234");
            }
        }));
        verify(em).close();
    }

    @Test
    void elServicioRecibeElDuplicadoConcurrenteAunqueLaConsultaPreviaNoLoDetecte() {
        EntityManager readEm = mock(EntityManager.class);
        TypedQuery<Long> query = countQuery(readEm);
        when(query.setParameter("patente", "AB1234")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);
        when(em.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        PersistenceException failure = new PersistenceException("Inserción concurrente",
                constraint("23505", "vehiculos_patente_key"));
        doThrow(failure).when(em).persist(any(Vehiculo.class));
        var managers = new ArrayDeque<>(List.of(readEm, em));
        VehiculoRepository realRepository = new VehiculoRepository(managers::removeFirst);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        when(usuarios.findById(7L)).thenReturn(new Usuario());
        VehiculoService service = new VehiculoService(realRepository, usuarios);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.registrarVehiculo(" ab1234 ", 7L));

        assertSame(failure, error.getCause());
        assertTrue(managers.isEmpty());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(readEm).close();
        verify(em).close();
    }

    private ConstraintViolationException constraint(String state, String name) {
        return new ConstraintViolationException("Restricción", new SQLException("Rechazada", state), name);
    }

    @SuppressWarnings("unchecked")
    private TypedQuery<Vehiculo> vehiculoQuery() {
        TypedQuery<Vehiculo> query = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Vehiculo.class))).thenReturn(query);
        return query;
    }

    @SuppressWarnings("unchecked")
    private TypedQuery<Long> countQuery(EntityManager manager) {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(manager.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        return query;
    }
}