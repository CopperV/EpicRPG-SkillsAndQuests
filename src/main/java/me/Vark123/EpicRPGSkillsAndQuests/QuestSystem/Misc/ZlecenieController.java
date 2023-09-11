package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Main;

@Getter
public final class ZlecenieController {

	private static final ZlecenieController inst = new ZlecenieController();
	
	private final Collection<UUID> zlecenieCooldown;
	
	private ZlecenieController() {
		zlecenieCooldown = new HashSet<>();
	}
	
	public static final ZlecenieController get() {
		return inst;
	}
	
	public void addZlecenieCooldown(Player p, long duration) {
		UUID uid = p.getUniqueId();
		zlecenieCooldown.add(uid);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				zlecenieCooldown.remove(uid);
				Player player = Bukkit.getPlayer(uid);
				if(player == null || !player.isOnline())
					return;
				player.sendTitle("§e§l ", "§e§lMOZESZ WZIAC KOLEJNE ZLECENIE", 5, 10, 15);
				player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.2f);
				player.spawnParticle(Particle.NAUTILUS, player.getLocation().add(0,1.25,0), 25, 0.75, 0.75, 0.75, 0.15);
			}
		}.runTaskLaterAsynchronously(Main.getInst(), duration);
	}
	
}
