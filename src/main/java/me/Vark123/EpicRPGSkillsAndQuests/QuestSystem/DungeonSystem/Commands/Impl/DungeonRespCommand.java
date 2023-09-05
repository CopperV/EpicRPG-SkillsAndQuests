package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.Impl;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands.ADungeonCommand;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;

public class DungeonRespCommand extends ADungeonCommand {

	public DungeonRespCommand() {
		super("resp", new String[] {"odrodzenie"});
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
						if(dungeon.getStage() < ((DungeonQuest) dungeon.getQuest()).getUnlockResp()) {
							returnValue.setFalse();
							return;
						}
						if(dungeon.getPresentRespAmount() <= 0) {
							returnValue.setFalse();
							return;
						}
						if(DungeonController.get().getRespTasks().containsKey(player)) {
							returnValue.setFalse();
							return;
						}
					}, () -> returnValue.setFalse());
			}, () -> returnValue.setFalse());
		return returnValue.booleanValue();
	}

	@Override
	public boolean useCommand(Player p, String... args) {
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
		qp.getActiveQuests().values()
			.stream()
			.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
			.findFirst()
			.ifPresent(dungeon -> ((PlayerDungeonQuest) dungeon).createRespTask(qp));
		return true;
	}

	@Override
	public void showCorrectUsage(Player sender) {
		sender.sendMessage("  §f§p/dungeon odrodzenie §7- Przeteleportuj sie na dungeona uzywajac jeden z dostepnych punktow odrodzen");
	}

}
