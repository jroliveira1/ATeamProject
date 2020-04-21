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
import javafx.stage.Stage;

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

    private VBox makeLeftComponent(){
        VBox leftComponent = new VBox(); // will contain part of gui where user can select different viewing options
        HBox modesHolder = new HBox(); // holds view and Edit button
        Button view = new Button("view");
        Button edit = new Button("edit");
        modesHolder.getChildren().addAll(view, edit);
        modesHolder.setPadding(new Insets(10,0,0,0));

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


        leftComponent.getChildren().addAll(modesHolder, farmId,farmIdInfo,year,yearInput,month,monthIn);
        return leftComponent;
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
