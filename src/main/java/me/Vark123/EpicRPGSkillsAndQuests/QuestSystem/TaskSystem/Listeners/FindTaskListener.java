package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FindTask;

public class FindTaskListener implements Listener {
	
	@EventHandler
	public void onEntry(PlayerMoveEvent e) {
		if(e.isCancelled())
			return;
		if(e.getFrom().getBlock().getLocation()
				.equals(e.getTo().getBlock().getLocation()))
			return;
		
		Player p = e.getPlayer();
		Set<ProtectedRegion> regions = WorldGuard.getInstance().getPlatform()
				.getRegionContainer().createQuery()
				.getApplicableRegions(BukkitAdapter.adapt(e.getTo()))
				.getRegions();
		if(regions == null 
				|| regions.isEmpty()) 
			return;
		if(regions.size()==1 
				&& ((ProtectedRegion)regions.toArray()[0]).getId().equalsIgnoreCase("__global__"))
			return;
		
		List<String> strRegions = regions.stream()
				.map(region -> region.getId())
				.collect(Collectors.toList());
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof FindTask
							&& !pTask.isCompleted()
							&& strRegions.contains(pTask.getTask().getTarget()))
					.forEach(pTask -> {
						pTask.complete();
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
						//TODO
						//Dodanie aktualizacji zadania
					});
			});
		});

	}

}
