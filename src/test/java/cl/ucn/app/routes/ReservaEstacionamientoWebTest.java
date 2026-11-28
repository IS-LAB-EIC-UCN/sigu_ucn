package cl.ucn.app.routes;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.function.BiFunction;

import cl.ucn.app.controller.AdminEstacionamientoController;
import cl.ucn.app.controller.ReservaEstacionamientoController;
import cl.ucn.app.controller.VehiculoController;
import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.EstacionamientoRepository;
import cl.ucn.app.repository.ReservaEstacionamientoRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.VehiculoRepository;
import cl.ucn.app.service.EstacionamientoService;
import cl.ucn.app.service.ReservaEstacionamientoService;
import cl.ucn.app.service.VehiculoService;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ReservaEstacionamientoWebTest {
    private static final String PERSONAL = "/estacionamientos/vehiculos/nuevo";
    private static final String ADMIN = "/estacionamientos/admin/reservas";
    private static final String CANCELAR = "/estacionamientos/reservas/";
    private static final LocalDateTime AHORA = LocalDateTime.of(2030, 1, 1, 9, 0);
    private static final Pattern FORMULARIO = Pattern.compile("<form\\s+method=\"post\"\\s+action=\"([^\"]+)\"\\s*>");

    @TempDir
    Path jteClasses;

    private Javalin app;
    private HttpClient http;
    private ReservaEstacionamientoRepository reservas;
    private VehiculoService vehiculos;
    private EntityManager em;
    private Usuario propietario;
    private Usuario administrador;
    private Estacionamiento espacio;

    @BeforeEach
    void preparar() {
        reservas = mock(ReservaEstacionamientoRepository.class);
        vehiculos = mock(VehiculoService.class);
        em = mock(EntityManager.class);
        var espacios = mock(EstacionamientoRepository.class);
        propietario = usuario(1L, "Propietaria", "DOCENTE", true);
        administrador = usuario(2L, "Administrador", "ADMIN", true);
        usuario(3L, "Cuenta inactiva", "ADMIN", false);
        espacio = mock(Estacionamiento.class);
        when(espacio.getId()).thenReturn(10L);
        when(espacio.getCodigo()).thenReturn("C01");
        when(espacios.conEstacionamientoBloqueado(eq(10L), any())).thenAnswer(invocacion -> {
            BiFunction<EntityManager, Estacionamiento, Object> operacion = invocacion.getArgument(1);
            return operacion.apply(em, espacio);
        });

        ZoneId zona = ZoneId.of("America/Santiago");
        Clock clock = Clock.fixed(AHORA.atZone(zona).toInstant(), zona);
        var service = new ReservaEstacionamientoService(reservas, mock(UsuarioRepository.class),
                mock(VehiculoRepository.class), espacios, clock);
        var renderer = new JavalinJte(TemplateEngine.create(new ResourceCodeResolver("jte"),
                jteClasses, ContentType.Html, getClass().getClassLoader()));
        app = Javalin.create(config -> {
            config.fileRenderer(renderer);
            VehiculoRoutes.register(config, new VehiculoController(vehiculos, service));
            EstacionamientoRoutes.register(config, new AdminEstacionamientoController(
                    new EstacionamientoService(espacios, reservas, clock)), new ReservaEstacionamientoController(service));
            config.routes.get("/sesion-de-prueba/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                ctx.sessionAttribute("usuarioId", id);
                ctx.sessionAttribute("usuarioNombre", "Cuenta de prueba");
                ctx.sessionAttribute("usuarioRol", id == 2L ? "DOCENTE" : "ADMIN");
                ctx.status(204);
            });
        }).start("127.0.0.1", 0);
        http = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
    }

    @AfterEach
    void cerrar() {
        if (http != null) {
            http.close();
        }
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void formularioPersonalUsaElIdRenderizadoYActualizaElHistorial() throws Exception {
        var pendiente = reserva(100L, propietario, "ABCD12", "PENDIENTE", AHORA.plusHours(1));
        var aprobadaIniciada = reserva(101L, propietario, "EFGH34", "APROBADA", AHORA);
        var vencida = reserva(102L, propietario, "IJKL56", "PENDIENTE", AHORA.minusHours(1));
        var cancelada = reserva(103L, propietario, "MNOP78", "CANCELADA", AHORA.minusHours(2));
        when(reservas.findByUsuarioId(1L)).thenReturn(List.of(pendiente, aprobadaIniciada, vencida, cancelada));
        iniciarSesion(1L);

        var pagina = get(PERSONAL);
        assertEquals(200, pagina.statusCode());
        for (String texto : List.of("Mis reservas", "C01", "ABCD12", "EFGH34", "IJKL56", "MNOP78",
                "01/01/2030 10:00", "01/01/2030 11:00", "PENDIENTE", "APROBADA", "VENCIDA", "CANCELADA",
                "<summary>Cancelar reserva</summary>", "¿Confirmas la cancelación de esta reserva?")) {
            assertTrue(pagina.body().contains(texto), texto);
        }
        assertFalse(pagina.body().contains("Propietario"));
        assertEquals(List.of(CANCELAR + "100/cancelar"), formulariosDeCancelacion(pagina.body()));

        var respuesta = post(formulariosDeCancelacion(pagina.body()).getFirst(), "");
        assertRedireccion(respuesta, 303, PERSONAL + "#mis-reservas");
        verify(reservas).findByIdBloqueada(em, 100L);
        assertEquals("CANCELADA", pendiente.getEstado());
        assertEquals("PENDIENTE", vencida.getEstado());
        var despues = get(PERSONAL).body();
        assertTrue(despues.contains("Reserva cancelada correctamente."));
        assertTrue(formulariosDeCancelacion(despues).isEmpty());
        assertFalse(get(PERSONAL).body().contains("Reserva cancelada correctamente."));
    }

    @Test
    void desdeElCatalogoElAdminCancelaLaFilaAjenaConElFormularioRenderizado() throws Exception {
        var ajena = reserva(201L, propietario, "ABCD12", "APROBADA", AHORA.plusHours(1));
        var propia = reserva(202L, administrador, "EFGH34", "PENDIENTE", AHORA.plusHours(2));
        when(reservas.findAllConDetalle()).thenReturn(List.of(ajena, propia));
        iniciarSesion(2L);

        var catalogo = get("/estacionamientos/admin");
        assertEquals(200, catalogo.statusCode());
        assertTrue(catalogo.body().contains("href=\"" + ADMIN + "\">Administrar reservas</a>"));
        var pagina = get(ADMIN);
        assertEquals(200, pagina.statusCode());
        assertTrue(pagina.body().contains("Propietaria (usuario1@ucn.test)"));
        assertTrue(pagina.body().contains("Administrador (usuario2@ucn.test)"));
        var formularios = formulariosDeCancelacion(pagina.body());
        assertEquals(List.of(ADMIN + "/201/cancelar", ADMIN + "/202/cancelar"), formularios);

        assertRedireccion(post(formularios.getFirst(), ""), 303, ADMIN);
        verify(reservas).findByIdBloqueada(em, 201L);
        assertEquals("CANCELADA", ajena.getEstado());
        assertEquals("PENDIENTE", propia.getEstado());
        assertEquals(List.of(ADMIN + "/202/cancelar"), formulariosDeCancelacion(get(ADMIN).body()));
    }

    @Test
    void sinSesionRedirigeAlLoginYGetNoCancela() throws Exception {
        assertRedireccion(get(PERSONAL), 302, "/login");
        assertRedireccion(get(ADMIN), 302, "/login");
        assertRedireccion(post(CANCELAR + "100/cancelar", ""), 302, "/login");
        assertRedireccion(post(ADMIN + "/100/cancelar", ""), 302, "/login");
        iniciarSesion(1L);
        assertTrue(get(CANCELAR + "100/cancelar").statusCode() >= 400);
        verifyNoInteractions(reservas);
    }

    @Test
    void exigeCuentaActivaYRolAdministrativoDeLaBase() throws Exception {
        iniciarSesion(1L);
        assertEquals(403, get(ADMIN).statusCode());
        assertEquals(403, post(ADMIN + "/100/cancelar", "usuarioId=2&rol=ADMIN").statusCode());
        var ajena = reserva(200L, administrador, "EFGH34", "PENDIENTE", AHORA.plusHours(1));
        assertRedireccion(post(CANCELAR + "200/cancelar", "usuarioId=2"), 303, PERSONAL + "#mis-reservas");
        assertTrue(get(PERSONAL).body().contains("No se encontró la reserva solicitada."));
        assertEquals("PENDIENTE", ajena.getEstado());

        iniciarSesion(3L);
        assertEquals(403, get(PERSONAL).statusCode());
        assertEquals(403, post(CANCELAR + "100/cancelar", "").statusCode());
        assertEquals(403, get(ADMIN).statusCode());
        assertEquals(403, post(ADMIN + "/100/cancelar", "").statusCode());
        verify(reservas, never()).findAllConDetalle();
        verify(reservas, never()).findByIdBloqueada(any(), any());
    }

    @ParameterizedTest
    @CsvSource({"texto, El identificador de la reserva no es válido.", "0, El identificador de la reserva no es válido.",
            "-1, El identificador de la reserva no es válido.", "999, No se encontró la reserva solicitada."})
    void manejaIdentificadoresInvalidosOInexistentes(String id, String aviso) throws Exception {
        iniciarSesion(1L);

        assertRedireccion(post(CANCELAR + id + "/cancelar", ""), 303, PERSONAL + "#mis-reservas");
        assertTrue(get(PERSONAL).body().contains(aviso));
        verify(reservas, never()).findByIdBloqueada(any(), any());
    }

    @Test
    void errorDeRegistroDeVehiculoConservaLaVistaPersonalCompleta() throws Exception {
        var propia = reserva(100L, propietario, "ABCD12", "PENDIENTE", AHORA.plusHours(1));
        when(reservas.findByUsuarioId(1L)).thenReturn(List.of(propia));
        doThrow(new IllegalArgumentException("Patente inválida.")).when(vehiculos).registrarVehiculo("?", 1L);
        iniciarSesion(1L);

        var respuesta = post("/estacionamientos/vehiculos", "patente=%3F");

        assertEquals(400, respuesta.statusCode());
        assertTrue(respuesta.body().contains("Patente inválida."));
        assertTrue(respuesta.body().contains("Mis reservas"));
        assertEquals(List.of(CANCELAR + "100/cancelar"), formulariosDeCancelacion(respuesta.body()));
    }

    private Usuario usuario(long id, String nombre, String rol, boolean activo) {
        Usuario usuario = mock(Usuario.class);
        Rol rolDeUsuario = mock(Rol.class);
        when(rolDeUsuario.getNombre()).thenReturn(rol);
        when(usuario.getId()).thenReturn(id);
        when(usuario.getNombre()).thenReturn(nombre);
        when(usuario.getCorreo()).thenReturn("usuario" + id + "@ucn.test");
        when(usuario.getRol()).thenReturn(rolDeUsuario);
        when(usuario.getActivo()).thenReturn(activo);
        when(reservas.findUsuarioConRol(id)).thenReturn(usuario);
        return usuario;
    }

    private ReservaEstacionamiento reserva(long id, Usuario usuario, String patente, String estado, LocalDateTime desde) {
        Vehiculo vehiculo = mock(Vehiculo.class);
        when(vehiculo.getPatente()).thenReturn(patente);
        var reserva = spy(new ReservaEstacionamiento(desde, desde.plusHours(1), estado, usuario, vehiculo, espacio));
        when(reserva.getId()).thenReturn(id);
        when(reservas.findById(id)).thenReturn(reserva);
        when(reservas.findByIdBloqueada(em, id)).thenReturn(reserva);
        return reserva;
    }

    private void iniciarSesion(long id) throws IOException, InterruptedException {
        assertEquals(204, get("/sesion-de-prueba/" + id).statusCode());
    }

    private HttpResponse<String> get(String ruta) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(uri(ruta)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String ruta, String datos) throws IOException, InterruptedException {
        var solicitud = HttpRequest.newBuilder(uri(ruta)).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(datos)).build();
        return http.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String ruta) {
        return URI.create("http://127.0.0.1:" + app.port() + ruta);
    }

    private void assertRedireccion(HttpResponse<String> respuesta, int estado, String destino) {
        assertEquals(estado, respuesta.statusCode());
        assertEquals(destino, respuesta.headers().firstValue("Location").orElseThrow());
    }

    private List<String> formulariosDeCancelacion(String html) {
        var coincidencias = FORMULARIO.matcher(html);
        List<String> acciones = new ArrayList<>();
        while (coincidencias.find()) {
            String accion = coincidencias.group(1);
            if (accion.endsWith("/cancelar")) {
                acciones.add(accion);
            }
        }
        return acciones;
    }
}
