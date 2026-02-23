package net.goldtreeservers.worldguardextraflags.protocollib;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.WGExtraFlags;

public class ProtocolLibHelper
{
	@Getter private final WGExtraFlags plugin;
	@Getter private final Plugin protocolLibPlugin;
	
	public ProtocolLibHelper(WGExtraFlags plugin, Plugin protocolLibPlugin)
	{
		this.plugin = plugin;
		this.protocolLibPlugin = protocolLibPlugin;
	}

	public void onEnable()
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new RemoveEffectPacketListener());
	}
}
