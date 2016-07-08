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
package org.dayflower.pathtracer.scene.texture;

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code SolidTexture} is a {@link Texture} implementation that models a texture with a solid color.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SolidTexture implements Texture {
	private final Color color;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code SolidTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new SolidTexture(Color.GREEN)}.
	 */
	public SolidTexture() {
		this(Color.GREEN);
	}
	
	/**
	 * Constructs a new {@code SolidTexture} instance given a {@link Color} as its color.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public SolidTexture(final Color color) {
		this.color = Objects.requireNonNull(color, "color == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code SolidTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code SolidTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code SolidTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code SolidTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof SolidTexture)) {
			return false;
		} else if(!Objects.equals(this.color, SolidTexture.class.cast(object).color)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns the {@link Color} assigned to this {@code SolidTexture} instance.
	 * 
	 * @return the {@code Color} assigned to this {@code SolidTexture} instance
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Returns a hash code for this {@code SolidTexture} instance.
	 * 
	 * @return a hash code for this {@code SolidTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.color);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code SolidTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code SolidTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("SolidTexture: [Color=%s]", this.color);
	}
}