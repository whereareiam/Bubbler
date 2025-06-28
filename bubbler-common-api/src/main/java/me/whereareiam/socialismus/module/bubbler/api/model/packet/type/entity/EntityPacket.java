package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;

import java.util.List;

@Getter
@SuperBuilder
public abstract class EntityPacket implements Packet {
	protected final int entityId = (int) (Math.random() * Integer.MAX_VALUE);
	protected final Vector3d position;
	protected final boolean noGravity;

	protected void addCommonMetadata(List<EntityData> metadata) {
		if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_19_4)) {
			metadata.add(new EntityData(5, EntityDataTypes.BOOLEAN, noGravity));
		} else {
			metadata.add(new EntityData(5, EntityDataTypes.BYTE, (byte) (noGravity ? 0x02 : 0)));
		}
	}
}
