package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;

import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCClickListener;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCMenuClickListener;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.NPCSpawnListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerDeathListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerJoinListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerQuitListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerWorldChangeListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.DungeonDamageListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.DungeonPortalEntryListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyCreateListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyJoinListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyKickListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyLeaderChangeListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyLeaveListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PartyRemoveListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PlayerBlockedRegionEntryListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners.PlayerMoveOnRespListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.DailyResetListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidBossDeathListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidDamageListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyCreateListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyJoinListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyKickListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyLeaderChangeListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyLeaveListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPartyRemoveListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPlayerBlockedRegionEntryListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPlayerJoinListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPlayerLeaveListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPlayerMoveOnRespListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidPortalEntryListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners.RaidsResetListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.MMExtension.RaidCustomConditionLoadListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.DropTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.FindTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.FishTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.GiveTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.KillTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.MobTalkListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.PlayerKillTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.PointsTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.TalkTaskListener;
import me.nikl.calendarevents.CalendarEventsApi;

public final class ListenerManager {

	private ListenerManager() { }
	
	public static void registerListeners() {
		Main inst = Main.getInst();

		Bukkit.getPluginManager().registerEvents(new NPCSpawnListener(), inst);
		Bukkit.getPluginManager().registerEvents(new EpicNPCClickListener(), inst);
		Bukkit.getPluginManager().registerEvents(new EpicNPCMenuClickListener(), inst);

		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerWorldChangeListener(), inst);
		
		Bukkit.getPluginManager().registerEvents(new FindTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new FishTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new GiveTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new KillTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new MobTalkListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerKillTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PointsTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new TalkTaskListener(), inst);
		Bukkit.getPluginManager().registerEvents(new DropTaskListener(), inst);

		Bukkit.getPluginManager().registerEvents(new DailyResetListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveOnRespListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PlayerBlockedRegionEntryListener(), inst);

		Bukkit.getPluginManager().registerEvents(new PartyCreateListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PartyJoinListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PartyKickListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PartyLeaderChangeListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PartyLeaveListener(), inst);
		Bukkit.getPluginManager().registerEvents(new PartyRemoveListener(), inst);

		Bukkit.getPluginManager().registerEvents(new DungeonDamageListener(), inst);
		Bukkit.getPluginManager().registerEvents(new DungeonPortalEntryListener(), inst);

		Bukkit.getPluginManager().registerEvents(new RaidDamageListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPlayerBlockedRegionEntryListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPlayerJoinListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPlayerLeaveListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPlayerMoveOnRespListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPortalEntryListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyCreateListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyJoinListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyKickListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyLeaderChangeListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyLeaveListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidPartyRemoveListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidBossDeathListener(), inst);
		Bukkit.getPluginManager().registerEvents(new RaidsResetListener(), inst);
		

		Bukkit.getPluginManager().registerEvents(new RaidCustomConditionLoadListener(), inst);
		
		CalendarEventsApi calendar = Main.getInst().getCalendar();
		if(calendar.isRegisteredEvent("reset_daily"))
			calendar.removeEvent("reset_daily");
		calendar.addEvent("reset_daily", "every day", "00:05");
		if(calendar.isRegisteredEvent("reset_raids"))
			calendar.removeEvent("reset_raids");
		calendar.addEvent("reset_raids", "friday", "00:05");
	}
	
}
