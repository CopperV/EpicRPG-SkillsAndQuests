package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.RuleImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidObjective;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidObjectiveRule;

@Getter
public class ObjectiveCompleteRule implements IRaidObjectiveRule {

	private RaidObjective objective;
	private int minGroups = -1;
	private boolean skip = false;
	private List<String> mustCompleted = new ArrayList<>();
	private List<String> objectiveLastGroups = new ArrayList<>();
	
	@Override
	public String getType() {
		return "OBJECTIVE_COMPLETE_RULE";
	}

	public ObjectiveCompleteRule(RaidObjective objective, int minGroups, boolean skip, List<String> mustCompleted) {
		super();
		this.objective = objective;
		this.minGroups = minGroups;
		this.skip = skip;
		this.mustCompleted = mustCompleted;
		
		objectiveLastGroups.addAll(objective.getLastGroups()
				.stream()
				.map(group -> group.getId())
				.collect(Collectors.toList()));
	}

	@Override
	public boolean checkRule(PlayerRaidQuest pQuest, Object... args) {
		List<String> completedGroups = pQuest.getCompletedGroups();
		long sizeOfCompletedGroups = completedGroups.stream()
				.filter(objectiveLastGroups::contains)
				.count();
		if(sizeOfCompletedGroups < objectiveLastGroups.size() && sizeOfCompletedGroups < minGroups)
			return false;
		
		MutableBoolean checkResult = new MutableBoolean(true);
		mustCompleted.stream()
			.filter(group -> !completedGroups.contains(group))
			.findAny().ifPresent(group -> checkResult.setFalse());
		return checkResult.booleanValue();
	}

	@Override
	public void applyConsequences(PlayerRaidQuest pQuest, Object... args) {
		RaidQuest raid = objective.getRaidQuest();
		RaidObjective nextObjective = raid.getNextObjective(objective).orElse(null);
		
		if(skip)
			pQuest.endObjective(objective);
		if(nextObjective != null)
			pQuest.startObjective(nextObjective);
	}

}
