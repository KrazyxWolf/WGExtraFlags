package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import net.goldtreeservers.worldguardextraflags.we.handlers.WorldEditFlagHandler;
import org.bukkit.Bukkit;

public class WorldEditListener {
	
	private final WorldGuardPlugin plugin;
	private final RegionContainer container;
	private final SessionManager session;

	public WorldEditListener(WorldGuardPlugin plugin, RegionContainer container, SessionManager session) {
		this.plugin = plugin;
		this.container = container;
		this.session = session;
	}

	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event) {
		World world = event.getWorld();

		RegionManager regionManager = container.get(world);
		
		if (regionManager == null) {
			return;
		}

		if (event.getActor() instanceof Player player) {
			LocalPlayer localPlayer = plugin.wrapPlayer(Bukkit.getPlayer(player.getUniqueId()));
			
			if (session.hasBypass(localPlayer, world)) {
				return;
			}

			event.setExtent(new WorldEditFlagHandler(world, event.getExtent(), localPlayer, regionManager));
		}
	}
}