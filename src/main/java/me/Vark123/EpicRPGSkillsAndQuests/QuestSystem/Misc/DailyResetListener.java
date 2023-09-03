package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDailyQuest;
import me.nikl.calendarevents.CalendarEvent;

public class DailyResetListener implements Listener {

	@EventHandler
	public void onDate(CalendarEvent e) {
		if(!e.getLabels().contains("reset_daily"))
			return;
		Bukkit.broadcastMessage(Main.getInstance().getPrefix()+" §2§lZadania dzienne zostaly zresetowane");

		Bukkit.getOnlinePlayers().forEach(p -> {
			PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
				qp.getActiveQuests().values().stream()
					.filter(pQuest -> pQuest instanceof PlayerDailyQuest)
					.findAny()
					.ifPresent(pQuest -> pQuest.removeQuest());
			});
		});
		DailyController.get().getDoneDaily().clear();
	}
	
}
