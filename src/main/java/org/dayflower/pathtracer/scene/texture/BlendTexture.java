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
package org.dayflower.pathtracer.scene.texture;

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code BlendTexture} is a {@link Texture} implementation that blends two other {@code Texture}s together.
 * <p>
 * There is one restriction with this {@code BlendTexture} class. None of the {@code Texture}s to blend can be {@code BlendTexture}s themselves. The renderer cannot currently handle multiple layers of blending. This restriction might be fixed in the
 * future.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class BlendTexture implements Texture {
	/**
	 * The relative offset of the Texture A Offset parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_TEXTURE_A_OFFSET = 2;
	
	/**
	 * The relative offset of the Texture B Offset parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_TEXTURE_B_OFFSET = 3;
	
	/**
	 * The relative offset of the Factor parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_FACTOR = 4;
	
	/**
	 * The size of a {@code BlendTexture} in the {@code float} array. The size is {@code 5}.
	 */
	public static final int SIZE = 5;
	
	/**
	 * The type number associated with a {@code BlendTexture}. The number is {@code 1}.
	 */
	public static final int TYPE = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Texture textureA;
	private final Texture textureB;
	private final float factor;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code BlendTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new BlendTexture(textureA, textureB, 0.5F)}.
	 * <p>
	 * If either {@code textureA} or {@code textureB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code textureA} or {@code textureB} are {@code BlendTexture}s, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param textureA a {@link Texture}
	 * @param textureB a {@code Texture}
	 * @throws IllegalArgumentException thrown if, and only if, either {@code textureA} or {@code textureB} are {@code BlendTexture}s
	 * @throws NullPointerException thrown if, and only if, either {@code textureA} or {@code textureB} are {@code null}
	 */
	public BlendTexture(final Texture textureA, final Texture textureB) {
		this(textureA, textureB, 0.5F);
	}
	
	/**
	 * Constructs a new {@code BlendTexture} instance.
	 * <p>
	 * If either {@code textureA} or {@code textureB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If either {@code textureA} or {@code textureB} are {@code BlendTexture}s, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param textureA a {@link Texture}
	 * @param textureB a {@code Texture}
	 * @param factor the blend factor
	 * @throws IllegalArgumentException thrown if, and only if, either {@code textureA} or {@code textureB} are {@code BlendTexture}s
	 * @throws NullPointerException thrown if, and only if, either {@code textureA} or {@code textureB} are {@code null}
	 */
	public BlendTexture(final Texture textureA, final Texture textureB, final float factor) {
		this.textureA = doRequireValidTexture(textureA, "textureA == null", "textureA cannot be BlendTexture");
		this.textureB = doRequireValidTexture(textureB, "textureB == null", "textureB cannot be BlendTexture");
		this.factor = factor;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code BlendTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code BlendTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		Objects.requireNonNull(primitiveIntersection, "primitiveIntersection == null");
		
		final Color colorA = this.textureA.getColor(primitiveIntersection);
		final Color colorB = this.textureB.getColor(primitiveIntersection);
		
		return Color.blend(colorA, colorB, this.factor);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code BlendTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code BlendTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new BlendTexture(%s, %s, %s)", this.textureA, this.textureB, Float.toString(this.factor));
	}
	
	/**
	 * Returns the {@link Texture} denoted by {@code A}.
	 * 
	 * @return the {@code Texture} denoted by {@code A}
	 */
	public Texture getTextureA() {
		return this.textureA;
	}
	
	/**
	 * Returns the {@link Texture} denoted by {@code B}.
	 * 
	 * @return the {@code Texture} denoted by {@code B}
	 */
	public Texture getTextureB() {
		return this.textureB;
	}
	
	/**
	 * Compares {@code object} to this {@code BlendTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code BlendTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code BlendTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code BlendTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof BlendTexture)) {
			return false;
		} else if(!Objects.equals(this.textureA, BlendTexture.class.cast(object).textureA)) {
			return false;
		} else if(!Objects.equals(this.textureB, BlendTexture.class.cast(object).textureB)) {
			return false;
		} else if(Float.compare(this.factor, BlendTexture.class.cast(object).factor) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code BlendTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code BlendTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return this.textureA.isEmissive() || this.textureB.isEmissive();
	}
	
	/**
	 * Returns the blend factor.
	 * 
	 * @return the blend factor
	 */
	public float getFactor() {
		return this.factor;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code BlendTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code BlendTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			0.0F,
			0.0F,
			getFactor()
		};
	}
	
	/**
	 * Returns the size of this {@code BlendTexture} instance.
	 * 
	 * @return the size of this {@code BlendTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code BlendTexture} instance.
	 * 
	 * @return the type of this {@code BlendTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code BlendTexture} instance.
	 * 
	 * @return a hash code for this {@code BlendTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.textureA, this.textureB, Float.valueOf(this.factor));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Texture doRequireValidTexture(final Texture texture, final String nullMessage, final String illegalTextureMessage) {
		if(texture == null) {
			throw new NullPointerException(nullMessage);
		}
		
		if(texture instanceof BlendTexture) {
			throw new IllegalArgumentException(illegalTextureMessage);
		}
		
		return texture;
	}
}