package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		QuestPlayer qp = PlayerManager.get().loadQuestPlayer(p);
		PlayerManager.get().registerPlayer(qp);
		qp.getActiveQuests().forEach((quest, pQuest) -> {
			TaskGroup group = quest.getTaskGroups().get(pQuest.getStage());
			group.getEventsByType(EventCall.JOIN).ifPresent(event -> event.executeEvent(pQuest));
		});
	}
	
}
