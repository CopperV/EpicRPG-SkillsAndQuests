package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.StandardQuest;

@Getter
@Setter
public class StandardQuestItem extends QuestItem {

	public StandardQuestItem(StandardQuest quest) {
		super(quest);
	}

	//TODO
	//Dodanie sprawdzania czy gracz juz wykonal zadanie
	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		if(it == null)
			return null;
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			if(qp.getCompletedQuests().contains(quest.getId()))
				it.setType(Material.RED_TERRACOTTA);
		});
		
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
			default:
				return it;
		}
		it.setItemMeta(im);
		
		return it;
	}

	@Override
	public AEpicItem clone() {
		return new StandardQuestItem((StandardQuest) quest);
	}

}
