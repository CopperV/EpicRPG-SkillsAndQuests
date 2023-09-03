package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.ZlecenieController;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
public class PlayerZlecenieQuest extends APlayerQuest {

	private List<TaskGroup> localTaskGroups;
	
	public PlayerZlecenieQuest(Player player, AQuest quest, int stage,
			Collection<PlayerTask> tasks, List<TaskGroup> localTaskGroups) {
		super(player, quest, stage, tasks);
		this.localTaskGroups = localTaskGroups;
	}

	@Override
	public void tryAutoudateQuest() {
		if((stage + 1) >= localTaskGroups.size())
			return;
		tryUpdateOrEndQuest();
	}

	@Override
	public void tryUpdateOrEndQuest() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				TaskGroup oldTaskGroup = localTaskGroups.get(stage);
				TaskGroup newTaskGroup = null;
				if(localTaskGroups.size() > stage + 1)
					newTaskGroup = localTaskGroups.get(stage + 1);
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
							updateQuest();
						});
				}
			});
	}

	@Override
	public void updateQuest() {
		localTaskGroups.get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		int newStage = stage + 1;
		TaskGroup taskGroup = localTaskGroups.get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		stage = newStage;
		tasks = pTasks;
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
		player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
	}

	@Override
	public void changeQuestStage(int newStage) {
		localTaskGroups.get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		TaskGroup taskGroup = localTaskGroups.get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		stage = newStage;
		tasks = pTasks;
	}

	@Override
	public void endQuest() {
		localTaskGroups.get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		qp.getActiveQuests().remove(quest);
		
		player.sendTitle("§6§lWYKONALES ZLECENIE", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
		player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		
		quest.getPrize().forEach(prize -> prize.givePrize(player));
		
		ZlecenieController.get().addZlecenieCooldown(player, 20*60*5);
//		Event event = new QuestEndEvent(this);
//		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public void removeQuest() {
		localTaskGroups.get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		qp.getActiveQuests().remove(quest);
	}

	@Override
	public TaskGroup getPresentTaskGroup() {
		return localTaskGroups.get(stage);
	}

}
