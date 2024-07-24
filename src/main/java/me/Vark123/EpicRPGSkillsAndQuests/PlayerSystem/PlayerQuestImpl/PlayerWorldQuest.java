package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Events.QuestEndEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.WorldQuestController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.WorldTask;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerWorldQuest extends APlayerQuest {

	public PlayerWorldQuest(AQuest quest, int stage, Collection<PlayerTask> tasks) {
		super(null, quest, stage, tasks);
	}

	@Override
	public void tryUpdateOrEndQuest() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				Collection<? extends Player> players = Bukkit.getOnlinePlayers();
				TaskGroup oldTaskGroup = quest.getTaskGroups().get(stage);
				TaskGroup newTaskGroup = quest.getTaskGroups().get(stage + 1);
				oldTaskGroup.getMessage()
					.ifPresent(message -> {
						players.forEach(player -> {
							String msg = message.replace("%player%", player.getName());
							msg = PlaceholderAPI.setPlaceholders(player, msg);
							player.sendMessage("§4§l» §r"+msg);
						});
						
					});

				players.forEach(player ->
					oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player)));
				if(newTaskGroup == null) {
					endQuest();
				} else {
					updateQuest();
				}
			});
	}

	@Override
	public void updateQuest() {
		int newStage = stage + 1;
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new WorldTask(quest, task, 0, false))
				.collect(Collectors.toList());
		
		stage = newStage;
		tasks = pTasks;
				
		DatabaseManager.updateWorldQuest(this);
		
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
			player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
			player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
	}

	@Override
	public void changeQuestStage(int newStage) {
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new WorldTask(quest, task, 0, false))
				.collect(Collectors.toList());
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		stage = newStage;
		tasks = pTasks;
		
		DatabaseManager.updateWorldQuest(this);
	}

	@Override
	public void endQuest() {
		WorldQuestController controller = WorldQuestController.get();
		DatabaseManager.deleteWorldQuest(this);
		controller.getActiveQuests().remove(quest);
		controller.getCompletedQuests().add(quest.getId());
		
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.sendTitle("§6§lWYKONO ZADANIE SWIATOWE", quest.getDisplay(), 5, 10, 15);
			player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
			player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
			player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
			
			quest.getPrize().forEach(prize -> prize.givePrize(player));
		
			PlayerManager.get().getQuestPlayer(player).ifPresent(qp ->
					qp.getActiveQuests().remove(quest));
		});
		Event event = new QuestEndEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public void removeQuest() {
		WorldQuestController controller = WorldQuestController.get();
		DatabaseManager.deleteWorldQuest(this);
		controller.getActiveQuests().remove(quest);
		
		PlayerManager.get().getQuestPlayer(player).ifPresent(qp ->
			qp.getActiveQuests().remove(quest));
	}

}
