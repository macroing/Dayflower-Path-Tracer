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
import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code FractionalBrownianMotionTexture} is a {@link Texture} implementation that uses fractional Brownian motion (fBm) to compute its image data.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class FractionalBrownianMotionTexture implements Texture {
	/**
	 * The relative offset of the Addend parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_ADDEND = 2;
	
	/**
	 * The relative offset of the Frequency parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_FREQUENCY = 4;
	
	/**
	 * The relative offset of the Gain parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int RELATIVE_OFFSET_GAIN = 5;
	
	/**
	 * The relative offset of the Multiplier parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_MULTIPLIER = 3;
	
	/**
	 * The relative offset of the Octaves parameter in the {@code float} array. The value is {@code 6}.
	 */
	public static final int RELATIVE_OFFSET_OCTAVES = 6;
	
	/**
	 * The size of a {@code FractionalBrownianMotionTexture} in the {@code float} array. The size is {@code 7}.
	 */
	public static final int SIZE = 7;
	
	/**
	 * The type number associated with a {@code FractionalBrownianMotionTexture}. The number is {@code 4}.
	 */
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color addend;
	private final Color multiplier;
	private final float frequency;
	private final float gain;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code FractionalBrownianMotionTexture} instance.
	 * <p>
	 * If either {@code addend} or {@code multiplier} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param addend the addend to use
	 * @param multiplier the multiplier to use
	 * @param frequency the frequency to use
	 * @param gain the gain to use
	 * @param octaves the octaves to use
	 * @throws NullPointerException thrown if, and only if, either {@code addend} or {@code multiplier} are {@code null}
	 */
	public FractionalBrownianMotionTexture(final Color addend, final Color multiplier, final float frequency, final float gain, final int octaves) {
		this.addend = Objects.requireNonNull(addend, "addend == null");
		this.multiplier = Objects.requireNonNull(multiplier, "multiplier == null");
		this.frequency = frequency;
		this.gain = gain;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the addend assigned to this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the addend assigned to this {@code FractionalBrownianMotionTexture} instance
	 */
	public Color getAddend() {
		return this.addend;
	}
	
	/**
	 * Returns a {@link Color} with the color of this {@code FractionalBrownianMotionTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code FractionalBrownianMotionTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		Objects.requireNonNull(primitiveIntersection, "primitiveIntersection == null");
		
		return new Color();
	}
	
	/**
	 * Returns the multiplier assigned to this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the multiplier assigned to this {@code FractionalBrownianMotionTexture} instance
	 */
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
		return String.format("new FractionalBrownianMotionTexture(%s, %s, %s, %s, %s)", this.addend, this.multiplier, Float.toString(this.frequency), Float.toString(this.gain), Integer.toString(this.octaves));
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
		} else if(Float.compare(this.frequency, FractionalBrownianMotionTexture.class.cast(object).frequency) != 0) {
			return false;
		} else if(Float.compare(this.gain, FractionalBrownianMotionTexture.class.cast(object).gain) != 0) {
			return false;
		} else if(this.octaves != FractionalBrownianMotionTexture.class.cast(object).octaves) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code FractionalBrownianMotionTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code FractionalBrownianMotionTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return !this.addend.isBlack() || !this.multiplier.isBlack();
	}
	
	/**
	 * Returns the frequency assigned to this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the frequency assigned to this {@code FractionalBrownianMotionTexture} instance
	 */
	public float getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Returns the gain assigned to this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the gain assigned to this {@code FractionalBrownianMotionTexture} instance
	 */
	public float getGain() {
		return this.gain;
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
			getFrequency(),
			getGain(),
			getOctaves()
		};
	}
	
	/**
	 * Returns the octaves assigned to this {@code FractionalBrownianMotionTexture} instance.
	 * 
	 * @return the octaves assigned to this {@code FractionalBrownianMotionTexture} instance
	 */
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
		return Objects.hash(this.addend, this.multiplier, Float.valueOf(this.frequency), Float.valueOf(this.gain), Integer.valueOf(this.octaves));
	}
}