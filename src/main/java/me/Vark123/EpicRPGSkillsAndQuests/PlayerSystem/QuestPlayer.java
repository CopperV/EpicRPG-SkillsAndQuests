package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class QuestPlayer {

	private Player player;
	private Collection<String> completedQuests;
	private Map<AQuest, PlayerQuest> activeQuests;
	
	//TODO
	//Przeniesienie tych metod
	//do questow
	public void tryUpdateOrEndQuest(AQuest quest) {
		PlayerQuest pQuest = activeQuests.get(quest);
		if(pQuest == null)
			return;
		pQuest.getTasks().stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				TaskGroup oldTaskGroup = quest.getTaskGroups().get(pQuest.getStage());
				TaskGroup newTaskGroup = quest.getTaskGroups().get(pQuest.getStage() + 1);
				oldTaskGroup.getMessage()
					.ifPresent(message -> {
						String msg = message.replace("%player%", player.getName());
						msg = PlaceholderAPI.setPlaceholders(player, msg);
						player.sendMessage("§4§l» §r"+msg);
					});
				if(newTaskGroup == null) {
					oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
					endQuest(quest);
				}
				else {
					newTaskGroup.getRequirements().stream()
						.filter(req -> !req.checkRequirement(player))
						.findFirst().ifPresentOrElse(req -> { }, () -> {
							oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
							updateQuest(quest);
						});
				}
			});
	}
	
	public void updateQuest(AQuest quest) {
		PlayerQuest pQuest = activeQuests.get(quest);
		if(pQuest == null)
			return;
		int newStage = pQuest.getStage() + 1;
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		pQuest.setStage(newStage);
		pQuest.setTasks(pTasks);
		
		DatabaseManager.updatePlayerStageQuest(pQuest);
		
		player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
		player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
	}
	
	public void degradeQuest(AQuest quest, int newStage) {
		PlayerQuest pQuest = activeQuests.get(quest);
		if(pQuest == null)
			return;
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		pQuest.setStage(newStage);
		pQuest.setTasks(pTasks);
		
		DatabaseManager.updatePlayerStageQuest(pQuest);
	}
	
	public void endQuest(AQuest quest) {
		PlayerQuest pQuest = activeQuests.get(quest);
		if(pQuest == null)
			return;
		
		DatabaseManager.deletePlayerQuest(pQuest);
		activeQuests.remove(quest);
		completedQuests.add(quest.getId());
		
		player.sendTitle("§6§lWYKONALES ZADANIE", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
		player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		
		quest.getPrize().forEach(prize -> prize.givePrize(player));
	}
	
	public void removeQuest(AQuest quest) {
		PlayerQuest pQuest = activeQuests.get(quest);
		if(pQuest == null)
			return;
		
		DatabaseManager.deletePlayerQuest(pQuest);
		activeQuests.remove(quest);
	}
	
}
