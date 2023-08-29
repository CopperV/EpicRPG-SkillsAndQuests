package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.Components.RpgReputation;
import me.Vark123.EpicRPG.Reputation.ReputationContainer;
import me.Vark123.EpicRPG.Reputation.ReputationLevels;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class ReputationRequirement implements IRequirement {

	private String fraction;
	private int status;

	public ReputationRequirement(String fraction, int status) {
		this.fraction = fraction;
		this.status = status;
	}

	@Override
	public boolean checkRequirement(Player p) {
		RpgReputation rep = PlayerManager.getInstance().getRpgPlayer(p).getReputation();
		if(!rep.getReputacja().containsKey(fraction.toLowerCase()))
			return false;
		ReputationLevels level = rep.getReputacja().get(fraction.toLowerCase()).getReputationLevel();
		return level.getId() >= status;
	}

	@Override
	public String getRequirementInfo() {
		String display = ReputationContainer.getInstance().getContainer()
				.get(fraction.toLowerCase()).getDisplay();
		String displayStatus = ReputationLevels.getReputationLevelById(status).getName();
		return "§cStosunek z §r"+display+"§r: §e"+displayStatus+" §7["+status+"§7]§r";
	}

}
