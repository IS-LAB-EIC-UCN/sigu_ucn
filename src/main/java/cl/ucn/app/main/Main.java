package cl.ucn.app.main;

import cl.ucn.app.controller.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.*;
import cl.ucn.app.routes.AuthRoutes;
import cl.ucn.app.routes.HomeRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        System.out.println("JTE Class found: " + gg.jte.html.HtmlTemplateOutput.class.getName());

        // Instanciamos los repositorios, servicios y controladores fuera del bloque de configuración
        // para poder usarlos en las rutas.
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        EspacioRepository espacioRepository = new JpaEspacioRepository();
        ReservaRepository reservaRepository = new JpaReservaRepository();

        AuthService authService = new AuthService(usuarioRepository);
        EspacioService espacioService = new EspacioService(espacioRepository);
        ReservaService reservaService = new ReservaService(reservaRepository, usuarioRepository, espacioRepository);

        AuthController authController = new AuthController(authService);
        EspacioController espacioController = new EspacioController(espacioService);
        ReservaController reservaController = new ReservaController(reservaService);

        Javalin app = Javalin.create(config -> {

            // 1. Forzamos al motor JTE a buscar en el recurso correcto del Classpath
            // Esto soluciona problemas donde el IDE no mapea bien la carpeta "jte"
            ResourceCodeResolver resolver = new ResourceCodeResolver("jte");

            // 2. Crear el motor asegurando el uso del ClassLoader actual
            TemplateEngine engine = TemplateEngine.create(
                    resolver,
                    Paths.get("jte-classes"), // Carpeta temporal para clases generadas
                    ContentType.Html,
                    Main.class.getClassLoader()
            );

            config.fileRenderer(new JavalinJte(engine));

            // Configuración de CORS
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });

            // Configuración del mapeador Jackson para LocalDateTime
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }));

            // Archivos estáticos
            config.staticFiles.add("/static");

            // Registro de rutas visuales JTE
            AuthRoutes.register(config, authController);
            HomeRoutes.register(config);

            // Registro de endpoints REST API
            config.routes.post("/register", authController::register);
            config.routes.post("/api/login", authController::login);
            config.routes.get("/espacios", espacioController::getAll);
            config.routes.post("/reservas", reservaController::create);
            config.routes.get("/reservas", reservaController::getAll);
            config.routes.patch("/reservas/{id}", reservaController::updateStatus);
        });

        app.start(7000);
    }
}