package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.Vark123.EpicRPG.FightSystem.Modifiers.DamageModifier;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.DungeonQuest;

public class DungeonDamageListener implements DamageModifier {
	
	@Override
	public double modifyDamage(Entity damager, Entity victim, double damage, DamageCause cause) {
		Entity trueDamager = damager;
		if(damager instanceof Projectile) {
			trueDamager = (Entity) ((Projectile) damager).getShooter();
		}
		Player p = (Player) trueDamager;
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
		return damage;
	}

}
