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
package org.dayflower.pathtracer.math;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.concurrent.ThreadLocalRandom;

//TODO: Add Javadocs.
public final class Math2 {
//	TODO: Add Javadocs!
	public static final float EPSILON = 0.01F;
	
//	TODO: Add Javadocs!
	public static final float PI = toFloat(Math.PI);
	
//	TODO: Add Javadocs!
	public static final float PI_DIVIDED_BY_FOUR = PI / 4.0F;
	
//	TODO: Add Javadocs!
	public static final float PI_DIVIDED_BY_TWO = PI / 2.0F;
	
//	TODO: Add Javadocs!
	public static final float PI_MULTIPLIED_BY_TWO = PI * 2.0F;
	
//	TODO: Add Javadocs!
	public static final float PI_RECIPROCAL = 1.0F / PI;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Math2() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public static boolean equals(final double a, final double b) {
		return Double.compare(a, b) == 0;
	}
	
//	TODO: Add Javadocs!
	public static boolean equals(final float a, final float b) {
		return Float.compare(a, b) == 0;
	}
	
//	TODO: Add Javadocs!
	public static double abs(final double value) {
		return Math.abs(value);
	}
	
//	TODO: Add Javadocs!
	public static double acos(final double value) {
		return Math.acos(value);
	}
	
//	TODO: Add Javadocs!
	public static double asin(final double value) {
		return Math.asin(value);
	}
	
//	TODO: Add Javadocs!
	public static double atan(final double value) {
		return Math.atan(value);
	}
	
//	TODO: Add Javadocs!
	public static double atan2(final double y, final double x) {
		return Math.atan2(y, x);
	}
	
//	TODO: Add Javadocs!
	public static double cos(final double angle) {
		return Math.cos(angle);
	}
	
//	TODO: Add Javadocs!
	public static double exp(final double exponent) {
		return Math.exp(exponent);
	}
	
//	TODO: Add Javadocs!
	public static double floor(final double value) {
		return Math.floor(value);
	}
	
//	TODO: Add Javadocs!
	public static double max(final double a, final double b) {
		return Math.max(a, b);
	}
	
//	TODO: Add Javadocs!
	public static double min(final double a, final double b) {
		return Math.min(a, b);
	}
	
//	TODO: Add Javadocs!
	public static double modulo(final double value) {
		return value - floor(value);
	}
	
//	TODO: Add Javadocs!
	public static double pow(final double base, final double exponent) {
		return Math.pow(base, exponent);
	}
	
//	TODO: Add Javadocs!
	public static double saturate(final double value) {
		return saturate(value, 0.0D, 1.0D);
	}
	
//	TODO: Add Javadocs!
	public static double saturate(final double value, final double minimum, final double maximum) {
		return max(min(value, maximum), minimum);
	}
	
//	TODO: Add Javadocs!
	public static double sin(final double angle) {
		return Math.sin(angle);
	}
	
//	TODO: Add Javadocs!
	public static double sqrt(final double value) {
		return Math.sqrt(value);
	}
	
//	TODO: Add Javadocs!
	public static double tan(final double angle) {
		return Math.tan(angle);
	}
	
//	TODO: Add Javadocs.
	public static double toDegrees(final double radians) {
		return Math.toDegrees(radians);
	}
	
//	TODO: Add Javadocs!
	public static double toRadians(final double angleInDegrees) {
		return Math.toRadians(angleInDegrees);
	}
	
//	TODO: Add Javadocs!
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
	
//	TODO: Add Javadocs!
	public static float abs(final float value) {
		return Math.abs(value);
	}
	
//	TODO: Add Javadocs!
	public static float acos(final float value) {
		return toFloat(Math.acos(value));
	}
	
//	TODO: Add Javadocs!
	public static float asin(final float value) {
		return toFloat(Math.asin(value));
	}
	
//	TODO: Add Javadocs!
	public static float atan(final float value) {
		return toFloat(Math.atan(value));
	}
	
//	TODO: Add Javadocs!
	public static float atan2(final float y, final float x) {
		return toFloat(Math.atan2(y, x));
	}
	
//	TODO: Add Javadocs!
	public static float cos(final float angle) {
		return toFloat(Math.cos(angle));
	}
	
//	TODO: Add Javadocs!
	public static float exp(final float exponent) {
		return toFloat(Math.exp(exponent));
	}
	
//	TODO: Add Javadocs!
	public static float floor(final float value) {
		return toFloat(Math.floor(value));
	}
	
//	TODO: Add Javadocs!
	public static float max(final float a, final float b) {
		return Math.max(a, b);
	}
	
//	TODO: Add Javadocs!
	public static float min(final float a, final float b) {
		return Math.min(a, b);
	}
	
//	TODO: Add Javadocs!
	public static float modulo(final float value) {
		return value - floor(value);
	}
	
//	TODO: Add Javadocs!
	public static float nextFloat() {
		return ThreadLocalRandom.current().nextFloat();
	}
	
//	TODO: Add Javadocs!
	public static float pow(final float base, final float exponent) {
		return toFloat(Math.pow(base, exponent));
	}
	
//	TODO: Add Javadocs!
	public static float saturate(final float value) {
		return saturate(value, 0.0F, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public static float saturate(final float value, final float minimum, final float maximum) {
		return max(min(value, maximum), minimum);
	}
	
//	TODO: Add Javadocs!
	public static float sin(final float angle) {
		return toFloat(Math.sin(angle));
	}
	
//	TODO: Add Javadocs!
	public static float sqrt(final float value) {
		return toFloat(Math.sqrt(value));
	}
	
//	TODO: Add Javadocs!
	public static float tan(final float angle) {
		return toFloat(Math.tan(angle));
	}
	
//	TODO: Add Javadocs.
	public static float toDegrees(final float radians) {
		return toFloat(Math.toDegrees(radians));
	}
	
//	TODO: Add Javadocs!
	public static float toFloat(final double value) {
		return (float)(value);
	}
	
//	TODO: Add Javadocs!
	public static float toRadians(final float angleInDegrees) {
		return toFloat(Math.toRadians(angleInDegrees));
	}
	
//	TODO: Add Javadocs!
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
	
//	TODO: Add Javadocs!
	public static int max(final int a, final int b) {
		return Math.max(a, b);
	}
	
//	TODO: Add Javadocs!
	public static int min(final int a, final int b) {
		return Math.min(a, b);
	}
	
//	TODO: Add Javadocs!
	public static int saturate(final int value) {
		return saturate(value, 0, 255);
	}
	
//	TODO: Add Javadocs!
	public static int saturate(final int value, final int minimum, final int maximum) {
		return max(min(value, maximum), minimum);
	}
}