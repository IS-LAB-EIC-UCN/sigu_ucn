package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EspacioRepository {

    public List<Espacio> findDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Espacio e WHERE e.disponible = true ORDER BY e.nombre",
                    Espacio.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public Espacio findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Espacio.class, id);
        } finally {
            em.close();
        }
    }
}