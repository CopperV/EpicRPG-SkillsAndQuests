package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class ATask {

	private AQuest quest;
	private String id;
	private String target;
	private String message;
	
}
