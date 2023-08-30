package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.TaskManager;

@Getter
public class TaskGroup {

	private AQuest quest;
	
	private boolean autoupdate;
	private String message;
	
	private Collection<ATask> tasks;
	
	public TaskGroup(ConfigurationSection groupSection, AQuest quest) {
		this.quest = quest;
		this.autoupdate = groupSection.getBoolean("talking");
		if(groupSection.contains("message"))
			this.message = ChatColor.translateAlternateColorCodes('&', groupSection.getString("message"));
		
		this.tasks = new LinkedList<>();
		ConfigurationSection tasksSection = groupSection.getConfigurationSection("targets");
		tasksSection.getKeys(false).forEach(key -> {
			ConfigurationSection taskSection = tasksSection.getConfigurationSection(key);
			ATask task = TaskManager.get().generateTask(quest, taskSection);
			if(task == null)
				return;
			tasks.add(task);
		});
	}
	
	public Optional<String> getMessage() {
		return Optional.ofNullable(message);
	}
	
}
