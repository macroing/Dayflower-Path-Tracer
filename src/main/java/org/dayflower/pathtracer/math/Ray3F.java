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
package org.dayflower.pathtracer.math;

import java.util.Objects;

/**
 * A {@code Ray3F} denotes a ray in 3D-space.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Ray3F {
	/**
	 * The origin of this {@code Ray3F} instance.
	 */
	public final Point3F origin;
	
	/**
	 * The direction of this {@code Ray3F} instance.
	 */
	public final Vector3F direction;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Ray3F} instance.
	 * <p>
	 * If either {@code origin} or {@code direction} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param origin the origin of the {@code Ray3F}
	 * @param direction the direction of the {@code Ray3F}
	 * @throws NullPointerException thrown if, and only if, either {@code origin} or {@code direction} are {@code null}
	 */
	public Ray3F(final Point3F origin, final Vector3F direction) {
		this.origin = Objects.requireNonNull(origin, "origin == null");
		this.direction = Objects.requireNonNull(direction, "direction == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the origin of this {@code Ray3F} instance.
	 * 
	 * @return the origin of this {@code Ray3F} instance
	 */
	public Point3F getOrigin() {
		return this.origin;
	}
	
	/**
	 * Returns a {@code Ray3F} that is reflected on a {@link Point3F} at {@code t} with a surface normal of {@code surfaceNormal}.
	 * <p>
	 * If {@code surfaceNormal} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param t the parametric {@code t} value
	 * @param surfaceNormal the surface normal
	 * @return a {@code Ray3F} that is reflected on a {@code Point3F} at {@code t} with a surface normal of {@code surfaceNormal}
	 */
	public Ray3F reflection(final float t, final Vector3F surfaceNormal) {
		final float surfaceNormalDotDirection = surfaceNormal.dotProduct(this.direction);
		
		final Point3F origin = this.origin.pointAt(this.direction, t);
		
		final Vector3F direction = this.direction.subtract(surfaceNormal.multiply(surfaceNormalDotDirection * 2.0F)).normalize();
		
		return new Ray3F(origin, direction);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Ray3F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Ray3F} instance
	 */
	@Override
	public String toString() {
		return String.format("new Ray3F(%s, %s)", this.origin, this.direction);
	}
	
	/**
	 * Returns the direction of this {@code Ray3F} instance.
	 * 
	 * @return the direction of this {@code Ray3F} instance
	 */
	public Vector3F getDirection() {
		return this.direction;
	}
	
	/**
	 * Compares {@code object} to this {@code Ray3F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Ray3F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Ray3F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Ray3F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Ray3F)) {
			return false;
		} else if(!Objects.equals(this.origin, Ray3F.class.cast(object).origin)) {
			return false;
		} else if(!Objects.equals(this.direction, Ray3F.class.cast(object).direction)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code Ray3F} instance.
	 * 
	 * @return a hash code for this {@code Ray3F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.origin, this.direction);
	}
}