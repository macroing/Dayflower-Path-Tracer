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

import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;
import org.macroing.image4j.Color;

/**
 * A {@code ConstantTexture} is a {@link Texture} implementation that models a texture with a constant color.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ConstantTexture implements Texture {
	/**
	 * The relative offset of the Color parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_COLOR = 2;
	
	/**
	 * The size of a {@code ConstantTexture} in the {@code float} array. The size is {@code 3}.
	 */
	public static final int SIZE = 3;
	
	/**
	 * The type number associated with a {@code ConstantTexture}. The number is {@code 4}.
	 */
	public static final int TYPE = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color color;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ConstantTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new ConstantTexture(Color.GREEN)}.
	 */
	public ConstantTexture() {
		this(Color.GREEN);
	}
	
	/**
	 * Constructs a new {@code ConstantTexture} instance given a {@link Color} as its color.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public ConstantTexture(final Color color) {
		this.color = Objects.requireNonNull(color, "color == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Color} assigned to this {@code ConstantTexture} instance.
	 * 
	 * @return the {@code Color} assigned to this {@code ConstantTexture} instance
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Returns a {@link Color} with the color of this {@code ConstantTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code ConstantTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		Objects.requireNonNull(primitiveIntersection, "primitiveIntersection == null");
		
		return this.color;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code ConstantTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code ConstantTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new ConstantTexture(%s)", this.color);
	}
	
	/**
	 * Compares {@code object} to this {@code ConstantTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code ConstantTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code ConstantTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code ConstantTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof ConstantTexture)) {
			return false;
		} else if(!Objects.equals(this.color, ConstantTexture.class.cast(object).color)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code ConstantTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code ConstantTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return !this.color.isBlack();
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code ConstantTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code ConstantTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			getColor().pack()
		};
	}
	
	/**
	 * Returns the size of this {@code ConstantTexture} instance.
	 * 
	 * @return the size of this {@code ConstantTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code ConstantTexture} instance.
	 * 
	 * @return the type of this {@code ConstantTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code ConstantTexture} instance.
	 * 
	 * @return a hash code for this {@code ConstantTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.color);
	}
}