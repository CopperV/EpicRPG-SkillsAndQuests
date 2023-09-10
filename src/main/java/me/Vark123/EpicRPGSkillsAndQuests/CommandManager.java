package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands.QuestCommand;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands.ZlecenieCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.BaseDungeonCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.DungeonCommandManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonJoinCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonLeaveCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl.DungeonRespCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.GMSystem.Commands.QuestGMCommand;

public final class CommandManager {

	private CommandManager() { }
	
	public static void setExecutors() {
		Bukkit.getPluginCommand("quests").setExecutor(new QuestCommand());
		Bukkit.getPluginCommand("zlecenie").setExecutor(new ZlecenieCommand());
		Bukkit.getPluginCommand("dungeon").setExecutor(new BaseDungeonCommand());
		Bukkit.getPluginCommand("questgm").setExecutor(new QuestGMCommand());
	
		DungeonCommandManager.get().registerSubcommand(new DungeonJoinCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonLeaveCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonRespCommand());
	}
	
}
