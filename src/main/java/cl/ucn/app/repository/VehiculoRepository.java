package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Vehiculo;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VehiculoRepository {

    public List<Vehiculo> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT v FROM Vehiculo v WHERE v.usuario.id = :usuarioId",
                    Vehiculo.class
            ).setParameter("usuarioId", usuarioId).getResultList();
        } finally {
            em.close();
        }
    }
}