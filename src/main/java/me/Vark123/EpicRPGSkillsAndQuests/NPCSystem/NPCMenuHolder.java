package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NPCMenuHolder implements InventoryHolder {

	private EpicNPC npc;
	
	@Override
	public Inventory getInventory() {
		return null;
	}

}
