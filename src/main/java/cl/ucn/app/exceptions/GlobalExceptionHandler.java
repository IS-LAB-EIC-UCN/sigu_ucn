package cl.ucn.app.exceptions;

import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private GlobalExceptionHandler() {
    }

    public static void register(JavalinConfig config) {
        config.unsafe.internalRouter.addHttpExceptionHandler(BusinessException.class, (e, ctx) -> {
            ctx.status(e.getStatus());
            ctx.render("biblioteca/error.jte", Map.of(
                "error", e.getMessage(),
                "status", e.getStatus().getCode()
            ));
        });

        config.unsafe.internalRouter.addHttpExceptionHandler(Exception.class, (e, ctx) -> {
            log.error("Error no controlado en {}", ctx.path(), e);
            String detail = e.getClass().getSimpleName() + ": " + e.getMessage();
            Throwable cause = e.getCause();
            while (cause != null) {
                detail += " | caused by: " + cause.getClass().getSimpleName() + ": " + cause.getMessage();
                cause = cause.getCause();
            }
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.render("biblioteca/error.jte", Map.of(
                "error", "Error interno del servidor: " + detail,
                "status", HttpStatus.INTERNAL_SERVER_ERROR.getCode()
            ));
        });
    }
}
