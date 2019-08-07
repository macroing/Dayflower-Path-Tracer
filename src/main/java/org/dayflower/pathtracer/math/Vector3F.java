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

import static org.dayflower.pathtracer.math.MathF.PI;
import static org.dayflower.pathtracer.math.MathF.cos;
import static org.dayflower.pathtracer.math.MathF.sin;
import static org.dayflower.pathtracer.math.MathF.sqrt;

import java.util.Objects;

import org.dayflower.pathtracer.util.Strings;

/**
 * A {@code Vector3F} denotes a 3-dimensional vector with elements denoted by X, Y and Z.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Vector3F {
	/**
	 * The element value along the X-axis.
	 */
	public final float x;
	
	/**
	 * The element value along the Y-axis.
	 */
	public final float y;
	
	/**
	 * The element value along the Z-axis.
	 */
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Vector3F} instance given its element values {@code 0.0F}, {@code 0.0F} and {@code 0.0F}.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Vector3F(0.0F, 0.0F, 0.0F)
	 * }
	 * </pre>
	 */
	public Vector3F() {
		this(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Vector3F} instance given its element values {@code x}, {@code y} and {@code z}.
	 * 
	 * @param x the element value along the X-axis
	 * @param y the element value along the Y-axis
	 * @param z the element value along the Z-axis
	 */
	public Vector3F(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructs a new {@code Vector3F} instance given a {@link Point3F}.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Vector3F(p.x, p.y, p.z);
	 * }
	 * </pre>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param p a {@code Point3F}
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public Vector3F(final Point3F p) {
		this(p.x, p.y, p.z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} representation of this {@code Vector3F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Vector3F} instance
	 */
	@Override
	public String toString() {
		return String.format("new Vector3F(%s, %s, %s)", Strings.toNonScientificNotation(this.x), Strings.toNonScientificNotation(this.y), Strings.toNonScientificNotation(this.z));
	}
	
	/**
	 * Adds {@code v} to this {@code Vector3F} instance.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the addition.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v the {@code Vector3F} to add
	 * @return a new {@code Vector3F} instance with the result of the addition
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Vector3F add(final Vector3F v) {
		return new Vector3F(this.x + v.x, this.y + v.y, this.z + v.z);
	}
	
	/**
	 * Computes the cross product between this {@code Vector3F} instance and {@code v}.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the computation.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@code Vector3F}
	 * @return a new {@code Vector3F} instance with the result of the computation
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Vector3F crossProduct(final Vector3F v) {
		return new Vector3F(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}
	
	/**
	 * Divides this {@code Vector3F} instance by {@code s}.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the division.
	 * 
	 * @param s the scalar value to divide by
	 * @return a new {@code Vector3F} instance with the result of the division
	 */
	public Vector3F divide(final float s) {
		return new Vector3F(this.x / s, this.y / s, this.z / s);
	}
	
	/**
	 * Multiplies this {@code Vector3F} instance with {@code s}.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the multiplication.
	 * 
	 * @param s the scalar value to multiply with
	 * @return a new {@code Vector3F} instance with the result of the multiplication
	 */
	public Vector3F multiply(final float s) {
		return new Vector3F(this.x * s, this.y * s, this.z * s);
	}
	
	/**
	 * Negates this {@code Vector3F} instance.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the negation.
	 * 
	 * @return a new {@code Vector3F} instance with the result of the negation
	 */
	public Vector3F negate() {
		return new Vector3F(-this.x, -this.y, -this.z);
	}
	
	/**
	 * Normalizes this {@code Vector3F} instance.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the normalization.
	 * 
	 * @return a new {@code Vector3F} instance with the result of the normalization
	 */
	public Vector3F normalize() {
		return divide(length());
	}
	
	/**
	 * Subtracts {@code v} from this {@code Vector3F} instance.
	 * <p>
	 * Returns a new {@code Vector3F} instance with the result of the subtraction.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v the {@code Vector3F} to subtract
	 * @return a new {@code Vector3F} instance with the result of the subtraction
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Vector3F subtract(final Vector3F v) {
		return new Vector3F(this.x - v.x, this.y - v.y, this.z - v.z);
	}
	
	/**
	 * Performs a transformation.
	 * <p>
	 * Returns a new {@code Vector3F} with the result of the transformation.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m the {@link Matrix44F} to perform the transformation with
	 * @return a new {@code Vector3F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public Vector3F transform(final Matrix44F m) {
		final float x = m.element00 * this.x + m.element01 * this.y + m.element02 * this.z + m.element03 * 0.0F;
		final float y = m.element10 * this.x + m.element11 * this.y + m.element12 * this.z + m.element13 * 0.0F;
		final float z = m.element20 * this.x + m.element21 * this.y + m.element22 * this.z + m.element23 * 0.0F;
		
		return new Vector3F(x, y, z);
	}
	
	/**
	 * Performs a transformation.
	 * <p>
	 * Returns a new {@code Vector3F} with the result of the transformation.
	 * <p>
	 * If {@code orthoNormalBasis} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param orthoNormalBasis an {@link OrthoNormalBasis33F}
	 * @return a new {@code Vector3F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code orthoNormalBasis} is {@code null}
	 */
	public Vector3F transform(final OrthoNormalBasis33F orthoNormalBasis) {
		final float x = this.x * orthoNormalBasis.u.x + this.y * orthoNormalBasis.v.x + this.z * orthoNormalBasis.w.x;
		final float y = this.x * orthoNormalBasis.u.y + this.y * orthoNormalBasis.v.y + this.z * orthoNormalBasis.w.y;
		final float z = this.x * orthoNormalBasis.u.z + this.y * orthoNormalBasis.v.z + this.z * orthoNormalBasis.w.z;
		
		return new Vector3F(x, y, z);
	}
	
	/**
	 * Performs a transformation in reverse order.
	 * <p>
	 * Returns a new {@code Vector3F} with the result of the transformation.
	 * <p>
	 * If {@code orthoNormalBasis} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param orthoNormalBasis an {@link OrthoNormalBasis33F}
	 * @return a new {@code Vector3F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code orthoNormalBasis} is {@code null}
	 */
	public Vector3F transformReverse(final OrthoNormalBasis33F orthoNormalBasis) {
		final float x = dotProduct(orthoNormalBasis.u);
		final float y = dotProduct(orthoNormalBasis.v);
		final float z = dotProduct(orthoNormalBasis.w);
		
		return new Vector3F(x, y, z);
	}
	
	/**
	 * Performs a transformation in transpose order.
	 * <p>
	 * Returns a new {@code Vector3F} with the result of the transformation.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m a {@link Matrix44F}
	 * @return a new {@code Vector3F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public Vector3F transformTranspose(final Matrix44F m) {
		final float x = m.element00 * this.x + m.element10 * this.y + m.element20 * this.z + m.element30 * 0.0F;
		final float y = m.element01 * this.x + m.element11 * this.y + m.element21 * this.z + m.element31 * 0.0F;
		final float z = m.element02 * this.x + m.element12 * this.y + m.element22 * this.z + m.element32 * 0.0F;
		
		return new Vector3F(x, y, z);
	}
	
	/**
	 * Compares {@code object} to this {@code Vector3F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Vector3F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Vector3F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Vector3F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Vector3F)) {
			return false;
		} else if(Float.compare(this.x, Vector3F.class.cast(object).x) != 0) {
			return false;
		} else if(Float.compare(this.y, Vector3F.class.cast(object).y) != 0) {
			return false;
		} else if(Float.compare(this.z, Vector3F.class.cast(object).z) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the dot product between this {@code Vector3F} instance and {@code v}.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@code Vector3F}
	 * @return the dot product between this {@code Vector3F} instance and {@code v}
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public float dotProduct(final Vector3F v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	/**
	 * Returns the length of this {@code Vector3F} instance.
	 * 
	 * @return the length of this {@code Vector3F} instance
	 */
	public float length() {
		return sqrt(lengthSquared());
	}
	
	/**
	 * Returns the length of this {@code Vector3F} instance in squared form.
	 * 
	 * @return the length of this {@code Vector3F} instance in squared form
	 */
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	/**
	 * Returns a hash code for this {@code Vector3F} instance.
	 * 
	 * @return a hash code for this {@code Vector3F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code Vector3F} based on the U- and V-coordinates {@code u} and {@code v}.
	 * 
	 * @param u the U-coordinate
	 * @param v the V-coordinate
	 * @return a {@code Vector3F} based on the U- and V-coordinates {@code u} and {@code v}
	 */
	public static Vector3F direction(final float u, final float v) {
		final float theta = u * 2.0F * PI;
		final float phi = v * PI;
		final float sinPhi = sin(phi);
		final float x = -sinPhi * cos(theta);
		final float y = cos(phi);
		final float z = sinPhi * sin(theta);
		
		return new Vector3F(x, y, z);
	}
	
	/**
	 * Returns a {@code Vector3F} pointing in the direction from {@code eye} to {@code lookAt}.
	 * <p>
	 * If either {@code eye} or {@code lookAt} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye a {@link Point3F} denoting the eye to look from
	 * @param lookAt a {@code Point3F} denoting the target to look at
	 * @return a {@code Vector3F} pointing in the direction from {@code eye} to {@code lookAt}
	 * @throws NullPointerException thrown if, and only if, either {@code eye} or {@code lookAt} are {@code null}
	 */
	public static Vector3F direction(final Point3F eye, final Point3F lookAt) {
		return new Vector3F(lookAt.x - eye.x, lookAt.y - eye.y, lookAt.z - eye.z);
	}
	
	/**
	 * Returns a {@code Vector3F} denoting the normal of the plane defined by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the three {@link Point3F}s in the plane
	 * @param b one of the three {@code Point3F}s in the plane
	 * @param c one of the three {@code Point3F}s in the plane
	 * @return a {@code Vector3F} denoting the normal of the plane defined by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static Vector3F normal(final Point3F a, final Point3F b, final Point3F c) {
		final Vector3F edge0 = direction(a, b);
		final Vector3F edge1 = direction(a, c);
		final Vector3F normal = edge0.crossProduct(edge1);
		
		return normal;
	}
	
	/**
	 * Returns a {@code Vector3F} denoting the normalized normal of the plane defined by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the three {@link Point3F}s in the plane
	 * @param b one of the three {@code Point3F}s in the plane
	 * @param c one of the three {@code Point3F}s in the plane
	 * @return a {@code Vector3F} denoting the normalized normal of the plane defined by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static Vector3F normalNormalized(final Point3F a, final Point3F b, final Point3F c) {
		return normal(a, b, c).normalize();
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(1.0F, 0.0F, 0.0F)}.
	 * 
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(1.0F, 0.0F, 0.0F)}
	 */
	public static Vector3F x() {
		return x(1.0F);
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(x, 0.0F, 0.0F)}.
	 * 
	 * @param x the element value along the X-axis
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(x, 0.0F, 0.0F)}
	 */
	public static Vector3F x(final float x) {
		return new Vector3F(x, 0.0F, 0.0F);
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 1.0F, 0.0F)}.
	 * 
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 1.0F, 0.0F)}
	 */
	public static Vector3F y() {
		return y(1.0F);
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, y, 0.0F)}.
	 * 
	 * @param y the element value along the Y-axis
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, y, 0.0F)}
	 */
	public static Vector3F y(final float y) {
		return new Vector3F(0.0F, y, 0.0F);
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 0.0F, 1.0F)}.
	 * 
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 0.0F, 1.0F)}
	 */
	public static Vector3F z() {
		return z(1.0F);
	}
	
	/**
	 * Returns a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 0.0F, z)}.
	 * 
	 * @param z the element value along the Z-axis
	 * @return a new {@code Vector3F} instance equivalent to {@code new Vector3F(0.0F, 0.0F, z)}
	 */
	public static Vector3F z(final float z) {
		return new Vector3F(0.0F, 0.0F, z);
	}
}