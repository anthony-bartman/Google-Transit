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
 * Observers under the Subject-Observer pattern must update their assigned components appropriately when notified
 * of a change by a Subject they are observing.
 */
package transit;

public interface Observer {

	/**
	 * This method is used by Observers to update program components relevant to themselves based on changes in
	 * Subjects they are observing at the time.
	 */
	void update();
}