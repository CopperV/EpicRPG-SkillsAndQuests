package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces;

import org.bukkit.entity.Player;

public interface IRaidDropTable {

	public double getChance();
	public boolean isLimited();
	public boolean isGuarantable();
	public void dropToPlayer(Player p);
	
}
