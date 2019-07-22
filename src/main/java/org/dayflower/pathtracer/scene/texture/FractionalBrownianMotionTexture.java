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
package org.dayflower.pathtracer.scene.texture;

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code FractionalBrownianMotionTexture} is a {@link Texture} implementation that uses fractional Brownian motion (fBm) to compute its image data.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class FractionalBrownianMotionTexture implements Texture {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_ADDEND = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_MULTIPLIER = 3;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_OCTAVES = 6;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_PERSISTENCE = 4;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SCALE = 5;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 7;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 5;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color addend;
	private final Color multiplier;
	private final float persistence;
	private final float scale;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public FractionalBrownianMotionTexture(final Color addend, final Color multiplier, final float persistence, final float scale, final int octaves) {
		this.addend = Objects.requireNonNull(addend, "addend == null");
		this.multiplier = Objects.requireNonNull(multiplier, "multiplier == null");
		this.persistence = persistence;
		this.scale = scale;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Color getAddend() {
		return this.addend;
	}
	
//	TODO: Add Javadocs.
	public Color getMultiplier() {
		return this.multiplier;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code FractionalBrownianMotionTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new FractionalBrownianMotionTexture(%s, %s, %s, %s, %s)", this.addend, this.multiplier, Float.toString(this.persistence), Float.toString(this.scale), Integer.toString(this.octaves));
	}
	
	/**
	 * Compares {@code object} to this {@code FractionalBrownianMotionTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code FractionalBrownianMotionTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code FractionalBrownianMotionTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code FractionalBrownianMotionTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof FractionalBrownianMotionTexture)) {
			return false;
		} else if(!Objects.equals(this.addend, FractionalBrownianMotionTexture.class.cast(object).addend)) {
			return false;
		} else if(!Objects.equals(this.multiplier, FractionalBrownianMotionTexture.class.cast(object).multiplier)) {
			return false;
		} else if(Float.compare(this.persistence, FractionalBrownianMotionTexture.class.cast(object).persistence) != 0) {
			return false;
		} else if(Float.compare(this.scale, FractionalBrownianMotionTexture.class.cast(object).scale) != 0) {
			return false;
		} else if(this.octaves != FractionalBrownianMotionTexture.class.cast(object).octaves) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs.
	public float getPersistence() {
		return this.persistence;
	}
	
//	TODO: Add Javadocs.
	public float getScale() {
		return this.scale;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code FractionalBrownianMotionTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			getAddend().multiply(255.0F).toRGB(),
			getMultiplier().multiply(255.0F).toRGB(),
			getPersistence(),
			getScale(),
			getOctaves()
		};
	}
	
//	TODO: Add Javadocs.
	public int getOctaves() {
		return this.octaves;
	}
	
	/**
	 * Returns the size of this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the size of this {@code FractionalBrownianMotionTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the type of this {@code FractionalBrownianMotionTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return a hash code for this {@code FractionalBrownianMotionTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.addend, this.multiplier, Float.valueOf(this.persistence), Float.valueOf(this.scale), Integer.valueOf(this.octaves));
	}
}