package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidObjectiveRule;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl.CommandEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl.CommandListEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl.SchematicEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.RuleImpl.ObjectiveCompleteRule;
import me.Vark123.EpicRPGSkillsAndQuests.Utils.ChainLinkedList;

@Getter
public class RaidObjective {

	private RaidQuest raidQuest;
	
	private String id;
	private RaidResp respLocation;
	private String display;				//Wykorzystywane do menu kontynuacji rajdu
	private String message;
	
	private Collection<ChainLinkedList<RaidGroup>> taskGroups;
	private Map<ERaidEventType, Collection<IRaidEvent>> events = new LinkedHashMap<>();
	private Collection<IRaidObjectiveRule> rules = new ArrayList<>();
	
	public RaidObjective(ConfigurationSection objectiveSection, RaidQuest quest) {
		this.raidQuest = quest;
		this.id = objectiveSection.getString("id");
		
		double respX = objectiveSection.getDouble("resp.x");
		double respY = objectiveSection.getDouble("resp.y");
		double respZ = objectiveSection.getDouble("resp.z");
		this.respLocation = new RaidResp(respX, respY, respZ);

		this.display = ChatColor.translateAlternateColorCodes('&', objectiveSection.getString("display"));
		if(objectiveSection.contains("message"))
			this.message = ChatColor.translateAlternateColorCodes('&', objectiveSection.getString("message"));
		
		generateGroups(objectiveSection.getConfigurationSection("groups"), quest);
		if(objectiveSection.contains("events") && objectiveSection.isConfigurationSection("events"))
			generateEvents(objectiveSection.getConfigurationSection("events"));
		if(objectiveSection.contains("rules") && objectiveSection.isConfigurationSection("rules"))
			generateRules(objectiveSection.getConfigurationSection("rules"));
	}
	
	private void generateGroups(ConfigurationSection section, RaidQuest quest) {
		section.getKeys(false).stream()
			.filter(section::isConfigurationSection)
			.map(section::getConfigurationSection)
			.forEach(outerSection -> {
				ChainLinkedList<RaidGroup> groups = new ChainLinkedList<>();
				outerSection.getKeys(false).stream()
					.filter(outerSection::isConfigurationSection)
					.map(outerSection::getConfigurationSection)
					.map(innerSection -> new RaidGroup(section, quest, this))
					.forEach(groups::add);
				taskGroups.add(groups);
			});
	}
	
	private void generateEvents(ConfigurationSection section) {
		section.getKeys(false).stream()
			.filter(ERaidEventType::isEnumValue)
			.filter(section::isConfigurationSection)
			.map(section::getConfigurationSection)
			.forEach(eventsSection -> {
				ERaidEventType eventType = ERaidEventType.valueOf(eventsSection.getName().toUpperCase());
				if(!events.containsKey(eventType))
					events.put(eventType, new LinkedList<>());
				Collection<IRaidEvent> localEvents = events.get(eventType);
				eventsSection.getKeys(false).stream()
					.filter(eventsSection::isConfigurationSection)
					.map(eventsSection::getConfigurationSection)
					.forEach(eventSection -> {
						switch(eventSection.getString("type", "UNKNOWN").toUpperCase()) {
							case "COMMAND-LIST":
								{
									List<String> commands = eventSection.getStringList("cmd");
									localEvents.add(new CommandListEvent(commands));
								}
								break;
							case "COMMAND":
								{
									String command = eventSection.getString("cmd");
									localEvents.add(new CommandEvent(command));
								}
								break;
							case "SCHEMATIC":
								{
									String schematic = eventSection.getString("name");
									int x = eventSection.getInt("location.x");
									int y = eventSection.getInt("location.y");
									int z = eventSection.getInt("location.z");
									localEvents.add(new SchematicEvent(schematic, x, y, z));
								}
								break;
						}
					});
			});
	}
	
	private void generateRules(ConfigurationSection section) {
		section.getKeys(false).stream()
			.filter(section::isConfigurationSection)
			.map(section::getConfigurationSection)
			.forEach(ruleSection -> {
				switch(ruleSection.getString("rule-type", "UNKNOWN").toUpperCase()) {
					case "OBJECTIVE_COMPLETE_RULE":
						{
							int minGroups = ruleSection.getInt("min-groups", taskGroups.size());
							boolean skip = ruleSection.getBoolean("skip", false);
							List<String> mustCompleted = ruleSection.getStringList("must-completed");
							rules.add(new ObjectiveCompleteRule(this, minGroups, skip, mustCompleted));
						}
						break;
				}
			});
	}
	
	public Collection<RaidGroup> getLastGroups() {
		return taskGroups.stream()
				.map(list -> list.getLast())
				.collect(Collectors.toList());
	}
	
}
