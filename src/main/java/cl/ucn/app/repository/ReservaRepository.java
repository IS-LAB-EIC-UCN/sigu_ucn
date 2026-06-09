package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Reserva;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ReservaRepository {

    public List<Reserva> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r " +
                            "JOIN FETCH r.espacio " +
                            "JOIN FETCH r.vehiculo " +
                            "WHERE r.usuario.id = :usuarioId",
                    Reserva.class
            ).setParameter("usuarioId", usuarioId).getResultList();
        } finally {
            em.close();
        }
    }
}