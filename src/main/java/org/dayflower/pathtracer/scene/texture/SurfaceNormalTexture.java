/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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
	 * Returns {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code SurfaceNormalTexture} shows the surface normal in tangent space, {@code false} otherwise
	 */
	public boolean isTangentSpace() {
		return this.isTangentSpace;
	}
}