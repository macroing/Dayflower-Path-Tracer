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
package org.dayflower.pathtracer.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.shape.Triangle;

//TODO: Add Javadocs.
public final class BoundingVolumeHierarchyTest {
	private BoundingVolumeHierarchyTest() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static void main(final String[] args) {
		final Scene scene = Scenes.newHouseScene();
		
		final List<Triangle> triangles = new ArrayList<>();
		
		for(final Shape shape : scene.getShapes()) {
			if(shape instanceof Triangle) {
				triangles.add(Triangle.class.cast(shape));
			}
		}
		
		final long currentTimeMillis0 = System.currentTimeMillis();
		
//		final BoundingVolumeHierarchy boundingVolumeHierarchy = BoundingVolumeHierarchy.createBoundingVolumeHierarchy(triangles);
		final BoundingVolumeHierarchy boundingVolumeHierarchy = BoundingVolumeHierarchy.createBoundingVolumeHierarchyConcurrently(triangles);
		
		final long currentTimeMillis1 = System.currentTimeMillis();
		final long elapsedTimeMillis = currentTimeMillis1 - currentTimeMillis0;
		
		System.out.println("It took " + elapsedTimeMillis + " millis to build.");
		
		try {
			Files.write(new File("BoundingVolumeHierarchy.txt").toPath(), boundingVolumeHierarchy.getRoot().toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
}