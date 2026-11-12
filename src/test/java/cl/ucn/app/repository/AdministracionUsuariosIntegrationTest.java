package cl.ucn.app.repository;
import cl.ucn.app.model.*;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
@EnabledIfSystemProperty(named="eventos.integration",matches="true")
class AdministracionUsuariosIntegrationTest {
    private final UsuarioRepository repo=new UsuarioRepository();
    private final AdministracionUsuariosService service=new AdministracionUsuariosService(repo);
    private final List<Long> creados=new ArrayList<>();
    private Long rol(String nombre) { return repo.roles().stream().filter(r->nombre.equals(r.getNombre())).findFirst().orElseThrow().getId(); }
    @AfterEach void limpiar() {
        try(var em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();for(Long id:creados) em.createQuery("DELETE FROM Usuario WHERE id=:id").setParameter("id",id).executeUpdate();em.getTransaction().commit();
        }
    }
    @Test void creaEditaAsignaRolYDesactivaSinPerderPassword() {
        Long admin=repo.findByCorreo("admin@sigu.cl").getId();String correo=UUID.randomUUID()+"@example.test";
        Usuario u=service.guardar(null,"Prueba admin",correo,"Clave12345",rol("ESTUDIANTE"),true,admin);creados.add(u.getId());
        assertTrue(PasswordHasher.verificar("Clave12345",u.getPassword()));String hash=u.getPassword();
        service.guardar(u.getId(),"Editado",correo,"",rol("DOCENTE"),false,admin);
        Usuario leido=repo.findById(u.getId());assertEquals("DOCENTE",leido.getRol().getNombre());assertFalse(leido.getActivo());assertEquals(hash,leido.getPassword());
        assertNull(new AuthService(repo).autenticar(correo,"Clave12345"));
        assertThrows(IllegalArgumentException.class,()->service.guardar(null,"Duplicado",correo,"Clave12345",rol("ESTUDIANTE"),true,admin));
    }
    @Test void rechazaActorSinPermisosYAutodesactivacion() {
        Usuario admin=repo.findByCorreo("admin@sigu.cl");Long estudiante=repo.findByCorreo("estudiante@sigu.cl").getId();
        assertThrows(SecurityException.class,()->service.guardar(admin.getId(),admin.getNombre(),admin.getCorreo(),"",rol("ADMIN"),true,estudiante));
        assertThrows(IllegalArgumentException.class,()->service.guardar(admin.getId(),admin.getNombre(),admin.getCorreo(),"",rol("ADMIN"),false,admin.getId()));
        assertTrue(repo.findById(admin.getId()).getActivo());
    }
}
