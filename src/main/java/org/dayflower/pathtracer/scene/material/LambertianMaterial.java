/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
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
 * A {@code LambertianMaterial} is a {@link Material} implementation that represents a Lambertian diffuse material.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class LambertianMaterial implements Material {
	/**
	 * The type number associated with a {@code LambertianMaterial}. The number is {@code 3}.
	 */
	public static final int TYPE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code LambertianMaterial} instance.
	 */
	public LambertianMaterial() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code LambertianMaterial} instance.
	 * 
	 * @return a {@code String} representation of this {@code LambertianMaterial} instance
	 */
	@Override
	public String toString() {
		return "new LambertianMaterial()";
	}
	
	/**
	 * Compares {@code object} to this {@code LambertianMaterial} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code LambertianMaterial}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code LambertianMaterial} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code LambertianMaterial}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof LambertianMaterial)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the type of this {@code LambertianMaterial} instance.
	 * 
	 * @return the type of this {@code LambertianMaterial} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code LambertianMaterial} instance.
	 * 
	 * @return a hash code for this {@code LambertianMaterial} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash();
	}
}