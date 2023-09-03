package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.DailyController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.ATask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events.GiveTaskEvent;

public class DailyQuestItem extends QuestItem {

	public DailyQuestItem(DailyQuest quest) {
		super(quest);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		if(it == null)
			return null;
		
		if(DailyController.get().getDoneDaily().contains(p.getUniqueId()))
			it.setType(Material.BLACK_TERRACOTTA);
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDailyQuest
						&& !pQuest.getQuest().equals(quest))
				.findAny()
				.ifPresent(pQuest -> it.setType(Material.BLACK_TERRACOTTA));
		});
		
		ItemMeta im = it.getItemMeta();
		switch(it.getType()) {
			case RED_TERRACOTTA:
				im.setDisplayName(quest.getDisplay());
				im.setLore(quest.getLore());
				break;
			case YELLOW_TERRACOTTA:
				QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
				PlayerDailyQuest pQuest = (PlayerDailyQuest) qp.getActiveQuests().get(quest);
				List<String> newLore = pQuest.getTasks().stream()
						.map(pTask -> pTask.getProgress())
						.collect(Collectors.toList());
				im.setLore(newLore);
				im.setDisplayName(quest.getDisplay());
				break;
			default:
				return it;
		}
		it.setItemMeta(im);
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		p.closeInventory();
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
		switch(info.getType()) {
			case GREEN_TERRACOTTA:
				{
					List<ATask> tasks = ((DailyQuest) quest).getRandomTasks();
					Collection<PlayerTask> playerTasks = tasks.stream()
							.map(task -> new PlayerTask(p, quest, task, 0, false))
							.collect(Collectors.toList());
					
					APlayerQuest pQuest = new PlayerDailyQuest(p, quest, 1, playerTasks);
					qp.getActiveQuests().put(quest, pQuest);
					
					quest.getTaskGroups().get(1).getEventsByType(EventCall.START)
						.ifPresent(event -> event.executeEvent(pQuest));
					
					p.sendTitle("§e§lROZPOCZALES", quest.getDisplay(), 5, 10, 15);
					p.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1);
					p.spawnParticle(Particle.TOTEM, p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
				}
				break;
			case YELLOW_TERRACOTTA:
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				if(pQuest == null)
					break;
				pQuest.tryUpdateOrEndQuest();
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof GiveTask
						&& !pTask.isCompleted())
					.forEach(pTask -> {
						Event event = new GiveTaskEvent(p, pTask);
						Bukkit.getPluginManager().callEvent(event);
					});
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new DailyQuestItem((DailyQuest) quest);
	}

}
