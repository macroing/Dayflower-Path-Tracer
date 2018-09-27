/**
 * Copyright 2009 - 2018 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.math;

import java.util.Objects;

/**
 * A {@code Point2} denotes a point in 2D with coordinates of X and Y.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Point2 {
	/**
	 * The X-coordinate.
	 */
	public final float x;
	
	/**
	 * The Y-coordinate.
	 */
	public final float y;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Point2} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Point2(0.0F, 0.0F)}.
	 */
	public Point2() {
		this(0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Point2} based on {@code x} and {@code y}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 */
	public Point2(final float x, final float y) {
		this.x = x;
		this.y = y;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Point2} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Point2}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Point2} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Point2}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Point2)) {
			return false;
		} else if(Float.compare(this.x, Point2.class.cast(object).x) != 0) {
			return false;
		} else if(Float.compare(this.y, Point2.class.cast(object).y) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code Point2} instance.
	 * 
	 * @return a hash code for this {@code Point2} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y));
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Point2} instance.
	 * 
	 * @return a {@code String} representation of this {@code Point2} instance
	 */
	@Override
	public String toString() {
		return String.format("Point2: [X=%s], [Y=%s]", Float.toString(this.x), Float.toString(this.y));
	}
}