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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.TriangleMesh;
import org.dayflower.pathtracer.util.Arrays2;

/**
 * A class that compiles {@link Scene}s so they can be used by Dayflower - Path Tracer.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SceneCompiler {
	private final List<SceneCompilerObserver> sceneCompilerObservers;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code SceneCompiler} instance.
	 */
	public SceneCompiler() {
		this.sceneCompilerObservers = new ArrayList<>();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compiles {@code scene} into a {@link CompiledScene} instance.
	 * <p>
	 * Returns a {@code CompiledScene} instance.
	 * <p>
	 * If {@code scene} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param scene the {@code Scene} to compile
	 * @return a {@code CompiledScene} instance
	 * @throws NullPointerException thrown if, and only if, {@code scene} is {@code null}
	 */
	public CompiledScene compile(final Scene scene) {
		final long currentTimeMillis = System.currentTimeMillis();
		
		doOnCompilationStart(scene, System.currentTimeMillis() - currentTimeMillis);
		
//		Retrieve all Primitives:
		final List<Primitive> allPrimitives = doFindAllPrimitives(scene);
		
//		Retrieve all Shapes:
		final List<Plane> allPlanes = doFindAllPlanes(allPrimitives);
		final List<Sphere> allSpheres = doFindAllSpheres(allPrimitives);
		final List<Terrain> allTerrains = doFindAllTerrains(allPrimitives);
		final List<Triangle> allTriangles = doFindAllTriangles(allPrimitives);
		final List<TriangleMesh> allTriangleMeshes = doFindAllTriangleMeshes(allPrimitives);
		
//		Retrieve all BoundingVolumeHierarchy root-Nodes:
		final List<Node> allBoundingVolumeHierarchyRootNodes = doFindAllBoundingVolumeHierarchyRootNodes(allTriangleMeshes);
		
//		Retrieve all Surfaces:
		final List<Surface> allSurfaces = doFindAllSurfaces(allPrimitives);
		
//		Retrieve all Textures:
		final List<Texture> allTextures = doFindAllTextures(allSurfaces);
		
//		Retrieve all Point2Fs, Point3Fs and Vector3Fs:
		final List<Point2F> allPoint2Fs = doFindAllPoint2Fs(allPrimitives);
		final List<Point3F> allPoint3Fs = doFindAllPoint3Fs(allBoundingVolumeHierarchyRootNodes, allPrimitives);
		final List<Vector3F> allVector3Fs = doFindAllVector3Fs(allPrimitives);
		
//		Retrieve all unique Primitives:
		final List<Primitive> uniquePrimitives = doFindUniquePrimitives(allPrimitives);
		final List<Primitive> uniquePrimitivesEmittingLight = doFindPrimitivesEmittingLight(uniquePrimitives);
		
//		Retrieve all unique Shapes:
		final List<Plane> uniquePlanes = doFindUniquePlanes(allPlanes);
		final List<Sphere> uniqueSpheres = doFindUniqueSpheres(allSpheres);
		final List<Terrain> uniqueTerrains = doFindUniqueTerrains(allTerrains);
		final List<Triangle> uniqueTriangles = doFindUniqueTriangles(allTriangles);
		final List<TriangleMesh> uniqueTriangleMeshes = doFindUniqueTriangleMeshes(allTriangleMeshes);
		
//		Retrieve all unique BoundingVolumeHierarchy root-Nodes:
		final List<Node> uniqueBoundingVolumeHierarchyRootNodes = doFindUniqueBoundingVolumeHierarchyRootNodes(allBoundingVolumeHierarchyRootNodes);
		
//		Retrieve all unique Surfaces:
		final List<Surface> uniqueSurfaces = doFindUniqueSurfaces(allSurfaces);
		
//		Retrieve all unique Textures:
		final List<Texture> uniqueTextures = doFindUniqueTextures(allTextures);
		
//		Retrieve all unique Point2Fs, Point3Fs and Vector3Fs:
		final List<Point2F> uniquePoint2Fs = doFindUniquePoint2Fs(allPoint2Fs);
		final List<Point3F> uniquePoint3Fs = doFindUniquePoint3Fs(allPoint3Fs);
		final List<Vector3F> uniqueVector3Fs = doFindUniqueVector3Fs(allVector3Fs);
		
//		Notify all SceneCompilerObservers of all vs. unique structures:
		doOnComparisonPrimitive(scene, System.currentTimeMillis() - currentTimeMillis, allPrimitives.size(), uniquePrimitives.size());
		doOnComparisonPlane(scene, System.currentTimeMillis() - currentTimeMillis, allPlanes.size(), uniquePlanes.size());
		doOnComparisonSphere(scene, System.currentTimeMillis() - currentTimeMillis, allSpheres.size(), uniqueSpheres.size());
		doOnComparisonTerrain(scene, System.currentTimeMillis() - currentTimeMillis, allTerrains.size(), uniqueTerrains.size());
		doOnComparisonTriangle(scene, System.currentTimeMillis() - currentTimeMillis, allTriangles.size(), uniqueTriangles.size());
		doOnComparisonTriangleMesh(scene, System.currentTimeMillis() - currentTimeMillis, allTriangleMeshes.size(), uniqueTriangleMeshes.size());
		doOnComparisonBoundingVolumeHierarchyRootNode(scene, System.currentTimeMillis() - currentTimeMillis, allBoundingVolumeHierarchyRootNodes.size(), uniqueBoundingVolumeHierarchyRootNodes.size());
		doOnComparisonSurface(scene, System.currentTimeMillis() - currentTimeMillis, allSurfaces.size(), uniqueSurfaces.size());
		doOnComparisonTexture(scene, System.currentTimeMillis() - currentTimeMillis, allTextures.size(), uniqueTextures.size());
		doOnComparisonPoint2F(scene, System.currentTimeMillis() - currentTimeMillis, allPoint2Fs.size(), uniquePoint2Fs.size());
		doOnComparisonPoint3F(scene, System.currentTimeMillis() - currentTimeMillis, allPoint3Fs.size(), uniquePoint3Fs.size());
		doOnComparisonVector3F(scene, System.currentTimeMillis() - currentTimeMillis, allVector3Fs.size(), uniqueVector3Fs.size());
		
//		Create mappings from Shapes to Integer indices:
		final Map<Plane, Integer> planeMappings = doCreatePlaneMappings(uniquePlanes);
		final Map<Sphere, Integer> sphereMappings = doCreateSphereMappings(uniqueSpheres);
		final Map<Terrain, Integer> terrainMappings = doCreateTerrainMappings(uniqueTerrains);
		final Map<Triangle, Integer> triangleMappings = doCreateTriangleMappings(uniqueTriangles);
		
//		Create mappings from Surfaces to Integer indices:
		final Map<Surface, Integer> surfaceMappings = doCreateSurfaceMappings(uniqueSurfaces);
		
//		Create mappings from Textures to Integer indices:
		final Map<Texture, Integer> textureMappings = doCreateTextureMappings(uniqueTextures);
		
//		Create mappings from Primitives to Integer indices:
		final Map<Primitive, Integer> primitiveMappings = doCreatePrimitiveMappings(uniquePrimitives);
		
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
		final int[] primitives = doCompilePrimitives(uniquePrimitives, uniqueTriangleMeshes, uniqueBoundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, surfaceMappings, terrainMappings, triangleMappings);
		final int[] primitivesEmittingLight = doCompilePrimitivesEmittingLight(uniquePrimitivesEmittingLight, primitiveMappings);
		final int[] triangles = doCompileTriangles(uniqueTriangles, point2FMappings, point3FMappings, vector3FMappings);
		
		doOnCompilationEnd(scene, System.currentTimeMillis() - currentTimeMillis);
		
		return new CompiledScene(scene.getName(), camera, point2Fs, point3Fs, spheres, surfaces, terrains, textures, vector3Fs, boundingVolumeHierarchies, planes, primitives, primitivesEmittingLight, triangles);
	}
	
	/**
	 * Adds {@code sceneCompilerObserver} to this {@code SceneCompiler} instance.
	 * <p>
	 * If {@code sceneCompilerObserver} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sceneCompilerObserver the {@link SceneCompilerObserver} to add
	 * @throws NullPointerException thrown if, and only if, {@code sceneCompilerObserver} is {@code null}
	 */
	public void addSceneCompilerObserver(final SceneCompilerObserver sceneCompilerObserver) {
		this.sceneCompilerObservers.add(Objects.requireNonNull(sceneCompilerObserver, "sceneCompilerObserver == null"));
	}
	
	/**
	 * Removes {@code sceneCompilerObserver} from this {@code SceneCompiler} instance.
	 * <p>
	 * If {@code sceneCompilerObserver} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sceneCompilerObserver the {@link SceneCompilerObserver} to remove
	 * @throws NullPointerException thrown if, and only if, {@code sceneCompilerObserver} is {@code null}
	 */
	public void removeSceneCompilerObserver(final SceneCompilerObserver sceneCompilerObserver) {
		this.sceneCompilerObservers.remove(Objects.requireNonNull(sceneCompilerObserver, "sceneCompilerObserver == null"));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doOnComparisonBoundingVolumeHierarchyRootNode(final Scene scene, final long milliseconds, final int boundingVolumeHierarchyRootNodeCountAll, final int boundingVolumeHierarchyRootNodeCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonBoundingVolumeHierarchyRootNode(scene, milliseconds, boundingVolumeHierarchyRootNodeCountAll, boundingVolumeHierarchyRootNodeCountUnique));
	}
	
	private void doOnComparisonPlane(final Scene scene, final long milliseconds, final int planeCountAll, final int planeCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonPlane(scene, milliseconds, planeCountAll, planeCountUnique));
	}
	
	private void doOnComparisonPoint2F(final Scene scene, final long milliseconds, final int point2FCountAll, final int point2FCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonPoint2F(scene, milliseconds, point2FCountAll, point2FCountUnique));
	}
	
	private void doOnComparisonPoint3F(final Scene scene, final long milliseconds, final int point3FCountAll, final int point3FCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonPoint3F(scene, milliseconds, point3FCountAll, point3FCountUnique));
	}
	
	private void doOnComparisonPrimitive(final Scene scene, final long milliseconds, final int primitiveCountAll, final int primitiveCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonPrimitive(scene, milliseconds, primitiveCountAll, primitiveCountUnique));
	}
	
	private void doOnComparisonSphere(final Scene scene, final long milliseconds, final int sphereCountAll, final int sphereCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonSphere(scene, milliseconds, sphereCountAll, sphereCountUnique));
	}
	
	private void doOnComparisonSurface(final Scene scene, final long milliseconds, final int surfaceCountAll, final int surfaceCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonSurface(scene, milliseconds, surfaceCountAll, surfaceCountUnique));
	}
	
	private void doOnComparisonTerrain(final Scene scene, final long milliseconds, final int terrainCountAll, final int terrainCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonTerrain(scene, milliseconds, terrainCountAll, terrainCountUnique));
	}
	
	private void doOnComparisonTexture(final Scene scene, final long milliseconds, final int textureCountAll, final int textureCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonTexture(scene, milliseconds, textureCountAll, textureCountUnique));
	}
	
	private void doOnComparisonTriangle(final Scene scene, final long milliseconds, final int triangleCountAll, final int triangleCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonTriangle(scene, milliseconds, triangleCountAll, triangleCountUnique));
	}
	
	private void doOnComparisonTriangleMesh(final Scene scene, final long milliseconds, final int triangleMeshCountAll, final int triangleMeshCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonTriangleMesh(scene, milliseconds, triangleMeshCountAll, triangleMeshCountUnique));
	}
	
	private void doOnComparisonVector3F(final Scene scene, final long milliseconds, final int vector3FCountAll, final int vector3FCountUnique) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onComparisonVector3F(scene, milliseconds, vector3FCountAll, vector3FCountUnique));
	}
	
	private void doOnCompilationEnd(final Scene scene, final long milliseconds) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onCompilationEnd(scene, milliseconds));
	}
	
	private void doOnCompilationStart(final Scene scene, final long milliseconds) {
		this.sceneCompilerObservers.forEach(sceneCompilerObserver -> sceneCompilerObserver.onCompilationStart(scene, milliseconds));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static List<Node> doFindAllBoundingVolumeHierarchyRootNodes(final List<TriangleMesh> triangleMeshes) {
		return triangleMeshes.stream().map(triangleMesh -> BoundingVolumeHierarchy.createBoundingVolumeHierarchy(triangleMesh.getTriangles()).getRoot()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Plane> doFindAllPlanes(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Plane).map(primitive -> Plane.class.cast(primitive.getShape())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Point2F> doFindAllPoint2Fs(final List<Primitive> primitives) {
		final List<Point2F> allPoint2Fs = new ArrayList<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point2F a = triangle.getA().getTextureCoordinates();
				final Point2F b = triangle.getB().getTextureCoordinates();
				final Point2F c = triangle.getC().getTextureCoordinates();
				
				allPoint2Fs.add(a);
				allPoint2Fs.add(b);
				allPoint2Fs.add(c);
			} else if(shape instanceof TriangleMesh) {
				final TriangleMesh triangleMesh = TriangleMesh.class.cast(shape);
				
				for(final Triangle triangle : triangleMesh.getTriangles()) {
					final Point2F a = triangle.getA().getTextureCoordinates();
					final Point2F b = triangle.getB().getTextureCoordinates();
					final Point2F c = triangle.getC().getTextureCoordinates();
					
					allPoint2Fs.add(a);
					allPoint2Fs.add(b);
					allPoint2Fs.add(c);
				}
			}
		}
		
		return allPoint2Fs;
	}
	
	private static List<Point3F> doFindAllPoint3Fs(final List<Node> uniqueBoundingVolumeHierarchyRootNodes, final List<Primitive> primitives) {
		final List<Point3F> allPoint3Fs = new ArrayList<>();
		
		for(final Node uniqueBoundingVolumeHierarchyRootNode : uniqueBoundingVolumeHierarchyRootNodes) {
			uniqueBoundingVolumeHierarchyRootNode.addBounds(allPoint3Fs);
		}
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Point3F a = plane.getA();
				final Point3F b = plane.getB();
				final Point3F c = plane.getC();
				
				allPoint3Fs.add(a);
				allPoint3Fs.add(b);
				allPoint3Fs.add(c);
			} else if(shape instanceof Sphere) {
				final Sphere sphere = Sphere.class.cast(shape);
				
				final Point3F position = sphere.getPosition();
				
				allPoint3Fs.add(position);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Point3F a = triangle.getA().getPosition();
				final Point3F b = triangle.getB().getPosition();
				final Point3F c = triangle.getC().getPosition();
				
				allPoint3Fs.add(a);
				allPoint3Fs.add(b);
				allPoint3Fs.add(c);
			} else if(shape instanceof TriangleMesh) {
				final TriangleMesh triangleMesh = TriangleMesh.class.cast(shape);
				
				for(final Triangle triangle : triangleMesh.getTriangles()) {
					final Point3F a = triangle.getA().getPosition();
					final Point3F b = triangle.getB().getPosition();
					final Point3F c = triangle.getC().getPosition();
					
					allPoint3Fs.add(a);
					allPoint3Fs.add(b);
					allPoint3Fs.add(c);
				}
			}
		}
		
		return allPoint3Fs;
	}
	
	private static List<Primitive> doFindAllPrimitives(final Scene scene) {
		return scene.getPrimitives().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Sphere> doFindAllSpheres(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Sphere).map(primitive -> Sphere.class.cast(primitive.getShape())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Surface> doFindAllSurfaces(final List<Primitive> primitives) {
		return primitives.stream().map(primitive -> primitive.getSurface()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Terrain> doFindAllTerrains(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof Terrain).map(primitive -> Terrain.class.cast(primitive.getShape())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Texture> doFindAllTextures(final List<Surface> surfaces) {
		final List<Texture> allTextures = new ArrayList<>();
		
		for(final Surface surface : surfaces) {
			final Texture textureAlbedo = surface.getTextureAlbedo();
			final Texture textureEmission = surface.getTextureEmission();
			final Texture textureNormal = surface.getTextureNormal();
			
			allTextures.add(textureAlbedo);
			allTextures.add(textureEmission);
			allTextures.add(textureNormal);
		}
		
		return allTextures;
	}
	
	private static List<Triangle> doFindAllTriangles(final List<Primitive> primitives) {
		final List<Triangle> allTriangles = new ArrayList<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Triangle) {
				allTriangles.add(Triangle.class.cast(shape));
			} else if(shape instanceof TriangleMesh) {
				final TriangleMesh triangleMesh = TriangleMesh.class.cast(shape);
				
				for(final Triangle triangle : triangleMesh.getTriangles()) {
					allTriangles.add(triangle);
				}
			}
		}
		
		return allTriangles;
	}
	
	private static List<TriangleMesh> doFindAllTriangleMeshes(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getShape() instanceof TriangleMesh).map(primitive -> TriangleMesh.class.cast(primitive.getShape())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Vector3F> doFindAllVector3Fs(final List<Primitive> primitives) {
		final List<Vector3F> allVector3Fs = new ArrayList<>();
		
		for(final Primitive primitive : primitives) {
			final Shape shape = primitive.getShape();
			
			if(shape instanceof Plane) {
				final Plane plane = Plane.class.cast(shape);
				
				final Vector3F surfaceNormal = plane.getSurfaceNormal();
				
				allVector3Fs.add(surfaceNormal);
			} else if(shape instanceof Triangle) {
				final Triangle triangle = Triangle.class.cast(shape);
				
				final Vector3F a = triangle.getA().getNormal();
				final Vector3F b = triangle.getB().getNormal();
				final Vector3F c = triangle.getC().getNormal();
				
				allVector3Fs.add(a);
				allVector3Fs.add(b);
				allVector3Fs.add(c);
			} else if(shape instanceof TriangleMesh) {
				final TriangleMesh triangleMesh = TriangleMesh.class.cast(shape);
				
				for(final Triangle triangle : triangleMesh.getTriangles()) {
					final Vector3F a = triangle.getA().getNormal();
					final Vector3F b = triangle.getB().getNormal();
					final Vector3F c = triangle.getC().getNormal();
					
					allVector3Fs.add(a);
					allVector3Fs.add(b);
					allVector3Fs.add(c);
				}
			}
		}
		
		return allVector3Fs;
	}
	
	private static List<Primitive> doFindPrimitivesEmittingLight(final List<Primitive> primitives) {
		return primitives.stream().filter(primitive -> primitive.getSurface().getTextureEmission().isEmissive()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Node> doFindUniqueBoundingVolumeHierarchyRootNodes(final List<Node> boundingVolumeHierarchyRootNodes) {
		return boundingVolumeHierarchyRootNodes.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Plane> doFindUniquePlanes(final List<Plane> planes) {
		return planes.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Point2F> doFindUniquePoint2Fs(final List<Point2F> point2Fs) {
		return point2Fs.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Point3F> doFindUniquePoint3Fs(final List<Point3F> point3Fs) {
		return point3Fs.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Primitive> doFindUniquePrimitives(final List<Primitive> primitives) {
		return primitives.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Sphere> doFindUniqueSpheres(final List<Sphere> spheres) {
		return spheres.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Surface> doFindUniqueSurfaces(final List<Surface> surfaces) {
		return surfaces.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Terrain> doFindUniqueTerrains(final List<Terrain> terrains) {
		return terrains.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Texture> doFindUniqueTextures(final List<Texture> textures) {
		return textures.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Triangle> doFindUniqueTriangles(final List<Triangle> triangles) {
		return triangles.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<TriangleMesh> doFindUniqueTriangleMeshes(final List<TriangleMesh> triangleMeshes) {
		return triangleMeshes.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	private static List<Vector3F> doFindUniqueVector3Fs(final List<Vector3F> vector3Fs) {
		return vector3Fs.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
	
	private static Map<Primitive, Integer> doCreatePrimitiveMappings(final List<Primitive> primitives) {
		final Map<Primitive, Integer> primitiveMappings = new HashMap<>();
		
		for(int i = 0; i < primitives.size(); i++) {
			primitiveMappings.put(primitives.get(i), Integer.valueOf(i * Primitive.SIZE));
		}
		
		return primitiveMappings;
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
		final float[] compiledPoint2Fs = new float[point2Fs.size() * 2];
		
		for(int i = 0; i < point2Fs.size(); i++) {
			final Point2F point2F = point2Fs.get(i);
			
			compiledPoint2Fs[i * 2 + 0] = point2F.x;
			compiledPoint2Fs[i * 2 + 1] = point2F.y;
		}
		
		return compiledPoint2Fs.length > 0 ? compiledPoint2Fs : new float[1];
	}
	
	private static float[] doCompilePoint3Fs(final List<Point3F> point3Fs) {
		final float[] compiledPoint3Fs = new float[point3Fs.size() * 3];
		
		for(int i = 0; i < point3Fs.size(); i++) {
			final Point3F point3F = point3Fs.get(i);
			
			compiledPoint3Fs[i * 3 + 0] = point3F.x;
			compiledPoint3Fs[i * 3 + 1] = point3F.y;
			compiledPoint3Fs[i * 3 + 2] = point3F.z;
		}
		
		return compiledPoint3Fs.length > 0 ? compiledPoint3Fs : new float[1];
	}
	
	private static float[] doCompileSphere(final Sphere sphere, final Map<Point3F, Integer> point3FMappings) {
		return new float[] {
			doGetPoint3FOffset(sphere.getPosition(), point3FMappings),
			sphere.getRadius()
		};
	}
	
	private static float[] doCompileSpheres(final List<Sphere> spheres, final Map<Point3F, Integer> point3FMappings) {
		return Arrays2.toFloatArray(spheres, sphere -> doCompileSphere(sphere, point3FMappings), 1);
	}
	
	private static float[] doCompileSurface(final Surface surface, final Map<Texture, Integer> textureMappings) {
		return new float[] {
			surface.getMaterial().getType(),
			doGetTextureOffset(surface.getTextureAlbedo(), textureMappings),
			doGetTextureOffset(surface.getTextureEmission(), textureMappings),
			doGetTextureOffset(surface.getTextureNormal(), textureMappings),
			surface.getNoiseAmount(),
			surface.getNoiseScale()
		};
	}
	
	private static float[] doCompileSurfaces(final List<Surface> surfaces, final Map<Texture, Integer> textureMappings) {
		return Arrays2.toFloatArray(surfaces, surface -> doCompileSurface(surface, textureMappings), 1);
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
		return Arrays2.toFloatArray(terrains, terrain -> doCompileTerrain(terrain), 1);
	}
	
	private static float[] doCompileTextures(final List<Texture> textures) {
		return Arrays2.toFloatArray(textures, texture -> texture.toArray(), 1);
	}
	
	private static float[] doCompileVector3Fs(final List<Vector3F> vector3Fs) {
		final float[] compiledVector3Fs = new float[vector3Fs.size() * 3];
		
		for(int i = 0; i < vector3Fs.size(); i++) {
			final Vector3F vector3F = vector3Fs.get(i);
			
			compiledVector3Fs[i * 3 + 0] = vector3F.x;
			compiledVector3Fs[i * 3 + 1] = vector3F.y;
			compiledVector3Fs[i * 3 + 2] = vector3F.z;
		}
		
		return compiledVector3Fs.length > 0 ? compiledVector3Fs : new float[1];
	}
	
	private static int doGetBoundingVolumeHierarchyRootNodeOffset(final TriangleMesh triangleMesh, final List<TriangleMesh> triangleMeshes, final List<Node> boundingVolumeHierarchyRootNodes) {
		for(int i = 0, j = 0; i < triangleMeshes.size(); i++) {
			final TriangleMesh currentTriangleMesh = triangleMeshes.get(i);
			
			final Node currentBoundingVolumeHierarchyRootNode = boundingVolumeHierarchyRootNodes.get(i);
			
			if(triangleMesh.equals(currentTriangleMesh)) {
				return j;
			}
			
			j += currentBoundingVolumeHierarchyRootNode.getSize();
		}
		
		throw new IllegalArgumentException(String.format("No such TriangleMesh found: %s", triangleMesh));
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
	
	private static int doGetShapeOffset(final Shape shape, final List<TriangleMesh> triangleMeshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		if(shape instanceof Plane) {
			return doGetPlaneOffset(Plane.class.cast(shape), planeMappings);
		} else if(shape instanceof Sphere) {
			return doGetSphereOffset(Sphere.class.cast(shape), sphereMappings);
		} else if(shape instanceof Terrain) {
			return doGetTerrainOffset(Terrain.class.cast(shape), terrainMappings);
		} else if(shape instanceof Triangle) {
			return doGetTriangleOffset(Triangle.class.cast(shape), triangleMappings);
		} else if(shape instanceof TriangleMesh) {
			return doGetBoundingVolumeHierarchyRootNodeOffset(TriangleMesh.class.cast(shape), triangleMeshes, boundingVolumeHierarchyRootNodes);
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
		return Arrays2.toIntArray(boundingVolumeHierarchyRootNodes, mesh -> doCompileBoundingVolumeHierarchy(mesh, point3FMappings, triangleMappings), 1);
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
		return Arrays2.toIntArray(planes, plane -> doCompilePlane(plane, point3FMappings, vector3FMappings), 1);
	}
	
	private static int[] doCompilePrimitive(final Primitive primitive, final List<TriangleMesh> triangleMeshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Surface, Integer> surfaceMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		return new int[] {
			primitive.getShape().getType(),
			doGetShapeOffset(primitive.getShape(), triangleMeshes, boundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, terrainMappings, triangleMappings),
			doGetSurfaceOffset(primitive.getSurface(), surfaceMappings)
		};
	}
	
	private static int[] doCompilePrimitives(final List<Primitive> primitives, final List<TriangleMesh> triangleMeshes, final List<Node> boundingVolumeHierarchyRootNodes, final Map<Plane, Integer> planeMappings, final Map<Sphere, Integer> sphereMappings, final Map<Surface, Integer> surfaceMappings, final Map<Terrain, Integer> terrainMappings, final Map<Triangle, Integer> triangleMappings) {
		return Arrays2.toIntArray(primitives, primitive -> doCompilePrimitive(primitive, triangleMeshes, boundingVolumeHierarchyRootNodes, planeMappings, sphereMappings, surfaceMappings, terrainMappings, triangleMappings), 1);
	}
	
	private static int[] doCompilePrimitivesEmittingLight(final List<Primitive> primitivesEmittingLight, final Map<Primitive, Integer> primitiveMappings) {
		final int[] compiledPrimitivesEmittingLight = new int[primitivesEmittingLight.size() + 1];
		
		compiledPrimitivesEmittingLight[0] = primitivesEmittingLight.size();
		
		for(int i = 0; i < primitivesEmittingLight.size(); i++) {
			compiledPrimitivesEmittingLight[i + 1] = primitiveMappings.get(primitivesEmittingLight.get(i)).intValue();
		}
		
		return compiledPrimitivesEmittingLight;
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
		return Arrays2.toIntArray(triangles, triangle -> doCompileTriangle(triangle, point2FMappings, point3FMappings, vector3FMappings), 1);
	}
}