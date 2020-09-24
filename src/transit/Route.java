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
 *  This class describes a Route. A Route in the context of a GTFS system is simply a group of Trips which go along
 *  said Route at different times. Routes are relatively simply as the details is in the Trips in the Route and
 *  furthermore the Stops along those Trips.
 */
package transit;

import java.util.ArrayList;
import java.awt.Color;

public class Route {
	private String routeID;
	private String colorHex;
	private Color color;
	private ArrayList<String> tripsInRoute = new ArrayList<>();

	/**
	 * Basic constructor for a Route object.
	 * @param routeID String used to uniquely identify a Route object
	 * @param colorHex String representing the color of a Route using 6-digit hexadecimal
	 */
	public Route(String routeID, String colorHex) {
		this.routeID = routeID;
		this.colorHex = colorHex;
		this.color = createColor(colorHex);
	}

	/**
	 * Parse 6-digit hexadecimal into a Color object
	 * @param colorHex 6-digits of hexadecimal
	 * @return Color object for future Route displaying
	 * @author Isaiah Doran
	 */
	public static Color createColor(String colorHex){
		//Set default color to be black in the case of bad input
		Color color = new Color(0,0,0);

		//Verify that at least 6 hexadecimal digits were provided
		if(colorHex.length() == 6){
			color = Color.decode("#" + colorHex);
		}
		return color;
	}

	/**
	 * Override the equals method to only verify that each object field has the same value.
	 * @param obj Route object to be compared
	 * @return true if the two Route objects have the same field attributes
	 */
	@Override
	public boolean equals(Object obj){
		Route route = (Route)obj;
		boolean tripsInRouteMismatch = false;

		//verify each Route has the same trip_id(s)
		ArrayList<String> tripIDList = route.tripsInRoute;

		for(String tripID : this.tripsInRoute){
			//check to see if they both contain the same trip_id(s)
			if(!tripIDList.contains(tripID)){
				tripsInRouteMismatch = true;
			}
		}

		if(this.routeID.equals(route.routeID) && this.colorHex.equals(route.colorHex) && !tripsInRouteMismatch){
			return true;
		}
		return false;
	}


	/**
	 * This method inserts the given trip_id String into the Routes list of trip_id(s)
	 * @param tripID the ID of the Trip that is associated with this Route
	 * @author Declan Bruce
	 */
	public void insertTripID(String tripID){
		this.tripsInRoute.add(tripID);
	}

	//getters
	public String getRouteID() { return routeID; }
	public String getColorHex() { return colorHex; }
	public Color getColor() { return color; }
	public ArrayList<String> getTripsInRoute() { return tripsInRoute; }
}