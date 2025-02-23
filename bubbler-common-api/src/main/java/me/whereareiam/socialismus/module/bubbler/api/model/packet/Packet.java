package me.whereareiam.socialismus.module.bubbler.api.model.packet;

import com.github.retrooper.packetevents.protocol.player.User;

public interface Packet {
	void send(User user);
}
