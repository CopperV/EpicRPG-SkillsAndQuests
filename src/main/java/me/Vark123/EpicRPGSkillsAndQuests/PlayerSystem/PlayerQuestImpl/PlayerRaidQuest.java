package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicParty.EpicPartyAPI;
import me.Vark123.EpicParty.PlayerPartySystem.Party;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.ERaidEventType;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidCheckpoint;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidObjective;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidResp;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Events.RaidEndEvent;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IPerformPlayerOnRaidAction;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IPerformPlayerRaidAction;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidDropTable;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.RuleImpl.ObjectiveCompleteRule;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.Utils.ChainLinkedList;

@Getter
@Setter
public class PlayerRaidQuest extends APlayerQuest {
	
	private PartyPlayer partyPlayer;
	private Party party;
	private boolean soloRun = true;
	private boolean canJoin = false;
	
	private boolean respFlag = false;
	private String respBlockerTaskGroupId = null;
	private List<Player> playerBossFightContainer = new LinkedList<>();

	@Setter(value = AccessLevel.NONE)
	private long randomizedValue;
	private String world;
	private Collection<MythicSpawner> spawners;
	
	private boolean damageFlag = false;
	private Map<Player, Double> damageCounter;
	
	private List<String> activeObjectives = new LinkedList<>();
	private List<String> completedObjectives = new LinkedList<>();
	private List<String> completedGroups = new LinkedList<>();
	private List<String> defeatedBosses = new LinkedList<>();

	public PlayerRaidQuest(Player player, RaidQuest quest, int stage, Collection<PlayerTask> tasks) {
		super(player, quest, stage, tasks);

		this.partyPlayer = EpicPartyAPI.get().getPlayerManager().getPartyPlayer(player).get();
		partyPlayer.getParty().ifPresent(party -> {
			this.party = party;
			soloRun = false;
		});
		
		this.randomizedValue = UUID.randomUUID().getMostSignificantBits();
		this.world = quest.getWorld()+"-"+randomizedValue;
		this.spawners = new HashSet<>();
		this.damageCounter = new ConcurrentHashMap<>();
		
		tasks.stream().forEach(task -> {
			String id = ((RaidGroup) task.getTask().getTaskGroup()).getObjective().getId();
			if(activeObjectives.contains(id))
				return;
			
			activeObjectives.add(id);
		});
	}

	@Override
	public void tryAutoudateQuest() {
		tryUpdateOrEndQuest();
	}
	
	@Override
	public void tryUpdateOrEndQuest() {
		List<RaidGroup> activeGroups = new LinkedList<>();
		tasks.stream()
			.map(pTask -> (RaidGroup) pTask.getTask().getTaskGroup())
			.filter(group -> !completedGroups.contains(group.getId()))
			.forEach(group -> {
				if(activeGroups.contains(group))
					return;
				activeGroups.add(group);
			});
		
		activeGroups.stream().forEach(group -> {
			tasks.stream()
				.filter(pTask -> ((RaidGroup)pTask.getTask().getTaskGroup())
						.getId().equals(group.getId()))
				.filter(pTask -> !pTask.isCompleted())
				.findAny().ifPresentOrElse(pTask -> { }, () -> {
					completedGroups.add(group.getId());
					RaidGroup newGroup = group.getObjective().getTaskGroups()
							.stream()
							.filter(chainedGroup -> chainedGroup.contains(group))
							.map(chainedGroup -> chainedGroup.getNext(group))
							.filter(chainedGroup -> chainedGroup != null)
							.findFirst().orElse(null);
					
					
					group.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(this));
					group.getEventsByType(EventCall.COMPLETE).ifPresent(event -> event.executeEvent(this));
					
					tasks.removeAll(tasks.stream()
							.filter(pTask -> ((RaidGroup) pTask.getTask()
									.getTaskGroup()).getId()
									.equals(group.getId()))
							.collect(Collectors.toList()));
					if(newGroup != null) {
						tasks.addAll(newGroup.getTasks().stream()
								.map(task -> new PlayerTask(player, quest, task, 0, false))
								.collect(Collectors.toList()));
						newGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));

						performAction(_p -> {
							_p.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
							_p.playSound(_p, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
							_p.spawnParticle(Particle.HAPPY_VILLAGER, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
						});
					} else {
						boolean comletedAllGroupsFlag = group.getObjective().getLastGroups()
								.stream()
								.filter(_group -> !completedGroups.contains(_group.getId()))
								.findAny()
								.isEmpty();
						
						RaidObjective presentObjective = group.getObjective();
						if(comletedAllGroupsFlag) {
							Optional<RaidObjective> nextObjective = presentObjective.getRaidQuest()
									.getNextObjective(presentObjective);
							endObjective(presentObjective);
							nextObjective.ifPresent(this::startObjective);
						} else {
							presentObjective.getRules().stream()
								.filter(rule -> rule instanceof ObjectiveCompleteRule)
								.filter(rule -> rule.checkRule(this))
								.forEach(rule -> rule.applyConsequences(this));
						}
					}
				});
		});
		
		performAction(_p -> {
			RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
				rp.getRaidInfo().stream()
				.filter(raidInfo -> raidInfo.getRaidId().equals(quest.getId()))
				.findAny()
				.ifPresent(raidInfo -> raidInfo.update(this));
		});
		if(tasks == null || tasks.isEmpty())
			endRaid();
	}

	@Override
	public void updateQuest() { }

	@Override
	public void changeQuestStage(int newStage) { }

	@Override
	public void endQuest() { }

	@Override
	public void removeQuest() {
		performAction(_p -> {
			QuestPlayer qp = PlayerManager.get().getQuestPlayer(_p).get();
			qp.getActiveQuests().remove(quest);
		});
		RaidManager.get().clearRaid(this);
	}
	
	private void endRaid() {
		String prefix = RaidManager.get().getRaidPrefix();
		RaidQuest raid = (RaidQuest) quest;
		boolean defeat = raid.isDefeated();
		
		if(defeat)
			Bukkit.broadcastMessage(prefix+" §eDruzyna §7"+partyPlayer.getPlayer().getName()+" §eukonczyla rajd §r"+raid.getDisplay());
		else
			Bukkit.broadcastMessage(prefix+" §eDruzyna §7"+partyPlayer.getPlayer().getName()+" §ejako pierwsza ukonczyla rajd §r"+raid.getDisplay());

		var max = damageCounter.entrySet()
			.stream()
			.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
			.get();
		
		Bukkit.broadcastMessage(prefix+" §eNajwieksze obrazenia zadal §7"+max.getKey().getName()+" §8[§f"+String.format("%.2f", max.getValue())+"§8]");
		Bukkit.broadcastMessage(prefix+" §eDo zwyciestwa przyczynili sie:");
		damageCounter.forEach((p, dmg) -> {
			Bukkit.broadcastMessage("§4§l» §7"+p.getName()+" §e- §f"+String.format("%.2f", dmg)+" §eobrazen");
		});
		
		raid.setDefeated(true);
		Event event = new RaidEndEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}

	@Override
	public List<String> getQuestInfo(Player viewer) {
		List<String> lore = new LinkedList<>();
		
		List<RaidObjective> localObjectives = new LinkedList<>();
		List<RaidGroup> localGroups = new LinkedList<>();
		Map<ATask, PlayerTask> localTasks = new LinkedHashMap<>();
		tasks.stream().forEach(pTask -> {
			ATask task = pTask.getTask();
			if(!(task.getTaskGroup() instanceof RaidGroup))
				return;
			RaidGroup raidGroup = (RaidGroup) task.getTaskGroup();
			RaidObjective raidObjective = raidGroup.getObjective();
			
			if(!localObjectives.contains(raidObjective))
				localObjectives.add(raidObjective);
			if(!localGroups.contains(raidGroup))
				localGroups.add(raidGroup);
			localTasks.put(task, pTask);
		});
		
		localObjectives.stream()
			.forEach(objective -> {
				if(objective.getMessage() != null)
					lore.add("§4§l▶ §e"+objective.getMessage().replace("%stan%", "§cNiewykonane"));
				objective.getTaskGroups().stream().forEach(groups -> {
					groups.stream()
						.filter(localGroups::contains)
						.forEach(group -> {
							if(group.getMessage().isPresent())
								lore.add("  §4§l▶ §e"+group.getMessage()
									.get().replace("%stan%", "§cNiewykonane"));
							group.getTasks().stream()
								.filter(localTasks::containsKey)
								.filter(task -> task.getMessage() != null)
								.forEach(task -> {
									lore.add("    §4§l▶ "+task.getProgess(localTasks.get(task)));
								});
						});
				});
			});
		
		//FOR GameMasters
		if(viewer.hasPermission("epicrpg.gm"))
			localObjectives.stream()
				.forEach(objective -> {
					lore.add("§4§l▶ §e"+objective.getId());
					objective.getTaskGroups().stream().forEach(groups -> {
						groups.stream()
							.filter(localGroups::contains)
							.forEach(group -> {
								lore.add("  §4§l▶ §e"+group.getId());
								group.getTasks().stream()
									.filter(localTasks::containsKey)
									.forEach(task -> {
										lore.add("    §4§l▶ §e"+task.getId()+": §7"+localTasks.get(task).isCompleted());
									});
							});
					});
				});
		return lore;
	}
	
	public void startObjective(RaidObjective objective) {
		if(completedObjectives.contains(objective.getId()))
			return;
		if(tasks.stream()
				.map(task -> task.getTask().getTaskGroup())
				.map(group -> (RaidGroup) group)
				.filter(group -> group.getObjective().equals(objective))
				.findAny()
				.isPresent())
			return;
		
		if(objective.getEvents().containsKey(ERaidEventType.START))
			objective.getEvents().get(ERaidEventType.START)
				.forEach(event -> event.doAction(player, this));
		
		List<RaidGroup> startGroups = objective.getTaskGroups().stream()
				.map(list -> list.getFirst())
				.collect(Collectors.toList());
		startGroups.stream()
			.forEach(taskGroup -> taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this)));
		
		List<PlayerTask> newTasks = new LinkedList<>();
		startGroups.stream()
			.map(taskGroup -> taskGroup.getTasks())
			.forEach(tasks -> tasks.stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.forEach(newTasks::add));
		tasks.addAll(newTasks);

		if(!activeObjectives.contains(objective.getId()))
			activeObjectives.add(objective.getId());
		performAction(_p -> {
			_p.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
			_p.playSound(_p, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
			_p.spawnParticle(Particle.HAPPY_VILLAGER, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
		
		if(objective.getDisplay() != null) {
			performAction(_p -> {
				RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
					rp.getRaidInfo().stream()
					.filter(raidInfo -> raidInfo.getRaidId().equals(quest.getId()))
					.findAny()
					.ifPresent(raidInfo -> raidInfo.unlockCheckpoint(objective.getId()));
				
			});
		}
	}
	
	public void endObjective(RaidObjective objective) {
		activeObjectives.remove(objective.getId());
		completedObjectives.add(objective.getId());
		if(objective.getEvents().containsKey(ERaidEventType.END))
			objective.getEvents().get(ERaidEventType.END)
				.forEach(event -> event.doAction(player, this));
		
		List<PlayerTask> activeTasks = tasks.stream()
				.filter(task -> ((RaidGroup) task.getTask().getTaskGroup())
						.getObjective().equals(objective))
				.collect(Collectors.toList());
		List<RaidGroup> activeGroups = new LinkedList<>();
		activeTasks.stream()
			.map(pTask -> (RaidGroup) pTask.getTask().getTaskGroup())
			.filter(group -> !completedGroups.contains(group.getId()))
			.forEach(group -> {
				if(activeGroups.contains(group))
					return;
				activeGroups.add(group);
			});
		
		activeGroups.stream().forEach(group -> {
			group.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(this));
			group.getEventsByType(EventCall.COMPLETE).ifPresent(event -> event.executeEvent(this));
		});
		
		tasks.removeAll(activeTasks);
		completedGroups.addAll(activeGroups.stream()
				.map(group -> group.getId())
				.collect(Collectors.toList()));
		
		performAction(_p -> {
			_p.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
			_p.playSound(_p, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
			_p.spawnParticle(Particle.HAPPY_VILLAGER, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
	}
	
	public void wipeRaid() {
		if(!respFlag)
			return;

		PlayerRaidQuest pQuest = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				RaidGroup group = tasks.stream()
						.map(task -> (RaidGroup) task.getTask().getTaskGroup())
						.filter(_group -> _group.getId().equals(respBlockerTaskGroupId))
						.findFirst()
						.orElse(null);
				if(group == null || group.getCheckpoint() == null) {
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eNie mozna cofnac rajdu do punktu kontrolnego!");
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eZglos to administratorowi!");
					return;
				}

				RaidCheckpoint checkpoint = group.getCheckpoint();
				RaidGroup targetGroup = group.getObjective().getRaidQuest().getObjectives()
						.values().stream()
						.flatMap(objective -> objective.getTaskGroups().stream())
						.flatMap(groupsList -> groupsList.stream())
						.filter(_group -> _group.getId().equals(checkpoint.getTargetGroup()))
						.findAny()
						.orElse(null);
				if(targetGroup == null) {
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eNie mozna cofnac rajdu do punktu kontrolnego!");
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eZglos to administratorowi!");
					return;
				}

				RaidObjective targetObjective = targetGroup.getObjective();
				RaidQuest raidQuest = targetObjective.getRaidQuest();
				int objectiveId = raidQuest.getObjectives().entrySet()
						.stream()
						.filter(entry -> entry.getValue().equals(targetObjective))
						.map(entry -> entry.getKey())
						.findAny()
						.orElse(-1);
				if(objectiveId < 0) {
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eNie mozna cofnac rajdu do punktu kontrolnego!");
					player.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eZglos to administratorowi!");
					return;
				}
				
				respFlag = false;
				respBlockerTaskGroupId = null;
				canJoin = false;
				new BukkitRunnable() {
					@Override
					public void run() {
						List<RaidObjective> objectivesToUndo = raidQuest.getObjectives().entrySet()
								.stream()
								.filter(entry -> entry.getKey() > objectiveId)
								.map(entry -> entry.getValue())
								.collect(Collectors.toList());
						List<String> groupsToUndo = objectivesToUndo
								.stream()
								.flatMap(objective -> objective.getTaskGroups().stream())
								.flatMap(groupsList -> groupsList.stream())
								.map(group -> group.getId())
								.collect(Collectors.toList());
						ChainLinkedList<RaidGroup> groupList = targetObjective.getTaskGroups()
								.stream()
								.filter(_groupList -> _groupList.contains(targetGroup))
								.findAny()
								.orElse(null);
						if(groupList != null) {
							RaidGroup tmp = targetGroup.getObjective().equals(group.getObjective()) ? targetGroup : groupList.get(0);
							while(tmp != null) {
								groupsToUndo.add(tmp.getId());
								tmp = groupList.getNext(tmp);
							}
						}
						List<PlayerTask> tasksToUndo = tasks.stream()
								.filter(task -> groupsToUndo.contains(((RaidGroup) task.getTask().getTaskGroup()).getId()))
								.collect(Collectors.toList());
						
						tasks.removeAll(tasksToUndo);
						completedGroups.removeAll(groupsToUndo);
						
						List<String> strObjectivesToUndo = objectivesToUndo.stream()
								.map(obj -> obj.getId())
								.collect(Collectors.toList());
						completedObjectives.removeAll(strObjectivesToUndo);
						activeObjectives.removeAll(strObjectivesToUndo);
						
						checkpoint.getCommands()
							.stream()
							.map(cmd -> cmd.replace("[RAID_WORLD]", world))
							.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));

						targetGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(pQuest));
						targetGroup.getTasks().stream()
							.map(task -> new PlayerTask(player, raidQuest, task, 0, false))
							.forEach(tasks::add);

						performAction(_p -> {
							RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
							rp.getRaidInfo().stream()
								.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
								.findAny()
								.ifPresent(raidInfo -> raidInfo.update(pQuest));
							_p.sendMessage(RaidManager.get().getRaidPrefix()+
									" §eRajd zostal cofniety do punktu kontrolnego!");
						});

						canJoin = true;
					}
				}.runTask(Main.getInst());
			}
		}.runTask(Main.getInst());
	}

	public Optional<Party> getParty() {
		return Optional.ofNullable(party);
	}
	
	public void sendMessage(String message) {
		performAction(_p -> _p.sendMessage(message));
	}
	
	public void performAction(IPerformPlayerRaidAction functionable) {
		getParty().ifPresentOrElse(party -> {
			party.getMembers().stream()
				.map(pp -> pp.getPlayer())
				.filter(player -> player.isOnline())
				.filter(player -> RaidManager.get().getRaidPlayer(player).isLoaded() 
						&& RaidManager.get().getRaidPlayer(player).getRaidInfo()
							.stream()
							.filter(raidInfo -> raidInfo.getRaidId().equals(quest.getId()))
							.findFirst()
							.isPresent())
				.forEach(player -> functionable.action(player));
		}, () -> {
			functionable.action(player);
		});
	}
	
	public void performAction(IPerformPlayerOnRaidAction functionable) {
		getParty().ifPresentOrElse(party -> {
			party.getMembers().stream()
				.map(pp -> pp.getPlayer())
				.filter(player -> player.isOnline())
				.filter(player -> RaidManager.get().getRaidPlayer(player).isLoaded() 
						&& RaidManager.get().getRaidPlayer(player).getRaidInfo()
							.stream()
							.filter(raidInfo -> raidInfo.getRaidId().equals(quest.getId()))
							.findFirst()
							.isPresent())
				.forEach(player -> functionable.action(player, this));
		}, () -> {
			functionable.action(player, this);
		});
	}

	public void createRespTask(QuestPlayer qp) {
		PlayerRaidQuest raidQuest = this;
		Player p = qp.getPlayer();
		p.sendMessage(RaidManager.get().getRaidPrefix()+
				" §ePoczekaj §f15 §esekund na teleportacje");
		BukkitTask respawnTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(isCancelled())
					return;
				if(!qp.getActiveQuests().values().contains(raidQuest))
					return;
				if(respFlag) {
					p.playSound(p, Sound.ENTITY_VILLAGER_HURT, 1, 0.85f);
					p.sendMessage(RaidManager.get().getRaidPrefix()+
							" §cNie mozna sie teraz teleportowac na rajd!");
					return;
				}
				
				resp(p);
				RaidManager.get().getRespTasks().remove(p);
				sendMessage(RaidManager.get().getRaidPrefix()+
						" §7"+p.getName()+" §ewszedl na rajd");
			}
		}.runTaskLaterAsynchronously(Main.getInst(), 20*15);
		RaidManager.get().getRespTasks().put(p, respawnTask);
	}
	private void resp(Player player) {
		World raidWorld = Bukkit.getWorld(world);
		RaidQuest raidQuest = (RaidQuest) quest;
		List<Integer> activeObjectiveIds = raidQuest.getObjectives().keySet()
				.stream()
				.filter(objectiveId -> this.activeObjectives.contains(raidQuest.getObjectives().get(objectiveId).getId()))
				.collect(Collectors.toList());
		RaidObjective objective = raidQuest.getObjectives().get(activeObjectiveIds
				.stream()
				.min(Integer::compare)
				.orElse(0));
		if(objective == null)
			return;
		RaidResp resp = objective.getRespLocation();
		Location pLoc = player.getLocation();
		Location loc = new Location(raidWorld,
				resp.getRespX(), resp.getRespY(), resp.getRespZ(),
				pLoc.getYaw(), pLoc.getPitch());
		new BukkitRunnable() {
			@Override
			public void run() {
				player.sendTitle("§e§l ", "§d§lTELEPORTACJA", 5, 10, 15);
				player.teleport(loc);
			}
		}.runTask(Main.getInst());
	}
	
	public void addDamage(Player player, double dmg) {
		double presentDmg = damageCounter.getOrDefault(player, 0.);
		presentDmg += dmg;
		damageCounter.put(player, presentDmg);
	}
	
	public void generateDrops(String mobId) {
		if(defeatedBosses.contains(mobId))
			return;
		
		RaidQuest raid = (RaidQuest) quest;
		if(!raid.getDropTables().containsKey(mobId))
			return;

		defeatedBosses.add(mobId);
		Random rand = new Random();
		List<IRaidDropTable> dropTable = raid.getDropTables().get(mobId);
		playerBossFightContainer.stream()
			.map(RaidManager.get()::loadPlayer)
			.forEach(rp -> {
				rp.getRaidInfo().stream()
					.filter(raidInfo -> raidInfo.getRaidId().equals(raid.getId()))
					.findAny()
					.ifPresent(raidInfo -> {
						List<IRaidDropTable> personalizedDropTable = new LinkedList<>();
						MutableBoolean guarantableFlag = new MutableBoolean(
								!raidInfo.getDropped().contains(mobId) && raidInfo.getGuaranteedDrops().contains(mobId));
						dropTable.stream()
							.filter(drop -> !raidInfo.getDropped().contains(mobId) || !drop.isLimited())
							.filter(drop -> drop.getChance() >= rand.nextDouble())
							.forEach(drop -> {
								personalizedDropTable.add(drop);
								if(drop.isGuarantable() && guarantableFlag.isTrue())
									guarantableFlag.setFalse();
							});
						if(guarantableFlag.isTrue()) {
							IRaidDropTable guaranteedDrop = dropTable.stream()
								.filter(drop -> drop.isGuarantable())
								.collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
									Collections.shuffle(list, rand);
									return list.size() > 0 ? list.get(0) : null;
								}));
							if(guaranteedDrop != null)
								personalizedDropTable.add(guaranteedDrop);
						}
						if(!raidInfo.getDropped().contains(mobId))
							raidInfo.getDropped().add(mobId);
						
						personalizedDropTable.forEach(drop -> drop.dropToPlayer(rp.getPlayer()));
					});
			});
	}
	
}
