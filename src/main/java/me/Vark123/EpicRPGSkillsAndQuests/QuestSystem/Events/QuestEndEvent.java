package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;

@Getter
@AllArgsConstructor
public class QuestEndEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private APlayerQuest playerQuest;
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
