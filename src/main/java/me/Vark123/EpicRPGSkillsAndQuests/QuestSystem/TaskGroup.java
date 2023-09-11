package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.PrizeManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.TaskManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.RequirementManager;

@Getter
public class TaskGroup {

	protected AQuest quest;
	
	protected boolean autoupdate;
	protected String message;
	
	protected Collection<ATask> tasks;
	protected Collection<IPrize> prize;
	protected Collection<IRequirement> requirements;
	protected Collection<QuestEvent> events;
	
	public TaskGroup(ConfigurationSection groupSection, AQuest quest) {
		this.quest = quest;
		this.autoupdate = !groupSection.getBoolean("talking");
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
		
		prize = PrizeManager.generatePrizes(groupSection.getStringList("prize"));
		requirements = RequirementManager.generateRequirements(groupSection.getStringList("require"));
	
		events = new HashSet<>();
		if(groupSection.contains("start"))
			events.add(new QuestEvent(this, EventCall.START, groupSection.getStringList("start")));
		if(groupSection.contains("end"))
			events.add(new QuestEvent(this, EventCall.END, groupSection.getStringList("end")));
		if(groupSection.contains("disconnect"))
			events.add(new QuestEvent(this, EventCall.DISCONNECT, groupSection.getStringList("disconnect")));
		if(groupSection.contains("join"))
			events.add(new QuestEvent(this, EventCall.JOIN, groupSection.getStringList("join")));
		if(groupSection.contains("death"))
			events.add(new QuestEvent(this, EventCall.DEATH, groupSection.getStringList("death")));
		if(groupSection.contains("world-change"))
			events.add(new QuestEvent(this, EventCall.WORLD_CHANGE, groupSection.getStringList("world-change")));
		if(groupSection.contains("complete"))
			events.add(new QuestEvent(this, EventCall.COMPLETE, groupSection.getStringList("complete")));
	}
	
	public Optional<QuestEvent> getEventsByType(EventCall call) {
		return events.stream()
				.filter(event -> event.getCallType().equals(call))
				.findFirst();
	}
	
	public Optional<String> getMessage() {
		return Optional.ofNullable(message);
	}
	
}
