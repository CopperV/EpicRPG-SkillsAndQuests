package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
@Setter
public class PointsTask extends ATask {

	private int amount;
	
	public PointsTask(AQuest quest, String id,
			String target, String message,
			int amount) {
		super(quest, id, target, message);
		this.amount = amount;
	}

	@Override
	public void showInfo(CommandSender sender) {
		super.showInfo(sender);
		sender.sendMessage("§4§l» §aIlosc§7: "+amount);
		sender.sendMessage("§4§l» §aTyp zadania§7: "+this.getClass().getName());
	}

	@Override
	public String getProgess(PlayerTask pTask) {
		String isCompleted = pTask.isCompleted() ? "§aWykonane" : "§c"+pTask.getIntProgress()+"§7/§c"+amount;
		String msg = message.replace("%stan%", isCompleted);
		return  "§e"+msg;
	}

}
