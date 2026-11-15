package cl.ucn.app.controller;

import cl.ucn.app.model.Pedido;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.service.CategoriaService;
import cl.ucn.app.service.PedidoService;
import cl.ucn.app.service.ProductoService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// Controlador web del módulo Cafetería (Grupo 7).
// Recibe los formularios, valida sesión y rol, llama a los servicios y
// muestra los errores en pantalla sin que la página se caiga.
public class CafeteriaController {

    private static final Logger log = LoggerFactory.getLogger(CafeteriaController.class);

    private static final String ADMIN = "ADMIN";
    private static final String CAJERO = "CAJERO";
    private static final String BARISTA = "BARISTA";

    private static final String FLASH_MENSAJE = "cafeteriaMensaje";

    // Permisos por rol: para cambiar quién puede hacer qué basta con editar estas listas.
    private static final Set<String> VER_PANEL = Set.of(ADMIN, CAJERO, BARISTA);
    private static final Set<String> GESTIONAR_CATALOGO = Set.of(ADMIN);
    private static final Set<String> GESTIONAR_STOCK = Set.of(ADMIN, CAJERO);
    private static final Set<String> GESTIONAR_PEDIDOS = Set.of(ADMIN, CAJERO);
    private static final Set<String> CAMBIAR_ESTADO = Set.of(ADMIN, CAJERO, BARISTA);
    private static final Set<String> VER_VENTAS = Set.of(ADMIN, CAJERO);

    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;

    public CafeteriaController() {
        this(new CategoriaService(), new ProductoService(), new PedidoService(), new UsuarioRepository());
    }

    public CafeteriaController(CategoriaService categoriaService,
                               ProductoService productoService,
                               PedidoService pedidoService,
                               UsuarioRepository usuarioRepository) {
        this.categoriaService = Objects.requireNonNull(categoriaService);
        this.productoService = Objects.requireNonNull(productoService);
        this.pedidoService = Objects.requireNonNull(pedidoService);
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
    }

    @FunctionalInterface
    private interface Accion {
        // Ejecuta la operación y devuelve el mensaje de éxito para el usuario.
        String ejecutar() throws Exception;
    }

    // ---------------------------------------------------------------- GET

    public void showPanel(Context ctx) {
        if (!autorizar(ctx, VER_PANEL)) {
            return;
        }
        renderPanel(ctx, 200, null, Map.of());
    }

    public void filtrarVentas(Context ctx) {
        if (!autorizar(ctx, VER_VENTAS)) {
            return;
        }
        try {
            String desdeTxt = textoOVacio(ctx.queryParam("desde"));
            String hastaTxt = textoOVacio(ctx.queryParam("hasta"));

            // Sin fechas se muestra el panel normal, sin filtro.
            if (desdeTxt.isEmpty() && hastaTxt.isEmpty()) {
                renderPanel(ctx, 200, null, Map.of());
                return;
            }
            if (desdeTxt.isEmpty() || hastaTxt.isEmpty()) {
                throw new IllegalArgumentException("Debe indicar la fecha desde y la fecha hasta.");
            }

            LocalDate desde = parseFecha(desdeTxt, "desde");
            LocalDate hasta = parseFecha(hastaTxt, "hasta");
            if (desde.isAfter(hasta)) {
                throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
            }

            List<Pedido> ventas = pedidoService.filtrarVentas(desde, hasta);

            Map<String, Object> extra = new HashMap<>();
            extra.put("ventas", ventas);
            extra.put("desde", desdeTxt);
            extra.put("hasta", hastaTxt);
            renderPanel(ctx, 200, null, extra);
        } catch (IllegalArgumentException | IllegalStateException e) {
            renderPanel(ctx, 400, e.getMessage(), Map.of());
        } catch (Exception e) {
            log.error("Error al filtrar ventas de cafetería", e);
            renderPanel(ctx, 500, "Ocurrió un error inesperado. Intente nuevamente.", Map.of());
        }
    }

    // ---------------------------------------------------------------- POST: catálogo y stock

    public void registrarCategoria(Context ctx) {
        ejecutar(ctx, GESTIONAR_CATALOGO, () -> {
            categoriaService.registrarCategoria(
                    ctx.formParam("nombre"),
                    ctx.formParam("descripcion"));
            return "Categoría registrada correctamente.";
        });
    }

    public void registrarProducto(Context ctx) {
        ejecutar(ctx, GESTIONAR_CATALOGO, () -> {
            productoService.registrarProducto(
                    ctx.formParam("nombre"),
                    parseDecimal(ctx.formParam("precio"), "precio"),
                    parseEntero(ctx.formParam("stock"), "stock"),
                    parseId(ctx.formParam("categoriaId"), "categoría"));
            return "Producto registrado correctamente.";
        });
    }

    public void actualizarStock(Context ctx) {
        ejecutar(ctx, GESTIONAR_STOCK, () -> {
            productoService.actualizarStock(
                    parseId(ctx.pathParam("id"), "producto"),
                    parseEntero(ctx.formParam("stock"), "stock"));
            return "Stock actualizado correctamente.";
        });
    }

    // ---------------------------------------------------------------- POST: pedidos

    public void crearPedido(Context ctx) {
        ejecutar(ctx, GESTIONAR_PEDIDOS, () -> {
            Long usuarioId = ctx.sessionAttribute("usuarioId");
            Usuario usuario = usuarioRepository.findById(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Debe haber un usuario con sesión activa.");
            }
            Pedido pedido = pedidoService.crearPedido(usuario, leerItems(ctx));
            return "Pedido #" + pedido.getId() + " creado. Total: " + pedido.getTotal();
        });
    }

    public void agregarProductos(Context ctx) {
        ejecutar(ctx, GESTIONAR_PEDIDOS, () -> {
            Long pedidoId = parseId(ctx.pathParam("id"), "pedido");
            pedidoService.agregarProductos(pedidoId, leerItems(ctx));
            return "Productos agregados al pedido #" + pedidoId + ".";
        });
    }

    // PedidoService solo permite avanzar un paso en el flujo:
    // PENDIENTE -> EN_PREPARACION -> LISTO -> ENTREGADO.
    public void avanzarEstado(Context ctx) {
        ejecutar(ctx, CAMBIAR_ESTADO, () -> {
            Long pedidoId = parseId(ctx.pathParam("id"), "pedido");
            Pedido pedido = pedidoService.avanzarEstado(pedidoId);
            return "El pedido #" + pedidoId + " ahora está en estado " + pedido.getEstado() + ".";
        });
    }

    public void anularPedido(Context ctx) {
        ejecutar(ctx, GESTIONAR_PEDIDOS, () -> {
            Long pedidoId = parseId(ctx.pathParam("id"), "pedido");
            pedidoService.anularPedido(pedidoId);
            return "Pedido #" + pedidoId + " anulado. El stock fue devuelto.";
        });
    }

    // ---------------------------------------------------------------- Infraestructura

    // Flujo común de los POST: autoriza, ejecuta, y si sale bien redirige (PRG);
    // si falla muestra el error en la misma página.
    private void ejecutar(Context ctx, Set<String> rolesPermitidos, Accion accion) {
        if (!autorizar(ctx, rolesPermitidos)) {
            return;
        }
        try {
            String mensaje = accion.ejecutar();
            ctx.sessionAttribute(FLASH_MENSAJE, mensaje);
            ctx.redirect("/cafeteria");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Errores de validación o reglas de negocio de los servicios.
            renderPanel(ctx, 400, e.getMessage(), Map.of());
        } catch (Exception e) {
            log.error("Error inesperado en cafetería ({} {})", ctx.method(), ctx.path(), e);
            renderPanel(ctx, 500, "Ocurrió un error inesperado. Intente nuevamente.", Map.of());
        }
    }

    // Verifica sesión activa y rol permitido. Devuelve false si ya respondió.
    private boolean autorizar(Context ctx, Set<String> rolesPermitidos) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String rol = ctx.sessionAttribute("usuarioRol");

        if (usuarioId == null || rol == null) {
            ctx.redirect("/login");
            return false;
        }
        if (!rolesPermitidos.contains(rol)) {
            renderPanel(ctx, 403, "No tiene permisos para realizar esta acción.", Map.of());
            return false;
        }
        return true;
    }

    private void renderPanel(Context ctx, int status, String error, Map<String, Object> extra) {
        String rol = ctx.sessionAttribute("usuarioRol");
        boolean puedeVer = rol != null && VER_PANEL.contains(rol);

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Cafetería - SIGU-UCN");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("rol", rol == null ? "" : rol);
        model.put("puedeCatalogo", rol != null && GESTIONAR_CATALOGO.contains(rol));
        model.put("puedeStock", rol != null && GESTIONAR_STOCK.contains(rol));
        model.put("puedePedidos", rol != null && GESTIONAR_PEDIDOS.contains(rol));
        model.put("puedeEstado", rol != null && CAMBIAR_ESTADO.contains(rol));
        model.put("puedeVentas", rol != null && VER_VENTAS.contains(rol));

        // El mensaje de éxito viene de la sesión y se muestra una sola vez.
        String mensaje = ctx.sessionAttribute(FLASH_MENSAJE);
        ctx.sessionAttribute(FLASH_MENSAJE, null);
        model.put("mensaje", mensaje == null ? "" : mensaje);
        model.put("error", error == null ? "" : error);

        String errorCarga = "";
        if (puedeVer) {
            try {
                model.put("categorias", categoriaService.listarCategorias());
                model.put("productos", productoService.listarProductos());
                model.put("pedidos", pedidoService.listarHistorial());
            } catch (Exception e) {
                log.error("No se pudieron cargar los datos de cafetería", e);
                errorCarga = "No se pudieron cargar los datos de la cafetería.";
            }
        }
        model.putIfAbsent("categorias", Collections.emptyList());
        model.putIfAbsent("productos", Collections.emptyList());
        model.putIfAbsent("pedidos", Collections.emptyList());
        if (!errorCarga.isEmpty() && model.get("error").toString().isEmpty()) {
            model.put("error", errorCarga);
        }

        model.put("ventas", extra.getOrDefault("ventas", Collections.emptyList()));
        model.put("hayFiltro", extra.containsKey("ventas"));
        model.put("desde", extra.getOrDefault("desde", ""));
        model.put("hasta", extra.getOrDefault("hasta", ""));

        ctx.status(status);
        ctx.render("cafeteria/panel.jte", model);
    }

    // Lee los pares productoId/cantidad del formulario (campos repetidos).
    // Se ignoran las filas con cantidad vacía o 0; los repetidos se suman.
    private Map<Long, Integer> leerItems(Context ctx) {
        List<String> ids = ctx.formParams("productoId");
        List<String> cantidades = ctx.formParams("cantidad");
        if (ids.size() != cantidades.size()) {
            throw new IllegalArgumentException("Los productos y las cantidades no coinciden.");
        }

        Map<Long, Integer> items = new LinkedHashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            String idTxt = textoOVacio(ids.get(i));
            String cantTxt = textoOVacio(cantidades.get(i));
            if (cantTxt.isEmpty() || cantTxt.equals("0")) {
                continue;
            }
            if (idTxt.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar un producto para cada cantidad ingresada.");
            }
            int cantidad = parseEntero(cantTxt, "cantidad");
            if (cantidad < 0) {
                throw new IllegalArgumentException("La cantidad no puede ser negativa.");
            }
            items.merge(parseId(idTxt, "producto"), cantidad, Integer::sum);
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto con cantidad mayor a cero.");
        }
        return items;
    }

    private static String textoOVacio(String valor) {
        return valor == null ? "" : valor.strip();
    }

    private static Long parseId(String valor, String campo) {
        try {
            long id = Long.parseLong(textoOVacio(valor));
            if (id <= 0) {
                throw new IllegalArgumentException("Debe seleccionar un valor válido para " + campo + ".");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe seleccionar un valor válido para " + campo + ".");
        }
    }

    private static Integer parseEntero(String valor, String campo) {
        try {
            return Integer.valueOf(textoOVacio(valor));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El valor de " + campo + " debe ser un número entero.");
        }
    }

    private static BigDecimal parseDecimal(String valor, String campo) {
        try {
            return new BigDecimal(textoOVacio(valor).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El valor de " + campo + " debe ser un número válido.");
        }
    }

    private static LocalDate parseFecha(String valor, String campo) {
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha " + campo + " no es válida (use el formato AAAA-MM-DD).");
        }
    }
}
