package cl.ucn.app.service;

import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.config.Transaccion;
import cl.ucn.app.dto.TutoriaDetalle;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.dto.TutoriaForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.ModalidadTutoria;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.TutorRepository;
import cl.ucn.app.repository.TutorRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepository;
import cl.ucn.app.repository.TutoriaRepositoryImpl;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TutoriaService {

    static final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);
    static final Duration DURACION_MINIMA = Duration.ofMinutes(30);
    static final Duration ANTICIPACION_MINIMA = Duration.ofHours(3);
    private static final int LARGO_MAXIMO_SALA = 50;
    private static final String SQLSTATE_UNICIDAD = "23505";

    private final TutoriaRepository tutoriaRepository;
    private final TutorRepository tutorRepository;
    private final Transaccion transaccion;
    private final Clock reloj;

    public TutoriaService(TutoriaRepository tutoriaRepository, TutorRepository tutorRepository,
                          Transaccion transaccion, Clock reloj) {
        this.tutoriaRepository = tutoriaRepository;
        this.tutorRepository = tutorRepository;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    public TutoriaService() {
        this(new TutoriaRepositoryImpl(), new TutorRepositoryImpl(), new JpaTransaccion(),
                Clock.systemDefaultZone());
    }

    // ------------------------------------------------------------------ consulta

    public boolean esTutor(Long usuarioId) {
        return usuarioId != null && tutorRepository.findByUsuarioId(usuarioId).isPresent();
    }

    public List<Asignatura> asignaturasCertificadas(Long usuarioId) {
        Tutor tutor = usuarioId == null ? null : tutorRepository.findByUsuarioId(usuarioId).orElse(null);
        if (tutor == null) {
            throw new TutoriaValidacionException(null, "Solo los tutores registrados pueden publicar tutorías.");
        }
        List<Asignatura> asignaturas = new ArrayList<>(tutor.getAsignaturasCertificadas());
        asignaturas.sort(Comparator.comparing(Asignatura::getSemestre).thenComparing(Asignatura::getNombre));
        return asignaturas;
    }

    public List<TutoriaDetalle> listarDisponibles(TutoriaFiltro filtro) {
        List<TutoriaDetalle> resultado = new ArrayList<>();
        for (Tutoria tutoria : tutoriaRepository.buscarDisponibles(filtro)) {
            resultado.add(new TutoriaDetalle(tutoria, tutoriaRepository.contarInscripcionesActivas(tutoria.getId())));
        }
        return resultado;
    }

    public TutoriaDetalle buscarDetalle(Long tutoriaId) {
        Tutoria tutoria = tutoriaId == null ? null : tutoriaRepository.findById(tutoriaId).orElse(null);
        if (tutoria == null) {
            throw new TutoriaValidacionException(null, "La tutoría solicitada no existe.");
        }
        return new TutoriaDetalle(tutoria, tutoriaRepository.contarInscripcionesActivas(tutoria.getId()));
    }

    // ------------------------------------------------------------------ publicar

    public Tutoria publicar(Long usuarioId, TutoriaForm form) {
        DatosTutoria datos = validarFormulario(form);

        try {
            return transaccion.ejecutar(em -> {
                Tutor tutor = tutorRepository.findByUsuarioId(em, usuarioId).orElseThrow(
                        () -> new TutoriaValidacionException(null,
                                "Solo los tutores registrados pueden publicar tutorías."));
                if (!Boolean.TRUE.equals(tutor.getCertificado())) {
                    throw new TutoriaValidacionException(null, "Tu perfil de tutor no está certificado.");
                }

                // Serializa las publicaciones del mismo tutor antes de revisar el cruce de horarios.
                tutorRepository.bloquear(em, tutor);

                Asignatura asignatura = tutor.getAsignaturasCertificadas().stream()
                        .filter(a -> a.getId().equals(datos.asignaturaId()))
                        .findFirst()
                        .orElseThrow(() -> new TutoriaValidacionException("asignaturaId",
                                "Solo puedes publicar tutorías de las asignaturas que tienes certificadas."));

                if (tutoriaRepository.existeConflictoTutor(em, tutor.getId(), datos.inicio(), datos.fin())) {
                    throw new TutoriaValidacionException("horaInicio",
                            "Ya tienes una tutoría publicada que se cruza con ese horario.");
                }

                Tutoria tutoria = new Tutoria(tutor, asignatura, datos.inicio(), datos.fin(),
                        datos.modalidad(), datos.sala(), datos.capacidad());
                tutoriaRepository.save(em, tutoria);
                return tutoria;
            });
        } catch (RuntimeException e) {
            if (esViolacionDeUnicidad(e)) {
                throw new TutoriaValidacionException("horaInicio",
                        "Ya tienes una tutoría publicada en esa misma franja.");
            }
            throw e;
        }
    }

    private DatosTutoria validarFormulario(TutoriaForm form) {
        Map<String, String> errores = new LinkedHashMap<>();

        Long asignaturaId = leerLong(form.asignaturaId());
        if (asignaturaId == null) {
            errores.put("asignaturaId", "Debe seleccionar una asignatura.");
        }

        LocalDate fecha = leerFecha(form.fecha());
        if (fecha == null) {
            errores.put("fecha", "Debe indicar una fecha válida.");
        }

        LocalTime horaInicio = leerHora(form.horaInicio());
        if (horaInicio == null) {
            errores.put("horaInicio", "Debe indicar una hora de inicio válida.");
        }

        LocalTime horaFin = leerHora(form.horaFin());
        if (horaFin == null) {
            errores.put("horaFin", "Debe indicar una hora de término válida.");
        }

        ModalidadTutoria modalidad = leerModalidad(form.modalidad());
        if (modalidad == null) {
            errores.put("modalidad", "La modalidad debe ser ONLINE o PRESENCIAL.");
        }

        Integer capacidad = leerEntero(form.capacidad());
        if (capacidad == null || capacidad <= 0) {
            errores.put("capacidad", "La capacidad debe ser un número entero mayor que 0.");
        }

        String sala = form.sala() == null ? "" : form.sala().trim();
        if (modalidad == ModalidadTutoria.PRESENCIAL && sala.isEmpty()) {
            errores.put("sala", "La sala es obligatoria en una tutoría presencial.");
        } else if (sala.length() > LARGO_MAXIMO_SALA) {
            errores.put("sala", "La sala no puede superar los " + LARGO_MAXIMO_SALA + " caracteres.");
        }

        if (horaInicio != null && horaFin != null) {
            validarHorario(horaInicio, horaFin, errores);
        }
        if (fecha != null && horaInicio != null && !errores.containsKey("horaInicio")) {
            validarFecha(fecha.atTime(horaInicio), errores);
        }

        if (!errores.isEmpty()) {
            throw new TutoriaValidacionException(errores);
        }

        return new DatosTutoria(
                asignaturaId,
                fecha.atTime(horaInicio),
                fecha.atTime(horaFin),
                modalidad,
                modalidad == ModalidadTutoria.PRESENCIAL ? sala : null,
                capacidad
        );
    }

    private void validarHorario(LocalTime inicio, LocalTime fin, Map<String, String> errores) {
        if (inicio.isBefore(HORA_APERTURA)) {
            errores.putIfAbsent("horaInicio", "La tutoría no puede comenzar antes de las 07:00.");
        }
        if (fin.isAfter(HORA_CIERRE)) {
            errores.putIfAbsent("horaFin", "La tutoría no puede terminar después de las 22:00.");
        }
        if (!fin.isAfter(inicio)) {
            errores.putIfAbsent("horaFin", "La hora de término debe ser posterior a la de inicio.");
        } else if (Duration.between(inicio, fin).compareTo(DURACION_MINIMA) < 0) {
            errores.putIfAbsent("horaFin", "La duración mínima de una tutoría es de 30 minutos.");
        }
    }

    private void validarFecha(LocalDateTime inicio, Map<String, String> errores) {
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (!inicio.isAfter(ahora)) {
            errores.putIfAbsent("fecha", "La fecha y hora de inicio deben ser futuras.");
        } else if (inicio.isBefore(ahora.plus(ANTICIPACION_MINIMA))) {
            errores.putIfAbsent("horaInicio", "La tutoría debe publicarse con al menos 3 horas de anticipación.");
        }
    }

    // ------------------------------------------------------------------ utilidades

    static boolean esViolacionDeUnicidad(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (actual instanceof SQLException sql && SQLSTATE_UNICIDAD.equals(sql.getSQLState())) {
                return true;
            }
            if (actual.getCause() == actual) {
                break;
            }
        }
        return false;
    }

    private static Long leerLong(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer leerEntero(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate leerFecha(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : LocalDate.parse(valor.trim());
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    private static LocalTime leerHora(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : LocalTime.parse(valor.trim());
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    private static ModalidadTutoria leerModalidad(String valor) {
        try {
            return valor == null || valor.isBlank() ? null : ModalidadTutoria.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private record DatosTutoria(Long asignaturaId, LocalDateTime inicio, LocalDateTime fin,
                                ModalidadTutoria modalidad, String sala, Integer capacidad) {
    }
}
