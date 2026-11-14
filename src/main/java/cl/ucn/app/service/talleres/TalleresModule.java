package cl.ucn.app.service.talleres;

import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.talleres.InscripcionRepository;
import cl.ucn.app.repository.talleres.JpaInscripcionRepository;
import cl.ucn.app.repository.talleres.JpaParticipanteRepository;
import cl.ucn.app.repository.talleres.JpaTallerRepository;
import cl.ucn.app.repository.talleres.TallerRepository;
import cl.ucn.app.service.talleres.reglas.ReglaSinDuplicado;
import cl.ucn.app.service.talleres.reglas.ReglaSinSolapamiento;
import cl.ucn.app.service.talleres.reglas.ReglaTallerAbierto;

import java.time.Clock;
import java.util.List;

/**
 * Punto único donde se crean y conectan las piezas del módulo (composition root).
 * Aquí se eligen las implementaciones concretas; el resto del código solo conoce interfaces (DIP).
 */
public final class TalleresModule {

    private final TallerService tallerService;
    private final InscripcionService inscripcionService;

    public TalleresModule() {
        Clock clock = Clock.systemDefaultZone();
        TallerRepository tallerRepository = new JpaTallerRepository();
        InscripcionRepository inscripcionRepository = new JpaInscripcionRepository();

        this.tallerService = new TallerService(tallerRepository, clock);
        this.inscripcionService = new InscripcionService(
                inscripcionRepository,
                tallerRepository,
                new JpaParticipanteRepository(new UsuarioRepository()),
                List.of(new ReglaTallerAbierto(), new ReglaSinDuplicado(), new ReglaSinSolapamiento()),
                new PoliticaCupo(),
                clock);
    }

    public TallerService tallerService() {
        return tallerService;
    }

    public InscripcionService inscripcionService() {
        return inscripcionService;
    }
}
