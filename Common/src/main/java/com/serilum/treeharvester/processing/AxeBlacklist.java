package com.serilum.treeharvester.processing;

import com.natamus.collective.services.Services;
import com.serilum.treeharvester.data.Constants;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AxeBlacklist {
	public static synchronized void attemptProcessingAxeBlacklist() {
		if (Variables.processedAxeBlacklist) {
			return;
		}

		Variables.processedAxeBlacklist = true;
		try {
			setupAxeBlacklist();
		} catch (IOException ex) {
			System.out.println("[" + Reference.NAME + "] Something went wrong setting up the axe blacklist file.");
		}
	}

	public static boolean isBlacklisted(Item item) {
		attemptProcessingAxeBlacklist();
		return Variables.blacklistedAxes.contains(BuiltInRegistries.ITEM.getKey(item).toString());
	}

	private static void setupAxeBlacklist() throws IOException {
		List<String> listedAxes = new ArrayList<>();
		List<String> blacklist = new ArrayList<>();

		boolean fileExists = Constants.dir.isDirectory() && Constants.file.isFile();
		if (fileExists) {
			String blcontent = new String(Files.readAllBytes(Constants.file.toPath()));
			for (String axerl : blcontent.split("," )) {
				String name = axerl.replace("\n", "").replace("\r", "").trim();
				if (name.startsWith("//")) {
					continue;
				}
				if (name.startsWith("!")) {
					name = name.replace("!", "").trim();
					blacklist.add(name);
				}
				listedAxes.add(name);
			}
		}
		else {
			boolean ignored = Constants.dir.mkdirs();
		}

		Variables.blacklistedAxes = blacklist;

		List<String> unlistedAxes = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (!Services.TOOLFUNCTIONS.isAxe(new ItemStack(item))) {
				continue;
			}

			String name = BuiltInRegistries.ITEM.getKey(item).toString();
			if (!listedAxes.contains(name)) {
				unlistedAxes.add(name);
			}
		}

		if (fileExists && unlistedAxes.isEmpty()) {
			return;
		}

		PrintWriter writer = new PrintWriter(new FileWriter(Constants.file, StandardCharsets.UTF_8, fileExists));
		if (!fileExists) {
			writer.println("// To disable a certain axe from being able to harvest trees, add an exclamation mark (!) in front of the line,");
		}
		for (String name : unlistedAxes) {
			writer.println(name + ",");
		}
		writer.close();
	}
}
