package cl.ucn.app.service;

import cl.ucn.app.service.Interfaces.IUsuario;

public class UsuarioService implements IUsuario {

    private final ConsoleService console;

    public UsuarioService() {
        this.console = new ConsoleService();
    }

    @Override
    public void consultar_inventario() {
        console.log_inventory(null);
    }

    @Override
    public void filtrar_categoria(String categoria) {
        console.log_inventory(categoria);
    }
    
}
