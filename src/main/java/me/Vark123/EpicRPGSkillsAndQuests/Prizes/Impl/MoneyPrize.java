package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.Main;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;
import net.milkbowl.vault.economy.EconomyResponse;

public class MoneyPrize implements IPrize {

	double money;
	
	public MoneyPrize(double money) {
		this.money = money;
	}

	@Override
	public void givePrize(Player p) {
		EconomyResponse r = Main.eco.depositPlayer(p, money);
		if(r.transactionSuccess())
			p.sendMessage("§aOtrzymales §e"+String.format("%.2f", money)+"§a$ na konto. "
					+ "Twoj aktualny stan konta wynosi: §e"+String.format("%.2f", Main.eco.getBalance(p)));
//		Bukkit.getPluginManager().callEvent(new CurrencyModEvent(me.Vark123.EpicRPG.Main.getListaRPG().get(p.getUniqueId().toString()), money, CurrencyType.MONEY));
	}

	@Override
	public String getPrizeInfo() {
		return "§ePieniadze: §r"+String.format("%.2f", money)+"§r";
	}

}
