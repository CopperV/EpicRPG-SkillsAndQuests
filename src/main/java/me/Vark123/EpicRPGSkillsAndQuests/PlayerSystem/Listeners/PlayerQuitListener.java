package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.ZleceniePlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.ZlecenieController;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		save(p);
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		save(p);
	}
	
	private void save(Player p) {
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().forEach(pQuest -> {
				TaskGroup taskGroup = pQuest.getPresentTaskGroup();
				taskGroup.getEventsByType(EventCall.DISCONNECT).ifPresent(event -> event.executeEvent(pQuest));
			});
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof ZleceniePlayerQuest)
				.findFirst()
				.ifPresent(pQuest -> {
					ZlecenieController.get().addZlecenieCooldown(p, 20*60*15);
				});
		});
		FileManager.savePlayer(p);
		DatabaseManager.savePlayerActiveQuests(p);
		PlayerManager.get().unregisterPlayer(p);
	}
	
}
