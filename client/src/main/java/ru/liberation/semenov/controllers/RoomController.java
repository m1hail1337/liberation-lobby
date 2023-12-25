package ru.liberation.semenov.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.liberation.semenov.Network;

public class RoomController {

    @FXML
    private TextArea chatField;
    @FXML
    private TextField chatInput;
    @FXML
    private Text firstPlayer;
    @FXML
    private Text secondPlayer;

    private Network network;

    public void setFirstPlayer(String nickname) {
        firstPlayer.setText(nickname);
    }

    public void setSecondPlayer(String enemyNickname) {
        secondPlayer.setText(enemyNickname);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    private void sendChatMessage(ActionEvent event) {
        network.sendChatMessage(firstPlayer.getText(), chatInput.getText());
        chatInput.clear();
    }

    public TextArea getChatField() {
        return chatField;
    }
}
