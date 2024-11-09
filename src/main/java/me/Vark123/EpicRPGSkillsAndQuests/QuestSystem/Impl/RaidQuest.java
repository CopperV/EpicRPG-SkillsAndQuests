package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.PrizeManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidObjective;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPortal;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidDropTable;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.DropTableImpl.CommandDropTable;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.DropTableImpl.MythicMobItemDropTable;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.RequirementManager;

@Getter
public class RaidQuest extends AQuest {

	private String world;
	@Setter
	private boolean defeated;

	private Collection<RaidPortal> portals;
	protected Collection<MythicSpawner> baseSpawners;
	
	protected Map<String, List<IRaidDropTable>> dropTables = new LinkedHashMap<>();
	protected Map<Integer, RaidObjective> objectives = new LinkedHashMap<>();
	
	public RaidQuest(ConfigurationSection questSection) {
		super(questSection);
		
		this.id = questSection.getString("id");
		this.display = ChatColor.translateAlternateColorCodes('&', questSection.getString("name"));
		this.questGiver = ChatColor.translateAlternateColorCodes('&', questSection.getString("zleceniodawca"));
		this.lore = questSection.getStringList("lore")
				.stream()
				.map(line -> ChatColor.translateAlternateColorCodes('&', line))
				.collect(Collectors.toList());
		this.prize = PrizeManager.generatePrizes(questSection.getStringList("prize"));
		this.requirements = RequirementManager.generateRequirements(questSection.getStringList("require"));
	
		baseSpawners = questSection.getStringList("spawners").stream()
				.map(spawner -> 
					MythicBukkit.inst().getSpawnerManager().getSpawnerByName(spawner))
				.filter(spawner -> spawner != null)
				.collect(Collectors.toList());
		
		this.taskGroups = new LinkedHashMap<>();
		
		if(questSection.contains("drop-tables") && questSection.isConfigurationSection("drop-tables"))
			generateDropTables(questSection.getConfigurationSection("drop-tables"));
		generateObjectives(questSection.getConfigurationSection("objectives"), this);	

		portals = new HashSet<>();
		ConfigurationSection portalSection = questSection.getConfigurationSection("portals");
		if(portalSection != null)
			portalSection.getKeys(false).stream().forEach(key -> {
				String region = portalSection.getString(key+".region");
				double x = portalSection.getDouble(key+".x");
				double y = portalSection.getDouble(key+".y");
				double z = portalSection.getDouble(key+".z");
				portals.add(new RaidPortal(region, x, y, z));
			});
	}
	
	private void generateDropTables(ConfigurationSection dropSection) {
		dropSection.getKeys(false).stream()
			.filter(dropSection::isConfigurationSection)
			.map(dropSection::getConfigurationSection)
			.forEach(dropTableSection -> {
				String mob = ChatColor.translateAlternateColorCodes('&', dropTableSection.getString("mob"));
				List<IRaidDropTable> dropTable = new LinkedList<>();
				ConfigurationSection drops = dropTableSection.getConfigurationSection("drops");
				drops.getKeys(false).stream()
					.filter(drops::isConfigurationSection)
					.map(drops::getConfigurationSection)
					.forEach(_dropSection -> {
						double chance = _dropSection.getDouble("chance", 1);
						boolean limited = _dropSection.getBoolean("limited", true);
						switch(_dropSection.getString("type").toUpperCase()) {
							case "COMMAND":
								{
									List<String> commands = _dropSection.getStringList("commands");
									dropTable.add(new CommandDropTable(commands, chance, limited));
								}
								break;
							case "ITEM":
								{
									String item = _dropSection.getString("id");
									int amount = _dropSection.getInt("amount", 1);
									dropTable.add(new MythicMobItemDropTable(item, amount, chance, limited));
								}
								break;
						}
					});
				dropTables.put(mob, dropTable);
			});
	}
	
	private void generateObjectives(ConfigurationSection objectiveSection, RaidQuest raidQuest) {
		objectiveSection.getKeys(false).stream()
			.filter(objectiveSection::isConfigurationSection)
			.filter(StringUtils::isNumeric)
			.map(objectiveSection::getConfigurationSection)
			.forEach(section -> {
				int stage = Integer.parseInt(section.getName());
				RaidObjective objective = new RaidObjective(objectiveSection, raidQuest);
				objectives.put(stage, objective);
			});
	}
	
	public Optional<RaidObjective> getNextObjective(RaidObjective objective){
		int index = objectives.entrySet().stream()
				.filter(entry -> entry.getValue().equals(objective))
				.map(entry -> entry.getKey())
				.findFirst()
				.orElseGet(() -> -1);
		if(index < 0)
			return Optional.empty();
		return Optional.ofNullable(objectives.get(index+1));
	}
	
	public Optional<RaidObjective> getPreviousObjective(RaidObjective objective){
		int index = objectives.entrySet().stream()
				.filter(entry -> entry.getValue().equals(objective))
				.map(entry -> entry.getKey())
				.findFirst()
				.orElseGet(() -> -1);
		if(index < 0)
			return Optional.empty();
		return Optional.ofNullable(objectives.get(index-1));
	}
	
	public boolean isLastObjective(RaidObjective objective) {
		int index = objectives.entrySet().stream()
				.filter(entry -> entry.getValue().equals(objective))
				.map(entry -> entry.getKey())
				.findFirst()
				.orElseGet(() -> -2);
		int max = objectives.keySet().stream()
				.max(Integer::compare)
				.orElse(-1);
		return index >= max;
		
	}

}
