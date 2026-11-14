package cl.ucn.app.model.talleres;

/** Estados posibles de una inscripción (coinciden con chk_inscripciones_estado). */
public enum EstadoInscripcion {
    /** Tiene cupo asegurado en el taller. */
    INSCRITO,
    /** El taller estaba lleno; espera a que se libere un cupo. */
    EN_ESPERA,
    /** El participante (o el administrador) anuló la inscripción. */
    ANULADA;

    /** Una inscripción vigente es la que todavía ocupa cupo o lugar en la lista de espera. */
    public boolean esVigente() {
        return this != ANULADA;
    }
}
