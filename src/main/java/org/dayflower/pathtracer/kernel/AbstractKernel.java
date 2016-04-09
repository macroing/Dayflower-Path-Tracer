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
package org.dayflower.pathtracer.kernel;

import java.util.concurrent.ThreadLocalRandom;

import org.dayflower.pathtracer.math.Math2;

import com.amd.aparapi.Kernel;

/**
 * An abstract extension of the {@code Kernel} class that adds additional features.
 * <p>
 * The features added are the following:
 * <ul>
 * <li>A random number generator</li>
 * <li>Constants such as {@code PI}</li>
 * <li>Methods that approximates sine and cosine based on tables</li>
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
	
	private static final float DEGREES_MAXIMUM = 360.0F;
	private static final float DEGREES_TO_INDEX = (~(-1 << 12) + 1) / DEGREES_MAXIMUM;
	private static final float NEXT_FLOAT_RECIPROCAL = 1.0F / (1 << 24);
	private static final float RADIANS = PI / 180.0F;
	private static final float RADIANS_MAXIMUM = PI * 2.0F;
	private static final float RADIANS_TO_INDEX = (~(-1 << 12) + 1) / RADIANS_MAXIMUM;
	private static final int BITS = 12;
	private static final int COUNT = ~(-1 << BITS) + 1;
	private static final int MASK_0 = ~(-1 << BITS);
	private static final long ADDEND = 0xBL;
	private static final long MASK_1 = (1L << 48L) - 1L;
	private static final long MULTIPLIER = 0x5DEECE66DL;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The cosine table (array) used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] cosTable = new float[0];
	
	/**
	 * The sine table (array) used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected float[] sinTable = new float[0];
	
	/**
	 * The seed array used by this {@code AbstractKernel} instance.
	 * <p>
	 * It appears that it cannot be private for Aparapi and OpenCL to work.
	 */
	protected long[] seeds = new long[0];
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractKernel} instance.
	 */
	protected AbstractKernel() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the cosine of the angle {@code degrees}.
	 * <p>
	 * The angle {@code degrees} must be in degrees.
	 * 
	 * @param degrees an angle in degrees
	 * @return the cosine of the angle {@code degrees}
	 */
	public final float cosDegrees(final float degrees) {
		return this.cosTable[(int)(degrees * DEGREES_TO_INDEX) & MASK_0];
	}
	
	/**
	 * Returns the cosine of the angle {@code radians}.
	 * <p>
	 * The angle {@code radians} must be in radians.
	 * 
	 * @param radians an angle in radians
	 * @return the cosine of the angle {@code radians}
	 */
	public final float cosRadians(final float radians) {
		return this.cosTable[(int)(radians * RADIANS_TO_INDEX) & MASK_0];
	}
	
	/**
	 * Returns the modulo of {@code value}.
	 * <p>
	 * This operation is equivalent to {@code value - floor(value)}.
	 * 
	 * @param value the value to perform the modulo operation on
	 * @return the modulo of {@code value}
	 */
	public final float modulo(final float value) {
		return value - floor(value);
	}
	
	/**
	 * Returns the next pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and {@code 1.0} from the random number generator's sequence.
	 * <p>
	 * This method is intended to be used by {@code run()}-reachable code.
	 * 
	 * @return the next pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and {@code 1.0} from the random number generator's sequence
	 */
	public final float nextFloat() {
		return doNext(24) * NEXT_FLOAT_RECIPROCAL;
	}
	
	/**
	 * Returns the remainder of {@code x} and {@code y}.
	 * 
	 * @param x the left hand side of the remainder operation
	 * @param y the right hand side of the remainder operation
	 * @return the remainder of {@code x} and {@code y}
	 */
	@SuppressWarnings("static-method")
	public float remainder(final float x, final float y) {
		final int n = (int)(x / y);
		
		return x - n * y;
	}
	
	/**
	 * Returns the sine of the angle {@code degrees}.
	 * <p>
	 * The angle {@code degrees} must be in degrees.
	 * 
	 * @param degrees an angle in degrees
	 * @return the sine of the angle {@code degrees}
	 */
	public final float sinDegrees(final float degrees) {
		return this.sinTable[(int)(degrees * DEGREES_TO_INDEX) & MASK_0];
	}
	
	/**
	 * Returns the sine of the angle {@code radians}.
	 * <p>
	 * The angle {@code radians} must be in radians.
	 * 
	 * @param radians an angle in radians
	 * @return the sine of the angle {@code radians}
	 */
	public final float sinRadians(final float radians) {
		return this.sinTable[(int)(radians * RADIANS_TO_INDEX) & MASK_0];
	}
	
	/**
	 * Updates the cosine- and sine tables used by this {@code AbstractKernel} instance.
	 */
	public final void updateTables() {
		this.cosTable = new float[COUNT];
		this.sinTable = new float[COUNT];
		
		for(int i = 0; i < COUNT; i++) {
			this.cosTable[i] = Math2.cos((i + 0.5F) / COUNT * RADIANS_MAXIMUM);
			this.sinTable[i] = Math2.sin((i + 0.5F) / COUNT * RADIANS_MAXIMUM);
		}
		
		for(int i = 0; i < DEGREES_MAXIMUM; i += 90) {
			this.cosTable[(int)(i * DEGREES_TO_INDEX) & MASK_0] = Math2.cos(i * RADIANS);
			this.sinTable[(int)(i * DEGREES_TO_INDEX) & MASK_0] = Math2.sin(i * RADIANS);
		}
		
		put(this.cosTable);
		put(this.sinTable);
	}
	
	/**
	 * Sets the seed of the random number generator.
	 * <p>
	 * This method should not be called by {@code run()}-reachable code, because it uses a {@code ThreadLocalRandom} to update {@code seed}.
	 * 
	 * @param seed the initial seed
	 * @param length the length of the array holding seeds
	 */
	public final void setSeed(final long seed, final int length) {
		this.seeds = new long[length];
		
		for(int i = 0; i < this.seeds.length; i++) {
			this.seeds[i] = ((seed + ThreadLocalRandom.current().nextLong()) ^ MULTIPLIER) & MASK_1;
		}
		
		put(this.seeds);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int doNext(final int bits) {
		final int index = getGlobalId();
		
		final long oldSeed = this.seeds[index];
		final long newSeed = (oldSeed * MULTIPLIER + ADDEND) & MASK_1;
		
		this.seeds[index] = newSeed;
		
		return (int)(newSeed >>> (48 - bits));
	}
}