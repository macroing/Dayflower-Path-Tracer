/**
 * Copyright 2009 - 2018 J&#246;rgen Lundgren
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

import org.dayflower.pathtracer.math.Matrix44;
import org.dayflower.pathtracer.math.Point2;
import org.dayflower.pathtracer.math.Point3;
import org.dayflower.pathtracer.math.Vector3;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;

/**
 * A {@link Shape} implementation that implements a triangle.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Triangle extends Shape {
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
	 * If either {@code surface}, {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface a {@link Surface} denoting the surface of this {@code Triangle}
	 * @param a a {@link Vertex} denoted by A
	 * @param b a {@code Vertex} denoted by B
	 * @param c a {@code Vertex} denoted by C
	 * @throws NullPointerException thrown if, and only if, either {@code surface}, {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public Triangle(final Surface surface, final Vertex a, final Vertex b, final Vertex c) {
		super(surface);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		} else if(!Objects.equals(getSurface(), Triangle.class.cast(object).getSurface())) {
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
	 * Returns a hash code for this {@code Triangle} instance.
	 * 
	 * @return a hash code for this {@code Triangle} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getSurface(), this.a, this.b, this.c);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Triangle} instance.
	 * 
	 * @return a {@code String} representation of this {@code Triangle} instance
	 */
	@Override
	public String toString() {
		return String.format("Triangle: [A=%s], [B=%s], [C=%s]", this.a, this.b, this.c);
	}
	
	/**
	 * Rotates this {@code Triangle} instance.
	 * <p>
	 * Returns a new rotated version of this {@code Triangle} instance.
	 * <p>
	 * If either {@code v} or {@code w} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param v a {@link Vector3}
	 * @param w a {@code Vector3}
	 * @return a new rotated version of this {@code Triangle} instance
	 * @throws NullPointerException thrown if, and only if, either {@code v} or {@code w} are {@code null}
	 */
	public Triangle rotate(final Vector3 v, final Vector3 w) {
		final Matrix44 m = Matrix44.rotation(v, w);
		
		return new Triangle(getSurface(), getA().transform(m), getB().transform(m), getC().transform(m));
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
		
		return new Triangle(getSurface(), this.a.setPosition(new Point3(a1X, a1Y, a1Z)), this.b.setPosition(new Point3(b1X, b1Y, b1Z)), this.c.setPosition(new Point3(c1X, c1Y, c1Z)));
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
		return new Triangle(getSurface(), getA().translateX(x), getB().translateX(x), getC().translateX(x));
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
		return new Triangle(getSurface(), getA().translateY(y), getB().translateY(y), getC().translateY(y));
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
		return new Triangle(getSurface(), getA().translateZ(z), getB().translateZ(z), getC().translateZ(z));
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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the maximum {@link Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}.
	 * <p>
	 * If either {@code triangles} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param triangles a {@code List} with {@code Triangle}s
	 * @return the maximum {@code Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}
	 * @throws NullPointerException thrown if, and only if, either {@code triangles} or any of its elements are {@code null}
	 */
	public static Point3 maximum(final List<Triangle> triangles) {
		Point3 maximum = Point3.MINIMUM;
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.a.position;
			final Point3 b = triangle.b.position;
			final Point3 c = triangle.c.position;
			
			maximum = Point3.maximum(maximum, Point3.maximum(a, b, c));
		}
		
		return maximum;
	}
	
	/**
	 * Returns the minimum {@link Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}.
	 * <p>
	 * If either {@code triangles} or any of its elements are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param triangles a {@code List} with {@code Triangle}s
	 * @return the minimum {@code Point3} from the positions of the {@code Triangle}s in the {@code List} {@code triangles}
	 * @throws NullPointerException thrown if, and only if, either {@code triangles} or any of its elements are {@code null}
	 */
	public static Point3 minimum(final List<Triangle> triangles) {
		Point3 minimum = Point3.MAXIMUM;
		
		for(final Triangle triangle : triangles) {
			final Point3 a = triangle.a.position;
			final Point3 b = triangle.b.position;
			final Point3 c = triangle.c.position;
			
			minimum = Point3.minimum(minimum, Point3.minimum(a, b, c));
		}
		
		return minimum;
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
		public final Point2 textureCoordinates;
		
		/**
		 * The position of this {@code Vertex} instance.
		 */
		public final Point3 position;
		
		/**
		 * The normal of this {@code Vertex} instance.
		 */
		public final Vector3 normal;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code Vertex} instance.
		 * <p>
		 * If either {@code textureCoordinates}, {@code position} or {@code normal} are {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param textureCoordinates the texture coordinates of this {@code Vertex}
		 * @param position the position of this {@code Vertex}
		 * @param normal the normal of this {@code Vertex}
		 * @throws NullPointerException thrown if, and only if, either {@code textureCoordinates}, {@code position} or {@code normal} are {@code null}
		 */
		public Vertex(final Point2 textureCoordinates, final Point3 position, final Vector3 normal) {
			this.textureCoordinates = Objects.requireNonNull(textureCoordinates, "textureCoordinates == null");
			this.position = Objects.requireNonNull(position, "position == null");
			this.normal = Objects.requireNonNull(normal, "normal == null");
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
			return Objects.hash(this.textureCoordinates, this.position, this.normal);
		}
		
		/**
		 * Returns the texture coordinates of this {@code Vertex} instance.
		 * 
		 * @return the texture coordinates of this {@code Vertex} instance
		 */
		public Point2 getTextureCoordinates() {
			return this.textureCoordinates;
		}
		
		/**
		 * Returns the position of this {@code Vertex} instance.
		 * 
		 * @return the position of this {@code Vertex} instance
		 */
		public Point3 getPosition() {
			return this.position;
		}
		
		/**
		 * Returns a {@code String} representation of this {@code Vertex} instance.
		 * 
		 * @return a {@code String} representation of this {@code Vertex} instance
		 */
		@Override
		public String toString() {
			return String.format("Vertex: [TextureCoordinates=%s], [Position=%s], [Normal=%s]", this.textureCoordinates, this.position, this.normal);
		}
		
		/**
		 * Returns the normal of this {@code Vertex} instance.
		 * 
		 * @return the normal of this {@code Vertex} instance
		 */
		public Vector3 getNormal() {
			return this.normal;
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
		public Vertex setPosition(final Point3 position) {
			return new Vertex(this.textureCoordinates, position, this.normal);
		}
		
		/**
		 * Transforms this {@code Vertex} given the {@link Matrix44} {@code m}.
		 * <p>
		 * Returns a new {@code Vertex} with the transformation performed.
		 * <p>
		 * If {@code m} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param m a {@code Matrix44}
		 * @return a new {@code Vertex} with the transformation performed
		 * @throws NullPointerException thrown if, and only if, {@code m} is {@code null}
		 */
		public Vertex transform(final Matrix44 m) {
			return new Vertex(this.textureCoordinates, this.position.transform(m), this.normal);
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
			return new Vertex(this.textureCoordinates, this.position.translateX(x), this.normal);
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
			return new Vertex(this.textureCoordinates, this.position.translateY(y), this.normal);
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
			return new Vertex(this.textureCoordinates, this.position.translateZ(z), this.normal);
		}
	}
}