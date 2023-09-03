package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

@Getter
public class ZlecenieQuest extends AQuest {

	private int stages;
	
	public ZlecenieQuest(ConfigurationSection questSection) {
		super(questSection);
		this.stages = questSection.getInt("etapy");
	}
	
	public List<TaskGroup> getRandomTaskGroups() {
		List<TaskGroup> toReturn = new LinkedList<>();
		
		Random rand = new Random();
		int controller = stages;
		Object[] groups = taskGroups.values().toArray();
		int size = groups.length;
		while(controller > 0) {
			TaskGroup group = (TaskGroup) groups[rand.nextInt(size)];
			if(toReturn.contains(group))
				continue;
			toReturn.add(group);
			--controller;
		}
		
		return toReturn;
	}

}
