package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;

import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCClickListener;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.Listeners.EpicNPCMenuClickListener;

public final class ListenerManager {

	private ListenerManager() { }
	
	public static void registerListeners() {
		Main inst = Main.getInst();
		
		Bukkit.getPluginManager().registerEvents(new EpicNPCClickListener(), inst);
		Bukkit.getPluginManager().registerEvents(new EpicNPCMenuClickListener(), inst);
	}
	
}
