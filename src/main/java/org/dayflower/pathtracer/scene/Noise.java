/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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

import static org.dayflower.pathtracer.math.Math2.abs;
import static org.dayflower.pathtracer.math.Math2.floor;

public final class Noise {
	private float amplitude;
	private float frequency;
	private float gain;
	private float lacunarity;
	private int octaves;
	private final int[] permutations0 = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
	private final int[] permutations1 = new int[512];
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Noise() {
		setAmplitude(0.5F);
		setFrequency(0.1F);
		setLacunarity(4.0F);
		setGain(1.0F / getLacunarity());
		setOctaves(8);
		
		for(int i = 0; i < this.permutations0.length; i++) {
			this.permutations1[i] = this.permutations0[i];
			this.permutations1[i + this.permutations0.length] = this.permutations0[i];
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public float getAmplitude() {
		return this.amplitude;
	}
	
	public float getFrequency() {
		return this.frequency;
	}
	
	public float getGain() {
		return this.gain;
	}
	
	public float getLacunarity() {
		return this.lacunarity;
	}
	
	public float perlinNoise(final float x, final float y, final float z) {
//		Calculate the floor of the X-, Y- and Z-coordinates:
		final float floorX = floor(x);
		final float floorY = floor(y);
		final float floorZ = floor(z);
		
//		Cast the previously calculated floors of the X-, Y- and Z-coordinates to ints:
		final int x0 = (int)(floorX) & 0xFF;
		final int y0 = (int)(floorY) & 0xFF;
		final int z0 = (int)(floorZ) & 0xFF;
		
//		Calculate the fractional parts of the X-, Y- and Z-coordinates by subtracting their respective floor values:
		final float x1 = x - floorX;
		final float y1 = y - floorY;
		final float z1 = z - floorZ;
		
//		Calculate the U-, V- and W-coordinates:
		final float u = x1 * x1 * x1 * (x1 * (x1 * 6.0F - 15.0F) + 10.0F);
		final float v = y1 * y1 * y1 * (y1 * (y1 * 6.0F - 15.0F) + 10.0F);
		final float w = z1 * z1 * z1 * (z1 * (z1 * 6.0F - 15.0F) + 10.0F);
		
//		Calculate some hash values:
		final int a0 = this.permutations1[x0] + y0;
		final int a1 = this.permutations1[a0] + z0;
		final int a2 = this.permutations1[a0 + 1] + z0;
		final int b0 = this.permutations1[x0 + 1] + y0;
		final int b1 = this.permutations1[b0] + z0;
		final int b2 = this.permutations1[b0 + 1] + z0;
		final int hash0 = this.permutations1[a1] & 15;
		final int hash1 = this.permutations1[b1] & 15;
		final int hash2 = this.permutations1[a2] & 15;
		final int hash3 = this.permutations1[b2] & 15;
		final int hash4 = this.permutations1[a1 + 1] & 15;
		final int hash5 = this.permutations1[b1 + 1] & 15;
		final int hash6 = this.permutations1[a2 + 1] & 15;
		final int hash7 = this.permutations1[b2 + 1] & 15;
		
//		Calculate the gradients:
		final float gradient0U = hash0 < 8 || hash0 == 12 || hash0 == 13 ? x1 : y1;
		final float gradient0V = hash0 < 4 || hash0 == 12 || hash0 == 13 ? y1 : z1;
		final float gradient0 = ((hash0 & 1) == 0 ? gradient0U : -gradient0U) + ((hash0 & 2) == 0 ? gradient0V : -gradient0V);
		final float gradient1U = hash1 < 8 || hash1 == 12 || hash1 == 13 ? x1 - 1.0F : y1;
		final float gradient1V = hash1 < 4 || hash1 == 12 || hash1 == 13 ? y1 : z1;
		final float gradient1 = ((hash1 & 1) == 0 ? gradient1U : -gradient1U) + ((hash1 & 2) == 0 ? gradient1V : -gradient1V);
		final float gradient2U = hash2 < 8 || hash2 == 12 || hash2 == 13 ? x1 : y1 - 1.0F;
		final float gradient2V = hash2 < 4 || hash2 == 12 || hash2 == 13 ? y1 - 1.0F : z1;
		final float gradient2 = ((hash2 & 1) == 0 ? gradient2U : -gradient2U) + ((hash2 & 2) == 0 ? gradient2V : -gradient2V);
		final float gradient3U = hash3 < 8 || hash3 == 12 || hash3 == 13 ? x1 - 1.0F : y1 - 1.0F;
		final float gradient3V = hash3 < 4 || hash3 == 12 || hash3 == 13 ? y1 - 1.0F : z1;
		final float gradient3 = ((hash3 & 1) == 0 ? gradient3U : -gradient3U) + ((hash3 & 2) == 0 ? gradient3V : -gradient3V);
		final float gradient4U = hash4 < 8 || hash4 == 12 || hash4 == 13 ? x1 : y1;
		final float gradient4V = hash4 < 4 || hash4 == 12 || hash4 == 13 ? y1 : z1 - 1.0F;
		final float gradient4 = ((hash4 & 1) == 0 ? gradient4U : -gradient4U) + ((hash4 & 2) == 0 ? gradient4V : -gradient4V);
		final float gradient5U = hash5 < 8 || hash5 == 12 || hash5 == 13 ? x1 - 1.0F : y1;
		final float gradient5V = hash5 < 4 || hash5 == 12 || hash5 == 13 ? y1 : z1 - 1.0F;
		final float gradient5 = ((hash5 & 1) == 0 ? gradient5U : -gradient5U) + ((hash5 & 2) == 0 ? gradient5V : -gradient5V);
		final float gradient6U = hash6 < 8 || hash6 == 12 || hash6 == 13 ? x1 : y1 - 1.0F;
		final float gradient6V = hash6 < 4 || hash6 == 12 || hash6 == 13 ? y1 - 1.0F : z1 - 1.0F;
		final float gradient6 = ((hash6 & 1) == 0 ? gradient6U : -gradient6U) + ((hash6 & 2) == 0 ? gradient6V : -gradient6V);
		final float gradient7U = hash7 < 8 || hash7 == 12 || hash7 == 13 ? x1 - 1.0F : y1 - 1.0F;
		final float gradient7V = hash7 < 4 || hash7 == 12 || hash7 == 13 ? y1 - 1.0F : z1 - 1.0F;
		final float gradient7 = ((hash7 & 1) == 0 ? gradient7U : -gradient7U) + ((hash7 & 2) == 0 ? gradient7V : -gradient7V);
		
//		Perform linear interpolation:
		final float lerp0 = gradient0 + u * (gradient1 - gradient0);
		final float lerp1 = gradient2 + u * (gradient3 - gradient2);
		final float lerp2 = gradient4 + u * (gradient5 - gradient4);
		final float lerp3 = gradient6 + u * (gradient7 - gradient6);
		final float lerp4 = lerp0 + v * (lerp1 - lerp0);
		final float lerp5 = lerp2 + v * (lerp3 - lerp2);
		final float lerp6 = lerp4 + w * (lerp5 - lerp4);
		
		return lerp6;
	}
	
	public float simplexFractalX(final float x) {
		return simplexFractalX(x, this.octaves);
	}
	
	public float simplexFractalX(final float x, final int octaves) {
		float result = 0.0F;
		
		float amplitude = this.amplitude;
		float frequency = this.frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += amplitude * simplexNoiseX(x * frequency);
			
			amplitude *= this.gain;
			frequency *= this.lacunarity;
		}
		
		return result;
	}
	
	public float simplexFractalXY(final float x, final float y) {
		return simplexFractalXY(x, y, this.octaves);
	}
	
	public float simplexFractalXY(final float x, final float y, final int octaves) {
		float result = 0.0F;
		
		float amplitude = this.amplitude;
		float frequency = this.frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += amplitude * simplexNoiseXY(x * frequency, y * frequency);
			
			amplitude *= this.gain;
			frequency *= this.lacunarity;
		}
		
		return result;
	}
	
	public float simplexNoiseX(final float x) {
		final int i0 = doFastFloor(x);
		final int i1 = i0 + 1;
		
		final float x0 = x - i0;
		final float x1 = x0 - 1.0F;
		
		final float t00 = 1.0F - x0 * x0;
		final float t01 = t00 * t00;
		
		final float t10 = 1.0F - x1 * x1;
		final float t11 = t10 * t10;
		
		final float n0 = t01 * t01 * doGradientX(doHash(i0), x0);
		final float n1 = t11 * t11 * doGradientX(doHash(i1), x1);
		
		return 0.395F * (n0 + n1);
	}
	
	public float simplexNoiseXY(final float x, final float y) {
		final float a = 0.366025403F;
		final float b = 0.211324865F;
		
		final float s = (x + y) * a;
		final float sx = s + x;
		final float sy = s + y;
		
		final int i0 = doFastFloor(sx);
		final int j0 = doFastFloor(sy);
		
		final float t = (i0 + j0) * b;
		
		final float x00 = i0 - t;
		final float y00 = j0 - t;
		final float x01 = x - x00;
		final float y01 = y - y00;
		
		final int i1 = x01 > y01 ? 1 : 0;
		final int j1 = x01 > y01 ? 0 : 1;
		
		final float x1 = x01 - i1 + b;
		final float y1 = y01 - j1 + b;
		final float x2 = x01 - 1.0F + 2.0F * b;
		final float y2 = y01 - 1.0F + 2.0F * b;
		
		final float t00 = 0.5F - x01 * x01 - y01 * y01;
		final float t01 = t00 < 0.0F ? t00 : t00 * t00;
		
		final float n0 = t00 < 0.0F ? 0.0F : t01 * t01 * doGradientXY(doHash(i0 + doHash(j0)), x01, y01);
		
		final float t10 = 0.5F - x1 * x1 - y1 * y1;
		final float t11 = t10 < 0.0F ? t10 : t10 * t10;
		
		final float n1 = t10 < 0.0F ? 0.0F : t11 * t11 * doGradientXY(doHash(i0 + i1 + doHash(j0 + j1)), x1, y1);
		
		final float t20 = 0.5F - x2 * x2 - y2 * y2;
		final float t21 = t20 < 0.0F ? t20 : t20 * t20;
		
		final float n2 = t20 < 0.0F ? 0.0F : t21 * t21 * doGradientXY(doHash(i0 + 1 + doHash(j0 + 1)), x2, y2);
		
		return 45.23065F * (n0 + n1 + n2);
	}
	
	public int getOctaves() {
		return this.octaves;
	}
	
	public void setAmplitude(final float amplitude) {
		this.amplitude = amplitude;
	}
	
	public void setFrequency(final float frequency) {
		this.frequency = frequency;
	}
	
	public void setGain(final float gain) {
		this.gain = gain;
	}
	
	public void setLacunarity(final float lacunarity) {
		this.lacunarity = lacunarity;
	}
	
	public void setOctaves(final int octaves) {
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int doHash(final int index) {
		return this.permutations1[abs(index) % this.permutations1.length];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float doGradientX(final int hash, final float x) {
		final int hash0 = hash & 0x0F;
		
		float gradient = 1.0F + (hash0 & 7);
		
		if((hash0 & 8) != 0) {
			gradient = -gradient;
		}
		
		return gradient * x;
	}
	
	private static float doGradientXY(final int hash, final float x, final float y) {
		final int hash0 = hash & 0x3F;
		
		final float u = hash0 < 4 ? x : y;
		final float v = hash0 < 4 ? y : x;
		
		return ((hash0 & 1) == 1 ? -u : u) + ((hash0 & 2) == 1 ? -2.0F * v : 2.0F * v);
	}
	
	private static int doFastFloor(final float value) {
		final int i = (int)(value);
		
		return value < i ? i - 1 : i;
	}
}