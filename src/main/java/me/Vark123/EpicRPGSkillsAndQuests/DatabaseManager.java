package me.Vark123.EpicRPGSkillsAndQuests;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

public final class DatabaseManager {

	private DatabaseManager() { }
	
	public static void init() {
		
	}
	
	//TODO
	public static Map<AQuest, Collection<PlayerTask>> getPlayerActiveQuests(Player player) {
		Map<AQuest, Collection<PlayerTask>> toReturn = new LinkedHashMap<>();
	
		return toReturn;
	}
	
	public static void savePlayerActiveQuests(Player player) {
		PlayerManager.get().getQuestPlayer(player)
			.ifPresent(qp -> {
				
			});
	}
	
}
