package ru.liberation.semenov;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.liberation.semenov.controllers.RoomController;
import ru.liberation.semenov.enums.Command;

/**
 * Основной класс сетевого взаимодействия с сервером. Здесь настраивается соединение, устанавливаются
 * обработчики ответов, выполняется отправка команд на сервер.
 * <br>*ВАЖНО*: даже в команду без аргументов необходимо добавить {@link Network#SEPARATOR}!
 */
public class Network {

    public static final String SEPARATOR = "|";

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private final DefaultClientHandler defaultHandler = new DefaultClientHandler();

    private SocketChannel channel;
    private ClientRoomHandler roomHandler;

    public Network() {
        Thread t = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                ChannelPipeline pipeline = socketChannel.pipeline();

                                pipeline.addLast("stringDecoder", new StringDecoder());
                                pipeline.addLast("stringEncoder", new StringEncoder());
                                pipeline.addLast("defaultHandler", defaultHandler);
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void register(String login, String password) {
        channel.writeAndFlush(Command.REGISTER.name() + SEPARATOR + login.length() + login + password);
    }

    public void authorize(String login, String password) {
        channel.writeAndFlush(Command.AUTH.name() + SEPARATOR + login.length() + login + password);
    }

    public void createNewRoom(String roomName, String nickname) {
        channel.writeAndFlush(Command.NEW_ROOM.name() + SEPARATOR + roomName + SEPARATOR + nickname);
    }

    public void close() {
        channel.close();
    }

    public DefaultClientHandler getDefaultHandler() {
        return defaultHandler;
    }

    public ClientRoomHandler getRoomHandler() {
        return roomHandler;
    }

    public void connectToExistedRoom(String nickname, String enemyNickname, RoomController controller) {
        this.roomHandler = new ClientRoomHandler(nickname, controller, defaultHandler);
        roomHandler.setEnemyNickname(enemyNickname);
        channel.pipeline().replace("defaultHandler", "roomHandler", roomHandler);
    }

    public void connectToCreatedRoom(String nickname, RoomController controller) {
        this.roomHandler = new ClientRoomHandler(nickname, controller, defaultHandler);
        channel.pipeline().replace("defaultHandler", "roomHandler", roomHandler);
        roomHandler.setFirstPlayer(true);
        // channel.writeAndFlush("START");
    }

    public void connect(String room, String nickname) {
        channel.writeAndFlush(Command.CONNECT.name() + SEPARATOR + room + SEPARATOR + nickname);
    }

    public void sendChatMessage(String nickname, String message) {
        channel.writeAndFlush("P2" + SEPARATOR + Command.CHAT.name() + SEPARATOR + nickname + SEPARATOR + message);
    }
}
