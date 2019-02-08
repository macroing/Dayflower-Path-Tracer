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

import static org.dayflower.pathtracer.math.MathF.cos;
import static org.dayflower.pathtracer.math.MathF.sin;
import static org.dayflower.pathtracer.math.MathF.toRadians;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.LeafNode;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.Node;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.TreeNode;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.FractionalBrownianMotionTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;
import org.dayflower.pathtracer.scene.texture.SurfaceNormalTexture;

//TODO: Add Javadocs.
//TODO: Split "float[] shapes" into "float[] shapes" and "int[] triangles".
//TODO: Remove "type" from "int[] triangles".
//TODO: Update the RendererKernel with the new changes.
//TODO: See if "int[] triangles" could be changed into "short[] triangles" instead, or "char[] triangles".
public final class CompiledScene {
//	TODO: Add Javadocs.
	public static final int BVH_NODE_TYPE_LEAF = 2;
	
//	TODO: Add Javadocs.
	public static final int BVH_NODE_TYPE_TREE = 1;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_0 = 2;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_1 = 3;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_COS = 4;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN = 5;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_U = 6;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_V = 7;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_SIZE = 8;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_TYPE = 1;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_RELATIVE_OFFSET_ADDEND = 2;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_RELATIVE_OFFSET_MULTIPLIER = 3;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_RELATIVE_OFFSET_OCTAVES = 6;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_RELATIVE_OFFSET_PERSISTENCE = 4;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_RELATIVE_OFFSET_SCALE = 5;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_SIZE = 7;
	
//	TODO: Add Javadocs.
	public static final int FRACTIONAL_BROWNIAN_MOTION_TEXTURE_TYPE = 5;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_DATA = 8;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_HEIGHT = 5;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_COS = 2;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN = 3;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_U = 6;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_V = 7;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_WIDTH = 4;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_TYPE = 3;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_A_POINT3S_OFFSET = 2;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_B_POINT3S_OFFSET = 3;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_C_POINT3S_OFFSET = 4;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_SURFACE_NORMAL_VECTOR3S_OFFSET = 5;
	
//	TODO: Add Javadocs.
	public static final int PLANE_SIZE = 6;
	
//	TODO: Add Javadocs.
	public static final int PLANE_TYPE = 3;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET = 1;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_TYPE = 0;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_RELATIVE_OFFSET_COLOR = 2;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_SIZE = 3;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_TYPE = 2;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_RELATIVE_OFFSET_POSITION_POINT3S_OFFSET = 3;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_RELATIVE_OFFSET_RADIUS = 2;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_SIZE = 4;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_TYPE = 1;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_NORMAL_TEXTURE_RELATIVE_OFFSET_IS_TANGENT_SPACE = 2;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_NORMAL_TEXTURE_SIZE = 3;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_NORMAL_TEXTURE_TYPE = 4;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_EMISSION = 0;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_EMISSION_B = SURFACE_RELATIVE_OFFSET_EMISSION + 2;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_EMISSION_G = SURFACE_RELATIVE_OFFSET_EMISSION + 1;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_EMISSION_R = SURFACE_RELATIVE_OFFSET_EMISSION + 0;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_MATERIAL = 3;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT = 6;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE = 7;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO = 4;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL = 5;
	
//	TODO: Add Javadocs.
	public static final int SURFACE_SIZE = 8;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_RELATIVE_OFFSET_MAXIMUM = 5;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_RELATIVE_OFFSET_MINIMUM = 4;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_RELATIVE_OFFSET_OCTAVES = 6;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_RELATIVE_OFFSET_PERSISTENCE = 2;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_RELATIVE_OFFSET_SCALE = 3;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_SIZE = 7;
	
//	TODO: Add Javadocs.
	public static final int TERRAIN_TYPE = 4;
	
//	TODO: Add Javadocs.
	public static final int TEXTURE_RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	public static final int TEXTURE_RELATIVE_OFFSET_TYPE = 0;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_A_POINT3S_OFFSET = 2;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_B_POINT3S_OFFSET = 3;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_C_POINT3S_OFFSET = 4;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_A_VECTOR3S_OFFSET = 5;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_B_VECTOR3S_OFFSET = 6;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_C_VECTOR3S_OFFSET = 7;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_A_POINT2S_OFFSET = 8;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_B_POINT2S_OFFSET = 9;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_C_POINT2S_OFFSET = 10;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_SIZE = 11;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_TYPE = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final float[] boundingVolumeHierarchy;
	private final float[] camera;
	private final float[] point2Fs;
	private final float[] point3Fs;
	private final float[] shapes;
	private final float[] surfaces;
	private final float[] textures;
	private final float[] vector3Fs;
	private final int[] shapeOffsets;
	private final String name;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CompiledScene(final float[] boundingVolumeHierarchy, final float[] camera, final float[] point2Fs, final float[] point3Fs, final float[] shapes, final float[] surfaces, final float[] textures, final float[] vector3Fs, final int[] shapeOffsets, final String name) {
		this.boundingVolumeHierarchy = Objects.requireNonNull(boundingVolumeHierarchy, "boundingVolumeHierarchy == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.point2Fs = Objects.requireNonNull(point2Fs, "point2Fs == null");
		this.point3Fs = Objects.requireNonNull(point3Fs, "point3Fs == null");
		this.shapes = Objects.requireNonNull(shapes, "shapes == null");
		this.surfaces = Objects.requireNonNull(surfaces, "surfaces == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.vector3Fs = Objects.requireNonNull(vector3Fs, "vector3Fs == null");
		this.shapeOffsets = Objects.requireNonNull(shapeOffsets, "shapeOffsets == null");
		this.name = Objects.requireNonNull(name, "name == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public boolean hasNormalMapping() {
		boolean hasNormalMapping = false;
		
		for(int i = 0; i < this.surfaces.length; i += SURFACE_SIZE) {
			final int textureOffsetNormal = (int)(this.surfaces[i + SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL]);
			final int textureType = (int)(this.textures[textureOffsetNormal + TEXTURE_RELATIVE_OFFSET_TYPE]);
			
			if(textureType == IMAGE_TEXTURE_TYPE) {
				hasNormalMapping = true;
				
				break;
			}
		}
		
		return hasNormalMapping;
	}
	
//	TODO: Add Javadocs.
	public boolean hasPerlinNoiceNormalMapping() {
		boolean hasPerlinNoiceNormalMapping = false;
		
		for(int i = 0; i < this.surfaces.length; i += SURFACE_SIZE) {
			final float perlinNoiceAmount = this.surfaces[i + SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT];
			final float perlinNoiceScale = this.surfaces[i + SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE];
			
			if(perlinNoiceAmount > 0.0F || perlinNoiceScale > 0.0F) {
				hasPerlinNoiceNormalMapping = true;
				
				break;
			}
		}
		
		return hasPerlinNoiceNormalMapping;
	}
	
//	TODO: Add Javadocs.
	public boolean hasPlanes() {
		return hasShapes(PLANE_TYPE);
	}
	
//	TODO: Add Javadocs.
	public boolean hasShapes(final int shapeType) {
		boolean hasShapes = false;
		
		for(int i = 0; i < this.shapes.length;) {
			final int shapeType0 = (int)(this.shapes[i + SHAPE_RELATIVE_OFFSET_TYPE]);
			
			if(shapeType0 == PLANE_TYPE) {
				i += PLANE_SIZE;
			} else if(shapeType0 == SPHERE_TYPE) {
				i += SPHERE_SIZE;
			} else if(shapeType0 == TERRAIN_TYPE) {
				i += TERRAIN_SIZE;
			} else if(shapeType0 == TRIANGLE_TYPE) {
				i += TRIANGLE_SIZE;
			}
			
			if(shapeType0 == shapeType) {
				hasShapes = true;
				
				break;
			}
		}
		
		return hasShapes;
	}
	
//	TODO: Add Javadocs.
	public boolean hasSpheres() {
		return hasShapes(SPHERE_TYPE);
	}
	
//	TODO: Add Javadocs.
	public boolean hasTriangles() {
		return hasShapes(TRIANGLE_TYPE);
	}
	
//	TODO: Add Javadocs.
	public CompiledScene scale(final float scale) {
		for(int i = 0; i < this.boundingVolumeHierarchy.length;) {
			final int type = (int)(this.boundingVolumeHierarchy[i]);
			
			for(int j = 2; j < 8; j++) {
				this.boundingVolumeHierarchy[i + j] *= scale;
			}
			
			if(type == BVH_NODE_TYPE_TREE) {
				i += 9;
			} else {
				i += 9 + (int)(this.boundingVolumeHierarchy[i + 8]);
			}
		}
		
		for(int i = 0; i < this.point2Fs.length; i++) {
			this.point2Fs[i] *= scale;
		}
		
		for(int i = 0; i < this.point3Fs.length; i++) {
			this.point3Fs[i] *= scale;
		}
		
		for(int i = 0; i < this.vector3Fs.length; i++) {
			this.vector3Fs[i] *= scale;
		}
		
		return this;
	}
	
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
	public void write() {
		write(new File("resources/distribution/scene/" + getName() + ".scene"));
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
	public static CompiledScene compile(final Camera camera, final Scene scene) {
		return compile(camera, scene, scene.getName());
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene compile(final Camera camera, final Scene scene, final String name) {
		final List<Point2F> point2Fs0 = doFindPoint2Fs(scene);
		final List<Point3F> point3Fs0 = doFindPoint3Fs(scene);
		final List<Surface> surfaces0 = doFindSurfaces(scene);
		final List<Texture> textures0 = doFindTextures(scene);
		final List<Vector3F> vector3Fs0 = doFindVector3Fs(scene);
		
		final Map<Point2F, Integer> point2Fs1 = doCreatePoint2FMapping(point2Fs0);
		final Map<Point3F, Integer> point3Fs1 = doCreatePoint3FMapping(point3Fs0);
		final Map<Vector3F, Integer> vector3Fs1 = doCreateVector3FMapping(vector3Fs0);
		
		final float[] boundingVolumeHierarchy = doCompileBoundingVolumeHierarchy(scene);
		final float[] camera0 = camera.getArray();
		final float[] point2Fs = doCompilePoint2Fs(point2Fs0);
		final float[] point3Fs = doCompilePoint3Fs(point3Fs0);
		final float[] shapes = doCompileShapes(surfaces0, point2Fs1, point3Fs1, vector3Fs1, scene);
		final float[] surfaces = doCompileSurfaces(surfaces0, textures0);
		final float[] textures = doCompileTextures(textures0);
		final float[] vector3Fs = doCompileVector3Fs(vector3Fs0);
		
		final int[] shapeOffsets = doCompileShapeOffsets(scene);
		
		doReorderShapes(boundingVolumeHierarchy, shapes, shapeOffsets);
		
		return new CompiledScene(boundingVolumeHierarchy, camera0, point2Fs, point3Fs, shapes, surfaces, textures, vector3Fs, shapeOffsets, name);
	}
	
//	TODO: Add Javadocs.
	public static CompiledScene read(final Camera camera, final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(Objects.requireNonNull(file, "file == null"))))) {
			return read(camera, dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
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
			
			return new CompiledScene(boundingVolumeHierarchy, cameraArray, point2Fs, point3Fs, shapes, surfaces, textures, vector3s, shapeOffsets, name);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float[] doCompileBoundingVolumeHierarchy(final Scene scene) {
		doReportProgress("Compiling BoundingVolumeHierarchy...");
		
		final List<Shape> shapes = scene.getShapes();
		final List<Triangle> triangles = new ArrayList<>();
		
		for(final Shape shape : shapes) {
			if(shape instanceof Triangle) {
				triangles.add(Triangle.class.cast(shape));
			}
		}
		
		if(triangles.size() == 0) {
			doReportProgress(" Done.\n");
			
			return new float[] {BVH_NODE_TYPE_LEAF, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
		}
		
		final BoundingVolumeHierarchy boundingVolumeHierarchy = BoundingVolumeHierarchy.createBoundingVolumeHierarchy(triangles);
		
		final Node root = boundingVolumeHierarchy.getRoot();
		
		final int size = doSize(root);
		
		final float[] boundingVolumeHierarchyArray = new float[size];
		
		final List<Node> nodes = doToList(root);
		
		final int[] offsets = new int[nodes.size()];
		
		for(int i = 0, j = 0; i < nodes.size(); i++) {
			offsets[i] = j;
			
			final Node node = nodes.get(i);
			
			if(node instanceof LeafNode) {
				j += 9 + LeafNode.class.cast(node).getTriangles().size();
			} else if(node instanceof TreeNode) {
				j += 9;
			}
		}
		
		for(int i = 0, j = 0; i < nodes.size(); i++) {
			final Node node = nodes.get(i);
			
			if(node instanceof LeafNode) {
				final LeafNode leafNode = LeafNode.class.cast(node);
				
				final int depth = leafNode.getDepth();
				
				int next = -1;
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node node0 = nodes.get(k);
					
					if(node0.getDepth() <= depth) {
						next = offsets[k];
						
						break;
					}
				}
				
				boundingVolumeHierarchyArray[j + 0] = BVH_NODE_TYPE_LEAF;
				boundingVolumeHierarchyArray[j + 1] = next;
				boundingVolumeHierarchyArray[j + 2] = leafNode.getMinimumX();
				boundingVolumeHierarchyArray[j + 3] = leafNode.getMinimumY();
				boundingVolumeHierarchyArray[j + 4] = leafNode.getMinimumZ();
				boundingVolumeHierarchyArray[j + 5] = leafNode.getMaximumX();
				boundingVolumeHierarchyArray[j + 6] = leafNode.getMaximumY();
				boundingVolumeHierarchyArray[j + 7] = leafNode.getMaximumZ();
				boundingVolumeHierarchyArray[j + 8] = leafNode.getTriangles().size();
				
				for(int k = 0; k < leafNode.getTriangles().size(); k++) {
					boundingVolumeHierarchyArray[j + 9 + k] = doGetOffset(leafNode.getTriangles().get(k), shapes);
				}
				
				j += 9 + leafNode.getTriangles().size();
			} else if(node instanceof TreeNode) {
				final TreeNode treeNode = TreeNode.class.cast(node);
				
				final int depth = treeNode.getDepth();
				
				int next = -1;
				int leftIndex = -1;
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node node0 = nodes.get(k);
					
					if(node0.getDepth() <= depth) {
						next = offsets[k];
						
						break;
					}
				}
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node node0 = nodes.get(k);
					
					if(node0.getDepth() == depth + 1) {
						leftIndex = offsets[k];
						
						break;
					}
				}
				
				boundingVolumeHierarchyArray[j + 0] = BVH_NODE_TYPE_TREE;
				boundingVolumeHierarchyArray[j + 1] = next;
				boundingVolumeHierarchyArray[j + 2] = treeNode.getMinimumX();
				boundingVolumeHierarchyArray[j + 3] = treeNode.getMinimumY();
				boundingVolumeHierarchyArray[j + 4] = treeNode.getMinimumZ();
				boundingVolumeHierarchyArray[j + 5] = treeNode.getMaximumX();
				boundingVolumeHierarchyArray[j + 6] = treeNode.getMaximumY();
				boundingVolumeHierarchyArray[j + 7] = treeNode.getMaximumZ();
				boundingVolumeHierarchyArray[j + 8] = leftIndex;
				
				j += 9;
			}
		}
		
		doReportProgress(" Done.\n");
		
		return boundingVolumeHierarchyArray;
	}
	
	private static float[] doCompilePoint2Fs(final List<Point2F> point2Fs) {
		doReportProgress("Compiling Point2Fs...");
		
		final float[] point2Fs0 = new float[point2Fs.size() * 2];
		
		for(int i = 0, j = 0; i < point2Fs.size(); i++, j += 2) {
			final Point2F point2F = point2Fs.get(i);
			
			point2Fs0[j + 0] = point2F.x;
			point2Fs0[j + 1] = point2F.y;
		}
		
		doReportProgress(" Done.\n");
		
		return point2Fs0;
	}
	
	private static float[] doCompilePoint3Fs(final List<Point3F> point3Fs) {
		doReportProgress("Compiling Point3Fs...");
		
		final float[] point3Fs0 = new float[point3Fs.size() * 3];
		
		for(int i = 0, j = 0; i < point3Fs.size(); i++, j += 3) {
			final Point3F point3F = point3Fs.get(i);
			
			point3Fs0[j + 0] = point3F.x;
			point3Fs0[j + 1] = point3F.y;
			point3Fs0[j + 2] = point3F.z;
		}
		
		doReportProgress(" Done.\n");
		
		return point3Fs0;
	}
	
	private static float[] doCompileShapes(final List<Surface> surfaces, final Map<Point2F, Integer> point2Fs, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs, final Scene scene) {
		doReportProgress("Compiling Shapes...");
		
		final List<Float> floats = new ArrayList<>();
		
		for(final Shape shape : scene.getShapes()) {
			final float[] floatArray = doToFloatArray(shape, surfaces, point2Fs, point3Fs, vector3Fs);
			
			for(final float value : floatArray) {
				floats.add(Float.valueOf(value));
			}
		}
		
		final float[] floatArray = new float[floats.size()];
		
		for(int i = 0; i < floats.size(); i++) {
			floatArray[i] = floats.get(i).floatValue();
		}
		
		doReportProgress(" Done.\n");
		
		return floatArray;
	}
	
	private static float[] doCompileSurfaces(final List<Surface> surfaces, final List<Texture> textures) {
		doReportProgress("Compiling Surfaces...");
		
		final List<Float> floats = new ArrayList<>();
		
		for(final Surface surface : surfaces) {
			final float[] floatArray = doToFloatArray(surface, textures);
			
			for(final float value : floatArray) {
				floats.add(Float.valueOf(value));
			}
		}
		
		final float[] floatArray = new float[floats.size()];
		
		for(int i = 0; i < floats.size(); i++) {
			floatArray[i] = floats.get(i).floatValue();
		}
		
		doReportProgress(" Done.\n");
		
		return floatArray;
	}
	
	private static float[] doCompileTextures(final List<Texture> textures) {
		doReportProgress("Compiling Textures...");
		
		final List<Float> floats = new ArrayList<>();
		
		for(final Texture texture : textures) {
			final float[] floatArray = doToFloatArray(texture);
			
			for(final float value : floatArray) {
				floats.add(Float.valueOf(value));
			}
		}
		
		final float[] floatArray = new float[floats.size()];
		
		for(int i = 0; i < floats.size(); i++) {
			floatArray[i] = floats.get(i).floatValue();
		}
		
		doReportProgress(" Done.\n");
		
		return floatArray;
	}
	
	private static float[] doCompileVector3Fs(final List<Vector3F> vector3Fs) {
		doReportProgress("Compiling Vector3Fs...");
		
		final float[] vector3Fs0 = new float[vector3Fs.size() * 3];
		
		for(int i = 0, j = 0; i < vector3Fs.size(); i++, j += 3) {
			final Vector3F vector3F = vector3Fs.get(i);
			
			vector3Fs0[j + 0] = vector3F.x;
			vector3Fs0[j + 1] = vector3F.y;
			vector3Fs0[j + 2] = vector3F.z;
		}
		
		doReportProgress(" Done.\n");
		
		return vector3Fs0;
	}
	
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
	
	private static float[] doToFloatArray(final Shape shape, final List<Surface> surfaces, final Map<Point2F, Integer> point2Fs, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs) {
		if(shape instanceof Plane) {
			return doToFloatArrayPlane(Plane.class.cast(shape), surfaces, point3Fs, vector3Fs);
		} else if(shape instanceof Sphere) {
			return doToFloatArraySphere(Sphere.class.cast(shape), surfaces, point3Fs);
		} else if(shape instanceof Terrain) {
			return doToFloatArrayTerrain(Terrain.class.cast(shape), surfaces);
		} else if(shape instanceof Triangle) {
			return doToFloatArrayTriangle(Triangle.class.cast(shape), surfaces, point2Fs, point3Fs, vector3Fs);
		} else {
			throw new IllegalArgumentException(String.format("The Shape provided is not supported: %s", shape));
		}
	}
	
	private static float[] doToFloatArray(final Surface surface, final List<Texture> textures) {
		return new float[] {
			surface.getEmission().r,
			surface.getEmission().g,
			surface.getEmission().b,
			surface.getMaterial().ordinal(),
			doGetOffset(surface.getTextureAlbedo(), textures),
			doGetOffset(surface.getTextureNormal(), textures),
			surface.getPerlinNoiseAmount(),
			surface.getPerlinNoiseScale()
		};
	}
	
	private static float[] doToFloatArray(final Texture texture) {
		if(texture instanceof CheckerboardTexture) {
			return doToFloatArrayCheckerboardTexture(CheckerboardTexture.class.cast(texture));
		} else if(texture instanceof FractionalBrownianMotionTexture) {
			return doToFloatArrayFractionalBrownianMotionTexture(FractionalBrownianMotionTexture.class.cast(texture));
		} else if(texture instanceof ImageTexture) {
			return doToFloatArrayImageTexture(ImageTexture.class.cast(texture));
		} else if(texture instanceof SolidTexture) {
			return doToFloatArraySolidTexture(SolidTexture.class.cast(texture));
		} else if(texture instanceof SurfaceNormalTexture) {
			return doToFloatArraySurfaceNormalTexture(SurfaceNormalTexture.class.cast(texture));
		} else {
			throw new IllegalArgumentException(String.format("The Texture provided is not supported: %s", texture));
		}
	}
	
	private static float[] doToFloatArrayCheckerboardTexture(final CheckerboardTexture checkerboardTexture) {
		return new float[] {
			CHECKERBOARD_TEXTURE_TYPE,
			CHECKERBOARD_TEXTURE_SIZE,
			checkerboardTexture.getColor0().multiply(255.0F).toRGB(),
			checkerboardTexture.getColor1().multiply(255.0F).toRGB(),
			cos(toRadians(checkerboardTexture.getDegrees())),
			sin(toRadians(checkerboardTexture.getDegrees())),
			checkerboardTexture.getScaleU(),
			checkerboardTexture.getScaleV()
		};
	}
	
	private static float[] doToFloatArrayFractionalBrownianMotionTexture(final FractionalBrownianMotionTexture fractionalBrownianMotionTexture) {
		return new float[] {
			FRACTIONAL_BROWNIAN_MOTION_TEXTURE_TYPE,
			FRACTIONAL_BROWNIAN_MOTION_TEXTURE_SIZE,
			fractionalBrownianMotionTexture.getAddend().multiply(255.0F).toRGB(),
			fractionalBrownianMotionTexture.getMultiplier().multiply(255.0F).toRGB(),
			fractionalBrownianMotionTexture.getPersistence(),
			fractionalBrownianMotionTexture.getScale(),
			fractionalBrownianMotionTexture.getOctaves()
		};
	}
	
	private static float[] doToFloatArrayImageTexture(final ImageTexture imageTexture) {
		final int size = 8 + imageTexture.getData().length;
		
		final float[] floatArray = new float[size];
		
		floatArray[0] = IMAGE_TEXTURE_TYPE;
		floatArray[1] = size;
		floatArray[2] = cos(toRadians(imageTexture.getDegrees()));
		floatArray[3] = sin(toRadians(imageTexture.getDegrees()));
		floatArray[4] = imageTexture.getWidth();
		floatArray[5] = imageTexture.getHeight();
		floatArray[6] = imageTexture.getScaleU();
		floatArray[7] = imageTexture.getScaleV();
		
		final int[] data = imageTexture.getData();
		
		for(int i = 0; i < data.length; i++) {
			floatArray[i + 8] = data[i];
		}
		
		return floatArray;
	}
	
	private static float[] doToFloatArrayPlane(final Plane plane, final List<Surface> surfaces, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs) {
		return new float[] {
			PLANE_TYPE,
			doGetOffset(plane.getSurface(), surfaces),
			point3Fs.get(plane.getA()).intValue(),
			point3Fs.get(plane.getB()).intValue(),
			point3Fs.get(plane.getC()).intValue(),
			vector3Fs.get(plane.getSurfaceNormal()).intValue()
		};
	}
	
	private static float[] doToFloatArraySolidTexture(final SolidTexture solidTexture) {
		return new float[] {
			SOLID_TEXTURE_TYPE,
			SOLID_TEXTURE_SIZE,
			solidTexture.getColor().multiply(255.0F).toRGB()
		};
	}
	
	private static float[] doToFloatArraySphere(final Sphere sphere, final List<Surface> surfaces, final Map<Point3F, Integer> point3Fs) {
		return new float[] {
			SPHERE_TYPE,
			doGetOffset(sphere.getSurface(), surfaces),
			sphere.getRadius(),
			point3Fs.get(sphere.getPosition()).intValue()
		};
	}
	
	private static float[] doToFloatArraySurfaceNormalTexture(final SurfaceNormalTexture surfaceNormalTexture) {
		return new float[] {
			SURFACE_NORMAL_TEXTURE_TYPE,
			SURFACE_NORMAL_TEXTURE_SIZE,
			surfaceNormalTexture.isTangentSpace() ? 1.0F : 0.0F
		};
	}
	
	private static float[] doToFloatArrayTerrain(final Terrain terrain, final List<Surface> surfaces) {
		return new float[] {
			TERRAIN_TYPE,
			doGetOffset(terrain.getSurface(), surfaces),
			terrain.getPersistence(),
			terrain.getScale(),
			terrain.getMinimum(),
			terrain.getMaximum(),
			terrain.getOctaves()
		};
	}
	
	private static float[] doToFloatArrayTriangle(final Triangle triangle, final List<Surface> surfaces, final Map<Point2F, Integer> point2Fs, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs) {
		return new float[] {
			TRIANGLE_TYPE,
			doGetOffset(triangle.getSurface(), surfaces),
			point3Fs.get(triangle.getA().position).intValue(),
			point3Fs.get(triangle.getB().position).intValue(),
			point3Fs.get(triangle.getC().position).intValue(),
			vector3Fs.get(triangle.getA().normal).intValue(),
			vector3Fs.get(triangle.getB().normal).intValue(),
			vector3Fs.get(triangle.getC().normal).intValue(),
			point2Fs.get(triangle.getA().textureCoordinates).intValue(),
			point2Fs.get(triangle.getB().textureCoordinates).intValue(),
			point2Fs.get(triangle.getC().textureCoordinates).intValue()
		};
	}
	
	private static int doGetOffset(final Shape shape, final List<Shape> shapes) {
		for(int i = 0, j = 0; i < shapes.size(); i++) {
			final Shape shape0 = shapes.get(i);
			
			if(shape.equals(shape0)) {
				return j;
			}
			
			j += doSize(shape0);
		}
		
		throw new IllegalArgumentException(String.format("No such Shape found: %s", shape));
	}
	
	private static int doGetOffset(final Surface surface, final List<Surface> surfaces) {
		for(int i = 0, j = 0; i < surfaces.size(); i++) {
			final Surface surface0 = surfaces.get(i);
			
			if(surface.equals(surface0)) {
				return j;
			}
			
			j += SURFACE_SIZE;
		}
		
		throw new IllegalArgumentException(String.format("No such Surface found: %s", surface));
	}
	
	private static int doGetOffset(final Texture texture, final List<Texture> textures) {
		for(int i = 0, j = 0; i < textures.size(); i++) {
			final Texture texture0 = textures.get(i);
			
			if(texture.equals(texture0)) {
				return j;
			}
			
			j += doSize(texture0);
		}
		
		throw new IllegalArgumentException(String.format("No such Texture found: %s", texture));
	}
	
	private static int doSize(final int type) {
		switch(type) {
			case PLANE_TYPE:
				return PLANE_SIZE;
			case SPHERE_TYPE:
				return SPHERE_SIZE;
			case TERRAIN_TYPE:
				return TERRAIN_SIZE;
			case TRIANGLE_TYPE:
				return TRIANGLE_SIZE;
			default:
				throw new IllegalArgumentException(String.format("No such Shape found: %s", Integer.toString(type)));
		}
	}
	
	private static int doSize(final Node node) {
		int size = 0;
		
		if(node instanceof LeafNode) {
			size += 9 + LeafNode.class.cast(node).getTriangles().size();
		} else if(node instanceof TreeNode) {
			final Optional<Node> left = TreeNode.class.cast(node).getLeft();
			final Optional<Node> right = TreeNode.class.cast(node).getRight();
			
			size += 9;
			
			if(left.isPresent()) {
				size += doSize(left.get());
			}
			
			if(right.isPresent()) {
				size += doSize(right.get());
			}
		}
		
		return size;
	}
	
	private static int doSize(final Shape shape) {
		if(shape instanceof Plane) {
			return PLANE_SIZE;
		} else if(shape instanceof Sphere) {
			return SPHERE_SIZE;
		} else if(shape instanceof Terrain) {
			return TERRAIN_SIZE;
		} else if(shape instanceof Triangle) {
			return TRIANGLE_SIZE;
		} else {
			throw new IllegalArgumentException(String.format("The Shape provided is not supported: %s", shape));
		}
	}
	
	private static int doSize(final Texture texture) {
		if(texture instanceof CheckerboardTexture) {
			return CHECKERBOARD_TEXTURE_SIZE;
		} else if(texture instanceof FractionalBrownianMotionTexture) {
			return FRACTIONAL_BROWNIAN_MOTION_TEXTURE_SIZE;
		} else if(texture instanceof ImageTexture) {
			return 8 + ImageTexture.class.cast(texture).getData().length;
		} else if(texture instanceof SolidTexture) {
			return SOLID_TEXTURE_SIZE;
		} else if(texture instanceof SurfaceNormalTexture) {
			return SURFACE_NORMAL_TEXTURE_SIZE;
		} else {
			throw new IllegalArgumentException(String.format("The Texture provided is not supported: %s", texture));
		}
	}
	
	private static int[] doCompileShapeOffsets(final Scene scene) {
		final List<Shape> shapes = scene.getShapes();
		
		int count = 0;
		
		for(final Shape shape : shapes) {
			if(!(shape instanceof Triangle)) {
				count++;
			}
		}
		
		final int[] shapeOffsets = new int[count];
		
		int index = 0;
		
		for(final Shape shape : shapes) {
			if(!(shape instanceof Triangle)) {
				shapeOffsets[index++] = doGetOffset(shape, shapes);
			}
		}
		
		return shapeOffsets;
	}
	
	private static int[] doReadIntArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final int[] array = new int[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readInt();
		}
		
		return array;
	}
	
	private static List<Node> doToList(final Node node) {
		return doToList(node, new ArrayList<>());
	}
	
	private static List<Node> doToList(final Node node, final List<Node> nodes) {
		if(node instanceof LeafNode) {
			nodes.add(node);
		} else if(node instanceof TreeNode) {
			nodes.add(node);
			
			final TreeNode treeNode = TreeNode.class.cast(node);
			
			final Optional<Node> optionalLeft = treeNode.getLeft();
			final Optional<Node> optionalRight = treeNode.getRight();
			
			if(optionalLeft.isPresent()) {
				final Node left = optionalLeft.get();
				
				doToList(left, nodes);
			}
			
			if(optionalRight.isPresent()) {
				final Node right = optionalRight.get();
				
				doToList(right, nodes);
			}
		}
		
		return nodes;
	}
	
	private static List<Point2F> doFindPoint2Fs(final Scene scene) {
		final Set<Point2F> point2Fs = new LinkedHashSet<>();
		
		for(final Shape shape : scene.getShapes()) {
			if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point2F a = triangle.getA().getTextureCoordinates();
				final Point2F b = triangle.getB().getTextureCoordinates();
				final Point2F c = triangle.getC().getTextureCoordinates();
				
				point2Fs.add(a);
				point2Fs.add(b);
				point2Fs.add(c);
			}
		}
		
		return new ArrayList<>(point2Fs);
	}
	
	private static List<Point3F> doFindPoint3Fs(final Scene scene) {
		final Set<Point3F> point3Fs = new LinkedHashSet<>();
		
		for(final Shape shape : scene.getShapes()) {
			if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Point3F a = plane.getA();
				final Point3F b = plane.getB();
				final Point3F c = plane.getC();
				
				point3Fs.add(a);
				point3Fs.add(b);
				point3Fs.add(c);
			} else if(shape instanceof Sphere) {
				final Sphere sphere = Sphere.class.cast(shape);
				
				final Point3F position = sphere.getPosition();
				
				point3Fs.add(position);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point3F a = triangle.getA().getPosition();
				final Point3F b = triangle.getB().getPosition();
				final Point3F c = triangle.getC().getPosition();
				
				point3Fs.add(a);
				point3Fs.add(b);
				point3Fs.add(c);
			}
		}
		
		return new ArrayList<>(point3Fs);
	}
	
	private static List<Surface> doFindSurfaces(final Scene scene) {
		final Set<Surface> surfaces = new LinkedHashSet<>();
		
		for(final Shape shape : scene.getShapes()) {
			final Surface surface = shape.getSurface();
			
			surfaces.add(surface);
		}
		
		return new ArrayList<>(surfaces);
	}
	
	private static List<Texture> doFindTextures(final Scene scene) {
		final Set<Texture> textures = new LinkedHashSet<>();
		
		for(final Shape shape : scene.getShapes()) {
			final Surface surface = shape.getSurface();
			
			final Texture textureAlbedo = surface.getTextureAlbedo();
			final Texture textureNormal = surface.getTextureNormal();
			
			textures.add(textureAlbedo);
			textures.add(textureNormal);
		}
		
		return new ArrayList<>(textures);
	}
	
	private static List<Vector3F> doFindVector3Fs(final Scene scene) {
		final Set<Vector3F> vector3Fs = new LinkedHashSet<>();
		
		for(final Shape shape : scene.getShapes()) {
			if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Vector3F surfaceNormal = plane.getSurfaceNormal();
				
				vector3Fs.add(surfaceNormal);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Vector3F a = triangle.getA().getNormal();
				final Vector3F b = triangle.getB().getNormal();
				final Vector3F c = triangle.getC().getNormal();
				
				vector3Fs.add(a);
				vector3Fs.add(b);
				vector3Fs.add(c);
			}
		}
		
		return new ArrayList<>(vector3Fs);
	}
	
	private static Map<Point2F, Integer> doCreatePoint2FMapping(final List<Point2F> point2Fs) {
		final Map<Point2F, Integer> point2FMapping = new HashMap<>();
		
		for(int i = 0, j = 0; i < point2Fs.size(); i++, j += 2) {
			final Point2F point2F = point2Fs.get(i);
			
			point2FMapping.put(point2F, Integer.valueOf(j));
		}
		
		return point2FMapping;
	}
	
	private static Map<Point3F, Integer> doCreatePoint3FMapping(final List<Point3F> point3Fs) {
		final Map<Point3F, Integer> point3FMapping = new HashMap<>();
		
		for(int i = 0, j = 0; i < point3Fs.size(); i++, j += 3) {
			final Point3F point3F = point3Fs.get(i);
			
			point3FMapping.put(point3F, Integer.valueOf(j));
		}
		
		return point3FMapping;
	}
	
	private static Map<Vector3F, Integer> doCreateVector3FMapping(final List<Vector3F> vector3Fs) {
		final Map<Vector3F, Integer> vector3FMapping = new HashMap<>();
		
		for(int i = 0, j = 0; i < vector3Fs.size(); i++, j += 3) {
			final Vector3F vector3F = vector3Fs.get(i);
			
			vector3FMapping.put(vector3F, Integer.valueOf(j));
		}
		
		return vector3FMapping;
	}
	
	private static void doReorderShapes(final float[] boundingVolumeHierarchy, final float[] shapes, final int[] shapeOffsets) {
		doReportProgress("Reordering Shapes...");
		
		if(boundingVolumeHierarchy.length > 9) {
			final float[] shapes0 = shapes;
			final float[] shapes1 = new float[shapes0.length];
			
			int boundingVolumeHierarchyOffset = 0;
			int shapes1Offset = 0;
			
			while(boundingVolumeHierarchyOffset != -1) {
				final int type = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset]);
				
				if(type == BVH_NODE_TYPE_TREE) {
					boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
				} else if(type == BVH_NODE_TYPE_LEAF) {
					for(int i = 0; i < (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]); i++) {
						final int index = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
						final int type0 = (int)(shapes0[index + SHAPE_RELATIVE_OFFSET_TYPE]);
						final int size = doSize(type0);
						
						System.arraycopy(shapes0, index, shapes1, shapes1Offset, size);
						
						boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i] = shapes1Offset;
						
						shapes1Offset += size;
					}
					
					boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
				}
			}
			
			for(int i = 0, j = 0, k = 0; i < shapes0.length; i += j) {
				final int type = (int)(shapes0[i + SHAPE_RELATIVE_OFFSET_TYPE]);
				final int size = doSize(type);
				
				j = size;
				
				if(type != TRIANGLE_TYPE) {
					System.arraycopy(shapes0, i, shapes1, shapes1Offset, size);
					
					shapeOffsets[k] = shapes1Offset;
					
					shapes1Offset += size;
					
					k++;
				}
			}
			
			System.arraycopy(shapes1, 0, shapes0, 0, shapes1.length);
		}
		
		doReportProgress(" Done.\n");
	}
	
	private static void doReportProgress(final String message) {
		System.out.print(message);
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