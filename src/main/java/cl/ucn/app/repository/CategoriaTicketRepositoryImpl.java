package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.CategoriaTicket;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CategoriaTicketRepositoryImpl implements ICategoriaTicketRepository {

    @Override
    public List<CategoriaTicket> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM CategoriaTicket c", CategoriaTicket.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CategoriaTicket> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(CategoriaTicket.class, id));
        } finally {
            em.close();
        }
    }
}
