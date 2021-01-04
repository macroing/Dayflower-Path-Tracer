/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.scene.texture;

import static org.macroing.math4j.MathF.abs;
import static org.macroing.math4j.MathF.cos;
import static org.macroing.math4j.MathF.remainder;
import static org.macroing.math4j.MathF.sin;
import static org.macroing.math4j.MathF.toRadians;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Texture;
import org.macroing.image4j.Color;
import org.macroing.image4j.PackedIntComponentOrder;
import org.macroing.image4j.RGBColorSpace;

/**
 * An {@code ImageTexture} is a {@link Texture} implementation that models a texture based on an image.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ImageTexture implements Texture {
	/**
	 * The relative offset of the Data parameter in the {@code float} array. The value is {@code 8}.
	 */
	public static final int RELATIVE_OFFSET_DATA = 8;
	
	/**
	 * The relative offset of the Height parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int RELATIVE_OFFSET_HEIGHT = 5;
	
	/**
	 * The relative offset of the Radians Cos parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int RELATIVE_OFFSET_RADIANS_COS = 2;
	
	/**
	 * The relative offset of the Radians Sin parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int RELATIVE_OFFSET_RADIANS_SIN = 3;
	
	/**
	 * The relative offset of the Scale U parameter in the {@code float} array. The value is {@code 6}.
	 */
	public static final int RELATIVE_OFFSET_SCALE_U = 6;
	
	/**
	 * The relative offset of the Scale V parameter in the {@code float} array. The value is {@code 7}.
	 */
	public static final int RELATIVE_OFFSET_SCALE_V = 7;
	
	/**
	 * The relative offset of the Width parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int RELATIVE_OFFSET_WIDTH = 4;
	
	/**
	 * The type number associated with a {@code ImageTexture}. The number is {@code 6}.
	 */
	public static final int TYPE = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float degrees;
	private final float height;
	private final float radians;
	private final float scaleU;
	private final float scaleV;
	private final float width;
	private final int[] data;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ImageTexture(final float degrees, final float width, final float height, final float scaleU, final float scaleV, final int[] data) {
		this.degrees = degrees;
		this.width = width;
		this.height = height;
		this.scaleU = scaleU;
		this.scaleV = scaleV;
		this.data = data;
		this.radians = toRadians(this.degrees);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link Color} with the color of this {@code ImageTexture} at {@code primitiveIntersection}.
	 * <p>
	 * If {@code primitiveIntersection} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitiveIntersection a {@link PrimitiveIntersection}
	 * @return a {@code Color} with the color of this {@code ImageTexture} at {@code primitiveIntersection}
	 * @throws NullPointerException thrown if, and only if, {@code primitiveIntersection} is {@code null}
	 */
	@Override
	public Color getColor(final PrimitiveIntersection primitiveIntersection) {
		final float u = primitiveIntersection.getShapeIntersection().getTextureCoordinates().x;
		final float v = primitiveIntersection.getShapeIntersection().getTextureCoordinates().y;
		
		final float width = getWidth();
		final float height = getHeight();
		
		final float scaleU = getScaleU();
		final float scaleV = getScaleV();
		
		final float cosAngle = cos(getRadians());
		final float sinAngle = sin(getRadians());
		
		final float u0 = (u * cosAngle - v * sinAngle);
		final float v0 = (v * cosAngle + u * sinAngle);
		final float u1 = remainder(u0 * scaleU * width, width);
		final float v1 = remainder(v0 * scaleV * height, height);
		final float u2 = u1 >= 0.0F ? u1 : width - abs(u1);
		final float v2 = v1 >= 0.0F ? v1 : height - abs(v1);
		
		final int x = (int)(u2);
		final int y = (int)(v2);
		
		final int x00 = x + 0;
		final int y00 = y + 0;
		final int x01 = x + 1;
		final int y01 = y + 0;
		final int x10 = x + 0;
		final int y10 = y + 1;
		final int x11 = x + 1;
		final int y11 = y + 1;
		
		final int w = (int)(width);
		
		final int index00 = y00 * w + x00;
		final int index01 = y01 * w + x01;
		final int index10 = y10 * w + x10;
		final int index11 = y11 * w + x11;
		
		final int colorRGB00 = index00 >= 0 && index00 < this.data.length ? this.data[index00] : 0;
		final int colorRGB01 = index01 >= 0 && index01 < this.data.length ? this.data[index01] : 0;
		final int colorRGB10 = index10 >= 0 && index10 < this.data.length ? this.data[index10] : 0;
		final int colorRGB11 = index11 >= 0 && index11 < this.data.length ? this.data[index11] : 0;
		
		final float factorX = u2 - x;
		final float factorY = v2 - y;
		
		final Color color00 = new Color(colorRGB00);
		final Color color01 = new Color(colorRGB01);
		final Color color10 = new Color(colorRGB10);
		final Color color11 = new Color(colorRGB11);
		
		final Color color = Color.blend(Color.blend(color00, color01, factorX), Color.blend(color10, color11, factorX), factorY);
		
		return color;
	}
	
	/**
	 * Performs a Gamma Correction redo operation.
	 * <p>
	 * Returns this {@code ImageTexture} instance.
	 * 
	 * @return this {@code ImageTexture} instance
	 */
	public ImageTexture redoGammaCorrection() {
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = new Color(this.data[i]).redoGammaCorrection(RGBColorSpace.SRGB).pack();
		}
		
		return this;
	}
	
	/**
	 * Performs a Gamma Correction undo operation.
	 * <p>
	 * Returns this {@code ImageTexture} instance.
	 * 
	 * @return this {@code ImageTexture} instance
	 */
	public ImageTexture undoGammaCorrection() {
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = new Color(this.data[i]).undoGammaCorrection(RGBColorSpace.SRGB).pack();
		}
		
		return this;
	}
	
	/**
	 * Returns a {@code String} representation of this {@code ImageTexture} instance.
	 * 
	 * @return a {@code String} representation of this {@code ImageTexture} instance
	 */
	@Override
	public String toString() {
		return String.format("new ImageTexture(%s, %s, %s, %s, %s, data)", Float.toString(this.degrees), Float.toString(this.width), Float.toString(this.height), Float.toString(this.scaleU), Float.toString(this.scaleV));
	}
	
	/**
	 * Compares {@code object} to this {@code ImageTexture} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code ImageTexture}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code ImageTexture} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code ImageTexture}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof ImageTexture)) {
			return false;
		} else if(Float.compare(this.degrees, ImageTexture.class.cast(object).degrees) != 0) {
			return false;
		} else if(Float.compare(this.height, ImageTexture.class.cast(object).height) != 0) {
			return false;
		} else if(Float.compare(this.radians, ImageTexture.class.cast(object).radians) != 0) {
			return false;
		} else if(Float.compare(this.scaleU, ImageTexture.class.cast(object).scaleU) != 0) {
			return false;
		} else if(Float.compare(this.scaleV, ImageTexture.class.cast(object).scaleV) != 0) {
			return false;
		} else if(Float.compare(this.width, ImageTexture.class.cast(object).width) != 0) {
			return false;
		} else if(!Arrays.equals(this.data, ImageTexture.class.cast(object).data)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code ImageTexture} instance is emissive, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code ImageTexture} instance is emissive, {@code false} otherwise
	 */
	@Override
	public boolean isEmissive() {
		for(final int dataElement : this.data) {
			if(dataElement > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the angle in degrees that this {@code ImageTexture} instance should be rotated.
	 * 
	 * @return the angle in degrees that this {@code ImageTexture} instance should be rotated
	 */
	public float getDegrees() {
		return this.degrees;
	}
	
	/**
	 * Returns the height of this {@code ImageTexture} instance.
	 * 
	 * @return the height of this {@code ImageTexture} instance
	 */
	public float getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the angle in radians that this {@code ImageTexture} instance should be rotated.
	 * 
	 * @return the angle in radians that this {@code ImageTexture} instance should be rotated
	 */
	public float getRadians() {
		return this.radians;
	}
	
	/**
	 * Returns the scale factor in the U-direction assigned to this {@code ImageTexture} instance.
	 * 
	 * @return the scale factor in the U-direction assigned to this {@code ImageTexture} instance
	 */
	public float getScaleU() {
		return this.scaleU;
	}
	
	/**
	 * Returns the scale factor in the V-direction assigned to this {@code ImageTexture} instance.
	 * 
	 * @return the scale factor in the V-direction assigned to this {@code ImageTexture} instance
	 */
	public float getScaleV() {
		return this.scaleV;
	}
	
	/**
	 * Returns the width of this {@code ImageTexture} instance.
	 * 
	 * @return the width of this {@code ImageTexture} instance
	 */
	public float getWidth() {
		return this.width;
	}
	
	/**
	 * Returns a {@code float} array representation of this {@code ImageTexture} instance.
	 * 
	 * @return a {@code float} array representation of this {@code ImageTexture} instance
	 */
	@Override
	public float[] toArray() {
		final int size = 8 + this.data.length;
		
		final float[] array = new float[size];
		
		array[0] = TYPE;
		array[1] = size;
		array[2] = cos(getRadians());
		array[3] = sin(getRadians());
		array[4] = getWidth();
		array[5] = getHeight();
		array[6] = getScaleU();
		array[7] = getScaleV();
		
		final int[] data = this.data;
		
		for(int i = 0; i < data.length; i++) {
			array[i + 8] = data[i];
		}
		
		return array;
	}
	
	/**
	 * Returns the length of the data in this {@code ImageTexture} instance.
	 * 
	 * @return the length of the data in this {@code ImageTexture} instance
	 */
	public int getDataLength() {
		return this.data.length;
	}
	
	/**
	 * Returns the size of this {@code ImageTexture} instance.
	 * 
	 * @return the size of this {@code ImageTexture} instance
	 */
	@Override
	public int getSize() {
		return 8 + this.data.length;
	}
	
	/**
	 * Returns the type of this {@code ImageTexture} instance.
	 * 
	 * @return the type of this {@code ImageTexture} instance
	 */
	@Override
	public int getType() {
		return TYPE;
	}
	
	/**
	 * Returns a hash code for this {@code ImageTexture} instance.
	 * 
	 * @return a hash code for this {@code ImageTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.degrees), Float.valueOf(this.height), Float.valueOf(this.radians), Float.valueOf(this.scaleU), Float.valueOf(this.scaleV), Float.valueOf(this.width), Integer.valueOf(Arrays.hashCode(this.data)));
	}
	
	/**
	 * Returns the data of the image as an {@code int} array.
	 * <p>
	 * Modifying the array will not affect this {@code ImageTexture} instance.
	 * 
	 * @return the data of the image as an {@code int} array
	 */
	public int[] getData() {
		return this.data.clone();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loads an {@code ImageTexture} from a file.
	 * <p>
	 * Returns an {@code ImageTexture} instance.
	 * <p>
	 * Calling this method is equivalent to calling {@code ImageTexture.load(file, 0.0F)}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs when loading the image, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} denoting a file to load from
	 * @return an {@code ImageTexture} instance
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs when loading the image
	 */
	public static ImageTexture load(final File file) {
		return load(file, 0.0F);
	}
	
	/**
	 * Loads an {@code ImageTexture} from a file given an angle in degrees to rotate it.
	 * <p>
	 * Returns an {@code ImageTexture} instance.
	 * <p>
	 * Calling this method is equivalent to calling {@code ImageTexture.load(file, degrees, 1.0F, 1.0F)}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs when loading the image, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} denoting a file to load from
	 * @param degrees an angle in degrees to rotate the {@code ImageTexture}
	 * @return an {@code ImageTexture} instance
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs when loading the image
	 */
	public static ImageTexture load(final File file, final float degrees) {
		return load(file, degrees, 1.0F, 1.0F);
	}
	
	/**
	 * Loads an {@code ImageTexture} from a file given an angle in degrees to rotate it and the scale factors in the U- and V-directions.
	 * <p>
	 * Returns an {@code ImageTexture} instance.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs when loading the image, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} denoting a file to load from
	 * @param degrees an angle in degrees to rotate the {@code ImageTexture}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @return an {@code ImageTexture} instance
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs when loading the image
	 */
	public static ImageTexture load(final File file, final float degrees, final float scaleU, final float scaleV) {
		final BufferedImage bufferedImage = doCreateBufferedImageFrom(Objects.requireNonNull(file, "file == null"));
		
		final int width = bufferedImage.getWidth();
		final int height = bufferedImage.getHeight();
		
		final int[] data = doGetDataFrom(bufferedImage);
		
		return new ImageTexture(degrees, width, height, scaleU, scaleV, data);
	}
	
	/**
	 * Returns a randomly generated {@code ImageTexture} based on its width and height.
	 * 
	 * @param width the width of the {@code ImageTexture}
	 * @param height the height of the {@code ImageTexture}
	 * @param degrees an angle in degrees to rotate the {@code ImageTexture}
	 * @param scaleU the scale factor in the U-direction
	 * @param scaleV the scale factor in the V-direction
	 * @return a randomly generated {@code ImageTexture} based on its width and height
	 */
	public static ImageTexture random(final int width, final int height, final float degrees, final float scaleU, final float scaleV) {
		final int[] data = new int[width * height];
		
		for(int i = 0; i < data.length; i++) {
			final int r = ThreadLocalRandom.current().nextInt(0, 256);
			final int g = ThreadLocalRandom.current().nextInt(0, 256);
			final int b = ThreadLocalRandom.current().nextInt(0, 256);
			
			final int rGB = PackedIntComponentOrder.ARGB.pack(r, g, b);
			
			data[i] = rGB;
		}
		
		return new ImageTexture(degrees, width, height, scaleU, scaleV, data);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static BufferedImage doCreateBufferedImageFrom(final File file) {
		try(final InputStream inputStream = new FileInputStream(file)) {
			return doCreateBufferedImageFrom(inputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static BufferedImage doCreateBufferedImageFrom(final InputStream inputStream) {
		try(final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
			BufferedImage bufferedImage0 = ImageIO.read(bufferedInputStream);
			
			if(bufferedImage0.getType() != BufferedImage.TYPE_INT_RGB) {
				final BufferedImage bufferedImage1 = new BufferedImage(bufferedImage0.getWidth(), bufferedImage0.getHeight(), BufferedImage.TYPE_INT_RGB);
				
				final
				Graphics2D graphics2D = bufferedImage1.createGraphics();
				graphics2D.drawImage(bufferedImage0, 0, 0, null);
				graphics2D.dispose();
				
				bufferedImage0 = bufferedImage1;
			}
			
			return bufferedImage0;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static int[] doGetDataFrom(final BufferedImage bufferedImage) {
		final WritableRaster writableRaster = bufferedImage.getRaster();
		
		final DataBuffer dataBuffer = writableRaster.getDataBuffer();
		
		final DataBufferInt dataBufferInt = DataBufferInt.class.cast(dataBuffer);
		
		final int[] data = dataBufferInt.getData();
		
		return data;
	}
}