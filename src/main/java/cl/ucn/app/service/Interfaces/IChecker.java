package cl.ucn.app.service.Interfaces;

public interface IChecker {
    public boolean validar_string(String string);
    public boolean validar_num(String num);
    public boolean validar_correo(String correo);
    public boolean validar_fecha(String fecha);
    public boolean validar_hora(String hora);
    boolean validar_telefono(String telefono);
    public boolean verificar_existencia();
    public boolean verificar_existencia_categoria(String categoria);
}
