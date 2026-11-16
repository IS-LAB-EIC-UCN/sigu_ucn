package cl.ucn.app.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Convención de fechas del módulo de inventario (Grupo 5): se guardan en UTC
 * y se muestran en la zona horaria de Santiago.
 */
public final class FechasInventario {

    private static final ZoneId SANTIAGO = ZoneId.of("America/Santiago");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private FechasInventario() {
    }

    /** Momento actual en UTC, listo para persistir. */
    public static LocalDateTime ahoraUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    /** Convierte una fecha guardada en UTC a texto en hora de Santiago. */
    public static String formatear(LocalDateTime fechaUtc) {
        if (fechaUtc == null) {
            return "—";
        }
        return fechaUtc.atZone(ZoneOffset.UTC).withZoneSameInstant(SANTIAGO).format(FORMATO);
    }
}
