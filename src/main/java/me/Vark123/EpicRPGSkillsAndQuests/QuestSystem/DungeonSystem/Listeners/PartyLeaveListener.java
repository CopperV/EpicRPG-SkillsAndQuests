package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyLeaveEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

public class PartyLeaveListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PartyLeaveEvent e) {
		if(e.isCancelled())
			return;
		
		Party party = e.getParty();
		PartyPlayer leader = party.getLeader();
		PartyPlayer member = e.getOldMember();
		
		PlayerManager.get().getQuestPlayer(member.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.findAny()
				.ifPresent(dungeon -> {
					qp.getActiveQuests().remove(dungeon.getQuest());
					if(party.getMembers().size() > 2)
						return;
					if(!leader.equals(member))
						return;
					
					PartyPlayer newMember = party.getMembers().get(0).equals(leader) ? party.getMembers().get(1) : party.getMembers().get(0);
					dungeon.setPartyPlayer(newMember);
					dungeon.setPlayer(newMember.getPlayer());
					dungeon.getTasks().forEach(pTask -> pTask.setPlayer(newMember.getPlayer()));
					dungeon.setParty(null);
				});
		});
	}

}
