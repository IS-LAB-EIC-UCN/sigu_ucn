package cl.ucn.app.service.biblioteca;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.model.biblioteca.Lector;
import java.util.List;

public class LectorService {

    private final LectorRepository lectorRepository;
    private final MultaRepository multaRepository;

    public LectorService() {
        this.lectorRepository = new LectorRepository();
        this.multaRepository = new MultaRepository();
    }

    public Lector registrarLector(String nombre, String correo, String rut) {
        if (buscarPorRut(rut) != null || lectorRepository.findByCorreo(correo) != null){
            return null;
        }
        Lector nuevoLector = new Lector();
        nuevoLector.setNombre(nombre);
        nuevoLector.setCorreo(correo);
        nuevoLector.setRut(rut);

        lectorRepository.save(nuevoLector);
        return nuevoLector;
    }

    public Lector buscarPorRut(String rut) {
        return lectorRepository.findByRut(rut);
    }

    public List<Lector> listarTodos() {
        return lectorRepository.findAll();
    }

    public Lector actualizarLector(Long id, String nombre, String correo, String rut) {
        Lector lector = lectorRepository.findById(id);
        if (lector == null) {
            throw new IllegalArgumentException("No existe un lector con ID " + id);
        }

        Lector existenteCorreo = lectorRepository.findByCorreo(correo);
        if (existenteCorreo != null && !existenteCorreo.getId().equals(id)) {
            throw new IllegalArgumentException("El correo " + correo + " ya está registrado por otro lector");
        }

        Lector existenteRut = lectorRepository.findByRut(rut);
        if (existenteRut != null && !existenteRut.getId().equals(id)) {
            throw new IllegalArgumentException("El RUT " + rut + " ya está registrado por otro lector");
        }

        lector.setNombre(nombre);
        lector.setCorreo(correo);
        lector.setRut(rut);
        lectorRepository.save(lector);
        return lector;
    }

    public void bloquearLector(Long id) {
        Lector lector = lectorRepository.findById(id);
        if (lector != null) {
            lector.setBloqueado(true);
            lectorRepository.save(lector);
        }
    }

    public void desbloquearLector(Long id) {
        Lector lector = lectorRepository.findById(id);
        if (lector != null) {
            lector.setBloqueado(false);
            lectorRepository.save(lector);
        }
    }

    public boolean tieneDeudaPendiente(Long lectorId) {
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null){return false;}
        List<Multa> multasPendientes = multaRepository.findPendientesByLector(lector);
        return !multasPendientes.isEmpty();
    }
}