package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyJoinEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer.RaidPlayerInfo;

public class RaidPartyJoinListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PartyJoinEvent e) {
		if(e.isCancelled())
			return;
		
		Party party = e.getParty();
		PartyPlayer leader = party.getLeader();
		PartyPlayer member = e.getNewMember();

		PlayerManager.get().getQuestPlayer(member.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.filter(pQuest -> pQuest.getParty().isEmpty())
				.findAny()
				.ifPresent(raid -> {
					raid.removeQuest();
				});
		});
		
		PlayerManager.get().getQuestPlayer(leader.getPlayer()).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.findAny()
				.ifPresent(raid -> {
					PlayerManager.get().getQuestPlayer(member.getPlayer())
						.ifPresent(qp2 -> {
							qp2.getActiveQuests().put(raid.getQuest(), raid);
							
							RaidPlayer rp = RaidManager.get().getRaidPlayer(member.getPlayer());
							rp.getRaidInfo().stream()
								.filter(raidInfo -> raidInfo.getRaidId().equals(raid.getQuest().getId()))
								.findAny()
								.ifPresentOrElse(raidInfo -> raidInfo.update(raid), 
										() -> rp.getRaidInfo().add(new RaidPlayerInfo((RaidQuest) raid.getQuest())));
						});
				});
		});
	}

}
