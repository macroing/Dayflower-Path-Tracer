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

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;

/**
 * A {@code Shape} is a model of a shape.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class Shape {
	private final Color emission;
	private final float perlinNoiseAmount;
	private final float perlinNoiseScale;
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Shape} instance.
	 * <p>
	 * If either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param emission a {@link Color} denoting the emissivity of this {@code Shape}
	 * @param perlinNoiseAmount the Perlin Noise amount associated with this {@code Shape}, used for Perlin Noise Normal Mapping
	 * @param perlinNoiseScale the Perlin Noise scale associated with this {@code Shape}, used for Perlin Noise Normal Mapping
	 * @param material the {@link Material} used for this {@code Shape}
	 * @param textureAlbedo the {@link Texture} used for the albedo of this {@code Shape}
	 * @param textureNormal the {@code Texture} used for Normal Mapping of this {@code Shape}
	 * @throws NullPointerException thrown if, and only if, either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}
	 */
	protected Shape(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		this.emission = Objects.requireNonNull(emission, "emission == null");
		this.perlinNoiseAmount = perlinNoiseAmount;
		this.perlinNoiseScale = perlinNoiseScale;
		this.material = Objects.requireNonNull(material, "material == null");
		this.textureAlbedo = Objects.requireNonNull(textureAlbedo, "textureAlbedo == null");
		this.textureNormal = Objects.requireNonNull(textureNormal, "textureNormal == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, this {@code Shape} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Shape} instance is emissive, {@code false} otherwise
	 */
	public final boolean isEmissive() {
		return !this.emission.isBlack();
	}
	
	/**
	 * Returns the {@link Color} denoting the emissivity of this {@code Shape} instance.
	 * 
	 * @return the {@code Color} denoting the emissivity of this {@code Shape} instance
	 */
	public final Color getEmission() {
		return this.emission;
	}
	
	/**
	 * Returns the Perlin Noise amount associated with this {@code Shape} instance.
	 * <p>
	 * The Perlin Noise amount is used for Perlin Noise Normal Mapping.
	 * 
	 * @return the Perlin Noise amount associated with this {@code Shape} instance
	 */
	public final float getPerlinNoiseAmount() {
		return this.perlinNoiseAmount;
	}
	
	/**
	 * Returns the Perlin Noise scale associated with this {@code Shape} instance.
	 * <p>
	 * The Perlin Noise scale is used for Perlin Noise Normal Mapping.
	 * 
	 * @return the Perlin Noise scale associated with this {@code Shape} instance
	 */
	public final float getPerlinNoiseScale() {
		return this.perlinNoiseScale;
	}
	
	/**
	 * Returns the {@link Material} associated with this {@code Shape} instance.
	 * 
	 * @return the {@code Material} associated with this {@code Shape} instance
	 */
	public final Material getMaterial() {
		return this.material;
	}
	
	/**
	 * Returns the {@link Texture} associated with this {@code Shape} instance for its albedo.
	 * 
	 * @return the {@code Texture} associated with this {@code Shape} instance for its albedo
	 */
	public final Texture getTextureAlbedo() {
		return this.textureAlbedo;
	}
	
	/**
	 * Returns the {@link Texture} associated with this {@code Shape} instance for Normal Mapping.
	 * 
	 * @return the {@code Texture} associated with this {@code Shape} instance for Normal Mapping
	 */
	public final Texture getTextureNormal() {
		return this.textureNormal;
	}
}