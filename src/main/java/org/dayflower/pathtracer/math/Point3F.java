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

import static org.dayflower.pathtracer.math.MathF.abs;
import static org.dayflower.pathtracer.math.MathF.max;
import static org.dayflower.pathtracer.math.MathF.min;
import static org.dayflower.pathtracer.math.MathF.sqrt;

import java.util.Objects;

import org.dayflower.pathtracer.util.Strings;

/**
 * A {@code Point3F} denotes a point in 3D-space.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Point3F {
	/**
	 * The maximum {@code Point3F}.
	 */
	public static final Point3F MAXIMUM = maximum();
	
	/**
	 * The minimum {@code Point3F}.
	 */
	public static final Point3F MINIMUM = minimum();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	 * Constructs a new {@code Point3F} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Point3F(0.0F, 0.0F, 0.0F)
	 * }
	 * </pre>
	 */
	public Point3F() {
		this(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Point3F} instance given its coordinates.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 */
	public Point3F(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructs a new {@code Point3F} instance given a {@link Vector3F}.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new Point3F(v.x, v.y, v.z)
	 * }
	 * </pre>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@code Vector3}
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Point3F(final Vector3F v) {
		this(v.x, v.y, v.z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code Point3F} offset from this {@code Point3F} in the direction of {@code v} and with the distance of {@code t}.
	 * <p>
	 * If {@code v} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@link Vector3F} with the direction of the {@code Point3F} to return
	 * @param t the distance to the {@code Point3F} to return
	 * @return a {@code Point3F} offset from this {@code Point3F} in the direction of {@code v} and with the distance of {@code t}
	 * @throws NullPointerException thrown if, and only if, {@code v} is {@code null}
	 */
	public Point3F pointAt(final Vector3F v, final float t) {
		return new Point3F(this.x + v.x * t, this.y + v.y * t, this.z + v.z * t);
	}
	
	/**
	 * Performs a transformation.
	 * <p>
	 * Returns a new {@code Point3F} with the result of the transformation.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m the {@link Matrix44F} to perform the transformation with
	 * @return a new {@code Point3F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public Point3F transform(final Matrix44F m) {
		final float x = m.element00 * this.x + m.element01 * this.y + m.element02 * this.z + m.element03 * 1.0F;
		final float y = m.element10 * this.x + m.element11 * this.y + m.element12 * this.z + m.element13 * 1.0F;
		final float z = m.element20 * this.x + m.element21 * this.y + m.element22 * this.z + m.element23 * 1.0F;
		
		return new Point3F(x, y, z);
	}
	
	/**
	 * Performs a transformation and a divide.
	 * <p>
	 * Returns a new {@code Point3F} with the result of the transformation and division.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m the {@link Matrix44F} to perform the transformation with
	 * @return a new {@code Point3F} with the result of the transformation and division
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public Point3F transformAndDivide(final Matrix44F m) {
		final float x = m.element00 * this.x + m.element01 * this.y + m.element02 * this.z + m.element03 * 1.0F;
		final float y = m.element10 * this.x + m.element11 * this.y + m.element12 * this.z + m.element13 * 1.0F;
		final float z = m.element20 * this.x + m.element21 * this.y + m.element22 * this.z + m.element23 * 1.0F;
		final float w = m.element30 * this.x + m.element31 * this.y + m.element32 * this.z + m.element33 * 1.0F;
		
		return MathF.equals(w, 1.0F) ? new Point3F(x / w, y / w, z / w) : new Point3F(x, y, z);
	}
	
	/**
	 * Performs a translation on the X-axis.
	 * <p>
	 * Returns a new {@code Point3F} with the result of the translation.
	 * 
	 * @param x a delta value to add to the value of the X-coordinate
	 * @return a new {@code Point3F} with the result of the translation
	 */
	public Point3F translateX(final float x) {
		return new Point3F(this.x + x, this.y, this.z);
	}
	
	/**
	 * Performs a translation on the Y-axis.
	 * <p>
	 * Returns a new {@code Point3F} with the result of the translation.
	 * 
	 * @param y a delta value to add to the value of the Y-coordinate
	 * @return a new {@code Point3F} with the result of the translation
	 */
	public Point3F translateY(final float y) {
		return new Point3F(this.x, this.y + y, this.z);
	}
	
	/**
	 * Performs a translation on the Z-axis.
	 * <p>
	 * Returns a new {@code Point3F} with the result of the translation.
	 * 
	 * @param z a delta value to add to the value of the Z-coordinate
	 * @return a new {@code Point3F} with the result of the translation
	 */
	public Point3F translateZ(final float z) {
		return new Point3F(this.x, this.y, this.z + z);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Point3F} instance.
	 * 
	 * @return a {@code String} representation of this {@code Point3F} instance
	 */
	@Override
	public String toString() {
		return String.format("new Point3F(%s, %s, %s)", Strings.toNonScientificNotation(this.x), Strings.toNonScientificNotation(this.y), Strings.toNonScientificNotation(this.z));
	}
	
	/**
	 * Compares {@code object} to this {@code Point3F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Point3F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Point3F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Point3F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Point3F)) {
			return false;
		} else if(Float.compare(this.x, Point3F.class.cast(object).x) != 0) {
			return false;
		} else if(Float.compare(this.y, Point3F.class.cast(object).y) != 0) {
			return false;
		} else if(Float.compare(this.z, Point3F.class.cast(object).z) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, {@code p} is contained in the sphere defined with the center as this {@code Point3F} instance and a radius of {@code radius}, {@code false} otherwise.
	 * <p>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param p the {@code Point3F} instance to check
	 * @param radius the radius to use
	 * @return {@code true} if, and only if, {@code p} is contained in the sphere defined with the center as this {@code Point3F} instance and a radius of {@code radius}, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public boolean isWithinSphereRadius(final Point3F p, final float radius) {
		final float x = abs(this.x - p.x);
		final float y = abs(this.y - p.y);
		final float z = abs(this.z - p.z);
		
		return x * x + y * y + z * z < radius;
	}
	
	/**
	 * Returns the distance from this {@code Point3F} to {@code p}.
	 * <p>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param p a {@code Point3F} instance
	 * @return the distance from this {@code Point3F} to {@code p}
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public float distanceTo(final Point3F p) {
		return sqrt(distanceToSquared(p));
	}
	
	/**
	 * Returns the distance from this {@code Point3F} to {@code p} in squared form.
	 * <p>
	 * If {@code p} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param p a {@code Point3F} instance
	 * @return the distance from this {@code Point3F} to {@code p} in squared form
	 * @throws NullPointerException thrown if, and only if, {@code p} is {@code null}
	 */
	public float distanceToSquared(final Point3F p) {
		final float deltaX = this.x - p.x;
		final float deltaY = this.y - p.y;
		final float deltaZ = this.z - p.z;
		
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}
	
	/**
	 * Returns a hash code for this {@code Point3F} instance.
	 * 
	 * @return a hash code for this {@code Point3F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the center {@code Point3F} given the bounds represented by {@code a} and {@code b}.
	 * <p>
	 * If either {@code a} or {@code b} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @return the center {@code Point3F} given the bounds represented by {@code a} and {@code b}
	 * @throws NullPointerException thrown if, and only if, either {@code a} or {@code b} are {@code null}
	 */
	public static Point3F center(final Point3F a, final Point3F b) {
		return new Point3F((a.x + b.x) * 0.5F, (a.y + b.y) * 0.5F, (a.z + b.z) * 0.5F);  
	}
	
	/**
	 * Returns the maximum {@code Point3F}.
	 * 
	 * @return the maximum {@code Point3F}
	 */
	public static Point3F maximum() {
		return new Point3F(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	/**
	 * Returns the maximum {@code Point3F} given the bounds represented by {@code a} and {@code b}.
	 * <p>
	 * If either {@code a} or {@code b} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @return the maximum {@code Point3F} given the bounds represented by {@code a} and {@code b}
	 * @throws NullPointerException thrown if, and only if, either {@code a} or {@code b} are {@code null}
	 */
	public static Point3F maximum(final Point3F a, final Point3F b) {
		final float maximumX = max(a.x, b.x);
		final float maximumY = max(a.y, b.y);
		final float maximumZ = max(a.z, b.z);
		
		return new Point3F(maximumX, maximumY, maximumZ);
	}
	
	/**
	 * Returns the maximum {@code Point3F} given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the maximum {@code Point3F} given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static Point3F maximum(final Point3F a, final Point3F b, final Point3F c) {
		final float maximumX = max(a.x, b.x, c.x);
		final float maximumY = max(a.y, b.y, c.y);
		final float maximumZ = max(a.z, b.z, c.z);
		
		return new Point3F(maximumX, maximumY, maximumZ);
	}
	
	/**
	 * Returns the minimum {@code Point3F}.
	 * 
	 * @return the minimum {@code Point3F}
	 */
	public static Point3F minimum() {
		return new Point3F(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	}
	
	/**
	 * Returns the minimum {@code Point3F} given the bounds represented by {@code a} and {@code b}.
	 * <p>
	 * If either {@code a} or {@code b} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @return the minimum {@code Point3F} given the bounds represented by {@code a} and {@code b}
	 * @throws NullPointerException thrown if, and only if, either {@code a} or {@code b} are {@code null}
	 */
	public static Point3F minimum(final Point3F a, final Point3F b) {
		final float minimumX = min(a.x, b.x);
		final float minimumY = min(a.y, b.y);
		final float minimumZ = min(a.z, b.z);
		
		return new Point3F(minimumX, minimumY, minimumZ);
	}
	
	/**
	 * Returns the minimum {@code Point3F} given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the minimum {@code Point3F} given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static Point3F minimum(final Point3F a, final Point3F b, final Point3F c) {
		final float minimumX = min(a.x, b.x, c.x);
		final float minimumY = min(a.y, b.y, c.y);
		final float minimumZ = min(a.z, b.z, c.z);
		
		return new Point3F(minimumX, minimumY, minimumZ);
	}
	
	/**
	 * Returns the maximum X-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the maximum X-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float maximumX(final Point3F a, final Point3F b, final Point3F c) {
		return a.x > b.x && a.x > c.x ? a.x : b.x > c.x ? b.x : c.x;
	}
	
	/**
	 * Returns the maximum Y-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the maximum Y-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float maximumY(final Point3F a, final Point3F b, final Point3F c) {
		return a.y > b.y && a.y > c.y ? a.y : b.y > c.y ? b.y : c.y;
	}
	
	/**
	 * Returns the maximum Z-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the maximum Z-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float maximumZ(final Point3F a, final Point3F b, final Point3F c) {
		return a.z > b.z && a.z > c.z ? a.z : b.z > c.z ? b.z : c.z;
	}
	
	/**
	 * Returns the minimum X-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the minimum X-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float minimumX(final Point3F a, final Point3F b, final Point3F c) {
		return a.x < b.x && a.x < c.x ? a.x : b.x < c.x ? b.x : c.x;
	}
	
	/**
	 * Returns the minimum Y-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the minimum Y-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float minimumY(final Point3F a, final Point3F b, final Point3F c) {
		return a.y < b.y && a.y < c.y ? a.y : b.y < c.y ? b.y : c.y;
	}
	
	/**
	 * Returns the minimum Z-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a one of the {@code Point3F}s defining the bounds
	 * @param b one of the {@code Point3F}s defining the bounds
	 * @param c one of the {@code Point3F}s defining the bounds
	 * @return the minimum Z-coordinate given the bounds represented by {@code a}, {@code b} and {@code c}
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public static float minimumZ(final Point3F a, final Point3F b, final Point3F c) {
		return a.z < b.z && a.z < c.z ? a.z : b.z < c.z ? b.z : c.z;
	}
}