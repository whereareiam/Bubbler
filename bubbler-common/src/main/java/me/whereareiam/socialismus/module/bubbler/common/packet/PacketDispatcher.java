package me.whereareiam.socialismus.module.bubbler.common.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import jakarta.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;

@Singleton
public class PacketDispatcher {
  public void sendPacket(DummyPlayer dummyPlayer, PacketWrapper<?> packet) {
    User user = PacketEvents.getAPI().getPlayerManager().getUser(dummyPlayer.getAudience());

    user.sendPacket(packet);
  }
}
