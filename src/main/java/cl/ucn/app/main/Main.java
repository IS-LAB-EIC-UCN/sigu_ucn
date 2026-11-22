package cl.ucn.app.main;

import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.EventosRoutes;
import cl.ucn.app.routes.HomeRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import cl.ucn.app.routes.InscripcionesRoutes;

public class Main {
    public static void main(String[] args) {



        Javalin app = Javalin.create(config -> {

            TemplateEngine engine = TemplateEngine.createPrecompiled(ContentType.Html);

            config.fileRenderer(new JavalinJte(engine));

            // 2. Archivos estáticos
            config.staticFiles.add("/static");


            // 3. Registro de rutas
            AuthRoutes.register(config);
            HomeRoutes.register(config);
            EventosRoutes.register(config);
            InscripcionesRoutes.register(config);

        });

        cl.ucn.app.config.JPAUtil.getEntityManagerFactory();
        new cl.ucn.app.repository.UsuarioRepository().migrarPasswords();
        app.start(cl.ucn.app.util.AppProperties.getInt("server.port", 7000));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            cl.ucn.app.config.JPAUtil.shutdown();
        }));
    }
}