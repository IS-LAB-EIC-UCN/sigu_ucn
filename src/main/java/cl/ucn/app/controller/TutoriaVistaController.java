package cl.ucn.app.controller;

import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.ModalidadTutoria;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutorRepository;
import cl.ucn.app.repository.TutoriaRepository;
import cl.ucn.app.util.SessionUtil;
import cl.ucn.app.util.VistaUtil;
import io.javalin.http.Context;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Esqueleto de vistas del módulo (Wave 0, E2-09): detalle de tutoría y formulario de publicación.
 * Solo lee datos y renderiza. Las acciones POST /tutorias (E2-10) y
 * POST /tutorias/{id}/reservar (E2-12) las implementa su responsable.
 */
public class TutoriaVistaController {

    private final TutoriaRepository tutoriaRepository;
    private final InscripcionTutoriaRepository inscripcionRepository;
    private final TutorRepository tutorRepository;

    public TutoriaVistaController(TutoriaRepository tutoriaRepository,
                                  InscripcionTutoriaRepository inscripcionRepository,
                                  TutorRepository tutorRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.tutorRepository = tutorRepository;
    }

    public void detalle(Context ctx) {
        Optional<Tutoria> tutoria = parseId(ctx.pathParam("id")).flatMap(tutoriaRepository::findById);
        if (tutoria.isEmpty()) {
            Map<String, Object> model = VistaUtil.modeloBase(ctx, "Tutoría no encontrada - SIGU-UCN");
            model.put("mensaje", "La tutoría solicitada no existe.");
            ctx.status(404).render("tutorias/error.jte", model);
            return;
        }
        long ocupados = inscripcionRepository.countActivasByTutoriaId(tutoria.get().getId());
        Map<String, Object> model = VistaUtil.modeloBase(ctx, "Detalle de tutoría - SIGU-UCN");
        model.put("tutoria", tutoria.get());
        model.put("cuposDisponibles", Math.max(0, tutoria.get().getCapacidad() - ocupados));
        model.put("mensaje", ctx.queryParam("mensaje"));
        model.put("error", ctx.queryParam("error"));
        ctx.render("tutorias/detalle.jte", model);
    }

    public void mostrarPublicar(Context ctx) {
        List<Asignatura> asignaturas = tutorRepository.findByUsuarioId(SessionUtil.usuarioId(ctx))
                .map(Tutor::getAsignaturasCertificadas)
                .map(set -> set.stream().sorted(Comparator.comparing(Asignatura::getCodigo)).toList())
                .orElse(List.of());
        Map<String, Object> model = VistaUtil.modeloBase(ctx, "Publicar tutoría - SIGU-UCN");
        model.put("asignaturas", asignaturas);
        model.put("modalidades", ModalidadTutoria.values());
        ctx.render("tutorias/publicar.jte", model);
    }

    private Optional<Long> parseId(String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
