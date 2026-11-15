package cl.ucn.app.repository;

import cl.ucn.app.model.Insumo;
import cl.ucn.app.model.Producto;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class JpaProductoRepository extends JpaRepositorioBase implements ProductoRepository {
    
    private static final String SELECT_CON_RELACIONES =
            "SELECT p FROM Producto p JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor ";

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

    @Override
    public List<Producto> buscar(Long categoriaId, String texto) {
        boolean filtrarCategoria = categoriaId != null;
        boolean filtrarTexto = texto != null && !texto.isBlank();

        StringBuilder jpql = new StringBuilder(SELECT_CON_RELACIONES).append("WHERE p.activo = true");
        if (filtrarCategoria) {
            jpql.append(" AND p.categoria.id = :categoriaId");
        }
        if (filtrarTexto) {
            jpql.append(" AND (LOWER(p.nombre) LIKE :texto OR LOWER(p.codigo) LIKE :texto)");
        }
        jpql.append(" ORDER BY p.codigo");

        return leer(em -> {
            TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
            if (filtrarCategoria) {
                query.setParameter("categoriaId", categoriaId);
            }
            if (filtrarTexto) {
                query.setParameter("texto", "%" + texto.trim().toLowerCase() + "%");
            }
            return query.getResultList();
        });
    }


    @Override
    public List<Insumo> listarInsumos() {
        return leer(em -> em.createQuery(
                        "SELECT i FROM Insumo i JOIN FETCH i.categoria WHERE i.activo = true ORDER BY i.codigo",
                        Insumo.class)
                .getResultList());
    }
}