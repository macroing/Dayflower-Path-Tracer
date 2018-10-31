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
package org.dayflower.pathtracer.scene;

import static org.dayflower.pathtracer.math.MathF.PI;
import static org.dayflower.pathtracer.math.MathF.acos;
import static org.dayflower.pathtracer.math.MathF.max;
import static org.dayflower.pathtracer.math.MathF.saturate;
import static org.dayflower.pathtracer.math.MathF.sin;

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.SpectralCurve;
import org.dayflower.pathtracer.color.colorspace.RGBColorSpace;
import org.dayflower.pathtracer.color.spectralcurve.ChromaticSpectralCurve;
import org.dayflower.pathtracer.color.spectralcurve.ConstantSpectralCurve;
import org.dayflower.pathtracer.color.spectralcurve.IrregularSpectralCurve;
import org.dayflower.pathtracer.color.spectralcurve.RegularSpectralCurve;
import org.dayflower.pathtracer.math.OrthoNormalBasis33F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;

//TODO: Add Javadocs!
public final class Sky {
	private static final float[] K_G_AMPLITUDES = {0.0F, 3.0F, 0.210F, 0.0F};
	private static final float[] K_G_WAVELENGTHS = {759.0F, 760.0F, 770.0F, 771.0F};
	private static final float[] K_O_AMPLITUDES = {10.0F, 4.8F, 2.7F, 1.35F, 0.8F, 0.380F, 0.160F, 0.075F, 0.04F, 0.019F, 0.007F, 0.0F, 0.003F, 0.003F, 0.004F, 0.006F, 0.008F, 0.009F, 0.012F, 0.014F, 0.017F, 0.021F, 0.025F, 0.03F, 0.035F, 0.04F, 0.045F, 0.048F, 0.057F, 0.063F, 0.07F, 0.075F, 0.08F, 0.085F, 0.095F, 0.103F, 0.110F, 0.12F, 0.122F, 0.12F, 0.118F, 0.115F, 0.12F, 0.125F, 0.130F, 0.12F, 0.105F, 0.09F, 0.079F, 0.067F, 0.057F, 0.048F, 0.036F, 0.028F, 0.023F, 0.018F, 0.014F, 0.011F, 0.010F, 0.009F, 0.007F, 0.004F, 0.0F, 0.0F};
	private static final float[] K_O_WAVELENGTHS = {300.0F, 305.0F, 310.0F, 315.0F, 320.0F, 325.0F, 330.0F, 335.0F, 340.0F, 345.0F, 350.0F, 355.0F, 445.0F, 450.0F, 455.0F, 460.0F, 465.0F, 470.0F, 475.0F, 480.0F, 485.0F, 490.0F, 495.0F, 500.0F, 505.0F, 510.0F, 515.0F, 520.0F, 525.0F, 530.0F, 535.0F, 540.0F, 545.0F, 550.0F, 555.0F, 560.0F, 565.0F, 570.0F, 575.0F, 580.0F, 585.0F, 590.0F, 595.0F, 600.0F, 605.0F, 610.0F, 620.0F, 630.0F, 640.0F, 650.0F, 660.0F, 670.0F, 680.0F, 690.0F, 700.0F, 710.0F, 720.0F, 730.0F, 740.0F, 750.0F, 760.0F, 770.0F, 780.0F, 790.0F};
	private static final float[] K_WA_AMPLITUDES = {0.0F, 0.160e-1F, 0.240e-1F, 0.125e-1F, 0.100e+1F, 0.870F, 0.610e-1F, 0.100e-2F, 0.100e-4F, 0.100e-4F, 0.600e-3F, 0.175e-1F, 0.360e-1F};
	private static final float[] K_WA_WAVELENGTHS = {689.0F, 690.0F, 700.0F, 710.0F, 720.0F, 730.0F, 740.0F, 750.0F, 760.0F, 770.0F, 780.0F, 790.0F, 800.0F};
	private static final float[] SOL_AMPLITUDES = {165.5F, 162.3F, 211.2F, 258.8F, 258.2F, 242.3F, 267.6F, 296.6F, 305.4F, 300.6F, 306.6F, 288.3F, 287.1F, 278.2F, 271.0F, 272.3F, 263.6F, 255.0F, 250.6F, 253.1F, 253.5F, 251.3F, 246.3F, 241.7F, 236.8F, 232.1F, 228.2F, 223.4F, 219.7F, 215.3F, 211.0F, 207.3F, 202.4F, 198.7F, 194.3F, 190.7F, 186.3F, 182.6F};
	private static final SpectralCurve K_G_SPECTRAL_CURVE = new IrregularSpectralCurve(K_G_AMPLITUDES, K_G_WAVELENGTHS);
	private static final SpectralCurve K_O_SPECTRAL_CURVE = new IrregularSpectralCurve(K_O_AMPLITUDES, K_O_WAVELENGTHS);
	private static final SpectralCurve K_WA_SPECTRAL_CURVE = new IrregularSpectralCurve(K_WA_AMPLITUDES, K_WA_WAVELENGTHS);
	private static final SpectralCurve SOL_SPECTRAL_CURVE = new RegularSpectralCurve(380.0F, 750.0F, SOL_AMPLITUDES);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Color sunColor;
	private double zenithRelativeLuminance;
	private double zenithX;
	private double zenithY;
	private final double[] perezRelativeLuminance = new double[5];
	private final double[] perezX = new double[5];
	private final double[] perezY = new double[5];
	private float jacobian;
	private float theta;
	private float turbidity;
	private float[] colHistogram;
	private float[] imageHistogram;
	private final int imageHistogramHeight = 32;
	private final int imageHistogramWidth = 32;
	private final int samples = 4;
	private final OrthoNormalBasis33F orthoNormalBasis = new OrthoNormalBasis33F(Vector3F.y(), Vector3F.z(), Vector3F.x());
	private Point3F sunOrigin;
	private SpectralCurve radiance;
	private Vector3F sunDirection;
	private Vector3F sunDirectionWorld;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Sky() {
		set();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public double getZenithRelativeLuminance() {
		return this.zenithRelativeLuminance;
	}
	
//	TODO: Add Javadocs.
	public double getZenithX() {
		return this.zenithX;
	}
	
//	TODO: Add Javadocs.
	public double getZenithY() {
		return this.zenithY;
	}
	
//	TODO: Add Javadocs.
	public double[] getPerezRelativeLuminance() {
		return this.perezRelativeLuminance.clone();
	}
	
//	TODO: Add Javadocs.
	public double[] getPerezX() {
		return this.perezX.clone();
	}
	
//	TODO: Add Javadocs.
	public double[] getPerezY() {
		return this.perezY.clone();
	}
	
//	TODO: Add Javadocs.
	public float getJacobian() {
		return this.jacobian;
	}
	
//	TODO: Add Javadocs.
	public float getTheta() {
		return this.theta;
	}
	
//	TODO: Add Javadocs.
	public float getTurbidity() {
		return this.turbidity;
	}
	
//	TODO: Add Javadocs.
	public float[] getColHistogram() {
		return this.colHistogram;
	}
	
//	TODO: Add Javadocs.
	public float[] getImageHistogram() {
		return this.imageHistogram;
	}
	
//	TODO: Add Javadocs.
	public int getImageHistogramHeight() {
		return this.imageHistogramHeight;
	}
	
//	TODO: Add Javadocs.
	public int getImageHistogramWidth() {
		return this.imageHistogramWidth;
	}
	
//	TODO: Add Javadocs.
	public int getSamples() {
		return this.samples;
	}
	
//	TODO: Add Javadocs.
	public Color getSunColor() {
		return this.sunColor;
	}
	
//	TODO: Add Javadocs.
	public OrthoNormalBasis33F getOrthoNormalBasis() {
		return this.orthoNormalBasis;
	}
	
//	TODO: Add Javadocs.
	public Point3F getSunOrigin() {
		return this.sunOrigin;
	}
	
//	TODO: Add Javadocs.
	public Vector3F getSunDirection() {
		return this.sunDirection;
	}
	
//	TODO: Add Javadocs.
	public Vector3F getSunDirectionWorld() {
		return this.sunDirectionWorld;
	}
	
//	TODO: Add Javadocs.
	public void set() {
		set(new Vector3F(1.0F, 1.0F, 1.0F));
	}
	
//	TODO: Add Javadocs.
	public void set(final Vector3F sunDirectionWorld) {
		set(sunDirectionWorld, 2.0F);
	}
	
//	TODO: Add Javadocs.
	public void set(final Vector3F sunDirectionWorld, final float turbidity) {
		this.sunDirectionWorld = sunDirectionWorld.normalize();
		this.turbidity = turbidity;
		this.sunDirection = this.sunDirectionWorld.transformReverse(this.orthoNormalBasis).normalize();
		this.sunOrigin = new Point3F().pointAt(this.sunDirectionWorld, 10000.0F);
		this.theta = acos(saturate(this.sunDirection.z, -1.0F, 1.0F));
		
		if(this.sunDirection.z > 0.0F) {
			this.radiance = doCalculateAttenuatedSunlight(this.theta, turbidity);
			this.sunColor = RGBColorSpace.SRGB.convertXYZToRGB(this.radiance.toXYZ().multiply(1.0e-4F)).constrain();
		} else {
			this.radiance = new ConstantSpectralCurve(0.0F);
			this.sunColor = Color.BLACK;
		}
		
		final double theta = this.theta;
		final double theta2 = theta * theta;
		final double theta3 = theta * theta * theta;
		final double turbidity2 = turbidity * turbidity;
		final double chi = (4.0D / 9.0D - turbidity / 120.0D) * (PI - 2.0D * this.theta);
		
		this.zenithRelativeLuminance = ((4.0453D * turbidity - 4.9710D) * Math.tan(chi) - 0.2155D * turbidity + 2.4192D) * 1000.0D;
		this.zenithX = (0.00165D * theta3 - 0.00374D * theta2 + 0.00208D * theta + 0.0D) * turbidity2 + (-0.02902D * theta3 + 0.06377D * theta2 - 0.03202D * theta + 0.00394D) * turbidity + (0.11693D * theta3 - 0.21196D * theta2 + 0.06052D * theta + 0.25885D);
		this.zenithY = (0.00275D * theta3 - 0.00610D * theta2 + 0.00316D * theta + 0.0D) * turbidity2 + (-0.04212D * theta3 + 0.08970D * theta2 - 0.04153D * theta + 0.00515D) * turbidity + (0.15346D * theta3 - 0.26756D * theta2 + 0.06669D * theta + 0.26688D);
		this.perezRelativeLuminance[0] = 0.17872D * turbidity - 1.46303D;
		this.perezRelativeLuminance[1] = -0.35540D * turbidity + 0.42749D;
		this.perezRelativeLuminance[2] = -0.02266D * turbidity + 5.32505D;
		this.perezRelativeLuminance[3] = 0.12064D * turbidity - 2.57705D;
		this.perezRelativeLuminance[4] = -0.06696D * turbidity + 0.37027D;
		this.perezX[0] = -0.01925D * turbidity - 0.25922D;
		this.perezX[1] = -0.06651D * turbidity + 0.00081D;
		this.perezX[2] = -0.00041D * turbidity + 0.21247D;
		this.perezX[3] = -0.06409D * turbidity - 0.89887D;
		this.perezX[4] = -0.00325D * turbidity + 0.04517D;
		this.perezY[0] = -0.01669D * turbidity - 0.26078D;
		this.perezY[1] = -0.09495D * turbidity + 0.00921D;
		this.perezY[2] = -0.00792D * turbidity + 0.21023D;
		this.perezY[3] = -0.04405D * turbidity - 1.65369D;
		this.perezY[4] = -0.01092D * turbidity + 0.05291D;
		
		final int w = this.imageHistogramWidth;
		final int h = this.imageHistogramHeight;
		
		this.colHistogram = new float[w];
		this.imageHistogram = new float[w * h];
		
		final float deltaU = 1.0F / w;
		final float deltaV = 1.0F / h;
		
		for(int x = 0, index = 0; x < w; x++) {
			for(int y = 0; y < h; y++, index++) {
				final float u = (x + 0.5F) * deltaU;
				final float v = (y + 0.5F) * deltaV;
				
				final Color color = doCalculateColor(Vector3F.direction(u, v));
				
				this.imageHistogram[index] = color.luminance() * sin(PI * v);
				
				if(y > 0) {
					this.imageHistogram[index] += this.imageHistogram[index - 1];
				}
			}
			
			this.colHistogram[x] = this.imageHistogram[index - 1];
			
			if(x > 0) {
				this.colHistogram[x] += this.colHistogram[x - 1];
			}
			
			for(int y = 0; y < h; y++) {
				this.imageHistogram[index - h + y] /= this.imageHistogram[index - 1];
			}
		}
		
		for(int x = 0; x < w; x++) {
			this.colHistogram[x] /= this.colHistogram[w - 1];
		}
		
		this.jacobian = (2.0F * PI * PI) / (w * h);
	}
	
	public void setTurbidity(final float turbidity) {
		set(this.sunDirectionWorld, turbidity);
	}
	
	public void setX(final float x) {
		set(new Vector3F(x, this.sunDirectionWorld.y, this.sunDirectionWorld.z).normalize(), this.turbidity);
	}
	
	public void setY(final float y) {
		set(new Vector3F(this.sunDirectionWorld.x, y, this.sunDirectionWorld.z).normalize(), this.turbidity);
	}
	
	public void setZ(final float z) {
		set(new Vector3F(this.sunDirectionWorld.x, this.sunDirectionWorld.y, z).normalize(), this.turbidity);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Color doCalculateColor(final Vector3F direction) {
		if(direction.z < 0.0F) {
			return Color.BLACK;
		}
		
		final Vector3F direction0 = new Vector3F(direction.x, direction.y, max(direction.z, 0.001F)).normalize();
		
		final double theta = Math.acos(saturate(direction0.z, -1.0F, 1.0F));
		final double gamma = Math.acos(saturate(direction0.dotProduct(this.sunDirection), -1.0F, 1.0F));
		final double relativeLuminance = doCalculatePerezFunction(this.perezRelativeLuminance, theta, gamma, this.zenithRelativeLuminance) * 1.0e-4F;
		final double x = doCalculatePerezFunction(this.perezX, theta, gamma, this.zenithX);
		final double y = doCalculatePerezFunction(this.perezY, theta, gamma, this.zenithY);
		
		final Color color = ChromaticSpectralCurve.getXYZ((float)(x), (float)(y));
		
		final float x0 = (float)(color.r * relativeLuminance / color.g);
		final float y0 = (float)(relativeLuminance);
		final float z0 = (float)(color.b * relativeLuminance / color.g);
		
		return RGBColorSpace.SRGB.convertXYZToRGB(new Color(x0, y0, z0));
	}
	
	private double doCalculatePerezFunction(final double[] lam, final double theta, final double gamma, final double lvz) {
		final double den = ((1.0D + lam[0] * Math.exp(lam[1])) * (1.0D + lam[2] * Math.exp(lam[3] * this.theta) + lam[4] * Math.cos(this.theta) * Math.cos(this.theta)));
		final double num = ((1.0D + lam[0] * Math.exp(lam[1] / Math.cos(theta))) * (1.0D + lam[2] * Math.exp(lam[3] * gamma) + lam[4] * Math.cos(gamma) * Math.cos(gamma)));
		
		return lvz * num / den;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static SpectralCurve doCalculateAttenuatedSunlight(final float theta, final float turbidity) {
		final float[] spectrum = new float[91];
		
		final double alpha = 1.3D;
		final double lozone = 0.35D;
		final double w = 2.0D;
		final double beta = 0.04608365822050D * turbidity - 0.04586025928522D;
		final double relativeOpticalMass = 1.0D / (Math.cos(theta) + 0.000940D * Math.pow(1.6386D - theta, -1.253D));
		
		for(int i = 0, lambda = 350; lambda <= 800; i++, lambda += 5) {
			final double tauRayleighScattering = Math.exp(-relativeOpticalMass * 0.008735D * Math.pow(lambda / 1000.0D, -4.08D));
			final double tauAerosolAttenuation = Math.exp(-relativeOpticalMass * beta * Math.pow(lambda / 1000.0D, -alpha));
			final double tauOzoneAbsorptionAttenuation = Math.exp(-relativeOpticalMass * K_O_SPECTRAL_CURVE.sample(lambda) * lozone);
			final double tauGasAbsorptionAttenuation = Math.exp(-1.41D * K_G_SPECTRAL_CURVE.sample(lambda) * relativeOpticalMass / Math.pow(1.0D + 118.93D * K_G_SPECTRAL_CURVE.sample(lambda) * relativeOpticalMass, 0.45D));
			final double tauWaterVaporAbsorptionAttenuation = Math.exp(-0.2385D * K_WA_SPECTRAL_CURVE.sample(lambda) * w * relativeOpticalMass / Math.pow(1.0D + 20.07D * K_WA_SPECTRAL_CURVE.sample(lambda) * w * relativeOpticalMass, 0.45D));
			final double amplitude = SOL_SPECTRAL_CURVE.sample(lambda) * tauRayleighScattering * tauAerosolAttenuation * tauOzoneAbsorptionAttenuation * tauGasAbsorptionAttenuation * tauWaterVaporAbsorptionAttenuation;
			
			spectrum[i] = (float)(amplitude);
		}
		
		return new RegularSpectralCurve(350.0F, 800.0F, spectrum);
	}
}