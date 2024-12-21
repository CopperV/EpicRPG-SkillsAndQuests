package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension.Conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import io.lumine.mythic.core.skills.SkillCondition;

public class DespawnStateRaidCondition extends SkillCondition implements IEntityCondition {

	private PlaceholderString despawnState;
	
	public DespawnStateRaidCondition(String line, MythicLineConfig config) {
		super(line);
		despawnState = config.getPlaceholderString(new String[] { "state", "despawn", "ds", "despawnstate" }, "NORMAL");
	}

	@Override
	public boolean check(AbstractEntity arg0) {
		if(!MythicBukkit.inst().getMobManager().isActiveMob(arg0))
			return false;
		
		DespawnMode despawnMode = DespawnMode.get(despawnState.get(arg0).toUpperCase());
		if(despawnMode == null)
			return false;
		ActiveMob aMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(arg0);
		
		return aMob.getDespawnMode().equals(despawnMode);
	}

}
