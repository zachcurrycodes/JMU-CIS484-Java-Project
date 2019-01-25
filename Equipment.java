package Lab1;

/*
Zachary Curry

On my honor, I have neither given nor received any unauthorized assistance on
this academic work
*/

public class Equipment {
    Integer ID, EquipmentYear, DriverID;
    String VinNumber, Make, Model, LicenseNumber, LastUpdatedBy, LastUpdated;
    Double PriceAcquired;
    
    public Equipment(){
        ID = 0;
        VinNumber = "";
        Make = "";
        Model = "";
        EquipmentYear = 0;
        PriceAcquired = 0.0;
        LicenseNumber = "";
        DriverID = 0;
        LastUpdatedBy = "";
        LastUpdated = "";
    }
    
    //Setter Methods
    public void setID(int i){
        this.ID = i;
    }
    public void setVinNumber(String a){
        this.VinNumber = a;
    }
    public void setMake(String a){
        this.Make = a;
    }
    public void setModel(String a){
        this.Model = a;
    }
    public void setEquipmentYear(int i){
        this.EquipmentYear = i;
    }
    public void setPriceAcuired(double d){
        this.PriceAcquired = d;
    }
    public void setLicenseNumber(String a){
        this.LicenseNumber = a;
    }
    public void setDriverID(int i){
        this.DriverID = i;
    }
    public void setLastUpdatedBy(String a){
        this.LastUpdatedBy = a;
    }
    public void setLastUpdated(String a){
        this.LastUpdated = a;
    }
    
    //Getter Methods
    public Integer getID(){
        return this.ID;
    }
    public String getVinNumber(){
        return this.VinNumber;
    }
    public String getMake(){
        return this.Make;
    }
    public String getModel(){
        return this.Model;
    }
    public Integer getEquipmentYear(){
        return this.EquipmentYear;
    }
    public Double getPriceAcquired(){
        return this.PriceAcquired;
    }
    public String getLicenseNumber(){
        return this.LicenseNumber;
    }
    public Integer getDriverID(){
        return this.DriverID;
    }
    public String getLastUpdatedBy(){
        return this.LastUpdatedBy;
    }
    public String getLastUpdated(){
        return this.LastUpdated;
    }
   
    public void reset(){
        ID = 0;
        VinNumber = "";
        Make = "";
        Model = "";
        EquipmentYear = 0;
        PriceAcquired = 0.0;
        LicenseNumber = "";
        DriverID = 0;
        LastUpdatedBy = "";
        LastUpdated = "";
    }
}
