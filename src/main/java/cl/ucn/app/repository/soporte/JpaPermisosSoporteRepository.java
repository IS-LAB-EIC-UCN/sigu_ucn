package cl.ucn.app.repository.soporte;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.soporte.RolesSoporte;
import cl.ucn.app.model.soporte.TecnicoDisponible;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Predicate;

public class JpaPermisosSoporteRepository implements PermisosSoporteRepository {
    @Override
    public boolean esAdministrador(Long usuarioId) {
        return comprobarRol(usuarioId, RolesSoporte::esAdministrador);
    }

    @Override
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

    @Override
    public List<TecnicoDisponible> listarTecnicosActivos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM Usuario u JOIN FETCH u.rol r " +
                                    "WHERE r.nombre = :rol AND u.activo = true " +
                                    "ORDER BY u.nombre", Usuario.class)
                    .setParameter("rol", RolesSoporte.ROL_TECNICO)
                    .getResultList().stream()
                    .map(usuario -> new TecnicoDisponible(usuario.getId(), usuario.getNombre()))
                    .toList();
        } finally {
            em.close();
        }
    }
}