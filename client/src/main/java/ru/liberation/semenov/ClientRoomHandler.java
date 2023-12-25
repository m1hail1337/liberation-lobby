package ru.liberation.semenov;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.liberation.semenov.controllers.RoomController;
import ru.liberation.semenov.game.ChatAction;
import ru.liberation.semenov.game.EnemyConnectedAction;

import java.util.HashMap;
import java.util.Map;

import static ru.liberation.semenov.Utils.getArgsAsString;
import static ru.liberation.semenov.Utils.getStatus;


public class ClientRoomHandler extends SimpleChannelInboundHandler<String> {

    private static final String SEPARATOR = "|";
    /**
     * Ссылка на стандартный хендлер работы со строковыми командами в лобби, при завершении файлового обмена переключимся на него
     */
    private final DefaultClientHandler defaultHandler;
    private final RoomController controller;
    private final Map<String, Action> gameActions = new HashMap<>() {{
        put("ENEMY_CONNECTED", new EnemyConnectedAction(ClientRoomHandler.this));
        put("CHAT", new ChatAction(ClientRoomHandler.this));
    }};

    private boolean isFirstPlayer = false;
    private String enemyNickname;

    public ClientRoomHandler(String enemyNickname, RoomController controller, DefaultClientHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        this.enemyNickname = enemyNickname;
        this.controller = controller;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        String action = getStatus(msg);
        String args = getArgsAsString(msg);
        gameActions.get(action).handle(args);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        switchToDefaultHandler(ctx);
    }

    private void switchToDefaultHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().replace("roomHandler", "defaultHandler", defaultHandler);

    }

    public void setEnemyNickname(String enemyNickname) {
        this.enemyNickname = enemyNickname;
        controller.setSecondPlayer(enemyNickname);
    }

    public void setFirstPlayer(boolean firstPlayer) {
        isFirstPlayer = firstPlayer;
    }

    public RoomController getController() {
        return controller;
    }
}
