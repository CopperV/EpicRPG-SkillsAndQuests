package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
@AllArgsConstructor
public class PlayerTask {

	@Setter
	private Player player;
	protected AQuest quest;
	protected ATask task;
	@Setter
	protected int progress;
	@Setter
	protected boolean completed;
	
	public void addProgress(int progress) {
		this.progress += progress;
	}
	
	public void complete() {
		this.completed = true;
		player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);
		
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		APlayerQuest pQuest = qp.getActiveQuests().get(quest);
		pQuest.tryAutoudateQuest();
	}
	
	public String getProgress() {
		return task.getProgess(this);
	}
	
	public int getIntProgress() {
		return progress;
	}
	
}
