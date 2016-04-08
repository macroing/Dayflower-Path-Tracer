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
import static org.dayflower.pathtracer.math.Math2.atan2;
import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.sqrt;

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.math.Math2;

//TODO: Add Javadocs!
public final class Quaternion {
	private static final float EPSILON = 1000.0F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public final float w;
	
//	TODO: Add Javadocs!
	public final float x;
	
//	TODO: Add Javadocs!
	public final float y;
	
//	TODO: Add Javadocs!
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Quaternion(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
//	TODO: Add Javadocs!
	public Quaternion(final Matrix44 m) {
		final float trace = m.e11 + m.e22 + m.e33;
		
		final int type = trace > 0.0F ? 1 : m.e11 > m.e22 && m.e11 > m.e33 ? 2 : m.e22 > m.e33 ? 3 : 4;
		
		final float s = type == 1 ? 0.5F / sqrt(trace + 1.0F) : type == 2 ? 2.0F * sqrt(1.0F + m.e11 - m.e22 - m.e33) : type == 3 ? 2.0F * sqrt(1.0F + m.e22 - m.e11 - m.e33) : 2.0F * sqrt(1.0F + m.e33 - m.e11 - m.e22);
		final float x = type == 1 ? (m.e23 - m.e32) * s : type == 2 ? 0.25F * s : type == 3 ? (m.e21 + m.e12) / s : (m.e31 + m.e13) / s;
		final float y = type == 1 ? (m.e31 - m.e13) * s : type == 2 ? (m.e21 + m.e12) / s : type == 3 ? 0.25F * s : (m.e23 + m.e32) / s;
		final float z = type == 1 ? (m.e12 - m.e21) * s : type == 2 ? (m.e31 + m.e13) / s : type == 3 ? (m.e32 + m.e23) / s : 0.25F * s;
		final float w = type == 1 ? 0.25F / s : type == 2 ? (m.e23 - m.e32) / s : type == 3 ? (m.e31 - m.e13) / s : (m.e12 - m.e21) / s;
		final float length = sqrt(x * x + y * y + z * z + w * w);
		
		this.x = x / length;
		this.y = y / length;
		this.z = z / length;
		this.w = w / length;
	}
	
//	TODO: Add Javadocs!
	public Quaternion(final Vector3 v, final float angle) {
		final float sinHalfAngle = sin(angle / 2.0F);
		final float cosHalfAngle = cos(angle / 2.0F);
		
		this.x = v.x * sinHalfAngle;
		this.y = v.y * sinHalfAngle;
		this.z = v.z * sinHalfAngle;
		this.w = cosHalfAngle;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public float dotProduct(final Quaternion q) {
		return this.x * q.x + this.y * q.y + this.z * q.z + this.w * q.w;
	}
	
//	TODO: Add Javadocs!
	public float length() {
		return sqrt(lengthSquared());
	}
	
//	TODO: Add Javadocs!
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}
	
//	TODO: Add Javadocs!
	public Quaternion add(final Quaternion q) {
		return new Quaternion(this.x + q.x, this.y + q.y, this.z + q.z, this.w + q.w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion conjugate() {
		return new Quaternion(-this.x, -this.y, -this.z, this.w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion divide(final float s) {
		return new Quaternion(this.x / s, this.y / s, this.z / s, this.w / s);
	}
	
//	TODO: Add Javadocs!
	public Quaternion multiply(final float s) {
		return new Quaternion(this.x * s, this.y * s, this.z * s, this.w * s);
	}
	
//	TODO: Add Javadocs!
	public Quaternion multiply(final Quaternion q) {
		final float x = this.x * q.w + this.w * q.x + this.y * q.z - this.z * q.y;
		final float y = this.y * q.w + this.w * q.y + this.z * q.x - this.x * q.z;
		final float z = this.z * q.w + this.w * q.z + this.x * q.y - this.y * q.x;
		final float w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
		
		return new Quaternion(x, y, z, w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion multiply(final Vector3 v) {
		final float x = this.w * v.x + this.y * v.z - this.z * v.y;
		final float y = this.w * v.y + this.z * v.x - this.x * v.z;
		final float z = this.w * v.z + this.x * v.y - this.y * v.x;
		final float w = -this.x * v.x - this.y * v.y - this.z * v.z;
		
		return new Quaternion(x, y, z, w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion multiply(final Vector4 v) {
		final float x = this.w * v.x + this.y * v.z - this.z * v.y;
		final float y = this.w * v.y + this.z * v.x - this.x * v.z;
		final float z = this.w * v.z + this.x * v.y - this.y * v.x;
		final float w = -this.x * v.x - this.y * v.y - this.z * v.z;
		
		return new Quaternion(x, y, z, w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion negate() {
		return new Quaternion(-this.x, -this.y, -this.z, -this.w);
	}
	
//	TODO: Add Javadocs!
	public Quaternion normalize() {
		return divide(length());
	}
	
//	TODO: Add Javadocs!
	public Quaternion normalizedLinearInterpolation(final Quaternion q, final float fraction, final boolean isInterpolatingShortest) {
		final Quaternion q0 = isInterpolatingShortest && dotProduct(q) < 0.0D ? q.negate() : q;
		final Quaternion q1 = q0.subtract(this);
		final Quaternion q2 = q1.multiply(fraction);
		final Quaternion q3 = q2.add(this);
		final Quaternion q4 = q3.normalize();
		
		return q4;
	}
	
//	TODO: Add Javadocs!
	public Quaternion sphericalLinearInterpolation(final Quaternion q, final float fraction, final boolean isInterpolatingShortest) {
		final float cos0 = dotProduct(q);
		final float cos1 = isInterpolatingShortest && cos0 < 0.0F ? -cos0 : cos0;
		
		final Quaternion q0 = Math2.equals(cos0, cos1) ? q : q.negate();
		
		if(abs(cos1) >= 1.0D - EPSILON) {
			return normalizedLinearInterpolation(q0, fraction, false);
		}
		
		final float sin = sqrt(1.0F - cos1 * cos1);
		final float angle = atan2(sin, cos1);
		final float s0 = sin((1.0F - fraction) * angle) / sin;
		final float s1 = sin(fraction * angle) / sin;
		
		final Quaternion q1 = multiply(s0);
		final Quaternion q2 = q0.multiply(s1);
		final Quaternion q3 = q1.add(q2);
		
		return q3;
	}
	
//	TODO: Add Javadocs!
	public Quaternion subtract(final Quaternion q) {
		return new Quaternion(this.x - q.x, this.y - q.y, this.z - q.z, this.w - q.w);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Quaternion lookAtRotation(final Point3 eye, final Point3 lookAt, final Vector3 up) {
		return new Quaternion(Matrix44.rotation(Vector3.direction(eye, lookAt), up));
	}
}