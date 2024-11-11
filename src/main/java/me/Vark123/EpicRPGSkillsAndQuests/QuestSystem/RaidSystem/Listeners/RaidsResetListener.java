package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.nikl.calendarevents.CalendarEvent;

public class RaidsResetListener implements Listener {

	@EventHandler
	public void onDate(CalendarEvent e) {
		if(!e.getLabels().contains("reset_raids"))
			return;
		Bukkit.broadcastMessage(RaidManager.get().getRaidPrefix()+" §e§lRajdy zostaly zresetowane");
	
		Collection<PlayerRaidQuest> activeRaids = new LinkedList<>();
		Bukkit.getOnlinePlayers()
			.stream()
			.map(PlayerManager.get()::getQuestPlayer)
			.filter(qp -> qp.isPresent())
			.map(qp -> qp.get())
			.flatMap(qp -> qp.getActiveQuests().values().stream())
			.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
			.map(pQuest -> (PlayerRaidQuest) pQuest)
			.forEach(pQuest -> {
				if(activeRaids.contains(pQuest))
					return;
				activeRaids.add(pQuest);
			});
		activeRaids.forEach(raid -> raid.removeQuest());
		
		Bukkit.getOnlinePlayers()
			.stream()
			.map(RaidManager.get()::loadPlayer)
			.forEach(rp -> rp.getRaidInfo().clear());
		
		FileManager.clearPlayerRaidsFile();
	}
	
}
