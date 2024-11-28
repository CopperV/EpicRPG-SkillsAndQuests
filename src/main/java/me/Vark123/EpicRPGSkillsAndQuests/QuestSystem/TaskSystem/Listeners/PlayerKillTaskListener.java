package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;

public class PlayerKillTaskListener implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player victim = e.getEntity();
		Entity _killer = victim.getKiller();
		if(_killer == null) {
			Event event = victim.getLastDamageCause();
			if(event instanceof EntityDamageByEntityEvent)
				_killer = ((EntityDamageByEntityEvent) event).getDamager();
		}
		if(_killer == null)
			return;
		if(!(_killer instanceof Player))
			return;
		
		Player killer = (Player) _killer;
		if(victim.equals(killer))
			return;
		
		RpgPlayer rpg = me.Vark123.EpicRPG.Players.PlayerManager
				.getInstance()
				.getRpgPlayer((Player) victim);
		
		MutableObject<List<PlayerTask>> tasksToComplete = new MutableObject<>(new LinkedList<>());
		PlayerManager.get().getQuestPlayer(killer).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				tasksToComplete.getValue().addAll(pQuest.getTasks().stream()
						.filter(pTask -> pTask.getTask() instanceof PlayerKillTask
								&& !pTask.isCompleted()
								&& ((PlayerKillTask) pTask.getTask()).getLevel() <= rpg.getInfo().getLevel())
						.collect(Collectors.toList()));
			});
		});
		tasksToComplete.getValue().forEach(pTask -> {
			pTask.addProgress(1);
			if(pTask.getIntProgress() >= ((PlayerKillTask)pTask.getTask()).getAmount())
				pTask.complete();
			if(pTask.getTask().getMessage() != null)
				killer.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}
	
}
