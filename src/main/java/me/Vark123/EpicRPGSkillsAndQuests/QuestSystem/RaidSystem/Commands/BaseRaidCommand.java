package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;

public class BaseRaidCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("raid"))
			return false;
		
		if(args.length == 0) {
			showCorrectUsage(sender);
			return false;
		}
		
		MutableBoolean returnValue = new MutableBoolean(true);
		RaidCommandManager.get().getRaidSubcommand(args[0].toLowerCase())
			.ifPresentOrElse(subcmd -> {
				if(!subcmd.canUse(sender)) {
					showCorrectUsage(sender);
					returnValue.setFalse();
					return;
				}
				if(args.length > 1) {
					String[] newArgs = new String[args.length - 1];
					for(int i = 0; i < newArgs.length; ++i)
						newArgs[i] = args[i+1];
					boolean res = subcmd.useCommand(sender, newArgs);
					if(!res)
						subcmd.showCorrectUsage(sender);
					returnValue.setValue(res);
				} else {
					boolean res = subcmd.useCommand(sender);
					if(!res)
						subcmd.showCorrectUsage(sender);
					returnValue.setValue(res);
				}
			}, () -> {
				showCorrectUsage(sender);
				returnValue.setFalse();
			});
		return returnValue.booleanValue();
	}
	
	private void showCorrectUsage(CommandSender sender) {
		sender.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §7Poprawne uzycie komendy §f§o/raid");
		RaidCommandManager.get().getRaidSubcommands().keySet().stream()
			.filter(key -> {
				ARaidCommand cmd = RaidCommandManager.get().getRaidSubcommand(key).get();
				return cmd.getCmd().equals(key)
						&& cmd.canUse(sender);
			}).forEach(key -> {
				RaidCommandManager.get().getRaidSubcommand(key).get().showCorrectUsage(sender);
			});
	}
	
}
