package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands.QuestCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.BaseDungeonCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.DungeonCommandManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonJoinCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonLeaveCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonRespCommand;

public final class CommandManager {

	private CommandManager() { }
	
	public static void setExecutors() {
		Bukkit.getPluginCommand("quests").setExecutor(new QuestCommand());
		Bukkit.getPluginCommand("dungeon").setExecutor(new BaseDungeonCommand());
	
		DungeonCommandManager.get().registerSubcommand(new DungeonJoinCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonLeaveCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonRespCommand());
	}
	
}
