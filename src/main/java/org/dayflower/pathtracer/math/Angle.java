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

import static org.dayflower.pathtracer.math.Math2.PI_MULTIPLIED_BY_TWO;
import static org.dayflower.pathtracer.math.Math2.toDegrees;
import static org.dayflower.pathtracer.math.Math2.toRadians;

import java.util.Objects;

/**
 * An {@code Angle} encapsulates angles in forms such as degrees and radians.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Angle {
	/**
	 * An {@code Angle} created as {@code Angle.degrees(0.0F)}.
	 */
	public static final Angle DEGREES_0 = degrees(0.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(40.0F)}.
	 */
	public static final Angle DEGREES_40 = degrees(40.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(50.0F)}.
	 */
	public static final Angle DEGREES_50 = degrees(50.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(60.0F)}.
	 */
	public static final Angle DEGREES_60 = degrees(60.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(70.0F)}.
	 */
	public static final Angle DEGREES_70 = degrees(70.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(80.0F)}.
	 */
	public static final Angle DEGREES_80 = degrees(80.0F);
	
	/**
	 * An {@code Angle} created as {@code Angle.degrees(90.0F)}.
	 */
	public static final Angle DEGREES_90 = degrees(90.0F);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The angle in degrees.
	 */
	public final float degrees;
	
	/**
	 * The angle in radians.
	 */
	public final float radians;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Angle(final float degrees, final float radians) {
		this.degrees = degrees;
		this.radians = radians;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Angle add(final Angle angle) {
		return add(angle, 0.0F, 360.0F);
	}
	
	public Angle add(final Angle angle, final float minimumDegrees, final float maximumDegrees) {
		return degrees(degrees(getDegrees(), minimumDegrees, maximumDegrees).degrees + degrees(angle.degrees, minimumDegrees, maximumDegrees).degrees, minimumDegrees, maximumDegrees);
	}
	
	/**
	 * Compares {@code object} to this {@code Angle} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Angle}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Angle} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Angle}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Angle)) {
			return false;
		} else if(Float.compare(this.degrees, Angle.class.cast(object).degrees) != 0) {
			return false;
		} else if(Float.compare(this.radians, Angle.class.cast(object).radians) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the angle in degrees.
	 * 
	 * @return the angle in degrees
	 */
	public float getDegrees() {
		return this.degrees;
	}
	
	/**
	 * Returns the angle in radians.
	 * 
	 * @return the angle in radians
	 */
	public float getRadians() {
		return this.radians;
	}
	
	/**
	 * Returns a hash code for this {@code Angle} instance.
	 * 
	 * @return a hash code for this {@code Angle} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.degrees), Float.valueOf(this.radians));
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Angle} instance.
	 * 
	 * @return a {@code String} representation of this {@code Angle} instance
	 */
	@Override
	public String toString() {
		return String.format("Angle.degrees(%s)", Float.toString(this.degrees));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code Angle} instance based on an angle in degrees.
	 * 
	 * @param degrees the angle in degrees
	 * @return a new {@code Angle} instance based on an angle in degrees
	 */
	public static Angle degrees(final float degrees) {
		return degrees(degrees, 0.0F, 360.0F);
	}
	
	/**
	 * Returns a new {@code Angle} instance based on an angle in degrees.
	 * 
	 * @param degrees the angle in degrees
	 * @param minimumDegrees the minimum value for the degrees
	 * @param maximumDegrees the maximum value for the degrees
	 * @return a new {@code Angle} instance based on an angle in degrees
	 */
	public static Angle degrees(final float degrees, final float minimumDegrees, final float maximumDegrees) {
		final float degrees0 = doToInterval(degrees, minimumDegrees, maximumDegrees);
		
		return new Angle(degrees0, toRadians(degrees0));
	}
	
	/**
	 * Returns a new {@code Angle} instance based on an angle in radians.
	 * 
	 * @param radians the angle in radians
	 * @return a new {@code Angle} instance based on an angle in radians
	 */
	public static Angle radians(final float radians) {
		final float radians0 = doToInterval(radians, 0.0F, PI_MULTIPLIED_BY_TWO);
		
		return new Angle(toDegrees(radians0), radians0);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float doToInterval(final float value, final float minimumValue, final float maximumValue) {
		float value0 = value;
		
		while(value0 < minimumValue || value0 > maximumValue) {
			if(value0 < minimumValue) {
				value0 = maximumValue - (minimumValue - value0);
			} else if(value0 > maximumValue) {
				value0 = minimumValue + (value0 - maximumValue);
			}
		}
		
		return value0;
	}
}