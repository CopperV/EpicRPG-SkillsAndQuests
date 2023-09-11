package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyRemoveEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

public class PartyRemoveListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRemove(PartyRemoveEvent e) {
		if(e.isCancelled())
			return;
		
		Party party = e.getParty();
		PartyPlayer leader = party.getLeader();
		
		PlayerManager.get().getQuestPlayer(leader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.findAny()
				.ifPresent(dungeon -> {
					dungeon.setParty(null);
					PartyPlayer newOnwer = dungeon.getPartyPlayer();
					party.getMembers().stream()
						.filter(member -> !member.equals(newOnwer))
						.map(member -> PlayerManager.get().getQuestPlayer(member.getPlayer()))
						.filter(qp2 -> qp2.isPresent())
						.map(qp2 -> qp2.get())
						.forEach(qp2 -> {
							qp2.getActiveQuests().values().stream()
								.filter(quest -> quest.equals(dungeon))
								.findAny()
								.ifPresent(quest -> qp2.getActiveQuests().remove(dungeon.getQuest()));
						});
				});
		});
	}

}
