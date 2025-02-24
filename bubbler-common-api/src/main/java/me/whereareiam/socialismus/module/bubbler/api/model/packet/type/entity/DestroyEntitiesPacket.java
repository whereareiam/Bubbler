package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;

@SuperBuilder
public class DestroyEntitiesPacket implements Packet {
	private final int[] entityIds;

	@Override
	public void send(User user) {
		WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(entityIds);
		user.sendPacket(packet);
	}
}
