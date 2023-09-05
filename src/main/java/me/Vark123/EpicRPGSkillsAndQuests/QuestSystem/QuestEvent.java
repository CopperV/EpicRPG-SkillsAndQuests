package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@AllArgsConstructor
public class QuestEvent {

	private TaskGroup taskGroup;
	private EventCall callType;
	private List<String> events;
	
	public void executeEvent(APlayerQuest pQuest) {
		Player p = pQuest.getPlayer();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			events.forEach(line -> {
				String newLine = line.replace("%player%", p.getName());
				if(newLine.contains("[DUNGEON_WORLD]"))
					newLine = newLine.replace("[DUNGEON_WORLD]", ((PlayerDungeonQuest) pQuest).getWorld());
				newLine = PlaceholderAPI.setPlaceholders(p, newLine);
				String[] event = newLine.split(": ");
				switch(event[0].toLowerCase()) {
				case "quest":
					String tmp[] = event[1].split(" ");
					switch(tmp[0].toLowerCase()) {
						case "back":
							if(!StringUtils.isNumeric(tmp[1]))
								return;
							int newStage = Integer.parseInt(tmp[1]);
							pQuest.changeQuestStage(newStage);
							break;
						case "remove":
							pQuest.removeQuest();
							break;
					}
					break;
				case "cmd":
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), event[1]);
					break;
				default:
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), newLine);
					break;
				}
			});
		});
		
	}
	
}
