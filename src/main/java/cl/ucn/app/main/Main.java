package cl.ucn.app.main;

import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.HomeRoutes;
import cl.ucn.app.routes.SoporteRoutes;
import io.javalin.Javalin;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import gg.jte.output.StringOutput;

import java.nio.file.Paths;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        ResourceCodeResolver resolver = new ResourceCodeResolver("jte");
        TemplateEngine engine = TemplateEngine.create(
                resolver,
                Paths.get("jte-classes"),
                ContentType.Html,
                Main.class.getClassLoader()
        );

        Javalin app = Javalin.create(config -> {

            config.fileRenderer((filePath, model, ctx) -> {
                StringOutput output = new StringOutput();
                engine.render(filePath, (Map<String, Object>) model, output);
                return output.toString();
            });

            config.staticFiles.add("/static");

            AuthRoutes.register(config);
            HomeRoutes.register(config);
            SoporteRoutes.register(config);
        });

        app.start(7000);
    }
}