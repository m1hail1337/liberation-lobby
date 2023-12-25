package ru.liberation.semenov.game;

import ru.liberation.semenov.Action;
import ru.liberation.semenov.ClientRoomHandler;

public class ChatAction implements Action {

    private final ClientRoomHandler roomHandler;

    public ChatAction(ClientRoomHandler roomHandler) {
        this.roomHandler = roomHandler;
    }

    @Override
    public void handle(String args) {
        StringBuilder sb = new StringBuilder();
        String nickname = args.substring(0, args.indexOf(SEPARATOR));
        String inputMessage = args.substring(args.indexOf(SEPARATOR) + 1);
        sb.append("[").append(nickname).append("]: ").append(inputMessage).append("\n");
        roomHandler.getController().getChatField().appendText(sb.toString());
    }
}
