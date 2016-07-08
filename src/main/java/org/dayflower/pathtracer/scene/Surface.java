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
 * A {@code Surface} is a model of a surface with different properties.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Surface {
	private final Color emission;
	private final float perlinNoiseAmount;
	private final float perlinNoiseScale;
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Surface} instance.
	 * <p>
	 * If either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param emission the emission of this {@code Surface}
	 * @param perlinNoiseAmount the Perlin Noise amount of this {@code Surface}
	 * @param perlinNoiseScale the Perlin Noise scale of this {@code Surface}
	 * @param material the {@link Material} of this {@code Surface}
	 * @param textureAlbedo the Albedo {@link Texture} of this {@code Surface}
	 * @param textureNormal the Normal Map {@code Texture} of this {@code Surface}
	 * @throws NullPointerException thrown if, and only if, either {@code emission}, {@code material}, {@code textureAlbedo} or {@code textureNormal} are {@code null}
	 */
	public Surface(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		this.emission = Objects.requireNonNull(emission, "emission == null");
		this.perlinNoiseAmount = perlinNoiseAmount;
		this.perlinNoiseScale = perlinNoiseScale;
		this.material = Objects.requireNonNull(material, "material == null");
		this.textureAlbedo = Objects.requireNonNull(textureAlbedo, "textureAlbedo == null");
		this.textureNormal = Objects.requireNonNull(textureNormal, "textureNormal == null");
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
		if(object == this) {
			return true;
		} else if(!(object instanceof Surface)) {
			return false;
		} else if(!Objects.equals(this.emission, Surface.class.cast(object).emission)) {
			return false;
		} else if(Float.compare(this.perlinNoiseAmount, Surface.class.cast(object).perlinNoiseAmount) != 0) {
			return false;
		} else if(Float.compare(this.perlinNoiseScale, Surface.class.cast(object).perlinNoiseScale) != 0) {
			return false;
		} else if(!Objects.equals(this.material, Surface.class.cast(object).material)) {
			return false;
		} else if(!Objects.equals(this.textureAlbedo, Surface.class.cast(object).textureAlbedo)) {
			return false;
		} else if(!Objects.equals(this.textureNormal, Surface.class.cast(object).textureNormal)) {
			return false;
		} else {
			return true;
		}
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
		return Objects.hash(this.emission, Float.valueOf(this.perlinNoiseAmount), Float.valueOf(this.perlinNoiseScale), this.material, this.textureAlbedo, this.textureNormal);
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
}