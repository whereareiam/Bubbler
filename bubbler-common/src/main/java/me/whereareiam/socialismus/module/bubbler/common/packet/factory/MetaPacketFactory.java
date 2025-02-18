package me.whereareiam.socialismus.module.bubbler.common.packet.factory;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import jakarta.inject.Singleton;
import java.util.List;
import net.kyori.adventure.text.Component;

@Singleton
public class MetaPacketFactory {
  public static WrapperPlayServerEntityMetadata createTextDisplayMeta(int id, Component message) {
    return new WrapperPlayServerEntityMetadata(id, List.of(
            new EntityData(9, EntityDataTypes.INT, 0),
            new EntityData(10, EntityDataTypes.INT, 0),
            new EntityData(12, EntityDataTypes.VECTOR3F, new Vector3f(1F, 1F, 1F)),
            new EntityData(15, EntityDataTypes.BYTE, (byte) 1),
            new EntityData(23, EntityDataTypes.ADV_COMPONENT, message)
    ));
  }
}
