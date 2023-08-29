package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;

public class ClassPrize implements IPrize {

	private String _class;
	
	public ClassPrize(String _class) {
		this._class = ChatColor.translateAlternateColorCodes('&', _class);
	}
	
	//TODO
	//Implementacja po dodaniu systemu graczy
	@Override
	public void givePrize(Player p) {
		
	}

	@Override
	public String getPrizeInfo() {
		return "§eKlasa: §r"+_class;
	}

	
	
}
