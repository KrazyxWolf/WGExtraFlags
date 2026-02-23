package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class PlayerSkillListener implements Listener {

    private final RegionContainer container;

    public PlayerSkillListener(RegionContainer container) {
        this.container = container;
    }

    @EventHandler
    public void onSkillCast(PlayerCastSkillEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();

        Skill skill = e.getCast();

        if(skill == null) return;

        SkillHandler<?> handler = skill.getHandler();

        ApplicableRegionSet regions = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        Set<String> state = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.BLOCKED_SKILLS);

        if(state != null && state.contains(handler.getId())) {
            e.setCancelled(true);
        }
    }
}