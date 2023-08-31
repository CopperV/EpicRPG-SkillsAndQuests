package me.Vark123.EpicRPGSkillsAndQuests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.TaskManager;

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
				+ "last_nick VARCHAR(20),"
				+ "uuid VARCHAR(36) UNIQUE);";
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
//		String sql4 = "CREATE OR REPLACE PROCEDURE SavePlayerTask( "
//				+ "IN playerId INT, "
//				+ "IN questId TEXT, "
//				+ "IN questStage INT, "
//				+ "IN taskId TEXT, "
//				+ "IN taskProgress INT, "
//				+ "IN taskComplete TINYINT(1) "
//				+ ") "
//				+ "BEGIN "
//				+ "DECLARE totalTasks INT DEFAULT 0; "
//				+ "SELECT count(*) INTO totalTasks FROM quests WHERE quests.player_id = playerId AND quests.task_id LIKE taskId; "
//				+ "IF totalTasks = 0 THEN "
//				+ "INSERT INTO `quests` (player_id, quest_id, quest_stage, task_id, task_progress, task_complete) VALUES (playerId, questId, questStage, taskId, taskProgress, taskComplete); "
//				+ "ELSE "
//				+ "UPDATE `quests` SET task_progress = taskProgress, task_complete = taskComplete WHERE player_id = playerId AND task_id LIKE taskId; "
//				+ "END IF; "
//				+ "END ";
		try {
			c.createStatement().execute(sql1);
			c.createStatement().execute(sql2);
			c.createStatement().execute(sql3);
//			c.createStatement().execute(sql4);
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
	//Add daily
	public static Map<AQuest, PlayerQuest> getPlayerActiveQuests(Player player) {
		Map<AQuest, PlayerQuest> toReturn = new LinkedHashMap<>();
		
		if(!isPlayerExistsInDatabase(player))
			addPlayerToDatabase(player);
		int id = getPlayerId(player);
		if(id < 0)
			return toReturn;
		
		String sql = "SELECT * FROM `quests` WHERE player_id = "+id+";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			while(set.next()) {
				String questId = set.getString("quest_id");
				int stage = set.getInt("quest_stage");
				String taskId = set.getString("task_id");
				int progress = set.getInt("task_progress");
				boolean complete = set.getBoolean("task_complete");
				QuestManager.get().getQuestById(questId).ifPresent(quest -> {
					TaskManager.get().getTaskById(taskId).ifPresent(task -> {
						PlayerQuest pQuest = toReturn.getOrDefault(quest, new PlayerQuest(player, quest, stage, new LinkedList<>()));
						PlayerTask pTask = new PlayerTask(player, quest, task, progress, complete);
						pQuest.getTasks().add(pTask);
						toReturn.put(quest, pQuest);
					});
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return toReturn;
		}
		
		return toReturn;
	}
	
	public static void savePlayerActiveQuests(Player player) {
		if(!isPlayerExistsInDatabase(player))
			addPlayerToDatabase(player);
		int id = getPlayerId(player);
		if(id < 0)
			return;
		
		List<String> querys = new LinkedList<>();
		PlayerManager.get().getQuestPlayer(player)
			.ifPresent(qp -> {
				qp.getActiveQuests().values().stream().forEach(pQuest -> {
					String questId = pQuest.getQuest().getId();
					int questStage = pQuest.getStage();
					pQuest.getTasks().stream().forEach(pTask -> {
						String taskId = pTask.getTask().getId();
						int progress = pTask.getIntProgress();
						boolean complete = pTask.isCompleted();
						String call = "CALL SavePlayerTask("+id+",\""+questId+"\","+questStage+","
								+ "\""+taskId+"\","+progress+","+complete+");";
						querys.add(call);
					});
				});
			});
		if(!querys.isEmpty()) {
			try {
				try {
					c.setAutoCommit(false);
					for(String query : querys)
						c.createStatement().execute(query);
					c.commit();
				} catch(SQLException e) {
					e.printStackTrace();
					c.rollback();
				} finally {
					c.setAutoCommit(true);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isPlayerExistsInDatabase(Player p) {
		String uid = p.getUniqueId().toString();
		String sql = "SELECT id FROM users WHERE uuid LIKE \""+uid+"\";";
		try {
			return c.createStatement().executeQuery(sql).next();
		} catch (SQLException e) {
			return false;
		}
	}
	
	private static void addPlayerToDatabase(Player p) {
		String sql = "INSERT INTO `users` (last_nick, uuid) "
				+ "VALUES (\""+p.getName()+"\",\""+p.getUniqueId().toString()+"\");";
		try {
			c.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static int getPlayerId(Player p) {
		String uid = p.getUniqueId().toString();
		String sql = "SELECT id FROM users WHERE uuid LIKE \""+uid+"\";";
		try {
			ResultSet set = c.createStatement().executeQuery(sql);
			set.next();
			return set.getInt("id");
		} catch (SQLException e) {
			return -1;
		}
	}
	
}
