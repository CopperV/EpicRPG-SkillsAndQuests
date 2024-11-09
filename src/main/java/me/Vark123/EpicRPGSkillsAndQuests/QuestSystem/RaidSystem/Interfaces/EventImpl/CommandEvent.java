package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidEvent;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@AllArgsConstructor
public class CommandEvent implements IRaidEvent {
	
	private String command;
	
	@Override
	public String getType() {
		return "COMMAND";
	}

	@Override
	public void doAction(Player p, PlayerRaidQuest pQuest, Object... args) {
		String w = pQuest.getWorld();
		String cmd = PlaceholderAPI.setPlaceholders(p, command).replace("[RAID_WORLD]", w);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

}
