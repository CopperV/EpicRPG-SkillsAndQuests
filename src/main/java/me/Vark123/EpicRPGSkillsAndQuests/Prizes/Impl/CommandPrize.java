package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;
import me.clip.placeholderapi.PlaceholderAPI;

public class CommandPrize implements IPrize {

	private String cmd;
	
	public CommandPrize(String cmd) {
		this.cmd = cmd;
	}

	@Override
	public void givePrize(Player p) {
		String finalCmd = cmd.replaceAll("%player%", p.getName());
		finalCmd = PlaceholderAPI.setPlaceholders(p, finalCmd);
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalCmd);
	}

	@Override
	public String getPrizeInfo() {
		return "§eKomenda: §r"+cmd;
	}
	
}
