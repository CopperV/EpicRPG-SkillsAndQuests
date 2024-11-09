package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer.RaidPlayerInfo;

@Getter
public final class RaidManager {

	private static final RaidManager inst = new RaidManager();
	
	private final String raidPrefix = "§7[§x§e§7§5§b§1§f§lD§x§e§e§8§2§4§8§lU§x§f§6§a§9§7§2§lN§x§f§d§d§0§9§b§lG§x§e§a§9§d§7§9§lE§x§d§6§6§9§5§8§lO§x§c§3§3§6§3§6§lN§7]";
	
	private Map<UUID, RaidPlayer> raidPlayers = new LinkedHashMap<>();
	private final Map<Player, BukkitTask> respTasks = new ConcurrentHashMap<>();
	
	private RaidManager() { }
	
	public static final RaidManager get() { return inst; }
	
	public RaidPlayer getRaidPlayer(Player player) {
		UUID uid = player.getUniqueId();
		if(!raidPlayers.containsKey(uid))
			raidPlayers.put(uid, new RaidPlayer(player));
		return raidPlayers.get(uid);
	}
	
	public void prepareRaid(PlayerRaidQuest raidQuest, List<String> completedObjectives, List<String> completedGroups) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String targetWorld = raidQuest.getWorld();
				World raidWorld = Bukkit.getWorld(((RaidQuest) raidQuest.getQuest()).getWorld());
				File sourceDir = raidWorld.getWorldFolder();
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
							raidQuest.removeQuest();
							return;
						}
					}
				});
				
				copyThread.start();
				try {
					copyThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					raidQuest.removeQuest();
					return;
				}
				
				cleanThread.start();
				try {
					cleanThread.join();
				} catch (InterruptedException e) {
					raidQuest.removeQuest();
					return;
				}
				
				WorldCreator creator = new WorldCreator(targetWorld);
				creator.environment(raidWorld.getEnvironment());
				World newRaidWorld = creator.createWorld();
				newRaidWorld.setAutoSave(false);
				
				SpawnerManager manager = MythicBukkit.inst().getSpawnerManager();
				AbstractWorld aw = BukkitAdapter.adapt(newRaidWorld);
				raidQuest.setSpawners(((RaidQuest) raidQuest.getQuest()).getBaseSpawners()
						.parallelStream()
						.map(spawner -> {
							AbstractLocation source = spawner.getLocation();
							AbstractLocation target = new AbstractLocation(aw, source.getX(), source.getY(), source.getZ());
							String name = spawner.getName()+"-"+raidQuest.getRandomizedValue();
							manager.copySpawner(spawner.getName(), name, target);
							return manager.getSpawnerByName(name);
						})
						.collect(Collectors.toSet()));
				
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager source = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(raidWorld));
				RegionManager target = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(newRaidWorld));
				target.setRegions(source.getRegions());

				Collection<RaidObjective> _completedObjectives = ((RaidQuest) raidQuest.getQuest()).getObjectives()
						.values()
						.stream()
						.filter(objective -> completedObjectives.contains(objective.getId()))
						.collect(Collectors.toList());
				Collection<RaidGroup> _completedGroups = new LinkedList<>();
				Collection<RaidObjective> activeObjectives = new LinkedList<>();
				Collection<RaidGroup> activeGroups = new LinkedList<>();
				((RaidQuest) raidQuest.getQuest()).getObjectives().values().stream()
					.forEach(objective -> objective.getTaskGroups().stream()
							.forEach(groupList -> groupList.stream()
									.filter(group -> completedGroups.contains(group.getId()))
									.forEach(_completedGroups::add))
					);
				raidQuest.getTasks().stream()
					.map(pTask -> pTask.getTask().getTaskGroup())
					.map(group -> (RaidGroup) group)
					.forEach(group -> {
						if(!activeObjectives.contains(group.getObjective()))
							activeObjectives.add(group.getObjective());
						if(!activeGroups.contains(group))
							activeGroups.add(group);
					});
				
				_completedObjectives.stream()
					.forEachOrdered(objective -> {
						if(objective.getEvents().containsKey(ERaidEventType.START))
							objective.getEvents().get(ERaidEventType.START)
								.forEach(event -> event.doAction(raidQuest.getPlayer(), raidQuest));
						
						_completedGroups.stream()
							.filter(group -> group.getObjective().equals(objective))
							.forEach(group -> {
								group.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(raidQuest));
								group.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(raidQuest));
								group.getEventsByType(EventCall.COMPLETE).ifPresent(event -> event.executeEvent(raidQuest));
							});
						
						if(objective.getEvents().containsKey(ERaidEventType.END))
							objective.getEvents().get(ERaidEventType.END)
								.forEach(event -> event.doAction(raidQuest.getPlayer(), raidQuest));
					});
				activeObjectives.stream()
					.forEachOrdered(objective -> {
						if(objective.getEvents().containsKey(ERaidEventType.START))
							objective.getEvents().get(ERaidEventType.START)
								.forEach(event -> event.doAction(raidQuest.getPlayer(), raidQuest));
						
						_completedGroups.stream()
							.filter(group -> group.getObjective().equals(objective))
							.forEach(group -> {
								group.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(raidQuest));
								group.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(raidQuest));
								group.getEventsByType(EventCall.COMPLETE).ifPresent(event -> event.executeEvent(raidQuest));
							});
						activeGroups.stream()
							.filter(group -> group.getObjective().equals(objective))
							.forEach(group -> {
								group.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(raidQuest));
							});
					});
				
				raidQuest.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §eRajd zostal stworzony");
				raidQuest.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §eDolacz na niego przy pomocy komendy §f§o/rajd");
				
				raidQuest.setCanJoin(true);
				raidQuest.setStartTime(new Date().getTime());
			}
		}.runTask(Main.getInst());
	}
	
	public void clearRaid(PlayerRaidQuest raidQuest) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				World mainWorld = Bukkit.getWorld("F_RPG");
				Location mainLoc = mainWorld.getSpawnLocation();
				World raidWorld = Bukkit.getWorld(raidQuest.getWorld());
				
				raidWorld.getPlayers().forEach(player -> player.teleport(mainLoc));

				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionManager target = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(raidWorld));
				
				Collection<String> regionList = target.getRegions().keySet();
				regionList.forEach(region -> target.removeRegion(region));
				
				SpawnerManager manager = MythicBukkit.inst().getSpawnerManager();
				raidQuest.getSpawners().forEach(spawner -> {
					spawner.getAssociatedMobs().stream()
						.map(uid -> MythicBukkit.inst().getMobManager().getActiveMob(uid))
						.filter(mob -> mob.isPresent())
						.map(mob -> mob.get())
						.forEach(mob -> mob.remove());
					manager.removeSpawner(spawner);
				});
				
				raidWorld.getEntities().stream()
					.filter(entity -> !(entity instanceof Player))
					.forEach(entity -> entity.remove());

				raidQuest.sendMessage(raidPrefix+" §eRajd §r"+raidQuest.getQuest().getDisplay()+" §ezostal opuszczony!");

				Bukkit.getServer().unloadWorld(raidWorld, false);
				
				Thread deleteThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtils.deleteDirectory(raidWorld.getWorldFolder());
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
	
	public RaidPlayer loadPlayer(Player player) {
		UUID uid = player.getUniqueId();
		if(raidPlayers.containsKey(uid))
			return raidPlayers.get(uid);
		
		File f = FileManager.getRaidController();
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(f);
		
		RaidPlayer raidPlayer;
		if(!fYml.contains(uid.toString()) || !fYml.isConfigurationSection(uid.toString())) {
			raidPlayer = new RaidPlayer(player);
		} else {
			ConfigurationSection pSection = fYml.getConfigurationSection(uid.toString());
			List<RaidPlayerInfo> raidInfos = new LinkedList<>();
			if(pSection.contains("raids") && pSection.isConfigurationSection("raids")) {
				ConfigurationSection raidsSection = pSection.getConfigurationSection("raids");
				raidsSection.getKeys(false).stream()
					.filter(raidsSection::isConfigurationSection)
					.map(raidsSection::getConfigurationSection)
					.forEach(section -> {
						String raidId = section.getName();
						List<String> guaranteedDrops = section.getStringList("guaranteed-drops");
						List<String> dropped = section.getStringList("dropped");
						List<String> completedObjectives = section.getStringList("completed.objectives");
						List<String> completedGroups = section.getStringList("completed.groups");
						raidInfos.add(new RaidPlayerInfo(raidId, guaranteedDrops, dropped, completedObjectives, completedGroups));
					});
			}
			raidPlayer = new RaidPlayer(player, raidInfos);
		}
		
		raidPlayers.put(uid, raidPlayer);
		return raidPlayer;
	}
	
	public void savePlayer(Player player) {
		UUID uid = player.getUniqueId();
		if(!raidPlayers.containsKey(uid))
			return;
		
		RaidPlayer raidPlayer = raidPlayers.get(uid);
		savePlayer(raidPlayer);
	}
	
	public void savePlayer(RaidPlayer raidPlayer) {
		File f = FileManager.getRaidController();
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(f);
		
		Player p = raidPlayer.getPlayer();
		String uid = p.getUniqueId().toString();
		fYml.set(uid+".last-nick", p.getName());
		raidPlayer.getRaidInfo().forEach(raidInfo -> {
			fYml.set(uid+".raids."+raidInfo.getRaidId(), null);
			fYml.set(uid+".raids."+raidInfo.getRaidId()+".guaranteed-drops", 
					raidInfo.getGuaranteedDrops());
			fYml.set(uid+".raids."+raidInfo.getRaidId()+".dropped", 
					raidInfo.getDropped());
			fYml.set(uid+".raids."+raidInfo.getRaidId()+".completed.objectives", 
					raidInfo.getCompletedObjectives());
			fYml.set(uid+".raids."+raidInfo.getRaidId()+".completed.groups", 
					raidInfo.getCompletedGroups());
		});
		
		try {
			fYml.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Optional<RaidPlayer> removePlayer(Player player) {
		UUID uid = player.getUniqueId();
		RaidPlayer raidPlayer = raidPlayers.remove(uid);
		return Optional.ofNullable(raidPlayer);
	}
	
}
