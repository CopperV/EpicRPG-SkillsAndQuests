package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPortal;

public class RaidPortalEntryListener implements Listener {
	
	@EventHandler
	public void onEntry(PlayerMoveEvent e) {
		if(e.isCancelled())
			return;
		if(e.getFrom().getBlock().getLocation()
				.equals(e.getTo().getBlock().getLocation()))
			return;
		
		Player p = e.getPlayer();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().keySet().stream()
				.filter(quest -> quest instanceof RaidQuest)
				.map(quest -> (RaidQuest) quest)
				.forEach(raid -> {
					Collection<RaidPortal> raidPortals = raid.getPortals();
					if(raidPortals == null || raidPortals.isEmpty())
						return;
					
					List<String> portals = WorldGuard.getInstance()
							.getPlatform()
							.getRegionContainer().createQuery()
							.getApplicableRegions(BukkitAdapter.adapt(e.getTo()))
							.getRegions().stream()
							.map(region -> region.getId())
							.filter(region -> !region.equalsIgnoreCase("__global__"))
							.collect(Collectors.toList());
					
					raidPortals.stream()
						.filter(raidPortal -> portals.contains(raidPortal.getPortalRegion()))
						.findAny()
						.ifPresent(portal -> {
							Location destination = new Location(p.getWorld(),
									portal.getDestX(),
									portal.getDestY(),
									portal.getDestZ());
							p.sendMessage("§7[§aTELEPORTACJA§7]");
							p.teleport(destination);
						});
				});
		});
	}

}
