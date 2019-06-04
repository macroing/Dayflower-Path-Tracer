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

import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code SurfaceNormalTexture} is a {@link Texture} implementation that shows the surface normal at a surface.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SurfaceNormalTexture implements Texture {
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
	 * Returns {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise
	 */
	public boolean isTangentSpace() {
		return this.isTangentSpace;
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