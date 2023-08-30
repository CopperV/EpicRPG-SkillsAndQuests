package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class QuestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("quests"))
			return false;
		sender.sendMessage("Odpalam liste questow");
		return true;
	}

}
