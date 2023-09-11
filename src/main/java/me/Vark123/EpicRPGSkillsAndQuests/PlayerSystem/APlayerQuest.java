package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import java.util.Collection;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

@Getter
@AllArgsConstructor
public abstract class APlayerQuest {

	@Setter
	protected Player player;
	protected AQuest quest;
	@Setter
	protected int stage;
	@Setter
	protected Collection<PlayerTask> tasks;

	public void tryAutoudateQuest() {
		if(!quest.getTaskGroups().get(stage).isAutoupdate())
			return;
		tryUpdateOrEndQuest();
	}
	public abstract void tryUpdateOrEndQuest();
	public abstract void updateQuest();
	public abstract void changeQuestStage(int newStage);
	public abstract void endQuest();
	public abstract void removeQuest();
	
	public TaskGroup getPresentTaskGroup() {
		return quest.getTaskGroups().get(stage);
	}
}
