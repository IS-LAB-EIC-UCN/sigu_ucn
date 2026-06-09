package cl.ucn.app.service;

import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface ITallerService {
    Taller crearTaller(String nombre, String descripcion, Integer cupos, LocalDate inicio, LocalDate fin, Character bloque, Long profesorId) throws Exception;
    String inscribirAlumno(Long tallerId, Long usuarioId) throws Exception;
    void cancelarInscripcion(Long tallerId, Long usuarioId) throws Exception;
    List<Taller> obtenerTalleres(Long usuarioId, String rol);
    List<Inscripcion> obtenerMisInscripciones(Long usuarioId);
    List<Inscripcion> obtenerInscripcionesPorTaller(Long tallerId, Long docenteId) throws Exception;
    List<Usuario> obtenerDocentes();
}
