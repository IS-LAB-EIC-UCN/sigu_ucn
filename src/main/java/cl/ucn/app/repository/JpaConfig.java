package cl.ucn.app.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaConfig {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("sigu_ucn_pu");

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        emf.close();
    }
}
