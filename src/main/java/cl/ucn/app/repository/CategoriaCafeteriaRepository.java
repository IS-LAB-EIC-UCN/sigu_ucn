package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.model.CategoriaCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class CategoriaCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("sigu_ucn");

    public List<CategoriaCafeteria> listar() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM CategoriaCafeteria c",
                    CategoriaCafeteria.class
            ).getResultList();
        } finally {
            em.close();
        }}

    public CategoriaCafeteria buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(CategoriaCafeteria.class, id);
        } finally {
            em.close();
        }}}