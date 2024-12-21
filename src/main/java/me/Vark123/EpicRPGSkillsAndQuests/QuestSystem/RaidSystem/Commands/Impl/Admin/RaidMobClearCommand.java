package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidMobManager;

public class RaidMobClearCommand extends AAdminRaidCommand {

	public RaidMobClearCommand() {
		super("clear-mobs", new String[] {});
	}

	@Override
	public boolean canUse(CommandSender sender) {
		return super.canUse(sender);
	}

	@Override
	public boolean useCommand(CommandSender sender, String... args) {
		if(args == null || args.length < 2)
			return false;
		World w = Bukkit.getWorld(args[0]);
		if(w == null) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §eSwiat §7"+args[0]+" §enie istnieje!");
			return false;
		}

		List<String> mobs = Arrays.asList(args[1].split(","));
		RaidMobManager.get().getStoredMob().keySet().stream()
			.filter(aMob -> aMob.getSpawnLocation().getWorld().getName().equals(w.getName()))
			.filter(aMob -> mobs.contains(aMob.getMobType()))
			.forEach(aMob -> {
				RaidMobManager.get().getCleanerMob().put(aMob, w.getName());
			});
				
		return true;
	}

	@Override
	public void showCorrectUsage(CommandSender sender) {
		super.showCorrectUsage(sender);
		sender.sendMessage("  §f§o/raid clear-mobs [world] [mobs] §7- Usuwa moby danego typu na rajdzie");
	}

}
