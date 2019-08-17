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
package org.dayflower.pathtracer.util;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

/**
 * This {@code Image} class represents an image that can be drawn to.
 * <p>
 * This class will store all colors in a packed {@code int}. The format used is the same as {@link PackedIntComponentOrder#ARGB}.
 * <p>
 * Most methods take colors as parameter arguments. These are {@code r}, {@code g} and {@code b}, or {@code r}, {@code g}, {@code b} and {@code a}. All of them are stored as {@code float}s, with an expected range of {@code [0.0F, 1.0F]}. Any value
 * outside of this range will be saturated. Calling {@code setColor(0, 0, -1.0F, 0.5F, 2.0F)} will effectively be equivalent to {@code setColor(0, 0, 0.0F, 0.5F, 1.0F)}.
 * <p>
 * Methods that only take {@code r}, {@code g} and {@code b} as parameter arguments, will treat {@code a} as {@code 1.0F}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Image {
	private float colorSpaceBreakPoint;
	private float colorSpaceGamma;
	private float colorSpaceSegmentOffset;
	private float colorSpaceSlope;
	private float colorSpaceSlopeMatch;
	private final float[] colorSpaceMatrixRGBToXYZ;
	private final float[] colorSpaceMatrixXYZToRGB;
	private final int resolutionX;
	private final int resolutionY;
	private final int[] array;
	private final int[] colorSpaceGammaCurve;
	private final int[] colorSpaceGammaCurveReciprocal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Image(800, 800)}.
	 */
	public Image() {
		this(800, 800);
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs while loading the image, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} pointing to an image file that should be loaded
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs while loading the image
	 */
	public Image(final File file) {
		final BufferedImage bufferedImage = doLoadBufferedImage(Objects.requireNonNull(file, "file == null"));
		
		this.resolutionX = bufferedImage.getWidth();
		this.resolutionY = bufferedImage.getHeight();
		
		this.array = DataBufferInt.class.cast(bufferedImage.getRaster().getDataBuffer()).getData().clone();
		
		this.colorSpaceGammaCurve = new int[256];
		this.colorSpaceGammaCurveReciprocal = new int[256];
		this.colorSpaceMatrixRGBToXYZ = new float[12];
		this.colorSpaceMatrixXYZToRGB = new float[12];
		
		setColorSpaceSRGB();
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 */
	public Image(final int resolutionX, final int resolutionY) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		
		this.array = new int[resolutionX * resolutionY];
		
		this.colorSpaceGammaCurve = new int[256];
		this.colorSpaceGammaCurveReciprocal = new int[256];
		this.colorSpaceMatrixRGBToXYZ = new float[12];
		this.colorSpaceMatrixXYZToRGB = new float[12];
		
		setColorSpaceSRGB();
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Image(resolutionX, resolutionY, array, ArrayComponentOrder.BGRA)}.
	 * <p>
	 * It is expected that {@code array.length} is the same as {@code resolutionX * resolutionY * ArrayComponentOrder.BGRA.getComponentCount()}.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code array.length} is incorrect, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 * @param array the array to create this {@code Image} instance from
	 * @throws IllegalArgumentException thrown if, and only if, {@code array.length} is incorrect
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public Image(final int resolutionX, final int resolutionY, final byte[] array) {
		this(resolutionX, resolutionY, array, ArrayComponentOrder.BGRA);
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * It is expected that {@code array.length} is the same as {@code resolutionX * resolutionY * arrayComponentOrder.getComponentCount()}.
	 * <p>
	 * If either {@code array} or {@code arrayComponentOrder} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code array.length} is incorrect, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 * @param array the array to create this {@code Image} instance from
	 * @param arrayComponentOrder an {@link ArrayComponentOrder} to get the components from {@code array} in the correct order
	 * @throws IllegalArgumentException thrown if, and only if, {@code array.length} is incorrect
	 * @throws NullPointerException thrown if, and only if, either {@code array} or {@code arrayComponentOrder} are {@code null}
	 */
	public Image(final int resolutionX, final int resolutionY, final byte[] array, final ArrayComponentOrder arrayComponentOrder) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		
		this.array = doCreateIntArrayFromByteArray(resolutionX, resolutionY, array, arrayComponentOrder);
		
		this.colorSpaceGammaCurve = new int[256];
		this.colorSpaceGammaCurveReciprocal = new int[256];
		this.colorSpaceMatrixRGBToXYZ = new float[12];
		this.colorSpaceMatrixXYZToRGB = new float[12];
		
		setColorSpaceSRGB();
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Image(resolutionX, resolutionY, array, PackedIntComponentOrder.ARGB)}.
	 * <p>
	 * It is expected that {@code array.length} is the same as {@code resolutionX * resolutionY}.
	 * <p>
	 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code array.length} is incorrect, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 * @param array the array to create this {@code Image} instance from
	 * @throws IllegalArgumentException thrown if, and only if, {@code array.length} is incorrect
	 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
	 */
	public Image(final int resolutionX, final int resolutionY, final int[] array) {
		this(resolutionX, resolutionY, array, PackedIntComponentOrder.ARGB);
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * It is expected that {@code array.length} is the same as {@code resolutionX * resolutionY * arrayComponentOrder.getComponentCount()}.
	 * <p>
	 * If either {@code array} or {@code arrayComponentOrder} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code array.length} is incorrect, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 * @param array the array to create this {@code Image} instance from
	 * @param arrayComponentOrder an {@link ArrayComponentOrder} to get the components from {@code array} in the correct order
	 * @throws IllegalArgumentException thrown if, and only if, {@code array.length} is incorrect
	 * @throws NullPointerException thrown if, and only if, either {@code array} or {@code arrayComponentOrder} are {@code null}
	 */
	public Image(final int resolutionX, final int resolutionY, final int[] array, final ArrayComponentOrder arrayComponentOrder) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		
		this.array = doCreateIntArrayFromIntArray(resolutionX, resolutionY, array, arrayComponentOrder);
		
		this.colorSpaceGammaCurve = new int[256];
		this.colorSpaceGammaCurveReciprocal = new int[256];
		this.colorSpaceMatrixRGBToXYZ = new float[12];
		this.colorSpaceMatrixXYZToRGB = new float[12];
		
		setColorSpaceSRGB();
	}
	
	/**
	 * Constructs a new {@code Image} instance.
	 * <p>
	 * It is expected that {@code array.length} is the same as {@code resolutionX * resolutionY}.
	 * <p>
	 * If either {@code array} or {@code packedIntComponentOrder} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If {@code array.length} is incorrect, an {@code IllegalArgumentException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis, also known as the width
	 * @param resolutionY the resolution along the Y-axis, also known as the height
	 * @param array the array to create this {@code Image} instance from
	 * @param packedIntComponentOrder a {@link PackedIntComponentOrder} to get the components from the {@code int} values of the {@code array} in the correct order
	 * @throws IllegalArgumentException thrown if, and only if, {@code array.length} is incorrect
	 * @throws NullPointerException thrown if, and only if, either {@code array} or {@code packedIntComponentOrder} are {@code null}
	 */
	public Image(final int resolutionX, final int resolutionY, final int[] array, final PackedIntComponentOrder packedIntComponentOrder) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		
		this.array = doCreateIntArrayFromIntArray(resolutionX, resolutionY, array, packedIntComponentOrder);
		
		this.colorSpaceGammaCurve = new int[256];
		this.colorSpaceGammaCurveReciprocal = new int[256];
		this.colorSpaceMatrixRGBToXYZ = new float[12];
		this.colorSpaceMatrixXYZToRGB = new float[12];
		
		setColorSpaceSRGB();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code BufferedImage} representation of this {@code Image} instance.
	 * 
	 * @return a {@code BufferedImage} representation of this {@code Image} instance
	 */
	public BufferedImage toBufferedImage() {
		final BufferedImage bufferedImage = new BufferedImage(this.resolutionX, this.resolutionY, BufferedImage.TYPE_INT_ARGB);
		
		final int[] data = DataBufferInt.class.cast(bufferedImage.getRaster().getDataBuffer()).getData();
		
		System.arraycopy(this.array, 0, data, 0, this.array.length);
		
		return bufferedImage;
	}
	
	/**
	 * Returns a {@code byte} array representation of this {@code Image} instance.
	 * <p>
	 * Calling this method is equivalent to {@code toByteArray(ArrayComponentOrder.BGRA)}.
	 * 
	 * @return a {@code byte} array representation of this {@code Image} instance
	 */
	public byte[] toByteArray() {
		return toByteArray(ArrayComponentOrder.BGRA);
	}
	
	/**
	 * Returns a {@code byte} array representation of this {@code Image} instance.
	 * <p>
	 * The R-, G-, B- and A-components will be ordered according to {@code arrayComponentOrder}.
	 * <p>
	 * If {@code arrayComponentOrder} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param arrayComponentOrder an {@link ArrayComponentOrder}
	 * @return a {@code byte} array representation of this {@code Image} instance
	 * @throws NullPointerException thrown if, and only if, {@code arrayComponentOrder} is {@code null}
	 */
	public byte[] toByteArray(final ArrayComponentOrder arrayComponentOrder) {
		final int resolutionX = this.resolutionX;
		final int resolutionY = this.resolutionY;
		
		final int length = resolutionX * resolutionY;
		final int lengthResult = length * arrayComponentOrder.getComponentCount();
		
		final byte[] arrayResult = new byte[lengthResult];
		
		for(int i = 0, j = 0; i < length; i++, j += arrayComponentOrder.getComponentCount()) {
			if(arrayComponentOrder.hasOffsetR()) {
				arrayResult[j + arrayComponentOrder.getOffsetR()] = (byte)(doGetR(i));
			}
			
			if(arrayComponentOrder.hasOffsetG()) {
				arrayResult[j + arrayComponentOrder.getOffsetG()] = (byte)(doGetG(i));
			}
			
			if(arrayComponentOrder.hasOffsetB()) {
				arrayResult[j + arrayComponentOrder.getOffsetB()] = (byte)(doGetB(i));
			}
			
			if(arrayComponentOrder.hasOffsetA()) {
				arrayResult[j + arrayComponentOrder.getOffsetA()] = (byte)(doGetA(i));
			}
		}
		
		return arrayResult;
	}
	
	/**
	 * Returns the A-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code index} is outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param index the index of the color
	 * @return the A-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getA(final int index) {
		return doToFloat((doGetARGB(index) >> 24) & 0xFF);
	}
	
	/**
	 * Returns the A-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param x the X-coordinate of the color
	 * @param y the Y-coordinate of the color
	 * @return the A-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getA(final int x, final int y) {
		return doToFloat((doGetARGB(x, y) >> 24) & 0xFF);
	}
	
	/**
	 * Returns the B-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code index} is outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param index the index of the color
	 * @return the B-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getB(final int index) {
		return doToFloat((doGetARGB(index) >> 0) & 0xFF);
	}
	
	/**
	 * Returns the B-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param x the X-coordinate of the color
	 * @param y the Y-coordinate of the color
	 * @return the B-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getB(final int x, final int y) {
		return doToFloat((doGetARGB(x, y) >> 0) & 0xFF);
	}
	
	/**
	 * Returns the G-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code index} is outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param index the index of the color
	 * @return the G-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getG(final int index) {
		return doToFloat((doGetARGB(index) >> 8) & 0xFF);
	}
	
	/**
	 * Returns the G-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param x the X-coordinate of the color
	 * @param y the Y-coordinate of the color
	 * @return the G-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getG(final int x, final int y) {
		return doToFloat((doGetARGB(x, y) >> 8) & 0xFF);
	}
	
	/**
	 * Returns the R-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code index} is outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param index the index of the color
	 * @return the R-component of the color at {@code index} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getR(final int index) {
		return doToFloat((doGetARGB(index) >> 16) & 0xFF);
	}
	
	/**
	 * Returns the R-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, {@code 0.0F} will be returned.
	 * 
	 * @param x the X-coordinate of the color
	 * @param y the Y-coordinate of the color
	 * @return the R-component of the color at {@code x} and {@code y} as a {@code float} in the range {@code [0.0, 1.0]}
	 */
	public float getR(final int x, final int y) {
		return doToFloat((doGetARGB(x, y) >> 16) & 0xFF);
	}
	
	/**
	 * Returns the resolution along the X-axis, also known as the width.
	 * 
	 * @return the resolution along the X-axis, also known as the width
	 */
	public int getResolutionX() {
		return this.resolutionX;
	}
	
	/**
	 * Returns the resolution along the Y-axis, also known as the height.
	 * 
	 * @return the resolution along the Y-axis, also known as the height
	 */
	public int getResolutionY() {
		return this.resolutionY;
	}
	
	/**
	 * Returns a clone of the array that stores the colors.
	 * 
	 * @return a clone of the array that stores the colors
	 */
	public int[] getArray() {
		return this.array.clone();
	}
	
	/**
	 * Returns an {@code int} array representation of this {@code Image} instance.
	 * <p>
	 * Calling this method is equivalent to {@code toIntArray(PackedIntComponentOrder.ARGB)}.
	 * 
	 * @return an {@code int} array representation of this {@code Image} instance
	 */
	public int[] toIntArray() {
		return toIntArray(PackedIntComponentOrder.ARGB);
	}
	
	/**
	 * Returns an {@code int} array representation of this {@code Image} instance.
	 * <p>
	 * The R-, G-, B- and A-components will be ordered according to {@code packedIntComponentOrder}.
	 * <p>
	 * If {@code packedIntComponentOrder} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param packedIntComponentOrder a {@link PackedIntComponentOrder}
	 * @return an {@code int} array representation of this {@code Image} instance
	 * @throws NullPointerException thrown if, and only if, {@code packedIntComponentOrder} is {@code null}
	 */
	public int[] toIntArray(final PackedIntComponentOrder packedIntComponentOrder) {
		final int resolutionX = this.resolutionX;
		final int resolutionY = this.resolutionY;
		
		final int length = resolutionX * resolutionY;
		
		final int[] arrayResult = new int[length];
		
		for(int i = 0; i < length; i++) {
			arrayResult[i] = packedIntComponentOrder.pack(doGetR(i), doGetG(i), doGetB(i), doGetA(i));
		}
		
		return arrayResult;
	}
	
	/**
	 * Clears the image with a color of black.
	 * <p>
	 * Calling this method is equivalent to {@code clear(0.0F, 0.0F, 0.0F)}.
	 */
	public void clear() {
		clear(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Clears the image with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Calling this method is equivalent to {@code clear(r, g, b, 1.0F)}.
	 * 
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void clear(final float r, final float g, final float b) {
		clear(r, g, b, 1.0F);
	}
	
	/**
	 * Clears the image with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * 
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void clear(final float r, final float g, final float b, final float a) {
		Arrays.fill(this.array, doToARGB(r, g, b, a));
	}
	
	/**
	 * Applies a convolution filter with three rows and three columns to the image.
	 * 
	 * @param factor the factor to use
	 * @param bias the bias to use
	 * @param element00 the element at index {@code 0} or row {@code 0} and column {@code 0}
	 * @param element01 the element at index {@code 1} or row {@code 0} and column {@code 1}
	 * @param element02 the element at index {@code 2} or row {@code 0} and column {@code 2}
	 * @param element10 the element at index {@code 3} or row {@code 1} and column {@code 0}
	 * @param element11 the element at index {@code 4} or row {@code 1} and column {@code 1}
	 * @param element12 the element at index {@code 5} or row {@code 1} and column {@code 2}
	 * @param element20 the element at index {@code 6} or row {@code 2} and column {@code 0}
	 * @param element21 the element at index {@code 7} or row {@code 2} and column {@code 1}
	 * @param element22 the element at index {@code 8} or row {@code 2} and column {@code 2}
	 */
	public void convolutionFilter33(final float factor, final float bias, final float element00, final float element01, final float element02, final float element10, final float element11, final float element12, final float element20, final float element21, final float element22) {
		final int[] array = new int[this.array.length];
		
		final int resolutionX = this.resolutionX;
		
		for(int i = 0; i < array.length; i++) {
			final int x = i % resolutionX;
			final int y = i / resolutionX;
			
			float r = 0.0F;
			float g = 0.0F;
			float b = 0.0F;
			
//			Row #0:
			r += getR(x + -1, y + -1) * element00;
			g += getG(x + -1, y + -1) * element00;
			b += getB(x + -1, y + -1) * element00;
			
			r += getR(x + +0, y + -1) * element01;
			g += getG(x + +0, y + -1) * element01;
			b += getB(x + +0, y + -1) * element01;
			
			r += getR(x + +1, y + -1) * element02;
			g += getG(x + +1, y + -1) * element02;
			b += getB(x + +1, y + -1) * element02;
			
//			Row #1:
			r += getR(x + -1, y + +0) * element10;
			g += getG(x + -1, y + +0) * element10;
			b += getB(x + -1, y + +0) * element10;
			
			r += getR(x + +0, y + +0) * element11;
			g += getG(x + +0, y + +0) * element11;
			b += getB(x + +0, y + +0) * element11;
			
			r += getR(x + +1, y + +0) * element12;
			g += getG(x + +1, y + +0) * element12;
			b += getB(x + +1, y + +0) * element12;
			
//			Row #2:
			r += getR(x + -1, y + +1) * element20;
			g += getG(x + -1, y + +1) * element20;
			b += getB(x + -1, y + +1) * element20;
			
			r += getR(x + +0, y + +1) * element21;
			g += getG(x + +0, y + +1) * element21;
			b += getB(x + +0, y + +1) * element21;
			
			r += getR(x + +1, y + +1) * element22;
			g += getG(x + +1, y + +1) * element22;
			b += getB(x + +1, y + +1) * element22;
			
			r = r * factor + bias;
			g = g * factor + bias;
			b = b * factor + bias;
			
			final float maxComponentValue = max(r, max(g, b));
			
			if(maxComponentValue > 1.0F) {
				r /= maxComponentValue;
				g /= maxComponentValue;
				b /= maxComponentValue;
			}
			
			r = doSaturate(r);
			g = doSaturate(g);
			b = doSaturate(b);
			
			final int colorARGB = doToARGB(r, g, b, 1.0F);
			
			array[i] = colorARGB;
		}
		
		System.arraycopy(array, 0, this.array, 0, array.length);
	}
	
	/**
	 * Applies a detect edges convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33DetectEdges() {
		convolutionFilter33(1.0F, 0.0F, -1.0F, -1.0F, -1.0F, -1.0F, 8.0F, -1.0F, -1.0F, -1.0F, -1.0F);
	}
	
	/**
	 * Applies an emboss convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33Emboss() {
		convolutionFilter33(1.0F, 0.5F, -1.0F, -1.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F);
	}
	
	/**
	 * Applies a horizontal gradient convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33GradientHorizontal() {
		convolutionFilter33(1.0F, 0.0F, -1.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	/**
	 * Applies a vertical gradient convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33GradientVertical() {
		convolutionFilter33(1.0F, 0.0F, -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F);
	}
	
	/**
	 * Applies a random convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33Random() {
		final float element00 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element01 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element02 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element10 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element11 = 1.0F;
		final float element12 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element20 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element21 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element22 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		
		convolutionFilter33(1.0F, 0.0F, element00, element01, element02, element10, element11, element12, element20, element21, element22);
	}
	
	/**
	 * Applies a sharpen convolution filter with three rows and three columns to the image.
	 */
	public void convolutionFilter33Sharpen() {
		convolutionFilter33(1.0F, 0.0F, -1.0F, -1.0F, -1.0F, -1.0F, 9.0F, -1.0F, -1.0F, -1.0F, -1.0F);
	}
	
	/**
	 * Applies a convolution filter with five rows and five columns to the image.
	 * 
	 * @param factor the factor to use
	 * @param bias the bias to use
	 * @param element00 the element at index {@code 0} or row {@code 0} and column {@code 0}
	 * @param element01 the element at index {@code 1} or row {@code 0} and column {@code 1}
	 * @param element02 the element at index {@code 2} or row {@code 0} and column {@code 2}
	 * @param element03 the element at index {@code 3} or row {@code 0} and column {@code 3}
	 * @param element04 the element at index {@code 4} or row {@code 0} and column {@code 4}
	 * @param element10 the element at index {@code 5} or row {@code 1} and column {@code 0}
	 * @param element11 the element at index {@code 6} or row {@code 1} and column {@code 1}
	 * @param element12 the element at index {@code 7} or row {@code 1} and column {@code 2}
	 * @param element13 the element at index {@code 8} or row {@code 1} and column {@code 3}
	 * @param element14 the element at index {@code 9} or row {@code 1} and column {@code 4}
	 * @param element20 the element at index {@code 10} or row {@code 2} and column {@code 0}
	 * @param element21 the element at index {@code 11} or row {@code 2} and column {@code 1}
	 * @param element22 the element at index {@code 12} or row {@code 2} and column {@code 2}
	 * @param element23 the element at index {@code 13} or row {@code 2} and column {@code 3}
	 * @param element24 the element at index {@code 14} or row {@code 2} and column {@code 4}
	 * @param element30 the element at index {@code 15} or row {@code 3} and column {@code 0}
	 * @param element31 the element at index {@code 16} or row {@code 3} and column {@code 1}
	 * @param element32 the element at index {@code 17} or row {@code 3} and column {@code 2}
	 * @param element33 the element at index {@code 18} or row {@code 3} and column {@code 3}
	 * @param element34 the element at index {@code 19} or row {@code 3} and column {@code 4}
	 * @param element40 the element at index {@code 20} or row {@code 4} and column {@code 0}
	 * @param element41 the element at index {@code 21} or row {@code 4} and column {@code 1}
	 * @param element42 the element at index {@code 22} or row {@code 4} and column {@code 2}
	 * @param element43 the element at index {@code 23} or row {@code 4} and column {@code 3}
	 * @param element44 the element at index {@code 24} or row {@code 4} and column {@code 4}
	 */
	public void convolutionFilter55(final float factor, final float bias, final float element00, final float element01, final float element02, final float element03, final float element04, final float element10, final float element11, final float element12, final float element13, final float element14, final float element20, final float element21, final float element22, final float element23, final float element24, final float element30, final float element31, final float element32, final float element33, final float element34, final float element40, final float element41, final float element42, final float element43, final float element44) {
		final int[] array = new int[this.array.length];
		
		final int resolutionX = this.resolutionX;
		
		for(int i = 0; i < array.length; i++) {
			final int x = i % resolutionX;
			final int y = i / resolutionX;
			
			float r = 0.0F;
			float g = 0.0F;
			float b = 0.0F;
			
//			Row #0:
			r += getR(x + -2, y + -2) * element00;
			g += getG(x + -2, y + -2) * element00;
			b += getB(x + -2, y + -2) * element00;
			
			r += getR(x + -1, y + -2) * element01;
			g += getG(x + -1, y + -2) * element01;
			b += getB(x + -1, y + -2) * element01;
			
			r += getR(x + +0, y + -2) * element02;
			g += getG(x + +0, y + -2) * element02;
			b += getB(x + +0, y + -2) * element02;
			
			r += getR(x + +1, y + -2) * element03;
			g += getG(x + +1, y + -2) * element03;
			b += getB(x + +1, y + -2) * element03;
			
			r += getR(x + +2, y + -2) * element04;
			g += getG(x + +2, y + -2) * element04;
			b += getB(x + +2, y + -2) * element04;
			
//			Row #1:
			r += getR(x + -2, y + -1) * element10;
			g += getG(x + -2, y + -1) * element10;
			b += getB(x + -2, y + -1) * element10;
			
			r += getR(x + -1, y + -1) * element11;
			g += getG(x + -1, y + -1) * element11;
			b += getB(x + -1, y + -1) * element11;
			
			r += getR(x + +0, y + -1) * element12;
			g += getG(x + +0, y + -1) * element12;
			b += getB(x + +0, y + -1) * element12;
			
			r += getR(x + +1, y + -1) * element13;
			g += getG(x + +1, y + -1) * element13;
			b += getB(x + +1, y + -1) * element13;
			
			r += getR(x + +2, y + -1) * element14;
			g += getG(x + +2, y + -1) * element14;
			b += getB(x + +2, y + -1) * element14;
			
//			Row #2:
			r += getR(x + -2, y + +0) * element20;
			g += getG(x + -2, y + +0) * element20;
			b += getB(x + -2, y + +0) * element20;
			
			r += getR(x + -1, y + +0) * element21;
			g += getG(x + -1, y + +0) * element21;
			b += getB(x + -1, y + +0) * element21;
			
			r += getR(x + +0, y + +0) * element22;
			g += getG(x + +0, y + +0) * element22;
			b += getB(x + +0, y + +0) * element22;
			
			r += getR(x + +1, y + +0) * element23;
			g += getG(x + +1, y + +0) * element23;
			b += getB(x + +1, y + +0) * element23;
			
			r += getR(x + +2, y + +0) * element24;
			g += getG(x + +2, y + +0) * element24;
			b += getB(x + +2, y + +0) * element24;
			
//			Row #3:
			r += getR(x + -2, y + +1) * element30;
			g += getG(x + -2, y + +1) * element30;
			b += getB(x + -2, y + +1) * element30;
			
			r += getR(x + -1, y + +1) * element31;
			g += getG(x + -1, y + +1) * element31;
			b += getB(x + -1, y + +1) * element31;
			
			r += getR(x + +0, y + +1) * element32;
			g += getG(x + +0, y + +1) * element32;
			b += getB(x + +0, y + +1) * element32;
			
			r += getR(x + +1, y + +1) * element33;
			g += getG(x + +1, y + +1) * element33;
			b += getB(x + +1, y + +1) * element33;
			
			r += getR(x + +2, y + +1) * element34;
			g += getG(x + +2, y + +1) * element34;
			b += getB(x + +2, y + +1) * element34;
			
//			Row #4:
			r += getR(x + -2, y + +2) * element40;
			g += getG(x + -2, y + +2) * element40;
			b += getB(x + -2, y + +2) * element40;
			
			r += getR(x + -1, y + +2) * element41;
			g += getG(x + -1, y + +2) * element41;
			b += getB(x + -1, y + +2) * element41;
			
			r += getR(x + +0, y + +2) * element42;
			g += getG(x + +0, y + +2) * element42;
			b += getB(x + +0, y + +2) * element42;
			
			r += getR(x + +1, y + +2) * element43;
			g += getG(x + +1, y + +2) * element43;
			b += getB(x + +1, y + +2) * element43;
			
			r += getR(x + +2, y + +2) * element44;
			g += getG(x + +2, y + +2) * element44;
			b += getB(x + +2, y + +2) * element44;
			
			r = r * factor + bias;
			g = g * factor + bias;
			b = b * factor + bias;
			
			final float maxComponentValue = max(r, max(g, b));
			
			if(maxComponentValue > 1.0F) {
				r /= maxComponentValue;
				g /= maxComponentValue;
				b /= maxComponentValue;
			}
			
			r = doSaturate(r);
			g = doSaturate(g);
			b = doSaturate(b);
			
			final int colorARGB = doToARGB(r, g, b, 1.0F);
			
			array[i] = colorARGB;
		}
		
		System.arraycopy(array, 0, this.array, 0, array.length);
	}
	
	/**
	 * Applies a blur convolution filter with five rows and five columns to the image.
	 */
	public void convolutionFilter55Blur() {
		convolutionFilter55(1.0F / 13.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Applies a random convolution filter with five rows and five columns to the image.
	 */
	public void convolutionFilter55Random() {
		final float element00 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element01 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element02 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element03 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element04 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element10 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element11 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element12 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element13 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element14 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element20 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element21 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element22 = 1.0F;
		final float element23 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element24 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element30 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element31 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element32 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element33 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element34 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element40 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element41 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element42 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element43 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		final float element44 = ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F;
		
		convolutionFilter55(1.0F, 0.0F, element00, element01, element02, element03, element04, element10, element11, element12, element13, element14, element20, element21, element22, element23, element24, element30, element31, element32, element33, element34, element40, element41, element42, element43, element44);
	}
	
	/**
	 * Draws a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of black.
	 * <p>
	 * Only the parts of the circle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawCircle(x, y, radius, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 */
	public void drawCircle(final int x, final int y, final int radius) {
		drawCircle(x, y, radius, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Draws a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the circle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawCircle(x, y, radius, r, g, b, 1.0F)}.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void drawCircle(final int x, final int y, final int radius, final float r, final float g, final float b) {
		drawCircle(x, y, radius, r, g, b, 1.0F);
	}
	
	/**
	 * Draws a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the circle that are inside the image will be drawn.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void drawCircle(final int x, final int y, final int radius, final float r, final float g, final float b, final float a) {
		final int colorARGB = doToARGB(r, g, b, a);
		
		for(int i = -radius; i <= radius; i++) {
			for(int j = -radius; j <= radius; j++) {
				if(j * j + i * i == radius * radius) {
					doSetColor(x + j, y + i, colorARGB);
				}
			}
		}
	}
	
	/**
	 * Draws a line from {@code startX} and {@code startY} to {@code endX} and {@code endY}, with a color of black.
	 * <p>
	 * Only the parts of the line that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawLine(startX, startY, endX, endY, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param startX the X-coordinate to start the line at
	 * @param startY the Y-coordinate to start the line at
	 * @param endX the X-coordinate to end the line at
	 * @param endY the Y-coordinate to end the line at
	 */
	public void drawLine(final int startX, final int startY, final int endX, final int endY) {
		drawLine(startX, startY, endX, endY, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Draws a line from {@code startX} and {@code startY} to {@code endX} and {@code endY}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the line that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawLine(startX, startY, endX, endY, r, g, b, 1.0F)}.
	 * 
	 * @param startX the X-coordinate to start the line at
	 * @param startY the Y-coordinate to start the line at
	 * @param endX the X-coordinate to end the line at
	 * @param endY the Y-coordinate to end the line at
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void drawLine(final int startX, final int startY, final int endX, final int endY, final float r, final float g, final float b) {
		drawLine(startX, startY, endX, endY, r, g, b, 1.0F);
	}
	
	/**
	 * Draws a line from {@code startX} and {@code startY} to {@code endX} and {@code endY}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the line that are inside the image will be drawn.
	 * 
	 * @param startX the X-coordinate to start the line at
	 * @param startY the Y-coordinate to start the line at
	 * @param endX the X-coordinate to end the line at
	 * @param endY the Y-coordinate to end the line at
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void drawLine(final int startX, final int startY, final int endX, final int endY, final float r, final float g, final float b, final float a) {
		final int colorARGB = doToARGB(r, g, b, a);
		
		final int w = endX - startX;
		final int h = endY - startY;
		
		final int wAbs = abs(w);
		final int hAbs = abs(h);
		
		final int dAX = w < 0 ? -1 : w > 0 ? 1 : 0;
		final int dAY = h < 0 ? -1 : h > 0 ? 1 : 0;
		final int dBX = wAbs > hAbs ? dAX : 0;
		final int dBY = wAbs > hAbs ? 0 : dAY;
		
		final int l = wAbs > hAbs ? wAbs : hAbs;
		final int s = wAbs > hAbs ? hAbs : wAbs;
		
		int n = l >> 1;
		
		int x = startX;
		int y = startY;
		
		for(int i = 0; i <= l; i++) {
			doSetColor(x, y, colorARGB);
			
			n += s;
			
			if(n >= l) {
				n -= l;
				
				x += dAX;
				y += dAY;
			} else {
				x += dBX;
				y += dBY;
			}
		}
	}
	
	/**
	 * Draws a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of black.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawRectangle(x, y, w, h, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 */
	public void drawRectangle(final int x, final int y, final int w, final int h) {
		drawRectangle(x, y, w, h, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Draws a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawRectangle(x, y, w, h, r, g, b, 1.0F)}.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void drawRectangle(final int x, final int y, final int w, final int h, final float r, final float g, final float b) {
		drawRectangle(x, y, w, h, r, g, b, 1.0F);
	}
	
	/**
	 * Draws a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be drawn.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void drawRectangle(final int x, final int y, final int w, final int h, final float r, final float g, final float b, final float a) {
		final int colorARGB = doToARGB(r, g, b, a);
		
		for(int i = y; i < y + h; i++) {
			for(int j = x; j < x + w; j++) {
				if(i == y || i + 1 == y + h || j == x || j + 1 == x + w) {
					doSetColor(j, i, colorARGB);
				}
			}
		}
	}
	
	/**
	 * Draws a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of black.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawTriangle(aX, aY, bX, bY, cX, cY, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 */
	public void drawTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY) {
		drawTriangle(aX, aY, bX, bY, cX, cY, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Draws a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be drawn.
	 * <p>
	 * Calling this method is equivalent to {@code drawTriangle(aX, aY, bX, bY, cX, cY, r, g, b, 1.0F)}.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void drawTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY, final float r, final float g, final float b) {
		drawTriangle(aX, aY, bX, bY, cX, cY, r, g, b, 1.0F);
	}
	
	/**
	 * Draws a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be drawn.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void drawTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY, final float r, final float g, final float b, final float a) {
		drawLine(aX, aY, bX, bY, r, g, b, a);
		drawLine(bX, bY, cX, cY, r, g, b, a);
		drawLine(cX, cY, aX, aY, r, g, b, a);
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the average value of the old R-, G- and B-component values.
	 */
	public void effectGrayscaleAverage() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float average = (oldR + oldG + oldB) / 3.0F;
			
			final float newR = average;
			final float newG = average;
			final float newB = average;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the value of the old B-component.
	 */
	public void effectGrayscaleB() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float newR = oldB;
			final float newG = oldB;
			final float newB = oldB;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the value of the old G-component.
	 */
	public void effectGrayscaleG() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldG = getG(i);
			final float oldA = getA(i);
			
			final float newR = oldG;
			final float newG = oldG;
			final float newB = oldG;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the lightness of the old R-, G- and B-component values.
	 */
	public void effectGrayscaleLightness() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float max = max(oldR, max(oldG, oldB));
			final float min = min(oldR, min(oldG, oldB));
			
			final float grayscale = (max + min) / 2.0F;
			
			final float newR = grayscale;
			final float newG = grayscale;
			final float newB = grayscale;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the luminance of the old R-, G- and B-component values.
	 */
	public void effectGrayscaleLuminance() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float luminance = 0.212671F * oldR + 0.715160F * oldG + 0.072169F * oldB;
			
			final float newR = luminance;
			final float newG = luminance;
			final float newB = luminance;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a grayscale effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be the value of the old R-component.
	 */
	public void effectGrayscaleR() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldA = getA(i);
			
			final float newR = oldR;
			final float newG = oldR;
			final float newB = oldR;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies an inverse effect to all pixels of the image.
	 * <p>
	 * For each pixel, the new R-, G- and B-component values will be {@code 1.0F - x}, where {@code x} refers to the old R-, G- and B-component values, respectively.
	 */
	public void effectInverse() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float newR = 1.0F - oldR;
			final float newG = 1.0F - oldG;
			final float newB = 1.0F - oldB;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a sepia effect to all pixels of the image.
	 */
	public void effectSepia() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float newR = oldR * 0.393F + oldG * 0.769F + oldB * 0.189F;
			final float newG = oldR * 0.349F + oldG * 0.686F + oldB * 0.168F;
			final float newB = oldR * 0.272F + oldG * 0.534F + oldB * 0.131F;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Fills a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of black.
	 * <p>
	 * Only the parts of the circle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillCircle(x, y, radius, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 */
	public void fillCircle(final int x, final int y, final int radius) {
		fillCircle(x, y, radius, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Fills a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the circle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillCircle(x, y, radius, r, g, b, 1.0F)}.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void fillCircle(final int x, final int y, final int radius, final float r, final float g, final float b) {
		fillCircle(x, y, radius, r, g, b, 1.0F);
	}
	
	/**
	 * Fills a circle with a center point of {@code x} and {@code y} and a radius of {@code radius}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the circle that are inside the image will be filled.
	 * 
	 * @param x the X-coordinate of the center point of the circle
	 * @param y the Y-coordinate of the center point of the circle
	 * @param radius the radius of the circle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void fillCircle(final int x, final int y, final int radius, final float r, final float g, final float b, final float a) {
		final int colorARGB = doToARGB(r, g, b, a);
		
		for(int i = -radius; i <= radius; i++) {
			for(int j = -radius; j <= radius; j++) {
				if(j * j + i * i <= radius * radius) {
					doSetColor(x + j, y + i, colorARGB);
				}
			}
		}
	}
	
	/**
	 * Fills a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of black.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillRectangle(x, y, w, h, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 */
	public void fillRectangle(final int x, final int y, final int w, final int h) {
		fillRectangle(x, y, w, h, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Fills a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillRectangle(x, y, w, h, r, g, b, 1.0F)}.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void fillRectangle(final int x, final int y, final int w, final int h, final float r, final float g, final float b) {
		fillRectangle(x, y, w, h, r, g, b, 1.0F);
	}
	
	/**
	 * Fills a rectangle from {@code x} and {@code y} to {@code x + w - 1} and {@code y + h - 1}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the rectangle that are inside the image will be filled.
	 * 
	 * @param x the X-coordinate to start the rectangle at
	 * @param y the Y-coordinate to start the rectangle at
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void fillRectangle(final int x, final int y, final int w, final int h, final float r, final float g, final float b, final float a) {
		final int colorARGB = doToARGB(r, g, b, a);
		
		for(int i = y; i < y + h; i++) {
			for(int j = x; j < x + w; j++) {
				doSetColor(j, i, colorARGB);
			}
		}
	}
	
	/**
	 * Fills a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of black.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillTriangle(aX, aY, bX, bY, cX, cY, 0.0F, 0.0F, 0.0F)}.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 */
	public void fillTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY) {
		fillTriangle(aX, aY, bX, bY, cX, cY, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Fills a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of {@code r}, {@code g} and {@code b}.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be filled.
	 * <p>
	 * Calling this method is equivalent to {@code fillTriangle(aX, aY, bX, bY, cX, cY, r, g, b, 1.0F)}.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void fillTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY, final float r, final float g, final float b) {
		fillTriangle(aX, aY, bX, bY, cX, cY, r, g, b, 1.0F);
	}
	
	/**
	 * Fills a triangle from three points, denoted {@code A}, {@code B} and {@code C}, with a color of {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * Only the parts of the triangle that are inside the image will be filled.
	 * 
	 * @param aX the X-coordinate of the point {@code A}
	 * @param aY the Y-coordinate of the point {@code A}
	 * @param bX the X-coordinate of the point {@code B}
	 * @param bY the Y-coordinate of the point {@code B}
	 * @param cX the X-coordinate of the point {@code C}
	 * @param cY the Y-coordinate of the point {@code C}
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void fillTriangle(final int aX, final int aY, final int bX, final int bY, final int cX, final int cY, final float r, final float g, final float b, final float a) {
//		TODO: Implement!
	}
	
	/**
	 * Redoes the gamma correction for the image.
	 * <p>
	 * This method assumes the colors of the image are linear.
	 * <p>
	 * The gamma correction redo operation is performed with the current color space.
	 */
	public void gammaCorrectionRedo() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float newR = oldR <= 0.0F ? 0.0F : oldR >= 1.0F ? 1.0F : oldR <= this.colorSpaceBreakPoint ? oldR * this.colorSpaceSlope : this.colorSpaceSlopeMatch * (float)(pow(oldR, 1.0F / this.colorSpaceGamma)) - this.colorSpaceSegmentOffset;
			final float newG = oldG <= 0.0F ? 0.0F : oldG >= 1.0F ? 1.0F : oldG <= this.colorSpaceBreakPoint ? oldG * this.colorSpaceSlope : this.colorSpaceSlopeMatch * (float)(pow(oldG, 1.0F / this.colorSpaceGamma)) - this.colorSpaceSegmentOffset;
			final float newB = oldB <= 0.0F ? 0.0F : oldB >= 1.0F ? 1.0F : oldB <= this.colorSpaceBreakPoint ? oldB * this.colorSpaceSlope : this.colorSpaceSlopeMatch * (float)(pow(oldB, 1.0F / this.colorSpaceGamma)) - this.colorSpaceSegmentOffset;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Undoes the gamma correction for the image.
	 * <p>
	 * This method assumes the colors of the image are non-linear and that they were gamma corrected with the same color space as the current one.
	 * <p>
	 * The gamma correction undo operation is performed with the current color space.
	 */
	public void gammaCorrectionUndo() {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i);
			final float oldG = getG(i);
			final float oldB = getB(i);
			final float oldA = getA(i);
			
			final float newR = oldR <= 0.0F ? 0.0F : oldR >= 1.0F ? 1.0F : oldR <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldR / this.colorSpaceSlope : (float)(pow((oldR + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma));
			final float newG = oldG <= 0.0F ? 0.0F : oldG >= 1.0F ? 1.0F : oldG <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldG / this.colorSpaceSlope : (float)(pow((oldG + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma));
			final float newB = oldB <= 0.0F ? 0.0F : oldB >= 1.0F ? 1.0F : oldB <= this.colorSpaceBreakPoint * this.colorSpaceSlope ? oldB / this.colorSpaceSlope : (float)(pow((oldB + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, this.colorSpaceGamma));
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Saves this {@code Image} as a .PNG image to the file represented by {@code file}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} that represents the file to save to
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public void save(final File file) {
		try {
			ImageIO.write(toBufferedImage(), "png", Objects.requireNonNull(file, "file == null"));
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Saves this {@code Image} as a .PNG image to the file represented by the filename {@code filename}.
	 * <p>
	 * If {@code filename} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} that represents the filename of the file to save to
	 * @throws NullPointerException thrown if, and only if, {@code filename} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public void save(final String filename) {
		save(new File(Objects.requireNonNull(filename, "filename == null")));
	}
	
	/**
	 * Sets the color of {@code x} and {@code y} to {@code r}, {@code g} and {@code b}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, nothing will happen.
	 * <p>
	 * Calling this method is equivalent to {@code setColor(x, y, r, g, b, 1.0F)}.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 */
	public void setColor(final int x, final int y, final float r, final float g, final float b) {
		setColor(x, y, r, g, b, 1.0F);
	}
	
	/**
	 * Sets the color of {@code x} and {@code y} to {@code r}, {@code g}, {@code b} and {@code a}.
	 * <p>
	 * If {@code x} or {@code y} are outside the image, nothing will happen.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 * @param r the R-component of the color
	 * @param g the G-component of the color
	 * @param b the B-component of the color
	 * @param a the A-component of the color
	 */
	public void setColor(final int x, final int y, final float r, final float g, final float b, final float a) {
		doSetColor(x, y, doToARGB(r, g, b, a));
	}
	
	/**
	 * Sets the color space of this {@code Image} instance.
	 * 
	 * @param breakPoint the break point to use
	 * @param gamma the gamma to use
	 * @param xR the R-component of the X-axis
	 * @param yR the R-component of the Y-axis
	 * @param xG the G-component of the X-axis
	 * @param yG the G-component of the Y-axis
	 * @param xB the B-component of the X-axis
	 * @param yB the B-component of the Y-axis
	 * @param xW the W-component of the X-axis
	 * @param yW the W-component of the Y-axis
	 */
	public void setColorSpace(final float breakPoint, final float gamma, final float xR, final float yR, final float xG, final float yG, final float xB, final float yB, final float xW, final float yW) {
		this.colorSpaceBreakPoint = breakPoint;
		this.colorSpaceGamma = gamma;
		this.colorSpaceSlope = breakPoint > 0.0F ? 1.0F / (gamma / (float)(pow(breakPoint, 1.0F / gamma - 1.0F)) - gamma * breakPoint + breakPoint) : 1.0F;
		this.colorSpaceSlopeMatch = breakPoint > 0.0F ? gamma * this.colorSpaceSlope / (float)(pow(breakPoint, 1.0F / gamma - 1.0F)) : 1.0F;
		this.colorSpaceSegmentOffset = breakPoint > 0.0F ? this.colorSpaceSlopeMatch * (float)(pow(breakPoint, 1.0F / gamma)) - this.colorSpaceSlope * breakPoint : 0.0F;
		
		for(int i = 0; i < 256; i++) {
			final float value = i / 255.0F;
			final float valueRedoGammaCorrection = value <= 0.0F ? 0.0F : value >= 1.0F ? 1.0F : value <= breakPoint ? value * this.colorSpaceSlope : this.colorSpaceSlopeMatch * (float)(pow(value, 1.0F / gamma)) - this.colorSpaceSegmentOffset;
			final float valueUndoGammaCorrection = value <= 0.0F ? 0.0F : value >= 1.0F ? 1.0F : value <= breakPoint * this.colorSpaceSlope ? value / this.colorSpaceSlope : (float)(pow((value + this.colorSpaceSegmentOffset) / this.colorSpaceSlopeMatch, gamma));
			
			this.colorSpaceGammaCurve[i] = (int)(min(max(valueRedoGammaCorrection * 255.0F + 0.5F, 0.0F), 255.0F));
			this.colorSpaceGammaCurveReciprocal[i] = (int)(min(max(valueUndoGammaCorrection * 255.0F + 0.5F, 0.0F), 255.0F));
		}
		
		float zR = 1.0F - (xR + yR);
		float zG = 1.0F - (xG + yG);
		float zB = 1.0F - (xB + yB);
		float zW = 1.0F - (xW + yW);
		
		float rX = yG * zB - yB * zG;
		float rY = xB * zG - xG * zB;
		float rZ = xG * yB - xB * yG;
		float rW = (rX * xW + rY * yW + rZ * zW) / yW;
		
		float gX = yB * zR - yR * zB;
		float gY = xR * zB - xB * zR;
		float gZ = xB * yR - xR * yB;
		float gW = (gX * xW + gY * yW + gZ * zW) / yW;
		
		float bX = yR * zG - yG * zR;
		float bY = xG * zR - xR * zG;
		float bZ = xR * yG - xG * yR;
		float bW = (bX * xW + bY * yW + bZ * zW) / yW;
		
		rX /= rW;
		rY /= rW;
		rZ /= rW;
		gX /= gW;
		gY /= gW;
		gZ /= gW;
		bX /= bW;
		bY /= bW;
		bZ /= bW;
		
		this.colorSpaceMatrixRGBToXYZ[ 0] = rX;
		this.colorSpaceMatrixRGBToXYZ[ 1] = rY;
		this.colorSpaceMatrixRGBToXYZ[ 2] = rZ;
		this.colorSpaceMatrixRGBToXYZ[ 3] = gX;
		this.colorSpaceMatrixRGBToXYZ[ 4] = gY;
		this.colorSpaceMatrixRGBToXYZ[ 5] = gZ;
		this.colorSpaceMatrixRGBToXYZ[ 6] = bX;
		this.colorSpaceMatrixRGBToXYZ[ 7] = bY;
		this.colorSpaceMatrixRGBToXYZ[ 8] = bZ;
		this.colorSpaceMatrixRGBToXYZ[ 9] = rW;
		this.colorSpaceMatrixRGBToXYZ[10] = gW;
		this.colorSpaceMatrixRGBToXYZ[11] = bW;
		
		final float s = 1.0F / (rX * (gY * bZ - bY * gZ) - rY * (gX * bZ - bX * gZ) + rZ * (gX * bY - bX * gY));
		
		this.colorSpaceMatrixXYZToRGB[ 0] = s * (gY * bZ - gZ * bY);
		this.colorSpaceMatrixXYZToRGB[ 1] = s * (gZ * bX - gX * bZ);
		this.colorSpaceMatrixXYZToRGB[ 2] = s * (gX * bY - gY * bX);
		this.colorSpaceMatrixXYZToRGB[ 3] = s * (rZ * bY - rY * bZ);
		this.colorSpaceMatrixXYZToRGB[ 4] = s * (rX * bZ - rZ * bX);
		this.colorSpaceMatrixXYZToRGB[ 5] = s * (rY * bX - rX * bY);
		this.colorSpaceMatrixXYZToRGB[ 6] = s * (rY * gZ - rZ * gY);
		this.colorSpaceMatrixXYZToRGB[ 7] = s * (rZ * gX - rX * gZ);
		this.colorSpaceMatrixXYZToRGB[ 8] = s * (rX * gY - rY * gX);
		this.colorSpaceMatrixXYZToRGB[ 9] = xW;
		this.colorSpaceMatrixXYZToRGB[10] = yW;
		this.colorSpaceMatrixXYZToRGB[11] = zW;
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to Adobe.
	 */
	public void setColorSpaceAdobe() {
		setColorSpace(0.0F, 2.2F, 0.6400F, 0.3300F, 0.2100F, 0.7100F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to Apple.
	 */
	public void setColorSpaceApple() {
		setColorSpace(0.0F, 1.8F, 0.6250F, 0.3400F, 0.2800F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to CIE.
	 */
	public void setColorSpaceCIE() {
		setColorSpace(0.0F, 2.2F, 0.7350F, 0.2650F, 0.2740F, 0.7170F, 0.1670F, 0.0090F, 1.0F / 3.0F, 1.0F / 3.0F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to EBU.
	 */
	public void setColorSpaceEBU() {
		setColorSpace(0.018F, 20.0F / 9.0F, 0.6400F, 0.3300F, 0.2900F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to HDTV.
	 */
	public void setColorSpaceHDTV() {
		setColorSpace(0.018F, 20.0F / 9.0F, 0.6400F, 0.3300F, 0.3000F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to NTSC.
	 */
	public void setColorSpaceNTSC() {
		setColorSpace(0.018F, 20.0F / 9.0F, 0.6700F, 0.3300F, 0.2100F, 0.7100F, 0.1400F, 0.0800F, 0.31010F, 0.31620F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to SMPTE-240M.
	 */
	public void setColorSpaceSMPTE240M() {
		setColorSpace(0.018F, 20.0F / 9.0F, 0.6300F, 0.3400F, 0.3100F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to SMPTE-C.
	 */
	public void setColorSpaceSMPTEC() {
		setColorSpace(0.018F, 20.0F / 9.0F, 0.6300F, 0.3400F, 0.3100F, 0.5950F, 0.1550F, 0.0700F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to sRGB.
	 */
	public void setColorSpaceSRGB() {
		setColorSpace(0.00304F, 2.4F, 0.6400F, 0.3300F, 0.3000F, 0.6000F, 0.1500F, 0.0600F, 0.31271F, 0.32902F);
	}
	
	/**
	 * Sets the color space of this {@code Image} instance to Wide Gamut.
	 */
	public void setColorSpaceWideGamut() {
		setColorSpace(0.0F, 2.2F, 0.7347F, 0.2653F, 0.1152F, 0.8264F, 0.1566F, 0.0177F, 0.3457F, 0.3585F);
	}
	
	/**
	 * Applies an ACES filmic curve tone mapping operator to the image.
	 * <p>
	 * Calling this method is equivalent to {@code toneMappingFilmicCurve(exposure, a, b, c, d, e, 0.0F, Float.MIN_VALUE)}.
	 * 
	 * @param exposure the exposure to apply to the image
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @param c a {@code float} value
	 * @param d a {@code float} value
	 * @param e a {@code float} value
	 */
	public void toneMappingFilmicCurve(final float exposure, final float a, final float b, final float c, final float d, final float e) {
		toneMappingFilmicCurve(exposure, a, b, c, d, e, 0.0F, Float.MIN_VALUE);
	}
	
	/**
	 * Applies an ACES filmic curve tone mapping operator to the image.
	 * 
	 * @param exposure the exposure to apply to the image
	 * @param a a {@code float} value
	 * @param b a {@code float} value
	 * @param c a {@code float} value
	 * @param d a {@code float} value
	 * @param e a {@code float} value
	 * @param subtract a value to subtract from each R-, G- and B-component before performing the tone mapping operation
	 * @param minimum the minimum value allowed for each R-, G- and B-component
	 */
	public void toneMappingFilmicCurve(final float exposure, final float a, final float b, final float c, final float d, final float e, final float subtract, final float minimum) {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = max(getR(i) * exposure - subtract, minimum);
			final float oldG = max(getG(i) * exposure - subtract, minimum);
			final float oldB = max(getB(i) * exposure - subtract, minimum);
			final float oldA = getA(i);
			
			final float newR = doSaturate((oldR * (a * oldR + b)) / (oldR * (c * oldR + d) + e));
			final float newG = doSaturate((oldG * (a * oldG + b)) / (oldG * (c * oldG + d) + e));
			final float newB = doSaturate((oldB * (a * oldB + b)) / (oldB * (c * oldB + d) + e));
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a modified ACES filmic curve tone mapping operator to the image.
	 * <p>
	 * To use the original ACES filmic curve, set {@code exposure} to {@code 0.6F}.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingFilmicCurveACES2(final float exposure) {
//		Source: https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/
		toneMappingFilmicCurve(exposure, 2.51F, 0.03F, 2.43F, 0.59F, 0.14F);
	}
	
	/**
	 * Applies a filmic curve tone mapping operator to the image.
	 * <p>
	 * This tone mapping operator also performs gamma correction with a gamma of 2.2. So, do not use gamma correction if this tone mapping operator is used.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingFilmicCurveGammaCorrection22(final float exposure) {
//		Source: http://filmicworlds.com/blog/why-a-filmic-curve-saturates-your-blacks/
		toneMappingFilmicCurve(exposure, 6.2F, 0.5F, 6.2F, 1.7F, 0.06F, 0.004F, 0.0F);
	}
	
	/**
	 * Applies a Reinhard tone mapping operator to the image.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingReinhard(final float exposure) {
//		Source: https://www.shadertoy.com/view/WdjSW3
		
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i) * exposure;
			final float oldG = getG(i) * exposure;
			final float oldB = getB(i) * exposure;
			final float oldA = getA(i);
			
			final float newR = oldR / (1.0F + oldR);
			final float newG = oldG / (1.0F + oldG);
			final float newB = oldB / (1.0F + oldB);
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a modified Reinhard tone mapping operator to the image.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingReinhardModifiedV1(final float exposure) {
//		Source: https://www.shadertoy.com/view/WdjSW3
		
		final float lWhite = 4.0F;
		
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i) * exposure;
			final float oldG = getG(i) * exposure;
			final float oldB = getB(i) * exposure;
			final float oldA = getA(i);
			
			final float newR = oldR * (1.0F + oldR / (lWhite * lWhite)) / (1.0F + oldR);
			final float newG = oldG * (1.0F + oldG / (lWhite * lWhite)) / (1.0F + oldG);
			final float newB = oldB * (1.0F + oldB / (lWhite * lWhite)) / (1.0F + oldB);
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies a modified Reinhard tone mapping operator to the image.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingReinhardModifiedV2(final float exposure) {
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i) * exposure;
			final float oldG = getG(i) * exposure;
			final float oldB = getB(i) * exposure;
			final float oldA = getA(i);
			
			final float newR = 1.0F - (float)(exp(-oldR * exposure));
			final float newG = 1.0F - (float)(exp(-oldG * exposure));
			final float newB = 1.0F - (float)(exp(-oldB * exposure));
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	/**
	 * Applies an Unreal 3 tone mapping operator to the image.
	 * <p>
	 * This tone mapping operator also performs gamma correction with a gamma of 2.2. So, do not use gamma correction if this tone mapping operator is used.
	 * 
	 * @param exposure the exposure to apply to the image
	 */
	public void toneMappingUnreal3(final float exposure) {
//		Source: https://www.shadertoy.com/view/WdjSW3
		
		for(int i = 0; i < this.array.length; i++) {
			final float oldR = getR(i) * exposure;
			final float oldG = getG(i) * exposure;
			final float oldB = getB(i) * exposure;
			final float oldA = getA(i);
			
			final float newR = oldR / (oldR + 0.155F) * 1.019F;
			final float newG = oldG / (oldG + 0.155F) * 1.019F;
			final float newB = oldB / (oldB + 0.155F) * 1.019F;
			final float newA = oldA;
			
			final int colorARGB = doToARGB(newR, newG, newB, newA);
			
			doSetColor(i, colorARGB);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * An {@code ArrayComponentOrder} is used to tell us what order the R-, G-, B- and A-components are stored in arrays.
	 * <p>
	 * This class has nothing to do with the way the R-, G-, B- and A-components are stored in an {@code int}, in packed form.
	 * <p>
	 * The names of the constants in this class should not be confused with the names of similar things in other libraries. They only reflect the way the components are stored in an array. The order of the letters signify the order in which they
	 * are stored in the array, starting from some offset. An example would be {@code ARGB}, where {@code A} denotes {@code offset + 0}, {@code R} denotes {@code offset + 1}, {@code G} denotes {@code offset + 2} and {@code B} denotes
	 * {@code offset + 3}.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static enum ArrayComponentOrder {
		/**
		 * The components are stored as A, R, G and B.
		 * <p>
		 * The following code demonstrates the way it is stored in an array:
		 * <pre>
		 * {@code
		 * array = new int[4];
		 * array[0] = a;
		 * array[1] = r;
		 * array[2] = g;
		 * array[3] = b;
		 * }
		 * </pre>
		 */
		ARGB(1, 2, 3, 0),
		
		/**
		 * The components are stored as B, G and R.
		 * <p>
		 * The following code demonstrates the way it is stored in an array:
		 * <pre>
		 * {@code
		 * array = new int[3];
		 * array[0] = b;
		 * array[1] = g;
		 * array[2] = r;
		 * }
		 * </pre>
		 */
		BGR(2, 1, 0, -1),
		
		/**
		 * The components are stored as B, G, R and A.
		 * <p>
		 * The following code demonstrates the way it is stored in an array:
		 * <pre>
		 * {@code
		 * array = new int[4];
		 * array[0] = b;
		 * array[1] = g;
		 * array[2] = r;
		 * array[3] = a;
		 * }
		 * </pre>
		 */
		BGRA(2, 1, 0, 3),
		
		/**
		 * The components are stored as R, G and B.
		 * <p>
		 * The following code demonstrates the way it is stored in an array:
		 * <pre>
		 * {@code
		 * array = new int[3];
		 * array[0] = r;
		 * array[1] = g;
		 * array[2] = b;
		 * }
		 * </pre>
		 */
		RGB(0, 1, 2, -1),
		
		/**
		 * The components are stored as R, G, B and A.
		 * <p>
		 * The following code demonstrates the way it is stored in an array:
		 * <pre>
		 * {@code
		 * array = new int[4];
		 * array[0] = r;
		 * array[1] = g;
		 * array[2] = b;
		 * array[3] = a;
		 * }
		 * </pre>
		 */
		RGBA(0, 1, 2, 3);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private final int offsetA;
		private final int offsetB;
		private final int offsetG;
		private final int offsetR;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private ArrayComponentOrder(final int offsetR, final int offsetG, final int offsetB, final int offsetA) {
			this.offsetR = offsetR;
			this.offsetG = offsetG;
			this.offsetB = offsetB;
			this.offsetA = offsetA;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the A-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the A-component, {@code false} otherwise
		 */
		public boolean hasOffsetA() {
			return this.offsetA != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the B-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the B-component, {@code false} otherwise
		 */
		public boolean hasOffsetB() {
			return this.offsetB != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the G-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the G-component, {@code false} otherwise
		 */
		public boolean hasOffsetG() {
			return this.offsetG != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the R-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code ArrayComponentOrder} has an offset for the R-component, {@code false} otherwise
		 */
		public boolean hasOffsetR() {
			return this.offsetR != -1;
		}
		
		/**
		 * Returns the component count of this {@code ArrayComponentOrder} instance.
		 * 
		 * @return the component count of this {@code ArrayComponentOrder} instance
		 */
		public int getComponentCount() {
			return (hasOffsetR() ? 1 : 0) + (hasOffsetG() ? 1 : 0) + (hasOffsetB() ? 1 : 0) + (hasOffsetA() ? 1 : 0);
		}
		
		/**
		 * Returns the offset for the A-component, or {@code -1} if it does not have one.
		 * 
		 * @return the offset for the A-component, or {@code -1} if it does not have one
		 */
		public int getOffsetA() {
			return this.offsetA;
		}
		
		/**
		 * Returns the offset for the B-component, or {@code -1} if it does not have one.
		 * 
		 * @return the offset for the B-component, or {@code -1} if it does not have one
		 */
		public int getOffsetB() {
			return this.offsetB;
		}
		
		/**
		 * Returns the offset for the G-component, or {@code -1} if it does not have one.
		 * 
		 * @return the offset for the G-component, or {@code -1} if it does not have one
		 */
		public int getOffsetG() {
			return this.offsetG;
		}
		
		/**
		 * Returns the offset for the R-component, or {@code -1} if it does not have one.
		 * 
		 * @return the offset for the R-component, or {@code -1} if it does not have one
		 */
		public int getOffsetR() {
			return this.offsetR;
		}
		
		/**
		 * Returns an {@code int} with the A-component, or {@code 255} if it does not have an offset for the A-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetA()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the A-component, or {@code 255} if it does not have an offset for the A-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetA()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readA(final byte[] array, final int offset) {
			return hasOffsetA() ? array[offset + getOffsetA()] & 0xFF : 255;
		}
		
		/**
		 * Returns an {@code int} with the A-component, or {@code 255} if it does not have an offset for the A-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetA()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the A-component, or {@code 255} if it does not have an offset for the A-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetA()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readA(final int[] array, final int offset) {
			return hasOffsetA() ? array[offset + getOffsetA()] & 0xFF : 255;
		}
		
		/**
		 * Returns an {@code int} with the B-component, or {@code 0} if it does not have an offset for the B-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetB()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the B-component, or {@code 0} if it does not have an offset for the B-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetB()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readB(final byte[] array, final int offset) {
			return hasOffsetB() ? array[offset + getOffsetB()] & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the B-component, or {@code 0} if it does not have an offset for the B-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetB()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the B-component, or {@code 0} if it does not have an offset for the B-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetB()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readB(final int[] array, final int offset) {
			return hasOffsetB() ? array[offset + getOffsetB()] & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the G-component, or {@code 0} if it does not have an offset for the G-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetG()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the G-component, or {@code 0} if it does not have an offset for the G-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetG()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readG(final byte[] array, final int offset) {
			return hasOffsetG() ? array[offset + getOffsetG()] & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the G-component, or {@code 0} if it does not have an offset for the G-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetG()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the G-component, or {@code 0} if it does not have an offset for the G-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetG()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readG(final int[] array, final int offset) {
			return hasOffsetG() ? array[offset + getOffsetG()] & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the R-component, or {@code 0} if it does not have an offset for the R-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetR()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the R-component, or {@code 0} if it does not have an offset for the R-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetR()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readR(final byte[] array, final int offset) {
			return hasOffsetR() ? array[offset + getOffsetR()] & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the R-component, or {@code 0} if it does not have an offset for the R-component.
		 * <p>
		 * If {@code array} is {@code null}, a {@code NullPointerException} will be thrown.
		 * <p>
		 * If {@code offset + getOffsetR()} is less than {@code 0}, or greater than or equal to {@code array.length}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
		 * 
		 * @param array the array to read from
		 * @param offset the absolute offset in the array to read from
		 * @return an {@code int} with the R-component, or {@code 0} if it does not have an offset for the R-component
		 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code offset + getOffsetR()} is less than {@code 0}, or greater than or equal to {@code array.length}
		 * @throws NullPointerException thrown if, and only if, {@code array} is {@code null}
		 */
		public int readR(final int[] array, final int offset) {
			return hasOffsetR() ? array[offset + getOffsetR()] & 0xFF : 0;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code PackedIntComponentOrder} is used to tell us what order the R-, G-, B- and A-components are stored in an {@code int}, in a packed form.
	 * <p>
	 * The names of the constants in this class signifies the order of the components, from most significant byte to least significant byte.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static enum PackedIntComponentOrder {
		/**
		 * A {@code PackedIntComponentOrder} that stores the A-, B-, G- and R-components, in that order, from most significant byte to least significant byte.
		 * <p>
		 * The components are stored in the following way:
		 * <pre>
		 * {@code
		 * int a = (colorABGR >> 24) & 0xFF;
		 * int b = (colorABGR >> 16) & 0xFF;
		 * int g = (colorABGR >>  8) & 0xFF;
		 * int r = (colorABGR >>  0) & 0xFF;
		 * }
		 * </pre>
		 */
		ABGR(0, 8, 16, 24),
		
		/**
		 * A {@code PackedIntComponentOrder} that stores the A-, R-, G- and B-components, in that order, from most significant byte to least significant byte.
		 * <p>
		 * The components are stored in the following way:
		 * <pre>
		 * {@code
		 * int a = (colorARGB >> 24) & 0xFF;
		 * int r = (colorARGB >> 16) & 0xFF;
		 * int g = (colorARGB >>  8) & 0xFF;
		 * int b = (colorARGB >>  0) & 0xFF;
		 * }
		 * </pre>
		 */
		ARGB(16, 8, 0, 24),
		
		/**
		 * A {@code PackedIntComponentOrder} that stores the B-, G- and R-components, in that order, from most significant byte to least significant byte.
		 * <p>
		 * The components are stored in the following way:
		 * <pre>
		 * {@code
		 * int b = (colorBGR >> 16) & 0xFF;
		 * int g = (colorBGR >>  8) & 0xFF;
		 * int r = (colorBGR >>  0) & 0xFF;
		 * }
		 * </pre>
		 */
		BGR(0, 8, 16, -1),
		
		/**
		 * A {@code PackedIntComponentOrder} that stores the R-, G- and B-components, in that order, from most significant byte to least significant byte.
		 * <p>
		 * The components are stored in the following way:
		 * <pre>
		 * {@code
		 * int r = (colorRGB >> 16) & 0xFF;
		 * int g = (colorRGB >>  8) & 0xFF;
		 * int b = (colorRGB >>  0) & 0xFF;
		 * }
		 * </pre>
		 */
		RGB(16, 8, 0, -1);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private final int shiftA;
		private final int shiftB;
		private final int shiftG;
		private final int shiftR;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private PackedIntComponentOrder(final int shiftR, final int shiftG, final int shiftB, final int shiftA) {
			this.shiftR = shiftR;
			this.shiftG = shiftG;
			this.shiftB = shiftB;
			this.shiftA = shiftA;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the A-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the A-component, {@code false} otherwise
		 */
		public boolean hasShiftA() {
			return this.shiftA != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the B-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the B-component, {@code false} otherwise
		 */
		public boolean hasShiftB() {
			return this.shiftB != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the G-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the G-component, {@code false} otherwise
		 */
		public boolean hasShiftG() {
			return this.shiftG != -1;
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the R-component, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code PackedIntComponentOrder} has a shift for the R-component, {@code false} otherwise
		 */
		public boolean hasShiftR() {
			return this.shiftR != -1;
		}
		
		/**
		 * Returns the component count of this {@code PackedIntComponentOrder} instance.
		 * 
		 * @return the component count of this {@code PackedIntComponentOrder} instance
		 */
		public int getComponentCount() {
			return (hasShiftR() ? 1 : 0) + (hasShiftG() ? 1 : 0) + (hasShiftB() ? 1 : 0) + (hasShiftA() ? 1 : 0);
		}
		
		/**
		 * Returns the shift for the A-component, or {@code -1} if it does not have one.
		 * 
		 * @return the shift for the A-component, or {@code -1} if it does not have one
		 */
		public int getShiftA() {
			return this.shiftA;
		}
		
		/**
		 * Returns the shift for the B-component, or {@code -1} if it does not have one.
		 * 
		 * @return the shift for the B-component, or {@code -1} if it does not have one
		 */
		public int getShiftB() {
			return this.shiftB;
		}
		
		/**
		 * Returns the shift for the G-component, or {@code -1} if it does not have one.
		 * 
		 * @return the shift for the G-component, or {@code -1} if it does not have one
		 */
		public int getShiftG() {
			return this.shiftG;
		}
		
		/**
		 * Returns the shift for the R-component, or {@code -1} if it does not have one.
		 * 
		 * @return the shift for the R-component, or {@code -1} if it does not have one
		 */
		public int getShiftR() {
			return this.shiftR;
		}
		
		/**
		 * Returns an {@code int} with {@code r}, {@code g} and {@code b} in a packed form.
		 * 
		 * @param r the R-component
		 * @param g the G-component
		 * @param b the B-component
		 * @return an {@code int} with {@code r}, {@code g} and {@code b} in a packed form
		 */
		public int pack(final int r, final int g, final int b) {
			return (hasShiftR() ? ((r & 0xFF) << getShiftR()) : 0) | (hasShiftG() ? ((g & 0xFF) << getShiftG()) : 0) | (hasShiftB() ? ((b & 0xFF) << getShiftB()) : 0);
		}
		
		/**
		 * Returns an {@code int} with {@code r}, {@code g}, {@code b} and {@code a} in a packed form.
		 * 
		 * @param r the R-component
		 * @param g the G-component
		 * @param b the B-component
		 * @param a the A-component
		 * @return an {@code int} with {@code r}, {@code g}, {@code b} and {@code a} in a packed form
		 */
		public int pack(final int r, final int g, final int b, final int a) {
			return (hasShiftR() ? ((r & 0xFF) << getShiftR()) : 0) | (hasShiftG() ? ((g & 0xFF) << getShiftG()) : 0) | (hasShiftB() ? ((b & 0xFF) << getShiftB()) : 0) | (hasShiftA() ? ((a & 0xFF) << getShiftA()) : 0);
		}
		
		/**
		 * Returns an {@code int} with the unpacked A-component, or {@code 255} if it could not unpack.
		 * 
		 * @param color an {@code int} with the components in packed form
		 * @return an {@code int} with the unpacked A-component, or {@code 255} if it could not unpack
		 */
		public int unpackA(final int color) {
			return hasShiftA() ? (color >> getShiftA()) & 0xFF : 255;
		}
		
		/**
		 * Returns an {@code int} with the unpacked B-component, or {@code 0} if it could not unpack.
		 * 
		 * @param color an {@code int} with the components in packed form
		 * @return an {@code int} with the unpacked B-component, or {@code 0} if it could not unpack
		 */
		public int unpackB(final int color) {
			return hasShiftB() ? (color >> getShiftB()) & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the unpacked G-component, or {@code 0} if it could not unpack.
		 * 
		 * @param color an {@code int} with the components in packed form
		 * @return an {@code int} with the unpacked G-component, or {@code 0} if it could not unpack
		 */
		public int unpackG(final int color) {
			return hasShiftG() ? (color >> getShiftG()) & 0xFF : 0;
		}
		
		/**
		 * Returns an {@code int} with the unpacked R-component, or {@code 0} if it could not unpack.
		 * 
		 * @param color an {@code int} with the components in packed form
		 * @return an {@code int} with the unpacked R-component, or {@code 0} if it could not unpack
		 */
		public int unpackR(final int color) {
			return hasShiftR() ? (color >> getShiftR()) & 0xFF : 0;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int doGetA(final int index) {
		return PackedIntComponentOrder.ARGB.unpackA(doGetARGB(index));
	}
	
	private int doGetARGB(final int index) {
		return index >= 0 && index < this.array.length ? this.array[index] : 0;
	}
	
	private int doGetARGB(final int x, final int y) {
		final int resolutionX = this.resolutionX;
		final int resolutionY = this.resolutionY;
		
		if(x >= 0 && x < resolutionX && y >= 0 && y < resolutionY) {
			return this.array[y * resolutionX + x];
		}
		
		return 0;
	}
	
	private int doGetB(final int index) {
		return PackedIntComponentOrder.ARGB.unpackB(doGetARGB(index));
	}
	
	private int doGetG(final int index) {
		return PackedIntComponentOrder.ARGB.unpackG(doGetARGB(index));
	}
	
	private int doGetR(final int index) {
		return PackedIntComponentOrder.ARGB.unpackR(doGetARGB(index));
	}
	
	private void doSetColor(final int index, final int colorARGB) {
		if(index >= 0 && index < this.array.length) {
			this.array[index] = colorARGB;
		}
	}
	
	private void doSetColor(final int x, final int y, final int colorARGB) {
		final int resolutionX = this.resolutionX;
		final int resolutionY = this.resolutionY;
		
		if(x >= 0 && x < resolutionX && y >= 0 && y < resolutionY) {
			this.array[y * resolutionX + x] = colorARGB;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static BufferedImage doLoadBufferedImage(final File file) {
		try {
			final BufferedImage bufferedImage0 = ImageIO.read(Objects.requireNonNull(file, "file == null"));
			final BufferedImage bufferedImage1 = new BufferedImage(bufferedImage0.getWidth(), bufferedImage0.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			final
			Graphics2D graphics2D = bufferedImage1.createGraphics();
			graphics2D.drawImage(bufferedImage0, 0, 0, null);
			
			return bufferedImage1;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static float doSaturate(final float value) {
		return doSaturate(value, 0.0F, 1.0F);
	}
	
	private static float doSaturate(final float value, final float minimum, final float maximum) {
		return value < minimum ? minimum : value > maximum ? maximum : value;
	}
	
	private static float doToFloat(final int value) {
		return value / 255.0F;
	}
	
	private static int doToARGB(final float r, final float g, final float b, final float a) {
		return doToARGB(doToInt(r), doToInt(g), doToInt(b), doToInt(a));
	}
	
	private static int doToARGB(final int r, final int g, final int b, final int a) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}
	
	private static int[] doCreateIntArrayFromByteArray(final int resolutionX, final int resolutionY, final byte[] array, final ArrayComponentOrder arrayComponentOrder) {
		final int componentCount = arrayComponentOrder.getComponentCount();
		
		final int length = resolutionX * resolutionY;
		final int lengthExpected = length * componentCount;
		
		if(array.length == lengthExpected) {
			final int[] arrayResult = new int[length];
			
			for(int i = 0, j = 0; i < length; i++, j += componentCount) {
				final int r = arrayComponentOrder.readR(array, j);
				final int g = arrayComponentOrder.readG(array, j);
				final int b = arrayComponentOrder.readB(array, j);
				final int a = arrayComponentOrder.readA(array, j);
				
				final int colorARGB = doToARGB(r, g, b, a);
				
				arrayResult[i] = colorARGB;
			}
			
			return arrayResult;
		}
		
		throw new IllegalArgumentException(String.format("Expected a length of %s but found %s.", Integer.toString(lengthExpected), Integer.toString(array.length)));
	}
	
	private static int[] doCreateIntArrayFromIntArray(final int resolutionX, final int resolutionY, final int[] array, final ArrayComponentOrder arrayComponentOrder) {
		final int componentCount = arrayComponentOrder.getComponentCount();
		
		final int length = resolutionX * resolutionY;
		final int lengthExpected = length * componentCount;
		
		if(array.length == lengthExpected) {
			final int[] arrayResult = new int[length];
			
			for(int i = 0, j = 0; i < length; i++, j += componentCount) {
				final int r = arrayComponentOrder.readR(array, j);
				final int g = arrayComponentOrder.readG(array, j);
				final int b = arrayComponentOrder.readB(array, j);
				final int a = arrayComponentOrder.readA(array, j);
				
				final int colorARGB = doToARGB(r, g, b, a);
				
				arrayResult[i] = colorARGB;
			}
			
			return arrayResult;
		}
		
		throw new IllegalArgumentException(String.format("Expected a length of %s but found %s.", Integer.toString(lengthExpected), Integer.toString(array.length)));
	}
	
	private static int[] doCreateIntArrayFromIntArray(final int resolutionX, final int resolutionY, final int[] array, final PackedIntComponentOrder packedIntComponentOrder) {
		final int length = resolutionX * resolutionY;
		
		if(array.length == length) {
			final int[] arrayResult = new int[length];
			
			for(int i = 0; i < length; i++) {
				final int r = packedIntComponentOrder.unpackR(array[i]);
				final int g = packedIntComponentOrder.unpackG(array[i]);
				final int b = packedIntComponentOrder.unpackB(array[i]);
				final int a = packedIntComponentOrder.unpackA(array[i]);
				
				final int colorARGB = doToARGB(r, g, b, a);
				
				arrayResult[i] = colorARGB;
			}
		}
		
		throw new IllegalArgumentException(String.format("Expected a length of %s but found %s.", Integer.toString(length), Integer.toString(array.length)));
	}
	
	private static int doToInt(final float value) {
		return (int)(doSaturate(value) * 255.0F + 0.5F);
	}
}