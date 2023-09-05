package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.ZlecenieController;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
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
				.filter(pQuest -> pQuest instanceof PlayerZlecenieQuest)
				.findFirst()
				.ifPresent(pQuest -> {
					ZlecenieController.get().addZlecenieCooldown(p, 20*60*15);
				});
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest
						&& ((PlayerDungeonQuest) pQuest).getParty().isEmpty())
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.findFirst()
				.ifPresent(dungeon -> dungeon.removeQuest());
		});
		FileManager.savePlayer(p);
		DatabaseManager.savePlayerActiveQuests(p);
		PlayerManager.get().unregisterPlayer(p);
	}
	
}
