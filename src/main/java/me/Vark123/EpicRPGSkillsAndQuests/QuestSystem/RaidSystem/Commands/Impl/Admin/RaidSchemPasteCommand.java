package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Commands.Impl.Admin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;

import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.RaidManager;

public class RaidSchemPasteCommand extends AAdminRaidCommand {

	public RaidSchemPasteCommand() {
		super("paste-schem", new String[] {});
	}

	@Override
	public boolean canUse(CommandSender sender) {
		return super.canUse(sender);
	}

	@Override
	public boolean useCommand(CommandSender sender, String... args) {
		if(args == null || args.length < 3)
			return false;
		
		File schematicFile = new File("plugins/FastAsyncWorldEdit/schematics", args[0]);
        if (!schematicFile.exists()) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §eSchematic §7"+args[0]+" §enie zostal odnaleziony!");
			return false;
        }
		
        org.bukkit.World w = Bukkit.getWorld(args[1]);
		if(w == null) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §eSwiat §7"+args[1]+" §enie istnieje!");
			return false;
		}
		
		List<String> strCoords = Arrays.asList(args[2].split(","));
		if(strCoords.size() != 3) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §eDo wklejenia shcematica potrzebuje 3 wspolrzednych. Otrzymalem §7"+strCoords.size());
			return false;
		}
		List<Integer> coords = strCoords.stream()
				.filter(str -> str.matches("^-?\\d+$"))
				.filter(str -> !str.contains("."))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
		if(coords.size() != 3) {
			sender.sendMessage(RaidManager.get().getRaidPrefix()
					+" §eNie wszystkie podane wspolrzedne sa liczbami");
			return false;
		}
		
		MutableObject<String> message = new MutableObject<>("");
		MutableBoolean result = new MutableBoolean(true);
		
		TaskManager.taskManager().async(() -> {
			try {
				ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
				Clipboard schematic;
				
				BlockVector3 pasteLocation = new MutableBlockVector3(coords.get(0), coords.get(1), coords.get(2));
				World weWorld = BukkitAdapter.adapt(w);

				ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile));
				EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
						.world(weWorld)
						.build();
				
				schematic = clipboardReader.read();
				Operation operation = new ClipboardHolder(schematic)
					.createPaste(editSession)
					.to(pasteLocation)
					.ignoreAirBlocks(false)
					.build();
				
				Operations.complete(operation);
				editSession.close();
				Bukkit.getLogger().info("Schematic pasted successfully: " + schematicFile.getName());
			} catch(Exception e) {
				Bukkit.getLogger().severe("Failed to paste schematic asynchronously with FAWE: " + e.getMessage());
				message.setValue("Failed to paste schematic asynchronously with FAWE: " + e.getMessage());
                result.setFalse();
			}
		});
		
		if(result.isFalse())
			sender.sendMessage(message.getValue());
		return result.booleanValue();
	}

	@Override
	public void showCorrectUsage(CommandSender sender) {
		super.showCorrectUsage(sender);
		sender.sendMessage("  §f§o/raid paste-schem [schem] [world] x,y,z §7- Wkleja schematic na wybranych koordynatach");
	}

}
