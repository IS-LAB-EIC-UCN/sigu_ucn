package cl.ucn.app.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256, incluido en el JDK (no requiere dependencias).
 *
 * Formato almacenado: pbkdf2_sha256$iteraciones$salBase64$hashBase64
 * Cada contraseña tiene su propia sal aleatoria de 16 bytes, por lo que dos usuarios
 * con la misma contraseña obtienen hashes distintos.
 */
public class Pbkdf2PasswordHasher implements PasswordHasher {

    private static final String PREFIJO = "pbkdf2_sha256";
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES_POR_DEFECTO = 310_000;
    private static final int LARGO_SAL_BYTES = 16;
    private static final int LARGO_HASH_BITS = 256;

    private final int iteraciones;
    private final SecureRandom random = new SecureRandom();

    public Pbkdf2PasswordHasher() {
        this(ITERACIONES_POR_DEFECTO);
    }

    /** Permite usar menos iteraciones en las pruebas unitarias. */
    public Pbkdf2PasswordHasher(int iteraciones) {
        if (iteraciones < 1) {
            throw new IllegalArgumentException("Las iteraciones deben ser positivas.");
        }
        this.iteraciones = iteraciones;
    }

    @Override
    public String hash(String passwordPlano) {
        if (passwordPlano == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        byte[] sal = new byte[LARGO_SAL_BYTES];
        random.nextBytes(sal);
        byte[] hash = derivar(passwordPlano, sal, iteraciones);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIJO + "$" + iteraciones + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    @Override
    public boolean verificar(String passwordPlano, String hashAlmacenado) {
        if (passwordPlano == null || !esHash(hashAlmacenado)) {
            return false;
        }
        String[] partes = hashAlmacenado.split("\\$");
        try {
            int iter = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(passwordPlano, sal, iter);
            // Comparación en tiempo constante para no filtrar información por tiempos de respuesta.
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean esHash(String valorAlmacenado) {
        return valorAlmacenado != null
                && valorAlmacenado.startsWith(PREFIJO + "$")
                && valorAlmacenado.split("\\$").length == 4;
    }

    private byte[] derivar(String password, byte[] sal, int iter) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iter, LARGO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No fue posible calcular el hash de la contraseña.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
