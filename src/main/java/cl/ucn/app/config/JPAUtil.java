package cl.ucn.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "siguPU";
    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}