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

import java.lang.reflect.Field;//TODO: Add Javadocs.

//TODO: Add Javadocs!
public final class Matrix44 {
//	TODO: Add Javadocs!
	public final float e11;
	
//	TODO: Add Javadocs!
	public final float e12;
	
//	TODO: Add Javadocs!
	public final float e13;
	
//	TODO: Add Javadocs!
	public final float e14;
	
//	TODO: Add Javadocs!
	public final float e21;
	
//	TODO: Add Javadocs!
	public final float e22;
	
//	TODO: Add Javadocs!
	public final float e23;
	
//	TODO: Add Javadocs!
	public final float e24;
	
//	TODO: Add Javadocs!
	public final float e31;
	
//	TODO: Add Javadocs!
	public final float e32;
	
//	TODO: Add Javadocs!
	public final float e33;
	
//	TODO: Add Javadocs!
	public final float e34;
	
//	TODO: Add Javadocs!
	public final float e41;
	
//	TODO: Add Javadocs!
	public final float e42;
	
//	TODO: Add Javadocs!
	public final float e43;
	
//	TODO: Add Javadocs!
	public final float e44;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	private Matrix44() {
		this(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	private Matrix44(final float e11, final float e12, final float e13, final float e14, final float e21, final float e22, final float e23, final float e24, final float e31, final float e32, final float e33, final float e34, final float e41, final float e42, final float e43, final float e44) {
		this.e11 = e11;
		this.e12 = e12;
		this.e13 = e13;
		this.e14 = e14;
		this.e21 = e21;
		this.e22 = e22;
		this.e23 = e23;
		this.e24 = e24;
		this.e31 = e31;
		this.e32 = e32;
		this.e33 = e33;
		this.e34 = e34;
		this.e41 = e41;
		this.e42 = e42;
		this.e43 = e43;
		this.e44 = e44;
	}
	
//	TODO: Add Javadocs!
	private Matrix44(final float[][] elements) {
		this.e11 = elements[0][0];
		this.e12 = elements[0][1];
		this.e13 = elements[0][2];
		this.e14 = elements[0][3];
		this.e21 = elements[1][0];
		this.e22 = elements[1][1];
		this.e23 = elements[1][2];
		this.e24 = elements[1][3];
		this.e31 = elements[2][0];
		this.e32 = elements[2][1];
		this.e33 = elements[2][2];
		this.e34 = elements[2][3];
		this.e41 = elements[3][0];
		this.e42 = elements[3][1];
		this.e43 = elements[3][2];
		this.e44 = elements[3][3];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Matrix44 multiply(final Matrix44 m) {
		final float e11 = this.e11 * m.e11 + this.e12 * m.e21 + this.e13 * m.e31 + this.e14 * m.e41;
		final float e12 = this.e11 * m.e12 + this.e12 * m.e22 + this.e13 * m.e32 + this.e14 * m.e42;
		final float e13 = this.e11 * m.e13 + this.e12 * m.e23 + this.e13 * m.e33 + this.e14 * m.e43;
		final float e14 = this.e11 * m.e14 + this.e12 * m.e24 + this.e13 * m.e34 + this.e14 * m.e44;
		
		final float e21 = this.e21 * m.e11 + this.e22 * m.e21 + this.e23 * m.e31 + this.e24 * m.e41;
		final float e22 = this.e21 * m.e12 + this.e22 * m.e22 + this.e23 * m.e32 + this.e24 * m.e42;
		final float e23 = this.e21 * m.e13 + this.e22 * m.e23 + this.e23 * m.e33 + this.e24 * m.e43;
		final float e24 = this.e21 * m.e14 + this.e22 * m.e24 + this.e23 * m.e34 + this.e24 * m.e44;
		
		final float e31 = this.e31 * m.e11 + this.e32 * m.e21 + this.e33 * m.e31 + this.e34 * m.e41;
		final float e32 = this.e31 * m.e12 + this.e32 * m.e22 + this.e33 * m.e32 + this.e34 * m.e42;
		final float e33 = this.e31 * m.e13 + this.e32 * m.e23 + this.e33 * m.e33 + this.e34 * m.e43;
		final float e34 = this.e31 * m.e14 + this.e32 * m.e24 + this.e33 * m.e34 + this.e34 * m.e44;
		
		final float e41 = this.e41 * m.e11 + this.e42 * m.e21 + this.e43 * m.e31 + this.e44 * m.e41;
		final float e42 = this.e41 * m.e12 + this.e42 * m.e22 + this.e43 * m.e32 + this.e44 * m.e42;
		final float e43 = this.e41 * m.e13 + this.e42 * m.e23 + this.e43 * m.e33 + this.e44 * m.e43;
		final float e44 = this.e41 * m.e14 + this.e42 * m.e24 + this.e43 * m.e34 + this.e44 * m.e44;
		
		return new Matrix44(e11, e12, e13, e14, e21, e22, e23, e24, e31, e32, e33, e34, e41, e42, e43, e44);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Matrix44 rotation(final Vector3 v, final Vector3 w) {
		final Vector3 w0 = w.normalize();
		final Vector3 u0 = v.normalize().crossProduct(w0);
		final Vector3 v0 = w0.crossProduct(u0);
		
		return rotation(u0, v0, w0);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 rotation(final Vector3 u, final Vector3 v, final Vector3 w) {
		return new Matrix44(u.x, u.y, u.z, 0.0F, v.x, v.y, v.z, 0.0F, w.x, w.y, w.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
}