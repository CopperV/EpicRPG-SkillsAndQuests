package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.KillTask;

public class KillTaskListener implements Listener {

	@EventHandler
	public void onKill(EntityDeathEvent e) {
		LivingEntity victim = e.getEntity();
		Player killer = e.getEntity().getKiller();
		if(victim instanceof Player)
			return;
		if(killer == null)
			return;
		
		MutableObject<List<PlayerTask>> tasksToComplete = new MutableObject<>(new LinkedList<>());
		String name = victim.getName();
		PlayerManager.get().getQuestPlayer(killer).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				tasksToComplete.getValue().addAll(pQuest.getTasks().stream()
						.filter(pTask -> pTask.getTask() instanceof KillTask
								&& !pTask.isCompleted()
								&& pTask.getTask().getTarget().equals(name))
						.collect(Collectors.toList()));
			});
		});
		tasksToComplete.getValue().forEach(pTask -> {
			pTask.addProgress(1);
			if(pTask.getIntProgress() >= ((KillTask)pTask.getTask()).getAmount())
				pTask.complete();
			killer.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}
	
}
