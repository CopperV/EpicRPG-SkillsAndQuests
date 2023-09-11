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

public class CiosKrytycznyItem extends SkillItem {

	public CiosKrytycznyItem(Collection<IRequirement> requirements) {
		super("skill_cios_krytyczny", "§2§lSkill: §e§oCios krytyczny", 10,
				requirements, Arrays.asList("§eMordercze szkolenie assasynow Archolos","§epod katem precyzyjnych, zabojczych ciosow"));
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getSkills().hasCiosKrytyczny())
			it = null;
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		switch(info.getType()) {
			case BLACK_TERRACOTTA:
				p.sendMessage(npc.getName()+"§r: §aNie spelniasz moich wymagan! Nie naucze Ciebie tego!");
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
		rpg.getSkills().setCiosKrytyczny(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new CiosKrytycznyItem(new LinkedList<>(requirements));
	}

}
