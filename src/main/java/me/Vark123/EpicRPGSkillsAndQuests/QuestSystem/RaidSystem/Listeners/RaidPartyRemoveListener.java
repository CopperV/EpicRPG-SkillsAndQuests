package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyRemoveEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class RaidPartyRemoveListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRemove(PartyRemoveEvent e) {
		if(e.isCancelled())
			return;
		
		Party party = e.getParty();
		PartyPlayer leader = party.getLeader();
				
		PlayerManager.get().getQuestPlayer(leader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.findAny()
				.ifPresent(raid -> {
					raid.setParty(null);
					PartyPlayer newOwner = raid.getPartyPlayer();
					party.getMembers().stream()
						.filter(member -> !member.equals(newOwner))
						.map(member -> PlayerManager.get().getQuestPlayer(
								member.getPlayer()))
						.filter(_qp -> _qp.isPresent())
						.map(_qp -> _qp.get())
						.forEach(_qp -> {
							_qp.getActiveQuests().values().stream()
								.filter(quest -> quest.equals(raid))
								.findAny()
								.ifPresent(quest -> _qp.getActiveQuests().remove(raid.getQuest()));
						});
				});
		});
	}
	
}
