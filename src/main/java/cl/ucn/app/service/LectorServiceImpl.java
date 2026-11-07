package cl.ucn.app.service;

import cl.ucn.app.model.Lector;
import cl.ucn.app.model.TipoLector;
import cl.ucn.app.repository.LectorRepository;
import cl.ucn.app.util.RutValidator;

import java.util.List;
import java.util.Optional;

public class LectorServiceImpl implements LectorService {

    private final LectorRepository lectorRepository;

    public LectorServiceImpl(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    @Override
    public Lector registrarLector(String rut, String nombre, String correo, TipoLector tipo) {
        validarDatosLector(rut, nombre, correo, tipo);
        String rutFormateado = RutValidator.formatear(rut);
        String correoLimpio = correo.trim();

        if (lectorRepository.findByRut(rutFormateado).isPresent()) {
            throw new ReglaNegocioException("RN-10", "Ya existe un lector registrado con el RUT ingresado.");
        }
        if (lectorRepository.findByCorreo(correoLimpio).isPresent()) {
            throw new ReglaNegocioException("RN-10", "Ya existe un lector registrado con el correo ingresado.");
        }

        Lector nuevo = new Lector(rutFormateado, nombre.trim(), correoLimpio, tipo);
        return lectorRepository.save(nuevo);
    }

    private void validarDatosLector(String rut, String nombre, String correo, TipoLector tipo) {
        if (!RutValidator.esValido(rut)) {
            throw new DatosInvalidosException("rut", "El RUT ingresado no es válido con dígito verificador.");
        }
        if (nombre == null || nombre.trim().isBlank()) {
            throw new DatosInvalidosException("nombre", "El nombre es obligatorio.");
        }
        if (correo == null || correo.trim().isBlank() || !correo.contains("@")) {
            throw new DatosInvalidosException("correo", "El correo ingresado no es válido.");
        }
        if (tipo == null) {
            throw new DatosInvalidosException("tipo", "El tipo de lector es obligatorio.");
        }
    }

    @Override
    public Optional<Lector> buscarPorId(Long id) {
        return lectorRepository.findById(id);
    }

    @Override
    public Optional<Lector> buscarPorRut(String rut) {
        return lectorRepository.findByRut(RutValidator.formatear(rut));
    }

    @Override
    public Optional<Lector> buscarPorCorreo(String correo) {
        return lectorRepository.findByCorreo(correo);
    }

    @Override
    public List<Lector> listarTodos() {
        return lectorRepository.findAll();
    }
}
