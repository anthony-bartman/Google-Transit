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
 * This class hosts all tests for the "Trip" class. Trip acts as a specific collection of Stops traversed in a certain
 * order at certain times. Trips effectively act as a specific way of traveling along a certain Route. Tests here
 * include calculating the speed or distance of a Trip as well as finding future Trips.
 */
package transit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    /**
     * This test will calculate the trip speed of a fake stop time and sequence to see
     * if it returns the correct values.
     *
     * @Author Bartman
     */
    @Test
    void calcAverageTripSpeed() {
        //Uses our data storing object
        TransitData transitData = TransitData.getInstance();

        //Fake trip
        Trip trip1 = new Trip("rt1", "1", "trip1", "1", 1, "1", "1");

        //Creates fake stop sequence and fake stops
        StopTime stopTime1 = new StopTime("trip1", "12:00:00", "12:01:00", "11", "1", "n/a", "n/a", "n/a");
        StopTime stopTime2 = new StopTime("trip1", "12:01:00", "12:02:00", "12", "2", "n/a", "n/a", "n/a");
        StopTime stopTime3 = new StopTime("trip1", "12:02:00", "12:03:00", "13", "3", "n/a", "n/a", "n/a");
        StopTime stopTime4 = new StopTime("trip1", "12:03:00", "12:04:00", "14", "4", "n/a", "n/a", "n/a");
        StopTime stopTime5 = new StopTime("trip1", "12:04:00", "12:05:00", "15", "5", "n/a", "n/a", "n/a");
        StopTime stopTime6 = new StopTime("trip1", "12:05:00", "12:06:00", "16", "6", "n/a", "n/a", "n/a");
        Stop stop1 = new Stop("11","1","1",1.1,1.1);
        Stop stop2 = new Stop("12","2","2",2.2,2.2);
        Stop stop3 = new Stop("13","3","3",3.3,3.3);
        Stop stop4 = new Stop("14","4","4",4.4,4.4);
        Stop stop5 = new Stop("15","5","5",5.5,5.5);
        Stop stop6 = new Stop("16","6","6",6.6,6.6);

        //Add stoptimes to trip sequence
        trip1.getStopSequence().add(stopTime1);
        trip1.getStopSequence().add(stopTime2);
        trip1.getStopSequence().add(stopTime3);
        trip1.getStopSequence().add(stopTime4);
        trip1.getStopSequence().add(stopTime5);
        trip1.getStopSequence().add(stopTime6);

        //Add to transitData
        transitData.getAllTrips().add(trip1);
        transitData.getAllStops().add(stop1);
        transitData.getAllStops().add(stop2);
        transitData.getAllStops().add(stop3);
        transitData.getAllStops().add(stop4);
        transitData.getAllStops().add(stop5);
        transitData.getAllStops().add(stop6);

        int averageSpeed = (int) trip1.calcAverageTripSpeed();
        int theoreticalSpeed = 8637;
        assertEquals(theoreticalSpeed,averageSpeed);
    }
}