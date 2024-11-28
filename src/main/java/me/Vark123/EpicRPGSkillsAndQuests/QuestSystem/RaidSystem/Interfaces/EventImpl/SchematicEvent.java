package me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.EventImpl;

import java.io.File;
import java.io.FileInputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

import lombok.Getter;
import me.Vark123.EpicRPGSkillsAndQuests.PlayerSystem.PlayerQuestImpl.PlayerRaidQuest;
import me.Vark123.EpicRPGSkillsAndQuests.QuestSystem.RaidSystem.Interfaces.IRaidEvent;

@Getter
public class SchematicEvent implements IRaidEvent {

	private String schematic;
	private int x,y,z = 0;

	public SchematicEvent(String schematic, int x, int y, int z) {
		super();
		this.schematic = schematic;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String getType() {
		return "SCHEMATIC";
	}

	@Override
	public void doAction(Player p, PlayerRaidQuest pQuest, Object... args) {
        File schematicFile = new File("plugins/FastAsyncWorldEdit/schematics", schematic);
        if (!schematicFile.exists()) {
            Bukkit.getLogger().warning("Schematic file not found: " + schematicFile.getName());
            return;
        }

		String w = pQuest.getWorld();
		TaskManager.taskManager().async(() -> {
			try {
				ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
				Clipboard schematic;
				
				BlockVector3 pasteLocation = new MutableBlockVector3(x, y, z);
				World weWorld = BukkitAdapter.adapt(Bukkit.getWorld(w));
				
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
                e.printStackTrace();
			}
		});
	}

}
