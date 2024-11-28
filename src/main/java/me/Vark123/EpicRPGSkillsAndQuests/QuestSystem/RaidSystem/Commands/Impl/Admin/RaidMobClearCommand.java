package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

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
		
		MutableObject<String> message = new MutableObject<>("");
		MutableBoolean result = new MutableBoolean(true);
//		String[] mobs = args[1].split(",");
		List<String> mobs = Arrays.asList(args[1].split(","));
		w.getEntities().stream()
			.filter(e -> MythicBukkit.inst().getMobManager().isActiveMob(e.getUniqueId()))
			.map(e -> MythicBukkit.inst().getMobManager().getActiveMob(e.getUniqueId()).get())
			.filter(mob -> mobs.contains(mob.getMobType()))
			.collect(Collectors.toList())
			.forEach(mob -> mob.remove());
		
		if(result.isFalse())
			sender.sendMessage(message.getValue());
		return result.booleanValue();
	}

	@Override
	public void showCorrectUsage(CommandSender sender) {
		super.showCorrectUsage(sender);
		sender.sendMessage("  §f§o/raid clear-mobs [world] [mobs] §7- Usuwa moby danego typu na rajdzie");
	}

}
