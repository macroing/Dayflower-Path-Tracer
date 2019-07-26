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
import java.util.Objects;

import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.util.Arrays2;

//TODO: Add Javadocs.
public final class CompiledScene {
	private final String name;
	private final float[] camera;
	private final float[] point2Fs;
	private final float[] point3Fs;
	private final float[] spheres;
	private final float[] surfaces;
	private final float[] terrains;
	private final float[] textures;
	private final float[] vector3Fs;
	private final int[] boundingVolumeHierarchies;
	private final int[] planes;
	private final int[] primitives;
	private final int[] primitivesEmittingLight;
	private final int[] triangles;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public CompiledScene(final String name, final float[] camera, final float[] point2Fs, final float[] point3Fs, final float[] spheres, final float[] surfaces, final float[] terrains, final float[] textures, final float[] vector3Fs, final int[] boundingVolumeHierarchies, final int[] planes, final int[] primitives, final int[] primitivesEmittingLight, final int[] triangles) {
		this.name = Objects.requireNonNull(name, "name == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.point2Fs = Objects.requireNonNull(point2Fs, "point2Fs == null");
		this.point3Fs = Objects.requireNonNull(point3Fs, "point3Fs == null");
		this.spheres = Objects.requireNonNull(spheres, "spheres == null");
		this.surfaces = Objects.requireNonNull(surfaces, "surfaces == null");
		this.terrains = Objects.requireNonNull(terrains, "terrains == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.vector3Fs = Objects.requireNonNull(vector3Fs, "vector3Fs == null");
		this.boundingVolumeHierarchies = Objects.requireNonNull(boundingVolumeHierarchies, "boundingVolumeHierarchies == null");
		this.planes = Objects.requireNonNull(planes, "planes == null");
		this.primitives = Objects.requireNonNull(primitives, "primitives == null");
		this.primitivesEmittingLight = Objects.requireNonNull(primitivesEmittingLight, "primitivesEmittingLight == null");
		this.triangles = Objects.requireNonNull(triangles, "triangles == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Camera toCamera() {
		return new Camera(this.camera.clone());
	}
	
//	TODO: Add Javadocs.
	public String getName() {
		return this.name;
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
	public float[] getSpheres() {
		return this.spheres;
	}
	
//	TODO: Add Javadocs.
	public float[] getSurfaces() {
		return this.surfaces;
	}
	
//	TODO: Add Javadocs.
	public float[] getTerrains() {
		return this.terrains;
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
	public int[] getBoundingVolumeHierarchies() {
		return this.boundingVolumeHierarchies;
	}
	
//	TODO: Add Javadocs.
	public int[] getPlanes() {
		return this.planes;
	}
	
//	TODO: Add Javadocs.
	public int[] getPrimitives() {
		return this.primitives;
	}
	
//	TODO: Add Javadocs.
	public int[] getPrimitivesEmittingLight() {
		return this.primitivesEmittingLight;
	}
	
//	TODO: Add Javadocs.
	public int[] getTriangles() {
		return this.triangles;
	}
	
//	TODO: Add Javadocs.
	public void write(final DataOutputStream dataOutputStream) {
		try {
			dataOutputStream.writeUTF(this.name);
			
			Arrays2.writeFloatArray(dataOutputStream, this.point2Fs);
			Arrays2.writeFloatArray(dataOutputStream, this.point3Fs);
			Arrays2.writeFloatArray(dataOutputStream, this.vector3Fs);
			Arrays2.writeIntArray(dataOutputStream, this.planes);
			Arrays2.writeFloatArray(dataOutputStream, this.spheres);
			Arrays2.writeFloatArray(dataOutputStream, this.terrains);
			Arrays2.writeIntArray(dataOutputStream, this.triangles);
			Arrays2.writeIntArray(dataOutputStream, this.boundingVolumeHierarchies);
			Arrays2.writeFloatArray(dataOutputStream, this.textures);
			Arrays2.writeFloatArray(dataOutputStream, this.surfaces);
			Arrays2.writeIntArray(dataOutputStream, this.primitives);
			Arrays2.writeIntArray(dataOutputStream, this.primitivesEmittingLight);
			Arrays2.writeFloatArray(dataOutputStream, this.camera);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public void write(final File file) {
		final File parentFile = file.getParentFile();
		
		if(parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
		
		try(final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			write(dataOutputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene read(final DataInputStream dataInputStream) {
		try {
			final String name = dataInputStream.readUTF();
			
			final float[] point2Fs = Arrays2.readFloatArray(dataInputStream);
			final float[] point3Fs = Arrays2.readFloatArray(dataInputStream);
			final float[] vector3Fs = Arrays2.readFloatArray(dataInputStream);
			final int[] planes = Arrays2.readIntArray(dataInputStream);
			final float[] spheres = Arrays2.readFloatArray(dataInputStream);
			final float[] terrains = Arrays2.readFloatArray(dataInputStream);
			final int[] triangles = Arrays2.readIntArray(dataInputStream);
			final int[] boundingVolumeHierarchies = Arrays2.readIntArray(dataInputStream);
			final float[] textures = Arrays2.readFloatArray(dataInputStream);
			final float[] surfaces = Arrays2.readFloatArray(dataInputStream);
			final int[] primitives = Arrays2.readIntArray(dataInputStream);
			final int[] primitivesEmittingLight = Arrays2.readIntArray(dataInputStream);
			final float[] camera = Arrays2.readFloatArray(dataInputStream);
			
			return new CompiledScene(name, camera, point2Fs, point3Fs, spheres, surfaces, terrains, textures, vector3Fs, boundingVolumeHierarchies, planes, primitives, primitivesEmittingLight, triangles);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene read(final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(Objects.requireNonNull(file, "file == null"))))) {
			return read(dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}