package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicParty.PlayerPartySystem.Events.PartyKickEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class RaidPartyKickListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PartyKickEvent e) {
		if(e.isCancelled())
			return;

		
		PartyPlayer member = e.getOldMember();
		Player p = member.getPlayer();
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.findAny()
				.ifPresent(raid -> {
					qp.getActiveQuests().remove(raid.getQuest());
					if(p.isOnline() && p.getWorld().getName().equals(raid.getWorld())) {
						World mainWorld = Bukkit.getWorld("F_RPG");
						Location mainLoc = mainWorld.getSpawnLocation();
						p.teleport(mainLoc);
					}
				});
		});
	}

}
