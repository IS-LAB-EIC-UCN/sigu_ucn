package cl.ucn.app.service.talleres.reglas;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.talleres.Inscripcion;
import cl.ucn.app.model.talleres.Taller;

import java.time.LocalDate;
import java.util.List;

/**
 * Todo lo que una regla necesita saber para decidir si una inscripción es válida.
 * El servicio la arma una vez; las reglas no consultan la base de datos, por eso son fáciles de probar.
 *
 * @param taller                    taller al que se quiere inscribir
 * @param participante              usuario que se inscribe
 * @param inscripcionesVigentes     inscripciones INSCRITO o EN_ESPERA que ya tiene el participante
 * @param hoy                       fecha actual (inyectada para poder probar con fechas fijas)
 */
public record SolicitudInscripcion(
        Taller taller,
        Usuario participante,
        List<Inscripcion> inscripcionesVigentes,
        LocalDate hoy
) {
}
