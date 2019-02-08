/**
 * Copyright 2015 - 2019 J&#246;rgen Lundgren
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;

/**
 * A {@code Surface} is a model of a surface with different properties.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Surface {
	private static final Map<String, Surface> INSTANCES = new HashMap<>();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color emission;
	private final float perlinNoiseAmount;
	private final float perlinNoiseScale;
	private final int hashCode;
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Surface(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		this.emission = emission;
		this.perlinNoiseAmount = perlinNoiseAmount;
		this.perlinNoiseScale = perlinNoiseScale;
		this.material = material;
		this.textureAlbedo = textureAlbedo;
		this.textureNormal = textureNormal;
		this.hashCode = Objects.hash(this.emission, Float.valueOf(this.perlinNoiseAmount), Float.valueOf(this.perlinNoiseScale), this.material, this.textureAlbedo, this.textureNormal);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Surface} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Surface}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Surface} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Surface}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		return object == this;
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code Surface} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Surface} instance is emissive, {@code false} otherwise
	 */
	public boolean isEmissive() {
		return !this.emission.isBlack();
	}
	
	/**
	 * Returns the emission of this {@code Surface} instance.
	 * 
	 * @return the emission of this {@code Surface} instance
	 */
	public Color getEmission() {
		return this.emission;
	}
	
	/**
	 * Returns the Perlin Noise amount of this {@code Surface} instance.
	 * 
	 * @return the Perlin Noise amount of this {@code Surface} instance
	 */
	public float getPerlinNoiseAmount() {
		return this.perlinNoiseAmount;
	}
	
	/**
	 * Returns the Perlin Noise scale of this {@code Surface} instance.
	 * 
	 * @return the Perlin Noise scale of this {@code Surface} instance
	 */
	public float getPerlinNoiseScale() {
		return this.perlinNoiseScale;
	}
	
	/**
	 * Returns a hash code for this {@code Surface} instance.
	 * 
	 * @return a hash code for this {@code Surface} instance
	 */
	@Override
	public int hashCode() {
		return this.hashCode;
	}
	
	/**
	 * Returns the {@link Material} of this {@code Surface} instance.
	 * 
	 * @return the {@code Material} of this {@code Surface} instance
	 */
	public Material getMaterial() {
		return this.material;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Surface} instance.
	 * 
	 * @return a {@code String} representation of this {@code Surface} instance
	 */
	@Override
	public String toString() {
		return String.format("Surface: [Emission=%s], [PerlinNoiseAmount=%s], [PerlinNoiseScale=%s], [Material=%s], [TextureAlbedo=%s], [TextureNormal=%s]", this.emission, Float.toString(this.perlinNoiseAmount), Float.toString(this.perlinNoiseScale), this.material, this.textureAlbedo, this.textureNormal);
	}
	
	/**
	 * Returns the Albedo {@link Texture} of this {@code Surface} instance.
	 * 
	 * @return the Albedo {@code Texture} of this {@code Surface} instance
	 */
	public Texture getTextureAlbedo() {
		return this.textureAlbedo;
	}
	
	/**
	 * Returns the Normal Map {@link Texture} of this {@code Surface} instance.
	 * 
	 * @return the Normal Map {@code Texture} of this {@code Surface} instance
	 */
	public Texture getTextureNormal() {
		return this.textureNormal;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code Surface} instance.
	 * <p>
	 * If either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param emission the emission of the {@code Surface}
	 * @param perlinNoiseAmount the Perlin Noise amount of the {@code Surface}
	 * @param perlinNoiseScale the Perlin Noise scale of the {@code Surface}
	 * @param material the {@link Material} of the {@code Surface}
	 * @param textureAlbedo the Albedo {@link Texture} of the {@code Surface}
	 * @param textureNormal the Normal Map {@code Texture} of the {@code Surface}
	 * @return a {@code Surface} instance
	 * @throws NullPointerException thrown if, and only if, either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}
	 */
	public static Surface getInstance(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		final int hashCodeEmission = emission.hashCode();
		final int hashCodePerlinNoiseAmount = Float.hashCode(perlinNoiseAmount);
		final int hashCodePerlinNoiseScale = Float.hashCode(perlinNoiseScale);
		final int hashCodeMaterial = material.hashCode();
		final int hashCodeTextureAlbedo = textureAlbedo.hashCode();
		final int hashCodeTextureNormal = textureNormal.hashCode();
		
		final String key = "Surface" + hashCodeEmission + hashCodePerlinNoiseAmount + hashCodePerlinNoiseScale + hashCodeMaterial + hashCodeTextureAlbedo + hashCodeTextureNormal;
		
		synchronized(INSTANCES) {
			return INSTANCES.computeIfAbsent(key, key0 -> new Surface(emission, perlinNoiseAmount, perlinNoiseScale, material, textureAlbedo, textureNormal));
		}
	}
}