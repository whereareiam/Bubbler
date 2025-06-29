package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;

import java.util.List;

@Getter
@SuperBuilder
public class InterpolationMetadataPacket implements Packet {
	private final int entityId;
	private final int startDelayTicks;
	private final int transformDurationTicks;
	private final int positionRotationDurationTicks;

	@Override
	public void send(User user) {
		user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, createMetadata()));
	}

	private List<EntityData<?>> createMetadata() {
		return List.of(
				new EntityData<>(8, EntityDataTypes.INT, startDelayTicks),
				new EntityData<>(9, EntityDataTypes.INT, transformDurationTicks),
				new EntityData<>(10, EntityDataTypes.INT, positionRotationDurationTicks)
		);
	}
}
