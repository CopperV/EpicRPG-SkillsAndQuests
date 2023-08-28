package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class EpicNPC {

	private String name;
	private String title;
	private int size;
	private Map<Integer, AEpicItem> itemSlots;
	
	public void openMenu(Player p) {
		Inventory inv = Bukkit.createInventory(new NPCMenuHolder(this), size, title);
		itemSlots.forEach((slot, item) -> inv.setItem(slot, item.getItem(p)));
		p.openInventory(inv);
	}
	
}
