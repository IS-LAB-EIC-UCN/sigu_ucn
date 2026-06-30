package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Categoriaticket;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CategoriaTicketRepositoryImpl implements ICategoriaTicketRepository {

    @Override
    public List<Categoriaticket> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Categoriaticket c", Categoriaticket.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Categoriaticket> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Categoriaticket.class, id));
        } finally {
            em.close();
        }
    }
}
