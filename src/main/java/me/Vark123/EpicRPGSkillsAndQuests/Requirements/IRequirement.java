package me.Vark123.EpicRPGSkillsAndQuests.Requirements;

import org.bukkit.entity.Player;

public interface IRequirement {

	public boolean checkRequirement(Player p);
	public String getRequirementInfo();
	
}
