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

import java.util.Objects;

import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Shape;

/**
 * A {@link Shape} implementation that implements a plane.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Plane implements Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_A_OFFSET = 0;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_B_OFFSET = 1;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_C_OFFSET = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET = 3;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 4;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 3;
	
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
	 * If either {@code surface}, {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface a {@link Surface} denoting the surface of this {@code Plane}
	 * @param a a {@link Point3F} denoting the point A
	 * @param b a {@code Point3} denoting the point A
	 * @param c a {@code Point3} denoting the point A
	 * @throws NullPointerException thrown if, and only if, either {@code surface}, {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public Plane(final Point3F a, final Point3F b, final Point3F c) {
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
		this.surfaceNormal = Vector3F.normalNormalized(this.a, this.b, this.c);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	 * Returns the type of this {@code Plane} instance.
	 * 
	 * @return the type of this {@code Plane} instance
	 */
	@Override
	public int getType() {
		return TYPE;
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
}