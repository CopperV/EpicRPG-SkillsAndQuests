package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.StatItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.IRequirement;

public class WytrzymaloscStat extends StatItem {

	private static final List<String> randomMsg = new LinkedList<>(Arrays.asList(
			"Dobrze!",
			"Wlasnie tak",
			"Ekhem...",
			"Praktyka czyni mistrza",
			"Juz stales sie lepszy",
			"Nie przestawaj!"));
	
	public WytrzymaloscStat(String id, String name,
			int price, Collection<IRequirement> requirements,
			int limit, int amount) {
		super(id, name, price, requirements, limit, amount);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		if(rpg.getStats().getWytrzymalosc() + amount > limit)
			it.setType(Material.WHITE_TERRACOTTA);
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
			case WHITE_TERRACOTTA:
				p.sendMessage(npc.getName()+"§r: §aJestes dla mnie zbyt dobry. Nie moge Ciebie niczego wiecej nauczyc.");
				return false;
			case GREEN_TERRACOTTA:
				break;
			default:
				return false;
		}
		
		Random rand = new Random();
		p.sendMessage(npc.getName()+"§r: §a"+randomMsg.get(rand.nextInt(randomMsg.size())));
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		rpg.getInfo().removePN(price);
		rpg.getStats().addWytrzymalosc(amount);
		npc.openMenu(p);
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new WytrzymaloscStat(name, name, price, new LinkedList<>(requirements), limit, amount);
	}

}
