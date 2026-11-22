package cl.ucn.app.config;
import java.util.HashMap;
import java.util.Map;
/** Las propiedades Java prevalecen sobre el entorno; persistence.xml conserva los valores locales. */
public final class ConfiguracionPersistencia {
    private ConfiguracionPersistencia() {}
    public static Map<String,Object> propiedades() {
        Map<String,Object> m=new HashMap<>();
        copiar(m,"db.url","DB_URL","jakarta.persistence.jdbc.url");
        copiar(m,"db.user","DB_USER","jakarta.persistence.jdbc.user");
        copiar(m,"db.password","DB_PASSWORD","jakarta.persistence.jdbc.password");return m;
    }
    private static void copiar(Map<String,Object> m,String propiedad,String entorno,String clave) {
        String valor=System.getProperty(propiedad);if(valor==null) valor=System.getenv(entorno);
        if(valor!=null) m.put(clave,valor);
    }
}
