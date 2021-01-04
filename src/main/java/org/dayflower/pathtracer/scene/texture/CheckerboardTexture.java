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
package org.dayflower.pathtracer.scene.texture;

import static org.macroing.math4j.MathF.cos;
import static org.macroing.math4j.MathF.fractionalPart;
import static org.macroing.math4j.MathF.sin;
import static org.macroing.math4j.MathF.toRadians;

import java.util.Objects;

import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;
import org.macroing.image4j.Color;

/**
 * A {@code CheckerboardTexture} is a {@link Texture} implementation that models a texture with a checkerboard pattern.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class CheckerboardTexture implements Texture {
	/**
	 * The relative offset of the Color 0 parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_COLOR_0 = 2;
	
	/**
	 * The relative offset of the Color 1 parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_COLOR_1 = 3;
	
	/**
	 * The relative offset of the Radians Cos parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_RADIANS_COS = 4;
	
	/**
	 * The relative offset of the Radians Sin parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int RELATIVE_OFFSET_RADIANS_SIN = 5;
	
	/**
	 * The relative offset of the Scale U parameter in the {@code float} array. The value is {@code 6}.
	 */
	public static final int RELATIVE_OFFSET_SCALE_U = 6;
	
	/**
	 * The relative offset of the Scale V parameter in the {@code float} array. The value is {@code 7}.
	 */
	public static final int RELATIVE_OFFSET_SCALE_V = 7;
	
	/**
	 * The size of a {@code CheckerboardTexture} in the {@code float} array. The size is {@code 8}.
	 */
	public static final int SIZE = 8;
	
	/**
	 * The type number associated with a {@code CheckerboardTexture}. The number is {@code 3}.
	 */
	public static final int TYPE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color color0;
	private final Color color1;
	private final float degrees;
	private final float radians;
	private final float scaleU;
	private final float scaleV;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(Color.GRAY)}.
	 */
	public CheckerboardTexture() {
		this(Color.GRAY);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with a given {@link Color}.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(color, 5.0F, 5.0F)}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public CheckerboardTexture(final Color color) {
		this(color, 5.0F, 5.0F);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with two different {@link Color}s.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(color0, color1, 5.0F, 5.0F)}.
	 * <p>
	 * If either {@code color0} or {@code color1} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color0 a {@code Color}
	 * @param color1 a {@code Color}
	 * @throws NullPointerException thrown if, and only if, either {@code color0} or {@code color1} are {@code null}
	 */
	public CheckerboardTexture(final Color color0, final Color color1) {
		this(color0, color1, 5.0F, 5.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with two different {@link Color}s and scale factors in the U- and V-directions.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(color0, color1, scaleU, scaleV, 0.0F)}.
	 * <p>
	 * If either {@code color0} or {@code color1} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color0 a {@code Color}
	 * @param color1 a {@code Color}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @throws NullPointerException thrown if, and only if, either {@code color0} or {@code color1} are {@code null}
	 */
	public CheckerboardTexture(final Color color0, final Color color1, final float scaleU, final float scaleV) {
		this(color0, color1, scaleU, scaleV, 0.0F);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with two different {@link Color}s, scale factors in the U- and V-directions and the angle in degrees to rotate it.
	 * <p>
	 * If either {@code color0} or {@code color1} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color0 a {@code Color}
	 * @param color1 a {@code Color}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @param degrees the angle in degrees to rotate it
	 * @throws NullPointerException thrown if, and only if, either {@code color0} or {@code color1} are {@code null}
	 */
	public CheckerboardTexture(final Color color0, final Color color1, final float scaleU, final float scaleV, final float degrees) {
		this.color0 = Objects.requireNonNull(color0, "color0 == null");
		this.color1 = Objects.requireNonNull(color1, "color1 == null");
		this.scaleU = scaleU;
		this.scaleV = scaleV;
		this.degrees = degrees;
		this.radians = toRadians(this.degrees);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with a given {@link Color} and scale factors in the U- and V-directions.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(color, scaleU, scaleV, 0.0F)}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public CheckerboardTexture(final Color color, final float scaleU, final float scaleV) {
		this(color, scaleU, scaleV, 0.0F);
	}
	
	/**
	 * Constructs a new {@code CheckerboardTexture} instance with a given {@link Color}, scale factors in the U- and V-directions and the angle in degrees to rotate it.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new CheckerboardTexture(color, color, scaleU, scaleV, degrees)}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @param degrees the angle in degrees to rotate it
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public CheckerboardTexture(final Color color, final float scaleU, final float scaleV, final float degrees) {
		this(color, color, scaleU, scaleV, degrees);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code CheckerboardTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code CheckerboardTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		final Color color0 = getColor0();
		final Color color1 = getColor1();
		
		final float u = primitiveIntersection.getShapeIntersection().getTextureCoordinates().x;
		final float v = primitiveIntersection.getShapeIntersection().getTextureCoordinates().y;
		
		final float scaleU = getScaleU();
		final float scaleV = getScaleV();
		
		final float cosAngle = cos(getRadians());
		final float sinAngle = sin(getRadians());
		
		final float textureU = fractionalPart((u * cosAngle - v * sinAngle) * scaleU);
		final float textureV = fractionalPart((v * cosAngle + u * sinAngle) * scaleV);
		
		final boolean isDarkU = textureU > 0.5F;
		final boolean isDarkV = textureV > 0.5F;
		final boolean isDark = isDarkU ^ isDarkV;
		
		final float textureMultiplier = isDark ? 0.8F : 1.2F;
		
		Color color = isDark ? color0 : color1;
		
		if(color0.equals(color1)) {
			color = color.multiply(textureMultiplier);
		}
		
		return color;
	}
	
	/**
	 * Returns one of the two {@link Color}s assigned to this {@code CheckerboardTexture} instance.
	 * 
	 * @return one of the two {@code Color}s assigned to this {@code CheckerboardTexture} instance
	 */
	public Color getColor0() {
		return this.color0;
	}
	
	/**
	 * Returns one of the two {@link Color}s assigned to this {@code CheckerboardTexture} instance.
	 * 
	 * @return one of the two {@code Color}s assigned to this {@code CheckerboardTexture} instance
	 */
	public Color getColor1() {
		return this.color1;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code CheckerboardTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code CheckerboardTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new CheckerboardTexture(%s, %s, %s, %s, %s)", this.color0, this.color1, Float.toString(this.scaleU), Float.toString(this.scaleV), Float.toString(this.degrees));
	}
	
	/**
	 * Compares {@code object} to this {@code CheckerboardTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code CheckerboardTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code CheckerboardTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code CheckerboardTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof CheckerboardTexture)) {
			return false;
		} else if(!Objects.equals(this.color0, CheckerboardTexture.class.cast(object).color0)) {
			return false;
		} else if(!Objects.equals(this.color1, CheckerboardTexture.class.cast(object).color1)) {
			return false;
		} else if(Float.compare(this.degrees, CheckerboardTexture.class.cast(object).degrees) != 0) {
			return false;
		} else if(Float.compare(this.radians, CheckerboardTexture.class.cast(object).radians) != 0) {
			return false;
		} else if(Float.compare(this.scaleU, CheckerboardTexture.class.cast(object).scaleU) != 0) {
			return false;
		} else if(Float.compare(this.scaleV, CheckerboardTexture.class.cast(object).scaleV) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code CheckerboardTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code CheckerboardTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		return !this.color0.isBlack() || !this.color1.isBlack();
	}
	
	/**
	 * Returns the angle in degrees that this {@code CheckerboardTexture} instance should be rotated.
	 * 
	 * @return the angle in degrees that this {@code CheckerboardTexture} instance should be rotated
	 */
	public float getDegrees() {
		return this.degrees;
	}
	
	/**
	 * Returns the angle in radians that this {@code CheckerboardTexture} instance should be rotated.
	 * 
	 * @return the angle in radians that this {@code CheckerboardTexture} instance should be rotated
	 */
	public float getRadians() {
		return this.radians;
	}
	
	/**
	 * Returns the scale factor in the U-direction assigned to this {@code CheckerboardTexture} instance.
	 * 
	 * @return the scale factor in the U-direction assigned to this {@code CheckerboardTexture} instance
	 */
	public float getScaleU() {
		return this.scaleU;
	}
	
	/**
	 * Returns the scale factor in the V-direction assigned to this {@code CheckerboardTexture} instance.
	 * 
	 * @return the scale factor in the V-direction assigned to this {@code CheckerboardTexture} instance
	 */
	public float getScaleV() {
		return this.scaleV;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code CheckerboardTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code CheckerboardTexture} instance
	 */
	@Override
	public float[] toArray() {
		return new float[] {
			getType(),
			getSize(),
			getColor0().pack(),
			getColor1().pack(),
			cos(getRadians()),
			sin(getRadians()),
			getScaleU(),
			getScaleV()
		};
	}
	
	/**
	 * Returns the size of this {@code CheckerboardTexture} instance.
	 * 
	 * @return the size of this {@code CheckerboardTexture} instance
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the type of this {@code CheckerboardTexture} instance.
	 * 
	 * @return the type of this {@code CheckerboardTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code CheckerboardTexture} instance.
	 * 
	 * @return a hash code for this {@code CheckerboardTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.color0, this.color1, Float.valueOf(this.degrees), Float.valueOf(this.radians), Float.valueOf(this.scaleU), Float.valueOf(this.scaleV));
	}
}