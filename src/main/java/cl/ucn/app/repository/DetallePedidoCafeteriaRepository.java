package cl.ucn.app.repository;

import cl.ucn.app.model.DetallePedidoCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DetallePedidoCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("siguPU");

    public void guardar(DetallePedidoCafeteria detalle) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(detalle);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}