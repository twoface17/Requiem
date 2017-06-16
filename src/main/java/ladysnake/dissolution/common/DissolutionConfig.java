package ladysnake.dissolution.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class DissolutionConfig {
	
	public static final int NO_FLIGHT = -1;
	public static final int CUSTOM_FLIGHT = 0;
	public static final int PAINFUL_FLIGHT = 1;
	public static final int CREATIVE_FLIGHT = 2;
	public static final int SPECTATOR_FLIGHT = 3;

	public static boolean anchorsXRay = false;
	public static boolean doSableDrop = true;
	public static boolean invisibleGhosts = false;
	public static int flightMode = CUSTOM_FLIGHT;
	public static boolean oneUseWaystone = true;
	public static boolean respawnInNether = true;
	public static boolean soulCompass = true;
	public static boolean soulCompassAnchors = true;
	
	public static void syncConfig() {
		try {
	        Dissolution.config.load();
	        
	        Property versionProp = Dissolution.config.get(
	        		"Don't touch that", 
	        		"version", 
	        		"1.0", 
	        		"The version of this configuration file. Don't modify this number unless you want your changes randomly reset.");

	        // Read props from config
	        Property shouldRespawnInNetherProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	                "shouldRespawnInNether", // Property name
	                "true", // Default value
	                "Whether players should respawn in the nether when they die");
	        
	        Property anchorsXRayProp = Dissolution.config.get(
	        		Configuration.CATEGORY_CLIENT,
	                "anchorsXRay", // Property name
	                "false", // Default value
	                "Whether soul anchors should be visible through blocks to ghost players (graphical glitches might occur)");
	        
	        Property invisibleGhostProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	        		"invisibleGhosts",
	        		"false",
	        		"If set to true, dead players will be fully invisible");
	        
	        Property flightModeProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	        		"flightMode",
	        		"0",
	        		"-1= noflight, 0=custom flight, 1=painful flight, 2=creative, 3=spectator-lite");
	        
	        Property showSoulCompassProp = Dissolution.config.get(
	        		Configuration.CATEGORY_CLIENT,
	                "showSoulCompass", // Property name
	                "true", // Default value
	                "Whether the HUD pointing to respawn locations should appear");
	        
	        Property showAnchorsInSoulCompassProp = Dissolution.config.get(
	        		Configuration.CATEGORY_CLIENT,
	                "showAnchorsInSoulCompass", // Property name
	                "true", // Default value
	                "Whether soul anchors should have an indicator in the soul compass HUD");
	        
	        Property oneUseWaystoneProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	                "oneUseWaystone", // Property name
	                "true", // Default value
	                "Whether Mercurius's waystone should disappear after one use");
	        
	        Property interactableBlocksProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	        		"soulInteractableBlocks",
	        		"lever, glass_pane",
	        		"The blocks that can be right clicked/broken by ghosts (doesn't affect anything currently)");
	        
	        Property doSablePopProp = Dissolution.config.get(
	        		Configuration.CATEGORY_GENERAL,
	        		"doSablePop",
	        		"true",
	        		"Whether output stacks from the extractor should spawn items in world when there is no appropriate container");
	        
	        

	        anchorsXRay = anchorsXRayProp.getBoolean();
	        doSableDrop = doSablePopProp.getBoolean();
	        invisibleGhosts = invisibleGhostProp.getBoolean();
	        flightMode = flightModeProp.getInt();
	        oneUseWaystone = oneUseWaystoneProp.getBoolean();
	        respawnInNether = shouldRespawnInNetherProp.getBoolean();
	        soulCompass = showSoulCompassProp.getBoolean();
	        soulCompassAnchors = showAnchorsInSoulCompassProp.getBoolean();
	        interactableBlocksProp.getArrayEntryClass();
	    } catch (Exception e) {
	    } finally {
	        if (Dissolution.config.hasChanged()) Dissolution.config.save();
	    }
	}
}