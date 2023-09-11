package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class DungeonCommandManager {

	private static final DungeonCommandManager inst = new DungeonCommandManager();
	
	private final Map<String, ADungeonCommand> dungeonSubcommands;
	
	private DungeonCommandManager() {
		dungeonSubcommands = new LinkedHashMap<>();
	}
	
	public static final DungeonCommandManager get() {
		return inst;
	}
	
	public void registerSubcommand(ADungeonCommand subcmd) {
		dungeonSubcommands.put(subcmd.getCmd(), subcmd);
		for(String alias : subcmd.getAliases())
			dungeonSubcommands.put(alias, subcmd);
	}
	
	public Optional<ADungeonCommand> getDungeonSubcommand(String subcmd) {
		return Optional.ofNullable(dungeonSubcommands.get(subcmd.toLowerCase()));
	}
	
}
