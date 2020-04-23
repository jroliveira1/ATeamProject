package application;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn;

public class Main extends Application {
    private int farmId; // used to store farmId user entered
    private int year; // used to store year user entered
    private int month; // used to store month user entered

    Scene mainScene;

    private Label minMaxOrAve  = new Label();

    private TableView<FarmData> table;
    private ObservableList<FarmData> data = FXCollections.observableArrayList();
    private Text txtStatus;



    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private int leftCompWidth = 200;
    private int prefButtonWidth = 200;
    private static final String APP_TITLE = "Milk Weight Analyzer";

private VBox reportHolder;
    @Override
    public void start(Stage primaryStage) throws Exception {


        // load button for top component
        VBox loadContaner = new VBox();
        Label dragFile = new Label("Drag and Drop File Here");
        dragFile.setId("drop");
        dragFile.setPadding(new Insets(10,10,0,10));
        dragFile.setOnDragOver(event -> {
            if(event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY);
            }
        });
        dragFile.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            String filePath = new String(files.get(0).getAbsolutePath());
            System.out.println(filePath);
        });
        Separator separator1 = new Separator();
        separator1.setMaxWidth(300);
        minMaxOrAve.setId("minMaxOrAve");
        minMaxOrAve.setPadding(new Insets(5,0,0,300));
        loadContaner.getChildren().addAll(dragFile,minMaxOrAve, separator1);

        // make left component
        AtomicBoolean dragging = new AtomicBoolean(false);
        TabPane leftComponent = makeLeftComponent();
        leftComponent.setPrefWidth(leftCompWidth);
       // leftComponent.setOnMousePressed(event -> dragging.set(true));

        leftComponent.setOnMouseMoved(event -> {
            if(event.getX() < leftCompWidth - 15 || event.getX() > leftCompWidth + 15) mainScene.setCursor(Cursor.DEFAULT);
            else mainScene.setCursor(Cursor.H_RESIZE);
        });
        leftComponent.setOnMouseDragged(event -> {
            if(mainScene.getCursor().equals(Cursor.H_RESIZE) && event.getX() > 10 && event.getX() < WINDOW_WIDTH - 10) leftCompWidth = (int) event.getX();
             leftComponent.setPrefWidth(leftCompWidth);


        });
      //  leftComponent.setOnMouseReleased(event -> dragging.set(false));
        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // add top, left, center, right, and bottom components
        root.setTop(loadContaner);

        root.setLeft(leftComponent);
        makeTable();
        table.setOnMouseMoved(event -> {

            if(event.getX() > 15 ) mainScene.setCursor(Cursor.DEFAULT);
        });
        root.setCenter(table);

         mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        mainScene.getStylesheets().add(getClass().getResource("application..css").toExternalForm());

        // Add the stuff and set the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void makeTable() {
        table = new TableView<FarmData>();

        TableColumn<FarmData, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("month"));

        TableColumn<FarmData, String> farmIDCol = new TableColumn<>("FarmID");
        farmIDCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("farmID"));

        TableColumn<FarmData, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<FarmData, Integer>("weight"));

        TableColumn<FarmData, Integer> percentCol = new TableColumn<>("Percent");
        percentCol.setCellValueFactory(new PropertyValueFactory<FarmData, Integer>("percent"));



        table.getColumns().setAll(monthCol, farmIDCol,weightCol, percentCol);
        table.getColumns().get(3).setVisible(false);

        // set table properties
        table.setPrefWidth(400);
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedIndexProperty().
                addListener(new RowChangeHandler());
        table.setEditable(true);
        farmIDCol.setCellFactory(((TextFieldTableCell.forTableColumn())));
        weightCol.setOnEditCommit(event -> (event.getTableView().
                getItems().get(event.getTablePosition().getRow())).
                setWeight(event.getNewValue()));
        txtStatus = new Text();


        table.setPadding(new Insets(20, 20, 20, 20));

    }

    private class RowChangeHandler implements
            ChangeListener {
        @Override
        public void changed(ObservableValue ov, Object oldVal,
                            Object newVal) {
            int val = ((Number)newVal).intValue();
            if (data.size()<=0) {
                return;
            }
            FarmData farmData = (FarmData) data.get(val);
            txtStatus.setText(farmData.toString());
        }
    }



    private void dummyData() {
        data.add(new FarmData( "farm1", 2000,"5%"));
        data.add(new FarmData( "2", 6000, "20%"));
        data.add(new FarmData("3", 3000, "15%"));
        data.add(new FarmData("4", 4000));
        data.get(3).setPercent("50%");
        data.add(new FarmData("farm5", 40484800));
    }

    private void determineMax(){
        try {
            FarmData max = data.stream().max(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText("Max : " + max);
        } catch (NoSuchElementException ignored){

        }

    }

    private void determineMin(){
        try {
            FarmData min = data.stream().min(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText("Min : " + min);
        } catch (NoSuchElementException ignored){

        }
    }

    private void determineAverage(){

        if(data.size() == 0) return;

        double sum = 0;
        for(int i = 0; i < data.size(); i++){
            sum += data.get(i).getWeight();
        }

        minMaxOrAve.setText("Average weight : " + sum/data.size());
    }

    private void farmReport() {
        table.getColumns().get(0).setVisible(true);
        table.getColumns().get(1).setVisible(false);
    }

    private void annualReport() {
        table.setVisible(true);
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(1).setVisible(true);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        dummyData();
        data.add(new FarmData("Sion's farm", 4558858));
    }

    private void monthlyReport() {

    }

    private void dateRangeReport() {

    }


    /**
     * create the gui for the left component
     *
     * @return
     */
    private TabPane makeLeftComponent() {
        TabPane modesHolder = new TabPane(); // holds view and Edit button
        Tab view = new Tab("view");
        Tab edit = new Tab("edit");

        view.setContent(makeViewTab());
        edit.setContent(makeEditTab());
        modesHolder.getTabs().addAll(view, edit);
        modesHolder.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return modesHolder;
    }

    private VBox makeViewTab() {

        VBox viewComponent = new VBox(); // holds view and Edit button
        viewComponent.setStyle("-fx-background-color: #FFFFFF;");
       // viewComponent.setPrefWidth(200);
        viewComponent.setMaxHeight(600);
        viewComponent.setPadding(new Insets(0, 10, 10, 10));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdInfo = new TextField();
        //farmIdInfo.setPadding(new Insets(500));
        farmIdInfo.setMaxWidth(Double.MAX_VALUE);


        Label year = new Label("Year");
        year.setPadding(inputPadd);
        TextField yearInput = new TextField();
        yearInput.setMaxWidth(Double.MAX_VALUE);

        Label month = new Label("Month");
        month.setPadding(inputPadd);
        TextField monthIn = new TextField();
        monthIn.setMaxWidth(Double.MAX_VALUE);

        VBox searchDataHolder = new VBox();
        searchDataHolder.getChildren().addAll(farmId,farmIdInfo,year,yearInput,month,monthIn);
        searchDataHolder.setAlignment(Pos.CENTER_LEFT);
        searchDataHolder.setPadding(new Insets(0,60,0,20));


        // make radio buttons for diff views
        VBox hboxHolder = new VBox();
        HBox percentHolder = new HBox();
        HBox minHolder = new HBox();
        HBox maxHolder = new HBox();
        HBox averageHolder = new HBox();

        Label percentL = new Label("display %");
        Label minL = new Label("Min");
        Label maxL = new Label("Max");
        Label averageL = new Label("Average");

        ToggleButton percentTog = new ToggleButton();
        percentTog.setOnAction(event -> {if (percentTog.isSelected()) table.getColumns().get(3).setVisible(true);
        else table.getColumns().get(3).setVisible(false);
            System.out.println(data.get(0).getFarmID());});



        ToggleButton minTog = new ToggleButton();
        minTog.setOnAction(event -> {if (minTog.isSelected()) determineMin();
        else minMaxOrAve.setText("");});

        ToggleButton maxTog = new ToggleButton();
        maxTog.setOnAction(event -> {if (maxTog.isSelected()) determineMax();
        else minMaxOrAve.setText("");});

        ToggleButton averageTog = new ToggleButton();
        averageTog.setOnAction(event -> {if (averageTog.isSelected()) determineAverage();
        else minMaxOrAve.setText("");});

        // creating region for spacing of label and button
        Region spacing1 = new Region();
        Region spacing2 = new Region();
        Region spacing3 = new Region();
        Region spacing4 = new Region();
        HBox.setHgrow(spacing1, Priority.ALWAYS);
        HBox.setHgrow(spacing2, Priority.ALWAYS);
        HBox.setHgrow(spacing3, Priority.ALWAYS);
        HBox.setHgrow(spacing4, Priority.ALWAYS);

        percentHolder.getChildren().addAll(percentL,spacing1,percentTog);
        percentHolder.setPadding(new Insets(10,10,15,10));
        minHolder.getChildren().addAll(minL,spacing2,minTog);
        minHolder.setPadding(new Insets(0,10,0,10));
        maxHolder.getChildren().addAll(maxL,spacing3,maxTog);
        maxHolder.setPadding(new Insets(0,10,0,10));
        averageHolder.getChildren().addAll(averageL,spacing4,averageTog);
        averageHolder.setPadding(new Insets(0,10,0,10));

        hboxHolder.getChildren().addAll(percentHolder,minHolder,maxHolder,averageHolder);
        hboxHolder.setPadding(new Insets(20));
        hboxHolder.setSpacing(5);



       // add min max and average to group


        ToggleGroup group = new ToggleGroup();
        minTog.setToggleGroup(group);
        maxTog.setToggleGroup(group);
        averageTog.setToggleGroup(group);


        // report type buttons
         reportHolder = new VBox(5);
        reportHolder.setPadding(new Insets(50, 0, 10, 0));

        Button farmReport = new Button("Farm Report");
      //  farmReport.setPrefWidth(prefButtonWidth);
        farmReport.setMaxWidth(Double.MAX_VALUE);
        farmReport.setOnAction(event -> farmReport());

        Button annualReport = new Button("Annual Report");
        annualReport.setMaxWidth(Double.MAX_VALUE);
        annualReport.setOnAction(event -> annualReport());

        Button monthReport = new Button("Monthly Report");
        monthReport.setMaxWidth(Double.MAX_VALUE);
        reportHolder.getChildren().addAll(farmReport, annualReport, monthReport);
       // HBox.setHgrow(farmReport, Priority.ALWAYS);


        // make start date and end date textfields
        VBox startHolder = new VBox(5);
        VBox endHolder = new VBox(5);
        HBox datesHold = new HBox(5);
        Label start = new Label("Start Date");
        TextField startDate = new TextField();
        startDate.setMaxWidth(Double.MAX_VALUE);
        TextField endDate = new TextField();
        Label end = new Label("End Date");
        Label dash = new Label("-");
        dash.setPadding(new Insets(25, 5, 0, 5));
        endDate.setMaxWidth(Double.MAX_VALUE);
        startHolder.getChildren().addAll(start, startDate);
        startHolder.setPadding(new Insets(0, 0, 0, 10));
        endHolder.getChildren().addAll(end, endDate);
        endHolder.setPadding(new Insets(0, 10, 0, 0));
        datesHold.getChildren().addAll(startHolder, dash, endHolder);
        datesHold.setAlignment(Pos.BASELINE_CENTER);
        datesHold.setMaxWidth(Double.MAX_VALUE);


        // report data range button
        VBox rangeHolder = new VBox();
        Button rangeReport = new Button("Date Range Report");
        rangeReport.setMaxWidth(Double.MAX_VALUE);
        rangeHolder.getChildren().add(rangeReport);
        rangeHolder.setPadding(new Insets(5, 0, 0, 0));

        viewComponent.getChildren().addAll(searchDataHolder, hboxHolder, reportHolder, datesHold, rangeHolder);
        return viewComponent;
    }


    private VBox makeEditTab() {
        VBox editComponent = new VBox(); // holds view and Edit button
        editComponent.setStyle("-fx-background-color: #FFFFFF;");
        editComponent.setPrefWidth(200);
        editComponent.setMaxHeight(600);
        editComponent.setPadding(new Insets(0, 10, 10, 30));
        Label instr = new Label("11/07/1999 format");
        instr.setPadding(new Insets(50, 0, 0, 0));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdInfo = new TextField();
        farmId.setPadding(new Insets(50, 0, 0, 0));
        farmIdInfo.setMaxWidth(Double.MAX_VALUE);;


        Label date = new Label("Date");
        date.setPadding(inputPadd);
        TextField dateIn = new TextField();
        dateIn.setMaxWidth(Double.MAX_VALUE);;

        Label weight = new Label("Weight");
        weight.setPadding(inputPadd);
        TextField weightInput = new TextField();
        weightInput.setMaxWidth(Double.MAX_VALUE);;

        // report data range button
        VBox addHolder = new VBox();
        Button addData = new Button("Add Data");
        // addData.setPrefWidth(50);
        addHolder.getChildren().add(addData);
        addHolder.setPadding(new Insets(30, 0, 0, 0));

        editComponent.getChildren().addAll(instr, farmId, farmIdInfo, date, dateIn, weight, weightInput, addHolder);
        editComponent.setPadding(new Insets(0,60,0,30));

        return editComponent;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}