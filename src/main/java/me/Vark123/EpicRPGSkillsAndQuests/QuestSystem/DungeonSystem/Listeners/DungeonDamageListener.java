package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicRPG.FightSystem.Events.EpicEffectEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;

public class DungeonDamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEffect(EpicEffectEvent e) {
		if(e.isCancelled())
			return;
		
		Entity damager = e.getDamager();
		if(damager instanceof Projectile)
			damager = (Entity) ((Projectile) damager).getShooter();
		
		if(!(damager instanceof Player))
			return;
		
		Player p = (Player) damager;
		double damage = e.getFinalDamage();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerDungeonQuest)
				.map(pQuest -> (PlayerDungeonQuest) pQuest)
				.filter(dungeon -> dungeon.getStage() >= ((DungeonQuest) dungeon.getQuest()).getDamageCount())
				.findAny()
				.ifPresent(dungeon -> {
					dungeon.addDamage(p, damage);
				});
		});
	}

}
