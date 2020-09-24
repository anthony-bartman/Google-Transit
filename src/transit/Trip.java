/**
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This class describes a Trip. A Trip in the context of a GTFS system is a collection of Stops in a certain order
 * with times associated with them such that attributes such as time taken and distance traveled can be determined.
 * Trips are typically associated with Routes which would be considered a general path of travel which can be made
 * more specific by laying out what order Stops are reached and at what times.
 */
package transit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Trip {
	private String routeID;
	private String serviceID;
	private String tripID;
	private String tripHeadsign;
	private int directionID;
	private String blockID;
	private String shapeID;

	//list for linking Stops, StopTimes, and Trips together
	private ArrayList<StopTime> stopSequence = new ArrayList<>();

	//use single instance of TransitData
	private TransitData transitData;

	/**
	 * Basic constructor for a Trip object.
	 * TODO: describe these attributes
	 * @param routeID
	 * @param serviceID
	 * @param tripID
	 * @param tripHeadsign
	 * @param directionID
	 * @param blockID
	 * @param shapeID
	 */
	public Trip(String routeID, String serviceID, String tripID, String tripHeadsign,
			int directionID, String blockID, String shapeID) {
		this.routeID = routeID;
		this.serviceID = serviceID;
		this.tripID = tripID;
		this.tripHeadsign = tripHeadsign;
		this.directionID = directionID;
		this.blockID = blockID;
		this.shapeID = shapeID;
		this.transitData = TransitData.getInstance();
	}

	//radius of the earth in meters (6,371,000m) for finding trip distance/average speed
	private static int EARTH_RADIUS = 6371000;

	/**
	 * This method utilizes the longitude and latitude of both the first and last stop to find
	 * the distance between two adjacent Stops in a sequence of Stop
	 *
	 * The Haversine formula helps define a straightforward way to find the distance between two
	 * points on Earth that are denoted in latitude and longitude.
	 * SOURCE: https://www.movable-type.co.uk/scripts/latlong.html
	 *
	 * @return the distance between the two points in kilometers
	 * @author Declan Bruce
	 */
	private double calcStopToStopDistance(Stop firstStop, Stop lastStop){
		//obtain latitudes and longitudes, convert each to radians for trig functions
		double startLatRadians = Math.toRadians(firstStop.getLatitude());
		double startLongRadians = Math.toRadians(firstStop.getLongitude());
		double endLatRadians = Math.toRadians(lastStop.getLatitude());
		double endLongRadians = Math.toRadians(lastStop.getLongitude());

		//find change in latitude and longitude, must be in radians for trig functions
		double deltaLatitude = (endLatRadians - startLatRadians);
		double deltaLongitude = (endLongRadians - startLongRadians);

		/*
		Haversine formula:
		A = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
		C = 2 ⋅ arctan2( √A, √(1−A) )
		Distance = R ⋅ C

		1 = start position, 2 = end position
		φ = latitude
		λ = longitude
		R = earth's mean radius
		*/

		//Haversine formula variables
		double A;
		double C;

		/*
		 * Find the value of "A" according to the Haversine formula.
		 * "A" is the square of half the chord length between the points.
		 */
		A = Math.pow(Math.sin((deltaLatitude/2)),2) + (Math.cos(startLatRadians) *
				Math.cos(endLatRadians) * Math.pow(Math.sin((deltaLongitude/2)),2));

		/*
		 * Find the value of "C" according to the Haversine formula.
		 * "C" is the angular distance in radians.
		 */
		C = 2 * Math.atan2(Math.sqrt(A),Math.sqrt(1 - A));

		//return the final distance in kilometers
		return (EARTH_RADIUS * C)/1000;
	}

	/**
	 * This method produces a difference between the start and end time of a trip in hours. This allows for
	 * functionality alongside the method that determines the total distance of a trip in kilometers such that the
	 * average kilometers/hour of the trip can be calculated.
	 *
	 * Date format of start and end times: "HH:mm:ss"
	 *
	 * @return number of hours from first Stop to last Stop of a Trip
	 * @throws ParseException thrown if input date Strings do not follow specified date format
	 * @author Declan Bruce
	 */
	private double calcTripTime() throws ParseException {
		//obtain the starting and ending time of the trip as Strings (from first/last StopTimes)
		String startTimeStr = stopSequence.get(0).getArrivalTime();
		String endTimeStr = stopSequence.get(stopSequence.size() - 1).getDepartureTime();

		//define the format of the departure/arrival times in GTFS text files
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

		try {
			//turn both starting and ending dates in the specified format into Date objects
			Date startDate = dateFormat.parse(startTimeStr);
			Date endDate = dateFormat.parse(endTimeStr);

			//find the difference between each date in milliseconds
			long duration = endDate.getTime() - startDate.getTime();

			//find the number of seconds elapsed
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

			//convert seconds to precise number of hours
			double numHours = (diffInSeconds/3600.0);

			return numHours;
		} catch (ParseException e){
			throw new ParseException("Date(s) misformatted, could not parse into actual time difference.", 0);
		}
	}

	/**
	 * This calculates the cumulative distance of a Trip by adding up the individual distances between each of its
	 * participating Stops.
	 *
	 * @return the total distance of the Trip
	 * @author Declan Bruce
	 */
	public double calcTotalDistance(){
		double totalTripDistance = 0.0;

		/*
		 * Obtain a list of every Stop related to this Trip.
		 * WARNING: this list of Stops will contain the same Stop twice if the Trip starts and ends in the same
		 * 			location (i.e. circular route)
		 */
		ArrayList<Stop> stopsInTrip = new ArrayList<>();
		for(StopTime stopTime : stopSequence){
			//find each Stop that connects to a specific StopTime
			Stop stop = transitData.searchStops(stopTime.getStopID());
			if(stop != null){
				stopsInTrip.add(stop);
			}
		}

		//incrementally find the distance between each Stop for a cumulative Trip distance
		int numStops = (stopsInTrip.size() - 1);
		for(int i = 0; i < numStops; i++) {
			totalTripDistance += calcStopToStopDistance(stopsInTrip.get(i), stopsInTrip.get(i + 1));
		}

		return totalTripDistance;
	}

	/**
	 * This method uses the cumulative distance between all Stops along a trip in order to find a cumulative Trip
	 * distance. The time taken to complete a Trip is found through the time difference between the first and last
	 * Stops along a Trip. Together, these values are used to calculate the average speed of a Trip.
	 *
	 * @return average speed of a trip based on number of kilometers over number of hours
	 * @author Declan Bruce
	 */
	public double calcAverageTripSpeed() {
		double tripSpeed = 0;
		if(!stopSequence.isEmpty()) {
			//create variables representing total Trip distance and total Trip time taken
			double totalTripDistance = 0;
			double totalTripTime = 0;

			//get the total distance of the Trip by adding up the distances between each Stop
			totalTripDistance = calcTotalDistance();

			try {
				//find the time taken in hours to get from the first Stop to the last Stop
				totalTripTime = calcTripTime();
			} catch (ParseException e) {
				TransitController.LOGGER.log(Level.SEVERE, e.getMessage() + "\n");
			}

			if (totalTripTime == 0 || totalTripDistance == 0) {
				//return 0 if we received poor values for hours or distance that would affect speed calculations
				tripSpeed = 0;
			} else {
				//return Δx/Δt for average speed
				tripSpeed = (totalTripDistance/totalTripTime);
			}
		}

		return tripSpeed;
	}

	/**
	 * This method inserts the given StopTime object into the Trip's list of StopTimes.
	 *
	 * @param stopTime the next StopTime to be inserted in this Trip's sequence of StopTimes
	 * @author Declan Bruce
	 */
	public void insertNextStopTime(StopTime stopTime){
		stopSequence.add(stopTime);
	}

	//getters
	public String getRouteID() { return routeID; }
	public String getServiceID() { return serviceID; }
	public String getTripID() { return tripID; }
	public String getTripHeadsign() { return tripHeadsign; }
	public ArrayList<StopTime> getStopSequence() { return stopSequence; }
	public int getDirectionID() { return directionID; }
	public String getBlockID() { return blockID; }
	public String getShapeID() { return shapeID; }
}