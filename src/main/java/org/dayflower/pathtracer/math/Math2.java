/**
 * Copyright 2009 - 2018 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.math;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that consists exclusively of static methods and constants for various mathematical operations.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Math2 {
	/**
	 * An epsilon value.
	 */
	public static final float EPSILON = 0.01F;
	
	/**
	 * A {@code float} representation of pi.
	 */
	public static final float PI = toFloat(Math.PI);
	
	/**
	 * A {@code float} representation of pi divided by {@code 4.0F}.
	 */
	public static final float PI_DIVIDED_BY_FOUR = PI / 4.0F;
	
	/**
	 * A {@code float} representation of pi divided by {@code 2.0F}.
	 */
	public static final float PI_DIVIDED_BY_TWO = PI / 2.0F;
	
	/**
	 * A {@code float} representation of pi multiplied by {@code 2.0F}.
	 */
	public static final float PI_MULTIPLIED_BY_TWO = PI * 2.0F;
	
	/**
	 * A {@code float} representation of the reciprocal of pi.
	 */
	public static final float PI_RECIPROCAL = 1.0F / PI;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Math2() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code a} and {@code b} for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code a} and {@code b} are equal, {@code false} otherwise.
	 * 
	 * @param a a {@code double} value
	 * @param b a {@code double} value
	 * @return {@code true} if, and only if, {@code a} and {@code b} are equal, {@code false} otherwise
	 */
	public static boolean equals(final double a, final double b) {
		return Double.compare(a, b) == 0;
	}
	
	/**
	 * Compares {@code a} and {@code b} for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code a} and {@code b} are equal, {@code false} otherwise.
	 * 
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @return {@code true} if, and only if, {@code a} and {@code b} are equal, {@code false} otherwise
	 */
	public static boolean equals(final float a, final float b) {
		return Float.compare(a, b) == 0;
	}
	
	/**
	 * Returns the absolute version of {@code value}.
	 * 
	 * @param value a {@code double} value
	 * @return the absolute version of {@code value}
	 */
	public static double abs(final double value) {
		return Math.abs(value);
	}
	
	/**
	 * Returns the arc cosine of {@code value}.
	 * 
	 * @param value a {@code double} value
	 * @return the arc cosine of {@code value}
	 */
	public static double acos(final double value) {
		return Math.acos(value);
	}
	
	/**
	 * Returns the arc sine of {@code value}.
	 * 
	 * @param value a {@code double} value
	 * @return the arc sine of {@code value}
	 */
	public static double asin(final double value) {
		return Math.asin(value);
	}
	
	/**
	 * Returns the arc tangent of {@code value}.
	 * 
	 * @param value a {@code double} value
	 * @return the arc tangent of {@code value}
	 */
	public static double atan(final double value) {
		return Math.atan(value);
	}
	
	/**
	 * Returns the theta component of the point (r, theta) in polar coordinates that corresponds to the point (x, y) in Cartesian coordinates.
	 * 
	 * @param y the ordinate coordinate
	 * @param x the abscissa coordinate
	 * @return the theta component of the point (r, theta) in polar coordinates that corresponds to the point (x, y) in Cartesian coordinates
	 */
	public static double atan2(final double y, final double x) {
		return Math.atan2(y, x);
	}
	
	/**
	 * Returns the trigonometric cosine of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric cosine of {@code angle}
	 */
	public static double cos(final double angle) {
		return Math.cos(angle);
	}
	
	/**
	 * Returns Euler's number {@code e} raised to the power of {@code exponent}.
	 * 
	 * @param exponent the exponent to raise {@code e} to
	 * @return Euler's number {@code e} raised to the power of {@code exponent}
	 */
	public static double exp(final double exponent) {
		return Math.exp(exponent);
	}
	
	/**
	 * Returns the largest (closest to positive infinity) {@code double} value that is less than or equal to {@code value} and is equal to a mathematical integer.
	 * 
	 * @param value a value
	 * @return the largest (closest to positive infinity) {@code double} value that is less than or equal to {@code value} and is equal to a mathematical integer
	 */
	public static double floor(final double value) {
		return Math.floor(value);
	}
	
	/**
	 * Returns the greater value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the greater value of {@code a} and {@code b}
	 */
	public static double max(final double a, final double b) {
		return Math.max(a, b);
	}
	
	/**
	 * Returns the smaller value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the smaller value of {@code a} and {@code b}
	 */
	public static double min(final double a, final double b) {
		return Math.min(a, b);
	}
	
	/**
	 * Returns the value that is computed as {@code value - floor(value)}.
	 * 
	 * @param value a value
	 * @return the value that is computed as {@code value - floor(value)}
	 */
	public static double modulo(final double value) {
		return value - floor(value);
	}
	
	/**
	 * Returns a pseudorandom {@code double} value between {@code 0.0D} (inclusive) and {@code 1.0D} (exclusive).
	 * 
	 * @return a pseudorandom {@code double} value between {@code 0.0D} (inclusive) and {@code 1.0D} (exclusive)
	 */
	public static double nextDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	/**
	 * Returns {@code base} raised to the power of {@code exponent}.
	 * 
	 * @param base the base
	 * @param exponent the exponent
	 * @return {@code base} raised to the power of {@code exponent}
	 */
	public static double pow(final double base, final double exponent) {
		return Math.pow(base, exponent);
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * Calling this method is equivalent to calling {@code Math2.saturate(value, 0.0D, 1.0D)}.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static double saturate(final double value) {
		return saturate(value, 0.0D, 1.0D);
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * If {@code value} is less than {@code minimum}, {@code minimum} will be returned. If {@code value} is greater than {@code maximum}, {@code maximum} will be returned. Otherwise {@code value} will be returned.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @param minimum the minimum value
	 * @param maximum the maximum value
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static double saturate(final double value, final double minimum, final double maximum) {
		return max(min(value, maximum), minimum);
	}
	
	/**
	 * Returns the trigonometric sine of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric sine of {@code angle}
	 */
	public static double sin(final double angle) {
		return Math.sin(angle);
	}
	
	/**
	 * Returns the correctly rounded positive square root of {@code value}.
	 * 
	 * @param value a value
	 * @return the correctly rounded positive square root of {@code value}
	 */
	public static double sqrt(final double value) {
		return Math.sqrt(value);
	}
	
	/**
	 * Returns the trigonometric tangent of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric tangent of {@code angle}
	 */
	public static double tan(final double angle) {
		return Math.tan(angle);
	}
	
	/**
	 * Returns an approximately equivalent angle measured in degrees from an angle measured in radians.
	 * 
	 * @param radians an angle, in radians
	 * @return an approximately equivalent angle measured in degrees from an angle measured in radians
	 */
	public static double toDegrees(final double radians) {
		return Math.toDegrees(radians);
	}
	
	/**
	 * Returns an approximately equivalent angle measured in radians from an angle measured in degrees.
	 * 
	 * @param angleInDegrees an angle, in degrees
	 * @return an approximately equivalent angle measured in radians from an angle measured in degrees
	 */
	public static double toRadians(final double angleInDegrees) {
		return Math.toRadians(angleInDegrees);
	}
	
	/**
	 * Attempts to solve the quadratic system based on the values {@code a}, {@code b} and {@code c}.
	 * <p>
	 * Returns a {@code double} array with a length of {@code 2} containing the result.
	 * <p>
	 * If the quadratic system could not be solved, the result will contain the values {@code Double.NaN}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @param c a value
	 * @return a {@code double} array with a length of {@code 2} containing the result
	 */
	public static double[] solveQuadraticSystem(final double a, final double b, final double c) {
		final double[] result = new double[] {Double.NaN, Double.NaN};
		
		final double discriminant = b * b - 4.0D * a * c;
		
		if(discriminant >= 0.0D) {
			final double discriminantSqrt = sqrt(discriminant);
			final double quadratic = -0.5D * (b < 0.0D ? b - discriminantSqrt : b + discriminantSqrt);
			final double result0 = quadratic / a;
			final double result1 = c / quadratic;
			
			result[0] = min(result0, result1);
			result[1] = max(result0, result1);
		}
		
		return result;
	}
	
	/**
	 * Returns the absolute version of {@code value}.
	 * 
	 * @param value a {@code float} value
	 * @return the absolute version of {@code value}
	 */
	public static float abs(final float value) {
		return Math.abs(value);
	}
	
	/**
	 * Returns the arc cosine of {@code value}.
	 * 
	 * @param value a {@code float} value
	 * @return the arc cosine of {@code value}
	 */
	public static float acos(final float value) {
		return toFloat(Math.acos(value));
	}
	
	/**
	 * Returns the arc sine of {@code value}.
	 * 
	 * @param value a {@code float} value
	 * @return the arc sine of {@code value}
	 */
	public static float asin(final float value) {
		return toFloat(Math.asin(value));
	}
	
	/**
	 * Returns the arc tangent of {@code value}.
	 * 
	 * @param value a {@code float} value
	 * @return the arc tangent of {@code value}
	 */
	public static float atan(final float value) {
		return toFloat(Math.atan(value));
	}
	
	/**
	 * Returns the theta component of the point (r, theta) in polar coordinates that corresponds to the point (x, y) in Cartesian coordinates.
	 * 
	 * @param y the ordinate coordinate
	 * @param x the abscissa coordinate
	 * @return the theta component of the point (r, theta) in polar coordinates that corresponds to the point (x, y) in Cartesian coordinates
	 */
	public static float atan2(final float y, final float x) {
		return toFloat(Math.atan2(y, x));
	}
	
	/**
	 * Returns the trigonometric cosine of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric cosine of {@code angle}
	 */
	public static float cos(final float angle) {
		return toFloat(Math.cos(angle));
	}
	
	/**
	 * Returns Euler's number {@code e} raised to the power of {@code exponent}.
	 * 
	 * @param exponent the exponent to raise {@code e} to
	 * @return Euler's number {@code e} raised to the power of {@code exponent}
	 */
	public static float exp(final float exponent) {
		return toFloat(Math.exp(exponent));
	}
	
	/**
	 * Returns the largest (closest to positive infinity) {@code float} value that is less than or equal to {@code value} and is equal to a mathematical integer.
	 * 
	 * @param value a value
	 * @return the largest (closest to positive infinity) {@code float} value that is less than or equal to {@code value} and is equal to a mathematical integer
	 */
	public static float floor(final float value) {
		return toFloat(Math.floor(value));
	}
	
	/**
	 * Returns the greater value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the greater value of {@code a} and {@code b}
	 */
	public static float max(final float a, final float b) {
		return Math.max(a, b);
	}
	
	/**
	 * Returns the smaller value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the smaller value of {@code a} and {@code b}
	 */
	public static float min(final float a, final float b) {
		return Math.min(a, b);
	}
	
	/**
	 * Returns the value that is computed as {@code value - floor(value)}.
	 * 
	 * @param value a value
	 * @return the value that is computed as {@code value - floor(value)}
	 */
	public static float modulo(final float value) {
		return value - floor(value);
	}
	
	/**
	 * Returns a pseudorandom {@code float} value between {@code 0.0F} (inclusive) and {@code 1.0F} (exclusive).
	 * 
	 * @return a pseudorandom {@code float} value between {@code 0.0F} (inclusive) and {@code 1.0F} (exclusive)
	 */
	public static float nextFloat() {
		return ThreadLocalRandom.current().nextFloat();
	}
	
	/**
	 * Returns {@code base} raised to the power of {@code exponent}.
	 * 
	 * @param base the base
	 * @param exponent the exponent
	 * @return {@code base} raised to the power of {@code exponent}
	 */
	public static float pow(final float base, final float exponent) {
		return toFloat(Math.pow(base, exponent));
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * Calling this method is equivalent to calling {@code Math2.saturate(value, 0.0F, 1.0F)}.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static float saturate(final float value) {
		return saturate(value, 0.0F, 1.0F);
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * If {@code value} is less than {@code minimum}, {@code minimum} will be returned. If {@code value} is greater than {@code maximum}, {@code maximum} will be returned. Otherwise {@code value} will be returned.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @param minimum the minimum value
	 * @param maximum the maximum value
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static float saturate(final float value, final float minimum, final float maximum) {
		return max(min(value, maximum), minimum);
	}
	
	/**
	 * Returns the trigonometric sine of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric sine of {@code angle}
	 */
	public static float sin(final float angle) {
		return toFloat(Math.sin(angle));
	}
	
	/**
	 * Returns the correctly rounded positive square root of {@code value}.
	 * 
	 * @param value a value
	 * @return the correctly rounded positive square root of {@code value}
	 */
	public static float sqrt(final float value) {
		return toFloat(Math.sqrt(value));
	}
	
	/**
	 * Returns the trigonometric tangent of {@code angle}.
	 * 
	 * @param angle an angle, in radians
	 * @return the trigonometric tangent of {@code angle}
	 */
	public static float tan(final float angle) {
		return toFloat(Math.tan(angle));
	}
	
	/**
	 * Returns an approximately equivalent angle measured in degrees from an angle measured in radians.
	 * 
	 * @param radians an angle, in radians
	 * @return an approximately equivalent angle measured in degrees from an angle measured in radians
	 */
	public static float toDegrees(final float radians) {
		return toFloat(Math.toDegrees(radians));
	}
	
	/**
	 * Returns a {@code float} representation of a {@code double} value.
	 * 
	 * @param value a {@code double} value
	 * @return a {@code float} representation of a {@code double} value
	 */
	public static float toFloat(final double value) {
		return (float)(value);
	}
	
	/**
	 * Returns an approximately equivalent angle measured in radians from an angle measured in degrees.
	 * 
	 * @param angleInDegrees an angle, in degrees
	 * @return an approximately equivalent angle measured in radians from an angle measured in degrees
	 */
	public static float toRadians(final float angleInDegrees) {
		return toFloat(Math.toRadians(angleInDegrees));
	}
	
	/**
	 * Attempts to solve the quadratic system based on the values {@code a}, {@code b} and {@code c}.
	 * <p>
	 * Returns a {@code float} array with a length of {@code 2} containing the result.
	 * <p>
	 * If the quadratic system could not be solved, the result will contain the values {@code Float.NaN}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @param c a value
	 * @return a {@code double} array with a length of {@code 2} containing the result
	 */
	public static float[] solveQuadraticSystem(final float a, final float b, final float c) {
		final float[] result = new float[] {Float.NaN, Float.NaN};
		
		final float discriminant = b * b - 4.0F * a * c;
		
		if(discriminant >= 0.0F) {
			final float discriminantSqrt = sqrt(discriminant);
			final float quadratic = -0.5F * (b < 0.0F ? b - discriminantSqrt : b + discriminantSqrt);
			final float result0 = quadratic / a;
			final float result1 = c / quadratic;
			
			result[0] = min(result0, result1);
			result[1] = max(result0, result1);
		}
		
		return result;
	}
	
	/**
	 * Returns the absolute version of {@code value}.
	 * 
	 * @param value an {@code int} value
	 * @return the absolute version of {@code value}
	 */
	public static int abs(final int value) {
		return Math.abs(value);
	}
	
	/**
	 * Returns the greater value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the greater value of {@code a} and {@code b}
	 */
	public static int max(final int a, final int b) {
		return Math.max(a, b);
	}
	
	/**
	 * Returns the smaller value of {@code a} and {@code b}.
	 * 
	 * @param a a value
	 * @param b a value
	 * @return the smaller value of {@code a} and {@code b}
	 */
	public static int min(final int a, final int b) {
		return Math.min(a, b);
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * Calling this method is equivalent to calling {@code Math2.saturate(value, 0, 255)}.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static int saturate(final int value) {
		return saturate(value, 0, 255);
	}
	
	/**
	 * Returns a saturated (or clamped) value based on {@code value}.
	 * <p>
	 * If {@code value} is less than {@code minimum}, {@code minimum} will be returned. If {@code value} is greater than {@code maximum}, {@code maximum} will be returned. Otherwise {@code value} will be returned.
	 * 
	 * @param value the value to saturate (or clamp)
	 * @param minimum the minimum value
	 * @param maximum the maximum value
	 * @return a saturated (or clamped) value based on {@code value}
	 */
	public static int saturate(final int value, final int minimum, final int maximum) {
		return max(min(value, maximum), minimum);
	}
}