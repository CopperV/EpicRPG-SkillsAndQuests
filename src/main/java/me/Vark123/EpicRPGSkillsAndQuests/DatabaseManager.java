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

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerTask;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDailyQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerStandardQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerWorldQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.AQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.QuestManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.WorldQuestController;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.WorldTask;
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
		String sql4 = "CREATE TABLE IF NOT EXISTS world_quests ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "quest_id TEXT,"
				+ "quest_stage INT,"
				+ "task_id TEXT,"
				+ "task_progress INT,"
				+ "task_complete BOOLEAN);";
		try {
			c.createStatement().execute(sql1);
			c.createStatement().execute(sql2);
			c.createStatement().execute(sql3);
			c.createStatement().execute(sql4);
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
	
	public static Map<AQuest, APlayerQuest> getPlayerActiveQuests(Player player) {
		Map<AQuest, APlayerQuest> toReturn = new LinkedHashMap<>();
		
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
						APlayerQuest pQuest = toReturn.getOrDefault(quest, new PlayerStandardQuest(player, quest, stage, new LinkedList<>()));
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
		
		sql = "SELECT * FROM `daily` WHERE player_id = "+id+";";
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
						APlayerQuest pQuest = toReturn.getOrDefault(quest, new PlayerDailyQuest(player, quest, stage, new LinkedList<>()));
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
		WorldQuestController.get().getActiveQuests().forEach((quest, pQuest) -> {
			toReturn.put(quest, pQuest);
		});
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
					if(!(pQuest instanceof PlayerStandardQuest || pQuest instanceof PlayerDailyQuest))
						return;
					String questId = pQuest.getQuest().getId();
					int questStage = pQuest.getStage();
					pQuest.getTasks().stream().forEach(pTask -> {
						String taskId = pTask.getTask().getId();
						int progress = pTask.getIntProgress();
						boolean complete = pTask.isCompleted();
						String call;
						if(pQuest instanceof PlayerStandardQuest)
							call = "CALL SavePlayerTask("+id+",\""+questId+"\","+questStage+","
								+ "\""+taskId+"\","+progress+","+complete+");";
						else
							call = "CALL SavePlayerDaily("+id+",\""+questId+"\","+questStage+","
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
	
	public static Map<AQuest, PlayerWorldQuest> getActiveWorldQuests() {
		Map<AQuest, PlayerWorldQuest> toReturn = new LinkedHashMap<>();
		
		String sql = "SELECT * FROM `world_quests`;";
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
						PlayerWorldQuest pQuest = toReturn.getOrDefault(quest, new PlayerWorldQuest(quest, stage, new LinkedList<>()));
						PlayerTask pTask = new WorldTask(quest, task, progress, complete);
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
	
	public static void saveWorldQuests() {
		List<String> querys = new LinkedList<>();
		WorldQuestController.get().getActiveQuests().values().stream().forEach(pQuest -> {
			String questId = pQuest.getQuest().getId();
			int questStage = pQuest.getStage();
			pQuest.getTasks().stream().forEach(pTask -> {
				String taskId = pTask.getTask().getId();
				int progress = pTask.getIntProgress();
				boolean complete = pTask.isCompleted();
				String call = "CALL SaveWorldTask(\""+questId+"\","+questStage+","
						+ "\""+taskId+"\","+progress+","+complete+");";
				querys.add(call);
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
	
	public static void updatePlayerStageQuest(APlayerQuest pQuest) {
		Player player = pQuest.getPlayer();
		if(!isPlayerExistsInDatabase(player))
			addPlayerToDatabase(player);
		int id = getPlayerId(player);
		if(id < 0)
			return;

		if(!(pQuest instanceof PlayerStandardQuest || pQuest instanceof PlayerDailyQuest))
			return;
		
		String questId = pQuest.getQuest().getId();
		int questStage = pQuest.getStage();
		String deleteQuery;
		if(pQuest instanceof PlayerStandardQuest)
			deleteQuery = "DELETE FROM quests WHERE player_id = "+id+" AND quest_id LIKE\""+questId+"\";";
		else
			deleteQuery = "DELETE FROM quests WHERE daily = "+id+" AND quest_id LIKE\""+questId+"\";";
		
		try {
			try {
				c.setAutoCommit(false);
				
				List<String> querys = new LinkedList<>();
				pQuest.getTasks().stream().forEach(pTask -> {
					String taskId = pTask.getTask().getId();
					int progress = pTask.getIntProgress();
					boolean complete = pTask.isCompleted();
					String call;
					if(pQuest instanceof PlayerStandardQuest)
						call = "CALL SavePlayerTask("+id+",\""+questId+"\","+questStage+","
							+ "\""+taskId+"\","+progress+","+complete+");";
					else
						call = "CALL SavePlayerDaily("+id+",\""+questId+"\","+questStage+","
								+ "\""+taskId+"\","+progress+","+complete+");";
					querys.add(call);
				});
				c.createStatement().executeUpdate(deleteQuery);
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
	
	public static void updateWorldQuest(PlayerWorldQuest worldQuest) {
		String questId = worldQuest.getQuest().getId();
		int questStage = worldQuest.getStage();
		String deleteQuery = "DELETE FROM world_quests WHERE quest_id LIKE\""+questId+"\";";
		
		try {
			try {
				c.setAutoCommit(false);
				List<String> querys = new LinkedList<>();
				worldQuest.getTasks().stream().forEach(pTask -> {
					String taskId = pTask.getTask().getId();
					int progress = pTask.getIntProgress();
					boolean complete = pTask.isCompleted();
					String call = "CALL SaveWorldTask(\""+questId+"\","+questStage+","
							+ "\""+taskId+"\","+progress+","+complete+");";
					querys.add(call);
				});
				c.createStatement().executeUpdate(deleteQuery);
				for(String query : querys)
					c.createStatement().execute(query);
				c.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				c.rollback();
			} finally {
				c.setAutoCommit(true);
			}
		}  catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePlayerQuest(APlayerQuest pQuest) {
		Player player = pQuest.getPlayer();
		if(!isPlayerExistsInDatabase(player))
			addPlayerToDatabase(player);
		int id = getPlayerId(player);
		if(id < 0)
			return;

		String questId = pQuest.getQuest().getId();
		String deleteQuery = "DELETE FROM quests WHERE player_id = "+id+" AND quest_id LIKE\""+questId+"\";";
		try {
			c.createStatement().executeUpdate(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePlayerDaily(Player player) {
		if(!isPlayerExistsInDatabase(player))
			addPlayerToDatabase(player);
		int id = getPlayerId(player);
		if(id < 0)
			return;

		String deleteQuery = "DELETE FROM daily WHERE player_id = "+id+";";
		try {
			c.createStatement().executeUpdate(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteWorldQuest(PlayerWorldQuest worldQuest) {
		String questId = worldQuest.getQuest().getId();
		String deleteQuery = "DELETE FROM world_quests WHERE quest_id LIKE\""+questId+"\";";
		try {
			c.createStatement().executeUpdate(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearDaily() {
		String polecenie = "TRUNCATE TABLE `daily`;";
		try {
			c.createStatement().executeUpdate(polecenie);
		} catch (SQLException e) {
			System.out.println("Blad czyszczenia daily questow: "+e.getMessage());
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
