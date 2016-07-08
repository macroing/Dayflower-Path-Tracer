/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
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

import java.util.Objects;

/**
 * A {@code Shape} is a model of a shape.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class Shape {
	private final Surface surface;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Shape} instance.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface a {@link Surface} denoting the surface of this {@code Shape}
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	protected Shape(final Surface surface) {
		this.surface = Objects.requireNonNull(surface, "surface == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Surface} of this {@code Shape} instance.
	 * 
	 * @return the {@code Surface} of this {@code Shape} instance
	 */
	public final Surface getSurface() {
		return this.surface;
	}
}