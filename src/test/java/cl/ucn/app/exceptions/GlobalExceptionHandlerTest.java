package cl.ucn.app.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    @Test
    void businessException_llevaStatusCorrecto() {
        BusinessException e = new ValidacionException("ISBN duplicado");
        assertEquals(400, e.getStatus().getCode());
        assertEquals("ISBN duplicado", e.getMessage());
    }

    @Test
    void recursoNoEncontrado_lleva404() {
        BusinessException e = new RecursoNoEncontradoException("Lector no existe");
        assertEquals(404, e.getStatus().getCode());
    }

    @Test
    void conflictoEstado_lleva409() {
        BusinessException e = new ConflictoEstadoException("Ejemplar prestado");
        assertEquals(409, e.getStatus().getCode());
    }

    @Test
    void accesoDenegado_lleva403() {
        BusinessException e = new AccesoDenegadoException("Sin permisos");
        assertEquals(403, e.getStatus().getCode());
    }

    @Test
    void mensajePropagaCorrectamente() {
        BusinessException e = new ValidacionException("Dato obligatorio");
        assertTrue(e.getMessage().contains("obligatorio"));
    }
}
