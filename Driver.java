package Lab1;

/*
Zachary Curry

On my honor, I have neither given nor received any unauthorized assistance on
this academic work
*/

public class Driver {
    Integer DriverID, ContractorID, HouseNumber;
    String FirstName, LastName, MiddleInitial,Street, CityCounty, StateAbb,
            CountryAbb, ZipCode, DateOfBirth, CDL, CDLDate, HireDate,
            TerminationDate, LastUpdatedBy, LastUpdated;
    Double Salary;
    
    public Driver(){
        //Data Fields
        DriverID = 0;
        FirstName = null;
        LastName = null;
        MiddleInitial = null;
        Salary = null;
        ContractorID = 0;
        HouseNumber = null;
        Street = null;
        CityCounty = null;
        StateAbb = null;
        CountryAbb = null;
        ZipCode = null;
        DateOfBirth = null;
        CDL = null;
        CDLDate = null;
        HireDate = null;
        TerminationDate = null;
        LastUpdatedBy = null;
        LastUpdated = null;
    }
    
    //Setter Methods
    public void setDriverID(int i){
        this.DriverID = i;
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
    public void setSalary(Double d){
        this.Salary = d;
    }
    public void setContractorID(int i){
        this.ContractorID = i;
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
    public void setDateOfBirth(String a){
        this.DateOfBirth = a;
    }
    public void setCDL(String a){
        this.CDL = a;
    }
    public void setCDLDate(String a){
        this.CDLDate = a;
    }
    public void setHireDate(String a){
        this.HireDate = a;
    }
    public void setTerminationDate(String a){
        this.TerminationDate = a;
    }
    public void setLastUpdatedBy(String a){
        this.LastUpdatedBy = a;
    }
    public void setLastUpdated(String a){
        this.LastUpdated = a;
    }
    
    //Getter Methods
    public Integer getDriverID(){
        return this.DriverID;
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
    public Double getSalary(){
        return this.Salary;
    }
    public Integer getContractorID(){
        return this.ContractorID;
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
    public String getDateOfBirth(){
        return this.DateOfBirth;   
    }
    public String getCDL(){
        return this.CDL;
    }
    public String getCDLDate(){
        return this.CDLDate;
    }
    public String getHireDate(){
        return this.HireDate;
    }
    public String getTerminationDate(){
        return this.TerminationDate;
    }
    public String getLastUpdatedBy(){
        return this.LastUpdatedBy;
    }
    public String getLastUpdated(){
        return this.LastUpdated;
    }
    
    public void reset(){
        DriverID = 0;
        FirstName = null;
        LastName = null;
        MiddleInitial = null;
        Salary = null;
        ContractorID = 0;
        HouseNumber = null;
        Street = null;
        CityCounty = null;
        StateAbb = null;
        CountryAbb = null;
        ZipCode = null;
        DateOfBirth = null;
        CDL = null;
        CDLDate = null;
        HireDate = null;
        TerminationDate = null;
        LastUpdatedBy = null;
        LastUpdated = null;
    }
}
