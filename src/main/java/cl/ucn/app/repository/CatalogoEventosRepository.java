package cl.ucn.app.repository;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CatalogoEventosRepository {
    public List<Expositor> expositores() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("FROM Expositor ORDER BY nombre", Expositor.class).getResultList();
        }
    }
    public List<Espacio> espacios() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("FROM Espacio ORDER BY nombre", Espacio.class).getResultList();
        }
    }
    public <T> T buscar(Class<T> tipo, Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) { return em.find(tipo, id); }
    }
    public void guardar(Object entidad) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin(); em.merge(entidad); em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
        }
    }
}
