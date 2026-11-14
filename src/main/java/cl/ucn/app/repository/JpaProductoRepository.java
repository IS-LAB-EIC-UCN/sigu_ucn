package cl.ucn.app.repository;

import cl.ucn.app.model.Producto;

public class JpaProductoRepository extends JpaRepositorioBase implements ProductoRepository {

    @Override
    public boolean existeCodigo(String codigo) {
        return leer(em -> em.createQuery(
                        "SELECT COUNT(p) FROM Producto p WHERE LOWER(p.codigo) = :codigo", Long.class)
                .setParameter("codigo", codigo.trim().toLowerCase())
                .getSingleResult() > 0);
    }

    @Override
    public void guardar(Producto producto) {
        enTransaccion(em -> {
            em.persist(producto);
            return null;
        });
    }
}