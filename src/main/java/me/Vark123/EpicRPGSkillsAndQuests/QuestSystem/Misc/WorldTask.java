package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerWorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

public class WorldTask extends PlayerTask {

	public WorldTask(AQuest quest, ATask task, int progress, boolean completed) {
		super(null, quest, task, progress, completed);
	}

	@Override
	public void complete() {
		this.completed = true;
		
		Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f));
		
		PlayerWorldQuest worldQuest = WorldQuestController.get().getActiveQuests().get(quest);
		worldQuest.tryAutoudateQuest();
	}

}
