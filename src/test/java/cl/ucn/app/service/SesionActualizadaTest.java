package cl.ucn.app.service;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.model.*;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
class SesionActualizadaTest {
    @Test void actualizaRolDeUnaSesionYaAbierta() {
        var repo=mock(UsuarioRepository.class);var ctx=mock(Context.class,RETURNS_SELF);var u=new Usuario("Nombre","u@example.test","hash",true,new Rol("ESTUDIANTE"));
        when(ctx.sessionAttribute("usuarioId")).thenReturn(2L);when(repo.findById(2L)).thenReturn(u);
        new SesionActualizada(repo).verificar(ctx);verify(ctx).sessionAttribute("usuarioRol","ESTUDIANTE");
    }
    @Test void cuentaInactivaInvalidaSesionYDetieneRuta() {
        var repo=mock(UsuarioRepository.class);var ctx=mock(Context.class,RETURNS_DEEP_STUBS);
        when(ctx.sessionAttribute("usuarioId")).thenReturn(2L);var u=new Usuario();u.setActivo(false);when(repo.findById(2L)).thenReturn(u);
        new SesionActualizada(repo).verificar(ctx);verify(ctx.req().getSession(false)).invalidate();verify(ctx).redirect("/login");verify(ctx).skipRemainingHandlers();
    }
}
