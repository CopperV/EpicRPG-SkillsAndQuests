package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;

@Getter
public final class DungeonController {

	private static final DungeonController inst = new DungeonController();
	
	private final String dungeonPrefix = "§7[§x§e§7§5§b§1§f§lD§x§e§e§8§2§4§8§lU§x§f§6§a§9§7§2§lN§x§f§d§d§0§9§b§lG§x§e§a§9§d§7§9§lE§x§d§6§6§9§5§8§lO§x§c§3§3§6§3§6§lN§7]";
	
	private final Map<Player, BukkitTask> respTasks;
	
	private DungeonController() {
		respTasks = new ConcurrentHashMap<>();
	}
	
	public static final DungeonController get() {
		return inst;
	}
	
	public void prepareDungeon(PlayerDungeonQuest dungeonQuest) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String targetWorld = dungeonQuest.getWorld();
				World dungeonWorld = Bukkit.getWorld(((DungeonQuest) dungeonQuest.getQuest()).getWorld());
				File sourceDir = dungeonWorld.getWorldFolder();
				File targetDir = new File(sourceDir.getParent(), targetWorld);
				
				Thread copyThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtils.copyDirectory(sourceDir, targetDir);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				Thread cleanThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							File uid = new File(Bukkit.getWorldContainer().getCanonicalPath()+"/"+targetWorld+"/uid.dat");
							uid.delete();
							File session1 = new File(Bukkit.getWorldContainer().getCanonicalPath()+"/"+targetWorld+"session.dat");
							if(session1.exists())
								session1.delete();
							File session2 = new File(Bukkit.getWorldContainer().getCanonicalPath()+"/"+targetWorld+"session.lock");
							if(session2.exists())
								session2.delete();
							File poi = new File(Bukkit.getWorldContainer().getCanonicalPath()+"/"+targetWorld+"/poi");
							FileUtils.deleteDirectory(poi);
						} catch (IOException e) {
							e.printStackTrace();
							dungeonQuest.removeQuest();
							return;
						}
					}
				});
				
				copyThread.start();
				try {
					copyThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					dungeonQuest.removeQuest();
					return;
				}
				
				cleanThread.start();
				try {
					cleanThread.join();
				} catch (InterruptedException e) {
					dungeonQuest.removeQuest();
					return;
				}
				
				WorldCreator creator = new WorldCreator(targetWorld);
				creator.environment(dungeonWorld.getEnvironment());
				World newDungeonWorld = creator.createWorld();
				newDungeonWorld.setAutoSave(false);
				
				SpawnerManager manager = MythicBukkit.inst().getSpawnerManager();
				AbstractWorld aw = BukkitAdapter.adapt(newDungeonWorld);
				dungeonQuest.setSpawners(((DungeonQuest) dungeonQuest.getQuest()).getBaseSpawners()
						.parallelStream()
						.map(spawner -> {
							AbstractLocation source = spawner.getLocation();
							AbstractLocation target = new AbstractLocation(aw, source.getX(), source.getY(), source.getZ());
							String name = spawner.getName()+"-"+dungeonQuest.getRandomizedValue();
							manager.copySpawner(spawner.getName(), name, target);
							return manager.getSpawnerByName(name);
						})
						.collect(Collectors.toSet()));
				
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager source = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(dungeonWorld));
				RegionManager target = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(newDungeonWorld));
				target.setRegions(source.getRegions());
				
				dungeonQuest.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §eDungeon zostal stworzony");
				dungeonQuest.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §eDolacz na niego przy pomocy komendy §f§o/dungeon");
				
				dungeonQuest.setCanJoin(true);
			}
		}.runTask(Main.getInst());
		
	}
	
	public void clearDungeon(PlayerDungeonQuest dungeonQuest) {
		new BukkitRunnable() {
			@Override
			public void run() {
				World mainWorld = Bukkit.getWorld("F_RPG");
				Location mainLoc = mainWorld.getSpawnLocation();
				World dungeonWorld = Bukkit.getWorld(dungeonQuest.getWorld());
				
				dungeonWorld.getPlayers().forEach(player -> player.teleport(mainLoc));

				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager target = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(dungeonWorld));
				
				Collection<String> regionList = target.getRegions().keySet();
				regionList.forEach(region -> target.removeRegion(region));
				
				SpawnerManager manager = MythicBukkit.inst().getSpawnerManager();
				dungeonQuest.getSpawners().forEach(spawner -> {
					spawner.getAssociatedMobs().stream()
						.map(uid -> MythicBukkit.inst().getMobManager().getActiveMob(uid))
						.filter(mob -> mob.isPresent())
						.map(mob -> mob.get())
						.forEach(mob -> mob.remove());
					manager.removeSpawner(spawner);
				});
				
				dungeonWorld.getEntities().stream()
					.filter(entity -> !(entity instanceof Player))
					.forEach(entity -> entity.remove());

				dungeonQuest.sendMessage(dungeonPrefix+" §eDungeon §r"+dungeonQuest.getQuest().getDisplay()+" §ezostal opuszczony!");

				Bukkit.getServer().unloadWorld(dungeonWorld, false);
				
				Thread deleteThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtils.deleteDirectory(dungeonWorld.getWorldFolder());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				
				deleteThread.start();
				try {
					deleteThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.runTask(Main.getInst());
	}
	
}
