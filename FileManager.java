package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FileManager {
	 private static final String COMMA = "\\s*,\\s*";
	 
	 boolean validFile;
	
	
	  /**
     * used to load a file dragged and dropped the the appropriate location
     * @param inputFilePath
     * @return
     */
    List<FarmData> loadFile(String inputFilePath) {
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
              this.validFile = false;
        }
        return inputList;
    }
    
    boolean checkValidity() {
    	return this.validFile;
    }
    
    private Function<String, FarmData> mapToItem  = (line) -> {

        String[] curFarm = line.split(COMMA);// a CSV has comma separated lines
        String[] dateParts = curFarm[0].split("-"); // use to get month which will be at index 1

        return new FarmData(curFarm[0], getMonth(Integer.parseInt(dateParts[1])), curFarm[1], Integer.parseInt(curFarm[2]));
    };
    private String getMonth(int month){
        return new DateFormatSymbols().getMonths()[month-1];
    }

}
