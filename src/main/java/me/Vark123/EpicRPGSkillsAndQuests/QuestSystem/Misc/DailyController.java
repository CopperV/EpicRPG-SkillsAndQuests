package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import lombok.Getter;

@Getter
public final class DailyController {

	private static final DailyController inst = new DailyController();
	
	private final Collection<UUID> doneDaily;
	
	private DailyController() {
		doneDaily = new HashSet<>();
	}
	
	public static final DailyController get() {
		return inst;
	}
	
}
