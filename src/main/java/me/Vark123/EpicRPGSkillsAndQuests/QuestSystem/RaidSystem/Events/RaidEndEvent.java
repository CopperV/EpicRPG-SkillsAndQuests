package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

@Getter
@AllArgsConstructor
public class RaidEndEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private PlayerRaidQuest dungeon;
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}