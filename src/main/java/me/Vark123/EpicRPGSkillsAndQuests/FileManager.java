package me.Vark123.EpicRPGSkillsAndQuests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.EpicItemManager;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.LearnItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.StatItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests.StandardQuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPCManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.StandardQuest;
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
							.ifPresent(epicItem -> {
								AEpicItem clone = epicItem.clone();
								if(clone instanceof StatItem && slots.contains(slot+".max")) {
									((StatItem)clone).setLimit(slots.getInt(slot+".max"));
								}
								if(clone instanceof LearnItem && slots.contains(slot+".wymagania")) {
									List<String> lines = slots.getStringList(slot+".wymagania");
									((LearnItem)clone).getRequirements().addAll(RequirementManager.generateRequirements(lines));
								}
								items.put(_slot, clone);
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
				fYml.set("done", qp.getCompletedQuests());
			});
		try {
			fYml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
