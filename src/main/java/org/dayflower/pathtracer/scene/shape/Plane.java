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