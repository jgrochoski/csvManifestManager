/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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
    
    public ScrollingAlert(AlertType alertType, String title, String infoToScroll) {
        super(alertType);
        
        this.parentPane = getDialogPane();
        setResizable(true);
        setTitle(title);
        //setHeaderText(title);
        parentGrid = new GridPane();
        parentGrid.setPadding(new Insets(20, 10, 0, 10));
        contents = new TextArea(infoToScroll);
        contents.setPrefHeight(1000.0);
        contents.setPrefWidth(3000.0);
        //VBox vbox = new VBox();
        //vbox.getChildren().add(contents);
        
        //parentGrid.add(vbox, 0, 0);
        parentGrid.add(contents, 0, 0);

        this.parentPane.setContent(parentGrid);
        this.parentPane.setPrefHeight(300.0);
        this.parentPane.setPrefWidth(600.0);

    }
    
}
