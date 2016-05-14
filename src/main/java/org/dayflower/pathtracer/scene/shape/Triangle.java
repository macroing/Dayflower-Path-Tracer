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
	public final Vertex a;
	
//	TODO: Add Javadocs.
	public final Vertex b;
	
//	TODO: Add Javadocs.
	public final Vertex c;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Triangle(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final Vertex a, final Vertex b, final Vertex c) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	@Override
	public String toString() {
		return String.format("Triangle: A=%s, B=%s, C=%s", getA(), getB(), getC());
	}
	
//	TODO: Add Javadocs.
	public Triangle rotate(final Vector3 v, final Vector3 w) {
		final Matrix44 m = Matrix44.rotation(v, w);
		
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), getA().transform(m), getB().transform(m), getC().transform(m));
	}
	
//	TODO: Add Javadocs.
	public Triangle scale(final float s) {
		final float a0X = this.a.position.x;
		final float a0Y = this.a.position.y;
		final float a0Z = this.a.position.z;
		
		final float b0X = this.b.position.x;
		final float b0Y = this.b.position.y;
		final float b0Z = this.b.position.z;
		
		final float c0X = this.c.position.x;
		final float c0Y = this.c.position.y;
		final float c0Z = this.c.position.z;
		
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
		
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), this.a.setPosition(new Point3(a1X, a1Y, a1Z)), this.b.setPosition(new Point3(b1X, b1Y, b1Z)), this.c.setPosition(new Point3(c1X, c1Y, c1Z)));
	}
	
//	TODO: Add Javadocs.
	public Triangle translate(final float x, final float y, final float z) {
		return translateX(x).translateY(y).translateZ(z);
	}
	
//	TODO: Add Javadocs.
	public Triangle translateX(final float x) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), getA().translateX(x), getB().translateX(x), getC().translateX(x));
	}
	
//	TODO: Add Javadocs.
	public Triangle translateY(final float y) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), getA().translateY(y), getB().translateY(y), getC().translateY(y));
	}
	
//	TODO: Add Javadocs.
	public Triangle translateZ(final float z) {
		return new Triangle(getEmission(), getPerlinNoiseAmount(), getPerlinNoiseScale(), getMaterial(), getTextureAlbedo(), getTextureNormal(), getA().translateZ(z), getB().translateZ(z), getC().translateZ(z));
	}
	
//	TODO: Add Javadocs.
	public Vertex getA() {
		return this.a;
	}
	
//	TODO: Add Javadocs.
	public Vertex getB() {
		return this.b;
	}
	
//	TODO: Add Javadocs.
	public Vertex getC() {
		return this.c;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static Point3 maximum(final List<Triangle> triangles) {
		Point3 maximum = Point3.minimum();
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.a.position;
			final Point3 b = triangle.b.position;
			final Point3 c = triangle.c.position;
			
			maximum = Point3.maximum(maximum, Point3.maximum(a, b, c));
		}
		
		return maximum;
	}
	
//	TODO: Add Javadocs.
	public static Point3 minimum(final List<Triangle> triangles) {
		Point3 minimum = Point3.maximum();
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.a.position;
			final Point3 b = triangle.b.position;
			final Point3 c = triangle.c.position;
			
			minimum = Point3.minimum(minimum, Point3.minimum(a, b, c));
		}
		
		return minimum;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static final class Vertex {
//		TODO: Add Javadocs.
		public final Point2 textureCoordinates;
		
//		TODO: Add Javadocs.
		public final Point3 position;
		
//		TODO: Add Javadocs.
		public final String material;
		
//		TODO: Add Javadocs.
		public final Vector3 normal;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		public Vertex(final Point2 textureCoordinates, final Point3 position, final String material, final Vector3 normal) {
			this.textureCoordinates = Objects.requireNonNull(textureCoordinates, "textureCoordinates == null");
			this.position = Objects.requireNonNull(position, "position == null");
			this.material = Objects.requireNonNull(material, "material == null");
			this.normal = Objects.requireNonNull(normal, "normal == null");
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		public Point2 getTextureCoordinates() {
			return this.textureCoordinates;
		}
		
//		TODO: Add Javadocs.
		public Point3 getPosition() {
			return this.position;
		}
		
//		TODO: Add Javadocs.
		public String getMaterial() {
			return this.material;
		}
		
//		TODO: Add Javadocs.
		public Vector3 getNormal() {
			return this.normal;
		}
		
//		TODO: Add Javadocs.
		public Vertex setPosition(final Point3 position) {
			return new Vertex(this.textureCoordinates, position, this.material, this.normal);
		}
		
//		TODO: Add Javadocs.
		public Vertex transform(final Matrix44 m) {
			return new Vertex(this.textureCoordinates, this.position.transform(m), this.material, this.normal);
		}
		
//		TODO: Add Javadocs.
		public Vertex translateX(final float x) {
			return new Vertex(this.textureCoordinates, this.position.translateX(x), this.material, this.normal);
		}
		
//		TODO: Add Javadocs.
		public Vertex translateY(final float y) {
			return new Vertex(this.textureCoordinates, this.position.translateY(y), this.material, this.normal);
		}
		
//		TODO: Add Javadocs.
		public Vertex translateZ(final float z) {
			return new Vertex(this.textureCoordinates, this.position.translateZ(z), this.material, this.normal);
		}
	}
}