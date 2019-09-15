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

import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;
import org.macroing.image4j.Color;
import org.macroing.math4j.Point2F;

/**
 * A {@code UVTexture} is a {@link Texture} implementation that shows the UV-coordinates (or texture coordinates) at a surface.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class UVTexture implements Texture {
	/**
	 * The size of a {@code UVTexture} in the {@code float} array. The size is {@code 2}.
	 */
	public static final int SIZE = 2;
	
	/**
	 * The type number associated with a {@code UVTexture}. The number is {@code 7}.
	 */
	public static final int TYPE = 7;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code UVTexture} instance.
	 */
	public UVTexture() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code UVTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code UVTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		final Point2F textureCoordinates = primitiveIntersection.getShapeIntersection().getTextureCoordinates();
		
		final float r = textureCoordinates.x;
		final float g = textureCoordinates.y;
		final float b = 0.0F;
		
		return new Color(r, g, b);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code UVTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code UVTexture} instance
	 */
	@Override
	public String toString() {
		return "new UVTexture()";
	}
	
	/**
	 * Compares {@code object} to this {@code UVTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code UVTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code UVTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code UVTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof UVTexture)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code UVTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code UVTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return true;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code UVTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code UVTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize()
		};
	}
	
	/**
	 * Returns the size of this {@code UVTexture} instance.
	 * 
	 * @return the size of this {@code UVTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code UVTexture} instance.
	 * 
	 * @return the type of this {@code UVTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code UVTexture} instance.
	 * 
	 * @return a hash code for this {@code UVTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash();
	}
}