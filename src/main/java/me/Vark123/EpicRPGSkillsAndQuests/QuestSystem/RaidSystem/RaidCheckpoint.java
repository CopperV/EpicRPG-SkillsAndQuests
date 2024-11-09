package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RaidCheckpoint {
	private String targetGroup;
	private List<String> commands;
	private List<String> blockers;
}
