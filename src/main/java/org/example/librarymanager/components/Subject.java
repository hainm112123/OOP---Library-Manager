package org.example.librarymanager.components;

public interface Subject {
    public void attach(Observer observer);

    public void detach(Observer observer);

    public void notifyObservers();
}
