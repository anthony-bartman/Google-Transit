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
 * Subjects under the Subject-Observer pattern need to know what Observers are currently observing them. This is
 * because Subjects will notify all of their current Observers whenever significant changes are made
 */
package transit;

public class Subject {
	public Subject(){ }

	/**
	 * This method adds a new Observer to the Subject's current list of Observers.
	 * @param observer new Observer that would like to observer the Subject
	 */
	public void attach(Observer observer){ }

	/**
	 * This method removes a previous Observer from the Subject's current list of Observers.
	 * @param observer previous Observer that no longer needs to observe the Subject
	 */
	public void detach(Observer observer){ }

	/**
	 * This method calls the "update()" method for all of the Observers currently observing the Subject. This
	 * causes the Observers to acknowledge any significant changes recently made to/by the Subject.
	 */
	public void notifyObservers(){ }
}