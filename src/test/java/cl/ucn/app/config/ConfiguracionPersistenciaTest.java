package cl.ucn.app.config;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ConfiguracionPersistenciaTest {
    @Test void permiteElegirUnaBaseAisladaSinModificarXml() {
        String anterior=System.getProperty("db.url");
        try {
            System.setProperty("db.url","jdbc:postgresql://localhost:5999/prueba");
            assertEquals("jdbc:postgresql://localhost:5999/prueba",ConfiguracionPersistencia.propiedades().get("jakarta.persistence.jdbc.url"));
        } finally { if(anterior==null) System.clearProperty("db.url");else System.setProperty("db.url",anterior); }
    }
}
