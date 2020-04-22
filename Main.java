package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import static javafx.geometry.HorizontalDirection.LEFT;

public class Main extends Application {
    int farmId; // used to store farmId user entered
    int year; // used to store year user entered
    int month; // used to store month user entered

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private static final String APP_TITLE = "Hello World!";
    private static int clicks = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {



        // load button for top component
        VBox loadContaner = new VBox();
        Button loadBut = new Button("Load");
        Separator separator1 = new Separator();
        separator1.setMaxWidth(200);
        loadContaner.getChildren().addAll(loadBut,separator1);


        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // add top, left, center, right, and bottom components
        root.setTop(loadContaner);
        root.setLeft(makeLeftComponent());

        Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Add the stuff and set the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void viewAction(Button viewButton){
        clicks = 0;
        viewButton.setText(clicks + " clicks");
    }

    private TabPane makeLeftComponent(){
        final ToggleGroup group = new ToggleGroup();
        VBox leftComponent= new VBox(); // holds view and Edit button
        TabPane modesHolder = new TabPane(); // holds view and Edit button
        Tab view = new Tab("view");
        Tab edit = new Tab("edit");

        // farm year and month input
        Insets inputPadd = new Insets(10,0,0,0);

        Label farmId = new Label("Farm ID");
        TextField farmIdInfo = new TextField();
        farmId.setPadding(new Insets(50,0,0,0));

        Label year = new Label("year");
        year.setPadding(inputPadd);
        TextField yearInput = new TextField();

        Label month = new Label("month");
        month.setPadding(inputPadd);
        TextField monthIn = new TextField();

        // make containers for different ways to view data
        
        HBox display = new HBox();
        HBox min = new HBox();
        HBox max = new HBox();
        HBox average = new HBox();

        Label displayLab = new Label("Display %");
        Label minLab = new Label("Min");
        Label maxLab = new Label("Max");
        Label averageLab = new Label("Average");

        RadioButton displayCheck = new RadioButton("Display %");
        RadioButton minCheck = new RadioButton("Min");
        RadioButton maxCheck = new RadioButton("Max");
        RadioButton averageCheck = new RadioButton("Average");
        minCheck.setToggleGroup(group);
        maxCheck.setToggleGroup(group);
        averageCheck.setToggleGroup(group);
        
        display.getChildren().addAll(displayLab, displayCheck);
        min.getChildren().addAll(minLab, minCheck);
        max.getChildren().addAll(maxLab, maxCheck);
        average.getChildren().addAll(averageLab, averageCheck);





        leftComponent.getChildren().addAll(modesHolder, farmId,farmIdInfo,year,yearInput,month,monthIn,displayCheck,minCheck,maxCheck,averageCheck);
        view.setContent(leftComponent);
        modesHolder.getTabs().addAll(view,edit);
        modesHolder.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return modesHolder;
    }

    /**
     * creates instances of everything needed for the desired vBox
     * @return
     */
    private VBox makeRightComponent(){
        VBox vBox = new VBox();
        ObservableList<String> possGrades = FXCollections.observableArrayList("A", "B", "C", "D","F");
        ListView<String> grades = new ListView<>(possGrades);


        // tracks how many times the button was clicked
        Button clicksButton = new Button(clicks + " clicks");
        clicksButton.setOnAction(
                event -> clicksButton.setText(++clicks + " clicks")
        );
        clicksButton.setPadding(new Insets(20));

        Button resetButton = new Button("reset");
        resetButton.setOnAction(
                event ->  viewAction(clicksButton)
        );
        resetButton.setPadding(new Insets(20));

        HBox buttonMan = new HBox();
        buttonMan.getChildren().addAll(clicksButton, resetButton);
        buttonMan.setSpacing(5);

        vBox.getChildren().addAll(buttonMan, grades);
        VBox.setMargin(buttonMan, new Insets(10));
        return vBox;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}