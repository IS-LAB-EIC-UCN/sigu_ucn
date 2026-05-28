package cl.ucn.app.controller;

import io.javalin.http.Context;
import java.util.Collections;
import java.util.Map;

public class TallerController {
    public static void listarTalleres(Context ctx) {

        String nombreUsuario = ctx.sessionAttribute("usuarioNombre");
        String rolUsuario = ctx.sessionAttribute("usuarioRol");

        ctx.render("talleres.jte", Map.of(
                "title", "Talleres - SIGU-UCN",
                "usuarioNombre", nombreUsuario != null ? nombreUsuario : "Estudiante Demo",
                "rol", rolUsuario,
                "talleres", Collections.emptyList()
        ));
    }

    public static void registrarTaller(Context ctx) {
        String nombre = ctx.formParam("nombreTaller");
        String cuposStr = ctx.formParam("cupos");

        System.out.println("====== [DEBUG GRUPO 3] REGISTRANDO TALLER ======");
        System.out.println("Nombre: " + nombre);
        System.out.println("Cupos: " + cuposStr);
        System.out.println("================================================");



        ctx.redirect("/talleres");
    }
}
