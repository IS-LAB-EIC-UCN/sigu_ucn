package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Libro;

public class LibroDisponible {
    private final Libro libro;
    private final int disponibles;

    public LibroDisponible(Libro libro, int disponibles) {
        this.libro = libro;
        this.disponibles = disponibles;
    }

    public Libro getLibro() { return libro; }
    public int getDisponibles() { return disponibles; }

    public String getLabel() {
        return libro.getTitulo() + " (" + disponibles + (disponibles == 1 ? " disponible)" : " disponibles)");
    }
}
