package me.whereareiam.socialismus.module.bubbler.api.model.packet;

import me.whereareiam.socialismus.api.type.Version;

public interface PacketBuilder<T extends Packet> {
	T build(Version version);
}
