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

/**
 * A {@link Shape} implementation that implements a sphere.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Sphere extends Shape {
	private final float radius;
	private final Point3 position;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Sphere} instance.
	 * <p>
	 * If either {@code emission}, {@code material}, {@code textureAlbedo}, {@code textureNormal} or {@code position} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param emission a {@link Color} denoting the emissivity of this {@code Sphere}
	 * @param perlinNoiseAmount the Perlin Noise amount associated with this {@code Sphere}, used for Perlin Noise Normal Mapping
	 * @param perlinNoiseScale the Perlin Noise scale associated with this {@code Sphere}, used for Perlin Noise Normal Mapping
	 * @param material the {@link Material} used for this {@code Sphere}
	 * @param textureAlbedo the {@link Texture} used for the albedo of this {@code Sphere}
	 * @param textureNormal the {@code Texture} used for Normal Mapping of this {@code Sphere}
	 * @param radius the radius of this {@code Sphere}
	 * @param position the position of this {@code Sphere}
	 * @throws NullPointerException thrown if, and only if, either {@code emission}, {@code material}, {@code textureAlbedo}, {@code textureNormal} or {@code position} are {@code null}
	 */
	public Sphere(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final float radius, final Point3 position) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.radius = radius;
		this.position = Objects.requireNonNull(position, "position == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise.
	 * <p>
	 * Calling this method is equivalent to calling {@code isWithinRadius(x, y, z, 0.0F)}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise
	 */
	public boolean isWithinRadius(final float x, final float y, final float z) {
		return isWithinRadius(x, y, z, 0.0F);
	}
	
	/**
	 * Returns {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise.
	 * <p>
	 * The actual radius will be {@code sphere.getRadius() + threshold}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @param threshold the threshold value
	 * @return {@code true} if, and only if, the point denoted by {@code x}, {@code y} and {@code z} is within radius to this {@code Sphere} instance, {@code false} otherwise
	 */
	public boolean isWithinRadius(final float x, final float y, final float z, final float threshold) {
		final float radius = getRadius() + threshold;
		final float radiusSquared = radius * radius;
		final float deltaX = x - getPosition().x;
		final float deltaY = y - getPosition().y;
		final float deltaZ = z - getPosition().z;
		final float distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		
		return distanceSquared < radiusSquared;
	}
	
	/**
	 * Returns the radius of this {@code Sphere}.
	 * 
	 * @return the radius of this {@code Sphere}
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/**
	 * Returns the center position of this {@code Sphere}.
	 * 
	 * @return the center position of this {@code Sphere}
	 */
	public Point3 getPosition() {
		return this.position;
	}
}