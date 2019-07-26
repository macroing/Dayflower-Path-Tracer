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
package org.dayflower.pathtracer.scene;

/**
 * A {@code Texture} is a model of a texture.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface Texture {
//	TODO: Add Javadocs.
	int RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	int RELATIVE_OFFSET_TYPE = 0;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, this {@code Texture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Texture} instance is emissive, {@code false} otherwise
	 */
	boolean isEmissive();
	
	/**
	 * Returns a {@code float} array representation of this {@code Texture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code Texture} instance
	 */
	float[] toArray();
	
	/**
	 * Returns the size of this {@code Texture} instance.
	 * 
	 * @return the size of this {@code Texture} instance
	 */
	int getSize();
	
	/**
	 * Returns the type of this {@code Texture} instance.
	 * 
	 * @return the type of this {@code Texture} instance
	 */
	int getType();
}