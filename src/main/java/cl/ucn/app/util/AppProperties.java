package cl.ucn.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppProperties {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = AppProperties.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("No se encontró application.properties");
            }
            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new RuntimeException("No fue posible cargar application.properties", e);
        }
    }

    private AppProperties() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}