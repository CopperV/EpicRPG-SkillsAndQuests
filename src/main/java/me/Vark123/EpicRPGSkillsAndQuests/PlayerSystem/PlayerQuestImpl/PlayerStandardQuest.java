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
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Events.QuestEndEvent;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.TakeItemRequirement;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerStandardQuest extends APlayerQuest {

	public PlayerStandardQuest(Player player, AQuest quest, int stage, Collection<PlayerTask> tasks) {
		super(player, quest, stage, tasks);
	}

	@Override
	public void tryUpdateOrEndQuest() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				TaskGroup oldTaskGroup = quest.getTaskGroups().get(stage);
				TaskGroup newTaskGroup = quest.getTaskGroups().get(stage + 1);
				oldTaskGroup.getMessage()
					.ifPresent(message -> {
						String msg = message.replace("%player%", player.getName());
						msg = PlaceholderAPI.setPlaceholders(player, msg);
						player.sendMessage("§4§l» §r"+msg);
					});
				
				oldTaskGroup.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(this));
				
				if(newTaskGroup == null) {
					oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
					endQuest();
				}
				else {
					newTaskGroup.getRequirements().stream()
						.filter(req -> !req.checkRequirement(player))
						.findFirst().ifPresentOrElse(req -> { }, () -> {
							oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
							newTaskGroup.getRequirements().stream()
								.filter(req -> req instanceof TakeItemRequirement)
								.forEach(req -> ((TakeItemRequirement) req).takeItems(player));
							updateQuest();
						});
				}
			});
	}

	@Override
	public void updateQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		int newStage = stage + 1;
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		stage = newStage;
		tasks = pTasks;
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		DatabaseManager.updatePlayerStageQuest(this);
		
		player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
		player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
	}

	@Override
	public void changeQuestStage(int newStage) {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		stage = newStage;
		tasks = pTasks;
		
		DatabaseManager.updatePlayerStageQuest(this);
	}

	@Override
	public void endQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		DatabaseManager.deletePlayerQuest(this);
		qp.getActiveQuests().remove(quest);
		qp.getCompletedQuests().add(quest.getId());
		
		player.sendTitle("§6§lWYKONALES ZADANIE", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
		player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		
		quest.getPrize().forEach(prize -> prize.givePrize(player));
		
		Event event = new QuestEndEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public void removeQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		DatabaseManager.deletePlayerQuest(this);
		qp.getActiveQuests().remove(quest);
	}


}
