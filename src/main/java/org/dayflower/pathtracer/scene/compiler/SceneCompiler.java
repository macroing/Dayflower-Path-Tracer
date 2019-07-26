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
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.LeafNode;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.Node;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.TreeNode;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.util.Arrays2;

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
		final long currentTimeMillis0 = System.currentTimeMillis();
		
//		Retrieve all unique Primitives:
		final List<Primitive> uniquePrimitives = doFindUniquePrimitives(scene);
		
//		Retrieve all unique Shapes:
		final List<Mesh> uniqueMeshes = doFindUniqueMeshes(uniquePrimitives);
		final List<Plane> uniquePlanes = doFindUniquePlanes(uniquePrimitives);
		final List<Sphere> uniqueSpheres = doFindUniqueSpheres(uniquePrimitives);
		final List<Terrain> uniqueTerrains = doFindUniqueTerrains(uniquePrimitives);
		final List<Triangle> uniqueTriangles = doFindUniqueTriangles(uniquePrimitives);
		
//		Retrieve all unique BoundingVolumeHierarchy root-Nodes:
		final List<Node> uniqueBoundingVolumeHierarchyRootNodes = doFindUniqueBoundingVolumeHierarchyRootNodes(uniqueMeshes);
		
//		Retrieve all unique Surfaces:
		final List<Surface> uniqueSurfaces = doFindUniqueSurfaces(uniquePrimitives);
		
//		Retrieve all unique Textures:
		final List<Texture> uniqueTextures = doFindUniqueTextures(uniqueSurfaces);
		
//		Retrieve all unique Point2Fs, Point3Fs and Vector3Fs:
		final List<Point2F> uniquePoint2Fs = doFindUniquePoint2Fs(uniquePrimitives);
		final List<Point3F> uniquePoint3Fs = doFindUniquePoint3Fs(uniqueBoundingVolumeHierarchyRootNodes, uniquePrimitives);
		final List<Vector3F> uniqueVector3Fs = doFindUniqueVector3Fs(uniquePrimitives);
		
//		Create mappings from Shapes to Integer indices:
		final Map<Plane, Integer> planeMappings = doCreatePlaneMappings(uniquePlanes);
		final Map<Sphere, Integer> sphereMappings = doCreateSphereMappings(uniqueSpheres);
		final Map<Terrain, Integer> terrainMappings = doCreateTerrainMappings(uniqueTerrains);
		final Map<Triangle, Integer> triangleMappings = doCreateTriangleMappings(uniqueTriangles);
		
//		Create mappings from Surfaces to Integer indices:
		final Map<Surface, Integer> surfaceMappings = doCreateSurfaceMappings(uniqueSurfaces);
		
//		Create mappings from Textures to Integer indices:
		final Map<Texture, Integer> textureMappings = doCreateTextureMappings(uniqueTextures);
		
//		Create mappings from Point2Fs, Point3Fs and Vector3Fs to Integer indices:
		final Map<Point2F, Integer> point2FMappings = doCreatePoint2FMappings(uniquePoint2Fs);
		final Map<Point3F, Integer> point3FMappings = doCreatePoint3FMappings(uniquePoint3Fs);
		final Map<Vector3F, Integer> vector3FMappings = doCreateVector3FMappings(uniqueVector3Fs);
		
//		Compile the scene:
		final float[] camera = scene.getCamera().getArray();
		final float[] point2Fs = doCompilePoint2Fs(uniquePoint2Fs);
		final float[] point3Fs = doCompilePoint3Fs(uniquePoint3Fs);
		final float[] spheres = doCompileSpheres(uniqueSpheres, point3FMappings);
		final float[] surfaces = doCompileSurfaces(uniqueSurfaces, textureMappings);
		final float[] terrains = doCompileTerrains(uniqueTerrains);
		final float[] textures = doCompileTextures(uniqueTextures);
		final float[] vector3Fs = doCompileVector3Fs(uniqueVector3Fs);
		
		final int[] boundingVolumeHierarchies = doCompileBoundingVolumeHierarchies(uniqueBoundingVolumeHierarchyRootNodes, point3FMappings, triangleMappings);
		final int[] planes = doCompilePlanes(uniquePlanes, point3FMappings, vector3FMappings);
		final int[] primitives = doCompilePrimitives(uniquePrimitives, uniqueMeshes, uniqueBoundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, surfaceMappings, terrainMappings, triangleMappings);
		final int[] triangles = doCompileTriangles(uniqueTriangles, point2FMappings, point3FMappings, vector3FMappings);
		
		final long currentTimeMillis1 = System.currentTimeMillis();
		final long currentTimeMillis2 = currentTimeMillis1 - currentTimeMillis0;
		
		doReportProgress("Compilation took " + currentTimeMillis2 + " ms.");
		
		return new CompiledScene(scene.getName(), camera, point2Fs, point3Fs, spheres, surfaces, terrains, textures, vector3Fs, boundingVolumeHierarchies, planes, primitives, triangles);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static List<Node> doFindUniqueBoundingVolumeHierarchyRootNodes(final List<Mesh> meshes) {
		return meshes.stream().map(mesh -> BoundingVolumeHierarchy.createBoundingVolumeHierarchy(mesh.getTriangles()).getRoot()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Mesh> doFindUniqueMeshes(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Mesh).map(primitive -> Mesh.class.cast(primitive.getShape())).distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Plane> doFindUniquePlanes(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Plane).map(primitive -> Plane.class.cast(primitive.getShape())).distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Point2F> doFindUniquePoint2Fs(final List<Primitive> primitives) {
		final Set<Point2F> uniquePoint2Fs = new LinkedHashSet<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Mesh) {
				final Mesh mesh = Mesh.class.cast(shape);
				
				for(final Triangle triangle : mesh.getTriangles()) {
					final Point2F a = triangle.getA().getTextureCoordinates();
					final Point2F b = triangle.getB().getTextureCoordinates();
					final Point2F c = triangle.getC().getTextureCoordinates();
					
					uniquePoint2Fs.add(a);
					uniquePoint2Fs.add(b);
					uniquePoint2Fs.add(c);
				}
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point2F a = triangle.getA().getTextureCoordinates();
				final Point2F b = triangle.getB().getTextureCoordinates();
				final Point2F c = triangle.getC().getTextureCoordinates();
				
				uniquePoint2Fs.add(a);
				uniquePoint2Fs.add(b);
				uniquePoint2Fs.add(c);
			}
		}
		
		return new ArrayList<>(uniquePoint2Fs);
	}
	
	private static List<Point3F> doFindUniquePoint3Fs(final List<Node> uniqueBoundingVolumeHierarchyRootNodes, final List<Primitive> primitives) {
		final Set<Point3F> uniquePoint3Fs = new LinkedHashSet<>();
		
		for(final Node uniqueBoundingVolumeHierarchyRootNode : uniqueBoundingVolumeHierarchyRootNodes) {
			uniqueBoundingVolumeHierarchyRootNode.addBounds(uniquePoint3Fs);
		}
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Mesh) {
				final Mesh mesh = Mesh.class.cast(shape);
				
				for(final Triangle triangle : mesh.getTriangles()) {
					final Point3F a = triangle.getA().getPosition();
					final Point3F b = triangle.getB().getPosition();
					final Point3F c = triangle.getC().getPosition();
					
					uniquePoint3Fs.add(a);
					uniquePoint3Fs.add(b);
					uniquePoint3Fs.add(c);
				}
			} else if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Point3F a = plane.getA();
				final Point3F b = plane.getB();
				final Point3F c = plane.getC();
				
				uniquePoint3Fs.add(a);
				uniquePoint3Fs.add(b);
				uniquePoint3Fs.add(c);
			} else if(shape instanceof Sphere) {
				final Sphere sphere = Sphere.class.cast(shape);
				
				final Point3F position = sphere.getPosition();
				
				uniquePoint3Fs.add(position);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point3F a = triangle.getA().getPosition();
				final Point3F b = triangle.getB().getPosition();
				final Point3F c = triangle.getC().getPosition();
				
				uniquePoint3Fs.add(a);
				uniquePoint3Fs.add(b);
				uniquePoint3Fs.add(c);
			}
		}
		
		return new ArrayList<>(uniquePoint3Fs);
	}
	
	private static List<Primitive> doFindUniquePrimitives(final Scene scene) {
		return scene.getPrimitives().stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Sphere> doFindUniqueSpheres(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Sphere).map(primitive -> Sphere.class.cast(primitive.getShape())).distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Surface> doFindUniqueSurfaces(final List<Primitive> primitives) {
		return primitives.stream().map(primitive -> primitive.getSurface()).distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Terrain> doFindUniqueTerrains(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Terrain).map(primitive -> Terrain.class.cast(primitive.getShape())).distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Texture> doFindUniqueTextures(final List<Surface> surfaces) {
		final Set<Texture> uniqueTextures = new LinkedHashSet<>();
		
		for(final Surface surface : surfaces) {
			final Texture textureAlbedo = surface.getTextureAlbedo();
			final Texture textureEmission = surface.getTextureEmission();
			final Texture textureNormal = surface.getTextureNormal();
			
			uniqueTextures.add(textureAlbedo);
			uniqueTextures.add(textureEmission);
			uniqueTextures.add(textureNormal);
		}
		
		return new ArrayList<>(uniqueTextures);
	}
	
	private static List<Triangle> doFindUniqueTriangles(final List<Primitive> primitives) {
		final Set<Triangle> uniqueTriangles = new LinkedHashSet<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Mesh) {
				final Mesh mesh = Mesh.class.cast(shape);
				
				for(final Triangle triangle : mesh.getTriangles()) {
					uniqueTriangles.add(triangle);
				}
			} else if(shape instanceof Triangle) {
				uniqueTriangles.add(Triangle.class.cast(shape));
			}
		}
		
		return new ArrayList<>(uniqueTriangles);
	}
	
	private static List<Vector3F> doFindUniqueVector3Fs(final List<Primitive> primitives) {
		final Set<Vector3F> uniqueVector3Fs = new LinkedHashSet<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Mesh) {
				final Mesh mesh = Mesh.class.cast(shape);
				
				for(final Triangle triangle : mesh.getTriangles()) {
					final Vector3F a = triangle.getA().getNormal();
					final Vector3F b = triangle.getB().getNormal();
					final Vector3F c = triangle.getC().getNormal();
					
					uniqueVector3Fs.add(a);
					uniqueVector3Fs.add(b);
					uniqueVector3Fs.add(c);
				}
			} else if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Vector3F surfaceNormal = plane.getSurfaceNormal();
				
				uniqueVector3Fs.add(surfaceNormal);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Vector3F a = triangle.getA().getNormal();
				final Vector3F b = triangle.getB().getNormal();
				final Vector3F c = triangle.getC().getNormal();
				
				uniqueVector3Fs.add(a);
				uniqueVector3Fs.add(b);
				uniqueVector3Fs.add(c);
			}
		}
		
		return new ArrayList<>(uniqueVector3Fs);
	}
	
	private static Map<Plane, Integer> doCreatePlaneMappings(final List<Plane> planes) {
		final Map<Plane, Integer> planeMappings = new HashMap<>();
		
		for(int i = 0; i < planes.size(); i++) {
			planeMappings.put(planes.get(i), Integer.valueOf(i * Plane.SIZE));
		}
		
		return planeMappings;
	}
	
	private static Map<Point2F, Integer> doCreatePoint2FMappings(final List<Point2F> point2Fs) {
		final Map<Point2F, Integer> point2FMappings = new HashMap<>();
		
		for(int i = 0; i < point2Fs.size(); i++) {
			point2FMappings.put(point2Fs.get(i), Integer.valueOf(i * 2));
		}
		
		return point2FMappings;
	}
	
	private static Map<Point3F, Integer> doCreatePoint3FMappings(final List<Point3F> point3Fs) {
		final Map<Point3F, Integer> point3FMappings = new HashMap<>();
		
		for(int i = 0; i < point3Fs.size(); i++) {
			point3FMappings.put(point3Fs.get(i), Integer.valueOf(i * 3));
		}
		
		return point3FMappings;
	}
	
	private static Map<Sphere, Integer> doCreateSphereMappings(final List<Sphere> spheres) {
		final Map<Sphere, Integer> sphereMappings = new HashMap<>();
		
		for(int i = 0; i < spheres.size(); i++) {
			sphereMappings.put(spheres.get(i), Integer.valueOf(i * Sphere.SIZE));
		}
		
		return sphereMappings;
	}
	
	private static Map<Surface, Integer> doCreateSurfaceMappings(final List<Surface> surfaces) {
		final Map<Surface, Integer> surfaceMappings = new HashMap<>();
		
		for(int i = 0; i < surfaces.size(); i++) {
			surfaceMappings.put(surfaces.get(i), Integer.valueOf(i * Surface.SIZE));
		}
		
		return surfaceMappings;
	}
	
	private static Map<Terrain, Integer> doCreateTerrainMappings(final List<Terrain> terrains) {
		final Map<Terrain, Integer> terrainMappings = new HashMap<>();
		
		for(int i = 0; i < terrains.size(); i++) {
			terrainMappings.put(terrains.get(i), Integer.valueOf(i * Terrain.SIZE));
		}
		
		return terrainMappings;
	}
	
	private static Map<Texture, Integer> doCreateTextureMappings(final List<Texture> textures) {
		final Map<Texture, Integer> textureMappings = new HashMap<>();
		
		for(int i = 0, j = 0; i < textures.size(); i++) {
			final Texture texture = textures.get(i);
			
			textureMappings.put(texture, Integer.valueOf(j));
			
			j += texture.getSize();
		}
		
		return textureMappings;
	}
	
	private static Map<Triangle, Integer> doCreateTriangleMappings(final List<Triangle> triangles) {
		final Map<Triangle, Integer> triangleMappings = new HashMap<>();
		
		for(int i = 0; i < triangles.size(); i++) {
			triangleMappings.put(triangles.get(i), Integer.valueOf(i * Triangle.SIZE));
		}
		
		return triangleMappings;
	}
	
	private static Map<Vector3F, Integer> doCreateVector3FMappings(final List<Vector3F> vector3Fs) {
		final Map<Vector3F, Integer> vector3FMappings = new HashMap<>();
		
		for(int i = 0; i < vector3Fs.size(); i++) {
			vector3FMappings.put(vector3Fs.get(i), Integer.valueOf(i * 3));
		}
		
		return vector3FMappings;
	}
	
	private static float[] doCompilePoint2Fs(final List<Point2F> point2Fs) {
		doReportProgress("Compiling Point2Fs...");
		
		final float[] compiledPoint2Fs = new float[point2Fs.size() * 2];
		
		for(int i = 0; i < point2Fs.size(); i++) {
			final Point2F point2F = point2Fs.get(i);
			
			compiledPoint2Fs[i * 2 + 0] = point2F.x;
			compiledPoint2Fs[i * 2 + 1] = point2F.y;
		}
		
		doReportProgress(" Done.\n");
		
		return compiledPoint2Fs.length > 0 ? compiledPoint2Fs : new float[1];
	}
	
	private static float[] doCompilePoint3Fs(final List<Point3F> point3Fs) {
		doReportProgress("Compiling Point3Fs...");
		
		final float[] compiledPoint3Fs = new float[point3Fs.size() * 3];
		
		for(int i = 0; i < point3Fs.size(); i++) {
			final Point3F point3F = point3Fs.get(i);
			
			compiledPoint3Fs[i * 3 + 0] = point3F.x;
			compiledPoint3Fs[i * 3 + 1] = point3F.y;
			compiledPoint3Fs[i * 3 + 2] = point3F.z;
		}
		
		doReportProgress(" Done.\n");
		
		return compiledPoint3Fs.length > 0 ? compiledPoint3Fs : new float[1];
	}
	
	private static float[] doCompileSphere(final Sphere sphere, final Map<Point3F, Integer> point3FMappings) {
		return new float[] {
			doGetPoint3FOffset(sphere.getPosition(), point3FMappings),
			sphere.getRadius()
		};
	}
	
	private static float[] doCompileSpheres(final List<Sphere> spheres, final Map<Point3F, Integer> point3FMappings) {
		doReportProgress("Compiling Spheres...");
		
		final float[] compiledSpheres = Arrays2.toFloatArray(spheres, sphere -> doCompileSphere(sphere, point3FMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledSpheres.length > 0 ? compiledSpheres : new float[1];
	}
	
	private static float[] doCompileSurface(final Surface surface, final Map<Texture, Integer> textureMappings) {
		return new float[] {
			surface.getMaterial().ordinal(),
			doGetTextureOffset(surface.getTextureAlbedo(), textureMappings),
			doGetTextureOffset(surface.getTextureEmission(), textureMappings),
			doGetTextureOffset(surface.getTextureNormal(), textureMappings),
			surface.getNoiseAmount(),
			surface.getNoiseScale()
		};
	}
	
	private static float[] doCompileSurfaces(final List<Surface> surfaces, final Map<Texture, Integer> textureMappings) {
		doReportProgress("Compiling Surfaces...");
		
		final float[] compiledSurfaces = Arrays2.toFloatArray(surfaces, surface -> doCompileSurface(surface, textureMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledSurfaces.length > 0 ? compiledSurfaces : new float[1];
	}
	
	private static float[] doCompileTerrain(final Terrain terrain) {
		return new float[] {
			terrain.getFrequency(),
			terrain.getGain(),
			terrain.getMinimum(),
			terrain.getMaximum(),
			terrain.getOctaves()
		};
	}
	
	private static float[] doCompileTerrains(final List<Terrain> terrains) {
		doReportProgress("Compiling Terrains...");
		
		final float[] compiledTerrains = Arrays2.toFloatArray(terrains, terrain -> doCompileTerrain(terrain));
		
		doReportProgress(" Done.\n");
		
		return compiledTerrains.length > 0 ? compiledTerrains : new float[1];
	}
	
	private static float[] doCompileTextures(final List<Texture> textures) {
		doReportProgress("Compiling Textures...");
		
		final float[] compiledTextures = Arrays2.toFloatArray(textures, texture -> texture.toArray());
		
		doReportProgress(" Done.\n");
		
		return compiledTextures.length > 0 ? compiledTextures : new float[1];
	}
	
	private static float[] doCompileVector3Fs(final List<Vector3F> vector3Fs) {
		doReportProgress("Compiling Vector3Fs...");
		
		final float[] compiledVector3Fs = new float[vector3Fs.size() * 3];
		
		for(int i = 0; i < vector3Fs.size(); i++) {
			final Vector3F vector3F = vector3Fs.get(i);
			
			compiledVector3Fs[i * 3 + 0] = vector3F.x;
			compiledVector3Fs[i * 3 + 1] = vector3F.y;
			compiledVector3Fs[i * 3 + 2] = vector3F.z;
		}
		
		doReportProgress(" Done.\n");
		
		return compiledVector3Fs.length > 0 ? compiledVector3Fs : new float[1];
	}
	
	private static int doGetBoundingVolumeHierarchyRootNodeOffset(final Mesh mesh, final List<Mesh> meshes, final List<Node> boundingVolumeHierarchyRootNodes) {
		for(int i = 0, j = 0; i < meshes.size(); i++) {
			final Mesh currentMesh = meshes.get(i);
			
			final Node currentBoundingVolumeHierarchyRootNode = boundingVolumeHierarchyRootNodes.get(i);
			
			if(mesh.equals(currentMesh)) {
				return j;
			}
			
			j += currentBoundingVolumeHierarchyRootNode.getSize();
		}
		
		throw new IllegalArgumentException(String.format("No such Mesh found: %s", mesh));
	}
	
	private static int doGetPlaneOffset(final Plane plane, final Map<Plane, Integer> planeMappings) {
		return planeMappings.get(plane).intValue();
	}
	
	private static int doGetPoint2FOffset(final Point2F point2F, final Map<Point2F, Integer> point2FMappings) {
		return point2FMappings.get(point2F).intValue();
	}
	
	private static int doGetPoint3FOffset(final Point3F point3F, final Map<Point3F, Integer> point3FMappings) {
		return point3FMappings.get(point3F).intValue();
	}
	
	private static int doGetShapeOffset(final Shape shape, final List<Mesh> meshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		if(shape instanceof Mesh) {
			return doGetBoundingVolumeHierarchyRootNodeOffset(Mesh.class.cast(shape), meshes, boundingVolumeHierarchyRootNodes);
		} else if(shape instanceof Plane) {
			return doGetPlaneOffset(Plane.class.cast(shape), planeMappings);
		} else if(shape instanceof Sphere) {
			return doGetSphereOffset(Sphere.class.cast(shape), sphereMappings);
		} else if(shape instanceof Terrain) {
			return doGetTerrainOffset(Terrain.class.cast(shape), terrainMappings);
		} else if(shape instanceof Triangle) {
			return doGetTriangleOffset(Triangle.class.cast(shape), triangleMappings);
		} else {
			throw new IllegalArgumentException(String.format("No such Shape found: %s", shape));
		}
	}
	
	private static int doGetSphereOffset(final Sphere sphere, final Map<Sphere, Integer> sphereMappings) {
		return sphereMappings.get(sphere).intValue();
	}
	
	private static int doGetSurfaceOffset(final Surface surface, final Map<Surface, Integer> surfaceMappings) {
		return surfaceMappings.get(surface).intValue();
	}
	
	private static int doGetTerrainOffset(final Terrain terrain, final Map<Terrain, Integer> terrainMappings) {
		return terrainMappings.get(terrain).intValue();
	}
	
	private static int doGetTextureOffset(final Texture texture, final Map<Texture, Integer> textureMappings) {
		return textureMappings.get(texture).intValue();
	}
	
	private static int doGetTriangleOffset(final Triangle triangle, final Map<Triangle, Integer> triangleMappings) {
		return triangleMappings.get(triangle).intValue();
	}
	
	private static int doGetVector3FOffset(final Vector3F vector3F, final Map<Vector3F, Integer> vector3FMappings) {
		return vector3FMappings.get(vector3F).intValue();
	}
	
	private static int[] doCompileBoundingVolumeHierarchies(final List<Node> boundingVolumeHierarchyRootNodes, final Map<Point3F, Integer> point3FMappings, final Map<Triangle, Integer> triangleMappings) {
		doReportProgress("Compiling Bounding Volume Hierarchies...");
		
		final int[] compiledBoundingVolumeHierarchies = Arrays2.toIntArray(boundingVolumeHierarchyRootNodes, mesh -> doCompileBoundingVolumeHierarchy(mesh, point3FMappings, triangleMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledBoundingVolumeHierarchies.length > 0 ? compiledBoundingVolumeHierarchies : new int[1];
	}
	
	private static int[] doCompileBoundingVolumeHierarchy(final Node boundingVolumeHierarchyRootNode, final Map<Point3F, Integer> point3FMappings, final Map<Triangle, Integer> triangleMappings) {
		final int size = boundingVolumeHierarchyRootNode.getSize();
		
		final int[] boundingVolumeHierarchy = new int[size];
		
		final List<Node> nodes = boundingVolumeHierarchyRootNode.toList();
		
		final int[] offsets = new int[nodes.size()];
		
		for(int i = 0, j = 0; i < nodes.size(); i++) {
			offsets[i] = j;
			
			final Node node = nodes.get(i);
			
			if(node instanceof LeafNode) {
				j += 5 + LeafNode.class.cast(node).getTriangles().size();
			} else if(node instanceof TreeNode) {
				j += 5;
			}
		}
		
		for(int i = 0, j = 0; i < nodes.size(); i++) {
			final Node node = nodes.get(i);
			
			if(node instanceof LeafNode) {
				final LeafNode leafNode = LeafNode.class.cast(node);
				
				final int depth = leafNode.getDepth();
				
				int nextIndex = -1;
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node currentNode = nodes.get(k);
					
					if(currentNode.getDepth() <= depth) {
						nextIndex = offsets[k];
						
						break;
					}
				}
				
				final Point3F maximum = leafNode.getMaximum();
				final Point3F minimum = leafNode.getMinimum();
				
				final int maximumOffset = doGetPoint3FOffset(maximum, point3FMappings);
				final int minimumOffset = doGetPoint3FOffset(minimum, point3FMappings);
				
				boundingVolumeHierarchy[j + 0] = BoundingVolumeHierarchy.NODE_TYPE_LEAF;
				boundingVolumeHierarchy[j + 1] = nextIndex;
				boundingVolumeHierarchy[j + 2] = minimumOffset;
				boundingVolumeHierarchy[j + 3] = maximumOffset;
				boundingVolumeHierarchy[j + 4] = leafNode.getTriangles().size();
				
				for(int k = 0; k < leafNode.getTriangles().size(); k++) {
					boundingVolumeHierarchy[j + 5 + k] = doGetTriangleOffset(leafNode.getTriangles().get(k), triangleMappings);
				}
				
				j += 5 + leafNode.getTriangles().size();
			} else if(node instanceof TreeNode) {
				final TreeNode treeNode = TreeNode.class.cast(node);
				
				final int depth = treeNode.getDepth();
				
				int nextIndex = -1;
				int leftIndex = -1;
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node currentNode = nodes.get(k);
					
					if(currentNode.getDepth() <= depth) {
						nextIndex = offsets[k];
						
						break;
					}
				}
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node currentNode = nodes.get(k);
					
					if(currentNode.getDepth() == depth + 1) {
						leftIndex = offsets[k];
						
						break;
					}
				}
				
				final Point3F maximum = treeNode.getMaximum();
				final Point3F minimum = treeNode.getMinimum();
				
				final int maximumOffset = doGetPoint3FOffset(maximum, point3FMappings);
				final int minimumOffset = doGetPoint3FOffset(minimum, point3FMappings);
				
				boundingVolumeHierarchy[j + 0] = BoundingVolumeHierarchy.NODE_TYPE_TREE;
				boundingVolumeHierarchy[j + 1] = nextIndex;
				boundingVolumeHierarchy[j + 2] = minimumOffset;
				boundingVolumeHierarchy[j + 3] = maximumOffset;
				boundingVolumeHierarchy[j + 4] = leftIndex;
				
				j += 5;
			}
		}
		
		return boundingVolumeHierarchy;
	}
	
	private static int[] doCompilePlane(final Plane plane, final Map<Point3F, Integer> point3FMappings, final Map<Vector3F, Integer> vector3FMappings) {
		return new int[] {
			doGetPoint3FOffset(plane.getA(), point3FMappings),
			doGetPoint3FOffset(plane.getB(), point3FMappings),
			doGetPoint3FOffset(plane.getC(), point3FMappings),
			doGetVector3FOffset(plane.getSurfaceNormal(), vector3FMappings)
		};
	}
	
	private static int[] doCompilePlanes(final List<Plane> planes, final Map<Point3F, Integer> point3FMappings, final Map<Vector3F, Integer> vector3FMappings) {
		doReportProgress("Compiling Planes...");
		
		final int[] compiledPlanes = Arrays2.toIntArray(planes, plane -> doCompilePlane(plane, point3FMappings, vector3FMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledPlanes.length > 0 ? compiledPlanes : new int[1];
	}
	
	private static int[] doCompilePrimitive(final Primitive primitive, final List<Mesh> meshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Surface, Integer> surfaceMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		return new int[] {
			primitive.getShape().getType(),
			doGetShapeOffset(primitive.getShape(), meshes, boundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, terrainMappings, triangleMappings),
			doGetSurfaceOffset(primitive.getSurface(), surfaceMappings)
		};
	}
	
	private static int[] doCompilePrimitives(final List<Primitive> primitives, final List<Mesh> meshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Surface, Integer> surfaceMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		doReportProgress("Compiling Primitives...");
		
		final int[] compiledPrimitives = Arrays2.toIntArray(primitives, primitive -> doCompilePrimitive(primitive, meshes, boundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, surfaceMappings, terrainMappings, triangleMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledPrimitives.length > 0 ? compiledPrimitives : new int[1];
	}
	
	private static int[] doCompileTriangle(final Triangle triangle, final Map<Point2F, Integer> point2FMappings, final Map<Point3F, Integer> point3FMappings, final Map<Vector3F, Integer> vector3FMappings) {
		return new int[] {
			doGetPoint3FOffset(triangle.getA().getPosition(), point3FMappings),
			doGetPoint3FOffset(triangle.getB().getPosition(), point3FMappings),
			doGetPoint3FOffset(triangle.getC().getPosition(), point3FMappings),
			doGetVector3FOffset(triangle.getA().getNormal(), vector3FMappings),
			doGetVector3FOffset(triangle.getB().getNormal(), vector3FMappings),
			doGetVector3FOffset(triangle.getC().getNormal(), vector3FMappings),
			doGetPoint2FOffset(triangle.getA().getTextureCoordinates(), point2FMappings),
			doGetPoint2FOffset(triangle.getB().getTextureCoordinates(), point2FMappings),
			doGetPoint2FOffset(triangle.getC().getTextureCoordinates(), point2FMappings)
		};
	}
	
	private static int[] doCompileTriangles(final List<Triangle> triangles, final Map<Point2F, Integer> point2FMappings, final Map<Point3F, Integer> point3FMappings, final Map<Vector3F, Integer> vector3FMappings) {
		doReportProgress("Compiling Triangles...");
		
		final int[] compiledTriangles = Arrays2.toIntArray(triangles, triangle -> doCompileTriangle(triangle, point2FMappings, point3FMappings, vector3FMappings));
		
		doReportProgress(" Done.\n");
		
		return compiledTriangles.length > 0 ? compiledTriangles : new int[1];
	}
	
	private static void doReportProgress(final String message) {
		System.out.print(message);
	}
}