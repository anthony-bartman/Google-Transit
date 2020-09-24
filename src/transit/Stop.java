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
 *  This class describes a Stop. A Stop in the context of a GTFS system is simply a latitude/longitude location that
 *  which may be visited at any unspecific time during a Trip. The time at which each Stop in a Trip is visited is
 *  specified in the imported "stop_times.txt" file.
 */
package transit;

public class Stop {
	private String stopID;
	private String name;
	private String desc;
	private double latitude;
	private double longitude;

	/**
	 * Basic constructor for a Stop object.
	 * TODO: describe each of this objects attribute in minor detail
	 * @param stopID
	 * @param name
	 * @param desc
	 * @param latitude double for coordinate placement
	 * @param longitude double for coordinate placement
	 */
	public Stop(String stopID, String name, String desc, double latitude, double longitude){
		this.stopID = stopID;
		this.name = name;
		this.desc = desc;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * This method updates a Stop's current location to new coordinates.
	 * @param latitude new latitude
	 * @param longitude new longitude
	 */
	public void updateStopLocation(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	//getters
	public String getStopID() { return stopID; }
	public String getName() { return name; }
	public String getDesc() { return desc; }
	public double getLatitude() { return latitude; }
	public double getLongitude() { return longitude; }
}