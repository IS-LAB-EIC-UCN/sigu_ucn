package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long idTicket;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "prioridad", nullable = false)
    private String prioridad;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "resolucion")
    private String resolucion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaTicket categoria;

    public Long getIdTicket() { return idTicket; }
    public void setIdTicket(Long id) { this.idTicket = id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public String getResolucion() { return resolucion; }
    public void setResolucion(String r) { this.resolucion = r; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario u) { this.solicitante = u; }
    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario u) { this.tecnico = u; }
    public CategoriaTicket getCategoria() { return categoria; }
    public void setCategoria(CategoriaTicket c) { this.categoria = c; }
}