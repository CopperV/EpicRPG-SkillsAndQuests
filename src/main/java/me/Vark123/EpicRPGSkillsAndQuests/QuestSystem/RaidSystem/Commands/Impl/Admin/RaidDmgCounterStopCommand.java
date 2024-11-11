package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerManager;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

public class RaidDmgCounterStopCommand extends AAdminRaidCommand {

	public RaidDmgCounterStopCommand() {
		super("counter-stop", new String[] {});
	}

	@Override
	public boolean canUse(Player player) {
		return super.canUse(player);
	}

	@Override
	public boolean useCommand(Player sender, String... args) {
		if(args == null || args.length < 1)
			return false;
		Player p = Bukkit.getPlayerExact(args[0]);
		if(p == null || !p.isOnline()) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §7"+args[0]+" §ejest offline!");
			return false;
		}
		
		MutableObject<String> message = new MutableObject<>("");
		MutableBoolean result = new MutableBoolean(true);
		PlayerManager.get().getQuestPlayer(p).ifPresentOrElse(qp -> {
			qp.getActiveQuests().values().stream()
				.filter(quest -> quest instanceof PlayerRaidQuest)
				.map(quest -> (PlayerRaidQuest) quest)
				.filter(raid -> raid.isDamageFlag())
				.findAny()
				.ifPresentOrElse(raid -> {
					raid.setDamageFlag(false);
				}, () -> {
					message.setValue(RaidManager.get().getRaidPrefix()
							+" §7"+p.getName()+" §enie ma rajda z odblokowanym licznikiem obrazen!");
					result.setFalse();
				});
		}, () -> {
			message.setValue(RaidManager.get().getRaidPrefix()
					+" §7"+p.getName()+" §enie ma zadnych questow! §7[§4§lBUG§7]");
			result.setFalse();
		});
		
		if(result.isFalse())
			sender.sendMessage(message.getValue());
		return result.booleanValue();
	}

	@Override
	public void showCorrectUsage(Player sender) {
		super.showCorrectUsage(sender);
		sender.sendMessage("  §f§o/raid counter-stop [gracz] §7- Wylacza licznik obrazen na rajdzie");
	}

}
