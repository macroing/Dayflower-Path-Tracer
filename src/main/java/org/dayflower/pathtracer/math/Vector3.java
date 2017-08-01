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
package org.dayflower.pathtracer.math;

import static org.dayflower.pathtracer.math.Math2.PI;
import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.sqrt;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

//TODO: Add Javadocs!
public final class Vector3 {
//	TODO: Add Javadocs!
	public final float x;
	
//	TODO: Add Javadocs!
	public final float y;
	
//	TODO: Add Javadocs!
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Vector3() {
		this(0.0F, 0.0F, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public Vector3(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
//	TODO: Add Javadocs!
	public Vector3(final Point3 p) {
		this(p.x, p.y, p.z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Vector3)) {
			return false;
		} else if(Float.compare(this.x, Vector3.class.cast(object).x) != 0) {
			return false;
		} else if(Float.compare(this.y, Vector3.class.cast(object).y) != 0) {
			return false;
		} else if(Float.compare(this.z, Vector3.class.cast(object).z) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs!
	public float dotProduct(final Vector3 v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
//	TODO: Add Javadocs!
	public float length() {
		return sqrt(lengthSquared());
	}
	
//	TODO: Add Javadocs!
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
//	TODO: Add Javadocs!
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
	}
	
//	TODO: Add Javadocs!
	@Override
	public String toString() {
		return String.format("Vector3: [X=%s], [Y=%s], [Z=%s]", Float.toString(this.x), Float.toString(this.y), Float.toString(this.z));
	}
	
//	TODO: Add Javadocs!
	public Vector3 add(final Vector3 v) {
		return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
//	TODO: Add Javadocs!
	public Vector3 crossProduct(final Vector3 v) {
		return new Vector3(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}
	
//	TODO: Add Javadocs!
	public Vector3 divide(final float s) {
		return new Vector3(this.x / s, this.y / s, this.z / s);
	}
	
//	TODO: Add Javadocs!
	public Vector3 multiply(final float s) {
		return new Vector3(this.x * s, this.y * s, this.z * s);
	}
	
//	TODO: Add Javadocs!
	public Vector3 negate() {
		return new Vector3(-this.x, -this.y, -this.z);
	}
	
//	TODO: Add Javadocs!
	public Vector3 normalize() {
		return divide(length());
	}
	
//	TODO: Add Javadocs!
	public Vector3 subtract(final Vector3 v) {
		return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
//	TODO: Add Javadocs!
	public Vector3 transformReverse(final OrthoNormalBasis orthoNormalBasis) {
		final float x = dotProduct(orthoNormalBasis.u);
		final float y = dotProduct(orthoNormalBasis.v);
		final float z = dotProduct(orthoNormalBasis.w);
		
		return new Vector3(x, y, z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Vector3 direction(final float u, final float v) {
		final float theta = u * 2.0F * PI;
		final float phi = v * PI;
		final float sinPhi = sin(phi);
		final float x = -sinPhi * cos(theta);
		final float y = cos(phi);
		final float z = sinPhi * sin(theta);
		
		return new Vector3(x, y, z);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 direction(final Point3 eye, final Point3 lookAt) {
		return new Vector3(lookAt.x - eye.x, lookAt.y - eye.y, lookAt.z - eye.z);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 normal(final Point3 p0, final Point3 p1, final Point3 p2) {
		final Vector3 edge0 = direction(p0, p1);
		final Vector3 edge1 = direction(p0, p2);
		final Vector3 normal = edge0.crossProduct(edge1);
		
		return normal;
	}
	
//	TODO: Add Javadocs!
	public static Vector3 normalNormalized(final Point3 p0, final Point3 p1, final Point3 p2) {
		return normal(p0, p1, p2).normalize();
	}
	
//	TODO: Add Javadocs!
	public static Vector3 x() {
		return x(1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 x(final float x) {
		return new Vector3(x, 0.0F, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 y() {
		return y(1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 y(final float y) {
		return new Vector3(0.0F, y, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 z() {
		return z(1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Vector3 z(final float z) {
		return new Vector3(0.0F, 0.0F, z);
	}
}