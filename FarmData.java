package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * public class that is used to represent the data stored in all 4 types of data reports
 */
public class FarmData {
    private SimpleStringProperty month;
    private SimpleStringProperty farmID;
    private SimpleIntegerProperty weight;
    private SimpleStringProperty percent;

    public FarmData(Integer weight, String month) {
        this.month = new SimpleStringProperty(month);
        this.weight = new SimpleIntegerProperty(weight);
    }
    public FarmData(Integer weight, String month, String percent) {
        this.month = new SimpleStringProperty(month);
        this.weight = new SimpleIntegerProperty(weight);
        this.percent = new SimpleStringProperty(percent);
    }

    public FarmData(String farmID, Integer weight) {
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
    }
    public FarmData(String farmID, Integer weight, String percent) {
        this.farmID = new SimpleStringProperty(farmID);
        this.weight = new SimpleIntegerProperty(weight);
        this.percent = new SimpleStringProperty(percent);
    }


    public void setMonth(String month) {
        if (this.month == null) this.month = new SimpleStringProperty(month);
        this.month.set(month);
    }

    public void getMonth() {
        if (this.month == null) this.month = new SimpleStringProperty();
        this.month.get();
    }

    public StringProperty monthProperty() {
        return month;
    }


    // methods for farmID field
    public void setFarmID(String farmID) {
        if(this.farmID == null) this.farmID = new SimpleStringProperty(farmID);
        this.farmID.set(farmID);
    }

    public String getFarmID() {
        if(this.farmID == null) this.farmID = new SimpleStringProperty();
        return farmID.get();
    }

    public SimpleStringProperty farmIDProperty() {
        return farmID;
    }


    // methods for weight field

    /**
     * sets weight will never be null since no every constructor required a weight
     * @param weight
     */
    public void setWeight(Integer weight) {
        this.weight.set(weight);
    }

    public Integer getWeight() {
        return weight.get();
    }

    public SimpleIntegerProperty weightProperty() {
        return weight;
    }


    // methods for percent field


    public void setPercent(String percent) {
        if(this.percent == null) this.percent = new SimpleStringProperty();
        this.percent.set(percent);
    }

    public String getPercent() {
        return percent.get();
    }

    public SimpleStringProperty percentProperty() {
        return percent;
    }

    @Override
    public String toString() {
        String s = "";
        if(farmID != null) s += "FarmID " + farmID.get() + "   ";
        if(month != null) s += "Month " + month.get() + "   ";
        if(weight != null) s += "Weight " + weight.get() + "   ";
        if(percent != null) s += "Percent " + percent.get() + "   ";

        return s;
    }
}
