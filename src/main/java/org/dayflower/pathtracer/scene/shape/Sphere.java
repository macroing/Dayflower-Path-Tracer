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
package org.dayflower.pathtracer.scene.shape;

import java.util.Objects;

import org.dayflower.pathtracer.math.Point3;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;

/**
 * A {@link Shape} implementation that implements a sphere.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Sphere extends Shape {
	private final float radius;
	private final Point3 position;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Sphere} instance.
	 * <p>
	 * If either {@code surface} or {@code position} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface a {@link Surface} denoting the surface of this {@code Sphere}
	 * @param radius the radius of this {@code Sphere}
	 * @param position the position of this {@code Sphere}
	 * @throws NullPointerException thrown if, and only if, either {@code surface} or {@code position} are {@code null}
	 */
	public Sphere(final Surface surface, final float radius, final Point3 position) {
		super(surface);
		
		this.radius = radius;
		this.position = Objects.requireNonNull(position, "position == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Sphere} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Sphere}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Sphere} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Sphere}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Sphere)) {
			return false;
		} else if(!Objects.equals(getSurface(), Sphere.class.cast(object).getSurface())) {
			return false;
		} else if(Float.compare(this.radius, Sphere.class.cast(object).radius) != 0) {
			return false;
		} else if(!Objects.equals(this.position, Sphere.class.cast(object).position)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise.
	 * <p>
	 * Calling this method is equivalent to calling {@code isWithinRadius(x, y, z, 0.0F)}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise
	 */
	public boolean isWithinRadius(final float x, final float y, final float z) {
		return isWithinRadius(x, y, z, 0.0F);
	}
	
	/**
	 * Returns {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise.
	 * <p>
	 * The actual radius will be {@code sphere.getRadius() + threshold}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @param threshold the threshold value
	 * @return {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise
	 */
	public boolean isWithinRadius(final float x, final float y, final float z, final float threshold) {
		final float radius = getRadius() + threshold;
		final float radiusSquared = radius * radius;
		final float deltaX = x - getPosition().x;
		final float deltaY = y - getPosition().y;
		final float deltaZ = z - getPosition().z;
		final float distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		
		return distanceSquared < radiusSquared;
	}
	
	/**
	 * Returns the radius of this {@code Sphere}.
	 * 
	 * @return the radius of this {@code Sphere}
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/**
	 * Returns a hash code for this {@code Sphere} instance.
	 * 
	 * @return a hash code for this {@code Sphere} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getSurface(), Float.valueOf(this.radius), this.position);
	}
	
	/**
	 * Returns the center position of this {@code Sphere}.
	 * 
	 * @return the center position of this {@code Sphere}
	 */
	public Point3 getPosition() {
		return this.position;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Sphere} instance.
	 * 
	 * @return a {@code String} representation of this {@code Sphere} instance
	 */
	@Override
	public String toString() {
		return String.format("Sphere: [Radius=%s], [Position=%s]", Float.toString(this.radius), this.position);
	}
}