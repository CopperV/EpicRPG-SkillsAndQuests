package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.KillTask;

public class KillTaskListener implements Listener {

	@EventHandler
	public void onKill(MythicMobDeathEvent e) {
		Entity victim = e.getEntity();
		LivingEntity _killer = e.getKiller();
		
		if(_killer == null || !(_killer instanceof Player)) {
			if(!(victim instanceof LivingEntity))
				return;
			Player killer = ((LivingEntity) victim).getKiller();
			if(killer == null)
				return;
			_killer = killer;
		};
		if(!(_killer instanceof Player))
			return;
		
		Player killer = (Player) _killer;
		
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
