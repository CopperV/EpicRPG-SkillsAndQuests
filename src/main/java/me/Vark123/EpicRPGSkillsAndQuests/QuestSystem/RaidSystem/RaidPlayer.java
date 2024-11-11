package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;

@Getter
public class RaidPlayer {

	private Player player;
	private List<RaidPlayerInfo> raidInfo = new LinkedList<>();
	
	private boolean loaded = false;
	
	public RaidPlayer(Player player) {
		this(player, new LinkedList<>());
	}
	
	public RaidPlayer(Player player, List<RaidPlayerInfo> raidInfos) {
		this.player = player;
		this.raidInfo = raidInfos;
		loaded = true;
	}
	
	@Getter
	public static class RaidPlayerInfo {
		
		private String raidId;
		private List<String> guaranteedDrops;
		private List<String> dropped;
		private List<String> completedObjectives;
		private List<String> completedGroups;
		
		public RaidPlayerInfo(RaidQuest quest) {
			this.raidId = quest.getId();

			this.guaranteedDrops = new LinkedList<>();
			String drop = quest.getDropTables().keySet().stream()
				.filter(mob -> quest.getDropTables().get(mob).stream()
						.filter(dropTable -> dropTable.isGuarantable())
						.findAny().isPresent())
				.collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
					Collections.shuffle(list);
					return list.size() > 0 ? list.get(0) : null;
				}));
			if(drop != null)
				guaranteedDrops.add(drop);
			
			this.dropped = new LinkedList<>();
			this.completedObjectives = new LinkedList<>();
			this.completedGroups = new LinkedList<>();
		}

		public RaidPlayerInfo(String raidId, List<String> guaranteedDrops, List<String> dropped,
				List<String> completedObjectives, List<String> completedGroups) {
			super();
			this.raidId = raidId;
			this.guaranteedDrops = guaranteedDrops;
			this.dropped = dropped;
			this.completedObjectives = completedObjectives;
			this.completedGroups = completedGroups;
		}
		
		public void update(PlayerRaidQuest raidQuest) {
			this.completedObjectives.clear();
			this.completedGroups.clear();
			this.completedObjectives.addAll(raidQuest.getCompletedObjectives());
			this.completedGroups.addAll(raidQuest.getCompletedGroups());
		}
		
	}
	
}
