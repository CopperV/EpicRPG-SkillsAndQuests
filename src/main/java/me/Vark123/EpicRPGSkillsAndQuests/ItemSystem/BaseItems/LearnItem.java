package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

@Getter
@Setter
public abstract class LearnItem extends AEpicItem {

	protected String name;
	protected int price;
	protected Collection<IRequirement> requirements;
	
	public LearnItem(String id, String name, int price, Collection<IRequirement> requirements) {
		super(id);
		this.name = name;
		this.price = price;
		this.requirements = requirements;
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		
		ItemStack it = new ItemStack(Material.GREEN_TERRACOTTA);
		
		if(rpg.getInfo().getPn() < price)
			it.setType(Material.RED_TERRACOTTA);
			
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(name);
		
		List<String> lore = new LinkedList<>();
		lore.add("§2Koszt: §a"+price+" §2Punktow Nauki");
		lore.add("§2Posiadasz §a"+rpg.getInfo().getPn()+" §2Punktow Nauki");
		requirements.stream()
			.filter(req -> !req.checkRequirement(p))
			.findFirst()
			.ifPresent(req -> {
				it.setType(Material.BLACK_TERRACOTTA);
				lore.add(" ");
				lore.add("§c§l§nWYMAGANIA");
				requirements.forEach(check -> {
					lore.add("§4§l» "+check.getRequirementInfo()+" "
							+(check.checkRequirement(p) ? 
									EpicRPGSkillsAndQuestsAPI.get().getGreenInfo() 
									: EpicRPGSkillsAndQuestsAPI.get().getRedInfo()));
				});
			});
		im.setLore(lore);
		
		it.setItemMeta(im);
		
		return it;
	}

}
