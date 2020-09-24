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
 * The Bus class is not yet fully implemented.
 */
package transit;

public class Bus {
	private String busID;
	private Route currentRoute;
	private double latitude;
	private double longitude;

	/**
	 * Basic constructor for a Bus object.
	 * TODO: describe each of this objects attribute in minor detail
	 * @param busID
	 * @param currentRoute
	 * @param latitude
	 * @param longitude
	 */
	public Bus(String busID, Route currentRoute, double latitude, double longitude){
		this.busID = busID;
		this.currentRoute = currentRoute;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Changes the Bus's Route to a new Route.
	 * @param route the Bus's new Route
	 */
	public void updateCurrentRoute(Route route){
		currentRoute = route;
	}
}