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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;

import static javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn;

public class Main extends Application {
    private static final String COMMA = "\\s*,\\s*";
    private String farmIdInput; // used to store farmId user entered
    private String yearInput; // used to store year user entered
    private String monthInput; // used to store month user entered

    Scene scene;

    private TableView<FarmData> table;
    private Set<FarmData> data = new HashSet<FarmData>();
    private List<FarmData> formatedData = new ArrayList<>();
    private Label minMaxOrAve = new Label();
    private boolean validFile = true;


    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 700;
    private int leftCompWidth = 200;
    private int prefButtonWidth = 200;
    private static final String APP_TITLE = "Milk Weight Analyzer";

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

        // add top, left, center, right, and bottom components
        root.setTop(loadContaner);

        root.setLeft(leftComponent);
        makeTable();
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
     * method used to create the label that is used to drag and drop a file
     * @return
     */
    private Label makeDragLabel(){
        Label dragFile = new Label("Drag and Drop File Here");
        dragFile.setId("drop");
        dragFile.setPadding(new Insets(10, 10, 0, 10));
        dragFile.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.COPY);
        });

        dragFile.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            String filePath;

            // add data in provided files to data set
            for(int i = 0; i < files.size(); i++){
                filePath = files.get(i).getAbsolutePath();

                if(validFile) data.addAll(loadFile(filePath));

            }
            validFile = true;

            table.setItems(FXCollections.observableArrayList(data));
            table.getColumns().get(0).setVisible(true);
            table.getColumns().get(1).setVisible(false);
            table.getColumns().get(2).setVisible(true);
            table.getColumns().get(3).setVisible(true);
            table.getColumns().get(4).setVisible(false);
            System.out.println(data.size());
        });
        
        return dragFile;
    }

    /**
     * used to load a file dragged and dropped the the appropriate location
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

        } catch (Exception e){
              Alert invalidFile = new Alert(Alert.AlertType.WARNING, "One or more of the provided files had invalid csv format");
              invalidFile.showAndWait().filter(alert -> alert == ButtonType.OK);
              validFile = false;
        }

        return inputList;
    }

    private Function<String, FarmData> mapToItem  = (line) -> {

        String[] curFarm = line.split(COMMA);// a CSV has comma separated lines


        String[] dateParts = curFarm[0].split("-"); // use to get month which will be at index 1

        return new FarmData(curFarm[0], getMonth(Integer.parseInt(dateParts[1])), curFarm[1], Integer.parseInt(curFarm[2]));


    };

    /**
     * private method used to return month given an int
     * @param month
     */
    private String getMonth(int month){
        return new DateFormatSymbols().getMonths()[month-1];
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


        table.getColumns().setAll(dateCol,monthCol, farmIDCol, weightCol, percentCol);
        table.getColumns().get(1).setVisible(false);
        table.getColumns().get(4).setVisible(false);

        // set table properties
        table.setPrefWidth(400);
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.getSelectionModel().selectedIndexProperty().
//                addListener(new RowChangeHandler());
//        table.setEditable(true);
//        farmIDCol.setCellFactory(((TextFieldTableCell.forTableColumn())));
//        weightCol.setOnEditCommit(event -> (event.getTableView().
//                getItems().get(event.getTablePosition().getRow())).
//                setWeight(event.getNewValue()));
//        txtStatus = new Text();


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
            System.out.println(min);
            minMaxOrAve.setText(min.toString());
        } catch (NoSuchElementException ignored) {

        }
    }

    private void determineAverage() {

        if (data.size() == 0) return;

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        minMaxOrAve.setText("Average weight : " + sum / data.size());
    }

    private void setPercent(List<FarmData> farmData){

        double sum = 0;

        for (FarmData farm : formatedData) {
            sum += farm.getWeight();
        }

        DecimalFormat  df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        for(FarmData farm : farmData) {
            farm.setPercent(df.format((farm.getWeight() / sum) * 100) + "%"); // format decimal for %
        }

    }

    private List<FarmData> makeListForFarmReport(){
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

        formatedData = data.stream().filter(farmData ->
             farmData.getDate().substring(0,4).equalsIgnoreCase(yearInput) && farmData.getFarmID().equals(farmIdInput)
        ).collect(Collectors.toList());


        List<FarmData> weightByMonths = makeListForFarmReport();

        String[] dateParts;

        System.out.println("farmReport size: " + formatedData.size());
        for (FarmData farm : formatedData) {

           dateParts = farm.getDate().split("-");
            switch (Integer.parseInt(dateParts[1])){
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

//        boolean containes = false;
//        for (FarmData farm : farmReport) {
//            for (FarmData farmInTemp : weightByMonths) {
//                if(farm.getFarmID().equals(farmInTemp.getFarmID())){
//                    containes = true;
//                    farmInTemp.addWeight(farm.getWeight());
//                }
//            }
//            if(!containes){
//                weightByMonths.add(new FarmData(farm.getDate(), farm.getFarmID(), farm.getWeight()));
//            }
//            containes = false;
//        }
       setPercent(weightByMonths);


        table.setItems(FXCollections.observableArrayList(weightByMonths));
        printReport();
    }

    private void annualReport() {
        table.setVisible(true);
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(1).setVisible(true);

//        data = FXCollections.observableArrayList();
//        table.setItems(data);


        data.add(new FarmData("Sion's farm", 4558858));
    }

    private void monthlyReport() {
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(1).setVisible(true);

        System.out.println("hello");

        formatedData = data.stream().filter(farmData ->
                farmData.getDate().substring(0,4).equalsIgnoreCase(yearInput) && farmData.getMonth().equals(monthInput)
        ).collect(Collectors.toList()); // filtered out all entries without the desired year and month

        List<FarmData> monthFormat = formatedData.stream().filter(distinctByFarmId(FarmData::getFarmID)).collect(Collectors.toList());

        for (FarmData farm : monthFormat) {
            farm.setWeight(0);
            System.out.println(farm.getFarmID());
        }

        for(FarmData farm : formatedData){

        }


    }

    public static <T> Predicate<T> distinctByFarmId(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


//    List<Person> personListFiltered = personList.stream()
//            .filter(distinctByKey(p -> p.getName()))
//            .collect(Collectors.toList());
    private void dateRangeReport() {

    }

    private void printReport() throws IOException {
        File newFile = new File(System.getProperty("user.dir") );
        File newFile2 = new File("C:\\Users\\sionc\\Downloads\\Temp\\testing.csv");
        FileWriter writer = new FileWriter(newFile2);
        writer.write("date,farm_id,weight\n");

        // write each data element in formatted
        for(FarmData farm : formatedData){
            writer.write(farm.printToCsvFile() + "\n");
        }
        writer.close();
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
        TextField farmIdIn = new TextField();
        farmIdIn.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        farmIdIn.setOnAction(event -> {
            farmIdInput = farmIdIn.getText();
            farmIdIn.clear();
        });



        Label year = new Label("Year");
        year.setPadding(inputPadd);
        TextField yearIn = new TextField();
        yearIn.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        yearIn.setOnAction(event -> {
            yearInput = yearIn.getText();
            yearIn.clear();
        });


        Label month = new Label("Month");
        month.setPadding(inputPadd);
        TextField monthIn = new TextField();
        monthIn.setMaxWidth(Double.MAX_VALUE);

        // set listener for reading
        monthIn.setOnAction(event -> {
            monthInput = monthIn.getText();
            monthIn.clear();
        });

        VBox searchDataHolder = new VBox();
        searchDataHolder.getChildren().addAll(farmId, farmIdIn, year, yearIn, month, monthIn);
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
            if (percentTog.isSelected()) table.getColumns().get(4).setVisible(true);
            else table.getColumns().get(4).setVisible(false);
        });


        ToggleButton minTog = new ToggleButton();
        minTog.setOnAction(event -> {
            if (minTog.isSelected()) determineMin();
            else minMaxOrAve.setText("");
        });

        ToggleButton maxTog = new ToggleButton();
        maxTog.setOnAction(event -> {
            if (maxTog.isSelected()) determineMax();
            else minMaxOrAve.setText("");
        });

        ToggleButton averageTog = new ToggleButton();
        averageTog.setOnAction(event -> {
            if (averageTog.isSelected()) determineAverage();
            else minMaxOrAve.setText("");
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
        //  farmReport.setPrefWidth(prefButtonWidth);
        farmReport.setMaxWidth(Double.MAX_VALUE);
        farmReport.setOnAction(event -> {
            try {
                farmReport();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button annualReport = new Button("Annual Report");
        annualReport.setMaxWidth(Double.MAX_VALUE);
        annualReport.setOnAction(event -> annualReport());

        Button monthReport = new Button("Monthly Report");
        monthReport.setMaxWidth(Double.MAX_VALUE);
        monthReport.setOnAction(event -> monthlyReport());

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
        farmIdInfo.setMaxWidth(Double.MAX_VALUE);
        ;


        Label date = new Label("Date");
        date.setPadding(inputPadd);
        TextField dateIn = new TextField();
        dateIn.setMaxWidth(Double.MAX_VALUE);
        ;

        Label weight = new Label("Weight");
        weight.setPadding(inputPadd);
        TextField weightInput = new TextField();
        weightInput.setMaxWidth(Double.MAX_VALUE);
        ;

        // report data range button
        VBox addHolder = new VBox();
        Button addData = new Button("Add Data");
        // addData.setPrefWidth(50);
        addHolder.getChildren().add(addData);
        addHolder.setPadding(new Insets(30, 0, 0, 0));

        editComponent.getChildren().addAll(instr, farmId, farmIdInfo, date, dateIn, weight, weightInput, addHolder);
        editComponent.setPadding(new Insets(0, 60, 0, 30));

        return editComponent;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }


}