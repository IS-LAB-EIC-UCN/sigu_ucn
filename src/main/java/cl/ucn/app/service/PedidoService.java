package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.DetallePedido;
import cl.ucn.app.model.Pedido;
import cl.ucn.app.model.Producto;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.PedidoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

public class PedidoService {

    static final List<String> FLUJO_ESTADOS = List.of("PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO");
    static final String ANULADO = "ANULADO";

    private final PedidoRepository pedidoRepository;

    public PedidoService() {
        this(new PedidoRepository());
    }

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = Objects.requireNonNull(pedidoRepository);
    }

    public List<Pedido> listarHistorial() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPedido(Long pedidoId) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }
        Pedido pedido = pedidoRepository.findById(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido seleccionado no existe.");
        }
        return pedido;
    }

    public List<Pedido> filtrarVentas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debe indicar fecha de inicio y de término.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de término.");
        }
        return pedidoRepository.findByFechaBetween(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay().minusNanos(1000))
                .stream().filter(p -> !ANULADO.equals(p.getEstado())).toList();
    }

    public Pedido crearPedido(Usuario usuario, Map<Long, Integer> productos) {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Debe haber un usuario con sesión activa.");
        }
        validarProductos(productos);

        return enTransaccion(em -> {
            Pedido pedido = new Pedido(em.getReference(Usuario.class, usuario.getId()));
            // orden por id: dos pedidos simultáneos bloquean los productos en el mismo orden y no se traban
            for (Map.Entry<Long, Integer> item : new TreeMap<>(productos).entrySet()) {
                Producto producto = descontarStock(em, item.getKey(), item.getValue());
                pedido.getDetalles().add(new DetallePedido(pedido, producto, item.getValue(), producto.getPrecio()));
            }
            pedido.recalcularTotal();
            em.persist(pedido);
            return pedido;
        });
    }

    public Pedido agregarProductos(Long pedidoId, Map<Long, Integer> productos) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }
        validarProductos(productos);

        return enTransaccion(em -> {
            Pedido pedido = em.find(Pedido.class, pedidoId, LockModeType.PESSIMISTIC_WRITE);
            if (pedido == null) {
                throw new IllegalArgumentException("El pedido seleccionado no existe.");
            }
            if (!"PENDIENTE".equals(pedido.getEstado())) {
                throw new IllegalArgumentException("Solo se pueden agregar productos a pedidos pendientes.");
            }
            for (Map.Entry<Long, Integer> item : new TreeMap<>(productos).entrySet()) {
                Producto producto = descontarStock(em, item.getKey(), item.getValue());
                DetallePedido existente = pedido.getDetalles().stream()
                        .filter(d -> d.getProducto().getId().equals(producto.getId()))
                        .findFirst().orElse(null);
                if (existente != null) {
                    // se mantiene el precio registrado al crear el pedido (decisión del grupo)
                    existente.setCantidad(existente.getCantidad() + item.getValue());
                } else {
                    pedido.getDetalles().add(new DetallePedido(pedido, producto, item.getValue(), producto.getPrecio()));
                }
            }
            pedido.recalcularTotal();
            return pedido;
        });
    }

    public Pedido avanzarEstado(Long pedidoId) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }
        return enTransaccion(em -> {
            Pedido pedido = em.find(Pedido.class, pedidoId, LockModeType.PESSIMISTIC_WRITE);
            if (pedido == null) {
                throw new IllegalArgumentException("El pedido seleccionado no existe.");
            }
            int actual = FLUJO_ESTADOS.indexOf(pedido.getEstado());
            // indexOf da -1 para ANULADO (o estados fuera del flujo) y el último índice para ENTREGADO
            if (actual < 0 || actual == FLUJO_ESTADOS.size() - 1) {
                throw new IllegalArgumentException("El pedido ya está " + pedido.getEstado() + ", no puede avanzar.");
            }
            pedido.setEstado(FLUJO_ESTADOS.get(actual + 1));
            return pedido;
        });
    }

    public Pedido anularPedido(Long pedidoId) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }
        return enTransaccion(em -> {
            Pedido pedido = em.find(Pedido.class, pedidoId, LockModeType.PESSIMISTIC_WRITE);
            if (pedido == null) {
                throw new IllegalArgumentException("El pedido seleccionado no existe.");
            }
            if ("ENTREGADO".equals(pedido.getEstado()) || ANULADO.equals(pedido.getEstado())) {
                throw new IllegalArgumentException("El pedido ya está " + pedido.getEstado() + ", no se puede anular.");
            }
            List<DetallePedido> detalles = new ArrayList<>(pedido.getDetalles());
            detalles.sort(Comparator.comparing(d -> d.getProducto().getId()));
            for (DetallePedido d : detalles) {
                Producto producto = d.getProducto();
                em.refresh(producto, LockModeType.PESSIMISTIC_WRITE);
                producto.setStock(producto.getStock() + d.getCantidad());
            }
            pedido.setEstado(ANULADO);
            return pedido;
        });
    }

    private void validarProductos(Map<Long, Integer> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }
        for (Map.Entry<Long, Integer> item : productos.entrySet()) {
            if (item.getKey() == null || item.getKey() <= 0) {
                throw new IllegalArgumentException("Debe seleccionar un producto válido.");
            }
            if (item.getValue() == null || item.getValue() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
        }
    }

    private Producto descontarStock(EntityManager em, Long productoId, int cantidad) {
        Producto producto = em.find(Producto.class, productoId);
        if (producto == null) {
            throw new IllegalArgumentException("El producto seleccionado no existe.");
        }
        // refresh y no find con bloqueo: si el producto ya estaba cargado (agregarProductos), find no relee el stock
        em.refresh(producto, LockModeType.PESSIMISTIC_WRITE);
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()
                    + ": quedan " + producto.getStock() + ".");
        }
        producto.setStock(producto.getStock() - cantidad);
        return producto;
    }

    private <T> T enTransaccion(Function<EntityManager, T> trabajo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = trabajo.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

}
