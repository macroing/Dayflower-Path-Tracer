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

import org.dayflower.pathtracer.scene.Shape;

/**
 * A {@code Mesh} represents a triangle mesh.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Mesh implements Shape {
//	TODO: Add Javadocs.
	public static final int TYPE = 5;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final List<Triangle> triangles;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Mesh(final List<Triangle> triangles) {
		this.triangles = new ArrayList<>(triangles);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code List} with all {@link Triangle}s added to this {@code Mesh} instance.
	 * 
	 * @return a {@code List} with all {@code Triangle}s added to this {@code Mesh} instance
	 */
	public List<Triangle> getTriangles() {
		return new ArrayList<>(this.triangles);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Mesh} instance.
	 * 
	 * @return a {@code String} representation of this {@code Mesh} instance
	 */
	@Override
	public String toString() {
		return String.format("new Mesh(%s)", this.triangles);
	}
	
	/**
	 * Compares {@code object} to this {@code Mesh} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Mesh}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Mesh} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Mesh}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Mesh)) {
			return false;
		} else if(!Objects.equals(this.triangles, Mesh.class.cast(object).triangles)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the size of this {@code Mesh} instance.
	 * 
	 * @return the size of this {@code Mesh} instance
	 */
	@Override
	public int getSize() {
		return this.triangles.size() * Triangle.SIZE;
	}
	
	/**
	 * Returns the type of this {@code Mesh} instance.
	 * 
	 * @return the type of this {@code Mesh} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code Mesh} instance.
	 * 
	 * @return a hash code for this {@code Mesh} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.triangles);
	}
}