/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.scene.material;

import java.util.Objects;

import org.dayflower.pathtracer.scene.Material;

/**
 * A {@code PhongMaterial} is a {@link Material} implementation that represents a Phong material that can be used for metallic or plastic surfaces.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class PhongMaterial implements Material {
	/**
	 * The type number associated with a {@code PhongMaterial}. The number is {@code 4}.
	 */
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float exponent;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code PhongMaterial} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new PhongMaterial(20.0F)}.
	 */
	public PhongMaterial() {
		this(20.0F);
	}
	
	/**
	 * Constructs a new {@code PhongMaterial} instance.
	 * 
	 * @param exponent the exponent to use
	 */
	public PhongMaterial(final float exponent) {
		this.exponent = exponent;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code PhongMaterial} instance.
	 * 
	 * @return a {@code String} representation of this {@code PhongMaterial} instance
	 */
	@Override
	public String toString() {
		return String.format("new PhongMaterial(%s)", Float.toString(this.exponent));
	}
	
	/**
	 * Compares {@code object} to this {@code PhongMaterial} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code PhongMaterial}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code PhongMaterial} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code PhongMaterial}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof PhongMaterial)) {
			return false;
		} else if(Float.compare(this.exponent, PhongMaterial.class.cast(object).exponent) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the exponent of this {@code PhongMaterial} instance.
	 * 
	 * @return the exponent of this {@code PhongMaterial} instance
	 */
	public float getExponent() {
		return this.exponent;
	}
	
	/**
	 * Returns the type of this {@code PhongMaterial} instance.
	 * 
	 * @return the type of this {@code PhongMaterial} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code PhongMaterial} instance.
	 * 
	 * @return a hash code for this {@code PhongMaterial} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.exponent));
	}
}