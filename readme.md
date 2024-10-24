<h2>Tree Harvester</h2>
<p><a href="https://github.com/Serilum/Tree-Harvester"><img src="https://serilum.com/assets/data/logo/tree-harvester.png"></a></p><h2>Download</h2>
<p>You can download Tree Harvester on CurseForge and Modrinth:</p><p>&nbsp;&nbsp;CurseForge: &nbsp;&nbsp;<a href="https://curseforge.com/minecraft/mc-mods/tree-harvester">https://curseforge.com/minecraft/mc-mods/tree-harvester</a><br>&nbsp;&nbsp;Modrinth: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://modrinth.com/mod/tree-harvester">https://modrinth.com/mod/tree-harvester</a></p>
<h2>Issue Tracker</h2>
<p>To keep a better overview of all mods, the issue tracker is located in a separate repository.<br>&nbsp;&nbsp;For issues, ideas, suggestions or anything else, please follow this link:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;-> <a href="https://serilum.com/url/issue-tracker">Issue Tracker</a></p>
<h2>Pull Requests</h2>
<p>Because of the way mod loader files are bundled into one jar, some extra information is needed to do a PR.<br>&nbsp;&nbsp;A wiki page entry about it is available here:</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;-> <a href="https://serilum.com/url/pull-requests">Pull Request Information</a></p>
<h2>Mod Description</h2>
<p style="text-align:center"><a href="https://serilum.com/" target="_blank" rel="nofollow"><img src="https://github.com/Serilum/.cdn/raw/main/description/header/header.png" alt="" width="838" height="400"></a></p>
<p style="text-align:center"><a href="https://curseforge.com/members/serilum/projects" target="_blank" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/curseforge.svg" width="200"></a> <a href="https://modrinth.com/user/Serilum" target="_blank" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/modrinth.svg" width="200"></a> <a href="https://patreon.com/serilum" target="_blank" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/patreon.svg" width="200"></a> <a href="https://youtube.com/@serilum" target="_blank" rel="nofollow"><img src="https://raw.githubusercontent.com/Serilum/.data-workflow/main/badges/svg/youtube.svg" width="200"></a></p>
<p><strong><span style="font-size:24px">Requires the library mod <a style="font-size:24px" href="https://curseforge.com/minecraft/mc-mods/collective" target="_blank" rel="nofollow">Collective</a>.</span></strong><br><br><strong>&nbsp;&nbsp;&nbsp;This mod is part of <span style="color:#008000"><a style="color:#008000" href="https://curseforge.com/minecraft/modpacks/the-vanilla-experience" target="_blank" rel="nofollow">The Vanilla Experience</a></span> modpack and <span style="color:#bf8f6f"><a style="color:#bf8f6f" href="https://curseforge.com/minecraft/mc-mods/serilums-qol-bundle" target="_blank" rel="nofollow">Serilum's Quality of Life Bundle</a></span> mod.</strong><br><span style="font-size:18px">Tree Harvester is a configurable mod which allows harvesting trees, warped trees and huge mushrooms instantly by chopping down one base block. This can be any part of the tree. The sapling will automatically be replaced, by using what the tree drops. There are many checks in the mod, from preventing accidental harvesting of houses made from logs to what leaves should decay.<br><br>By default the harvesting only works when holding the crouch button, but this can be toggled in the config. For more feature examples see below. There are lots of config options to tweak the mod exactly to your liking.<br><br>Developed to work alongside my <a style="font-size:18px" href="https://curseforge.com/minecraft/mc-mods/ore-harvester" rel="nofollow">Ore Harvester</a> mod.</span><br><br><br><span style="font-size:24px"><strong>Features with the default configuration settings:</strong></span><br><span style="font-size:18px"><strong>◉&nbsp; &nbsp;</strong>All trees, vanilla and modded, can be harvested quickly by crouching and chopping down one block.<br><strong>◉&nbsp; &nbsp;</strong>The time it takes to chop down a tree is determined by the amount of logs in it.<br><strong>◉&nbsp; &nbsp;</strong>Leaves will decay very quickly after harvesting a tree.<br><strong>◉&nbsp; &nbsp;</strong>Saplings will be replaced after the last leaf has been decayed.<br><strong>◉&nbsp; &nbsp;</strong>Warped trees, in the nether, can be harvested quickly.<br><strong>◉&nbsp; &nbsp;</strong>Huge mushrooms can also be harvested quickly<br><strong>◉&nbsp; &nbsp;</strong>Certain axe types can be exempted from harvesting trees via a blacklist.<br><strong>◉&nbsp; &nbsp;</strong>Many checks to determine what is a tree and what isn't. No accidental destroying of houses.<br><strong>◉&nbsp; &nbsp;</strong>When two types of trees are next to each other, only the leaves of the tree broken will decay.<br><strong>◉&nbsp; &nbsp;</strong>Compatible with other mods.<br></span><br><br><br><strong><span style="font-size:20px">Configurable:</span> <span style="color:#008000;font-size:14px"><a style="color:#008000" href="https://github.com/Serilum/.information/wiki/how-to-configure-mods" rel="nofollow">(&nbsp;how do I configure?&nbsp;)</a></span><br></strong><span style="font-size:12px"><strong>mustHoldAxeForTreeHarvest</strong>&nbsp;(default = true): If enabled, tree harvesting only works when a player is holding an axe in the main hand.</span><br><span style="font-size:12px"><strong>treeHarvestWithoutSneak</strong>&nbsp;(default = false): If enabled, tree harvesting works when not holding the sneak button. If disabled it's reversed, and only works when sneaking.</span><br><span style="font-size:12px"><strong>automaticallyFindBottomBlock</strong>&nbsp;(default = true): Whether the mod should attempt to find the actual bottom log of the tree and start there. This means you can break a tree in the middle and it will still completely be felled.</span><br><br><span style="font-size:12px"><strong>enableFastLeafDecay</strong>&nbsp;(default = true): If enabled, the leaves around a broken tree will quickly disappear.</span><br><span style="font-size:12px"><strong>enableNetherTrees</strong>&nbsp;(default = true): If enabled, the warped stem/crimson trees in the nether will also be chopped down quickly.</span><br><span style="font-size:12px"><strong>enableHugeMushrooms</strong>&nbsp;(default = true): If enabled, giant/huge mushrooms will also be chopped down quickly.<br><strong>ignorePlayerMadeTrees</strong> (default = true): If enabled, trees with leaves placed by players won't be destroyed.</span><br><br><span style="font-size:12px"><strong>replaceSaplingOnTreeHarvest</strong>&nbsp;(default = true): If enabled, automatically replaces the sapling from the drops when a tree is harvested.</span><br><span style="font-size:12px"><strong>replaceMushroomOnMushroomHarvest</strong>&nbsp;(default = true): If enabled, automatically replaces the sapling from the drops when a huge mushroom is harvested and 'enableHugeMushrooms' is enabled.</span><br><br><span style="font-size:12px"><strong>loseDurabilityPerHarvestedLog</strong>&nbsp;(default = true): If enabled, for every log harvested, the axe held loses durability.</span><br><span style="font-size:12px"><strong>loseDurabilityModifier</strong>&nbsp;(default = 1.0, min 0.001, max 1.0): Here you can set how much durability chopping down a tree should take from the axe. For example if set to 0.1, this means that every 10 logs take 1 durability.</span><br><br><span style="font-size:12px"><strong>increaseExhaustionPerHarvestedLog</strong>&nbsp;(default = true): If enabled, players' exhaustion level increases 0.005 per harvested log (Minecraft's default per broken block) * increaseExhaustionModifier.</span><br><span style="font-size:12px"><strong>increaseExhaustionModifier</strong>&nbsp;(default = 1.0, min 0.001, max 1.0): This determines how much exhaustion should be added to the player per harvested log. By default 0.005 * 1.0.</span><br><br><span style="font-size:12px"><strong>increaseHarvestingTimePerLog</strong>&nbsp;(default = true): If enabled, harvesting time will increase per existing log in the tree. The amount is determined by 'increasedHarvestingTimePerLogModifier'.</span><br><span style="font-size:12px"><strong>increasedHarvestingTimePerLogModifier</strong>&nbsp;(default = 0.1, min 0.01, max 10.0): How much longer it takes to harvest a tree with 'increaseHarvestingTimePerLog' enabled. The actual speed is: newSpeed = originalSpeed / (1 + (logCount * increasedHarvestingTimePerLogModifier)).</span><br><br><span style="font-size:12px"><strong>amountOfLeavesBrokenPerTick</strong>&nbsp;(default = 3, min 1, max 16): How many leaves should be broken per tick after a tree has been harvested. Increasing this will speed up the fast leaf decay, but costs more processing power per tick.</span><br><br><br><br><span style="font-size:24px"><strong>Axe Blacklist Feature:</strong></span><br>It's possible to only allow certain axes to harvest trees. On first load of the mod a blacklist file is generated. It is located at <strong><em>./config/treeharvester/harvestable_axe_blacklist.txt</em></strong>. This works for modded axe items as well.<br><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/blacklist.jpg"></picture><br><br><br><span style="font-size:36px"><strong>Some GIFs:</strong></span><br><span style="font-size:18px"><strong>Harvesting a birch tree next to another oak. Only the birch leaves are decaying:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/birch_only.gif"></picture></p>
</div>
<p>&nbsp;<br><span style="font-size:18px"><strong>A big oak tree:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/big_oak_only.gif"></picture></p>
</div>
<p>&nbsp;<br><span style="font-size:18px"><strong>A 2x2 spruce tree. The time it takes to break the tree is longer, and all 4 saplings are replaced correctly:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/big_spruce.gif"></picture></p>
</div>
<p>&nbsp;<br><span style="font-size:18px"><strong>Works with warped (nether) trees:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/nether_tree.gif"></picture></p>
</div>
<p>&nbsp;<br><span style="font-size:18px"><strong>Also for huge mushrooms, here's a brown one:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/brown_mushroom.gif"></picture></p>
</div>
<p>&nbsp;<br><span style="font-size:18px"><strong>And a red mushroom:</strong></span></p>
<div class="spoiler">
<p><picture><img src="https://github.com/Serilum/.cdn/raw/main/projects/tree-harvester/red_mushroom.gif"></picture></p>
</div>
<p><br>------------------<br><br><span style="font-size:24px"><strong>You may freely use this mod in any modpack, as long as the download remains hosted within the CurseForge or Modrinth ecosystem.</strong></span><br><br><span style="font-size:18px"><a style="font-size:18px;color:#008000" href="https://serilum.com/" rel="nofollow">Serilum.com</a> contains an overview and more information on all mods available.</span><br><br><span style="font-size:14px">Comments are disabled as I'm unable to keep track of all the separate pages on each mod.</span><span style="font-size:14px"><br>For issues, ideas, suggestions or anything else there is the&nbsp;<a style="font-size:14px;color:#008000" href="https://github.com/Serilum/.issue-tracker" rel="nofollow">Github repo</a>. Thanks!</span><span style="font-size:6px"><br><br></span></p>
<p style="text-align:center"><a href="https://serilum.com/donate" rel="nofollow"><img src="https://github.com/Serilum/.cdn/raw/main/description/projects/support.svg" alt="" width="306" height="50"></a></p>