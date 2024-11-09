package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.EnumSet;

public enum ERaidEventType {

	
	START,
	END;
	
	private static final EnumSet<ERaidEventType> EventTypes = EnumSet.allOf(ERaidEventType.class);
	public static boolean isEnumValue(String value) {
		return EventTypes.stream()
				.anyMatch(enumValue -> enumValue.name().equalsIgnoreCase(value));
	}
	
}
