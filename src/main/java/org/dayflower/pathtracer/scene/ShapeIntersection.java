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
package org.dayflower.pathtracer.scene;

import java.util.Objects;

import org.macroing.math4j.OrthoNormalBasis33F;
import org.macroing.math4j.Point2F;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.Vector3F;

/**
 * A {@code ShapeIntersection} contains the properties of an intersected {@link Shape}.
 * <p>
 * This class is immutable and therefore thread-safe.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ShapeIntersection {
	private final OrthoNormalBasis33F orthoNormalBasis;
	private final Point2F textureCoordinates;
	private final Point3F surfaceIntersectionPoint;
	private final Shape shape;
	private final Vector3F surfaceNormal;
	private final float t;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ShapeIntersection} instance.
	 * <p>
	 * If either {@code orthoNormalBasis}, {@code textureCoordinates}, {@code surfaceIntersectionPoint}, {@code shape} or {@code surfaceNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param orthoNormalBasis the orthonormal basis for the surface
	 * @param textureCoordinates the texture coordinates
	 * @param surfaceIntersectionPoint the surface intersection point
	 * @param shape the {@link Shape} for which the {@code ShapeIntersection} is created
	 * @param surfaceNormal the surface normal
	 * @param t the parametric {@code t}
	 * @throws NullPointerException thrown if, and only if, either {@code orthoNormalBasis}, {@code textureCoordinates}, {@code surfaceIntersectionPoint}, {@code shape} or {@code surfaceNormal} are {@code null}
	 */
	public ShapeIntersection(final OrthoNormalBasis33F orthoNormalBasis, final Point2F textureCoordinates, final Point3F surfaceIntersectionPoint, final Shape shape, final Vector3F surfaceNormal, final float t) {
		this.orthoNormalBasis = Objects.requireNonNull(orthoNormalBasis, "orthoNormalBasis == null");
		this.textureCoordinates = Objects.requireNonNull(textureCoordinates, "textureCoordinates == null");
		this.surfaceIntersectionPoint = Objects.requireNonNull(surfaceIntersectionPoint, "surfaceIntersectionPoint == null");
		this.shape = Objects.requireNonNull(shape, "shape == null");
		this.surfaceNormal = surfaceNormal.normalize();
		this.t = t;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns an {@link OrthoNormalBasis33F} denoting the orthonormal basis for the surface.
	 * <p>
	 * The {@code W} component of the orthonormal basis refers to the surface normal.
	 * 
	 * @return an {@code OrthoNormalBasis33F} denoting the orthonormal basis for the surface
	 */
	public OrthoNormalBasis33F getOrthoNormalBasis() {
		return this.orthoNormalBasis;
	}
	
	/**
	 * Returns a {@link Point2F} with the texture coordinates.
	 * 
	 * @return a {@code Point2F} with the texture coordinates
	 */
	public Point2F getTextureCoordinates() {
		return this.textureCoordinates;
	}
	
	/**
	 * Returns a {@link Point3F} with the surface intersection point.
	 * 
	 * @return a {@code Point3F} with the surface intersection point
	 */
	public Point3F getSurfaceIntersectionPoint() {
		return this.surfaceIntersectionPoint;
	}
	
	/**
	 * Returns the {@link Shape} for which this {@code ShapeInstance} was created.
	 * 
	 * @return the {@code Shape} for which this {@code ShapeInstance} was created
	 */
	public Shape getShape() {
		return this.shape;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code ShapeIntersection} instance.
	 * 
	 * @return a {@code String} representation of this {@code ShapeIntersection} instance
	 */
	@Override
	public String toString() {
		return String.format("new ShapeIntersection(%s, %s, %s, %s, %s, %s)", this.orthoNormalBasis, this.textureCoordinates, this.surfaceIntersectionPoint, this.shape, this.surfaceNormal, Float.toString(this.t));
	}
	
	/**
	 * Returns a {@link Vector3F} with the normalized surface normal.
	 * 
	 * @return a {@code Vector3F} with the normalized surface normal
	 */
	public Vector3F getSurfaceNormal() {
		return this.surfaceNormal;
	}
	
	/**
	 * Compares {@code object} to this {@code ShapeIntersection} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code ShapeIntersection}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code ShapeIntersection} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code ShapeIntersection}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof ShapeIntersection)) {
			return false;
		} else if(!Objects.equals(this.orthoNormalBasis, ShapeIntersection.class.cast(object).orthoNormalBasis)) {
			return false;
		} else if(!Objects.equals(this.textureCoordinates, ShapeIntersection.class.cast(object).textureCoordinates)) {
			return false;
		} else if(!Objects.equals(this.surfaceIntersectionPoint, ShapeIntersection.class.cast(object).surfaceIntersectionPoint)) {
			return false;
		} else if(!Objects.equals(this.shape, ShapeIntersection.class.cast(object).shape)) {
			return false;
		} else if(!Objects.equals(this.surfaceNormal, ShapeIntersection.class.cast(object).surfaceNormal)) {
			return false;
		} else if(Float.compare(this.t, ShapeIntersection.class.cast(object).t) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the parametric {@code t}.
	 * 
	 * @return the parametric {@code t}
	 */
	public float getT() {
		return this.t;
	}
	
	/**
	 * Returns a hash code for this {@code ShapeIntersection} instance.
	 * 
	 * @return a hash code for this {@code ShapeIntersection} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.orthoNormalBasis, this.textureCoordinates, this.surfaceIntersectionPoint, this.shape, this.surfaceNormal, Float.valueOf(this.t));
	}
}