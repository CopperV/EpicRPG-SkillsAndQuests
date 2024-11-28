package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidEvent;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@AllArgsConstructor
public class CommandListEvent implements IRaidEvent {

	private Collection<String> commands;
	
	@Override
	public String getType() {
		return "COMMAND_LIST";
	}

	@Override
	public void doAction(Player p, PlayerRaidQuest pQuest, Object... args) {
		String w = pQuest.getWorld();
		commands.stream()
			.map(cmd -> PlaceholderAPI.setPlaceholders(p, cmd))
			.map(cmd -> cmd.replace("[RAID_WORLD]", w))
			.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
	}

}
