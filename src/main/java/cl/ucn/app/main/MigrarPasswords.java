package cl.ucn.app.main;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.repository.UsuarioRepository;
public final class MigrarPasswords {
    private MigrarPasswords() {}
    public static void main(String[] args) {
        try { System.out.println("Contraseñas migradas: "+new UsuarioRepository().migrarPasswords()); }
        finally { JPAUtil.shutdown(); }
    }
}
