package me.Vark123.EpicRPGSkillsAndQuests.Requirements;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.ActiveQuestRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.ClassRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.FeeItemRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.ItemRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.LevelRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.MMItemRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.MaxLevelRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.NoClassRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.PartyRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.QuestRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.RaidPartyRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.RangaRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.ReputationRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.TakeItemRequirement;

public class RequirementManager {
	
	private RequirementManager() { }
	
	public static List<IRequirement> generateRequirements(List<String> lines) {
		List<IRequirement> toReturn = new LinkedList<>();
		lines.forEach(line -> {
			String[] arr = line.split(": ");
			switch(arr[0].toLowerCase()) {
				case "do_quest":
					toReturn.add(new ActiveQuestRequirement(arr[1]));
					break;
				case "item":
					{
						String[] item = arr[1].split(";");
						int amount = Integer.parseInt(item[1]);
						toReturn.add(new ItemRequirement(item[0], amount));
					}
					break;
				case "maxlevel":
					toReturn.add(new MaxLevelRequirement(Integer.parseInt(arr[1])));
					break;
				case "mmitem":
					{
						String[] item = arr[1].split(";");
						int amount = Integer.parseInt(item[1]);
						toReturn.add(new MMItemRequirement(item[0], amount));
					}
					break;
				case "brak przynaleznosci do":
					toReturn.add(new NoClassRequirement(arr[1]));
					break;
				case "party":
					toReturn.add(new PartyRequirement());
					return;
				case "raidparty":
					{
						String raidId = arr[1];
						toReturn.add(new RaidPartyRequirement(raidId));
					}
					return;
				case "quest":
					toReturn.add(new QuestRequirement(arr[1]));
					break;
				case "ranga":
					toReturn.add(new RangaRequirement(arr[1]));
					break;
				case "level":
					toReturn.add(new LevelRequirement(Integer.parseInt(arr[1])));
					break;
				case "klasa":
					toReturn.add(new ClassRequirement(ChatColor.translateAlternateColorCodes('&', arr[1])));
					break;
				case "reputation":
					String[] rep = arr[1].split(";");
					toReturn.add(new ReputationRequirement(rep[0], Integer.parseInt(rep[1])));
					break;
				case "takeitem":
					{
						String[] item = arr[1].split(";");
						int amount = Integer.parseInt(item[1]);
						toReturn.add(new TakeItemRequirement(item[0], amount));
					}
					break;
				case "feeitem":
					{
						String[] tab = arr[1].split(";");
						String questId = tab[0];
						String item = tab[1];
						int amount = Integer.parseInt(tab[2]);
						toReturn.add(new FeeItemRequirement(questId, item, amount));
					}
					break;
			}
		});
		return toReturn;
	}

}
