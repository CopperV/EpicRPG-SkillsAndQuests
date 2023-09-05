package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.ADungeonCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;

public class DungeonJoinCommand extends ADungeonCommand {

	public DungeonJoinCommand() {
		super("join", new String[] {"dolacz"});
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
						if(dungeon.getStage() >= ((DungeonQuest) dungeon.getQuest()).getUnlockResp()) {
							returnValue.setFalse();
							return;
						}
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
			.ifPresent(dungeon -> ((PlayerDungeonQuest) dungeon).resp(p));
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §f§p/dungeon dolacz §7- Dolacz do dungeona natychmiastowo, nie zuzywajac zapasu odrodzen!");
	}

}
