package me.whereareiam.socialismus.module.bubbler.common.packet;

import com.github.retrooper.packetevents.protocol.player.User;

public interface Packet {
	void send(User user);
}
