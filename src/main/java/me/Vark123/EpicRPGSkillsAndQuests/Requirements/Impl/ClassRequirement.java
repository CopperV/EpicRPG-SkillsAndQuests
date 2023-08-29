package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class ClassRequirement implements IRequirement {

	private String _class;
	
	public ClassRequirement(String _class) {
		this._class = ChatColor.translateAlternateColorCodes('&', _class);
	}
	
	@Override
	public boolean checkRequirement(Player p) {
		String pKlasa = PlayerManager.getInstance().getRpgPlayer(p).getInfo().getProffesion();
		return pKlasa.equalsIgnoreCase(_class);
	}

	@Override
	public String getRequirementInfo() {
		return "§cKlasa: §r"+_class+"§r";
	}

}
