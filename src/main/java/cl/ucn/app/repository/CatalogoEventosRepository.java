package cl.ucn.app.repository;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.*;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CatalogoEventosRepository {
    public List<Categoria> categorias() {
        try (EntityManager em=JPAUtil.getEntityManager()) {
            return em.createQuery("FROM Categoria ORDER BY nombre",Categoria.class).getResultList();
        }
    }
    public void registrarCategoria(String nombre) {
        try (EntityManager em=JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("INSERT INTO categorias(nombre) VALUES (:nombre) ON CONFLICT DO NOTHING").setParameter("nombre",nombre).executeUpdate();
                em.getTransaction().commit();
            } catch(RuntimeException e) { if(em.getTransaction().isActive()) em.getTransaction().rollback();throw e; }
        }
    }
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
    public void actualizarEspacio(Espacio datos) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("LOCK TABLE eventos IN SHARE ROW EXCLUSIVE MODE").executeUpdate();
                Espacio actual = em.find(Espacio.class, datos.getId(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                if (actual == null) throw new IllegalArgumentException("Ubicación no encontrada.");
                if (datos.getNombre() == null || datos.getNombre().isBlank() || datos.getNombre().length() > 100 ||
                        datos.getTipo() == null || datos.getTipo().isBlank() || datos.getTipo().length() > 50 ||
                        datos.getCapacidad() == null || datos.getCapacidad() < 1)
                    throw new IllegalArgumentException("Revisa el nombre, tipo y capacidad de la ubicación.");
                long duplicados = em.createQuery("SELECT COUNT(s) FROM Espacio s WHERE lower(trim(s.nombre))=lower(trim(:nombre)) AND s.id<>:id",Long.class)
                    .setParameter("nombre",datos.getNombre()).setParameter("id",datos.getId()).getSingleResult();
                if (duplicados > 0) throw new IllegalArgumentException("Ya existe una ubicación con ese nombre.");
                long incompatibles = em.createQuery("SELECT COUNT(e) FROM Evento e WHERE e.espacio.id=:id AND e.estado IN ('PUBLICADO','BORRADOR') AND e.fecha >= :hoy AND e.cupoMaximo > :capacidad",Long.class)
                    .setParameter("id",datos.getId()).setParameter("hoy",java.time.LocalDate.now()).setParameter("capacidad",datos.getCapacidad()).getSingleResult();
                if (incompatibles > 0) throw new IllegalArgumentException("Hay eventos programados cuyo cupo supera esta capacidad. Ajusta sus cupos antes de reducirla.");
                em.createQuery("UPDATE Evento SET lugar=:nombre WHERE espacio.id=:id")
                    .setParameter("nombre",datos.getNombre().strip()).setParameter("id",datos.getId()).executeUpdate();
                actual.setNombre(datos.getNombre().strip());actual.setTipo(datos.getTipo().strip());
                actual.setCapacidad(datos.getCapacidad());actual.setDisponible(datos.getDisponible());
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
        }
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
