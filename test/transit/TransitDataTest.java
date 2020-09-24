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
 *
 * This class hosts all tests for the "TransitData" class. TransitData hosts all program data and much of the
 * parsing and data interpretation of the program. Tests here include proper parsing of imported text files,
 * proper searching of data structures and so on.
 */
package transit;

    import org.junit.jupiter.api.Test;

    import java.awt.*;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.util.ArrayList;
    import static org.junit.jupiter.api.Assertions.*;

class TransitDataTest {

    /**
     * Test the "parseRoutes()" method to see if it throws an Exception when given an empty routes.txt file.
     *
     * @author Declan Bruce
     */
    @Test
    void emptyRoutesTest() throws FileNotFoundException {
        //create a File object using the empty routes.txt text files
        File file = new File("./data_files/GTFS_MCTS_Test/empty_routes.txt");

        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //run parseRoutes using the empty text file
        transitData.parseRoutes(file);
    }

    /**
     * Test the "parseRoutes()" method to see if any Routes are in the ArrayList of "allRoutes" when the method is only
     * given one invalid line representing a single Route.
     *
     * @author Declan Bruce
     */
    @Test
    void oneInvalidRouteTest() throws FileNotFoundException {
        //create a File object using the empty_routes.txt text file
        File file = new File("./data_files/GTFS_MCTS_Test/one_invalid_route.txt");

        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //run parseRoutes using a file with a single invalid line
        transitData.parseRoutes(file);

        //check to see if there are no routes created after parsing a single invalid Route
        assertTrue(transitData.getAllRoutes().isEmpty());
    }

    /**
     * Test the "parseStops()" method to see if Stop objects are successfully created on normal file import.
     *
     * @author Isaiah Doran
     */
    @Test
    void parseStopsTest() throws FileNotFoundException {
        //create a File using a regular stops.txt file
        File file = new File("./data_files/GTFS_MCTS/stops.txt");

        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //run parseStops using the normal text file
        transitData.parseStops(file);

        //ensure that there are Stops created from a normal stops.txt file
        assertFalse(transitData.getAllStops().isEmpty(), "Stops should have been successfully created.");
    }

    /**
     * Test the "parseStops()" method to see if it throws an Exceptions when given an empty stops.txt file.
     *
     * @author Isaiah Doran
     */
    @Test
    void emptyStopsTest() throws FileNotFoundException {
        //create a File object using the empty stops.txt file
        File file = new File("./data_files/GTFS_MCTS_Test/empty_stops.txt");

        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //run parseStops using the empty text file
        transitData.parseStops(file);
    }

    /**
     * This test creates several fake Stop objects and injects them into TransitData. From there, the result of the
     * search from the overall list of Stops is compared to the Stop that was added to TransitData in the first place.
     *
     * @author Declan Bruce
     */
    @Test
    void testSearchStops() {
        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //create several fake Stops and add them to the list of all Stops
        Stop stop1 = new Stop("stop_1", "First", "desc", 1,1);
        Stop stop2 = new Stop("stop_2","Second","desc",2,2);
        Stop stop3 = new Stop("stop_3", "Third", "desc", 3,3);
        Stop stop4 = new Stop("stop_4","Fourth","desc",4,4);
        transitData.getAllStops().add(stop1);
        transitData.getAllStops().add(stop2);
        transitData.getAllStops().add(stop3);
        transitData.getAllStops().add(stop4);

        //see what Stop searching returns and compare Stops appropriately
        Stop foundStop = transitData.searchStops(stop1.getStopID());
        assertEquals(foundStop, stop1);
    }

    /**
     * This is a test of Feature 5 - Search for a stop by stop_id and display all route_id(s) that contain the stop
     * Success is found by making sure the expected Route is in the list of Routes found.
     *
     * @author Declan Bruce
     */
    @Test
    void testFindRoutesWithStop() throws FileNotFoundException {
        //create a File using a regular stops.txt file
        File routeFile = new File("./data_files/GTFS_LAX/routes.txt");
        File tripFile = new File("./data_files/GTFS_LAX/trips.txt");
        File stopFile = new File("./data_files/GTFS_LAX/stops.txt");
        File stopTimesFile = new File("./data_files/GTFS_LAX/stop_times.txt");

        //obtain common data store
        TransitData transitData = TransitData.getInstance();

        //parse the La Crosse set of GTFS files
        transitData.parseRoutes(routeFile);
        transitData.parseTrips(tripFile);
        transitData.parseStops(stopFile);
        transitData.parseStopTimes(stopTimesFile);

        //obtain the list of Routes that contain the specified stop_id (Stop #424)
        ArrayList<Route> foundRoutes = transitData.findRoutesWithStop("424");

        //Stop "424" is in Trip "Rt10AMTrp1" which is along Route "Route10"
        Route expectedRoute = new Route("Route10","F7ACBB");

        //check to make sure that Route "Route10" is included in the list of Routes found
        assertTrue(foundRoutes.contains(expectedRoute));
    }

    /**
     * This test is used to verify that the color in hexadecimal of each Route is being parsed correctly into a Color
     * object.
     *
     * @author Isaiah Doran
     */
    @Test
    public void testRouteColor(){
       Route route = new Route("1", "FF0096");
       Color color = Color.decode("#" + route.getColorHex());
       assertEquals(color, route.getColor());
    }

    /**
     * This method verifies the searchTrips method to see the list will contain the indicated trip_id.
     *
     * @author Bartman
     */
    @Test
    public void testSearchTrips(){
        //Uses our data storing object
        TransitData transitData = TransitData.getInstance();

        //Creates 6 fake trips to choose from
        Trip trip1 = new Trip("rt1", "1", "trip1", "one",1,"block1","one");
        Trip trip2 = new Trip("rt2", "2", "trip2", "two",2,"block2","two");
        Trip trip3 = new Trip("rt3", "3", "trip3", "three",3,"block3","three");
        Trip trip4 = new Trip("rt4", "4", "trip4", "four",4,"block4","four");
        Trip trip5 = new Trip("rt5", "5", "trip5", "five",5,"block5","five");
        Trip trip6 = new Trip("rt6", "6", "trip6", "six",6,"block6","six");

        //Adds fake trips to arraylist
        transitData.getAllTrips().add(trip1);
        transitData.getAllTrips().add(trip2);
        transitData.getAllTrips().add(trip3);
        transitData.getAllTrips().add(trip4);
        transitData.getAllTrips().add(trip5);
        transitData.getAllTrips().add(trip6);

        //Verifies searchTrip works, by returning searched trip
        Trip foundTrip = transitData.searchTrips(trip3.getTripID());
        assertEquals(foundTrip, trip3);
    }

    /**
     * This method verifies the searchTrips method to see the list will contain
     * and indicated trip_id
     *
     * @author Bartman
     */
    @Test
    public void testSearchRoutes(){
        //Uses our data storing object
        TransitData transitData = TransitData.getInstance();

        //Creates 6 fake routes to choose from
        Route route1 = new Route("rt1", "0xFFFFFF");
        Route route2 = new Route("rt2", "0xFFFFFF");
        Route route3 = new Route("rt3", "0xFFFFFF");

        //Adds fake routes to arraylist
        transitData.getAllRoutes().add(route1);
        transitData.getAllRoutes().add(route2);
        transitData.getAllRoutes().add(route3);

        //Verifies searchTrip works, by returning searched trip
        Route foundRoute = transitData.searchRoutes(route2.getRouteID());
        assertEquals(foundRoute, route2);
    }
}