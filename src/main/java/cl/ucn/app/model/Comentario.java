package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @Column(name = "contenido", nullable = false)
    private String contenido;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    public Long getIdComentario() { return idComentario; }
    public void setIdComentario(Long id) { this.idComentario = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String c) { this.contenido = c; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime f) { this.fecha = f; }
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket t) { this.ticket = t; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario u) { this.autor = u; }
}