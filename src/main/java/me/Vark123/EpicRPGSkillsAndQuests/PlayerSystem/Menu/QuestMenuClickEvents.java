package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Menu;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import io.github.rysefoxx.inventory.plugin.other.EventCreator;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.Config;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;

@Getter
public final class QuestMenuClickEvents {

	private static final QuestMenuClickEvents container = new QuestMenuClickEvents();
	
	private final EventCreator<InventoryClickEvent> questListClickEvent;
	private final EventCreator<InventoryClickEvent> questInfoClickEvent;
	
	private QuestMenuClickEvents() {
		questListClickEvent = questListClickEventCreator();
		questInfoClickEvent = questInfoClickEventCreator();
	}
	
	public static final QuestMenuClickEvents get() {
		return container;
	}
	
	private EventCreator<InventoryClickEvent> questListClickEventCreator() {
		Consumer<InventoryClickEvent> event = e -> {
			if(e.isCancelled())
				return;
			
			Player viewer = (Player) e.getWhoClicked();
			Inventory inv = e.getView().getTopInventory();
			if(e.getClickedInventory() == null || !e.getClickedInventory().equals(inv))
				return;
			
			ItemStack it = inv.getItem(e.getSlot());
			if(it == null || it.getType().equals(Material.AIR))
				return;
			
			NBTItem nbt = new NBTItem(it);
			if(!(nbt.hasTag("quest-owner") && nbt.hasTag("quest-id")))
				return;
			
			String questId = nbt.getString("quest-id");
			UUID uid = UUID.fromString(nbt.getString("quest-owner"));
			Player p = Bukkit.getPlayer(uid);
			if(p == null || !p.isOnline()) {
				viewer.sendMessage(Config.get().getPrefix()+" §7"+p.getName()+" §cwyszedl z serwera");
				viewer.closeInventory();
				return;
			}
			
			PlayerManager.get().getQuestPlayer(p).ifPresentOrElse(qp -> {
				QuestManager.get().getQuestById(questId).ifPresentOrElse(quest -> {
					if(!qp.getActiveQuests().containsKey(quest)) {
						viewer.sendMessage(Config.get().getPrefix()+" §7"+p.getName()+" §cnie wykonuje obecnie zadania §r"+quest.getDisplay());
						viewer.closeInventory();
						return;
					}
					APlayerQuest pQuest = qp.getActiveQuests().get(quest);
					QuestMenuManager.get().openQuestInfoMenu(viewer, p, pQuest);
				}, () -> {
					viewer.sendMessage(Config.get().getPrefix()+" §cQuset o ID §7"+questId+" §cnie istnieje!");
					viewer.closeInventory();
					return;
				});
			}, () -> {
				viewer.sendMessage(Config.get().getPrefix()+" §cBlad do testowania. Zglos to administratorowi!");
				viewer.closeInventory();
				return;
			});
		};
		
		EventCreator<InventoryClickEvent> creator = new EventCreator<>(InventoryClickEvent.class, event);
		return creator;
	}
	
	private EventCreator<InventoryClickEvent> questInfoClickEventCreator() {
		Consumer<InventoryClickEvent> event = e -> {
			if(e.isCancelled())
				return;
			
			Player viewer = (Player) e.getWhoClicked();
			Inventory inv = e.getView().getTopInventory();
			if(e.getClickedInventory() == null || !e.getClickedInventory().equals(inv))
				return;
			
			ItemStack it = inv.getItem(e.getSlot());
			if(it == null || it.getType().equals(Material.AIR))
				return;
			
			NBTItem nbt = new NBTItem(it);
			if(!nbt.hasTag("quest-owner"))
				return;
			
			UUID uid = UUID.fromString(nbt.getString("quest-owner"));
			Player p = Bukkit.getPlayer(uid);
			if(p == null || !p.isOnline()) {
				viewer.sendMessage(Config.get().getPrefix()+" §7"+p.getName()+" §cwyszedl z serwera");
				viewer.closeInventory();
				return;
			}
			
			QuestMenuManager.get().openQuestListMenu(viewer, p);
		};
		
		EventCreator<InventoryClickEvent> creator = new EventCreator<>(InventoryClickEvent.class, event);
		return creator;
	}
	
}
