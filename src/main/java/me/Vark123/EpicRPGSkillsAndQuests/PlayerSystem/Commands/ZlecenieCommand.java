package me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.Commands;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerZlecenieQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.Misc.ZlecenieController;

public class ZlecenieCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("zlecenie"))
			return false;
		if(!(sender instanceof Player))
			return false;
		
		String prefix = Main.getInstance().getPrefix();
		if(args.length == 0 || args[0].equalsIgnoreCase("opusc")) {
			sender.sendMessage(prefix+" §cPoprawne uzycie komendy §c§o/zlecenie:");
			sender.sendMessage("§4§l»  §c§o/zlecenie opusc");
			return false;
		}
		
		Player p = (Player) sender;
		PlayerManager.get().getQuestPlayer(p).ifPresent(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(pQuest -> pQuest instanceof PlayerZlecenieQuest)
				.findAny()
				.ifPresent(pQuest -> {
					p.sendTitle("§e§l ", "§c§lOPUSZCZONO ZLECENIE", 5, 10, 15);
					p.playSound(p, Sound.ENTITY_BLAZE_HURT, 1, 1);
					p.spawnParticle(Particle.SMOKE_LARGE, p.getLocation().add(0, 1.25, 0), 20, 0.7, 0.7, 0.7, 0.05);
					pQuest.removeQuest();
					ZlecenieController.get().addZlecenieCooldown(p, 20*60*15);
				});
		});
		
		return true;
	}

}
