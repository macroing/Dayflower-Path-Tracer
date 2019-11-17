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
package org.dayflower.pathtracer.test;

import java.util.List;
import java.util.Optional;

import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.Node;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.TreeNode;
import org.dayflower.pathtracer.scene.shape.TriangleMesh;
import org.dayflower.pathtracer.scene.wavefront.ObjectLoader;
import org.dayflower.pathtracer.util.Strings;

public final class BoundingVolumeHierarchyTest {
	private BoundingVolumeHierarchyTest() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final List<Primitive> primitives = ObjectLoader.load("./resources/distribution/resources/model/icosphere.obj");
		
		final Primitive primitive = primitives.get(0);
		
		final Shape shape = primitive.getShape();
		
		final TriangleMesh triangleMesh = TriangleMesh.class.cast(shape);
		
		final BoundingVolumeHierarchy boundingVolumeHierarchy = BoundingVolumeHierarchy.createBoundingVolumeHierarchy(triangleMesh.getTriangles());
		
		final Node node = boundingVolumeHierarchy.getRoot();
		
		final List<Node> nodes = node.toList();
		
		doPrint(node);
		doPrint(nodes);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void doPrint(final Node node) {
		System.out.println(Strings.repeat(" ", node.getDepth()) + node.getId());
		
		if(node instanceof TreeNode) {
			final TreeNode treeNode = TreeNode.class.cast(node);
			
			final Optional<Node> optionalNodeLeft = treeNode.getLeft();
			final Optional<Node> optionalNodeRight = treeNode.getRight();
			
			if(optionalNodeLeft.isPresent()) {
				doPrint(optionalNodeLeft.get());
			}
			
			if(optionalNodeRight.isPresent()) {
				doPrint(optionalNodeRight.get());
			}
		}
	}
	
	private static void doPrint(final List<Node> nodes) {
		for(int i = 0; i < nodes.size(); i++) {
			System.out.println(nodes.get(i).getId());
		}
	}
}