package me.Vark123.EpicRPGSkillsAndQuests.Prizes.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPG.Players.Components.RpgPlayerInfo;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.Prizes.IPrize;

public class ClassPrize implements IPrize {

	private String _class;
	
	public ClassPrize(String _class) {
		this._class = ChatColor.translateAlternateColorCodes('&', _class);
	}
	
	@Override
	public void givePrize(Player p) {
		RpgPlayer rpg = me.Vark123.EpicRPG.Players.PlayerManager
				.getInstance().getRpgPlayer(p);
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
		
		RpgPlayerInfo info = rpg.getInfo();
		info.setProffesion(_class);
		if(ChatColor.stripColor(_class).equalsIgnoreCase("mag")) {
			p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100+(info.getLevel()-1)*6);
			rpg.getStats().setHealth(100 + (info.getLevel()-1) * 6);
		}
		
		List<APlayerQuest> activeClassQuests = qp.getActiveQuests().values().stream()
				.filter(quest -> quest.getQuest().getId().startsWith("klasa_quest_"))
				.collect(Collectors.toList());
		activeClassQuests.forEach(quest -> {
			quest.removeQuest();
		});
		List<String> completedClassQuests = qp.getCompletedQuests().stream()
				.filter(quest -> quest.startsWith("klasa_quest_"))
				.collect(Collectors.toList());
		completedClassQuests.forEach(quest -> qp.getCompletedQuests().remove(quest));
	}

	@Override
	public String getPrizeInfo() {
		return "§eKlasa: §r"+_class;
	}

	
	
}
