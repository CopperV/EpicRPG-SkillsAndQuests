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

public class KowalstwoItem extends LearnItem {

	public KowalstwoItem(Collection<IRequirement> requirements) {
		super("Kowalstwo", "§4§lRzemioslo: §cKowalstwo", 10, requirements);
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getRzemiosla().hasKowalstwo())
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

		p.sendMessage(npc.getName()+"§r: §aDobra, wez ten mlot kowalski. Zobaczysz, to nic trudnego!");
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getRzemiosla().setKowalstwo(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new KowalstwoItem(new LinkedList<>(requirements));
	}

}
