package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Optional;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

@Getter
@AllArgsConstructor
public class RaidPartyRequirement implements IRequirement {

	private String raidId;
	
	@Override
	public boolean checkRequirement(Player p) {
		PartyPlayer pp = PlayerManager.get().getPartyPlayer(p).get();
		if(pp.getParty().isEmpty())
			return true;
		
		Player leader = pp.getParty().get().getLeader().getPlayer();
		if(p.equals(leader))
			return true;
		
		Optional<QuestPlayer> oQp = me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager.get().getQuestPlayer(leader);
		if(oQp.isEmpty())
			return false;
		
		QuestPlayer qp = oQp.get();
		return qp.getActiveQuests().keySet().stream()
				.map(quest -> quest.getId())
				.filter(raidId::equals)
				.findAny()
				.isPresent();
	}

	@Override
	public String getRequirementInfo() {
		return "§cPrzewodnik druzyny§r";
	}

}
