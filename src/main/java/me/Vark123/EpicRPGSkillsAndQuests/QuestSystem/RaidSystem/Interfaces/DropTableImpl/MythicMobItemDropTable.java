package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.DropTableImpl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidDropTable;

@AllArgsConstructor
@Getter
public class MythicMobItemDropTable implements IRaidDropTable {

	private String mmId;
	private int amount;
	private double chance;
	private boolean limited;
	
	@Override
	public double getChance() {
		return chance;
	}

	@Override
	public boolean isLimited() {
		return limited;
	}

	@Override
	public void dropToPlayer(Player p) {
		Location loc = p.getLocation().clone().add(0, 0.5, 0);
		ItemStack it = MythicBukkit.inst().getItemManager().getItemStack(mmId, amount);
		if(it == null)
			return;
		
		var item = loc.getWorld().dropItem(loc, it);
		item.setOwner(p.getUniqueId());
	}

}
