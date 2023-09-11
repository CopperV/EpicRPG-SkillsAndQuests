package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class RangaRequirement implements IRequirement {

	private String ranga;
	
	public RangaRequirement(String ranga) {
		this.ranga = ranga;
	}

	@Override
	public boolean checkRequirement(Player p) {
		return p.hasPermission("rpg."+ranga);
	}

	@Override
	public String getRequirementInfo() {
		String ranga = this.ranga;
		switch(ranga.toLowerCase()) {
			case "magnat":
				ranga = "§b§oVIP";
				break;
			}
		return "§cRanga: §r"+ranga+"§r";
	}

}
