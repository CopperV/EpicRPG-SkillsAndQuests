package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;

@Getter
@AllArgsConstructor
public class GiveTaskEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private PlayerTask task;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
