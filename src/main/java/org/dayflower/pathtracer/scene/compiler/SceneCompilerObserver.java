/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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

import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.Node;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.TriangleMesh;
import org.macroing.math4j.Point2F;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.Vector3F;

/**
 * A {@code SceneCompilerObserver} can be added to a {@link SceneCompiler} to observe the compilation process.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface SceneCompilerObserver {
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link BoundingVolumeHierarchy} root {@link Node}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param boundingVolumeHierarchyRootNodeCountAll the {@code BoundingVolumeHierarchy} root {@code Node} count
	 * @param boundingVolumeHierarchyRootNodeCountUnique the unique {@code BoundingVolumeHierarchy} root {@code Node} count
	 */
	void onComparisonBoundingVolumeHierarchyRootNode(final Scene scene, final long milliseconds, final int boundingVolumeHierarchyRootNodeCountAll, final int boundingVolumeHierarchyRootNodeCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Plane}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param planeCountAll the {@code Plane} count
	 * @param planeCountUnique the unique {@code Plane} count
	 */
	void onComparisonPlane(final Scene scene, final long milliseconds, final int planeCountAll, final int planeCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Point2F}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param point2FCountAll the {@code Point2F} count
	 * @param point2FCountUnique the unique {@code Point2F} count
	 */
	void onComparisonPoint2F(final Scene scene, final long milliseconds, final int point2FCountAll, final int point2FCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Point3F}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param point3FCountAll the {@code Point3F} count
	 * @param point3FCountUnique the unique {@code Point3F} count
	 */
	void onComparisonPoint3F(final Scene scene, final long milliseconds, final int point3FCountAll, final int point3FCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Primitive}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param primitiveCountAll the {@code Primitive} count
	 * @param primitiveCountUnique the unique {@code Primitive} count
	 */
	void onComparisonPrimitive(final Scene scene, final long milliseconds, final int primitiveCountAll, final int primitiveCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Sphere}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param sphereCountAll the {@code Sphere} count
	 * @param sphereCountUnique the unique {@code Sphere} count
	 */
	void onComparisonSphere(final Scene scene, final long milliseconds, final int sphereCountAll, final int sphereCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Surface}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param surfaceCountAll the {@code Surface} count
	 * @param surfaceCountUnique the unique {@code Surface} count
	 */
	void onComparisonSurface(final Scene scene, final long milliseconds, final int surfaceCountAll, final int surfaceCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Terrain}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param terrainCountAll the {@code Terrain} count
	 * @param terrainCountUnique the unique {@code Terrain} count
	 */
	void onComparisonTerrain(final Scene scene, final long milliseconds, final int terrainCountAll, final int terrainCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Texture}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param textureCountAll the {@code Texture} count
	 * @param textureCountUnique the unique {@code Texture} count
	 */
	void onComparisonTexture(final Scene scene, final long milliseconds, final int textureCountAll, final int textureCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Triangle}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param triangleCountAll the {@code Triangle} count
	 * @param triangleCountUnique the unique {@code Triangle} count
	 */
	void onComparisonTriangle(final Scene scene, final long milliseconds, final int triangleCountAll, final int triangleCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link TriangleMesh}es.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param triangleMeshCountAll the {@code TriangleMesh} count
	 * @param triangleMeshCountUnique the unique {@code TriangleMesh} count
	 */
	void onComparisonTriangleMesh(final Scene scene, final long milliseconds, final int triangleMeshCountAll, final int triangleMeshCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report statistics about the {@link Vector3F}s.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 * @param vector3FCountAll the {@code Vector3F} count
	 * @param vector3FCountUnique the unique {@code Vector3F} count
	 */
	void onComparisonVector3F(final Scene scene, final long milliseconds, final int vector3FCountAll, final int vector3FCountUnique);
	
	/**
	 * Called by a {@link SceneCompiler} to report when the compilation process ends.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 */
	void onCompilationEnd(final Scene scene, final long milliseconds);
	
	/**
	 * Called by a {@link SceneCompiler} to report when the compilation process starts.
	 * 
	 * @param scene the {@link Scene} being compiled
	 * @param milliseconds the time the compilation process has taken this far, in milliseconds
	 */
	void onCompilationStart(final Scene scene, final long milliseconds);
}