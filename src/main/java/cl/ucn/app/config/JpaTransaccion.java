package cl.ucn.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public class JpaTransaccion implements Transaccion {

    @Override
    public <T> T ejecutar(Function<EntityManager, T> accion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try {
                T resultado = accion.apply(em);
                tx.commit();
                return resultado;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    try {
                        tx.rollback();
                    } catch (RuntimeException rollbackError) {
                        e.addSuppressed(rollbackError);
                    }
                }
                throw e;
            }
        } finally {
            em.close();
        }
    }

    public static <T> T leer(Function<EntityManager, T> consulta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return consulta.apply(em);
        } finally {
            em.close();
        }
    }
}
