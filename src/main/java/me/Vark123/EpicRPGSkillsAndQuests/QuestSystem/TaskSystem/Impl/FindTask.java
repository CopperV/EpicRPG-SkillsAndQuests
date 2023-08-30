package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
@Setter
public class FindTask extends ATask {
	
	public FindTask(AQuest quest, String id,
			String target, String message) {
		super(quest, id, target, message);
	}

	@Override
	public void showInfo(CommandSender sender) {
		super.showInfo(sender);
		sender.sendMessage("§4§l» §aTyp zadania§7: "+this.getClass().getName());
	}

	@Override
	public String getProgess(PlayerTask pTask) {
		String isCompleted = pTask.isCompleted() ? "§aWykonane" : "§cNiewykonane";
		String msg = message.replace("%stan%", isCompleted);
		return  "§e"+msg;
	}

}
