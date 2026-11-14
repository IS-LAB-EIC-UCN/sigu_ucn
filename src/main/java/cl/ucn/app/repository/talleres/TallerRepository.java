package cl.ucn.app.repository.talleres;

import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.model.talleres.Taller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de talleres. Los servicios dependen de esta interfaz, no de JPA (DIP). */
public interface TallerRepository {

    Taller save(Taller taller);

    Optional<Taller> findById(Long id);

    /**
     * Talleres activos desde la fecha indicada, con conteo de inscritos y en espera.
     *
     * @param desde     fecha mínima (normalmente hoy)
     * @param categoria filtro opcional; null o vacío = todas
     * @param fecha     filtro opcional por día exacto; null = todas
     */
    List<TallerResumen> buscarDisponibles(LocalDate desde, String categoria, LocalDate fecha);

    Optional<TallerResumen> findResumenById(Long id);

    List<String> listarCategorias();
}
