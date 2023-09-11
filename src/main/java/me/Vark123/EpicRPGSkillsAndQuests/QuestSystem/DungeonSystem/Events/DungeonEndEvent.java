package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;

@Getter
@AllArgsConstructor
public class DungeonEndEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private PlayerDungeonQuest dungeon;
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}