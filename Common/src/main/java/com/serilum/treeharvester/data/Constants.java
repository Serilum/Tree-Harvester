package com.serilum.treeharvester.data;

import com.mojang.logging.LogUtils;
import com.natamus.collective.functions.DataFunctions;
import org.slf4j.Logger;

import java.io.File;

public class Constants {
	public static final Logger logger = LogUtils.getLogger();
	public static final String dirpath = DataFunctions.getConfigDirectory() + File.separator + "treeharvester";
	public static final File dir = new File(dirpath);
	public static final File file = new File(dirpath + File.separator + "harvestable_axe_blacklist.txt");
}
