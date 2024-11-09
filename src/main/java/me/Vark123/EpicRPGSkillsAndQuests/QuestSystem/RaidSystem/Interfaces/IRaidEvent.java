package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public interface IRaidEvent {

	public String getType();
	public void doAction(Player p, PlayerRaidQuest pQuest, Object... args);
	
}
