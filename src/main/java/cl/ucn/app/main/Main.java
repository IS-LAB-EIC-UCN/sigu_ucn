package cl.ucn.app.main;

import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.HomeRoutes;
import cl.ucn.app.routes.TutoriaRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        System.out.println("JTE Class found: " + gg.jte.html.HtmlTemplateOutput.class.getName());

        Javalin app = Javalin.create(config -> {

            // 1. Forzamos al motor JTE a buscar en el recurso correcto del Classpath
            // Esto soluciona problemas donde el IDE no mapea bien la carpeta "jte"
            // 1. Crear un resolver
            ResourceCodeResolver resolver = new ResourceCodeResolver("jte");

            // 2. Crear el motor asegurando el uso del ClassLoader actual
            TemplateEngine engine = TemplateEngine.create(
                    resolver,
                    Paths.get("jte-classes"), // Carpeta temporal para clases generadas
                    ContentType.Html,
                    Main.class.getClassLoader()  // <--- ESTO ES LA CLAVE
            );

            config.fileRenderer(new JavalinJte(engine));

            // 2. Archivos estáticos
            config.staticFiles.add("/static");

            // 3. Registro de rutas
            AuthRoutes.register(config);
            HomeRoutes.register(config);
            TutoriaRoutes.register(config);
        });

        app.start(7000);
    }
}