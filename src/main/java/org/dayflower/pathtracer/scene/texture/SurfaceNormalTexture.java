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
import org.macroing.math4j.Vector3F;

/**
 * A {@code SurfaceNormalTexture} is a {@link Texture} implementation that shows the surface normal at a surface.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SurfaceNormalTexture implements Texture {
	/**
	 * The relative offset of the Is Tangent Space parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_IS_TANGENT_SPACE = 2;
	
	/**
	 * The size of a {@code SurfaceNormalTexture} in the {@code float} array. The size is {@code 3}.
	 */
	public static final int SIZE = 3;
	
	/**
	 * The type number associated with a {@code SurfaceNormalTexture}. The number is {@code 6}.
	 */
	public static final int TYPE = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final boolean isTangentSpace;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code SurfaceNormalTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new SurfaceNormalTexture(false)
	 * }
	 * </pre>
	 */
	public SurfaceNormalTexture() {
		this(false);
	}
	
	/**
	 * Constructs a new {@code SurfaceNormalTexture} instance.
	 * 
	 * @param isTangentSpace {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise
	 */
	public SurfaceNormalTexture(final boolean isTangentSpace) {
		this.isTangentSpace = isTangentSpace;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code SurfaceNormalTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code SurfaceNormalTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		final Vector3F surfaceNormal = primitiveIntersection.getShapeIntersection().getSurfaceNormal();
		
		final float r = (surfaceNormal.x + 1.0F) * 0.5F;
		final float g = (surfaceNormal.y + 1.0F) * 0.5F;
		final float b = (surfaceNormal.z + 1.0F) * 0.5F;
		
		return new Color(r, g, b);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code SurfaceNormalTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code SurfaceNormalTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new SurfaceNormalTexture(%s)", Boolean.toString(this.isTangentSpace));
	}
	
	/**
	 * Compares {@code object} to this {@code SurfaceNormalTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code SurfaceNormalTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code SurfaceNormalTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code SurfaceNormalTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof SurfaceNormalTexture)) {
			return false;
		} else if(this.isTangentSpace != SurfaceNormalTexture.class.cast(object).isTangentSpace) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code SurfaceNormalTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code SurfaceNormalTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return true;
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise
	 */
	public boolean isTangentSpace() {
		return this.isTangentSpace;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code SurfaceNormalTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code SurfaceNormalTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			isTangentSpace() ? 1.0F : 0.0F
		};
	}
	
	/**
	 * Returns the size of this {@code SurfaceNormalTexture} instance.
	 * 
	 * @return the size of this {@code SurfaceNormalTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code SurfaceNormalTexture} instance.
	 * 
	 * @return the type of this {@code SurfaceNormalTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code SurfaceNormalTexture} instance.
	 * 
	 * @return a hash code for this {@code SurfaceNormalTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Boolean.valueOf(this.isTangentSpace));
	}
}