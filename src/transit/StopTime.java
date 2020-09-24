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
 *  This class describes a StopTime. A StopTime in the context of a GTFS system is when a Stop is visited in a Trip.
 *  StopTimes are used to determine things such as what order Stops are visited in a Trip as well as what Stops are
 *  even visited in a Trip. Furthermore, attributes such as Trip distance and speed can be determined with StopTimes.
 */
package transit;

public class StopTime {
	private String tripID;
	private String arrivalTime;
	private String departureTime;
	private String stopID;
	private String stopSequence;
	private String stopHeadsign;
	private String pickupType;
	private String dropoffType;

	/**
	 * Basic constructor for a StopTime object.
	 * TODO: describe each of this objects attribute in minor detail
	 * @param tripID
	 * @param arrivalTime
	 * @param departureTime
	 * @param stopID
	 * @param stopSequence
	 * @param stopHeadsign
	 * @param pickupType
	 * @param dropoffType
	 */
	public StopTime(String tripID, String arrivalTime, String departureTime, String stopID,
					String stopSequence, String stopHeadsign, String pickupType,
					String dropoffType) {
		this.tripID = tripID;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.stopID = stopID;
		this.stopSequence = stopSequence;
		this.stopHeadsign = stopHeadsign;
		this.pickupType = pickupType;
		this.dropoffType = dropoffType;
	}

	//getters
	public String getTripID() { return tripID; }
	public String getArrivalTime() { return arrivalTime; }
	public String getDepartureTime() { return departureTime; }
	public String getStopID() { return stopID; }
	public String getStopSequence() { return stopSequence; }
	public String getStopHeadsign() { return stopHeadsign; }
	public String getPickupType() { return pickupType; }
	public String getDropoffType() { return dropoffType; }
}