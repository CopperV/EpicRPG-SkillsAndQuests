package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyJoinEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

public class PartyJoinListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PartyJoinEvent e) {
		if(e.isCancelled())
			return;
		
		Party party = e.getParty();
		PartyPlayer leader = party.getLeader();
		PartyPlayer member = e.getNewMember();


		//Probably useless code
		//At this moment players should have no party
//		PlayerManager.get().getQuestPlayer(member.getPlayer()).ifPresent(qp -> {
//			qp.getActiveQuests().values().stream()
//				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
//				.findAny()
//				.ifPresent(dungeon -> {
//					dungeon.removeQuest();
//				});
//		});
		
		PlayerManager.get().getQuestPlayer(leader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.findAny()
				.ifPresent(dungeon -> {
					PlayerManager.get().getQuestPlayer(member.getPlayer())
						.ifPresent(qp2 -> qp2.getActiveQuests().put(dungeon.getQuest(), dungeon));
				});
		});
	}

}
