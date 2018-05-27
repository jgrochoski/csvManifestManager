/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import java.util.ArrayList;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author jgroc
 */
public class VersionManifestRecord implements Constants {
    public String id = NULL_STRING;
    public String type = NULL_STRING;
    public String path = NULL_STRING;
    public String version = DEFAULT_VERSION;
    public String addedOn = DEFAULT_ADDEDON;
    public String updatedOn = DEFAULT_UPDATEDON;
    public String removedOn = DEFAULT_REMOVEDON;
    public String assetBundleName = DEFAULT_ASSETBUNDLENAME;
    public String assetBundlePersistent = DEFAULT_ASSETBUNDLEPERSISTENT;
    public String assetBundleCRC = DEFAULT_ASSETBUNDLECRC;
    public String addendum = DEFAULT_ADDENDUM;
    public String exceptionText = null;
    
    public static VersionManifestRecord createVersionManifestRecord(CSVRecord csvRecord) {
        return(new VersionManifestRecord(csvRecord));
    }
    
    public VersionManifestRecord() {
        
    }
    
    public VersionManifestRecord(CSVRecord csvRecord) {
        this();
        this.loadCSVRecord(csvRecord);
    }
    
    public void loadCSVRecord(CSVRecord csvRecord) {
//System.out.println(csvRecord.toString());
        this.id = csvRecord.get(ID_INDEX).trim();
        this.type = csvRecord.get(TYPE_INDEX).trim();
        this.path = csvRecord.get(PATH_INDEX).trim();
        this.version = csvRecord.get(VERSION_INDEX).trim();
        this.addedOn = csvRecord.get(ADDEDON_INDEX).trim();
        this.updatedOn = csvRecord.get(UPDATEDON_INDEX).trim();
        this.removedOn = csvRecord.get(REMOVEDON_INDEX).trim();
        this.assetBundleName = csvRecord.get(ASSETBUNDLENAME_INDEX).trim();
        this.assetBundlePersistent = csvRecord.get(ASSETBUNDLEPERSISTENT_INDEX).trim();
        this.assetBundleCRC = csvRecord.get(ASSETBUNDLECRC_INDEX).trim();
        this.addendum = csvRecord.get(ADDENDUM_INDEX).trim();
    }
    
    public Iterable<String> getIterableValues() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(this.id);
        result.add(this.type);
        result.add(this.path);
        result.add(this.version);
        result.add(this.addedOn);
        result.add(this.updatedOn);
        result.add(this.removedOn);
        result.add(this.assetBundleName);
        result.add(this.assetBundlePersistent);
        result.add(this.assetBundleCRC);
        result.add(this.addendum);
        return(result);
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.id);
        result.append(", ");
        result.append(this.type);
        result.append(", ");
        result.append(this.path);
        result.append(", ");
        result.append(this.version);
        result.append(", ");
        result.append(this.addedOn);
        result.append(", ");
        result.append(this.updatedOn);
        result.append(", ");
        result.append(this.removedOn);
        result.append(", ");
        result.append(this.assetBundleName);
        result.append(", ");
        result.append(this.assetBundlePersistent);
        result.append(", ");
        result.append(this.assetBundleCRC);
        result.append(", ");
        result.append(this.addendum);
        return(result.toString());
    }
}
