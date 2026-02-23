package net.goldtreeservers.worldguardextraflags.packet;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.entity.Player;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;

public class RemoveEffectPacketListener extends PacketListenerAbstract {
	
	public RemoveEffectPacketListener() {
		super(PacketListenerPriority.NORMAL);
	}
	
	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
		if(event.isCancelled()) return;
		if(event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
			Player player = event.getPlayer();
			
			if(!player.isValid()) return;

			try {
				Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(WorldGuardPlugin.inst().wrapPlayer(player));
				GiveEffectsFlagHandler giveEffectsHandler = session.getHandler(GiveEffectsFlagHandler.class);
				
				if (giveEffectsHandler.isSupressRemovePotionPacket()) {
					event.setCancelled(true);
				}
			} catch(IllegalStateException wgBug) {}
		}
	}
}