package cl.ucn.app.controller;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.EventoService;
import cl.ucn.app.service.PermisosEventos;
import io.javalin.http.*;
import java.time.*;
import java.util.*;

public class EventosController {
    private final EventoRepository eventos = new EventoRepository();
    private final InscripcionRepository inscripciones = new InscripcionRepository();
    private final CatalogoEventosRepository catalogo = new CatalogoEventosRepository();
    private final EventoService servicio = new EventoService(eventos);

    private boolean acceso(Context ctx, boolean escritura) {
        if (ctx.sessionAttribute("usuarioId") == null) { ctx.redirect("/login"); return false; }
        if (escritura) {
            String token = ctx.sessionAttribute("csrfToken");
            String recibido = ctx.formParam("csrfToken");
            if (token == null || recibido == null || !java.security.MessageDigest.isEqual(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8), recibido.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                ctx.status(403).result("Token de seguridad inválido. Vuelve a iniciar sesión."); return false;
            }
        }
        return true;
    }
    private boolean accesoOrganizador(Context ctx, boolean escritura) {
        if (!acceso(ctx, escritura)) return false;
        if (!PermisosEventos.organiza(ctx.sessionAttribute("usuarioRol")))
            throw new ForbiddenResponse("Solo el docente puede organizar eventos.");
        return true;
    }
    private Map<String,Object> modelo(Context ctx) {
        Map<String,Object> m = new HashMap<>();
        m.put("title", "Eventos académicos · SIGU-UCN");
        m.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        m.put("usuarioId", ctx.sessionAttribute("usuarioId"));
        m.put("csrfToken", ctx.sessionAttribute("csrfToken"));
        m.put("puedeInscribirse", PermisosEventos.participa(ctx.sessionAttribute("usuarioRol")));
        m.put("puedeOrganizar", PermisosEventos.organiza(ctx.sessionAttribute("usuarioRol")));
        m.put("admin", "ADMIN".equals(ctx.sessionAttribute("usuarioRol")));
        m.put("error", "");
        String aviso = ctx.sessionAttribute("eventosAviso");
        ctx.sessionAttribute("eventosAviso", null);
        m.put("aviso", aviso == null ? "" : aviso);
        return m;
    }
    private String parametro(Context ctx, String nombre) {
        String valor = ctx.queryParam(nombre); return valor == null ? "" : valor.strip();
    }
    public void mostrarEventos(Context ctx) {
        if (!acceso(ctx,false)) return;
        Map<String,Object> m = modelo(ctx);
        String q = parametro(ctx,"q"), tema = parametro(ctx,"tematica"), fecha = parametro(ctx,"fecha");
        String vista = parametro(ctx,"vista");
        if ("ADMIN".equals(ctx.sessionAttribute("usuarioRol")) && "mios".equals(vista)) { ctx.redirect("/eventos"); return; }
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        List<Evento> todos = eventos.findAll();
        Map<Long,long[]> resumen = inscripciones.resumenPorEvento(usuarioId);
        List<Evento> lista = todos.stream()
            .filter(e -> !"BORRADOR".equals(e.getEstado()) || PermisosEventos.administra(ctx.sessionAttribute("usuarioRol"), usuarioId, e.getOrganizador().getId()))
            .filter(e -> q.isEmpty() || (e.getTitulo()+" "+e.getLugar()).toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT)))
            .filter(e -> tema.isEmpty() || tema.equals(e.getTematica()))
            .filter(e -> fecha.isEmpty() || fecha.equals(e.getFecha().toString()))
            .filter(e -> !"agenda".equals(vista) || e.isDisponible())
            .filter(e -> !"mios".equals(vista) || usuarioId.equals(e.getOrganizador().getId()) || resumen.getOrDefault(e.getId(),new long[]{0,0})[1] > 0)
            .toList();
        m.put("eventos", lista); m.put("resumen", resumen);
        m.put("tematicas", todos.stream().map(Evento::getTematica).distinct().sorted().toList());
        m.put("q",q); m.put("tematica",tema); m.put("fecha",fecha); m.put("vista",vista);
        m.put("proximos",todos.stream().filter(Evento::isDisponible).count());
        m.put("misInscripciones",resumen.values().stream().filter(v -> v[1]>0).count());
        m.put("organizados",todos.stream().filter(e -> usuarioId.equals(e.getOrganizador().getId())).count());
        ctx.render("eventos.jte",m);
    }
    public void detalle(Context ctx) {
        if(!acceso(ctx,false)) return;
        long id;
        try { id=Long.parseLong(ctx.pathParam("id")); }
        catch(NumberFormatException ex) { throw new BadRequestResponse("ID de evento inválido"); }
        Evento e=eventos.findById(id);
        if(e==null) throw new NotFoundResponse("Evento no encontrado");
        Long usuario=ctx.sessionAttribute("usuarioId");
        boolean gestiona=PermisosEventos.administra(ctx.sessionAttribute("usuarioRol"),usuario,e.getOrganizador().getId());
        if("BORRADOR".equals(e.getEstado()) && !gestiona) throw new ForbiddenResponse("El evento aún no está publicado.");
        Map<String,Object> m=modelo(ctx);m.put("evento",e);m.put("gestiona",gestiona);
        m.put("consultaInscritos",PermisosEventos.consultaInscritos(ctx.sessionAttribute("usuarioRol"),usuario,e.getOrganizador().getId(),e.getEstado()));
        m.put("disponibles",Math.max(0L,e.getCupoMaximo()-inscripciones.contarInscritos(e.getId())));
        m.put("inscrito",inscripciones.estaInscrito(e.getId(),usuario));
        ctx.render("eventos-detalle.jte",m);
    }
    private Evento propio(Context ctx) {
        long id;
        try { id=Long.parseLong(ctx.pathParam("id")); }
        catch (NumberFormatException e) { throw new BadRequestResponse("ID de evento inválido"); }
        Evento evento=eventos.findById(id);
        if(evento==null) throw new NotFoundResponse("Evento no encontrado");
        Long usuario=ctx.sessionAttribute("usuarioId");
        if(!PermisosEventos.administra(ctx.sessionAttribute("usuarioRol"), usuario, evento.getOrganizador().getId())) throw new ForbiddenResponse("Solo el organizador puede administrar este evento.");
        return evento;
    }
    private void formulario(Context ctx, Evento e, Map<String,String> datos, String error) {
        Map<String,Object> m=modelo(ctx);
        m.put("evento",e); m.put("datos",datos); m.put("error",error);
        m.put("categorias",catalogo.categorias());
        m.put("expositores",PermisosEventos.organiza(ctx.sessionAttribute("usuarioRol")) ? catalogo.expositores() : List.of()); m.put("espacios",catalogo.espacios());
        ctx.render("eventos-formulario.jte",m);
    }
    private Map<String,String> datos(Evento e) {
        Map<String,String> d=new HashMap<>();
        if(e!=null) {
            d.put("titulo",e.getTitulo()); d.put("descripcion",e.getDescripcion()==null?"":e.getDescripcion());
            d.put("fecha",e.getFecha().toString()); d.put("horaInicio",e.getHoraInicio().toString()); d.put("horaFin",e.getHoraFin().toString());
            d.put("categoriaId",e.getCategoria()==null?"":e.getCategoria().getId().toString());
            d.put("cupoMaximo",e.getCupoMaximo().toString()); d.put("tematica",e.getTematica());
            d.put("espacioId",e.getEspacio()==null?"":e.getEspacio().getId().toString());
            d.put("expositorIds",e.getExpositores().stream().map(x -> x.getId().toString()).collect(java.util.stream.Collectors.joining(",")));
        }
        return d;
    }
    public void mostrarFormulario(Context ctx) { if(accesoOrganizador(ctx,false)) formulario(ctx,null,Map.of(),""); }
    public void mostrarFormularioEditar(Context ctx) {
        if(!accesoOrganizador(ctx,false)) return;
        Evento e=propio(ctx);
        if("CANCELADO".equals(e.getEstado())) throw new ConflictResponse("El evento cancelado no se puede editar.");
        formulario(ctx,e,datos(e),"");
    }
    private void guardar(Context ctx, Evento e) {
        Map<String,String> d=new HashMap<>();
        for(String campo:List.of("titulo","descripcion","fecha","horaInicio","horaFin","cupoMaximo","tematica","categoriaId","espacioId","expositorId")) {
            String valor=ctx.formParam(campo); d.put(campo,valor==null?"":valor.strip());
        }
        d.put("expositorIds", String.join(",", ctx.formParams("expositorIds")));
        try {
            if("CANCELADO".equals(e.getEstado())) throw new IllegalArgumentException("El evento cancelado no se puede editar.");
            e.setTitulo(d.get("titulo"));e.setDescripcion(d.get("descripcion"));e.setCategoria(catalogo.buscar(Categoria.class,Long.valueOf(d.get("categoriaId"))));
            e.setFecha(LocalDate.parse(d.get("fecha")));e.setHoraInicio(LocalTime.parse(d.get("horaInicio")));e.setHoraFin(LocalTime.parse(d.get("horaFin")));
            e.setCupoMaximo(Integer.parseInt(d.get("cupoMaximo")));
            e.setEspacio(catalogo.buscar(Espacio.class,Long.valueOf(d.get("espacioId"))));
            java.util.List<String> ids = ctx.formParams("expositorIds");
            if (ids.isEmpty() && !d.get("expositorId").isBlank()) ids = java.util.List.of(d.get("expositorId"));
            java.util.List<Expositor> seleccionados = new java.util.ArrayList<>();
            for(String id : new java.util.LinkedHashSet<>(ids)) seleccionados.add(catalogo.buscar(Expositor.class,Long.valueOf(id)));
            if (seleccionados.stream().anyMatch(java.util.Objects::isNull)) throw new IllegalArgumentException("Hay un expositor que ya no existe.");
            e.setExpositores(seleccionados);
            servicio.guardar(e); exito(ctx,"Evento guardado correctamente.");
        } catch(IllegalArgumentException | java.time.format.DateTimeParseException ex) {
            ctx.status(400); formulario(ctx,e.getId()==null?null:e,d,
                ex instanceof NumberFormatException || ex instanceof java.time.format.DateTimeParseException ? "Completa los campos con una fecha, horarios, cupo, ubicación y expositor válidos." : ex.getMessage());
        }
    }
    public void crearEvento(Context ctx) {
        if(!accesoOrganizador(ctx,true)) return;
        Evento e=new Evento(); e.setOrganizador(new UsuarioRepository().findById(ctx.sessionAttribute("usuarioId")));
        guardar(ctx,e);
    }
    public void actualizarEvento(Context ctx) { if(accesoOrganizador(ctx,true)) guardar(ctx,propio(ctx)); }
    public void reactivarEvento(Context ctx) {
        if (!accesoOrganizador(ctx, true)) return;
        try {
            Evento evento = propio(ctx);
            servicio.reactivar(evento);
            ctx.sessionAttribute("eventosAviso", "Evento recuperado como borrador. Revisa fecha, sala y cupos antes de publicarlo. Las inscripciones anteriores quedan canceladas.");
            ctx.redirect("/eventos/" + evento.getId() + "/editar");
        } catch (IllegalArgumentException e) {
            ctx.status(409);
            Map<String,Object> m = modelo(ctx); m.put("error", e.getMessage());
            ctx.render("eventos-error.jte", m);
        }
    }
    public void cancelarEvento(Context ctx) { cambiar(ctx,false); }
    public void publicarEvento(Context ctx) { cambiar(ctx,true); }
    private void cambiar(Context ctx,boolean publicar) {
        if(!accesoOrganizador(ctx,true)) return;
        try {
            Evento e=propio(ctx); if(publicar) servicio.publicar(e); else servicio.cancelar(e);
            exito(ctx,publicar?"Evento publicado. Las inscripciones ya están abiertas.":"Evento cancelado. Se conserva el historial de participantes.");
        } catch(IllegalArgumentException e) {
            ctx.status(409); Map<String,Object> m=modelo(ctx);m.put("error",e.getMessage()); ctx.render("eventos-error.jte",m);
        }
    }
    private void exito(Context ctx,String mensaje) { ctx.sessionAttribute("eventosAviso",mensaje);ctx.redirect("/eventos"); }
    public void catalogos(Context ctx) {
        if(!acceso(ctx,false)) return;
        if (!PermisosEventos.organiza(ctx.sessionAttribute("usuarioRol")) && !"ADMIN".equals(ctx.sessionAttribute("usuarioRol")))
            throw new ForbiddenResponse("Los catálogos son exclusivos del docente y del administrador.");
        catalogos(ctx,"");
    }
    private void catalogos(Context ctx,String error) {
        Map<String,Object> m=modelo(ctx);m.put("error",error);
        m.put("categorias",catalogo.categorias());
        m.put("expositores",PermisosEventos.organiza(ctx.sessionAttribute("usuarioRol")) ? catalogo.expositores() : List.of());m.put("espacios",catalogo.espacios());
        ctx.render("eventos-catalogos.jte",m);
    }
    public void guardarCategoria(Context ctx) {
        if(!acceso(ctx,true)) return;
        if(!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse("Solo el administrador gestiona categorías.");
        try {
            catalogo.registrarCategoria(texto(ctx,"nombre",100,true));
            ctx.sessionAttribute("eventosAviso","Categoría registrada.");ctx.redirect("/eventos/catalogos");
        } catch(IllegalArgumentException ex) { ctx.status(400);catalogos(ctx,ex.getMessage()); }
    }
    public void guardarExpositor(Context ctx) {
        if(!accesoOrganizador(ctx,true)) return;
        try {
            Expositor e=new Expositor(); e.setNombre(texto(ctx,"nombre",100,true));
            e.setCorreo(texto(ctx,"correo",120,false));e.setEspecialidad(texto(ctx,"especialidad",150,false));
            e.setInstitucion(texto(ctx,"institucion",150,false));
            if(!e.getCorreo().isBlank() && !e.getCorreo().matches("[^\\s@]+@[^\\s@]+[.][^\\s@]+")) throw new IllegalArgumentException("Ingresa un correo válido.");
            catalogo.guardar(e);ctx.sessionAttribute("eventosAviso","Expositor registrado.");ctx.redirect("/eventos/catalogos");
        } catch(IllegalArgumentException e) { ctx.status(400);catalogos(ctx,e.getMessage()); }
    }
    public void mostrarExpositorEditar(Context ctx) {
        if (!accesoOrganizador(ctx, false)) return;
        formularioExpositor(ctx, expositor(ctx), "");
    }
    private Expositor expositor(Context ctx) {
        try {
            Expositor e = catalogo.buscar(Expositor.class, Long.valueOf(ctx.pathParam("id")));
            if(e == null) throw new NotFoundResponse("Expositor no encontrado");
            return e;
        } catch(NumberFormatException ex) { throw new BadRequestResponse("ID inválido"); }
    }
    private void formularioExpositor(Context ctx, Expositor e, String error) {
        Map<String,Object> m=modelo(ctx);m.put("expositor",e);m.put("error",error);
        ctx.render("eventos-expositor.jte",m);
    }
    public void actualizarExpositor(Context ctx) {
        if (!accesoOrganizador(ctx, true)) return;
        Expositor e = expositor(ctx);
        try {
            e.setNombre(texto(ctx,"nombre",100,true));e.setCorreo(texto(ctx,"correo",120,false));
            e.setEspecialidad(texto(ctx,"especialidad",150,false));e.setInstitucion(texto(ctx,"institucion",150,false));
            if (!e.getCorreo().isBlank() && !e.getCorreo().matches("[^\\s@]+@[^\\s@]+[.][^\\s@]+")) throw new IllegalArgumentException("Ingresa un correo válido.");
            catalogo.guardar(e);ctx.sessionAttribute("eventosAviso","Expositor actualizado.");ctx.redirect("/eventos/catalogos");
        } catch(IllegalArgumentException ex) { ctx.status(400);formularioExpositor(ctx,e,ex.getMessage()); }
    }
    public void guardarUbicacion(Context ctx) {
        if(!acceso(ctx,true)) return;
        if(!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse("Solo el administrador gestiona ubicaciones.");
        try {
            String nombre=texto(ctx,"nombre",100,true); int capacidad=Integer.parseInt(texto(ctx,"capacidad",9,true));
            if(capacidad<1) throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
            if(catalogo.espacios().stream().anyMatch(e->e.getNombre().strip().equalsIgnoreCase(nombre))) throw new IllegalArgumentException("Ya existe una ubicación con ese nombre.");
            catalogo.guardar(new Espacio(nombre,"EVENTOS",capacidad,true));ctx.sessionAttribute("eventosAviso","Ubicación registrada.");ctx.redirect("/eventos/catalogos");
        } catch(IllegalArgumentException e) { ctx.status(400);catalogos(ctx,e instanceof NumberFormatException?"Ingresa una capacidad válida.":e.getMessage()); }
    }
    public void mostrarUbicacionEditar(Context ctx) {
        if (!acceso(ctx, false)) return;
        if (!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse();
        formularioUbicacion(ctx, ubicacion(ctx), "");
    }
    private Espacio ubicacion(Context ctx) {
        try {
            Espacio e = catalogo.buscar(Espacio.class, Long.valueOf(ctx.pathParam("id")));
            if(e == null) throw new NotFoundResponse("Ubicación no encontrada");
            return e;
        } catch(NumberFormatException ex) { throw new BadRequestResponse("ID inválido"); }
    }
    private void formularioUbicacion(Context ctx, Espacio e, String error) {
        Map<String,Object> m = modelo(ctx);m.put("espacio",e);m.put("error",error);
        ctx.render("eventos-ubicacion.jte",m);
    }
    public void actualizarUbicacion(Context ctx) {
        if (!acceso(ctx, true)) return;
        if (!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse();
        Espacio e = ubicacion(ctx);
        try {
            e.setNombre(texto(ctx,"nombre",100,true));e.setTipo(texto(ctx,"tipo",50,true));
            e.setCapacidad(Integer.parseInt(texto(ctx,"capacidad",9,true)));
            String disponible=ctx.formParam("disponible");
            if (!"true".equals(disponible) && !"false".equals(disponible)) throw new IllegalArgumentException("Selecciona la disponibilidad.");
            e.setDisponible(Boolean.valueOf(disponible));catalogo.actualizarEspacio(e);
            ctx.sessionAttribute("eventosAviso","Ubicación actualizada.");ctx.redirect("/eventos/catalogos");
        } catch(IllegalArgumentException ex) {
            ctx.status(400);formularioUbicacion(ctx,e,ex instanceof NumberFormatException?"La capacidad debe ser un número entero.":ex.getMessage());
        }
    }
    public void cambiarUbicacion(Context ctx) {
        if(!acceso(ctx,true)) return;
        if(!"ADMIN".equals(ctx.sessionAttribute("usuarioRol"))) throw new ForbiddenResponse();
        try {
            Espacio e=catalogo.buscar(Espacio.class,Long.valueOf(ctx.pathParam("id")));
            if(e==null) throw new NotFoundResponse();
            e.setDisponible(!Boolean.TRUE.equals(e.getDisponible()));catalogo.guardar(e);ctx.redirect("/eventos/catalogos");
        } catch(NumberFormatException e) { throw new BadRequestResponse("ID inválido"); }
    }
    private String texto(Context ctx,String campo,int max,boolean obligatorio) {
        String v=ctx.formParam(campo);v=v==null?"":v.strip();
        if((obligatorio && v.isBlank()) || v.length()>max) throw new IllegalArgumentException("Revisa el campo "+campo+" (máximo "+max+" caracteres).");
        return v;
    }
}
