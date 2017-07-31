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
package org.dayflower.pathtracer.scene;

import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.sqrt;

import java.lang.reflect.Field;//TODO: Add Javadocs.

//TODO: Add Javadocs.
public final class Vector4 {
//	TODO: Add Javadocs.
	public final float w;
	
//	TODO: Add Javadocs.
	public final float x;
	
//	TODO: Add Javadocs.
	public final float y;
	
//	TODO: Add Javadocs.
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Vector4(final float x, final float y, final float z) {
		this(x, y, z, 1.0F);
	}
	
//	TODO: Add Javadocs.
	public Vector4(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float dotProduct(final Vector4 vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z + this.w * vector.w;
	}
	
//	TODO: Add Javadocs.
	public float length() {
		return sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
	}
	
//	TODO: Add Javadocs.
	public float maximum() {
		return Math.max(Math.max(this.x, this.y), Math.max(this.z, this.w));
	}
	
//	TODO: Add Javadocs.
	@Override
	public String toString() {
		return String.format("Vector4: [X=%s], [Y=%s], [Z=%s], [W=%s]", Float.toString(this.x), Float.toString(this.y), Float.toString(this.z), Float.toString(this.w));
	}
	
//	TODO: Add Javadocs.
	public Vector4 absolute() {
		return new Vector4(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z), Math.abs(this.w));
	}
	
//	TODO: Add Javadocs.
	public Vector4 add(final float scalar) {
		return new Vector4(this.x + scalar, this.y + scalar, this.z + scalar, this.w + scalar);
	}
	
//	TODO: Add Javadocs.
	public Vector4 add(final Vector4 vector) {
		return new Vector4(this.x + vector.x, this.y + vector.y, this.z + vector.z, this.w + vector.w);
	}
	
//	TODO: Add Javadocs.
	public Vector4 crossProduct(final Vector4 vector) {
		final float x = this.y * vector.z - this.z * vector.y;
		final float y = this.z * vector.x - this.x * vector.z;
		final float z = this.x * vector.y - this.y * vector.x;
		
		return new Vector4(x, y, z, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public Vector4 divide(final float scalar) {
		final float reciprocal = 1.0F / scalar;
		
		return new Vector4(this.x * reciprocal, this.y * reciprocal, this.z * reciprocal, this.w * reciprocal);
	}
	
//	TODO: Add Javadocs.
	public Vector4 divide(final Vector4 vector) {
		return new Vector4(this.x / vector.x, this.y / vector.y, this.z / vector.z, this.w / vector.w);
	}
	
//	TODO: Add Javadocs.
	public Vector4 linearInterpolation(final Vector4 vector, final float fraction) {
		return vector.subtract(this).multiply(fraction).add(this);
	}
	
//	TODO: Add Javadocs.
	public Vector4 multiply(final float scalar) {
		return new Vector4(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
	}
	
//	TODO: Add Javadocs.
	public Vector4 multiply(final Vector4 vector) {
		return new Vector4(this.x * vector.x, this.y * vector.y, this.z * vector.z, this.w * vector.w);
	}
	
//	TODO: Add Javadocs.
	public Vector4 normalize() {
		final float lengthReciprocal = 1.0F / length();
		
		return new Vector4(this.x * lengthReciprocal, this.y * lengthReciprocal, this.z * lengthReciprocal, this.w * lengthReciprocal);
	}
	
//	TODO: Add Javadocs.
	public Vector4 rotate(final Vector4 axis, final float angle) {
		final float sinAngle = sin(-angle);
		final float cosAngle = cos(-angle);
		
		return crossProduct(axis.multiply(sinAngle)).add((multiply(cosAngle)).add(axis.multiply(dotProduct(axis.multiply(1.0F - cosAngle)))));
	}
	
//	TODO: Add Javadocs.
	public Vector4 subtract(final float scalar) {
		return new Vector4(this.x - scalar, this.y - scalar, this.z - scalar, this.w - scalar);
	}
	
//	TODO: Add Javadocs.
	public Vector4 subtract(final Vector4 vector) {
		return new Vector4(this.x - vector.x, this.y - vector.y, this.z - vector.z, this.w - vector.w);
	}
	
//	TODO: Add Javadocs.
	public Vector4 transform(final Matrix44 m) {
		final float x = m.e11 * this.x + m.e12 * this.y + m.e13 * this.z + m.e14 * this.w;
		final float y = m.e21 * this.x + m.e22 * this.y + m.e23 * this.z + m.e24 * this.w;
		final float z = m.e31 * this.x + m.e32 * this.y + m.e33 * this.z + m.e34 * this.w;
		final float w = m.e41 * this.x + m.e42 * this.y + m.e43 * this.z + m.e44 * this.w;
		
		return new Vector4(x, y, z, w);
	}
}