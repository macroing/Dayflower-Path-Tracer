/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.pathtracer.scene;

/**
 * A {@code CameraPredicate} is used for collision detection.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface CameraPredicate {
	/**
	 * Returns a {@code boolean} array with length {@code 3} for each X-, Y- and Z-axis that tells the {@link Camera} whether it can move in a given direction or not.
	 * 
	 * @param oldX the old X-coordinate
	 * @param oldY the old Y-coordinate
	 * @param oldZ the old Z-coordinate
	 * @param newX the new X-coordinate
	 * @param newY the new Y-coordinate
	 * @param newZ the new Z-coordinate
	 * @return a {@code boolean} array with length {@code 3} for each X-, Y- and Z-axis that tells the {@code Camera} whether it can move in a given direction or not
	 */
	boolean[] test(final float oldX, final float oldY, final float oldZ, final float newX, final float newY, final float newZ);
}