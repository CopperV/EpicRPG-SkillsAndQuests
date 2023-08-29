package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class QuestManager {

	private static final QuestManager inst = new QuestManager();
	
	private final Collection<AQuest> quests;
	
	private QuestManager() {
		this.quests = new HashSet<>();
	}
	
	public static final QuestManager get() {
		return inst;
	}
	
	public void registerQuest(AQuest quest) {
		quests.add(quest);
	}
	
	public Optional<AQuest> getQuestById(String id) {
		return quests.stream()
				.filter(quest -> quest.getId().equals(id))
				.findFirst();
	}
	
}
