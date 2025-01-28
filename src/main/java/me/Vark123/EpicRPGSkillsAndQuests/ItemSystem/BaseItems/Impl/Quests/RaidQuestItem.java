package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Quests;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.Vark123.EpicParty.PlayerPartySystem.PartyPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.EpicRPGSkillsAndQuestsAPI;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.AEpicItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.QuestItem;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPC;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.APlayerQuest;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.QuestPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskGroup;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Impl.RaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidPlayer.RaidPlayerInfo;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.GiveTask;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.TaskSystem.Impl.Events.GiveTaskEvent;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.FeeItemRequirement;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.TakeItemRequirement;

@Getter
@Setter
public class RaidQuestItem extends QuestItem {

	public RaidQuestItem(RaidQuest quest) {
		super(quest);
	}

	@Override
	public ItemStack getItem(Player p) {
		ItemStack it = super.getItem(p);
		if(it == null)
			return null;
		
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerRaidQuest
						&& !pQuest.getQuest().equals(quest))
				.findAny()
				.ifPresent(pQuest -> it.setType(Material.RED_TERRACOTTA));
		});
		
		if(it.getType().equals(Material.GREEN_TERRACOTTA)) {
			me.Vark123.EpicParty.PlayerPartySystem.PlayerManager.get().getPartyPlayer(p).ifPresent(pp -> {
				if(pp.getParty().isEmpty() || pp.getParty().get().getLeader().equals(pp)) {
					RaidManager.get().getRaidPlayer(p).getRaidInfo().stream()
					.filter(info -> info.getRaidId().equals(quest.getId()))
					.findAny()
					.ifPresent(info -> it.setType(Material.ORANGE_TERRACOTTA));
				}
			});
		}
		
		ItemMeta im = it.getItemMeta();
		switch(it.getType()) {
			case BLACK_TERRACOTTA:
				im.setDisplayName("§6Rajd §r"+im.getDisplayName());
				break;
			case RED_TERRACOTTA:
				im.setDisplayName(quest.getDisplay());
				im.setLore(quest.getLore());
			case ORANGE_TERRACOTTA:
			case GREEN_TERRACOTTA:
				im.setDisplayName("§6Rajd §r"+im.getDisplayName());
				break;
			case YELLOW_TERRACOTTA:
				QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				List<String> newLore = pQuest.getQuestInfo(p);
				TaskGroup taskGroup = quest.getTaskGroups().get(pQuest.getStage() + 1);
				if(taskGroup != null && !taskGroup.getRequirements().isEmpty()) {
					newLore.add(" ");
					newLore.add("§c§l§nWYMAGANIA");
					taskGroup.getRequirements().forEach(check -> {
						newLore.add("§4§l» "+check.getRequirementInfo()+" "
								+(check.checkRequirement(p) ? 
										EpicRPGSkillsAndQuestsAPI.get().getGreenInfo() 
										: EpicRPGSkillsAndQuestsAPI.get().getRedInfo()));
					});
				}
				im.setLore(newLore);
				im.setDisplayName("§6Rajd §r"+quest.getDisplay());
				break;
			default:
				return it;
		}
		it.setItemMeta(im);
		
		return it;
	}

	@Override
	public boolean clickAction(Player p, ItemStack info, EpicNPC npc) {
		if(!info.getType().equals(Material.GREEN_TERRACOTTA) 
				&& !info.getType().equals(Material.ORANGE_TERRACOTTA))
			return true;
		
		quest.getRequirements().stream()
			.filter(req -> req instanceof TakeItemRequirement)
			.forEach(req -> ((TakeItemRequirement) req).takeItems(p));
		quest.getRequirements().stream()
			.filter(req -> req instanceof FeeItemRequirement)
			.forEach(req -> ((FeeItemRequirement) req).takeItems(p));
		
		p.closeInventory();
		QuestPlayer qp = PlayerManager.get().getQuestPlayer(p).get();
		RaidQuest raidQuest = (RaidQuest) quest;
		switch(info.getType()) {
			case GREEN_TERRACOTTA:
				{
					PartyPlayer pp = me.Vark123.EpicParty.PlayerPartySystem.PlayerManager
							.get().getPartyPlayer(p).get();
					if(pp.getParty().isEmpty() ||
							(pp.getParty().isPresent() && pp.getParty().get().getLeader().equals(pp))) {
						
						RaidPlayer rp = RaidManager.get().getRaidPlayer(p);
						rp.getRaidInfo().stream()
							.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
							.findAny()
							.ifPresentOrElse(raidInfo -> { }, () -> {
								rp.getRaidInfo().add(new RaidPlayerInfo(raidQuest));
							});
						
						RaidManager.get().createNewRaid(p, raidQuest);
					} else {
						if (PlayerManager.get().getQuestPlayer(pp.getParty().get().getLeader().getPlayer()).get()
								.getActiveQuests().keySet().stream().filter(quest -> quest instanceof RaidQuest)
								.filter(_quest -> quest.getId().equals(_quest.getId())).findAny().isEmpty())
							return false;
						
						PlayerRaidQuest pQuest = (PlayerRaidQuest) PlayerManager.get()
								.getQuestPlayer(
										pp.getParty().get().getLeader().getPlayer())
								.get()
								.getActiveQuests()
								.values().stream()
								.filter(quest -> quest instanceof PlayerRaidQuest)
								.findAny().get();
						
						RaidPlayer _rp = RaidManager.get().getRaidPlayer(p);
						_rp.getRaidInfo().stream()
							.filter(raidInfo -> raidInfo.getRaidId().equals(raidQuest.getId()))
							.findAny()
							.ifPresentOrElse(raidInfo -> {
								raidInfo.update(pQuest);
							}, () -> {
								RaidPlayerInfo raidInfo = new RaidPlayerInfo(raidQuest);
								raidInfo.update(pQuest);
								_rp.getRaidInfo().add(raidInfo);
							});
						
						
						qp.getActiveQuests().put(pQuest.getQuest(), pQuest);
						pQuest.performAction(_p -> {
							if(!_p.getUniqueId().equals(p.getUniqueId())) {
								_p.sendMessage(EpicRPGSkillsAndQuestsAPI.get().getPrefix()+" §7"+p.getName()+" §edolaczyl do rajdu!");
								return;
							}
							RaidPlayer rp = RaidManager.get().getRaidPlayer(_p);
							rp.getRaidInfo().stream()
								.filter(raidInfo -> raidInfo.getRaidId().equals(quest.getId()))
								.findAny()
								.ifPresentOrElse(raidInfo -> raidInfo.update(pQuest), 
										() -> rp.getRaidInfo().add(new RaidPlayerInfo(raidQuest)));
							
							_p.sendTitle("§6§lDOLACZYLES DO RAJDU", quest.getDisplay(), 5, 10, 15);
							_p.playSound(_p, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1);
							_p.spawnParticle(Particle.TOTEM_OF_UNDYING, _p.getLocation().add(0,1,0), 25, 0.75, 1, 0.75, 0.15);
						});
					}
				}
				break;
			case ORANGE_TERRACOTTA:
				{
					RaidManager.get().openRaidConfigureMenu(p, raidQuest);
				}
				break;
			case YELLOW_TERRACOTTA:
				APlayerQuest pQuest = qp.getActiveQuests().get(quest);
				if(pQuest == null)
					break;
				pQuest.tryUpdateOrEndQuest();
				pQuest.getTasks().stream()
					.filter(pTask -> pTask.getTask() instanceof GiveTask
						&& !pTask.isCompleted())
					.forEach(pTask -> {
						Event event = new GiveTaskEvent(p, pTask);
						Bukkit.getPluginManager().callEvent(event);
					});
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public AEpicItem clone() {
		return new RaidQuestItem((RaidQuest) quest);
	}

}
