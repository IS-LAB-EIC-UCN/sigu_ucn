package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;
import org.hibernate.event.spi.ClearEvent;

import javax.swing.text.StyledEditorKit;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public final class RolesSoporte {
    private RolesSoporte() {}

    public static boolean esAdministrador(Usuario usuario) {
        return tieneRol(usuario, "ADMIN", "ADMINISTRADOR");
    }

    public static boolean esTecnico(Usuario usuario) {
        return tieneRol(usuario, "TECNICO", "TECNICO_SOPORTE", "SOPORTE");
    }

    public static String nombreVisible(Usuario usuario, Long id) {
        Object nombre = leer(usuario, "nombre", "nombreCompleto", "username", "correo", "email");
        return nombre == null || nombre.toString().isBlank()
                ? "Técnico #" + id : nombre.toString();
    }

    private static boolean tieneRol(Usuario usuario, String... nombres) {
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            return false;
        }

        Object rol = leer(usuario, "rol", "roles", "tipoUsuario");
        if (rol instanceof Collection<?> varios) {
            return varios.stream().anyMatch(r -> coincide(r, nombres));
        }
        return coincide(rol, nombres);
    }

    private static boolean coincide(Object rol, String... nombres) {
        if(rol == null) {return false;}

        Object valor = rol instanceof CharSequence || rol instanceof Enum<?>
                ? rol :leer(rol, "nombre", "codigo", "tipo", "nombreRol");

        if (valor == null) {return false;}

        String normalizado = normalizar(valor.toString()).replaceFirst("^ROLE_", "");
        return Arrays.stream(nombres).anyMatch(n -> normalizado.equals(normalizar(n)));
    }

    private static String normalizar(String valor) {
        return Normalizer.normalize(valor.trim().toUpperCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[\\s-]+", "_");
    }

    private static Object leer(Object objeto, String... propiedades) {
        if (objeto == null) {return null;}

        for (String propiedad : propiedades) {
            Object valor = leerPropiedad(objeto, propiedad);
            if (valor != null) {return valor;}
        }
        return null;
    }

    private static Object leerPropiedad(Object objeto, String propiedad) {
        String getter = "get" + Character.toUpperCase(propiedad.charAt(0)) + propiedad.substring(1);

        try {
            Method metodo = objeto.getClass().getMethod(getter);
            return metodo.invoke(objeto);
        } catch (ReflectiveOperationException ignored) {
            return leerCampo(objeto, propiedad);
        }
    }

    private static Object leerCampo(Object objeto, String propiedad) {
        for (Class<?> clase = objeto.getClass(); clase != null; clase = clase.getSuperclass()) {
            try {
                Field campo = clase.getDeclaredField(propiedad);
                campo.setAccessible(true);
                return campo.get(objeto);
            } catch (NoSuchFieldException ignored) {
            } catch (ReflectiveOperationException ignored) {
                return null;
            }
        }
        return null;
    }
}
