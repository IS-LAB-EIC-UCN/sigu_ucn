package cl.ucn.app.service.biblioteca;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.BusinessException;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.model.biblioteca.Lector;
import jakarta.persistence.EntityManager;
import java.util.List;

public class LectorService {

    private final LectorRepository lectorRepository;
    private final MultaRepository multaRepository;
    private final PrestamoLibroRepository prestamoLibroRepository;

    public LectorService() {
        this.lectorRepository = new LectorRepository();
        this.multaRepository = new MultaRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
    }

    public Lector registrarLector(String nombre, String correo, String rut) {
        validarDatosLector(nombre, correo, rut);
        if (buscarPorRut(rut) != null || lectorRepository.findByCorreo(correo) != null) {
            throw new ValidacionException("El RUT o correo ya se encuentra registrado");
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

    public Lector buscarPorId(Long id) {
        return lectorRepository.findById(id);
    }

    public List<Lector> listarTodos() {
        return lectorRepository.findAll();
    }

    public Lector actualizarLector(Long id, String nombre, String correo, String rut) {
        validarDatosLector(nombre, correo, rut);
        Lector lector = lectorRepository.findById(id);
        if (lector == null) {
            throw new RecursoNoEncontradoException("No existe un lector con ID " + id);
        }

        Lector existenteCorreo = lectorRepository.findByCorreo(correo);
        if (existenteCorreo != null && !existenteCorreo.getId().equals(id)) {
            throw new ValidacionException("El correo " + correo + " ya está registrado por otro lector");
        }

        Lector existenteRut = lectorRepository.findByRut(rut);
        if (existenteRut != null && !existenteRut.getId().equals(id)) {
            throw new ValidacionException("El RUT " + rut + " ya está registrado por otro lector");
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

    public void eliminarLector(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("ID de lector no proporcionado");
        }
        Lector lector = lectorRepository.findById(id);
        if (lector == null) {
            throw new RecursoNoEncontradoException("No existe un lector con ID " + id);
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Long count = (Long) em.createQuery(
                "SELECT COUNT(p) FROM PrestamoLibro p WHERE p.lector.id = :id AND p.estado = 'ACTIVO'")
                .setParameter("id", id)
                .getSingleResult();
            if (count > 0) {
                em.getTransaction().rollback();
                throw new ConflictoEstadoException("No se puede eliminar el lector porque tiene prestamos activos. Primero devuelve los prestamos.");
            }
            em.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE lector_id = ?)")
                .setParameter(1, id)
                .executeUpdate();
            em.createNativeQuery("DELETE FROM prestamo WHERE lector_id = ?")
                .setParameter(1, id)
                .executeUpdate();
            em.createQuery("DELETE FROM Lector l WHERE l.id = :id")
                .setParameter("id", id)
                .executeUpdate();
            em.getTransaction().commit();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean tieneDeudaPendiente(Long lectorId) {
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null){return false;}
        List<Multa> multasPendientes = multaRepository.findPendientesByLector(lector);
        return !multasPendientes.isEmpty();
    }

    private void validarDatosLector(String nombre, String correo, String rut) {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new ValidacionException("El nombre debe tener al menos 3 caracteres");
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new ValidacionException("El nombre solo puede contener letras y espacios");
        }
        if (correo == null || !correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new ValidacionException("El correo electrónico no tiene un formato válido");
        }
        if (rut == null || !rut.matches("^\\d{7,8}-[0-9kK]$")) {
            throw new ValidacionException("El RUT debe tener formato 12345678-9");
        }
        if (!validarDigitoVerificador(rut)) {
            throw new ValidacionException("El dígito verificador del RUT no es válido");
        }
    }

    private boolean validarDigitoVerificador(String rut) {
        String[] partes = rut.split("-");
        String numero = partes[0];
        String dv = partes[1].toUpperCase();
        int suma = 0;
        int multiplicador = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(numero.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = suma % 11;
        String dvCalculado = resto == 0 ? "0" : resto == 1 ? "K" : String.valueOf(11 - resto);
        return dv.equals(dvCalculado);
    }
}