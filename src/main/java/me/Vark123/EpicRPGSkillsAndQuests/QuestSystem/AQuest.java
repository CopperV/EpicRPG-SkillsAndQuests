package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.PrizeManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.RequirementManager;

@Getter
@EqualsAndHashCode
public abstract class AQuest {

	protected String id;
	protected String display;
	protected String questGiver;
	protected List<String> lore;
	protected List<IPrize> prize;
	protected List<IRequirement> requirements;
	
	protected Map<Integer, TaskGroup> taskGroups;
	
	public AQuest(ConfigurationSection questSection) {
		this.id = questSection.getString("id");
		this.display = ChatColor.translateAlternateColorCodes('&', questSection.getString("name"));
		this.questGiver = ChatColor.translateAlternateColorCodes('&', questSection.getString("zleceniodawca"));
		this.lore = questSection.getStringList("lore")
				.stream()
				.map(line -> ChatColor.translateAlternateColorCodes('&', line))
				.collect(Collectors.toList());
		this.prize = PrizeManager.generatePrizes(questSection.getStringList("prize"));
		this.requirements = RequirementManager.generateRequirements(questSection.getStringList("require"));
		
		this.taskGroups = new LinkedHashMap<>();
		ConfigurationSection taskGroupsSection = questSection.getConfigurationSection("stopnie");
		taskGroupsSection.getKeys(false).forEach(key -> {
			if(!StringUtils.isNumeric(key))
				return;
			Integer groupNum = Integer.parseInt(key);
			taskGroups.put(groupNum, new TaskGroup(taskGroupsSection.getConfigurationSection(key), this));
		});
	}
	
}
