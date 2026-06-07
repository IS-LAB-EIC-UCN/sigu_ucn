package cl.ucn.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expositores")
public class Expositor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 120)
    private String email;

    @Column(length = 200)
    private String afiliacion;

    @Column(length = 20)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ManyToMany(mappedBy = "expositores")
    private List<Evento> eventos = new ArrayList<>();

    public Expositor() {}

    public Expositor(String nombre, String email, String afiliacion) {
        this.nombre = nombre;
        this.email = email;
        this.afiliacion = afiliacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAfiliacion() { return afiliacion; }
    public void setAfiliacion(String afiliacion) { this.afiliacion = afiliacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<Evento> getEventos() { return eventos; }
    public void setEventos(List<Evento> eventos) { this.eventos = eventos; }
}
