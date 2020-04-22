package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FarmData {
    private final SimpleStringProperty farmID;
    private SimpleIntegerProperty weight;

    public FarmData(String farmID, Integer weight) {
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
    }


    public void setFarmID(String farmID) {
        this.farmID.set(farmID);
    }

    public String getFarmID() {
        return farmID.get();
    }

    public StringProperty getF(){
        return farmID;
    }

    public void setWeight(Integer weight) {
        this.weight.set(weight);
    }

    public Integer getWeight() {
        return weight.get();
    }

//    public String toString() {
//        return getFarmID() + " : " + getWeight();
//    }
}
