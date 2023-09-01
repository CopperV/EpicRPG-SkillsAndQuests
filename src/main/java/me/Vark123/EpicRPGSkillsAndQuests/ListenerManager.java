package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;

import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCClickListener;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCMenuClickListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerDeathListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerJoinListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerQuitListener;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Listeners.PlayerWorldChangeListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.FindTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.FishTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.GiveTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.KillTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.MobTalkListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.PlayerKillTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.PointsTaskListener;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Listeners.TalkTaskListener;

public final class ListenerManager {

	private ListenerManager() { }
	
	public static void registerListeners() {
		Main inst = Main.getInst();
		
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
	}
	
}
