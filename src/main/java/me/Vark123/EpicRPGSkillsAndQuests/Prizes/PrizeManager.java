package me.Vark123.EpicRPGSkillsAndQuests.Prizes;

import java.util.LinkedList;
import java.util.List;

import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.BroadcastPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.ClassPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.CommandPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.ItemPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.MoneyPrize;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl.XpPrize;

public class PrizeManager {

	private PrizeManager() { }
	
	public static List<IPrize> generatePrizes(List<String> lines) {
		List<IPrize> toReturn = new LinkedList<>();
		lines.forEach(line -> {
			String[] arr = line.split(": ");
			switch(arr[0].toLowerCase()) {
				case "money":
					double money = Double.parseDouble(arr[1]);
					toReturn.add(new MoneyPrize(money));
					break;
				case "xp":
					int xp = Integer.parseInt(arr[1]);
					toReturn.add(new XpPrize(xp));
					break;
				case "item":
					String[] item = arr[1].split(" ");
					int amount = 1;
					if(item.length > 1)
						amount = Integer.parseInt(item[1]);
					toReturn.add(new ItemPrize(item[0], amount));
					break;
				case "klasa":
					toReturn.add(new ClassPrize(arr[1]));
					break;
				case "bc":
					toReturn.add(new BroadcastPrize(arr[1]));
					break;
				case "cmd":
					toReturn.add(new CommandPrize(arr[1]));
					break;
			}
		});
		return toReturn;
	}
	
}
