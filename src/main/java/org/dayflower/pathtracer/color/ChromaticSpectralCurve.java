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

//TODO: Add Javadocs!
public final class ChromaticSpectralCurve extends SpectralCurve {
	private static final Color S0_XYZ;
	private static final Color S1_XYZ;
	private static final Color S2_XYZ;
	private static final float[] S0_AMPLITUDES = {0.04F, 6.0F, 29.6F, 55.3F, 57.3F, 61.8F, 61.5F, 68.8F, 63.4F, 65.8F, 94.8F, 104.8F, 105.9F, 96.8F, 113.9F, 125.6F, 125.5F, 121.3F, 121.3F, 113.5F, 113.1F, 110.8F, 106.5F, 108.8F, 105.3F, 104.4F, 100.0F, 96.0F, 95.1F, 89.1F, 90.5F, 90.3F, 88.4F, 84.0F, 85.1F, 81.9F, 82.6F, 84.9F, 81.3F, 71.9F, 74.3F, 76.4F, 63.3F, 71.7F, 77.0F, 65.2F, 47.7F, 68.6F, 65.0F, 66.0F, 61.0F, 53.3F, 58.9F, 61.9F};
	private static final float[] S1_AMPLITUDES = {0.02F, 4.5F, 22.4F, 42.0F, 40.6F, 41.6F, 38.0F, 42.4F, 38.5F, 35.0F, 43.4F, 46.3F, 43.9F, 37.1F, 36.7F, 35.9F, 32.6F, 27.9F, 24.3F, 20.1F, 16.2F, 13.2F, 8.6F, 6.1F, 4.2F, 1.9F, 0.0F, -1.6F, -3.5F, -3.5F, -5.8F, -7.2F, -8.6F, -9.5F, -10.9F, -10.7F, -12.0F, -14.0F, -13.6F, -12.0F, -13.3F, -12.9F, -10.6F, -11.6F, -12.2F, -10.2F, -7.8F, -11.2F, -10.4F, -10.6F, -9.7F, -8.3F, -9.3F, -9.8F};
	private static final float[] S2_AMPLITUDES = {0.0F, 2.0F, 4.0F, 8.5F, 7.8F, 6.7F, 5.3F, 6.1F, 3.0F, 1.2F, -1.1F, -0.5F, -0.7F, -1.2F, -2.6F, -2.9F, -2.8F, -2.6F, -2.6F, -1.8F, -1.5F, -1.3F, -1.2F, -1.0F, -0.5F, -0.3F, 0.0F, 0.2F, 0.5F, 2.1F, 3.2F, 4.1F, 4.7F, 5.1F, 6.7F, 7.3F, 8.6F, 9.8F, 10.2F, 8.3F, 9.6F, 8.5F, 7.0F, 7.6F, 8.0F, 6.7F, 5.2F, 7.4F, 6.8F, 7.0F, 6.4F, 5.5F, 6.1F, 6.5F};
	private static final SpectralCurve K_S0_SPECTRAL_CURVE = new RegularSpectralCurve(300.0F, 830.0F, S0_AMPLITUDES);
	private static final SpectralCurve K_S1_SPECTRAL_CURVE = new RegularSpectralCurve(300.0F, 830.0F, S1_AMPLITUDES);
	private static final SpectralCurve K_S2_SPECTRAL_CURVE = new RegularSpectralCurve(300.0F, 830.0F, S2_AMPLITUDES);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float a;
	private final float b;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		S0_XYZ = K_S0_SPECTRAL_CURVE.toXYZ();
		S1_XYZ = K_S1_SPECTRAL_CURVE.toXYZ();
		S2_XYZ = K_S2_SPECTRAL_CURVE.toXYZ();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public ChromaticSpectralCurve(final float x, final float y) {
		this.a = (-1.3515F - 1.7703F * x + 5.9114F * y) / (0.0241F + 0.2562F * x - 0.7341F * y);
		this.b = (0.03F - 31.4424F * x + 30.0717F * y) / (0.0241F + 0.2562F * x - 0.7341F * y);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	@Override
	public float sample(float lambda) {
		return K_S0_SPECTRAL_CURVE.sample(lambda) + this.a * K_S1_SPECTRAL_CURVE.sample(lambda) + this.b * K_S2_SPECTRAL_CURVE.sample(lambda);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static Color getXYZ(final float x, final float y) {
		float a = (-1.3515F - 1.7703F * x + 5.9114F * y) / (0.0241F + 0.2562F * x - 0.7341F * y);
		float b = (0.03F - 31.4424F * x + 30.0717F * y) / (0.0241F + 0.2562F * x - 0.7341F * y);
		float x0 = S0_XYZ.r + a * S1_XYZ.r + b * S2_XYZ.r;
		float y0 = S0_XYZ.g + a * S1_XYZ.g + b * S2_XYZ.g;
		float z0 = S0_XYZ.b + a * S1_XYZ.b + b * S2_XYZ.b;
		
		return new Color(x0, y0, z0);
	}
	
//	TODO: Add Javadocs!
	public static float[] getS0XYZ() {
		return new float[] {S0_XYZ.r, S0_XYZ.g, S0_XYZ.b};
	}
	
//	TODO: Add Javadocs!
	public static float[] getS1XYZ() {
		return new float[] {S1_XYZ.r, S1_XYZ.g, S1_XYZ.b};
	}
	
//	TODO: Add Javadocs!
	public static float[] getS2XYZ() {
		return new float[] {S2_XYZ.r, S2_XYZ.g, S2_XYZ.b};
	}
}