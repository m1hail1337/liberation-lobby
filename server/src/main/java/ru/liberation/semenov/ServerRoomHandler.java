package ru.liberation.semenov;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.liberation.semenov.commands.ChatCommand;
import ru.liberation.semenov.commands.Command;

import java.util.HashMap;
import java.util.Map;

import static ru.liberation.semenov.Utils.getArgsAsStringInRoom;
import static ru.liberation.semenov.Utils.getPlayerInRoomMessages;
import static ru.liberation.semenov.Utils.getCommandInRoomMessages;


@ChannelHandler.Sharable
public class ServerRoomHandler extends SimpleChannelInboundHandler<String> {

    private final String roomName;
    private final PlayerData firstPlayer;
    private PlayerData secondPlayer;
    private final Map<String, Command> commands = new HashMap<>(){{
        put("CHAT", new ChatCommand(ServerRoomHandler.this));
    }};

    public ServerRoomHandler(String roomName, PlayerData firstPlayer) {
        this.roomName = roomName;
        this.firstPlayer = firstPlayer;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        PlayerData player = getEnemyPlayer(getPlayerInRoomMessages(msg));
        String command = getCommandInRoomMessages(msg);
        String args = getArgsAsStringInRoom(msg);
        String response = commands.get(command).execute(args);
        player.playerChannel().writeAndFlush(response);
    }

    private PlayerData getEnemyPlayer(String player) {
        if (player.equals("P1"))
            return secondPlayer;
        return firstPlayer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switchToDefaultHandler(ctx);

    }

    private void switchToDefaultHandler(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.channel().pipeline();
        pipeline.replace("roomHandler", "defaultHandler", firstPlayer.playerHandler());
        pipeline.replace("roomHandler", "defaultHandler", secondPlayer.playerHandler());
    }

    public void setSecondPlayer(PlayerData secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getRoomName() {
        return roomName;
    }

    public PlayerData getFirstPlayer() {
        return firstPlayer;
    }

    public PlayerData getSecondPlayer() {
        return secondPlayer;
    }
}
