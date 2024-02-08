package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;

@Getter
@Setter
public class CustomItem extends AEpicItem {
	
	protected Material material;
	protected String display;
	protected List<String> actions;
	
	public CustomItem(String id, Material material, String display, List<String> actions) {
		super(id);
		this.material = material;
		this.display = display;
		this.actions = actions;
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = new ItemStack(material);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(display);
		it.setItemMeta(im);
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		actions.stream()
			.map(cmd -> cmd.replace("%player%", p.getName()))
			.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new CustomItem(id, material, display, new LinkedList<>(actions));
	}
	
	

}
