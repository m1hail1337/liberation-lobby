package ru.liberation.semenov;

public interface Action {

    String SEPARATOR = "|";

    void handle(String args);
}
