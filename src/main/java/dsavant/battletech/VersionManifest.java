/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

/**
 *
 * @author jgroc
 */
public class VersionManifest implements Constants {
    
    private File mainFile = null;
    //<ChassisDef, <chassisdef_atlas_AS7-D, CSVRecord>>
    private Map<String, Map<String, VersionManifestRecord>> manifestRecords = new TreeMap<String, Map<String, VersionManifestRecord>>(String.CASE_INSENSITIVE_ORDER);
    //holds the first id for each type, to use later to lookup locations in the allManifestRecords and insert appropriately
    private Map<String, VersionManifestRecord> manifestType_ID = new TreeMap<String, VersionManifestRecord>(String.CASE_INSENSITIVE_ORDER);
    //private List<VersionManifestRecord> otherManifestRecords = new ArrayList<VersionManifestRecord>();
    private List<VersionManifestRecord> allManifestRecords = new ArrayList<VersionManifestRecord>();
    
    private VersionManifest() {
        for(String s : MANIFEST_MAP.values()) {
            this.manifestRecords.put(s, new TreeMap<String, VersionManifestRecord>(String.CASE_INSENSITIVE_ORDER));
        }
    }
    
    public VersionManifest(File versionManifestFile) throws IOException {
        this();
        if(versionManifestFile == null) {
            throw new IOException("Must have a valid versionManifest.csv in the data directory");
        }
        if(!versionManifestFile.exists()) {
            throw new IOException(versionManifestFile.getAbsolutePath() + " is not a valid file");
        }
        if(!versionManifestFile.canWrite()) {
            throw new IOException(versionManifestFile.getAbsolutePath() + " is not writable");
        }
        this.mainFile = versionManifestFile;
    }
    
    public void loadCSV() throws IOException {
        Reader in = new FileReader(this.mainFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
        for(CSVRecord record : records) {
            if(record.size() == MANIFEST_HEADERS_ARRAY.length && record.get(ID_INDEX) != null && !record.get(ID_INDEX).equals(NULL_STRING)) {
                String type = record.get(TYPE_INDEX);
                String id = record.get(ID_INDEX);
                VersionManifestRecord vmRecord = VersionManifestRecord.createVersionManifestRecord(record);
    //System.out.println(type+", "+id+"::"+vmRecord);
                this.allManifestRecords.add(vmRecord);
                if(type != null && !type.equals(NULL_STRING) && this.manifestRecords.containsKey(type)) {
                    Map<String, VersionManifestRecord> map = this.manifestRecords.get(type);
    //System.out.println("loadCSV type="+type+", map="+map);
                    if(map != null) {
                        map.put(id, vmRecord);
                        if(!this.manifestType_ID.containsKey(type)) {
                            this.manifestType_ID.put(type, vmRecord);
                        }
                    }
                }
            } else {
                VersionManifestRecord malformedRecord = new VersionManifestRecord();
                StringWriter writer = new StringWriter();
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withIgnoreSurroundingSpaces().withEscape('\\').withQuoteMode(QuoteMode.NONE));
                csvPrinter.printRecord(record);
                csvPrinter.flush();
                csvPrinter.close();
                malformedRecord.exceptionText = writer.toString().trim();
                this.allManifestRecords.add(malformedRecord);
            }
        }
    }
    
    public void writeCSV() throws IOException {
        String mainFileName = this.mainFile.getAbsolutePath();
        File backupFile = new File(this.mainFile.getParentFile(), "VersionManifest" + FILE_DATEFORMAT.format(new Date()) + ".csv");
        this.mainFile.renameTo(backupFile);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(mainFileName));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withIgnoreSurroundingSpaces().withEscape('\\').withQuoteMode(QuoteMode.NONE));
        //print header record
        //csvPrinter.printRecord(MANIFEST_HEADERS);
        //print "all" records
        for(VersionManifestRecord r : this.allManifestRecords) {
            if(r.exceptionText != null) {
                csvPrinter.flush();
                writer.write(r.exceptionText);
                writer.newLine();
            } else {
                csvPrinter.printRecord(r.getIterableValues());
            }
        }
        csvPrinter.flush();
        csvPrinter.close();
    }
    
    public boolean versionManifestRecordExists(String type, String id) {
        boolean result = false;
        if(MANIFEST_MAP.containsKey(type)){
            type = MANIFEST_MAP.get(type);
        }
        if(this.manifestRecords.containsKey(type)) {
            result = this.manifestRecords.get(type).containsKey(id);
        }
        return(result);
    }
    
    public VersionManifestRecord get(String type, String id) {
        VersionManifestRecord result = null;
        if(this.manifestRecords.containsKey(type)) {
            Map<String, VersionManifestRecord> map = this.manifestRecords.get(type);
            if(map.containsKey(id)) {
                result = map.get(id);
            }
        }
        return(result);
    }
    
    public VersionManifestRecord put(DataElement e) {
        VersionManifestRecord result = new VersionManifestRecord();
        result.id = e.getID();
        result.type = e.getType();
        result.path = e.getPath();
        Map<String, VersionManifestRecord> map = this.manifestRecords.get(result.type);
        if(map != null) {
            if(!map.containsKey(result.id)) {
                map.put(result.id, result);
                //insert into the right place in the original csv order
                if(this.manifestType_ID.containsKey(result.type)) {
                    VersionManifestRecord allManifestReference = this.manifestType_ID.get(result.type);
                    int index = this.allManifestRecords.indexOf(allManifestReference);               
                    this.allManifestRecords.add(index+1, result);
                }
            }
        }

        return(result);
    }
    
}
