package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;

public class EntityPotionEffectEventListener implements Listener {
	
	private final WorldGuardPlugin plugin;
	private final SessionManager session;

	public EntityPotionEffectEventListener(WorldGuardPlugin plugin, SessionManager session) {
		this.plugin = plugin;
		this.session = session;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityPotionEffectEvent(EntityPotionEffectEvent event) {
		if (event.getAction() != EntityPotionEffectEvent.Action.REMOVED || event.getCause() != EntityPotionEffectEvent.Cause.PLUGIN) {
			return;
		}
		if (!(event.getEntity() instanceof Player player) || !player.isValid()) {
			return;
		}

		try {
			Session wGSession = session.get(plugin.wrapPlayer(player));
			GiveEffectsFlagHandler giveEffectsHandler = wGSession.getHandler(GiveEffectsFlagHandler.class);
			
			if (giveEffectsHandler.isSupressRemovePotionPacket()) {
				event.setCancelled(true);
			}
		} catch(IllegalStateException wgBug) {}
	}
}
