package cl.ucn.app.controller;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.service.EstacionamientoService;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cl.ucn.app.model.RegistroEstacionamiento;
import cl.ucn.app.service.RegistroEstacionamientoService;


public class EstacionamientoController {

    private final EstacionamientoService estacionamientoService;
    private final RegistroEstacionamientoService registroEstacionamientoService;

    public EstacionamientoController() {
        this.estacionamientoService = new EstacionamientoService();
        this.registroEstacionamientoService = new RegistroEstacionamientoService();
    }

    private boolean esAdmin(Context ctx) {
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        return "ADMIN".equals(usuarioRol);
    }

    private boolean bloquearSiNoEsAdmin(Context ctx) {
        if (!esAdmin(ctx)) {
            ctx.status(403);
            ctx.result("Acceso denegado");
            return true;
        }
        return false;
    }

    public void showEstacionamientos(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        List<Espacio> espacios = estacionamientoService.obtenerEspaciosPorRol(usuarioRol);
        List<Reserva> reservasActivas = estacionamientoService.obtenerReservasActivas();

        Map<Long, List<Integer>> puestosOcupados = reservasActivas.stream()
                .filter(r -> r.getPuestoNumero() != null)
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                r -> r.getEspacio().getId(),
                                java.util.stream.Collectors.mapping(
                                        Reserva::getPuestoNumero,
                                        java.util.stream.Collectors.toList()
                                )
                        )
                );

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Estacionamientos");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("espacios", espacios);
        model.put("puestosOcupados", puestosOcupados);

        ctx.render("estacionamientos.jte", model);
    }

    public void showNuevo(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Nuevo Estacionamiento");
        model.put("usuarioNombre", usuarioNombre);

        ctx.render("nuevo-estacionamiento.jte", model);
    }

    public void guardar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Espacio espacio = new Espacio(
                ctx.formParam("nombre"),
                ctx.formParam("tipo"),
                Integer.parseInt(ctx.formParam("capacidad")),
                true
        );

        estacionamientoService.guardar(espacio);

        ctx.redirect("/estacionamientos");
    }

    public void eliminar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        estacionamientoService.eliminar(id);

        ctx.redirect("/estacionamientos");
    }

    public void showEditar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        Espacio espacio = estacionamientoService.obtenerPorId(id);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Editar Estacionamiento");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("espacio", espacio);

        ctx.render("editar-estacionamiento.jte", model);
    }

    public void actualizar(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Long id = Long.parseLong(ctx.pathParam("id"));

        Espacio espacio = estacionamientoService.obtenerPorId(id);

        espacio.setNombre(ctx.formParam("nombre"));
        espacio.setTipo(ctx.formParam("tipo"));
        espacio.setCapacidad(Integer.parseInt(ctx.formParam("capacidad")));

        estacionamientoService.actualizar(espacio);

        ctx.redirect("/estacionamientos");
    }

    public void showReservar(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");
        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        List<Espacio> espacios;

        String espacioIdParam = ctx.queryParam("espacioId");
        String puestoParam = ctx.queryParam("puesto");

        if (espacioIdParam != null && !espacioIdParam.isBlank()) {
            Long espacioId = Long.parseLong(espacioIdParam);
            Espacio espacioSeleccionado = estacionamientoService.obtenerPorId(espacioId);
            espacios = List.of(espacioSeleccionado);
        } else {
            espacios = estacionamientoService.obtenerEspaciosDisponiblesPorRol(usuarioRol);
        }

        List<Vehiculo> vehiculos = estacionamientoService.obtenerVehiculosPorUsuario(usuarioId);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Reservar Estacionamiento");
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("espacios", espacios);
        model.put("vehiculos", vehiculos);
        model.put("puestoNumero", puestoParam);

        ctx.render("reservar-estacionamiento.jte", model);
    }

    public void reservar(Context ctx) {

        try {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }

            Long espacioId = Long.parseLong(ctx.formParam("espacioId"));
            Long vehiculoId = Long.parseLong(ctx.formParam("vehiculoId"));
            Integer puestoNumero = Integer.parseInt(ctx.formParam("puestoNumero"));
            LocalDate fechaReserva = LocalDate.parse(ctx.formParam("fechaReserva"));
            LocalTime horaInicio = LocalTime.parse(ctx.formParam("horaInicio"));
            LocalTime horaFin = LocalTime.parse(ctx.formParam("horaFin"));

            estacionamientoService.reservarEspacio(
                    usuarioId,
                    espacioId,
                    vehiculoId,
                    puestoNumero,
                    fechaReserva,
                    horaInicio,
                    horaFin
            );

            ctx.redirect("/mis-reservas/estacionamientos");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al reservar: " + e.getMessage());
        }
    }

    public void misReservas(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        List<Reserva> reservas = estacionamientoService.obtenerReservasPorUsuario(usuarioId);

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Mis Reservas");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("reservas", reservas);

        ctx.render("mis-reservas-estacionamiento.jte", model);
    }


    public void cancelarReserva(Context ctx) {

        try {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }

            Long reservaId = Long.parseLong(ctx.pathParam("id"));

            estacionamientoService.cancelarReserva(usuarioId, reservaId);

            ctx.redirect("/mis-reservas/estacionamientos");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al cancelar reserva: " + e.getMessage());
        }
    }


    public void registrarIngreso(Context ctx) {

        try {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }

            Long reservaId = Long.parseLong(ctx.pathParam("id"));

            registroEstacionamientoService.registrarIngreso(reservaId);

            ctx.redirect("/mis-reservas/estacionamientos");

        } catch (Exception e) {
            e.printStackTrace();

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            ctx.status(500);
            ctx.result("Error al registrar ingreso: " + causa.getMessage());
        }
    }

    public void registrarSalida(Context ctx) {

        try {
            Long usuarioId = ctx.sessionAttribute("usuarioId");

            if (usuarioId == null) {
                ctx.redirect("/login");
                return;
            }

            Long registroId = Long.parseLong(ctx.pathParam("id"));

            registroEstacionamientoService.registrarSalida(registroId);

            ctx.redirect("/estacionamientos/historial");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al registrar salida: " + e.getMessage());
        }
    }

    public void historialEstacionamientos(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        List<RegistroEstacionamiento> registros;

        if ("ADMIN".equals(usuarioRol)) {
            registros = registroEstacionamientoService.listarHistorial();
        } else {
            registros = registroEstacionamientoService.listarHistorialPorUsuario(usuarioId);
        }

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Historial de Estacionamientos");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", usuarioRol);
        model.put("registros", registros);

        ctx.render("historial-estacionamiento.jte", model);
    }

    public void reservasPendientes(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        List<Reserva> reservas = estacionamientoService.obtenerReservasPendientes();

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Reservas Pendientes");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("reservas", reservas);

        ctx.render("reservas-pendientes-estacionamiento.jte", model);
    }

    public void aprobarReserva(Context ctx) {

        try {
            if (bloquearSiNoEsAdmin(ctx)) {
                return;
            }

            Long reservaId = Long.parseLong(ctx.pathParam("id"));

            estacionamientoService.aprobarReserva(reservaId);

            ctx.redirect("/estacionamientos/reservas-pendientes");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al aprobar reserva: " + e.getMessage());
        }
    }

    public void rechazarReserva(Context ctx) {

        try {
            if (bloquearSiNoEsAdmin(ctx)) {
                return;
            }

            Long reservaId = Long.parseLong(ctx.pathParam("id"));

            estacionamientoService.rechazarReserva(reservaId);

            ctx.redirect("/estacionamientos/reservas-pendientes");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Error al rechazar reserva: " + e.getMessage());
        }
    }

    public void filtrarHistorialEstacionamientos(Context ctx) {

        Long usuarioId = ctx.sessionAttribute("usuarioId");

        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        String usuario = ctx.queryParam("usuario");
        String patente = ctx.queryParam("patente");
        String fechaParam = ctx.queryParam("fecha");

        LocalDate fecha = null;

        if (fechaParam != null && !fechaParam.isBlank()) {
            fecha = LocalDate.parse(fechaParam);
        }

        List<RegistroEstacionamiento> registros =
                registroEstacionamientoService.filtrarHistorial(usuario, patente, fecha);

        if (!"ADMIN".equals(usuarioRol)) {
            Long idActual = usuarioId;
            registros = registros.stream()
                    .filter(r -> r.getReserva().getUsuario().getId().equals(idActual))
                    .toList();
        }

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Historial de Estacionamientos");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", usuarioRol);
        model.put("registros", registros);
        model.put("usuarioFiltro", usuario);
        model.put("patenteFiltro", patente);
        model.put("fechaFiltro", fechaParam);


        ctx.render("historial-estacionamiento.jte", model);
    }

    public void showRegistrarUsuarioVehiculo(Context ctx) {

        if (bloquearSiNoEsAdmin(ctx)) {
            return;
        }

        Map<String, Object> model = new HashMap<>();

        model.put("title", "Registrar Usuario y Vehículo");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));

        ctx.render("registrar-usuario-vehiculo.jte", model);
    }

    public void registrarUsuarioVehiculo(Context ctx) {

        try {
            if (bloquearSiNoEsAdmin(ctx)) {
                return;
            }

            String nombre = ctx.formParam("nombre");
            String correo = ctx.formParam("correo");
            String password = ctx.formParam("password");
            String rol = ctx.formParam("rol");

            String patente = ctx.formParam("patente");
            String marca = ctx.formParam("marca");
            String modelo = ctx.formParam("modelo");

            estacionamientoService.registrarUsuarioConVehiculo(
                    nombre,
                    correo,
                    password,
                    rol,
                    patente,
                    marca,
                    modelo
            );

            ctx.redirect("/estacionamientos");

        } catch (Exception e) {
            e.printStackTrace();

            Throwable causa = e;
            while (causa.getCause() != null) {
                causa = causa.getCause();
            }

            ctx.status(500);
            ctx.result("Error al registrar usuario y vehículo: " + causa.getMessage());
        }
    }

}