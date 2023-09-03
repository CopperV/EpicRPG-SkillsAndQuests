package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Config;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Menu.QuestMenuManager;

public class QuestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("quests"))
			return false;
		if(!(sender instanceof Player)) {
			sender.sendMessage(Config.get().getPrefix()+" §cTylko gracz moze uzywac tej komendy!");
			return false;
		}
		Player viewer = (Player) sender;
		Player player = (Player) sender;
		if(args.length > 0 && viewer.hasPermission("epicrpg.gm") && Bukkit.getPlayerExact(args[0]) != null)
			player = Bukkit.getPlayerExact(args[0]);
		QuestMenuManager.get().openQuestListMenu(viewer, player);
		return true;
	}

}
