package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.SkillItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class SlugaBeliaraItem extends SkillItem {

	public SlugaBeliaraItem(Collection<IRequirement> requirements) {
		super("skill_sluga_beliara", "§2§lSkill: §e§oSluga Beliara", 10,
				requirements, Arrays.asList("§ePotezny sluga Beliara zdolny wykorzystac","§epelnie mocy Szpona Beliara"));
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getSkills().hasSlugaBeliara())
			it = null;
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		switch(info.getType()) {
			case BLACK_TERRACOTTA:
				p.sendMessage(npc.getName()+"§r: §aNie spelniasz moich wymagan! Nie nauczy Ciebie tego!");
				return false;
			case RED_TERRACOTTA:
				p.sendMessage(npc.getName()+"§r: §aWroc, gdy zdobedziesz wiecej doswiadczenia!");
				return false;
			case GREEN_TERRACOTTA:
				break;
			default:
				return false;
		}
	
		p.sendMessage(npc.getName()+"§r: §aIdz i wykorzystaj nabyte umiejetnosci");
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getSkills().setSlugaBeliara(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new SlugaBeliaraItem(new LinkedList<>(requirements));
	}

}
