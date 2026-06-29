package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.model.ProductoCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ProductoCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("siguPU");

    public List<ProductoCafeteria> listar() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM ProductoCafeteria p",
                    ProductoCafeteria.class
            ).getResultList();
        } finally {
            em.close();
        }}

    public void actualizar(ProductoCafeteria producto) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }}

    public void guardar(ProductoCafeteria producto) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }}
        
    public ProductoCafeteria buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(ProductoCafeteria.class, id);
        } finally {
            em.close();
        }}}
        