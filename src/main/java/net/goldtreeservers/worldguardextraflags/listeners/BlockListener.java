package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class BlockListener implements Listener {
	
	private final WorldGuardPlugin plugin;
	private final RegionContainer container;
	private final SessionManager session;

	public BlockListener(WorldGuardPlugin plugin, RegionContainer container, SessionManager session) {
		this.plugin = plugin;
		this.container = container;
		this.session = session;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event) {
		BlockState newState = event.getNewState();
		
		if (newState.getType() == Material.FROSTED_ICE) {
			Location location = BukkitAdapter.adapt(newState.getLocation());

			LocalPlayer localPlayer;
			
			if (event.getEntity() instanceof Player player) {
				localPlayer = plugin.wrapPlayer(player);
				
				if (session.hasBypass(localPlayer, (World) location.getExtent())) {
					return;
				}
			} else {
				localPlayer = null;
			}

			if (container.createQuery().queryValue(location, localPlayer, Flags.FROSTWALKER) == State.DENY) {
				event.setCancelled(true);
			}
		}
	}
}