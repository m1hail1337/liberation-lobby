package ru.liberation.semenov.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import ru.liberation.semenov.Action;
import ru.liberation.semenov.Network;
import ru.liberation.semenov.Utils;
import ru.liberation.semenov.enums.Response;

public class RegisterController {

    public record RegisteredUser(String login, String password) { }

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button regButton;
    @FXML
    private Text statusInfo;

    private Network network;
    private RegisteredUser registeredUser;

    private final Action registerAction = message -> {
        if (Utils.getStatus(message).equals(Response.SUCCESS.name())) {
            onSuccessRegister();

        } else if (Utils.getStatus(message).equals(Response.FAILED.name())) {
            statusInfo.setText("Пользователь " + loginField.getText().trim() + " уже существует.");
            statusInfo.setFill(Color.RED);
            statusInfo.setVisible(true);

        } else {
            statusInfo.setText("Ошибка сервера.");
            statusInfo.setFill(Color.RED);
        }
    };

    @FXML
    private void onRegister() {
        network.getDefaultHandler().setAction(registerAction);
        network.register(loginField.getText().trim(), passwordField.getText().trim());
    }

    private void onSuccessRegister() {
        registeredUser = new RegisteredUser(loginField.getText().trim(), passwordField.getText().trim());
        statusInfo.setText("Пользователь " + registeredUser.login() + " успешно зарегистрирован.");
        statusInfo.setFill(Color.GREEN);
        statusInfo.setVisible(true);
        regButton.setVisible(false);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public RegisteredUser getRegisteredUser() {
        return registeredUser;
    }
}
