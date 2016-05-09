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
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;

//TODO: Add Javadocs.
public final class Plane extends Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_A = 10;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_B = 13;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_C = 16;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_NORMAL = 19;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 22;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public final Point3 a;
	
//	TODO: Add Javadocs.
	public final Point3 b;
	
//	TODO: Add Javadocs.
	public final Point3 c;
	
//	TODO: Add Javadocs.
	public final Vector3 surfaceNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Plane(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final Point3 a, final Point3 b, final Point3 c) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
		this.surfaceNormal = Vector3.normalNormalized(this.a, this.b, this.c);
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
//			TODO: getA().x
//			TODO: getA().y
//			TODO: getA().z
//			TODO: getB().x
//			TODO: getB().y
//			TODO: getB().z
//			TODO: getC().x
//			TODO: getC().y
//			TODO: getC().z
//			TODO: getSurfaceNormal().x
//			TODO: getSurfaceNormal().y
//			TODO: getSurfaceNormal().z
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
			getA().x,
			getA().y,
			getA().z,
			getB().x,
			getB().y,
			getB().z,
			getC().x,
			getC().y,
			getC().z,
			getSurfaceNormal().x,
			getSurfaceNormal().y,
			getSurfaceNormal().z
		};
	}
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return SIZE;
	}
	
//	TODO: Add Javadocs.
	public Point3 getA() {
		return this.a;
	}
	
//	TODO: Add Javadocs.
	public Point3 getB() {
		return this.b;
	}
	
//	TODO: Add Javadocs.
	public Point3 getC() {
		return this.c;
	}
	
//	TODO: Add Javadocs.
	public Vector3 getSurfaceNormal() {
		return this.surfaceNormal;
	}
}