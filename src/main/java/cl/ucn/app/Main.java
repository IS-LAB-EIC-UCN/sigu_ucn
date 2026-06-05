package cl.ucn.app;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin.start(config -> {
            config.routes.get("/", ctx -> ctx.result("SIGU-UCN Grupo 4 - Tickets de soporte"));
        });
    }
}