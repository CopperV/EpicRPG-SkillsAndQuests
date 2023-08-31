package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;

public class PlayerKillTaskListener implements Listener {

	@EventHandler
	public void onKill(EntityDeathEvent e) {
		LivingEntity victim = e.getEntity();
		Player killer = e.getEntity().getKiller();
		if(!(victim instanceof Player))
			return;
		if(killer == null)
			return;
		
		RpgPlayer rpg = me.Vark123.EpicRPG.Players.PlayerManager
				.getInstance()
				.getRpgPlayer((Player) victim);
		
		PlayerManager.get().getQuestPlayer(killer).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof PlayerKillTask
							&& !pTask.isCompleted()
							&& ((PlayerKillTask) pTask.getTask()).getLevel() >= rpg.getInfo().getLevel())
					.forEach(pTask -> {
						pTask.addProgress(1);
						if(pTask.getIntProgress() >= ((PlayerKillTask)pTask.getTask()).getAmount())
							pTask.complete();
						killer.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
						//TODO
						//Dodanie aktualizacji zadania
					});
			});
		});
	}
	
}
