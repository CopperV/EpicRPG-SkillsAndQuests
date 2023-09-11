package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyLeaderChangeEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

public class PartyLeaderChangeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreate(PartyLeaderChangeEvent e) {
		if(e.isCancelled())
			return;
		
		PartyPlayer oldLeader = e.getOldLeader();
		PartyPlayer newLeader = e.getNewLeader();
		
		PlayerManager.get().getQuestPlayer(oldLeader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.findAny()
				.ifPresent(dungeon -> {
					dungeon.setPartyPlayer(newLeader);
					dungeon.setPlayer(newLeader.getPlayer());
					dungeon.getTasks().forEach(pTask -> pTask.setPlayer(newLeader.getPlayer()));
				});
		});
	}
	
}
