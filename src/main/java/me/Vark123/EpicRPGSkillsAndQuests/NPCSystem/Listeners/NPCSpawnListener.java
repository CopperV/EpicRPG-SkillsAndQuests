package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.Vark123.EpicNPC.Core.EpicNPC;
import me.Vark123.EpicNPC.Events.EpicNpcSpawnEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;

public class NPCSpawnListener implements Listener {

	@EventHandler
	public void onSpawn(EpicNpcSpawnEvent e) {
		Player p = e.getPlayer();
		EpicNPC npc = e.getNpc();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			npc.getShowQuest().ifPresent(questId -> {
				if(!qp.getCompletedQuests().contains(questId))
					e.setCancelled(true);
			});
			npc.getHideQuest().ifPresent(questId -> {
				if(qp.getCompletedQuests().contains(questId))
					e.setCancelled(true);
			});
		});
	}
	
}
