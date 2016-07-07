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

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;

/**
 * A {@link Shape} implementation that implements a plane.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Plane extends Shape {
	/**
	 * A {@link Point3} denoting the point A.
	 */
	public final Point3 a;
	
	/**
	 * A {@link Point3} denoting the point B.
	 */
	public final Point3 b;
	
	/**
	 * A {@link Point3} denoting the point C.
	 */
	public final Point3 c;
	
	/**
	 * The surface normal.
	 */
	public final Vector3 surfaceNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Plane} instance.
	 * <p>
	 * If either {@code emission}, {@code material}, {@code textureAlbedo}, {@code textureNormal}, {@code a}, {@code b} or {@code c} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param emission a {@link Color} denoting the emissivity of this {@code Plane}
	 * @param perlinNoiseAmount the Perlin Noise amount associated with this {@code Plane}, used for Perlin Noise Normal Mapping
	 * @param perlinNoiseScale the Perlin Noise scale associated with this {@code Plane}, used for Perlin Noise Normal Mapping
	 * @param material the {@link Material} used for this {@code Plane}
	 * @param textureAlbedo the {@link Texture} used for the albedo of this {@code Plane}
	 * @param textureNormal the {@code Texture} used for Normal Mapping of this {@code Plane}
	 * @param a a {@link Point3} denoting the point A
	 * @param b a {@code Point3} denoting the point A
	 * @param c a {@code Point3} denoting the point A
	 * @throws NullPointerException thrown if, and only if, either {@code emission}, {@code material}, {@code textureAlbedo}, {@code textureNormal}, {@code a}, {@code b} or {@code c} are {@code null}
	 */
	public Plane(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final Point3 a, final Point3 b, final Point3 c) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.a = Objects.requireNonNull(a, "a == null");
		this.b = Objects.requireNonNull(b, "b == null");
		this.c = Objects.requireNonNull(c, "c == null");
		this.surfaceNormal = Vector3.normalNormalized(this.a, this.b, this.c);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the point A.
	 * 
	 * @return the point A
	 */
	public Point3 getA() {
		return this.a;
	}
	
	/**
	 * Returns the point B.
	 * 
	 * @return the point B
	 */
	public Point3 getB() {
		return this.b;
	}
	
	/**
	 * Returns the point C.
	 * 
	 * @return the point C
	 */
	public Point3 getC() {
		return this.c;
	}
	
	/**
	 * Returns the surface normal.
	 * 
	 * @return the surface normal
	 */
	public Vector3 getSurfaceNormal() {
		return this.surfaceNormal;
	}
}