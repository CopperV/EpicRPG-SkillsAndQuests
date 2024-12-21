package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Main;

@Getter
public class RaidMobManager implements Listener {

	private static final RaidMobManager inst = new RaidMobManager();
	
	private final Map<ActiveMob, String> storedMob;
	private final Map<ActiveMob, String> cleanerMob;
	
	private RaidMobManager() {
		storedMob = new ConcurrentHashMap<>();
		cleanerMob = new ConcurrentHashMap<>();
		
		startRaidMobManagerTask();
	}
	
	public static final RaidMobManager get() {
		return inst;
	}
	
	private void startRaidMobManagerTask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				storedMob.entrySet().stream()
					.filter(entry -> Bukkit.getWorld(entry.getValue()) == null)
					.map(entry -> entry.getKey())
					.collect(Collectors.toSet())
					.forEach(aMob -> {
						storedMob.remove(aMob);
						cleanerMob.remove(aMob);
					});
				
				Collection<ActiveMob> mobsToRemove = new HashSet<>();
				cleanerMob.keySet().stream()
					.filter(aMob -> MythicBukkit.inst().getMobManager().isActiveMob(aMob.getUniqueId()))
					.filter(aMob -> MythicBukkit.inst().getMobManager().getMobRegistry().isActiveMob(aMob.getUniqueId()))
					.forEach(aMob -> {
						mobsToRemove.add(MythicBukkit.inst().getMobManager().getActiveMob(aMob.getUniqueId()).get());
						storedMob.remove(aMob);
						cleanerMob.remove(aMob);
					});
				
				if(mobsToRemove != null && mobsToRemove.size() > 0)
					removeMobsTask(mobsToRemove);
			}
		}.runTaskTimerAsynchronously(Main.getInst(), 0, 20);
	}
	
	private void removeMobsTask(Collection<ActiveMob> mobs) {
		new BukkitRunnable() {
			@Override
			public void run() {
				mobs.forEach(aMob -> {
					aMob.remove();
				});
			}
		}.runTask(Main.getInst());
	}
	
	@EventHandler
	private void onMMSpawn(MythicMobSpawnEvent e) {
		if(e.isCancelled())
			return;
		
		ActiveMob mob = e.getMob();
		if(!(mob.getDespawnMode().equals(DespawnMode.PERSISTENT) 
				|| mob.getDespawnMode().equals(DespawnMode.NEVER)))
			return;
		
		String world = e.getLocation().getWorld().getName();
		if(!world.toLowerCase().contains("raid"))
			return;
		
		storedMob.put(mob, world);
	}
	
	@EventHandler
	private void onMMDeath(MythicMobDeathEvent e) {
		storedMob.remove(e.getMob());
		cleanerMob.remove(e.getMob());
	}
	
	@EventHandler
	private void onMMDespawn(MythicMobDespawnEvent e) {
		storedMob.remove(e.getMob());
		cleanerMob.remove(e.getMob());
	}
	
	@EventHandler
	private void onWorldUnload(WorldUnloadEvent e) {
		if(e.isCancelled())
			return;
		if(!e.getWorld().getName().toLowerCase().contains("raid"))
			return;
		
		String world = e.getWorld().getName();
		storedMob.entrySet().stream()
			.filter(entry -> entry.getValue().equals(world))
			.map(entry -> entry.getKey())
			.forEach(aMob -> {
				storedMob.remove(aMob);
				cleanerMob.remove(aMob);
			});
	}
	
}
