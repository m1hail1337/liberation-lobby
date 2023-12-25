package ru.liberation.semenov;

import static io.netty.channel.ChannelHandler.Sharable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.liberation.semenov.commands.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Серверный обработчик сообщений. Аннотация {@link Sharable} позволяет сохранить текущий хендлер и переключится на него
 * после загрузки файла.
 */
@Sharable
public class MainHandler extends SimpleChannelInboundHandler<String> {

    public static final String SEPARATOR = "|";
    private static final String PATH_TO_AUTH_DATA = "server\\src\\main\\resources\\users.txt";
    private static final Map<Channel, String> channels = new HashMap<>();
    private static final Map<String, ServerRoomHandler> rooms = new HashMap<>();

    private static final Map<String, String> users = new HashMap<>();

    private final Map<String, Command> commands = new HashMap<>() {{
        put("AUTH", new AuthCommand(MainHandler.this));
        put("REGISTER", new RegisterCommand());
        put("NEW_ROOM", new NewRoomCommand(MainHandler.this));
        put("CONNECT", new ConnectCommand(MainHandler.this));
    }};

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
        initUsersMap();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        this.context = ctx;
        String command = msg.substring(0, msg.indexOf(SEPARATOR));
        String args = msg.substring(msg.indexOf(SEPARATOR) + 1);
        String response = commands.get(command).execute(args);

        if (!response.equals(Response.EMPTY.name())) {
            ctx.channel().writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        throw new RuntimeException("Ошибка сервера", cause);
    }

    private void initUsersMap() {
        Path filePath = Paths.get(PATH_TO_AUTH_DATA);
        try (Stream<String> lines = Files.lines(filePath)) {
            users.clear();
            users.putAll(lines.collect(Collectors.toMap(k -> k.split("\\s")[0], v -> v.split("\\s")[1])));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл: " + PATH_TO_AUTH_DATA, e);
        }
    }

    public static String getPathToAuthData() {
        return PATH_TO_AUTH_DATA;
    }

    public static Map<String, String> getUsers() {
        return users;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public static Map<Channel, String> getChannels() {
        return channels;
    }

    public static Map<String, ServerRoomHandler> getRooms() {
        return rooms;
    }
}
