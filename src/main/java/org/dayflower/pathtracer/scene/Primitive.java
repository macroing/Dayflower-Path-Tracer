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

import org.macroing.math4j.Matrix44F;
import org.macroing.math4j.Ray3F;

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
	
	private Matrix44F objectToWorld;
	private Matrix44F worldToObject;
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
		this(shape, surface, new Matrix44F());
	}
	
	/**
	 * Constructs a new {@code Primitive} instance.
	 * <p>
	 * If either {@code shape}, {@code surface} or {@code objectToWorld} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code objectToWorld} cannot be inversed, an {@code IllegalStateException} will be thrown.
	 * 
	 * @param shape the {@link Shape} to use
	 * @param surface the {@link Surface} to use
	 * @param objectToWorld the {@link Matrix44F} to transform from object-space to world-space
	 * @throws IllegalStateException thrown if, and only if, {@code objectToWorld} cannot be inversed
	 * @throws NullPointerException thrown if, and only if, either {@code shape}, {@code surface} or {@code objectToWorld} are {@code null}
	 */
	public Primitive(final Shape shape, final Surface surface, final Matrix44F objectToWorld) {
		this.shape = Objects.requireNonNull(shape, "shape == null");
		this.surface = Objects.requireNonNull(surface, "surface == null");
		this.objectToWorld = Objects.requireNonNull(objectToWorld, "objectToWorld == null");
		this.worldToObject = this.objectToWorld.inverse();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Matrix44F} to transform from object-space to world-space.
	 * 
	 * @return the {@code Matrix44F} to transform from object-space to world-space
	 */
	public Matrix44F getObjectToWorld() {
		return this.objectToWorld;
	}
	
	/**
	 * Returns the {@link Matrix44F} to transform from world-space to object-space.
	 * 
	 * @return the {@code Matrix44F} to transform from world-space to object-space
	 */
	public Matrix44F getWorldToObject() {
		return this.worldToObject;
	}
	
	/**
	 * Returns an {@code Optional} of {@link PrimitiveIntersection} with the optional intersection given a specified {@link Ray3F}.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray a {@code Ray3F}
	 * @return an {@code Optional} of {@code PrimitiveIntersection} with the optional intersection given a specified {@code Ray3F}
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	public Optional<PrimitiveIntersection> intersection(final Ray3F ray) {
		final Optional<ShapeIntersection> optionalShapeIntersection = this.shape.intersection(Objects.requireNonNull(ray, "ray == null"));
		
		if(optionalShapeIntersection.isPresent()) {
			final ShapeIntersection shapeIntersection = optionalShapeIntersection.get();
			
			return Optional.of(new PrimitiveIntersection(this, shapeIntersection));
		}
		
		return Optional.empty();
	}
	
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
		return String.format("new Primitive(%s, %s, %s)", this.shape, this.surface, this.objectToWorld);
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
		} else if(!Objects.equals(this.objectToWorld, Primitive.class.cast(object).objectToWorld)) {
			return false;
		} else if(!Objects.equals(this.worldToObject, Primitive.class.cast(object).worldToObject)) {
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
		return Objects.hash(this.objectToWorld, this.worldToObject, this.shape, this.surface);
	}
	
	/**
	 * Sets the {@link Matrix44F} to transform from object-space to world-space and updates the {@code Matrix44F} to transform from world-space to object-space.
	 * <p>
	 * If {@code objectToWorld} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code objectToWorld} cannot be inversed, an {@code IllegalStateException} will be thrown.
	 * 
	 * @param objectToWorld the new {@code Matrix44F} to transform from object-space to world-space
	 * @throws IllegalStateException thrown if, and only if, {@code objectToWorld} cannot be inversed
	 * @throws NullPointerException thrown if, and only if, {@code objectToWorld} is {@code null}
	 */
	public void setObjectToWorld(final Matrix44F objectToWorld) {
		this.objectToWorld = Objects.requireNonNull(objectToWorld, "objectToWorld == null");
		this.worldToObject = this.objectToWorld.inverse();
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
	
	/**
	 * Sets the {@link Matrix44F} to transform from world-space to object-space and updates the {@code Matrix44F} to transform from object-space to world-space.
	 * <p>
	 * If {@code worldToObject} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code worldToObject} cannot be inversed, an {@code IllegalStateException} will be thrown.
	 * 
	 * @param worldToObject the new {@code Matrix44F} to transform from world-space to object-space
	 * @throws IllegalStateException thrown if, and only if, {@code worldToObject} cannot be inversed
	 * @throws NullPointerException thrown if, and only if, {@code worldToObject} is {@code null}
	 */
	public void setWorldToObject(final Matrix44F worldToObject) {
		this.worldToObject = Objects.requireNonNull(worldToObject, "worldToObject == null");
		this.objectToWorld = this.worldToObject.inverse();
	}
}