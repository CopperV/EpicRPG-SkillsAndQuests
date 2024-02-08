package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGFishing.KhorinisFishing.Events.KoloniaFishEvent;
import me.Vark123.EpicRPGFishing.Tanalorr.Events.TanalorrFishEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FishTask;

public class FishTaskListener implements Listener {

	@EventHandler
	public void onFish(KoloniaFishEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		ItemStack fish = e.getFish();
		NBTItem nbt = new NBTItem(fish);
		String mmId = nbt.getString("MYTHIC_TYPE");

		MutableObject<List<PlayerTask>> tasksToComplete = new MutableObject<>(new LinkedList<>());
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				tasksToComplete.getValue().addAll(pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof FishTask
							&& !pTask.isCompleted()
							&& pTask.getTask().getTarget().equals(mmId))
					.collect(Collectors.toList()));
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof FishTask
							&& !pTask.isCompleted()
							&& !pTask.getTask().getTarget().equals(mmId)
							&& ((FishTask)pTask.getTask()).isInRow())
					.forEach(pTask -> {
						pTask.setProgress(0);
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
						p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1, 0.8f);
					});
			});
		});
		tasksToComplete.getValue().forEach(pTask -> {
			pTask.addProgress(1);
			if(pTask.getIntProgress() >= ((FishTask)pTask.getTask()).getAmount())
				pTask.complete();
			p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}

	@EventHandler
	public void onFish(TanalorrFishEvent e) {
		if(e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		ItemStack fish = e.getFish();
		NBTItem nbt = new NBTItem(fish);
		String mmId = nbt.getString("MYTHIC_TYPE");

		MutableObject<List<PlayerTask>> tasksToComplete = new MutableObject<>(new LinkedList<>());
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream().forEach(pQuest -> {
				tasksToComplete.getValue().addAll(pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof FishTask
							&& !pTask.isCompleted()
							&& pTask.getTask().getTarget().equals(mmId))
					.collect(Collectors.toList()));
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof FishTask
							&& !pTask.isCompleted()
							&& !pTask.getTask().getTarget().equals(mmId)
							&& ((FishTask)pTask.getTask()).isInRow())
					.forEach(pTask -> {
						pTask.setProgress(0);
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
						p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1, 0.8f);
					});
			});
		});
		tasksToComplete.getValue().forEach(pTask -> {
			pTask.addProgress(1);
			if(pTask.getIntProgress() >= ((FishTask)pTask.getTask()).getAmount())
				pTask.complete();
			p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
		});
	}
	
}
