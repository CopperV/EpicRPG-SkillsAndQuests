package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class RaidBossDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onDeath(MythicMobDeathEvent e) {
		MythicMob mMob = e.getMobType();
		String mmId = mMob.getInternalName();
		
		e.getEntity().getWorld().getPlayers().stream()
			.map(PlayerManager.get()::getQuestPlayer)
			.filter(qp -> qp.isPresent())
			.map(qp -> qp.get())
			.flatMap(qp -> qp.getActiveQuests().values().stream())
			.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
			.map(pQuest -> (PlayerRaidQuest) pQuest)
			.findAny()
			.ifPresent(pQuest -> pQuest.generateDrops(mmId));
			
	}
	
}
