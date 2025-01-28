package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.Vark123.EpicRPG.OldFightSystem.Events.EpicEffectEvent;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;

public class RaidDamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEffect(EpicEffectEvent e) {
		if(e.isCancelled())
			return;
		
		Entity damager = e.getDamager();
		if(damager instanceof Projectile)
			damager = (Entity) ((Projectile) damager).getShooter();
		
		if(!(damager instanceof Player))
			return;
		
		if(!(damager.getWorld().getName().toLowerCase().contains("raid")))
			return;
		
		Player p = (Player) damager;
		double damage = e.getFinalDamage();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest)
				.map(pQuest -> (PlayerRaidQuest) pQuest)
				.filter(raid -> raid.isDamageFlag())
				.findAny()
				.ifPresent(raid -> {
					raid.addDamage(p, damage);
				});
		});
	}

}
