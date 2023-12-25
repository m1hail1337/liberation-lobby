package ru.liberation.semenov;

import io.netty.channel.Channel;

public record PlayerData(String nickname, Channel playerChannel, MainHandler playerHandler) {
}
