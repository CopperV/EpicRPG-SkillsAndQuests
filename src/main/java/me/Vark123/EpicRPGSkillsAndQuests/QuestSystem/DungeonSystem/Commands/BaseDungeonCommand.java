package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;

public class BaseDungeonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("dungeon"))
			return false;
		if(!(sender instanceof Player))
			return false;
		
		Player p = (Player) sender;
		if(args.length == 0) {
			showCorrectUsage(p);
			return false;
		}
		
		MutableBoolean returnValue = new MutableBoolean(true);
		DungeonCommandManager.get().getDungeonSubcommand(args[0].toLowerCase())
			.ifPresentOrElse(subcmd -> {
				if(!subcmd.canUse(p)) {
					showCorrectUsage(p);
					returnValue.setFalse();
					return;
				}
				if(args.length > 1) {
					String[] newArgs = new String[args.length - 1];
					for(int i = 0; i < newArgs.length; ++i)
						newArgs[i] = args[i+1];
					boolean res = subcmd.useCommand(p, newArgs);
					if(!res)
						subcmd.showCorrectUsage(p);
					returnValue.setValue(res);
				} else {
					boolean res = subcmd.useCommand(p);
					if(!res)
						subcmd.showCorrectUsage(p);
					returnValue.setValue(res);
				}
			}, () -> {
				showCorrectUsage(p);
				returnValue.setFalse();
			});
		return returnValue.booleanValue();
	}
	
	private void showCorrectUsage(Player p) {
		p.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §7Poprawne uzycie komendy §f§o/dungeon");
		DungeonCommandManager.get().getDungeonSubcommands().keySet().stream()
			.filter(key -> {
				ADungeonCommand cmd = DungeonCommandManager.get().getDungeonSubcommand(key).get();
				return cmd.getCmd().equals(key)
						&& cmd.canUse(p);
			}).forEach(key -> {
				DungeonCommandManager.get().getDungeonSubcommand(key).get().showCorrectUsage(p);
			});
	}
	
}
