package cl.ucn.app.repository;

import cl.ucn.app.model.Pedido;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PedidoRepository {
    private final EntityManager em;

    public PedidoRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Pedido pedido) {
        em.persist(pedido);
    }

    public Pedido findById(Long id) {
        return em.find(Pedido.class, id);
    }

    public List<Pedido> findAll() {
        return em.createQuery("SELECT p FROM Pedido p", Pedido.class).getResultList();
    }
}