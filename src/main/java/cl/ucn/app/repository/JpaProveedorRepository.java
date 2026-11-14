package cl.ucn.app.repository;

import cl.ucn.app.model.Proveedor;

import java.util.List;
import java.util.Optional;

public class JpaProveedorRepository extends JpaRepositorioBase implements ProveedorRepository {

    @Override
    public List<Proveedor> listar() {
        return leer(em -> em.createQuery("SELECT p FROM Proveedor p ORDER BY p.nombre", Proveedor.class)
                .getResultList());
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) {
        return leer(em -> Optional.ofNullable(em.find(Proveedor.class, id)));
    }
}
