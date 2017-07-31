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

import static org.dayflower.pathtracer.math.Math2.abs;

import java.util.Objects;

/**
 * An {@code OrthoNormalBasis} denotes an orthonormal basis.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class OrthoNormalBasis {
	/**
	 * A {@link Vector3} pointing in the U-direction.
	 */
	public final Vector3 u;
	
	/**
	 * A {@link Vector3} pointing in the V-direction.
	 */
	public final Vector3 v;
	
	/**
	 * A {@link Vector3} pointing in the W-direction.
	 */
	public final Vector3 w;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code OrthoNormalBasis} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new OrthoNormalBasis(Vector3.z())}.
	 */
	public OrthoNormalBasis() {
		this(Vector3.z());
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis} instance given {@code eye}, {@code lookAt} and {@code up}.
	 * <p>
	 * If either {@code eye}, {@code lookAt} or {@code up} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param eye a {@link Point3} denoting the eye from which the W-direction is computed
	 * @param lookAt a {@code Point3} used in the computation of the W-direction
	 * @param up a {@link Vector3} denoting the up vector
	 * @throws NullPointerException thrown if, and only if, either {@code eye}, {@code lookAt} or {@code up} are {@code null}
	 */
	public OrthoNormalBasis(final Point3 eye, final Point3 lookAt, final Vector3 up) {
		this.w = Vector3.direction(eye, lookAt).normalize();
		this.u = up.crossProduct(this.w).normalize();
		this.v = this.w.crossProduct(this.u);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis} instance given {@code w}.
	 * <p>
	 * If {@code w} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3}
	 * @throws NullPointerException thrown if, and only if, {@code w} is {@code null}
	 */
	public OrthoNormalBasis(final Vector3 w) {
		this.w = w.normalize();
		
		final float absWX = abs(this.w.x);
		final float absWY = abs(this.w.y);
		final float absWZ = abs(this.w.z);
		
		final float vX = absWX < absWY && absWX < absWZ ? 0.0F : absWY < absWZ ? this.w.z : this.w.y;
		final float vY = absWX < absWY && absWX < absWZ ? this.w.z : absWY < absWZ ? 0.0F : -this.w.x;
		final float vZ = absWX < absWY && absWX < absWZ ? -this.w.y : absWY < absWZ ? -this.w.x : 0.0F;
		
		this.v = new Vector3(vX, vY, vZ).normalize();
		this.u = this.v.crossProduct(this.w);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis} instance given {@code w} and {@code v}.
	 * <p>
	 * If either {@code w} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3} pointing in the W-direction
	 * @param v a {@code Vector3} pointing in the V-direction
	 * @throws NullPointerException thrown if, and only if, either {@code w} or {@code v} are {@code null}
	 */
	public OrthoNormalBasis(final Vector3 w, final Vector3 v) {
		this.w = w.normalize();
		this.u = v.crossProduct(this.w).normalize();
		this.v = this.w.crossProduct(this.u);
	}
	
	/**
	 * Constructs a new {@code OrthoNormalBasis} instance given {@code w}, {@code v} and {@code u}.
	 * <p>
	 * If either {@code w}, {@code v} or {@code u} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3} pointing in the W-direction
	 * @param v a {@code Vector3} pointing in the V-direction
	 * @param u a {@code Vector3} pointing in the U-direction
	 * @throws NullPointerException thrown if, and only if, either {@code w}, {@code v} or {@code u} are {@code null}
	 */
	public OrthoNormalBasis(final Vector3 w, final Vector3 v, final Vector3 u) {
		this.w = Objects.requireNonNull(w, "w == null");
		this.v = Objects.requireNonNull(v, "v == null");
		this.u = Objects.requireNonNull(u, "u == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code OrthoNormalBasis} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code OrthoNormalBasis}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code OrthoNormalBasis} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code OrthoNormalBasis}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof OrthoNormalBasis)) {
			return false;
		} else if(!Objects.equals(this.u, OrthoNormalBasis.class.cast(object).u)) {
			return false;
		} else if(!Objects.equals(this.v, OrthoNormalBasis.class.cast(object).v)) {
			return false;
		} else if(!Objects.equals(this.w, OrthoNormalBasis.class.cast(object).w)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code OrthoNormalBasis} instance.
	 * 
	 * @return a hash code for this {@code OrthoNormalBasis} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.u, this.v, this.w);
	}
	
	/**
	 * Flips the U-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the U-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the U-direction flipped
	 */
	public OrthoNormalBasis flipU() {
		return new OrthoNormalBasis(this.w, this.v, this.u.negate());
	}
	
	/**
	 * Flips the V-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the V-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the V-direction flipped
	 */
	public OrthoNormalBasis flipV() {
		return new OrthoNormalBasis(this.w, this.v.negate(), this.u);
	}
	
	/**
	 * Flips the W-direction.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the W-direction flipped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the W-direction flipped
	 */
	public OrthoNormalBasis flipW() {
		return new OrthoNormalBasis(this.w.negate(), this.v, this.u);
	}
	
	/**
	 * Swaps the U- and V-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the U- and V-directions swapped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the U- and V-directions swapped
	 */
	public OrthoNormalBasis swapUV() {
		return new OrthoNormalBasis(this.w, this.u, this.v);
	}
	
	/**
	 * Swaps the V- and W-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the V- and W-directions swapped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the V- and W-directions swapped
	 */
	public OrthoNormalBasis swapVW() {
		return new OrthoNormalBasis(this.v, this.w, this.u);
	}
	
	/**
	 * Swaps the W- and U-directions.
	 * <p>
	 * Returns a new {@code OrthoNormalBasis} with the W- and U-directions swapped.
	 * 
	 * @return a new {@code OrthoNormalBasis} with the W- and U-directions swapped
	 */
	public OrthoNormalBasis swapWU() {
		return new OrthoNormalBasis(this.u, this.v, this.w);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code OrthoNormalBasis} instance.
	 * 
	 * @return a {@code String} representation of this {@code OrthoNormalBasis} instance
	 */
	@Override
	public String toString() {
		return String.format("OrthoNormalBasis: [U=%s], [V=%s], [W=%s]", this.u, this.v, this.w);
	}
}