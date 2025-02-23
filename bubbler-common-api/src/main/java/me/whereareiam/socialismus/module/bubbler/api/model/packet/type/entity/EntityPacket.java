package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EntityPacket implements Packet {
	protected final int entityId;
	protected final Vector3d position;
	protected final Version version;

	protected boolean noGravity;

	protected EntityPacket(Builder<?> builder) {
		this.entityId = builder.entityId;
		this.position = builder.position;
		this.version = builder.version;
		this.noGravity = builder.noGravity;
	}

	protected void addCommonMetadata(List<EntityData> metadata) {
		if (version.isAtLeast(Version.V_1_19_4)) {
			metadata.add(new EntityData(5, EntityDataTypes.BOOLEAN, noGravity));
		} else {
			metadata.add(new EntityData(5, EntityDataTypes.BYTE, (byte) (noGravity ? 0x02 : 0)));
		}
	}

	public abstract static class Builder<T extends Builder<T>> {
		protected int entityId = ThreadLocalRandom.current().nextInt();
		protected Vector3d position;
		protected Version version;
		protected boolean noGravity;

		protected abstract T self();

		public T withEntityId(int entityId) {
			this.entityId = entityId;
			return self();
		}

		public T withPosition(Vector3d position) {
			this.position = position;
			return self();
		}

		public T withNoGravity(boolean noGravity) {
			this.noGravity = noGravity;
			return self();
		}
	}
}
