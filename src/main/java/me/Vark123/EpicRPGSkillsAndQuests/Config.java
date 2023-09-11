package me.Vark123.EpicRPGSkillsAndQuests;

import java.util.Arrays;
import java.util.LinkedList;

import lombok.Getter;
import me.Vark123.EpicRPG.Main;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.EpicItemManager;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo.AlchemiaItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo.JubilerstwoItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo.KowalstwoItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo.LuczarstwoItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Rzemioslo.PlatnerstwoItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.CiosKrytycznyItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.HungerlessItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.MagKrwiItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.MagnetyzmItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.ManaRegenerationItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.PolnocnyBarbarzyncaItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.RozprucieItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.SilaZywiolowItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.SlugaBeliaraItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Skills.UnlimitedArrowsItem;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.InteligencjaStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.KragStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.ManaStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.SilaStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.WalkaStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.WytrzymaloscStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.ZdolnosciStat;
import me.Vark123.EpicRPGSkillsAndQuests.ItemSystem.BaseItems.Impl.Stats.ZrecznoscStat;
import me.Vark123.EpicRPGSkillsAndQuests.Requirements.Impl.LevelRequirement;

@Getter
public final class Config {

	private static final Config conf = new Config();
	
	private String prefix;
	
	private Config() { }
	
	public static final Config get() {
		return conf;
	}
	
	public void init() {
		this.prefix = Main.getInstance().getPrefix();
		
		EpicItemManager.get().registerItem(new AlchemiaItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new JubilerstwoItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new KowalstwoItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new LuczarstwoItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new PlatnerstwoItem(new LinkedList<>()));

		EpicItemManager.get().registerItem(new CiosKrytycznyItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new HungerlessItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new MagKrwiItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new MagnetyzmItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new ManaRegenerationItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new PolnocnyBarbarzyncaItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new RozprucieItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new SilaZywiolowItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new SlugaBeliaraItem(new LinkedList<>()));
		EpicItemManager.get().registerItem(new UnlimitedArrowsItem(new LinkedList<>()));

		EpicItemManager.get().registerItem(new SilaStat("Sila1", "§4§lSila: §c+1", 2, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new SilaStat("Sila5", "§4§lSila: §c+5", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new WytrzymaloscStat("Wytrzymalosc1", "§4§lWytrzymalosc: §c+1", 2, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new WytrzymaloscStat("Wytrzymalosc5", "§4§lWytrzymalosc: §c+5", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new ZrecznoscStat("Zrecznosc1", "§4§lZrecznosc: §c+1", 2, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new ZrecznoscStat("Zrecznosc5", "§4§lZrecznosc: §c+5", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new ZdolnosciStat("Zdolnosci1", "§4§lZdolnosci mysliwskie: §c+1", 2, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new ZdolnosciStat("Zdolnosci5", "§4§lZdolnosci mysliwskie: §c+5", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new ManaStat("Mana1", "§4§lMana: §c+1", 1, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new ManaStat("Mana5", "§4§lMana: §c+5", 5, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new InteligencjaStat("Inteligencja1", "§4§lInteligencja: §c+1", 2, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new InteligencjaStat("Inteligencja5", "§4§lInteligencja: §c+5", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 5));
		EpicItemManager.get().registerItem(new WalkaStat("Walka1", "§4§lWalka: §c+1", 5, 
				new LinkedList<>(), Integer.MAX_VALUE, 1));
		EpicItemManager.get().registerItem(new WalkaStat("Walka2", "§4§lWalka: §c+2", 10, 
				new LinkedList<>(), Integer.MAX_VALUE, 2));

		EpicItemManager.get().registerItem(new KragStat("Krag1", "§4§lKrag: §cI", 2, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(5))), 1));
		EpicItemManager.get().registerItem(new KragStat("Krag2", "§4§lKrag: §cII", 5, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(11))), 2));
		EpicItemManager.get().registerItem(new KragStat("Krag3", "§4§lKrag: §cIII", 8, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(18))), 3));
		EpicItemManager.get().registerItem(new KragStat("Krag4", "§4§lKrag: §cIV", 11, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(24))), 4));
		EpicItemManager.get().registerItem(new KragStat("Krag5", "§4§lKrag: §cV", 15, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(32))), 5));
		EpicItemManager.get().registerItem(new KragStat("Krag6", "§4§lKrag: §cVI", 19, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(41))), 6));
		EpicItemManager.get().registerItem(new KragStat("Krag7", "§4§lKrag: §cVII", 24, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(51))), 7));
		EpicItemManager.get().registerItem(new KragStat("Krag8", "§4§lKrag: §cVIII", 29, 
				new LinkedList<>(Arrays.asList(new LevelRequirement(85))), 8));
	}
	
}
