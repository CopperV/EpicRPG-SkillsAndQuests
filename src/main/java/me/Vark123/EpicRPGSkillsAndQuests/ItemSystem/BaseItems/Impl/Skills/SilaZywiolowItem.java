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

public class SilaZywiolowItem extends SkillItem {

	public SilaZywiolowItem(Collection<IRequirement> requirements) {
		super("skill_sila_zywiolow", "§2§lSkill: §e§oSila zywiolow", 10,
				requirements, Arrays.asList("§ePotezny mag jest w stanie korzystac","§eze swoich run bez korzystania ze swojej many","§eunikajac przy tym czasu odnowienia runy"));
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getSkills().hasSilaZywiolow())
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
		rpg.getSkills().setSilaZywiolow(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new SilaZywiolowItem(new LinkedList<>(requirements));
	}

}
