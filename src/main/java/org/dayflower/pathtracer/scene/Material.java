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
package org.dayflower.pathtracer.scene;

/**
 * A {@code Material} denotes a type of material.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public enum Material {
	/**
	 * A {@code Material} that represents a clear coat material, such as the surface of a car.
	 * <p>
	 * It is implemented as a combination of {@link #LAMBERTIAN_DIFFUSE} and {@link #MIRROR}.
	 */
	CLEAR_COAT,
	
	/**
	 * A {@code Material} that represents the material of glass.
	 * <p>
	 * It is implemented as a combination of a refractive material and {@link #MIRROR}.
	 */
	GLASS,
	
	/**
	 * A {@code Material} that represents a Lambertian diffuse material.
	 */
	LAMBERTIAN_DIFFUSE,
	
	/**
	 * A {@code Material} that represents the material of a mirror.
	 */
	MIRROR,
	
	/**
	 * A {@code Material} that represents a Phong metal material.
	 */
	PHONG_METAL,
	
	/**
	 * A {@code Material} that represents water.
	 */
	WATER;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Material() {
		
	}
}