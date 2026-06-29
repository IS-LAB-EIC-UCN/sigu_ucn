package cl.ucn.app.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.persistence.EntityManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integracion que validan la interaccion entre las 3 capas
 * (presentacion, servicio, persistencia) via HTTP.
 *
 * Requiere que la aplicacion este levantada (URL por defecto localhost:7000,
 * Si no esta, el setUp() falla con un mensaje claro.
 *
 * Para correr la app: mvn exec:java -Dexec.mainClass="cl.ucn.app.main.Main"
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BibliotecaIT {

    private static final String BASE_URL =
        System.getenv().getOrDefault("BIBLIOTECA_TEST_URL", "http://localhost:7000");
    private HttpClient client;

    @BeforeAll
    public void setUp() throws Exception {
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                "La aplicacion no esta levantada en " + BASE_URL + ". " +
                "Ejecuta: mvn exec:java -Dexec.mainClass=cl.ucn.app.main.Main");
        }
    }

    @Test
    public void testLoginPage_respondeCon200() throws Exception {
        HttpResponse<String> response = get("/login");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().toLowerCase().contains("iniciar")
                || response.body().toLowerCase().contains("login"));
    }



    @Test
    public void testBibliotecaLibros_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/libros");
        assertEquals(302, response.statusCode());
        assertTrue(response.headers().firstValue("Location").orElse("").contains("/login"));
    }

    @Test
    public void testBibliotecaHistorial_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/historial");
        assertEquals(302, response.statusCode());
    }

    @Test
    public void testBibliotecaLectores_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/lectores");
        assertEquals(302, response.statusCode());
    }

    @Test
    public void testBibliotecaMisDatos_respondeConLogin302() throws Exception {
        HttpResponse<String> response = get("/biblioteca/mis-datos");
        assertEquals(302, response.statusCode());
    }

    @Test
    public void testAdminFlow_crearEditarEliminarLibro() throws Exception {
        // Limpiar registros antiguos para evitar duplicados
        EntityManager emCleanup = cl.ucn.app.config.JPAUtil.getEntityManager();
        emCleanup.getTransaction().begin();
        emCleanup.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-3-16-148410-0')))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-3-16-148410-0'))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-3-16-148410-0')")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM libro WHERE isbn = '978-3-16-148410-0'")
                 .executeUpdate();
        emCleanup.getTransaction().commit();
        emCleanup.close();

        login("admin@sigu.cl", "admin123");
        
        // 1. Registrar un libro
        String postData = "titulo=" + URLEncoder.encode("Libro Integracion", StandardCharsets.UTF_8) +
                "&autor=" + URLEncoder.encode("Autor Integracion", StandardCharsets.UTF_8) +
                "&categoria=" + URLEncoder.encode("Ciencia", StandardCharsets.UTF_8) +
                "&isbn=" + URLEncoder.encode("978-3-16-148410-0", StandardCharsets.UTF_8);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/libros/nuevo"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Buscar el libro creado en base de datos
        cl.ucn.app.repository.biblioteca.LibroRepository repo = new cl.ucn.app.repository.biblioteca.LibroRepository();
        List<cl.ucn.app.model.biblioteca.Libro> libros = repo.findAll();
        cl.ucn.app.model.biblioteca.Libro creado = libros.stream()
                .filter(l -> "978-3-16-148410-0".equals(l.getIsbn()))
                .findFirst()
                .orElse(null);
        assertNotNull(creado, "El libro debería haber sido guardado en la base de datos");

        // 2. Editar el libro
        String editData = "id=" + creado.getId() +
                "&titulo=" + URLEncoder.encode("Libro Integracion Editado", StandardCharsets.UTF_8) +
                "&autor=" + URLEncoder.encode("Autor Editado", StandardCharsets.UTF_8) +
                "&categoria=" + URLEncoder.encode("Ciencia", StandardCharsets.UTF_8) +
                "&isbn=" + URLEncoder.encode("978-3-16-148410-0", StandardCharsets.UTF_8);
        
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/libros/editar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(editData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar cambio en base de datos
        cl.ucn.app.model.biblioteca.Libro editado = repo.findById(creado.getId());
        assertEquals("Libro Integracion Editado", editado.getTitulo());

        // 3. Eliminar el libro
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/libros/eliminar?id=" + creado.getId()))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar eliminacion
        assertNull(repo.findById(creado.getId()));
        logout();
    }

    @Test
    public void testLectorFlow_solicitarPrestamoYDevolver() throws Exception {
        // Limpiar registros antiguos para evitar duplicados
        EntityManager emCleanup = cl.ucn.app.config.JPAUtil.getEntityManager();
        emCleanup.getTransaction().begin();
        emCleanup.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9')))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9'))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9')")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM libro WHERE isbn = '978-0-12-345678-9'")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE lector_id IN (SELECT id FROM lector WHERE correo = 'docente@sigu.cl'))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM prestamo WHERE lector_id IN (SELECT id FROM lector WHERE correo = 'docente@sigu.cl')")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM lector WHERE correo = 'docente@sigu.cl'")
                 .executeUpdate();
        emCleanup.getTransaction().commit();
        emCleanup.close();

        // Asegurar que existe un libro y ejemplar disponible
        cl.ucn.app.repository.biblioteca.LibroRepository libroRepo = new cl.ucn.app.repository.biblioteca.LibroRepository();
        cl.ucn.app.repository.biblioteca.EjemplarRepository ejemplarRepo = new cl.ucn.app.repository.biblioteca.EjemplarRepository();
        
        cl.ucn.app.model.biblioteca.Libro libro = new cl.ucn.app.model.biblioteca.Libro();
        libro.setTitulo("Libro Test Prestamo");
        libro.setAutor("Autor Test");
        libro.setCategoria("Ciencias");
        libro.setIsbn("978-0-12-345678-9");
        libroRepo.save(libro);

        cl.ucn.app.model.biblioteca.Ejemplar ejemplar = new cl.ucn.app.model.biblioteca.Ejemplar();
        ejemplar.setLibro(libro);
        ejemplar.setEstado(cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE);
        ejemplarRepo.save(ejemplar);

        // Crear lector directamente en la BD para el test
        cl.ucn.app.repository.biblioteca.LectorRepository lectorRepo = new cl.ucn.app.repository.biblioteca.LectorRepository();
        cl.ucn.app.model.biblioteca.Lector lector = new cl.ucn.app.model.biblioteca.Lector();
        lector.setNombre("Docente Demo");
        lector.setCorreo("docente@sigu.cl");
        lector.setRut("12345678-5");
        lector.setBloqueado(false);
        lectorRepo.save(lector);

        // 1. Iniciar sesion como lector
        login("docente@sigu.cl", "docente123");

        // Solicitar préstamo (Reserva)
        String prestamoData = "libroId=" + libro.getId() + "&fechaVencimiento=" + LocalDate.now().plusDays(5).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/nuevo"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(prestamoData))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que el ejemplar quede PRESTADO (reservado)
        cl.ucn.app.model.biblioteca.Ejemplar updatedEjemplar = ejemplarRepo.findById(ejemplar.getId());
        assertEquals(cl.ucn.app.model.biblioteca.EstadoEjemplar.PRESTADO, updatedEjemplar.getEstado());

        // Buscar el prestamo creado y verificar que esté en SOLICITADO
        cl.ucn.app.repository.biblioteca.PrestamoLibroRepository prestamoRepo = new cl.ucn.app.repository.biblioteca.PrestamoLibroRepository();
        cl.ucn.app.model.biblioteca.PrestamoLibro prestamo = prestamoRepo.findAll().stream()
                .filter(p -> p.getEjemplar().getId().equals(ejemplar.getId()) && p.getEstado() == cl.ucn.app.model.biblioteca.EstadoPrestamo.SOLICITADO)
                .findFirst()
                .orElse(null);
        assertNotNull(prestamo);

        // 2. Iniciar sesion como admin para confirmar la entrega (retiro físico)
        logout();
        login("admin@sigu.cl", "admin123");

        String confirmarEntregaData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/confirmar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(confirmarEntregaData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que el préstamo pase a ACTIVO
        cl.ucn.app.model.biblioteca.PrestamoLibro activePrestamo = prestamoRepo.findById(prestamo.getId());
        assertEquals(cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO, activePrestamo.getEstado());

        // 3. Iniciar sesión como lector para solicitar devolución
        logout();
        login("docente@sigu.cl", "docente123");

        String devolverData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/devolver"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(devolverData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que el préstamo pase a PENDIENTE_DEVOLUCION
        cl.ucn.app.model.biblioteca.PrestamoLibro pendingReturnPrestamo = prestamoRepo.findById(prestamo.getId());
        assertEquals(cl.ucn.app.model.biblioteca.EstadoPrestamo.PENDIENTE_DEVOLUCION, pendingReturnPrestamo.getEstado());

        // 4. Iniciar sesión como admin para confirmar devolución (recepción física)
        logout();
        login("admin@sigu.cl", "admin123");

        String finalizarData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/finalizar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(finalizarData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que el ejemplar vuelva a estar DISPONIBLE y el prestamo FINALIZADO
        updatedEjemplar = ejemplarRepo.findById(ejemplar.getId());
        assertEquals(cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE, updatedEjemplar.getEstado());
        
        cl.ucn.app.model.biblioteca.PrestamoLibro updatedPrestamo = prestamoRepo.findById(prestamo.getId());
        assertEquals(cl.ucn.app.model.biblioteca.EstadoPrestamo.FINALIZADO, updatedPrestamo.getEstado());

        // Limpiar
        EntityManager em = cl.ucn.app.config.JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM multa WHERE prestamo_id = ?").setParameter(1, prestamo.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM prestamo WHERE id = ?").setParameter(1, prestamo.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM ejemplar WHERE id = ?").setParameter(1, ejemplar.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM libro WHERE id = ?").setParameter(1, libro.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM lector WHERE id = ?").setParameter(1, lector.getId()).executeUpdate();
        em.getTransaction().commit();
        em.close();
        logout();
    }

    @Test
    public void testLectorFlow_multaPorAtrasoyPago() throws Exception {
        // Limpiar registros antiguos para evitar duplicados
        EntityManager emCleanup = cl.ucn.app.config.JPAUtil.getEntityManager();
        emCleanup.getTransaction().begin();
        emCleanup.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9')))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9'))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM ejemplar WHERE libro_id IN (SELECT id FROM libro WHERE isbn = '978-0-12-345678-9')")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM libro WHERE isbn = '978-0-12-345678-9'")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE lector_id IN (SELECT id FROM lector WHERE correo = 'estudiante@sigu.cl'))")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM prestamo WHERE lector_id IN (SELECT id FROM lector WHERE correo = 'estudiante@sigu.cl')")
                 .executeUpdate();
        emCleanup.createNativeQuery("DELETE FROM lector WHERE correo = 'estudiante@sigu.cl'")
                 .executeUpdate();
        emCleanup.getTransaction().commit();
        emCleanup.close();

        // Asegurar que existe un libro y ejemplar disponible
        cl.ucn.app.repository.biblioteca.LibroRepository libroRepo = new cl.ucn.app.repository.biblioteca.LibroRepository();
        cl.ucn.app.repository.biblioteca.EjemplarRepository ejemplarRepo = new cl.ucn.app.repository.biblioteca.EjemplarRepository();
        
        cl.ucn.app.model.biblioteca.Libro libro = new cl.ucn.app.model.biblioteca.Libro();
        libro.setTitulo("Libro Test Multa");
        libro.setAutor("Autor Multa");
        libro.setCategoria("Ciencias");
        libro.setIsbn("978-0-12-345678-9");
        libroRepo.save(libro);

        cl.ucn.app.model.biblioteca.Ejemplar ejemplar = new cl.ucn.app.model.biblioteca.Ejemplar();
        ejemplar.setLibro(libro);
        ejemplar.setEstado(cl.ucn.app.model.biblioteca.EstadoEjemplar.DISPONIBLE);
        ejemplarRepo.save(ejemplar);

        // Crear lector directamente en la BD para el test (usando el correo del estudiante del seed)
        cl.ucn.app.repository.biblioteca.LectorRepository lectorRepo = new cl.ucn.app.repository.biblioteca.LectorRepository();
        cl.ucn.app.model.biblioteca.Lector lector = new cl.ucn.app.model.biblioteca.Lector();
        lector.setNombre("Estudiante Demo");
        lector.setCorreo("estudiante@sigu.cl");
        lector.setRut("87654321-4");
        lector.setBloqueado(false);
        lectorRepo.save(lector);

        // 1. Iniciar sesion como lector (estudiante)
        login("estudiante@sigu.cl", "estudiante123");

        // Solicitar préstamo (Reserva)
        String prestamoData = "libroId=" + libro.getId() + "&fechaVencimiento=" + LocalDate.now().plusDays(5).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/nuevo"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(prestamoData))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Buscar el prestamo creado y verificar que esté en SOLICITADO
        cl.ucn.app.repository.biblioteca.PrestamoLibroRepository prestamoRepo = new cl.ucn.app.repository.biblioteca.PrestamoLibroRepository();
        cl.ucn.app.model.biblioteca.PrestamoLibro prestamo = prestamoRepo.findAll().stream()
                .filter(p -> p.getEjemplar().getId().equals(ejemplar.getId()) && p.getEstado() == cl.ucn.app.model.biblioteca.EstadoPrestamo.SOLICITADO)
                .findFirst()
                .orElse(null);
        assertNotNull(prestamo);

        // MODIFICAR LA FECHA DE VENCIMIENTO DEL PRESTAMO DIRECTAMENTE EN BD A HACE 5 DIAS
        EntityManager emModify = cl.ucn.app.config.JPAUtil.getEntityManager();
        emModify.getTransaction().begin();
        emModify.createNativeQuery("UPDATE prestamo SET fecha_vencimiento = ? WHERE id = ?")
                .setParameter(1, java.sql.Date.valueOf(LocalDate.now().minusDays(5)))
                .setParameter(2, prestamo.getId())
                .executeUpdate();
        emModify.getTransaction().commit();
        emModify.close();

        // 2. Iniciar sesion como admin para confirmar la entrega (retiro físico)
        logout();
        login("admin@sigu.cl", "admin123");

        String confirmarEntregaData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/confirmar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(confirmarEntregaData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // 3. Iniciar sesión como lector para solicitar devolución
        logout();
        login("estudiante@sigu.cl", "estudiante123");

        String devolverData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/devolver"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(devolverData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // 4. Iniciar sesión como admin para confirmar devolución (recepción física y cobro de multa)
        logout();
        login("admin@sigu.cl", "admin123");

        String finalizarData = "prestamoId=" + prestamo.getId();
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/prestamo/finalizar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(finalizarData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que la multa haya sido generada y el lector esté bloqueado en la base de datos
        cl.ucn.app.repository.biblioteca.MultaRepository multaRepo = new cl.ucn.app.repository.biblioteca.MultaRepository();
        cl.ucn.app.model.biblioteca.Multa multa = multaRepo.findByPrestamo(prestamo);
        assertNotNull(multa, "La multa debería haber sido generada por devolución atrasada");
        assertFalse(multa.getPagada());

        cl.ucn.app.model.biblioteca.Lector updatedLector = lectorRepo.findById(lector.getId());
        assertTrue(updatedLector.isBloqueado(), "El lector con multa pendiente debería quedar bloqueado");

        // 5. Pagar la multa como admin
        String payData = "multaId=" + multa.getId() + "&returnTo=" + URLEncoder.encode("/biblioteca/historial", StandardCharsets.UTF_8);
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/biblioteca/multa/pagar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(payData))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());

        // Verificar que la multa esté pagada y el lector desbloqueado
        cl.ucn.app.model.biblioteca.Multa updatedMulta = multaRepo.findById(multa.getId());
        assertTrue(updatedMulta.getPagada(), "La multa debería estar pagada");

        updatedLector = lectorRepo.findById(lector.getId());
        assertFalse(updatedLector.isBloqueado(), "El lector debería estar desbloqueado tras pagar su única multa");

        // Limpiar
        EntityManager em = cl.ucn.app.config.JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM multa WHERE id = ?").setParameter(1, multa.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM prestamo WHERE id = ?").setParameter(1, prestamo.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM ejemplar WHERE id = ?").setParameter(1, ejemplar.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM libro WHERE id = ?").setParameter(1, libro.getId()).executeUpdate();
        em.createNativeQuery("DELETE FROM lector WHERE id = ?").setParameter(1, lector.getId()).executeUpdate();
        em.getTransaction().commit();
        em.close();
        logout();
    }

    private void login(String correo, String password) throws Exception {
        String formData = "correo=" + URLEncoder.encode(correo, StandardCharsets.UTF_8) +
                "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());
    }

    private void logout() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/logout"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, response.statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
