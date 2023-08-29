package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.Vark123.EpicRPG.Utils.Utils;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;

public class ItemPrize implements IPrize {
	
	private ItemStack item;
	
	public ItemPrize(String mmItem, int amount) {
		ItemStack it = MythicBukkit.inst().getItemManager()
				.getItemStack(mmItem, amount);
		this.item = it;
	}

	@Override
	public void givePrize(Player p) {
		p.getInventory().addItem(item).forEach((i, it) -> {
			Utils.dropItemStack(p, it);
		});
	}

	@Override
	public String getPrizeInfo() {
		return item.getItemMeta().getDisplayName()+" §ex"+item.getAmount();
	}

}
