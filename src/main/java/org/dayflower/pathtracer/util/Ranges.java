/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.util;

/**
 * A class that consists exclusively of static methods that performs clamping and other range-specific operations.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Ranges {
	private Ranges() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a clamped version of {@code value}.
	 * <p>
	 * The clamped version will be at least as low as {@code minimum}, or at most as high as {@code maximum}.
	 * 
	 * @param value the {@code float} value to clamp
	 * @param minimum the minimum {@code float} value to be returned
	 * @param maximum the maximum {@code float} value to be returned
	 * @return a clamped version of {@code value}
	 */
	public static float clamp(final float value, final float minimum, final float maximum) {
		if(value < minimum) {
			return minimum;
		} else if(value > maximum) {
			return maximum;
		} else {
			return value;
		}
	}
	
	/**
	 * Returns {@code value}, but only if it is within the range of {@code minimum} (inclusive) and {@code maximum} (inclusive).
	 * <p>
	 * If it is not within said range, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * Calling this method is equivalent to calling {@code Ranges.requireRange(value, minimum, maximum, null)}.
	 * 
	 * @param value the value to verify
	 * @param minimum the minimum value allowed (inclusive)
	 * @param maximum the maximum value allowed (inclusive)
	 * @return {@code value}, but only if it is within the range of {@code minimum} (inclusive) and {@code maximum} (inclusive)
	 * @throws IllegalArgumentException thrown if, and only if, {@code value} is less than {@code minimum} or greater than {@code maximum}
	 */
	public static float requireRange(final float value, final float minimum, final float maximum) {
		return requireRange(value, minimum, maximum, null);
	}
	
	/**
	 * Returns {@code value}, but only if it is within the range of {@code minimum} (inclusive) and {@code maximum} (inclusive).
	 * <p>
	 * If it is not within said range, an {@code IllegalArgumentException} will be thrown.
	 * <p>
	 * The {@code message} parameter will be the detail message of the thrown {@code IllegalArgumentException}. It may be {@code null}.
	 * 
	 * @param value the value to verify
	 * @param minimum the minimum value allowed (inclusive)
	 * @param maximum the maximum value allowed (inclusive)
	 * @param message the detail message to the {@code IllegalArgumentException}
	 * @return {@code value}, but only if it is within the range of {@code minimum} (inclusive) and {@code maximum} (inclusive)
	 * @throws IllegalArgumentException thrown if, and only if, {@code value} is less than {@code minimum} or greater than {@code maximum}
	 */
	public static float requireRange(final float value, final float minimum, final float maximum, final String message) {
		if(value >= minimum && value <= maximum) {
			return value;
		}
		
		throw new IllegalArgumentException(message);
	}
}