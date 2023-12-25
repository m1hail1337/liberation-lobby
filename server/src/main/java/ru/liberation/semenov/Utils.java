package ru.liberation.semenov;

import static ru.liberation.semenov.MainHandler.SEPARATOR;

public class Utils {

    public static String getCommandInRoomMessages(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return "";
        }
        return message.substring(index + 1).split("\\|")[0];
    }

    public static String getPlayerInRoomMessages(String message) {
        int index = message.indexOf(SEPARATOR);
        if (index == -1) {
            return message;
        }
        return message.substring(0, index);
    }

    public static String getArgsAsStringInRoom(String message) {
        String messageWithCommand = message.substring(message.indexOf("|") + 1);
        return messageWithCommand.substring(messageWithCommand.indexOf("|") + 1); // пропускаем P1 и команду
    }
}
