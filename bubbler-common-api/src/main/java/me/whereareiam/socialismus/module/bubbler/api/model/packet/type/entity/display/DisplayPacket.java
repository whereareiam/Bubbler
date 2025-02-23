package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.EntityPacket;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@SuperBuilder
public class DisplayPacket extends EntityPacket {
	private final Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
	private final DisplayType type;

	protected void addDisplayMetadata(List<EntityData> metadata) {
		super.addCommonMetadata(metadata);

		if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_21_4)) {
			metadata.add(new EntityData(12, EntityDataTypes.VECTOR3F, scale));
			metadata.add(new EntityData(15, EntityDataTypes.BYTE, type.getValue()));
		}
	}

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		user.sendPacket(spawnPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.DISPLAY,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}
}
