package me.Vark123.EpicRPGSkillsAndQuests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.EpicItemManager;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.CustomItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.LearnItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.StatItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.DailyQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.DungeonQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.StandardQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.WorldQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.ZlecenieQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPCManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerStandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerWorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.StandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.WorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.ZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.DailyController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.WorldQuestController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FishTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.RequirementManager;

@Getter
public final class FileManager {

	@Getter
	private static final File dailyDir = new File(Main.getInst().getDataFolder(), "daily");
	@Getter
	private static final File dungeonsDir = new File(Main.getInst().getDataFolder(), "dungeons");
	@Getter
	private static final File npcDir = new File(Main.getInst().getDataFolder(), "npc");
	@Getter
	private static final File questsDir = new File(Main.getInst().getDataFolder(), "quests");
	@Getter
	private static final File playerQuestsDir = new File(Main.getInst().getDataFolder(), "pquests");
	private static final File oldPlayerQuestDir = new File(Main.getInst().getDataFolder(), "old_pquests");
	@Getter
	private static final File worldQuestsDir = new File(Main.getInst().getDataFolder(), "worldquests");
	@Getter
	private static final File zleceniaDir = new File(Main.getInst().getDataFolder(), "zlecenia");

	@Getter
	private static final File config = new File(Main.getInst().getDataFolder(), "config.yml");
	@Getter
	private static final File daily = new File(Main.getInst().getDataFolder(), "daily.yml");
	@Getter
	private static final File dungeonsController = new File(Main.getInst().getDataFolder(), "dungeons.yml");

	private FileManager() { }
	
	public static void init() {
		if(!Main.getInst().getDataFolder().exists())
			Main.getInst().getDataFolder().mkdir();
		
		Main.getInst().saveResource("config.yml", false);
		if(!daily.exists())
			try {
				daily.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(!dungeonsController.exists())
			try {
				dungeonsController.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if(oldPlayerQuestDir.exists())
			convert();
		
		if(!dailyDir.exists())
			dailyDir.mkdir();
		if(!dungeonsDir.exists())
			dungeonsDir.mkdir();
		if(!npcDir.exists())
			npcDir.mkdir();
		if(!questsDir.exists())
			questsDir.mkdir();
		if(!playerQuestsDir.exists())
			playerQuestsDir.mkdir();
		if(!worldQuestsDir.exists())
			worldQuestsDir.mkdir();
		if(!zleceniaDir.exists())
			zleceniaDir.mkdir();
		
		loadConfig();
		loadAllQuests();
		loadNPC();
	}
	
	private static void loadConfig() {
		Config.get().init();
		Collection<UUID> dailyDone = YamlConfiguration.loadConfiguration(daily)
				.getStringList("done")
				.stream()
				.map(UUID::fromString)
				.collect(Collectors.toSet());
		DailyController.get().getDoneDaily().addAll(dailyDone);
	}
	
	private static void loadAllQuests() {
		Arrays.asList(questsDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				StandardQuest quest = new StandardQuest(fYml);
				QuestManager.get().registerQuest(quest);
				EpicItemManager.get().registerItem(new StandardQuestItem(quest));
			});
		Arrays.asList(zleceniaDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				ZlecenieQuest quest = new ZlecenieQuest(fYml);
				QuestManager.get().registerQuest(quest);
				EpicItemManager.get().registerItem(new ZlecenieQuestItem(quest));
			});
		Arrays.asList(dailyDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				DailyQuest quest = new DailyQuest(fYml);
				QuestManager.get().registerQuest(quest);
				EpicItemManager.get().registerItem(new DailyQuestItem(quest));
			});
		Arrays.asList(worldQuestsDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				WorldQuest quest = new WorldQuest(fYml);
				QuestManager.get().registerQuest(quest);
				EpicItemManager.get().registerItem(new WorldQuestItem(quest));
			});
		Arrays.asList(dungeonsDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				DungeonQuest quest = new DungeonQuest(fYml);
				QuestManager.get().registerQuest(quest);
				EpicItemManager.get().registerItem(new DungeonQuestItem(quest));
			});
	}
	
	private static void loadNPC() {
		Arrays.asList(npcDir.listFiles()).stream()
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				String name = ChatColor.translateAlternateColorCodes('&', fYml.getString("name"));
				String title = ChatColor.translateAlternateColorCodes('&', fYml.getString("title"));
				int size = fYml.getInt("size");
				
				Map<Integer, AEpicItem> items = new LinkedHashMap<>();
				ConfigurationSection slots = fYml.getConfigurationSection("slots");
				slots.getKeys(false).stream()
					.forEach(slot -> {
						if(!StringUtils.isNumeric(slot))
							return;
						int _slot = Integer.parseInt(slot) - 1;
						String item = slots.getString(slot+".item");
						
						EpicItemManager.get().getItemById(item)
							.ifPresentOrElse(epicItem -> {
								AEpicItem clone = epicItem.clone();
								if(clone instanceof StatItem && slots.contains(slot+".max")) {
									((StatItem)clone).setLimit(slots.getInt(slot+".max"));
								}
								if(clone instanceof LearnItem && slots.contains(slot+".wymagania")) {
									List<String> lines = slots.getStringList(slot+".wymagania");
									((LearnItem)clone).getRequirements().addAll(RequirementManager.generateRequirements(lines));
								}
								items.put(_slot, clone);
							}, () -> {
								if(!item.equalsIgnoreCase("custom"))
									return;
								String id = slots.getString(slot+".id");
								Material m = Material.valueOf(slots.getString(slot+".material").toUpperCase());
								String display = ChatColor.translateAlternateColorCodes('&', slots.getString(slot+".display"));
								List<String> actions = slots.getStringList(slot+".actions");
								CustomItem _item = new CustomItem(id, m, display, actions);
								EpicItemManager.get().registerItem(_item);
								items.put(_slot, _item);
							});
					});
				EpicNPC npc = new EpicNPC(name, title, size, items);
				EpicNPCManager.get().registerNPC(npc);
			});
	}
	
	public static Collection<String> getPlayerCompletedQuests(Player p) {
		String uid = p.getUniqueId().toString();
		File file = new File(playerQuestsDir, uid+".yml");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return new LinkedList<>();
			}
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
		return fYml.getStringList("done");
	}
	
	public static void savePlayer(Player p) {
		String uid = p.getUniqueId().toString();
		File file = new File(playerQuestsDir, uid+".yml");
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(file);
		PlayerManager.get().getQuestPlayer(p)
			.ifPresent(qp -> {
				fYml.set("last-nick", p.getName());
				fYml.set("done", qp.getCompletedQuests());
			});
		try {
			fYml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void save() {
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(daily);
		fYml.set("done", DailyController.get().getDoneDaily()
				.stream()
				.map(uid -> uid.toString())
				.collect(Collectors.toList()));
		try {
			fYml.save(daily);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadWorldQuests() {
		WorldQuestController.get();
		QuestManager.get().getQuests().stream()
			.filter(quest -> quest instanceof WorldQuest
					&& ((WorldQuest) quest).isCompleted())
			.map(quest -> quest.getId())
			.forEach(WorldQuestController.get().getCompletedQuests()::add);
	}
	
	public static void saveWorldQuests() {
		DatabaseManager.saveWorldQuests();
		WorldQuestController.get().getCompletedQuests().stream()
			.map(id -> new File(worldQuestsDir, id+".yml"))
			.filter(f -> f.exists())
			.forEach(f -> {
				YamlConfiguration fYml = YamlConfiguration.loadConfiguration(f);
				fYml.set("completed", true);
				try {
					fYml.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		QuestManager.get().getQuests().stream()
			.filter(quest -> quest instanceof DungeonQuest
					&& ((DungeonQuest) quest).isDefeated())
			.map(quest -> new File(dungeonsDir, quest.getId()+".yml"))
			.filter(f -> f.exists())
			.forEach(f -> {
				YamlConfiguration fYml = YamlConfiguration.loadConfiguration(f);
				fYml.set("defeat", true);
				try {
					fYml.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
	}
	
	public static void updateTaskQuest(String change, PlayerTask pTask, APlayerQuest pQuest) {
		File dir;
		if(pQuest instanceof PlayerStandardQuest)
			dir = questsDir;
		else if(pQuest instanceof PlayerZlecenieQuest)
			dir = zleceniaDir;
		else if(pQuest instanceof PlayerDailyQuest)
			dir = dailyDir;
		else if(pQuest instanceof PlayerWorldQuest)
			dir = worldQuestsDir;
		else if(pQuest instanceof PlayerDungeonQuest)
			dir = dungeonsDir;
		else 
			return;
		
		AQuest quest = pQuest.getQuest();
		File questFile = new File(dir, quest.getId()+".yml");
		if(!questFile.exists())
			return;
		
		MutableInt trueStage = new MutableInt(pQuest.getStage());
		if(pQuest instanceof PlayerZlecenieQuest) {
			quest.getTaskGroups().entrySet().stream()
				.filter(entry -> entry.getValue().equals(pQuest.getPresentTaskGroup()))
				.findAny()
				.ifPresentOrElse(entry -> trueStage.setValue(entry.getKey()),
						() -> {
							trueStage.setValue(-1);
						});
			if(trueStage.getValue() < 0)
				return;
		}
		
		YamlConfiguration fYml = YamlConfiguration.loadConfiguration(questFile);
		ConfigurationSection section = fYml.getConfigurationSection("stopnie."+trueStage.intValue()+".targets");
		section.getKeys(false).stream()
			.filter(key -> section.getString(key+".id").equals(pTask.getTask().getId()))
			.findAny()
			.ifPresent(key -> {
				ATask task = pTask.getTask();
				switch(change.toLowerCase()) {
					case "target":
						section.set(key+".targetName", task.getTarget().replace('§', '&'));
						break;
					case "message":
						section.set(key+".message", task.getMessage().replace('§', '&'));
						break;
					case "amount":
						try {
							Field field = task.getClass().getDeclaredField("amount");
							field.setAccessible(true);
							section.set(key+".ilosc", field.get(task));
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e) {
							e.printStackTrace();
						}
						break;
					case "level":
						section.set(key+".level", ((PlayerKillTask) task).getLevel());
						break;
					case "fish":
						section.set(key+".inrow", ((FishTask) task).isInRow());
						break;
				}
			});
		try {
			fYml.save(questFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void convert() {
		Arrays.asList(oldPlayerQuestDir.listFiles()).stream()
			.filter(file -> file.isFile())
			.filter(file -> file.getName().endsWith(".yml"))
			.map(YamlConfiguration::loadConfiguration)
			.forEach(fYml -> {
				String nick = fYml.getString("nazwa");
				String uid = fYml.getString("UUID");
				List<String> quests = fYml.getStringList("done");
				
				File file = new File(playerQuestsDir, uid+".yml");
				if(file.exists()) {
					File toCompare1 = new File(oldPlayerQuestDir, nick.toLowerCase()+".yml");
					YamlConfiguration fYml2 = YamlConfiguration.loadConfiguration(file);
					String nick2 = fYml2.getString("last-nick");
					File toCompare2 = new File(oldPlayerQuestDir, nick2.toLowerCase()+".yml");
					if(toCompare2.exists()
							&& FileUtils.isFileNewer(toCompare1, toCompare2))
						return;
					if(!toCompare2.exists())
						return;
				} else {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration fYml2 = YamlConfiguration.loadConfiguration(file);
				fYml2.set("last-nick", nick);
				fYml2.set("done", quests);
				try {
					fYml2.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		oldPlayerQuestDir.renameTo(new File(Main.getInst().getDataFolder(), "archive"));
	}
	
}
