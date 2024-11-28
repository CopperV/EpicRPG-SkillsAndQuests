package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Common;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.ARaidCommand;

public class RaidRespCommand extends ARaidCommand {

	public RaidRespCommand() {
		super("resp", new String[] {"odrodzenie", "join", "dolacz"});
	}

	@Override
	public boolean canUse(CommandSender sender) {
		if(!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		if(player.getWorld().getName().toLowerCase().contains("tutorial"))
			return false;
		MutableBoolean returnValue = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer(player)
			.ifPresentOrElse(qp -> {
				qp.getActiveQuests().values().stream()
					.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
					.map(pQuest -> (PlayerRaidQuest) pQuest)
					.filter(pQuest -> pQuest.isCanJoin())
					.filter(pQuest -> !pQuest.isRespFlag())
					.filter(pQuest -> !RaidManager.get().getRespTasks().containsKey(player))
					.findFirst()
					.ifPresentOrElse(pQuest -> { },
							() -> returnValue.setFalse());
			}, () -> returnValue.setFalse());
		return returnValue.booleanValue();
	}

	@Override
	public boolean useCommand(CommandSender sender, String... args) {
		if(!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		qp.getActiveQuests().values()
			.stream()
			.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
			.findFirst()
			.ifPresent(raid -> ((PlayerRaidQuest) raid).createRespTask(qp));
		return true;
	}

	@Override
	public void showCorrectUsage(CommandSender sender) {
		sender.sendMessage("  §f§o/raid dolacz §7- Przeteleportuj sie na rajd uzywajac");
	}

}
