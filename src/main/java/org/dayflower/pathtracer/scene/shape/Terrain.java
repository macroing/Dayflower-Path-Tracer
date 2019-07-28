/**
 * Copyright 2015 - 2019 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.scene.shape;

import java.util.Objects;

import org.dayflower.pathtracer.scene.Shape;

/**
 * A {@link Shape} implementation that implements a noise-based terrain.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Terrain implements Shape {
	/**
	 * The relative offset of the Frequency parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int RELATIVE_OFFSET_FREQUENCY = 0;
	
	/**
	 * The relative offset of the Gain parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_GAIN = 1;
	
	/**
	 * The relative offset of the Maximum parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_MAXIMUM = 3;
	
	/**
	 * The relative offset of the Minimum parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_MINIMUM = 2;
	
	/**
	 * The relative offset of the Octaves parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_OCTAVES = 4;
	
	/**
	 * The size of a {@code Terrain} in the {@code float} array. The size is {@code 5}.
	 */
	public static final int SIZE = 5;
	
	/**
	 * The type number associated with a {@code Terrain}. The number is {@code 4}.
	 */
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float frequency;
	private final float gain;
	private final float maximum;
	private final float minimum;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Terrain} instance.
	 * 
	 * @param frequency the frequency to use
	 * @param gain the gain to use
	 * @param minimum the minimum to use
	 * @param maximum the maximum to use
	 * @param octaves the octaves to use
	 */
	public Terrain(final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		this.frequency = frequency;
		this.gain = gain;
		this.minimum = minimum;
		this.maximum = maximum;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Terrain} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Terrain}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Terrain} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Terrain}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Terrain)) {
			return false;
		} else if(Float.compare(this.frequency, Terrain.class.cast(object).frequency) != 0) {
			return false;
		} else if(Float.compare(this.gain, Terrain.class.cast(object).gain) != 0) {
			return false;
		} else if(Float.compare(this.maximum, Terrain.class.cast(object).maximum) != 0) {
			return false;
		} else if(Float.compare(this.minimum, Terrain.class.cast(object).minimum) != 0) {
			return false;
		} else if(this.octaves != Terrain.class.cast(object).octaves) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the frequency of this {@code Terrain} instance.
	 * 
	 * @return the frequency of this {@code Terrain} instance
	 */
	public float getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Returns the gain of this {@code Terrain} instance.
	 * 
	 * @return the gain of this {@code Terrain} instance
	 */
	public float getGain() {
		return this.gain;
	}
	
	/**
	 * Returns the maximum of this {@code Terrain} instance.
	 * 
	 * @return the maximum of this {@code Terrain} instance
	 */
	public float getMaximum() {
		return this.maximum;
	}
	
	/**
	 * Returns the minimum of this {@code Terrain} instance.
	 * 
	 * @return the minimum of this {@code Terrain} instance
	 */
	public float getMinimum() {
		return this.minimum;
	}
	
	/**
	 * Returns the octaves of this {@code Terrain} instance.
	 * 
	 * @return the octaves of this {@code Terrain} instance
	 */
	public int getOctaves() {
		return this.octaves;
	}
	
	/**
	 * Returns the size of this {@code Terrain} instance.
	 * 
	 * @return the size of this {@code Terrain} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code Terrain} instance.
	 * 
	 * @return the type of this {@code Terrain} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code Terrain} instance.
	 * 
	 * @return a hash code for this {@code Terrain} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.frequency), Float.valueOf(this.gain), Float.valueOf(this.maximum), Float.valueOf(this.minimum), Integer.valueOf(this.octaves));
	}
}