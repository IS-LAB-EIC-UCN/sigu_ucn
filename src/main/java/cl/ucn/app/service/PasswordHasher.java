package cl.ucn.app.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.util.Base64;

/** Formato versionado: algoritmo$iteraciones$sal$hash. No acepta claves almacenadas en texto plano. */
public final class PasswordHasher {
    private static final int ITERACIONES=600_000;
    private static final String PREFIJO="pbkdf2-sha256$";
    private static final SecureRandom RANDOM=new SecureRandom();
    private PasswordHasher() {}
    public static String generar(String clave) {
        if(clave==null || clave.isBlank() || clave.length()>1024) throw new IllegalArgumentException("La contraseña admite entre 1 y 1024 caracteres.");
        byte[] sal=new byte[16];RANDOM.nextBytes(sal);
        return PREFIJO+ITERACIONES+"$"+Base64.getEncoder().encodeToString(sal)+"$"+Base64.getEncoder().encodeToString(derivar(clave,sal,ITERACIONES));
    }
    public static boolean verificar(String clave,String almacenado) {
        if(clave==null || clave.length()>1024) return false;
        try {
            Registro hash=leer(almacenado);
            return MessageDigest.isEqual(hash.valor(),derivar(clave,hash.sal(),hash.iteraciones()));
        } catch(IllegalArgumentException ex) { return false; }
    }
    public static boolean esHash(String valor) {
        try { leer(valor);return true; } catch(IllegalArgumentException ex) { return false; }
    }
    public static boolean tienePrefijo(String valor) { return valor!=null && valor.startsWith(PREFIJO); }
    private record Registro(int iteraciones,byte[] sal,byte[] valor) {}
    private static Registro leer(String valor) {
        if(valor==null || valor.length()>255) throw new IllegalArgumentException("Hash inválido");
        String[] partes=valor.split("\\$",-1);
        if(partes.length!=4 || !"pbkdf2-sha256".equals(partes[0])) throw new IllegalArgumentException("Hash inválido");
        int iteraciones=Integer.parseInt(partes[1]);
        if(iteraciones<ITERACIONES || iteraciones>2_000_000) throw new IllegalArgumentException("Parámetros inválidos");
        byte[] sal=Base64.getDecoder().decode(partes[2]), hash=Base64.getDecoder().decode(partes[3]);
        if(sal.length!=16 || hash.length!=32) throw new IllegalArgumentException("Longitud inválida");
        return new Registro(iteraciones,sal,hash);
    }
    private static byte[] derivar(String clave,byte[] sal,int iteraciones) {
        PBEKeySpec spec=new PBEKeySpec(clave.toCharArray(),sal,iteraciones,256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch(GeneralSecurityException ex) { throw new IllegalStateException("PBKDF2 no está disponible",ex); }
        finally { spec.clearPassword(); }
    }
}
