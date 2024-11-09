package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.ARaidCommand;

public class RaidWipeCommand extends ARaidCommand {

	public RaidWipeCommand() {
		super("wipe", new String[] {"cofnij", "checkpoint"});
	}

	@Override
	public boolean canUse(Player player) {
		if(player.getWorld().getName().toLowerCase().contains("tutorial"))
			return false;
		MutableBoolean returnValue = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer(player)
			.ifPresentOrElse(qp -> {
				qp.getActiveQuests().values().stream()
					.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
					.map(pQuest -> (PlayerRaidQuest) pQuest)
					.filter(pQuest -> pQuest.isCanJoin())
					.filter(pQuest -> pQuest.isRespFlag())
					.filter(pQuest -> pQuest.getParty().isEmpty()
							|| (pQuest.getParty().isPresent()
									&& pQuest.getParty().get().getLeader().getPlayer().getUniqueId()
										.equals(player.getUniqueId())))
					.filter(pQuest -> Bukkit.getWorld(pQuest.getWorld())
							.getPlayers().size() < 1)
					.findFirst()
					.ifPresentOrElse(pQuest -> {},
							() -> returnValue.setFalse());
			}, () -> returnValue.setFalse());
		return returnValue.booleanValue();
	}

	@Override
	public boolean useCommand(Player player, String... args) {
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
		qp.getActiveQuests().values()
			.stream()
			.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
			.findFirst()
			.ifPresent(raid -> ((PlayerRaidQuest) raid).wipeRaid());
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §f§p/raid wipe §7- Cofnij rajd do ostatniego punktu kontrolnego");
	}

}
