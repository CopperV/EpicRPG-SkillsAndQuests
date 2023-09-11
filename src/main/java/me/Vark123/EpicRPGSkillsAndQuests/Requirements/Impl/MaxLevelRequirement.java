package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class MaxLevelRequirement implements IRequirement {

	private int lvl;
	
	public MaxLevelRequirement(int lvl) {
		this.lvl = lvl;
	}

	@Override
	public boolean checkRequirement(Player p) {
		int pLvl = PlayerManager.getInstance().getRpgPlayer(p).getInfo().getLevel();
		return lvl >= pLvl;
	}

	@Override
	public String getRequirementInfo() {
		return "§cMaksymalny poziom: §7"+lvl+"§r";
	}
	
}
