/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
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

import static org.macroing.math4j.MathF.remainder;

import java.util.Objects;

import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;
import org.macroing.image4j.Color;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.Vector3F;

/**
 * A {@code BullseyeTexture} is a {@link Texture} implementation that models a texture with a bullseye pattern.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class BullseyeTexture implements Texture {
	/**
	 * The relative offset of the Color A parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_COLOR_A = 2;
	
	/**
	 * The relative offset of the Color B parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_COLOR_B = 3;
	
	/**
	 * The size of a {@code BullseyeTexture} in the {@code float} array. The size is {@code 4}.
	 */
	public static final int SIZE = 4;
	
	/**
	 * The type number associated with a {@code BullseyeTexture}. The number is {@code 2}.
	 */
	public static final int TYPE = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color colorA;
	private final Color colorB;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code BullseyeTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new BullseyeTexture(Color.GRAY)}.
	 */
	public BullseyeTexture() {
		this(Color.GRAY);
	}
	
	/**
	 * Constructs a new {@code BullseyeTexture} instance with a given {@link Color}.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new BullseyeTexture(color, color)}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public BullseyeTexture(final Color color) {
		this(color, color);
	}
	
	/**
	 * Constructs a new {@code BullseyeTexture} instance with two different {@link Color}s.
	 * <p>
	 * If either {@code colorA} or {@code colorB} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param colorA a {@code Color}
	 * @param colorB a {@code Color}
	 * @throws NullPointerException thrown if, and only if, either {@code colorA} or {@code colorB} are {@code null}
	 */
	public BullseyeTexture(final Color colorA, final Color colorB) {
		this.colorA = Objects.requireNonNull(colorA, "colorA == null");
		this.colorB = Objects.requireNonNull(colorB, "colorB == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code BullseyeTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code BullseyeTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		final Color colorA = getColorA();
		final Color colorB = getColorB();
		
		final Vector3F direction = Vector3F.direction(new Point3F(), primitiveIntersection.getShapeIntersection().getSurfaceIntersectionPoint());
		
		final float length = direction.length();
		
		final float value = remainder(length * 0.25F, 1.0F);
		
		return value > 0.5F ? colorA : colorB;
	}
	
	/**
	 * Returns one of the two {@link Color}s assigned to this {@code BullseyeTexture} instance.
	 * 
	 * @return one of the two {@code Color}s assigned to this {@code BullseyeTexture} instance
	 */
	public Color getColorA() {
		return this.colorA;
	}
	
	/**
	 * Returns one of the two {@link Color}s assigned to this {@code BullseyeTexture} instance.
	 * 
	 * @return one of the two {@code Color}s assigned to this {@code BullseyeTexture} instance
	 */
	public Color getColorB() {
		return this.colorB;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code BullseyeTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code BullseyeTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new BullseyeTexture(%s, %s)", this.colorA, this.colorB);
	}
	
	/**
	 * Compares {@code object} to this {@code BullseyeTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code BullseyeTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code BullseyeTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code BullseyeTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof BullseyeTexture)) {
			return false;
		} else if(!Objects.equals(this.colorA, BullseyeTexture.class.cast(object).colorA)) {
			return false;
		} else if(!Objects.equals(this.colorB, BullseyeTexture.class.cast(object).colorB)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code BullseyeTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code BullseyeTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return !this.colorA.isBlack() || !this.colorB.isBlack();
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code BullseyeTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code BullseyeTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			getColorA().pack(),
			getColorB().pack()
		};
	}
	
	/**
	 * Returns the size of this {@code BullseyeTexture} instance.
	 * 
	 * @return the size of this {@code BullseyeTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code BullseyeTexture} instance.
	 * 
	 * @return the type of this {@code BullseyeTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code BullseyeTexture} instance.
	 * 
	 * @return a hash code for this {@code BullseyeTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.colorA, this.colorB);
	}
}