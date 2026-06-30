package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface ITallerService {
    Taller crearTaller(String nombre, String descripcion, String categoria, Integer cupos, LocalDate inicio, LocalDate fin, Character bloque, Long profesorId, Long espacioId) throws Exception;
    String inscribirAlumno(Long tallerId, Long usuarioId) throws Exception;
    void cancelarInscripcion(Long tallerId, Long usuarioId) throws Exception;
    List<Taller> obtenerTalleres(Long usuarioId, String rol);
    List<Taller> obtenerTalleres(Long usuarioId, String rol, String categoria, Character bloque);
    List<Inscripcion> obtenerMisInscripciones(Long usuarioId);
    List<Inscripcion> obtenerInscripcionesPorTaller(Long tallerId, Long docenteId) throws Exception;
    List<Usuario> obtenerDocentes();
    List<Espacio> obtenerEspacios();
    void eliminarTaller(Long tallerId) throws Exception;
    Taller obtenerTallerPorId(Long id) throws Exception;
    void editarTaller(Long tallerId, String nombre, String descripcion, String categoria, Integer cupos, LocalDate inicio, LocalDate fin, Character bloque, Long profesorId, Long espacioId) throws Exception;
    void solicitarAnulacion(Long tallerId, Long usuarioId, String justificacion) throws Exception;
    void procesarAnulacion(Long tallerId, Long usuarioId, boolean aprobada) throws Exception;
    List<Inscripcion> obtenerAnulacionesPendientes() throws Exception;
}
