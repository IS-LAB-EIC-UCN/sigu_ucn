package cl.ucn.app.main;

import java.nio.file.Paths;

import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.CafeteriaRoutes;
import cl.ucn.app.routes.HomeRoutes;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

public class Main {
    public static void main(String[] args) {

        System.out.println("JTE Class found: " + gg.jte.html.HtmlTemplateOutput.class.getName());

        Javalin app = Javalin.create(config -> {

            ResourceCodeResolver resolver = new ResourceCodeResolver("jte");

            TemplateEngine engine = TemplateEngine.create(
                    resolver,
                    Paths.get("jte-classes"),
                    ContentType.Html,
                    Main.class.getClassLoader()
            );

            config.fileRenderer(new JavalinJte(engine));

            config.staticFiles.add("/static");

            AuthRoutes.register(config);
            HomeRoutes.register(config);
            CafeteriaRoutes.register(config);
        });
        System.out.println("Registrando rutas de cafetería...");
   

        app.start(7000);
    }
}