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
package org.dayflower.pathtracer.scene.bvh;

import static org.dayflower.pathtracer.math.MathF.abs;
import static org.dayflower.pathtracer.math.MathF.max;
import static org.dayflower.pathtracer.math.MathF.min;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.math.Point3F;
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
		final List<LeafNode> leafNodes = new ArrayList<>(triangles.size());
		
		float maximumX = Float.MIN_VALUE;
		float maximumY = Float.MIN_VALUE;
		float maximumZ = Float.MIN_VALUE;
		float minimumX = Float.MAX_VALUE;
		float minimumY = Float.MAX_VALUE;
		float minimumZ = Float.MAX_VALUE;
		
		for(final Triangle triangle : triangles) {
			final Point3F p0 = triangle.a.position;
			final Point3F p1 = triangle.b.position;
			final Point3F p2 = triangle.c.position;
			
			final
			LeafNode leafNode = new LeafNode(0);
			leafNode.addTriangle(triangle);
			leafNode.setMaximum(doGetMaximumX(p0, p1, p2), doGetMaximumY(p0, p1, p2), doGetMaximumZ(p0, p1, p2));
			leafNode.setMinimum(doGetMinimumX(p0, p1, p2), doGetMinimumY(p0, p1, p2), doGetMinimumZ(p0, p1, p2));
			
			leafNodes.add(leafNode);
			
			maximumX = max(maximumX, leafNode.getMaximumX());
			maximumY = max(maximumY, leafNode.getMaximumY());
			maximumZ = max(maximumZ, leafNode.getMaximumZ());
			minimumX = min(minimumX, leafNode.getMinimumX());
			minimumY = min(minimumY, leafNode.getMinimumY());
			minimumZ = min(minimumZ, leafNode.getMinimumZ());
		}
		
		final
		Node nodeRoot = doCreateBoundingVolumeHierarchy(leafNodes, 0, maximumX, maximumY, maximumZ, minimumX, minimumY, minimumZ);
		nodeRoot.setMaximum(maximumX, maximumY, maximumZ);
		nodeRoot.setMinimum(minimumX, minimumY, minimumZ);
		
		return new BoundingVolumeHierarchy(nodeRoot);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static final class LeafNode extends Node {
		private final List<Triangle> triangles = new ArrayList<>(40);
		
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
		private float maximumX = Float.MAX_VALUE;
		private float maximumY = Float.MAX_VALUE;
		private float maximumZ = Float.MAX_VALUE;
		private float minimumX = Float.MIN_VALUE;
		private float minimumY = Float.MIN_VALUE;
		private float minimumZ = Float.MIN_VALUE;
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
		public final float getCenterX() {
			return (this.maximumX + this.minimumX) * 0.5F;
		}
		
//		TODO: Add Javadocs.
		public final float getCenterY() {
			return (this.maximumY + this.minimumY) * 0.5F;
		}
		
//		TODO: Add Javadocs.
		public final float getCenterZ() {
			return (this.maximumZ + this.minimumZ) * 0.5F;
		}
		
//		TODO: Add Javadocs.
		public final float getMaximumX() {
			return this.maximumX;
		}
		
//		TODO: Add Javadocs.
		public final float getMaximumY() {
			return this.maximumY;
		}
		
//		TODO: Add Javadocs.
		public final float getMaximumZ() {
			return this.maximumZ;
		}
		
//		TODO: Add Javadocs.
		public final float getMinimumX() {
			return this.minimumX;
		}
		
//		TODO: Add Javadocs.
		public final float getMinimumY() {
			return this.minimumY;
		}
		
//		TODO: Add Javadocs.
		public final float getMinimumZ() {
			return this.minimumZ;
		}
		
//		TODO: Add Javadocs.
		public final int getDepth() {
			return this.depth;
		}
		
//		TODO: Add Javadocs.
		public abstract String toString(final int indentation);
		
//		TODO: Add Javadocs.
		public final void setMaximum(final float maximumX, final float maximumY, final float maximumZ) {
			this.maximumX = maximumX;
			this.maximumY = maximumY;
			this.maximumZ = maximumZ;
		}
		
//		TODO: Add Javadocs.
		public final void setMinimum(final float minimumX, final float minimumY, final float minimumZ) {
			this.minimumX = minimumX;
			this.minimumY = minimumY;
			this.minimumZ = minimumZ;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static final class TreeNode extends Node {
		private Node left;
		private Node right;
		
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
	
	private static float doGetMaximumX(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.x > p1.x && p0.x > p2.x ? p0.x : p1.x > p2.x ? p1.x : p2.x;
	}
	
	private static float doGetMaximumY(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.y > p1.y && p0.y > p2.y ? p0.y : p1.y > p2.y ? p1.y : p2.y;
	}
	
	private static float doGetMaximumZ(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.z > p1.z && p0.z > p2.z ? p0.z : p1.z > p2.z ? p1.z : p2.z;
	}
	
	private static float doGetMinimumX(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.x < p1.x && p0.x < p2.x ? p0.x : p1.x < p2.x ? p1.x : p2.x;
	}
	
	private static float doGetMinimumY(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.y < p1.y && p0.y < p2.y ? p0.y : p1.y < p2.y ? p1.y : p2.y;
	}
	
	private static float doGetMinimumZ(final Point3F p0, final Point3F p1, final Point3F p2) {
		return p0.z < p1.z && p0.z < p2.z ? p0.z : p1.z < p2.z ? p1.z : p2.z;
	}
	
	private static Node doCreateBoundingVolumeHierarchy(final List<LeafNode> leafNodes, final int depth, final float maximumX, final float maximumY, final float maximumZ, final float minimumX, final float minimumY, final float minimumZ) {
		final int size0 = leafNodes.size();
		final int size1 = size0 / 2;
		
		if(size0 < 4) {
			final LeafNode leafNode = new LeafNode(depth);
			
			for(final LeafNode leafNode0 : leafNodes) {
				for(final Triangle triangle : leafNode0.getTriangles()) {
					leafNode.addTriangle(triangle);
				}
			}
			
			return leafNode;
		}
		
		final float sideX = maximumX - minimumX;
		final float sideY = maximumY - minimumY;
		final float sideZ = maximumZ - minimumZ;
		
		float minimumCost = size0 * (sideX * sideY + sideY * sideZ + sideZ * sideX);
		float bestSplit = Float.MAX_VALUE;
		
		int bestAxis = -1;
		
		for(int i = 0; i < 3; i++) {
			final int axis = i;
			
			final float start = axis == 0 ? minimumX : axis == 1 ? minimumY : minimumZ;
			final float stop = axis == 0 ? maximumX : axis == 1 ? maximumY : maximumZ;
			
			if(abs(stop - start) < 1.0e-3F) {//Used to be 1.0e-4F.
				continue;
			}
			
			final float step = (stop - start) / (1024.0F / (depth + 1.0F));
			
			for(float split = start + step; split < stop - step; split += step) {
				float maximumLeftX = Float.MIN_VALUE;
				float maximumLeftY = Float.MIN_VALUE;
				float maximumLeftZ = Float.MIN_VALUE;
				float minimumLeftX = Float.MAX_VALUE;
				float minimumLeftY = Float.MAX_VALUE;
				float minimumLeftZ = Float.MAX_VALUE;
				
				float maximumRightX = Float.MIN_VALUE;
				float maximumRightY = Float.MIN_VALUE;
				float maximumRightZ = Float.MIN_VALUE;
				float minimumRightX = Float.MAX_VALUE;
				float minimumRightY = Float.MAX_VALUE;
				float minimumRightZ = Float.MAX_VALUE;
				
				int countLeft = 0;
				int countRight = 0;
				
				for(final LeafNode leafNode : leafNodes) {
					final float centerX = leafNode.getCenterX();
					final float centerY = leafNode.getCenterY();
					final float centerZ = leafNode.getCenterZ();
					
					final float value = axis == 0 ? centerX : axis == 1 ? centerY : centerZ;
					
					if(value < split) {
						maximumLeftX = max(maximumLeftX, leafNode.getMaximumX());
						maximumLeftY = max(maximumLeftY, leafNode.getMaximumY());
						maximumLeftZ = max(maximumLeftZ, leafNode.getMaximumZ());
						minimumLeftX = min(minimumLeftX, leafNode.getMinimumX());
						minimumLeftY = min(minimumLeftY, leafNode.getMinimumY());
						minimumLeftZ = min(minimumLeftZ, leafNode.getMinimumZ());
						
						countLeft++;
					} else {
						maximumRightX = max(maximumRightX, leafNode.getMaximumX());
						maximumRightY = max(maximumRightY, leafNode.getMaximumY());
						maximumRightZ = max(maximumRightZ, leafNode.getMaximumZ());
						minimumRightX = min(minimumRightX, leafNode.getMinimumX());
						minimumRightY = min(minimumRightY, leafNode.getMinimumY());
						minimumRightZ = min(minimumRightZ, leafNode.getMinimumZ());
						
						countRight++;
					}
				}
				
				if(countLeft <= 1 || countRight <= 1) {
					continue;
				}
				
				final float sideLeftX = maximumLeftX - minimumLeftX;
				final float sideLeftY = maximumLeftY - minimumLeftY;
				final float sideLeftZ = maximumLeftZ - minimumLeftZ;
				
				final float sideRightX = maximumRightX - minimumRightX;
				final float sideRightY = maximumRightY - minimumRightY;
				final float sideRightZ = maximumRightZ - minimumRightZ;
				
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
		
		final List<LeafNode> leafNodesLeft = new ArrayList<>(size1);
		final List<LeafNode> leafNodesRight = new ArrayList<>(size1);
		
		float maximumLeftX = Float.MIN_VALUE;
		float maximumLeftY = Float.MIN_VALUE;
		float maximumLeftZ = Float.MIN_VALUE;
		float minimumLeftX = Float.MAX_VALUE;
		float minimumLeftY = Float.MAX_VALUE;
		float minimumLeftZ = Float.MAX_VALUE;
		
		float maximumRightX = Float.MIN_VALUE;
		float maximumRightY = Float.MIN_VALUE;
		float maximumRightZ = Float.MIN_VALUE;
		float minimumRightX = Float.MAX_VALUE;
		float minimumRightY = Float.MAX_VALUE;
		float minimumRightZ = Float.MAX_VALUE;
		
		for(final LeafNode leafNode : leafNodes) {
			final float centerX = leafNode.getCenterX();
			final float centerY = leafNode.getCenterY();
			final float centerZ = leafNode.getCenterZ();
			
			final float value = bestAxis == 0 ? centerX : bestAxis == 1 ? centerY : centerZ;
			
			if(value < bestSplit) {
				leafNodesLeft.add(leafNode);
				
				maximumLeftX = max(maximumLeftX, leafNode.getMaximumX());
				maximumLeftY = max(maximumLeftY, leafNode.getMaximumY());
				maximumLeftZ = max(maximumLeftZ, leafNode.getMaximumZ());
				minimumLeftX = min(minimumLeftX, leafNode.getMinimumX());
				minimumLeftY = min(minimumLeftY, leafNode.getMinimumY());
				minimumLeftZ = min(minimumLeftZ, leafNode.getMinimumZ());
			} else {
				leafNodesRight.add(leafNode);
				
				maximumRightX = max(maximumRightX, leafNode.getMaximumX());
				maximumRightY = max(maximumRightY, leafNode.getMaximumY());
				maximumRightZ = max(maximumRightZ, leafNode.getMaximumZ());
				minimumRightX = min(minimumRightX, leafNode.getMinimumX());
				minimumRightY = min(minimumRightY, leafNode.getMinimumY());
				minimumRightZ = min(minimumRightZ, leafNode.getMinimumZ());
			}
		}
		
		final
		Node nodeLeft = doCreateBoundingVolumeHierarchy(leafNodesLeft, depth + 1, maximumLeftX, maximumLeftY, maximumLeftZ, minimumLeftX, minimumLeftY, minimumLeftZ);
		nodeLeft.setMaximum(maximumLeftX, maximumLeftY, maximumLeftZ);
		nodeLeft.setMinimum(minimumLeftX, minimumLeftY, minimumLeftZ);
		
		final
		Node nodeRight = doCreateBoundingVolumeHierarchy(leafNodesRight, depth + 1, maximumRightX, maximumRightY, maximumRightZ, minimumRightX, minimumRightY, minimumRightZ);
		nodeRight.setMaximum(maximumRightX, maximumRightY, maximumRightZ);
		nodeRight.setMinimum(minimumRightX, minimumRightY, minimumRightZ);
		
		final
		TreeNode treeNode = new TreeNode(depth);
		treeNode.setLeft(nodeLeft);
		treeNode.setRight(nodeRight);
		
		return treeNode;
	}
}