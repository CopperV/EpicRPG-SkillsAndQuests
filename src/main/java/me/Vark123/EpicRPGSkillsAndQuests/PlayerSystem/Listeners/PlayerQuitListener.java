package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		save(p);
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		save(p);
	}
	
	private void save(Player p) {
		FileManager.savePlayer(p);
		DatabaseManager.savePlayerActiveQuests(p);
		PlayerManager.get().unregisterPlayer(p);
	}
	
}
