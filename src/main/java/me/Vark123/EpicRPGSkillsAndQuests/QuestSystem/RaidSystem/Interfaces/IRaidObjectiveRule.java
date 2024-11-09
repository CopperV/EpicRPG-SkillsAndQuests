package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public interface IRaidObjectiveRule {

	public String getType();
	public boolean checkRule(PlayerRaidQuest pQuest, Object... args);
	public void applyConsequences(PlayerRaidQuest pQuest, Object... args);
	
}
