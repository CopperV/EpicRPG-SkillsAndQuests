package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
//TODO
//Ogarniecie wczytywania dungow
public class DungeonQuest extends AQuest {

	private String world;
	private boolean defeated;
	private int maxResps;
	private int unlockResp;
	
	public DungeonQuest(ConfigurationSection questSection) {
		super(questSection);
		
	}

}
