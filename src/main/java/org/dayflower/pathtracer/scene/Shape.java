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
package org.dayflower.pathtracer.scene;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;

//TODO: Add Javadocs.
public abstract class Shape {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_EMISSION = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_MATERIAL = 5;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT = 8;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_PERLIN_NOISE_SCALE = 9;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO = 6;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL = 7;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_TYPE = 0;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color emission;
	private final float perlinNoiseAmount;
	private final float perlinNoiseScale;
	private int offset;
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	protected Shape(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		this.emission = Objects.requireNonNull(emission, "emission == null");
		this.perlinNoiseAmount = perlinNoiseAmount;
		this.perlinNoiseScale = perlinNoiseScale;
		this.material = Objects.requireNonNull(material, "material == null");
		this.textureAlbedo = Objects.requireNonNull(textureAlbedo, "textureAlbedo == null");
		this.textureNormal = Objects.requireNonNull(textureNormal, "textureNormal == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public final boolean isEmissive() {
		return !this.emission.isBlack();
	}
	
//	TODO: Add Javadocs.
	public final Color getEmission() {
		return this.emission;
	}
	
//	TODO: Add Javadocs.
	public final float getPerlinNoiseAmount() {
		return this.perlinNoiseAmount;
	}
	
//	TODO: Add Javadocs.
	public final float getPerlinNoiseScale() {
		return this.perlinNoiseScale;
	}
	
//	TODO: Add Javadocs.
	public abstract float[] toFloatArray();
	
//	TODO: Add Javadocs.
	public final int getOffset() {
		return this.offset;
	}
	
//	TODO: Add Javadocs.
	public abstract int size();
	
//	TODO: Add Javadocs.
	public final Material getMaterial() {
		return this.material;
	}
	
//	TODO: Add Javadocs.
	public final Texture getTextureAlbedo() {
		return this.textureAlbedo;
	}
	
//	TODO: Add Javadocs.
	public final Texture getTextureNormal() {
		return this.textureNormal;
	}
	
//	TODO: Add Javadocs.
	public final void setOffset(final int offset) {
		this.offset = offset;
	}
}