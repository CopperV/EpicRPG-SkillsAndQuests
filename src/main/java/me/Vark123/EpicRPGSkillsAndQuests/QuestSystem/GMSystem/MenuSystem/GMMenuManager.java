package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.GMSystem.MenuSystem;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import io.github.rysefoxx.inventory.anvilgui.AnvilGUI.Builder;
import io.github.rysefoxx.inventory.anvilgui.AnvilGUI.ResponseAction;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.InventoryOpenerType;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerStandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerWorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FishTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.KillTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PointsTask;

@Getter
public final class GMMenuManager {

	private static final GMMenuManager inst = new GMMenuManager();
	
	private final ItemStack completeQuest;
	private final ItemStack removeQuest;
	private final ItemStack updateQuest;
	private final ItemStack degradeQuest;

	private final ItemStack deleteTask;
	private final ItemStack completeTask;
	private final ItemStack uncompleteTask;
	private final ItemStack changeTargetTask;
	private final ItemStack changeMessageTask;
	private final ItemStack changeProgressTask;
	private final ItemStack changeAmountTask;
	private final ItemStack changeLevelTask;
	private final ItemStack changeInRowTask;
	
	private final ItemStack back;
	
	private GMMenuManager() {
		completeQuest = new ItemStack(Material.GOLD_NUGGET);{
			ItemMeta im = completeQuest.getItemMeta();
			im.setDisplayName("§e§lZAKONCZ ZADANIE");
			completeQuest.setItemMeta(im);
		}
		removeQuest = new ItemStack(Material.RED_TERRACOTTA);{
			ItemMeta im = removeQuest.getItemMeta();
			im.setDisplayName("§7§lUSUN ZADANIE");
			removeQuest.setItemMeta(im);
		}
		updateQuest = new ItemStack(Material.EXPERIENCE_BOTTLE);{
			ItemMeta im = updateQuest.getItemMeta();
			im.setDisplayName("§a§lAKTUALIZUJ ZADANIE");
			updateQuest.setItemMeta(im);
		}
		degradeQuest = new ItemStack(Material.FIRE);{
			ItemMeta im = degradeQuest.getItemMeta();
			im.setDisplayName("§c§lDEGRADUJ ZADANIE");
			degradeQuest.setItemMeta(im);
		}

		deleteTask = new ItemStack(Material.WOODEN_PICKAXE);{
			ItemMeta im = deleteTask.getItemMeta();
			im.setDisplayName("§7§lUSUN TASKA");
			deleteTask.setItemMeta(im);
		}
		completeTask = new ItemStack(Material.GREEN_TERRACOTTA);{
			ItemMeta im = completeTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN STAN TASKA NA §a§lWYKONANY");
			completeTask.setItemMeta(im);
		}
		uncompleteTask = new ItemStack(Material.RED_TERRACOTTA);{
			ItemMeta im = uncompleteTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN STAN TASKA NA §c§lNIEWYKONANY");
			uncompleteTask.setItemMeta(im);
		}
		changeTargetTask = new ItemStack(Material.TOTEM_OF_UNDYING);{
			ItemMeta im = changeTargetTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN CEL TASKA");
			im.setLore(Arrays.asList(" ","§4§lUWAGA §7[§e§lOPERACJA GLOBALNA§7]"));
			changeTargetTask.setItemMeta(im);
		}
		changeMessageTask = new ItemStack(Material.WRITABLE_BOOK);{
			ItemMeta im = changeMessageTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN WIADOMOSC TASKA");
			im.setLore(Arrays.asList(" ","§4§lUWAGA §7[§e§lOPERACJA GLOBALNA§7]"));
			changeMessageTask.setItemMeta(im);
		}
		changeProgressTask = new ItemStack(Material.EXPERIENCE_BOTTLE);{
			ItemMeta im = changeProgressTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN POSTEP TASKA");
			changeProgressTask.setItemMeta(im);
		}
		changeAmountTask = new ItemStack(Material.IRON_AXE);{
			ItemMeta im = changeAmountTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN ILOSC DO WYKONANIA TASKA");
			im.setLore(Arrays.asList(" ","§4§lUWAGA §7[§e§lOPERACJA GLOBALNA§7]"));
			changeAmountTask.setItemMeta(im);
		}
		changeLevelTask = new ItemStack(Material.DIAMOND_SWORD);{
			ItemMeta im = changeLevelTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN MINIMALNY POZIOM GRACZY NA PVP");
			im.setLore(Arrays.asList(" ","§4§lUWAGA §7[§e§lOPERACJA GLOBALNA§7]"));
			changeLevelTask.setItemMeta(im);
		}
		changeInRowTask = new ItemStack(Material.FISHING_ROD);{
			ItemMeta im = changeInRowTask.getItemMeta();
			im.setDisplayName("§7§lZMIEN LOWIENIE \"POD RZAD\"");
			im.setLore(Arrays.asList(" ","§4§lUWAGA §7[§e§lOPERACJA GLOBALNA§7]"));
			changeInRowTask.setItemMeta(im);
		}
		
		back = new ItemStack(Material.BARRIER);{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName("§c§lPOWROT");
			back.setItemMeta(im);
		}
	}
	
	public static final GMMenuManager get() {
		return inst;
	}
	
	public void openBaseMenu(Player gm, Player player) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		QuestPlayer qp = oQp.get();
		int size = qp.getActiveQuests().size()/9 + 1;
		if(size > 6)
			size = 6;
		RyseInventory.builder()
			.title("§7[§4§lGM§7] §a§lZadania §7"+player.getName())
			.rows(size)
			.disableUpdateTask()
			.listener(GMMenuClickEvents.get().getQuestsClickEvent())
			.provider(getQuestListProvider(qp))
			.build(Main.getInst())
			.open(gm);
	}
	
	public void openQuestEditMenu(Player gm, Player player, APlayerQuest pQuest) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		int size = pQuest.getTasks().size() / 9 + 2;
		if(size > 6)
			size = 6;
		RyseInventory.builder()
			.title("§7[§4§lGM§7] §a§lZadania §7"+player.getName())
			.rows(size)
			.disableUpdateTask()
			.listener(GMMenuClickEvents.get().getQuestEditClickEvent())
			.provider(getQuestEditProvider(pQuest))
			.build(Main.getInst())
			.open(gm);
	}
	
	public void openTaskEditMenu(Player gm, Player player, PlayerTask pTask) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		RyseInventory.builder()
			.title("§7[§4§lGM§7] §a§lZadania §7"+player.getName())
			.rows(1)
			.disableUpdateTask()
			.listener(GMMenuClickEvents.get().getTaskEditClickEvent())
			.provider(getTaskEditProvider(pTask))
			.build(Main.getInst())
			.open(gm);
	}
	
	public void openTaskEditAnvilMenu(Player gm, Player player, PlayerTask pTask,
			String defaultText, String type) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		RyseInventory.builder()
			.title("§7[§4§lGM§7] §a§lZadania §7"+player.getName())
			.type(InventoryOpenerType.ANVIL)
			.disableUpdateTask()
			.provider(getTaskEditAnvilProvider(pTask, defaultText, type))
			.build(Main.getInst())
			.open(gm);
	}
	
	private InventoryProvider getQuestListProvider(QuestPlayer qp) {
		return new InventoryProvider() {
			@Override
			public void init(Player player, InventoryContents contents) {
				MutableInt slot = new MutableInt();
				qp.getActiveQuests().values().forEach(pQuest -> {
					ItemStack questItem = new ItemStack(Material.KNOWLEDGE_BOOK);
					AQuest quest = pQuest.getQuest();
					
					ItemMeta im = questItem.getItemMeta();
					if(pQuest instanceof PlayerStandardQuest)
						im.setDisplayName("§a§lZadanie§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerZlecenieQuest)
						im.setDisplayName("§e§lZlecenie§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerDailyQuest)
						im.setDisplayName(quest.getDisplay());
					else if(pQuest instanceof PlayerWorldQuest)
						im.setDisplayName("§d§lZadanie swiatowe§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerDungeonQuest)
						im.setDisplayName("§c§lDungeon§r: "+quest.getDisplay());
					List<String> lore = pQuest.getTasks().stream()
							.map(pTask -> pTask.getProgress())
							.collect(Collectors.toList());
					lore.add(0, " ");
					lore.add(0, "§eID: §7[§f"+quest.getId()+"§7]");
					lore.add(0, "§eZlecenodawca: §r"+quest.getQuestGiver());
					im.setLore(lore);
					questItem.setItemMeta(im);
					
					NBTItem nbt = new NBTItem(questItem);
					nbt.setString("quest-owner", qp.getPlayer().getUniqueId().toString());
					nbt.setString("quest-id", quest.getId());
					nbt.applyNBT(questItem);
					
					contents.set(slot.getAndIncrement(), questItem);
				});
			}
		};
	}
	
	private InventoryProvider getQuestEditProvider(APlayerQuest pQuest) {
		return new InventoryProvider() {
			@Override
			public void init(Player player, InventoryContents contents) {
				Player owner = pQuest.getPlayer();
				AQuest quest = pQuest.getQuest();
				
				ItemStack questInfo = new ItemStack(Material.KNOWLEDGE_BOOK);{
					ItemMeta im = questInfo.getItemMeta();
					if(pQuest instanceof PlayerStandardQuest)
						im.setDisplayName("§a§lZadanie§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerZlecenieQuest)
						im.setDisplayName("§e§lZlecenie§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerDailyQuest)
						im.setDisplayName(quest.getDisplay());
					else if(pQuest instanceof PlayerWorldQuest)
						im.setDisplayName("§d§lZadanie swiatowe§r: "+quest.getDisplay());
					else if(pQuest instanceof PlayerDungeonQuest)
						im.setDisplayName("§c§lDungeon§r: "+quest.getDisplay());
					List<String> lore = new LinkedList<>();
					lore.add("§eZlecenodawca: §r"+quest.getQuestGiver());
					lore.add("§eID: §7[§f"+quest.getId()+"§7]");
					im.setLore(lore);
					questInfo.setItemMeta(im);
					
					NBTItem nbt = new NBTItem(questInfo);
					nbt.setString("quest-owner", owner.getUniqueId().toString());
					nbt.setString("quest-id", quest.getId());
					nbt.applyNBT(questInfo);
				}
				contents.set(7, questInfo);
				contents.set(0, completeQuest);
				contents.set(1, removeQuest);
				contents.set(2, updateQuest);
				contents.set(3, degradeQuest);
				contents.set(8, back);

				MutableInt slot = new MutableInt(9);
				pQuest.getTasks().forEach(pTask -> {
					ItemStack taskInfo = new ItemStack(Material.PAPER);
					
					ItemMeta im = taskInfo.getItemMeta();
					im.setDisplayName("§e"+pTask.getTask().getMessage().replace(": %stan%", ""));
					
					String progress = "§aPostep: "+pTask.getProgress().split(": ")[1];
					List<String> lore = new LinkedList<>();
					lore.add(progress);
					lore.add("§aTyp zadania: §7"+pTask.getTask().getClass().getSimpleName());
					
					im.setLore(lore);
					taskInfo.setItemMeta(im);
					
					NBTItem nbt = new NBTItem(taskInfo);
					nbt.setString("task-id", pTask.getTask().getId());
					nbt.applyNBT(taskInfo);
					
					contents.set(slot.getAndIncrement(), taskInfo);
				});
			}
		};
	}
	
	private InventoryProvider getTaskEditProvider(PlayerTask pTask) {
		return new InventoryProvider() {
			@Override
			public void init(Player player, InventoryContents contents) {
				Player owner = pTask.getPlayer();
				AQuest quest = pTask.getQuest();
				ATask task = pTask.getTask();
				
				PlayerManager.get().getQuestPlayer(owner).ifPresent(qp -> {
					APlayerQuest pQuest = qp.getActiveQuests().get(quest);
					if(pQuest == null)
						return;
					
					ItemStack taskInfo = new ItemStack(Material.KNOWLEDGE_BOOK);{
						ItemMeta im = taskInfo.getItemMeta();
						if(pQuest instanceof PlayerStandardQuest)
							im.setDisplayName("§a§lZadanie§r: "+quest.getDisplay());
						else if(pQuest instanceof PlayerZlecenieQuest)
							im.setDisplayName("§e§lZlecenie§r: "+quest.getDisplay());
						else if(pQuest instanceof PlayerDailyQuest)
							im.setDisplayName(quest.getDisplay());
						else if(pQuest instanceof PlayerWorldQuest)
							im.setDisplayName("§d§lZadanie swiatowe§r: "+quest.getDisplay());
						else if(pQuest instanceof PlayerDungeonQuest)
							im.setDisplayName("§c§lDungeon§r: "+quest.getDisplay());
						List<String> lore = new LinkedList<>();
						lore.add("§eZlecenodawca: §r"+quest.getQuestGiver());
						lore.add("§eQuest ID: §7[§f"+quest.getId()+"§7]");
						lore.add("§eTask ID: §7[§f"+task.getId()+"§7]");
						lore.add("§ePostep: §r"+pTask.getProgress());
						lore.add("§eCel: §r"+task.getTarget());
						lore.add("§eTyp: §7"+task.getClass().getSimpleName());
						im.setLore(lore);
						taskInfo.setItemMeta(im);
						
						NBTItem nbt = new NBTItem(taskInfo);
						nbt.setString("quest-owner", owner.getUniqueId().toString());
						nbt.setString("quest-id", quest.getId());
						nbt.setString("task-id", task.getId());
						nbt.applyNBT(taskInfo);
					}

					contents.set(0, deleteTask);
					if(pTask.isCompleted())
						contents.set(1, uncompleteTask);
					else
						contents.set(1, completeTask);
					contents.set(2, changeTargetTask);
					contents.set(3, changeMessageTask);
					if(task instanceof FishTask || task instanceof GiveTask || task instanceof KillTask
							|| task instanceof PlayerKillTask || task instanceof PointsTask) {
						contents.set(4, changeProgressTask);
						contents.set(5, changeAmountTask);
					}
					if(task instanceof PlayerKillTask)
						contents.set(6, changeLevelTask);
					if(task instanceof FishTask)
						contents.set(6, changeInRowTask);
					contents.set(7, taskInfo);
					contents.set(8, back);
				});
			}
		};
	}
	
	private InventoryProvider getTaskEditAnvilProvider(PlayerTask pTask, String defaultName, String type) {
		return new InventoryProvider() {
			@Override
			public void anvil(Player player, Builder anvil) {
				Player owner = pTask.getPlayer();
				AQuest quest = pTask.getQuest();
				ATask task = pTask.getTask();
				
				PlayerManager.get().getQuestPlayer(owner).ifPresent(qp -> {
					APlayerQuest pQuest = qp.getActiveQuests().get(quest);
					if(pQuest == null)
						return;
					
					ItemStack taskInfo = new ItemStack(Material.PAPER);{
						ItemMeta im = taskInfo.getItemMeta();
						im.setDisplayName(defaultName);
						taskInfo.setItemMeta(im);
					}

					anvil.itemLeft(taskInfo);
					anvil.onClick((slot, _anvil) -> {
						if(slot != 2)
							return Arrays.asList();
						ItemStack it = _anvil.getOutputItem();
						if(it == null || it.getType().equals(Material.AIR))
							return Arrays.asList();

						String text = _anvil.getText();
						switch(type.toLowerCase()) {
							case "target":
							{
								task.setTarget(ChatColor.translateAlternateColorCodes('&', text));
								FileManager.updateTaskQuest(type, pTask, pQuest);
							}
								break;
							case "message":
							{
								text += ": %stan%";
								task.setTarget(ChatColor.translateAlternateColorCodes('&', text));
								FileManager.updateTaskQuest(type, pTask, pQuest);
							}
								break;
							case "progress":
							{
								if(!StringUtils.isNumeric(text))
									break;
								int progress = Integer.parseInt(text);
								pQuest.getTasks().stream()
									.filter(pTask -> pTask.getTask().equals(task))
									.findAny()
									.ifPresent(pTask -> {
										pTask.setProgress(progress);
									});
							}
								break;
							case "amount":
							{
								if(!StringUtils.isNumeric(text))
									break;
								int amount = Integer.parseInt(text);
								try {
									Field field = task.getClass().getDeclaredField("amount");
									field.setAccessible(true);
									field.set(task, amount);
									FileManager.updateTaskQuest(type, pTask, pQuest);
								} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
										| SecurityException e) {
									e.printStackTrace();
								}
							}
								break;
							case "level":
							{
								if(!StringUtils.isNumeric(text))
									break;
								int level = Integer.parseInt(text);
								((PlayerKillTask) task).setLevel(level);
								FileManager.updateTaskQuest(type, pTask, pQuest);
							}
								break;
							case "fish":
							{
								if(!StringUtils.isNumeric(text))
									break;
								boolean inRow = Integer.parseInt(text) == 0 ? false : true;
								((FishTask) task).setInRow(inRow);
								FileManager.updateTaskQuest(type, pTask, pQuest);
							}
								break;
						}
						
						return Arrays.asList(ResponseAction.close());
					});
				});
			}
		};
	}
	
}
