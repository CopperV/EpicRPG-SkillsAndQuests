package me.Vark123.EpicRPGSkillsAndQuests.ItemSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import lombok.Getter;

@Getter
public final class EpicItemManager {

	private static final EpicItemManager inst = new EpicItemManager();
	
	private final Collection<AEpicItem> items;
	
	private EpicItemManager() {
		items = new HashSet<>();
	}
	
	public static final EpicItemManager get() {
		return inst;
	}
	
	public void registerItem(AEpicItem item) {
		items.add(item);
	}
	
	public Optional<AEpicItem> getItemById(String identifier) {
		return items.stream()
				.filter(item -> item.getId().equals(identifier))
				.findFirst();
	}
	
}
