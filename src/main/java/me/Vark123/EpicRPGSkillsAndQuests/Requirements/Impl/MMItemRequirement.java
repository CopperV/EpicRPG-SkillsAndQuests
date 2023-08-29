package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.tr7zw.nbtapi.NBTItem;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class MMItemRequirement implements IRequirement {

	private String mmItem;
	private int amount;

	public MMItemRequirement(String mmItem, int amount) {
		this.mmItem = mmItem;
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
				NBTItem nbt = new NBTItem(it);
				if(!nbt.hasTag("MYTHIC_TYPE"))
					return false;
				return nbt.getString("MYTHIC_TYPE").equals(mmItem);
			}).map(it -> it.getAmount())
			.forEach(amount::subtract);
		return amount.getValue() <= 0;
	}

	@Override
	public String getRequirementInfo() {
		return "§cPosiadaj §7"+amount+"§r "+mmItem+"§r";
	}

}
