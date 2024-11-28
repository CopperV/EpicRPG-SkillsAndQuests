package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

public class RaidCompleteTaskCommand extends AAdminRaidCommand {

	public RaidCompleteTaskCommand() {
		super("complete-task", new String[] {});
	}

	@Override
	public boolean canUse(CommandSender sender) {
		return super.canUse(sender);
	}

	@Override
	public boolean useCommand(CommandSender sender, String... args) {
		if(args == null || args.length < 2)
			return false;
		String taskId = args[1];
		Player p = Bukkit.getPlayerExact(args[0]);
		if(p == null || !p.isOnline()) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §7"+args[0]+" §ejest offline!");
			return false;
		}
		
		MutableObject<String> message = new MutableObject<>("");
		MutableBoolean result = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer(p).ifPresentOrElse(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(quest -> quest instanceof PlayerRaidQuest)
				.map(quest -> (PlayerRaidQuest) quest)
				.flatMap(raid -> raid.getTasks().stream())
				.filter(task -> task.getTask().getId().equals(taskId))
				.findAny()
				.ifPresentOrElse(task -> {
					task.complete();
				}, () -> {
					message.setValue(RaidManager.get().getRaidPrefix()
							+" §7"+p.getName()+" §enie posiada zadnego taska o ID §7"+taskId+"§e!");
					result.setFalse();
				});
		}, () -> {
			message.setValue(RaidManager.get().getRaidPrefix()
					+" §7"+p.getName()+" §enie ma zadnych questow! §7[§4§lBUG§7]");
			result.setFalse();
		});
		
		if(result.isFalse())
			sender.sendMessage(message.getValue());
		return result.booleanValue();
	}

	@Override
	public void showCorrectUsage(CommandSender sender) {
		super.showCorrectUsage(sender);
		sender.sendMessage("  §f§o/raid drop [gracz] [taskId] §7- Konczy taska o danym id, jesli takowy istnieje");
	}

}
