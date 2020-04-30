package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;

import static javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn;

public class Main extends Application {
    private static final String COMMA = "\\s*,\\s*";
    
    Scene scene;

    private TableView<FarmData> table = new TableView<FarmData>();
    private Set<FarmData> data = new HashSet<FarmData>();   
    private Label minMaxOrAve = new Label();

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 700;
    private int leftCompWidth = 275;
    private int prefButtonWidth = 200;
    private static final String APP_TITLE = "Milk Weight Analyzer";
    private VBox reportHolder;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	makeTable();
    	GUI guiElements = new GUI(data, table, minMaxOrAve); // Create an instance of the GUI class
    	
        // load button for top component
        VBox loadContaner = guiElements.makeLoadButton();

        // make left component
        TabPane leftComponent = makeLeftComponent();
        leftComponent.setPrefWidth(leftCompWidth);
        
        leftComponent.setOnMouseMoved(event -> { // code for dragging left component to the right or left
            if (event.getX() < leftCompWidth - 15 || event.getX() > leftCompWidth + 15) scene.setCursor(Cursor.DEFAULT);
            else scene.setCursor(Cursor.H_RESIZE);
        });
        leftComponent.setOnMouseDragged(event -> { // code for dragging left component to the right or left
            if (scene.getCursor().equals(Cursor.H_RESIZE) && event.getX() > 10 && event.getX() < WINDOW_WIDTH - 10)
                leftCompWidth = (int) event.getX();
            leftComponent.setPrefWidth(leftCompWidth);
        });

        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // Add top and left components
        root.setTop(loadContaner);
        root.setLeft(leftComponent);
        
        
        // Make the table and set the center component
        table.setOnMouseMoved(event -> {
            if (event.getX() > 15) scene.setCursor(Cursor.DEFAULT);
        }); // for expanding tabpane
        root.setCenter(table);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Add the stuff and set the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * create the GUI for the left component
     *
     * @return
     */
    private TabPane makeLeftComponent() {
    	GUI guiElements = new GUI(data, table, minMaxOrAve);
        TabPane modesHolder = new TabPane(); // holds view and Edit button
        Tab view = new Tab("View Data");
        Tab add = new Tab("Add Data");

        view.setContent(guiElements.makeViewTab());
        add.setContent(guiElements.makeAddTab());
        modesHolder.getTabs().addAll(view, add);
        modesHolder.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return modesHolder;
    }


    private void makeTable() {
    	GUI guiElements = new GUI(data, table);
    	table = guiElements.makeTable();
    } 


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


}