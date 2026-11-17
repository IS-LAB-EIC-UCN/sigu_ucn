package cl.ucn.app.controller.soporte;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoporteControllerTest {

    @Mock
    private Context ctx;

    private final SoporteController controller = new SoporteController();

    @BeforeEach
    void setUp() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(4L);
        when(ctx.sessionAttribute("soporteCsrf")).thenReturn("token");
        when(ctx.header("X-CSRF-Token")).thenReturn("token");
        when(ctx.status(anyInt())).thenReturn(ctx);
        lenient().when(ctx.bodyAsClass(any())).thenAnswer(invocacion -> {
            throw new JsonParseException(null, "JSON mal formado");
        });
    }

    @Test
    void cerrarTicket_cuerpoJsonInvalido_responde400() {
        when(ctx.pathParam("id")).thenReturn("1");

        controller.cerrarTicket(ctx);

        verify(ctx).status(400);
    }

    @Test
    void iniciarResolucion_idNoNumerico_responde400ConMensajeClaro() {
        when(ctx.pathParam("id")).thenReturn("abc");

        controller.iniciarResolucion(ctx);

        verify(ctx).status(400);
        verify(ctx).json(argThat(respuesta ->
                "Los datos enviados no son válidos: Id de ticket no válido.".equals(((Map<?, ?>) respuesta).get("mensaje"))));
    }

    @Test
    void asignarTecnico_cuerpoJsonInvalido_responde400() {
        when(ctx.pathParam("id")).thenReturn("1");

        controller.asignarTecnico(ctx);

        verify(ctx).status(400);
    }
}
