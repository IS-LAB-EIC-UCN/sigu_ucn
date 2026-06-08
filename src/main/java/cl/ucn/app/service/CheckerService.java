package cl.ucn.app.service;

import cl.ucn.app.repository.EntradaRepository;
import cl.ucn.app.repository.ProveedorRepository;
import cl.ucn.app.repository.RecursoRepository;
import cl.ucn.app.repository.SalidaRepository;
import cl.ucn.app.repository.PrestamoRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.service.Interfaces.IChecker;

public class CheckerService implements IChecker {

    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;
    private final EntradaRepository entradaRepository;
    private final SalidaRepository salidaRepository;
    private final PrestamoRepository prestamoRepository;
    private final ProveedorRepository proveedorRepository;

    public CheckerService() {
        this.usuarioRepository = new UsuarioRepository();
        this.recursoRepository = new RecursoRepository();
        this.entradaRepository = new EntradaRepository();
        this.salidaRepository = new SalidaRepository();
        this.prestamoRepository = new PrestamoRepository();
        this.proveedorRepository = new ProveedorRepository();
    }

    @Override
    public boolean validar_string(String string) {
        if (string == null || string.equals("") || !string.matches("^[a-ZA-Z\s]+$")) return false;
        return true;
    }

    @Override
    public boolean validar_num(String num) {
        if (num == null || num.equals("") || !num.matches("^\\d+$")) return false;
        return true;
    }

    @Override
    public boolean validar_correo(String correo) {
        if (correo == null || correo.equals("") || !correo.matches("^\\w+@\\w+\\.ucn\\.cl$")) return false;
        return true;
    }

    @Override
    public boolean validar_fecha(String fecha) {
        if (fecha == null || fecha.equals("") || !fecha.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) return false;
        return true;
    }

    @Override
    public boolean validar_hora(String hora) {
        if (hora == null || hora.equals("") || !hora.matches("^([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")) return false;
        return true;
    }

    @Override
    public boolean validar_telefono(String telefono) {
        if (telefono == null || telefono.equals("") || !telefono.matches("^\\+\\d{11}$")) return false;
        return true;
    }

    @Override
    public boolean verificar_existencia() {
        if (
            usuarioRepository.findAll() == null
            || recursoRepository.findAll() == null
            || entradaRepository.findAll() == null
            || salidaRepository.findAll() == null
            || prestamoRepository.findAll() == null
            || proveedorRepository.findAll() == null
        ) return false;
        return true;
    }

    @Override
    public boolean verificar_existencia_categoria(String categoria) {
        if (recursoRepository.findByCategoria(categoria) == null) return false;
        return true;
    }
    
}
