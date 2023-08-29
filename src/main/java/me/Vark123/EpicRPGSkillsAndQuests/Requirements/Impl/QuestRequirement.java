package me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl;

import java.util.Optional;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

//TODO
//Implementacja questow
public class QuestRequirement implements IRequirement {

	private String questId;
	
	public QuestRequirement(String questId) {
		this.questId = questId;
	}
	
	@Override
	public boolean checkRequirement(Player p) {
		return true;
	}

	@Override
	public String getRequirementInfo() {
		Optional<AQuest> quest = QuestManager.get().getQuestById(questId);
		if(quest.isEmpty())
			return "§cZadanie§r: BLAD [ZGLOS ADMINISTRATOROWI]";
		return "§cZadanie: §r"+quest.get().getDisplay()+"§r";
	}

}
