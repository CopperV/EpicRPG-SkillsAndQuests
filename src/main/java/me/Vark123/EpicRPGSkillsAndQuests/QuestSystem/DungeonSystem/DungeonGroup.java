package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem;

import java.util.Collection;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;

@Getter
public class DungeonGroup extends TaskGroup {
	
	private DungeonResp respLocation;
	private Collection<String> blockedRegions;

	public DungeonGroup(ConfigurationSection groupSection, AQuest quest) {
		super(groupSection, quest);
		
		this.blockedRegions = groupSection.getStringList("blocked");
		
		double respX = groupSection.getDouble("resp.x");
		double respY = groupSection.getDouble("resp.y");
		double respZ = groupSection.getDouble("resp.z");
		this.respLocation = new DungeonResp(respX, respY, respZ);
	}

	
	
}
