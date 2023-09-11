package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

public class PlayerWorldChangeListener implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().forEach((quest, pQuest) -> {
				TaskGroup group = quest.getTaskGroups().get(pQuest.getStage());
				group.getEventsByType(EventCall.WORLD_CHANGE).ifPresent(event -> event.executeEvent(pQuest));
			});
		});
	}
	
}
