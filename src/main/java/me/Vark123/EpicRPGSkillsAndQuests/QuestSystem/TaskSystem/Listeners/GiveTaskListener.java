package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events.GiveTaskEvent;

public class GiveTaskListener implements Listener {

	@EventHandler
	public void onGive(GiveTaskEvent e) {
		Player p = e.getPlayer();
		PlayerTask pTask = e.getTask();
		if(!(pTask.getTask() instanceof GiveTask))
			return;
		
		int presentAmount = pTask.getIntProgress();
		int goalAmount = ((GiveTask)pTask.getTask()).getAmount();
		int giveAmount = 0;
		String target = pTask.getTask().getTarget();
		
		ItemStack[] storage = p.getInventory().getStorageContents();
		for(int i = 0; i < storage.length; ++i) {
			ItemStack it = storage[i];
			if(it == null || it.getType().equals(Material.AIR))
				continue;
			if(!it.hasItemMeta() || !it.getItemMeta().hasDisplayName())
				continue;
			if(!it.getItemMeta().getDisplayName().equals(target))
				continue;
			if(goalAmount > presentAmount + giveAmount + it.getAmount()) {
				giveAmount += it.getAmount();
				p.getInventory().setItem(i, null);
			} else {
				int toRemove = goalAmount - (presentAmount + giveAmount);
				if(it.getAmount() == toRemove)
					p.getInventory().setItem(i, null);
				else
					it.setAmount(it.getAmount() - toRemove);
				giveAmount += toRemove;
				break;
			}
		}
		
		p.updateInventory();
		
		pTask.addProgress(giveAmount);
		if(pTask.getIntProgress() >= ((GiveTask)pTask.getTask()).getAmount())
			pTask.complete();
		p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		//TODO
		//Dodanie aktualizacji zadania
	}
	
}
