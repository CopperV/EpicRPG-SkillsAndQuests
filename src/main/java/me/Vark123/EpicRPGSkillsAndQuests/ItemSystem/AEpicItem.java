package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;

@Getter
@EqualsAndHashCode
public abstract class AEpicItem {

	private String id;

	public AEpicItem(String id) {
		super();
		this.id = id;
	}
	
	public abstract ItemStack getItem(Player p);
	public abstract boolean clickAction(Player p, ItemStack info, EpicNPC npc);
	public abstract AEpicItem clone();
	
}
