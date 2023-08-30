package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FindTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FishTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.KillTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.MobTalkTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PointsTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.TalkTask;

@Getter
public final class TaskManager {

	private static final TaskManager inst = new TaskManager();
	
	private TaskManager() { 
		
	}
	
	public static final TaskManager get() {
		return inst;
	}
	
	public ATask generateTask(AQuest quest, ConfigurationSection section) {
		
		String id = section.getString("id");
		String target;
		if(section.contains("targetName"))
			target = ChatColor.translateAlternateColorCodes('&', section.getString("targetName"));
		else
			target = "player";
		String message = ChatColor.translateAlternateColorCodes('&', section.getString("message"));
		
		ATask task;
		String type = section.getString("type");
		switch(type.toLowerCase()) {
			case "zabij":
				{
					int amount = section.getInt("ilosc");
					task = new KillTask(quest, id, target, message, amount);
				}
				break;
			case "przynies":
				{
					int amount = section.getInt("ilosc");
					task = new GiveTask(quest, id, target, message, amount);
				}
				break;
			case "zagadaj":
				{
					List<String> dialog = section.getStringList("dialog")
							.stream()
							.map(line -> ChatColor.translateAlternateColorCodes('&', line))
							.collect(Collectors.toList());
					task = new TalkTask(quest, id, target, message, dialog);
				}
				break;
			case "player":
				{
					int amount = section.getInt("ilosc");
					int level = section.getInt("level");
					task = new PlayerKillTask(quest, id, target, message, amount, level);
				}
				break;
			case "points":
				{
					int amount = section.getInt("ilosc");
					task = new PointsTask(quest, id, target, message, amount);
				}
				break;
			case "fishing":
				{
					int amount = section.getInt("ilosc");
					boolean inRow = section.getBoolean("inrow");
					task = new FishTask(quest, id, target, message, amount, inRow);
				}
				break;
			case "znajdz":
				{
					task = new FindTask(quest, id, target, message);
				}
				break;
			case "mobzagadaj":
				{
					List<String> dialog = section.getStringList("dialog")
							.stream()
							.map(line -> ChatColor.translateAlternateColorCodes('&', line))
							.collect(Collectors.toList());
					task = new MobTalkTask(quest, id, target, message, dialog);
				}
				break;
			default:
				return null;
		}
		
		return task;
	}
	
}
