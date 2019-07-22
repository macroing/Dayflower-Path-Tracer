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
package org.dayflower.pathtracer.scene.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Arrays;
import java.util.Objects;

import org.dayflower.pathtracer.scene.Camera;

public final class CompiledScene {
	private final String name;
	private final float[] boundingVolumeHierarchy;
	private final float[] camera;
	private final float[] point2Fs;
	private final float[] point3Fs;
	private final float[] shapes;
	private final float[] surfaces;
	private final float[] textures;
	private final float[] vector3Fs;
	private final int[] shapeOffsets;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public CompiledScene(final String name, final float[] boundingVolumeHierarchy, final float[] camera, final float[] point2Fs, final float[] point3Fs, final float[] shapes, final float[] surfaces, final float[] textures, final float[] vector3Fs, final int[] shapeOffsets) {
		this.name = Objects.requireNonNull(name, "name == null");
		this.boundingVolumeHierarchy = Objects.requireNonNull(boundingVolumeHierarchy, "boundingVolumeHierarchy == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.point2Fs = Objects.requireNonNull(point2Fs, "point2Fs == null");
		this.point3Fs = Objects.requireNonNull(point3Fs, "point3Fs == null");
		this.shapes = Objects.requireNonNull(shapes, "shapes == null");
		this.surfaces = Objects.requireNonNull(surfaces, "surfaces == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.vector3Fs = Objects.requireNonNull(vector3Fs, "vector3Fs == null");
		this.shapeOffsets = Objects.requireNonNull(shapeOffsets, "shapeOffsets == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float[] getBoundingVolumeHierarchy() {
		return this.boundingVolumeHierarchy;
	}
	
//	TODO: Add Javadocs.
	public float[] getCamera() {
		return this.camera;
	}
	
//	TODO: Add Javadocs.
	public float[] getPoint2Fs() {
		return this.point2Fs;
	}
	
//	TODO: Add Javadocs.
	public float[] getPoint3Fs() {
		return this.point3Fs;
	}
	
//	TODO: Add Javadocs.
	public float[] getShapes() {
		return this.shapes;
	}
	
//	TODO: Add Javadocs.
	public float[] getSurfaces() {
		return this.surfaces;
	}
	
//	TODO: Add Javadocs.
	public float[] getTextures() {
		return this.textures;
	}
	
//	TODO: Add Javadocs.
	public float[] getVector3Fs() {
		return this.vector3Fs;
	}
	
//	TODO: Add Javadocs.
	public int[] getShapeOffsets() {
		return this.shapeOffsets;
	}
	
//	TODO: Add Javadocs.
	public String getName() {
		return this.name;
	}
	
//	TODO: Add Javadocs.
	@Override
	public String toString() {
		final
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("BoundingVolumeHierarchy: " + Arrays.toString(this.boundingVolumeHierarchy) + "\n");
		stringBuilder.append("Camera: " + Arrays.toString(this.camera) + "\n");
		stringBuilder.append("Point2Fs: " + Arrays.toString(this.point2Fs) + "\n");
		stringBuilder.append("Point3s: " + Arrays.toString(this.point3Fs) + "\n");
		stringBuilder.append("Shapes: " + Arrays.toString(this.shapes) + "\n");
		stringBuilder.append("Surfaces: " + Arrays.toString(this.surfaces) + "\n");
		stringBuilder.append("Textures: " + Arrays.toString(this.textures) + "\n");
		stringBuilder.append("Vector3Fs: " + Arrays.toString(this.vector3Fs) + "\n");
		stringBuilder.append("ShapeOffsets: " + Arrays.toString(this.shapeOffsets) + "\n");
		
		return stringBuilder.toString();
	}
	
//	TODO: Add Javadocs.
	public void write(final File file) {
		final File parentFile = file.getParentFile();
		
		if(parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		
		try(final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Objects.requireNonNull(file, "file == null"))))) {
			write(dataOutputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public void write(final DataOutputStream dataOutputStream) {
		try {
			dataOutputStream.writeUTF(this.name);
			
			doWriteFloatArray(dataOutputStream, this.boundingVolumeHierarchy);
			doWriteFloatArray(dataOutputStream, this.camera);
			doWriteFloatArray(dataOutputStream, this.point2Fs);
			doWriteFloatArray(dataOutputStream, this.point3Fs);
			doWriteFloatArray(dataOutputStream, this.shapes);
			doWriteFloatArray(dataOutputStream, this.surfaces);
			doWriteFloatArray(dataOutputStream, this.textures);
			doWriteFloatArray(dataOutputStream, this.vector3Fs);
			doWriteIntArray(dataOutputStream, this.shapeOffsets);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene read(final Camera camera, final DataInputStream dataInputStream) {
		try {
			final String name = dataInputStream.readUTF();
			
			final float[] boundingVolumeHierarchy = doReadFloatArray(dataInputStream);
			final float[] cameraArray = doReadFloatArray(dataInputStream, camera.getArray());
			final float[] point2Fs = doReadFloatArray(dataInputStream);
			final float[] point3Fs = doReadFloatArray(dataInputStream);
			final float[] shapes = doReadFloatArray(dataInputStream);
			final float[] surfaces = doReadFloatArray(dataInputStream);
			final float[] textures = doReadFloatArray(dataInputStream);
			final float[] vector3s = doReadFloatArray(dataInputStream);
			
			final int[] shapeOffsets = doReadIntArray(dataInputStream);
			
			return new CompiledScene(name, boundingVolumeHierarchy, cameraArray, point2Fs, point3Fs, shapes, surfaces, textures, vector3s, shapeOffsets);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene read(final Camera camera, final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(Objects.requireNonNull(file, "file == null"))))) {
			return read(camera, dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float[] doReadFloatArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final float[] array = new float[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
	private static float[] doReadFloatArray(final DataInputStream dataInputStream, final float[] array) throws IOException {
		final int length = dataInputStream.readInt();
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
	private static int[] doReadIntArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final int[] array = new int[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readInt();
		}
		
		return array;
	}
	
	private static void doWriteFloatArray(final DataOutputStream dataOutputStream, final float[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final float value : array) {
			dataOutputStream.writeFloat(value);
		}
	}
	
	private static void doWriteIntArray(final DataOutputStream dataOutputStream, final int[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final int value : array) {
			dataOutputStream.writeInt(value);
		}
	}
}