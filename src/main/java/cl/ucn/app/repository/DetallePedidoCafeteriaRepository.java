package cl.ucn.app.repository;

import java.util.List;

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

    public List<DetallePedidoCafeteria> listarPorPedido(Long pedidoId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM DetallePedidoCafeteria d WHERE d.pedido.id = :pedidoId",
                    DetallePedidoCafeteria.class
            )
            .setParameter("pedidoId", pedidoId)
            .getResultList();
        } finally {
            em.close();
        }
    }

    public void eliminarPorPedido(Long pedidoId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.createQuery(
                    "DELETE FROM DetallePedidoCafeteria d WHERE d.pedido.id = :pedidoId"
            )
            .setParameter("pedidoId", pedidoId)
            .executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }}
}