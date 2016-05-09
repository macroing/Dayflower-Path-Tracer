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
package org.dayflower.pathtracer.scene.shape;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.List;
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Matrix44;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;

//TODO: Add Javadocs.
public final class Triangle extends Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_POINT_A = 10;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_POINT_B = 13;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_POINT_C = 16;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL_A = 19;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL_B = 22;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL_C = 25;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_UV_A = 28;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_UV_B = 30;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_UV_C = 32;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 48;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public final Point2 uVA;
	
//	TODO: Add Javadocs.
	public final Point2 uVB;
	
//	TODO: Add Javadocs.
	public final Point2 uVC;
	
//	TODO: Add Javadocs.
	public final Point3 pointA;
	
//	TODO: Add Javadocs.
	public final Point3 pointB;
	
//	TODO: Add Javadocs.
	public final Point3 pointC;
	
//	TODO: Add Javadocs.
	public final Vector3 surfaceNormalA;
	
//	TODO: Add Javadocs.
	public final Vector3 surfaceNormalB;
	
//	TODO: Add Javadocs.
	public final Vector3 surfaceNormalC;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Triangle(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final Point3 pointA, final Point3 pointB, final Point3 pointC, final Vector3 surfaceNormalA, final Vector3 surfaceNormalB, final Vector3 surfaceNormalC, final Point2 uVA, final Point2 uVB, final Point2 uVC) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.pointA = Objects.requireNonNull(pointA, "pointA == null");
		this.pointB = Objects.requireNonNull(pointB, "pointB == null");
		this.pointC = Objects.requireNonNull(pointC, "pointC == null");
		this.surfaceNormalA = Objects.requireNonNull(surfaceNormalA, "surfaceNormalA == null");
		this.surfaceNormalB = Objects.requireNonNull(surfaceNormalB, "surfaceNormalB == null");
		this.surfaceNormalC = Objects.requireNonNull(surfaceNormalC, "surfaceNormalC == null");
		this.uVA = Objects.requireNonNull(uVA, "uVA == null");
		this.uVB = Objects.requireNonNull(uVB, "uVB == null");
		this.uVC = Objects.requireNonNull(uVC, "uVC == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	@Override
	public byte[] toByteArray() {
		return new byte[] {
			(byte)(TYPE),
			(byte)(SIZE),
//			TODO: getEmission().r
//			TODO: getEmission().g
//			TODO: getEmission().b
			(byte)(getMaterial().ordinal()),
//			TODO: getTextureAlbedo().getOffset()
//			TODO: getTextureNormal().getOffset()
//			TODO: getPerlinNoiseAmount()
//			TODO: getPerlinNoiseScale()
//			TODO: getPointA().x
//			TODO: getPointA().y
//			TODO: getPointA().z
//			TODO: getPointB().x
//			TODO: getPointB().y
//			TODO: getPointB().z
//			TODO: getPointC().x
//			TODO: getPointC().y
//			TODO: getPointC().z
//			TODO: getSurfaceNormalA().x
//			TODO: getSurfaceNormalA().y
//			TODO: getSurfaceNormalA().z
//			TODO: getSurfaceNormalB().x
//			TODO: getSurfaceNormalB().y
//			TODO: getSurfaceNormalB().z
//			TODO: getSurfaceNormalC().x
//			TODO: getSurfaceNormalC().y
//			TODO: getSurfaceNormalC().z
//			TODO: getUVA().x
//			TODO: getUVA().y
//			TODO: getUVB().x
//			TODO: getUVB().y
//			TODO: getUVC().x
//			TODO: getUVC().y
		};
	}
	
//	TODO: Add Javadocs.
	@Override
	public float[] toFloatArray() {
		return new float[] {
			TYPE,
			SIZE,
			getEmission().r,
			getEmission().g,
			getEmission().b,
			getMaterial().ordinal(),
			getTextureAlbedo().getOffset(),
			getTextureNormal().getOffset(),
			getPerlinNoiseAmount(),
			getPerlinNoiseScale(),
			getPointA().x,
			getPointA().y,
			getPointA().z,
			getPointB().x,
			getPointB().y,
			getPointB().z,
			getPointC().x,
			getPointC().y,
			getPointC().z,
			getSurfaceNormalA().x,
			getSurfaceNormalA().y,
			getSurfaceNormalA().z,
			getSurfaceNormalB().x,
			getSurfaceNormalB().y,
			getSurfaceNormalB().z,
			getSurfaceNormalC().x,
			getSurfaceNormalC().y,
			getSurfaceNormalC().z,
			getUVA().x,
			getUVA().y,
			getUVB().x,
			getUVB().y,
			getUVC().x,
			getUVC().y,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F
		};
	}
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return SIZE;
	}
	
//	TODO: Add Javadocs.
	public Point2 getUVA() {
		return this.uVA;
	}
	
//	TODO: Add Javadocs.
	public Point2 getUVB() {
		return this.uVB;
	}
	
//	TODO: Add Javadocs.
	public Point2 getUVC() {
		return this.uVC;
	}
	
//	TODO: Add Javadocs.
	public Point3 getPointA() {
		return this.pointA;
	}
	
//	TODO: Add Javadocs.
	public Point3 getPointB() {
		return this.pointB;
	}
	
//	TODO: Add Javadocs.
	public Point3 getPointC() {
		return this.pointC;
	}
	
//	TODO: Add Javadocs.
	@Override
	public String toString() {
		return String.format("Triangle: A=%s, B=%s, C=%s", getPointA(), getPointB(), getPointC());
	}
	
//	TODO: Add Javadocs.
	public Triangle rotate(final Vector3 v, final Vector3 w) {
		final Matrix44 m = Matrix44.rotation(v, w);
		
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), this.pointA.transform(m), this.pointB.transform(m), this.pointC.transform(m), this.surfaceNormalA, this.surfaceNormalB, this.surfaceNormalC, this.uVA, this.uVB, this.uVC);
	}
	
//	TODO: Add Javadocs.
	public Triangle scale(final float s) {
		final float a0X = this.pointA.x;
		final float a0Y = this.pointA.y;
		final float a0Z = this.pointA.z;
		
		final float b0X = this.pointB.x;
		final float b0Y = this.pointB.y;
		final float b0Z = this.pointB.z;
		
		final float c0X = this.pointC.x;
		final float c0Y = this.pointC.y;
		final float c0Z = this.pointC.z;
		
		final float centerX = (a0X + b0X + c0X) / 3.0F;
		final float centerY = (a0Y + b0Y + c0Y) / 3.0F;
		final float centerZ = (a0Z + b0Z + c0Z) / 3.0F;
		
		final float a1X = centerX + (a0X - centerX) * s;
		final float a1Y = centerY + (a0Y - centerY) * s;
		final float a1Z = centerZ + (a0Z - centerZ) * s;
		
		final float b1X = centerX + (b0X - centerX) * s;
		final float b1Y = centerY + (b0Y - centerY) * s;
		final float b1Z = centerZ + (b0Z - centerZ) * s;
		
		final float c1X = centerX + (c0X - centerX) * s;
		final float c1Y = centerY + (c0Y - centerY) * s;
		final float c1Z = centerZ + (c0Z - centerZ) * s;
		
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), new Point3(a1X, a1Y, a1Z), new Point3(b1X, b1Y, b1Z), new Point3(c1X, c1Y, c1Z), this.surfaceNormalA, this.surfaceNormalB, this.surfaceNormalC, this.uVA, this.uVB, this.uVC);
	}
	
//	TODO: Add Javadocs.
	public Triangle translate(final float x, final float y, final float z) {
		return translateX(x).translateY(y).translateZ(z);
	}
	
//	TODO: Add Javadocs.
	public Triangle translateX(final float x) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), this.pointA.translateX(x), this.pointB.translateX(x), this.pointC.translateX(x), this.surfaceNormalA, this.surfaceNormalB, this.surfaceNormalC, this.uVA, this.uVB, this.uVC);
	}
	
//	TODO: Add Javadocs.
	public Triangle translateY(final float y) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), this.pointA.translateY(y), this.pointB.translateY(y), this.pointC.translateY(y), this.surfaceNormalA, this.surfaceNormalB, this.surfaceNormalC, this.uVA, this.uVB, this.uVC);
	}
	
//	TODO: Add Javadocs.
	public Triangle translateZ(final float z) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), this.pointA.translateZ(z), this.pointB.translateZ(z), this.pointC.translateZ(z), this.surfaceNormalA, this.surfaceNormalB, this.surfaceNormalC, this.uVA, this.uVB, this.uVC);
	}
	
//	TODO: Add Javadocs.
	public Vector3 getSurfaceNormalA() {
		return this.surfaceNormalA;
	}
	
//	TODO: Add Javadocs.
	public Vector3 getSurfaceNormalB() {
		return this.surfaceNormalB;
	}
	
//	TODO: Add Javadocs.
	public Vector3 getSurfaceNormalC() {
		return this.surfaceNormalC;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static Point3 maximum(final List<Triangle> triangles) {
		Point3 maximum = Point3.minimum();
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.pointA;
			final Point3 b = triangle.pointB;
			final Point3 c = triangle.pointC;
			
			maximum = Point3.maximum(maximum, Point3.maximum(a, b, c));
		}
		
		return maximum;
	}
	
//	TODO: Add Javadocs.
	public static Point3 minimum(final List<Triangle> triangles) {
		Point3 minimum = Point3.maximum();
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.pointA;
			final Point3 b = triangle.pointB;
			final Point3 c = triangle.pointC;
			
			minimum = Point3.minimum(minimum, Point3.minimum(a, b, c));
		}
		
		return minimum;
	}
}