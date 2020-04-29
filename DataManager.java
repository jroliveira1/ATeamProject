package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class DataManager {

	private List<FarmData> formatedData = new ArrayList<>();
	private Label minMaxOrAve = new Label();
	private boolean validFile = true;
	private Set<FarmData> data = new HashSet<FarmData>();
	private TableView<FarmData> table;

	void determineMax() {
		try {
			FarmData max = formatedData.stream().max(Comparator.comparing(FarmData::getWeight)).get();
			minMaxOrAve.setText(max.toString());
		} catch (NoSuchElementException ignored) {

		}

	}

	void determineMin() {
		try {
			FarmData min = formatedData.stream().min(Comparator.comparing(FarmData::getWeight)).get();
			System.out.println(min);
			minMaxOrAve.setText(min.toString());
		} catch (NoSuchElementException ignored) {

		}
	}

	void determineAverage() {

		if (data.size() == 0)
			return;

		double sum = 0;

		for (FarmData farm : formatedData) {
			sum += farm.getWeight();
		}

		minMaxOrAve.setText("Average weight : " + sum / data.size());
	}

	void setPercent(List<FarmData> farmData) {

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

	void farmReport(String farmIdInput, String yearInput) throws IOException {
		table.getColumns().get(0).setVisible(false); // make date not visible
		table.getColumns().get(1).setVisible(true); // make month visible
		table.getColumns().get(2).setVisible(false); // make farmID not visible

		formatedData = data.stream().filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput)
				&& farmData.getFarmID().equals(farmIdInput)).collect(Collectors.toList());

		List<FarmData> weightByMonths = makeListForFarmReport();

		String[] dateParts;

		System.out.println("farmReport size: " + formatedData.size());
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

//	        boolean containes = false;
//	        for (FarmData farm : farmReport) {
//	            for (FarmData farmInTemp : weightByMonths) {
//	                if(farm.getFarmID().equals(farmInTemp.getFarmID())){
//	                    containes = true;
//	                    farmInTemp.addWeight(farm.getWeight());
//	                }
//	            }
//	            if(!containes){
//	                weightByMonths.add(new FarmData(farm.getDate(), farm.getFarmID(), farm.getWeight()));
//	            }
//	            containes = false;
//	        }
		setPercent(weightByMonths);

		table.setItems(FXCollections.observableArrayList(weightByMonths));
		printReport();
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

	void annualReport(String yearInput) {
		table.setVisible(true);
		table.getColumns().get(0).setVisible(false);
		table.getColumns().get(1).setVisible(true);

		formatedData = data.stream().filter(farmData -> farmData.getDate().substring(0, 4).equalsIgnoreCase(yearInput))
				.collect(Collectors.toList());

		data.add(new FarmData("Sion's farm", 4558858));
	}

	void monthlyReport() {

	}

	void dateRangeReport() {

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

}
