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
package org.dayflower.pathtracer.scene.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

import javax.imageio.ImageIO;

import org.dayflower.pathtracer.scene.Texture;

//TODO: Add Javadocs.
public final class ImageTexture extends Texture {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_DATA = 7;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_DEGREES = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_HEIGHT = 4;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SCALE_U = 5;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SCALE_V = 6;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_WIDTH = 3;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float degrees;
	private final float height;
	private final float scaleU;
	private final float scaleV;
	private final float width;
	private final float[] data;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private ImageTexture(final float degrees, final float width, final float height, final float scaleU, final float scaleV, final float[] data) {
		this.degrees = degrees;
		this.width = width;
		this.height = height;
		this.scaleU = scaleU;
		this.scaleV = scaleV;
		this.data = data;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float getDegrees() {
		return this.degrees;
	}
	
//	TODO: Add Javadocs.
	public float getHeight() {
		return this.height;
	}
	
//	TODO: Add Javadocs.
	public float getScaleU() {
		return this.scaleU;
	}
	
//	TODO: Add Javadocs.
	public float getScaleV() {
		return this.scaleV;
	}
	
//	TODO: Add Javadocs.
	public float getWidth() {
		return this.width;
	}
	
//	TODO: Add Javadocs.
	public float[] getData() {
		return this.data.clone();
	}
	
//	TODO: Add Javadocs.
	@Override
	public float[] toFloatArray() {
		final float[] floatArray = new float[size()];
		
		floatArray[0] = TYPE;
		floatArray[1] = size();
		floatArray[2] = getDegrees();
		floatArray[3] = getWidth();
		floatArray[4] = getHeight();
		floatArray[5] = getScaleU();
		floatArray[6] = getScaleV();
		
		for(int i = 0; i < this.data.length; i++) {
			floatArray[i + 7] = this.data[i];
		}
		
		return floatArray;
	}
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return 7 + this.data.length;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static ImageTexture load(final File file) {
		return load(file, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public static ImageTexture load(final File file, final float degrees) {
		return load(file, degrees, 1.0F, 1.0F);
	}
	
//	TODO: Add Javadocs.
	public static ImageTexture load(final File file, final float degrees, final float scaleU, final float scaleV) {
		final BufferedImage bufferedImage = doCreateBufferedImageFrom(Objects.requireNonNull(file, "file == null"));
		
		final int width = bufferedImage.getWidth();
		final int height = bufferedImage.getHeight();
		
		final int[] data0 = doGetDataFrom(bufferedImage);
		
		final float[] data1 = new float[width * height];
		
		for(int i = 0; i < data0.length; i++) {
			data1[i] = data0[i];
		}
		
		return new ImageTexture(degrees, width, height, scaleU, scaleV, data1);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static BufferedImage doCreateBufferedImageFrom(final File file) {
		try {
			return doCreateBufferedImageFrom(new FileInputStream(file));
		} catch(final FileNotFoundException e) {
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