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
package org.dayflower.pathtracer.kernel;

import org.macroing.image4j.RGBColorSpace;

/**
 * An abstract extension of the {@code AbstractKernel} class that adds additional features.
 * <p>
 * The features added are the following:
 * <ul>
 * <li>Color conversion methods</li>
 * <li>Image rendering methods</li>
 * <li>Monte Carlo-method based image sampling using a stable Moving Average algorithm</li>
 * </ul>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractImageKernel extends AbstractKernel {
	private static final int FILM_FLAG_CLEAR = 0x0001;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code byte} array with image data.
	 */
	protected byte[] imageDataByte;
	
	/**
	 * The break point for the color space.
	 */
	protected float colorSpaceBreakPoint;
	
	/**
	 * The gamma for the color space.
	 */
	protected float colorSpaceGamma;
	
	/**
	 * The segment offset for the color space.
	 */
	protected float colorSpaceSegmentOffset;
	
	/**
	 * The slope for the color space.
	 */
	protected float colorSpaceSlope;
	
	/**
	 * The slope match for the color space.
	 */
	protected float colorSpaceSlopeMatch;
	
	/**
	 * A {@code float} array with film data.
	 */
	protected float[] filmData;
	
	/**
	 * A {@code float} array with image data.
	 */
	protected float[] imageDataFloat;
	
	/**
	 * The image flags.
	 */
	protected int filmFlags;
	
	/**
	 * An {@code int} array with film data samples.
	 */
	protected int[] filmDataSamples;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractImageKernel} instance.
	 */
	protected AbstractImageKernel() {
//		Initialize the color space variables:
		this.colorSpaceBreakPoint = RGBColorSpace.SRGB.getBreakPoint();
		this.colorSpaceGamma = RGBColorSpace.SRGB.getGamma();
		this.colorSpaceSegmentOffset = RGBColorSpace.SRGB.getSegmentOffset();
		this.colorSpaceSlope = RGBColorSpace.SRGB.getSlope();
		this.colorSpaceSlopeMatch = RGBColorSpace.SRGB.getSlopeMatch();
		
//		Initialize the film variables:
		this.filmData = new float[1];
		this.filmDataSamples = new int[1];
		this.filmFlags = 0;
		
//		Initialize the image variables:
		this.imageDataByte = new byte[1];
		this.imageDataFloat = new float[1];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Call this method to hint to this {@code AbstractImageKernel} instance that it should clear the image before rendering to it in the next render pass.
	 */
	public final void clear() {
		this.filmFlags |= FILM_FLAG_CLEAR;
	}
	
	/**
	 * Call this method to clear the film flags.
	 */
	public final void clearFilmFlags() {
		this.filmFlags = 0;
	}
	
	/**
	 * Updates all necessary variables in this {@code AbstractImageKernel} instance.
	 * <p>
	 * This method should not be called by {@code run()}-reachable code, because it calls {@link AbstractKernel#update(int, int)}.
	 * <p>
	 * If {@code imageDataByte} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code resolutionX * resolutionY * 4 != imageDataByte.length}, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis
	 * @param resolutionY the resolution along the Y-axis
	 * @param imageDataByte the {@code byte} array to render to
	 * @throws IllegalArgumentException thrown if, and only if, {@code resolutionX * resolutionY != imageDataByte.length}
	 * @throws NullPointerException thrown if, and only if, {@code imageDataByte} is {@code null}
	 */
	public final void update(final int resolutionX, final int resolutionY, final byte[] imageDataByte) {
		if(resolutionX * resolutionY * 4 != imageDataByte.length) {
			throw new IllegalArgumentException(String.format("resolutionX * resolutionY * 4 != imageDataByte.length: resolutionX=%s, resolutionY=%s, imageDataByte.length=%s", Integer.toString(resolutionX), Integer.toString(resolutionY), Integer.toString(imageDataByte.length)));
		}
		
		update(resolutionX, resolutionY);
		
//		Initialize the film variables:
		this.filmData = new float[resolutionX * resolutionY * 3];
		this.filmDataSamples = new int[resolutionX * resolutionY];
		this.filmFlags = 0;
		
//		Initialize the image variables:
		this.imageDataByte = imageDataByte;
		this.imageDataFloat = new float[resolutionX * resolutionY * 3];
		
		put(this.filmData);
		put(this.filmDataSamples);
		put(this.imageDataByte);
		put(this.imageDataFloat);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the B-component value of {@code rGB} as a {@code float}.
	 * <p>
	 * The returned value will be between {@code 0.0F} and {@code 1.0F}.
	 * 
	 * @param rGB an {@code int} with an RGB-color
	 * @return the B-component value of {@code rGB} as a {@code float}
	 */
	@SuppressWarnings("static-method")
	protected final float colorGetB(final int rGB) {
		return (rGB & 0xFF) / 255.0F;
	}
	
	/**
	 * Returns the G-component value of {@code rGB} as a {@code float}.
	 * <p>
	 * The returned value will be between {@code 0.0F} and {@code 1.0F}.
	 * 
	 * @param rGB an {@code int} with an RGB-color
	 * @return the G-component value of {@code rGB} as a {@code float}
	 */
	@SuppressWarnings("static-method")
	protected final float colorGetG(final int rGB) {
		return ((rGB >> 8) & 0xFF) / 255.0F;
	}
	
	/**
	 * Returns the R-component value of {@code rGB} as a {@code float}.
	 * <p>
	 * The returned value will be between {@code 0.0F} and {@code 1.0F}.
	 * 
	 * @param rGB an {@code int} with an RGB-color
	 * @return the R-component value of {@code rGB} as a {@code float}
	 */
	@SuppressWarnings("static-method")
	protected final float colorGetR(final int rGB) {
		return ((rGB >> 16) & 0xFF) / 255.0F;
	}
	
	/**
	 * Returns the RGB-component value of B from the current pixel of the image.
	 * 
	 * @return the RGB-component value of B from the current pixel of the image
	 */
	protected final float imageGetB() {
		return this.imageDataFloat[getGlobalId() * 3 + 2];
	}
	
	/**
	 * Returns the RGB-component value of G from the current pixel of the image.
	 * 
	 * @return the RGB-component value of G from the current pixel of the image
	 */
	protected final float imageGetG() {
		return this.imageDataFloat[getGlobalId() * 3 + 1];
	}
	
	/**
	 * Returns the RGB-component value of R from the current pixel of the image.
	 * 
	 * @return the RGB-component value of R from the current pixel of the image
	 */
	protected final float imageGetR() {
		return this.imageDataFloat[getGlobalId() * 3 + 0];
	}
	
	/**
	 * Returns an RGB-color from {@code r}, {@code g} and {@code b} as an {@code int}.
	 * <p>
	 * This method assumes {@code r}, {@code g} and {@code b} are all between {@code 0.0F} and {@code 1.0F}.
	 * 
	 * @param r the R-component value, between {@code 0.0F} and {@code 1.0F}
	 * @param g the G-component value, between {@code 0.0F} and {@code 1.0F}
	 * @param b the B-component value, between {@code 0.0F} and {@code 1.0F}
	 * @return an RGB-color from {@code r}, {@code g} and {@code b} as an {@code int}
	 */
	@SuppressWarnings("static-method")
	protected final int colorGetRGB(final float r, final float g, final float b) {
		return (((int)(r * 255.0F + 0.5F) & 0xFF) << 16) | (((int)(g * 255.0F + 0.5F) & 0xFF) << 8) | (((int)(b * 255.0F + 0.5F) & 0xFF));
	}
	
	/**
	 * Returns the resolution along the X-axis of the film.
	 * 
	 * @return the resolution along the X-axis of the film
	 */
	protected final int filmGetResolutionX() {
		return super.resolutionX;
	}
	
	/**
	 * Returns the resolution along the Y-axis of the film.
	 * 
	 * @return the resolution along the Y-axis of the film
	 */
	protected final int filmGetResolutionY() {
		return super.resolutionY;
	}
	
	/**
	 * Returns the X-coordinate of the film.
	 * 
	 * @return the X-coordinate of the film
	 */
	protected final int filmGetX() {
		return getGlobalId() % super.resolutionX;
	}
	
	/**
	 * Returns the Y-coordinate of the film.
	 * 
	 * @return the Y-coordinate of the film
	 */
	protected final int filmGetY() {
		return getGlobalId() / super.resolutionX;
	}
	
	/**
	 * Returns the resolution along the X-axis of the image.
	 * 
	 * @return the resolution along the X-axis of the image
	 */
	protected final int imageGetResolutionX() {
		return super.resolutionX;
	}
	
	/**
	 * Returns the resolution along the Y-axis of the image.
	 * 
	 * @return the resolution along the Y-axis of the image
	 */
	protected final int imageGetResolutionY() {
		return super.resolutionY;
	}
	
	/**
	 * Returns the X-coordinate of the image.
	 * 
	 * @return the X-coordinate of the image
	 */
	protected final int imageGetX() {
		return getGlobalId() % super.resolutionX;
	}
	
	/**
	 * Returns the Y-coordinate of the image.
	 * 
	 * @return the Y-coordinate of the image
	 */
	protected final int imageGetY() {
		return getGlobalId() / super.resolutionX;
	}
	
	/**
	 * Adds the sample RGB-component values {@code r}, {@code g} and {@code b} to the current pixel of the film.
	 * <p>
	 * This method is useful for Monte Carlo-method based rendering. It should be avoided if you already know the final RGB-color.
	 * <p>
	 * The current moving average algorithm used by this method is stable enough, that adding the same RGB-color repeatedly won't cause major precision loss.
	 * 
	 * @param r the value of the RGB-component R
	 * @param g the value of the RGB-component G
	 * @param b the value of the RGB-component B
	 */
	protected final void filmAddColor(final float r, final float g, final float b) {
		final int filmDataOffset = getGlobalId() * 3;
		final int filmDataSamplesOffset = getGlobalId();
		
		if((this.filmFlags & FILM_FLAG_CLEAR) != 0) {
			this.filmData[filmDataOffset + 0] = r;
			this.filmData[filmDataOffset + 1] = g;
			this.filmData[filmDataOffset + 2] = b;
			this.filmDataSamples[filmDataSamplesOffset] = 1;
		} else {
			final int oldFilmDataSample = this.filmDataSamples[filmDataSamplesOffset];
			final int newFilmDataSample = oldFilmDataSample + 1;
			
			final float oldAverageR = this.filmData[filmDataOffset + 0];
			final float oldAverageG = this.filmData[filmDataOffset + 1];
			final float oldAverageB = this.filmData[filmDataOffset + 2];
			
			final float newAverageR = oldAverageR + ((r - oldAverageR) / newFilmDataSample);
			final float newAverageG = oldAverageG + ((g - oldAverageG) / newFilmDataSample);
			final float newAverageB = oldAverageB + ((b - oldAverageB) / newFilmDataSample);
			
			this.filmData[filmDataOffset + 0] = newAverageR;
			this.filmData[filmDataOffset + 1] = newAverageG;
			this.filmData[filmDataOffset + 2] = newAverageB;
			this.filmDataSamples[filmDataSamplesOffset] = newFilmDataSample;
		}
	}
	
	/**
	 * Sets the RGB-component values {@code r}, {@code g} and {@code b} for the current pixel of the film.
	 * <p>
	 * This method is useful if you already know the final RGB-color. It should be avoided if you're using Monte Carlo-method based rendering.
	 * 
	 * @param r the value of the RGB-component R
	 * @param g the value of the RGB-component G
	 * @param b the value of the RGB-component B
	 */
	protected final void filmSetColor(final float r, final float g, final float b) {
		final int filmDataOffset = getGlobalId() * 3;
		final int filmDataSamplesOffset = getGlobalId();
		
		this.filmData[filmDataOffset + 0] = r;
		this.filmData[filmDataOffset + 1] = g;
		this.filmData[filmDataOffset + 2] = b;
		this.filmDataSamples[filmDataSamplesOffset] = 1;
	}
	
	/**
	 * Adds the RGB-component values {@code r}, {@code g} and {@code b} to the current pixel of the image.
	 * 
	 * @param r the value of the RGB-component R
	 * @param g the value of the RGB-component G
	 * @param b the value of the RGB-component B
	 */
	protected final void imageAddColor(final float r, final float g, final float b) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = oldR + r;
		final float newG = oldG + g;
		final float newB = oldB + b;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * The image processing stage begins.
	 * <p>
	 * This method copies the current film data into an image data buffer. The image data buffer can be manipulated by other image processing methods.
	 * <p>
	 * When the image processing is done, {@link #imageEnd()} has to be called in order to end the image processing stage.
	 */
	protected final void imageBegin() {
		final int filmDataOffset = getGlobalId() * 3;
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float r = this.filmData[filmDataOffset + 0];
		final float g = this.filmData[filmDataOffset + 1];
		final float b = this.filmData[filmDataOffset + 2];
		
		this.imageDataFloat[imageDataFloatOffset + 0] = r;
		this.imageDataFloat[imageDataFloatOffset + 1] = g;
		this.imageDataFloat[imageDataFloatOffset + 2] = b;
	}
	
	/**
	 * The image processing stage ends.
	 * <p>
	 * This method copies the current image data to its final destination so it can be displayed.
	 */
	protected final void imageEnd() {
//		Retrieve the current color:
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float r = this.imageDataFloat[imageDataFloatOffset + 0];
		final float g = this.imageDataFloat[imageDataFloatOffset + 1];
		final float b = this.imageDataFloat[imageDataFloatOffset + 2];
		
//		Scale and clamp the gamma corrected color so it can be displayed:
		final int imageDataR = (int)(max(min(r * 255.0F + 0.5F, 255.0F), 0.0F));
		final int imageDataG = (int)(max(min(g * 255.0F + 0.5F, 255.0F), 0.0F));
		final int imageDataB = (int)(max(min(b * 255.0F + 0.5F, 255.0F), 0.0F));
		final int imageDataA = 255;
		
//		Update the image data with the new color:
		final int imageDataByteOffset = getGlobalId() * 4;
		
		this.imageDataByte[imageDataByteOffset + 0] = (byte)(imageDataB);
		this.imageDataByte[imageDataByteOffset + 1] = (byte)(imageDataG);
		this.imageDataByte[imageDataByteOffset + 2] = (byte)(imageDataR);
		this.imageDataByte[imageDataByteOffset + 3] = (byte)(imageDataA);
	}
	
	/**
	 * Redoes the gamma correction for the current pixel of the image.
	 * <p>
	 * This method assumes the RGB-color for the current pixel is linear.
	 * <p>
	 * The gamma correction is performed in the color space sRGB.
	 */
	protected final void imageRedoGammaCorrection() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = oldR <= 0.0F ? 0.0F : oldR >= 1.0F ? 1.0F : oldR <= this.colorSpaceBreakPoint ? oldR * this.colorSpaceSlope : this.colorSpaceSlopeMatch * pow(oldR, 1.0F / this.colorSpaceGamma) - this.colorSpaceSegmentOffset;
		final float newG = oldG <= 0.0F ? 0.0F : oldG >= 1.0F ? 1.0F : oldG <= this.colorSpaceBreakPoint ? oldG * this.colorSpaceSlope : this.colorSpaceSlopeMatch * pow(oldG, 1.0F / this.colorSpaceGamma) - this.colorSpaceSegmentOffset;
		final float newB = oldB <= 0.0F ? 0.0F : oldB >= 1.0F ? 1.0F : oldB <= this.colorSpaceBreakPoint ? oldB * this.colorSpaceSlope : this.colorSpaceSlopeMatch * pow(oldB, 1.0F / this.colorSpaceGamma) - this.colorSpaceSegmentOffset;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-component values {@code r}, {@code g} and {@code b} for the current pixel of the image.
	 * 
	 * @param r the value of the RGB-component R
	 * @param g the value of the RGB-component G
	 * @param b the value of the RGB-component B
	 */
	protected final void imageSetColor(final float r, final float g, final float b) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = r;
		this.imageDataFloat[imageDataFloatOffset + 1] = g;
		this.imageDataFloat[imageDataFloatOffset + 2] = b;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses a general filmic curve algorithm.
	 * 
	 * @param exposure the exposure to apply to the image
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @param c a {@code float} value
	 * @param d a {@code float} value
	 * @param e a {@code float} value
	 * @param subtract a value to subtract from each R-, G- and B-component before performing the tone mapping operation
	 * @param minimum the minimum value allowed for each R-, G- and B-component
	 */
	protected final void imageSetFilmicCurve(final float exposure, final float a, final float b, final float c, final float d, final float e, final float subtract, final float minimum) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float oldRModified = max(oldR * exposure - subtract, minimum);
		final float oldGModified = max(oldG * exposure - subtract, minimum);
		final float oldBModified = max(oldB * exposure - subtract, minimum);
		
		final float newR = saturate((oldRModified * (a * oldRModified + b)) / (oldRModified * (c * oldRModified + d) + e), 0.0F, 1.0F);
		final float newG = saturate((oldGModified * (a * oldGModified + b)) / (oldGModified * (c * oldGModified + d) + e), 0.0F, 1.0F);
		final float newB = saturate((oldBModified * (a * oldBModified + b)) / (oldBModified * (c * oldBModified + d) + e), 0.0F, 1.0F);
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses a modified version of the ACES filmic curve algorithm.
	 * <p>
	 * To use the original ACES filmic curve, set {@code exposure} to {@code 0.6F}.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	protected final void imageSetFilmicCurveACESModified(final float exposure) {
		imageSetFilmicCurve(exposure, 2.51F, 0.03F, 2.43F, 0.59F, 0.14F, 0.0F, Float.MIN_VALUE);
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses a version of the filmic curve algorithm that also performs gamma correction with a gamma of 2.2.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	protected final void imageSetFilmicCurveGammaCorrection22(final float exposure) {
		imageSetFilmicCurve(exposure, 6.2F, 0.5F, 6.2F, 1.7F, 0.06F, 0.004F, 0.0F);
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the average of the current RGB-component values.
	 */
	protected final void imageSetGrayscaleAverage() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float average = (oldR + oldG + oldB) / 3.0F;
		
		final float newR = average;
		final float newG = average;
		final float newB = average;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the current value of the RGB-component B.
	 */
	protected final void imageSetGrayscaleB() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = oldB;
		final float newG = oldB;
		final float newB = oldB;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the current value of the RGB-component G.
	 */
	protected final void imageSetGrayscaleG() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		
		final float newR = oldG;
		final float newG = oldG;
		final float newB = oldG;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the lightness of the current RGB-component values.
	 */
	protected final void imageSetGrayscaleLightness() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float max = max(oldR, oldG, oldB);
		final float min = min(oldR, oldG, oldB);
		
		final float grayscale = (max + min) / 2.0F;
		
		final float newR = grayscale;
		final float newG = grayscale;
		final float newB = grayscale;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the luminance of the current RGB-component values.
	 */
	protected final void imageSetGrayscaleLuminance() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float luminance = 0.212671F * oldR + 0.715160F * oldG + 0.072169F * oldB;
		
		final float newR = luminance;
		final float newG = luminance;
		final float newB = luminance;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to grayscale.
	 * <p>
	 * The grayscale is based on the current value of the RGB-component R.
	 */
	protected final void imageSetGrayscaleR() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		
		final float newR = oldR;
		final float newG = oldR;
		final float newB = oldR;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the inverse of the current RGB-color.
	 */
	protected final void imageSetInverse() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = 1.0F - oldR;
		final float newG = 1.0F - oldG;
		final float newB = 1.0F - oldB;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses the Reinhard algorithm.
	 * 
	 * @param exposure the exposure to use
	 */
	protected final void imageSetReinhard(final float exposure) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float oldRModified = oldR * exposure;
		final float oldGModified = oldG * exposure;
		final float oldBModified = oldB * exposure;
		
		final float newR = oldRModified / (1.0F + oldRModified);
		final float newG = oldGModified / (1.0F + oldGModified);
		final float newB = oldBModified / (1.0F + oldBModified);
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses a modified version of the Reinhard algorithm.
	 * 
	 * @param exposure the exposure to use
	 */
	protected final void imageSetReinhardModified1(final float exposure) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = 1.0F - exp(-oldR * exposure);
		final float newG = 1.0F - exp(-oldG * exposure);
		final float newB = 1.0F - exp(-oldB * exposure);
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the tone mapped version of the current RGB-color.
	 * <p>
	 * This method uses a modified version of the Reinhard algorithm.
	 * 
	 * @param exposure the exposure to use
	 */
	protected final void imageSetReinhardModified2(final float exposure) {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float lWhite = 4.0F;
		final float lWhite2 = lWhite * lWhite;
		final float lWhite2Reciprocal = 1.0F / lWhite2;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float oldRModified = oldR * exposure;
		final float oldGModified = oldG * exposure;
		final float oldBModified = oldB * exposure;
		
		final float newR = oldRModified * (1.0F + oldRModified * lWhite2Reciprocal) / (1.0F + oldRModified);
		final float newG = oldGModified * (1.0F + oldGModified * lWhite2Reciprocal) / (1.0F + oldGModified);
		final float newB = oldBModified * (1.0F + oldBModified * lWhite2Reciprocal) / (1.0F + oldBModified);
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Sets the RGB-color of the current pixel to the sepia tone of the current RGB-color.
	 */
	protected final void imageSetSepia() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = oldR * 0.393F + oldG * 0.769F + oldB * 0.189F;
		final float newG = oldR * 0.349F + oldG * 0.686F + oldB * 0.168F;
		final float newB = oldR * 0.272F + oldG * 0.534F + oldB * 0.131F;
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
	
	/**
	 * Undoes the gamma correction for the current pixel of the image.
	 * <p>
	 * This method assumes the RGB-color for the current pixel is non-linear.
	 * <p>
	 * The gamma correction is performed in the color space sRGB.
	 */
	protected final void imageUndoGammaCorrection() {
		final int imageDataFloatOffset = getGlobalId() * 3;
		
		final float oldR = this.imageDataFloat[imageDataFloatOffset + 0];
		final float oldG = this.imageDataFloat[imageDataFloatOffset + 1];
		final float oldB = this.imageDataFloat[imageDataFloatOffset + 2];
		
		final float newR = oldR <= 0.0F ? 0.0F : oldR >= 1.0F ? 1.0F : oldR <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldR / this.colorSpaceSlope : pow((oldR + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma);
		final float newG = oldG <= 0.0F ? 0.0F : oldG >= 1.0F ? 1.0F : oldG <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldG / this.colorSpaceSlope : pow((oldG + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma);
		final float newB = oldB <= 0.0F ? 0.0F : oldB >= 1.0F ? 1.0F : oldB <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldB / this.colorSpaceSlope : pow((oldB + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma);
		
		this.imageDataFloat[imageDataFloatOffset + 0] = newR;
		this.imageDataFloat[imageDataFloatOffset + 1] = newG;
		this.imageDataFloat[imageDataFloatOffset + 2] = newB;
	}
}