package cl.ucn.app.repository.soporte;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.RolesSoporte;
import cl.ucn.app.model.soporte.TecnicoDisponible;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PermisosSoporteRepository {
    public boolean esAdminsitrador(Long usuarioId) {
        return comprobarRol(usuarioId, RolesSoporte::esAdministrador);
    }

    public boolean esTecnico(Long usuarioId) {
        return comprobarRol(usuarioId, RolesSoporte::esTecnico);
    }

    private boolean comprobarRol(Long usuarioId, Predicate<Usuario> verificador) {
        if (usuarioId == null || usuarioId <= 0) {return false;}

        EntityManager em = JPAUtil.getEntityManager();
        try {
            return verificador.test(em.find(Usuario.class, usuarioId));
        } finally {
            em.close();
        }
    }

    public List<TecnicoDisponible> listarTecnicoActivos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
            List<TecnicoDisponible> tecnicos = new ArrayList<>();

            for (Usuario usuario : usuarios) {
                if (!RolesSoporte.esTecnico(usuario)) {continue;}

                Object identificador = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(usuario);
                Long id = ((Number) identificador).longValue();
                tecnicos.add(new TecnicoDisponible(id, RolesSoporte.nombreVisible(usuario, id)));
            }
            return tecnicos;
        } finally {
            em.close();
        }
    }
}