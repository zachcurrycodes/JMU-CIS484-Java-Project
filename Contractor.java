package Lab1;

/*
Zachary Curry

On my honor, I have neither given nor received any unauthorized assistance on
this academic work
*/

public class Contractor {
    Integer ContractorID, HouseNumber;
    String FirstName, LastName, MiddleInitial, Street, CityCounty, StateAbb,
            CountryAbb, ZipCode, LastUpdatedBy, LastUpdated;
    Double Fee;

    //Constructor
    public Contractor() {
        //Data Fields
        ContractorID = 0;
        FirstName = null;
        LastName = null;
        MiddleInitial = null;
        HouseNumber = null;
        Street = null;
        CityCounty = null;
        StateAbb = null;
        CountryAbb = null;
        ZipCode = null;
        Fee = null;
        LastUpdatedBy = null;
        LastUpdated = null;
    }
    
    //Setter Methods
    public void setContractorID(int i){
        this.ContractorID = i;
    }
    public void setFirstName(String a){
        this.FirstName = a;
    }
    public void setLastName(String a){
        this.LastName = a;
    }
    public void setMiddleInitial(String a){
        this.MiddleInitial = a;
    }
    public void setHouseNumber(int i){
        this.HouseNumber = i;
    }
    public void setStreet(String a){
        this.Street = a;
    }
    public void setCityCounty(String a){
        this.CityCounty = a;
    }
    public void setStateAbb(String a){
        this.StateAbb = a;
    }
    public void setCountryAbb(String a){
        this.CountryAbb = a;
    }
    public void setZipCode(String a){
        this.ZipCode = a;
    }
    public void setFee(Double d){
        this.Fee = d;
    }
    public void setLastUpdatedBy(String a){
        this.LastUpdatedBy = a;
    }
    public void setLastUpdated(String a){
        this.LastUpdated = a;
    }   
    
    //Getter Methods
    public Integer getContratorID(){
        return this.ContractorID;
    }
    public String getFirstName(){
        return this.FirstName;
    }
    public String getLastName(){
        return this.LastName;
    }
    public String getMiddleInitial(){
        return this.MiddleInitial;
    }
    public Integer getHouseNumber(){
        return this.HouseNumber;
    }
    public String getStreet(){
        return this.Street;
    }
    public String getCityCounty(){
        return this.CityCounty;
    }
    public String getStateAbb(){
        return this.StateAbb;
    }
    public String getCountryAbb(){
        return this.CountryAbb;
    }
    public String getZipCode(){
        return this.ZipCode;
    }
    public Double getFee(){ 
        return this.Fee;
    }
    public String getLastUpdatedBy(){
        return this.LastUpdatedBy;
    }
    public String getLastUpdated(){
        return this.LastUpdated;
    }
    
}
