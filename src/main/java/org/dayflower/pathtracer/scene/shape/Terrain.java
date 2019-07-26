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

//TODO: Add Javadocs.
public final class Terrain implements Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_FREQUENCY = 0;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_GAIN = 1;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_MAXIMUM = 3;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_MINIMUM = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_OCTAVES = 4;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 5;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float frequency;
	private final float gain;
	private final float maximum;
	private final float minimum;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Terrain(final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		this.frequency = frequency;
		this.gain = gain;
		this.minimum = minimum;
		this.maximum = maximum;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
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
	
//	TODO: Add Javadocs.
	public float getFrequency() {
		return this.frequency;
	}
	
//	TODO: Add Javadocs.
	public float getGain() {
		return this.gain;
	}
	
//	TODO: Add Javadocs.
	public float getMaximum() {
		return this.maximum;
	}
	
//	TODO: Add Javadocs.
	public float getMinimum() {
		return this.minimum;
	}
	
//	TODO: Add Javadocs.
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
	
//	TODO: Add Javadocs.
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.frequency), Float.valueOf(this.gain), Float.valueOf(this.maximum), Float.valueOf(this.minimum), Integer.valueOf(this.octaves));
	}
}