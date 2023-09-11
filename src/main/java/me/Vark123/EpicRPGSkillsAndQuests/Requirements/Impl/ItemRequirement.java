package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class ItemRequirement implements IRequirement {

	private String item;
	private int amount;
	
	public ItemRequirement(String item, int amount) {
		this.item = ChatColor.translateAlternateColorCodes('&', item);
		this.amount = amount;
	}

	@Override
	public boolean checkRequirement(Player p) {
		MutableInt amount = new MutableInt(this.amount);
		Arrays.asList(p.getInventory().getStorageContents())
			.stream()
			.filter(it -> {
				if(it == null || it.getType().equals(Material.AIR))
					return false;
				if(!it.hasItemMeta() || !it.getItemMeta().hasDisplayName())
					return false;
				if(!it.getItemMeta().getDisplayName().equalsIgnoreCase(item))
					return false;
				return true;
			}).map(it -> it.getAmount())
			.forEach(amount::subtract);
		return amount.getValue() <= 0;
	}

	@Override
	public String getRequirementInfo() {
		return "§cPosiadaj §7"+amount+"§r "+item+"§r";
	}

}
