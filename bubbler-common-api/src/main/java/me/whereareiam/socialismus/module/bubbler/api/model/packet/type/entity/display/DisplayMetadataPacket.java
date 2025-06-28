package me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.Version;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.Packet;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet used to update display entity metadata, such as scale or translation.
 */
@Getter
@SuperBuilder
public class DisplayMetadataPacket implements Packet {
    private final int entityId;
    private final Vector3f translation;
    private final Vector3f scale;
    private final DisplayType type;

    @Override
    public void send(User user) {
        WrapperPlayServerEntityMetadata packet = createMetadataPacket();
        user.sendPacket(packet);
    }

    private WrapperPlayServerEntityMetadata createMetadataPacket() {
        List<EntityData> metadata = new ArrayList<>();

        if (ProtocolVersion.VERSION.isAtLeast(Version.V_1_21_4)) {
            if (translation != null) {
                metadata.add(new EntityData(11, EntityDataTypes.VECTOR3F, translation));
            }
            if (scale != null) {
                metadata.add(new EntityData(12, EntityDataTypes.VECTOR3F, scale));
            }
            if (type != null) {
                metadata.add(new EntityData(15, EntityDataTypes.BYTE, type.getValue()));
            }
        }

        return new WrapperPlayServerEntityMetadata(entityId, metadata);
    }
}
