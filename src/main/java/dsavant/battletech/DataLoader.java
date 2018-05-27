/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.json.stream.JsonParsingException;

/**
 *
 * @author jgroc
 */
public class DataLoader implements Constants {
    public List<String> errorList;
    
    public String dataDir = "c:\\games\\steam\\steamapps\\common\\BATTLETECH\\BattleTech_Data\\StreamingAssets\\data";
    public String modDir = "c:\\games\\steam\\steamapps\\common\\BATTLETECH\\BattleTech_Data\\StreamingAssets\\mods";
    
    private ArrayList<EventListener> eventListeners = new ArrayList<EventListener>();
    public Map<String, List<DataElement>> jsonFiles = new TreeMap<String, List<DataElement>>(String.CASE_INSENSITIVE_ORDER);
    private File versionManifestFile = null;
    public VersionManifest versionManifest = null;
    
    public DataLoader() {
        for(String s : MANIFEST_MAP.keySet()) {//this starts the map off with empty lists for each dir
            jsonFiles.put(s, new ArrayList<DataElement>());
        }
    }
    
    public DataLoader(String dataDir, String modDir) {
        this();
        this.dataDir = dataDir;
        this.modDir = modDir;
    }
    
    /**
     * Main class called from outside that loads the versionManifest.csv from data\versionManifest.csv
     * and any json files in directories highlighted by the keys in Constants.MANIFEST_MAP
     * 
     * @throws IOException 
     */
    public void loadElements() throws IOException {
        //create a new errorlist
        this.errorList = new ArrayList<String>();
        //start by loading all json files in manifest directories into the map
        //files in children "data" directories
        File dataDirFile = new File(this.dataDir);
        if(dataDirFile.exists() && dataDirFile.isDirectory()) {
            //load the version manifest
            this.versionManifestFile = new File(dataDirFile, VERSION_MANIFEST_FILENAME);
            if(!this.versionManifestFile.exists()) {
                this.publishEvent(NO_VERSION_MANIFEST, NO_VERSION_MANIFEST, this.dataDir + File.separator + VERSION_MANIFEST_FILENAME);
                this.errorList.add("versionManifest.csv was not found at "+ this.dataDir + File.separator + VERSION_MANIFEST_FILENAME);
            }
            this.loadVersionManifest(this.versionManifestFile);
            //load the data into DataElement array and map
            this.loadDirectory(dataDirFile, "data", null, dataDirFile.getParent(), 0);
        } else {
            this.publishEvent(INVALID_DIRECTORY, "No valid data directory selected.  Skipping.", this.dataDir);
            this.errorList.add("No valid data directory selected.  Skipping.");
        }
        //files in children "mods" directories
        if(modDir != null && !modDir.trim().equals(NULL_STRING)) {
            File modDirFile = new File(this.modDir);
            if(modDirFile.exists() && modDirFile.isDirectory()) {
                this.loadDirectory(modDirFile, "mods", null, modDirFile.getParent(), 0);
            } else {
                this.publishEvent(INVALID_DIRECTORY, "No valid mods directory selected.  Skipping.", this.modDir);
                this.errorList.add("No valid mods directory selected.  Skipping.");
            }
        }
    }
    
    private void loadDirectory(File dir, String parentShortName, String manifestDirName, String stripDir, int depth) throws IOException {
        //may change the case of the name on some platforms
        String mDirName = manifestDirName == null ? this.testManifestDirs(parentShortName) : manifestDirName;
        if(dir.isDirectory() && depth < MAX_DEPTH) {
            File[] subs = dir.listFiles();
            for (File f : subs) {
                if(f.isDirectory()) {//recurse directories
                    this.loadDirectory(f, f.getName(), mDirName, stripDir, depth + 1);
                } else {
                    if(mDirName != null) {
                        if(f.getName().endsWith(JSON_EXT)) {//found a json file
                            try {
                                DataElement de = new DataElement(MANIFEST_MAP.get(mDirName), f, stripDir);
                                this.jsonFiles.get(mDirName).add(de);
                            } catch(JsonParsingException t) {
                                //JGG - TODO - write all of these errors to a log to display
                                System.err.println("Caught an exception trying to read JSON file "+f.getName());
                                System.err.println("Exception is::"+t);
                                this.errorList.add("Skipping malformed JSON file "+f.getName()+" in "+f.getParent()+"::\n\t\t "+t.getMessage());
                                //t.printStackTrace();
                            }
                        } else {
                            this.errorList.add("Skipping JSON file "+f.getName()+"::\t file is not in a directory supported by this program.");
                        }
                    }
                }
            }
        }
    }
    
    public Map<String, List<VersionManifestRecord>> copyDataElementsToVersionManifest() {
        Map<String, List<VersionManifestRecord>> result = new HashMap<String, List<VersionManifestRecord>>();
        List<VersionManifestRecord> newVMRecords = new ArrayList<VersionManifestRecord>();
        result.put(NEW_ID, newVMRecords);
        List<VersionManifestRecord> originalVMRecords = new ArrayList<VersionManifestRecord>();
        result.put(ORIGINAL_ID, originalVMRecords);
        List<VersionManifestRecord> overrideVMRecords = new ArrayList<VersionManifestRecord>();
        result.put(OVERRIDE_ID, overrideVMRecords);
        for(String type : this.jsonFiles.keySet()) {
            List<DataElement> lde = this.jsonFiles.get(type);
            for(DataElement de : lde) {
                String id = de.getID();
                if(this.versionManifest.versionManifestRecordExists(type, id)) {
                    //originalVMRecords.add(this.versionManifest.get(type, id));
                    //overrideVMRecords.add(this.versionManifest.put(de));
                } else {
                    newVMRecords.add(this.versionManifest.put(de));
                }
            }
        }
        return(result);
    }
    
    public void writeFiles() throws IOException {
        this.versionManifest.writeCSV();
    }
    
    private void loadVersionManifest(File vmFile) throws IOException {
        this.versionManifest = new VersionManifest(vmFile);
        this.versionManifest.loadCSV();
    }
    
    private String testManifestDirs(String name) {
        for(String s : MANIFEST_MAP.keySet()) {
            if(s.equalsIgnoreCase(name)) {
                return(s);//break loop at first found dir
            }
        }
        return(null);
    } 
    
    public void registerEventListener(EventListener listener) {
        if(!this.eventListeners.contains(listener)) {
            this.eventListeners.add(listener);
        }
    }
    
    public void deregisterEventListener(EventListener listener) {
        if(this.eventListeners.contains(listener)) {
            this.eventListeners.remove(listener);
        }
    }
    
    private void publishEvent(String eventType, String eventName, Object relatedValue) {
        for(EventListener e : this.eventListeners) {
            e.handleEvent(eventType, eventName, relatedValue);
        }
    }
}
