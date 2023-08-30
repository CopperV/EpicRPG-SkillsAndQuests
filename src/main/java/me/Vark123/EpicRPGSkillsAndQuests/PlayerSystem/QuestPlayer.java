package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import java.util.Collection;
import java.util.Map;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class QuestPlayer {

	private Player player;
	private Collection<String> completedQuests;
	private Map<AQuest, Collection<PlayerTask>> activeQuests;
	
}
