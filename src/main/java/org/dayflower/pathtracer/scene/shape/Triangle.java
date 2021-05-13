/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.ShapeIntersection;
import org.macroing.math4j.Matrix44F;
import org.macroing.math4j.OrthoNormalBasis33F;
import org.macroing.math4j.Point2F;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.Ray3F;
import org.macroing.math4j.Vector3F;

/**
 * A {@link Shape} implementation that implements a triangle.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Triangle extends Shape {
	/**
	 * The relative offset of the A Position Offset parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int RELATIVE_OFFSET_A_POSITION_OFFSET = 0;
	
	/**
	 * The relative offset of the A Surface Normal Offset parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_A_SURFACE_NORMAL_OFFSET = 3;
	
	/**
	 * The relative offset of the A Surface Tangent Offset parameter in the {@code float} array. The value is {@code 9}.
	 */
	public static final int RELATIVE_OFFSET_A_SURFACE_TANGENT_OFFSET = 9;
	
	/**
	 * The relative offset of the A Texture Coordinates Offset parameter in the {@code float} array. The value is {@code 6}.
	 */
	public static final int RELATIVE_OFFSET_A_TEXTURE_COORDINATES_OFFSET = 6;
	
	/**
	 * The relative offset of the B Position Offset parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_B_POSITION_OFFSET = 1;
	
	/**
	 * The relative offset of the B Surface Normal Offset parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_B_SURFACE_NORMAL_OFFSET = 4;
	
	/**
	 * The relative offset of the B Surface Tangent Offset parameter in the {@code float} array. The value is {@code 10}.
	 */
	public static final int RELATIVE_OFFSET_B_SURFACE_TANGENT_OFFSET = 10;
	
	/**
	 * The relative offset of the B Texture Coordinates Offset parameter in the {@code float} array. The value is {@code 7}.
	 */
	public static final int RELATIVE_OFFSET_B_TEXTURE_COORDINATES_OFFSET = 7;
	
	/**
	 * The relative offset of the C Position Offset parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_C_POSITION_OFFSET = 2;
	
	/**
	 * The relative offset of the C Surface Normal Offset parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int RELATIVE_OFFSET_C_SURFACE_NORMAL_OFFSET = 5;
	
	/**
	 * The relative offset of the C Surface Tangent Offset parameter in the {@code float} array. The value is {@code 11}.
	 */
	public static final int RELATIVE_OFFSET_C_SURFACE_TANGENT_OFFSET = 11;
	
	/**
	 * The relative offset of the C Texture Coordinates Offset parameter in the {@code float} array. The value is {@code 8}.
	 */
	public static final int RELATIVE_OFFSET_C_TEXTURE_COORDINATES_OFFSET = 8;
	
	/**
	 * The size of a {@code Triangle} in the {@code float} array. The size is {@code 9}.
	 */
	public static final int SIZE = 12;
	
	/**
	 * The type number associated with a {@code Triangle}. The number is {@code 4}.
	 */
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float EPSILON = 0.0001F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The surface normal of this {@code Triangle} instance.
	 */
	public final Vector3F surfaceNormal;
	
	/**
	 * The {@link Vertex} A.
	 */
	public final Vertex a;
	
	/**
	 * The {@link Vertex} B.
	 */
	public final Vertex b;
	
	/**
	 * The {@link Vertex} C.
	 */
	public final Vertex c;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Triangle} instance.
	 * <p>
	 * If either {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param a a {@link Vertex} denoted by A
	 * @param b a {@code Vertex} denoted by B
	 * @param c a {@code Vertex} denoted by C
	 * @throws NullPointerException thrown if, and only if, either {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public Triangle(final Vertex a, final Vertex b, final Vertex c) {
		super(TYPE);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
		this.surfaceNormal = Vector3F.normalNormalized(this.a.position, this.b.position, this.c.position);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns an {@code Optional} of {@link ShapeIntersection} with the optional intersection given a specified {@link Ray3F}.
	 * <p>
	 * If {@code ray} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param ray a {@code Ray3F}
	 * @return an {@code Optional} of {@code ShapeIntersection} with the optional intersection given a specified {@code Ray3F}
	 * @throws NullPointerException thrown if, and only if, {@code ray} is {@code null}
	 */
	@Override
	public Optional<ShapeIntersection> intersection(final Ray3F ray) {
//		Initialize the positions of the vertices A, B and C:
		final Point3F positionA = this.a.position;
		final Point3F positionB = this.b.position;
		final Point3F positionC = this.c.position;
		
//		Initialize the origin of the ray:
		final Point3F origin = ray.origin;
		
//		Initialize the direction of the ray:
		final Vector3F direction = ray.direction;
		
//		Calculate the edges from vertex A to vertex B and vertex A to vertex C:
		final Vector3F edgeAB = Vector3F.direction(positionA, positionB);
		final Vector3F edgeAC = Vector3F.direction(positionA, positionC);
		
		final Vector3F v0 = direction.crossProduct(edgeAC);
		
		final float determinant = edgeAB.dotProduct(v0);
		final float determinantReciprocal = 1.0F / determinant;
		
		if(determinant > -EPSILON && determinant < EPSILON) {
			return Optional.empty();
		}
		
		final Vector3F v1 = Vector3F.direction(positionA, origin);
		
//		Calculate the Barycentric coordinate U:
		final float u = v1.dotProduct(v0) * determinantReciprocal;
		
		if(u < 0.0F || u > 1.0F) {
			return Optional.empty();
		}
		
		final Vector3F v2 = v1.crossProduct(edgeAB);
		
//		Calculate the Barycentric coordinate V:
		final float v = direction.dotProduct(v2) * determinantReciprocal;
		
		if(v < 0.0F || u + v > 1.0F) {
			return Optional.empty();
		}
		
		final float t = edgeAC.dotProduct(v2) * determinantReciprocal;
		
		return t > EPSILON ? Optional.of(doCreateShapeIntersection(ray, t)) : Optional.empty();
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Triangle} instance.
	 * 
	 * @return a {@code String} representation of this {@code Triangle} instance
	 */
	@Override
	public String toString() {
		return String.format("new Triangle(%s, %s, %s)", this.a, this.b, this.c);
	}
	
	/**
	 * Flips the texture coordinates for Y of this {@code Triangle} instance.
	 * <p>
	 * Returns a new version of this {@code Triangle} instance.
	 * 
	 * @return a new version of this {@code Triangle} instance
	 */
	public Triangle flipTextureCoordinatesForY() {
		final Point2F oldTextureCoordinatesA = this.a.getTextureCoordinates();
		final Point2F oldTextureCoordinatesB = this.b.getTextureCoordinates();
		final Point2F oldTextureCoordinatesC = this.c.getTextureCoordinates();
		
		final Point2F newTextureCoordinatesA = new Point2F(oldTextureCoordinatesA.x, 1.0F - oldTextureCoordinatesA.y);
		final Point2F newTextureCoordinatesB = new Point2F(oldTextureCoordinatesB.x, 1.0F - oldTextureCoordinatesB.y);
		final Point2F newTextureCoordinatesC = new Point2F(oldTextureCoordinatesC.x, 1.0F - oldTextureCoordinatesC.y);
		
		return new Triangle(this.a.setTextureCoordinates(newTextureCoordinatesA), this.b.setTextureCoordinates(newTextureCoordinatesB), this.c.setTextureCoordinates(newTextureCoordinatesC));
	}
	
	/**
	 * Rotates this {@code Triangle} instance.
	 * <p>
	 * Returns a new rotated version of this {@code Triangle} instance.
	 * <p>
	 * If either {@code w} or {@code v} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param w a {@link Vector3F}
	 * @param v a {@code Vector3F}
	 * @return a new rotated version of this {@code Triangle} instance
	 * @throws NullPointerException thrown if, and only if, either {@code w} or {@code v} are {@code null}
	 */
	public Triangle rotate(final Vector3F w, final Vector3F v) {
		final Matrix44F m = Matrix44F.rotation(w, v);
		
		return new Triangle(getA().transform(m), getB().transform(m), getC().transform(m));
	}
	
	/**
	 * Scales this {@code Triangle} instance.
	 * <p>
	 * Returns a new scaled version of this {@code Triangle} instance.
	 * 
	 * @param s the scaling factor
	 * @return a new scaled version of this {@code Triangle} instance
	 */
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
		
		return new Triangle(this.a.setPosition(new Point3F(a1X, a1Y, a1Z)), this.b.setPosition(new Point3F(b1X, b1Y, b1Z)), this.c.setPosition(new Point3F(c1X, c1Y, c1Z)));
	}
	
	/**
	 * Scales the texture coordinates of this {@code Triangle} instance.
	 * <p>
	 * Returns a new scaled version of this {@code Triangle} instance.
	 * 
	 * @param scale the scaling factor
	 * @return a new scaled version of this {@code Triangle} instance
	 */
	public Triangle scaleTextureCoordinates(final float scale) {
		final Point2F oldTextureCoordinatesA = this.a.getTextureCoordinates();
		final Point2F oldTextureCoordinatesB = this.b.getTextureCoordinates();
		final Point2F oldTextureCoordinatesC = this.c.getTextureCoordinates();
		
		final Point2F newTextureCoordinatesA = new Point2F(oldTextureCoordinatesA.x * scale, scale - oldTextureCoordinatesA.y * scale);
		final Point2F newTextureCoordinatesB = new Point2F(oldTextureCoordinatesB.x * scale, scale - oldTextureCoordinatesB.y * scale);
		final Point2F newTextureCoordinatesC = new Point2F(oldTextureCoordinatesC.x * scale, scale - oldTextureCoordinatesC.y * scale);
		
		return new Triangle(this.a.setTextureCoordinates(newTextureCoordinatesA), this.b.setTextureCoordinates(newTextureCoordinatesB), this.c.setTextureCoordinates(newTextureCoordinatesC));
	}
	
	/**
	 * Transforms this {@code Triangle} from World Space to Object Space.
	 * <p>
	 * Returns a new {@code Triangle} with the transformation performed.
	 * <p>
	 * If either {@code objectToWorld} or {@code worldToObject} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param objectToWorld a {@code Matrix44F}
	 * @param worldToObject a {@code Matrix44F}
	 * @return a new {@code Triangle} with the transformation performed
	 * @throws NullPointerException thrown if, and only if, either {@code objectToWorld} or {@code worldToObject} are {@code null}
	 */
	public Triangle transformToObjectSpace(final Matrix44F objectToWorld, final Matrix44F worldToObject) {
		return new Triangle(this.a.transformToObjectSpace(objectToWorld, worldToObject), this.b.transformToObjectSpace(objectToWorld, worldToObject), this.c.transformToObjectSpace(objectToWorld, worldToObject));
	}
	
	/**
	 * Transforms this {@code Triangle} from Object Space to World Space.
	 * <p>
	 * Returns a new {@code Triangle} with the transformation performed.
	 * <p>
	 * If either {@code objectToWorld} or {@code worldToObject} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param objectToWorld a {@code Matrix44F}
	 * @param worldToObject a {@code Matrix44F}
	 * @return a new {@code Triangle} with the transformation performed
	 * @throws NullPointerException thrown if, and only if, either {@code objectToWorld} or {@code worldToObject} are {@code null}
	 */
	public Triangle transformToWorldSpace(final Matrix44F objectToWorld, final Matrix44F worldToObject) {
		return new Triangle(this.a.transformToWorldSpace(objectToWorld, worldToObject), this.b.transformToWorldSpace(objectToWorld, worldToObject), this.c.transformToWorldSpace(objectToWorld, worldToObject));
	}
	
	/**
	 * Translates this {@code Triangle} instance.
	 * <p>
	 * Returns a new translated version of this {@code Triangle} instance.
	 * 
	 * @param x the amount to translate in the X-direction
	 * @param y the amount to translate in the Y-direction
	 * @param z the amount to translate in the Z-direction
	 * @return a new translated version of this {@code Triangle} instance
	 */
	public Triangle translate(final float x, final float y, final float z) {
		return translateX(x).translateY(y).translateZ(z);
	}
	
	/**
	 * Translates this {@code Triangle} instance in the X-direction.
	 * <p>
	 * Returns a new translated version of this {@code Triangle} instance.
	 * 
	 * @param x the amount to translate in the X-direction
	 * @return a new translated version of this {@code Triangle} instance
	 */
	public Triangle translateX(final float x) {
		return new Triangle(getA().translateX(x), getB().translateX(x), getC().translateX(x));
	}
	
	/**
	 * Translates this {@code Triangle} instance in the Y-direction.
	 * <p>
	 * Returns a new translated version of this {@code Triangle} instance.
	 * 
	 * @param y the amount to translate in the Y-direction
	 * @return a new translated version of this {@code Triangle} instance
	 */
	public Triangle translateY(final float y) {
		return new Triangle(getA().translateY(y), getB().translateY(y), getC().translateY(y));
	}
	
	/**
	 * Translates this {@code Triangle} instance in the Z-direction.
	 * <p>
	 * Returns a new translated version of this {@code Triangle} instance.
	 * 
	 * @param z the amount to translate in the Z-direction
	 * @return a new translated version of this {@code Triangle} instance
	 */
	public Triangle translateZ(final float z) {
		return new Triangle(getA().translateZ(z), getB().translateZ(z), getC().translateZ(z));
	}
	
	/**
	 * Returns the surface normal of this {@code Triangle} instance.
	 * 
	 * @return the surface normal of this {@code Triangle} instance
	 */
	public Vector3F getSurfaceNormal() {
		return this.surfaceNormal;
	}
	
	/**
	 * Returns the {@link Vertex} denoted by A.
	 * 
	 * @return the {@code Vertex} denoted by A
	 */
	public Vertex getA() {
		return this.a;
	}
	
	/**
	 * Returns the {@link Vertex} denoted by B.
	 * 
	 * @return the {@code Vertex} denoted by B
	 */
	public Vertex getB() {
		return this.b;
	}
	
	/**
	 * Returns the {@link Vertex} denoted by C.
	 * 
	 * @return the {@code Vertex} denoted by C
	 */
	public Vertex getC() {
		return this.c;
	}
	
	/**
	 * Compares {@code object} to this {@code Triangle} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Triangle}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Triangle} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Triangle}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Triangle)) {
			return false;
		} else if(!Objects.equals(this.a, Triangle.class.cast(object).a)) {
			return false;
		} else if(!Objects.equals(this.b, Triangle.class.cast(object).b)) {
			return false;
		} else if(!Objects.equals(this.c, Triangle.class.cast(object).c)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the size of this {@code Triangle} instance.
	 * 
	 * @return the size of this {@code Triangle} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns a hash code for this {@code Triangle} instance.
	 * 
	 * @return a hash code for this {@code Triangle} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.a, this.b, this.c);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the maximum {@link Point3F} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}.
	 * <p>
	 * If either {@code triangles} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param triangles a {@code List} with {@code Triangle}s
	 * @return the maximum {@code Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}
	 * @throws NullPointerException thrown if, and only if, either {@code triangles} or any of its elements are {@code null}
	 */
	public static Point3F maximum(final List<Triangle> triangles) {
		Point3F maximum = new Point3F(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		
		for(final Triangle triangle : triangles) {
			final Point3F a = triangle.a.position;
			final Point3F b = triangle.b.position;
			final Point3F c = triangle.c.position;
			
			maximum = Point3F.maximum(maximum, Point3F.maximum(a, b, c));
		}
		
		return maximum;
	}
	
	/**
	 * Returns the minimum {@link Point3F} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}.
	 * <p>
	 * If either {@code triangles} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param triangles a {@code List} with {@code Triangle}s
	 * @return the minimum {@code Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}
	 * @throws NullPointerException thrown if, and only if, either {@code triangles} or any of its elements are {@code null}
	 */
	public static Point3F minimum(final List<Triangle> triangles) {
		Point3F minimum = new Point3F(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		
		for(final Triangle triangle : triangles) {
			final Point3F a = triangle.a.position;
			final Point3F b = triangle.b.position;
			final Point3F c = triangle.c.position;
			
			minimum = Point3F.minimum(minimum, Point3F.minimum(a, b, c));
		}
		
		return minimum;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ShapeIntersection doCreateShapeIntersection(final Ray3F ray, final float t) {
//		Initialize the origin of the ray:
		final Point3F origin = ray.origin;
		
//		Initialize the direction of the ray:
		final Vector3F direction = ray.direction;
		
//		Initialize the texture coordinates of the vertices A, B and C:
		final Point2F textureCoordinatesA = this.a.textureCoordinates;
		final Point2F textureCoordinatesB = this.b.textureCoordinates;
		final Point2F textureCoordinatesC = this.c.textureCoordinates;
		
//		Initialize the positions of the vertices A, B and C:
		final Point3F positionA = this.a.position;
		final Point3F positionB = this.b.position;
		final Point3F positionC = this.c.position;
		
//		Initialize the normals of the vertices A, B and C:
		final Vector3F normalA = this.a.normal;
		final Vector3F normalB = this.b.normal;
		final Vector3F normalC = this.c.normal;
		
//		Calculate the edges from vertex A to vertex B and vertex A to vertex C:
		final Vector3F edgeAB = Vector3F.direction(positionA, positionB);
		final Vector3F edgeAC = Vector3F.direction(positionA, positionC);
		
		final Vector3F v0 = direction.crossProduct(edgeAC);
		final Vector3F v1 = Vector3F.direction(positionA, origin);
		final Vector3F v2 = v1.crossProduct(edgeAB);
		
		final float determinant = edgeAB.dotProduct(v0);
		final float determinantReciprocal = 1.0F / determinant;
		
		final float barycentricU = v1.dotProduct(v0) * determinantReciprocal;
		final float barycentricV = direction.dotProduct(v2) * determinantReciprocal;
		final float barycentricW = 1.0F - barycentricU - barycentricV;
		
		final float u = barycentricW * textureCoordinatesA.x + barycentricU * textureCoordinatesB.x + barycentricV * textureCoordinatesC.x;
		final float v = barycentricW * textureCoordinatesA.y + barycentricU * textureCoordinatesB.y + barycentricV * textureCoordinatesC.y;
		
		final Point2F textureCoordinates = new Point2F(u, v);
		
		final Point3F surfaceIntersectionPoint = origin.add(direction, t);
		
		final Vector3F surfaceNormal = Vector3F.normalNormalized(normalA, normalB, normalC, barycentricU, barycentricV, barycentricW);
		
		final OrthoNormalBasis33F orthoNormalBasis = new OrthoNormalBasis33F(surfaceNormal);
		
		return new ShapeIntersection(orthoNormalBasis, textureCoordinates, surfaceIntersectionPoint, this, surfaceNormal, t);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code Vertex} represents a vertex in a {@link Triangle}.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static final class Vertex {
		/**
		 * The texture coordinates of this {@code Vertex} instance.
		 */
		public final Point2F textureCoordinates;
		
		/**
		 * The position of this {@code Vertex} instance.
		 */
		public final Point3F position;
		
		/**
		 * The normal of this {@code Vertex} instance.
		 */
		public final Vector3F normal;
		
		/**
		 * The tangent of this {@code Vertex} instance.
		 */
		public final Vector3F tangent;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code Vertex} instance.
		 * <p>
		 * If either {@code textureCoordinates}, {@code position}, {@code normal} or {@code tangent} are {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param textureCoordinates the texture coordinates of this {@code Vertex}
		 * @param position the position of this {@code Vertex}
		 * @param normal the normal of this {@code Vertex}
		 * @param tangent the tangent of this {@code Vertex}
		 * @throws NullPointerException thrown if, and only if, either {@code textureCoordinates}, {@code position}, {@code normal} or {@code tangent} are {@code null}
		 */
		public Vertex(final Point2F textureCoordinates, final Point3F position, final Vector3F normal, final Vector3F tangent) {
			this.textureCoordinates = Objects.requireNonNull(textureCoordinates, "textureCoordinates == null");
			this.position = Objects.requireNonNull(position, "position == null");
			this.normal = Objects.requireNonNull(normal, "normal == null");
			this.tangent = Objects.requireNonNull(tangent, "tangent == null");
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Compares {@code object} to this {@code Vertex} instance for equality.
		 * <p>
		 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Vertex}, and their respective values are equal, {@code false} otherwise.
		 * 
		 * @param object the {@code Object} to compare to this {@code Vertex} instance for equality
		 * @return {@code true} if, and only if, {@code object} is an instance of {@code Vertex}, and their respective values are equal, {@code false} otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			} else if(!(object instanceof Vertex)) {
				return false;
			} else if(!Objects.equals(this.textureCoordinates, Vertex.class.cast(object).textureCoordinates)) {
				return false;
			} else if(!Objects.equals(this.position, Vertex.class.cast(object).position)) {
				return false;
			} else if(!Objects.equals(this.normal, Vertex.class.cast(object).normal)) {
				return false;
			} else if(!Objects.equals(this.tangent, Vertex.class.cast(object).tangent)) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Returns a hash code for this {@code Vertex} instance.
		 * 
		 * @return a hash code for this {@code Vertex} instance
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.textureCoordinates, this.position, this.normal, this.tangent);
		}
		
		/**
		 * Returns the texture coordinates of this {@code Vertex} instance.
		 * 
		 * @return the texture coordinates of this {@code Vertex} instance
		 */
		public Point2F getTextureCoordinates() {
			return this.textureCoordinates;
		}
		
		/**
		 * Returns the position of this {@code Vertex} instance.
		 * 
		 * @return the position of this {@code Vertex} instance
		 */
		public Point3F getPosition() {
			return this.position;
		}
		
		/**
		 * Returns a {@code String} representation of this {@code Vertex} instance.
		 * 
		 * @return a {@code String} representation of this {@code Vertex} instance
		 */
		@Override
		public String toString() {
			return String.format("new Vertex(%s, %s, %s)", this.textureCoordinates, this.position, this.normal);
		}
		
		/**
		 * Returns the normal of this {@code Vertex} instance.
		 * 
		 * @return the normal of this {@code Vertex} instance
		 */
		public Vector3F getNormal() {
			return this.normal;
		}
		
		/**
		 * Returns the tangent of this {@code Vertex} instance.
		 * 
		 * @return the tangent of this {@code Vertex} instance
		 */
		public Vector3F getTangent() {
			return this.tangent;
		}
		
		/**
		 * Sets a new position.
		 * <p>
		 * Returns a new {@code Vertex} with the new position set.
		 * <p>
		 * If {@code position} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param position the new position
		 * @return a new {@code Vertex} with the new position set
		 * @throws NullPointerException thrown if, and only if, {@code position} is {@code null}
		 */
		public Vertex setPosition(final Point3F position) {
			return new Vertex(this.textureCoordinates, position, this.normal, this.tangent);
		}
		
		/**
		 * Sets new texture coordinates.
		 * <p>
		 * Returns a new {@code Vertex} with the new texture coordinates set.
		 * <p>
		 * If {@code textureCoordinates} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param textureCoordinates the new texture coordinates
		 * @return a new {@code Vertex} with the new texture coordinates set
		 * @throws NullPointerException thrown if, and only if, {@code textureCoordinates} is {@code null}
		 */
		public Vertex setTextureCoordinates(final Point2F textureCoordinates) {
			return new Vertex(textureCoordinates, this.position, this.normal, this.tangent);
		}
		
		/**
		 * Transforms this {@code Vertex} given the {@link Matrix44F} {@code m}.
		 * <p>
		 * Returns a new {@code Vertex} with the transformation performed.
		 * <p>
		 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param m a {@code Matrix44}
		 * @return a new {@code Vertex} with the transformation performed
		 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
		 */
		public Vertex transform(final Matrix44F m) {
			return new Vertex(this.textureCoordinates, this.position.transformAndDivide(m), this.normal, this.tangent);
		}
		
		/**
		 * Transforms this {@code Vertex} from World Space to Object Space.
		 * <p>
		 * Returns a new {@code Vertex} with the transformation performed.
		 * <p>
		 * If either {@code objectToWorld} or {@code worldToObject} are {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param objectToWorld a {@code Matrix44F}
		 * @param worldToObject a {@code Matrix44F}
		 * @return a new {@code Vertex} with the transformation performed
		 * @throws NullPointerException thrown if, and only if, either {@code objectToWorld} or {@code worldToObject} are {@code null}
		 */
		public Vertex transformToObjectSpace(final Matrix44F objectToWorld, final Matrix44F worldToObject) {
			return new Vertex(this.textureCoordinates, this.position.transformAndDivide(worldToObject), this.normal.transformTranspose(objectToWorld), this.tangent.transformTranspose(objectToWorld));
		}
		
		/**
		 * Transforms this {@code Vertex} from Object Space to World Space.
		 * <p>
		 * Returns a new {@code Vertex} with the transformation performed.
		 * <p>
		 * If either {@code objectToWorld} or {@code worldToObject} are {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param objectToWorld a {@code Matrix44F}
		 * @param worldToObject a {@code Matrix44F}
		 * @return a new {@code Vertex} with the transformation performed
		 * @throws NullPointerException thrown if, and only if, either {@code objectToWorld} or {@code worldToObject} are {@code null}
		 */
		public Vertex transformToWorldSpace(final Matrix44F objectToWorld, final Matrix44F worldToObject) {
			return new Vertex(this.textureCoordinates, this.position.transformAndDivide(objectToWorld), this.normal.transformTranspose(worldToObject), this.tangent.transformTranspose(worldToObject));
		}
		
		/**
		 * Translates this {@code Vertex} in the X-direction.
		 * <p>
		 * Returns a new {@code Vertex} with the translation performed.
		 * 
		 * @param x the amount to translate this {@code Vertex} in the X-direction
		 * @return a new {@code Vertex} with the translation performed
		 */
		public Vertex translateX(final float x) {
			return new Vertex(this.textureCoordinates, new Point3F(this.position.x + x, this.position.y, this.position.z), this.normal, this.tangent);
		}
		
		/**
		 * Translates this {@code Vertex} in the Y-direction.
		 * <p>
		 * Returns a new {@code Vertex} with the translation performed.
		 * 
		 * @param y the amount to translate this {@code Vertex} in the Y-direction
		 * @return a new {@code Vertex} with the translation performed
		 */
		public Vertex translateY(final float y) {
			return new Vertex(this.textureCoordinates, new Point3F(this.position.x, this.position.y + y, this.position.z), this.normal, this.tangent);
		}
		
		/**
		 * Translates this {@code Vertex} in the Z-direction.
		 * <p>
		 * Returns a new {@code Vertex} with the translation performed.
		 * 
		 * @param z the amount to translate this {@code Vertex} in the Z-direction
		 * @return a new {@code Vertex} with the translation performed
		 */
		public Vertex translateZ(final float z) {
			return new Vertex(this.textureCoordinates, new Point3F(this.position.x, this.position.y, this.position.z + z), this.normal, this.tangent);
		}
	}
}