package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.WorldQuest;

public class WorldQuestItem extends QuestItem {

	public WorldQuestItem(WorldQuest quest) {
		super(quest);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new WorldQuestItem((WorldQuest) quest);
	}

}
