package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

@Getter
@Setter
public abstract class SkillItem extends LearnItem {

	protected List<String> lore;
	
	public SkillItem(String id, String name,
			int price, Collection<IRequirement> requirements,
			List<String> lore) {
		super(id, name, price, requirements);
		this.lore = lore;
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		ItemMeta im = it.getItemMeta();
		List<String> lore = new LinkedList<String>(this.lore);
		lore.add(" ");
		lore.addAll(im.getLore());
		im.setLore(lore);
		it.setItemMeta(im);
		return it;
	}

}
