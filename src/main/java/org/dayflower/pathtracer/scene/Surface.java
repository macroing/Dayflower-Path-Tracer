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

/**
 * A {@code Surface} is a model of a surface with different properties.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Surface {
	/**
	 * The relative offset of the Material parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int RELATIVE_OFFSET_MATERIAL = 0;
	
	/**
	 * The relative offset of the Noise Amount parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_NOISE_AMOUNT = 4;
	
	/**
	 * The relative offset of the Noise Scale parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int RELATIVE_OFFSET_NOISE_SCALE = 5;
	
	/**
	 * The relative offset of the Texture Albedo Offset parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET = 1;
	
	/**
	 * The relative offset of the Texture Emission Offset parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_TEXTURE_EMISSION_OFFSET = 2;
	
	/**
	 * The relative offset of the Texture Normal Offset parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_TEXTURE_NORMAL_OFFSET = 3;
	
	/**
	 * The size of a {@code Surface} in the {@code float} array. The size is {@code 6}.
	 */
	public static final int SIZE = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureEmission;
	private final Texture textureNormal;
	private final float noiseAmount;
	private final float noiseScale;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Surface} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Surface(material, textureAlbedo, textureEmission, textureNormal, 0.0F, 0.0F)}.
	 * <p>
	 * If either {@code material}, {@code textureAlbedo}, {@code textureEmission} or {@code textureNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param material a {@link Material}
	 * @param textureAlbedo an Albedo {@link Texture}
	 * @param textureEmission an Emission {@code Texture}
	 * @param textureNormal a Normal {@code Texture}
	 * @throws NullPointerException thrown if, and only if, either {@code material}, {@code textureAlbedo}, {@code textureEmission} or {@code textureNormal} are {@code null}
	 */
	public Surface(final Material material, final Texture textureAlbedo, final Texture textureEmission, final Texture textureNormal) {
		this(material, textureAlbedo, textureEmission, textureNormal, 0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Surface} instance.
	 * <p>
	 * If either {@code material}, {@code textureAlbedo}, {@code textureEmission} or {@code textureNormal} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param material a {@link Material}
	 * @param textureAlbedo an Albedo {@link Texture}
	 * @param textureEmission an Emission {@code Texture}
	 * @param textureNormal a Normal {@code Texture}
	 * @param noiseAmount the noise amount
	 * @param noiseScale the noise scale
	 * @throws NullPointerException thrown if, and only if, either {@code material}, {@code textureAlbedo}, {@code textureEmission} or {@code textureNormal} are {@code null}
	 */
	public Surface(final Material material, final Texture textureAlbedo, final Texture textureEmission, final Texture textureNormal, final float noiseAmount, final float noiseScale) {
		this.material = Objects.requireNonNull(material, "material == null");
		this.textureAlbedo = Objects.requireNonNull(textureAlbedo, "textureAlbedo == null");
		this.textureEmission = Objects.requireNonNull(textureEmission, "textureEmission == null");
		this.textureNormal = Objects.requireNonNull(textureNormal, "textureNormal == null");
		this.noiseAmount = noiseAmount;
		this.noiseScale = noiseScale;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		return String.format("new Surface(%s, %s, %s, %s, %s, %s)", this.material, this.textureAlbedo, this.textureEmission, this.textureNormal, Float.toString(this.noiseAmount), Float.toString(this.noiseScale));
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
	 * Returns the Emission {@link Texture} of this {@code Surface} instance.
	 * 
	 * @return the Emission {@link Texture} of this {@code Surface} instance
	 */
	public Texture getTextureEmission() {
		return this.textureEmission;
	}
	
	/**
	 * Returns the Normal Map {@link Texture} of this {@code Surface} instance.
	 * 
	 * @return the Normal Map {@code Texture} of this {@code Surface} instance
	 */
	public Texture getTextureNormal() {
		return this.textureNormal;
	}
	
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
		} else if(!Objects.equals(this.material, Surface.class.cast(object).material)) {
			return false;
		} else if(!Objects.equals(this.textureAlbedo, Surface.class.cast(object).textureAlbedo)) {
			return false;
		} else if(!Objects.equals(this.textureEmission, Surface.class.cast(object).textureEmission)) {
			return false;
		} else if(!Objects.equals(this.textureNormal, Surface.class.cast(object).textureNormal)) {
			return false;
		} else if(Float.compare(this.noiseAmount, Surface.class.cast(object).noiseAmount) != 0) {
			return false;
		} else if(Float.compare(this.noiseScale, Surface.class.cast(object).noiseScale) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the Noise amount of this {@code Surface} instance.
	 * 
	 * @return the Noise amount of this {@code Surface} instance
	 */
	public float getNoiseAmount() {
		return this.noiseAmount;
	}
	
	/**
	 * Returns the Noise scale of this {@code Surface} instance.
	 * 
	 * @return the Noise scale of this {@code Surface} instance
	 */
	public float getNoiseScale() {
		return this.noiseScale;
	}
	
	/**
	 * Returns a hash code for this {@code Surface} instance.
	 * 
	 * @return a hash code for this {@code Surface} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.material, this.textureAlbedo, this.textureEmission, this.textureNormal, Float.valueOf(this.noiseAmount), Float.valueOf(this.noiseScale));
	}
}