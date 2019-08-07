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

import java.util.Objects;

/**
 * An {@code OrthoNormalBasis33F} denotes an orthonormal basis.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class OrthoNormalBasis33F {
	/**
	 * A {@link Vector3F} pointing in the U-direction.
	 */
	public final Vector3F u;
	
	/**
	 * A {@link Vector3F} pointing in the V-direction.
	 */
	public final Vector3F v;
	
	/**
	 * A {@link Vector3F} pointing in the W-direction.
	 */
	public final Vector3F w;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new OrthoNormalBasis33F(Vector3F.z(), Vector3F.y(), Vector3F.x())
	 * }
	 * </pre>
	 */
	public OrthoNormalBasis33F() {
		this(Vector3F.z(), Vector3F.y(), Vector3F.x());
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance given {@code eye} and {@code lookAt}.
	 * <p>
	 * Calling this constructor is equivalent to the following:
	 * <pre>
	 * {@code
	 * new OrthoNormalBasis33F(eye, lookAt, Vector3F.y())
	 * }
	 * </pre>
	 * If either {@code eye} or {@code lookAt} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye a {@link Point3F} denoting the eye from which the W-direction is computed
	 * @param lookAt a {@code Point3F} used in the computation of the W-direction
	 * @throws NullPointerException thrown if, and only if, either {@code eye} or {@code lookAt} are {@code null}
	 */
	public OrthoNormalBasis33F(final Point3F eye, final Point3F lookAt) {
		this(eye, lookAt, Vector3F.y());
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance given {@code eye}, {@code lookAt} and {@code v}.
	 * <p>
	 * The W-direction is computed by {@code Vector3F.direction(lookAt, eye)}, which is the opposite direction.
	 * <p>
	 * If either {@code eye}, {@code lookAt} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye a {@link Point3F} denoting the eye from which the W-direction is computed
	 * @param lookAt a {@code Point3F} used in the computation of the W-direction
	 * @param v a {@link Vector3F} denoting the V-direction
	 * @throws NullPointerException thrown if, and only if, either {@code eye}, {@code lookAt} or {@code v} are {@code null}
	 */
	public OrthoNormalBasis33F(final Point3F eye, final Point3F lookAt, final Vector3F v) {
		this(Vector3F.direction(lookAt, eye), v);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance given {@code w}.
	 * <p>
	 * If {@code w} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3F} pointing in the W-direction
	 * @throws NullPointerException thrown if, and only if, {@code w} is {@code null}
	 */
	public OrthoNormalBasis33F(final Vector3F w) {
		this.w = w.normalize();
		this.v = doCalculateV(this.w);
		this.u = this.v.crossProduct(this.w);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance given {@code w} and {@code v}.
	 * <p>
	 * If either {@code w} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3F} pointing in the W-direction
	 * @param v a {@code Vector3F} pointing in the V-direction
	 * @throws NullPointerException thrown if, and only if, either {@code w} or {@code v} are {@code null}
	 */
	public OrthoNormalBasis33F(final Vector3F w, final Vector3F v) {
		this.w = w.normalize();
		this.u = v.crossProduct(this.w).normalize();
		this.v = this.w.crossProduct(this.u);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis33F} instance given {@code w}, {@code v} and {@code u}.
	 * <p>
	 * If either {@code w}, {@code v} or {@code u} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3F} pointing in the W-direction
	 * @param v a {@code Vector3F} pointing in the V-direction
	 * @param u a {@code Vector3F} pointing in the U-direction
	 * @throws NullPointerException thrown if, and only if, either {@code w}, {@code v} or {@code u} are {@code null}
	 */
	public OrthoNormalBasis33F(final Vector3F w, final Vector3F v, final Vector3F u) {
		this.w = Objects.requireNonNull(w, "w == null");
		this.v = Objects.requireNonNull(v, "v == null");
		this.u = Objects.requireNonNull(u, "u == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Flips the U-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the U-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the U-direction flipped
	 */
	public OrthoNormalBasis33F flipU() {
		return new OrthoNormalBasis33F(this.w, this.v, this.u.negate());
	}
	
	/**
	 * Flips the V-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the V-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the V-direction flipped
	 */
	public OrthoNormalBasis33F flipV() {
		return new OrthoNormalBasis33F(this.w, this.v.negate(), this.u);
	}
	
	/**
	 * Flips the W-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the W-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the W-direction flipped
	 */
	public OrthoNormalBasis33F flipW() {
		return new OrthoNormalBasis33F(this.w.negate(), this.v, this.u);
	}
	
	/**
	 * Swaps the U- and V-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the U- and V-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapVU()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the U- and V-directions swapped
	 */
	public OrthoNormalBasis33F swapUV() {
		return new OrthoNormalBasis33F(this.w, this.u, this.v);
	}
	
	/**
	 * Swaps the U- and W-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the U- and W-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapWU()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the U- and W-directions swapped
	 */
	public OrthoNormalBasis33F swapUW() {
		return new OrthoNormalBasis33F(this.u, this.v, this.w);
	}
	
	/**
	 * Swaps the V- and U-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the V- and U-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapUV()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the V- and U-directions swapped
	 */
	public OrthoNormalBasis33F swapVU() {
		return new OrthoNormalBasis33F(this.w, this.u, this.v);
	}
	
	/**
	 * Swaps the V- and W-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the V- and W-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapWV()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the V- and W-directions swapped
	 */
	public OrthoNormalBasis33F swapVW() {
		return new OrthoNormalBasis33F(this.v, this.w, this.u);
	}
	
	/**
	 * Swaps the W- and U-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the W- and U-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapUW()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the W- and U-directions swapped
	 */
	public OrthoNormalBasis33F swapWU() {
		return new OrthoNormalBasis33F(this.u, this.v, this.w);
	}
	
	/**
	 * Swaps the W- and V-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the W- and V-directions swapped.
	 * <p>
	 * This method does the same thing as {@link #swapVW()}.
	 * 
	 * @return a new {@code OrthoNormalBasis33F} with the W- and V-directions swapped
	 */
	public OrthoNormalBasis33F swapWV() {
		return new OrthoNormalBasis33F(this.v, this.w, this.u);
	}
	
	/**
	 * Performs a transformation.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis33F} with the result of the transformation.
	 * <p>
	 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param m a {@link Matrix44F}
	 * @return a new {@code OrthoNormalBasis33F} with the result of the transformation
	 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
	 */
	public OrthoNormalBasis33F transform(final Matrix44F m) {
		return new OrthoNormalBasis33F(this.w.transform(m), this.v.transform(m), this.u.transform(m));
	}
	
	/**
	 * Returns a {@code String} representation of this {@code OrthoNormalBasis33F} instance.
	 * 
	 * @return a {@code String} representation of this {@code OrthoNormalBasis33F} instance
	 */
	@Override
	public String toString() {
		return String.format("new OrthoNormalBasis33F(%s, %s, %s)", this.w, this.v, this.u);
	}
	
	/**
	 * Returns the {@link Vector3F} pointing in the U-direction.
	 * 
	 * @return the {@code Vector3F} pointing in the U-direction
	 */
	public Vector3F getU() {
		return this.u;
	}
	
	/**
	 * Returns the {@link Vector3F} pointing in the V-direction.
	 * 
	 * @return the {@code Vector3F} pointing in the V-direction
	 */
	public Vector3F getV() {
		return this.v;
	}
	
	/**
	 * Returns the {@link Vector3F} pointing in the W-direction.
	 * 
	 * @return the {@code Vector3F} pointing in the W-direction
	 */
	public Vector3F getW() {
		return this.w;
	}
	
	/**
	 * Compares {@code object} to this {@code OrthoNormalBasis33F} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code OrthoNormalBasis33F}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code OrthoNormalBasis33F} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code OrthoNormalBasis33F}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof OrthoNormalBasis33F)) {
			return false;
		} else if(!Objects.equals(this.u, OrthoNormalBasis33F.class.cast(object).u)) {
			return false;
		} else if(!Objects.equals(this.v, OrthoNormalBasis33F.class.cast(object).v)) {
			return false;
		} else if(!Objects.equals(this.w, OrthoNormalBasis33F.class.cast(object).w)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code OrthoNormalBasis33F} instance.
	 * 
	 * @return a hash code for this {@code OrthoNormalBasis33F} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.u, this.v, this.w);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Vector3F doCalculateV(final Vector3F w) {
		final float absWX = abs(w.x);
		final float absWY = abs(w.y);
		final float absWZ = abs(w.z);
		
		final float vX = absWX < absWY && absWX < absWZ ? 0.0F : absWY < absWZ ? w.z : w.y;
		final float vY = absWX < absWY && absWX < absWZ ? w.z : absWY < absWZ ? 0.0F : -w.x;
		final float vZ = absWX < absWY && absWX < absWZ ? -w.y : absWY < absWZ ? -w.x : 0.0F;
		
		return new Vector3F(vX, vY, vZ).normalize();
	}
}