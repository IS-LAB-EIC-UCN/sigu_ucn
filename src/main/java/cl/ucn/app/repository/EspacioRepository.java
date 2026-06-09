package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EspacioRepository {

    public List<Espacio> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Espacio> query = em.createQuery("SELECT e FROM Espacio e", Espacio.class);
            return query.getResultList();
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
