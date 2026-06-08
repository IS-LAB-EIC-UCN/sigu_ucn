package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.model.ProductoCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ProductoCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("sigu_ucn");

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

    public ProductoCafeteria buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(ProductoCafeteria.class, id);
        } finally {
            em.close();
        }}}