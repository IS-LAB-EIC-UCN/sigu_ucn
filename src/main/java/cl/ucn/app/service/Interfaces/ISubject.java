package cl.ucn.app.service.Interfaces;

public interface ISubject {
    public void addObserver(IObserver observer);
    public void removeObserver(IObserver observer);
    public void notifyObservers();
}
