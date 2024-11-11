package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.ARaidCommand;

public abstract class AAdminRaidCommand extends ARaidCommand {

	public AAdminRaidCommand(String cmd, String[] aliases) {
		super(cmd, aliases);
	}

	@Override
	public boolean canUse(Player player) {
		return player.hasPermission("epicrpg.gm-admin");
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("§7========== §4§lKOMENDA ADMINISTRACYJNA §r§7==========");
	}

}
