package net.goldtreeservers.worldguardextraflags;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import net.goldtreeservers.worldguardextraflags.listeners.*;
import net.goldtreeservers.worldguardextraflags.packet.PacketEventsHelper;
import net.goldtreeservers.worldguardextraflags.wg.handlers.*;
import org.bukkit.World;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

import org.bukkit.plugin.java.JavaPlugin;

public class WGExtraFlags extends JavaPlugin {
	
	private WorldEditPlugin worldEditPlugin;

	private WorldGuardPlugin worldGuardPlugin;
	private WorldGuard worldGuard;

	private RegionContainer regionContainer;
	private SessionManager sessionManager;

	private PacketEventsHelper packetEventsHelper;
	
	@Override
	public void onLoad() {
		this.worldEditPlugin = JavaPlugin.getPlugin(WorldEditPlugin.class);
		this.worldGuardPlugin = JavaPlugin.getPlugin(WorldGuardPlugin.class);

		this.worldGuard = WorldGuard.getInstance();

		try {
			FlagRegistry flagRegistry = this.worldGuard.getFlagRegistry();
			flagRegistry.register(Flags.TELEPORT_ON_ENTRY);
			flagRegistry.register(Flags.TELEPORT_ON_EXIT);
			flagRegistry.register(Flags.COMMAND_ON_ENTRY);
			flagRegistry.register(Flags.COMMAND_ON_EXIT);
			flagRegistry.register(Flags.CONSOLE_COMMAND_ON_ENTRY);
			flagRegistry.register(Flags.CONSOLE_COMMAND_ON_EXIT);
			flagRegistry.register(Flags.WALK_SPEED);
			flagRegistry.register(Flags.KEEP_INVENTORY);
			flagRegistry.register(Flags.KEEP_EXP);
			flagRegistry.register(Flags.CHAT_PREFIX);
			flagRegistry.register(Flags.CHAT_SUFFIX);
			flagRegistry.register(Flags.BLOCKED_EFFECTS);
			flagRegistry.register(Flags.GODMODE);
			flagRegistry.register(Flags.RESPAWN_LOCATION);
			flagRegistry.register(Flags.WORLDEDIT);
			flagRegistry.register(Flags.GIVE_EFFECTS);
			flagRegistry.register(Flags.FLY);
			flagRegistry.register(Flags.FLY_SPEED);
			flagRegistry.register(Flags.PLAY_SOUNDS);
			flagRegistry.register(Flags.FROSTWALKER);
			flagRegistry.register(Flags.NETHER_PORTALS);
			flagRegistry.register(Flags.GLIDE);
			flagRegistry.register(Flags.CHUNK_UNLOAD);
			flagRegistry.register(Flags.ITEM_DURABILITY);
			flagRegistry.register(Flags.JOIN_LOCATION);
		} catch (Exception e) {
			this.getServer().getPluginManager().disablePlugin(this);

			throw new RuntimeException(e instanceof IllegalStateException ?
					"WorldGuard prevented flag registration. Did you reload the plugin? This is not supported!" :
					"Flag registration failed!", e);
		}
		
		try {
			if (getServer().getPluginManager().getPlugin("packetevents") != null) {
				this.packetEventsHelper = new PacketEventsHelper();
			}
		} catch(Exception ignore) {}
	}
	
	@Override
	public void onEnable() {
		this.regionContainer = this.worldGuard.getPlatform().getRegionContainer();
		this.sessionManager = this.worldGuard.getPlatform().getSessionManager();

		this.sessionManager.registerHandler(TeleportOnEntryFlagHandler.FACTORY(this), null);
		this.sessionManager.registerHandler(TeleportOnExitFlagHandler.FACTORY(this), null);

		this.sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(FlySpeedFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(FlyFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(GlideFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(GodmodeFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(PlaySoundsFlagHandler.FACTORY(this), null);
		this.sessionManager.registerHandler(BlockedEffectsFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(GiveEffectsFlagHandler.FACTORY(), null);

		this.sessionManager.registerHandler(CommandOnEntryFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(CommandOnExitFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY(), null);

		getServer().getPluginManager().registerEvents(new PlayerListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);
		getServer().getPluginManager().registerEvents(new WorldListener(this, this.regionContainer), this);
		getServer().getPluginManager().registerEvents(new EntityListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);

		this.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager));
		
		if (this.packetEventsHelper != null) {
			try {
				this.packetEventsHelper.onEnable();
			} catch (Exception ignore) {
				getServer().getPluginManager().registerEvents(new EntityPotionEffectEventListener(this.worldGuardPlugin, this.sessionManager), this);
			}
		} else {
			getServer().getPluginManager().registerEvents(new EntityPotionEffectEventListener(this.worldGuardPlugin, this.sessionManager), this);
		}
		
		for(World world : this.getServer().getWorlds()) {
			this.doUnloadChunkFlagCheck(world);
		}
	}

	public void doUnloadChunkFlagCheck(org.bukkit.World world) {
		RegionManager regionManager = this.regionContainer.get(BukkitAdapter.adapt(world));
		
		if (regionManager == null) {
			return;
		}

		for (ProtectedRegion region : regionManager.getRegions().values()) {
			if (region.getFlag(Flags.CHUNK_UNLOAD) == StateFlag.State.DENY) {
				this.getLogger().info("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");

				BlockVector3 min = region.getMinimumPoint();
				BlockVector3 max = region.getMaximumPoint();

				for(int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; x++) {
					for(int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++) {
						world.getChunkAt(x, z).addPluginChunkTicket(this);
					}
				}
			}
		}
	}

	public static WGExtraFlags getPlugin() {
		return JavaPlugin.getPlugin(WGExtraFlags.class);
	}
}