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

import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.ZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.ZlecenieController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events.GiveTaskEvent;

public class ZlecenieQuestItem extends QuestItem {

	public ZlecenieQuestItem(ZlecenieQuest quest) {
		super(quest);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		if(it == null)
			return null;
		
		if(ZlecenieController.get().getZlecenieCooldown().contains(p.getUniqueId()))
			it.setType(Material.BLACK_TERRACOTTA);
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerZlecenieQuest
						&& !pQuest.getQuest().equals(quest))
				.findAny()
				.ifPresent(pQuest -> it.setType(Material.RED_TERRACOTTA));
		});
		
		ItemMeta im = it.getItemMeta();
		switch(it.getType()) {
			case BLACK_TERRACOTTA:
				im.setDisplayName("§eZlecenie §r"+im.getDisplayName());
				break;
			case RED_TERRACOTTA:
				im.setDisplayName(quest.getDisplay());
				im.setLore(quest.getLore());
			case GREEN_TERRACOTTA:
				im.setDisplayName("§eZlecenie §r"+im.getDisplayName());
				break;
			case YELLOW_TERRACOTTA:
				QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
				PlayerZlecenieQuest pQuest = (PlayerZlecenieQuest) qp.getActiveQuests().get(quest);
				List<String> newLore = pQuest.getTasks().stream()
						.map(pTask -> pTask.getProgress())
						.collect(Collectors.toList());
				if(pQuest.getLocalTaskGroups().size() < pQuest.getStage() + 1) {
					TaskGroup taskGroup = pQuest.getLocalTaskGroups().get(pQuest.getStage() + 1);
					if(taskGroup != null && !taskGroup.getRequirements().isEmpty()) {
						newLore.add(" ");
						newLore.add("§c§l§nWYMAGANIA");
						taskGroup.getRequirements().forEach(check -> {
							newLore.add("§4§l» "+check.getRequirementInfo()+" "
									+(check.checkRequirement(p) ? 
											EpicRPGSkillsAndQuestsAPI.get().getGreenInfo() 
											: EpicRPGSkillsAndQuestsAPI.get().getRedInfo()));
						});
					}
				}
				im.setLore(newLore);
				im.setDisplayName("§eZlecenie §r"+quest.getDisplay());
				break;
			default:
				return it;
		}
		it.setItemMeta(im);
		
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		super.clickAction(p, info, npc);
		p.closeInventory();
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
		switch(info.getType()) {
			case GREEN_TERRACOTTA:
				{
					List<TaskGroup> taskGroups = ((ZlecenieQuest) quest).getRandomTaskGroups();
					Collection<PlayerTask> playerTasks = taskGroups.get(0)
							.getTasks().stream()
							.map(task -> new PlayerTask(p, quest, task, 0, false))
							.collect(Collectors.toList());
					
					APlayerQuest pQuest = new PlayerZlecenieQuest(p, quest, 0, playerTasks, taskGroups);
					qp.getActiveQuests().put(quest, pQuest);
					
					taskGroups.get(0).getEventsByType(EventCall.START)
						.ifPresent(event -> event.executeEvent(pQuest));
					
					p.sendTitle("§e§lROZPOCZALES ZLECENIE", quest.getDisplay(), 5, 10, 15);
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
		return new ZlecenieQuestItem((ZlecenieQuest) quest);
	}

}
