package ru.liberation.semenov.commands;

import ru.liberation.semenov.PlayerData;
import ru.liberation.semenov.Response;
import ru.liberation.semenov.ServerRoomHandler;

public class ChatCommand implements Command {

    private final ServerRoomHandler serverRoomHandler;

    public ChatCommand(ServerRoomHandler serverRoomHandler) {
        this.serverRoomHandler = serverRoomHandler;
    }

    @Override
    public String execute(String args) {
        String nickname = args.substring(0, args.indexOf(ARGS_SEPARATOR));
        String message = args.substring(args.indexOf(ARGS_SEPARATOR) + 1);
        sendMessageToPlayer(serverRoomHandler.getFirstPlayer(), nickname, message);
        sendMessageToPlayer(serverRoomHandler.getSecondPlayer(), nickname, message);
        return "";
    }

    private void sendMessageToPlayer(PlayerData playerData, String sourceNickname, String message) {
        if (playerData != null) {
            playerData.playerChannel().writeAndFlush(
                    Response.CHAT.name() + ARGS_SEPARATOR + sourceNickname + ARGS_SEPARATOR + message
            );
        }
    }
}
