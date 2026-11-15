package cl.ucn.app.service;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.exception.RegistroInvalidoException;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.RolRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.security.PasswordHasher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * RF-01: registro de usuarios.
 * Valida los datos de entrada, impide correos duplicados y guarda la contraseña
 * cifrada (RNF-01). Nunca permite auto-registrarse como ADMIN.
 */
public class RegistroService {

    /** Roles que un usuario puede elegir al registrarse. ADMIN queda excluido a propósito. */
    public static final List<String> ROLES_REGISTRABLES = List.of("ESTUDIANTE", "DOCENTE");

    static final int LARGO_MAX_NOMBRE = 100;
    static final int LARGO_MAX_CORREO = 120;
    static final int LARGO_MIN_PASSWORD = 8;
    static final int LARGO_MAX_PASSWORD = 64;

    private static final Pattern FORMATO_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TIENE_LETRA = Pattern.compile("[A-Za-zÁÉÍÓÚáéíóúÑñ]");
    private static final Pattern TIENE_DIGITO = Pattern.compile("\\d");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordHasher passwordHasher;
    private final Set<String> dominiosPermitidos;

    public RegistroService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           PasswordHasher passwordHasher, Set<String> dominiosPermitidos) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
        this.rolRepository = Objects.requireNonNull(rolRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.dominiosPermitidos = Set.copyOf(dominiosPermitidos);
    }

    public Usuario registrar(RegistroForm form) {
        Map<String, String> errores = new LinkedHashMap<>();

        String nombre = limpiar(form.nombre());
        String correo = normalizarCorreo(form.correo());
        String password = form.password() == null ? "" : form.password();
        String rolNombre = limpiar(form.rol()).toUpperCase(Locale.ROOT);

        validarNombre(nombre, errores);
        validarCorreo(correo, errores);
        validarPassword(password, form.confirmacion(), errores);
        if (!ROLES_REGISTRABLES.contains(rolNombre)) {
            errores.put("rol", "Seleccione un tipo de usuario válido.");
        }

        // Solo consultamos la BD si el correo es válido en formato.
        if (!errores.containsKey("correo") && usuarioRepository.findByCorreo(correo) != null) {
            errores.put("correo", "Ya existe una cuenta registrada con este correo.");
        }

        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }

        Rol rol = rolRepository.findByNombre(rolNombre);
        if (rol == null) {
            throw new IllegalStateException("El rol " + rolNombre + " no existe en la base de datos.");
        }

        Usuario usuario = new Usuario(nombre, correo, passwordHasher.hash(password), true, rol);
        usuarioRepository.save(usuario);
        return usuario;
    }

    /** Correo en minúsculas y sin espacios, para que el login no dependa de mayúsculas. */
    public static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }

    private void validarNombre(String nombre, Map<String, String> errores) {
        if (nombre.isEmpty()) {
            errores.put("nombre", "Ingrese su nombre.");
        } else if (nombre.length() > LARGO_MAX_NOMBRE) {
            errores.put("nombre", "El nombre no puede superar los " + LARGO_MAX_NOMBRE + " caracteres.");
        }
    }

    private void validarCorreo(String correo, Map<String, String> errores) {
        if (correo.isEmpty()) {
            errores.put("correo", "Ingrese su correo institucional.");
        } else if (correo.length() > LARGO_MAX_CORREO || !FORMATO_CORREO.matcher(correo).matches()) {
            errores.put("correo", "El correo no tiene un formato válido (ejemplo: nombre@alumnos.ucn.cl).");
        } else if (!dominiosPermitidos.isEmpty()
                && !dominiosPermitidos.contains(correo.substring(correo.indexOf('@') + 1))) {
            errores.put("correo", "Debe usar un correo institucional (" + String.join(", ", dominiosOrdenados()) + ").");
        }
    }

    private void validarPassword(String password, String confirmacion, Map<String, String> errores) {
        if (password.length() < LARGO_MIN_PASSWORD || password.length() > LARGO_MAX_PASSWORD) {
            errores.put("password", "La contraseña debe tener entre " + LARGO_MIN_PASSWORD
                    + " y " + LARGO_MAX_PASSWORD + " caracteres.");
        } else if (!TIENE_LETRA.matcher(password).find() || !TIENE_DIGITO.matcher(password).find()) {
            errores.put("password", "La contraseña debe contener al menos una letra y un número.");
        }
        if (!password.equals(confirmacion)) {
            errores.put("confirmacion", "Las contraseñas no coinciden.");
        }
    }

    private List<String> dominiosOrdenados() {
        return dominiosPermitidos.stream().sorted().map(d -> "@" + d).toList();
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
