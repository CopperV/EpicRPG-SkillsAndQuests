package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.StatItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class KragStat extends StatItem {
	
	public KragStat(String id, String name,
			int price, Collection<IRequirement> requirements,
			int amount) {
		super(id, name, price, requirements, Integer.MAX_VALUE, amount);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		if(rpg.getStats().getKrag() + 1 < amount)
			it.setType(Material.BLACK_TERRACOTTA);
		if(rpg.getStats().getKrag() >= amount)
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
		
		p.sendMessage(npc.getName()+"§r: §aPoznaj sekrety magii!");
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getStats().setKrag(amount);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new KragStat(name, name, price, new LinkedList<>(requirements), amount);
	}

}
