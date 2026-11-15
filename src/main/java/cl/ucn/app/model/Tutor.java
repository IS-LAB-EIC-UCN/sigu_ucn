package cl.ucn.app.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tut_tutores")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
    private Estudiante estudiante;

    @Column(nullable = false)
    private Boolean certificado = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tut_tutor_asignaturas",
            joinColumns = @JoinColumn(name = "tutor_id"),
            inverseJoinColumns = @JoinColumn(name = "asignatura_id")
    )
    private Set<Asignatura> asignaturasCertificadas = new HashSet<>();

    public Tutor() {
    }

    public Tutor(Estudiante estudiante, Set<Asignatura> asignaturasCertificadas) {
        this.estudiante = estudiante;
        this.asignaturasCertificadas = asignaturasCertificadas;
        this.certificado = true;
    }

    public Long getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Boolean getCertificado() {
        return certificado;
    }

    public void setCertificado(Boolean certificado) {
        this.certificado = certificado;
    }

    public Set<Asignatura> getAsignaturasCertificadas() {
        return asignaturasCertificadas;
    }

    public void setAsignaturasCertificadas(Set<Asignatura> asignaturasCertificadas) {
        this.asignaturasCertificadas = asignaturasCertificadas;
    }
}