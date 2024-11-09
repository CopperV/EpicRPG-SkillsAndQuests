package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class RaidPartyRequirement implements IRequirement {

	@Override
	public boolean checkRequirement(Player p) {
		PartyPlayer pp = PlayerManager.get().getPartyPlayer(p).get();
		if(pp.getParty().isEmpty())
			return true;
		Party party = pp.getParty().get();
		if(party.getLeader().equals(pp))
			return true;
		QuestPlayer qp = me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager
				.get().getQuestPlayer(party.getLeader().getPlayer()).get();
		return qp.getActiveQuests().keySet().stream()
				.filter(quest -> quest instanceof RaidQuest)
				.findAny()
				.isPresent();
	}

	@Override
	public String getRequirementInfo() {
		return "§cPrzewodnik druzyny§r";
	}

}
