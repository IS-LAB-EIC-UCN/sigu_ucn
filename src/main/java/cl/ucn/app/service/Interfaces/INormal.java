package cl.ucn.app.service.Interfaces;

public interface INormal {
    public void pedirPrestamo(Long usuario_id);
    public void devolverEquipo(Long usuario_id, Long movimiento_id);
}
