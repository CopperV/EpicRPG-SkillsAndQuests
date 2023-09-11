package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Core.ExpSystem;
import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;

public class XpPrize implements IPrize {

	private int xp;
	
	public XpPrize(int xp) {
		this.xp = xp;
	}

	@Override
	public void givePrize(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		ExpSystem.getInstance().addQuestExp(rpg, xp);
	}

	@Override
	public String getPrizeInfo() {
		return "§eXP: §r"+xp+"§r";
	}

}
