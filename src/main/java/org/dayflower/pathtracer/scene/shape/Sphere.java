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

import static org.dayflower.pathtracer.math.MathF.PI_MULTIPLIED_BY_TWO_RECIPROCAL;
import static org.dayflower.pathtracer.math.MathF.asinpi;
import static org.dayflower.pathtracer.math.MathF.atan2;
import static org.dayflower.pathtracer.math.MathF.sqrt;

import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.math.OrthoNormalBasis33F;
import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Ray3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.ShapeIntersection;

/**
 * A {@link Shape} implementation that implements a sphere.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Sphere extends Shape {
	/**
	 * The relative offset of the Position Offset parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int RELATIVE_OFFSET_POSITION_OFFSET = 0;
	
	/**
	 * The relative offset of the Radius parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_RADIUS = 1;
	
	/**
	 * The size of a {@code Sphere} in the {@code float} array. The size is {@code 2}.
	 */
	public static final int SIZE = 2;
	
	/**
	 * The type number associated with a {@code Sphere}. The number is {@code 1}.
	 */
	public static final int TYPE = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float EPSILON = 0.0001F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Point3F position;
	private final float radius;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Sphere} instance.
	 * <p>
	 * If {@code position} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param position the position of this {@code Sphere}
	 * @throws NullPointerException thrown if, and only if, {@code position} is {@code null}
	 */
	public Sphere(final Point3F position) {
		this(position, 1.0F);
	}
	
	/**
	 * Constructs a new {@code Sphere} instance.
	 * <p>
	 * If {@code position} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param position the position of this {@code Sphere}
	 * @param radius the radius of this {@code Sphere}
	 * @throws NullPointerException thrown if, and only if, {@code position} is {@code null}
	 */
	public Sphere(final Point3F position, final float radius) {
		super(TYPE);
		
		this.position = Objects.requireNonNull(position, "position == null");
		this.radius = radius;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		final Point3F origin = ray.origin;
		final Point3F position = this.position;
		
		final Vector3F direction = ray.direction;
		final Vector3F originToPosition = Vector3F.direction(origin, position);
		
		final float originToPositionDotDirection = originToPosition.dotProduct(direction);
		final float originToPositionLengthSquared = originToPosition.lengthSquared();
		final float radius = this.radius;
		final float determinantSquared = originToPositionDotDirection * originToPositionDotDirection - originToPositionLengthSquared + radius * radius;
		
		if(determinantSquared >= 0.0F) {
			final float determinant = sqrt(determinantSquared);
			
			final float t0 = originToPositionDotDirection - determinant;
			final float t1 = originToPositionDotDirection + determinant;
			
			if(t0 > EPSILON) {
				return Optional.of(doCreateShapeIntersection(ray, t0));
			}
			
			if(t1 > EPSILON) {
				return Optional.of(doCreateShapeIntersection(ray, t1));
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Returns the center position of this {@code Sphere}.
	 * 
	 * @return the center position of this {@code Sphere}
	 */
	public Point3F getPosition() {
		return this.position;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Sphere} instance.
	 * 
	 * @return a {@code String} representation of this {@code Sphere} instance
	 */
	@Override
	public String toString() {
		return String.format("new Sphere(%s, %s)", this.position, Float.toString(this.radius));
	}
	
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
		} else if(!Objects.equals(this.position, Sphere.class.cast(object).position)) {
			return false;
		} else if(Float.compare(this.radius, Sphere.class.cast(object).radius) != 0) {
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
	 * Returns the size of this {@code Sphere} instance.
	 * 
	 * @return the size of this {@code Sphere} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns a hash code for this {@code Sphere} instance.
	 * 
	 * @return a hash code for this {@code Sphere} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.position, Float.valueOf(this.radius));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ShapeIntersection doCreateShapeIntersection(final Ray3F ray, final float t) {
		final Point3F surfaceIntersectionPoint = ray.origin.pointAt(ray.direction, t);
		
		final Vector3F surfaceNormal = Vector3F.direction(this.position, surfaceIntersectionPoint).normalize();
		
		final Vector3F direction = surfaceNormal.negate();
		
		final float u = 0.5F + atan2(direction.z, direction.x) * PI_MULTIPLIED_BY_TWO_RECIPROCAL;
		final float v = 0.5F - asinpi(direction.y);
		
		final Point2F textureCoordinates = new Point2F(u, v);
		
		final OrthoNormalBasis33F orthoNormalBasis = new OrthoNormalBasis33F(surfaceNormal);
		
		return new ShapeIntersection(orthoNormalBasis, textureCoordinates, surfaceIntersectionPoint, this, surfaceNormal, t);
	}
}