package cl.ucn.app.repository;

import cl.ucn.app.model.Categoria;

import java.util.List;
import java.util.Optional;

public class JpaCategoriaRepository extends JpaRepositorioBase implements CategoriaRepository {

    @Override
    public List<Categoria> listar() {
        return leer(em -> em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                .getResultList());
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return leer(em -> Optional.ofNullable(em.find(Categoria.class, id)));
    }
}
