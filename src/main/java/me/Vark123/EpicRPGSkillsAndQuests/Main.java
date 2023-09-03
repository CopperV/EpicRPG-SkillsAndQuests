package me.Vark123.EpicRPGSkillsAndQuests;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import me.nikl.calendarevents.CalendarEvents;
import me.nikl.calendarevents.CalendarEventsApi;
import net.milkbowl.vault.economy.Economy;

@Getter
public class Main extends JavaPlugin {
	
	@Getter
	private static Main inst;
	
	public static Economy eco;
	public static Permission perm;
	
	private InventoryManager inventoryManager;
	
	private CalendarEventsApi calendar;

	@Override
	public void onEnable() {
		inst = this;
		
		inventoryManager = new InventoryManager(inst);
		inventoryManager.invoke();
		CalendarEvents calend = (CalendarEvents) Bukkit.getPluginManager().getPlugin("CalendarEvents");
		calendar = calend.getApi();
		
		CommandManager.setExecutors();
		ListenerManager.registerListeners();
		FileManager.init();
		DatabaseManager.init();
		FileManager.loadWorldQuests();

		checkEco();
		checkPerm();
	}

	@Override
	public void onDisable() {
		//TODO
		//Zapisywanie graczy
		Bukkit.getOnlinePlayers().forEach(p -> {
			
		});
		FileManager.saveWorldQuests();
		DatabaseManager.clean();
		FileManager.save();
	}
	
	private boolean checkEco() {
		RegisteredServiceProvider<Economy> ecop = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if(ecop == null) {
			return false;
		}
		eco = ecop.getProvider();
		if(eco == null) {
			return false;
		}
		return true;
	}
	
	private boolean checkPerm() {
		RegisteredServiceProvider<Permission> ecop = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
		if(ecop == null) {
			return false;
		}
		perm = ecop.getProvider();
		if(perm == null) {
			return false;
		}
		return true;
	}
	
}
