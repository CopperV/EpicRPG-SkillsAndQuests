package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class BroadcastPrize implements IPrize {

	private String message;
	
	public BroadcastPrize(String message) {
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}

	@Override
	public void givePrize(Player p) {
		String finalbc = message.replace("%player%", p.getName());
		finalbc = PlaceholderAPI.setPlaceholders(p, finalbc);
		Bukkit.broadcastMessage(finalbc);
	}

	@Override
	public String getPrizeInfo() {
		return "§eBroadcast: §r"+message;
	}

}
