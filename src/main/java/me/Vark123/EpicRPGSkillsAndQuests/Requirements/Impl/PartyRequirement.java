package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class PartyRequirement implements IRequirement {

	@Override
	public boolean checkRequirement(Player p) {
		PartyPlayer pp = PlayerManager.get().getPartyPlayer(p).get();
		if(pp.getParty().isEmpty())
			return true;
		return pp.getParty().get().getLeader().equals(pp);
	}

	@Override
	public String getRequirementInfo() {
		return "§cPrzewodnik druzyny§r";
	}

}
