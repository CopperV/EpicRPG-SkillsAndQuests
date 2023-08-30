package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import java.util.Collection;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
@AllArgsConstructor
public class PlayerQuest {

	private Player player;
	private AQuest quest;
	private int stage;
	@Setter
	private Collection<PlayerTask> tasks;
	
}
