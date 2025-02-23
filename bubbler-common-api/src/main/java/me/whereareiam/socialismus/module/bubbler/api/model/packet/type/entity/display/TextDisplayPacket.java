package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.api.type.AlignmentType;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@SuperBuilder
public class TextDisplayPacket extends DisplayPacket {
	private final Component text;
	private final boolean hasShadow;
	private final boolean isSeeThrough;
	private final AlignmentType alignment;
	private final int backgroundColor;
	private final short transparency;

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		WrapperPlayServerEntityMetadata metadataPacket = createMetadataPacket();

		user.sendPacket(spawnPacket);
		user.sendPacket(metadataPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData> metadata = new ArrayList<>();
		addDisplayMetadata(metadata);

		if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_21_4)) {
			metadata.add(new EntityData(23, EntityDataTypes.ADV_COMPONENT, text));
			metadata.add(new EntityData(25, EntityDataTypes.INT, backgroundColor));
			metadata.add(new EntityData(26, EntityDataTypes.BYTE, (byte) (255 - (transparency * 255) / 100)));

			byte flags = 0;
			if (hasShadow) flags |= 0x01;
			if (isSeeThrough) flags |= 0x02;
			if (alignment != null) flags |= alignment.getValue();

			metadata.add(new EntityData(27, EntityDataTypes.BYTE, flags));
		}

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}
}
