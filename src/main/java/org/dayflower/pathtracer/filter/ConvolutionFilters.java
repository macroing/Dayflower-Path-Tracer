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
package org.dayflower.pathtracer.filter;

import static org.dayflower.pathtracer.math.Math2.max;
import static org.dayflower.pathtracer.math.Math2.min;

//TODO: Add Javadocs.
public final class ConvolutionFilters {
	private static final float[][] FILTER_BLUR = new float[][] {new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F}, new float[] {0.0F, 1.0F, 1.0F, 1.0F, 0.0F}, new float[] {1.0F, 1.0F, 1.0F, 1.0F, 1.0F}, new float[] {0.0F, 1.0F, 1.0F, 1.0F, 0.0F}, new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F}};
	private static final float[][] FILTER_DETECT_EDGES = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {-1.0F, 8.0F, -1.0F}, new float[] {-1.0F, -1.0F, -1.0F}};
	private static final float[][] FILTER_EMBOSS = new float[][] {new float[] {-1.0F, -1.0F, 0.0F}, new float[] {-1.0F, 0.0F, 1.0F}, new float[] {0.0F, 1.0F, 1.0F}};
	private static final float[][] FILTER_GRADIENT_HORIZONTAL = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {0.0F, 0.0F, 0.0F}, new float[] {1.0F, 1.0F, 1.0F}};
	private static final float[][] FILTER_GRADIENT_VERTICAL = new float[][] {new float[] {-1.0F, 0.0F, 1.0F}, new float[] {-1.0F, 0.0F, 1.0F}, new float[] {-1.0F, 0.0F, 1.0F}};
	private static final float[][] FILTER_SHARPEN = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {-1.0F, 9.0F, -1.0F}, new float[] {-1.0F, -1.0F, -1.0F}};
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ConvolutionFilters() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static void filterBlur(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 5, 5, FILTER_BLUR, 1.0F / 13.0F, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public static void filterDetectEdges(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 3, 3, FILTER_DETECT_EDGES, 1.0F, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public static void filterEmboss(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 3, 3, FILTER_EMBOSS, 1.0F, 128.0F);
	}
	
//	TODO: Add Javadocs.
	public static void filterGradientHorizontal(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 3, 3, FILTER_GRADIENT_HORIZONTAL, 1.0F, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public static void filterGradientVertical(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 3, 3, FILTER_GRADIENT_VERTICAL, 1.0F, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public static void filterSharpen(final byte[] pixels, final int width, final int height) {
		doFilter(pixels, width, height, 3, 3, FILTER_SHARPEN, 1.0F, 0.0F);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void doFilter(final byte[] pixels, final int width, final int height, final int filterWidth, final int filterHeight, final float[][] filter, final float factor, final float bias) {
		final byte[] result = new byte[pixels.length];
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				float r0 = 0.0F;
				float g0 = 0.0F;
				float b0 = 0.0F;
				
				for(int filterY = 0; filterY < filterHeight; filterY++) {
					for(int filterX = 0; filterX < filterWidth; filterX++) {
						final int imageX = (x - filterWidth / 2 + filterX + width) % width;
						final int imageY = (y - filterHeight / 2 + filterY + height) % height;
						
						final int index = (imageY * width + imageX) * 4;
						
						r0 += (pixels[index + 0] & 0xFF) * filter[filterY][filterX];
						g0 += (pixels[index + 1] & 0xFF) * filter[filterY][filterX];
						b0 += (pixels[index + 2] & 0xFF) * filter[filterY][filterX];
					}
				}
				
				final int index = (y * width + x) * 4;
				
				final int r1 = min(max((int)(factor * r0 + bias), 0), 255);
				final int g1 = min(max((int)(factor * g0 + bias), 0), 255);
				final int b1 = min(max((int)(factor * b0 + bias), 0), 255);
				
				result[index + 0] = (byte)(r1);
				result[index + 1] = (byte)(g1);
				result[index + 2] = (byte)(b1);
			}
		}
		
		for(int i = 0; i < pixels.length; i += 4) {
			pixels[i + 0] = result[i + 0];
			pixels[i + 1] = result[i + 1];
			pixels[i + 2] = result[i + 2];
		}
	}
}