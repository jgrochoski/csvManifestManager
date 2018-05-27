/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import javax.json.*;

/**
 * id,type,path,version,addedOn,updatedOn,removedOn,assetBundleName,assetBundlePersistent,assetBundleCRC,addendum
 * 
 * @author jgroc
 */
public class DataElement implements Constants {
    
    
    private String manifestType = null;
    private String descriptionID = null;
    private String path = null;
    //the json contents of the file
    private JsonObject contents = null;
    
    
    public DataElement(String manifestType, File sourceFile, String stripDir) throws IOException {
        this.manifestType = manifestType;
        String newPath = sourceFile.getAbsolutePath();
        if(newPath.startsWith(stripDir)) {
            newPath = newPath.substring(stripDir.length());
            if(newPath.startsWith(File.separator)) {
                newPath = newPath.substring(File.separator.length());//strip the leading \ or / if its there
            }
        }
        this.setPath(newPath);
        this.convertFileToJSON(sourceFile);
    }
    
    public JsonObject getContents() {
        return(this.contents);
    }
    
    public void setContents(JsonObject contents) {
        this.contents = contents;
    }
    
    public void setContents(String contents) {
        JsonReader reader = Json.createReader(new StringReader(contents));
        this.contents = reader.readObject();
    }
    
    public String getType() {
        return(this.manifestType);
    }
    
    public void setType(String manifestType) {
        this.manifestType = manifestType;
    }
    
    public String getID() {
        return(this.descriptionID);
    }
    
    public void setID(String descriptionID) {
        this.descriptionID = descriptionID;
    }
    
    public String getPath() {
        return(this.path);
    }
    
    public void setPath(String newPath) {
        //app uses unix style paths in the manifest - need to convert if on another platform
        if(File.separatorChar != VERSION_FILE_SEPARATOR) {
            newPath = newPath.replace(File.separatorChar, VERSION_FILE_SEPARATOR);
        }
        this.path = newPath;
    }
    
    public static String getIdFromJsonObject(JsonObject j) {
        String result = null;
        if (j.containsKey("id")) {//some objects like audioevent have the id in the root
            result = j.getString("id");
        } else if (j.containsKey("Id")) {
            result = j.getString("Id");
        } else if (j.containsKey("ID")) {
            result = j.getString("ID");
        }
        return(result);
    }
    
    public static JsonObject getDescriptionFromJsonObject(JsonObject j) {
        JsonObject result = null;
        if(j.containsKey("description")) {
            result = j.getJsonObject("description");
        } else if(j.containsKey("Description")) {
            result = j.getJsonObject("Description");
        } else if(j.containsKey("DESCRIPTION")) {
            result = j.getJsonObject("DESCRIPTION");
        }
        return(result);
    }
    
    private void convertFileToJSON(File sourceFile) throws IOException {
        FileReader fr = new FileReader(sourceFile);
        JsonReader jr = Json.createReader(fr);
        this.contents = jr.readObject();
        try {
            this.descriptionID = getIdFromJsonObject(this.contents);
            if(this.descriptionID == null || this.descriptionID.trim().equals(NULL_STRING)) {
                JsonObject descObject = getDescriptionFromJsonObject(this.contents);
                this.descriptionID = getIdFromJsonObject(descObject);
            }
        } catch(Throwable t) { 
            System.out.println("Error Retrieving descriptionID on "+sourceFile.getName());
        }
        if(this.descriptionID == null || this.descriptionID.trim().equals(NULL_STRING)) {
            String descTemp = sourceFile.getName();
            this.descriptionID = descTemp.substring(0, descTemp.indexOf(JSON_EXT));
        }
        this.descriptionID = this.descriptionID.trim();
    }
}
