package ru.liberation.semenov;

public class Utils {

    public static String getStatus(String message) {
        int index = message.indexOf(Network.SEPARATOR);
        if (index == -1) {
            return message;
        }
        return message.substring(0, index);
    }

    public static String[] getArgs(String message) {
        int index = message.indexOf(Network.SEPARATOR);
        if (index == -1) {
            return new String[0];
        }
        return message.substring(index + 1).split("\\|");
    }

    public static String getArgsAsString(String message) {
        return message.substring(message.indexOf("|") + 1);
    }
}
