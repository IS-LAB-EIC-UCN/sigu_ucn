package cl.ucn.app.service;

import cl.ucn.app.model.BloqueHorario;
import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de consulta de disponibilidad horaria de salas (RF-04, RN-05).
 */
public class DisponibilidadService {

    public static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    private final ReservaRepository reservaRepository;
    private final EspacioRepository espacioRepository;

    public DisponibilidadService(ReservaRepository reservaRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = Objects.requireNonNull(reservaRepository);
        this.espacioRepository = Objects.requireNonNull(espacioRepository);
    }

    public Disponibilidad consultar(Long espacioId, LocalDate fecha) {
        if (espacioId == null || fecha == null) {
            throw new ReservaException("Debe seleccionar una sala y una fecha.");
        }

        Espacio espacio = espacioRepository.findById(espacioId);
        if (espacio == null) {
            throw new ReservaException("La sala seleccionada no existe.");
        }
        if (!Boolean.TRUE.equals(espacio.getDisponible())) {
            throw new ReservaException("La sala seleccionada no está habilitada para reservas.");
        }

        List<Reserva> reservas = reservaRepository.buscarActivasPorEspacioYFecha(espacioId, fecha);
        List<BloqueHorario> ocupados = (reservas == null ? List.<Reserva>of() : reservas).stream()
                .filter(r -> r.getEstado() != null && r.getEstado().esActiva())
                .map(r -> new BloqueHorario(r.getHoraInicio(), r.getHoraFin(), r.getEstado()))
                .sorted(Comparator.comparing(BloqueHorario::inicio).thenComparing(BloqueHorario::fin))
                .toList();

        List<BloqueHorario> libres = calcularLibres(ocupados);
        return new Disponibilidad(espacio, fecha, ocupados, libres);
    }

    private List<BloqueHorario> calcularLibres(List<BloqueHorario> ocupados) {
        List<BloqueHorario> libres = new ArrayList<>();
        LocalTime cursor = HORA_APERTURA;

        for (BloqueHorario ocupado : ocupados) {
            if (!ocupado.fin().isAfter(HORA_APERTURA)) {
                continue;
            }
            if (!ocupado.inicio().isBefore(HORA_CIERRE)) {
                break;
            }
            LocalTime inicio = ocupado.inicio().isBefore(HORA_APERTURA) ? HORA_APERTURA : ocupado.inicio();
            LocalTime fin = ocupado.fin().isAfter(HORA_CIERRE) ? HORA_CIERRE : ocupado.fin();

            if (!fin.isAfter(inicio)) {
                continue;
            }
            if (inicio.isAfter(cursor)) {
                libres.add(new BloqueHorario(cursor, inicio, null));
            }
            if (fin.isAfter(cursor)) {
                cursor = fin;
            }
        }

        if (cursor.isBefore(HORA_CIERRE)) {
            libres.add(new BloqueHorario(cursor, HORA_CIERRE, null));
        }

        return List.copyOf(libres);
    }
}
