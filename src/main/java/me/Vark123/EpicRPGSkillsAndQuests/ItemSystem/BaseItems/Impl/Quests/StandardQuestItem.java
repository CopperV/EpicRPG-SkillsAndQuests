package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerStandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.EventCall;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.StandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events.GiveTaskEvent;

@Getter
@Setter
public class StandardQuestItem extends QuestItem {

	public StandardQuestItem(StandardQuest quest) {
		super(quest);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		if(it == null)
			return null;
		
		ItemMeta im = it.getItemMeta();
		switch(it.getType()) {
			case BLACK_TERRACOTTA:
				im.setDisplayName("§aZadanie §r"+im.getDisplayName());
				break;
			case RED_TERRACOTTA:
				im.setDisplayName(quest.getDisplay());
				im.setLore(quest.getLore());
			case GREEN_TERRACOTTA:
				im.setDisplayName("§aZadanie §r"+im.getDisplayName());
				break;
			case YELLOW_TERRACOTTA:
				QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				List<String> newLore = pQuest.getTasks().stream()
						.map(pTask -> pTask.getProgress())
						.collect(Collectors.toList());
				TaskGroup taskGroup = quest.getTaskGroups().get(pQuest.getStage() + 1);
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
				im.setLore(newLore);
				im.setDisplayName("§aZadanie §r"+quest.getDisplay());
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
					Collection<PlayerTask> playerTasks = quest.getTaskGroups().get(1)
							.getTasks().stream()
							.map(task -> new PlayerTask(p, quest, task, 0, false))
							.collect(Collectors.toList());
					
					APlayerQuest pQuest = new PlayerStandardQuest(p, quest, 1, playerTasks);
					qp.getActiveQuests().put(quest, pQuest);
					
					quest.getTaskGroups().get(1).getEventsByType(EventCall.START)
						.ifPresent(event -> event.executeEvent(pQuest));
					
					p.sendTitle("§e§lROZPOCZALES ZADANIE", quest.getDisplay(), 5, 10, 15);
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
		return new StandardQuestItem((StandardQuest) quest);
	}

}
