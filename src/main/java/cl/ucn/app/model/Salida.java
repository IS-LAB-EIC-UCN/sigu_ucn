package cl.ucn.app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import cl.ucn.app.service.Interfaces.IObserver;
import cl.ucn.app.service.Interfaces.ISubject;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "salidas")
public class Salida extends MovimientoInventario implements ISubject {
    
    private ArrayList<IObserver> observers;

    public Salida() {}

    public Salida(Recurso recurso, int cantidad, LocalDate fecha, LocalTime hora) {
        super(recurso, cantidad, fecha, hora);
        observers = new ArrayList<IObserver>();
    }

    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (IObserver o: observers) {
            o.actualizarStock();
        }
    }

}
