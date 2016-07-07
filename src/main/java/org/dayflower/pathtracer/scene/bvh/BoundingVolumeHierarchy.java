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
package org.dayflower.pathtracer.scene.bvh;

import static org.dayflower.pathtracer.math.Math2.abs;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.util.Strings;

//TODO: Add Javadocs.
public final class BoundingVolumeHierarchy {
	private final Node root;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private BoundingVolumeHierarchy(final Node root) {
		this.root = Objects.requireNonNull(root, "root == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Node getRoot() {
		return this.root;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static BoundingVolumeHierarchy createBoundingVolumeHierarchy(final List<Triangle> triangles) {
		final List<LeafNode> leafNodes = doCreateLeafNodes(triangles);
		
		final Node root = doCreateBoundingVolumeHierarchy(leafNodes, 0);
		
		return new BoundingVolumeHierarchy(root);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static final class LeafNode extends Node {
		private final List<Triangle> triangles = new ArrayList<>();
		private Point3 maximum;
		private Point3 minimum;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		public LeafNode(final int depth) {
			super(depth);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		@Override
		public boolean isLeaf() {
			return true;
		}
		
//		TODO: Add Javadocs.
		public List<Triangle> getTriangles() {
			return this.triangles;
		}
		
//		TODO: Add Javadocs.
		@Override
		public Point3 getMaximum() {
			if(this.maximum == null) {
				this.maximum = Triangle.maximum(this.triangles);
			}
			
			return this.maximum;
		}
		
//		TODO: Add Javadocs.
		@Override
		public Point3 getMinimum() {
			if(this.minimum == null) {
				this.minimum = Triangle.minimum(this.triangles);
			}
			
			return this.minimum;
		}
		
//		TODO: Add Javadocs.
		@Override
		public String toString() {
			return toString(0);
		}
		
//		TODO: Add Javadocs.
		@Override
		public String toString(final int indentation) {
			return String.format("%sLeafNode", Strings.repeat(" ", indentation));
		}
		
//		TODO: Add Javadocs.
		public void addTriangle(final Triangle triangle) {
			this.triangles.add(Objects.requireNonNull(triangle, "triangle == null"));
		}
		
//		TODO: Add Javadocs.
		public void removeTriangle(final Triangle triangle) {
			this.triangles.remove(Objects.requireNonNull(triangle, "triangle == null"));
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static abstract class Node {
		private final int depth;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		protected Node(final int depth) {
			this.depth = depth;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		public abstract boolean isLeaf();
		
//		TODO: Add Javadocs.
		public final int getDepth() {
			return this.depth;
		}
		
//		TODO: Add Javadocs.
		public final Point3 getCenter() {
			return Point3.center(getMaximum(), getMinimum());
		}
		
//		TODO: Add Javadocs.
		public abstract Point3 getMaximum();
		
//		TODO: Add Javadocs.
		public abstract Point3 getMinimum();
		
//		TODO: Add Javadocs.
		public abstract String toString(final int indentation);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static final class TreeNode extends Node {
		private Node left;
		private Node right;
		private Point3 maximum;
		private Point3 minimum;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		public TreeNode(final int depth) {
			super(depth);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		TODO: Add Javadocs.
		@Override
		public boolean isLeaf() {
			return false;
		}
		
//		TODO: Add Javadocs.
		public Optional<Node> getLeft() {
			return Optional.ofNullable(this.left);
		}
		
//		TODO: Add Javadocs.
		public Optional<Node> getRight() {
			return Optional.ofNullable(this.right);
		}
		
//		TODO: Add Javadocs.
		@Override
		public Point3 getMaximum() {
			if(this.maximum == null) {
				this.maximum = Point3.maximum(this.left != null ? this.left.getMaximum() : new Point3(), this.right != null ? this.right.getMaximum() : new Point3());
			}
			
			return this.maximum;
		}
		
//		TODO: Add Javadocs.
		@Override
		public Point3 getMinimum() {
			if(this.minimum == null) {
				this.minimum = Point3.minimum(this.left != null ? this.left.getMinimum() : new Point3(), this.right != null ? this.right.getMinimum() : new Point3());
			}
			
			return this.minimum;
		}
		
//		TODO: Add Javadocs.
		@Override
		public String toString() {
			return toString(0);
		}
		
//		TODO: Add Javadocs.
		@Override
		public String toString(final int indentation) {
			final Node left = this.left;
			final Node right = this.right;
			
			final
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(String.format("%sTreeNode: {%n", Strings.repeat(" ", indentation)));
			stringBuilder.append(String.format("%s%n", left != null ? left.toString(indentation + 1) : ""));
			stringBuilder.append(String.format("%s%n", right != null ? right.toString(indentation + 1) : ""));
			stringBuilder.append(String.format("%s}%n", Strings.repeat(" ", indentation)));
			
			return stringBuilder.toString();
		}
		
//		TODO: Add Javadocs.
		public void setLeft(final Node left) {
			this.left = Objects.requireNonNull(left, "left == null");
		}
		
//		TODO: Add Javadocs.
		public void setRight(final Node right) {
			this.right = Objects.requireNonNull(right, "right == null");
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static List<LeafNode> doCreateLeafNodes(final List<Triangle> triangles) {
		final List<LeafNode> leafNodes = new ArrayList<>(triangles.size());
		
		for(final Triangle triangle : triangles) {
			final
			LeafNode leafNode = new LeafNode(0);
			leafNode.addTriangle(triangle);
			
			leafNodes.add(leafNode);
		}
		
		return leafNodes;
	}
	
	private static Node doCreateBoundingVolumeHierarchy(final List<LeafNode> leafNodes, final int depth) {
		if(leafNodes.size() < 4) {
			final LeafNode leafNode = new LeafNode(depth);
			
			for(final LeafNode leafNode0 : leafNodes) {
				for(final Triangle triangle : leafNode0.getTriangles()) {
					leafNode.addTriangle(triangle);
				}
			}
			
			return leafNode;
		}
		
		final Point3 maximum = doMaximum(leafNodes);
		final Point3 minimum = doMinimum(leafNodes);
		
		final float sideX = maximum.x - minimum.x;
		final float sideY = maximum.y - minimum.y;
		final float sideZ = maximum.z - minimum.z;
		
		float minimumCost = leafNodes.size() * (sideX * sideY + sideY * sideZ + sideZ * sideX);
		float bestSplit = Float.MAX_VALUE;
		
		int bestAxis = -1;
		
		for(int i = 0; i < 3; i++) {
			final int axis = i;
			
			final float start = axis == 0 ? minimum.x : axis == 1 ? minimum.y : minimum.z;
			final float stop = axis == 0 ? maximum.x : axis == 1 ? maximum.y : maximum.z;
			
			if(abs(stop - start) < 1.0e-4F) {
				continue;
			}
			
			final float step = (stop - start) / (1024.0F / (depth + 1.0F));
			
			for(float split = start + step; split < stop - step; split += step) {
				Point3 maximumLeft = Point3.MINIMUM;
				Point3 minimumLeft = Point3.MAXIMUM;
				
				Point3 maximumRight = Point3.MINIMUM;
				Point3 minimumRight = Point3.MAXIMUM;
				
				int countLeft = 0;
				int countRight = 0;
				
				for(final LeafNode leafNode : leafNodes) {
					final Point3 center = leafNode.getCenter();
					
					final float value = axis == 0 ? center.x : axis == 1 ? center.y : center.z;
					
					if(value < split) {
						maximumLeft = Point3.maximum(maximumLeft, leafNode.getMaximum());
						minimumLeft = Point3.minimum(minimumLeft, leafNode.getMinimum());
						
						countLeft++;
					} else {
						maximumRight = Point3.maximum(maximumRight, leafNode.getMaximum());
						minimumRight = Point3.minimum(minimumRight, leafNode.getMinimum());
						
						countRight++;
					}
				}
				
				if(countLeft <= 1 || countRight <= 1) {
					continue;
				}
				
				final float sideLeftX = maximumLeft.x - minimumLeft.x;
				final float sideLeftY = maximumLeft.y - minimumLeft.y;
				final float sideLeftZ = maximumLeft.z - minimumLeft.z;
				
				final float sideRightX = maximumRight.x - minimumRight.x;
				final float sideRightY = maximumRight.y - minimumRight.y;
				final float sideRightZ = maximumRight.z - minimumRight.z;
				
				final float surfaceLeft = sideLeftX * sideLeftY + sideLeftY * sideLeftZ + sideLeftZ * sideLeftX;
				final float surfaceRight = sideRightX * sideRightY + sideRightY * sideRightZ + sideRightZ * sideRightX;
				
				final float cost = surfaceLeft * countLeft + surfaceRight * countRight;
				
				if(cost < minimumCost) {
					minimumCost = cost;
					bestSplit = split;
					bestAxis = axis;
				}
			}
		}
		
		if(bestAxis == -1) {
			final LeafNode leafNode = new LeafNode(depth);
			
			for(final LeafNode leafNode0 : leafNodes) {
				for(final Triangle triangle : leafNode0.getTriangles()) {
					leafNode.addTriangle(triangle);
				}
			}
			
			return leafNode;
		}
		
		final List<LeafNode> leafNodesLeft = new ArrayList<>();
		final List<LeafNode> leafNodesRight = new ArrayList<>();
		
		for(final LeafNode leafNode : leafNodes) {
			final Point3 center = leafNode.getCenter();
			
			final float value = bestAxis == 0 ? center.x : bestAxis == 1 ? center.y : center.z;
			
			if(value < bestSplit) {
				leafNodesLeft.add(leafNode);
			} else {
				leafNodesRight.add(leafNode);
			}
		}
		
		final
		TreeNode treeNode = new TreeNode(depth);
		treeNode.setLeft(doCreateBoundingVolumeHierarchy(leafNodesLeft, depth + 1));
		treeNode.setRight(doCreateBoundingVolumeHierarchy(leafNodesRight, depth + 1));
		
		return treeNode;
	}
	
	private static Point3 doMaximum(final List<LeafNode> leafNodes) {
		Point3 maximum = Point3.MINIMUM;
		
		for(final LeafNode leafNode : leafNodes) {
			maximum = Point3.maximum(maximum, leafNode.getMaximum());
		}
		
		return maximum;
	}
	
	private static Point3 doMinimum(final List<LeafNode> leafNodes) {
		Point3 minimum = Point3.MAXIMUM;
		
		for(final LeafNode leafNode : leafNodes) {
			minimum = Point3.minimum(minimum, leafNode.getMinimum());
		}
		
		return minimum;
	}
}