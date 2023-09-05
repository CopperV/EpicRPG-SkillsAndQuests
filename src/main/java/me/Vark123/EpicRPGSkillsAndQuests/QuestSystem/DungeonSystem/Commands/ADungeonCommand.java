package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Commands;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class ADungeonCommand {

	protected String cmd;
	protected String[] aliases;
	
	public abstract boolean canUse(Player player);
	public abstract boolean useCommand(Player sender, String... args);
	public abstract void showCorrectUsage(Player sender);

}
