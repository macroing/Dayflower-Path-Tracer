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

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.math.Math2;

/**
 * The {@code Color} class is used to encapsulate colors in the default {@code sRGB} color space.
 * <p>
 * This class is thread-safe and therefore suitable for concurrent use without external synchronization.
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
	
//	TODO: Add Javadocs!
	public Color add(final float s) {
		return new Color(this.r + s, this.g + s, this.b + s, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color add(final float r, final float g, final float b) {
		return new Color(this.r + r, this.g + g, this.b + b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color add(final float r, final float g, final float b, final float a) {
		return new Color(this.r + r, this.g + g, this.b + b, this.a + a);
	}
	
//	TODO: Add Javadocs!
	public Color applyGammaCorrection() {
		return applyGammaCorrection(DEFAULT_GAMMA);
	}
	
//	TODO: Add Javadocs!
	public Color applyGammaCorrection(final float gamma) {
		return pow(1.0F / gamma);
	}
	
//	TODO: Add Javadocs!
	public Color applyToneMapping() {
		return applyToneMapping(color -> color.max() > 1.0F ? color.divide(color.max()) : color);
	}
	
//	TODO: Add Javadocs!
	public Color applyToneMapping(final ToneMapper toneMapper) {
		return toneMapper.applyToneMapping(this);
	}
	
//	TODO: Add Javadocs!
	public Color divide(final Color color) {
		return new Color(this.r / color.r, this.g / color.g, this.b / color.b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color divide(final float s) {
		return new Color(this.r / s, this.g / s, this.b / s, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color divide(final float r, final float g, final float b) {
		return new Color(this.r / r, this.g / g, this.b / b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color divide(final float r, final float g, final float b, final float a) {
		return new Color(this.r / r, this.g / g, this.b / b, this.a / a);
	}
	
//	TODO: Add Javadocs!
	public Color exp() {
		return new Color(Math2.exp(this.r), Math2.exp(this.g), Math2.exp(this.b), this.a);
	}
	
//	TODO: Add Javadocs!
	public Color makeDisplayCompatible() {
		return makeDisplayCompatible(DEFAULT_GAMMA);
	}
	
//	TODO: Add Javadocs!
	public Color makeDisplayCompatible(final float gamma) {
		return makeDisplayCompatible(gamma, color -> color.max() > 1.0F ? color.divide(color.max()) : color);
	}
	
//	TODO: Add Javadocs!
	public Color makeDisplayCompatible(final float gamma, final ToneMapper toneMapper) {
		return applyToneMapping(toneMapper).applyGammaCorrection(gamma).saturate(0.0F, 1.0F).multiply(255.0F);
	}
	
//	TODO: Add Javadocs!
	public Color multiply(final Color color) {
		return new Color(this.r * color.r, this.g * color.g, this.b * color.b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color multiply(final float s) {
		return new Color(this.r * s, this.g * s, this.b * s, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color multiply(final float r, final float g, final float b) {
		return new Color(this.r * r, this.g * g, this.b * b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color multiply(final float r, final float g, final float b, final float a) {
		return new Color(this.r * r, this.g * g, this.b * b, this.a * a);
	}
	
//	TODO: Add Javadocs!
	public Color negate() {
		return new Color(-this.r, -this.g, -this.b);
	}
	
//	TODO: Add Javadocs!
	public Color pow(final Color color) {
		return new Color(Math2.pow(this.r, color.r), Math2.pow(this.g, color.g), Math2.pow(this.b, color.b), this.a);
	}
	
//	TODO: Add Javadocs!
	public Color pow(final float exponent) {
		return new Color(Math2.pow(this.r, exponent), Math2.pow(this.g, exponent), Math2.pow(this.b, exponent), this.a);
	}
	
//	TODO: Add Javadocs!
	public Color pow(final float r, final float g, final float b) {
		return new Color(Math2.pow(this.r, r), Math2.pow(this.g, g), Math2.pow(this.b, b), this.a);
	}
	
//	TODO: Add Javadocs!
	public Color pow(final float r, final float g, final float b, final float a) {
		return new Color(Math2.pow(this.r, r), Math2.pow(this.g, g), Math2.pow(this.b, b), Math2.pow(this.a, a));
	}
	
//	TODO: Add Javadocs!
	public Color saturate(final float min, final float max) {
		final float r = Math.max(Math.min(this.r, max), min);
		final float g = Math.max(Math.min(this.g, max), min);
		final float b = Math.max(Math.min(this.b, max), min);
		final float a = Math.max(Math.min(this.a, max), min);
		
		return new Color(r, g, b, a);
	}
	
//	TODO: Add Javadocs!
	public Color sqrt() {
		return new Color(Math2.sqrt(this.r), Math2.sqrt(this.g), Math2.sqrt(this.b), this.a);
	}
	
//	TODO: Add Javadocs!
	public Color subtract(final Color color) {
		return new Color(this.r - color.r, this.g - color.g, this.b - color.b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color subtract(final float s) {
		return new Color(this.r - s, this.g - s, this.b - s, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color subtract(final float r, final float g, final float b) {
		return new Color(this.r - r, this.g - g, this.b - b, this.a);
	}
	
//	TODO: Add Javadocs!
	public Color subtract(final float r, final float g, final float b, final float a) {
		return new Color(this.r - r, this.g - g, this.b - b, this.a - a);
	}
	
//	TODO: Add Javadocs!
	public float average() {
		return (this.r + this.g + this.b) / 3.0F;
	}
	
//	TODO: Add Javadocs!
	public float getA() {
		return this.a;
	}
	
//	TODO: Add Javadocs!
	public float getB() {
		return this.b;
	}
	
//	TODO: Add Javadocs!
	public float getG() {
		return this.g;
	}
	
//	TODO: Add Javadocs!
	public float getR() {
		return this.r;
	}
	
//	TODO: Add Javadocs!
	public float luminance() {
//		return 0.212671F * this.r + 0.715160F * this.g + 0.072169F * this.b;
		return 0.2989F * this.r + 0.5866F * this.g + 0.1145F * this.b;
	}
	
//	TODO: Add Javadocs!
	public float max() {
		return Math.max(this.r, Math.max(this.g, this.b));
	}
	
//	TODO: Add Javadocs!
	public float min() {
		return Math.min(this.r, Math.min(this.r, this.b));
	}
	
//	TODO: Add Javadocs!
	public int toARGB() {
		return (((int)(this.a) & 0xFF) << 24) | (((int)(this.r) & 0xFF) << 16) | (((int)(this.g) & 0xFF) << 8) | ((int)(this.b) & 0xFF);
	}
	
//	TODO: Add Javadocs!
	public int toRGB() {
		return (((int)(this.r) & 0xFF) << 16) | (((int)(this.g) & 0xFF) << 8) | ((int)(this.b) & 0xFF);
	}
	
//	TODO: Add Javadocs!
	@Override
	public String toString() {
		return String.format("Color: [R=%s, G=%s, B=%s, A=%s]", Float.toString(this.r), Float.toString(this.g), Float.toString(this.b), Float.toString(this.a));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static int toA(final int aRGB) {
		return (aRGB >> 24) & 0xFF;
	}
	
//	TODO: Add Javadocs!
	public static int toB(final int rGB) {
		return rGB & 0xFF;
	}
	
//	TODO: Add Javadocs!
	public static int toG(final int rGB) {
		return (rGB >> 8) & 0xFF;
	}
	
//	TODO: Add Javadocs!
	public static int toR(final int rGB) {
		return (rGB >> 16) & 0xFF;
	}
}