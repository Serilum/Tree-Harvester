package com.natamus.treeharvester.processing;

import com.natamus.collective.services.Services;
import com.natamus.treeharvester.data.Constants;
import com.natamus.treeharvester.data.Variables;
import com.natamus.treeharvester.util.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AxeBlacklist {
	public static void attemptProcessingAxeBlacklist(Level level) {
		if (!Variables.processedAxeBlacklist) {
			try {
				setupAxeBlacklist(level);
				Variables.processedAxeBlacklist = true;
			} catch (IOException ex) {
				System.out.println("[" + Reference.NAME + "] Something went wrong setting up the axe blacklist file.");
			}
		}
	}

	public static void setupAxeBlacklist(Level level) throws IOException {
		Registry<Item> itemRegistry = level.registryAccess().lookupOrThrow(Registries.ITEM);
		List<String> blacklist = new ArrayList<String>();

		PrintWriter writer = null;
		if (!Constants.dir.isDirectory() || !Constants.file.isFile()) {
			boolean ignored = Constants.dir.mkdirs();
			writer = new PrintWriter(Constants.dirpath + File.separator + "harvestable_axe_blacklist.txt", StandardCharsets.UTF_8);
		}
		else {
			String blcontent = new String(Files.readAllBytes(Paths.get(Constants.dirpath + File.separator + "harvestable_axe_blacklist.txt")));
			for (String axerl : blcontent.split("," )) {
				String name = axerl.replace("\n", "").trim();
				if (name.startsWith("//")) {
					continue;
				}
				if (name.startsWith("!")) {
					blacklist.add(name.replace("!", ""));
				}
			}
		}

		if (writer != null) {
			writer.println("// To disable a certain axe from being able to harvest trees, add an exclamation mark (!) in front of the line,");
		}

		for (Item item : itemRegistry) {
			if (Services.TOOLFUNCTIONS.isAxe(new ItemStack(item))) {
				ResourceLocation rl = itemRegistry.getKey(item);
				if (rl == null) {
					continue;
				}

				String name = rl.toString();

				if (writer != null) {
					writer.println(name + ",");
				}

				if (!blacklist.contains(name)) {
					Variables.allowedAxes.add(item);
				}
			}
		}

		if (writer != null) {
			writer.close();
		}
	}
}
