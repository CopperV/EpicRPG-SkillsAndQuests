package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PlayerTask {

	private Player player;
	private AQuest quest;
	private ATask task;
	@Setter
	private int progress;
	@Setter
	private boolean completed;
	
	public void addProgress(int progress) {
		
	}
	
	public void complete() {
		
	}
	
}
