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
package org.dayflower.pathtracer.scene.texture;

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

import javax.imageio.ImageIO;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.colorspace.RGBColorSpace;
import org.dayflower.pathtracer.scene.Texture;

/**
 * An {@code ImageTexture} is a {@link Texture} implementation that models a texture based on an image.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ImageTexture implements Texture {
	private final float degrees;
	private final float height;
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
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public ImageTexture redoGammaCorrection() {
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = new Color(this.data[i]).redoGammaCorrection(RGBColorSpace.SRGB).multiply(255.0F).toRGB();
		}
		
		return this;
	}
	
//	TODO: Add Javadocs.
	public ImageTexture undoGammaCorrection() {
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = new Color(this.data[i]).undoGammaCorrection(RGBColorSpace.SRGB).multiply(255.0F).toRGB();
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
	 * Returns the length of the data in this {@code ImageTexture} instance.
	 * 
	 * @return the length of the data in this {@code ImageTexture} instance
	 */
	public int getDataLength() {
		return this.data.length;
	}
	
	/**
	 * Returns a hash code for this {@code ImageTexture} instance.
	 * 
	 * @return a hash code for this {@code ImageTexture} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.degrees), Float.valueOf(this.height), Float.valueOf(this.scaleU), Float.valueOf(this.scaleV), Float.valueOf(this.width), Integer.valueOf(Arrays.hashCode(this.data)));
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