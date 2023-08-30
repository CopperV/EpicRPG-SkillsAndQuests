package me.Vark123.EpicRPGSkillsAndQuests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;

//TODO
//Ogarniecie transakcji i commitów
public final class DatabaseManager {

	private static Connection c;
	
	private DatabaseManager() { }
	
	public static void init() {
		FileConfiguration fc = Main.getInst().getConfig();
		Properties prop = new Properties();
		prop.setProperty("user", fc.getString("DB.user"));
		prop.setProperty("password", fc.getString("DB.passwd"));
		prop.setProperty("autoReconnect", "true");
		try {
			c = DriverManager.getConnection("jdbc:mysql://"+fc.getString("DB.ip")+"/"+fc.getString("DB.database")+"?useSSL=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",prop);
		} catch (SQLException e) {
			Main.getInst().getPluginLoader().disablePlugin(Main.getInst());
			return;
		}
		
		String sql1 = "CREATE TABLE IF NOT EXISTS users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "nick VARCHAR(20),"
				+ "uuid VARCHAR(36));";
		String sql2 = "CREATE TABLE IF NOT EXISTS quests ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "player_id INT,"
				+ "quest_id TEXT,"
				+ "quest_stage INT,"
				+ "task_id TEXT,"
				+ "task_progress INT,"
				+ "task_complete BOOLEAN,"
				+ "FOREIGN KEY (player_id) REFERENCES users(id));";
		String sql3 = "CREATE TABLE IF NOT EXISTS daily ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "player_id INT,"
				+ "quest_id TEXT,"
				+ "quest_stage INT,"
				+ "task_id TEXT,"
				+ "task_progress INT,"
				+ "task_complete BOOLEAN,"
				+ "FOREIGN KEY (player_id) REFERENCES users(id));";
		
		try {
			c.createStatement().execute(sql1);
			c.createStatement().execute(sql2);
			c.createStatement().execute(sql3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void clean() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//TODO
	public static Map<AQuest, PlayerQuest> getPlayerActiveQuests(Player player) {
		Map<AQuest, PlayerQuest> toReturn = new LinkedHashMap<>();
	
		return toReturn;
	}
	
	public static void savePlayerActiveQuests(Player player) {
		PlayerManager.get().getQuestPlayer(player)
			.ifPresent(qp -> {
				
			});
	}
	
}
