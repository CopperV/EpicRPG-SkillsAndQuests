package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.ARaidCommand;

public class RaidLeaveCommand extends ARaidCommand {

	public RaidLeaveCommand() {
		super("leave", new String[] {"opusc"});
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
			.map(pQuest -> (PlayerRaidQuest) pQuest)
			.findFirst()
			.ifPresent(raid -> {
				if(raid.getParty().isEmpty() || 
						(raid.getParty().isPresent() && raid.getParty().get().getLeader().getPlayer()
						.getUniqueId().equals(player.getUniqueId()))) {
					raid.removeQuest();
				} else {
					qp.getActiveQuests().remove(raid.getQuest());
					World raidWorld = Bukkit.getWorld(raid.getWorld());
					if(raidWorld.getName().equals(player.getWorld().getName())) {
						World mainWorld = Bukkit.getWorld("F_RPG");
						Location mainLoc = mainWorld.getSpawnLocation();
						player.teleport(mainLoc);
					}
					raid.performAction(_p -> {
						if(_p.getUniqueId().equals(player.getUniqueId())) {
							_p.sendMessage(RaidManager.get().getRaidPrefix()+
									" §eOpusciles rajd §r"+raid.getQuest().getDisplay());
						} else {
							_p.sendMessage(RaidManager.get().getRaidPrefix()+
									" §7"+player.getName()+" §eopuscil rajd §r"+raid.getQuest().getDisplay());
						}
					});
				}
			});
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §f§p/raid opusc §7- Zrezygnuj ze swojego udzialu w rajdzie oraz calej druzyny, jesli jestes jej liderem");
	}

}
