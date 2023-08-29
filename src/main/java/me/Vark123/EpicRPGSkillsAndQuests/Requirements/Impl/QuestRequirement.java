package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

//TODO
//Implementacja questow
public class QuestRequirement implements IRequirement {

	private String questId;
	
	public QuestRequirement(String questId) {
		this.questId = questId;
	}
	
	@Override
	public boolean checkRequirement(Player p) {
		return true;
	}

	@Override
	public String getRequirementInfo() {
		String name = "Temp name";
		return "§cZadanie: §r"+name+"§r";
	}

}
