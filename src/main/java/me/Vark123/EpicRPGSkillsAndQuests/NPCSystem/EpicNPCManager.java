package me.Vark123.EpicRPGSkillsAndQuests.NPCSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class EpicNPCManager {

	private static final EpicNPCManager inst = new EpicNPCManager();
	
	private final Collection<EpicNPC> npcs;
	
	private EpicNPCManager() {
		npcs = new HashSet<>();
	}
	
	public static final EpicNPCManager get() {
		return inst;
	}
	
	public void registerNPC(EpicNPC npc) {
		npcs.add(npc);
	}
	
	public Optional<EpicNPC> getNPCByName(String name) {
		return npcs.stream()
				.filter(npc -> npc.getName().equals(name))
				.findFirst();
	}
	
}
