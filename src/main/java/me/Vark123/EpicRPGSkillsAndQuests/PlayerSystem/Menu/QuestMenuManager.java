package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Menu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.DailyPlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.StandardPlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.ZleceniePlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
public final class QuestMenuManager {

	private static final QuestMenuManager inst = new QuestMenuManager();
	
	private final ItemStack back;
	
	private QuestMenuManager() {
		back = new ItemStack(Material.BARRIER);{
			ItemMeta im = back.getItemMeta();
			im.setDisplayName("§c§lPOWROT");
			back.setItemMeta(im);
		}
	}
	
	public static final QuestMenuManager get() {
		return inst;
	}
	
	public void openQuestListMenu(Player viewer, Player player) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		QuestPlayer qp = oQp.get();
		int size = qp.getActiveQuests().size()/9 + 1;
		if(size > 6)
			size = 6;
		RyseInventory.builder()
			.title("§a§lZadania §7"+player.getName())
			.rows(size)
			.disableUpdateTask()
			.listener(QuestMenuClickEvents.get().getQuestListClickEvent())
			.provider(getQuestListProvider(qp))
			.build(Main.getInst())
			.open(viewer);
	}
	
	public void openQuestInfoMenu(Player viewer, Player player, APlayerQuest pQuest) {
		Optional<QuestPlayer> oQp = PlayerManager.get().getQuestPlayer(player);
		if(oQp.isEmpty())
			return;
		QuestPlayer qp = oQp.get();
		int size = pQuest.getTasks().size() / 9 + 2;
		if(size > 6)
			size = 6;
		RyseInventory.builder()
			.title("§e§lZadanie §r"+pQuest.getQuest().getDisplay())
			.rows(size)
			.disableUpdateTask()
			.listener(QuestMenuClickEvents.get().getQuestInfoClickEvent())
			.provider(getQuestInfoProvider(qp, pQuest))
			.build(Main.getInst())
			.open(viewer);
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
					if(pQuest instanceof StandardPlayerQuest)
						im.setDisplayName("§a§lZadanie§r: "+quest.getDisplay());
					else if(pQuest instanceof ZleceniePlayerQuest)
						im.setDisplayName("§e§lZlecenie§r: "+quest.getDisplay());
					else if(pQuest instanceof DailyPlayerQuest)
						im.setDisplayName(quest.getDisplay());
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
	
	private InventoryProvider getQuestInfoProvider(QuestPlayer qp, APlayerQuest pQuest) {
		return new InventoryProvider() {
			@Override
			public void init(Player player, InventoryContents contents) {
				ItemStack newBack = back.clone();
				NBTItem nbt = new NBTItem(newBack);
				nbt.setString("quest-owner", qp.getPlayer().getUniqueId().toString());
				nbt.applyNBT(newBack);
				contents.set(4, newBack);
				
				MutableInt slot = new MutableInt(9);
				pQuest.getTasks().forEach(pTask -> {
					ItemStack taskItem = new ItemStack(Material.PAPER);
					
					ItemMeta im = taskItem.getItemMeta();
					im.setDisplayName("§e"+pTask.getTask().getMessage().replace(": %stan%", ""));
					String progress = "§aPostep: "+pTask.getProgress().split(": ")[1];
					im.setLore(new LinkedList<>(Arrays.asList(progress)));
					taskItem.setItemMeta(im);
					
					contents.set(slot.getAndIncrement(), taskItem);
				});
			}
		};
	}
	
}
