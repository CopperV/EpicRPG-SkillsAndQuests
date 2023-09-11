package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl;

import java.util.List;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
public class MobTalkTask extends ATask {
	
	private List<String> dialog;
	
	public MobTalkTask(AQuest quest, String id,
			String target, String message,
			List<String> dialog) {
		super(quest, id, target, message);
		this.dialog = dialog;
	}

	@Override
	public void showInfo(CommandSender sender) {
		super.showInfo(sender);
		sender.sendMessage("§4§l» §aDialog§7: ");
		dialog.stream().forEachOrdered(line -> {
			sender.sendMessage("    §4§l§4§l» §r"+line);
		});
		sender.sendMessage("§4§l» §aTyp zadania§7: "+this.getClass().getName());
	}

	@Override
	public String getProgess(PlayerTask pTask) {
		String isCompleted = pTask.isCompleted() ? "§aWykonane" : "§cNiewykonane";
		String msg = message.replace("%stan%", isCompleted);
		return  "§e"+msg;
	}

}
