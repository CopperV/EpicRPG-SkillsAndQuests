package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.TaskManager;

@Getter
public class DailyQuest extends AQuest {

	private int weightLimit;
	private LinkedHashMap<ATask, Integer> taskWeights;
	
	public DailyQuest(ConfigurationSection questSection) {
		super(questSection);
		
		this.weightLimit = questSection.getInt("stack");
		taskWeights = new LinkedHashMap<>();
		ConfigurationSection weightSection = questSection.getConfigurationSection("stopnie.1.targets");
		weightSection.getKeys(false).stream().forEach(key -> {
			String taskId = weightSection.getString(key+".id");
			int weight = weightSection.getInt(key+".waga");
			TaskManager.get().getTaskById(taskId)
				.ifPresent(task -> taskWeights.put(task, weight));
		});
	}

	public List<ATask> getRandomTasks() {
		List<ATask> toReturn = new LinkedList<>();
		
		Random rand = new Random();
		int weightController = weightLimit;
		Object[] taskArray = taskWeights.keySet().toArray();
		int size = taskArray.length;
		
		while(weightController > 0) {
			ATask task = (ATask) taskArray[rand.nextInt(size)];
			if(toReturn.contains(task))
				continue;
			toReturn.add(task);
			weightController -= taskWeights.get(task);
		}
		
		return toReturn;
	}
	
}
