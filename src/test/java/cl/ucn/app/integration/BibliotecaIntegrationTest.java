package cl.ucn.app.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BibliotecaIntegrationTest {

    private static final String BASE_URL = "http://localhost:7002";
    private HttpClient client;

    @BeforeAll
    public void setUp() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Test
    public void testLoginPage_respondeCon200() throws Exception {
        HttpResponse<String> response = get("/login");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Iniciar sesion") || response.body().contains("Iniciar"));
    }

    @Test
    public void testBibliotecaLibros_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/libros");
        assertEquals(302, response.statusCode());
        assertTrue(response.headers().firstValue("Location").orElse("").contains("/login"));
    }

    @Test
    public void testBibliotecaRegistrarLibro_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/libros/nuevo");
        assertEquals(302, response.statusCode());
    }

    @Test
    public void testBibliotecaLectores_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/lectores");
        assertEquals(302, response.statusCode());
    }

    @Test
    public void testBibliotecaEjemplares_sinSesion_redirigeALogin() throws Exception {
        HttpResponse<String> response = get("/biblioteca/ejemplares/nuevo");
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
