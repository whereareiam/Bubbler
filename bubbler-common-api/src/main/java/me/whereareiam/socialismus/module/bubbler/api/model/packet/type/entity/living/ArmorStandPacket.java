package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.living;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.EntityPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArmorStandPacket extends EntityPacket {
	private final boolean small;

	private ArmorStandPacket(Builder builder) {
		super(builder);
		this.small = builder.small;
	}

	@Override
	public void send(User user) {
		WrapperPlayServerSpawnEntity spawnPacket = createSpawnPacket();
		WrapperPlayServerEntityMetadata metadataPacket = createMetadataPacket();

		user.sendPacket(spawnPacket);
		user.sendPacket(metadataPacket);
	}

	private WrapperPlayServerSpawnEntity createSpawnPacket() {
		return new WrapperPlayServerSpawnEntity(
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData> metadata = new ArrayList<>();
		addCommonMetadata(metadata);

		byte status = 0;
		if (small) status |= 0x01;

		if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_21_4)) {
			metadata.add(new EntityData(15, EntityDataTypes.BYTE, status));
		} else {
			// TODO
		}

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}

	public static class Builder extends EntityPacket.Builder<Builder> {
		private boolean small;

		@Override
		protected Builder self() {
			return this;
		}

		public Builder withSmall(boolean small) {
			this.small = small;
			return this;
		}

		public ArmorStandPacket build(Version version) {
			this.version = version;
			return new ArmorStandPacket(this);
		}
	}
}
