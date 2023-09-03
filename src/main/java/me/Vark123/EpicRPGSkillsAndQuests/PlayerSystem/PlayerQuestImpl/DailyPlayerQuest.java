package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.DailyController;
import me.clip.placeholderapi.PlaceholderAPI;

public class DailyPlayerQuest extends APlayerQuest {

	public DailyPlayerQuest(Player player, AQuest quest, int stage, Collection<PlayerTask> tasks) {
		super(player, quest, stage, tasks);
	}

	@Override
	public void tryAutoudateQuest() {
		return;
	}

	@Override
	public void tryUpdateOrEndQuest() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				TaskGroup oldTaskGroup = quest.getTaskGroups().get(stage);
				oldTaskGroup.getMessage()
					.ifPresent(message -> {
						String msg = message.replace("%player%", player.getName());
						msg = PlaceholderAPI.setPlaceholders(player, msg);
						player.sendMessage("§4§l» §r"+msg);
					});
				
				oldTaskGroup.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(this));
				oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
				endQuest();
			});
	}

	@Override
	public void updateQuest() {
		
	}

	@Override
	public void changeQuestStage(int newStage) {
		
	}

	@Override
	public void endQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		DatabaseManager.deletePlayerDaily(player);
		DailyController.get().getDoneDaily().add(player.getUniqueId());
		qp.getActiveQuests().remove(quest);
		
		player.sendTitle("§6§lWYKONALES ZADANIE DZIENNE", quest.getDisplay(), 5, 10, 15);
		player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
		player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
		
		quest.getPrize().forEach(prize -> prize.givePrize(player));
	}

	@Override
	public void removeQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		DatabaseManager.deletePlayerDaily(player);
		qp.getActiveQuests().remove(quest);
	}

	
	
}
