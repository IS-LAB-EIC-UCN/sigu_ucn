package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.function.Function;

/**
 * Base común de los repositorios JPA del módulo de inventario (Grupo 5).
 * Centraliza la apertura/cierre del EntityManager y el manejo de transacciones,
 * evitando repetir el mismo bloque try/commit/rollback en cada repositorio.
 */
abstract class JpaRepositorioBase {

    /**
     * Ejecuta una consulta de solo lectura.
     */
    protected <T> T leer(Function<EntityManager, T> consulta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return consulta.apply(em);
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una operación de escritura dentro de una transacción.
     * Si ocurre cualquier error, la transacción se revierte y el error se propaga.
     */
    protected <T> T enTransaccion(Function<EntityManager, T> operacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = operacion.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
