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
import static org.dayflower.pathtracer.math.Math2.tan;

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.math.Math2;

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
	public Matrix44() {
		this(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public Matrix44(final float e11, final float e12, final float e13, final float e14, final float e21, final float e22, final float e23, final float e24, final float e31, final float e32, final float e33, final float e34, final float e41, final float e42, final float e43, final float e44) {
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
	public Matrix44(final float[][] elements) {
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
	public boolean isIdentity() {
		final boolean isIdentity0 = Math2.equals(this.e11, 1.0F) && Math2.equals(this.e12, 0.0F) && Math2.equals(this.e13, 0.0F) && Math2.equals(this.e14, 0.0F);
		final boolean isIdentity1 = Math2.equals(this.e21, 0.0F) && Math2.equals(this.e22, 1.0F) && Math2.equals(this.e23, 0.0F) && Math2.equals(this.e24, 0.0F);
		final boolean isIdentity2 = Math2.equals(this.e31, 0.0F) && Math2.equals(this.e32, 0.0F) && Math2.equals(this.e33, 1.0F) && Math2.equals(this.e34, 0.0F);
		final boolean isIdentity3 = Math2.equals(this.e41, 0.0F) && Math2.equals(this.e42, 0.0F) && Math2.equals(this.e43, 0.0F) && Math2.equals(this.e44, 1.0F);
		
		return isIdentity0 && isIdentity1 && isIdentity2 && isIdentity3;
	}
	
//	TODO: Add Javadocs!
	public float determinant() {
		final float a = this.e11 * (this.e22 * this.e33 - this.e23 * this.e32);
		final float b = this.e12 * (this.e21 * this.e33 - this.e23 * this.e31);
		final float c = this.e13 * (this.e21 * this.e32 - this.e22 * this.e31);
		
		return a - b + c;
	}
	
//	TODO: Add Javadocs!
	public float[][] toArray2() {
		return new float[][] {
			new float[] {this.e11, this.e12, this.e13, this.e14},
			new float[] {this.e21, this.e22, this.e23, this.e24},
			new float[] {this.e31, this.e32, this.e33, this.e34},
			new float[] {this.e41, this.e42, this.e43, this.e44}
		};
	}
	
//	TODO: Add Javadocs!
	public Matrix44 inverse() {
		final float[][] elements = toArray2();
		
		final int[] columnIndices = new int[4];
		final int[] pivotIndices = new int[4];
		final int[] rowIndices = new int[4];
		
		for(int i = 0; i < 4; i++) {
			int columnIndex = -1;
			int rowIndex = -1;
			
			float element = 0.0F;
			
			for(int j = 0; j < 4; j++) {
				if(pivotIndices[j] != 1) {
					for(int k = 0; k < 4; k++) {
						if(pivotIndices[k] == 0) {
							if(abs(elements[j][k]) >= element) {
								element = elements[j][k];
								columnIndex = k;
								rowIndex = j;
							}
						} else if(pivotIndices[k] > 1) {
							throw new IllegalStateException("A Singular Matrix cannot be inversed!");
						}
					}
				}
			}
			
			pivotIndices[columnIndex]++;
			
			if(columnIndex != rowIndex) {
				for(int j = 0; j < 4; j++) {
					final float element0 = elements[rowIndex][j];
					final float element1 = elements[columnIndex][j];
					
					elements[rowIndex][j] = element1;
					elements[columnIndex][j] = element0;
				}
			}
			
			columnIndices[i] = columnIndex;
			rowIndices[i] = rowIndex;
			
			if(Math2.equals(elements[columnIndex][columnIndex], 0.0F)) {
				throw new IllegalStateException("A Singular Matrix cannot be inversed!");
			}
			
			final float pivotReciprocal = 1.0F / elements[columnIndex][columnIndex];
			
			elements[columnIndex][columnIndex] = 1.0F;
			
			for(int j = 0; j < 4; j++) {
				elements[columnIndex][j] *= pivotReciprocal;
			}
			
			for(int j = 0; j < 4; j++) {
				if(j != columnIndex) {
					final float element0 = elements[j][columnIndex];
					
					elements[j][columnIndex] = 0.0F;
					
					for(int k = 0; k < 4; k++) {
						elements[j][k] -= elements[columnIndex][k] * element0;
					}
				}
			}
		}
		
		for(int i = 3; i >= 0; i--) {
			if(columnIndices[i] != rowIndices[i]) {
				for(int j = 0; j < 4; j++) {
					final float element0 = elements[j][rowIndices[i]];
					final float element1 = elements[j][columnIndices[i]];
					
					elements[j][rowIndices[i]] = element1;
					elements[j][columnIndices[i]] = element0;
				}
			}
		}
		
		return new Matrix44(elements);
	}
	
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
	
//	TODO: Add Javadocs!
	public Matrix44 transpose() {
		return new Matrix44(this.e11, this.e21, this.e31, this.e41, this.e12, this.e22, this.e32, this.e42, this.e13, this.e23, this.e33, this.e43, this.e14, this.e24, this.e34, this.e44);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Matrix44 orthographic(final float left, final float right, final float bottom, final float top, final float near, final float far) {
		final float width = right - left;
		final float height = top - bottom;
		final float depth = far - near;
		
		return new Matrix44(2.0F / width, 0.0F, 0.0F, -(right + left) / width, 0.0F, 2.0F / height, 0.0F, -(top + bottom) / height, 0.0F, 0.0F, -2.0F / depth, -(far + near) / depth, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 perspective(final float fieldOfView, final float aspectRatio, final float zNear, final float zFar) {
		final float tanHalfFieldOfView = tan(fieldOfView / 2.0F);
		final float zRange = zNear - zFar;
		
		return new Matrix44(1.0F / (tanHalfFieldOfView * aspectRatio), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / tanHalfFieldOfView, 0.0F, 0.0F, 0.0F, 0.0F, (-zNear - zFar) / zRange, 2.0F * zFar * zNear / zRange, 0.0F, 0.0F, 1.0F, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 rotation(final Quaternion q) {
		final Vector3 u = new Vector3(1.0F - 2.0F * (q.y * q.y + q.z * q.z), 2.0F * (q.x * q.y - q.w * q.z), 2.0F * (q.x * q.z + q.w * q.y));
		final Vector3 v = new Vector3(2.0F * (q.x * q.y + q.w * q.z), 1.0F - 2.0F * (q.x * q.x + q.z * q.z), 2.0F * (q.y * q.z - q.w * q.x));
		final Vector3 w = new Vector3(2.0F * (q.x * q.z - q.w * q.y), 2.0F * (q.y * q.z + q.w * q.x), 1.0F - 2.0F * (q.x * q.x + q.y * q.y));
		
		return rotation(u, v, w);
	}
	
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
	
//	TODO: Add Javadocs!
	public static Matrix44 rotation(final Vector4 v, final Vector4 w) {
		final Vector4 w0 = w.normalize();
		final Vector4 u0 = v.normalize().crossProduct(w0);
		final Vector4 v0 = w0.crossProduct(u0);
		
		return rotation(u0, v0, w0);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 rotation(final Vector4 u, final Vector4 v, final Vector4 w) {
		return new Matrix44(u.x, u.y, u.z, 0.0F, v.x, v.y, v.z, 0.0F, w.x, w.y, w.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 scale(final Vector3 v) {
		return new Matrix44(v.x, 0.0F, 0.0F, 0.0F, 0.0F, v.y, 0.0F, 0.0F, 0.0F, 0.0F, v.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 screenSpaceTransform(final float halfWidth, final float halfHeight) {
		return new Matrix44(halfWidth, 0.0F, 0.0F, halfWidth - 0.5F, 0.0F, -halfHeight, 0.0F, halfHeight - 0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public static Matrix44 translation(final Point3 p) {
		return new Matrix44(1.0F, 0.0F, 0.0F, p.x, 0.0F, 1.0F, 0.0F, p.y, 0.0F, 0.0F, 1.0F, p.z, 0.0F, 0.0F, 0.0F, 1.0F);
	}
}