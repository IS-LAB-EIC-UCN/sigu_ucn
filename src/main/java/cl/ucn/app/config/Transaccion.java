package cl.ucn.app.config;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Transaccion {

    <T> T ejecutar(Function<EntityManager, T> accion);

    default void ejecutarSinResultado(Consumer<EntityManager> accion) {
        ejecutar(em -> {
            accion.accept(em);
            return null;
        });
    }
}
