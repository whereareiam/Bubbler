package me.whereareiam.socialismus.module.bubbler.common.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TextDisplayPacket extends DisplayPacket {
	private final Component text;
	private final byte alignment;
	private final int backgroundColor;

	private TextDisplayPacket(Builder builder) {
		super(builder);
		this.text = builder.text;
		this.alignment = builder.alignment;
		this.backgroundColor = builder.backgroundColor;
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
				entityId, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
				position, 0.0F, 0.0F, 0.0F, 0, Optional.empty()
		);
	}

	private WrapperPlayServerEntityMetadata createMetadataPacket() {
		List<EntityData> metadata = new ArrayList<>();
		addDisplayMetadata(metadata);

		if (version.isAtLeast(Version.V_1_21_4)) {
			metadata.add(new EntityData(23, EntityDataTypes.ADV_COMPONENT, text));
			metadata.add(new EntityData(27, EntityDataTypes.BYTE, alignment));
		} else {
			// TODO
		}

		return new WrapperPlayServerEntityMetadata(entityId, metadata);
	}

	public static class Builder extends DisplayPacket.Builder<Builder> {
		private Component text;
		private byte alignment;
		private int backgroundColor;

		@Override
		protected Builder self() { return this; }

		public Builder withText(Component text) {
			this.text = text;
			return this;
		}

		public Builder withAlignment(byte alignment) {
			this.alignment = alignment;
			return this;
		}

		public Builder withBackgroundColor(int backgroundColor) {
			this.backgroundColor = backgroundColor;
			return this;
		}

		public TextDisplayPacket build(Version version) {
			this.version = version;
			return new TextDisplayPacket(this);
		}
	}
}
