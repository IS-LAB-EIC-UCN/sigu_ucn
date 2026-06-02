package cl.ucn.app.service.Interfaces;

public interface IChecker {
    public boolean validar_string(String string);
    public boolean validar_num(String num);
    public boolean verificar_existencia();
    public boolean verificar_existencia_categoria(String categoria);
}
