package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.EntityPacket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DisplayPacket extends EntityPacket {
	protected final Vector3f scale;

	protected DisplayPacket(Builder<?> builder) {
		super(builder);
		this.scale = builder.scale;
	}

	protected void addDisplayMetadata(List<EntityData> metadata) {
		super.addCommonMetadata(metadata);

		if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_21_4)) {
			metadata.add(new EntityData(12, EntityDataTypes.VECTOR3F, scale));
		} else {
			// TODO
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

	public abstract static class Builder<T extends Builder<T>> extends EntityPacket.Builder<T> {
		protected Vector3f scale = new Vector3f(1F, 1F, 1F);

		public T withScale(Vector3f scale) {
			this.scale = scale;
			return self();
		}
	}
}
