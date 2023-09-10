package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.Listeners;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.Vark123.EpicRPG.FightSystem.ManualDamage;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerDungeonQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.DungeonSystem.DungeonGroup;

public class PlayerBlockedRegionEntryListener implements Listener {
	
	private static Map<Player, Date> cooldown = new ConcurrentHashMap<>();

	@EventHandler
	public void onEntry(PlayerMoveEvent e) {
		if(e.isCancelled())
			return;
		if(e.getFrom().getBlock().getLocation().equals(e.getTo().getBlock().getLocation()))
			return;
		
		Player p = e.getPlayer();
		if(cooldown.containsKey(p) 
				&& (new Date().getTime() - cooldown.get(p).getTime()) < 2_000)
			return;
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(quest -> quest instanceof PlayerDungeonQuest)
				.map(quest -> (PlayerDungeonQuest) quest)
				.findAny()
				.ifPresent(dungeon -> {
					DungeonGroup group = (DungeonGroup) dungeon.getQuest().getTaskGroups().get(dungeon.getStage());
					Collection<String> blockedRegions = group.getBlockedRegions();
					
					Set<ProtectedRegion> regions = WorldGuard.getInstance().getPlatform()
							.getRegionContainer().createQuery()
							.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()))
							.getRegions();
					if(regions == null || regions.isEmpty()) 
						return;
					
					regions.stream().map(region -> region.getId())
						.filter(blockedRegions::contains)
						.findAny()
						.ifPresent(region -> {
							EntityDamageEvent event = new EntityDamageEvent(p, DamageCause.CUSTOM, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/3.0F);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled())
								return;
							ManualDamage.doDamage(p, event.getFinalDamage(), event);
							
							Location A = e.getTo();
							Location B = e.getFrom();
							Vector knock = new Vector(B.getX() - A.getX(), 0, B.getZ() - A.getZ()).normalize().multiply(2.0).setY(0.5);
							p.setVelocity(knock);
							
							cooldown.put(p, new Date());
						});
				});
		});
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if(e.isCancelled())
			return;
		
		String world1 = e.getFrom().getWorld().getName();
		String world2 = e.getFrom().getWorld().getName();
		if(!world1.equals(world2))
			return;
		
		Player p = e.getPlayer();
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(quest -> quest instanceof PlayerDungeonQuest)
				.map(quest -> (PlayerDungeonQuest) quest)
				.findAny()
				.ifPresent(dungeon -> {
					DungeonGroup group = (DungeonGroup) dungeon.getQuest().getTaskGroups().get(dungeon.getStage());
					Collection<String> blockedRegions = group.getBlockedRegions();
					
					Set<ProtectedRegion> regions = WorldGuard.getInstance().getPlatform()
							.getRegionContainer().createQuery()
							.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()))
							.getRegions();
					if(regions == null || regions.isEmpty()) 
						return;
					
					regions.stream().map(region -> region.getId())
						.filter(blockedRegions::contains)
						.findAny()
						.ifPresent(region -> {
							EntityDamageEvent event = new EntityDamageEvent(p, DamageCause.CONTACT, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/3.0F);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled())
								return;
							ManualDamage.doDamage(p, event.getFinalDamage(), event);
							
							e.setCancelled(true);
						});
				});
		});
	}
	
}
