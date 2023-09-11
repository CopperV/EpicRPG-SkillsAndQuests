package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicNPC.Events.EpicNpcInteractEvent;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPCManager;

public class EpicNPCClickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(EpicNpcInteractEvent e) {
		if(e.isCancelled())
			return;
		Player p = e.getPlayer();
		String name = e.getNpc().getName();
		EpicNPCManager.get().getNPCByName(name)
			.ifPresent(npc -> npc.openMenu(p));
	}
	
}
