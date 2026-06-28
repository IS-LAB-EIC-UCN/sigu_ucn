package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.service.biblioteca.BusquedaService;
import cl.ucn.app.service.biblioteca.EjemplarService;
import cl.ucn.app.service.biblioteca.LibroService;
import cl.ucn.app.service.biblioteca.api.IEjemplarService;
import cl.ucn.app.service.biblioteca.api.ILibroService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoController {

    private final ILibroService libroService = new LibroService();
    private final IEjemplarService ejemplarService = new EjemplarService();
    private final ILibroRepository libroRepository = new LibroRepository();
    private final IEjemplarRepository ejemplarRepository = new EjemplarRepository();
    private final cl.ucn.app.service.biblioteca.api.IBusquedaService busquedaService = new BusquedaService();

    public void listar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        String usuarioRol = ctx.sessionAttribute("usuarioRol");

        String search = ctx.queryParam("search");
        String categoria = ctx.queryParam("categoria");
        String soloDisponiblesParam = ctx.queryParam("soloDisponibles");
        boolean soloDisponibles = soloDisponiblesParam != null && (soloDisponiblesParam.equals("on") || soloDisponiblesParam.equals("true"));

        List<Libro> libros = libroService.listarTodos();
        Map<Long, Integer> totalEjemplares = new HashMap<>();
        Map<Long, Integer> disponibles = new HashMap<>();
        for (Libro libro : libros) {
            List<Ejemplar> ejemplares = ejemplarRepository.findByLibro(libro);
            totalEjemplares.put(libro.getId(), ejemplares.size());
            int disp = 0;
            for (Ejemplar e : ejemplares) {
                if (cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE == e.getEstado()) {
                    disp++;
                }
            }
            disponibles.put(libro.getId(), disp);
        }

        List<Libro> librosFiltrados = new java.util.ArrayList<>();
        for (Libro libro : libros) {
            boolean matchesSearch = true;
            if (search != null && !search.isBlank()) {
                String term = search.toLowerCase().trim();
                matchesSearch = libro.getTitulo().toLowerCase().contains(term)
                        || libro.getAutor().toLowerCase().contains(term);
            }

            boolean matchesCategory = true;
            if (categoria != null && !categoria.isBlank()) {
                matchesCategory = libro.getCategoria().equalsIgnoreCase(categoria.trim());
            }

            boolean matchesAvailability = true;
            if (soloDisponibles) {
                Integer disp = disponibles.get(libro.getId());
                matchesAvailability = disp != null && disp > 0;
            }

            if (matchesSearch && matchesCategory && matchesAvailability) {
                librosFiltrados.add(libro);
            }
        }

        int totalLibrosCount = libros.size();
        int totalEjemplaresCount = 0;
        for (Libro l : libros) {
            Integer tot = totalEjemplares.get(l.getId());
            if (tot != null) {
                totalEjemplaresCount += tot;
            }
        }

        int prestamosActivosCount = 0;
        int prestamosAtrasadosCount = 0;
        double totalMultasAcumuladas = 0.0;

        cl.ucn.app.repository.biblioteca.PrestamoLibroRepository prestamoRepo = new cl.ucn.app.repository.biblioteca.PrestamoLibroRepository();
        for (cl.ucn.app.model.biblioteca.PrestamoLibro p : prestamoRepo.findAll()) {
            if (cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO == p.getEstado()) {
                prestamosActivosCount++;
                if (p.getFechaVencimiento() != null && p.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
                    prestamosAtrasadosCount++;
                }
            }
        }

        cl.ucn.app.repository.biblioteca.MultaRepository multaRepo = new cl.ucn.app.repository.biblioteca.MultaRepository();
        for (cl.ucn.app.model.biblioteca.Multa m : multaRepo.findAll()) {
            if (!m.getPagada()) {
                totalMultasAcumuladas += m.getMonto().doubleValue();
            }
        }

        double lectorMultasPendientes = 0.0;
        cl.ucn.app.model.biblioteca.Lector lector = cl.ucn.app.auth.LectorSessionHelper.obtenerExistente(ctx);
        if (lector != null) {
            for (cl.ucn.app.model.biblioteca.Multa m : multaRepo.findPendientesByLector(lector)) {
                lectorMultasPendientes += m.getMonto().doubleValue();
            }
        }

        List<cl.ucn.app.model.biblioteca.PrestamoLibro> entregasPendientes = new java.util.ArrayList<>();
        List<cl.ucn.app.model.biblioteca.PrestamoLibro> devolucionesPendientes = new java.util.ArrayList<>();
        if ("ADMIN".equals(usuarioRol)) {
            for (cl.ucn.app.model.biblioteca.PrestamoLibro p : prestamoRepo.findAll()) {
                if (cl.ucn.app.model.biblioteca.EstadoPrestamo.SOLICITADO == p.getEstado()) {
                    entregasPendientes.add(p);
                } else if (cl.ucn.app.model.biblioteca.EstadoPrestamo.PENDIENTE_DEVOLUCION == p.getEstado()) {
                    devolucionesPendientes.add(p);
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("usuarioRol", usuarioRol);
        model.put("libros", librosFiltrados);
        model.put("totalEjemplares", totalEjemplares);
        model.put("disponibles", disponibles);

        model.put("search", search != null ? search : "");
        model.put("categoria", categoria != null ? categoria : "");
        model.put("soloDisponibles", soloDisponibles);

        model.put("kpiTotalLibros", totalLibrosCount);
        model.put("kpiTotalEjemplares", totalEjemplaresCount);
        model.put("kpiPrestamosActivos", prestamosActivosCount);
        model.put("kpiPrestamosAtrasados", prestamosAtrasadosCount);
        model.put("kpiTotalMultas", totalMultasAcumuladas);
        model.put("lectorMultasPendientes", lectorMultasPendientes);
        model.put("entregasPendientes", entregasPendientes);
        model.put("devolucionesPendientes", devolucionesPendientes);
        model.put("asignado", "1".equals(ctx.queryParam("asignado")));

        ctx.render("biblioteca/libros.jte", model);
    }

    public void formularioRegistrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("error", null);
        model.put("titulo", "");
        model.put("autor", "");
        model.put("categoria", "");
        model.put("isbn", "");
        ctx.render("biblioteca/registrar-libro.jte", model);
    }

    public void registrarLibro(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String titulo = ctx.formParam("titulo");
        String autor = ctx.formParam("autor");
        String categoria = ctx.formParam("categoria");
        String isbn = ctx.formParam("isbn");

        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()
                || categoria == null || categoria.isBlank() || isbn == null || isbn.isBlank()) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", "Todos los campos son obligatorios");
            model.put("titulo", titulo);
            model.put("autor", autor);
            model.put("categoria", categoria);
            model.put("isbn", isbn);
            ctx.render("biblioteca/registrar-libro.jte", model);
            return;
        }

        try {
            libroService.registrarLibro(titulo.trim(), autor.trim(), categoria.trim(), isbn.trim());
            ctx.redirect("/biblioteca/libros");
        } catch (ValidacionException e) {
            String msg = e.getMessage();
            String isbnPersistido = msg.toLowerCase().contains("isbn") ? "" : isbn;
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("error", e.getMessage());
            model.put("titulo", titulo);
            model.put("autor", autor);
            model.put("categoria", categoria);
            model.put("isbn", isbnPersistido);
            ctx.render("biblioteca/registrar-libro.jte", model);
        }
    }

    public void formularioRegistrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        List<Libro> libros = libroService.listarTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("usuarioNombre", usuarioNombre);
        model.put("libros", libros);
        model.put("error", null);
        model.put("mensaje", null);
        model.put("libroId", "");
        ctx.render("biblioteca/registrar-ejemplar.jte", model);
    }

    public void registrarEjemplar(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String libroIdStr = ctx.formParam("libroId");
        String cantidadStr = ctx.formParam("cantidad");

        List<Libro> libros = libroService.listarTodos();
        try {
            Long libroId = Long.parseLong(libroIdStr);
            int cantidad = Integer.parseInt(cantidadStr != null ? cantidadStr : "1");
            if (cantidad < 1 || cantidad > 100) {
                throw new ValidacionException("La cantidad debe estar entre 1 y 100");
            }
            ejemplarService.agregarEjemplares(libroId, cantidad);
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", null);
            model.put("mensaje", cantidad + (cantidad == 1 ? " ejemplar registrado" : " ejemplares registrados") + " exitosamente");
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        } catch (ValidacionException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", e.getMessage());
            model.put("mensaje", null);
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        } catch (NumberFormatException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("usuarioNombre", usuarioNombre);
            model.put("libros", libros);
            model.put("error", "Cantidad invalida");
            model.put("mensaje", null);
            model.put("libroId", libroIdStr);
            ctx.render("biblioteca/registrar-ejemplar.jte", model);
        }
    }

    public void getEjemplaresJson(Context ctx) {
        Long libroId = Long.parseLong(ctx.pathParam("id"));
        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            ctx.status(404);
            ctx.contentType("application/json");
            ctx.result("{\"error\":\"Libro no encontrado\"}");
            return;
        }
        List<Ejemplar> ejemplares = ejemplarRepository.findByLibro(libro);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        cl.ucn.app.repository.biblioteca.PrestamoLibroRepository prestamoRepo = new cl.ucn.app.repository.biblioteca.PrestamoLibroRepository();
        for (Ejemplar e : ejemplares) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", e.getId());
            item.put("estado", e.getEstado().name());
            if (cl.ucn.app.model.biblioteca.EstadoEjemplar.PRESTADO == e.getEstado()) {
                cl.ucn.app.model.biblioteca.PrestamoLibro p = prestamoRepo.findActivoByEjemplar(e);
                if (p != null && p.getFechaVencimiento() != null) {
                    item.put("fechaVencimiento", p.getFechaVencimiento().toString());
                } else {
                    item.put("fechaVencimiento", "N/A");
                }
            } else {
                item.put("fechaVencimiento", null);
            }
            result.add(item);
        }
        ctx.contentType("application/json");
        ctx.result(serializeListToJson(result));
    }

    private String serializeListToJson(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{");
            Map<String, Object> map = list.get(i);
            int j = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (j > 0) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                Object val = entry.getValue();
                if (val == null) {
                    sb.append("null");
                } else if (val instanceof Number || val instanceof Boolean) {
                    sb.append(val);
                } else {
                    String escaped = val.toString().replace("\"", "\\\"");
                    sb.append("\"").append(escaped).append("\"");
                }
                j++;
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}
