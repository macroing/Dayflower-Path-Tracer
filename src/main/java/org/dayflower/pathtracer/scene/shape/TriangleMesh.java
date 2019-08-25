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
package org.dayflower.pathtracer.scene.shape;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.math.Ray3F;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.ShapeIntersection;

/**
 * A {@code TriangleMesh} represents a triangle mesh.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class TriangleMesh extends Shape {
	/**
	 * The type number associated with a {@code TriangleMesh}. The number is {@code 5}.
	 */
	public static final int TYPE = 5;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float EPSILON = 0.0001F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final List<Triangle> triangles;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code TriangleMesh} instance.
	 * <p>
	 * If {@code triangles} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Modifying {@code triangles} after this {@code TriangleMesh} instance has been created, will not affect it. The {@code List} is copied.
	 * 
	 * @param triangles the {@link Triangle}s to use
	 * @throws NullPointerException thrown if, and only if, {@code triangles} is {@code null}
	 */
	public TriangleMesh(final List<Triangle> triangles) {
		super(TYPE);
		
		this.triangles = new ArrayList<>(triangles);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code List} with all {@link Triangle}s added to this {@code TriangleMesh} instance.
	 * 
	 * @return a {@code List} with all {@code Triangle}s added to this {@code TriangleMesh} instance
	 */
	public List<Triangle> getTriangles() {
		return new ArrayList<>(this.triangles);
	}
	
	/**
	 * Returns an {@code Optional} of {@link ShapeIntersection} with the optional intersection given a specified {@link Ray3F}.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray a {@code Ray3F}
	 * @return an {@code Optional} of {@code ShapeIntersection} with the optional intersection given a specified {@code Ray3F}
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public Optional<ShapeIntersection> intersection(final Ray3F ray) {
		Objects.requireNonNull(ray, "ray == null");
		
		ShapeIntersection shapeIntersection = null;
		
		for(final Triangle triangle : this.triangles) {
			final Optional<ShapeIntersection> optionalShapeIntersection = triangle.intersection(ray);
			
			if(optionalShapeIntersection.isPresent()) {
				final ShapeIntersection currentShapeIntersection = optionalShapeIntersection.get();
				
				final float t = currentShapeIntersection.getT();
				
				if(t > EPSILON && (shapeIntersection == null || t < shapeIntersection.getT())) {
					shapeIntersection = currentShapeIntersection;
				}
			}
		}
		
		return Optional.ofNullable(shapeIntersection);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code TriangleMesh} instance.
	 * 
	 * @return a {@code String} representation of this {@code TriangleMesh} instance
	 */
	@Override
	public String toString() {
		return String.format("new Mesh(%s)", this.triangles);
	}
	
	/**
	 * Compares {@code object} to this {@code TriangleMesh} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code TriangleMesh}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code TriangleMesh} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code TriangleMesh}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof TriangleMesh)) {
			return false;
		} else if(!Objects.equals(this.triangles, TriangleMesh.class.cast(object).triangles)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the size of this {@code TriangleMesh} instance.
	 * 
	 * @return the size of this {@code TriangleMesh} instance
	 */
	@Override
	public int getSize() {
		return this.triangles.size() * Triangle.SIZE;
	}
	
	/**
	 * Returns a hash code for this {@code TriangleMesh} instance.
	 * 
	 * @return a hash code for this {@code TriangleMesh} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.triangles);
	}
}