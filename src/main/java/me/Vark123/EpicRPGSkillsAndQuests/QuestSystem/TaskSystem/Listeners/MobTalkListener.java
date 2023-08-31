package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.MobTalkTask;
import me.clip.placeholderapi.PlaceholderAPI;

public class MobTalkListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		if(entity instanceof Player)
			return;
		String name = entity.getName();
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().forEach(pQuest -> {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof MobTalkTask
							&& !pTask.isCompleted()
							&& pTask.getTask().getTarget().equals(name))
					.forEach(pTask -> {
						((MobTalkTask)pTask.getTask()).getDialog().stream().forEach(line -> {
							String msg = line.replace("%player%", p.getName());
							msg = PlaceholderAPI.setPlaceholders(p, msg);
							p.sendMessage("§4§l» §r"+msg);
						});
						pTask.complete();
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
						//TODO
						//Dodanie aktualizacji zadania
					});
			});
		});
	}
	
}
