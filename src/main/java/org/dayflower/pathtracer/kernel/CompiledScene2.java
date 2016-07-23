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
package org.dayflower.pathtracer.kernel;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.dayflower.pathtracer.main.Scenes;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;

//TODO: Add Javadocs.
public final class CompiledScene2 {
	private final float[] boundingVolumeHierarchy;
	private final float[] point2s;
	private final float[] point3s;
	private final float[] shapes;
	private final float[] surfaces;
	private final float[] textures;
	private final float[] vector3s;
	private final int[] triangles;
	private final String name;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public CompiledScene2(final float[] boundingVolumeHierarchy, final float[] point2s, final float[] point3s, final float[] shapes, final float[] surfaces, final float[] textures, final float[] vector3s, final int[] triangles, final String name) {
		this.boundingVolumeHierarchy = Objects.requireNonNull(boundingVolumeHierarchy, "boundingVolumeHierarchy == null");
		this.point2s = Objects.requireNonNull(point2s, "point2s == null");
		this.point3s = Objects.requireNonNull(point3s, "point3s == null");
		this.shapes = Objects.requireNonNull(shapes, "shapes == null");
		this.surfaces = Objects.requireNonNull(surfaces, "surfaces == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.vector3s = Objects.requireNonNull(vector3s, "vector3s == null");
		this.triangles = Objects.requireNonNull(triangles, "triangles == null");
		this.name = Objects.requireNonNull(name, "name == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float[] getBoundingVolumeHierarchy() {
		return this.boundingVolumeHierarchy;
	}
	
//	TODO: Add Javadocs.
	public float[] getPoint2s() {
		return this.point2s;
	}
	
//	TODO: Add Javadocs.
	public float[] getPoint3s() {
		return this.point3s;
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
	public float[] getVector3s() {
		return this.vector3s;
	}
	
//	TODO: Add Javadocs.
	public int[] getTriangles() {
		return this.triangles;
	}
	
//	TODO: Add Javadocs.
	public String getName() {
		return this.name;
	}
	
//	TODO: Add Javadocs.
	public void write(final DataOutputStream dataOutputStream) {
		try {
			doWriteFloatArray(dataOutputStream, this.boundingVolumeHierarchy);
			doWriteFloatArray(dataOutputStream, this.point2s);
			doWriteFloatArray(dataOutputStream, this.point3s);
			doWriteFloatArray(dataOutputStream, this.shapes);
			doWriteFloatArray(dataOutputStream, this.surfaces);
			doWriteFloatArray(dataOutputStream, this.textures);
			doWriteFloatArray(dataOutputStream, this.vector3s);
			doWriteIntArray(dataOutputStream, this.triangles);
			
			dataOutputStream.writeUTF(this.name);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public void write(final File file) {
		try(final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Objects.requireNonNull(file, "file == null"))))) {
			write(dataOutputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public void write(final String filename) {
		write(new File(Objects.requireNonNull(filename, "filename == null")));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene2 compile(final Scene scene) {
		final
		SceneCache sceneCache = new SceneCache();
		sceneCache.cache(scene);
		
		return SceneCompiler.compile(sceneCache);
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene2 read(final DataInputStream dataInputStream) {
		try {
			final float[] boundingVolumeHierarchy = doReadFloatArray(dataInputStream);
			final float[] point2s = doReadFloatArray(dataInputStream);
			final float[] point3s = doReadFloatArray(dataInputStream);
			final float[] shapes = doReadFloatArray(dataInputStream);
			final float[] surfaces = doReadFloatArray(dataInputStream);
			final float[] textures = doReadFloatArray(dataInputStream);
			final float[] vector3s = doReadFloatArray(dataInputStream);
			
			final int[] triangles = doReadIntArray(dataInputStream);
			
			final String name = dataInputStream.readUTF();
			
			return new CompiledScene2(boundingVolumeHierarchy, point2s, point3s, shapes, surfaces, textures, vector3s, triangles, name);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene2 read(final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(Objects.requireNonNull(file, "file == null"))))) {
			return read(dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene2 read(final String filename) {
		return read(new File(Objects.requireNonNull(filename, "filename == null")));
	}
	
//	TODO: Add Javadocs.
	public static void main(final String[] args) {
		final long currentTimeMillis0 = System.currentTimeMillis();
		
		final Scene scene = Scenes.newHouseScene();
		
		final long currentTimeMillis1 = System.currentTimeMillis();
		final long elapsedTimeMillis0 = currentTimeMillis1 - currentTimeMillis0;
		final long currentTimeMillis2 = System.currentTimeMillis();
		
		final
		SceneCache sceneCache = new SceneCache();
		sceneCache.cache(scene);
		
		final CompiledScene2 compiledScene2 = SceneCompiler.compile(sceneCache);
		
		final long currentTimeMillis3 = System.currentTimeMillis();
		final long elapsedTimeMillis1 = currentTimeMillis3 - currentTimeMillis2;
		
		System.out.println("It took " + elapsedTimeMillis0 + " millis to load the Scene.");
		System.out.println("It took " + elapsedTimeMillis1 + " millis to cache the Scene.");
		System.out.println("It took " + (elapsedTimeMillis0 + elapsedTimeMillis1) + " millis in total.");
		System.out.println("CompiledScene2: " + compiledScene2);
		
		sceneCache.printStatistics();
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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class SceneCache {
		public static final int SIZE_CHECKERBOARD_TEXTURE = 11;
		public static final int SIZE_IMAGE_TEXTURE = 7;
		public static final int SIZE_PLANE = 6;
		public static final int SIZE_POINT2 = 2;
		public static final int SIZE_POINT3 = 3;
		public static final int SIZE_SOLID_TEXTURE = 5;
		public static final int SIZE_SPHERE = 4;
		public static final int SIZE_SURFACE = 8;
		public static final int SIZE_TRIANGLE = 11;
		public static final int SIZE_VECTOR3 = 3;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private int acceptedBytes;
		private int rejectedBytes;
		private int rejectedPoint2s;
		private int rejectedPoint3s;
		private int rejectedSurfaces;
		private int rejectedTextures;
		private int rejectedVector3s;
		private final List<Shape> shapes = new ArrayList<>();
		private final List<Triangle> triangles = new ArrayList<>();
		private final Map<Point2, Integer> point2Offsets = new HashMap<>();
		private final Map<Point3, Integer> point3Offsets = new HashMap<>();
		private final Map<Shape, Integer> shapeOffsets = new HashMap<>();
		private final Map<Surface, Integer> surfaceOffsets = new HashMap<>();
		private final Map<Texture, Integer> textureOffsets = new HashMap<>();
		private final Map<Triangle, Integer> triangleOffsets = new HashMap<>();
		private final Map<Vector3, Integer> vector3Offsets = new HashMap<>();
		private final Set<Point2> point2s = new LinkedHashSet<>();
		private final Set<Point3> point3s = new LinkedHashSet<>();
		private final Set<Surface> surfaces = new LinkedHashSet<>();
		private final Set<Texture> textures = new LinkedHashSet<>();
		private final Set<Vector3> vector3s = new LinkedHashSet<>();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public SceneCache() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public List<Shape> getShapes() {
			return this.shapes;
		}
		
		public List<Triangle> getTriangles() {
			return this.triangles;
		}
		
		public Map<Point2, Integer> getPoint2Offsets() {
			return this.point2Offsets;
		}
		
		public Map<Point3, Integer> getPoint3Offsets() {
			return this.point3Offsets;
		}
		
		public Map<Shape, Integer> getShapeOffsets() {
			return this.shapeOffsets;
		}
		
		public Map<Surface, Integer> getSurfaceOffsets() {
			return this.surfaceOffsets;
		}
		
		public Map<Texture, Integer> getTextureOffsets() {
			return this.textureOffsets;
		}
		
		public Map<Triangle, Integer> getTriangleOffsets() {
			return this.triangleOffsets;
		}
		
		public Map<Vector3, Integer> getVector3Offsets() {
			return this.vector3Offsets;
		}
		
		public Set<Point2> getPoint2s() {
			return this.point2s;
		}
		
		public Set<Point3> getPoint3s() {
			return this.point3s;
		}
		
		public Set<Surface> getSurfaces() {
			return this.surfaces;
		}
		
		public Set<Texture> getTextures() {
			return this.textures;
		}
		
		public Set<Vector3> getVector3s() {
			return this.vector3s;
		}
		
		public void cache(final Scene scene) {
			doClear();
			doCache(scene);
		}
		
		public void printStatistics() {
			System.out.printf("Point2: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Integer.toString(this.point2s.size()), Integer.toString(this.rejectedPoint2s), Integer.toString(this.point2s.size() + this.rejectedPoint2s));
			System.out.printf("Point3: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Integer.toString(this.point3s.size()), Integer.toString(this.rejectedPoint3s), Integer.toString(this.point3s.size() + this.rejectedPoint3s));
			System.out.printf("Surface: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Integer.toString(this.surfaces.size()), Integer.toString(this.rejectedSurfaces), Integer.toString(this.surfaces.size() + this.rejectedSurfaces));
			System.out.printf("Texture: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Integer.toString(this.textures.size()), Integer.toString(this.rejectedTextures), Integer.toString(this.textures.size() + this.rejectedTextures));
			System.out.printf("Vector3: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Integer.toString(this.vector3s.size()), Integer.toString(this.rejectedVector3s), Integer.toString(this.vector3s.size() + this.rejectedVector3s));
			System.out.printf("MB: [Accepted=%s], [Rejected=%s], [Total=%s]%n", Float.toString(this.acceptedBytes / 1024.0F / 1024.0F), Float.toString(this.rejectedBytes / 1024.0F / 1024.0F), Float.toString((this.acceptedBytes + this.rejectedBytes) / 1024.0F / 1024.0F));
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private int doAddPoint2(final int offset, final Point2 p) {
			if(this.point2s.add(p)) {
				this.point2Offsets.put(p, Integer.valueOf(offset));
				
				this.acceptedBytes += 8;
				
				return offset + SIZE_POINT2;
			}
			
			this.rejectedBytes += 8;
			this.rejectedPoint2s++;
			
			return offset;
		}
		
		private int doAddPoint3(final int offset, final Point3 p) {
			if(this.point3s.add(p)) {
				this.point3Offsets.put(p, Integer.valueOf(offset));
				
				this.acceptedBytes += 12;
				
				return offset + SIZE_POINT3;
			}
			
			this.rejectedBytes += 12;
			this.rejectedPoint3s++;
			
			return offset;
		}
		
		private int doAddShape(final int offset, final Shape shape) {
			if(shape instanceof Plane || shape instanceof Sphere) {
				if(this.shapes.add(shape)) {
					this.shapeOffsets.put(shape, Integer.valueOf(offset));
					
					if(shape instanceof Plane) {
						return offset + SIZE_PLANE;
					} else if(shape instanceof Sphere) {
						this.acceptedBytes += 4;
						
						return offset + SIZE_SPHERE;
					}
				}
			}
			
			if(shape instanceof Sphere) {
				this.rejectedBytes += 4;
			}
			
			return offset;
		}
		
		private int doAddSurface(final int offset, final Surface surface) {
			if(this.surfaces.add(surface)) {
				this.surfaceOffsets.put(surface, Integer.valueOf(offset));
				
				this.acceptedBytes += 4 * SIZE_SURFACE;
				
				return offset + SIZE_SURFACE;
			}
			
			this.rejectedBytes += 4 * SIZE_SURFACE;
			this.rejectedSurfaces++;
			
			return offset;
		}
		
		private int doAddTexture(final int offset, final Texture texture) {
			if(this.textures.add(texture)) {
				this.textureOffsets.put(texture, Integer.valueOf(offset));
				
				if(texture instanceof CheckerboardTexture) {
					this.acceptedBytes += 4 * SIZE_CHECKERBOARD_TEXTURE;
					
					return offset + SIZE_CHECKERBOARD_TEXTURE;
				} else if(texture instanceof ImageTexture) {
					this.acceptedBytes += 4 * (SIZE_IMAGE_TEXTURE + ImageTexture.class.cast(texture).getDataLength());
					
					return offset + SIZE_IMAGE_TEXTURE + ImageTexture.class.cast(texture).getDataLength();
				} else if(texture instanceof SolidTexture) {
					this.acceptedBytes += 4 * SIZE_SOLID_TEXTURE;
					
					return offset + SIZE_SOLID_TEXTURE;
				}
			}
			
			if(texture instanceof CheckerboardTexture) {
				this.rejectedBytes += 4 * SIZE_CHECKERBOARD_TEXTURE;
			} else if(texture instanceof ImageTexture) {
				this.rejectedBytes += 4 * (SIZE_IMAGE_TEXTURE + ImageTexture.class.cast(texture).getDataLength());
			} else if(texture instanceof SolidTexture) {
				this.rejectedBytes += 4 * SIZE_SOLID_TEXTURE;
			}
			
			this.rejectedTextures++;
			
			return offset;
		}
		
		private int doAddTriangle(final int offset, final Triangle triangle) {
			if(this.triangles.add(triangle)) {
				this.triangleOffsets.put(triangle, Integer.valueOf(offset));
				
				return offset + SIZE_TRIANGLE;
			}
			
			return offset;
		}
		
		private int doAddVector3(final int offset, final Vector3 v) {
			if(this.vector3s.add(v)) {
				this.vector3Offsets.put(v, Integer.valueOf(offset));
				
				this.acceptedBytes += 12;
				
				return offset + SIZE_VECTOR3;
			}
			
			this.rejectedBytes += 12;
			this.rejectedVector3s++;
			
			return offset;
		}
		
		private void doCache(final Scene scene) {
			final List<Shape> shapes = scene.getShapes();
			
			int point2Offset = 0;
			int point3Offset = 0;
			int shapeOffset = 0;
			int surfaceOffset = 0;
			int textureOffset = 0;
			int triangleOffset = 0;
			int vector3Offset = 0;
			
			for(final Shape shape : shapes) {
				final Surface surface = shape.getSurface();
				
				if(surfaceOffset != (surfaceOffset = doAddSurface(surfaceOffset, surface))) {
					textureOffset = doAddTexture(textureOffset, surface.getTextureAlbedo());
					textureOffset = doAddTexture(textureOffset, surface.getTextureNormal());
				}
				
				if(shape instanceof Plane) {
					final Plane plane = Plane.class.cast(shape);
					
					point3Offset = doAddPoint3(point3Offset, plane.getA());
					point3Offset = doAddPoint3(point3Offset, plane.getB());
					point3Offset = doAddPoint3(point3Offset, plane.getC());
					
					shapeOffset = doAddShape(shapeOffset, plane);
					
					vector3Offset = doAddVector3(vector3Offset, plane.getSurfaceNormal());
				} else if(shape instanceof Sphere) {
					final Sphere sphere = Sphere.class.cast(shape);
					
					point3Offset = doAddPoint3(point3Offset, sphere.getPosition());
					
					shapeOffset = doAddShape(shapeOffset, sphere);
				} else if(shape instanceof Triangle) {
					final Triangle triangle = Triangle.class.cast(shape);
					
					point2Offset = doAddPoint2(point2Offset, triangle.getA().getTextureCoordinates());
					point2Offset = doAddPoint2(point2Offset, triangle.getB().getTextureCoordinates());
					point2Offset = doAddPoint2(point2Offset, triangle.getC().getTextureCoordinates());
					
					point3Offset = doAddPoint3(point3Offset, triangle.getA().getPosition());
					point3Offset = doAddPoint3(point3Offset, triangle.getB().getPosition());
					point3Offset = doAddPoint3(point3Offset, triangle.getC().getPosition());
					
					triangleOffset = doAddTriangle(triangleOffset, triangle);
					
					vector3Offset = doAddVector3(vector3Offset, triangle.getA().getNormal());
					vector3Offset = doAddVector3(vector3Offset, triangle.getB().getNormal());
					vector3Offset = doAddVector3(vector3Offset, triangle.getC().getNormal());
				}
			}
		}
		
		private void doClear() {
			this.acceptedBytes = 0;
			this.rejectedBytes = 0;
			this.rejectedPoint2s = 0;
			this.rejectedPoint3s = 0;
			this.rejectedSurfaces = 0;
			this.rejectedTextures = 0;
			this.rejectedVector3s = 0;
			
			this.shapes.clear();
			this.triangles.clear();
			this.point2Offsets.clear();
			this.point3Offsets.clear();
			this.shapeOffsets.clear();
			this.surfaceOffsets.clear();
			this.textureOffsets.clear();
			this.triangleOffsets.clear();
			this.vector3Offsets.clear();
			this.point2s.clear();
			this.point3s.clear();
			this.surfaces.clear();
			this.textures.clear();
			this.vector3s.clear();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class SceneCompiler {
		private SceneCompiler() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public static CompiledScene2 compile(final SceneCache sceneCache) {
			final float[] boundingVolumeHierarchy = new float[0];
			final float[] point2s = doCompilePoint2s(sceneCache);
			final float[] point3s = doCompilePoint3s(sceneCache);
			final float[] shapes = new float[0];
			final float[] surfaces = doCompileSurfaces(sceneCache);
			final float[] textures = new float[0];
			final float[] vector3s = doCompileVector3s(sceneCache);
			
			final int[] triangles = new int[0];
			
			final String name = "Scene";
			
			return new CompiledScene2(boundingVolumeHierarchy, point2s, point3s, shapes, surfaces, textures, vector3s, triangles, name);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private static float[] doCompilePoint2s(final SceneCache sceneCache) {
			final List<Point2> point2s = new ArrayList<>(sceneCache.getPoint2s());
			
			final float[] point2sArray = new float[point2s.size() * SceneCache.SIZE_POINT2];
			
			for(int i = 0, j = 0; i < point2sArray.length; i += SceneCache.SIZE_POINT2, j++) {
				final Point2 point2 = point2s.get(j);
				
				point2sArray[i + 0] = point2.x;
				point2sArray[i + 1] = point2.y;
			}
			
			return point2sArray;
		}
		
		private static float[] doCompilePoint3s(final SceneCache sceneCache) {
			final List<Point3> point3s = new ArrayList<>(sceneCache.getPoint3s());
			
			final float[] point3sArray = new float[point3s.size() * SceneCache.SIZE_POINT3];
			
			for(int i = 0, j = 0; i < point3sArray.length; i += SceneCache.SIZE_POINT3, j++) {
				final Point3 point3 = point3s.get(j);
				
				point3sArray[i + 0] = point3.x;
				point3sArray[i + 1] = point3.y;
				point3sArray[i + 2] = point3.z;
			}
			
			return point3sArray;
		}
		
		private static float[] doCompileSurfaces(final SceneCache sceneCache) {
			final List<Surface> surfaces = new ArrayList<>(sceneCache.getSurfaces());
			
			final Map<Texture, Integer> textureOffsets = sceneCache.getTextureOffsets();
			
			final float[] surfacesArray = new float[surfaces.size() * SceneCache.SIZE_SURFACE];
			
			for(int i = 0, j = 0; i < surfacesArray.length; i += SceneCache.SIZE_SURFACE, j++) {
				final Surface surface = surfaces.get(j);
				
				surfacesArray[i + 0] = surface.getEmission().r;
				surfacesArray[i + 1] = surface.getEmission().g;
				surfacesArray[i + 2] = surface.getEmission().b;
				surfacesArray[i + 3] = surface.getMaterial().ordinal();
				surfacesArray[i + 4] = textureOffsets.get(surface.getTextureAlbedo()).intValue();
				surfacesArray[i + 5] = textureOffsets.get(surface.getTextureNormal()).intValue();
				surfacesArray[i + 6] = surface.getPerlinNoiseAmount();
				surfacesArray[i + 7] = surface.getPerlinNoiseScale();
			}
			
			return surfacesArray;
		}
		
		private static float[] doCompileVector3s(final SceneCache sceneCache) {
			final List<Vector3> vector3s = new ArrayList<>(sceneCache.getVector3s());
			
			final float[] vector3sArray = new float[vector3s.size() * SceneCache.SIZE_VECTOR3];
			
			for(int i = 0, j = 0; i < vector3sArray.length; i += SceneCache.SIZE_VECTOR3, j++) {
				final Vector3 vector3 = vector3s.get(j);
				
				vector3sArray[i + 0] = vector3.x;
				vector3sArray[i + 1] = vector3.y;
				vector3sArray[i + 2] = vector3.z;
			}
			
			return vector3sArray;
		}
	}
}