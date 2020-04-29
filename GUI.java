package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GUI {
	
    private String farmIdInput; // used to store farmId user entered
    private String yearInput; // used to store year user entered
    private String monthInput; // used to store month user entered

	
	private VBox reportHolder;
	private static final String COMMA = "\\s*,\\s*";
	private Set<FarmData> data = new HashSet<FarmData>();
	private TableView<FarmData> table;
	private boolean validFile = true;
	private Label minMaxOrAve;
	
	public GUI (Set<FarmData> data, TableView<FarmData> table) {
		this.data = data;
		this.table = table;
	}
	
	public GUI (Set<FarmData> data, TableView<FarmData> table, Label minMaxOrAve) {
		this.data = data;
		this.table = table;
		this.validFile = validFile;
		this.minMaxOrAve = minMaxOrAve;
	}
	
	/**
	 * Returns the load button required at the top of the GUI.
	 * @return
	 */
	VBox makeLoadButton() {
		VBox loadContainer = new VBox();
        Label dragFile = makeDragLabel();      
        Separator separator1 = new Separator();
        separator1.setMaxWidth(400);
        minMaxOrAve.setId("minMaxOrAve");
        minMaxOrAve.setPadding(new Insets(5, 0, 0, 30));
        loadContainer.getChildren().addAll(dragFile, minMaxOrAve, separator1);
        return loadContainer;
	}

	/**
	 * Method used to create the label that is used to drag and drop a file
	 * 
	 * @return
	 */
	Label makeDragLabel() {
		FileManager fileManager = new FileManager();
		Label dragFile = new Label("Drag and Drop File Here");
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
					data.addAll(fileManager.loadFile(filePath));
					validFile = fileManager.checkValidity();
				}

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

	VBox makeViewTab() {
		FileManager fileManager = new FileManager();
		DataManager dataManager = new DataManager();
		
		VBox viewComponent = new VBox(); // holds view and Edit button
		viewComponent.setStyle("-fx-background-color: #FFFFFF;");
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
			if (percentTog.isSelected())
				table.getColumns().get(4).setVisible(true);
			else
				table.getColumns().get(4).setVisible(false);
		});

		ToggleButton minTog = new ToggleButton();
		minTog.setOnAction(event -> {
			if (minTog.isSelected())
				dataManager.determineMin();
			else
				minMaxOrAve.setText("");
		});

		ToggleButton maxTog = new ToggleButton();
		maxTog.setOnAction(event -> {
			if (maxTog.isSelected())
				dataManager.determineMax();
			else
				minMaxOrAve.setText("");
		});

		ToggleButton averageTog = new ToggleButton();
		averageTog.setOnAction(event -> {
			if (averageTog.isSelected())
				dataManager.determineAverage();
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
		// farmReport.setPrefWidth(prefButtonWidth);
		farmReport.setMaxWidth(Double.MAX_VALUE);
		farmReport.setOnAction(event -> {
			try {
				dataManager.farmReport(farmIdInput, yearInput);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Button annualReport = new Button("Annual Report");
		annualReport.setMaxWidth(Double.MAX_VALUE);
		annualReport.setOnAction(event -> dataManager.annualReport(yearInput));

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

	VBox makeEditTab() {
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
	
	TableView<FarmData> makeTable() {
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
//	        table.getSelectionModel().selectedIndexProperty().
//	                addListener(new RowChangeHandler());
//	        table.setEditable(true);
//	        farmIDCol.setCellFactory(((TextFieldTableCell.forTableColumn())));
//	        weightCol.setOnEditCommit(event -> (event.getTableView().
//	                getItems().get(event.getTablePosition().getRow())).
//	                setWeight(event.getNewValue()));
//	        txtStatus = new Text();


	        table.setPadding(new Insets(20, 20, 20, 20));
	        
	        return table;

	    }
}
