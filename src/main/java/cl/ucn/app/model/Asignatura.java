package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tut_asignaturas")
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String carrera;

    @Column(nullable = false)
    private Integer semestre;

    public Asignatura() {
    }

    public Asignatura(String codigo, String nombre, String carrera, Integer semestre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.carrera = carrera;
        this.semestre = semestre;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }
}
