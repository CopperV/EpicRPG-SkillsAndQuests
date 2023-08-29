package me.Vark123.EpicRPGSkillsAndQuests;

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.EpicItemManager;
import me.Vark123.EpicRPGSkillsAndQuests.NPCSystem.EpicNPCManager;

@Getter
public final class EpicRPGSkillsAndQuestsAPI {

	private static final EpicRPGSkillsAndQuestsAPI api = new EpicRPGSkillsAndQuestsAPI();

	private final Main epicRPGSkillsAndQuests;
	private final EpicItemManager itemManager;
	private final EpicNPCManager npcManager;
	
	private final String greenInfo = "§2§l✔§r";
	private final String redInfo = "§4§l✘§r";
	
	private EpicRPGSkillsAndQuestsAPI() {
		this.epicRPGSkillsAndQuests = Main.getInst();
		this.itemManager = EpicItemManager.get();
		this.npcManager = EpicNPCManager.get();
	}
	
	public static final EpicRPGSkillsAndQuestsAPI get() {
		return api;
	}
	
}
