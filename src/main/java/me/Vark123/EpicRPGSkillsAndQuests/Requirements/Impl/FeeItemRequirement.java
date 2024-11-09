package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tr7zw.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

@AllArgsConstructor
@Getter
public class FeeItemRequirement implements IRequirement {

	private String questId;
	private String mmItem;
	private int amount;
	
	@Override
	public boolean checkRequirement(Player p) {
		boolean searchResult = RaidManager.get()
				.getRaidPlayer(p)
				.getRaidInfo()
				.stream()
				.filter(info -> info.getRaidId().equals(questId))
				.findAny()
				.isPresent();
		if(searchResult)
			return true;
		
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
		String name = MythicBukkit.inst().getItemManager().getItemStack(mmItem).getItemMeta().getDisplayName();
		return "§cOddaj §7"+amount+"§r "+name+"§r";
	}
	
	public void takeItems(Player p) {
		if(RaidManager.get()
				.getRaidPlayer(p)
				.getRaidInfo()
				.stream()
				.filter(info -> info.getRaidId().equals(questId))
				.findAny()
				.isPresent())
			return;
		
		PlayerInventory inv = p.getInventory();
		int toRemove = amount;
		for(int i = 0; i < 36 && toRemove > 0; ++i) {
			ItemStack it = inv.getItem(i);
			if(it == null || it.getType().equals(Material.AIR))
				continue;
			NBTItem nbt = new NBTItem(it);
			if(!nbt.hasTag("MYTHIC_TYPE"))
				continue;
			if(!nbt.getString("MYTHIC_TYPE").equals(mmItem))
				continue;
			if(it.getAmount() > toRemove) {
				it.setAmount(it.getAmount() - toRemove);
				toRemove = 0;
			} else {
				toRemove -= it.getAmount();
				inv.setItem(i, null);
			}
		}
		p.updateInventory();
	}

}
