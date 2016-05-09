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

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

//TODO: Add Javadocs!
public final class OrthoNormalBasis {
//	TODO: Add Javadocs!
	public final Vector3 u;
	
//	TODO: Add Javadocs!
	public final Vector3 v;
	
//	TODO: Add Javadocs!
	public final Vector3 w;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis() {
		this(Vector3.z());
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis(final Point3 eye, final Point3 lookAt, final Vector3 up) {
		this.w = Vector3.direction(eye, lookAt).normalize();
		this.u = up.crossProduct(this.w).normalize();
		this.v = this.w.crossProduct(this.u);
	}
	
//	TODO: Add Javadocs!
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
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis(final Vector3 w, final Vector3 v) {
		this.w = w.normalize();
		this.u = v.crossProduct(this.w).normalize();
		this.v = this.w.crossProduct(this.u);
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis(final Vector3 w, final Vector3 v, final Vector3 u) {
		this.w = Objects.requireNonNull(w, "w == null");
		this.v = Objects.requireNonNull(v, "v == null");
		this.u = Objects.requireNonNull(u, "u == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis flipU() {
		return new OrthoNormalBasis(this.w, this.v, this.u.negate());
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis flipV() {
		return new OrthoNormalBasis(this.w, this.v.negate(), this.u);
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis flipW() {
		return new OrthoNormalBasis(this.w.negate(), this.v, this.u);
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis swapUV() {
		return new OrthoNormalBasis(this.w, this.u, this.v);
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis swapVW() {
		return new OrthoNormalBasis(this.v, this.w, this.u);
	}
	
//	TODO: Add Javadocs!
	public OrthoNormalBasis swapWU() {
		return new OrthoNormalBasis(this.u, this.v, this.w);
	}
}