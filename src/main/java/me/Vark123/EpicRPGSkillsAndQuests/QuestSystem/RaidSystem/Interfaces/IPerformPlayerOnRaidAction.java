package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public interface IPerformPlayerOnRaidAction {

	public void action(Player p, PlayerRaidQuest raidQuest);
	
}
