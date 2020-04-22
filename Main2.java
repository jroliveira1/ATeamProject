package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main2 extends Application {
    int farmId; // used to store farmId user entered
    int year; // used to store year user entered
    int month; // used to store month user entered
    VBox select;
    
    private TableView<FarmReportData> table = new TableView<FarmReportData>();
    private final ObservableList<FarmReportData> data =
        FXCollections.observableArrayList(
            new FarmReportData("January", "100", "8.333%"),
            new FarmReportData("February", "100", "8.333%"),
            new FarmReportData("March", "100", "8.333%"),
            new FarmReportData("April", "100", "8.333%"),
            new FarmReportData("May", "100", "8.333%"),
            new FarmReportData("June", "100", "8.333%"),
            new FarmReportData("July", "100", "8.333%"),
            new FarmReportData("August", "100", "8.333%"),
            new FarmReportData("September", "100", "8.333%"),
            new FarmReportData("October", "100", "8.333%"),
            new FarmReportData("November", "100", "8.333%"),
            new FarmReportData("December", "100", "8.333%")
        );

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
        separator1.setMaxWidth(300);
        loadContaner.getChildren().addAll(loadBut, separator1);


        // Main layout is Border Pane example (top,left,center,right,bottom)
        BorderPane root = new BorderPane();

        // add top, left, center, right, and bottom components
        root.setTop(loadContaner);
        root.setLeft(makeLeftComponent());
        
        
        select = farmtableDisplay();
        root.setCenter(select);

        Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Add the stuff and set the primary stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void viewAction(Button viewButton) {
        clicks = 0;
        viewButton.setText(clicks + " clicks");
    }

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

    private VBox makeEditTab() {
        VBox editComponent = new VBox(); // holds view and Edit button
        editComponent.setStyle("-fx-background-color: #FFFFFF;");
        editComponent.setPrefWidth(200);
        editComponent.setMaxHeight(600);
        editComponent.setPadding(new Insets(0, 10, 10, 30));
        Label instr = new Label("11/07/1999 format");
        instr.setPadding(new Insets(50,0,0,0));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdInfo = new TextField();
        farmId.setPadding(new Insets(50, 0, 0, 0));
        farmIdInfo.setMaxWidth(100);


        Label date = new Label("Date");
        date.setPadding(inputPadd);
        TextField dateIn = new TextField();
        dateIn.setMaxWidth(100);

        Label weight = new Label("Weight");
        weight.setPadding(inputPadd);
        TextField weightInput = new TextField();
        weightInput.setMaxWidth(100);

        // report data range button
        VBox addHolder = new VBox();
        Button addData = new Button("Add Data");
      //  addData.setPrefWidth(50);
        addHolder.getChildren().add(addData);
        addHolder.setPadding(new Insets(30, 0, 0, 0));

        editComponent.getChildren().addAll(instr, farmId, farmIdInfo, date, dateIn, weight, weightInput,addHolder);

        return editComponent;
    }

    private VBox makeViewTab() {
        final ToggleGroup group = new ToggleGroup();
        VBox viewComponent = new VBox(); // holds view and Edit button
        viewComponent.setStyle("-fx-background-color: #FFFFFF;");
        viewComponent.setPrefWidth(200);
        viewComponent.setMaxHeight(600);
        viewComponent.setPadding(new Insets(0, 10, 10, 10));

        // farm year and month input
        Insets inputPadd = new Insets(10, 0, 0, 0);
        Label farmId = new Label("Farm ID");
        TextField farmIdInfo = new TextField();
        farmId.setPadding(new Insets(50, 0, 0, 0));
        farmIdInfo.setMaxWidth(100);


        Label year = new Label("Year");
        year.setPadding(inputPadd);
        TextField yearInput = new TextField();
        yearInput.setMaxWidth(100);

        Label month = new Label("Month");
        month.setPadding(inputPadd);
        TextField monthIn = new TextField();
        monthIn.setMaxWidth(100);



        // make radio buttons for diff views
        RadioButton displayTog = new RadioButton("Display %");
        displayTog.setPadding(new Insets(20, 0, 0, 0));
        RadioButton minTog = new RadioButton("Min");
        RadioButton maxTog = new RadioButton("Max");
        RadioButton averageTog = new RadioButton("Average");
        averageTog.setPadding(new Insets(0, 0, 150, 0));
        minTog.setToggleGroup(group);
        maxTog.setToggleGroup(group);
        averageTog.setToggleGroup(group);


        // make report type buttons
        VBox reportHolder = new VBox(5);

        Button farmReport = new Button("Farm Report");
        farmReport.setPrefWidth(200);
        //farmReport.setOnAction(e -> );

        Button annualReport = new Button("Annual Report");
        annualReport.setPrefWidth(200);

        Button monthReport = new Button("Monthly Report");
        monthReport.setPrefWidth(200);
        reportHolder.getChildren().addAll(farmReport, annualReport, monthReport);
        reportHolder.setPadding(new Insets(0, 0, 10, 0));

        // make start date and end date textfields
        VBox startHolder = new VBox(5);
        VBox endHolder = new VBox(5);
        HBox datesHold = new HBox(5);
        Label start = new Label("Start Date");
        TextField startDate = new TextField();
        startDate.setPrefWidth(90);
        TextField endDate = new TextField();
        Label end = new Label("End Date");
        Label dash = new Label("-");
        dash.setPadding(new Insets(25, 5, 0, 5));
        endDate.setPrefWidth(90);
        startHolder.getChildren().addAll(start, startDate);
        startHolder.setPadding(new Insets(0, 0, 0, 10));
        endHolder.getChildren().addAll(end, endDate);
        endHolder.setPadding(new Insets(0, 10, 0, 0));
        datesHold.getChildren().addAll(startHolder, dash, endHolder);


        // report data range button
        VBox rangeHolder = new VBox();
        Button rangeReport = new Button("Date Range Report");
        rangeReport.setPrefWidth(200);
        rangeHolder.getChildren().add(rangeReport);
        rangeHolder.setPadding(new Insets(5, 0, 0, 0));

        viewComponent.getChildren().addAll(farmId, farmIdInfo, year, yearInput, month, monthIn, displayTog, minTog, maxTog, averageTog, reportHolder, datesHold, rangeHolder);
        return viewComponent;
    }

    /**
     * creates instances of everything needed for the desired vBox
     *
     * @return
     */
    private VBox makeRightComponent() {
        VBox vBox = new VBox();
        ObservableList<String> possGrades = FXCollections.observableArrayList("A", "B", "C", "D", "F");
        ListView<String> grades = new ListView<>(possGrades);


        // tracks how many times the button was clicked
        Button clicksButton = new Button(clicks + " clicks");
        clicksButton.setOnAction(
                event -> clicksButton.setText(++clicks + " clicks")
        );
        clicksButton.setPadding(new Insets(20));

        Button resetButton = new Button("reset");
        resetButton.setOnAction(
                event -> viewAction(clicksButton)
        );
        resetButton.setPadding(new Insets(20));

        HBox buttonMan = new HBox();
        buttonMan.getChildren().addAll(clicksButton, resetButton);
        buttonMan.setSpacing(5);

        vBox.getChildren().addAll(buttonMan, grades);
        VBox.setMargin(buttonMan, new Insets(10));
        return vBox;
    }
    
    private VBox farmtableDisplay() {
      // VBox vbox = new VBox();
       final Label label = new Label("Farm Report");
       label.setFont(new Font("Arial", 20));

       table.setEditable(true);

       TableColumn firstNameCol = new TableColumn("Month");
       firstNameCol.setMinWidth(100);
       firstNameCol.setCellValueFactory(
               new PropertyValueFactory<FarmReportData, String>("firstName"));

       TableColumn lastNameCol = new TableColumn("Weight");
       lastNameCol.setMinWidth(100);
       lastNameCol.setCellValueFactory(
               new PropertyValueFactory<FarmReportData, String>("lastName"));

       TableColumn emailCol = new TableColumn("Percentage by month");
       emailCol.setMinWidth(200);
       emailCol.setCellValueFactory(
               new PropertyValueFactory<FarmReportData, String>("email"));

       table.setItems(data);
       table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

       final VBox vbox = new VBox();
       vbox.setSpacing(5);
       vbox.setPadding(new Insets(10, 0, 0, 10));
       vbox.getChildren().addAll(label, table);

   return vbox;

     }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
public static class FarmReportData {
      
      private final SimpleStringProperty firstName;
      private final SimpleStringProperty lastName;
      private final SimpleStringProperty email;

      private FarmReportData(String month, String weight, String percent) {
          this.firstName = new SimpleStringProperty(month);
          this.lastName = new SimpleStringProperty(weight);
          this.email = new SimpleStringProperty(percent);
      }

      public String getFirstName() {
          return firstName.get();
      }

      public void setFirstName(String fName) {
          firstName.set(fName);
      }

      public String getLastName() {
          return lastName.get();
      }

      public void setLastName(String fName) {
          lastName.set(fName);
      }

      public String getEmail() {
          return email.get();
      }

      public void setEmail(String fName) {
          email.set(fName);
      }
  }
}