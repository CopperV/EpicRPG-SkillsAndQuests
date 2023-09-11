package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonPortal;

@Getter
public class DungeonQuest extends AQuest {

	private String world;
	@Setter
	private boolean defeated;
	private int maxResps;
	private int unlockResp;
	
	private Collection<DungeonPortal> portals;
	private Collection<MythicSpawner> baseSpawners;
	
	private int damageCount;
	
	public DungeonQuest(ConfigurationSection questSection) {
		super(questSection);
		
		this.world = questSection.getString("world");
		this.maxResps = questSection.getInt("maxResp");
		this.defeated = questSection.getBoolean("defeat");
		this.unlockResp = questSection.getInt("unlockResp");
		
		portals = new HashSet<>();
		ConfigurationSection portalSection = questSection.getConfigurationSection("portals");
		if(portalSection != null)
			portalSection.getKeys(false).stream().forEach(key -> {
				String region = portalSection.getString(key+".region");
				double x = portalSection.getDouble(key+".x");
				double y = portalSection.getDouble(key+".y");
				double z = portalSection.getDouble(key+".z");
				portals.add(new DungeonPortal(region, x, y, z));
			});
		
		taskGroups.clear();
		this.taskGroups = new LinkedHashMap<>();
		ConfigurationSection taskGroupsSection = questSection.getConfigurationSection("stopnie");
		taskGroupsSection.getKeys(false).forEach(key -> {
			if(!StringUtils.isNumeric(key))
				return;
			Integer groupNum = Integer.parseInt(key);
			taskGroups.put(groupNum, new DungeonGroup(taskGroupsSection.getConfigurationSection(key), this));
		});
		
		baseSpawners = questSection.getStringList("spawners").stream()
				.map(spawner -> 
					MythicBukkit.inst().getSpawnerManager().getSpawnerByName(spawner))
				.filter(spawner -> spawner != null)
				.collect(Collectors.toList());
		
		this.damageCount = questSection.getInt("count",taskGroups.size());
	}

}
