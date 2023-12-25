package ru.liberation.semenov.commands;

import ru.liberation.semenov.MainHandler;
import ru.liberation.semenov.Response;
import ru.liberation.semenov.ServerRoomHandler;

public class AuthCommand implements Command {

    private final MainHandler mainHandler;

    public AuthCommand(MainHandler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public String execute(String args) {
        int nicknameLength = Character.getNumericValue(args.charAt(0));
        String nickname = args.substring(1, nicknameLength + 1);
        String password = args.substring(nicknameLength + 1);
        if (MainHandler.getUsers().containsKey(nickname)) {
            if (MainHandler.getUsers().get(nickname).equals(password)) {
                putNewUserChannel(nickname);
                StringBuilder response = new StringBuilder(Response.SUCCESS.name());
                for (ServerRoomHandler roomHandler : MainHandler.getRooms().values()) {
                    if (roomHandler.getSecondPlayer() == null) {
                        response.append(ARGS_SEPARATOR).append(roomHandler.getRoomName());
                    }
                }
                return response.toString();
            }
            return Response.FAILED.name() + ARGS_SEPARATOR + "Incorrect password";
        }
        return Response.FAILED.name() + ARGS_SEPARATOR + "There's no user with this nickname";
    }

    private void putNewUserChannel(String nickname) {
        MainHandler.getChannels().put(mainHandler.getContext().channel(), nickname);
    }
}
