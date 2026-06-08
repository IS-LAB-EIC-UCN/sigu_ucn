package cl.ucn.app.repository;

import java.util.List;

import cl.ucn.app.model.PedidoCafeteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PedidoCafeteriaRepository {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("sigu_ucn");

    public void guardar(PedidoCafeteria pedido) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }}

    public PedidoCafeteria buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(PedidoCafeteria.class, id);
        } finally {
            em.close();
        }}

    public List<PedidoCafeteria> listar() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM PedidoCafeteria p",
                    PedidoCafeteria.class
            ).getResultList();
        } finally {
            em.close();
        }}

    public void actualizar(PedidoCafeteria pedido) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(pedido);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }}}