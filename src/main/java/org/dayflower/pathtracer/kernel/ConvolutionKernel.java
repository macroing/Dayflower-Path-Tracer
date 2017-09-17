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
package org.dayflower.pathtracer.kernel;

import java.util.Objects;

import com.amd.aparapi.Kernel;

public final class ConvolutionKernel extends Kernel {
	private static final int FILTER_BLUR = 1;
	private static final int FILTER_DETECT_EDGES = 2;
	private static final int FILTER_EMBOSS = 3;
	private static final int FILTER_GRADIENT_HORIZONTAL = 4;
	private static final int FILTER_GRADIENT_VERTICAL = 5;
	private static final int FILTER_SHARPEN = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final byte[] pixels;
	private final byte[] pixelsCopy;
	private final float[][] blur = new float[][] {new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F}, new float[] {0.0F, 1.0F, 1.0F, 1.0F, 0.0F}, new float[] {1.0F, 1.0F, 1.0F, 1.0F, 1.0F}, new float[] {0.0F, 1.0F, 1.0F, 1.0F, 0.0F}, new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F}};
	private final float[][] detectEdges = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {-1.0F, 8.0F, -1.0F}, new float[] {-1.0F, -1.0F, -1.0F}};
	private final float[][] emboss = new float[][] {new float[] {-1.0F, -1.0F, 0.0F}, new float[] {-1.0F, 0.0F, 1.0F}, new float[] {0.0F, 1.0F, 1.0F}};
	private final float[][] gradientHorizontal = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {0.0F, 0.0F, 0.0F}, new float[] {1.0F, 1.0F, 1.0F}};
	private final float[][] gradientVertical = new float[][] {new float[] {-1.0F, 0.0F, 1.0F}, new float[] {-1.0F, 0.0F, 1.0F}, new float[] {-1.0F, 0.0F, 1.0F}};
	private final float[][] sharpen = new float[][] {new float[] {-1.0F, -1.0F, -1.0F}, new float[] {-1.0F, 9.0F, -1.0F}, new float[] {-1.0F, -1.0F, -1.0F}};
	private int filter;
	private final int height;
	private final int width;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ConvolutionKernel(final byte[] pixels, final int width, final int height) {
		this.pixels = Objects.requireNonNull(pixels, "pixels == null");
		this.pixelsCopy = pixels.clone();
		this.width = width;
		this.height = height;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void enableBlur() {
		this.filter = FILTER_BLUR;
	}
	
	public void enableDetectEdges() {
		this.filter = FILTER_DETECT_EDGES;
	}
	
	public void enableEmboss() {
		this.filter = FILTER_EMBOSS;
	}
	
	public void enableGradientHorizontal() {
		this.filter = FILTER_GRADIENT_HORIZONTAL;
	}
	
	public void enableGradientVertical() {
		this.filter = FILTER_GRADIENT_VERTICAL;
	}
	
	public void enableSharpen() {
		this.filter = FILTER_SHARPEN;
	}
	
	@Override
	public void run() {
		final int index = getGlobalId();
		final int y = index / this.width;
		final int x = index - y * this.width;
		
		if(this.filter == FILTER_BLUR) {
			doFilterBlur(x, y);
		} else if(this.filter == FILTER_DETECT_EDGES) {
			doFilterDetectEdges(x, y);
		} else if(this.filter == FILTER_EMBOSS) {
			doFilterEmboss(x, y);
		} else if(this.filter == FILTER_GRADIENT_HORIZONTAL) {
			doFilterGradientHorizontal(x, y);
		} else if(this.filter == FILTER_GRADIENT_VERTICAL) {
			doFilterGradientVertical(x, y);
		} else if(this.filter == FILTER_SHARPEN) {
			doFilterSharpen(x, y);
		}
	}
	
	public void update() {
		for(int i = 0; i < this.pixels.length; i++) {
			this.pixelsCopy[i] = this.pixels[i];
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doFilterBlur(final int x, final int y) {
		final float factor = 1.0F / 13.0F;
		
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 5; filterY++) {
			for(int filterX = 0; filterX < 5; filterX++) {
				final int imageX = (x - 5 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 5 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.blur[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.blur[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.blur[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(factor * r0), 0), 255);
		final int g1 = min(max((int)(factor * g0), 0), 255);
		final int b1 = min(max((int)(factor * b0), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
	
	private void doFilterDetectEdges(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 3; filterY++) {
			for(int filterX = 0; filterX < 3; filterX++) {
				final int imageX = (x - 3 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 3 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.detectEdges[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.detectEdges[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.detectEdges[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
	
	private void doFilterEmboss(final int x, final int y) {
		final float bias = 128.0F;
		
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 3; filterY++) {
			for(int filterX = 0; filterX < 3; filterX++) {
				final int imageX = (x - 3 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 3 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.emboss[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.emboss[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.emboss[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(r0 + bias), 0), 255);
		final int g1 = min(max((int)(g0 + bias), 0), 255);
		final int b1 = min(max((int)(b0 + bias), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
	
	private void doFilterGradientHorizontal(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 3; filterY++) {
			for(int filterX = 0; filterX < 3; filterX++) {
				final int imageX = (x - 3 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 3 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.gradientHorizontal[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.gradientHorizontal[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.gradientHorizontal[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
	
	private void doFilterGradientVertical(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 3; filterY++) {
			for(int filterX = 0; filterX < 3; filterX++) {
				final int imageX = (x - 3 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 3 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.gradientVertical[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.gradientVertical[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.gradientVertical[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
	
	private void doFilterSharpen(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		for(int filterY = 0; filterY < 3; filterY++) {
			for(int filterX = 0; filterX < 3; filterX++) {
				final int imageX = (x - 3 / 2 + filterX + this.width) % this.width;
				final int imageY = (y - 3 / 2 + filterY + this.height) % this.height;
				
				final int index = (imageY * this.width + imageX) * 4;
				
				r0 += (this.pixelsCopy[index + 0] & 0xFF) * this.sharpen[filterY][filterX];
				g0 += (this.pixelsCopy[index + 1] & 0xFF) * this.sharpen[filterY][filterX];
				b0 += (this.pixelsCopy[index + 2] & 0xFF) * this.sharpen[filterY][filterX];
			}
		}
		
		final int index = (y * this.width + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.pixels[index + 0] = (byte)(r1);
		this.pixels[index + 1] = (byte)(g1);
		this.pixels[index + 2] = (byte)(b1);
	}
}