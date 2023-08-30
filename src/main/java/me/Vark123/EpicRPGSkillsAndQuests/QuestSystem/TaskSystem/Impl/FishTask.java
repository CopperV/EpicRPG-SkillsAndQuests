package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;

@Getter
@Setter
public class FishTask extends ATask {

	private int amount;
	private boolean inRow;
	
	public FishTask(AQuest quest, String id,
			String target, String message,
			int amount, boolean inRow) {
		super(quest, id, target, message);
		this.amount = amount;
		this.inRow = inRow;
	}

	@Override
	public void showInfo(CommandSender sender) {
		super.showInfo(sender);
		sender.sendMessage("§4§l» §aIlosc§7: "+amount);
		sender.sendMessage("§4§l» §aPod rzad§7: "+inRow);
		sender.sendMessage("§4§l» §aTyp zadania§7: "+this.getClass().getName());
	}

	@Override
	public String getProgess(PlayerTask pTask) {
		String isCompleted = pTask.isCompleted() ? "§aWykonane" : "§c"+pTask.getProgress()+"§7/§c"+amount;
		String msg = message.replace("%stan%", isCompleted);
		return  "§e"+msg;
	}

}
