package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.GMSystem.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.GMSystem.MenuSystem.GMMenuManager;

public class QuestGMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("questgm"))
			return false;
		
		if(!sender.hasPermission("epicrpg.gm"))
			return false;
		
		if(args.length < 1) {
			showCorrectUsage(sender);
			return false;
		}
		
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null) {
			sender.sendMessage(Main.getInstance().getPrefix()+" §cGracz "+args[0]+" jest offline.");
			return false;
		}

		PlayerManager.get().getQuestPlayer(player).ifPresent(qp -> {
			GMMenuManager.get().openBaseMenu((Player) sender, player);
		});
		
		return true;
	}
	
	private void showCorrectUsage(CommandSender sender) {
		sender.sendMessage(Main.getInstance().getPrefix()+" §cPoprawne uzycie komendy §c§o/questgm§c:");
		sender.sendMessage("§4- §e§o/questgm <gracz>");
	}

}
