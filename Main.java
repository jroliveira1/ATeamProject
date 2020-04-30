//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: Main.java
// Files: None
// Course: CS-400
//
// Author: sreenivas krishna nair
// Email: snair25@wisc.edu
// Lecturer's Name: Deb Deppeler
//
// Device: MacBook Pro
// OS: MacOs Catalina
// VErsion: 10.15.2 (19C57)
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name: NONE
// Partner Email: NONE
// Partner Lecturer's Name: NONE
//
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
// ___ Write-up states that pair programming is allowed for this assignment.
// ___ We have both read and understand the course Pair Programming Policy.
// ___ We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons: NONE
// Online Sources: NONE
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

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
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import static javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn;

public class Main extends Application {
    private static final String COMMA = "\\s*,\\s*";
    private String farmIdInput; // used to store farmId user entered
    private String yearInput; // used to store year user entered
    private String monthInput; // used to store month user entered
    private String startDateInput;
    private String endDateInput;

    // field variables used to store info for new data entry
    private String newFarmID;
    private String newDate;
    private String newWeight;

    Scene scene;

    private TableView<FarmData> table;
    private Set<FarmData> data = new HashSet<FarmData>();
    private List<FarmData> formatedData = new ArrayList<>();
    private Label minMaxOrAve = new Label();
    private boolean validFile = true;


    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;
    private int leftCompWidth = 222;
    private int prefButtonWidth = 200;
    private static final String APP_TITLE = "Milk Weight Analyzer";
    Button printButton = new Button();

    private VBox reportHolder;

    @Override
    public void start(Stage primaryStage) throws Exception {


        // load button for top component
        VBox loadContaner = new VBox();
        Label dragFile = makeDragLabel();


        Separator separator1 = new Separator();
        separator1.setMaxWidth(400);
        minMaxOrAve.setId("minMaxOrAve");
        minMaxOrAve.setPadding(new Insets(5, 0, 0, 30));
        loadContaner.getChildren().addAll(dragFile, minMaxOrAve, separator1);

        // make button for printing
        printButton = new Button("Download all Data");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        printButton.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                //No Directory selected
            } else {
                try {
                    printAllData(new File(selectedDirectory.getAbsolutePath() + "\\MilkData.csv"));
                    Alert invalidFile = new Alert(Alert.AlertType.CONFIRMATION,
                            "Your data was successfully downloaded in the selected folder");
                    invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
                } catch (IOException e) {
                    Alert invalidFile = new Alert(Alert.AlertType.WARNING,
                            "Unknown error the file did not download");
                    invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
                }

            }
        });

        // make left component
        TabPane leftComponent = makeLeftComponent();
        leftComponent.setPrefWidth(leftCompWidth);

        leftComponent.setOnMouseMoved(event -> { // code for dragging left component to the right or
            // left
            if (event.getX() < leftCompWidth - 15 || event.getX() > leftCompWidth + 15)
                scene.setCursor(Cursor.DEFAULT);
            else
                scene.setCursor(Cursor.H_RESIZE);
        });
        leftComponent.setOnMouseDragged(event -> { // code for dragging left component to the right or
            // left
            if (scene.getCursor().equals(Cursor.H_RESIZE) && event.getX() > 10
                    && event.getX() < WINDOW_WIDTH - 10)
                leftCompWidth = (int) event.getX();
            leftComponent.setPrefWidth(leftCompWidth);


        });

        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // add top, left, center, right, and bottom components
        root.setTop(makeTopComponents(loadContaner));

        root.setLeft(leftComponent);
        makeTable();
        table.setOnMouseMoved(event -> {
            if (event.getX() > 15)
                scene.setCursor(Cursor.DEFAULT);
        }); // for expanding tabpane
        root.setCenter(table);

        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


        // Add the stuff and set the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox makeTopComponents(VBox loadContainer) {
        HBox top = new HBox();
        VBox clearStack = new VBox();

        Button originalData = new Button("Display Original Data");

        Button clearData = new Button("Clear Loaded Data");

        Region spacer = new Region();
        spacer.setPrefWidth(370);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        originalData.setStyle("-fx-focus-color: lightblue;");
        clearData.setStyle("-fx-focus-color: lightblue;");

        clearData.setMaxWidth(Double.MAX_VALUE);
        originalData.setMaxWidth(Double.MAX_VALUE);

        Alert clearAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete all data that you have loaded?");

        clearData.setOnAction(event -> {

            Optional<ButtonType> action = clearAlert.showAndWait();
            if (action.get() == ButtonType.OK) {
                data.clear();
                table.setItems(FXCollections.observableArrayList(data));
            }
        });

        originalData.setOnAction(event -> {
            table.setItems(FXCollections.observableArrayList(data));
        });

        clearStack.getChildren().addAll(originalData, clearData);
        clearStack.setSpacing(5);
        clearStack.setPadding(new Insets(5,0,0,10));

        top.getChildren().addAll(loadContainer, spacer, clearStack);

        return top;

    }

    /**
     * method used to create the label that is used to drag and drop a file
     *
     * @return
     */
    private Label makeDragLabel() {
        Label dragFile = new Label("Drag and Drop File Here");
        Tooltip dragTip = new Tooltip("drag files here from folder to add to data set");
        Tooltip.install(dragFile, dragTip);
        dragFile.setId("drop");
        dragFile.setPadding(new Insets(10, 10, 0, 10));
        dragFile.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles())
                event.acceptTransferModes(TransferMode.COPY);
        });

        dragFile.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            String filePath;

            // add data in provided files to data set
            for (int i = 0; i < files.size(); i++) {
                filePath = files.get(i).getAbsolutePath();

                if (validFile) {
                    data.addAll(loadFile(filePath));
                }
            }
            validFile = true;

            table.setItems(FXCollections.observableArrayList(data));
            table.getColumns().get(0).setVisible(true);
            table.getColumns().get(1).setVisible(false);
            table.getColumns().get(2).setVisible(true);
            table.getColumns().get(3).setVisible(true);
            table.getColumns().get(4).setVisible(false);
        });

        return dragFile;
    }

    /**
     * used to load a file dragged and dropped the the appropriate location
     *
     * @param inputFilePath
     * @return
     */
    private List<FarmData> loadFile(String inputFilePath) {

        List<FarmData> inputList = new ArrayList<>();
        try {
            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            Alert invalidFile = new Alert(Alert.AlertType.WARNING,
                    "One or more of the provided files had invalid csv format");
            invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
            validFile = false;
        }


        return inputList;
    }

    private Function<String, FarmData> mapToItem = (line) -> {

        String[] curFarm = line.split(COMMA);// a CSV has comma separated lines


        String[] dateParts = curFarm[0].split("-"); // use to get month which will be at index 1

        return new FarmData(curFarm[0], getMonth(Integer.parseInt(dateParts[1])), curFarm[1],
                Integer.parseInt(curFarm[2]));


    };

    /**
     * private method used to return month given an int
     *
     * @param month
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private void makeTable() {
        table = new TableView<FarmData>();

        TableColumn<FarmData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("date"));

        TableColumn<FarmData, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("month"));

        TableColumn<FarmData, String> farmIDCol = new TableColumn<>("FarmID");
        farmIDCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("farmID"));

        TableColumn<FarmData, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<FarmData, Integer>("weight"));


        TableColumn<FarmData, String> percentCol = new TableColumn<>("Percent");
        percentCol.setCellValueFactory(new PropertyValueFactory<FarmData, String>("percent"));


        table.getColumns().setAll(dateCol, monthCol, farmIDCol, weightCol, percentCol);
        table.getColumns().get(1).setVisible(false);
        table.getColumns().get(4).setVisible(false);

        // make tooltip for sorting table
        Tooltip t = new Tooltip("click column title to sort");
        Tooltip.install(table, t);

        // set table properties
        table.setPrefWidth(400);
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        table.setPadding(new Insets(20, 20, 20, 20));

    }


    private void determineMax() {
        try {
            FarmData max = formatedData.stream().max(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText(max.toString());
        } catch (NoSuchElementException ignored) {

        }

    }

    private void determineMin() {
        try {
            FarmData min = formatedData.stream().min(Comparator.comparing(FarmData::getWeight)).get();
            minMaxOrAve.setText(min.toString());
        } catch (NoSuchElementException ignored) {

        }
    }

    private void determineAverage() {

        if (data.size() == 0)
            return;

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        minMaxOrAve.setText("Average weight : " + sum / data.size());
    }

    private void setPercent(List<FarmData> farmData) {

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        for (FarmData farm : farmData) {
            farm.setPercent(df.format((farm.getWeight() / sum) * 100) + "%"); // format decimal for %
        }

    }

    private List<FarmData> makeListForFarmReport() {
        List<FarmData> weightByMonths = new ArrayList<>();
        weightByMonths.add(new FarmData("Jan"));
        weightByMonths.add(new FarmData("Feb"));
        weightByMonths.add(new FarmData("March"));
        weightByMonths.add(new FarmData("April"));
        weightByMonths.add(new FarmData("May"));
        weightByMonths.add(new FarmData("June"));
        weightByMonths.add(new FarmData("July"));
        weightByMonths.add(new FarmData("Aug"));
        weightByMonths.add(new FarmData("Sep"));
        weightByMonths.add(new FarmData("Oct"));
        weightByMonths.add(new FarmData("Nov"));
        weightByMonths.add(new FarmData("Dec"));

        return weightByMonths;
    }


    private void farmReport() throws IOException {
        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(true); // make month visible
        table.getColumns().get(2).setVisible(false); // make farmID not visible
        table.getColumns().get(3).setVisible(true); //make weight Visible

        formatedData = data.stream()
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
                        && farmData.getFarmID().equalsIgnoreCase(farmIdInput))
                .collect(Collectors.toList());


        List<FarmData> weightByMonths = makeListForFarmReport();

        String[] dateParts;

        for (FarmData farm : formatedData) {

            dateParts = farm.getDate().split("-");
            switch (Integer.parseInt(dateParts[1])) {
                case 1:
                    weightByMonths.get(0).addWeight(farm.getWeight());

                    break;
                case 2:
                    weightByMonths.get(1).addWeight(farm.getWeight());
                    break;
                case 3:
                    weightByMonths.get(2).addWeight(farm.getWeight());
                    break;
                case 4:
                    weightByMonths.get(3).addWeight(farm.getWeight());
                    break;
                case 5:
                    weightByMonths.get(4).addWeight(farm.getWeight());
                    break;
                case 6:
                    weightByMonths.get(5).addWeight(farm.getWeight());
                    break;
                case 7:
                    weightByMonths.get(6).addWeight(farm.getWeight());
                    break;
                case 8:
                    weightByMonths.get(7).addWeight(farm.getWeight());
                    break;
                case 9:
                    weightByMonths.get(8).addWeight(farm.getWeight());
                    break;
                case 10:
                    weightByMonths.get(9).addWeight(farm.getWeight());
                    break;
                case 11:
                    weightByMonths.get(10).addWeight(farm.getWeight());
                    break;
                case 12:
                    weightByMonths.get(11).addWeight(farm.getWeight());
                    break;

            }
        }

        setPercent(weightByMonths);


        table.setItems(FXCollections.observableArrayList(weightByMonths));

    }

    private void annualReport() throws IOException {

        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(false); // make month visible
        table.getColumns().get(2).setVisible(true); // make farmID not visible
        table.getColumns().get(3).setVisible(true); //make weight Visible
        // data.add(new FarmData("Sion's farm", 4558858));

        List<FarmData> annualFarmWeight = new ArrayList<>();


        formatedData = data.stream()
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput))
                .collect(Collectors.toList());

        ////////////////
        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }

        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }

        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }



        for (String e : IDs) {
            annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
        }


        setPercent(annualFarmWeight);

        table.setItems(FXCollections.observableArrayList(annualFarmWeight));
    }


    private void monthlyReport() throws IOException {


        table.getColumns().get(0).setVisible(false); // make date
        table.getColumns().get(1).setVisible(false); // make month
        table.getColumns().get(2).setVisible(true); // make farmID
        table.getColumns().get(3).setVisible(true); //make weight

        List<FarmData> annualFarmWeight = new ArrayList<>();


        formatedData = data.stream()
                .filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
                        && farmData.getDate().substring(5, 6).equalsIgnoreCase(monthInput))
                .collect(Collectors.toList());


        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }

        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }

        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }

        for (String farm : IDs) {
            annualFarmWeight.add(new FarmData(farm, hashtable.get(farm)));
        }


        setPercent(annualFarmWeight);

        table.setItems(FXCollections.observableArrayList(annualFarmWeight));


    }


    // List<Person> personListFiltered = personList.stream()
    // .filter(distinctByKey(p -> p.getName()))
    // .collect(Collectors.toList());
    private void dateRangeReport() throws IOException {

        String[] startDate = startDateInput.split("-");
        String[] endDate = endDateInput.split("-");

        Alert invalidDate = new Alert(Alert.AlertType.WARNING,
                "There entered date was not in the correct format");

        // three distinct numbers were not entered
        if(startDate.length != 3 || endDate.length != 3){
            invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
            return;
        }

        // check that a zero was not entered before the day of the month
        if(startDate[1].charAt(0) == '0' || endDate[1].charAt(0) == '0' || startDate[2].charAt(0) == '0' || endDate[2].charAt(0) == '0'){
            invalidDate.setContentText("Please do not enter a 0 in front of the day or the month");
            invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
            return;
        }

        for(int i = 0; i < 3; i++){
            if( !isNumeric(startDate[i]) && !isNumeric(endDate[i])){
                invalidDate.setContentText("part of your date was not a number");
                invalidDate.showAndWait().filter(alert -> alert == ButtonType.OK);
                return;
            }
        }



        Date d1 = stringToDate(startDateInput);
        Date d2 = stringToDate(endDateInput);

        ///////////////////////////////

        table.getColumns().get(0).setVisible(false); // make date not visible
        table.getColumns().get(1).setVisible(false); // make month visible
        table.getColumns().get(2).setVisible(true); // make farmID not visible
        table.getColumns().get(3).setVisible(true); //make weight Visible
        // data.add(new FarmData("Sion's farm", 4558858));

        List<FarmData> annualFarmWeight = new ArrayList<>();



        formatedData =
                data.stream().filter(farmData -> dateInRange(d1, d2, stringToDate(farmData.getDate())))
                        .collect(Collectors.toList());

        HashSet<String> IDs = new HashSet<String>();
        for (FarmData e : formatedData) {
            IDs.add(e.getFarmID());
        }

        Hashtable<String, Integer> hashtable = new Hashtable<String, Integer>();
        for (String e : IDs) {
            hashtable.put(e, 0);
        }

        for (FarmData entry : formatedData) {
            String id = entry.getFarmID();
            Integer oldweight = hashtable.get(id);
            Integer newWeight = oldweight + entry.getWeight();
            hashtable.replace(id, newWeight);
        }


        for (String e : IDs) {
            annualFarmWeight.add(new FarmData(e, hashtable.get(e)));
        }


        setPercent(annualFarmWeight);

        table.setItems(FXCollections.observableArrayList(annualFarmWeight));


        ///////////////////////////////
        // boolean b = dateInRange(startDate,startDate,startDate);


    }

    public static boolean isNumeric( String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    private boolean dateInRange(Date d1, Date d2, Date d3) {
        return d3.after(d1) && d3.before(d2);
    }

    private Date stringToDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        try {
            d = format.parse(s);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

    private void printAllData(File destFile) throws IOException {
        FileWriter writer = new FileWriter(destFile);
        writer.write("date,farm_id,weight\n");

        // write each data element in formatted
        for (FarmData farm : data) {
            writer.write(farm.printToCsvFile() + "\n");
        }
        writer.close();
    }

    /**
     * private helper method to clear all red required labels
     */
    private void hideAllRequired(TextField farmIdInField, Label farmId, TextField monthInField, Label month, TextField yearInField, Label year,
                                 TextField startDateInField, Label start, TextField endDateInField, Label end) {

        hideRequired(farmIdInField, farmId, "FarmID");
        hideRequired(monthInField, month, "Month");
        hideRequired(yearInField, year, "Year");
        hideRequired(startDateInField, start, "Start");
        hideRequired(endDateInField, end, "End");
    }

//    /*
//    private helper method that clears all text fields
//     */
//    private void clearAllFields(TextField farmIdInField, TextField monthInField, TextField yearInField,
//                                TextField startDateInField, TextField endDateInField) {
//        farmIdInField.clear();
//        monthInField.clear();
//        yearInField.clear();
//        startDateInField.clear();
//        endDateInField.clear();
//    }


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
        Insets requiredPadd = new Insets(10, 0, 0, 5);
        Label farmId = new Label("Farm ID");
        TextField farmIdInField = new TextField();
        farmIdInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        farmIdInField.textProperty().addListener(event -> {
            farmIdInput = farmIdInField.getText();
        });


        Label start = new Label("Start Date");
        TextField endDateInField = new TextField();
        Label end = new Label("End Date");
        TextField startDateInField = new TextField();


        Label year = new Label("Year");
        year.setPadding(inputPadd);

        TextField yearInField = new TextField();
        yearInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        yearInField.textProperty().addListener(event -> {
            yearInput = yearInField.getText();
        });


        Label month = new Label("Month");
        month.setPadding(inputPadd);
        TextField monthInField = new TextField();


        // set listener for reading
        monthInField.textProperty().addListener(event -> {
            monthInput = monthInField.getText();
        });


        VBox searchDataHolder = new VBox();
        searchDataHolder.getChildren().addAll(farmId, farmIdInField, year, yearInField, month, monthInField);
        searchDataHolder.setAlignment(Pos.CENTER_LEFT);
        searchDataHolder.setPadding(new Insets(0, 60, 0, 20));


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
        percentTog.setOnAction(event -> {
            if (percentTog.isSelected())
                table.getColumns().get(4).setVisible(true);
            else
                table.getColumns().get(4).setVisible(false);
        });


        ToggleButton minTog = new ToggleButton();
        minTog.setOnAction(event -> {
            if (minTog.isSelected())
                determineMin();
            else
                minMaxOrAve.setText("");
        });

        ToggleButton maxTog = new ToggleButton();
        maxTog.setOnAction(event -> {
            if (maxTog.isSelected())
                determineMax();
            else
                minMaxOrAve.setText("");
        });

        ToggleButton averageTog = new ToggleButton();
        averageTog.setOnAction(event -> {
            if (averageTog.isSelected())
                determineAverage();
            else
                minMaxOrAve.setText("");
        });

        // creating region for spacing of label and button
        Region spacing1 = new Region();
        Region spacing2 = new Region();
        Region spacing3 = new Region();
        Region spacing4 = new Region();
        HBox.setHgrow(spacing1, Priority.ALWAYS);
        HBox.setHgrow(spacing2, Priority.ALWAYS);
        HBox.setHgrow(spacing3, Priority.ALWAYS);
        HBox.setHgrow(spacing4, Priority.ALWAYS);

        percentHolder.getChildren().addAll(percentL, spacing1, percentTog);
        percentHolder.setPadding(new Insets(10, 10, 15, 10));
        minHolder.getChildren().addAll(minL, spacing2, minTog);
        minHolder.setPadding(new Insets(0, 10, 0, 10));
        maxHolder.getChildren().addAll(maxL, spacing3, maxTog);
        maxHolder.setPadding(new Insets(0, 10, 0, 10));
        averageHolder.getChildren().addAll(averageL, spacing4, averageTog);
        averageHolder.setPadding(new Insets(0, 10, 0, 10));

        hboxHolder.getChildren().addAll(percentHolder, minHolder, maxHolder, averageHolder);
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

        farmReport.setMaxWidth(Double.MAX_VALUE);
        farmReport.setOnAction(event -> {
            try {
                if (farmIdInput != null && yearInput != null) {
                    {
                        farmReport();
                        hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year, startDateInField, start, endDateInField, end);
                        monthInField.clear();
                        startDateInField.clear();
                        endDateInField.clear();
                        monthInput = null;
                        startDateInput = null;
                        endDateInput = null;
                    }
                } else {
                    showRequired(farmIdInField, farmId, "FarmID *Required");
                    showRequired(yearInField, year, "Year *Required");

                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                    hideRequired(monthInField, month, "Month");
                    if (farmIdInput != null) hideRequired(farmIdInField, farmId, "FarmID");
                    if (yearInput != null) hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button annualReport = new Button("Annual Report");
        annualReport.setMaxWidth(Double.MAX_VALUE);
        annualReport.setOnAction(event -> {
            try {
                if (yearInput != null) {
                    annualReport();
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year, startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    startDateInField.clear();
                    endDateInField.clear();
                    monthInField.clear();
                    farmIdInput = null;
                    monthInput = null;
                    startDateInput = null;
                    endDateInput = null;
                } else {
                    showRequired(yearInField, year, "Year *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(monthInField, month, "Month");
                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });

        Button monthReport = new Button("Monthly Report");
        monthReport.setMaxWidth(Double.MAX_VALUE);
        monthReport.setOnAction(event -> {
            try {
                if (monthInput != null && yearInput != null) {
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year, startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    startDateInField.clear();
                    endDateInField.clear();
                    monthlyReport();

                    farmIdInput = null;
                    startDateInput = null;
                    endDateInput = null;
                } else {
                    showRequired(monthInField, month, "Month *Required");
                    showRequired(yearInField, year, "Year *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(yearInField, year, "Year");
                    hideRequired(startDateInField, start, "Start");
                    hideRequired(endDateInField, end, "End");
                    if (monthInput != null) hideRequired(monthInField, month, "Month");
                    if (yearInput != null) hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });


        reportHolder.getChildren().addAll(farmReport, annualReport, monthReport);
        // HBox.setHgrow(farmReport, Priority.ALWAYS);


        // make start date and end date textfields
        VBox startHolder = new VBox(5);
        VBox endHolder = new VBox(5);
        HBox datesHold = new HBox(5);
        VBox d = new VBox(5);

        Label exampleDate = new Label("yyyy-mm-dd");

        startDateInField.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        startDateInField.textProperty().addListener(event -> {
            startDateInput = startDateInField.getText();
        });


        endDateInField.setMaxWidth(Double.MAX_VALUE);

        //  set listener for reading end date
        endDateInField.textProperty().addListener(event -> endDateInput = endDateInField.getText());


        Label dash = new Label("-");
        Label filler = new Label("-");

        Label exampleDate2 = new Label("yyyy-mm-dd");

        // set up region where user enters dates
        filler.setVisible(false);
        dash.setPadding(new Insets(0, 5, 0, 5));
        d.getChildren().addAll(filler, dash);
        endDateInField.setMaxWidth(Double.MAX_VALUE);
        startHolder.getChildren().addAll(start, exampleDate, startDateInField);
        startHolder.setPadding(new Insets(0, 0, 0, 10));
        endHolder.getChildren().addAll(end, exampleDate2, endDateInField);
        endHolder.setPadding(new Insets(0, 10, 0, 0));
        datesHold.getChildren().addAll(startHolder, d, endHolder);
        datesHold.setAlignment(Pos.BASELINE_CENTER);
        datesHold.setMaxWidth(Double.MAX_VALUE);


        // report data range button
        VBox rangeHolder = new VBox();
        Button rangeReport = new Button("Date Range Report");
        rangeReport.setMaxWidth(Double.MAX_VALUE);
        rangeHolder.getChildren().add(rangeReport);
        rangeHolder.setPadding(new Insets(5, 0, 0, 0));
        rangeReport.setOnAction(event -> {
            try {
                if (startDateInput != null) {
                    hideAllRequired(farmIdInField, farmId, monthInField, month, yearInField, year, startDateInField, start, endDateInField, end);
                    farmIdInField.clear();
                    monthInField.clear();
                    yearInField.clear();
                    dateRangeReport();
                } else {
                    showRequired(startDateInField, start, "Start *Required");
                    showRequired(endDateInField, end, "End *Required");
                    hideRequired(farmIdInField, farmId, "FarmID");
                    hideRequired(monthInField, month, "Month");
                    hideRequired(yearInField, year, "Year");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        });

        viewComponent.getChildren().addAll(searchDataHolder, hboxHolder, reportHolder, datesHold,
                rangeHolder);
        return viewComponent;
    }


    private VBox makeEditTab() {
        VBox editComponent = new VBox(); // holds view and Edit button
        editComponent.setStyle("-fx-background-color: #FFFFFF;");
        editComponent.setPrefWidth(200);
        editComponent.setMaxHeight(600);
        editComponent.setPadding(new Insets(0, 10, 10, 30));
        Label instr = new Label("yyyy-mm-dd");
        instr.setPadding(new Insets(50, 0, 0, 0));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdIn = new TextField();
        farmId.setPadding(new Insets(50, 0, 0, 0));
        farmIdIn.setMaxWidth(Double.MAX_VALUE);
        farmIdIn.textProperty().addListener(event -> {
            newFarmID = farmIdIn.getText();
        });


        Label date = new Label("Date");
        date.setPadding(inputPadd);
        TextField dateIn = new TextField();
        dateIn.setMaxWidth(Double.MAX_VALUE);
        dateIn.textProperty().addListener(event -> {
            newDate = dateIn.getText();
        });


        Label weight = new Label("Weight");
        weight.setPadding(inputPadd);
        TextField weightIn = new TextField();
        weightIn.setMaxWidth(Double.MAX_VALUE);
        weightIn.textProperty().addListener(event -> {
            newWeight = weightIn.getText();
        });

        // report data range button
        VBox addHolder = new VBox();
        Button addData = new Button("Add Data");

        // set action for adding data including required labels
        Alert badData = new Alert(Alert.AlertType.WARNING,
                "There entered date was not in the correct format");

        addData.setOnAction(event -> {
            if (newFarmID != null && newDate != null && newWeight != null) {
                hideRequired(farmIdIn, farmId, "FarmID");
                hideRequired(dateIn, date, "Date");
                hideRequired(weightIn, weight, "Weight");

                // check that data entered is parable
                String[] dateParts = newDate.split("-");



                // three distinct numbers were not entered
                if(dateParts.length != 3 ){
                    badData.showAndWait();
                    return;
                }

                // check that a zero was not entered before the day of the month
                if(dateParts[1].charAt(0) == '0' || dateParts[2].charAt(0) == '0' ){
                    badData.setContentText("Please do not enter a 0 in front of the day or the month");
                    badData.showAndWait();
                    return;
                }

                for(int i = 0; i < 3; i++) {
                    if (!isNumeric(dateParts[i])) {
                           badData.setContentText("part of your date was not a number");
                          badData.showAndWait();
                           return;
                    }
                }

                    if(!isNumeric(newWeight)){
                        badData.setContentText("please enter a number");
                        badData.showAndWait();
                        return;
                    }

                data.add(new FarmData(newDate, newFarmID, Integer.parseInt(newWeight)));
                    table.setItems(FXCollections.observableArrayList(data));

                // set up for another entry
                farmIdIn.clear();
                dateIn.clear();
                weightIn.clear();
                newFarmID = null;
                newDate = null;
                newWeight = null;
            } else {
                showRequired(farmIdIn, farmId, "FarmID *Required");
                showRequired(dateIn, date, "Date *Required");
                showRequired(weightIn, weight, "Weight *Required");

                // some values may have already been entered
                if (newFarmID != null) hideRequired(farmIdIn, farmId, "FarmID");
                if (newDate != null) hideRequired(dateIn, date, "Date");
                if (newWeight != null) hideRequired(weightIn, weight, "Weight");

            }
        });

        addHolder.getChildren().addAll(addData, printButton);
        addHolder.setSpacing(10);
        addHolder.setPadding(new Insets(30, 0, 0, 0));

        editComponent.getChildren().addAll(instr, farmId, farmIdIn, date, dateIn, weight, weightIn,
                addHolder);
        editComponent.setPadding(new Insets(0, 60, 0, 30));

        return editComponent;
    }

    private void showRequired(TextField t, Label l1, String label) {
        l1.setText(label);
        t.setStyle("-fx-text-inner-color: red");
        t.setStyle("-fx-text-box-border: red ;");
        t.setStyle("-fx-focus-color: red ;");
        l1.setTextFill(Color.web("#FF0000")); // make boarder read
    }

    private void hideRequired(TextField t, Label l1, String required) {
        l1.setText(required);
        t.setStyle("-fx-text-inner-color: black ;");
        t.setStyle("-fx-text-box-border: black ;");
        t.setStyle("-fx-focus-color: black ;");
        l1.setTextFill(Color.web("#000000")); // make boarder black
    }


    private void popupHandler() {
        // create a label
        Stage stage = new Stage();
        TilePane popup = new TilePane();

        Label label = new Label("File succesfully added to current data");

        Button close = new Button("Close");

        close.setOnAction(event -> {
            stage.hide();
        });

        // set background
        label.setStyle(" -fx-background-color: white;");
        // set size of label
        label.setMinWidth(80);
        label.setMinHeight(50);

        popup.getChildren().addAll(label, close);

        Scene s = new Scene(popup, 200, 100);
        stage.setScene(s);
        stage.setAlwaysOnTop(true);
        stage.show();
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


}