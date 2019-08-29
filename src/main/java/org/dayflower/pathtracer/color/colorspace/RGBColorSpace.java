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
	 * An {@code RGBColorSpace} for the Adobe color space.
	 */
	public static final RGBColorSpace ADOBE = new RGBColorSpace(0.0F, 2.2F, 0.6400F, 0.3300F, 0.2100F, 0.7100F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the Apple color space.
	 */
	public static final RGBColorSpace APPLE = new RGBColorSpace(0.0F, 1.8F, 0.6250F, 0.3400F, 0.2800F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the CIE color space.
	 */
	public static final RGBColorSpace CIE = new RGBColorSpace(0.0F, 2.2F, 0.7350F, 0.2650F, 0.2740F, 0.7170F, 0.1670F, 0.0090F, 1.0F / 3.0F, 1.0F / 3.0F);
	
	/**
	 * An {@code RGBColorSpace} for the EBU color space.
	 */
	public static final RGBColorSpace EBU = new RGBColorSpace(0.018F, 20.0F / 9.0F, 0.6400F, 0.3300F, 0.2900F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the HDTV color space.
	 */
	public static final RGBColorSpace HDTV = new RGBColorSpace(0.018F, 20.0F / 9.0F, 0.6400F, 0.3300F, 0.3000F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the NTSC color space.
	 */
	public static final RGBColorSpace NTSC = new RGBColorSpace(0.018F, 20.0F / 9.0F, 0.6700F, 0.3300F, 0.2100F, 0.7100F, 0.1400F, 0.0800F, 0.31010F, 0.31620F);
	
	/**
	 * An {@code RGBColorSpace} for the SMPTE-240M color space.
	 */
	public static final RGBColorSpace SMPTE_240M = new RGBColorSpace(0.018F, 20.0F / 9.0F, 0.6300F, 0.3400F, 0.3100F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the SMPTE-C color space.
	 */
	public static final RGBColorSpace SMPTE_C = new RGBColorSpace(0.018F, 20.0F / 9.0F, 0.6300F, 0.3400F, 0.3100F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the sRGB color space.
	 */
	public static final RGBColorSpace SRGB = new RGBColorSpace(0.00304F, 2.4F, 0.6400F, 0.3300F, 0.3000F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	
	/**
	 * An {@code RGBColorSpace} for the Wide Gamut color space.
	 */
	public static final RGBColorSpace WIDE_GAMUT = new RGBColorSpace(0.0F, 2.2F, 0.7347F, 0.2653F, 0.1152F, 0.8264F, 0.1566F, 0.0177F, 0.3457F, 0.3585F);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final MatrixRGBToXYZ matrixRGBToXYZ;
	private final MatrixXYZToRGB matrixXYZToRGB;
	private final float breakPoint;
	private final float gamma;
	private final float segmentOffset;
	private final float slope;
	private final float slopeMatch;
	private final int[] gammaCurve;
	private final int[] gammaCurveReciprocal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private RGBColorSpace(final float breakPoint, final float gamma, final float xR, final float yR, final float xG, final float yG, final float xB, final float yB, final float xW, final float yW) {
		this.matrixXYZToRGB = MatrixXYZToRGB.create(xR, yR, xG, yG, xB, yB, xW, yW);
		this.matrixRGBToXYZ = this.matrixXYZToRGB.toMatrixRGBToXYZ(xW, yW);
		this.breakPoint = breakPoint;
		this.gamma = gamma;
		this.slope = doCreateSlope(breakPoint, gamma);
		this.slopeMatch = doCreateSlopeMatch(breakPoint, gamma, this.slope);
		this.segmentOffset = doCreateSegmentOffset(breakPoint, gamma, this.slope, this.slopeMatch);
		this.gammaCurve = doCreateGammaCurve();
		this.gammaCurveReciprocal = doCreateGammaCurveReciprocal();
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
		return this.matrixRGBToXYZ.convertRGBToXYZ(color);
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
		return this.matrixXYZToRGB.convertXYZToRGB(color);
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
	 * Returns the gamma value.
	 * 
	 * @return the gamma value
	 */
	public float getGamma() {
		return this.gamma;
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
	 * Call this method to redo gamma correction on {@code value}.
	 * <p>
	 * Returns {@code value} with gamma correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} with gamma correction
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
	 * Call this method to undo gamma correction on {@code value}.
	 * <p>
	 * Returns {@code value} without gamma correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} without gamma correction
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
	 * Converts the component values of {@code colorRGB} from non-linear to linear representations.
	 * <p>
	 * Returns a linear representation of {@code colorRGB}
	 * 
	 * @param colorRGB the component values to convert
	 * @return a linear representation of {@code colorRGB}
	 */
	public int convertRGBToLinear(final int colorRGB) {
		final int r = this.gammaCurveReciprocal[(colorRGB >> 16) & 0xFF];
		final int g = this.gammaCurveReciprocal[(colorRGB >>  8) & 0xFF];
		final int b = this.gammaCurveReciprocal[(colorRGB >>  0) & 0xFF];
		
		return (r << 16) | (g << 8) | b;
	}
	
	/**
	 * Converts the component values of {@code colorRGB} from linear to non-linear representations.
	 * <p>
	 * Returns a non-linear representation of {@code colorRGB}
	 * 
	 * @param colorRGB the component values to convert
	 * @return a non-linear representation of {@code colorRGB}
	 */
	public int convertRGBToNonLinear(final int colorRGB) {
		final int r = this.gammaCurve[(colorRGB >> 16) & 0xFF];
		final int g = this.gammaCurve[(colorRGB >>  8) & 0xFF];
		final int b = this.gammaCurve[(colorRGB >>  0) & 0xFF];
		
		return (r << 16) | (g << 8) | b;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int[] doCreateGammaCurve() {
		final int[] gammaCurve = new int[256];
		
		for(int i = 0; i < 256; i++) {
			gammaCurve[i] = saturate((int)(redoGammaCorrection(i / 255.0F) * 255.0F + 0.5F));
		}
		
		return gammaCurve;
	}
	
	private int[] doCreateGammaCurveReciprocal() {
		final int[] gammaCurveReciprocal = new int[256];
		
		for(int i = 0; i < 256; i++) {
			gammaCurveReciprocal[i] = saturate((int)(undoGammaCorrection(i / 255.0F) * 255.0F + 0.5F));
		}
		
		return gammaCurveReciprocal;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float doCreateSegmentOffset(final float breakPoint, final float gamma, final float slope, final float slopeMatch) {
		return breakPoint > 0.0F ? slopeMatch * pow(breakPoint, 1.0F / gamma) - slope * breakPoint : 0.0F;
	}
	
	private static float doCreateSlope(final float breakPoint, final float gamma) {
		return breakPoint > 0.0F ? 1.0F / (gamma / pow(breakPoint, 1.0F / gamma - 1.0F) - gamma * breakPoint + breakPoint) : 1.0F;
	}
	
	private static float doCreateSlopeMatch(final float breakPoint, final float gamma, final float slope) {
		return breakPoint > 0.0F ? gamma * slope / pow(breakPoint, 1.0F / gamma - 1.0F) : 1.0F;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unused")
	private static final class MatrixRGBToXYZ {
		public final float elementXB;
		public final float elementXG;
		public final float elementXR;
		public final float elementXW;
		public final float elementYB;
		public final float elementYG;
		public final float elementYR;
		public final float elementYW;
		public final float elementZB;
		public final float elementZG;
		public final float elementZR;
		public final float elementZW;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public MatrixRGBToXYZ(final float elementXR, final float elementYR, final float elementZR, final float elementXG, final float elementYG, final float elementZG, final float elementXB, final float elementYB, final float elementZB, final float elementXW, final float elementYW, final float elementZW) {
			this.elementXR = elementXR;
			this.elementYR = elementYR;
			this.elementZR = elementZR;
			this.elementXG = elementXG;
			this.elementYG = elementYG;
			this.elementZG = elementZG;
			this.elementXB = elementXB;
			this.elementYB = elementYB;
			this.elementZB = elementZB;
			this.elementXW = elementXW;
			this.elementYW = elementYW;
			this.elementZW = elementZW;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public Color convertRGBToXYZ(final Color color) {
			final float r = color.r;
			final float g = color.g;
			final float b = color.b;
			
			final float x = this.elementXR * r + this.elementXG * g + this.elementXB * b;
			final float y = this.elementYR * r + this.elementYG * g + this.elementYB * b;
			final float z = this.elementZR * r + this.elementZG * g + this.elementZB * b;
			
			return new Color(x, y, z);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unused")
	private static final class MatrixXYZToRGB {
		public final float elementBW;
		public final float elementBX;
		public final float elementBY;
		public final float elementBZ;
		public final float elementGW;
		public final float elementGX;
		public final float elementGY;
		public final float elementGZ;
		public final float elementRW;
		public final float elementRX;
		public final float elementRY;
		public final float elementRZ;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public MatrixXYZToRGB(final float elementRX, final float elementRY, final float elementRZ, final float elementGX, final float elementGY, final float elementGZ, final float elementBX, final float elementBY, final float elementBZ, final float elementRW, final float elementGW, final float elementBW) {
			this.elementRX = elementRX;
			this.elementRY = elementRY;
			this.elementRZ = elementRZ;
			this.elementGX = elementGX;
			this.elementGY = elementGY;
			this.elementGZ = elementGZ;
			this.elementBX = elementBX;
			this.elementBY = elementBY;
			this.elementBZ = elementBZ;
			this.elementRW = elementRW;
			this.elementGW = elementGW;
			this.elementBW = elementBW;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public Color convertXYZToRGB(final Color color) {
			final float x = color.r;
			final float y = color.g;
			final float z = color.b;
			
			final float r = this.elementRX * x + this.elementRY * y + this.elementRZ * z;
			final float g = this.elementGX * x + this.elementGY * y + this.elementGZ * z;
			final float b = this.elementBX * x + this.elementBY * y + this.elementBZ * z;
			
			return new Color(r, g, b);
		}
		
		public MatrixRGBToXYZ toMatrixRGBToXYZ(final float xW, final float yW) {
			final float a = this.elementRX * (this.elementGY * this.elementBZ - this.elementBY * this.elementGZ);
			final float b = this.elementRY * (this.elementGX * this.elementBZ - this.elementBX * this.elementGZ);
			final float c = this.elementRZ * (this.elementGX * this.elementBY - this.elementBX * this.elementGY);
			final float s = 1.0F / (a - b + c);
			
			final float elementXR = s * (this.elementGY * this.elementBZ - this.elementGZ * this.elementBY);
			final float elementYR = s * (this.elementGZ * this.elementBX - this.elementGX * this.elementBZ);
			final float elementZR = s * (this.elementGX * this.elementBY - this.elementGY * this.elementBX);
			final float elementXG = s * (this.elementRZ * this.elementBY - this.elementRY * this.elementBZ);
			final float elementYG = s * (this.elementRX * this.elementBZ - this.elementRZ * this.elementBX);
			final float elementZG = s * (this.elementRY * this.elementBX - this.elementRX * this.elementBY);
			final float elementXB = s * (this.elementRY * this.elementGZ - this.elementRZ * this.elementGY);
			final float elementYB = s * (this.elementRZ * this.elementGX - this.elementRX * this.elementGZ);
			final float elementZB = s * (this.elementRX * this.elementGY - this.elementRY * this.elementGX);
			final float elementXW = xW;
			final float elementYW = yW;
			final float elementZW = 1.0F - (xW + yW);
			
			return new MatrixRGBToXYZ(elementXR, elementYR, elementZR, elementXG, elementYG, elementZG, elementXB, elementYB, elementZB, elementXW, elementYW, elementZW);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public static MatrixXYZToRGB create(final float xR, final float yR, final float xG, final float yG, final float xB, final float yB, final float xW, final float yW) {
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
			
			return new MatrixXYZToRGB(rX / rW, rY / rW, rZ / rW, gX / gW, gY / gW, gZ / gW, bX / bW, bY / bW, bZ / bW, rW, gW, bW);
		}
	}
}