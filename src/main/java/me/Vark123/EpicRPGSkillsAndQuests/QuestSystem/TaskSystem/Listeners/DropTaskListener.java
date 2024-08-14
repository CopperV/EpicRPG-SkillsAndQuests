package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.DropTask;

public class DropTaskListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDrop(MythicMobDeathEvent e) {
		LivingEntity killer = e.getKiller();
		if(!(killer instanceof Player))
			return;
		
		Map<String, Integer> drops = new LinkedHashMap<>();
		e.getDrops().stream().forEach(it -> drops.put(it.getItemMeta().getDisplayName(), it.getAmount()));
		Collection<PlayerTask> tasks = new LinkedList<>();
		PlayerManager.get().getQuestPlayer((Player) killer)
			.ifPresent(qp -> {
				qp.getActiveQuests().values().stream().forEach(pQuest -> {
					pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof DropTask
							&& !pTask.isCompleted()
							&& drops.containsKey(pTask.getTask().getTarget()))
					.forEach(tasks::add);
				});
			});
		
		tasks.forEach(pTask -> {
			pTask.addProgress(drops.get(pTask.getTask().getTarget()));
			if(pTask.getIntProgress() >= ((DropTask)pTask.getTask()).getAmount())
				pTask.complete();
			killer.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}
}
