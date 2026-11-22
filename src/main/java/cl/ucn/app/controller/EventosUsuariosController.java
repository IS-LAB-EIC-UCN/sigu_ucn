package cl.ucn.app.controller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.service.AdministracionUsuariosService;
import io.javalin.http.*;
import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class EventosUsuariosController {
    private final UsuarioRepository repo;
    private final AdministracionUsuariosService service;
    public EventosUsuariosController() { this(new UsuarioRepository()); }
    public EventosUsuariosController(UsuarioRepository repo) { this.repo=repo;this.service=new AdministracionUsuariosService(repo); }
    private boolean acceso(Context ctx,boolean escritura) {
        if(ctx.sessionAttribute("usuarioId")==null) {ctx.redirect("/login");return false;}
        if(!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse("Solo ADMIN puede administrar usuarios y roles.");
        if(escritura) {
            String token=ctx.sessionAttribute("csrfToken"), recibido=ctx.formParam("csrfToken");
            if(token==null || recibido==null || token.isBlank() || !MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8),recibido.getBytes(StandardCharsets.UTF_8)))
                throw new ForbiddenResponse("Token de seguridad inválido.");
        }
        return true;
    }
    private Map<String,Object> modelo(Context ctx) {
        Map<String,Object> m=new HashMap<>();m.put("title","Administración de usuarios · Eventos");m.put("usuarioNombre",ctx.sessionAttribute("usuarioNombre"));
        m.put("csrfToken",ctx.sessionAttribute("csrfToken"));m.put("roles",repo.roles());
        String aviso=ctx.consumeSessionAttribute("adminAviso");m.put("aviso",aviso==null?"":aviso);m.put("error","");return m;
    }
    public void listar(Context ctx) {
        if(!acceso(ctx,false)) return;
        Map<String,Object> m=modelo(ctx);String q=ctx.queryParam("q");q=q==null?"":q.strip().toLowerCase(Locale.ROOT);
        final String filtro=q;
        m.put("usuarios",repo.findAll().stream().filter(u->(u.getNombre()+" "+u.getCorreo()).toLowerCase(Locale.ROOT).contains(filtro)).toList());m.put("q",q);
        ctx.render("eventos-usuarios.jte",m);
    }
    public void roles(Context ctx) {if(acceso(ctx,false)) ctx.render("eventos-roles.jte",modelo(ctx));}
    private Usuario usuario(Context ctx) {
        try {Usuario u=repo.findById(Long.valueOf(ctx.pathParam("id")));if(u==null) throw new NotFoundResponse("Usuario no encontrado.");return u;}
        catch(NumberFormatException ex) {throw new BadRequestResponse("ID de usuario inválido.");}
    }
    public void nuevo(Context ctx) {if(acceso(ctx,false)) formulario(ctx,null,Map.of("activo","true"),"");}
    public void editar(Context ctx) {
        if(!acceso(ctx,false)) return;Usuario u=usuario(ctx);
        formulario(ctx,u.getId(),Map.of("nombre",u.getNombre(),"correo",u.getCorreo(),"rolId",u.getRol().getId().toString(),"activo",u.getActivo().toString()),"");
    }
    private void formulario(Context ctx,Long id,Map<String,String> datos,String error) {
        Map<String,Object> m=modelo(ctx);m.put("id",id);m.put("datos",datos);m.put("error",error);ctx.render("eventos-usuario-formulario.jte",m);
    }
    public void crear(Context ctx) {if(acceso(ctx,true)) guardar(ctx,null);}
    public void actualizar(Context ctx) {if(acceso(ctx,true)) guardar(ctx,usuario(ctx).getId());}
    private void guardar(Context ctx,Long id) {
        Map<String,String> datos=new HashMap<>();
        for(String campo:List.of("nombre","correo","rolId","activo")) {String valor=ctx.formParam(campo);datos.put(campo,valor==null?"":valor);}
        try {
            if(!Set.of("true","false").contains(datos.get("activo"))) throw new IllegalArgumentException("Selecciona el estado de la cuenta.");
            service.guardar(id,datos.get("nombre"),datos.get("correo"),ctx.formParam("clave"),Long.valueOf(datos.get("rolId")),Boolean.parseBoolean(datos.get("activo")),ctx.sessionAttribute("usuarioId"));
            ctx.sessionAttribute("adminAviso",id==null?"Cuenta creada correctamente.":"Cuenta actualizada. Sus permisos se aplicarán en la siguiente petición.");ctx.redirect("/eventos/usuarios");
        } catch(SecurityException ex) {throw new ForbiddenResponse(ex.getMessage());}
        catch(IllegalArgumentException ex) {ctx.status(400);formulario(ctx,id,datos,ex instanceof NumberFormatException?"Selecciona un rol válido.":ex.getMessage());}
    }
}
