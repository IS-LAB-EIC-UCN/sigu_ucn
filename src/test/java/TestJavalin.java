import io.javalin.config.JavalinConfig;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class TestJavalin {
    public static void main(String[] args) {
        System.out.println("Methods in JavalinConfig:");
        for (Method m : JavalinConfig.class.getMethods()) {
            System.out.println(m.getName());
        }
        System.out.println("Fields in JavalinConfig:");
        for (Field f : JavalinConfig.class.getFields()) {
            System.out.println(f.getName());
        }
    }
}
