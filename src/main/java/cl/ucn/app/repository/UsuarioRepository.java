package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UsuarioRepository {

    public Usuario findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u JOIN FETCH u.rol WHERE lower(u.correo) = :correo",
                    Usuario.class
            );
            query.setParameter("correo", correo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void save(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            usuario.setPassword(cl.ucn.app.service.PasswordHasher.generar(usuario.getPassword()));
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Migración idempotente para bases anteriores. Se ejecuta antes de atender peticiones. */
    public int migrarPasswords() {
        try (EntityManager em=JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                int migrados=0;
                for(Usuario usuario:em.createQuery("FROM Usuario",Usuario.class).setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE).getResultList()) {
                    String valor=usuario.getPassword();
                    if(cl.ucn.app.service.PasswordHasher.esHash(valor)) continue;
                    if(cl.ucn.app.service.PasswordHasher.tienePrefijo(valor)) throw new IllegalStateException("Hay un hash de contraseña con formato inválido.");
                    usuario.setPassword(cl.ucn.app.service.PasswordHasher.generar(valor));migrados++;
                }
                em.getTransaction().commit();return migrados;
            } catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback();throw ex; }
        }
    }
    public Usuario findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.id=:id",Usuario.class).setParameter("id",id).getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public List<cl.ucn.app.model.Rol> roles() {
        try(EntityManager em=JPAUtil.getEntityManager()) {
            return em.createQuery("FROM Rol ORDER BY nombre",cl.ucn.app.model.Rol.class).getResultList();
        }
    }
    /** Serializa administración para proteger el último administrador activo. No elimina usuarios. */
    public Usuario administrar(Long id,String nombre,String correo,String passwordHash,Long rolId,boolean activo,Long actorId) {
        try(EntityManager em=JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("LOCK TABLE usuarios IN SHARE ROW EXCLUSIVE MODE").executeUpdate();
                Usuario actor=em.find(Usuario.class,actorId);
                if(actor==null || !Boolean.TRUE.equals(actor.getActivo()) || !"ADMIN".equals(actor.getRol().getNombre()))
                    throw new SecurityException("Solo un administrador activo puede gestionar cuentas.");
                cl.ucn.app.model.Rol rol=em.find(cl.ucn.app.model.Rol.class,rolId);
                if(rol==null || !java.util.Set.of("ADMIN","DOCENTE","ESTUDIANTE").contains(rol.getNombre()))
                    throw new IllegalArgumentException("Selecciona uno de los roles existentes del sistema.");
                if(id!=null && id.equals(actorId) && (!activo || !"ADMIN".equals(rol.getNombre())))
                    throw new IllegalArgumentException("No puedes desactivar tu propia cuenta ni quitarte el rol administrador.");
                String jpql="SELECT COUNT(u) FROM Usuario u WHERE lower(trim(u.correo))=:correo"+(id==null?"":" AND u.id<>:id");
                var duplicados=em.createQuery(jpql,Long.class).setParameter("correo",correo);
                if(id!=null) duplicados.setParameter("id",id);
                if(duplicados.getSingleResult()>0) throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
                Usuario usuario=id==null?new Usuario():em.find(Usuario.class,id,jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
                if(usuario==null) throw new IllegalArgumentException("Usuario no encontrado.");
                if(id!=null && Boolean.TRUE.equals(usuario.getActivo()) && "ADMIN".equals(usuario.getRol().getNombre()) && (!activo || !"ADMIN".equals(rol.getNombre()))) {
                    long admins=em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.activo=true AND u.rol.nombre='ADMIN'",Long.class).getSingleResult();
                    if(admins<=1) throw new IllegalArgumentException("Debe existir al menos un administrador activo.");
                }
                if(id==null && passwordHash==null) throw new IllegalArgumentException("La contraseña es obligatoria al crear una cuenta.");
                if(passwordHash!=null && !cl.ucn.app.service.PasswordHasher.esHash(passwordHash)) throw new IllegalArgumentException("La contraseña debe guardarse protegida.");
                usuario.setNombre(nombre);usuario.setCorreo(correo);usuario.setRol(rol);usuario.setActivo(activo);
                if(passwordHash!=null) usuario.setPassword(passwordHash);
                if(id==null) em.persist(usuario);
                em.getTransaction().commit();return usuario;
            } catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback();throw ex; }
        }
    }
    public List<Usuario> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol ORDER BY u.nombre", Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}