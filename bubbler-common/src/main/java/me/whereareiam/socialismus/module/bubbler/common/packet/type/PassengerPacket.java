package me.whereareiam.socialismus.module.bubbler.common.packet.type;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import lombok.AllArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.common.packet.Packet;

@AllArgsConstructor
public class PassengerPacket implements Packet {
	private final int vehicleId;
	private final int[] passengerIds;

	@Override
	public void send(User user) {
		WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicleId, passengerIds);
		user.sendPacket(packet);
	}
}
