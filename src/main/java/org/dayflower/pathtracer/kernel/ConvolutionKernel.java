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

import java.util.Objects;

import com.amd.aparapi.Kernel;

/**
 * A {@code ConvolutionKernel} is a {@code Kernel} that updates an image with convolution-based image effects.
 * <p>
 * The effects that are supported are Blur, Detect Edges, Emboss, Gradient (both Horizontal and Vertical) and Sharpen.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ConvolutionKernel extends Kernel {
	private static final int FILTER_BLUR = 1;
	private static final int FILTER_DETECT_EDGES = 2;
	private static final int FILTER_EMBOSS = 3;
	private static final int FILTER_GRADIENT_HORIZONTAL = 4;
	private static final int FILTER_GRADIENT_VERTICAL = 5;
	private static final int FILTER_SHARPEN = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final byte[] image;
	private final byte[] imageCopy;
	private final float[] blur = new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F};
	private final float[] detectEdges = new float[] {-1.0F, -1.0F, -1.0F, -1.0F, 8.0F, -1.0F, -1.0F, -1.0F, -1.0F};
	private final float[] emboss = new float[] {-1.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F};
	private final float[] gradientHorizontal = new float[] {-1.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
	private final float[] gradientVertical = new float[] {-1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F};
	private final float[] sharpen = new float[] {-1.0F, -1.0F, -1.0F, -1.0F, 9.0F, -1.0F, -1.0F, -1.0F, -1.0F};
	private int filter;
	private final int height;
	private final int width;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code ConvolutionKernel} instance.
	 * <p>
	 * If {@code pixels} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param image the image array
	 * @param width the width of the image
	 * @param height the height of the image
	 * @throws NullPointerException thrown if, and only if, {@code pixels} is {@code null}
	 */
	public ConvolutionKernel(final byte[] image, final int width, final int height) {
		this.image = Objects.requireNonNull(image, "image == null");
		this.imageCopy = image.clone();
		this.width = width;
		this.height = height;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Enables the Blur effect.
	 */
	public void enableBlur() {
		this.filter = FILTER_BLUR;
	}
	
	/**
	 * Enables the Detect Edges effect.
	 */
	public void enableDetectEdges() {
		this.filter = FILTER_DETECT_EDGES;
	}
	
	/**
	 * Enables the Emboss effect.
	 */
	public void enableEmboss() {
		this.filter = FILTER_EMBOSS;
	}
	
	/**
	 * Enables the Horizontal Gradient effect.
	 */
	public void enableGradientHorizontal() {
		this.filter = FILTER_GRADIENT_HORIZONTAL;
	}
	
	/**
	 * Enables the Vertical Gradient effect.
	 */
	public void enableGradientVertical() {
		this.filter = FILTER_GRADIENT_VERTICAL;
	}
	
	/**
	 * Enables the Sharpen effect.
	 */
	public void enableSharpen() {
		this.filter = FILTER_SHARPEN;
	}
	
	/**
	 * Returns the image array from the GPU.
	 */
	public void get() {
		get(this.image);
	}
	
	/**
	 * Runs this {@code ConvolutionKernel} instance.
	 */
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
	
	/**
	 * Updates the arrays.
	 */
	public void update() {
		setExplicit(true);
		
		for(int i = 0; i < this.image.length; i++) {
			this.imageCopy[i] = this.image[i];
		}
		
		put(this.image);
		put(this.imageCopy);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doFilterBlur(final int x, final int y) {
		final float factor = 1.0F / 13.0F;
		
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 25; filterIndex++) {
			final int filterX = filterIndex % 5;
			final int filterY = filterIndex / 5;
			
			final int imageX = (x - 5 / 2 + filterX) % imageW;
			final int imageY = (y - 5 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.blur[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.blur[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.blur[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(factor * r0), 0), 255);
		final int g1 = min(max((int)(factor * g0), 0), 255);
		final int b1 = min(max((int)(factor * b0), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
	
	private void doFilterDetectEdges(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 9; filterIndex++) {
			final int filterX = filterIndex % 3;
			final int filterY = filterIndex / 3;
			
			final int imageX = (x - 3 / 2 + filterX) % imageW;
			final int imageY = (y - 3 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.detectEdges[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.detectEdges[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.detectEdges[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
	
	private void doFilterEmboss(final int x, final int y) {
		final float bias = 128.0F;
		
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 9; filterIndex++) {
			final int filterX = filterIndex % 3;
			final int filterY = filterIndex / 3;
			
			final int imageX = (x - 3 / 2 + filterX) % imageW;
			final int imageY = (y - 3 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.emboss[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.emboss[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.emboss[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(r0 + bias), 0), 255);
		final int g1 = min(max((int)(g0 + bias), 0), 255);
		final int b1 = min(max((int)(b0 + bias), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
	
	private void doFilterGradientHorizontal(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 9; filterIndex++) {
			final int filterX = filterIndex % 3;
			final int filterY = filterIndex / 3;
			
			final int imageX = (x - 3 / 2 + filterX) % imageW;
			final int imageY = (y - 3 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.gradientHorizontal[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.gradientHorizontal[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.gradientHorizontal[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
	
	private void doFilterGradientVertical(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 9; filterIndex++) {
			final int filterX = filterIndex % 3;
			final int filterY = filterIndex / 3;
			
			final int imageX = (x - 3 / 2 + filterX) % imageW;
			final int imageY = (y - 3 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.gradientVertical[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.gradientVertical[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.gradientVertical[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
	
	private void doFilterSharpen(final int x, final int y) {
		float r0 = 0.0F;
		float g0 = 0.0F;
		float b0 = 0.0F;
		
		final int imageW = this.width;
		final int imageH = this.height;
		
		for(int filterIndex = 0; filterIndex < 9; filterIndex++) {
			final int filterX = filterIndex % 3;
			final int filterY = filterIndex / 3;
			
			final int imageX = (x - 3 / 2 + filterX) % imageW;
			final int imageY = (y - 3 / 2 + filterY) % imageH;
			final int imageIndex = (imageY * imageW + imageX) * 4;
			
			r0 += (this.imageCopy[imageIndex + 0] & 0xFF) * this.sharpen[filterIndex];
			g0 += (this.imageCopy[imageIndex + 1] & 0xFF) * this.sharpen[filterIndex];
			b0 += (this.imageCopy[imageIndex + 2] & 0xFF) * this.sharpen[filterIndex];
		}
		
		final int imageIndex = (y * imageW + x) * 4;
		
		final int r1 = min(max((int)(r0), 0), 255);
		final int g1 = min(max((int)(g0), 0), 255);
		final int b1 = min(max((int)(b0), 0), 255);
		
		this.image[imageIndex + 0] = (byte)(r1);
		this.image[imageIndex + 1] = (byte)(g1);
		this.image[imageIndex + 2] = (byte)(b1);
	}
}