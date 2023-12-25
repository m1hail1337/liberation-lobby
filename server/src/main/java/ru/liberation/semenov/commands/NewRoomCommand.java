package ru.liberation.semenov.commands;

import ru.liberation.semenov.MainHandler;
import ru.liberation.semenov.PlayerData;
import ru.liberation.semenov.Response;
import io.netty.channel.Channel;
import ru.liberation.semenov.ServerRoomHandler;

public class NewRoomCommand implements Command {

    private final MainHandler firstPlayerHandler;

    public NewRoomCommand(MainHandler handler) {
        this.firstPlayerHandler = handler;
    }

    @Override
    public String execute(String args) {
        String roomName = args.substring(0, args.indexOf(ARGS_SEPARATOR));
        String nickname = args.substring(args.indexOf(ARGS_SEPARATOR) + 1);
        swapHandlerToRoomHandler(roomName, nickname);
        for (Channel channel : MainHandler.getChannels().keySet()) {
            channel.writeAndFlush("ADD_ROOM" + ARGS_SEPARATOR + roomName);
        }
        return Response.CONNECTED.name() + ARGS_SEPARATOR + roomName;
    }

    private void swapHandlerToRoomHandler(String roomName, String nickname) {
        Channel playerChannel = getPlayerChannel(nickname);
        ServerRoomHandler roomHandler =  new ServerRoomHandler(
                roomName,
                new PlayerData(nickname, playerChannel, firstPlayerHandler)
        );
        firstPlayerHandler.getContext().channel().pipeline().replace(
                "defaultHandler",
                "roomHandler",
                roomHandler
        );
        MainHandler.getRooms().put(roomName, roomHandler);
    }

    private Channel getPlayerChannel(String nickname) {
        return MainHandler.getChannels().entrySet().stream()
                .filter(e -> e.getValue().equals(nickname)).toList().get(0).getKey();
    }
}
