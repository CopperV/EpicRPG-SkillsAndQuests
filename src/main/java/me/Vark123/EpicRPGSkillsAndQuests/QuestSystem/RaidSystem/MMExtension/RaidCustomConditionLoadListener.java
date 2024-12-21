package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.core.skills.SkillCondition;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension.Conditions.DespawnStateRaidCondition;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension.Conditions.UnlockedRaidRespCondition;

public class RaidCustomConditionLoadListener implements Listener {

	@EventHandler
	public void onConditionLoad(MythicConditionLoadEvent e) {
		String conditioner = e.getConditionName().toLowerCase();
		SkillCondition condition;
		switch(conditioner) {
			case "unlockedraidresp":
				condition = new UnlockedRaidRespCondition(e.getConfig().getLine(), e.getConfig());
				e.register(condition);
				break;
			case "despawnstate":
				condition = new DespawnStateRaidCondition(e.getConfig().getLine(), e.getConfig());
				e.register(condition);
				break;
		}
	}
	
}
