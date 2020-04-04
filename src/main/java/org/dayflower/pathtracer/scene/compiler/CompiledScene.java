/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
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
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.util.Arrays2;
import org.macroing.math4j.Point2F;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.Vector3F;

/**
 * A {@code CompiledScene} represents a compiled version of a {@link Scene} instance.
 * <p>
 * To compile a {@link Scene} into a {@code CompiledScene}, use {@link SceneCompiler} and its {@link SceneCompiler#compile(Scene) compile(Scene)} method.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class CompiledScene {
	private final String name;
	private final float[] camera;
	private final float[] point2Fs;
	private final float[] point3Fs;
	private final float[] primitivesObjectToWorld;
	private final float[] primitivesWorldToObject;
	private final float[] spheres;
	private final float[] surfaces;
	private final float[] terrains;
	private final float[] textures;
	private final float[] vector3Fs;
	private final int[] boundingVolumeHierarchies;
	private final int[] planes;
	private final int[] primitives;
//	private final int[] primitivesEmittingLight;
	private final int[] triangles;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code CompiledScene} instance.
	 * <p>
	 * If at least one of the parameters are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of this {@code CompiledScene} instance
	 * @param camera the array containing the compiled {@link Camera} instance
	 * @param point2Fs the array containing all the compiled {@link Point2F} instances
	 * @param point3Fs the array containing all the compiled {@link Point3F} instances
	 * @param primitivesObjectToWorld the array containing all the compiled matrices for object to world transformations
	 * @param primitivesWorldToObject the array containing all the compiled matrices for world to object transformations
	 * @param spheres the array containing all the compiled {@link Sphere} instances
	 * @param surfaces the array containing all the compiled {@link Surface} instances
	 * @param terrains the array containing all the compiled {@link Terrain} instances
	 * @param textures the array containing all the compiled {@link Texture} instances
	 * @param vector3Fs the array containing all the compiled {@link Vector3F} instances
	 * @param boundingVolumeHierarchies the array containing all the compiled {@link BoundingVolumeHierarchy} instances
	 * @param planes the array containing all the compiled {@link Plane} instances
	 * @param primitives the array containing all the compiled {@link Primitive} instances
	 * @param primitivesEmittingLight the array containing all the compiled {@link Primitive} instances that emits light
	 * @param triangles the array containing all the compiled {@link Triangle} instances
	 * @throws NullPointerException thrown if, and only if, at least one of the parameters are {@code null}
	 */
	public CompiledScene(final String name, final float[] camera, final float[] point2Fs, final float[] point3Fs, final float[] primitivesObjectToWorld, final float[] primitivesWorldToObject, final float[] spheres, final float[] surfaces, final float[] terrains, final float[] textures, final float[] vector3Fs, final int[] boundingVolumeHierarchies, final int[] planes, final int[] primitives, /*final int[] primitivesEmittingLight, */final int[] triangles) {
		this.name = Objects.requireNonNull(name, "name == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.point2Fs = Objects.requireNonNull(point2Fs, "point2Fs == null");
		this.point3Fs = Objects.requireNonNull(point3Fs, "point3Fs == null");
		this.primitivesObjectToWorld = Objects.requireNonNull(primitivesObjectToWorld, "primitivesObjectToWorld == null");
		this.primitivesWorldToObject = Objects.requireNonNull(primitivesWorldToObject, "primitivesWorldToObject == null");
		this.spheres = Objects.requireNonNull(spheres, "spheres == null");
		this.surfaces = Objects.requireNonNull(surfaces, "surfaces == null");
		this.terrains = Objects.requireNonNull(terrains, "terrains == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.vector3Fs = Objects.requireNonNull(vector3Fs, "vector3Fs == null");
		this.boundingVolumeHierarchies = Objects.requireNonNull(boundingVolumeHierarchies, "boundingVolumeHierarchies == null");
		this.planes = Objects.requireNonNull(planes, "planes == null");
		this.primitives = Objects.requireNonNull(primitives, "primitives == null");
//		this.primitivesEmittingLight = Objects.requireNonNull(primitivesEmittingLight, "primitivesEmittingLight == null");
		this.triangles = Objects.requireNonNull(triangles, "triangles == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the name of this {@code CompiledScene} instance.
	 * 
	 * @return the name of this {@code CompiledScene} instance
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the array containing the compiled {@link Camera} instance.
	 * 
	 * @return the array containing the compiled {@code Camera} instance
	 */
	public float[] getCamera() {
		return this.camera;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Point2F} instances.
	 * 
	 * @return the array containing all the compiled {@code Point2F} instances
	 */
	public float[] getPoint2Fs() {
		return this.point2Fs;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Point3F} instances.
	 * 
	 * @return the array containing all the compiled {@code Point3F} instances
	 */
	public float[] getPoint3Fs() {
		return this.point3Fs;
	}
	
	/**
	 * Returns the array containing all the compiled matrices for object to world transformations.
	 * 
	 * @return the array containing all the compiled matrices for object to world transformations
	 */
	public float[] getPrimitivesObjectToWorld() {
		return this.primitivesObjectToWorld;
	}
	
	/**
	 * Returns the array containing all the compiled matrices for world to object transformations.
	 * 
	 * @return the array containing all the compiled matrices for world to object transformations
	 */
	public float[] getPrimitivesWorldToObject() {
		return this.primitivesWorldToObject;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Sphere} instances.
	 * 
	 * @return the array containing all the compiled {@code Sphere} instances
	 */
	public float[] getSpheres() {
		return this.spheres;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Surface} instances.
	 * 
	 * @return the array containing all the compiled {@code Surface} instances
	 */
	public float[] getSurfaces() {
		return this.surfaces;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Terrain} instances.
	 * 
	 * @return the array containing all the compiled {@code Terrain} instances
	 */
	public float[] getTerrains() {
		return this.terrains;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Texture} instances.
	 * 
	 * @return the array containing all the compiled {@code Texture} instances
	 */
	public float[] getTextures() {
		return this.textures;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Vector3F} instances.
	 * 
	 * @return the array containing all the compiled {@code Vector3F} instances
	 */
	public float[] getVector3Fs() {
		return this.vector3Fs;
	}
	
	/**
	 * Returns the array containing all the compiled {@link BoundingVolumeHierarchy} instances.
	 * 
	 * @return the array containing all the compiled {@code BoundingVolumeHierarchy} instances
	 */
	public int[] getBoundingVolumeHierarchies() {
		return this.boundingVolumeHierarchies;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Plane} instances.
	 * 
	 * @return the array containing all the compiled {@code Plane} instances
	 */
	public int[] getPlanes() {
		return this.planes;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Primitive} instances.
	 * 
	 * @return the array containing all the compiled {@code Primitive} instances
	 */
	public int[] getPrimitives() {
		return this.primitives;
	}
	
	/**
	 * Returns the array containing all the compiled {@link Primitive} instances that emits light.
	 * 
	 * @return the array containing all the compiled {@code Primitive} instances that emits light
	 */
//	public int[] getPrimitivesEmittingLight() {
//		return this.primitivesEmittingLight;
//	}
	
	/**
	 * Returns the array containing all the compiled {@link Triangle} instances.
	 * 
	 * @return the array containing all the compiled {@code Triangle} instances
	 */
	public int[] getTriangles() {
		return this.triangles;
	}
	
	/**
	 * Writes this {@code CompiledScene} instance to {@code dataOutputStream}.
	 * <p>
	 * If {@code dataOutputStream} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataOutputStream the {@code DataOutputStream} to write to
	 * @throws NullPointerException thrown if, and only if, {@code dataOutputStream} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
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
//			Arrays2.writeIntArray(dataOutputStream, this.primitivesEmittingLight);
			Arrays2.writeFloatArray(dataOutputStream, this.primitivesObjectToWorld);
			Arrays2.writeFloatArray(dataOutputStream, this.primitivesWorldToObject);
			Arrays2.writeFloatArray(dataOutputStream, this.camera);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Writes this {@code CompiledScene} instance to the file represented by {@code file}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representation of the file to write to
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
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
	
	/**
	 * Writes this {@code CompiledScene} instance to the file represented by {@code filename}.
	 * <p>
	 * If {@code filename} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename the filename of the file to write to
	 * @throws NullPointerException thrown if, and only if, {@code filename} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public void write(final String filename) {
		write(new File(Objects.requireNonNull(filename, "filename")));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reads the data provided by {@code dataInputStream} into a {@code CompiledScene} instance.
	 * <p>
	 * Returns a new {@code CompiledScene} instance.
	 * <p>
	 * If {@code dataInputStream} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param dataInputStream the {@code DataInputStream} to read from
	 * @return a new {@code CompiledScene} instance
	 * @throws NullPointerException thrown if, and only if, {@code dataInputStream} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
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
//			final int[] primitivesEmittingLight = Arrays2.readIntArray(dataInputStream);
			final float[] primitivesObjectToWorld = Arrays2.readFloatArray(dataInputStream);
			final float[] primitivesWorldToObject = Arrays2.readFloatArray(dataInputStream);
			final float[] camera = Arrays2.readFloatArray(dataInputStream);
			
			return new CompiledScene(name, camera, point2Fs, point3Fs, primitivesObjectToWorld, primitivesWorldToObject, spheres, surfaces, terrains, textures, vector3Fs, boundingVolumeHierarchies, planes, primitives, /*primitivesEmittingLight, */triangles);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Reads the data provided by a file represented by {@code file} into a {@code CompiledScene} instance.
	 * <p>
	 * Returns a new {@code CompiledScene} instance.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representation of the file to read from
	 * @return a new {@code CompiledScene} instance
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static CompiledScene read(final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(Objects.requireNonNull(file, "file == null"))))) {
			return read(dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Reads the data provided by a file represented by {@code filename} into a {@code CompiledScene} instance.
	 * <p>
	 * Returns a new {@code CompiledScene} instance.
	 * <p>
	 * If {@code filename} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename the filename of the file to read from
	 * @return a new {@code CompiledScene} instance
	 * @throws NullPointerException thrown if, and only if, {@code filename} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static CompiledScene read(final String filename) {
		return read(new File(Objects.requireNonNull(filename, "filename == null")));
	}
}