package cl.ucn.app.main;

import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.HomeRoutes;
import cl.ucn.app.routes.EstacionamientoRoutes;

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

            ResourceCodeResolver resolver = new ResourceCodeResolver("jte");

            TemplateEngine engine = TemplateEngine.create(
                    resolver,
                    Paths.get("jte-classes"),
                    ContentType.Html,
                    Main.class.getClassLoader()
            );

            config.fileRenderer(new JavalinJte(engine));

            // Archivos estáticos
            config.staticFiles.add("/static");

            // Registro de rutas
            AuthRoutes.register(config);
            HomeRoutes.register(config);
            EstacionamientoRoutes.register(config);
        });

        app.start(7000);
    }
}