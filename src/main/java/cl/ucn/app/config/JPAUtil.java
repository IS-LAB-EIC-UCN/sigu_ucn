package cl.ucn.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.function.Supplier;

public final class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "siguPU";
    private static EntityManagerFactory entityManagerFactory;

    private static final ThreadLocal<Boolean> TX_SCOPE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<EntityManager> THREAD_EM = new ThreadLocal<>();

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        if (Boolean.TRUE.equals(TX_SCOPE.get())) {
            EntityManager em = THREAD_EM.get();
            if (em == null || !em.isOpen()) {
                em = getEntityManagerFactory().createEntityManager();
                em.getTransaction().begin();
                THREAD_EM.set(em);
            }
            return em;
        }
        return getEntityManagerFactory().createEntityManager();
    }

    public static boolean isManagedTransaction() {
        return Boolean.TRUE.equals(TX_SCOPE.get());
    }

    public static <T> T runInTransaction(Supplier<T> action) {
        if (isManagedTransaction()) {
            return action.get();
        }
        TX_SCOPE.set(true);
        try {
            T result = action.get();
            EntityManager em = THREAD_EM.get();
            if (em != null && em.isOpen()) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().commit();
                }
                em.close();
            }
            return result;
        } catch (RuntimeException e) {
            EntityManager em = THREAD_EM.get();
            if (em != null && em.isOpen()) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
            throw e;
        } finally {
            THREAD_EM.remove();
            TX_SCOPE.set(false);
        }
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
