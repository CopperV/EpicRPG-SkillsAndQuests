package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyKickEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

public class PartyKickListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PartyKickEvent e) {
		if(e.isCancelled())
			return;
		
		PartyPlayer member = e.getOldMember();
		
		PlayerManager.get().getQuestPlayer(member.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.findAny()
				.ifPresent(dungeon -> {
					dungeon.removeQuest();
				});
		});
	}

}
