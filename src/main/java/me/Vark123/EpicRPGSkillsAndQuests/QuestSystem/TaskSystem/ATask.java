package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem;

import org.bukkit.command.CommandSender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
@AllArgsConstructor
public abstract class ATask {

	protected AQuest quest;
	protected String id;
	@Setter
	protected String target;
	@Setter
	protected String message;
	
	public void showInfo(CommandSender sender) {
		sender.sendMessage("§4§l» §aID§7: "+id);
		sender.sendMessage("§4§l» §aCel§7: "+target);
		sender.sendMessage("§4§l» §aWiadomosc§7: "+message);
	}
	
	public abstract String getProgess(PlayerTask pTask);
	
}
