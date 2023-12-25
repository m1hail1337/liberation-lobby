package ru.liberation.semenov.commands;

import io.netty.channel.Channel;
import ru.liberation.semenov.MainHandler;
import ru.liberation.semenov.PlayerData;
import ru.liberation.semenov.Response;
import ru.liberation.semenov.ServerRoomHandler;

import java.util.Map;

public class ConnectCommand implements Command {

    private final MainHandler secondPlayerHandler;

    public ConnectCommand(MainHandler secondPlayerHandler) {
        this.secondPlayerHandler = secondPlayerHandler;
    }

    @Override
    public String execute(String args) {
        String[] data = args.split("\\|");
        String roomName = data[0];
        String nickname = data[1];
        Map<String, ServerRoomHandler> rooms = MainHandler.getRooms();
        ServerRoomHandler roomHandler = rooms.get(roomName);
        if (roomHandler != null) {
            Channel playerChannel = getPlayerChannel(nickname);
            roomHandler.setSecondPlayer(new PlayerData(nickname, playerChannel, secondPlayerHandler));
            swapHandlerToRoomHandler(roomHandler);
            for (Channel channel : MainHandler.getChannels().keySet()) {
                if (MainHandler.getChannels().get(channel).equals(roomHandler.getFirstPlayer().nickname())) {
                    channel.writeAndFlush(Response.ENEMY_CONNECTED.name() + ARGS_SEPARATOR + nickname);
                } else if (!MainHandler.getChannels().get(channel).equals(nickname)) {  // Пропускаем подключившегося
                    channel.writeAndFlush(Response.REMOVE_ROOM.name() + ARGS_SEPARATOR + roomHandler.getRoomName());
                }
            }
            return Response.CONNECTED.name() + ARGS_SEPARATOR + roomName + ARGS_SEPARATOR + roomHandler.getFirstPlayer().nickname();
        }
        return Response.FAILED.name();
    }

    private void swapHandlerToRoomHandler(ServerRoomHandler roomHandler) {
        secondPlayerHandler.getContext().channel().pipeline().replace(
                "defaultHandler",
                "roomHandler",
                roomHandler
        );
    }

    private Channel getPlayerChannel(String nickname) {
        return MainHandler.getChannels().entrySet().stream()
                .filter(e -> e.getValue().equals(nickname)).toList().get(0).getKey();
    }
}
