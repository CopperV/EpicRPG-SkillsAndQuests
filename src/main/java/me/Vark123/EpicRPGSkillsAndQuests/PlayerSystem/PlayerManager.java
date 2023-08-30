package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.DatabaseManager;
import me.Vark123.EpicRPGSkillsAndQuests.FileManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

@Getter
public final class PlayerManager {

	private static final PlayerManager inst = new PlayerManager();
	
	private final Collection<QuestPlayer> players;
	
	private PlayerManager() {
		players = new HashSet<>();
	}
	
	public static final PlayerManager get() {
		return inst;
	}
	
	public QuestPlayer loadQuestPlayer(Player player) {
		Collection<String> completedQuests = FileManager.getPlayerCompletedQuests(player);
		Map<AQuest, PlayerQuest> activeQuests = DatabaseManager.getPlayerActiveQuests(player);
		QuestPlayer qp = new QuestPlayer(player, completedQuests, activeQuests);
		return qp;
	}
	
	public boolean registerPlayer(QuestPlayer player) {
		return players.add(player);
	}
	
	public Optional<QuestPlayer> getQuestPlayer(Player player) {
		return players.stream()
				.filter(qp -> qp.getPlayer().equals(player))
				.findFirst();
	}
	
	public Optional<QuestPlayer> unregisterPlayer(Player player) {
		Optional<QuestPlayer> qp = getQuestPlayer(player);
		if(qp.isEmpty())
			return Optional.empty();
		players.remove(qp.get());
		return qp;
	}
	
}
