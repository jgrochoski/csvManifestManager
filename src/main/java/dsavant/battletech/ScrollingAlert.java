/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author jgroc
 */
public class ScrollingAlert extends Alert {
    private GridPane parentGrid;
    private DialogPane parentPane;
    private TextArea contents;
    private CheckBox allErrors;
    
    public ScrollingAlert(AlertType alertType, String title, String infoToScroll, String reducedInfoToScroll, String checkBoxLabel) {
        super(alertType);
        
        this.parentPane = getDialogPane();
        this.setResizable(true);
        this.setTitle(title);
        //setHeaderText(title);
        this.parentGrid = new GridPane();
        this.parentGrid.setPadding(new Insets(20, 10, 0, 10));
        this.contents = new TextArea(infoToScroll);
        this.contents.setPrefHeight(1000.0);
        this.contents.setPrefWidth(3000.0);
        //VBox vbox = new VBox();
        //vbox.getChildren().add(contents);
        //parentGrid.add(vbox, 0, 0);
        this.parentGrid.add(contents, 0, 0);
        if(reducedInfoToScroll != null) {
            this.allErrors = new CheckBox(checkBoxLabel);
            this.allErrors.setPrefHeight(50.0);
            this.allErrors.setPrefWidth(300.0);
            this.allErrors.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(newValue) {
                        contents.setText(reducedInfoToScroll);
                    } else {
                        contents.setText(infoToScroll);
                    }
                }
                
            });
            this.parentGrid.add(this.allErrors, 0, 1);
        }

        this.parentPane.setContent(parentGrid);
        this.parentPane.setPrefHeight(300.0);
        this.parentPane.setPrefWidth(600.0);

    }
    
}
