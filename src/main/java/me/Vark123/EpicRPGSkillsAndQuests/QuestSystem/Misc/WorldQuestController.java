package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerWorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
@EqualsAndHashCode
public class WorldQuestController {
	
	private static final WorldQuestController inst = new WorldQuestController();
	
	private final Collection<String> completedQuests;
	private final Map<AQuest, PlayerWorldQuest> activeQuests;
	
	private WorldQuestController() {
		completedQuests = new ArrayList<>();
		activeQuests = DatabaseManager.getActiveWorldQuests();
	}
	
	public static final WorldQuestController get() {
		return inst;
	}
	
}
