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
package org.dayflower.pathtracer.scene;

import static org.dayflower.pathtracer.math.Math2.PI;
import static org.dayflower.pathtracer.math.Math2.acos;
import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.exp;
import static org.dayflower.pathtracer.math.Math2.max;
import static org.dayflower.pathtracer.math.Math2.saturate;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.tan;

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.color.ChromaticSpectralCurve;
import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.RGBColorSpace;

//TODO: Add Javadocs!
public final class Sky {
//	private static final float[] K_G_AMPLITUDES = {0.0F, 3.0F, 0.210F, 0.0F};
//	private static final float[] K_G_WAVELENGTHS = {759.0F, 760.0F, 770.0F, 771.0F};
//	private static final float[] K_O_AMPLITUDES = {10.0F, 4.8F, 2.7F, 1.35F, 0.8F, 0.380F, 0.160F, 0.075F, 0.04F, 0.019F, 0.007F, 0.0F, 0.003F, 0.003F, 0.004F, 0.006F, 0.008F, 0.009F, 0.012F, 0.014F, 0.017F, 0.021F, 0.025F, 0.03F, 0.035F, 0.04F, 0.045F, 0.048F, 0.057F, 0.063F, 0.07F, 0.075F, 0.08F, 0.085F, 0.095F, 0.103F, 0.110F, 0.12F, 0.122F, 0.12F, 0.118F, 0.115F, 0.12F, 0.125F, 0.130F, 0.12F, 0.105F, 0.09F, 0.079F, 0.067F, 0.057F, 0.048F, 0.036F, 0.028F, 0.023F, 0.018F, 0.014F, 0.011F, 0.010F, 0.009F, 0.007F, 0.004F, 0.0F, 0.0F};
//	private static final float[] K_O_WAVELENGTHS = {300.0F, 305.0F, 310.0F, 315.0F, 320.0F, 325.0F, 330.0F, 335.0F, 340.0F, 345.0F, 350.0F, 355.0F, 445.0F, 450.0F, 455.0F, 460.0F, 465.0F, 470.0F, 475.0F, 480.0F, 485.0F, 490.0F, 495.0F, 500.0F, 505.0F, 510.0F, 515.0F, 520.0F, 525.0F, 530.0F, 535.0F, 540.0F, 545.0F, 550.0F, 555.0F, 560.0F, 565.0F, 570.0F, 575.0F, 580.0F, 585.0F, 590.0F, 595.0F, 600.0F, 605.0F, 610.0F, 620.0F, 630.0F, 640.0F, 650.0F, 660.0F, 670.0F, 680.0F, 690.0F, 700.0F, 710.0F, 720.0F, 730.0F, 740.0F, 750.0F, 760.0F, 770.0F, 780.0F, 790.0F};
//	private static final float[] K_WA_AMPLITUDES = {0.0F, 0.160e-1F, 0.240e-1F, 0.125e-1F, 0.100e+1F, 0.870F, 0.610e-1F, 0.100e-2F, 0.100e-4F, 0.100e-4F, 0.600e-3F, 0.175e-1F, 0.360e-1F};
//	private static final float[] K_WA_WAVELENGTHS = {689.0F, 690.0F, 700.0F, 710.0F, 720.0F, 730.0F, 740.0F, 750.0F, 760.0F, 770.0F, 780.0F, 790.0F, 800.0F};
//	private static final float[] SOL_AMPLITUDES = {165.5F, 162.3F, 211.2F, 258.8F, 258.2F, 242.3F, 267.6F, 296.6F, 305.4F, 300.6F, 306.6F, 288.3F, 287.1F, 278.2F, 271.0F, 272.3F, 263.6F, 255.0F, 250.6F, 253.1F, 253.5F, 251.3F, 246.3F, 241.7F, 236.8F, 232.1F, 228.2F, 223.4F, 219.7F, 215.3F, 211.0F, 207.3F, 202.4F, 198.7F, 194.3F, 190.7F, 186.3F, 182.6F};
//	private static final SpectralCurve K_G_SPECTRAL_CURVE = new IrregularSpectralCurve(K_G_AMPLITUDES, K_G_WAVELENGTHS);
//	private static final SpectralCurve K_O_SPECTRAL_CURVE = new IrregularSpectralCurve(K_O_AMPLITUDES, K_O_WAVELENGTHS);
//	private static final SpectralCurve K_WA_SPECTRAL_CURVE = new IrregularSpectralCurve(K_WA_AMPLITUDES, K_WA_WAVELENGTHS);
//	private static final SpectralCurve SOL_SPECTRAL_CURVE = new RegularSpectralCurve(380.0F, 750.0F, SOL_AMPLITUDES);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	private final Color color;
//	private final float jacobian;
	private final float theta;
	private final float turbidity = 2.0F;
	private final float zenithRelativeLuminance;
	private final float zenithX;
	private final float zenithY;
	private final float[] colHistogram;
	private final float[] perezRelativeLuminance = new float[5];
	private final float[] perezX = new float[5];
	private final float[] perezY = new float[5];
	private final float[][] imageHistogram;
//	private final int samples = 4;
	private final OrthoNormalBasis orthoNormalBasis = new OrthoNormalBasis(Vector3.y());
//	private final SpectralCurve radiance;
	private final Vector3 sunDirection;
	private final Vector3 sunDirectionWorld = new Vector3(1.0F, 1.0F, 1.0F).normalize();//Vector3.direction(new Point3(), new Point3(1000.0F, 3000.0F, 1000.0F)).normalize();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Sky() {
		this.sunDirection = this.sunDirectionWorld.untransform(this.orthoNormalBasis).normalize();
		this.theta = acos(saturate(this.sunDirection.z, -1.0F, 1.0F));
		
//		if(this.sunDirection.z > 0.0F) {
//			this.radiance = doCalculateAttenuatedSunlight(this.theta, this.turbidity);
//			this.color = RGBColorSpace.SRGB.convertXYZToRGB(this.radiance.toXYZ().multiply(1e-4F));
//		} else {
//			this.radiance = new ConstantSpectralCurve(0.0F);
//			this.color = Color.BLACK;
//		}
		
		final float theta = this.theta;
		final float theta2 = theta * theta;
		final float theta3 = theta * theta * theta;
		final float turbidity = this.turbidity;
		final float turbidity2 = turbidity * turbidity;
		final float chi = (4.0F / 9.0F - turbidity / 120.0F) * (PI - 2.0F * this.theta);
		
		this.zenithRelativeLuminance = ((4.0453F * turbidity - 4.9710F) * tan(chi) - 0.2155F * turbidity + 2.4192F) * 1000.0F;
		this.zenithX = (0.00165F * theta3 - 0.00374F * theta2 + 0.00208F * theta + 0.0F) * turbidity2 + (-0.02902F * theta3 + 0.06377F * theta2 - 0.03202F * theta + 0.00394F) * turbidity + (0.11693F * theta3 - 0.21196F * theta2 + 0.06052F * theta + 0.25885F);
		this.zenithY = (0.00275F * theta3 - 0.00610F * theta2 + 0.00316F * theta + 0.0F) * turbidity2 + (-0.04212F * theta3 + 0.08970F * theta2 - 0.04153F * theta + 0.00515F) * turbidity + (0.15346F * theta3 - 0.26756F * theta2 + 0.06669F * theta + 0.26688F);
		this.perezRelativeLuminance[0] = 0.17872F * turbidity - 1.46303F;
		this.perezRelativeLuminance[1] = -0.35540F * turbidity + 0.42749F;
		this.perezRelativeLuminance[2] = -0.02266F * turbidity + 5.32505F;
		this.perezRelativeLuminance[3] = 0.12064F * turbidity - 2.57705F;
		this.perezRelativeLuminance[4] = -0.06696F * turbidity + 0.37027F;
		this.perezX[0] = -0.01925F * turbidity - 0.25922F;
		this.perezX[1] = -0.06651F * turbidity + 0.00081F;
		this.perezX[2] = -0.00041F * turbidity + 0.21247F;
		this.perezX[3] = -0.06409F * turbidity - 0.89887F;
		this.perezX[4] = -0.00325F * turbidity + 0.04517F;
		this.perezY[0] = -0.01669F * turbidity - 0.26078F;
		this.perezY[1] = -0.09495F * turbidity + 0.00921F;
		this.perezY[2] = -0.00792F * turbidity + 0.21023F;
		this.perezY[3] = -0.04405F * turbidity - 1.65369F;
		this.perezY[4] = -0.01092F * turbidity + 0.05291F;
		
		final int w = 32;
		final int h = 32;
		
		this.colHistogram = new float[w];
		this.imageHistogram = new float[w][h];
		
		final float deltaU = 1.0F / w;
		final float deltaV = 1.0F / h;
		
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				final float u = (x + 0.5F) * deltaU;
				final float v = (y + 0.5F) * deltaV;
				
				final Color color = doCalculateColor(Vector3.direction(u, v));
				
				this.imageHistogram[x][y] = color.luminance() * sin(PI * v);
				
				if(y > 0) {
					this.imageHistogram[x][y] += this.imageHistogram[x][y - 1];
				}
			}
			
			this.colHistogram[x] = this.imageHistogram[x][h - 1];
			
			if(x > 0) {
				this.colHistogram[x] += this.colHistogram[x - 1];
			}
			
			for(int y = 0; y < h; y++) {
				this.imageHistogram[x][y] /= this.imageHistogram[x][h - 1];
			}
		}
		
		for(int x = 0; x < w; x++) {
			this.colHistogram[x] /= this.colHistogram[w - 1];
		}
		
//		this.jacobian = (2.0F * PI * PI) / (w * h);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float getTheta() {
		return this.theta;
	}
	
//	TODO: Add Javadocs.
	public float getZenithRelativeLuminance() {
		return this.zenithRelativeLuminance;
	}
	
//	TODO: Add Javadocs.
	public float getZenithX() {
		return this.zenithX;
	}
	
//	TODO: Add Javadocs.
	public float getZenithY() {
		return this.zenithY;
	}
	
//	TODO: Add Javadocs.
	public float[] getPerezRelativeLuminance() {
		return this.perezRelativeLuminance.clone();
	}
	
//	TODO: Add Javadocs.
	public float[] getPerezX() {
		return this.perezX.clone();
	}
	
//	TODO: Add Javadocs.
	public float[] getPerezY() {
		return this.perezY.clone();
	}
	
//	TODO: Add Javadocs.
	public OrthoNormalBasis getOrthoNormalBasis() {
		return this.orthoNormalBasis;
	}
	
//	TODO: Add Javadocs.
	public Vector3 getSunDirection() {
		return this.sunDirection;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Color doCalculateColor(final Vector3 direction) {
		if(direction.z < 0.0F) {
			return Color.BLACK;
		}
		
		final Vector3 direction0 = new Vector3(direction.x, direction.y, max(direction.z, 0.001F)).normalize();
		
		final float theta = acos(saturate(direction0.z, -1.0F, 1.0F));
		final float gamma = acos(saturate(direction0.dotProduct(this.sunDirection), -1.0F, 1.0F));
		final float relativeLuminance = doCalculatePerezFunction(this.perezRelativeLuminance, theta, gamma, this.zenithRelativeLuminance) * 1.0e-4F;
		final float x = doCalculatePerezFunction(this.perezX, theta, gamma, this.zenithX);
		final float y = doCalculatePerezFunction(this.perezY, theta, gamma, this.zenithY);
		
		final Color color = ChromaticSpectralCurve.getXYZ(x, y);
		
		final float x0 = color.r * relativeLuminance / color.g;
		final float y0 = relativeLuminance;
		final float z0 = color.b * relativeLuminance / color.g;
		
		return RGBColorSpace.SRGB.convertXYZToRGB(new Color(x0, y0, z0));
	}
	
	private float doCalculatePerezFunction(final float[] lam, final float theta, final float gamma, final float lvz) {
		final float den = ((1.0F + lam[0] * exp(lam[1])) * (1.0F + lam[2] * exp(lam[3] * this.theta) + lam[4] * cos(this.theta) * cos(this.theta)));
		final float num = ((1.0F + lam[0] * exp(lam[1] / cos(theta))) * (1.0F + lam[2] * exp(lam[3] * gamma) + lam[4] * cos(gamma) * cos(gamma)));
		
		return lvz * num / den;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	private static SpectralCurve doCalculateAttenuatedSunlight(final float theta, final float turbidity) {
		final float[] spectrum = new float[91];
		
		final float alpha = 1.3F;
		final float lozone = 0.35F;
		final float w = 2.0F;
		final float beta = 0.04608365822050F * turbidity - 0.04586025928522F;
		final float m = 1.0F / (cos(theta) + 0.000940F * pow(1.6386F - theta, -1.253F));
		
		for(int i = 0, lambda = 350; lambda <= 800; i++, lambda += 5) {
			final float tauR = exp(-m * 0.008735F * pow(lambda / 1000.0F, -4.08F));
			final float tauA = exp(-m * beta * pow(lambda / 1000.0F, -alpha));
			final float tauO = exp(-m * K_O_SPECTRAL_CURVE.sample(lambda) * lozone);
			final float tauG = exp(-1.41F * K_G_SPECTRAL_CURVE.sample(lambda) * m / pow(1.0F + 118.93F * K_G_SPECTRAL_CURVE.sample(lambda) * m, 0.45F));
			final float tauWA = exp(-0.2385F * K_WA_SPECTRAL_CURVE.sample(lambda) * w * m / pow(1.0F + 20.07F * K_WA_SPECTRAL_CURVE.sample(lambda) * w * m, 0.45F));
			final float amplitude = SOL_SPECTRAL_CURVE.sample(lambda) * tauR * tauA * tauO * tauG * tauWA;
			
			spectrum[i] = amplitude;
		}
		
		return new RegularSpectralCurve(350.0F, 800.0F, spectrum);
	}
	*/
}