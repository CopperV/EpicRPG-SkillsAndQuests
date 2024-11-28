package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands;

import org.bukkit.command.CommandSender;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class ARaidCommand {

	protected String cmd;
	protected String[] aliases;
	
	public abstract boolean canUse(CommandSender sender);
	public abstract boolean useCommand(CommandSender sender, String... args);
	public abstract void showCorrectUsage(CommandSender sender);

}
