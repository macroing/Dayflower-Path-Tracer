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
package org.dayflower.pathtracer.scene;

import java.util.Objects;
import java.util.Optional;

import org.macroing.math4j.Ray3F;

/**
 * A {@code Shape} is a model of a shape.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class Shape {
	private final int type;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Shape} instance.
	 * 
	 * @param type the type of this {@code Shape} instance
	 */
	protected Shape(final int type) {
		this.type = type;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns an {@code Optional} of {@link ShapeIntersection} with the optional intersection given a specified {@link Ray3F}.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} may be thrown. But no guarantees can be made.
	 * 
	 * @param ray a {@code Ray3F}
	 * @return an {@code Optional} of {@code ShapeIntersection} with the optional intersection given a specified {@code Ray3F}
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	public abstract Optional<ShapeIntersection> intersection(final Ray3F ray);
	
	/**
	 * Returns {@code true} if, and only if, the specified {@link Ray3F} intersects this {@code Shape} instance, {@code false} otherwise.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray a {@code Ray3F}
	 * @return {@code true} if, and only if, the specified {@code Ray3F} intersects this {@code Shape} instance, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	public final boolean intersects(final Ray3F ray) {
		return intersection(Objects.requireNonNull(ray, "ray == null")).isPresent();
	}
	
	/**
	 * Returns the size of this {@code Shape} instance.
	 * 
	 * @return the size of this {@code Shape} instance
	 */
	public abstract int getSize();
	
	/**
	 * Returns the type of this {@code Shape} instance.
	 * 
	 * @return the type of this {@code Shape} instance
	 */
	public final int getType() {
		return this.type;
	}
}