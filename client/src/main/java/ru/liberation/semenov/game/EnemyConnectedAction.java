package ru.liberation.semenov.game;

import ru.liberation.semenov.ClientRoomHandler;

public class EnemyConnectedAction implements ru.liberation.semenov.Action {

    private final ClientRoomHandler clientRoomHandler;

    public EnemyConnectedAction(ClientRoomHandler clientRoomHandler) {
        this.clientRoomHandler = clientRoomHandler;
    }

    @Override
    public void handle(String args) {
        clientRoomHandler.setEnemyNickname(args);
    }
}
