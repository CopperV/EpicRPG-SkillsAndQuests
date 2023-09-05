package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl;

import java.util.Collection;
import java.util.HashSet;
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
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonResp;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.TakeItemRequirement;
import me.clip.placeholderapi.PlaceholderAPI;

@Getter
@Setter
public class PlayerDungeonQuest extends APlayerQuest {

	private PartyPlayer partyPlayer;
	private Party party;
	private boolean soloRun = true;
	private boolean clanRun = false;
	private boolean canJoin = false;
	private int presentRespAmount;
	
	@Setter(value = AccessLevel.NONE)
	private long randomizedValue;
	private String world;
	private Collection<MythicSpawner> spawners;
	
	private Map<Player, Double> damageCounter;
	
	public PlayerDungeonQuest(Player player, AQuest quest, int stage, Collection<PlayerTask> tasks) {
		super(player, quest, stage, tasks);
		
		this.partyPlayer = EpicPartyAPI.get().getPlayerManager().getPartyPlayer(player).get();
		partyPlayer.getParty().ifPresent(party -> {
			this.party = party;
			soloRun = false;
		});
		this.presentRespAmount = ((DungeonQuest) quest).getMaxResps();
		this.randomizedValue = UUID.randomUUID().getMostSignificantBits();
		this.world = ((DungeonQuest) quest).getWorld()+"-"+randomizedValue;
		this.spawners = new HashSet<>();
		this.damageCounter = new ConcurrentHashMap<>();
	}

	@Override
	public void tryAutoudateQuest() {
		if(stage < quest.getTaskGroups().size())
			tryUpdateOrEndQuest();
		else
			tryEndDungeon();
	}

	public void tryEndDungeon() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				String prefix = DungeonController.get().getDungeonPrefix();
				DungeonQuest dungeon = (DungeonQuest) quest;
				boolean defeat = dungeon.isDefeated();
				
				if(soloRun) {
					if(defeat)
						Bukkit.broadcastMessage(prefix+" §7"+partyPlayer.getName()+" §esamodzielnie przeszedl dungeon §r"+dungeon.getDisplay());
					else
						Bukkit.broadcastMessage(prefix+" §7"+partyPlayer.getName()+" §ejako pierwszy przeszedl dungeon §r"+dungeon.getDisplay());
				} else {
					if(defeat) {
						Bukkit.broadcastMessage(prefix+" §eDruzyna §7"+partyPlayer.getName()+" §eprzeszla dungeon §r"+dungeon.getDisplay());
					} else {
						Bukkit.broadcastMessage(prefix+" §eDruzyna §7"+partyPlayer.getName()+" §ejako pierwsza przeszla dungeon §r"+dungeon.getDisplay());
					}
					var max = damageCounter.entrySet()
						.stream()
						.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
						.get();
					
					Bukkit.broadcastMessage(prefix+" §eNajwieksze obrazenia zadal §7"+max.getKey().getName()+" §8[§f"+String.format("%.2f", max.getValue())+"§8]");
					Bukkit.broadcastMessage(prefix+" §eDo zwyciestwa przyczynili sie:");
					damageCounter.forEach((p, dmg) -> {
						Bukkit.broadcastMessage("§4§l» §7"+p.getName()+" §e- §f"+String.format("%.2f", dmg)+" §eobrazen");
					});
				}
				
				dungeon.setDefeated(true);
			});
	}

	@Override
	public void tryUpdateOrEndQuest() {
		tasks.stream()
			.filter(pTask -> !pTask.isCompleted())
			.findAny().ifPresentOrElse(pTask -> { }, () -> {
				TaskGroup oldTaskGroup = quest.getTaskGroups().get(stage);
				TaskGroup newTaskGroup = quest.getTaskGroups().get(stage + 1);
				oldTaskGroup.getMessage()
					.ifPresent(message -> {
						getParty().ifPresentOrElse(party -> {
							party.getMembers().stream()
								.map(pp -> pp.getPlayer())
								.forEach(player -> {
									String msg = message.replace("%player%", player.getName());
									msg = PlaceholderAPI.setPlaceholders(player, msg);
									player.sendMessage("§4§l» §r"+msg);
								});
						}, () -> {
							String msg = message.replace("%player%", player.getName());
							msg = PlaceholderAPI.setPlaceholders(player, msg);
							player.sendMessage("§4§l» §r"+msg);
						});
						
					});
				
				oldTaskGroup.getEventsByType(EventCall.END).ifPresent(event -> event.executeEvent(this));
				
				if(newTaskGroup == null) {
					getParty().ifPresentOrElse(party -> {
						party.getMembers().stream()
							.map(pp -> pp.getPlayer())
							.forEach(player -> oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player)));
					}, () -> {
						oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
					});
					endQuest();
				}
				
				else {
					newTaskGroup.getRequirements().stream()
						.filter(req -> !req.checkRequirement(partyPlayer.getPlayer()))
						.findFirst().ifPresentOrElse(req -> { }, () -> {
							getParty().ifPresentOrElse(party -> {
								party.getMembers().stream()
									.map(pp -> pp.getPlayer())
									.forEach(player -> oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player)));
							}, () -> {
								oldTaskGroup.getPrize().forEach(prize -> prize.givePrize(player));
							});

							newTaskGroup.getRequirements().stream()
								.filter(req -> req instanceof TakeItemRequirement)
								.forEach(req -> ((TakeItemRequirement) req).takeItems(partyPlayer.getPlayer()));
							updateQuest();
						});
				}
			});
	}

	@Override
	public void updateQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		int newStage = stage + 1;
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		stage = newStage;
		tasks = pTasks;
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		getParty().ifPresentOrElse(party -> {
			party.getMembers().stream()
				.map(pp -> pp.getPlayer())
				.forEach(player -> {
					player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
					player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
					player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
				});
		}, () -> {
			player.sendTitle("§a§lAKTUALIZACJA", quest.getDisplay(), 5, 10, 15);
			player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.1f);
			player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
		});
	}

	@Override
	public void changeQuestStage(int newStage) {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
	
		TaskGroup taskGroup = quest.getTaskGroups().get(newStage);
		Collection<PlayerTask> pTasks = taskGroup.getTasks().stream()
				.map(task -> new PlayerTask(player, quest, task, 0, false))
				.collect(Collectors.toList());
		
		taskGroup.getEventsByType(EventCall.START).ifPresent(event -> event.executeEvent(this));
		
		stage = newStage;
		tasks = pTasks;
	}
	
	@Override
	public void endQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.COMPLETE)
			.ifPresent(event -> event.executeEvent(this));
	
		getParty().ifPresentOrElse(party -> {
			party.getMembers().stream()
				.map(pp -> pp.getPlayer())
				.forEach(player -> {
					QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
					qp.getActiveQuests().remove(quest);
					
					player.sendTitle("§6§lWYKONALES ZADANIE", quest.getDisplay(), 5, 10, 15);
					player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
					player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
					player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
					
					quest.getPrize().forEach(prize -> prize.givePrize(player));
				});
		}, () -> {
			QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
			qp.getActiveQuests().remove(quest);
			
			player.sendTitle("§6§lWYKONALES ZADANIE", quest.getDisplay(), 5, 10, 15);
			player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
			player.spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
			player.spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0,1,0), 15, 0.75, 1, 0.75, 0.15);
			
			quest.getPrize().forEach(prize -> prize.givePrize(player));
		});
		
		DungeonController.get().clearDungeon(this);
	}
	
	@Override
	public void removeQuest() {
		quest.getTaskGroups().get(stage).getEventsByType(EventCall.END)
			.ifPresent(event -> event.executeEvent(this));
		
		getParty().ifPresentOrElse(party -> {
			party.getMembers().stream()
				.map(pp -> pp.getPlayer())
				.forEach(player -> {
					QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
					qp.getActiveQuests().remove(quest);
				});
		}, () -> {
			QuestPlayer qp = PlayerManager.get().getQuestPlayer(player).get();
			qp.getActiveQuests().remove(quest);
		});
		
		DungeonController.get().clearDungeon(this);
	}
	
	public Optional<Party> getParty() {
		return Optional.ofNullable(party);
	}
	
	public void sendMessage(String msg) {
		getParty().ifPresentOrElse(party -> {
			party.getMembers().forEach(pp -> pp.sendMessage(msg));
		}, () -> partyPlayer.sendMessage(msg));
	}
	
	public void resp(Player player) {
		World dungeonWorld = Bukkit.getWorld(world);
		DungeonResp resp = ((DungeonGroup)((DungeonQuest) quest).getTaskGroups().get(stage))
				.getRespLocation();
		Location pLoc = player.getLocation();
		Location loc = new Location(dungeonWorld,
				resp.getRespX(), resp.getRespY(), resp.getRespZ(),
				pLoc.getPitch(), pLoc.getYaw());
		new BukkitRunnable() {
			@Override
			public void run() {
				player.sendTitle("§e§l ", "§d§lTELEPORTACJA", 5, 10, 15);
				player.teleport(loc);
			}
		}.runTask(Main.getInst());
	}
	
	public void createRespTask(QuestPlayer qp) {
		World dungeonWorld = Bukkit.getWorld(world);
		PlayerDungeonQuest tmp = this;
		BukkitTask respawnTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(isCancelled())
					return;
				if(!qp.getActiveQuests().values().contains(tmp))
					return;
				Player p = qp.getPlayer();
				if(presentRespAmount <= 0) {
					p.playSound(p, Sound.ENTITY_VILLAGER_HURT, 1, 0.85f);
					p.sendMessage(DungeonController.get().getDungeonPrefix()+
							" §cZabraklo punktow odrodzen. Nie mozesz sie odrodzic!");
					return;
				}
				if(dungeonWorld.getPlayers().isEmpty()) {
					p.playSound(p, Sound.ENTITY_VILLAGER_HURT, 1, 0.85f);
					p.sendMessage(DungeonController.get().getDungeonPrefix()+
							" §cNa dungeonie nie ma juz graczy. Sprobuj ponownie podejsc do tego dungeona ;)");
					return;
				}
				
				resp(p);
				DungeonController.get().getRespTasks().remove(p);
				--presentRespAmount;
				sendMessage(DungeonController.get().getDungeonPrefix()+
						" §7"+p.getName()+" §eodrodzil sie na dungeonie");
				sendMessage(DungeonController.get().getDungeonPrefix()+
						" §ePozostalo druzynie §f"+presentRespAmount+" §epunktow odrodzen");
			}
		}.runTaskLaterAsynchronously(Main.getInst(), 20*15);
		DungeonController.get().getRespTasks().put(qp.getPlayer(), respawnTask);
	}
	
	public void addDamage(Player player, double dmg) {
		double presentDmg = damageCounter.getOrDefault(player, 0.);
		presentDmg += dmg;
		damageCounter.put(player, presentDmg);
	}
	
}
