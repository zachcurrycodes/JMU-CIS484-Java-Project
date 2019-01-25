package Lab1;

/*
Zachary Curry

On my honor, I have neither given nor received any unauthorized assistance on
this academic work
*/

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.stage.Stage;
import oracle.jdbc.pool.*;

public class Lab1 extends Application{
    //Set up connection strings
    Connection dbConn;
    Statement commStmt;
    ResultSet dbResults;
    String sqlQuery;
    
    String jdbcConnectionURL = "jdbc:oracle:thin:@localhost:1521:XE";
    String userID = "javauser";
    String userPASS = "javapass";
    
    //Create Arrays
    Integer arraySize = 3;
    Contractor[] arrayContractor = new Contractor[arraySize];
    String stateList[] = new String[5];
    String countryList[] = new String[5];
    ArrayList<String> driverContractorList = new ArrayList();
    ArrayList<String> equipmentDriverList = new ArrayList();
    
    //Create Observable Lists
    ObservableList<String> olStateList;
    ObservableList<String> olCountryList;
    ObservableList<String> olContractorID;
    ObservableList<String> olDriverID;
    
    //Set Dates and Date Constraints
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentDate = getCurrentTimeStamp();
    Integer currentYear = Integer.parseInt(currentDate.substring(6,10));
    String tooOld;
    String tooYoung;
    String twentyFiveAfterDOB;
    String seventyFiveAfterDOB;
    Integer driverAge;
    
    //Set constant column restraints for all 3 gridPanes
    ColumnConstraints col1 = new ColumnConstraints(120);
    ColumnConstraints col2 = new ColumnConstraints(130);
    ColumnConstraints col3 = new ColumnConstraints(100);
    ColumnConstraints col4 = new ColumnConstraints(130);
    ColumnConstraints col5 = new ColumnConstraints(100);
    ColumnConstraints col6 = new ColumnConstraints(100);
    
    //Create text area on bottom pane
    TextArea taDisplayData = new TextArea();
    
    //Create Textfields for Contractor
    TextField tfContractorID = new TextField();
    TextField tfCFirstName = new TextField();
    TextField tfCMI = new TextField();
    TextField tfCLastName = new TextField();
    TextField tfCHouseNumber = new TextField();
    TextField tfCStreet = new TextField();
    TextField tfCCityCounty = new TextField();
    TextField tfCZipCode = new TextField();
    TextField tfCFee = new TextField();
    TextField tfCUpdatedBy = new TextField();

    //Create Buttons for Contractor
    Button btnCInsert = new Button("Insert");
    Button btnCCommit = new Button("Commit");

    //Create State and Country DropDown boxes
    ComboBox cbCHomeState;
    ComboBox cbCCountry = new ComboBox();
    
    //Create Driver object
    Driver myDriver = new Driver();
    
    //Create Textfields for Driver
    TextField tfDriverID = new TextField();
    TextField tfDFirstName = new TextField();
    TextField tfDMI = new TextField();
    TextField tfDLastName = new TextField();
    TextField tfDSalary = new TextField();
    TextField tfDHouseNumber = new TextField();
    TextField tfDStreet = new TextField();
    TextField tfDCityCounty = new TextField();
    TextField tfDZipCode = new TextField();
    TextField tfDDateOfBirth = new TextField();
    TextField tfDCDL = new TextField();
    TextField tfDCDLDate = new TextField();
    TextField tfDHireDate = new TextField();
    TextField tfDTerminationDate = new TextField();
    TextField tfDUpdatedBy = new TextField();
    
    //Create Button for Driver
    Button btnDCommit = new Button("Commit");

    //Create State and Country DropDown boxes
    ComboBox cbDHomeState;
    ComboBox cbDCountry;
    ComboBox cbDContractorID;
    
    //Create Equipment object
    Equipment myEquipment = new Equipment();
    
    //Create Textfields for Equipment
    TextField tfEquipmentID = new TextField();
    TextField tfEVin = new TextField();
    TextField tfEMake = new TextField();
    TextField tfEModel = new TextField();
    TextField tfEYear = new TextField();
    TextField tfEPriceAcquired = new TextField();
    TextField tfELicenseNumber = new TextField();
    TextField tfEUpdatedBy = new TextField();

    //Create Button for Driver
    Button btnECommit = new Button("Commit");

    //Create Driver Drop Down box
    ComboBox cbEDriverID;
    
    //Create BorderPane to place hbox, vbox, buttons
    BorderPane layout = new BorderPane();
    
    @Override
    public void start(Stage primaryStage){
        //Clear data that is not mine from the database
        sqlQuery = "DELETE FROM DRIVERCONTRACTOR";
        sendDBCommand(sqlQuery);
        sqlQuery = "DELETE FROM EQUIPMENT";
        sendDBCommand(sqlQuery);
        sqlQuery = "DELETE FROM DRIVER";
        sendDBCommand(sqlQuery);
        sqlQuery = "DELETE FROM CONTRACTOR";
        sendDBCommand(sqlQuery);
        //run methods that populate dropdowns on the program
        loadStatesDataFromDB();
        loadCountriesDataFromDB();
        
        //http://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        //got the idea to create the layout from the above link
        
        //Create Date 75 years from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -75);
        tooOld = sdf.format(cal.getTime());
        
        //Create Date 25 years before today
        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -25);
        tooYoung = sdf.format(cal.getTime());
        
        //Create LeftPane buttons
        Button btnContractor = new Button("Contractor");
        btnContractor.setPrefSize(100, 20);
        Button btnDriver = new Button("Driver");
        btnDriver.setPrefSize(100, 20);
        Button btnEquipment = new Button("Equipment");
        btnEquipment.setPrefSize(100, 20);
        Button btnDisplayTotalFees = new Button("Total Fees");
        btnDisplayTotalFees.setPrefWidth(100);
        Button btnDisplayDriverEquipment = new Button("Driver /\n"
                + "Equipment");
        btnDisplayDriverEquipment.setPrefWidth(100);
        
        //Show ToolTip when hovering over buttons
        Tooltip ttc = new Tooltip("Click this button to "
                + "add a Contractor to the database.");
        Tooltip ttd = new Tooltip("Click this button to "
                + "add a Driver to the database.");
        Tooltip tte = new Tooltip("Click this button to "
                + "add a Equipment to the database.");
        
        btnContractor.setTooltip(ttc);
        btnDriver.setTooltip(ttd);
        btnEquipment.setTooltip(tte);
        
        //Create LeftPane and add Buttons
        VBox vbLeftPane = new VBox();
        vbLeftPane.setPadding(new Insets(10));
        vbLeftPane.setSpacing(8);
        vbLeftPane.setStyle("-fx-background-color: #336699;");

        Text titleAdd = new Text("Add");
        titleAdd.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Text spacer = new Text(" ");
        spacer.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        Text titleDisplay = new Text("Display");
        titleDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        vbLeftPane.getChildren().addAll(titleAdd, btnContractor,
                btnDriver, btnEquipment, spacer, titleDisplay,
                btnDisplayTotalFees, btnDisplayDriverEquipment);

        //Create actions for navigation buttons
        btnContractor.setOnAction(e ->{
            layout.setCenter(createCGridPane());
            tfContractorID.requestFocus();
            });
        btnDriver.setOnAction(e ->{
            layout.setCenter(createDGridPane());
            tfDriverID.requestFocus();
            });
        btnEquipment.setOnAction(e ->{
            layout.setCenter(createEGridPane());
            tfEquipmentID.requestFocus();
            });

        //Create actions for buttons on Contractor pane
        btnCInsert.setOnAction(e ->{
            taDisplayData.setText("");
            insertButtonContractor();
        });
        btnCInsert.setOnKeyPressed(e ->{
            taDisplayData.setText("");
            insertButtonContractor();
        });
        btnCCommit.setOnAction(e ->{
            taDisplayData.setText("");
            commitContractor();
            layout.setCenter(createDGridPane());
            tfDriverID.requestFocus();
        });
        btnCCommit.setOnKeyPressed(e ->{
            taDisplayData.setText("");
            commitContractor();
            layout.setCenter(createDGridPane());
            tfDriverID.requestFocus();
        });
        btnDCommit.setOnAction(e ->{
            try {
                commitDriver();
            } catch (ParseException ex) {}
        });
        btnDCommit.setOnKeyPressed(e ->{
            try {
                commitDriver();
            } catch (ParseException ex) {}
        });
        btnECommit.setOnAction(e ->{
            commitEquipment();
        });
        btnECommit.setOnKeyPressed(e ->{
            commitEquipment();
        });
        btnDisplayTotalFees.setOnAction(e ->{
            taDisplayData.setText("");
            displayTotalFees();
        });
        btnDisplayDriverEquipment.setOnAction(e ->{
            taDisplayData.setText("");
            displayDriverEquipment();
        });

        //Place TopHBox, LeftVBox, and gridPane onto the scene
        HBox hbox = addTopHBox();
        layout.setTop(hbox);
        layout.setLeft(vbLeftPane);
        layout.setCenter(createCGridPane());
        layout.setBottom(addBottomHBox());
        Scene scene = new Scene(layout);
        
        //Set Focus in ContractorId Textbox
        tfContractorID.requestFocus();
        
        //Set the title of the window and load the window with the program
        primaryStage.setTitle("Lab 1 - Zachary Curry");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void displayTotalFees(){
        //Declare string and set to null
        String totalFees = null;
            try{
                //Count Contractors in the database
                sqlQuery = "SELECT COUNT(CONTRACTORID) FROM CONTRACTOR";
                sendDBCommand(sqlQuery);
                //If atleast one contractor in database then continue
                while (dbResults.next()){
                    if (Integer.parseInt(dbResults.getString(1)) > 0){
                        try{                        
                            //Get the sum of all commited Contractor's fees
                            sqlQuery = "SELECT SUM(FEE) FROM CONTRACTOR";
                            sendDBCommand(sqlQuery);
                            while (dbResults.next()){
                                //Display Total Fees and format to Dollars
                                totalFees = "Total amount of Fees for all committed Contractors\n"
                                + " -------------------------------------------\n"
                                + "$" + String.format("%.2f",
                                        Double.parseDouble(dbResults.getString(1)));
                            }
                            //Show String in bottom field
                            taDisplayData.setText(totalFees);
                        }catch (NullPointerException npe){
                            //if no fees enter for any contractor, display
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setHeaderText("No Contractor Fees entered into Database");
                            alert.setContentText("Please commit atleast one Contractor Fee to the Database");
                            alert.showAndWait();
                            layout.setCenter(createCGridPane());
                            tfCFee.requestFocus();
                            break;
                        }
                    }
                    else{
                        //if not contractors in database, display
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("No Contractors in Database");
                        alert.setContentText("Please commit atleast one Contractor to the Database");
                        alert.showAndWait();
                        layout.setCenter(createCGridPane());
                        tfContractorID.requestFocus();
                    }
                }
            }catch (SQLException e) {}
    }
    
    public void displayDriverEquipment(){
        //select data to show Equipment for each driver
        sqlQuery = "SELECT DRIVER.DRIVERID, FIRSTNAME, MIDDLEINITIAL, LASTNAME,\n"
                + "ID, VINNUMBER, MAKE, MODEL, EQUIPMENTYEAR, PRICEACQUIRED, LICENSENUMBER\n"
                + "FROM DRIVER\n"
                + "LEFT OUTER JOIN EQUIPMENT\n"
                + "ON DRIVER.DRIVERID = EQUIPMENT.DRIVERID\n"
                + "ORDER BY DRIVER.DRIVERID";
        sendDBCommand(sqlQuery);
        //set default result if database returns null
        String displayDE = "No Drivers or Equipment in the database";
        try{
            //if database has result, begin populating String
            if (dbResults.first()){
                displayDE = "";
                dbResults.beforeFirst();
            }
            while (dbResults.next()){
                //populate string from database
                displayDE += dbResults.getString(1) + "\t";
                displayDE += dbResults.getString(2) + " ";
                if (dbResults.getString(3) != null){
                    displayDE += dbResults.getString(3) + " ";}
                displayDE += dbResults.getString(4) + "\t";
                //if no equipment for driver, skip, otherwise contine
                if (dbResults.getString(5) != null){
                    displayDE += "Equipment Id: " + dbResults.getString(5) + "\t"; //id
                    displayDE += "Vin: " + dbResults.getString(6) + "\t"; //vinNumber
                    if (dbResults.getString(7) != null)
                        displayDE += "Make: " + dbResults.getString(7) + "\t"; //Make
                    if (dbResults.getString(8) != null)
                        displayDE += "Model: " + dbResults.getString(8) + "\t"; //Model
                    if (dbResults.getString(9) != null)
                        displayDE += "Year: " + dbResults.getString(9) + "\t"; //equipmentYear
                    if (dbResults.getString(10) != null)
                        displayDE += "Price Acquired: " + dbResults.getString(10) + "\t"; //priceAcquired
                    if (dbResults.getString(11) != null)
                        displayDE += "License Plate: " + dbResults.getString(11) + "\n"; //licenseNumber
                }
                else {
                    displayDE += "\n";
                }
            }
            //display string predetermined
            taDisplayData.setText(displayDE);
        }catch (SQLException e) {}
    }
    
    public HBox addTopHBox(){
        HBox hbTopPane = new HBox();
        hbTopPane.setPadding(new Insets(5, 12, 5, 12));
        hbTopPane.setSpacing(10);
        hbTopPane.setStyle("-fx-background-color: #0E3E6C;");
        
        //Add directions to the program
        String Directions = "Created by Zachary Curry\n"
                + "1. Please add Contractor before adding either a "
                + "Driver or any Equipment \n"
                + "2. Please add Driver before adding a piece of Equipment\n"
                + "3. Dates should be entered as a \"DD/MM/YYYY\" format\n"
                + "4. Please enter dollar amounts without the \"$\" in \"Fee\", \"Salary\", and \"Price Acquired\"\n"
                + "5. Write your name in the Updated By box at the bottom right before Committing information\n"
                + "6. Insert adds a Contractor to the array, max Contractors allowed at one time is 3\n"
                + "7. Commit enters the values (from array) into the database (and clears the array)\n"
                + "8. BOLD Lables indicate where information is required to commit to the database";
        Text txtDirections = new Text(Directions);
        txtDirections.setFill(Color.WHITE);
        
        hbTopPane.getChildren().add(txtDirections);

        return hbTopPane;
        }
    
    public HBox addBottomHBox(){
        HBox hbBottomPane = new HBox();
        hbBottomPane.setPadding(new Insets(15, 12, 15, 12));
        hbBottomPane.setSpacing(10);
        hbBottomPane.setStyle("-fx-background-color: #0E3E6C;");
        
        taDisplayData.setPrefSize(843, 75);
        taDisplayData.setEditable(false);
        
        //add readonly text area to bottom pane
        hbBottomPane.getChildren().addAll(taDisplayData);
        
        return hbBottomPane;
        }
    
    public GridPane createCGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(7); gridPane.setVgap(7);
        gridPane.getColumnConstraints().addAll(col1,col2,col3,col4,col5,col6);
        
        //Set row constraints
        for (int i = 0; i < 10; i++) {
         RowConstraints row = new RowConstraints(25);
         gridPane.getRowConstraints().add(row);
            }
        
        //Create Contrator Labels
        Label lblContractor = new Label("Contractor");
        lblContractor.setFont(Font.font("Courier", 16));
        Label lblContractorID = new Label("Contractor ID:");
        lblContractorID.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblCFirstName = new Label("First Name:");
        lblCFirstName.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblCMI = new Label ("MI:");
        Label lblCLastName = new Label ("Last Name:");
        lblCLastName.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblCHouseNumber = new Label ("House Number:");
        Label lblCStreet = new Label ("Street:");
        Label lblCCityCounty = new Label ("City or County:");
        Label lblCState = new Label ("State:");
        Label lblCZipCode = new Label ("Zip Code:");
        Label lblCCountry = new Label ("Country:");
        Label lblCFee = new Label ("Fee: $");
        Label lblCUpdatedBy = new Label ("Updated By:");
        lblCUpdatedBy.setFont(Font.font("Courier", FontWeight.BOLD, 12));

        //Position Label Text Right Alignment
        GridPane.setHalignment(lblContractorID, HPos.RIGHT);
        GridPane.setHalignment(lblCFirstName, HPos.RIGHT);
        GridPane.setHalignment(lblCMI, HPos.RIGHT);
        GridPane.setHalignment(lblCLastName, HPos.RIGHT);
        GridPane.setHalignment(lblCHouseNumber, HPos.RIGHT);
        GridPane.setHalignment(lblCStreet, HPos.RIGHT);
        GridPane.setHalignment(lblCCityCounty, HPos.RIGHT);
        GridPane.setHalignment(lblCState, HPos.RIGHT);
        GridPane.setHalignment(lblCZipCode, HPos.RIGHT);
        GridPane.setHalignment(lblCCountry, HPos.RIGHT);
        GridPane.setHalignment(lblCFee, HPos.RIGHT);
        GridPane.setHalignment(lblCUpdatedBy, HPos.RIGHT);
        
        //Add Labels to gridPane
        gridPane.add(lblContractor, 0, 0);
        gridPane.add(lblContractorID, 0, 1);
        gridPane.add(lblCFirstName, 0, 2);
        gridPane.add(lblCMI, 0, 3);
        gridPane.add(lblCLastName, 0, 4);
        gridPane.add(lblCFee, 0, 6);
        gridPane.add(lblCHouseNumber, 2, 2);
        gridPane.add(lblCStreet, 2, 3);
        gridPane.add(lblCCityCounty, 2, 4);
        gridPane.add(lblCState, 2, 5);
        gridPane.add(lblCZipCode, 2, 6);
        gridPane.add(lblCCountry, 2, 7);
        gridPane.add(lblCUpdatedBy, 4, 10);
        
        //Add Textfields and DropDowns to gridPane
        gridPane.add(tfContractorID, 1, 1);
        gridPane.add(tfCFirstName, 1, 2);
        gridPane.add(tfCMI, 1, 3);
        gridPane.add(tfCLastName, 1, 4);
        gridPane.add(tfCFee, 1, 6);
        gridPane.add(tfCHouseNumber, 3, 2);
        gridPane.add(tfCStreet, 3, 3);
        gridPane.add(tfCCityCounty, 3, 4);
        gridPane.add(cbCHomeState = new ComboBox(olStateList), 3, 5);
        gridPane.add(tfCZipCode, 3, 6);
        gridPane.add(cbCCountry = new ComboBox(olCountryList), 3, 7);
        gridPane.add(tfCUpdatedBy, 5, 10);
        
        //Add Buttons to gridPane
        btnCInsert.setPrefSize(100, 20);
        btnCCommit.setPrefSize(100, 20);
        gridPane.add(btnCInsert, 4, 0);
        gridPane.add(btnCCommit, 5, 0);
        
        return gridPane;
    }
    
    public GridPane createDGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(7); gridPane.setVgap(7);
        gridPane.getColumnConstraints().addAll(col1,col2,col3,col4,col5,col6);
        loadContractorIDFromDB();
        
        //set row size on pane
        for (int i = 0; i < 10; i++) {
            RowConstraints row = new RowConstraints(25);
            gridPane.getRowConstraints().add(row);
        }
        
        //Create Driver Labels
        Label lblDriver = new Label("Driver");
        lblDriver.setFont(Font.font("Arial", 16));
        Label lblDriverID = new Label("Driver ID:");
        lblDriverID.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblDFirstName = new Label("First Name:");
        lblDFirstName.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblDMI = new Label ("MI:");
        Label lblDLastName = new Label ("Last Name:");
        lblDLastName.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblDSalary = new Label ("Salary: $");
        Label lblDContractorID = new Label ("Contractor:");
        lblDContractorID.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblDHouseNumber = new Label ("House Number:");
        Label lblDStreet = new Label ("Street:");
        Label lblDCityCounty = new Label ("City or County:");
        Label lblDState = new Label ("State:");
        Label lblDZipCode = new Label ("Zip Code:");
        Label lblDCountry = new Label ("Country:");
        Label lblDOB = new Label ("Date of Birth:");
        lblDOB.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblCDL = new Label ("CDL ID:");
        Label lblCDLDate = new Label ("CDL Experation Date:");
        Label lblDHireDate = new Label ("Hire Date:");
        Label lblDTerminationDate = new Label ("Termination Date:");
        Label lblDUpdatedBy = new Label ("Updated By:");
        lblDUpdatedBy.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        
        //Position Label Text Right Alignment
        GridPane.setHalignment(lblDriverID, HPos.RIGHT);
        GridPane.setHalignment(lblDFirstName, HPos.RIGHT);
        GridPane.setHalignment(lblDMI, HPos.RIGHT);
        GridPane.setHalignment(lblDLastName, HPos.RIGHT);
        GridPane.setHalignment(lblDSalary, HPos.RIGHT);
        GridPane.setHalignment(lblDOB, HPos.RIGHT);
        GridPane.setHalignment(lblCDL, HPos.RIGHT);
        GridPane.setHalignment(lblCDLDate, HPos.RIGHT);
        GridPane.setHalignment(lblDContractorID, HPos.RIGHT);
        GridPane.setHalignment(lblDHouseNumber, HPos.RIGHT);
        GridPane.setHalignment(lblDStreet, HPos.RIGHT);
        GridPane.setHalignment(lblDCityCounty, HPos.RIGHT);
        GridPane.setHalignment(lblDState, HPos.RIGHT);
        GridPane.setHalignment(lblDZipCode, HPos.RIGHT);
        GridPane.setHalignment(lblDCountry, HPos.RIGHT);
        GridPane.setHalignment(lblDHireDate, HPos.RIGHT);
        GridPane.setHalignment(lblDTerminationDate, HPos.RIGHT);
        GridPane.setHalignment(lblDUpdatedBy, HPos.RIGHT);

        //Add Labels to gridPane
        gridPane.add(lblDriver, 0, 0);
        gridPane.add(lblDriverID, 0, 1);
        gridPane.add(lblDFirstName, 0, 2);
        gridPane.add(lblDMI, 0, 3);
        gridPane.add(lblDLastName, 0, 4);
        gridPane.add(lblDSalary, 0, 6);
        gridPane.add(lblDOB, 0, 7);
        gridPane.add(lblCDL, 0, 8);
        gridPane.add(lblCDLDate, 0, 9);
        gridPane.add(lblDContractorID, 2, 1);
        gridPane.add(lblDHouseNumber, 2, 2);
        gridPane.add(lblDStreet, 2, 3);
        gridPane.add(lblDCityCounty, 2, 4);
        gridPane.add(lblDState, 2, 5);
        gridPane.add(lblDZipCode, 2, 6);
        gridPane.add(lblDCountry, 2, 7);
        gridPane.add(lblDHireDate, 4, 2);
        gridPane.add(lblDTerminationDate, 4, 3);
        gridPane.add(lblDUpdatedBy, 4, 10);
        
        //Add TextFields and Dropdown boxes to gridPane
        gridPane.add(tfDriverID, 1, 1);
        gridPane.add(tfDFirstName, 1, 2);
        gridPane.add(tfDMI, 1, 3);
        gridPane.add(tfDLastName, 1, 4);
        gridPane.add(tfDSalary, 1, 6);
        gridPane.add(tfDDateOfBirth, 1, 7);
        gridPane.add(tfDCDL, 1, 8);
        gridPane.add(tfDCDLDate, 1, 9);
        gridPane.add(cbDContractorID = new ComboBox(olContractorID), 3, 1);
        GridPane.setColumnSpan(cbDContractorID, 2);
        cbDContractorID.setPrefWidth(200);
        gridPane.add(tfDHouseNumber, 3, 2);
        gridPane.add(tfDStreet, 3, 3);
        gridPane.add(tfDCityCounty, 3, 4);
        gridPane.add(cbDHomeState = new ComboBox(olStateList), 3, 5);
        gridPane.add(tfDZipCode, 3, 6);
        gridPane.add(cbDCountry = new ComboBox(olCountryList), 3, 7);
        gridPane.add(tfDHireDate, 5, 2);
        gridPane.add(tfDTerminationDate, 5, 3);
        gridPane.add(tfDUpdatedBy, 5, 10);
        
        //Add Button to gridPane
        btnDCommit.setPrefSize(100, 20);
        gridPane.add(btnDCommit, 5, 0);
        
        return gridPane;
    }
    
    public GridPane createEGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(7); gridPane.setVgap(7);
        gridPane.getColumnConstraints().addAll(col1,col2,col3,col4,col5,col6);
        loadDriverIDFromDB();
        
        //set row constraints
        for (int i = 0; i < 10; i++) {
         RowConstraints row = new RowConstraints(25);
         gridPane.getRowConstraints().add(row);
            }
        
        //Create Equipment Labels
        Label lblEquipment = new Label("Equipment");
        lblEquipment.setFont(Font.font("Arial", 16));
        Label lblEquipmentID = new Label("Equipment ID:");
        lblEquipmentID.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblVin = new Label ("Vin:");
        lblVin.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblMake = new Label ("Make:");
        Label lblModel = new Label ("Model:");
        Label lblYear = new Label ("Year:");
        Label lblPriceAcquired = new Label ("Price Acquired: $");
        Label lblLicenseNumber = new Label ("License Plate:");
        Label lblEDriverID = new Label ("Driver:");
        lblEDriverID.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        Label lblEUpdatedBy = new Label ("Updated By:");
        lblEUpdatedBy.setFont(Font.font("Courier", FontWeight.BOLD, 12));
        
        //Position Label Text Right Alignment
        GridPane.setHalignment(lblEquipmentID, HPos.RIGHT);
        GridPane.setHalignment(lblVin, HPos.RIGHT);
        GridPane.setHalignment(lblMake, HPos.RIGHT);
        GridPane.setHalignment(lblModel, HPos.RIGHT);
        GridPane.setHalignment(lblYear, HPos.RIGHT);
        GridPane.setHalignment(lblPriceAcquired, HPos.RIGHT);
        GridPane.setHalignment(lblLicenseNumber, HPos.RIGHT);
        GridPane.setHalignment(lblEDriverID, HPos.RIGHT);
        GridPane.setHalignment(lblEUpdatedBy, HPos.RIGHT);
        
        //Add Labels to gridPane
        gridPane.add(lblEquipment, 0, 0);
        gridPane.add(lblEquipmentID, 0, 1);
        gridPane.add(lblVin, 0, 2);
        gridPane.add(lblMake, 0, 3);
        gridPane.add(lblModel, 0, 4);
        gridPane.add(lblEDriverID, 2, 1);
        gridPane.add(lblYear, 2, 2);
        gridPane.add(lblPriceAcquired, 2, 3);
        gridPane.add(lblLicenseNumber, 2, 4);
        gridPane.add(lblEUpdatedBy, 4, 10);
        
        //Add TextFields and Dropdown box to gridPane
        gridPane.add(tfEquipmentID, 1, 1);
        gridPane.add(tfEVin, 1, 2);
        gridPane.add(tfEMake, 1, 3);
        gridPane.add(tfEModel, 1, 4);
        gridPane.add(cbEDriverID = new ComboBox(olDriverID), 3, 1);
        GridPane.setColumnSpan(cbEDriverID, 2);
        cbEDriverID.setPrefWidth(200);
        gridPane.add(tfEYear, 3, 2);
        gridPane.add(tfEPriceAcquired, 3, 3);
        gridPane.add(tfELicenseNumber, 3, 4);
        gridPane.add(tfEUpdatedBy, 5, 10);
        
        //Add Button to gridPane
        btnECommit.setPrefSize(100, 20);
        gridPane.add(btnECommit, 5, 0);
        
        return gridPane;
    }
    
    public void insertContractorValuesIntoDB(int i){
        //Set up Contractor Array Values into SQL language
        //in order to insert into the Contractor Table in the Database
        sqlQuery = "INSERT INTO JAVAUSER.CONTRACTOR VALUES(";
        sqlQuery += "'" + arrayContractor[i].getContratorID().toString() + "', ";
        sqlQuery += "'" + arrayContractor[i].getFirstName() + "', ";
        sqlQuery += "'" + arrayContractor[i].getLastName() + "', ";
        if ("NULL".equals(arrayContractor[i].getMiddleInitial()))
            sqlQuery += arrayContractor[i].getMiddleInitial()+ ", ";
        else sqlQuery += "'" + arrayContractor[i].getMiddleInitial() + "', ";
        if (arrayContractor[i].HouseNumber == null)
            sqlQuery += "NULL, ";
        else sqlQuery += "'" + arrayContractor[i].getHouseNumber().toString() + "', ";
        if ("NULL".equals(arrayContractor[i].getStreet()))
            sqlQuery += arrayContractor[i].getStreet() + ", ";
        else sqlQuery += "'" + arrayContractor[i].getStreet()+ "', ";
        if ("NULL".equals(arrayContractor[i].getCityCounty()))
            sqlQuery += arrayContractor[i].getCityCounty()+ ", ";
        else sqlQuery += "'" + arrayContractor[i].getCityCounty() + "', ";
        if ("NULL".equals(arrayContractor[i].getStateAbb()))
            sqlQuery += arrayContractor[i].getStateAbb()+ ", ";
        else sqlQuery += "'" + arrayContractor[i].getStateAbb() + "', ";
        if ("NULL".equals(arrayContractor[i].getCountryAbb()))
            sqlQuery += arrayContractor[i].getCountryAbb()+ ", ";
        else sqlQuery += "'" + arrayContractor[i].getCountryAbb() + "', ";
        if ("NULL".equals(arrayContractor[i].getZipCode()))
            sqlQuery += arrayContractor[i].getZipCode() + ", ";
        else sqlQuery += "'" + arrayContractor[i].getZipCode() + "', ";
        if (arrayContractor[i].Fee == null)
            sqlQuery += "NULL, ";
        else sqlQuery += "'" + arrayContractor[i].getFee().toString() + "', ";
        sqlQuery += "'" + arrayContractor[i].getLastUpdatedBy() + "', ";
        sqlQuery += "TO_DATE('" + arrayContractor[i].getLastUpdated() + "', 'MM/DD/YYYY'))";
        sendDBCommand(sqlQuery);
        //reset contractor array
        arrayContractor[i] = null;
    }
    
    public void insertDriverValuesIntoDB(){
        //Format a SQL Statement to insert Driver Values
        sqlQuery = "INSERT INTO JAVAUSER.DRIVER VALUES(";
        sqlQuery += "'" + myDriver.getDriverID().toString() + "', ";
        sqlQuery += "'" + myDriver.getFirstName() + "', ";
        sqlQuery += "'" + myDriver.getLastName() + "', ";
        if ("NULL".equals(myDriver.getMiddleInitial()))
            sqlQuery += myDriver.getMiddleInitial() + ", ";
        else sqlQuery += "'" + myDriver.getMiddleInitial() + "', ";
        if (myDriver.HouseNumber == null)
            sqlQuery += "NULL, ";
        else sqlQuery += "'" + myDriver.getHouseNumber().toString()+ "', ";
        if ("NULL".equals(myDriver.getStreet()))
            sqlQuery += myDriver.getStreet() + ", ";
        else sqlQuery += "'" + myDriver.getStreet() + "', ";
        if ("NULL".equals(myDriver.getCityCounty()))
            sqlQuery += myDriver.getCityCounty() + ", ";
        else sqlQuery += "'" + myDriver.getCityCounty() + "', ";
        if ("NULL".equals(myDriver.getStateAbb()))
            sqlQuery += myDriver.getStateAbb() + ", ";
        else sqlQuery += "'" + myDriver.getStateAbb() + "', ";
        if ("NULL".equals(myDriver.getCountryAbb()))
            sqlQuery += myDriver.getCountryAbb() + ", ";
        else sqlQuery += "'" + myDriver.getCountryAbb() + "', ";
        if ("NULL".equals(myDriver.getZipCode()))
            sqlQuery += myDriver.getZipCode() + ", ";
        else sqlQuery += "'" + myDriver.getZipCode() + "', ";
        if ("NULL".equals(myDriver.getDateOfBirth()))
            sqlQuery += "NULL, ";
        else sqlQuery += "TO_DATE('" + myDriver.getDateOfBirth() + "', 'MM/DD/YYYY'), ";
        if ("NULL".equals(myDriver.getCDL()))
            sqlQuery += myDriver.getCDL() + ", ";
        else sqlQuery += "'" + myDriver.getCDL() + "', ";
        if ("NULL".equals(myDriver.getCDLDate()))
            sqlQuery += "NULL, ";
        else sqlQuery += "TO_DATE('" + myDriver.getCDLDate() + "', 'MM/DD/YYYY'), ";
        sqlQuery += "'" + myDriver.getLastUpdatedBy() + "', ";
        sqlQuery += "TO_DATE('" + myDriver.getLastUpdated() + "', 'MM/DD/YYYY'))";
        sendDBCommand(sqlQuery);
        
        //Format a SQL Statement to insert DriverContractor Values
        sqlQuery = "INSERT INTO JAVAUSER.DRIVERCONTRACTOR VALUES(";
        sqlQuery += "'" + myDriver.getContractorID().toString() + "', ";
        sqlQuery += "'" + myDriver.getDriverID().toString() + "', ";
        if ("NULL".equals(myDriver.getHireDate()))
            sqlQuery += "NULL, ";
        else sqlQuery += "TO_DATE('" + myDriver.getHireDate() + "', 'MM/DD/YYYY'), ";
        if ("NULL".equals(myDriver.getTerminationDate()))
            sqlQuery += "NULL, ";
        else sqlQuery += "TO_DATE('" + myDriver.getTerminationDate() + "', 'MM/DD/YYYY'), ";
        if (myDriver.HouseNumber == null)
            sqlQuery += "NULL, ";
        else sqlQuery += myDriver.getSalary().toString() + ", ";
        sqlQuery += "'" + myDriver.getLastUpdatedBy() + "', ";
        sqlQuery += "TO_DATE('" + myDriver.getLastUpdated() + "', 'MM/DD/YYYY'))";
        sendDBCommand(sqlQuery);
        //reset myDriver object
        myDriver.reset();
    }
    
    public void insertEquipmentValuesIntoDB(){
        //Format a SQL Statement to insert Equipment Values
        sqlQuery = "INSERT INTO JAVAUSER.EQUIPMENT VALUES(";
        sqlQuery += "'" + myEquipment.getID().toString() + "', ";
        sqlQuery += "'" + myEquipment.getVinNumber() + "', ";
        if ("NULL".equals(myEquipment.getMake()))
            sqlQuery += myEquipment.getMake() + ", ";
        else sqlQuery += "'" + myEquipment.getMake() + "', ";
        if ("NULL".equals(myEquipment.getModel()))
            sqlQuery += myEquipment.getModel() + ", ";
        else sqlQuery += "'" + myEquipment.getModel() + "', ";
        if (myEquipment.getEquipmentYear() == null)
            sqlQuery += "NULL, ";
        else sqlQuery += "'" + myEquipment.getEquipmentYear().toString()+ "', ";
        if (myEquipment.getPriceAcquired() == null)
            sqlQuery += "NULL, ";
        else sqlQuery += myEquipment.getPriceAcquired().toString() + ", ";
        if ("NULL".equals(myEquipment.getLicenseNumber()))
            sqlQuery += myEquipment.getLicenseNumber() + ", ";
        else sqlQuery += "'" + myEquipment.getLicenseNumber()+ "', ";
        sqlQuery += "'" + myEquipment.getDriverID().toString()+ "', ";
        sqlQuery += "'" + myEquipment.getLastUpdatedBy() + "', ";
        sqlQuery += "TO_DATE('" + myEquipment.getLastUpdated() + "', 'MM/DD/YYYY'))";
        sendDBCommand(sqlQuery);
        //reset myDriver object
        myDriver.reset();
    }
    
    public void loadStatesDataFromDB(){
        //Populate states into dropdowns
        try{
            Integer i = 0;
            sqlQuery = "SELECT STATEABB FROM JAVAUSER.HOMESTATE";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){                    
                    stateList[i] = dbResults.getString(1);
                    i++;
                    }
            olStateList = FXCollections.observableArrayList(stateList);
        }catch (SQLException e) {
        }
    }
    
    public void loadCountriesDataFromDB(){
        //populate countries into dropdowns
        try{
            Integer i = 0;
            sqlQuery = "SELECT COUNTRYABB FROM JAVAUSER.COUNTRY";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){                    
                    countryList[i] = dbResults.getString(1);
                    i++;
                    }
            olCountryList = FXCollections.observableArrayList(countryList);
        }catch (SQLException e) {
        }
    }
    
    public void loadContractorIDFromDB(){
        //populate contractors into contractor dropdown
        try{
            sqlQuery = "SELECT CONTRACTORID, FIRSTNAME, LASTNAME, MIDDLEINITIAL "
                        + "FROM CONTRACTOR";
            sendDBCommand(sqlQuery);
            driverContractorList.clear();
            while (dbResults.next()){
                String nameConcatenated = null;
                nameConcatenated = dbResults.getString(1);
                nameConcatenated += " - " + dbResults.getString(2);
                if (dbResults.getString(4) != null)
                    nameConcatenated += " " + dbResults.getString(4);
                nameConcatenated += " " + dbResults.getString(3);
                driverContractorList.add(nameConcatenated);
            }
            olContractorID = FXCollections.observableArrayList(driverContractorList);
        }catch (SQLException e) {}
    }
    
    public void loadDriverIDFromDB(){
        //populate drivers on driver dropdown
        try{
                sqlQuery = "SELECT DRIVERID, FIRSTNAME, LASTNAME, MIDDLEINITIAL "
                            + "FROM DRIVER";
                    sendDBCommand(sqlQuery);
                    equipmentDriverList.clear();
                    while (dbResults.next()){
                        String nameConcatenated = null;
                        nameConcatenated = dbResults.getString(1);
                        nameConcatenated += " - " + dbResults.getString(2);
                        if (dbResults.getString(4) != null)
                            nameConcatenated += " " + dbResults.getString(4);
                        nameConcatenated += " " + dbResults.getString(3);
                        equipmentDriverList.add(nameConcatenated);
                    }
            olDriverID = FXCollections.observableArrayList(equipmentDriverList);
        }catch (SQLException e) {}
    }
    
    public void commitContractor(){
        //Check first location in contractor array
        if (arrayContractor[0] == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor Array Empty");
            alert.setContentText("Please insert a contractor before Committing");
            alert.showAndWait();
            tfContractorID.requestFocus();
        }
        else{
            //Delete current Equipment Table
            sqlQuery = "DELETE FROM EQUIPMENT";
            sendDBCommand(sqlQuery);
            //Delete current DriverContractor Table
            sqlQuery = "DELETE FROM DRIVERCONTRACTOR";
            sendDBCommand(sqlQuery);
            //Delete current Driver Table
            sqlQuery = "DELETE FROM DRIVER";
            sendDBCommand(sqlQuery);
            //Delete current Contractor Table
            sqlQuery = "DELETE FROM CONTRACTOR";
            sendDBCommand(sqlQuery);
            //For every object in Contractor array, add to Contractor Table in database
            for (int i=0; i<arrayContractor.length;i++){
                if (arrayContractor[i] != null){
                    try{
                       insertContractorValuesIntoDB(i);
                    }catch (NullPointerException npe){}
                }
            }
            driverContractorList.clear();
            equipmentDriverList.clear();
        }
    }
    
    public void insertButtonContractor(){
        //Check to see if ContractorID Field Empty
        if ("".equals(tfContractorID.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor ID can't be empty");
            alert.setContentText("Please enter a Contractor ID");
            alert.showAndWait();
            tfContractorID.requestFocus();
        }
        //Check to see if ContractorID fits into an integer constraint
        else if (tfContractorID.getText().length() > 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor ID Too Long");
            alert.setContentText("Only 10 characters allowed for a Contractor ID");
            alert.showAndWait();
            tfContractorID.requestFocus();
        }
        //Check to see if ContractorID is a number; acknowledge the max integer constraint
        else if (isInteger(tfContractorID.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor ID must be an integer");
            alert.setContentText("Please enter an number for Contractor ID\n"
                    + "\n"
                    + "Contractor ID must be less than 2,147,483,648");
            alert.showAndWait();
            tfContractorID.requestFocus();
        }
        //Check to see if ContractorID is a positive number
        else if (Integer.parseInt(tfContractorID.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor ID Negative");
            alert.setContentText("Contractor ID must be a positive number");
            alert.showAndWait();
            tfContractorID.requestFocus();
        }
        //Check to see if FirstName is left blank
        else if("".equals(tfCFirstName.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No First Name Specified");
                alert.setContentText("First Name can't be left blank");
                alert.showAndWait();
                tfCFirstName.requestFocus();
        }
        //Check to see if FirstName length is less than 20
        else if (tfCFirstName.getText().length() > 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("First Name Too Long");
            alert.setContentText("Only 20 characters allowed for a First Name");
            alert.showAndWait();
            tfCFirstName.requestFocus();
        }
        //Check to see if MI is only 1 character
        else if (tfCMI.getText().length() > 1){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Middle Initial Too Long");
            alert.setContentText("Only 1 character allowed for a Middle Initial");
            alert.showAndWait();
            tfCMI.requestFocus();
        }
        //Check to see if LastName is left blank
        else if("".equals(tfCLastName.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Last Name Specified");
                alert.setContentText("Last Name can't be left blank");
                alert.showAndWait();
                tfCLastName.requestFocus();
        }
        //Check to see if LastName is less than 20
        else if (tfCLastName.getText().length() > 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Last Name Too Long");
            alert.setContentText("Only 20 characters allowed for a Last Name");
            alert.showAndWait();
            tfCLastName.requestFocus();
        }
        //Check to see if Fee is a double IF the field is not empty
        else if(isDouble(tfCFee.getText()) == false && !"".equals(tfCFee.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fee must be a double");
            alert.setContentText("Please enter a numeric amount for Fee");
            alert.showAndWait();
            tfCFee.requestFocus();
        }
        //Check to see if Fee is a positive number
        else if(isDouble(tfCFee.getText()) == true &&
                !"".equals(tfCFee.getText()) &&
                Double.parseDouble(tfCFee.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fee must be a positive value");
            alert.setContentText("Please enter a positive value for Fee");
            alert.showAndWait();
            tfCFee.requestFocus();
        }
        //Check to see if Fee field contains a decimal point AND IF SO
        //...check to make sure it only contains 2 decimal places or less
        else if (tfCFee.getText().contains(".") == true &&
                    tfCFee.getText().substring(tfCFee.getText().indexOf("."),
                    tfCFee.getText().length()).length() > 3){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fee has too many decimal places");
            alert.setContentText("Please reduce amount of decimal places for the Fee");
            alert.showAndWait();
            tfCFee.requestFocus();
        }
        //Check to see if Fee falls within Decimal (8,2) constraints
        else if (tfCFee.getText().contains(".") == true &&
                    tfCFee.getText().substring(1,
                    tfCFee.getText().indexOf(".")).length() > 6){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fee Too Long");
            alert.setContentText("Fee must be less than $1,000,000.00");
            alert.showAndWait();
        }
        //Check to see if Fee is less than $1,000,000.00
        else if (tfCFee.getText().contains(".") == false &&
                    tfCFee.getText().length() > 6){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fee Too Long");
            alert.setContentText("Fee must be less than $1,000,000.00");
            alert.showAndWait();
            tfCFee.requestFocus();
        }
        //Check to see if House Number fits into an integer constraint
        else if (tfCHouseNumber.getText().length() > 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("House Number Too Long");
            alert.setContentText("Only 10 characters allowed for a House Number");
            alert.showAndWait();
            tfCHouseNumber.requestFocus();
        }
        //Check to see if House number is a number; acknowledge the max integer constraint
        else if(isInteger(tfCHouseNumber.getText()) == false &&
                !"".equals(tfCHouseNumber.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("House Number must be an integer");
            alert.setContentText("Please enter a number for House Number\n"
                    + "\n"
                    + "House Number must be less than 2,147,483,648");
            alert.showAndWait();
            tfCHouseNumber.requestFocus();
        }
        //Check to see if Street is less than 50 characters
        else if (tfCStreet.getText().length() > 50){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Street Too Long");
            alert.setContentText("Only 50 characters allowed for a Street");
            alert.showAndWait();
            tfCStreet.requestFocus();
        }
        //Check to see if City or County is less than 40 characters
        else if (tfCCityCounty.getText().length() > 40){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("City / County Too Long");
            alert.setContentText("Only 40 characters allowed for a City or County");
            alert.showAndWait();
            tfCCityCounty.requestFocus();
        }
        //Check to see if ZipCode is greater than 5 characters
        else if (tfCZipCode.getText().length() > 5){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code Too Long");
            alert.setContentText("Only 5 characters allowed for a Zip Code");
            alert.showAndWait();
            tfCZipCode.requestFocus();
        }
        //Check to see if ZipCode is less than 5 characters
        else if (!"".equals(tfCZipCode.getText()) &&
                tfCZipCode.getText().length() < 5){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code Too Short");
            alert.setContentText("Zip Code must have 5 characters");
            alert.showAndWait();
            tfCZipCode.requestFocus();
        }
        //Check to see if ZipCode is a number IF the field is not empty
        else if(isInteger(tfCZipCode.getText()) == false && !"".equals(tfCZipCode.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code must be an integer");
            alert.setContentText("Please enter a number for Zip Code");
            alert.showAndWait();
            tfCZipCode.requestFocus();
        }
        //Check to see if UpdatedBy is null
        else if("".equals(tfCUpdatedBy.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Ownership to Update Specified");
            alert.setContentText("Please write your name in the \"Updated By\" box");
            alert.showAndWait();
            tfCUpdatedBy.requestFocus();
        }
        //Check to see if UpdatedBy is less than 20 characters
        else if (tfCUpdatedBy.getText().length()> 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Updated By Too Long");
            alert.setContentText("Only 20 characters allowed for an Updated By Ownership");
            alert.showAndWait();
            tfCUpdatedBy.requestFocus();
        }
        //Check to see if Contractor array last location is empty
        else if (arrayContractor[arraySize-1] != null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Contractor Array is Full!");
            alert.setContentText("You must commit the Contractor Array \n"
                    + "before adding a new Contractor!");
            alert.showAndWait();
            btnCCommit.requestFocus();
        }
        //Check to see if the Contractor array is empty or has atleast one value
        else if (arrayContractor[0] != null){
            for (int i=0; i<arrayContractor.length; i++){
                //Check to see if current i location in Contractor Array is empty
                if (arrayContractor[i] != null){
                    if (checkForSameContractorID()){
                        //if same id, display
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Contractor ID unique");
                        alert.setContentText("Please use a different Contractor ID");
                        alert.showAndWait();
                        tfContractorID.requestFocus();
                        break;
                    }
                    else if (checkForSameContractorName()){
                        //if same exact name, display
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Contractor Name must be unique");
                        alert.setContentText("Contractor Name already exists:\n"
                            + "Please enter a drifferent Contractor Name");
                        alert.showAndWait();
                        tfCFirstName.requestFocus();
                        break;
                    }
                    else{
                        assignContractorValues();
                        tfContractorID.requestFocus();
                    }
                    break;
                }
            }
        }
        else{
            assignContractorValues();
            tfContractorID.requestFocus();
        }
    }
    
    public boolean checkForSameContractorID(){
        //check each location in contractor array for exact same integer
        for (int i=0;i<arrayContractor.length-1;i++){
            if(arrayContractor[i] != null)
                if(arrayContractor[i].ContractorID == Integer.parseInt(tfContractorID.getText()))
                    return true;
        }
        return false;
    }
    
    public boolean checkForSameContractorName(){
        String arrayNameConcatenated = null;
        String tfNameConcatenated = null;
        //concatenate names then compare to find exact match
        for (int i=0;i<arrayContractor.length-1;i++){
            if (arrayContractor[i] != null){
                arrayNameConcatenated = arrayContractor[i].getFirstName().toUpperCase();
                if (!"NULL".equals(arrayContractor[i].getMiddleInitial()))
                    arrayNameConcatenated += " " + arrayContractor[i].getMiddleInitial().toUpperCase();
                arrayNameConcatenated += " " + arrayContractor[i].getLastName().toUpperCase();

                tfNameConcatenated = tfCFirstName.getText().toUpperCase();
                if (!"".equals(tfCMI.getText()))
                    tfNameConcatenated += " " + tfCMI.getText().toUpperCase();
                tfNameConcatenated += " " + tfCLastName.getText().toUpperCase();
                if (arrayNameConcatenated.equals(tfNameConcatenated))
                    return true;
            }
        }
        return false;
    }
    
    public void assignContractorValues(){
        //Start at first free location in Contractor Array
        for (int i=0; i<arrayContractor.length; i++){
            try{
                //Check to see if location is free, if not move to next location in array
                if (arrayContractor[i] == null){
                    //Create Contractor object in Contractor Array
                    arrayContractor[i] = new Contractor();
                arrayContractor[i].setContractorID(Integer.parseInt(tfContractorID.getText()));
                //Assign FirstName TextField to Contractor Array
                arrayContractor[i].setFirstName(tfCFirstName.getText());
                //Assign MiddleInitial TextField to Contractor Array
                if ("".equals(tfCMI.getText()))
                    arrayContractor[i].setMiddleInitial("NULL");
                else arrayContractor[i].setMiddleInitial(tfCMI.getText());
                //Assign LastName TextField to Contractor Array
                arrayContractor[i].setLastName(tfCLastName.getText());                    
                //Assign HouseNumber TextField to Contractor Array
                if ("".equals(tfCHouseNumber.getText()))
                    arrayContractor[i].HouseNumber = null;
                else arrayContractor[i].setHouseNumber(Integer.parseInt(tfCHouseNumber.getText()));
                //Assign Street TextField to Contractor Array
                if ("".equals(tfCStreet.getText()))
                    arrayContractor[i].setStreet("NULL");
                else arrayContractor[i].setStreet(tfCStreet.getText());
                //Assign CityCounty TextField to Contractor Array
                if ("".equals(tfCCityCounty.getText()))
                    arrayContractor[i].setCityCounty("NULL");
                else arrayContractor[i].setCityCounty(tfCCityCounty.getText());
                //Assign StateDropDown to Contractor Array
                try{
                    arrayContractor[i].setStateAbb(cbCHomeState.getValue().toString());
                }catch (NullPointerException npe){
                    arrayContractor[i].StateAbb = "NULL";
                }
                //Assign ZipCode TextField to Contractor Array
                if ("".equals(tfCZipCode.getText()))
                    arrayContractor[i].setZipCode("NULL");
                else arrayContractor[i].setZipCode(tfCZipCode.getText());
                //Assign CountryDropDown to Contractor Array
                try{
                    arrayContractor[i].setCountryAbb(cbCCountry.getValue().toString());
                }catch (NullPointerException npe){
                    arrayContractor[i].setCountryAbb("NULL");
                }
                //Assign Fee TextField to Contractor Array
                if ("".equals(tfCFee.getText()))
                    arrayContractor[i].Fee = null;
                else arrayContractor[i].setFee(Double.parseDouble(tfCFee.getText()));
                //Assign UpdatedBy TextField to Contractor Array
                if ("".equals(tfCUpdatedBy.getText()))
                    arrayContractor[i].setLastUpdatedBy("NULL");
                else arrayContractor[i].setLastUpdatedBy(tfCUpdatedBy.getText());
                arrayContractor[i].setLastUpdated(getCurrentTimeStamp());
                //Exit if loop when empty array qualifications are met
                break;
                }
            }catch (NumberFormatException e) {}
        }
    }
    
    public boolean checkDateOfBirth(){
        //Date of Birth validations
        if (isDateValid(tfDDateOfBirth.getText()) == false &&
                    !"".equals(tfDDateOfBirth.getText())){
            return true;
        }
        else if (tfDDateOfBirth.getText().length() < 10 &&
                !"".equals(tfDDateOfBirth.getText())){
            return true;
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(0,2)) == false){
            return true;
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(3,5)) == false){
            return true;
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(6,10)) == false){
            return true;
        }
        return false;
    }
    
    public void commitDriver() throws ParseException{
        if ("".equals(tfDDateOfBirth.getText())){}
        else if (checkDateOfBirth() == false){
            //Populate the date 25 years past DOB
            Date dateDOB = sdf.parse(tfDDateOfBirth.getText());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateDOB);
            cal.add(Calendar.YEAR, 25);
            Date dateTwentyFiveAfterDOB = cal.getTime();
            twentyFiveAfterDOB = sdf.format(dateTwentyFiveAfterDOB);
            //Populate the date 75 years past DOB
            cal.setTime(dateDOB);
            cal.add(Calendar.YEAR, 75);
            cal.add(Calendar.DATE, -1);
            Date dateSeventyFiveAfterDOB = cal.getTime();
            seventyFiveAfterDOB = sdf.format(dateSeventyFiveAfterDOB);
            //find drivers age
            cal.setTime(sdf.parse(getCurrentTimeStamp()));
            Integer dateDOBYear = Integer.parseInt(tfDDateOfBirth.getText().substring(6, 10));
            Integer dateDOBMonth = Integer.parseInt(tfDDateOfBirth.getText().substring(0, 2));
            Integer dateDOBDay = Integer.parseInt(tfDDateOfBirth.getText().substring(3, 5));
            cal.add(Calendar.YEAR, -dateDOBYear);
            cal.add(Calendar.MONTH, -dateDOBMonth);
            cal.add(Calendar.DATE, -dateDOBDay);
            
            Date dateAge = cal.getTime();
            String strAge = sdf.format(dateAge);
            String lessAge = strAge.substring(6, 10);
            driverAge = Integer.parseInt(lessAge);
        }

        //Check to see if DriverID is empty
        if ("".equals(tfDriverID.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver ID can't be empty");
            alert.setContentText("Please enter a Driver ID");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Check to see if DriverID has already been used
        else if(checkForSameDriverID()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver ID must be unique");
            alert.setContentText("Please enter a drifferent Driver ID");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Check to see if DriverName is unique
        else if(checkForSameDriverName()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Name must be unique");
            alert.setContentText("Driver Name already exists");
            alert.showAndWait();
            tfDFirstName.requestFocus();
        }
        //Check to see if DriverID fits into an integer constraint
        else if (tfDriverID.getText().length()> 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver ID Too Long");
            alert.setContentText("Only 10 characters allowed for a Driver ID");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Check to see if DriverID is a number;
        //acknowledge the max integer constraint
        else if (isInteger(tfDriverID.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver ID must be an integer");
            alert.setContentText("Please enter an number for Driver ID\n"
                    + "\n"
                    + "Contractor ID must be less than 2,147,483,648");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Check to see if DriverID is a positive number
        else if (Integer.parseInt(tfDriverID.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver ID Negative");
            alert.setContentText("Driver ID must be a positive number");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Check to see if FirstName is left blank
        else if("".equals(tfDFirstName.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No First Name Specified");
                alert.setContentText("First Name can't be left blank");
                alert.showAndWait();
                tfDFirstName.requestFocus();
        }
        //Check to see if FirstName length is less than 20
        else if (tfDFirstName.getText().length()> 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("First Name Too Long");
            alert.setContentText("Only 20 characters allowed for a First Name");
            alert.showAndWait();
            tfDFirstName.requestFocus();
        }
        //Check to see if MI is only 1 character
        else if (tfDMI.getText().length()> 1){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Middle Initial Too Long");
            alert.setContentText("Only 1 character allowed for a Middle Initial");
            alert.showAndWait();
            tfDMI.requestFocus();
        }
        //Check to see if LastName is left blank
        else if("".equals(tfDLastName.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Last Name Specified");
                alert.setContentText("Last Name can't be left blank");
                alert.showAndWait();
                tfDLastName.requestFocus();
        }
        //Check to see if LastName is less than 20
        else if (tfDLastName.getText().length()> 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Last Name Too Long");
            alert.setContentText("Only 20 characters allowed for a Last Name");
            alert.showAndWait();
            tfDLastName.requestFocus();
        }
        //Check to see if Salary is a double IF the field is not empty
        else if(isDouble(tfDSalary.getText()) == false &&
                !"".equals(tfDSalary.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Salary must be a double");
            alert.setContentText("Please enter a numeric amount for Salary");
            alert.showAndWait();
            tfDSalary.requestFocus();
        }
        //Check to see if Salary is a positive number
        else if(isDouble(tfDSalary.getText()) == true &&
                !"".equals(tfDSalary.getText()) &&
                Double.parseDouble(tfDSalary.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Salary must be a positive value");
            alert.setContentText("Please enter a positive value for Salary");
            alert.showAndWait();
            tfDSalary.requestFocus();
        }
        //Check to see if Salary field contains a decimal point AND IF SO
        //...check to make sure it only contains 2 decimal places or less
        else if (tfDSalary.getText().contains(".") == true &&
                    tfDSalary.getText().substring(tfDSalary.getText().indexOf("."),
                    tfDSalary.getText().length()).length() > 3){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Salary has too many decimal places");
            alert.setContentText("Please reduce amount of decimal places for the Salary");
            alert.showAndWait();
            tfDSalary.requestFocus();
        }
        //Check to see if Salary falls within Decimal (10,2) constraints
        else if (tfDSalary.getText().contains(".") == true &&
                    tfDSalary.getText().substring(1,
                    tfDSalary.getText().indexOf(".")).length() > 8){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Salary Too Long");
            alert.setContentText("Salary must be less than $100,000,000.00");
            alert.showAndWait();
            tfDSalary.requestFocus();
        }
        //Check to see if Salary is less than $10,000,000,000.00
        else if (tfDSalary.getText().contains(".") == false &&
                    tfDSalary.getText().length() > 8){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Salary Too Long");
            alert.setContentText("Salary must be less than $100,000,000.00");
            alert.showAndWait();
            tfDSalary.requestFocus();
        }
        //Check to see if DOB is empty
        else if ("".equals(tfDDateOfBirth.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth can't be empty");
            alert.setContentText("Please enter a Date of Birth");
            alert.showAndWait();
            tfDriverID.requestFocus();
        }
        //Date of Birth validations
        else if (isDateValid(tfDDateOfBirth.getText()) == false &&
                    !"".equals(tfDDateOfBirth.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Not a Valid Date of Birth");
            alert.setContentText("Not a Real Date\n"
                    + "Please write a Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        else if (tfDDateOfBirth.getText().length() < 10 &&
                !"".equals(tfDDateOfBirth.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth Too Short");
            alert.setContentText("Please write Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        else if (tfDDateOfBirth.getText().length() > 10 &&
                !"".equals(tfDDateOfBirth.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth Too Long");
            alert.setContentText("Please write Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(0,2)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth written incorrectly");
            alert.setContentText("Please write Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(3,5)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth written incorrectly");
            alert.setContentText("Please write Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                isInteger(tfDDateOfBirth.getText().substring(6,10)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Date of Birth written incorrectly");
            alert.setContentText("Please write Date of Birth in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        //Check to see if Driver is atleast 25 years old
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                sdf.parse(tfDDateOfBirth.getText()).after(
                sdf.parse(tooYoung))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver is Too Young!");
            alert.setContentText("Driver must be at least 25 years old");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        //Check to see if Driver is younger than 75 years old
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                sdf.parse(tfDDateOfBirth.getText()).before(
                sdf.parse(tooOld))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver is Too Old!");
            alert.setContentText("Driver must be younger than 75 years old");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        //Check to see if CDL is less than 40 characters
        else if (tfDCDL.getText().length()> 40){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL ID Too Long");
            alert.setContentText("Only 40 characters allowed for a CDL ID");
            alert.showAndWait();
            tfDCDL.requestFocus();
        }
        //CDL Experation Date Validation
        else if (isDateValid(tfDCDLDate.getText()) == false &&
                    !"".equals(tfDCDLDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Not a Valid CDL Experation Date");
            alert.setContentText("Not a Real Date\n"
                    + "Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        else if (tfDCDLDate.getText().length() < 10 &&
                !"".equals(tfDCDLDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL Experation Date Too Short");
            alert.setContentText("Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        else if (tfDCDLDate.getText().length() > 10 &&
                !"".equals(tfDCDLDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL Experation Date Too Long");
            alert.setContentText("Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        else if (!"".equals(tfDCDLDate.getText()) &&
                isInteger(tfDCDLDate.getText().substring(0,2)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL Experation Date written incorrectly");
            alert.setContentText("Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        else if (!"".equals(tfDCDLDate.getText()) &&
                isInteger(tfDCDLDate.getText().substring(3,5)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL Experation Date written incorrectly");
            alert.setContentText("Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        else if (!"".equals(tfDCDLDate.getText()) &&
                isInteger(tfDCDLDate.getText().substring(6,10)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL Experation Date written incorrectly");
            alert.setContentText("Please write CDL Experation Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        //Make sure CDL is not expired
        else if (!"".equals(tfDCDLDate.getText()) &&
                sdf.parse(tfDCDLDate.getText()).before(
                sdf.parse(currentDate))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("CDL is Expired");
            alert.setContentText("Driver must have a Non-Expired CDL");
            alert.showAndWait();
            tfDCDLDate.requestFocus();
        }
        //Check to see if Contractor is left blank
        else if(cbDContractorID.getValue() == null){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Contractor Specified");
                alert.setContentText("Contractor can't be left blank");
                alert.showAndWait();
                cbDContractorID.requestFocus();
        }
        //Check to see if House Number fits into an integer constraint
        else if (tfDHouseNumber.getText().length()> 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("House Number Too Long");
            alert.setContentText("Only 10 characters allowed for a House Number");
            alert.showAndWait();
            tfDHouseNumber.requestFocus();
        }
        //Check to see if House number is a number;
        //acknowledge the max integer constraint
        else if(isInteger(tfDHouseNumber.getText()) == false &&
                !"".equals(tfDHouseNumber.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("House Number must be an integer");
            alert.setContentText("Please enter a number for House Number\n"
                    + "\n"
                    + "House Number must be less than 2,147,483,648");
            alert.showAndWait();
            tfDHouseNumber.requestFocus();
        }
        //Check to see if Street is less than 50 characters
        else if (tfDStreet.getText().length()> 50){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Street Too Long");
            alert.setContentText("Only 50 characters allowed for a Street");
            alert.showAndWait();
            tfDStreet.requestFocus();
        }
        //Check to see if City or County is less than 40 characters
        else if (tfDCityCounty.getText().length()> 40){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("City / County Too Long");
            alert.setContentText("Only 40 characters allowed for a City or County");
            alert.showAndWait();
            tfDCityCounty.requestFocus();
        }
        //Check to see if ZipCode is more than 5 characters
        else if (tfDZipCode.getText().length()> 5){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code Too Long");
            alert.setContentText("Only 5 characters allowed for a Zip Code");
            alert.showAndWait();
            tfDZipCode.requestFocus();
        }
        //Check to see if ZipCode is less than 5 characters
        else if (!"".equals(tfDZipCode.getText()) &&
                tfDZipCode.getText().length() < 5){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code Too Short");
            alert.setContentText("Zip Code must have 5 characters");
            alert.showAndWait();
            tfDZipCode.requestFocus();
        }
        //Check to see if ZipCode is a number IF the field is not empty
        else if(isInteger(tfDZipCode.getText()) == false &&
                !"".equals(tfDZipCode.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Zip Code must be an integer");
            alert.setContentText("Please enter a number for Zip Code");
            alert.showAndWait();
            tfDZipCode.requestFocus();
        }
        //Hire Date Validation
        else if (isDateValid(tfDHireDate.getText()) == false &&
                    !"".equals(tfDHireDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Not a Valid Hire Date");
            alert.setContentText("Not a Real Date\n"
                    + "Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        else if (tfDHireDate.getText().length() < 10 &&
                !"".equals(tfDHireDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Hire Date Too Short");
            alert.setContentText("Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        else if (tfDHireDate.getText().length() > 10 &&
                !"".equals(tfDHireDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Hire Date Too Long");
            alert.setContentText("Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        else if (!"".equals(tfDHireDate.getText()) &&
                isInteger(tfDHireDate.getText().substring(0,2)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Hire Date written incorrectly");
            alert.setContentText("Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        else if (!"".equals(tfDHireDate.getText()) &&
                isInteger(tfDHireDate.getText().substring(3,5)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Hire Date written incorrectly");
            alert.setContentText("Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
        }
        else if (!"".equals(tfDHireDate.getText()) &&
                isInteger(tfDHireDate.getText().substring(6,10)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Hire Date written incorrectly");
            alert.setContentText("Please write Hire Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        //Check to make sure DateOfBirth is provided if HireDate is provided
        else if ("".equals(tfDDateOfBirth.getText()) &&
                !"".equals(tfDHireDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver missing Date of Birth");
            alert.setContentText("Driver can only be hired after turning 25 years old\n"
                    + "Please adjust Date of Birth");
            alert.showAndWait();
            tfDDateOfBirth.requestFocus();
        }
        //Check to make sure HireDate is after 25 years from DateOfBirth
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                !"".equals(tfDHireDate.getText()) &&
                sdf.parse(tfDHireDate.getText()).before(
                sdf.parse(twentyFiveAfterDOB))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver hired before 25 years old");
            alert.setContentText("Driver can only be hired after turning 25 years old\n"
                    + "Please adjust Hire Date");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        //Check to make sure HireDate is before 75 years from DateOfBirth
        else if (!"".equals(tfDDateOfBirth.getText()) &&
                !"".equals(tfDHireDate.getText()) &&
                sdf.parse(tfDHireDate.getText()).after(
                sdf.parse(seventyFiveAfterDOB))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver hired after 75 years old");
            alert.setContentText("Driver can only be hired before turning 75 years old\n"
                    + "Please adjust Hire Date");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        //Check to see if Driver is Hired after today
        /*
        else if (!"".equals(tfDHireDate.getText()) &&
                sdf.parse(tfDHireDate.getText()).after(
                sdf.parse(currentDate))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Hired in the Future");
            alert.setContentText("Driver can only be Hired today or before\n"
                    + "Please adjust Termination Date");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        */
        //Termination Date Validation
        else if ("".equals(tfDHireDate.getText()) &&
                !"".equals(tfDTerminationDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Hire Date Written");
            alert.setContentText("Please provide a Hire Date if providing a Termination Date");
            alert.showAndWait();
            tfDHireDate.requestFocus();
        }
        else if (isDateValid(tfDTerminationDate.getText()) == false &&
                !"".equals(tfDTerminationDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Not a Valid Termination Date");
            alert.setContentText("Not a Real Date\n"
                    + "Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        else if (tfDTerminationDate.getText().length() < 10 &&
                !"".equals(tfDTerminationDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Termination Date Too Short");
            alert.setContentText("Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        else if (tfDTerminationDate.getText().length() > 10 &&
                !"".equals(tfDTerminationDate.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Termination Date Too Long");
            alert.setContentText("Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        else if (!"".equals(tfDTerminationDate.getText()) &&
                isInteger(tfDTerminationDate.getText().substring(0,2)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Termination Date written incorrectly");
            alert.setContentText("Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        else if (!"".equals(tfDTerminationDate.getText()) &&
                isInteger(tfDTerminationDate.getText().substring(3,5)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Termination Date written incorrectly");
            alert.setContentText("Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        else if (!"".equals(tfDTerminationDate.getText()) &&
                isInteger(tfDTerminationDate.getText().substring(6,10)) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Termination Date written incorrectly");
            alert.setContentText("Please write Termination Date in a MM/DD/YYYY Format");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        //Check to see if Driver is Terminated after Hire Date
        else if (!"".equals(tfDHireDate.getText()) &&
                !"".equals(tfDTerminationDate.getText()) &&
                sdf.parse(tfDHireDate.getText()).after(
                sdf.parse(tfDTerminationDate.getText()))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Terminated Before Hire Date");
            alert.setContentText("Driver can only be Terminated after being Hired\n"
                    + "Please adjust Termination Date");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        //Check to see if Driver is Terminated before turning 75
        else if (!"".equals(tfDTerminationDate.getText()) &&
                sdf.parse(tfDTerminationDate.getText()).after(
                sdf.parse(seventyFiveAfterDOB))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Terminated after turning 75");
            alert.setContentText("Max age allowed for employed Drivers is 74\n"
                    + "Driver must be terminated before their 75th birthday\n"
                    + "\n"
                    + "Please adjust Termination Date");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        //Check to see if Driver is Terminated before today
        /*
        else if (!"".equals(tfDTerminationDate.getText()) &&
                sdf.parse(tfDTerminationDate.getText()).after(
                sdf.parse(currentDate))){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Terminated in the Future");
            alert.setContentText("Driver can only be Terminated today or before\n"
                    + "Please adjust Termination Date");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        */
        //Check to see if Driver is Terminated after hire date
        else if (!"".equals(tfDTerminationDate.getText()) &&
                sdf.parse(tfDTerminationDate.getText()).after(
                sdf.parse(tfDHireDate.getText())) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Driver Terminated before employment");
            alert.setContentText("Driver must be employed for atleast one day\n"
                    + "Please adjust Termination Date");
            alert.showAndWait();
            tfDTerminationDate.requestFocus();
        }
        //Check to see if UpdatedBy is null
        else if("".equals(tfDUpdatedBy.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Ownership to Update Specified");
                alert.setContentText("Please write your name in the \"Updated By\" box");
                alert.showAndWait();
                tfDUpdatedBy.requestFocus();
        }
        //Check to see if UpdatedBy is less than 20 characters
        else if (tfDUpdatedBy.getText().length()> 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Updated By Too Long");
            alert.setContentText("Only 20 characters allowed for an Updated By Ownership");
            alert.showAndWait();
            tfDUpdatedBy.requestFocus();
        }
        else{
            assignDriverValues();
            insertDriverValuesIntoDB();
            tfDriverID.requestFocus();
            equipmentDriverList.clear();
            //populate bottom textarea with current driver's age
            String displayDriverAge = tfDFirstName.getText();
            if (tfDMI.getText() != null)
                displayDriverAge += " " + tfDMI.getText();
            displayDriverAge += " " + tfDLastName.getText() + "'s ";
            if ("".equals(tfDDateOfBirth.getText()))
                taDisplayData.setText(displayDriverAge + "Age: Not Specified");
            else taDisplayData.setText(displayDriverAge + "Age: " + driverAge);
        }
    }
    
    public boolean checkForSameDriverID(){
        //check database for exact match of driverid
        try{
            sqlQuery = "SELECT DRIVERID FROM DRIVER";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){
                String placeholderDriverID = null;
                placeholderDriverID = dbResults.getString(1);
                if (placeholderDriverID.equals(tfDriverID.getText())){
                    return true;
                }
            }
        }catch (SQLException e) {}
        return false;
    }
    
    public boolean checkForSameDriverName(){
        //check database for exact match of concatenated name
        try{
            sqlQuery = "SELECT FIRSTNAME, LASTNAME, MIDDLEINITIAL FROM DRIVER";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){
                String dbNameConcatenated = null;
                String tfNameConcatenated = null;
                
                dbNameConcatenated = dbResults.getString(1).toUpperCase();
                if (dbResults.getString(3) != null)
                    dbNameConcatenated += " " + dbResults.getString(3).toUpperCase();
                dbNameConcatenated += " " + dbResults.getString(2).toUpperCase();

                tfNameConcatenated = tfDFirstName.getText().toUpperCase();
                if (!"".equals(tfDMI.getText()))
                    tfNameConcatenated += " " + tfDMI.getText().toUpperCase();
                tfNameConcatenated += " " + tfDLastName.getText().toUpperCase();

                if (dbNameConcatenated.equals(tfNameConcatenated))
                    return true;
            }
        }catch (SQLException e) {}
        return false;
    }
    
    public void assignDriverValues(){
        //Assign DriverID to myDriver
        myDriver.setDriverID(Integer.parseInt(tfDriverID.getText()));
        //Assign FirstName to myDriver
        myDriver.setFirstName(tfDFirstName.getText());
        //Assign MiddleInitial to myDriver
        if ("".equals(tfDMI.getText()))
            myDriver.setMiddleInitial("NULL");
        else myDriver.setMiddleInitial(tfDMI.getText());
        //Assign LastName to myDriver
        myDriver.setLastName(tfDLastName.getText());
        //Assign Salary to myDriver
        if ("".equals(tfDSalary.getText()))
            myDriver.Salary = null;
        else myDriver.setSalary(Double.parseDouble(tfDSalary.getText()));
        //Assign DateOfBirth to myDriver
        if ("".equals(tfDDateOfBirth.getText()))
            myDriver.setDateOfBirth("NULL");
        else myDriver.setDateOfBirth(tfDDateOfBirth.getText());
        //Assign CDL ID to myDriver
        if ("".equals(tfDCDL.getText()))
            myDriver.setCDL("NULL");
        else myDriver.setCDL(tfDCDL.getText());
        //Assign CDL Experation Date to myDriver
        if ("".equals(tfDCDLDate.getText()))
            myDriver.setCDLDate("NULL");
        else myDriver.setCDLDate(tfDCDLDate.getText());
        //Assign ContractorID to myDriver
        myDriver.setContractorID(Integer.parseInt(
            cbDContractorID.getValue().toString().substring(0,
            cbDContractorID.getValue().toString().indexOf(" "))));
        //Assign HouseNumber to myDriver
        if ("".equals(tfDHouseNumber.getText()))
            myDriver.HouseNumber = null;
        else myDriver.setHouseNumber(Integer.parseInt(tfDHouseNumber.getText()));
        //Assign Street to myDriver
        if ("".equals(tfDStreet.getText()))
            myDriver.setStreet("NULL");
        else myDriver.setStreet(tfDStreet.getText());
        //Assing CityCountry to myDriver
        if ("".equals(tfDCityCounty.getText()))
            myDriver.setCityCounty("NULL");
        else myDriver.setCityCounty(tfDCityCounty.getText());
        //Assign StateDropDown to myDriver
        try{
            myDriver.setStateAbb(cbDHomeState.getValue().toString());
        }catch (NullPointerException npe){
            myDriver.StateAbb = "NULL";
        }
        //Assign ZipCode to myDriver
        if ("".equals(tfDZipCode.getText()))
            myDriver.setZipCode("NULL");
        else myDriver.setZipCode(tfDZipCode.getText());
        //assign CountryDropDown to myDriver
        try{
            myDriver.setCountryAbb(cbDCountry.getValue().toString());
        }catch (NullPointerException npe){
            myDriver.CountryAbb = "NULL";
        }
        //Assign HireDate to myDriver
        if ("".equals(tfDHireDate.getText()))
            myDriver.setHireDate("NULL");
        else myDriver.setHireDate(tfDHireDate.getText());
        //Assign TerminationDate to myDriver
        if ("".equals(tfDTerminationDate.getText()))
            myDriver.setTerminationDate("NULL");
        else myDriver.setTerminationDate(tfDTerminationDate.getText());
        //Assign UpdatedBy to myDriver
        if ("".equals(tfDUpdatedBy.getText()))
            myDriver.setLastUpdatedBy("NULL");
        else myDriver.setLastUpdatedBy(tfDUpdatedBy.getText());
        //Assign LastUpdated to myDriver
        myDriver.setLastUpdated(getCurrentTimeStamp());
    }
    
    public void commitEquipment(){
        //Check to see if EquipmentID is empty
        if ("".equals(tfEquipmentID.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment ID can't be empty");
            alert.setContentText("Please enter an Equipment ID");
            alert.showAndWait();
            tfEquipmentID.requestFocus();
        }
        //Check to see if DriverID has already been used
        else if(checkForSameEquipmentID()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment ID must be unique");
            alert.setContentText("Please enter a drifferent Equipment ID");
            alert.showAndWait();
            tfEquipmentID.requestFocus();
        }
        //Check to see if DriverName is unique
        else if(checkForSameEquipmentVin()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Vin Number must be unique");
            alert.setContentText("Please enter a drifferent Vin Number");
            alert.showAndWait();
            tfEVin.requestFocus();
        }
        //Check to see if EquipmentID fits into an integer constraint
        else if (tfEquipmentID.getText().length()> 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment ID Too Long");
            alert.setContentText("Only 10 characters allowed for a Equipment ID");
            alert.showAndWait();
            tfEquipmentID.requestFocus();
        }
        //Check to see if EquipmentID is a number;
        //acknowledge the max integer constraint
        else if (isInteger(tfEquipmentID.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment ID must be an integer");
            alert.setContentText("Please enter an number for Equipment ID\n"
                    + "\n"
                    + "Contractor ID must be less than 2,147,483,648");
            alert.showAndWait();
            tfEquipmentID.requestFocus();
        }
        //Check to see if EquipmentID is a positive number
        else if (Integer.parseInt(tfEquipmentID.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment ID Negative");
            alert.setContentText("Equipment ID must be a positive number");
            alert.showAndWait();
            tfEquipmentID.requestFocus();
        }
        //Check to see if EquipmentID is empty
        else if ("".equals(tfEVin.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Vin Number can't be empty");
            alert.setContentText("Please enter an Vin Number");
            alert.showAndWait();
            tfEVin.requestFocus();
        }
        //Check to see if VinNumber length is less than 20 characters
        else if (tfEVin.getText().length()> 40){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Vin Number Too Long");
            alert.setContentText("Only 40 characters allowed for a Vin Number");
            alert.showAndWait();
            tfEVin.requestFocus();
        }
        //Check to see if Make length is less than 35 characters
        else if (tfEMake.getText().length()> 35){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Make Too Long");
            alert.setContentText("Only 35 characters allowed for a Make of Equipment");
            alert.showAndWait();
            tfEMake.requestFocus();
        }
        //Check to see if Model length is less than 30 characters
        else if (tfEModel.getText().length()> 30){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Model Too Long");
            alert.setContentText("Only 30 characters allowed for a Make of Equipment");
            alert.showAndWait();
            tfEModel.requestFocus();
        }
        //Check to see if Driver is left blank
        else if(cbEDriverID.getValue() == null){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Drivier Specified");
                alert.setContentText("Driver can't be left blank");
                alert.showAndWait();
                cbEDriverID.requestFocus();
        }
        //Check to see if Equipment Year is more than 4 characters
        else if (tfEYear.getText().length()> 4){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Year Too Long");
            alert.setContentText("Only 4 characters allowed for a Year");
            alert.showAndWait();
            tfEYear.requestFocus();
        }
        //Check to see if Equipment Year is a number
        else if(!"".equals(tfEYear.getText()) &&
                isInteger(tfEYear.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Equipment Year must be an integer");
            alert.setContentText("Please enter a number for Equipment Year");
            alert.showAndWait();
            tfEYear.requestFocus();
        }
        //Check to see if Equipment Year is a Valid Year
        else if(!"".equals(tfEYear.getText()) &&
                tfEYear.getText().length() < 4){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Year Too Short");
            alert.setContentText("Please enter a Year in a \"YYYY\" Format");
            alert.showAndWait();
            tfEYear.requestFocus();
        }
        //Check to see if Equipment Year is from the past and not newer than the
        //present Year+1 ...Adding 1 to Year because I assume Equipment is released
        //a year "early" just like cars (ex. in year 2017, 2018 equipment is possible
        else if(!"".equals(tfEYear.getText()) &&
                Integer.parseInt(tfEYear.getText()) > currentYear + 1){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("This Model has not been released yet!");
            alert.setContentText("Please enter a Year older than " + (currentYear +1));
            alert.showAndWait();
            tfEYear.requestFocus();
        }
        //Check to see if PriceAcquired is a double IF the field is not empty
        else if(!"".equals(tfEPriceAcquired.getText()) &&
                isDouble(tfEPriceAcquired.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Price Acquired must be a double");
            alert.setContentText("Please enter a numeric amount for Price Acquired");
            alert.showAndWait();
            tfEPriceAcquired.requestFocus();
        }
        //Check to see if PriceAcquired is a positive number
        else if(isDouble(tfEPriceAcquired.getText()) == true &&
                !"".equals(tfEPriceAcquired.getText()) &&
                Double.parseDouble(tfEPriceAcquired.getText()) < 0){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Price Acquired must be a positive value");
            alert.setContentText("Please enter a positive value for Price Acquired");
            alert.showAndWait();
            tfEPriceAcquired.requestFocus();
        }
        //Check to see if PriceAcquired field contains a decimal point AND IF SO
        //...check to make sure it only contains 2 decimal places or less
        else if (tfEPriceAcquired.getText().contains(".") == true &&
                    tfEPriceAcquired.getText().substring(tfEPriceAcquired.getText().indexOf("."),
                    tfEPriceAcquired.getText().length()).length() > 3){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Price Acquired has too many decimal places");
            alert.setContentText("Please reduce amount of decimal places for the Price Acquired");
            alert.showAndWait();
            tfEPriceAcquired.requestFocus();
        }
        //Check to see if PriceAcquired falls within Decimal (8 ,2) constraints
        else if (tfEPriceAcquired.getText().contains(".") == true &&
                    tfEPriceAcquired.getText().substring(1,
                    tfEPriceAcquired.getText().indexOf(".")).length() > 6){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Price Acquired Too Long");
            alert.setContentText("Price Acquired must be less than $1,000,000.00");
            alert.showAndWait();
            tfEPriceAcquired.requestFocus();
        }
        //Check to see if PriceAcquired is less than $1,000,000.00
        else if (tfEPriceAcquired.getText().contains(".") == false &&
                    tfEPriceAcquired.getText().length() > 6){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Price Acquired Too Long");
            alert.setContentText("Price Acquired must be less than $1,000,000.00");
            alert.showAndWait();
            tfEPriceAcquired.requestFocus();
        }
        //Check to see if LicenseNumber length is less than 10 characters
        else if (tfELicenseNumber.getText().length()> 10){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("License Plate Too Long");
            alert.setContentText("Only 10 characters allowed for a License Plate Number");
            alert.showAndWait();
            tfELicenseNumber.requestFocus();
        }
        //Check to see if UpdatedBy is null
        else if("".equals(tfEUpdatedBy.getText())){
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("No Ownership to Update Specified");
                alert.setContentText("Please write your name in the \"Updated By\" box");
                alert.showAndWait();
                tfEUpdatedBy.requestFocus();
        }
        //Check to see if UpdatedBy is less than 20 characters
        else if (tfEUpdatedBy.getText().length()> 20){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Updated By Too Long");
            alert.setContentText("Only 20 characters allowed for an Updated By Ownership");
            alert.showAndWait();
            tfEUpdatedBy.requestFocus();
        }
        else{
            assignEquipmentValues();
            insertEquipmentValuesIntoDB();
            tfEquipmentID.requestFocus();
        }
    }
    
    public boolean checkForSameEquipmentID(){
        //check database for exact match of equipmentid
        try{
            sqlQuery = "SELECT ID FROM EQUIPMENT";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){
                String placeholderEquipmentID = null;
                placeholderEquipmentID = dbResults.getString(1);
                if (placeholderEquipmentID.equals(tfEquipmentID.getText())){
                    return true;
                }
            }
        }catch (SQLException e) {}
        return false;
    }
    
    public boolean checkForSameEquipmentVin(){
        //check database for exact match of equipment vin number
        try{
            sqlQuery = "SELECT VINNUMBER FROM EQUIPMENT";
            sendDBCommand(sqlQuery);
            while (dbResults.next()){
                String dbVinPlaceholder = null;
                String tfVinPlaceholder = null;
                dbVinPlaceholder = dbResults.getString(1).toUpperCase();
                
                tfVinPlaceholder = tfEVin.getText().toUpperCase();

                if (tfVinPlaceholder.equals(dbVinPlaceholder))
                    return true;
            }
        }catch (SQLException e) {}
        return false;
    }
    
    public void assignEquipmentValues(){
        //Assign EquipmentID to myEquipment
        myEquipment.setID(Integer.parseInt(tfEquipmentID.getText()));
        //Assign VinNumber to myEquipment
        myEquipment.setVinNumber(tfEVin.getText());
        //Assign Make to myEquipment
        if ("".equals(tfEMake.getText()))
            myEquipment.setMake("NULL");
        else myEquipment.setMake(tfEMake.getText());
        //Assign Model to myEquipment
        if ("".equals(tfEModel.getText()))
            myEquipment.setModel("NULL");
        else myEquipment.setModel(tfEModel.getText());
        //Assign EquipmentYear to myEquipment
        if ("".equals(tfEYear.getText()))
            myEquipment.EquipmentYear = null;
        else myEquipment.setEquipmentYear(Integer.parseInt(tfEYear.getText()));
        //Assign PriceAcquired to myEquipment
        if ("".equals(tfEPriceAcquired.getText()))
            myEquipment.PriceAcquired = null;
        else myEquipment.setPriceAcuired(Double.parseDouble(tfEPriceAcquired.getText()));
        //Assign LicenseNumber to myEquipment
        if ("".equals(tfELicenseNumber.getText()))
            myEquipment.setLicenseNumber("NULL");
        else myEquipment.setLicenseNumber(tfELicenseNumber.getText());
        //Assign DriverID to myEquipment
        myEquipment.setDriverID(Integer.parseInt(
                cbEDriverID.getValue().toString().substring(0,
                cbEDriverID.getValue().toString().indexOf(" "))));
        //Assign UpdatedBy to myEquipment
        if ("".equals(tfDUpdatedBy.getText()))
            myEquipment.setLastUpdatedBy("NULL");
        else myEquipment.setLastUpdatedBy(tfDUpdatedBy.getText());
        //Assign LastUpdated to myEquipment
        myEquipment.setLastUpdated(getCurrentTimeStamp());
        
    }
    
    public static String getCurrentTimeStamp(){
        //get current time and format to MM/dd/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        long longDate = System.currentTimeMillis();
        String Date = sdf.format(longDate);
        return Date;
    }
    
    public static boolean isInteger(String str){
        //Test a variable to see if it's an integer
        try{
          double d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe){
          return false;
        }
        return true;
    }
    
    public static boolean isDouble(String str){
        //Test a variable to see if it's a double
        try{
          double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe){
          return false;
        }
        return true;
    }
    
    public static boolean isDateValid(String date){
        //http://stackoverflow.com/questions/226910/how-to-sanity-check-a-date-in-java
        //check if a date is a valid date
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
}
    
    public void sendDBCommand(String sqlQuery){
        //try to connect
        try{
            //instatiate a new data source object
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(jdbcConnectionURL);
            
            //Send the user/pass and get an open connection
            dbConn = ds.getConnection(userID, userPASS);
            commStmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            dbResults = commStmt.executeQuery(sqlQuery);
            }catch (SQLException e){}  
        }
        
    public static void main(String[] args) {
        Application.launch(args);   
    }
    
}