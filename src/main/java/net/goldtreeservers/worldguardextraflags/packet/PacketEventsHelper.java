package net.goldtreeservers.worldguardextraflags.packet;

import com.github.retrooper.packetevents.PacketEvents;

public class PacketEventsHelper {

	public void onEnable() {
		PacketEvents.getAPI().getEventManager().registerListener(new RemoveEffectPacketListener());
	}
}