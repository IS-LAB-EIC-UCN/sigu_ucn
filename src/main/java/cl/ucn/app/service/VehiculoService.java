package cl.ucn.app.service;

import java.util.Locale;
import java.util.Objects;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Vehiculo;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.VehiculoRepository;

public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public VehiculoService() {
        this(new VehiculoRepository(), new UsuarioRepository());
    }

    public VehiculoService(VehiculoRepository vehiculoRepository, UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = Objects.requireNonNull(vehiculoRepository);
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
    }

    /**
     * Registra un vehículo para un usuario existente y devuelve la entidad guardada.
     * Los datos inválidos o las patentes duplicadas producen IllegalArgumentException;
     * los demás errores de persistencia se propagan al llamador.
     */
    public Vehiculo registrarVehiculo(String patente, Long usuarioId) {
        if (patente == null || patente.isBlank()) {
            throw new IllegalArgumentException("La patente es obligatoria.");
        }

        String patenteNormalizada = patente.strip().toUpperCase(Locale.ROOT);
        if (patenteNormalizada.codePointCount(0, patenteNormalizada.length()) > 10) {
            throw new IllegalArgumentException("La patente no puede superar los 10 caracteres.");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario propietario es obligatorio.");
        }

        Usuario propietario = usuarioRepository.findById(usuarioId);
        if (propietario == null) {
            throw new IllegalArgumentException("El usuario propietario no existe.");
        }
        if (vehiculoRepository.existsByPatente(patenteNormalizada)) {
            throw new IllegalArgumentException("La patente ya está registrada.");
        }

        Vehiculo vehiculo = new Vehiculo(patenteNormalizada, propietario);
        vehiculoRepository.save(vehiculo);
        return vehiculo;
    }
}