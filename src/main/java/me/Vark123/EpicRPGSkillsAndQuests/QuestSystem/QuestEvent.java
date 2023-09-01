package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuest;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@AllArgsConstructor
public class QuestEvent {

	private TaskGroup taskGroup;
	private EventCall callType;
	private List<String> events;
	
	public void executeEvent(PlayerQuest pQuest) {
		Player p = pQuest.getPlayer();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			events.forEach(line -> {
				String newLine = PlaceholderAPI.setPlaceholders(p, line);
				String[] event = newLine.split(": ");
				
				switch(event[0].toLowerCase()) {
				case "quest":
					String tmp[] = event[1].split(" ");
					switch(tmp[1].toLowerCase()) {
						case "back":
							if(!StringUtils.isNumeric(tmp[2]))
								return;
							int newStage = Integer.parseInt(tmp[2]);
							qp.degradeQuest(pQuest.getQuest(), newStage);
							break;
						case "remove":
							qp.removeQuest(pQuest.getQuest());
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
