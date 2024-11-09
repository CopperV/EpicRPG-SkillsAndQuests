package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

@Getter
public class RaidGroup extends TaskGroup {
	
	private String id;
	
	private RaidObjective objective;
	private Collection<String> blockedRegions;
	private RaidCheckpoint checkpoint;

	public RaidGroup(ConfigurationSection groupSection, AQuest quest, RaidObjective objective) {
		super(groupSection, quest);
		
		this.id = groupSection.getString("id");
		this.blockedRegions = groupSection.getStringList("blocked");
		
		this.objective = objective;
		this.autoupdate = true;
		
		if(groupSection.contains("checkpoint")) {
			ConfigurationSection checkpointSection = groupSection.getConfigurationSection("checkpoint");
			
			String targetGroup = checkpointSection.getString("group");
			List<String> commands = checkpointSection.getStringList("commands");
			List<String> blockers = checkpointSection.getStringList("blockers");
			this.checkpoint = new RaidCheckpoint(targetGroup, commands, blockers);
		}
	}

}
