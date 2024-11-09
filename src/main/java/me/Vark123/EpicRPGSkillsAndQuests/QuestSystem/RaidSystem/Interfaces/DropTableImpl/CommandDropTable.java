package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.DropTableImpl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidDropTable;
import me.clip.placeholderapi.PlaceholderAPI;

@AllArgsConstructor
@Getter
public class CommandDropTable implements IRaidDropTable {

	private List<String> commands;
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

	//TODO
	//Ogarniecie RAID_WORLD
	@Override
	public void dropToPlayer(Player p) {
		commands.stream()
			.map(line -> PlaceholderAPI.setPlaceholders(p, line))
			.map(line -> line.replace("[RAID_WORLD]", p.getWorld().getName()))
			.forEach(line -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line));
	}

}
