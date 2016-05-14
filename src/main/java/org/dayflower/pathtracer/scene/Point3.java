/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
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

import static org.dayflower.pathtracer.math.Math2.abs;
import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.sqrt;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

import org.dayflower.pathtracer.math.Math2;

/**
 * A {@code Point3} denotes a point in 3D with coordinates of X, Y and Z.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Point3 {
	/**
	 * The X-coordinate.
	 */
	public final float x;
	
	/**
	 * The Y-coordinate.
	 */
	public final float y;
	
	/**
	 * The Z-coordinate.
	 */
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Point3} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Point3(0.0F, 0.0F, 0.0F)}.
	 */
	public Point3() {
		this(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Point3} based on {@code x}, {@code y} and {@code z}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 */
	public Point3(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructs a new {@code Point3} based on the X-, Y- and Z-coordinates provided by {@code v}.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@link Vector3}
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Point3(final Vector3 v) {
		this(v.x, v.y, v.z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Point3} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Point3}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Point3} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Point3}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Point3)) {
			return false;
		} else if(!Objects.equals(Float.valueOf(this.x), Float.valueOf(Point3.class.cast(object).x))) {
			return false;
		} else if(!Objects.equals(Float.valueOf(this.y), Float.valueOf(Point3.class.cast(object).y))) {
			return false;
		} else if(!Objects.equals(Float.valueOf(this.z), Float.valueOf(Point3.class.cast(object).z))) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs!
	public boolean isWithinSphereRadius(final Point3 p, final float radius) {
		final float x = abs(this.x - p.x);
		final float y = abs(this.y - p.y);
		final float z = abs(this.z - p.z);
		
		return x * x + y * y + z * z < radius;
	}
	
//	TODO: Add Javadocs!
	public float distanceTo(final Point3 p) {
		return sqrt(distanceToSquared(p));
	}
	
//	TODO: Add Javadocs!
	public float distanceToSquared(final Point3 p) {
		final float deltaX = this.x - p.x;
		final float deltaY = this.y - p.y;
		final float deltaZ = this.z - p.z;
		
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}
	
	/**
	 * Returns a hash code for this {@code Point3} instance.
	 * 
	 * @return a hash code for this {@code Point3} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
	}
	
//	TODO: Add Javadocs!
	public Point3 pointAt(final Vector3 v, final float t) {
		return new Point3(this.x + v.x * t, this.y + v.y * t, this.z + v.z * t);
	}
	
//	TODO: Add Javadocs!
	public Point3 transform(final Matrix44 m) {
		final float x = m.e11 * this.x + m.e12 * this.y + m.e13 * this.z + m.e14 * 1.0F;
		final float y = m.e21 * this.x + m.e22 * this.y + m.e23 * this.z + m.e24 * 1.0F;
		final float z = m.e31 * this.x + m.e32 * this.y + m.e33 * this.z + m.e34 * 1.0F;
		
		return new Point3(x, y, z);
	}
	
//	TODO: Add Javadocs!
	public Point3 transformAndDivide(final Matrix44 m) {
		final float x = m.e11 * this.x + m.e12 * this.y + m.e13 * this.z + m.e14 * 1.0F;
		final float y = m.e21 * this.x + m.e22 * this.y + m.e23 * this.z + m.e24 * 1.0F;
		final float z = m.e31 * this.x + m.e32 * this.y + m.e33 * this.z + m.e34 * 1.0F;
		final float w = m.e41 * this.x + m.e42 * this.y + m.e43 * this.z + m.e44 * 1.0F;
		
		return Math2.equals(w, 1.0F) ? new Point3(x / w, y / w, z / w) : new Point3(x, y, z);
	}
	
//	TODO: Add Javadocs.
	public Point3 translateX(final float x) {
		return new Point3(this.x + x, this.y, this.z);
	}
	
//	TODO: Add Javadocs.
	public Point3 translateY(final float y) {
		return new Point3(this.x, this.y + y, this.z);
	}
	
//	TODO: Add Javadocs.
	public Point3 translateZ(final float z) {
		return new Point3(this.x, this.y, this.z + z);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Point3} instance.
	 * 
	 * @return a {@code String} representation of this {@code Point3} instance
	 */
	@Override
	public String toString() {
		return String.format("Point3: [X=%s], [Y=%s], [Z=%s]", Float.toString(this.x), Float.toString(this.y), Float.toString(this.z));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Point3 center(final Point3 p0, final Point3 p1) {
		final float centerX = (p0.x + p1.x) * 0.5F;
		final float centerY = (p0.y + p1.y) * 0.5F;
		final float centerZ = (p0.z + p1.z) * 0.5F;
		
		return new Point3(centerX, centerY, centerZ);
	}
	
//	TODO: Add Javadocs!
	public static Point3 maximum() {
		return new Point3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
//	TODO: Add Javadocs!
	public static Point3 maximum(final Point3 p0, final Point3 p1) {
		final float maximumX = p0.x > p1.x ? p0.x : p1.x;
		final float maximumY = p0.y > p1.y ? p0.y : p1.y;
		final float maximumZ = p0.z > p1.z ? p0.z : p1.z;
		
		return new Point3(maximumX, maximumY, maximumZ);
	}
	
//	TODO: Add Javadocs!
	public static Point3 maximum(final Point3 p0, final Point3 p1, final Point3 p2) {
		final float maximumX = p0.x > p1.x && p0.x > p2.x ? p0.x : p1.x > p2.x ? p1.x : p2.x;
		final float maximumY = p0.y > p1.y && p0.y > p2.y ? p0.y : p1.y > p2.y ? p1.y : p2.y;
		final float maximumZ = p0.z > p1.z && p0.z > p2.z ? p0.z : p1.z > p2.z ? p1.z : p2.z;
		
		return new Point3(maximumX, maximumY, maximumZ);
	}
	
//	TODO: Add Javadocs!
	public static Point3 minimum() {
		return new Point3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	}
	
//	TODO: Add Javadocs!
	public static Point3 minimum(final Point3 p0, final Point3 p1) {
		final float minimumX = p0.x < p1.x ? p0.x : p1.x;
		final float minimumY = p0.y < p1.y ? p0.y : p1.y;
		final float minimumZ = p0.z < p1.z ? p0.z : p1.z;
		
		return new Point3(minimumX, minimumY, minimumZ);
	}
	
//	TODO: Add Javadocs!
	public static Point3 minimum(final Point3 p0, final Point3 p1, final Point3 p2) {
		final float minimumX = p0.x < p1.x && p0.x < p2.x ? p0.x : p1.x < p2.x ? p1.x : p2.x;
		final float minimumY = p0.y < p1.y && p0.y < p2.y ? p0.y : p1.y < p2.y ? p1.y : p2.y;
		final float minimumZ = p0.z < p1.z && p0.z < p2.z ? p0.z : p1.z < p2.z ? p1.z : p2.z;
		
		return new Point3(minimumX, minimumY, minimumZ);
	}
	
//	TODO: Add Javadocs!
	public static Point3 rotate(final Point3 axis, final Vector3 direction, final Point3 point, final float theta) {
		final Vector3 d = direction.normalize();
		
		final float u = d.x * d.x;
		final float v = d.y * d.y;
		final float w = d.z * d.z;
		
		final float cosTheta = cos(theta);
		final float oneMinusCosTheta = 1.0F - cosTheta;
		final float sinTheta = sin(theta);
		
		final float x0 = axis.x;
		final float y0 = axis.y;
		final float z0 = axis.z;
		
		final float x1 = point.x;
		final float y1 = point.y;
		final float z1 = point.z;
		
		final float x2 = (x0 * (v + w) - d.x * (y0 * d.y + z0 * d.z - d.x * x1 - d.y * y1 - d.z * z1)) * oneMinusCosTheta + x1 * cosTheta + (-z0 * d.y + y0 * d.z - d.z * y1 + d.y * z1) * sinTheta;
		final float y2 = (y0 * (u + w) - d.y * (x0 * d.x + z0 * d.z - d.x * x1 - d.y * y1 - d.z * z1)) * oneMinusCosTheta + y1 * cosTheta + (z0 * d.x - x0 * d.z + d.z * x1 - d.x * z1) * sinTheta;
		final float z2 = (z0 * (u + v) - d.z * (x0 * d.x + y0 * d.y - d.x * x1 - d.y * y1 - d.z * z1)) * oneMinusCosTheta + z1 * cosTheta + (-y0 * d.x + x0 * d.y - d.y * x1 + d.x * y1) * sinTheta;
		
		return new Point3(x2, y2, z2);
	}
}