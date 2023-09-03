package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
public class WorldQuest extends AQuest {

	private boolean completed;

	public WorldQuest(ConfigurationSection questSection) {
		super(questSection);
		this.completed = questSection.getBoolean("completed");
	}
	
	
	
}
