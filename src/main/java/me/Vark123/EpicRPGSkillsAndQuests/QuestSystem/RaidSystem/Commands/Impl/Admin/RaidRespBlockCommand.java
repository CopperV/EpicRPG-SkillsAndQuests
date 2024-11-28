package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

public class RaidRespBlockCommand extends AAdminRaidCommand {

	public RaidRespBlockCommand() {
		super("block-resp", new String[] {});
	}

	@Override
	public boolean canUse(CommandSender sender) {
		return super.canUse(sender);
	}

	@Override
	public boolean useCommand(CommandSender sender, String... args) {
		if(args == null || args.length < 2)
			return false;
		String blockerId = args[0];
		Player p = Bukkit.getPlayerExact(args[1]);
		if(p == null || !p.isOnline()) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §7"+args[1]+" §ejest offline!");
			return false;
		}
		
		MutableObject<String> message = new MutableObject<>("");
		MutableBoolean result = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer(p).ifPresentOrElse(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(quest -> quest instanceof PlayerRaidQuest)
				.map(quest -> (PlayerRaidQuest) quest)
				.filter(raid -> !raid.isRespFlag())
				.findAny()
				.ifPresentOrElse(raid -> {
					raid.getTasks().stream()
						.map(pTask -> ((RaidGroup) pTask.getTask().getTaskGroup()))
						.filter(group -> group.getCheckpoint() != null)
						.filter(group -> group.getCheckpoint().getBlockers().contains(blockerId))
						.findAny()
						.ifPresentOrElse(group -> {
							raid.setRespFlag(true);
							raid.setRespBlockerTaskGroupId(group.getId());
							raid.getPlayerBossFightContainer().clear();
							raid.performAction(_p -> {
								if(!_p.getWorld().getName().equals(raid.getWorld()))
									return;
								raid.getPlayerBossFightContainer().add(_p);
							});
						}, () -> {
							message.setValue(RaidManager.get().getRaidPrefix()
									+" §7"+p.getName()+" §enie posiada grupy z aktywnym checkpointem z blockerem §7"+blockerId+"§e!");
							result.setFalse();
						});
				}, () -> {
					message.setValue(RaidManager.get().getRaidPrefix()
							+" §7"+p.getName()+" §enie wykonuje zadnego rajda z odblokowanymi respami!");
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
		sender.sendMessage("  §f§o/raid block-resp [blockerId] [gracz] §7- Zalacza bloker respow, by rozpoczac walke z bossem");
	}

}
