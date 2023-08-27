package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

@Getter
public class Main extends JavaPlugin {

	@Getter
	private static Main inst;

	@Override
	public void onEnable() {
		inst = this;
		
		CommandManager.setExecutors();
		ListenerManager.registerListeners();
		FileManager.init();
		DatabaseManager.init();
	}

	@Override
	public void onDisable() {
		
	}
	
}
