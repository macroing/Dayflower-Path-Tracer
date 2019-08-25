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

import static org.dayflower.pathtracer.math.MathF.abs;

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
 * A {@link Shape} implementation that implements a plane.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Plane extends Shape {
	/**
	 * The relative offset of the A Offset parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int RELATIVE_OFFSET_A_OFFSET = 0;
	
	/**
	 * The relative offset of the B Offset parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_B_OFFSET = 1;
	
	/**
	 * The relative offset of the C Offset parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_C_OFFSET = 2;
	
	/**
	 * The relative offset of the Surface Normal Offset parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET = 3;
	
	/**
	 * The size of a {@code Plane} in the {@code float} array. The size is {@code 4}.
	 */
	public static final int SIZE = 4;
	
	/**
	 * The type number associated with a {@code Plane}. The number is {@code 3}.
	 */
	public static final int TYPE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float EPSILON = 0.0001F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@link Point3F} denoting the point A.
	 */
	public final Point3F a;
	
	/**
	 * A {@link Point3F} denoting the point B.
	 */
	public final Point3F b;
	
	/**
	 * A {@link Point3F} denoting the point C.
	 */
	public final Point3F c;
	
	/**
	 * The surface normal.
	 */
	public final Vector3F surfaceNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Plane} instance.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a a {@link Point3F} denoting the point A
	 * @param b a {@code Point3} denoting the point A
	 * @param c a {@code Point3} denoting the point A
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public Plane(final Point3F a, final Point3F b, final Point3F c) {
		super(TYPE);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
		this.surfaceNormal = Vector3F.normalNormalized(this.a, this.b, this.c);
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
		final Vector3F direction = ray.direction;
		final Vector3F surfaceNormal = this.surfaceNormal;
		
		final float dotProduct = surfaceNormal.dotProduct(direction);
		
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
			final Point3F a = this.a;
			final Point3F origin = ray.origin;
			
			final Vector3F originToA = Vector3F.direction(origin, a);
			
			final float t = originToA.dotProduct(surfaceNormal) / dotProduct;
			
			if(t > EPSILON) {
				return Optional.of(doCreateShapeIntersection(ray, t));
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Returns the point A.
	 * 
	 * @return the point A
	 */
	public Point3F getA() {
		return this.a;
	}
	
	/**
	 * Returns the point B.
	 * 
	 * @return the point B
	 */
	public Point3F getB() {
		return this.b;
	}
	
	/**
	 * Returns the point C.
	 * 
	 * @return the point C
	 */
	public Point3F getC() {
		return this.c;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Plane} instance.
	 * 
	 * @return a {@code String} representation of this {@code Plane} instance
	 */
	@Override
	public String toString() {
		return String.format("new Plane(%s, %s, %s)", this.a, this.b, this.c);
	}
	
	/**
	 * Returns the surface normal.
	 * 
	 * @return the surface normal
	 */
	public Vector3F getSurfaceNormal() {
		return this.surfaceNormal;
	}
	
	/**
	 * Compares {@code object} to this {@code Plane} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Plane}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Plane} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Plane}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Plane)) {
			return false;
		} else if(!Objects.equals(this.a, Plane.class.cast(object).a)) {
			return false;
		} else if(!Objects.equals(this.b, Plane.class.cast(object).b)) {
			return false;
		} else if(!Objects.equals(this.c, Plane.class.cast(object).c)) {
			return false;
		} else if(!Objects.equals(this.surfaceNormal, Plane.class.cast(object).surfaceNormal)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the size of this {@code Plane} instance.
	 * 
	 * @return the size of this {@code Plane} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns a hash code for this {@code Plane} instance.
	 * 
	 * @return a hash code for this {@code Plane} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.a, this.b, this.c, this.surfaceNormal);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ShapeIntersection doCreateShapeIntersection(final Ray3F ray, final float t) {
		final Point3F surfaceIntersectionPoint = ray.origin.pointAt(ray.direction, t);
		
		final float x = abs(this.surfaceNormal.x);
		final float y = abs(this.surfaceNormal.y);
		final float z = abs(this.surfaceNormal.z);
		
		final boolean isX = x > y && x > z;
		final boolean isY = y > z;
		
		final float aX = isX ? this.a.y      : isY ? this.a.z      : this.a.x;
		final float aY = isX ? this.a.z      : isY ? this.a.x      : this.a.y;
		final float bX = isX ? this.c.y - aX : isY ? this.c.z - aX : this.c.x - aX;
		final float bY = isX ? this.c.z - aY : isY ? this.c.x - aY : this.c.y - aY;
		final float cX = isX ? this.b.y - aX : isY ? this.b.z - aX : this.b.x - aX;
		final float cY = isX ? this.b.z - aY : isY ? this.b.x - aY : this.b.y - aY;
		
		final float determinant = bX * cY - bY * cX;
		final float determinantReciprocal = 1.0F / determinant;
		
		final float bNU = -bY * determinantReciprocal;
		final float bNV =  bX * determinantReciprocal;
		final float bND = (bY * aX - bX * aY) * determinantReciprocal;
		final float cNU =  cY * determinantReciprocal;
		final float cNV = -cX * determinantReciprocal;
		final float cND = (cX * aY - cY * aX) * determinantReciprocal;
		
		final float hU = isX ? surfaceIntersectionPoint.y : isY ? surfaceIntersectionPoint.z : surfaceIntersectionPoint.x;
		final float hV = isX ? surfaceIntersectionPoint.z : isY ? surfaceIntersectionPoint.x : surfaceIntersectionPoint.y;
		
		final float u = hU * bNU + hV * bNV + bND;
		final float v = hU * cNU + hV * cNV + cND;
		
		final Point2F textureCoordinates = new Point2F(u, v);
		
		final Vector3F surfaceNormal = this.surfaceNormal;
		
		final OrthoNormalBasis33F orthoNormalBasis = new OrthoNormalBasis33F(surfaceNormal);
		
		return new ShapeIntersection(orthoNormalBasis, textureCoordinates, surfaceIntersectionPoint, this, surfaceNormal, t);
	}
}