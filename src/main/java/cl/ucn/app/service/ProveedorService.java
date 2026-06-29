package cl.ucn.app.service;

import cl.ucn.app.model.Proveedor;
import cl.ucn.app.repository.ProveedorRepository;

public class ProveedorService {
    private final ProveedorRepository proveedorRepository;

    public ProveedorService() {
        this.proveedorRepository = new ProveedorRepository();
    }

    public boolean crearProveedor(String nombre, String correo, String telefono) {
        Proveedor proveedor = new Proveedor(nombre, correo, telefono);
        proveedorRepository.save(proveedor);
        return true;
    }
}
