package cl.ucn.app;

import cl.ucn.app.controller.AuthController;
import cl.ucn.app.controller.EspacioController;
import cl.ucn.app.controller.ReservaController;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.AuthService;
import cl.ucn.app.service.EspacioService;
import cl.ucn.app.service.ReservaService;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class Main {
    public static void main(String[] args) {
        // Repositories (Using JPA)
        UsuarioRepository usuarioRepository = new JpaUsuarioRepository();
        EspacioRepository espacioRepository = new JpaEspacioRepository();
        ReservaRepository reservaRepository = new JpaReservaRepository();

        // Services
        AuthService authService = new AuthService(usuarioRepository);
        EspacioService espacioService = new EspacioService(espacioRepository);
        ReservaService reservaService = new ReservaService(reservaRepository, usuarioRepository, espacioRepository);

        // Controllers
        AuthController authController = new AuthController(authService);
        EspacioController espacioController = new EspacioController(espacioService);
        ReservaController reservaController = new ReservaController(reservaService);

        // Configure Javalin with Jackson for LocalDateTime
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }));
        }).start(8080);

        // Routes
        app.post("/register", authController::register);
        app.post("/login", authController::login);

        app.get("/espacios", espacioController::getAll);

        app.post("/reservas", reservaController::create);
        app.get("/reservas", reservaController::getAll);
        app.patch("/reservas/{id}", reservaController::updateStatus);

        System.out.println("Servidor iniciado en http://localhost:8080");
    }
}
