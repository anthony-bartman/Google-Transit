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
 * This class acts as primary data storage location for the project. At program start, a private instance is created
 * and acts as the only instance of the class. This single instance can be obtained publicly by any part of the program.
 * This class also acts as the primary Subject of the Subject-Observer pattern implemented in this program. This is
 * because changes to any of the data stored in this class would be very pertinent to Observers such as
 * TransitController which handles all GUI interactions.
 */
package transit;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

public class TransitData extends Subject {
    //TODO: change these data structures to support better .contains(ID) times ?
    private ArrayList<Bus> allBuses;
    private ArrayList<Route> allRoutes;
    private ArrayList<Stop> allStops;
    private ArrayList<Trip> allTrips;
    private ArrayList<Observer> dataObservers;

    //private constructor for singleton behavior
    private TransitData(ArrayList<Bus> allBuses, ArrayList<Route> allRoutes, ArrayList<Stop>
            allStops, ArrayList<Trip> allTrips) {
        this.allBuses = allBuses;
        this.allRoutes = allRoutes;
        this.allStops = allStops;
        this.allTrips = allTrips;
        dataObservers = new ArrayList<>();
    }

    //single instance of TransitData
    private static TransitData transitData = new TransitData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    /**
     * Private getter to support singleton behavior for TransitData.
     * This makes it so that there is only ever one instance of this class which will be used across several classes.
     * In this case, this is so that several areas have search capabilities of the same data parsed from imported
     * files from the user.
     *
     * @return transitData, the single instance of TransitData containing all parsed & imported GTFS data
     */
    public static TransitData getInstance() {
        return transitData;
    }

    //constant representing number of expected fields in each object's text file line
    private static int NUM_STOP_TIME_FIELDS = 8;
    private static int NUM_ROUTE_FIELDS = 9;
    private static int NUM_TRIP_FIELDS = 7;
    private static int NUM_STOP_FIELDS = 5;

    /**
     * This method parses each line of a text file into individual StopTime objects.
     *
     * @param file stop_times.txt
     * @throws FileNotFoundException thrown if an invalid file is put in while creating a Scanner
     */
    public void parseStopTimes(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);

        //counter to keep track of the current line number (skip line #1)
        int lineNumber = 2;

        if (file.length() != 0) {
            //skip the first line which shows each fields description
            fileScanner.nextLine();

            //clear all old data before parsing new data
            for (Trip trip : allTrips) {
                trip.getStopSequence().clear();
            }
        }

        while (fileScanner.hasNextLine()) {
            /*
             * Take in next line as String and split into Strings by commas, ignore commas in quotes as well
             * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
             */
            String[] currentLineFields = fileScanner.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", NUM_STOP_TIME_FIELDS);

            //check for the number of expected fields for a single StopTime object
            if (currentLineFields.length == NUM_STOP_TIME_FIELDS) {
                //check to see if the StopTime belongs to an existing Trip
                if (searchTrips(currentLineFields[0]) != null) {
                    //create a new StopTime object and add it to its respective Trip
                    StopTime stopTime = new StopTime(currentLineFields[0], currentLineFields[1],
                            currentLineFields[2],
                            currentLineFields[3],
                            currentLineFields[4],
                            currentLineFields[5],
                            currentLineFields[6],
                            currentLineFields[7]
                    );
                    Trip foundTrip = searchTrips(currentLineFields[0]);
                    foundTrip.insertNextStopTime(stopTime);
                }
            } else {
                //log the case in which a line is not properly formatted
                TransitController.LOGGER.log(Level.WARNING, "stop_times.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
                System.out.println("WARNING: stop_times.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
            }
            lineNumber++;
        }
        fileScanner.close();

        //notify Observers that new data has potentially been created
        notifyObservers();
    }

    /**
     * This method parses each line of a text file into individual Route objects.
     *
     * @param file routes.txt
     * @throws FileNotFoundException thrown if an invalid file is put in while creating a Scanner
     */
    public void parseRoutes(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);

        //counter to keep track of the current line number (skip line #1)
        int lineNumber = 2;

        if (file.length() != 0) {
            //skip the first line which shows each fields description
            fileScanner.nextLine();

            //clear all old data before parsing new data
            allRoutes.clear();
        }

        while (fileScanner.hasNextLine()) {
            /*
             * Take in next line as String and split into Strings by commas, ignore commas in quotes as well
             * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
             */
            String[] currentLineFields = fileScanner.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", NUM_ROUTE_FIELDS);

            //check for the number of expected fields for a single Route object
            if (currentLineFields.length == NUM_ROUTE_FIELDS) {
                //create a new Route object and add it to our overall list of Routes
                allRoutes.add(new Route(currentLineFields[0],
                        currentLineFields[7]
                ));
            } else {
                //log the case in which a line is not properly formatted
                TransitController.LOGGER.log(Level.WARNING, "routes.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
                System.out.println("WARNING: routes.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
            }
            lineNumber++;
        }
        fileScanner.close();

        //notify Observers that new data has potentially been created
        notifyObservers();
    }

    /**
     * This method parses each line of a text file into individual Trip objects.
     *
     * @param file trips.txt
     * @throws FileNotFoundException thrown if an invalid file is put in while creating a Scanner
     */
    public void parseTrips(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);

        //counter to keep track of the current line number (skip line #1)
        int lineNumber = 2;

        if (file.length() != 0) {
            //skip the first line which shows each fields description
            fileScanner.nextLine();

            //clear all old data before parsing new data
            allTrips.clear();
        }

        while (fileScanner.hasNextLine()) {
            /*
             * Take in next line as String and split into Strings by commas, ignore commas in quotes as well
             * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
             */
            String[] currentLineFields = fileScanner.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", NUM_TRIP_FIELDS);

            //check for the number of expected fields for a single Trip object
            if (currentLineFields.length == NUM_TRIP_FIELDS) {
                //check to see if numeric "trip_headsign" is empty or not
                if (currentLineFields[4].isEmpty()) {
                    currentLineFields[4] = "0";
                }

                //create a new Trip object and add it to our overall list of Trips
                allTrips.add(new Trip(currentLineFields[0], currentLineFields[1],
                        currentLineFields[2],
                        currentLineFields[3],
                        Integer.parseInt(currentLineFields[4]),
                        currentLineFields[5],
                        currentLineFields[6]
                ));
            } else {
                //log the case in which a line is not properly formatted
                TransitController.LOGGER.log(Level.WARNING, "trips.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
                System.out.println("WARNING: trips.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
            }
            lineNumber++;
        }

        //add trip_id(s) of every Trip to their appropriate Route
        for (Trip trip : allTrips) {
            Route route = searchRoutes(trip.getRouteID());

            //if a vaild Route is found, add this Trip's trip_id into the Route
            if (route != null) {
                route.insertTripID(trip.getTripID());
            }
        }

        //notify Observers that new data has potentially been created
        notifyObservers();
    }

    /**
     * This method takes in "stops.txt" and parses each of its lines into individual Stop objects.
     *
     * @param file stops.txt
     * @throws FileNotFoundException thrown if an invalid file is put in while creating a Scanner
     */
    public void parseStops(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);

        //counter to keep track of the current line number (skip line #1)
        int lineNumber = 2;

        if (file.length() != 0) {
            //skip the first line which shows each fields description
            fileScanner.nextLine();

            //clear all old data before parsing new data
            allStops.clear();
        }

        while (fileScanner.hasNextLine()) {
            /*
             * Take in next line as String and split into Strings by commas, ignore commas in quotes as well
             * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
             */
            String[] currentLineFields = fileScanner.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", NUM_STOP_FIELDS);

            //check for the number of expected fields for a single Stop object
            if (currentLineFields.length == NUM_STOP_FIELDS) {
                //create a new Stop object and add it to our overall list of Stops
                allStops.add(new Stop(currentLineFields[0], currentLineFields[1],
                        currentLineFields[2],
                        Double.parseDouble(currentLineFields[3]),
                        Double.parseDouble(currentLineFields[4])
                ));
            } else {
                //log the case in which a line is not properly formatted
                TransitController.LOGGER.log(Level.WARNING, "stops.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
                System.out.println("WARNING: stops.txt line #" + lineNumber +
                        " was incorrectly formatted.\n");
            }
            lineNumber++;
        }
        fileScanner.close();

        //notify Observers that new data has potentially been created
        notifyObservers();
    }

    /**
     * search for a route with routeID
     *
     * @param routeID ID of the desired Route
     */
    public Route searchRoutes(String routeID) {
        Route currentRoute = null;
        for (Route route : allRoutes) {
            if (route.getRouteID().equals(routeID)) {
                currentRoute = route;
            }
        }
        return currentRoute;
    }

    /**
     * Search for a stop based on stopID
     *
     * @param stopID ID of the desired Stop
     */
    public Stop searchStops(String stopID) {
        Stop currentStop = null;
        for (Stop stop : allStops) {
            if (stop.getStopID().equals(stopID)) {
                currentStop = stop;
            }
        }
        return currentStop;
    }

    /**
     * Search for a trip based on tripID
     *
     * @param tripID ID of the desired Trip
     */
    public Trip searchTrips(String tripID) {
        Trip currentTrip = null;
        for (Trip trip : allTrips) {
            if (trip.getTripID().equals(tripID)) {
                currentTrip = trip;
            }
        }
        return currentTrip;
    }

    /**
     * This method will find the closest trip to the stopID and return its
     * closest trip object.
     *
     * @param stopID Used to view closest trip to this ID
     * @return Closest trip to the parameter stop_id
     * @author Bartman
     */
    public ArrayList<Trip> findNextTrip(String stopID) {
        ArrayList<Trip> closestTrips = new ArrayList<>();
        ArrayList<Integer> arrivalTimes = new ArrayList<>();
        //Validates the stopId is connects to a stop
        Stop stop = transitData.searchStops(stopID);
        if (stop != null) {
            //Gets current time of computer
            LocalTime currentTime = LocalTime.now();
            //Searches through all trips and Gets a list of closest arrival trips, with the indicated stop_id
            for (Trip trip : allTrips) {
                //Searches through the stopSequence for all the stops inside the individual trip
                ArrayList<StopTime> stopSequence = trip.getStopSequence();
                for (StopTime stopTime : stopSequence) {
                    LocalTime arrivalTime = currentTime;
                    String stopATime = stopTime.getArrivalTime();
                    //Checks if this stopTime in the trips stopSequence, has the stopID we are looking for
                    if (stopTime.getStopID().equals(stopID)) {
                        arrivalTime = formatTime(stopATime);
                        //Checks if the arrival time of the matching stop, is after the current time
                        if (arrivalTime.isAfter(currentTime)) {
                            //List of arrivalTimes of all stops
                            arrivalTimes.add(timeToSecond(arrivalTime.toString()));
                            closestTrips.add(trip);
                        }
                    }
                }
            }
            //Arranges all trips by closest to current time
            if (closestTrips.size() > 1) {
                closestTrips = arrangeTrips(closestTrips, arrivalTimes, timeToSecond(currentTime.toString()));
            }
        } else { //No Stop was found with that Stop_ID
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Invalid Stop ID");
            errorAlert.setHeaderText("Stop was not found");
            errorAlert.setContentText("No Stop was Found With This Stop_ID : " + stopID);
            errorAlert.showAndWait();
        }
        return closestTrips;
    }

    /**
     * This method will arrange the trips from closest trip with a stopId to
     * farthest trip with stopId
     *
     * @param trips       List of FoundTrips
     * @param arrTimes    List of ArrivalTimes for found Stop_ids in trips
     * @param currentTime Current system time
     * @return Ordered arraylist
     * @author Bartman
     */
    private ArrayList<Trip> arrangeTrips(ArrayList<Trip> trips, ArrayList<Integer> arrTimes, int currentTime) {
        ArrayList<Trip> newTripList = new ArrayList<>();
        int totalTrips = trips.size();
        while (totalTrips != 0) {
            int indexOfLT = -1; //Will select which index use
            int lowestTime = Integer.MAX_VALUE;
            for (int i = 0; i < arrTimes.size(); i++) {
                int difference = arrTimes.get(i) - currentTime;
                if (lowestTime >= difference) {
                    lowestTime = difference;
                    indexOfLT = i;
                }
            }
            if (indexOfLT != -1) {
                //Adds the trips to the new ordered trip list
                newTripList.add(trips.get(indexOfLT));
                totalTrips--;
                arrTimes.remove(indexOfLT);
                trips.remove(indexOfLT);
            }
        }
        return newTripList;
    }

    /**
     * Converts a time to seconds
     *
     * @param localTime toString local time object
     * @return time in seconds
     */
    private int timeToSecond(String localTime) {
        //Take Strings and put them into ints
        String[] times = localTime.split(":");
        int time = 0;

        //Puts arrival time in terms of seconds
        for (int i = 0; i < times.length; i++) {
            if (!times[i].isEmpty()) {
                double mult = Math.pow(60, (2 - i));
                if (mult != 1) {
                    time += Integer.parseInt(times[i]) * (int) mult;
                } else {
                    time += (int) Double.parseDouble(times[i]);
                }
            }
        }
        return time;
    }

    /**
     * This method will format a string time into a correctly formatted
     * local time object
     *
     *  Used this link to help with Regex formatting
     *  https://stackoverflow.com/questions/8318236/regex-pattern-for-hhmmss-time-string
     *
     * @param time String to be formatted
     * @return Formatted Local time object, "hh:mm:ss"
     * @author Bartman
     */
    private LocalTime formatTime(String time) {
        LocalTime formattedTime = null;
        //Makes sure String time matches "HH:MM:SS"
        if (time.matches("^([1-2]\\d)(?::([0-5]?\\d))?(?::([0-5]?\\d))?$")) {
            formattedTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
        } else {
            formattedTime = LocalTime.parse("0" + time, DateTimeFormatter.ISO_LOCAL_TIME);
        }
        return formattedTime;
    }

    /**
     * This method finds each all of the Trips in each Route via a list of trip_ids in each Route. From that, the
     * sequence of Stops in a Trip are checked to see if they match the desired stop_id.
     *
     * @param stopID the ID of the Stop whose Routes you would like to see
     * @return list of Routes that contain the desired Stop
     */
    public ArrayList<Route> findRoutesWithStop(String stopID) {
        //obtain every Route we have
        ArrayList<Route> allRoutes = transitData.getAllRoutes();

        //create a list to hold all Routes found to have the requested Stop (found by stop_id)
        ArrayList<Route> routesWithStop = new ArrayList<>();

        //iterate over every Route, checking which Trips and then which Stops are in such
        for (Route route : allRoutes) {
            //obtain the list of all known trip_ids per a single Route
            ArrayList<String> tripsIDsInRoute = route.getTripsInRoute();

            //create an empty list meant to host all known Trip objects for a known Route object
            ArrayList<Trip> tripsInRoute = new ArrayList<>();

            //obtain each Trip object which belongs to a single Route object via a trip_id search function
            for (String tripID : tripsIDsInRoute) {
                Trip trip = transitData.searchTrips(tripID);
                if (trip != null) {
                    tripsInRoute.add(trip);
                }
            }

            //now that we know every Trip object that belongs to a single Route object, we must find all Stops
            for (Trip trip : tripsInRoute) {
                //obtain the list of StopTimes for a single Trip (signifying which Stops are in a single Trip)
                ArrayList<StopTime> stopSequence = trip.getStopSequence();

                //iterate over every StopTime object, checking if the requested stop_id is in the Trip
                for (StopTime stopTime : stopSequence) {
                    if (stopTime.getStopID().equals(stopID)) {
                        //avoid multiple entries of the same Route
                        if (!routesWithStop.contains(route)) {
                            //we have found a stopID that matches the requested stopID, return this Route
                            routesWithStop.add(route);
                        }
                    }
                }
            }
        }
        //give back all of the Routes which were found to have to requested Stop (found by stop_id)
        return routesWithStop;
    }

    /**
     * This method will return all the stop_ids on the indicated route_id
     *
     * @param routeId Route id used to return all stops
     * @return List of stops that are in a valid routeID
     * @author Bartman
     */
    public ArrayList<Stop> findAllStops(String routeId) {
        ArrayList<Stop> allValidStops = new ArrayList<>();
        //verifies routeID is valid
        Route route = transitData.searchRoutes(routeId);
        if (route != null) {
            for (String tripId : route.getTripsInRoute()) {
                Trip routeTrips = transitData.searchTrips(tripId);
                if (routeTrips != null) { //verifies tripId is valid
                    ArrayList<StopTime> tripStopTimes = routeTrips.getStopSequence();
                    for (StopTime stopTime : tripStopTimes) {
                        Stop stop = transitData.searchStops(stopTime.getStopID());
                        //Checks to make sure we dont add duplicate stops
                        if(!allValidStops.contains(stop)) {
                            allValidStops.add(stop);
                        }
                    }
                } else { //Trip was loaded in incorrectly, or incorrectly formatted
                    TransitController.LOGGER.log(Level.WARNING, "WARNING: Trip " + tripId + " was incorrectly formatted.\n");
                    System.out.println("WARNING: Trip " + tripId + " was incorrectly formatted.\n");
                }
            }
        } else { //No routes found with indicated routeId
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Invalid Route ID");
            errorAlert.setHeaderText("Route was not found");
            errorAlert.setContentText("No Route was Found With This Route_ID : " + routeId);
            errorAlert.showAndWait();
        }
        return allValidStops;
    }

	/**
	 * This method counts how many times each Stop's stop_id is found in all Trips.
	 *
	 * @return a HashMap with stop_id(s) as Keys and # of Trips with Stop as Values
	 * @author Declan Bruce
	 */
	public HashMap<String, Integer> findNumTripsPerStop(){
		ArrayList<Stop> allStops = transitData.getAllStops();
		ArrayList<Trip> allTrips = transitData.getAllTrips();

		//create a HashMap filled with keys made of every known stop_id
		HashMap<String,Integer> tripOccurrences = new HashMap<>(allStops.size());
		for(Stop stop : allStops){
			//initialize all counts to 0 for all known stop_id(s)
			tripOccurrences.put(stop.getStopID(), 0);
		}

		//iterate over every Stop in every Trip
		for(Trip trip : allTrips){
			//remove a single occurrence of a Stop if the Trip is a round Trip (avoid double counting)
			roundTripAdj(trip, tripOccurrences);

			ArrayList<StopTime> stopSequence = trip.getStopSequence();
			for(StopTime stopTime : stopSequence){
				//check to see if the HashMap contains a mapping for the associated stop_id
				if(tripOccurrences.get(stopTime.getStopID()) != null){
					String stop_id = stopTime.getStopID();

					//increment the number of occurrences for a single Stop
					tripOccurrences.replace(stop_id, tripOccurrences.get(stop_id) + 1);
				}
			}
		}
		return tripOccurrences;
	}

	/**
	 * This helper method lowers the count for the # of occurrences of the first Stop (from a Trip's sequence of Stops)
	 * if the first and last StopTimes in said sequence have the same stop_id. This resolves counting two occurrences
	 * of the same Stop in the same Trip.
	 *
	 * @param trip the Trip to check for same first and last Stop
	 * @author Declan Bruce
	 */
	private void roundTripAdj(Trip trip, HashMap<String,Integer> tripOccurrences){
		ArrayList<StopTime> stopSequence = trip.getStopSequence();
		if(stopSequence.size() >= 2) {
			StopTime firstStopTime = stopSequence.get(0);
			StopTime lastStopTime = stopSequence.get(stopSequence.size() - 1);

			//check if the first and last Stops in the sequence are the same
			if(firstStopTime.getStopID().equals(lastStopTime.getStopID())){
				//check to see if the HashMap contains a mapping for the associated stop_id
				if(tripOccurrences.get(firstStopTime.getStopID()) != null){
					String stop_id = firstStopTime.getStopID();

					//decrement the number of occurrences for a single Stop
					tripOccurrences.replace(stop_id, tripOccurrences.get(stop_id) - 1);
				}
			}
		}
	}


	/**
	 * feature 7
	 * @param routeID
	 */

	public void searchForTripByRouteID(String routeID){
		for (Route route : allRoutes) {
			StringBuilder builder = new StringBuilder();
			if(route.getRouteID().equals(routeID)){
				for (String trip: route.getTripsInRoute()) {

					//curret time and get arrived time to compare see if one is after another make a list and add to list
					//every trip has a list of stoptimes
					//when arrived at first stoptime
				}

			}

		}


	}


    /**
     * Add the specified Observer to the list of Observers.
     *
     * @param observer the observer that would like to know when TransitData changes
     */
    public void attach(Observer observer) {
        dataObservers.add(observer);
    }

    /**
     * Remove the specified Observer from the list of Observers.
     *
     * @param observer the observer that would no longer like to know when TransitData changes
     */
    public void detach(Observer observer) {
        dataObservers.remove(observer);
    }

    /**
     * This method notifies all attached Observers to update their respective components because the data in
     * TransitData has changed in some fashion.
     */
    public void notifyObservers() {
        //notify all Observers that the data in TransitData has changed and they should update accordingly
        for (Observer observer : dataObservers) {
            observer.update();
        }
    }

    //getters
    public ArrayList<Bus> getAllBuses() {
        return allBuses;
    }

    public ArrayList<Route> getAllRoutes() {
        return allRoutes;
    }

    public ArrayList<Stop> getAllStops() {
        return allStops;
    }

    public ArrayList<Trip> getAllTrips() {
        return allTrips;
    }

}