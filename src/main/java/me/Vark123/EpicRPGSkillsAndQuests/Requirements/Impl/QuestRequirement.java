package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Optional;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class QuestRequirement implements IRequirement {

	private String questId;
	
	public QuestRequirement(String questId) {
		this.questId = questId;
	}
	
	@Override
	public boolean checkRequirement(Player p) {
		Optional<AQuest> quest = QuestManager.get().getQuestById(questId);
		Optional<QuestPlayer> qp = PlayerManager.get().getQuestPlayer(p);
		if(quest.isEmpty() || qp.isEmpty())
			return false;
		return qp.get().getCompletedQuests().contains(quest.get().getId());
	}

	@Override
	public String getRequirementInfo() {
		Optional<AQuest> quest = QuestManager.get().getQuestById(questId);
		if(quest.isEmpty())
			return "§cZadanie§r: BLAD [ZGLOS ADMINISTRATOROWI]";
		return "§cZadanie: §r"+quest.get().getDisplay()+"§r";
	}

}
