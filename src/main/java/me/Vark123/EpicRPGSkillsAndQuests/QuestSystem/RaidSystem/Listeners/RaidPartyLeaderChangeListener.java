package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyLeaderChangeEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class RaidPartyLeaderChangeListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreate(PartyLeaderChangeEvent e) {
		if(e.isCancelled())
			return;
		
		PartyPlayer oldLeader = e.getOldLeader();
		PartyPlayer newLeader = e.getNewLeader();
		
		PlayerManager.get().getQuestPlayer(oldLeader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.findAny()
				.ifPresent(raid -> {
					raid.setPartyPlayer(newLeader);
					raid.setPlayer(newLeader.getPlayer());
					raid.getTasks().forEach(pTask -> pTask.setPlayer(newLeader.getPlayer()));
				});
		});
	}
	
}
