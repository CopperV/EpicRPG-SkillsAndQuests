package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.GMSystem.MenuSystem;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;
import io.github.rysefoxx.inventory.plugin.other.EventCreator;
import lombok.Getter;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.Config;
import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.TaskManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.FishTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.PlayerKillTask;

@Getter
public class GMMenuClickEvents {

private static final GMMenuClickEvents container = new GMMenuClickEvents();
	
	private final EventCreator<InventoryClickEvent> questsClickEvent;
	private final EventCreator<InventoryClickEvent> questEditClickEvent;
	private final EventCreator<InventoryClickEvent> taskEditClickEvent;
	
	private GMMenuClickEvents() {
		questsClickEvent = questsClickEventCreator();
		questEditClickEvent = questEditClickEventCreator();
		taskEditClickEvent = taskEditClickEventCreator();
	}
	
	public static final GMMenuClickEvents get() {
		return container;
	}
	
	private EventCreator<InventoryClickEvent> questsClickEventCreator() {
		Consumer<InventoryClickEvent> event = e -> {
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
					GMMenuManager.get().openQuestEditMenu(viewer, p, pQuest);
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
	
	private EventCreator<InventoryClickEvent> questEditClickEventCreator() {
		Consumer<InventoryClickEvent> event = e -> {
			Player viewer = (Player) e.getWhoClicked();
			Inventory inv = e.getView().getTopInventory();
			if(e.getClickedInventory() == null || !e.getClickedInventory().equals(inv))
				return;
			
			ItemStack it = inv.getItem(e.getSlot());
			if(it == null || it.getType().equals(Material.AIR))
				return;

			NBTItem nbt = new NBTItem(inv.getItem(7));
			if(!(nbt.hasTag("quest-owner") && nbt.hasTag("quest-id")))
				return;
			String questId = nbt.getString("quest-id");
			UUID uid = UUID.fromString(nbt.getString("quest-owner"));
			Player p = Bukkit.getPlayer(uid);
			
			Optional<QuestPlayer> oQuestPlayer = PlayerManager.get().getQuestPlayer(p);
			Optional<AQuest> oQuest = QuestManager.get().getQuestById(questId);
			if(oQuestPlayer.isEmpty() || oQuest.isEmpty()) {
				viewer.closeInventory();
				return;
			}
			
			QuestPlayer qp = oQuestPlayer.get();
			AQuest quest = oQuest.get();
			
			if(it.equals(GMMenuManager.get().getCompleteQuest())) {
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				if(pQuest == null)
					return;
				pQuest.endQuest();
				viewer.closeInventory();
				Bukkit.getLogger().log(Level.INFO, viewer.getName()+" has ended quest "+quest.getDisplay()+" ["+quest.getId()+"] for "+p.getName());
				return;
			}
			if(it.equals(GMMenuManager.get().getRemoveQuest())) {
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				if(pQuest == null)
					return;
				pQuest.removeQuest();
				viewer.closeInventory();
				Bukkit.getLogger().log(Level.INFO, viewer.getName()+" has removed quest "+quest.getDisplay()+" ["+quest.getId()+"] from "+p.getName());
				return;
			}
			if(it.equals(GMMenuManager.get().getUpdateQuest())) {
				return;
			}
			if(it.equals(GMMenuManager.get().getDegradeQuest())) {
				return;
			}
			if(it.equals(GMMenuManager.get().getBack())) {
				GMMenuManager.get().openBaseMenu(viewer, p);
				return;
			}
			if(!it.getType().equals(Material.PAPER))
				return;
			
			NBTItem taskNBT = new NBTItem(it);
			if(!taskNBT.hasTag("task-id"))
				return;
			
			APlayerQuest pQuest = qp.getActiveQuests().get(quest);
			if(pQuest == null)
				return;
			
			String taskId = taskNBT.getString("task-id");
			TaskManager.get().getTaskById(taskId).ifPresent(task -> {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresentOrElse(pTask -> {
						GMMenuManager.get().openTaskEditMenu(viewer, p, pTask);
					}, () -> {
						viewer.closeInventory();
					});
			});
		};
		
		EventCreator<InventoryClickEvent> creator = new EventCreator<>(InventoryClickEvent.class, event);
		return creator;
	}
	
	private EventCreator<InventoryClickEvent> taskEditClickEventCreator() {
		Consumer<InventoryClickEvent> event = e -> {
			Player viewer = (Player) e.getWhoClicked();
			Inventory inv = e.getView().getTopInventory();
			if(e.getClickedInventory() == null || !e.getClickedInventory().equals(inv))
				return;
			
			ItemStack it = inv.getItem(e.getSlot());
			if(it == null || it.getType().equals(Material.AIR))
				return;

			NBTItem nbt = new NBTItem(inv.getItem(7));
			if(!(nbt.hasTag("quest-owner") && nbt.hasTag("quest-id")))
				return;
			String taskId = nbt.getString("task-id");
			String questId = nbt.getString("quest-id");
			UUID uid = UUID.fromString(nbt.getString("quest-owner"));
			Player p = Bukkit.getPlayer(uid);
			
			Optional<QuestPlayer> oQuestPlayer = PlayerManager.get().getQuestPlayer(p);
			Optional<AQuest> oQuest = QuestManager.get().getQuestById(questId);
			Optional<ATask> oTask = TaskManager.get().getTaskById(taskId);
			if(oQuestPlayer.isEmpty() || oQuest.isEmpty() || oTask.isEmpty()) {
				viewer.closeInventory();
				return;
			}
			
			QuestPlayer qp = oQuestPlayer.get();
			AQuest quest = oQuest.get();
			ATask task = oTask.get();
			APlayerQuest pQuest = qp.getActiveQuests().get(quest);
			if(pQuest == null)
				return;

			if(it.equals(GMMenuManager.get().getDeleteTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						pQuest.getTasks().remove(pTask);
						DatabaseManager.updatePlayerStageQuest(pQuest);
					});
				viewer.closeInventory();
				return;
			}
			if(it.equals(GMMenuManager.get().getCompleteTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						pTask.setCompleted(true);
						p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);
						pQuest.tryAutoudateQuest();
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
					});
				viewer.closeInventory();
				return;
			}
			if(it.equals(GMMenuManager.get().getUncompleteTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						pTask.setCompleted(false);
						p.sendMessage(Main.getInstance().getPrefix()+" §r"+pTask.getProgress());
					});
				viewer.closeInventory();
				return;
			}
			if(it.equals(GMMenuManager.get().getChangeTargetTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
								task.getTarget().replace('§', '&'), "target");
					});
				return;
			}
			if(it.equals(GMMenuManager.get().getChangeMessageTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
								task.getTarget().replace('§', '&').replace(": %stan%", ""), "message");
					});
				return;
			}
			
			if(it.equals(GMMenuManager.get().getChangeProgressTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
								pTask.getIntProgress()+"", "progress");
					});
				return;
			}
			if(it.equals(GMMenuManager.get().getChangeAmountTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						try {
							Field field = task.getClass().getDeclaredField("amount");
							field.setAccessible(true);
							int amount = field.getInt(task);
							GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
									amount+"", "amount");
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e1) {
							e1.printStackTrace();
						}
					});
				return;
			}
			if(it.equals(GMMenuManager.get().getChangeLevelTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
								((PlayerKillTask) task).getLevel()+"", "level");
					});
				return;
			}
			if(it.equals(GMMenuManager.get().getChangeInRowTask())) {
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask().equals(task))
					.findAny()
					.ifPresent(pTask -> {
						GMMenuManager.get().openTaskEditAnvilMenu(viewer, p, pTask,
								((FishTask) task).isInRow()+"", "fish");
					});
				return;
			}
			if(it.equals(GMMenuManager.get().getBack())) {
				GMMenuManager.get().openQuestEditMenu(viewer, p, pQuest);
				return;
			}
		};
		
		EventCreator<InventoryClickEvent> creator = new EventCreator<>(InventoryClickEvent.class, event);
		return creator;
	}
	
}
