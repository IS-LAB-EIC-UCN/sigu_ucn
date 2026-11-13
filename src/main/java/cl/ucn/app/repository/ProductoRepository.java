package cl.ucn.app.repository;

import cl.ucn.app.model.Producto;
import jakarta.persistence.EntityManager;

public class ProductoRepository {
    private final EntityManager em;

    public ProductoRepository(EntityManager em) {
        this.em = em;
    }

    public Producto findById(Long id) {
        return em.find(Producto.class, id);
    }

    public void update(Producto producto) {
        em.merge(producto);
    }
}