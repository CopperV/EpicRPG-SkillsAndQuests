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
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.BaseRaidCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.RaidCommandManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidCompleteTaskCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidDmgCounterStartCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidDmgCounterStopCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidMobClearCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidDropTableCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidRespBlockCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidRespUnlockCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin.RaidSchemPasteCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Common.RaidLeaveCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Common.RaidRespCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Common.RaidWipeCommand;

public final class CommandManager {

	private CommandManager() { }
	
	public static void setExecutors() {
		Bukkit.getPluginCommand("quests").setExecutor(new QuestCommand());
		Bukkit.getPluginCommand("zlecenie").setExecutor(new ZlecenieCommand());
		Bukkit.getPluginCommand("dungeon").setExecutor(new BaseDungeonCommand());
		Bukkit.getPluginCommand("raid").setExecutor(new BaseRaidCommand());
		Bukkit.getPluginCommand("questgm").setExecutor(new QuestGMCommand());
	
		DungeonCommandManager.get().registerSubcommand(new DungeonJoinCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonLeaveCommand());
		DungeonCommandManager.get().registerSubcommand(new DungeonRespCommand());
		
		RaidCommandManager.get().registerSubcommand(new RaidRespCommand());
		RaidCommandManager.get().registerSubcommand(new RaidLeaveCommand());
		RaidCommandManager.get().registerSubcommand(new RaidWipeCommand());
		RaidCommandManager.get().registerSubcommand(new RaidDmgCounterStartCommand());
		RaidCommandManager.get().registerSubcommand(new RaidDmgCounterStopCommand());
		RaidCommandManager.get().registerSubcommand(new RaidRespBlockCommand());
		RaidCommandManager.get().registerSubcommand(new RaidRespUnlockCommand());
		RaidCommandManager.get().registerSubcommand(new RaidDropTableCommand());
		RaidCommandManager.get().registerSubcommand(new RaidMobClearCommand());
		RaidCommandManager.get().registerSubcommand(new RaidSchemPasteCommand());
		RaidCommandManager.get().registerSubcommand(new RaidCompleteTaskCommand());
	}
	
}
