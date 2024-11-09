package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class RaidCommandManager {

	private static final RaidCommandManager inst = new RaidCommandManager();
	
	private final Map<String, ARaidCommand> raidSubcommands;
	
	private RaidCommandManager() {
		raidSubcommands = new LinkedHashMap<>();
	}
	
	public static final RaidCommandManager get() {
		return inst;
	}
	
	public void registerSubcommand(ARaidCommand subcmd) {
		raidSubcommands.put(subcmd.getCmd(), subcmd);
		for(String alias : subcmd.getAliases())
			raidSubcommands.put(alias, subcmd);
	}
	
	public Optional<ARaidCommand> getRaidSubcommand(String subcmd) {
		return Optional.ofNullable(raidSubcommands.get(subcmd.toLowerCase()));
	}
	
}
