package org.example.librarymanager.models;

import javafx.util.Pair;

import java.util.List;

public interface Model extends Cloneable {
    public Model clone();

    public List<String> getAttributes();

    public List<Pair<String, String>> getData();

    public void setData(List<Pair<String, String>> data);
}
