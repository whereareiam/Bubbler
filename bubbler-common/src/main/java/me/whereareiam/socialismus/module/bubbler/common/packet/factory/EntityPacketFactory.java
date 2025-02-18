package me.whereareiam.socialismus.module.bubbler.common.packet.factory;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class EntityPacketFactory {
  public static WrapperPlayServerSpawnEntity createTextDisplay() {
    int entityId = ThreadLocalRandom.current().nextInt();

    return new WrapperPlayServerSpawnEntity(
        entityId,
        Optional.empty(),
        EntityTypes.TEXT_DISPLAY,
        new Vector3d(0, 0, 0),
        0.0F,
        0.0F,
        0.0F,
        0,
        Optional.empty());
  }
}
