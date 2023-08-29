package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.LearnItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class AlchemiaItem extends LearnItem {

	public AlchemiaItem(Collection<IRequirement> requirements) {
		super("Alchemia", "§4§lRzemioslo: §cAlchemia", 30, requirements);
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getRzemiosla().hasAlchemia())
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

		p.sendMessage(npc.getName()+"§r: §aNo wiec sluchaj. Bierzesz menzurke, a nastepnie...");
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getRzemiosla().setAlchemia(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new AlchemiaItem(new LinkedList<>(requirements));
	}

}
