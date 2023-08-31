package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicNPC.Events.EpicNpcInteractEvent;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.TalkTask;
import me.clip.placeholderapi.PlaceholderAPI;

public class TalkTaskListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(EpicNpcInteractEvent e) {
		if(e.isCancelled())
			return;
		Player p = e.getPlayer();
		String name = e.getNpc().getName();
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().forEach(pQuest -> {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof TalkTask
							&& !pTask.isCompleted()
							&& pTask.getTask().getTarget().equals(name))
					.forEach(pTask -> {
						e.setCancelled(true);
						((TalkTask)pTask.getTask()).getDialog().stream().forEach(line -> {
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
