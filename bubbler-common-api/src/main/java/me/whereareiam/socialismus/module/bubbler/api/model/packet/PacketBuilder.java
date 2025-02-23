package me.whereareiam.socialismus.module.bubbler.api.model.packet;

public interface PacketBuilder<T extends Packet> {
	T build();
}
