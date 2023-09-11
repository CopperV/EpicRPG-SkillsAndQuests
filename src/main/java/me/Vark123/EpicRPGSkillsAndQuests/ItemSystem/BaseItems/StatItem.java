package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

@Getter
@Setter
public abstract class StatItem extends LearnItem {

	protected int limit;
	protected int amount;
	
	public StatItem(String id, String name, 
			int price, Collection<IRequirement> requirements,
			int limit, int amount) {
		super(id, name, price, requirements);
		this.limit = limit;
		this.amount = amount;
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		it.setAmount(amount);
		if(!it.getType().equals(Material.GREEN_TERRACOTTA))
			return it;
		return it;
	}

}
