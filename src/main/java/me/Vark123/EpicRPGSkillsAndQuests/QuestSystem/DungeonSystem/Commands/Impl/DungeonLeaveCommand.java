package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Player;

import me.Vark123.EpicParty.EpicPartyAPI;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.ADungeonCommand;

public class DungeonLeaveCommand extends ADungeonCommand {

	public DungeonLeaveCommand() {
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
					.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
					.map(pQuest -> (PlayerDungeonQuest) pQuest)
					.findAny()
					.ifPresentOrElse(dungeon -> {
						if(!dungeon.isCanJoin()) {
							returnValue.setFalse();
							return;
						}
						EpicPartyAPI.get().getPlayerManager().getPartyPlayer(player)
							.ifPresentOrElse(pp -> {
								pp.getParty().ifPresent(party -> {
									returnValue.setValue(party.getLeader().equals(pp));
								});
							}, () -> returnValue.setFalse());
					}, () -> returnValue.setFalse());
			}, () -> returnValue.setFalse());
		return returnValue.booleanValue();
	}

	@Override
	public boolean useCommand(Player p, String... args) {
		PlayerManager.get().getQuestPlayer(p).get().getActiveQuests().values()
			.stream()
			.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
			.findFirst()
			.ifPresent(dungeon -> dungeon.removeQuest());
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §f§p/dungeon opusc §7- Zrezygnuj z obecnie wykonywanego dungeona");
	}

}
