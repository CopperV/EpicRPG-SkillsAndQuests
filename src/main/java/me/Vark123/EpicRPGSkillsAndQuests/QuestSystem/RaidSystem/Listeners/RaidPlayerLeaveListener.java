package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

public class RaidPlayerLeaveListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		savePlayer(e.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		savePlayer(e.getPlayer());
	}
	
	private void savePlayer(Player player) {
		RaidManager.get().removePlayer(player)
			.ifPresent(RaidManager.get()::savePlayer);
	}
	
}
