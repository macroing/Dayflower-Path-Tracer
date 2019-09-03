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

import java.util.concurrent.ThreadLocalRandom;

import org.dayflower.pathtracer.math.MathF;

import com.amd.aparapi.Kernel;

/**
 * An abstract extension of the {@code Kernel} class that adds additional features.
 * <p>
 * The features added are the following:
 * <ul>
 * <li>A pseudo-random number generator</li>
 * <li>Constants such as {@code PI}</li>
 * <li>Methods that approximates sine and cosine based on tables</li>
 * <li>Methods that computes Perlin- and Simplex noise</li>
 * <li>Methods that computes fractional Brownian motion (fBm)</li>
 * <li>Methods for linear and bilinear interpolation</li>
 * <li>Additional methods such as smoothstep, modulo and remainder</li>
 * </ul>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractKernel extends Kernel {
	/**
	 * A very small number.
	 */
	public static final float EPSILON = 0.02F;
	
	/**
	 * A very large number.
	 */
	public static final float INFINITY = 100000.0F;
	
	/**
	 * The {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter.
	 */
	public static final float PI = (float)(Math.PI);
	
	/**
	 * The {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter, divided by {@code 180.0F}.
	 */
	public static final float PI_DIVIDED_BY_180 = PI / 180.0F;
	
	/**
	 * The {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter, divided by {@code 360.0F}.
	 */
	public static final float PI_DIVIDED_BY_360 = PI / 360.0F;
	
	/**
	 * The {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter, multiplied by {@code 2.0F}.
	 */
	public static final float PI_MULTIPLIED_BY_TWO = PI * 2.0F;
	
	/**
	 * The reciprocal of the {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter, multiplied by {@code 2.0F}.
	 */
	public static final float PI_MULTIPLIED_BY_TWO_RECIPROCAL = 1.0F / PI_MULTIPLIED_BY_TWO;
	
	/**
	 * The reciprocal of the {@code float} value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter.
	 */
	public static final float PI_RECIPROCAL = 1.0F / PI;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float COS_TABLE_AND_SIN_TABLE_DEGREES_MAXIMUM = 360.0F;
	private static final float COS_TABLE_AND_SIN_TABLE_DEGREES_TO_INDEX = (~(-1 << 12) + 1) / COS_TABLE_AND_SIN_TABLE_DEGREES_MAXIMUM;
	private static final float COS_TABLE_AND_SIN_TABLE_RADIANS = PI / 180.0F;
	private static final float COS_TABLE_AND_SIN_TABLE_RADIANS_MAXIMUM = PI * 2.0F;
	private static final float COS_TABLE_AND_SIN_TABLE_RADIANS_TO_INDEX = (~(-1 << 12) + 1) / COS_TABLE_AND_SIN_TABLE_RADIANS_MAXIMUM;
	private static final float PRNG_NEXT_FLOAT_RECIPROCAL = 1.0F / (1 << 24);
	private static final float SIMPLEX_F2 = 0.3660254037844386F;
	private static final float SIMPLEX_F3 = 1.0F / 3.0F;
	private static final float SIMPLEX_F4 = 0.30901699437494745F;
	private static final float SIMPLEX_G2 = 0.21132486540518713F;
	private static final float SIMPLEX_G3 = 1.0F / 6.0F;
	private static final float SIMPLEX_G4 = 0.1381966011250105F;
	private static final int COS_TABLE_AND_SIN_TABLE_BITS = 12;
	private static final int COS_TABLE_AND_SIN_TABLE_COUNT = ~(-1 << COS_TABLE_AND_SIN_TABLE_BITS) + 1;
	private static final int COS_TABLE_AND_SIN_TABLE_MASK = ~(-1 << COS_TABLE_AND_SIN_TABLE_BITS);
	private static final long PRNG_ADDEND = 0xBL;
	private static final long PRNG_MASK = (1L << 48L) - 1L;
	private static final long PRNG_MULTIPLIER = 0x5DEECE66DL;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The global amplitude.
	 */
	protected float globalAmplitude;
	
	/**
	 * The global frequency.
	 */
	protected float globalFrequency;
	
	/**
	 * The global gain.
	 */
	protected float globalGain;
	
	/**
	 * The global lacunarity.
	 */
	protected float globalLacunarity;
	
	/**
	 * The cosine table (array) used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] cosTable;
	
	/**
	 * A gradient array used by the Simplex noise algorithms.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] simplexGradient3;
	
	/**
	 * A gradient array used by the Simplex noise algorithms.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] simplexGradient4;
	
	/**
	 * The sine table (array) used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] sinTable;
	
	/**
	 * The global octaves.
	 */
	protected int globalOctaves;
	
	/**
	 * The resolution along the X-axis.
	 */
	protected int resolutionX;
	
	/**
	 * The resolution along the Y-axis.
	 */
	protected int resolutionY;
	
	/**
	 * A permutations array used by the Perlin- and Simplex noise algorithms.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected int[] noisePermutations;
	
	/**
	 * A permutations array used by the Simplex noise algorithms.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected int[] noisePermutationsModulo12;
	
	/**
	 * The seed array used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected long[] seeds;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractKernel} instance.
	 */
	protected AbstractKernel() {
//		Initialize the resolution variables:
		this.resolutionX = 1;
		this.resolutionY = 1;
		
//		Initialize the global noise variables:
		this.globalAmplitude = 0.5F;
		this.globalFrequency = 0.2F;
		this.globalLacunarity = 2.0F;
		this.globalGain = 1.0F / this.globalLacunarity;
		this.globalOctaves = 2;
		
//		Initialize the noise variables:
		this.noisePermutations = new int[1];
		this.noisePermutationsModulo12 = new int[1];
		
//		Initialize the Simplex noise variables:
		this.simplexGradient3 = new float[1];
		this.simplexGradient4 = new float[1];
		
//		Initialize the cos table and sin table variables:
		this.cosTable = new float[1];
		this.sinTable = new float[1];
		
//		Initialize the PRNG variables:
		this.seeds = new long[this.resolutionX * this.resolutionY];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the global amplitude.
	 * 
	 * @return the global amplitude
	 */
	public final float getGlobalAmplitude() {
		return this.globalAmplitude;
	}
	
	/**
	 * Returns the global frequency.
	 * 
	 * @return the global frequency
	 */
	public final float getGlobalFrequency() {
		return this.globalFrequency;
	}
	
	/**
	 * Returns the global gain.
	 * 
	 * @return the global gain
	 */
	public final float getGlobalGain() {
		return this.globalGain;
	}
	
	/**
	 * Returns the global lacunarity.
	 * 
	 * @return the global lacunarity
	 */
	public final float getGlobalLacunarity() {
		return this.globalLacunarity;
	}
	
	/**
	 * Returns the global octaves.
	 * 
	 * @return the global octaves
	 */
	public final int getGlobalOctaves() {
		return this.globalOctaves;
	}
	
	/**
	 * Sets the global amplitude.
	 * 
	 * @param globalAmplitude the new global amplitude
	 */
	public final void setGlobalAmplitude(final float globalAmplitude) {
		this.globalAmplitude = globalAmplitude;
	}
	
	/**
	 * Sets the global frequency.
	 * 
	 * @param globalFrequency the new global frequency
	 */
	public final void setGlobalFrequency(final float globalFrequency) {
		this.globalFrequency = globalFrequency;
	}
	
	/**
	 * Sets the global gain.
	 * 
	 * @param globalGain the new global gain
	 */
	public final void setGlobalGain(final float globalGain) {
		this.globalGain = globalGain;
	}
	
	/**
	 * Sets the global lacunarity.
	 * 
	 * @param globalLacunarity the new global lacunarity
	 */
	public final void setGlobalLacunarity(final float globalLacunarity) {
		this.globalLacunarity = globalLacunarity;
	}
	
	/**
	 * Sets the global octaves.
	 * 
	 * @param globalOctaves the new global octaves
	 */
	public final void setGlobalOctaves(final int globalOctaves) {
		this.globalOctaves = globalOctaves;
	}
	
	/**
	 * Updates all necessary variables in this {@code AbstractKernel} instance.
	 * <p>
	 * This method should not be called by {@code run()}-reachable code, because it uses a {@code ThreadLocalRandom} to update the PRNG.
	 * 
	 * @param resolutionX the resolution along the X-axis
	 * @param resolutionY the resolution along the Y-axis
	 */
	public final void update(final int resolutionX, final int resolutionY) {
		doUpdateResolution(resolutionX, resolutionY);
		doUpdateCosTable();
		doUpdateSinTable();
		doUpdateNoise();
		doUpdateSeed();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	If you're porting back to the original Aparapi, uncomment this code:
//	protected final float asinpi(final float a) {
//		return asin(a) * PI_RECIPROCAL;
//	}
	
	/**
	 * Performs a bilinear interpolation operation on the supplied values.
	 * <p>
	 * Returns the bilinearly interpolated value.
	 * <p>
	 * Calling this method is equivalent to the following
	 * <pre>
	 * {@code
	 * lerp(lerp(value00, value01, tX), lerp(value10, value11, tX), tY)
	 * }
	 * </pre>
	 * 
	 * @param value00 a {@code float} value
	 * @param value01 a {@code float} value
	 * @param value10 a {@code float} value
	 * @param value11 a {@code float} value
	 * @param tX the X-axis factor
	 * @param tY the Y-axis factor
	 * @return the bilinearly interpolated value
	 */
	protected final float blerp(final float value00, final float value01, final float value10, final float value11, final float tX, final float tY) {
		return lerp(lerp(value00, value01, tX), lerp(value10, value11, tX), tY);
	}
	
	/**
	 * Returns a {@code float} value based on four {@code byte}s.
	 * 
	 * @param b0 the first {@code byte}
	 * @param b1 the second {@code byte}
	 * @param b2 the third {@code byte}
	 * @param b3 the fourth {@code byte}
	 * @return a {@code float} value based on four {@code byte}s
	 */
	protected final float bytesToFloat(final byte b0, final byte b1, final byte b2, final byte b3) {
		final int bits = ((b0 & 0xFF) << 0) | ((b1 & 0xFF) << 8) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 24);
		
		final int s = (bits >> 31) == 0 ? 1 : -1;
		final int e = (bits >> 23) & 0xFF;
		final int m = e == 0 ? (bits & 0x7FFFFF) << 1 : (bits & 0x7FFFFF) | 0x800000;
		
		return s * m * pow(2.0F, e - 150.0F);
	}
	
	/**
	 * Returns the cosine of the angle {@code degrees}.
	 * <p>
	 * The angle {@code degrees} must be in degrees.
	 * 
	 * @param degrees an angle in degrees
	 * @return the cosine of the angle {@code degrees}
	 */
	protected final float cosDegrees(final float degrees) {
		return this.cosTable[(int)(degrees * COS_TABLE_AND_SIN_TABLE_DEGREES_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK];
	}
	
	/**
	 * Returns the cosine of the angle {@code radians}.
	 * <p>
	 * The angle {@code radians} must be in radians.
	 * 
	 * @param radians an angle in radians
	 * @return the cosine of the angle {@code radians}
	 */
	protected final float cosRadians(final float radians) {
		return this.cosTable[(int)(radians * COS_TABLE_AND_SIN_TABLE_RADIANS_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK];
	}
	
	/**
	 * Performs a linear interpolation operation on the supplied values.
	 * <p>
	 * Returns the linearly interpolated value.
	 * 
	 * @param value0 a {@code float} value
	 * @param value1 a {@code float} value
	 * @param t the factor
	 * @return the linearly interpolated value
	 */
	@SuppressWarnings("static-method")
	protected final float lerp(final float value0, final float value1, final float t) {
		return (1.0F - t) * value0 + t * value1;
	}
	
	/**
	 * Returns the maximum value of {@code a}, {@code b} and {@code c}.
	 * 
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @param c a {@code float} value
	 * @return the maximum value of {@code a}, {@code b} and {@code c}
	 */
	protected final float max(final float a, final float b, final float c) {
		return max(max(a, b), c);
	}
	
	/**
	 * Returns the minimum value of {@code a}, {@code b} and {@code c}.
	 * 
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @param c a {@code float} value
	 * @return the minimum value of {@code a}, {@code b} and {@code c}
	 */
	protected final float min(final float a, final float b, final float c) {
		return min(min(a, b), c);
	}
	
	/**
	 * Returns the modulo of {@code value}.
	 * <p>
	 * This operation is equivalent to {@code value - floor(value)}.
	 * 
	 * @param value the value to perform the modulo operation on
	 * @return the modulo of {@code value}
	 */
	protected final float modulo(final float value) {
		return value - floor(value);
	}
	
	/**
	 * Returns the next pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and {@code 1.0} from the random number generator's sequence.
	 * <p>
	 * This method is intended to be used by {@code run()}-reachable code.
	 * 
	 * @return the next pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and {@code 1.0} from the random number generator's sequence
	 */
	protected final float nextFloat() {
		return doNext(24) * PRNG_NEXT_FLOAT_RECIPROCAL;
	}
	
	/**
	 * Returns a {@code float} with noise computed by the Perlin algorithm using the coordinates X, Y and Z.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return a {@code float} with noise computed by the Perlin algorithm using the coordinates X, Y and Z
	 */
	protected final float perlinNoiseXYZ(final float x, final float y, final float z) {
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
		final int a0 = this.noisePermutations[x0] + y0;
		final int a1 = this.noisePermutations[a0] + z0;
		final int a2 = this.noisePermutations[a0 + 1] + z0;
		final int b0 = this.noisePermutations[x0 + 1] + y0;
		final int b1 = this.noisePermutations[b0] + z0;
		final int b2 = this.noisePermutations[b0 + 1] + z0;
		final int hash0 = this.noisePermutations[a1] & 15;
		final int hash1 = this.noisePermutations[b1] & 15;
		final int hash2 = this.noisePermutations[a2] & 15;
		final int hash3 = this.noisePermutations[b2] & 15;
		final int hash4 = this.noisePermutations[a1 + 1] & 15;
		final int hash5 = this.noisePermutations[b1 + 1] & 15;
		final int hash6 = this.noisePermutations[a2 + 1] & 15;
		final int hash7 = this.noisePermutations[b2 + 1] & 15;
		
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
	
	/**
	 * Returns the remainder of {@code x} and {@code y}.
	 * 
	 * @param x the left hand side of the remainder operation
	 * @param y the right hand side of the remainder operation
	 * @return the remainder of {@code x} and {@code y}
	 */
	@SuppressWarnings("static-method")
	protected final float remainder(final float x, final float y) {
		final int n = (int)(x / y);
		
		return x - n * y;
	}
	
	/**
	 * Performs saturation arithmetic on {@code value}.
	 * <p>
	 * Returns {@code value} if, and only if, {@code min(a, b) <= value <= max(a, b)}, otherwise either {@code a} or {@code b}.
	 * <p>
	 * If {@code value} is less than {@code min(a, b)}, then {@code min(a, b)} will be returned.
	 * <p>
	 * If {@code value} is greater than {@code max(a, b)}, then {@code max(a, b)} will be returned.
	 * 
	 * @param value a {@code float} value
	 * @param a either the minimum or the maximum value
	 * @param b either the minimum or the maximum value
	 * @return {@code value} if, and only if, {@code min(a, b) <= value <= max(a, b)}, otherwise either {@code a} or {@code b}
	 */
	protected final float saturate(final float value, final float a, final float b) {
		final float minimumValue = min(a, b);
		final float maximumValue = max(a, b);
		
		return value < minimumValue ? minimumValue : value > maximumValue ? maximumValue : value;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X and Y.
	 * 
	 * @param amplitude the amplitude to start at
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param lacunarity the frequency multiplier
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X and Y
	 */
	protected final float simplexFractalXY(final float amplitude, final float frequency, final float gain, final float lacunarity, final int octaves, final float x, final float y) {
		float result = 0.0F;
		
		float currentAmplitude = amplitude;
		float currentFrequency = frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += currentAmplitude * simplexNoiseXY(x * currentFrequency, y * currentFrequency);
			
			currentAmplitude *= gain;
			currentFrequency *= lacunarity;
		}
		
		return result;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X, Y and Z.
	 * 
	 * @param amplitude the amplitude to start at
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param lacunarity the frequency multiplier
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X, Y and Z
	 */
	protected final float simplexFractalXYZ(final float amplitude, final float frequency, final float gain, final float lacunarity, final int octaves, final float x, final float y, final float z) {
		float result = 0.0F;
		
		float currentAmplitude = amplitude;
		float currentFrequency = frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += currentAmplitude * simplexNoiseXYZ(x * currentFrequency, y * currentFrequency, z * currentFrequency);
			
			currentAmplitude *= gain;
			currentFrequency *= lacunarity;
		}
		
		return result;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X, Y, Z and W.
	 * 
	 * @param amplitude the amplitude to start at
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param lacunarity the frequency multiplier
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @param w the W-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractal algorithm using the coordinates X, Y, Z and W
	 */
	protected final float simplexFractalXYZW(final float amplitude, final float frequency, final float gain, final float lacunarity, final int octaves, final float x, final float y, final float z, final float w) {
		float result = 0.0F;
		
		float currentAmplitude = amplitude;
		float currentFrequency = frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += currentAmplitude * simplexNoiseXYZW(x * currentFrequency, y * currentFrequency, z * currentFrequency, w * currentFrequency);
			
			currentAmplitude *= gain;
			currentFrequency *= lacunarity;
		}
		
		return result;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X and Y.
	 * 
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param minimum the minimum value to return
	 * @param maximum the maximum value to return
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X and Y
	 */
	protected final float simplexFractionalBrownianMotionXY(final float frequency, final float gain, final float minimum, final float maximum, final int octaves, final float x, final float y) {
		float currentAmplitude = 1.0F;
		float maximumAmplitude = 0.0F;
		
		float currentFrequency = frequency;
		
		float noise = 0.0F;
		
		for(int i = 0; i < octaves; i++) {
			noise += simplexNoiseXY(x * currentFrequency, y * currentFrequency) * currentAmplitude;
			
			maximumAmplitude += currentAmplitude;
			currentAmplitude *= gain;
			
			currentFrequency *= 2.0F;
		}
		
		noise /= maximumAmplitude;
		noise = noise * (maximum - minimum) / 2.0F + (maximum + minimum) / 2.0F;
		
		return noise;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X, Y and Z.
	 * 
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param minimum the minimum value to return
	 * @param maximum the maximum value to return
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X, Y and Z
	 */
	protected final float simplexFractionalBrownianMotionXYZ(final float frequency, final float gain, final float minimum, final float maximum, final int octaves, final float x, final float y, final float z) {
		float currentAmplitude = 1.0F;
		float maximumAmplitude = 0.0F;
		
		float currentFrequency = frequency;
		
		float noise = 0.0F;
		
		for(int i = 0; i < octaves; i++) {
			noise += simplexNoiseXYZ(x * currentFrequency, y * currentFrequency, z * currentFrequency) * currentAmplitude;
			
			maximumAmplitude += currentAmplitude;
			currentAmplitude *= gain;
			
			currentFrequency *= 2.0F;
		}
		
		noise /= maximumAmplitude;
		noise = noise * (maximum - minimum) / 2.0F + (maximum + minimum) / 2.0F;
		
		return noise;
	}
	
	/**
	 * Returns a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X, Y, Z and W.
	 * 
	 * @param frequency the frequency to start at
	 * @param gain the amplitude multiplier
	 * @param minimum the minimum value to return
	 * @param maximum the maximum value to return
	 * @param octaves the number of iterations to perform
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @param w the W-coordinate
	 * @return a {@code float} with noise computed by a Simplex-based fractional Brownian motion (fBm) algorithm using the coordinates X, Y, Z and W
	 */
	protected final float simplexFractionalBrownianMotionXYZW(final float frequency, final float gain, final float minimum, final float maximum, final int octaves, final float x, final float y, final float z, final float w) {
		float currentAmplitude = 1.0F;
		float maximumAmplitude = 0.0F;
		
		float currentFrequency = frequency;
		
		float noise = 0.0F;
		
		for(int i = 0; i < octaves; i++) {
			noise += simplexNoiseXYZW(x * currentFrequency, y * currentFrequency, z * currentFrequency, w * currentFrequency) * currentAmplitude;
			
			maximumAmplitude += currentAmplitude;
			currentAmplitude *= gain;
			
			currentFrequency *= 2.0F;
		}
		
		noise /= maximumAmplitude;
		noise = noise * (maximum - minimum) / 2.0F + (maximum + minimum) / 2.0F;
		
		return noise;
	}
	
	/**
	 * Returns a {@code float} with noise computed by the Simplex algorithm using the coordinates X and Y.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @return a {@code float} with noise computed by the Simplex algorithm using the coordinates X and Y
	 */
	protected final float simplexNoiseXY(final float x, final float y) {
		final float s = (x + y) * SIMPLEX_F2;
		
		final int i = doFastFloor(x + s);
		final int j = doFastFloor(y + s);
		
		final float t = (i + j) * SIMPLEX_G2;
		
		final float x0 = x - (i - t);
		final float y0 = y - (j - t);
		
		final int i1 = x0 > y0 ? 1 : 0;
		final int j1 = x0 > y0 ? 0 : 1;
		
		final float x1 = x0 - i1 + SIMPLEX_G2;
		final float y1 = y0 - j1 + SIMPLEX_G2;
		final float x2 = x0 - 1.0F + 2.0F * SIMPLEX_G2;
		final float y2 = y0 - 1.0F + 2.0F * SIMPLEX_G2;
		
		final int ii = i & 0xFF;
		final int jj = j & 0xFF;
		
		final int gi0 = this.noisePermutationsModulo12[ii + this.noisePermutations[jj]];
		final int gi1 = this.noisePermutationsModulo12[ii + i1 + this.noisePermutations[jj + j1]];
		final int gi2 = this.noisePermutationsModulo12[ii + 1 + this.noisePermutations[jj + 1]];
		
		final float t0 = 0.5F - x0 * x0 - y0 * y0;
		final float n0 = t0 < 0.0F ? 0.0F : (t0 * t0) * (t0 * t0) * doDotXY(this.simplexGradient3[gi0 * 3 + 0], this.simplexGradient3[gi0 * 3 + 1], x0, y0);
		
		final float t1 = 0.5F - x1 * x1 - y1 * y1;
		final float n1 = t1 < 0.0F ? 0.0F : (t1 * t1) * (t1 * t1) * doDotXY(this.simplexGradient3[gi1 * 3 + 0], this.simplexGradient3[gi1 * 3 + 1], x1, y1);
		
		final float t2 = 0.5F - x2 * x2 - y2 * y2;
		final float n2 = t2 < 0.0F ? 0.0F : (t2 * t2) * (t2 * t2) * doDotXY(this.simplexGradient3[gi2 * 3 + 0], this.simplexGradient3[gi2 * 3 + 1], x2, y2);
		
		return 70.0F * (n0 + n1 + n2);
	}
	
	/**
	 * Returns a {@code float} with noise computed by the Simplex algorithm using the coordinates X, Y and Z.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @return a {@code float} with noise computed by the Simplex algorithm using the coordinates X, Y and Z
	 */
	protected final float simplexNoiseXYZ(final float x, final float y, final float z) {
		final float s = (x + y + z) * SIMPLEX_F3;
		
		final int i = doFastFloor(x + s);
		final int j = doFastFloor(y + s);
		final int k = doFastFloor(z + s);
		
		final float t = (i + j + k) * SIMPLEX_G3;
		
		final float x0 = x - (i - t);
		final float y0 = y - (j - t);
		final float z0 = z - (k - t);
		
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		
		if(x0 >= y0) {
			if(y0 >= z0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 1;
				k2 = 0;
			} else if(x0 >= z0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			} else {
				i1 = 0;
				j1 = 0;
				k1 = 1;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			}
		} else {
			if(y0 < z0) {
				i1 = 0;
				j1 = 0;
				k1 = 1;
				i2 = 0;
				j2 = 1;
				k2 = 1;
			} else if(x0 < z0) {
				i1 = 0;
				j1 = 1;
				k1 = 0;
				i2 = 0;
				j2 = 1;
				k2 = 1;
			} else {
				i1 = 0;
				j1 = 1;
				k1 = 0;
				i2 = 1;
				j2 = 1;
				k2 = 0;
			}
		}
		
		final float x1 = x0 - i1 + SIMPLEX_G3;
		final float y1 = y0 - j1 + SIMPLEX_G3;
		final float z1 = z0 - k1 + SIMPLEX_G3;
		final float x2 = x0 - i2 + 2.0F * SIMPLEX_G3;
		final float y2 = y0 - j2 + 2.0F * SIMPLEX_G3;
		final float z2 = z0 - k2 + 2.0F * SIMPLEX_G3;
		final float x3 = x0 - 1.0F + 3.0F * SIMPLEX_G3;
		final float y3 = y0 - 1.0F + 3.0F * SIMPLEX_G3;
		final float z3 = z0 - 1.0F + 3.0F * SIMPLEX_G3;
		
		final int ii = i & 0xFF;
		final int jj = j & 0xFF;
		final int kk = k & 0xFF;
		
		final int gi0 = this.noisePermutationsModulo12[ii + this.noisePermutations[jj + this.noisePermutations[kk]]];
		final int gi1 = this.noisePermutationsModulo12[ii + i1 + this.noisePermutations[jj + j1 + this.noisePermutations[kk + k1]]];
		final int gi2 = this.noisePermutationsModulo12[ii + i2 + this.noisePermutations[jj + j2 + this.noisePermutations[kk + k2]]];
		final int gi3 = this.noisePermutationsModulo12[ii + 1 + this.noisePermutations[jj + 1 + this.noisePermutations[kk + 1]]];
		
		final float t0 = 0.6F - x0 * x0 - y0 * y0 - z0 * z0;
		final float n0 = t0 < 0.0F ? 0.0F : (t0 * t0) * (t0 * t0) * doDotXYZ(this.simplexGradient3[gi0 * 3 + 0], this.simplexGradient3[gi0 * 3 + 1], this.simplexGradient3[gi0 * 3 + 2], x0, y0, z0);
		
		final float t1 = 0.6F - x1 * x1 - y1 * y1 - z1 * z1;
		final float n1 = t1 < 0.0F ? 0.0F : (t1 * t1) * (t1 * t1) * doDotXYZ(this.simplexGradient3[gi1 * 3 + 0], this.simplexGradient3[gi1 * 3 + 1], this.simplexGradient3[gi1 * 3 + 2], x1, y1, z1);
		
		final float t2 = 0.6F - x2 * x2 - y2 * y2 - z2 * z2;
		final float n2 = t2 < 0.0F ? 0.0F : (t2 * t2) * (t2 * t2) * doDotXYZ(this.simplexGradient3[gi2 * 3 + 0], this.simplexGradient3[gi2 * 3 + 1], this.simplexGradient3[gi2 * 3 + 2], x2, y2, z2);
		
		final float t3 = 0.6F - x3 * x3 - y3 * y3 - z3 * z3;
		final float n3 = t3 < 0.0F ? 0.0F : (t3 * t3) * (t3 * t3) * doDotXYZ(this.simplexGradient3[gi3 * 3 + 0], this.simplexGradient3[gi3 * 3 + 1], this.simplexGradient3[gi3 * 3 + 2], x3, y3, z3);
		
		return 32.0F * (n0 + n1 + n2 + n3);
	}
	
	/**
	 * Returns a {@code float} with noise computed by the Simplex algorithm using the coordinates X, Y, Z and W.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param z the Z-coordinate
	 * @param w the W-coordinate
	 * @return a {@code float} with noise computed by the Simplex algorithm using the coordinates X, Y, Z and W
	 */
	protected final float simplexNoiseXYZW(final float x, final float y, final float z, final float w) {
		final float s = (x + y + z + w) * SIMPLEX_F4;
		
		final int i = doFastFloor(x + s);
		final int j = doFastFloor(y + s);
		final int k = doFastFloor(z + s);
		final int l = doFastFloor(w + s);
		
		final float t = (i + j + k + l) * SIMPLEX_G4;
		
		final float x0 = x - (i - t);
		final float y0 = y - (j - t);
		final float z0 = z - (k - t);
		final float w0 = w - (l - t);
		
		int rankX = 0;
		int rankY = 0;
		int rankZ = 0;
		int rankW = 0;
		
		if(x0 > y0) {
			rankX++;
		} else {
			rankY++;
		}
		
		if(x0 > z0) {
			rankX++;
		} else {
			rankZ++;
		}
		
		if(x0 > w0) {
			rankX++;
		} else {
			rankW++;
		}
		
		if(y0 > z0) {
			rankY++;
		} else {
			rankZ++;
		}
		
		if(y0 > w0) {
			rankY++;
		} else {
			rankW++;
		}
		
		if(z0 > w0) {
			rankZ++;
		} else {
			rankW++;
		}
		
		final int i1 = rankX >= 3 ? 1 : 0;
		final int j1 = rankY >= 3 ? 1 : 0;
		final int k1 = rankZ >= 3 ? 1 : 0;
		final int l1 = rankW >= 3 ? 1 : 0;
		final int i2 = rankX >= 2 ? 1 : 0;
		final int j2 = rankY >= 2 ? 1 : 0;
		final int k2 = rankZ >= 2 ? 1 : 0;
		final int l2 = rankW >= 2 ? 1 : 0;
		final int i3 = rankX >= 1 ? 1 : 0;
		final int j3 = rankY >= 1 ? 1 : 0;
		final int k3 = rankZ >= 1 ? 1 : 0;
		final int l3 = rankW >= 1 ? 1 : 0;
		
		final float x1 = x0 - i1 + SIMPLEX_G4;
		final float y1 = y0 - j1 + SIMPLEX_G4;
		final float z1 = z0 - k1 + SIMPLEX_G4;
		final float w1 = w0 - l1 + SIMPLEX_G4;
		final float x2 = x0 - i2 + 2.0F * SIMPLEX_G4;
		final float y2 = y0 - j2 + 2.0F * SIMPLEX_G4;
		final float z2 = z0 - k2 + 2.0F * SIMPLEX_G4;
		final float w2 = w0 - l2 + 2.0F * SIMPLEX_G4;
		final float x3 = x0 - i3 + 3.0F * SIMPLEX_G4;
		final float y3 = y0 - j3 + 3.0F * SIMPLEX_G4;
		final float z3 = z0 - k3 + 3.0F * SIMPLEX_G4;
		final float w3 = w0 - l3 + 3.0F * SIMPLEX_G4;
		final float x4 = x0 - 1.0F + 4.0F * SIMPLEX_G4;
		final float y4 = y0 - 1.0F + 4.0F * SIMPLEX_G4;
		final float z4 = z0 - 1.0F + 4.0F * SIMPLEX_G4;
		final float w4 = w0 - 1.0F + 4.0F * SIMPLEX_G4;
		
		final int ii = i & 0xFF;
		final int jj = j & 0xFF;
		final int kk = k & 0xFF;
		final int ll = l & 0xFF;
		
		final int gi0 = this.noisePermutations[ii + this.noisePermutations[jj + this.noisePermutations[kk + this.noisePermutations[ll]]]] % 32;
		final int gi1 = this.noisePermutations[ii + i1 + this.noisePermutations[jj + j1 + this.noisePermutations[kk + k1 + this.noisePermutations[ll + l1]]]] % 32;
		final int gi2 = this.noisePermutations[ii + i2 + this.noisePermutations[jj + j2 + this.noisePermutations[kk + k2 + this.noisePermutations[ll + l2]]]] % 32;
		final int gi3 = this.noisePermutations[ii + i3 + this.noisePermutations[jj + j3 + this.noisePermutations[kk + k3 + this.noisePermutations[ll + l3]]]] % 32;
		final int gi4 = this.noisePermutations[ii + 1 + this.noisePermutations[jj + 1 + this.noisePermutations[kk + 1 + this.noisePermutations[ll + 1]]]] % 32;
		
		final float t0 = 0.6F - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
		final float n0 = t0 < 0.0F ? 0.0F : (t0 * t0) * (t0 * t0) * doDotXYZW(this.simplexGradient4[gi0 * 4 + 0], this.simplexGradient4[gi0 * 4 + 1], this.simplexGradient4[gi0 * 4 + 2], this.simplexGradient4[gi0 * 4 + 3], x0, y0, z0, w0);
		
		final float t1 = 0.6F - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
		final float n1 = t1 < 0.0F ? 0.0F : (t1 * t1) * (t1 * t1) * doDotXYZW(this.simplexGradient4[gi1 * 4 + 0], this.simplexGradient4[gi1 * 4 + 1], this.simplexGradient4[gi1 * 4 + 2], this.simplexGradient4[gi1 * 4 + 3], x1, y1, z1, w1);
		
		final float t2 = 0.6F - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
		final float n2 = t2 < 0.0F ? 0.0F : (t2 * t2) * (t2 * t2) * doDotXYZW(this.simplexGradient4[gi2 * 4 + 0], this.simplexGradient4[gi2 * 4 + 1], this.simplexGradient4[gi2 * 4 + 2], this.simplexGradient4[gi2 * 4 + 3], x2, y2, z2, w2);
		
		final float t3 = 0.6F - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
		final float n3 = t3 < 0.0F ? 0.0F : (t3 * t3) * (t3 * t3) * doDotXYZW(this.simplexGradient4[gi3 * 4 + 0], this.simplexGradient4[gi3 * 4 + 1], this.simplexGradient4[gi3 * 4 + 2], this.simplexGradient4[gi3 * 4 + 3], x3, y3, z3, w3);
		
		final float t4 = 0.6F - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
		final float n4 = t4 < 0.0F ? 0.0F : (t4 * t4) * (t4 * t4) * doDotXYZW(this.simplexGradient4[gi4 * 4 + 0], this.simplexGradient4[gi4 * 4 + 1], this.simplexGradient4[gi4 * 4 + 2], this.simplexGradient4[gi4 * 4 + 3], x4, y4, z4, w4);
		
		return 27.0F * (n0 + n1 + n2 + n3 + n4);
	}
	
	/**
	 * Returns the sine of the angle {@code degrees}.
	 * <p>
	 * The angle {@code degrees} must be in degrees.
	 * 
	 * @param degrees an angle in degrees
	 * @return the sine of the angle {@code degrees}
	 */
	protected final float sinDegrees(final float degrees) {
		return this.sinTable[(int)(degrees * COS_TABLE_AND_SIN_TABLE_DEGREES_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK];
	}
	
	/**
	 * Returns the sine of the angle {@code radians}.
	 * <p>
	 * The angle {@code radians} must be in radians.
	 * 
	 * @param radians an angle in radians
	 * @return the sine of the angle {@code radians}
	 */
	protected final float sinRadians(final float radians) {
		return this.sinTable[(int)(radians * COS_TABLE_AND_SIN_TABLE_RADIANS_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK];
	}
	
	/**
	 * Performs a smoothstep operation on {@code value} and the edges {@code edgeA} and {@code edgeB}.
	 * <p>
	 * Returns a {@code float} value.
	 * 
	 * @param value a {@code float} value
	 * @param edgeA one of the edges
	 * @param edgeB one of the edges
	 * @return a {@code float} value
	 */
	protected final float smoothstep(final float value, final float edgeA, final float edgeB) {
		final float minimumEdge = min(edgeA, edgeB);
		final float maximumEdge = max(edgeA, edgeB);
		
		final float x = saturate((value - minimumEdge) / (maximumEdge - minimumEdge), 0.0F, 1.0F);
		
		return x * x * (3.0F - 2.0F * x);
	}
	
	/**
	 * Returns the resolution along the X-axis.
	 * <p>
	 * This is also known as the width.
	 * 
	 * @return the resolution along the X-axis
	 */
	protected final int getResolutionX() {
		return this.resolutionX;
	}
	
	/**
	 * Returns the resolution along the Y-axis.
	 * <p>
	 * This is also known as the height.
	 * 
	 * @return the resolution along the Y-axis
	 */
	protected final int getResolutionY() {
		return this.resolutionY;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int doNext(final int bits) {
		final int index = getGlobalId();
		
		final long oldSeed = this.seeds[index];
		final long newSeed = (oldSeed * PRNG_MULTIPLIER + PRNG_ADDEND) & PRNG_MASK;
		
		this.seeds[index] = newSeed;
		
		return (int)(newSeed >>> (48 - bits));
	}
	
	private void doUpdateCosTable() {
		this.cosTable = new float[COS_TABLE_AND_SIN_TABLE_COUNT];
		
		for(int i = 0; i < COS_TABLE_AND_SIN_TABLE_COUNT; i++) {
			this.cosTable[i] = MathF.cos((i + 0.5F) / COS_TABLE_AND_SIN_TABLE_COUNT * COS_TABLE_AND_SIN_TABLE_RADIANS_MAXIMUM);
		}
		
		for(int i = 0; i < COS_TABLE_AND_SIN_TABLE_DEGREES_MAXIMUM; i += 90) {
			this.cosTable[(int)(i * COS_TABLE_AND_SIN_TABLE_DEGREES_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK] = MathF.cos(i * COS_TABLE_AND_SIN_TABLE_RADIANS);
		}
		
		put(this.cosTable);
	}
	
	private void doUpdateNoise() {
		this.simplexGradient3 = new float[] {1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, -1.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F};
		this.simplexGradient4 = new float[] {0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, 1.0F, -1.0F, -1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, -1.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 1.0F, 0.0F, -1.0F, -1.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, -1.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, -1.0F, 0.0F, -1.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, -1.0F, 1.0F, -1.0F, 0.0F, 1.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 1.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, -1.0F, -1.0F, 0.0F, 1.0F, -1.0F, -1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, 1.0F, -1.0F, -1.0F, 0.0F, -1.0F, 1.0F, 1.0F, 0.0F, -1.0F, 1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 1.0F, 0.0F, -1.0F, -1.0F, -1.0F, 0.0F};
		
		this.noisePermutations = new int[] {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
		this.noisePermutationsModulo12 = new int[this.noisePermutations.length];
		
		for(int i = 0; i < this.noisePermutationsModulo12.length; i++) {
			this.noisePermutationsModulo12[i] = this.noisePermutations[i] % 12;
		}
		
		put(this.noisePermutations);
		put(this.noisePermutationsModulo12);
		put(this.simplexGradient3);
		put(this.simplexGradient4);
	}
	
	private void doUpdateResolution(final int resolutionX, final int resolutionY) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
	}
	
	private void doUpdateSeed() {
		this.seeds = new long[this.resolutionX * this.resolutionY];
		
		for(int i = 0; i < this.seeds.length; i++) {
			this.seeds[i] = ThreadLocalRandom.current().nextLong();
		}
		
		put(this.seeds);
	}
	
	private void doUpdateSinTable() {
		this.sinTable = new float[COS_TABLE_AND_SIN_TABLE_COUNT];
		
		for(int i = 0; i < COS_TABLE_AND_SIN_TABLE_COUNT; i++) {
			this.sinTable[i] = MathF.sin((i + 0.5F) / COS_TABLE_AND_SIN_TABLE_COUNT * COS_TABLE_AND_SIN_TABLE_RADIANS_MAXIMUM);
		}
		
		for(int i = 0; i < COS_TABLE_AND_SIN_TABLE_DEGREES_MAXIMUM; i += 90) {
			this.sinTable[(int)(i * COS_TABLE_AND_SIN_TABLE_DEGREES_TO_INDEX) & COS_TABLE_AND_SIN_TABLE_MASK] = MathF.sin(i * COS_TABLE_AND_SIN_TABLE_RADIANS);
		}
		
		put(this.sinTable);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float doDotXY(final float x0, final float y0, final float x1, final float y1) {
		return x0 * x1 + y0 * y1;
	}
	
	private static float doDotXYZ(final float x0, final float y0, final float z0, final float x1, final float y1, final float z1) {
		return x0 * x1 + y0 * y1 + z0 * z1;
	}
	
	private static float doDotXYZW(final float x0, final float y0, final float z0, final float w0, final float x1, final float y1, final float z1, final float w1) {
		return x0 * x1 + y0 * y1 + z0 * z1 + w0 * w1;
	}
	
	private static int doFastFloor(final float value) {
		final int i = (int)(value);
		
		return value < i ? i - 1 : i;
	}
}