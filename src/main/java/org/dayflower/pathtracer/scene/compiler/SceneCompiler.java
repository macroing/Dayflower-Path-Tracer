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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
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

/**
 * A class that compiles {@link Scene}s so they can be used by Dayflower - Path Tracer.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SceneCompiler {
	public SceneCompiler() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene compile(final Scene scene) {
		final List<Point2F> point2Fs0 = doFindPoint2Fs(scene);
		final List<Point3F> point3Fs0 = doFindPoint3Fs(scene);
		final List<Surface> surfaces0 = doFindSurfaces(scene);
		final List<Texture> textures0 = doFindTextures(scene);
		final List<Vector3F> vector3Fs0 = doFindVector3Fs(scene);
		
		final Map<Point2F, Integer> point2Fs1 = doCreatePoint2FMapping(point2Fs0);
		final Map<Point3F, Integer> point3Fs1 = doCreatePoint3FMapping(point3Fs0);
		final Map<Vector3F, Integer> vector3Fs1 = doCreateVector3FMapping(vector3Fs0);
		
		final float[] boundingVolumeHierarchy = doCompileBoundingVolumeHierarchy(scene);
		final float[] camera0 = scene.getCamera().getArray();
		final float[] point2Fs = doCompilePoint2Fs(point2Fs0);
		final float[] point3Fs = doCompilePoint3Fs(point3Fs0);
		final float[] shapes = doCompileShapes(surfaces0, point2Fs1, point3Fs1, vector3Fs1, scene);
		final float[] surfaces = doCompileSurfaces(surfaces0, textures0);
		final float[] textures = doCompileTextures(textures0);
		final float[] vector3Fs = doCompileVector3Fs(vector3Fs0);
		
		final int[] shapeOffsets = doCompileShapeOffsets(scene);
		
		doReorderShapes(boundingVolumeHierarchy, shapes, shapeOffsets);
		
		return new CompiledScene(scene.getName(), boundingVolumeHierarchy, camera0, point2Fs, point3Fs, shapes, surfaces, textures, vector3Fs, shapeOffsets);
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
			
			return new float[] {BoundingVolumeHierarchy.NODE_TYPE_LEAF, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
		}
		
		final BoundingVolumeHierarchy boundingVolumeHierarchy = BoundingVolumeHierarchy.createBoundingVolumeHierarchy(triangles);
		
		final Node root = boundingVolumeHierarchy.getRoot();
		
		final int size = root.getSize();
		
		final float[] boundingVolumeHierarchyArray = new float[size];
		
		final List<Node> nodes = root.toList();
		
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
				
				boundingVolumeHierarchyArray[j + 0] = BoundingVolumeHierarchy.NODE_TYPE_LEAF;
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
				
				boundingVolumeHierarchyArray[j + 0] = BoundingVolumeHierarchy.NODE_TYPE_TREE;
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
			final float[] floatArray = texture.toArray();
			
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
			surface.getNoiseAmount(),
			surface.getNoiseScale()
		};
	}
	
	private static float[] doToFloatArrayPlane(final Plane plane, final List<Surface> surfaces, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs) {
		return new float[] {
			Plane.TYPE,
			doGetOffset(plane.getSurface(), surfaces),
			point3Fs.get(plane.getA()).intValue(),
			point3Fs.get(plane.getB()).intValue(),
			point3Fs.get(plane.getC()).intValue(),
			vector3Fs.get(plane.getSurfaceNormal()).intValue()
		};
	}
	
	private static float[] doToFloatArraySphere(final Sphere sphere, final List<Surface> surfaces, final Map<Point3F, Integer> point3Fs) {
		return new float[] {
			Sphere.TYPE,
			doGetOffset(sphere.getSurface(), surfaces),
			sphere.getRadius(),
			point3Fs.get(sphere.getPosition()).intValue()
		};
	}
	
	private static float[] doToFloatArrayTerrain(final Terrain terrain, final List<Surface> surfaces) {
		return new float[] {
			Terrain.TYPE,
			doGetOffset(terrain.getSurface(), surfaces),
			terrain.getFrequency(),
			terrain.getGain(),
			terrain.getMinimum(),
			terrain.getMaximum(),
			terrain.getOctaves()
		};
	}
	
	private static float[] doToFloatArrayTriangle(final Triangle triangle, final List<Surface> surfaces, final Map<Point2F, Integer> point2Fs, final Map<Point3F, Integer> point3Fs, final Map<Vector3F, Integer> vector3Fs) {
		return new float[] {
			Triangle.TYPE,
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
			
			j += shape0.getSize();
		}
		
		throw new IllegalArgumentException(String.format("No such Shape found: %s", shape));
	}
	
	private static int doGetOffset(final Surface surface, final List<Surface> surfaces) {
		for(int i = 0, j = 0; i < surfaces.size(); i++) {
			final Surface surface0 = surfaces.get(i);
			
			if(surface.equals(surface0)) {
				return j;
			}
			
			j += Surface.SIZE;
		}
		
		throw new IllegalArgumentException(String.format("No such Surface found: %s", surface));
	}
	
	private static int doGetOffset(final Texture texture, final List<Texture> textures) {
		for(int i = 0, j = 0; i < textures.size(); i++) {
			final Texture texture0 = textures.get(i);
			
			if(texture.equals(texture0)) {
				return j;
			}
			
			j += texture0.getSize();
		}
		
		throw new IllegalArgumentException(String.format("No such Texture found: %s", texture));
	}
	
	private static int doSize(final int type) {
		switch(type) {
			case Plane.TYPE:
				return Plane.SIZE;
			case Sphere.TYPE:
				return Sphere.SIZE;
			case Terrain.TYPE:
				return Terrain.SIZE;
			case Triangle.TYPE:
				return Triangle.SIZE;
			default:
				throw new IllegalArgumentException(String.format("No such Shape found: %s", Integer.toString(type)));
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
				
				if(type == BoundingVolumeHierarchy.NODE_TYPE_TREE) {
					boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
				} else if(type == BoundingVolumeHierarchy.NODE_TYPE_LEAF) {
					for(int i = 0; i < (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]); i++) {
						final int index = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
						final int type0 = (int)(shapes0[index + Shape.RELATIVE_OFFSET_TYPE]);
						final int size = doSize(type0);
						
						System.arraycopy(shapes0, index, shapes1, shapes1Offset, size);
						
						boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i] = shapes1Offset;
						
						shapes1Offset += size;
					}
					
					boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
				}
			}
			
			for(int i = 0, j = 0, k = 0; i < shapes0.length; i += j) {
				final int type = (int)(shapes0[i + Shape.RELATIVE_OFFSET_TYPE]);
				final int size = doSize(type);
				
				j = size;
				
				if(type != Triangle.TYPE) {
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
}