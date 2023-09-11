package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicNPC.Events.EpicNpcInteractEvent;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.TalkTask;
import me.clip.placeholderapi.PlaceholderAPI;

public class TalkTaskListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(EpicNpcInteractEvent e) {
		if(e.isCancelled())
			return;
		Player p = e.getPlayer();
		String name = e.getNpc().getName();
		
		MutableObject<List<PlayerTask>> tasksToComplete = new MutableObject<>(new LinkedList<>());
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().forEach(pQuest -> {
				tasksToComplete.getValue().addAll(pQuest.getTasks().stream()
						.filter(pTask -> pTask.getTask() instanceof TalkTask
								&& !pTask.isCompleted()
								&& pTask.getTask().getTarget().equals(name))
						.collect(Collectors.toList()));
			});
		});
		tasksToComplete.getValue().forEach(pTask -> {
			e.setCancelled(true);
			((TalkTask)pTask.getTask()).getDialog().stream().forEach(line -> {
				String msg = line.replace("%player%", p.getName());
				msg = PlaceholderAPI.setPlaceholders(p, msg);
				p.sendMessage("§4§l» §r"+msg);
			});
			pTask.complete();
			p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}
	
}
