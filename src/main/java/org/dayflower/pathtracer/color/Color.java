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
package org.dayflower.pathtracer.color;

import java.util.Objects;

import org.dayflower.pathtracer.math.Math2;

/**
 * The {@code Color} class is used to encapsulate colors in the default {@code sRGB} color space.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Color {
	/**
	 * A {@code Color} denoting the color black.
	 */
	public static final Color BLACK = new Color();
	
	/**
	 * A {@code Color} denoting the color blue.
	 */
	public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F);
	
	/**
	 * A {@code Color} denoting the color cyan.
	 */
	public static final Color CYAN = new Color(0.0F, 1.0F, 1.0F);
	
	/**
	 * A {@code Color} denoting the color gray.
	 */
	public static final Color GRAY = new Color(0.5F, 0.5F, 0.5F);
	
	/**
	 * A {@code Color} denoting the color green.
	 */
	public static final Color GREEN = new Color(0.0F, 1.0F, 0.0F);
	
	/**
	 * A {@code Color} denoting the color magenta.
	 */
	public static final Color MAGENTA = new Color(1.0F, 0.0F, 1.0F);
	
	/**
	 * A {@code Color} denoting the color orange.
	 */
	public static final Color ORANGE = new Color(1.0F, 0.5F, 0.0F);
	
	/**
	 * A {@code Color} denoting the color red.
	 */
	public static final Color RED = new Color(1.0F, 0.0F, 0.0F);
	
	/**
	 * A {@code Color} denoting total transparency.
	 */
	public static final Color TRANSPARENT = new Color(1.0F, 1.0F, 1.0F, 0.0F);
	
	/**
	 * A {@code Color} denoting the color white.
	 */
	public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F);
	
	/**
	 * A {@code Color} denoting the color yellow.
	 */
	public static final Color YELLOW = new Color(1.0F, 1.0F, 0.0F);
	
	/**
	 * The default gamma used in gamma correction.
	 */
	public static final float DEFAULT_GAMMA = 2.2F;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The value of the alpha component (A).
	 */
	public final float a;
	
	/**
	 * The value of the blue component (B).
	 */
	public final float b;
	
	/**
	 * The value of the green component (G).
	 */
	public final float g;
	
	/**
	 * The value of the red component (R).
	 */
	public final float r;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Color} instance denoting the color black.
	 */
	public Color() {
		this(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Constructs a new {@code Color} instance given the specified components.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new Color(r, g, b, 1.0F)}.
	 * 
	 * @param r the value of the red component (R)
	 * @param g the value of the green component (G)
	 * @param b the value of the blue component (B)
	 */
	public Color(final float r, final float g, final float b) {
		this(r, g, b, 1.0F);
	}
	
	/**
	 * Constructs a new {@code Color} instance given the specified components.
	 * 
	 * @param r the value of the red component (R)
	 * @param g the value of the green component (G)
	 * @param b the value of the blue component (B)
	 * @param a the value of the alpha component (A)
	 */
	public Color(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	/**
	 * Constructs a new {@code Color} instance given the specified RGB-value.
	 * 
	 * @param rGB the RGB-value
	 */
	public Color(final int rGB) {
		this(toR(rGB), toG(rGB), toB(rGB));
	}
	
	/**
	 * Constructs a new {@code Color} instance given the specified RGB-values.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code new Color(r, g, b, 255)}.
	 * 
	 * @param r the value of the red component (R)
	 * @param g the value of the green component (G)
	 * @param b the value of the blue component (B)
	 */
	public Color(final int r, final int g, final int b) {
		this(r, g, b, 255);
	}
	
	/**
	 * Constructs a new {@code Color} instance given the specified RGBA-values.
	 * 
	 * @param r the value of the red component (R)
	 * @param g the value of the green component (G)
	 * @param b the value of the blue component (B)
	 * @param a the value of the alpha component (A)
	 */
	public Color(final int r, final int g, final int b, final int a) {
		this(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Color} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Color}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Color} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Color}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Color)) {
			return false;
		} else if(!Math2.equals(this.r, Color.class.cast(object).r)) {
			return false;
		} else if(!Math2.equals(this.g, Color.class.cast(object).g)) {
			return false;
		} else if(!Math2.equals(this.b, Color.class.cast(object).b)) {
			return false;
		} else if(!Math2.equals(this.a, Color.class.cast(object).a)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code Color} denotes the color black, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code Color} denotes the color black, {@code false} otherwise
	 */
	public boolean isBlack() {
		return Math2.equals(this.r, 0.0F) && Math2.equals(this.g, 0.0F) && Math2.equals(this.b, 0.0F);
	}
	
	/**
	 * Adds {@code color} to this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the addition.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to add to this {@code Color} instance
	 * @return a new {@code Color} instance with the result of the addition
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color add(final Color color) {
		return new Color(this.r + color.r, this.g + color.g, this.b + color.b, this.a);
	}
	
	/**
	 * Adds {@code s} to the R-, G- and B-component values of this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the addition.
	 * 
	 * @param s the value to add
	 * @return a new {@code Color} instance with the result of the addition
	 */
	public Color add(final float s) {
		return new Color(this.r + s, this.g + s, this.b + s, this.a);
	}
	
	/**
	 * Adds {@code r}, {@code g} and {@code b} to the R-, G- and B-component values of this {@code Color} instance, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the addition.
	 * 
	 * @param r the value to add to the R component
	 * @param g the value to add to the G component
	 * @param b the value to add to the B component
	 * @return a new {@code Color} instance with the result of the addition
	 */
	public Color add(final float r, final float g, final float b) {
		return new Color(this.r + r, this.g + g, this.b + b, this.a);
	}
	
	/**
	 * Adds {@code r}, {@code g}, {@code b} and {@code a} to the R-, G-, B- and A-component values of this {@code Color} instance, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the addition.
	 * 
	 * @param r the value to add to the R component
	 * @param g the value to add to the G component
	 * @param b the value to add to the B component
	 * @param a the value to add to the A component
	 * @return a new {@code Color} instance with the result of the addition
	 */
	public Color add(final float r, final float g, final float b, final float a) {
		return new Color(this.r + r, this.g + g, this.b + b, this.a + a);
	}
	
	/**
	 * Applies Gamma Correction to this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with Gamma Correction applied.
	 * <p>
	 * Calling this method is equivalent to {@code color.applyGammaCorrection(Color.DEFAULT_GAMMA)}.
	 * 
	 * @return a new {@code Color} instance with Gamma Correction applied
	 */
	public Color applyGammaCorrection() {
		return applyGammaCorrection(DEFAULT_GAMMA);
	}
	
	/**
	 * Applies Gamma Correction to this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with Gamma Correction applied.
	 * 
	 * @param gamma the gamma value to use
	 * @return a new {@code Color} instance with Gamma Correction applied
	 */
	public Color applyGammaCorrection(final float gamma) {
		return pow(1.0F / gamma);
	}
	
	/**
	 * Applies Tone Mapping to this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with Tone Mapping applied.
	 * <p>
	 * Calling this method is equivalent to {@code color.applyToneMapping(ToneMapper.linear())}.
	 * 
	 * @return a new {@code Color} instance with Tone Mapping applied
	 */
	public Color applyToneMapping() {
		return applyToneMapping(ToneMapper.linear());
	}
	
	/**
	 * Applies Tone Mapping to this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with Tone Mapping applied.
	 * <p>
	 * If {@code toneMapper} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param toneMapper the {@link ToneMapper} to use
	 * @return a new {@code Color} instance with Tone Mapping applied
	 * @throws NullPointerException thrown if, and only if, {@code toneMapper} is {@code null}
	 */
	public Color applyToneMapping(final ToneMapper toneMapper) {
		return toneMapper.applyToneMapping(this);
	}
	
	/**
	 * Constrains this {@code Color} instance to be representable.
	 * <p>
	 * Returns a constrained version of this {@code Color} instance.
	 * 
	 * @return a constrained version of this {@code Color} instance
	 */
	public Color constrain() {
		final float w = -Math2.min(0.0F, min());
		
		if(w > 0.0F) {
			return new Color(this.r + w, this.g + w, this.b + w);
		}
		
		return this;
	}
	
	/**
	 * Divides this {@code Color} instance with {@code color}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the division.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to divide this {@code Color} instance with
	 * @return a new {@code Color} instance with the result of the division
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color divide(final Color color) {
		return new Color(this.r / color.r, this.g / color.g, this.b / color.b, this.a);
	}
	
	/**
	 * Divides the R-, G- and B-component values of this {@code Color} instance with {@code s}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the division.
	 * 
	 * @param s the value to divide with
	 * @return a new {@code Color} instance with the result of the division
	 */
	public Color divide(final float s) {
		return new Color(this.r / s, this.g / s, this.b / s, this.a);
	}
	
	/**
	 * Divides the R-, G- and B-component values of this {@code Color} instance with {@code r}, {@code g} and {@code b}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the division.
	 * 
	 * @param r the value to divide the R component with
	 * @param g the value to divide the G component with
	 * @param b the value to divide the B component with
	 * @return a new {@code Color} instance with the result of the division
	 */
	public Color divide(final float r, final float g, final float b) {
		return new Color(this.r / r, this.g / g, this.b / b, this.a);
	}
	
	/**
	 * Divides the R-, G-, B- and A-component values of this {@code Color} instance with {@code r}, {@code g}, {@code b} and {@code a}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the division.
	 * 
	 * @param r the value to divide the R component with
	 * @param g the value to divide the G component with
	 * @param b the value to divide the B component with
	 * @param a the value to divide the A component with
	 * @return a new {@code Color} instance with the result of the division
	 */
	public Color divide(final float r, final float g, final float b, final float a) {
		return new Color(this.r / r, this.g / g, this.b / b, this.a / a);
	}
	
	/**
	 * Returns a new {@code Color} instance with Euler's number {@code e} raised to the power of each RGB-component value.
	 * 
	 * @return a new {@code Color} instance with Euler's number {@code e} raised to the power of each RGB-component value
	 */
	public Color exp() {
		return new Color(Math2.exp(this.r), Math2.exp(this.g), Math2.exp(this.b), this.a);
	}
	
	/**
	 * Makes this {@code Color} instance display compatible.
	 * <p>
	 * Returns a new {@code Color} instance.
	 * <p>
	 * Calling this method is equivalent to {@code color.makeDisplayCompatible(Color.DEFAULT_GAMMA)}.
	 * 
	 * @return a new {@code Color} instance
	 */
	public Color makeDisplayCompatible() {
		return makeDisplayCompatible(DEFAULT_GAMMA);
	}
	
	/**
	 * Makes this {@code Color} instance display compatible.
	 * <p>
	 * Returns a new {@code Color} instance.
	 * <p>
	 * Calling this method is equivalent to {@code color.makeDisplayCompatible(gamma, ToneMapper.linear())}.
	 * 
	 * @param gamma the gamma value to use
	 * @return a new {@code Color} instance
	 */
	public Color makeDisplayCompatible(final float gamma) {
		return makeDisplayCompatible(gamma, ToneMapper.linear());
	}
	
	/**
	 * Makes this {@code Color} instance display compatible.
	 * <p>
	 * Returns a new {@code Color} instance.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * color.applyToneMapping(toneMapper).applyGammaCorrection(gamma).saturate(0.0F, 1.0F).multiply(255.0F)
	 * }
	 * </pre>
	 * If {@code toneMapper} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param gamma the gamma value to use
	 * @param toneMapper the {@link ToneMapper} to use
	 * @return a new {@code Color} instance
	 * @throws NullPointerException thrown if, and only if, {@code toneMapper} is {@code null}
	 */
	public Color makeDisplayCompatible(final float gamma, final ToneMapper toneMapper) {
		return applyToneMapping(toneMapper).applyGammaCorrection(gamma).saturate(0.0F, 1.0F).multiply(255.0F);
	}
	
	/**
	 * Makes this {@code Color} instance display compatible.
	 * <p>
	 * Returns a new {@code Color} instance.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * color.applyToneMapping(toneMapper).saturate(0.0F, 1.0F).multiply(255.0F)
	 * }
	 * </pre>
	 * If {@code toneMapper} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * Note that this method should preferably only be used if {@code toneMapper} takes care of Gamma Correction.
	 * 
	 * @param toneMapper the {@link ToneMapper} to use
	 * @return a new {@code Color} instance
	 * @throws NullPointerException thrown if, and only if, {@code toneMapper} is {@code null}
	 */
	public Color makeDisplayCompatible(final ToneMapper toneMapper) {
		return applyToneMapping(toneMapper).saturate(0.0F, 1.0F).multiply(255.0F);
	}
	
	/**
	 * Multiplies this {@code Color} instance with {@code color}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the multiplication.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to multiply this {@code Color} instance with
	 * @return a new {@code Color} instance with the result of the multiplication
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color multiply(final Color color) {
		return new Color(this.r * color.r, this.g * color.g, this.b * color.b, this.a);
	}
	
	/**
	 * Multiplies the R-, G- and B-component values of this {@code Color} instance with {@code s}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the multiplication.
	 * 
	 * @param s the value to multiply with
	 * @return a new {@code Color} instance with the result of the multiplication
	 */
	public Color multiply(final float s) {
		return new Color(this.r * s, this.g * s, this.b * s, this.a);
	}
	
	/**
	 * Multiplies the R-, G- and B-component values of this {@code Color} instance with {@code r}, {@code g} and {@code b}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the multiplication.
	 * 
	 * @param r the value to multiply with the R component
	 * @param g the value to multiply with the G component
	 * @param b the value to multiply with the B component
	 * @return a new {@code Color} instance with the result of the multiplication
	 */
	public Color multiply(final float r, final float g, final float b) {
		return new Color(this.r * r, this.g * g, this.b * b, this.a);
	}
	
	/**
	 * Multiplies the R-, G-, B- and A-component values of this {@code Color} instance with {@code r}, {@code g}, {@code b} and {@code a}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the multiplication.
	 * 
	 * @param r the value to multiply with the R component
	 * @param g the value to multiply with the G component
	 * @param b the value to multiply with the B component
	 * @param a the value to multiply with the A component
	 * @return a new {@code Color} instance with the result of the multiplication
	 */
	public Color multiply(final float r, final float g, final float b, final float a) {
		return new Color(this.r * r, this.g * g, this.b * b, this.a * a);
	}
	
	/**
	 * Returns a new {@code Color} instance with each RGB-component value negated.
	 * 
	 * @return a new {@code Color} instance with each RGB-component value negated
	 */
	public Color negate() {
		return new Color(-this.r, -this.g, -this.b);
	}
	
	/**
	 * Returns a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of the R-, G- and B-component values of {@code color}, respectively.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color a {@code Color} instance with R-, G- and B-component values that are used as exponents
	 * @return a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of the R-, G- and B-component values of {@code color}, respectively
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color pow(final Color color) {
		return new Color(Math2.pow(this.r, color.r), Math2.pow(this.g, color.g), Math2.pow(this.b, color.b), this.a);
	}
	
	/**
	 * Returns a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of {@code exponent}.
	 * 
	 * @param exponent the exponent to use
	 * @return a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of {@code exponent}
	 */
	public Color pow(final float exponent) {
		return new Color(Math2.pow(this.r, exponent), Math2.pow(this.g, exponent), Math2.pow(this.b, exponent), this.a);
	}
	
	/**
	 * Returns a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of {@code r}, {@code g} and {@code b}, respectively.
	 * 
	 * @param r the exponent of the R-component value
	 * @param g the exponent of the G-component value
	 * @param b the exponent of the B-component value
	 * @return a new {@code Color} instance with the R-, G- and B-component values of this {@code Color} instance raised to the power of {@code r}, {@code g} and {@code b}, respectively
	 */
	public Color pow(final float r, final float g, final float b) {
		return new Color(Math2.pow(this.r, r), Math2.pow(this.g, g), Math2.pow(this.b, b), this.a);
	}
	
	/**
	 * Returns a new {@code Color} instance with the R-, G-, B- and A-component values of this {@code Color} instance raised to the power of {@code r}, {@code g}, {@code b} and {@code a}, respectively.
	 * 
	 * @param r the exponent of the R-component value
	 * @param g the exponent of the G-component value
	 * @param b the exponent of the B-component value
	 * @param a the exponent of the A-component value
	 * @return a new {@code Color} instance with the R-, G-, B- and A-component values of this {@code Color} instance raised to the power of {@code r}, {@code g}, {@code b} and {@code a}, respectively
	 */
	public Color pow(final float r, final float g, final float b, final float a) {
		return new Color(Math2.pow(this.r, r), Math2.pow(this.g, g), Math2.pow(this.b, b), Math2.pow(this.a, a));
	}
	
	/**
	 * Saturates this {@code Color} instance, such that each component value will lie in the range {@code [min, max]}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the saturation.
	 * 
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a new {@code Color} instance with the result of the saturation
	 */
	public Color saturate(final float min, final float max) {
		final float r = Math.max(Math.min(this.r, max), min);
		final float g = Math.max(Math.min(this.g, max), min);
		final float b = Math.max(Math.min(this.b, max), min);
		final float a = Math.max(Math.min(this.a, max), min);
		
		return new Color(r, g, b, a);
	}
	
	/**
	 * Returns a new {@code Color} instance with the square root performed on each RGB-component value.
	 * 
	 * @return a new {@code Color} instance with the square root performed on each RGB-component value
	 */
	public Color sqrt() {
		return new Color(Math2.sqrt(this.r), Math2.sqrt(this.g), Math2.sqrt(this.b), this.a);
	}
	
	/**
	 * Subtracts this {@code Color} instance with {@code color}.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the subtraction.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to subtract from this {@code Color} instance
	 * @return a new {@code Color} instance with the result of the subtraction
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color subtract(final Color color) {
		return new Color(this.r - color.r, this.g - color.g, this.b - color.b, this.a);
	}
	
	/**
	 * Subtracts {@code s} from the R-, G- and B-component values of this {@code Color} instance.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the subtraction.
	 * 
	 * @param s the value to subtract
	 * @return a new {@code Color} instance with the result of the subtraction
	 */
	public Color subtract(final float s) {
		return new Color(this.r - s, this.g - s, this.b - s, this.a);
	}
	
	/**
	 * Subtracts the R-, G- and B-component values of this {@code Color} instance with {@code r}, {@code g} and {@code b}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the subtraction.
	 * 
	 * @param r the value to subtract from the R component
	 * @param g the value to subtract from the G component
	 * @param b the value to subtract from the B component
	 * @return a new {@code Color} instance with the result of the subtraction
	 */
	public Color subtract(final float r, final float g, final float b) {
		return new Color(this.r - r, this.g - g, this.b - b, this.a);
	}
	
	/**
	 * Subtracts the R-, G-, B- and A-component values of this {@code Color} instance with {@code r}, {@code g}, {@code b} and {@code a}, respectively.
	 * <p>
	 * Returns a new {@code Color} instance with the result of the subtraction.
	 * 
	 * @param r the value to subtract from the R component
	 * @param g the value to subtract from the G component
	 * @param b the value to subtract from the B component
	 * @param a the value to subtract from the A component
	 * @return a new {@code Color} instance with the result of the subtraction
	 */
	public Color subtract(final float r, final float g, final float b, final float a) {
		return new Color(this.r - r, this.g - g, this.b - b, this.a - a);
	}
	
	/**
	 * Returns the average value of the RGB-component values of this {@code Color} instance.
	 * 
	 * @return the average value of the RGB-component values of this {@code Color} instance
	 */
	public float average() {
		return (this.r + this.g + this.b) / 3.0F;
	}
	
	/**
	 * Returns the value of the A component.
	 * 
	 * @return the value of the A component
	 */
	public float getA() {
		return this.a;
	}
	
	/**
	 * Returns the value of the B component.
	 * 
	 * @return the value of the B component
	 */
	public float getB() {
		return this.b;
	}
	
	/**
	 * Returns the value of the G component.
	 * 
	 * @return the value of the G component
	 */
	public float getG() {
		return this.g;
	}
	
	/**
	 * Returns the value of the R component.
	 * 
	 * @return the value of the R component
	 */
	public float getR() {
		return this.r;
	}
	
	/**
	 * Returns the luminance of this {@code Color} instance.
	 * 
	 * @return the luminance of this {@code Color} instance
	 */
	public float luminance() {
		return 0.2989F * this.r + 0.5866F * this.g + 0.1145F * this.b;
	}
	
	/**
	 * Returns the maximum RGB-component value of this {@code Color} instance.
	 * 
	 * @return the maximum RGB-component value of this {@code Color} instance
	 */
	public float max() {
		return Math.max(this.r, Math.max(this.g, this.b));
	}
	
	/**
	 * Returns the minimum RGB-component value of this {@code Color} instance.
	 * 
	 * @return the minimum RGB-component value of this {@code Color} instance
	 */
	public float min() {
		return Math.min(this.r, Math.min(this.r, this.b));
	}
	
	/**
	 * Returns a hash code for this {@code Color} instance.
	 * 
	 * @return a hash code for this {@code Color} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.r), Float.valueOf(this.g), Float.valueOf(this.b), Float.valueOf(this.a));
	}
	
	/**
	 * Returns an {@code int} representation of the ARGB-component values of this {@code Color} instance.
	 * 
	 * @return an {@code int} representation of the ARGB-component values of this {@code Color} instance
	 */
	public int toARGB() {
		return (((int)(this.a + 0.5F) & 0xFF) << 24) | (((int)(this.r + 0.5F) & 0xFF) << 16) | (((int)(this.g + 0.5F) & 0xFF) << 8) | ((int)(this.b + 0.5F) & 0xFF);
	}
	
	/**
	 * Returns an {@code int} representation of the RGB-component values of this {@code Color} instance.
	 * 
	 * @return an {@code int} representation of the RGB-component values of this {@code Color} instance
	 */
	public int toRGB() {
		return (((int)(this.r + 0.5F) & 0xFF) << 16) | (((int)(this.g + 0.5F) & 0xFF) << 8) | ((int)(this.b + 0.5F) & 0xFF);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Color} instance.
	 * 
	 * @return a {@code String} representation of this {@code Color} instance
	 */
	@Override
	public String toString() {
		return String.format("Color: [R=%s], [G=%s], [B=%s], [A=%s]", Float.toString(this.r), Float.toString(this.g), Float.toString(this.b), Float.toString(this.a));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the A component value of {@code aRGB}.
	 * 
	 * @param aRGB an {@code int} with the ARGB-component values
	 * @return the A component value of {@code aRGB}
	 */
	public static int toA(final int aRGB) {
		return (aRGB >> 24) & 0xFF;
	}
	
	/**
	 * Returns the B component value of {@code rGB}.
	 * 
	 * @param rGB an {@code int} with the RGB-component values
	 * @return the B component value of {@code rGB}
	 */
	public static int toB(final int rGB) {
		return rGB & 0xFF;
	}
	
	/**
	 * Returns the G component value of {@code rGB}.
	 * 
	 * @param rGB an {@code int} with the RGB-component values
	 * @return the G component value of {@code rGB}
	 */
	public static int toG(final int rGB) {
		return (rGB >> 8) & 0xFF;
	}
	
	/**
	 * Returns the R component value of {@code rGB}.
	 * 
	 * @param rGB an {@code int} with the RGB-component values
	 * @return the R component value of {@code rGB}
	 */
	public static int toR(final int rGB) {
		return (rGB >> 16) & 0xFF;
	}
}