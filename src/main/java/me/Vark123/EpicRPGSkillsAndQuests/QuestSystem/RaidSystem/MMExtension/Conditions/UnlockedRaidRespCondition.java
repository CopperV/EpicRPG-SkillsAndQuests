package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension.Conditions;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class UnlockedRaidRespCondition extends SkillCondition implements IEntityCondition {

	public UnlockedRaidRespCondition(String line, MythicLineConfig config) {
		super(line);
	}

	@Override
	public boolean check(AbstractEntity arg0) {
		Entity entity = arg0.getBukkitEntity();
		if(!(entity instanceof Player))
			return false;
		MutableBoolean result = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer((Player) entity)
			.ifPresentOrElse(qp -> {
				qp.getActiveQuests().values()
					.stream()
					.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
					.map(pQuest -> (PlayerRaidQuest) pQuest)
					.filter(pQuest -> entity.getWorld().getName().equals(pQuest.getWorld()))
					.filter(pQuest -> !pQuest.isRespFlag())
					.findAny()
					.ifPresentOrElse(pQuest -> { }, 
							() -> result.setFalse());
			}, () -> result.setFalse());
		return result.booleanValue();
	}

}
