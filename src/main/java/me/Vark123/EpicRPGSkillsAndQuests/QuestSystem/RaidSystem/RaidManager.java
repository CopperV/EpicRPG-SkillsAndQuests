package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;
import lombok.Getter;
import me.Vark123.EpicRPG.Utils.Utils;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer.RaidPlayerInfo;

@Getter
public final class RaidManager {

	private static final RaidManager inst = new RaidManager();
	
	private final String raidPrefix = "§7[§x§E§C§B§5§3§A§lR§x§E§9§C§8§5§0§lA§x§E§6§D§B§6§7§lJ§x§E§3§E§E§7§D§lD§7]";
	
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
	
	public void createNewRaid(Player p, RaidQuest raidQuest) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				RaidObjective objective = raidQuest.getObjectives().get(1);
				List<RaidGroup> startGroups = objective.getTaskGroups().stream()
						.map(list -> list.getFirst())
						.collect(Collectors.toList());
				List<PlayerTask> newTasks = new LinkedList<>();
				startGroups.stream()
					.map(taskGroup -> taskGroup.getTasks())
					.forEach(tasks -> tasks.stream()
						.map(task -> new PlayerTask(p, raidQuest, task, 0, false))
						.forEach(newTasks::add));
				
				PlayerRaidQuest pQuest = new PlayerRaidQuest(p, raidQuest, 1, newTasks);
				pQuest.performAction((_p) -> {
					QuestPlayer _qp = PlayerManager.get().getQuestPlayer(_p).get();
					_qp.getActiveQuests().put(raidQuest, pQuest);
					
					pQuest.getPlayerBossFightContainer().add(_p);
					
					RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
					rp.getRaidInfo().stream()
						.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
						.findAny()
						.ifPresentOrElse(raidInfo -> raidInfo.update(pQuest), 
								() -> rp.getRaidInfo().add(new RaidPlayerInfo(raidQuest)));
					
					if(_p.getUniqueId().equals(p.getUniqueId()))
						_p.sendTitle("§6§lROZPOCZALES RAJD", raidQuest.getDisplay(), 5, 10, 15);
					else
						_p.sendTitle("§6§lROZPOCZETO RAJD", raidQuest.getDisplay(), 5, 10, 15);
					_p.playSound(_p, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1);
					_p.spawnParticle(Particle.TOTEM, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
				});
				
				prepareRaid(pQuest, new LinkedList<>(), new LinkedList<>());
			}
		}.runTask(Main.getInst());
	}
	
	public void continueRaid(Player p, RaidQuest raidQuest, RaidObjective raidObjective) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				int objectiveId = raidQuest.getObjectives().entrySet()
						.stream()
						.filter(entry -> entry.getValue().equals(raidObjective))
						.map(entry -> entry.getKey())
						.findAny()
						.orElse(-1);
				if(objectiveId < 1) {
					createNewRaid(p, raidQuest);
					return;
				}
				List<RaidObjective> completedObjectives = raidQuest.getObjectives().entrySet()
						.stream()
						.filter(entry -> entry.getKey() < objectiveId)
						.map(entry -> entry.getValue())
						.collect(Collectors.toList());
				List<String> completedGroups = completedObjectives.stream()
						.flatMap(objective -> objective.getTaskGroups().stream())
						.flatMap(groupList -> groupList.stream())
						.map(group -> group.getId())
						.collect(Collectors.toList());
				List<String> strCompletedObjectives = completedObjectives.stream()
						.map(objective -> objective.getId())
						.collect(Collectors.toList());
				
				List<RaidGroup> startGroups = raidObjective.getTaskGroups()
						.stream()
						.map(groupList -> groupList.getFirst())
						.collect(Collectors.toList());
				List<PlayerTask> newTasks = new LinkedList<>();
				startGroups.stream()
					.map(taskGroup -> taskGroup.getTasks())
					.forEach(tasks -> tasks.stream()
						.map(task -> new PlayerTask(p, raidQuest, task, 0, false))
						.forEach(newTasks::add));
				
				PlayerRaidQuest pQuest = new PlayerRaidQuest(p, raidQuest, 1, newTasks);
				pQuest.getCompletedObjectives().clear();
				pQuest.getCompletedObjectives().addAll(strCompletedObjectives);
				pQuest.getCompletedGroups().clear();
				pQuest.getCompletedGroups().addAll(completedGroups);
				
				pQuest.performAction((_p) -> {
					QuestPlayer _qp = PlayerManager.get().getQuestPlayer(_p).get();
					_qp.getActiveQuests().put(raidQuest, pQuest);
					
					pQuest.getPlayerBossFightContainer().add(_p);
					
					RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
					rp.getRaidInfo().stream()
						.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
						.findAny()
						.ifPresentOrElse(raidInfo -> raidInfo.update(pQuest), 
								() -> {
									RaidPlayerInfo raidInfo = new RaidPlayerInfo(raidQuest);
									raidInfo.update(pQuest);
									rp.getRaidInfo().add(raidInfo);
								});
					
					if(_p.getUniqueId().equals(p.getUniqueId()))
						_p.sendTitle("§6§lKONTYNUUJESZ RAJD", raidQuest.getDisplay(), 5, 10, 15);
					else
						_p.sendTitle("§6§lKONTYNUACJA RAJDU", raidQuest.getDisplay(), 5, 10, 15);
					_p.playSound(_p, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1);
					_p.spawnParticle(Particle.TOTEM, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
				});
				
				prepareRaid(pQuest, strCompletedObjectives, completedGroups);
			}
		}.runTask(Main.getInst());
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
	
	public void openRaidConfigureMenu(Player player, RaidQuest raid) {
		RaidPlayer rp = loadPlayer(player);
		rp.getRaidInfo().stream()
			.filter(raidInfo -> raidInfo.getRaidId().equals(raid.getId()))
			.findAny()
			.ifPresentOrElse(raidInfo -> {
				List<Integer> completedObjectives = raidInfo.getCompletedObjectives()
						.stream()
						.map(objective -> raid.getObjectives().entrySet().stream()
								.filter(entry -> entry.getValue().getId().equals(objective))
								.filter(entry -> entry.getValue().getDisplay() != null)
								.map(entry -> entry.getKey())
								.findAny())
						.filter(objId -> objId.isPresent())
						.map(objId -> objId.get())
						.collect(Collectors.toList());
				int rows = (int) Utils.limitValue(1, 6, (completedObjectives.size() - 2) / 9 + 1);
				RyseInventory.builder()
					.title("§6§lWybierz etap rajdu")
					.rows(rows)
					.disableUpdateTask()
					.provider(new InventoryProvider() {
						@Override
						public void init(Player player, InventoryContents contents) {
							ItemStack startNew = new ItemStack(Material.TOTEM_OF_UNDYING);{
								ItemMeta im = startNew.getItemMeta();
								im.setDisplayName("§eZacznij od nowa");
								startNew.setItemMeta(im);
							}
							contents.set(0, IntelligentItem.of(startNew, e -> {
								createNewRaid(player, raid);
								e.getWhoClicked().closeInventory();
							}));
							
							for(int i = 0; i < completedObjectives.size() && (i+1) < rows*9; ++i) {
								int slot = i+1;
								RaidObjective targetObjective = raid.getObjectives().get(completedObjectives.get(i));
								ItemStack objectiveItem = new ItemStack(Material.END_CRYSTAL);{
									ItemMeta im = objectiveItem.getItemMeta();
									im.setDisplayName(targetObjective.getDisplay());
									objectiveItem.setItemMeta(im);
								}
								contents.set(slot, IntelligentItem.of(objectiveItem, e -> {
									continueRaid(player, raid, targetObjective);
									e.getWhoClicked().closeInventory();
								}));
							}
						}
					})
					.build(Main.getInst())
					.open(player);
			}, () -> {
				createNewRaid(player, raid);
			});
	}
	
}
