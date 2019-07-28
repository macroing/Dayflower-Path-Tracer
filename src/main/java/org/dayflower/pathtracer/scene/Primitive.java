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

import java.util.Objects;

/**
 * A {@code Primitive} represents a primitive that is associated with a {@link Shape} and a {@link Surface}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Primitive {
	/**
	 * The relative offset of the Shape Offset parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_SHAPE_OFFSET = 1;
	
	/**
	 * The relative offset of the Shape Type parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_SHAPE_TYPE = 0;
	
	/**
	 * The relative offset of the Surface Offset parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_SURFACE_OFFSET = 2;
	
	/**
	 * The size of a {@code Primitive} in the {@code float} array. The size is {@code 3}.
	 */
	public static final int SIZE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Shape shape;
	private Surface surface;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Primitive} instance.
	 * <p>
	 * If either {@code shape} or {@code surface} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param shape the {@link Shape} to use
	 * @param surface the {@link Surface} to use
	 * @throws NullPointerException thrown if, and only if, either {@code shape} or {@code surface} are {@code null}
	 */
	public Primitive(final Shape shape, final Surface surface) {
		this.shape = Objects.requireNonNull(shape, "shape == null");
		this.surface = Objects.requireNonNull(surface, "surface == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Shape} assigned to this {@code Primitive} instance.
	 * 
	 * @return the {@code Shape} assigned to this {@code Primitive} instance
	 */
	public Shape getShape() {
		return this.shape;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Primitive} instance.
	 * 
	 * @return a {@code String} representation of this {@code Primitive} instance
	 */
	@Override
	public String toString() {
		return String.format("new Primitive(%s, %s)", this.shape, this.surface);
	}
	
	/**
	 * Returns the {@link Surface} assigned to this {@code Primitive} instance.
	 * 
	 * @return the {@code Surface} assigned to this {@code Primitive} instance
	 */
	public Surface getSurface() {
		return this.surface;
	}
	
	/**
	 * Compares {@code object} to this {@code Primitive} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Primitive}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Primitive} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Primitive}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Primitive)) {
			return false;
		} else if(!Objects.equals(this.shape, Primitive.class.cast(object).shape)) {
			return false;
		} else if(!Objects.equals(this.surface, Primitive.class.cast(object).surface)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code Primitive} instance.
	 * 
	 * @return a hash code for this {@code Primitive} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.shape, this.surface);
	}
	
	/**
	 * Sets the {@link Shape} for this {@code Primitive} instance.
	 * <p>
	 * If {@code shape} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param shape the new {@code Shape}
	 * @throws NullPointerException thrown if, and only if, {@code shape} is {@code null}
	 */
	public void setShape(final Shape shape) {
		this.shape = Objects.requireNonNull(shape, "shape == null");
	}
	
	/**
	 * Sets the {@link Surface} for this {@code Primitive} instance.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface the new {@code Surface}
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	public void setSurface(final Surface surface) {
		this.surface = Objects.requireNonNull(surface, "surface == null");
	}
}