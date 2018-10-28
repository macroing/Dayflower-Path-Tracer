/**
 * Copyright 2015 - 2018 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.color.colorspace;

import static org.dayflower.pathtracer.math.MathF.pow;
import static org.dayflower.pathtracer.math.MathF.saturate;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.ColorSpace;

/**
 * The {@code RGBColorSpace} class is an implementation of {@link ColorSpace} and represents an RGB color space.
 * <p>
 * This class is thread-safe and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class RGBColorSpace extends ColorSpace {
	/**
	 * An {@code RGBColorSpace} for the sRGB color space.
	 */
	public static final RGBColorSpace SRGB = new RGBColorSpace(0.00304F, 2.4F, 0.6400F, 0.3300F, 0.3000F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float breakPoint;
	private final float gamma;
	private final float segmentOffset;
	private final float slope;
	private final float slopeMatch;
	private final float[] matrixRGBToXYZ = new float[12];
	private final float[] matrixXYZToRGB = new float[12];
	private final int[] gammaCurve = new int[256];
	private final int[] gammaCurveReciprocal = new int[256];
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private RGBColorSpace(final float breakPoint, final float gamma, final float xR, final float yR, final float xG, final float yG, final float xB, final float yB, final float xW, final float yW) {
		this.breakPoint = breakPoint;
		this.gamma = gamma;
		this.slope = breakPoint > 0.0F ? 1.0F / (gamma / pow(breakPoint, 1.0F / gamma - 1.0F) - gamma * breakPoint + breakPoint) : 1.0F;
		this.slopeMatch = breakPoint > 0.0F ? gamma * this.slope / pow(breakPoint, 1.0F / gamma - 1.0F) : 1.0F;
		this.segmentOffset = breakPoint > 0.0F ? this.slopeMatch * pow(breakPoint, 1.0F / gamma) - this.slope * breakPoint : 0.0F;
		
		for(int i = 0; i < 256; i++) {
			final float value = i / 255.0F;
			
			this.gammaCurve[i] = saturate((int)(redoGammaCorrection(value) * 255.0F + 0.5F));
			this.gammaCurveReciprocal[i] = saturate((int)(undoGammaCorrection(value) * 255.0F + 0.5F));
		}
		
		final float zR = 1.0F - (xR + yR);
		final float zG = 1.0F - (xG + yG);
		final float zB = 1.0F - (xB + yB);
		final float zW = 1.0F - (xW + yW);
		final float rX = (yG * zB) - (yB * zG);
		final float rY = (xB * zG) - (xG * zB);
		final float rZ = (xG * yB) - (xB * yG);
		final float rW = ((rX * xW) + (rY * yW) + (rZ * zW)) / yW;
		final float gX = (yB * zR) - (yR * zB);
		final float gY = (xR * zB) - (xB * zR);
		final float gZ = (xB * yR) - (xR * yB);
		final float gW = ((gX * xW) + (gY * yW) + (gZ * zW)) / yW;
		final float bX = (yR * zG) - (yG * zR);
		final float bY = (xG * zR) - (xR * zG);
		final float bZ = (xR * yG) - (xG * yR);
		final float bW = ((bX * xW) + (bY * yW) + (bZ * zW)) / yW;
		
		this.matrixRGBToXYZ[ 0] = rX / rW;
		this.matrixRGBToXYZ[ 1] = rY / rW;
		this.matrixRGBToXYZ[ 2] = rZ / rW;
		this.matrixRGBToXYZ[ 3] = gX / gW;
		this.matrixRGBToXYZ[ 4] = gY / gW;
		this.matrixRGBToXYZ[ 5] = gZ / gW;
		this.matrixRGBToXYZ[ 6] = bX / bW;
		this.matrixRGBToXYZ[ 7] = bY / bW;
		this.matrixRGBToXYZ[ 8] = bZ / bW;
		this.matrixRGBToXYZ[ 9] = rW;
		this.matrixRGBToXYZ[10] = gW;
		this.matrixRGBToXYZ[11] = bW;
		
		final float s = 1.0F / (getRX() * (getGY() * getBZ() - getBY() * getGZ()) - getRY() * (getGX() * getBZ() - getBX() * getGZ()) + getRZ() * (getGX() * getBY() - getBX() * getGY()));
		
		this.matrixXYZToRGB[ 0] = s * (getGY() * getBZ() - getGZ() * getBY());
		this.matrixXYZToRGB[ 1] = s * (getGZ() * getBX() - getGX() * getBZ());
		this.matrixXYZToRGB[ 2] = s * (getGX() * getBY() - getGY() * getBX());
		this.matrixXYZToRGB[ 3] = s * (getRZ() * getBY() - getRY() * getBZ());
		this.matrixXYZToRGB[ 4] = s * (getRX() * getBZ() - getRZ() * getBX());
		this.matrixXYZToRGB[ 5] = s * (getRY() * getBX() - getRX() * getBY());
		this.matrixXYZToRGB[ 6] = s * (getRY() * getGZ() - getRZ() * getGY());
		this.matrixXYZToRGB[ 7] = s * (getRZ() * getGX() - getRX() * getGZ());
		this.matrixXYZToRGB[ 8] = s * (getRX() * getGY() - getRY() * getGX());
		this.matrixXYZToRGB[ 9] = xW;
		this.matrixXYZToRGB[10] = yW;
		this.matrixXYZToRGB[11] = zW;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Converts {@code color} from the RGB color space to the XYZ color space.
	 * <p>
	 * Returns a {@link Color} in the XYZ color space.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to convert
	 * @return a {@code Color} in the XYZ color space
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color convertRGBToXYZ(final Color color) {
		final float r = color.r;
		final float g = color.g;
		final float b = color.b;
		
		final float x = getXR() * r + getXG() * g + getXB() * b;
		final float y = getYR() * r + getYG() * g + getYB() * b;
		final float z = getZR() * r + getZG() * g + getZB() * b;
		
		return new Color(x, y, z);
	}
	
	/**
	 * Converts {@code color} from the XYZ color space to the RGB color space.
	 * <p>
	 * Returns a {@link Color} in the RGB color space.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param color the {@code Color} to convert
	 * @return a {@code Color} in the RGB color space
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public Color convertXYZToRGB(final Color color) {
		final float x = color.r;
		final float y = color.g;
		final float z = color.b;
		
		final float r = getRX() * x + getRY() * y + getRZ() * z;
		final float g = getGX() * x + getGY() * y + getGZ() * z;
		final float b = getBX() * x + getBY() * y + getBZ() * z;
		
		return new Color(r, g, b);
	}
	
	/**
	 * Returns the BW component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the BW component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getBW() {
		return this.matrixRGBToXYZ[11];
	}
	
	/**
	 * Returns the BX component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the BX component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getBX() {
		return this.matrixRGBToXYZ[6];
	}
	
	/**
	 * Returns the BY component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the BY component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getBY() {
		return this.matrixRGBToXYZ[7];
	}
	
	/**
	 * Returns the BZ component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the BZ component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getBZ() {
		return this.matrixRGBToXYZ[8];
	}
	
	/**
	 * Returns the break point.
	 * 
	 * @return the break point
	 */
	public float getBreakPoint() {
		return this.breakPoint;
	}
	
	/**
	 * Returns the GW component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the GW component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getGW() {
		return this.matrixRGBToXYZ[10];
	}
	
	/**
	 * Returns the GX component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the GX component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getGX() {
		return this.matrixRGBToXYZ[3];
	}
	
	/**
	 * Returns the GY component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the GY component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getGY() {
		return this.matrixRGBToXYZ[4];
	}
	
	/**
	 * Returns the GZ component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the GZ component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getGZ() {
		return this.matrixRGBToXYZ[5];
	}
	
	/**
	 * Returns the gamma value.
	 * 
	 * @return the gamma value
	 */
	public float getGamma() {
		return this.gamma;
	}
	
	/**
	 * Returns the RW component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the RW component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getRW() {
		return this.matrixRGBToXYZ[9];
	}
	
	/**
	 * Returns the RX component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the RX component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getRX() {
		return this.matrixRGBToXYZ[0];
	}
	
	/**
	 * Returns the RY component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the RY component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getRY() {
		return this.matrixRGBToXYZ[1];
	}
	
	/**
	 * Returns the RZ component of the matrix that converts from RGB color space to XYZ color space.
	 * 
	 * @return the RZ component of the matrix that converts from RGB color space to XYZ color space
	 */
	public float getRZ() {
		return this.matrixRGBToXYZ[2];
	}
	
	/**
	 * Returns the segment offset.
	 * 
	 * @return the segment offset
	 */
	public float getSegmentOffset() {
		return this.segmentOffset;
	}
	
	/**
	 * Returns the slope.
	 * 
	 * @return the slope
	 */
	public float getSlope() {
		return this.slope;
	}
	
	/**
	 * Returns the slope match.
	 * 
	 * @return the slope match
	 */
	public float getSlopeMatch() {
		return this.slopeMatch;
	}
	
	/**
	 * Returns the XB component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the XB component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getXB() {
		return this.matrixXYZToRGB[6];
	}
	
	/**
	 * Returns the XG component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the XG component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getXG() {
		return this.matrixXYZToRGB[3];
	}
	
	/**
	 * Returns the XR component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the XR component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getXR() {
		return this.matrixXYZToRGB[0];
	}
	
	/**
	 * Returns the XW component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the XW component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getXW() {
		return this.matrixXYZToRGB[9];
	}
	
	/**
	 * Returns the YB component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the YB component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getYB() {
		return this.matrixXYZToRGB[7];
	}
	
	/**
	 * Returns the YG component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the YG component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getYG() {
		return this.matrixXYZToRGB[4];
	}
	
	/**
	 * Returns the YR component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the YR component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getYR() {
		return this.matrixXYZToRGB[1];
	}
	
	/**
	 * Returns the YW component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the YW component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getYW() {
		return this.matrixXYZToRGB[10];
	}
	
	/**
	 * Returns the ZB component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the ZB component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getZB() {
		return this.matrixXYZToRGB[8];
	}
	
	/**
	 * Returns the ZG component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the ZG component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getZG() {
		return this.matrixXYZToRGB[5];
	}
	
	/**
	 * Returns the ZR component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the ZR component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getZR() {
		return this.matrixXYZToRGB[2];
	}
	
	/**
	 * Returns the ZW component of the matrix that converts from XYZ color space to RGB color space.
	 * 
	 * @return the ZW component of the matrix that converts from XYZ color space to RGB color space
	 */
	public float getZW() {
		return this.matrixXYZToRGB[11];
	}
	
	/**
	 * Call this method to redo Gamma Correction on {@code value}.
	 * <p>
	 * Returns {@code value} with Gamma Correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} with Gamma Correction
	 */
	@Override
	public float redoGammaCorrection(final float value) {
		if(value <= 0.0F) {
			return 0.0F;
		} else if(value >= 1.0F) {
			return 1.0F;
		} else if(value <= this.breakPoint) {
			return value * this.slope;
		} else {
			return this.slopeMatch * pow(value, 1.0F / this.gamma) - this.segmentOffset;
		}
	}
	
	/**
	 * Call this method to undo Gamma Correction on {@code value}.
	 * <p>
	 * Returns {@code value} without Gamma Correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} without Gamma Correction
	 */
	@Override
	public float undoGammaCorrection(final float value) {
		if(value <= 0.0F) {
			return 0.0F;
		} else if(value >= 1.0F) {
			return 1.0F;
		} else if(value <= this.breakPoint * this.slope) {
			return value / this.slope;
		} else {
			return pow((value + this.segmentOffset) / this.slopeMatch, this.gamma);
		}
	}
	
	/**
	 * Converts the RGB-component values of {@code rGB} from non-linear to linear representations.
	 * <p>
	 * Returns a linear representation of {@code rGB}
	 * 
	 * @param rGB the RGB-component values to convert
	 * @return a linear representation of {@code rGB}
	 */
	public int convertRGBToLinear(final int rGB) {
		final int r = this.gammaCurveReciprocal[(rGB >> 16) & 0xFF];
		final int g = this.gammaCurveReciprocal[(rGB >>  8) & 0xFF];
		final int b = this.gammaCurveReciprocal[(rGB >>  0) & 0xFF];
		
		return (r << 16) | (g << 8) | b;
	}
	
	/**
	 * Converts the RGB-component values of {@code rGB} from linear to non-linear representations.
	 * <p>
	 * Returns a non-linear representation of {@code rGB}
	 * 
	 * @param rGB the RGB-component values to convert
	 * @return a non-linear representation of {@code rGB}
	 */
	public int convertRGBToNonLinear(final int rGB) {
		final int r = this.gammaCurve[(rGB >> 16) & 0xFF];
		final int g = this.gammaCurve[(rGB >>  8) & 0xFF];
		final int b = this.gammaCurve[(rGB >>  0) & 0xFF];
		
		return (r << 16) | (g << 8) | b;
	}
}