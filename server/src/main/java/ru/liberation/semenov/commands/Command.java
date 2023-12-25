package ru.liberation.semenov.commands;

public interface Command {

    String ARGS_SEPARATOR = "|";

    String execute(String args);
}
