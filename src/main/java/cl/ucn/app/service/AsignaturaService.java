package cl.ucn.app.service;

import cl.ucn.app.dto.AsignaturaForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.TutoriaRepository;

import java.util.List;
import java.util.Optional;

public class AsignaturaService {

    private static final int LARGO_MAXIMO_CODIGO = 20;
    private static final int LARGO_MAXIMO_TEXTO = 120;
    private static final int SEMESTRE_MINIMO = 1;
    private static final int SEMESTRE_MAXIMO = 12;

    private final AsignaturaRepository asignaturaRepository;
    private final TutoriaRepository tutoriaRepository;

    public AsignaturaService(AsignaturaRepository asignaturaRepository, TutoriaRepository tutoriaRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.tutoriaRepository = tutoriaRepository;
    }

    public List<Asignatura> listar() {
        return asignaturaRepository.findAll();
    }

    public Asignatura buscarPorId(Long id) {
        Optional<Asignatura> asignatura = id == null ? Optional.empty() : asignaturaRepository.findById(id);
        return asignatura.orElseThrow(
                () -> new TutoriaValidacionException(null, "La asignatura solicitada no existe.")
        );
    }

    public Asignatura crear(AsignaturaForm form) {
        Asignatura asignatura = leerDatos(form);
        validarCodigoDisponible(asignatura.getCodigo(), null);
        asignaturaRepository.save(asignatura);
        return asignatura;
    }

    public Asignatura actualizar(Long id, AsignaturaForm form) {
        Asignatura asignatura = buscarPorId(id);
        Asignatura datos = leerDatos(form);
        validarCodigoDisponible(datos.getCodigo(), asignatura.getId());

        asignatura.setCodigo(datos.getCodigo());
        asignatura.setNombre(datos.getNombre());
        asignatura.setCarrera(datos.getCarrera());
        asignatura.setSemestre(datos.getSemestre());
        asignaturaRepository.update(asignatura);
        return asignatura;
    }

    public Asignatura eliminar(Long id) {
        Asignatura asignatura = buscarPorId(id);

        if (tutoriaRepository.countByAsignaturaAndEstado(asignatura.getId(), EstadoTutoria.PUBLICADA) > 0) {
            throw new TutoriaValidacionException(null,
                    "No se puede eliminar " + asignatura.getCodigo() + " porque tiene tutorías activas.");
        }
        if (tutoriaRepository.countByAsignatura(asignatura.getId()) > 0) {
            throw new TutoriaValidacionException(null,
                    "No se puede eliminar " + asignatura.getCodigo() + " porque tiene tutorías en su historial.");
        }

        asignaturaRepository.delete(asignatura.getId());
        return asignatura;
    }

    private Asignatura leerDatos(AsignaturaForm form) {
        String codigo = validarTexto(form.codigo(), "codigo", "código", LARGO_MAXIMO_CODIGO).toUpperCase();
        if (!codigo.matches("[A-Z0-9-]+")) {
            throw new TutoriaValidacionException("codigo",
                    "El código solo puede contener letras, números y guiones.");
        }

        String nombre = validarTexto(form.nombre(), "nombre", "nombre", LARGO_MAXIMO_TEXTO);
        String carrera = validarTexto(form.carrera(), "carrera", "carrera", LARGO_MAXIMO_TEXTO);
        int semestre = validarSemestre(form.semestre());
        return new Asignatura(codigo, nombre, carrera, semestre);
    }

    private String validarTexto(String valor, String campo, String etiqueta, int largoMaximo) {
        String texto = valor == null ? "" : valor.trim();
        if (texto.isEmpty()) {
            throw new TutoriaValidacionException(campo, "El campo " + etiqueta + " es obligatorio.");
        }
        if (texto.length() > largoMaximo) {
            throw new TutoriaValidacionException(campo,
                    "El campo " + etiqueta + " no puede superar los " + largoMaximo + " caracteres.");
        }
        return texto;
    }

    private int validarSemestre(String valor) {
        int semestre;
        try {
            semestre = Integer.parseInt(valor == null ? "" : valor.trim());
        } catch (NumberFormatException e) {
            semestre = 0;
        }

        if (semestre < SEMESTRE_MINIMO || semestre > SEMESTRE_MAXIMO) {
            throw new TutoriaValidacionException("semestre",
                    "El semestre debe ser un número entre " + SEMESTRE_MINIMO + " y " + SEMESTRE_MAXIMO + ".");
        }
        return semestre;
    }

    private void validarCodigoDisponible(String codigo, Long idActual) {
        Optional<Asignatura> existente = asignaturaRepository.findByCodigo(codigo);
        if (existente.isPresent() && !existente.get().getId().equals(idActual)) {
            throw new TutoriaValidacionException("codigo", "Ya existe una asignatura con el código " + codigo + ".");
        }
    }
}
