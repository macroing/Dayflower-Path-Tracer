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

//TODO: Add Javadocs.
public final class Sphere extends Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_POSITION = 11;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_RADIUS = 10;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 14;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float radius;
	private final Point3 position;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Sphere(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal, final float radius, final Point3 position) {
		super(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal);
		
		this.radius = radius;
		this.position = Objects.requireNonNull(position, "position == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public boolean isWithinRadius(final float x, final float y, final float z) {
		return isWithinRadius(x, y, z, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public boolean isWithinRadius(final float x, final float y, final float z, final float threshold) {
		final float radius = getRadius() + threshold;
		final float radiusSquared = radius * radius;
		final float deltaX = x - getPosition().x;
		final float deltaY = y - getPosition().y;
		final float deltaZ = z - getPosition().z;
		final float distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		
		return distanceSquared < radiusSquared;
	}
	
//	TODO: Add Javadocs.
	public float getRadius() {
		return this.radius;
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
			getRadius(),
			getPosition().x,
			getPosition().y,
			getPosition().z
		};
	}
	
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return SIZE;
	}
	
//	TODO: Add Javadocs.
	public Point3 getPosition() {
		return this.position;
	}
}