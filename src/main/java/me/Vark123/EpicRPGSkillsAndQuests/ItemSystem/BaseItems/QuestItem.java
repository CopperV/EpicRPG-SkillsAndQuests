package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.TakeItemRequirement;

@Getter
@Setter
public abstract class QuestItem extends AEpicItem {

	protected AQuest quest;
	
	public QuestItem(AQuest quest) {
		super(quest.getId());
		this.quest = quest;
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = new ItemStack(Material.GREEN_TERRACOTTA);
		
		ItemMeta im = it.getItemMeta();
		quest.getRequirements().stream()
			.filter(req -> !req.checkRequirement(p))
			.findFirst()
			.ifPresentOrElse(req -> {
				it.setType(Material.BLACK_TERRACOTTA);
				im.setDisplayName("§c§lXXXXX");
				List<String> lore = new LinkedList<>();
				lore.add(" ");
				lore.add("§c§l§nWYMAGANIA");
				quest.getRequirements().forEach(check -> {
					lore.add("§4§l» "+check.getRequirementInfo()+" "
							+(check.checkRequirement(p) ? 
									EpicRPGSkillsAndQuestsAPI.get().getGreenInfo() 
									: EpicRPGSkillsAndQuestsAPI.get().getRedInfo()));
				});
				im.setLore(lore);
			}, () -> {
				im.setDisplayName(quest.getDisplay());
				im.setLore(quest.getLore());
			});
		it.setItemMeta(im);
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			if(qp.getActiveQuests().containsKey(quest))
				it.setType(Material.YELLOW_TERRACOTTA);
			if(qp.getCompletedQuests().contains(quest.getId()))
				it.setType(Material.RED_TERRACOTTA);
		});
		
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		if(!info.getType().equals(Material.GREEN_TERRACOTTA))
			return true;
		quest.getRequirements().stream()
			.filter(req -> req instanceof TakeItemRequirement)
			.forEach(req -> ((TakeItemRequirement) req).takeItems(p));
		return true;
	}
	
}
