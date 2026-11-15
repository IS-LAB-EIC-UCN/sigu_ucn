package cl.ucn.app.service;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.RegistroRepository;

public class RegistroService {

    private final RegistroRepository registroRepository;

    public RegistroService(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    public void registrar(RegistroForm form) {
        String nombre = normalizar(form.nombre());
        String correo = normalizar(form.correo()).toLowerCase();
        String carrera = normalizar(form.carrera());

        if (nombre.isBlank()) {
            throw new TutoriaValidacionException("nombre", "El nombre es obligatorio.");
        }
        if (!correo.matches("^[A-Za-z0-9._%+-]+@alumnos\\.ucn\\.cl$")) {
            throw new TutoriaValidacionException("correo", "Debe utilizar un correo @alumnos.ucn.cl.");
        }
        if (carrera.isBlank()) {
            throw new TutoriaValidacionException("carrera", "La carrera es obligatoria.");
        }
        int semestre = parsearSemestre(form.semestre());
        if (form.password() == null || form.password().length() < 8) {
            throw new TutoriaValidacionException("password", "La contraseña debe tener al menos 8 caracteres.");
        }
        if (!form.password().equals(form.confirmacion())) {
            throw new TutoriaValidacionException("confirmacion", "Las contraseñas no coinciden.");
        }
        if (registroRepository.existeUsuarioConCorreo(correo)
                || registroRepository.existeEstudianteConCorreo(correo)) {
            throw new TutoriaValidacionException("correo", "El correo ya está registrado.");
        }

        Rol rol = registroRepository.buscarRol("ESTUDIANTE");
        if (rol == null) {
            throw new IllegalStateException("No existe el rol ESTUDIANTE.");
        }

        Usuario usuario = new Usuario(nombre, correo, form.password(), true, rol);
        Estudiante estudiante = new Estudiante(correo, nombre, carrera, semestre);
        registroRepository.guardar(usuario, estudiante);
    }

    private int parsearSemestre(String valor) {
        try {
            int semestre = Integer.parseInt(valor == null ? "" : valor.trim());
            if (semestre < 1 || semestre > 12) {
                throw new NumberFormatException();
            }
            return semestre;
        } catch (NumberFormatException e) {
            throw new TutoriaValidacionException("semestre", "El semestre debe estar entre 1 y 12.");
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
