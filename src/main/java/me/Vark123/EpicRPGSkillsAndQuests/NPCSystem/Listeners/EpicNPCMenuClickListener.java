package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.NPCMenuHolder;

public class EpicNPCMenuClickListener implements Listener {
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = (Player) e.getWhoClicked();
		
		Inventory inv = e.getView().getTopInventory();
		if(!(inv.getHolder() instanceof NPCMenuHolder))
			return;
		
		e.setCancelled(true);
		if(e.getClickedInventory() == null || !e.getClickedInventory().equals(inv))
			return;
		
		EpicNPC npc = ((NPCMenuHolder) inv.getHolder()).getNpc();
		int slot = e.getSlot();
		if(!npc.getItemSlots().containsKey(slot))
			return;
		
		npc.getItemSlots().get(slot).clickAction(p, inv.getItem(slot), npc);
	}

}
