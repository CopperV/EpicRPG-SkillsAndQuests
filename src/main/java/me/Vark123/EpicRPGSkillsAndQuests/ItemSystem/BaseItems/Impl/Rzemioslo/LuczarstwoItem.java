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

public class LuczarstwoItem extends LearnItem {

	public LuczarstwoItem(Collection<IRequirement> requirements) {
		super("Luczarstwo", "§4§lRzemioslo: §cLuczarstwo", 10, requirements);
	}

	@Override
	public ItemStack getItem(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ItemStack it = super.getItem(p);
		if(rpg.getRzemiosla().hasLuczarstwo())
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

		p.sendMessage(npc.getName()+"§r: §aDobry luk nie powinien trzaskac podczas napinania. Dobor odpowiedniego drewna jest kluczowy. Oto pare przykladow...");
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getRzemiosla().setLuczarstwo(true);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new LuczarstwoItem(new LinkedList<>(requirements));
	}

}
