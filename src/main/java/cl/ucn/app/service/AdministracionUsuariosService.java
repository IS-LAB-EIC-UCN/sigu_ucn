package cl.ucn.app.service;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import java.util.Locale;
public class AdministracionUsuariosService {
    private final UsuarioRepository repository;
    public AdministracionUsuariosService(UsuarioRepository repository) { this.repository=repository; }
    public Usuario guardar(Long id,String nombre,String correo,String clave,Long rolId,boolean activo,Long actorId) {
        nombre=nombre==null?"":nombre.strip();correo=correo==null?"":correo.strip().toLowerCase(Locale.ROOT);
        if(nombre.isBlank() || nombre.length()>100) throw new IllegalArgumentException("El nombre admite entre 1 y 100 caracteres.");
        if(correo.length()>120 || !correo.matches("[^\\s@]+@[^\\s@]+[.][^\\s@]+")) throw new IllegalArgumentException("Ingresa un correo válido de hasta 120 caracteres.");
        if(rolId==null || actorId==null) throw new IllegalArgumentException("Falta el rol o el administrador responsable.");
        if(id==null && (clave==null || clave.isBlank())) throw new IllegalArgumentException("La contraseña inicial es obligatoria.");
        String hash=null;
        if(clave!=null && !clave.isEmpty()) {
            if(clave.length()<8 || clave.length()>1024 || clave.isBlank()) throw new IllegalArgumentException("La contraseña admite entre 8 y 1024 caracteres.");
            hash=PasswordHasher.generar(clave);
        }
        return repository.administrar(id,nombre,correo,hash,rolId,activo,actorId);
    }
}
