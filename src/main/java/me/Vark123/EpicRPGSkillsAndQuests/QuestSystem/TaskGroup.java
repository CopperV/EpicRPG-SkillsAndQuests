package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;

@Getter
public class TaskGroup {

	private AQuest quest;
	
	public TaskGroup(ConfigurationSection groupSection, AQuest quest) {
		this.quest = quest;
	}
	
}
