package dsavant.battletech;

import static dsavant.battletech.Constants.INVALID_DIRECTORY;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.json.Json;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

public class FXMLController implements Constants, Initializable, EventListener {
    private Stage myStage = null;
    private DataLoader dataLoader = null;
    private TreeItem<String> dataElementTreeRootNode = new TreeItem<String>("Json Files");
    
    @FXML
    private BorderPane rootPane;
    
    @FXML
    private TreeView dataElementTreeView;
    
    @FXML
    private TextArea dataElementTextArea;
    
    @FXML
    private TextField dataDirTextField;
    
    @FXML
    private TextField modsDirTextField;
    
    @FXML
    private Button launchDataFileChooserButton;
    
    @FXML
    private Button launchModsFileChooserButton;
    
    @FXML
    private Button loadFilesButton;
    
    @FXML
    private Button writeFilesButton;
    
    @FXML
    private void handleLaunchDataFileChooserButtonAction(ActionEvent event) {
        String fileName = null;
        File location = new File(this.dataDirTextField.getText());
        if(location.exists() && location.isDirectory()) {
            fileName = this.showDirChooser("Choose Battletech data directory", location);
        } else {
            fileName = this.showDirChooser("Choose Battletech data directory");
        }
        this.dataDirTextField.setText(fileName);
    }
    
    @FXML
    private void handleLaunchModsFileChooserButtonAction(ActionEvent event) {
        String fileName = null;
        File location = new File(this.modsDirTextField.getText());
        if(location.exists() && location.isDirectory()) {
            fileName = this.showDirChooser("Choose Battletech mods directory", location);
        } else {
            fileName = this.showDirChooser("Choose Battletech mods directory");
        }
        this.modsDirTextField.setText(fileName);
    }
    
    @FXML
    private void handleLoadFilesButtonAction(ActionEvent event) {
        this.loadFiles();
    }
    
    @FXML
    private void handleWriteFilesButtonAction(ActionEvent event) {
        this.copyDataElementsToVersionManifest();
        if(this.proceedAlert("Data has been copied into the in-memory versionManifest.csv.  Are you sure you'd like to continue?"))
        {
            try {
                this.writeFiles();
            } catch(IOException ioe) {
                System.err.println("Error trying to write version manifest csv to filesystem:"+ioe);
                ioe.printStackTrace();
                this.showAlert("Error trying to write version manifest csv to filesystem:"+ioe);
            }
            this.showAlert("Writing of versionManifest.csv complete");
        } else {
            this.showAlert("Writing of versionManifest.csv aborted");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up configuration
        if(Constants.config == null) {
            System.err.println("----------\nUnable to read config from file\n----------");
        } else {
            String dirProp = Constants.config.getProperty(DATA_DIR_CONFIG_PROP);
            if(dirProp != null) {
                this.dataDirTextField.setText(dirProp);
            }
            String modProp = Constants.config.getProperty(MODS_DIR_CONFIG_PROP);
            if(modProp != null) {
                this.modsDirTextField.setText(modProp);
            }
        }
        //set up a listener for tree selection events
        this.dataElementTreeView.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    TreeItem<String> selectedItem = (TreeItem<String>)newValue;
                    if(selectedItem.isLeaf()) {
                        String dir = selectedItem.getParent().getValue();
                        String id = selectedItem.getValue();
                        List<DataElement> deList = dataLoader.jsonFiles.get(dir);
                        for(DataElement de : deList) {
                            if(de.getID().equalsIgnoreCase(id)) {
                                StringWriter writer = new StringWriter();
                                JsonWriterFactory writerFactory = Json.createWriterFactory(Constants.prettyPrintProps);
                                JsonWriter jsonWriter = writerFactory.createWriter(writer);
                                jsonWriter.writeObject(de.getContents());
                                jsonWriter.close();
                                dataElementTextArea.setText(writer.toString());
                            }
                        }
                    }
                }
            }
        );
    }    
    
    public String showFileChooser(String title) {
        return(this.showFileChooser(title, null));
    }
    
    public String showFileChooser(String title, File location) {
        String result = null;
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if(location != null) {
            chooser.setInitialDirectory(location);
        }
        File resultFile = chooser.showOpenDialog(this.getStage());
        if(resultFile != null) {
            result = resultFile.getAbsolutePath();
        }
        return(result);
    }
    
    public String showDirChooser(String title) {
        return(this.showDirChooser(title, null));
    }
    
    public String showDirChooser(String title, File location) {
        String result = null;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        if(location != null) {
            chooser.setInitialDirectory(location);
        }
        File resultFile = chooser.showDialog(this.getStage());
        if(resultFile != null) {
            result = resultFile.getAbsolutePath();
        }
        return(result);
    }
    
    public Stage getStage() {
        if(this.myStage == null) {
            this.myStage = (Stage)this.rootPane.getScene().getWindow();
        }
        return(this.myStage);
    }
    
    private void loadFiles() {
        this.fileOpsSetup();
        //load manifest and json files
        try {
            this.dataLoader.loadElements();
            this.dataElementTreeRootNode = new TreeItem<String>("Json Files");
            this.dataElementTreeRootNode.setExpanded(true);
            this.dataElementTreeView.setRoot(dataElementTreeRootNode);
            for(String directoryName : this.dataLoader.jsonFiles.keySet()) {
                TreeItem<String> dir = new TreeItem<String>(directoryName);
                this.dataElementTreeRootNode.getChildren().add(dir);
                List<DataElement> lde = this.dataLoader.jsonFiles.get(directoryName);
                for(DataElement de : lde) {
                    TreeItem<String> deti = new TreeItem<String>(de.getID());
                    dir.getChildren().add(deti);
                }
            }
            if(this.dataLoader.errorList != null) {
                StringBuilder sb = new StringBuilder("These errors were encountered while processing source files.\nPlease fix these errors if they are meaningful (some you just won't care about) and try again.\n\n");
                for(String line : this.dataLoader.errorList) {
                    sb.append(line);
                    sb.append('\n');
                }
                this.scrollingAlert("Processing Errors", sb.toString());
            }
        } catch(IOException ioe) {
            System.err.println("Exception trying to load elements: "+ioe);
            ioe.printStackTrace();
            this.showAlert("Fatal error processing files.  Please restart and try again.  If it persists, please post this erro to the forum: "+ioe);
        }
    }
    
    private void copyDataElementsToVersionManifest() {
        this.fileOpsSetup();
        Map<String, List<VersionManifestRecord>> copiedVals = this.dataLoader.copyDataElementsToVersionManifest();
        List<VersionManifestRecord> newRecords = copiedVals.get(NEW_ID);
        StringBuilder recordStringBuilder = new StringBuilder("These records have been added to the in-memory manifest:\n");
        for(VersionManifestRecord r : newRecords) {
            if(r.exceptionText == null) {
                recordStringBuilder.append(r.type);
                recordStringBuilder.append(", ");
                recordStringBuilder.append(r.id);
                recordStringBuilder.append(", ");
                recordStringBuilder.append(r.path);
                recordStringBuilder.append("\n");
            } else {
                recordStringBuilder.append(r.exceptionText);
                recordStringBuilder.append("\n");
            }
        }
        this.scrollingAlert("Records added to in-memory manifest", recordStringBuilder.toString());
    }
    
    private void writeFiles() throws IOException {
        this.fileOpsSetup();
        this.dataLoader.writeFiles();
    }
    
    private void fileOpsSetup() {
        //lazy init the dataloader
        if(this.dataLoader == null) {
            this.dataLoader = new DataLoader();
            this.dataLoader.registerEventListener(this);
        }
        //set the dataLoader directories
        this.dataLoader.dataDir = this.dataDirTextField.getText();
        this.dataLoader.modDir = this.modsDirTextField.getText();
        //write files to config and write config to file
        Constants.config.setProperty(DATA_DIR_CONFIG_PROP, this.dataLoader.dataDir);
        Constants.config.setProperty(MODS_DIR_CONFIG_PROP, this.dataLoader.modDir);
        Constants.writeConfig();
    }

    @Override
    public void handleEvent(String eventType, String eventName, Object relatedValue) {
        if(eventType != null && eventType.equals(INVALID_DIRECTORY)) {
            this.showAlert(eventName);
        }
    }
    
    public boolean proceedAlert(String text) {
        Alert alert = new Alert(AlertType.CONFIRMATION, text);
        Optional<ButtonType> result = alert.showAndWait();
        return(result.get() == ButtonType.OK);
    }
    
    public boolean scrollingAlert(String title, String text) {
        ScrollingAlert alert = new ScrollingAlert(AlertType.INFORMATION, title, text);
        Optional<ButtonType> result = alert.showAndWait();
        //the check against button type is only valid if there are buttons to click - the INFORMATION type does not have that
        //return(result.get() == ButtonType.OK);
        return(true);
    }
    
    public Optional<ButtonType> showAlert(String text) {
        Alert alert = new Alert(AlertType.WARNING, text);
        return alert.showAndWait();
    }
    
}
