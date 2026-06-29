package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.model.CategoriaCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class CategoriaCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("siguPU");

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

    public void guardar(CategoriaCafeteria categoria) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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