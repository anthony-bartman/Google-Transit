/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * <p>
 * This class acts as the primary Observer of the Subject-Observer pattern implemented in this program. This means that
 * anytime there are significant changes to any Subjects, all Observers will be notified and proper action will be taken
 * depending on the Observer. In the case of this class (the FXML controller of the program) GUI elements are updated
 * with new data anytime TransitData parses in new data.
 */
package transit;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransitController implements Observer {
    //log errors anytime they occur
    public static Logger LOGGER = Logger.getAnonymousLogger();

    //Tab FXML
    @FXML
    TabPane tabPane = new TabPane();

    //Feature #1 FXML
    @FXML
    TextArea allRoutesTextArea = new TextArea();
    @FXML
    TextArea allTripsTextArea = new TextArea();
    @FXML
    TextArea allStopsTextArea = new TextArea();
    @FXML
    TextArea allStopTimesTextArea = new TextArea();

    //Feature #2 FXML
    @FXML
    TextArea textArea_F2 = new TextArea();
    @FXML
    Button tripDistanceButton_F2 = new Button();

    //Feature #3 FXML
    @FXML
    TextArea textArea_F3 = new TextArea();
    @FXML
    Button tripSpeedButton_F3 = new Button();

    //Feature #4 FXML
    @FXML
    TextArea tripsPerStopTextArea = new TextArea();
    @FXML
    Button tripsPerStopButton = new Button();

    //Feature #5 FXML
    @FXML
    TextArea textArea_F5 = new TextArea();
    @FXML
    TextField stopSearchBar_F5 = new TextField();
    @FXML
    Button stopSearchButton_F5 = new Button();

    //Feature #6 FXML
    @FXML
    TextArea textArea_F6 = new TextArea();
    @FXML
    TextField routeSearchBar_F6 = new TextField();
    @FXML
    Button routeSearchButton_F6 = new Button();

    //Feature #8 FXML
    @FXML
    TextArea textArea_F8 = new TextArea();
    @FXML
    TextField stopSearchBar_F8 = new TextField();
    @FXML
    Button stopSearchButton_F8 = new Button();

    //Feature #12 FXML
    @FXML
    TextArea textArea_F12 = new TextArea();
    @FXML
    TextField stopSearchBar_F12 = new TextField();
    @FXML
    Button stopSearchButton_F12 = new Button();
    @FXML
    Button updateTripsButton_F12 = new Button();

    //use single instance of TransitData
    private TransitData transitData = TransitData.getInstance();

    //Strings representing the valid first lines of each type of file
    private static String ROUTE_FIRST_LINE = "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color";
    private static String TRIP_FIRST_LINE = "route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id";
    private static String STOP_FIRST_LINE = "stop_id,stop_name,stop_desc,stop_lat,stop_lon";
    private static String STOP_TIME_FIRST_LINE = "trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type";

    /**
     * This method runs after the start of the program. This differs from a constructor in the FXML components have
     * been created at this point.
     */
    @FXML
    public void initialize() {
        //provide the Controller to TransitData
        transitData.attach(this);
    }

    /**
     * This method handles all aspects of importing GTFS files into the program so that they can be parsed. First,
     * it is validated that 4 files were selected. Next, it is made sure that there is at least one of each type of file
     * based on its name. After that, the first line of each file denoting each field is checked. Once the whole set is
     * validated, the set is sorted in order of parsing priority. Lastly, each file in the set is parsed into objects.
     *
     * @author Declan Bruce
     */
    public void importFiles() {
        try {
            //create a FileChooser for handling opening text files
            FileChooser fileChooser = new FileChooser();

            //only allow the user to select .txt files in the FileChooser
            FileChooser.ExtensionFilter txt = new
                    FileChooser.ExtensionFilter("GTFS File", "*.txt");
            fileChooser.getExtensionFilters().addAll(txt);

            List<File> fileList;
            fileList = fileChooser.showOpenMultipleDialog(null);

            //check to see if the user selected any files
            if (fileList != null && fileList.size() == 4) {
                if (validatedImportedFiles(fileList)) {
                    //sort the file set to be in the correct parsing order
                    List<File> sortedFileList = sortImportedFiles(fileList);

                    //import each respective text file in a specific order (Routes, Trips, Stops, StopTimes)
                    for (File file : sortedFileList) {
                        parseFile(file);
                    }

                    //with a valid file set being provided, allow users to access other program features
                    ObservableList<Tab> tabList = tabPane.getTabs();
                    for(Tab tab : tabList){
                        tab.setDisable(false);
                    }
                } else {
                    //create an alert to notify the user that they did not select the correct number of text files
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("File Validation Error");
                    errorAlert.setHeaderText("Invalid File Name/Format:");
                    errorAlert.setContentText("Please verify that all files are formatted correctly.");
                    errorAlert.showAndWait();
                }
            } else {
                //create an alert to notify the user that they did not select the correct number of text files
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("File Import Error");
                errorAlert.setHeaderText("Too Many/Too Few Files:");
                errorAlert.setContentText("Please select 4 files, one of each type (stops, routes, trips, and stop_times).");
                errorAlert.showAndWait();
            }

            fileChooser.getExtensionFilters().removeAll(txt);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File Parsing Error: FileNotFoundException\n");

            //create an alert to notify the user of poorly formatted text files
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("File Parsing Error");
            errorAlert.setHeaderText("File Selection Error:");
            errorAlert.setContentText("Please selection a valid, existing file.");
            errorAlert.showAndWait();
        }
    }

    /**
     * Helper method to sort an already validated set of text files in the necessary parsing order so that every object
     * is created correctly and with the right attributes (i.e. StopTimes in Trips).
     *
     * @param validImportedFiles list of imported text files, confirmed to be a good set of files previously
     * @return sorted list of imported text files, put in order of parsing priority
     * @author Declan Bruce
     */
    private List<File> sortImportedFiles(List<File> validImportedFiles) {
        //create an array that holds 4 files
        File[] sortedFileSet = new File[4];

        //repeat process 4 times to place each file in its correct location
        for (int i = 0; i < 4; i++) {
            //iterate over every file in the the given file set
            for (File file : validImportedFiles) {
                //obtain the name of the file
                String fileName = file.getName();
                String[] filePath = fileName.split("\\.");

                //determine which file is being handled based on its name (each file given WILL be one of these names)
                switch (filePath[0].toLowerCase()) {
                    case "routes":
                        //"routes.txt" should be parsed first
                        sortedFileSet[0] = file;
                        break;
                    case "trips":
                        //"trips.txt" should be parsed second
                        sortedFileSet[1] = file;
                        break;
                    case "stops":
                        //"stops.txt" should be parsed third
                        sortedFileSet[2] = file;
                        break;
                    case "stop_times":
                        //"stop_times.txt" should be parsed fourth
                        sortedFileSet[3] = file;
                        break;
                }
            }
        }
        //convert the sorted File array into an ArrayList
        return new ArrayList<>(Arrays.asList(sortedFileSet));
    }

    /**
     * Helper method to determine if the collection of imported text files includes one of each required text file.
     *
     * @param importedFiles list of imported text files from a FileChooser window
     * @return true if the list of imported files is okay to proceed and parse
     * @author Declan Bruce
     */
    private boolean validatedImportedFiles(List<File> importedFiles) {
        //flag signifying that the entire file set is valid
        boolean validImportedFiles = false;

        //create flags for finding one of each file by name
        boolean routeFileFound = false;
        boolean tripFileFound = false;
        boolean stopFileFound = false;
        boolean stopTimeFileFound = false;

        //create flags for having valid first lines in each file
        boolean routeFirstLineValid = false;
        boolean tripFirstLineValid = false;
        boolean stopFirstLineValid = false;
        boolean stopTimeFirstLineValid = false;

        for (File file : importedFiles) {
            //obtain the name of the file
            String fileName = file.getName();
            String[] filePath = fileName.split("\\.");

            //check the name and first line of each file
            try {
                Scanner fileScanner = new Scanner(file);

                //determine which file is being handled based on its name
                switch (filePath[0].toLowerCase()) {
                    case "routes":
                        routeFileFound = true;

                        //check the first line of "routes.txt" (as long as the file is not empty)
                        if (file.length() != 0 && fileScanner.nextLine().equals(ROUTE_FIRST_LINE)) {
                            routeFirstLineValid = true;
                        }
                        break;
                    case "trips":
                        tripFileFound = true;

                        //check the first line of "trips.txt" (as long as the file is not empty)
                        if (file.length() != 0 && fileScanner.nextLine().equals(TRIP_FIRST_LINE)) {
                            tripFirstLineValid = true;
                        }
                        break;
                    case "stops":
                        stopFileFound = true;

                        //check the first line of "stops.txt" (as long as the file is not empty)
                        if (file.length() != 0 && fileScanner.nextLine().equals(STOP_FIRST_LINE)) {
                            stopFirstLineValid = true;
                        }
                        break;
                    case "stop_times":
                        stopTimeFileFound = true;

                        //check the first line of "stop_times.txt" (as long as the file is not empty)
                        if (file.length() != 0 && fileScanner.nextLine().equals(STOP_TIME_FIRST_LINE)) {
                            stopTimeFirstLineValid = true;
                        }
                        break;
                    default:
                        //create an alert to notify the user that they provided a file with an invalid name
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("File Import Error");
                        errorAlert.setHeaderText("Incorrect File Name:");
                        errorAlert.setContentText("Acceptable File Names: routes.txt, trips.txt, stops.txt, stop_times.txt");
                        errorAlert.showAndWait();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading an imported file's first line.\n");
            }
        }

        //there is one of each file existing and the all first lines are valid, the imported file set is valid
        if (routeFileFound && tripFileFound && stopFileFound && stopTimeFileFound &&
                routeFirstLineValid && tripFirstLineValid && stopFirstLineValid && stopTimeFirstLineValid) {
            validImportedFiles = true;
        }
        return validImportedFiles;
    }

    /**
     * This method determines which file is being imported based on the name and then runs the corresponding parse
     * method to create usable objects from the text.
     *
     * @param file text file to be imported
     * @throws FileNotFoundException thrown if the file's parse method somehow does not find the file
     * @author Declan Bruce
     */
    public void parseFile(File file) throws FileNotFoundException {
        //obtain the name of the file
        String fileName = file.getName();
        String[] filePath = fileName.split("\\.");

        //determine which file is being handled based on its name
        switch (filePath[0].toLowerCase()) {
            case "routes":
                transitData.parseRoutes(file);
                break;
            case "stops":
                transitData.parseStops(file);
                break;
            case "trips":
                transitData.parseTrips(file);
                break;
            case "stop_times":
                transitData.parseStopTimes(file);
        }
    }

    /**
     * Displays the calculated average trip speed of every known Trip into a TextArea.
     *
     * @author Declan Bruce
     */
    public void displayAllTripSpeeds() {
        textArea_F3.clear();
        if (!transitData.getAllTrips().isEmpty()) {
            StringBuilder builder = new StringBuilder();

            //iterate over each Stop and combine all information into one massive String
            builder.append("______________________________________________" + "\n");
            ArrayList<Trip> allTrips = transitData.getAllTrips();

            //limit average trip speed results to 2 decimal places for readability
            DecimalFormat numberFormat = new DecimalFormat("#.00");

            for (Trip trip : allTrips) {
                //only display a Trip's speed if it contains a proper sequence of StopTimes
                if (!trip.getStopSequence().isEmpty()) {
                    builder.append("Trip ID: " + trip.getTripID() + "\n");
                    builder.append("Trip Speed: " + numberFormat.format(trip.calcAverageTripSpeed()) + "KM/HR\n");
                    builder.append("______________________________________________" + "\n");
                }
            }

            //display the overall String with information on all Stops in appropriate TextArea
            textArea_F3.setText(builder.toString());
        } else {
            textArea_F3.setText("*** No Available Trips ***");
        }
    }

    /**
     * Displays the calculated average trip speed of every known Trip into a TextArea.
     *
     * @author Declan Bruce
     */
    public void displayAllTripDistances() {
        textArea_F2.clear();
        if (!transitData.getAllTrips().isEmpty()) {
            StringBuilder builder = new StringBuilder();

            //iterate over each Stop and combine all information into one massive String
            builder.append("______________________________________________" + "\n");
            ArrayList<Trip> allTrips = transitData.getAllTrips();

            //limit trip distance results to 2 decimal places for readability
            DecimalFormat numberFormat = new DecimalFormat("#.00");

            for (Trip trip : allTrips) {
                //only display a Trip's distance if it contains a proper sequence of StopTimes
                if (!trip.getStopSequence().isEmpty()) {
                    builder.append("Trip ID: " + trip.getTripID() + "\n");
                    builder.append("Trip Distance: " + numberFormat.format(trip.calcTotalDistance()) + "KM\n");
                    builder.append("______________________________________________" + "\n");
                }
            }

            //display the overall String with information on all Stops in appropriate TextArea
            textArea_F2.setText(builder.toString());
        } else {
            textArea_F2.setText("*** No Available Trips ***");
        }
    }

    /**
     * Displays the number of Trips each Stop appears in, including 0 (suggesting an unused Stop).
     *
     * @author Declan Bruce
     */
    public void displayNumTripsPerStop() {
        tripsPerStopTextArea.clear();
        if (!transitData.getAllTrips().isEmpty() || !transitData.getAllStops().isEmpty()) {
            StringBuilder builder = new StringBuilder();

            //obtain the number of Trips each Stop (by stop_id) appears in
            HashMap<String, Integer> tripOccurrences = transitData.findNumTripsPerStop();

            //iterate over every Stop and display its number of occurrences
            builder.append("______________________________________________" + "\n");
            ArrayList<Stop> allStops = transitData.getAllStops();
            for (Stop stop : allStops) {
                //check to see if the HashMap contains a mapping for the associated stop_id
                if (tripOccurrences.get(stop.getStopID()) != null) {
                    //generate text representing the stop_id and the number of Trips the Stop was found in
                    String stop_id = stop.getStopID();

                    builder.append("Stop ID: " + stop.getStopID() + "\n");
                    builder.append("# of Trips w/ this Stop: " + tripOccurrences.get(stop_id) + "\n");
                    builder.append("______________________________________________" + "\n");
                } else {
                    //no mapping found for the given stop_id, signify no Trips containing said Stop
                    builder.append("Stop ID: " + stop.getStopID() + "\n");
                    builder.append("# of Trips w/ this Stop: 0 \n");
                    builder.append("______________________________________________" + "\n");
                }
            }

            //display the overall String with information on all Stops in appropriate TextArea
            tripsPerStopTextArea.setText(builder.toString());
        } else {
            tripsPerStopTextArea.setText("*** No Available Trips/Stops ***");
        }
    }

    /**
     * Method for generically applying a list of Routes to a given TextArea.
     *
     * @param textArea  the TextArea where the Routes should be printed out
     * @param routeList the list of Routes that are to be printed
     * @author Declan Bruce
     */
    public void displayRouteList(TextArea textArea, ArrayList<Route> routeList) {
        StringBuilder builder = new StringBuilder();
        textArea.clear();

        //check if there are any Routes to display
        if(!routeList.isEmpty()) {
            //iterate over every Route in a list of Routes and display them individually
            builder.append("______________________________________________" + "\n");
            for (Route route : routeList) {
                //iterate over each Route and combine all information into one massive String
                builder.append("Route ID: " + route.getRouteID() + "\n");
                builder.append("Color Hex: " + route.getColorHex() + "\n");
                builder.append("______________________________________________" + "\n");
            }
        } else {
            //notify the user of an empty list
            builder.append("______________________________________________" + "\n");
            builder.append("               No Routes Found                " + "\n");
            builder.append("______________________________________________" + "\n");
        }
        //display the overall String with information on all Routes in appropriate TextArea
        textArea.setText(builder.toString());
    }

    /**
     * Method for generically applying a list of Trips to a given TextArea.
     *
     * @param textArea the TextArea where the Trips should be printed out
     * @param tripList the list of Trips that are to be printed
     * @author Declan Bruce
     */
    public void displayTripList(TextArea textArea, ArrayList<Trip> tripList) {
        StringBuilder builder = new StringBuilder();
        textArea.clear();

        //check if there are any Trips to display
        if(!tripList.isEmpty()) {
            //iterate over every Trip in a list of Trips and display them individually
            builder.append("______________________________________________" + "\n");
            for (Trip trip : tripList) {
                //iterate over each Trip and combine all information into one massive String
                builder.append("Route ID: " + trip.getRouteID() + "\n");
                builder.append("Service ID: " + trip.getServiceID() + "\n");
                builder.append("Trip ID: " + trip.getTripID() + "\n");
                builder.append("Trip Headsign: " + trip.getTripHeadsign() + "\n");
                builder.append("Direction ID: " + trip.getDirectionID() + "\n");
                builder.append("Block ID: " + trip.getRouteID() + "\n");
                builder.append("Shape ID: " + trip.getShapeID() + "\n");
                builder.append("______________________________________________" + "\n");
            }
        } else {
            //notify the user of an empty list
            builder.append("______________________________________________" + "\n");
            builder.append("                No Trips Found                " + "\n");
            builder.append("______________________________________________" + "\n");
        }
        //display the overall String with information on all Trips in appropriate TextArea
        textArea.setText(builder.toString());
    }

    /**
     * Method for generically applying a list of Stops to a given TextArea.
     *
     * @param textArea the TextArea where the Stops should be printed out
     * @param stopList the list of Stops that are to be printed
     * @author Declan Bruce
     */
    public void displayStopList(TextArea textArea, ArrayList<Stop> stopList) {
        StringBuilder builder = new StringBuilder();
        textArea.clear();

        //check if there are any Stops to display
        if(!stopList.isEmpty()) {
            //iterate over every Stop in a list of Stops and display them individually
            builder.append("______________________________________________" + "\n");
            for (Stop stop : stopList) {
                //iterate over each Stop and combine all information into one massive String
                builder.append("Stop ID: " + stop.getStopID() + "\n");
                builder.append("Name: " + stop.getName() + "\n");
                builder.append("Description: " + stop.getDesc() + "\n");
                builder.append("Latitude: " + stop.getLatitude() + "\n");
                builder.append("Longitude: " + stop.getLongitude() + "\n");
                builder.append("______________________________________________" + "\n");
            }
        } else {
            //notify the user of an empty list
            builder.append("______________________________________________" + "\n");
            builder.append("                No Stops Found                " + "\n");
            builder.append("______________________________________________" + "\n");
        }
        //display the overall String with information on all Stops in appropriate TextArea
        textArea.setText(builder.toString());
    }

    /**
     * Method for generically applying a list of StopTimes to a given TextArea.
     *
     * @param textArea the TextArea where the StopTimes should be printed out
     * @param tripList the list of Trips whose StopTimes are to be printed
     * @author Isaiah Doran
     */
    public void displayStopTimeList(TextArea textArea, ArrayList<Trip> tripList) {
        StringBuilder builder = new StringBuilder();
        textArea.clear();

        //check if there are any StopTimes to display
        if (!tripList.isEmpty()) {
            //iterate over every StopTime in every Trip in a list of Trips and display relevant fields individually
            builder.append("______________________________________________" + "\n");
            for (Trip trip : tripList) {
                ArrayList<StopTime> stopSequence = trip.getStopSequence();
                String tripID = "N/A";

                //make sure the list of StopTimes in a Trip is not empty (for printing out trip_id)
                if (!stopSequence.isEmpty()) {
                    StopTime firstStopTime = stopSequence.get(0);
                    tripID = firstStopTime.getTripID();
                }
                builder.append("Trip ID: " + tripID + "\n");

                //iterate over each StopTime and combine all information into one massive String
                for (StopTime stopTime : stopSequence) {
                    //iterate over each StopTime and combine all information into one massive String
                    builder.append("Stop ID: " + stopTime.getStopID() + "\n");
                    builder.append("Arrival Time: " + stopTime.getArrivalTime() + "\n");
                }
                builder.append("______________________________________________" + "\n");
            }
        }else {
            //notify the user of an empty list
            builder.append("______________________________________________" + "\n");
            builder.append("          No Trips/Stop Times Found           " + "\n");
            builder.append("______________________________________________" + "\n");
        }
        //display the overall String with information on all StopTimes in appropriate TextArea
        textArea.setText(builder.toString());
    }

    /**
     * This method handles when the search button is pressed for the Feature 5 Tab. This searches for all Routes
     * containing the Stop signified by the stop_id provided by the user. All Routes found will be displayed to
     * an appropriate TextArea in the Feature 5 Tab.
     */
    public void routesContainingStopSearch() {
        String userEntry = stopSearchBar_F5.getText();

        //check if the user entered anything into the TextField
        if (userEntry != null && !userEntry.isEmpty()) {
            ArrayList<Route> routesWithStop = transitData.findRoutesWithStop(userEntry);

            //check to see if the search returned any Routes
            if (!routesWithStop.isEmpty()) {
                //display each of the Routes found to contain the Stop
                displayRouteList(textArea_F5, routesWithStop);
            } else {
                //the search found no appropriate Routes, notify the user
                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                errorAlert.setTitle("Search Results");
                errorAlert.setHeaderText("Search Results Inconclusive:");
                errorAlert.setContentText("No Routes were found to contain the specified Stop.");
                errorAlert.showAndWait();
            }
        } else {
            //the user pressed the search button without entering anything into the TextField
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Search Input");
            errorAlert.setHeaderText("Invalid Search Input:");
            errorAlert.setContentText("Please enter a valid stop_id.");
            errorAlert.showAndWait();
        }
    }

    /**
     * Handles when the F6 search button is pressed. Will display all stop_id's of a Route in the Feature 6 Tab.
     *
     * @author Anthony Bartman
     */
    public void routeSearchButton_F6() {
        //Checks if user has something in the TextField
        if (routeSearchBar_F6.getText() != null && !routeSearchBar_F6.getText().isEmpty()) {
            //Finds all stops that correspond to the indicated route id that the user will type in on the graphical user interface that we created becuase we are awesome... Declan xD
            ArrayList<Stop> allRouteStops = transitData.findAllStops(routeSearchBar_F6.getText());
            //display to GUI
            displayStopList(textArea_F6, allRouteStops);
        } else {  //User put nothing in the search bar
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Search Input");
            errorAlert.setHeaderText("Invalid Search Input:");
            errorAlert.setContentText("Please enter a valid route_id.");
            errorAlert.showAndWait();
        }
    }

    /**
     * Handles when the F8 search button is pressed. Will display the closest trips to the user inputted stop_id.
     *
     * @author Anthony Bartman
     */
    public void upcomingTripsSearch() {
        //Checks if user has something in the textfield
        if (stopSearchBar_F8.getText() != null && !stopSearchBar_F8.getText().isEmpty()) {
            //Search for the closest trip to the stop_id (Feature 8 - Bartman)
            ArrayList<Trip> closestTrips = transitData.findNextTrip(stopSearchBar_F8.getText());

            //display to GUI
            displayTripList(textArea_F8, closestTrips);
        } else {  //User put nothing in the search bar
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Search Input");
            errorAlert.setHeaderText("Invalid Search Input:");
            errorAlert.setContentText("Please enter a valid stop_id.");
            errorAlert.showAndWait();
        }
    }



    /*
     * This method handles when the search button is pressed for the Feature 12 Tab. This searches for all Trips in
     * the same Trip group (same stop sequence). This list of similar Trips can then be edited by the user.
     *
     * @author Anthony Bartman
     */
    public void searchButton_F12() {
        StringBuilder builder = new StringBuilder();
        //Gets all trips with indicated trip ID
        Trip selectedTrip = transitData.searchTrips(stopSearchBar_F12.getText());
        if (selectedTrip != null) {
            builder.append("______________________________________________" + "\n");
            builder.append("\t\tStop Sequence for Trip: " + stopSearchBar_F12.getText() + "\n");
            builder.append("______________________________________________" + "\n");
            builder.append("  Stop Id \t\tArrival \t\tDeparture" + "\n");

            for (StopTime stopTime : selectedTrip.getStopSequence()) {
                builder.append("\t" + stopTime.getStopID() + "\t\t" + stopTime.getArrivalTime()
                        + "\t\t" + stopTime.getDepartureTime() + "\n");
            }
            //Outputs string to transit data
            textArea_F12.setText(builder.toString());
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Invalid Trip ID");
            errorAlert.setHeaderText("Trip was not found");
            errorAlert.setContentText("No Trip was Found With This Trip_Id : " + stopSearchBar_F12.getText());
            errorAlert.showAndWait();
        }
    }

    /**
     * TODO: implement this method
     * TODO: describe this method
     * This method handles when the "Update" button is pressed in the Feature 12 Tab.
     *
     * @author Anthony Bartman
     */
    public void tripGroupUpdate() {
        //verify that something has already been printed to the Feature 12 TextArea
        if (textArea_F12.getText() != null && !textArea_F12.getText().isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
            errorAlert.setTitle("Feature 12 Error");
            errorAlert.setHeaderText("Feature 12 Not Yet Implemented");
            errorAlert.setContentText("METHOD IMPLEMENTATION IN PROGRESS");
            errorAlert.showAndWait();
        } else {
            //user has not searched for a trip yet
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Search Input");
            errorAlert.setHeaderText("Invalid Search Input:");
            errorAlert.setContentText("Please search for a Trip group before editing/updating.");
            errorAlert.showAndWait();
        }
    }

    /**
     * TODO: implement this method
     *
     * @param GTFSFiles
     */
    public void exportFiles(ArrayList<File> GTFSFiles) {
    }

    /**
     * This method determines what needs to be updated whenever a significant change is specified by a Subject.
     * In the context of this program, the only Subject being observed is TransitData, the single instance
     * data store for the entire program. Appropriate GUI components are updated to show the new data.
     *
     * @author Declan Bruce
     */
    @Override
    public void update() {
        //update each overall data display
        displayRouteList(allRoutesTextArea, transitData.getAllRoutes());
        displayTripList(allTripsTextArea, transitData.getAllTrips());
        displayStopList(allStopsTextArea, transitData.getAllStops());
        displayStopTimeList(allStopTimesTextArea, transitData.getAllTrips());
    }
}

