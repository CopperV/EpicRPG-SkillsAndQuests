package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IPerformPlayerOnRaidAction;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IPerformPlayerRaidAction;
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

	@Setter(value = AccessLevel.NONE)
	private long randomizedValue;
	private String world;
	private Collection<MythicSpawner> spawners;
	
	private boolean damageFlag = false;
	private Map<Player, Double> damageCounter;
	
	private long startTime;
	
	private List<String> activeObjectives = new LinkedList<>();
	private List<String> completedObjectives = new LinkedList<>();
	private List<String> completedGroups = new LinkedList<>();

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
							_p.spawnParticle(Particle.VILLAGER_HAPPY, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
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
	}

	@Override
	public void updateQuest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeQuestStage(int newStage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endQuest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeQuest() {
		performAction(_p -> {
			QuestPlayer qp = PlayerManager.get().getQuestPlayer(_p).get();
			qp.getActiveQuests().remove(quest);
			if(_p.getUniqueId().equals(player.getUniqueId())) {
				_p.sendMessage(RaidManager.get().getRaidPrefix()+
						" §eOpusciles rajd §r"+quest.getDisplay());
			} else {
				_p.sendMessage(RaidManager.get().getRaidPrefix()+
						" §eLider opuscil rajd §r"+quest.getDisplay());
			}
		});
		RaidManager.get().clearRaid(this);
	}

	@Override
	public List<String> getQuestInfo() {
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
								.forEach(task -> {
									lore.add("    §4§l▶ "+task.getProgess(localTasks.get(task)));
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

		performAction(_p -> {
			_p.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
			_p.playSound(_p, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
			_p.spawnParticle(Particle.VILLAGER_HAPPY, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
	}
	
	public void endObjective(RaidObjective objective) {
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
			_p.spawnParticle(Particle.VILLAGER_HAPPY, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
	}
	
	public void wipeRaid() {
		if(!respFlag)
			return;
		
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
		
		PlayerRaidQuest pRaidQuest = this;
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
					RaidGroup tmp = groupList.getNext(targetGroup);
					while(tmp != null) {
						groupsToUndo.add(tmp.getId());
						tmp = groupList.getNext(targetGroup);
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
				
				performAction(_p -> {
					RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
					rp.getRaidInfo().stream()
						.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
						.findAny()
						.ifPresent(raidInfo -> raidInfo.update(pRaidQuest));
					_p.sendMessage(RaidManager.get().getRaidPrefix()+
							" §eRajd zostal cofniety do punktu kontrolnego!");
				});
				
				canJoin = true;
			}
		}.runTask(Main.getInst());
	}
	
	public void tryEndRaid() {
		
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
	
}
