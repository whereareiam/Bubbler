package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;

import java.util.List;

@SuperBuilder
public final class TranslationMetadataPacket implements Packet {
	private final int entityId;
	private final Vector3f translation;

	@Override
	public void send(User user) {
		user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, createMetadata()));
	}

	private List<EntityData<?>> createMetadata() {
		return List.of(
				new EntityData<>(11, EntityDataTypes.VECTOR3F, translation)
		);
	}
}
