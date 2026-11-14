package cl.ucn.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public final class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "siguPU";
    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            // Permite usar otro puerto local sin cambiar la conexión compartida del proyecto.
            String jdbcUrl = System.getProperty("sigu.db.url", System.getenv("SIGU_DB_URL"));
            Map<String, String> overrides = jdbcUrl == null || jdbcUrl.isBlank()
                    ? Map.of() : Map.of("jakarta.persistence.jdbc.url", jdbcUrl);
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, overrides);
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
