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
 * A {@code PrimitiveIntersection} contains the properties of an intersected {@link Primitive}.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class PrimitiveIntersection {
	private final Primitive primitive;
	private final ShapeIntersection shapeIntersection;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code PrimitiveIntersection} instance.
	 * <p>
	 * If either {@code primitive} or {@code shapeIntersection} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitive the {@link Primitive} for which the {@code PrimitiveIntersection} is created
	 * @param shapeIntersection a {@link ShapeIntersection}
	 * @throws NullPointerException thrown if, and only if, either {@code primitive} or {@code shapeIntersection} are {@code null}
	 */
	public PrimitiveIntersection(final Primitive primitive, final ShapeIntersection shapeIntersection) {
		this.primitive = Objects.requireNonNull(primitive, "primitive == null");
		this.shapeIntersection = Objects.requireNonNull(shapeIntersection, "shapeIntersection == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Primitive} for which this {@code PrimitiveIntersection} was created.
	 * 
	 * @return the {@code Primitive} for which this {@code PrimitiveIntersection} was created
	 */
	public Primitive getPrimitive() {
		return this.primitive;
	}
	
	/**
	 * Returns the {@link ShapeIntersection} associated with this {@code PrimitiveIntersection} instance.
	 * 
	 * @return the {@code ShapeIntersection} associated with this {@code PrimitiveIntersection} instance
	 */
	public ShapeIntersection getShapeIntersection() {
		return this.shapeIntersection;
	}
}