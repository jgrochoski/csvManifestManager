/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

/**
 *
 * @author jgroc
 */
public interface Constants {
    public static final String NULL_STRING = "";
    public static final char VERSION_FILE_SEPARATOR = '/';
    public static final String NEW_ID = "new";
    public static final String OVERRIDE_ID = "override";
    public static final String ORIGINAL_ID = "original";
    public static final String INVALID_DIRECTORY = "Invalid Directory";
    public static final String NO_VERSION_MANIFEST = "VersionManifest.csv not found";
    public static final String VERSION_MANIFEST_FILENAME = "VersionManifest.csv";
    public static final int MAX_DEPTH = 20;
    public static final String JSON_EXT = ".json";
    public static final String ID_ID = "id";
    public static final int ID_INDEX = 0;
    public static final String TYPE_ID = "type";
    public static final int TYPE_INDEX = 1;
    public static final String PATH_ID = "path";
    public static final int PATH_INDEX = 2;
    public static final String VERSION_ID = "version";
    public static final int VERSION_INDEX = 3;
    public static final String DEFAULT_VERSION = "1";
    public static final String ADDEDON_ID = "addedOn";
    public static final int ADDEDON_INDEX = 4;
    public static final String DEFAULT_ADDEDON = "2018-02-27T00:45:27.0Z";
    public static final String UPDATEDON_ID = "updatedOn";
    public static final int UPDATEDON_INDEX = 5;
    public static final String DEFAULT_UPDATEDON = "2018-05-21T00:45:27.0Z";
    public static final String REMOVEDON_ID = "removedOn";
    public static final int REMOVEDON_INDEX = 6;
    public static final String DEFAULT_REMOVEDON = "";
    public static final String ASSETBUNDLENAME_ID = "assetBundleName";
    public static final int ASSETBUNDLENAME_INDEX = 7;
    public static final String DEFAULT_ASSETBUNDLENAME = "";
    public static final String ASSETBUNDLEPERSISTENT_ID = "assetBundlePersistent";
    public static final int ASSETBUNDLEPERSISTENT_INDEX = 8;
    public static final String DEFAULT_ASSETBUNDLEPERSISTENT = "FALSE";
    public static final String ASSETBUNDLECRC_ID = "assetBundleCRC";
    public static final int ASSETBUNDLECRC_INDEX = 9;
    public static final String DEFAULT_ASSETBUNDLECRC = "0";
    public static final String ADDENDUM_ID = "addendum";
    public static final int ADDENDUM_INDEX = 10;
    public static final String DEFAULT_ADDENDUM = "FALSE";
    public static final String DATA_DIR_CONFIG_PROP = "dataDirectory";
    public static final String MODS_DIR_CONFIG_PROP = "modsDirectory";
    public static Properties config = readConfig();
    
    public static final DateFormat FILE_DATEFORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static final DateFormat MANIFEST_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    public static final Map<String, Object> prettyPrintProps = createPrettyPrintProps();
    
    public static final ArrayList<String> MANIFEST_HEADERS = createManifestHeaders();
    public static final String[] MANIFEST_HEADERS_ARRAY = MANIFEST_HEADERS.toArray(new String[MANIFEST_HEADERS.size()]);
    public static final Map<String, String> MANIFEST_MAP = createManifestMap();
    public static Map<String, String> createManifestMap() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("abilities", "AbilityDef");
        result.put("ammunition", "AmmunitionDef");
        result.put("ammunitionBox", "AmmunitionBoxDef");
        result.put("audioevents", "AudioEventDef");
        result.put("backgrounds", "BackgroundDef");
        result.put("behaviorVariables", "BehaviorVariableScope");
        result.put("buildings", "BuildingDef");
        result.put("cast", "CastDef");
        result.put("chassis", "ChassisDef");
        result.put("contracts", "ContractOverride");
        result.put("conversationBuckets", "DialogBucketDef");
        result.put("conversations", "ConversationContent");
        //result.put("descriptions", "BaseDescriptionDef");
        result.put("designMasks", "DesignMaskDef");
        result.put("dropship", "DropshipDef");
        result.put("events", "SimGameEventDef");
        result.put("factions", "FactionDef");
        result.put("genderedoptions", "GenderedOptionsListDef");
        result.put("hardpoints", "HardpointDataDef");
        result.put("heatsinks", "HeatSinkDef");
        result.put("heraldry", "HeraldryDef");
        result.put("jumpjets", "JumpJetDef");
        result.put("lance", "LanceDef");
        result.put("lifepathNode", "LifepathNodeDef");
        result.put("mech", "MechDef");
        result.put("mechlabincludes", "MechLabIncludeDef");
        result.put("milestones", "SimGameMilestoneDef");
        result.put("movement", "MovementCapabilitiesDef");
        result.put("nameLists", "SimGameStringList");
        result.put("pathing", "PathingCapabilitiesDef");
        result.put("pilot", "PilotDef");
        result.put("portraits", "PortraitSettings");
        result.put("shipUpgrades", "ShipModuleUpgrade");
        result.put("shops", "ShopDef");
        result.put("simGameConstants", "SimGameConstants");
        result.put("simGameStatDesc", "SimGameStatDescDef");
        result.put("starsystem", "StarSystemDef");
        result.put("turretChassis", "TurretChassisDef");
        result.put("turrets", "TurretDef");
        result.put("upgrades", "UpgradeDef");
        result.put("vehicle", "VehicleDef");
        result.put("vehicleChassis", "VehicleChassisDef");
        result.put("weapon", "WeaponDef");
        return(result);
    }
    
    public static Map<String, Object> createPrettyPrintProps() {
        Map<String, Object> result = new HashMap<String, Object>(1);
        result.put(JsonGenerator.PRETTY_PRINTING, true);
        return(result);
    }
    
    public static ArrayList<String> createManifestHeaders() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(ID_ID);
        result.add(TYPE_ID);
        result.add(PATH_ID);
        result.add(VERSION_ID);
        result.add(ADDEDON_ID);
        result.add(UPDATEDON_ID);
        result.add(REMOVEDON_ID);
        result.add(ASSETBUNDLENAME_ID);
        result.add(ASSETBUNDLEPERSISTENT_ID);
        result.add(ASSETBUNDLECRC_ID);
        result.add(ADDENDUM_ID);
        return(result);
    }
    
    public static Properties readConfig() {
        Properties result = new Properties();
        try {
            File configFile = new File("config.properties");
            FileReader reader = new FileReader(configFile);
            result.load(reader);
            reader.close();
        } catch(IOException ioe) {
            
        }
        return(result);
    }
    
    public static void writeConfig() {
        try {
            if(config != null) {
                File configFile = new File("config.properties");
System.out.println("config.properties written to "+configFile.getAbsolutePath());
                FileWriter writer = new FileWriter(configFile);
                config.store(writer, "App Settings");
                writer.flush();
                writer.close();
            }
        } catch(IOException ioe) {
            
        }
    }
}
