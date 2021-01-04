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
package org.dayflower.pathtracer.scene.bvh;

import static org.macroing.math4j.MathF.abs;
import static org.macroing.math4j.MathF.max;
import static org.macroing.math4j.MathF.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.util.Strings;
import org.macroing.math4j.Point3F;

/**
 * A {@code BoundingVolumeHierarchy} is an implementation of an acceleration structure called a Bounding Volume Hierarchy (BVH).
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class BoundingVolumeHierarchy {
	/**
	 * The type number associated with a {@link LeafNode}. The number is {@code 2}.
	 */
	public static final int NODE_TYPE_LEAF = 2;
	
	/**
	 * The type number associated with a {@link TreeNode}. The number is {@code 1}.
	 */
	public static final int NODE_TYPE_TREE = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Node root;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private BoundingVolumeHierarchy(final Node root) {
		this.root = Objects.requireNonNull(root, "root == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the root {@link Node} of this {@code BoundingVolumeHierarchy} instance.
	 * 
	 * @return the root {@code Node} of this {@code BoundingVolumeHierarchy} instance
	 */
	public Node getRoot() {
		return this.root;
	}
	
	/**
	 * Compares {@code object} to this {@code BoundingVolumeHierarchy} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code BoundingVolumeHierarchy}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code BoundingVolumeHierarchy} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code BoundingVolumeHierarchy}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof BoundingVolumeHierarchy)) {
			return false;
		} else if(!Objects.equals(this.root, BoundingVolumeHierarchy.class.cast(object).root)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code BoundingVolumeHierarchy} instance.
	 * 
	 * @return a hash code for this {@code BoundingVolumeHierarchy} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.root);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a {@code BoundingVolumeHierarchy} based on a {@code List} of {@link Triangle}s.
	 * <p>
	 * Returns a {@code BoundingVolumeHierarchy} instance.
	 * <p>
	 * If either {@code triangles} or at least one {@code Triangle} in {@code triangles} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param triangles the {@code Triangle}s to create the {@code BoundingVolumeHierarchy} from
	 * @return a {@code BoundingVolumeHierarchy} instance
	 * @throws NullPointerException thrown if, and only if, either {@code triangles} or at least one {@code Triangle} in {@code triangles} are {@code null}
	 */
	public static BoundingVolumeHierarchy createBoundingVolumeHierarchy(final List<Triangle> triangles) {
		final AtomicInteger idGenerator = new AtomicInteger();
		
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
			LeafNode leafNode = new LeafNode(0, 0);
			leafNode.addTriangle(triangle);
			leafNode.setMaximum(max(p0.x, p1.x, p2.x), max(p0.y, p1.y, p2.y), max(p0.z, p1.z, p2.z));
			leafNode.setMinimum(min(p0.x, p1.x, p2.x), min(p0.y, p1.y, p2.y), min(p0.z, p1.z, p2.z));
			
			leafNodes.add(leafNode);
			
			maximumX = max(maximumX, leafNode.getMaximumX());
			maximumY = max(maximumY, leafNode.getMaximumY());
			maximumZ = max(maximumZ, leafNode.getMaximumZ());
			minimumX = min(minimumX, leafNode.getMinimumX());
			minimumY = min(minimumY, leafNode.getMinimumY());
			minimumZ = min(minimumZ, leafNode.getMinimumZ());
		}
		
		final
		Node nodeRoot = doCreateBoundingVolumeHierarchy(idGenerator, leafNodes, 0, maximumX, maximumY, maximumZ, minimumX, minimumY, minimumZ);
		nodeRoot.setMaximum(maximumX, maximumY, maximumZ);
		nodeRoot.setMinimum(minimumX, minimumY, minimumZ);
		
		return new BoundingVolumeHierarchy(nodeRoot);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code LeafNode} is a {@link Node} implementation that represents a leaf node in a Bounding Volume Hierarchy (BVH).
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static final class LeafNode extends Node {
		private final List<Triangle> triangles = new ArrayList<>(10);
		private int size;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code LeafNode} instance.
		 * 
		 * @param depth the depth of this {@code LeafNode}
		 * @param id the ID of this {@code LeafNode}
		 */
		public LeafNode(final int depth, final int id) {
			super(depth, id);
			
			this.size = 5;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns a {@code List} with all {@code Node}s added in a depth-first order.
		 * <p>
		 * If {@code nodes} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param nodes the {@code List} to add {@code Node}s to and return
		 * @return a {@code List} with all {@code Node}s added in a depth-first order
		 * @throws NullPointerException thrown if, and only if, {@code nodes} is {@code null}
		 */
		@Override
		public List<Node> toList(final List<Node> nodes) {
			nodes.add(this);
			
			return nodes;
		}
		
		/**
		 * Returns a {@code List} with the {@link Triangle}s currently added.
		 * <p>
		 * Modifying the returned {@code List} will affect this {@code LeafNode} instance.
		 * 
		 * @return a {@code List} with the {@code Triangle}s currently added
		 */
		public List<Triangle> getTriangles() {
			return this.triangles;
		}
		
		/**
		 * Returns a {@code String} representation of this {@code LeafNode} instance.
		 * <p>
		 * Calling this method is equivalent to {@code toString(0)}.
		 * 
		 * @return a {@code String} representation of this {@code LeafNode} instance
		 */
		@Override
		public String toString() {
			return toString(0);
		}
		
		/**
		 * Returns a {@code String} representation of this {@code LeafNode} instance.
		 * 
		 * @param indentation the number of indentations to use
		 * @return a {@code String} representation of this {@code LeafNode} instance
		 */
		@Override
		public String toString(final int indentation) {
			return String.format("%sLeafNode", Strings.repeat(" ", indentation));
		}
		
		/**
		 * Compares {@code object} to this {@code LeafNode} instance for equality.
		 * <p>
		 * Returns {@code true} if, and only if, {@code object} is an instance of {@code LeafNode}, and their respective values are equal, {@code false} otherwise.
		 * 
		 * @param object the {@code Object} to compare to this {@code LeafNode} instance for equality
		 * @return {@code true} if, and only if, {@code object} is an instance of {@code LeafNode}, and their respective values are equal, {@code false} otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			} else if(!(object instanceof LeafNode)) {
				return false;
			} else if(Float.compare(getMaximumX(), LeafNode.class.cast(object).getMaximumX()) != 0) {
				return false;
			} else if(Float.compare(getMaximumY(), LeafNode.class.cast(object).getMaximumY()) != 0) {
				return false;
			} else if(Float.compare(getMaximumZ(), LeafNode.class.cast(object).getMaximumZ()) != 0) {
				return false;
			} else if(Float.compare(getMinimumX(), LeafNode.class.cast(object).getMinimumX()) != 0) {
				return false;
			} else if(Float.compare(getMinimumY(), LeafNode.class.cast(object).getMinimumY()) != 0) {
				return false;
			} else if(Float.compare(getMinimumZ(), LeafNode.class.cast(object).getMinimumZ()) != 0) {
				return false;
			} else if(!Objects.equals(this.triangles, LeafNode.class.cast(object).triangles)) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Returns {@code true}.
		 * 
		 * @return {@code true}
		 */
		@Override
		public boolean isLeaf() {
			return true;
		}
		
		/**
		 * Returns the size of this {@code LeafNode} instance.
		 * 
		 * @return the size of this {@code LeafNode} instance
		 */
		@Override
		public int getSize() {
			return this.size;
		}
		
		/**
		 * Returns a hash code for this {@code LeafNode} instance.
		 * 
		 * @return a hash code for this {@code LeafNode} instance
		 */
		@Override
		public int hashCode() {
			return Objects.hash(Float.valueOf(getMaximumX()), Float.valueOf(getMaximumY()), Float.valueOf(getMaximumZ()), Float.valueOf(getMinimumX()), Float.valueOf(getMinimumY()), Float.valueOf(getMinimumZ()), this.triangles);
		}
		
		/**
		 * Adds the maximum and minimum bounds to {@code point3Fs}.
		 * <p>
		 * If {@code point3Fs} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param point3Fs a {@code Collection} of {@link Point3F}s
		 * @throws NullPointerException thrown if, and only if, {@code point3Fs} is {@code null}
		 */
		@Override
		public void addBounds(final Collection<Point3F> point3Fs) {
			point3Fs.add(getMaximum());
			point3Fs.add(getMinimum());
		}
		
		/**
		 * Adds {@code triangle} to this {@code LeafNode} instance.
		 * <p>
		 * If {@code triangle} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param triangle the {@link Triangle} to add
		 * @throws NullPointerException thrown if, and only if, {@code triangle} is {@code null}
		 */
		public void addTriangle(final Triangle triangle) {
			if(this.triangles.add(Objects.requireNonNull(triangle, "triangle == null"))) {
				this.size += 1;
			}
		}
		
		/**
		 * Removes {@code triangle} from this {@code LeafNode} instance.
		 * <p>
		 * If {@code triangle} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param triangle the {@link Triangle} to remove
		 * @throws NullPointerException thrown if, and only if, {@code triangle} is {@code null}
		 */
		public void removeTriangle(final Triangle triangle) {
			if(this.triangles.remove(Objects.requireNonNull(triangle, "triangle == null"))) {
				this.size -= 1;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code Node} is an abstract base type used to represent a node in a Bounding Volume Hierarchy (BVH).
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static abstract class Node {
		private float maximumX = Float.MAX_VALUE;
		private float maximumY = Float.MAX_VALUE;
		private float maximumZ = Float.MAX_VALUE;
		private float minimumX = Float.MIN_VALUE;
		private float minimumY = Float.MIN_VALUE;
		private float minimumZ = Float.MIN_VALUE;
		private final int depth;
		private final int id;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code Node} instance.
		 * 
		 * @param depth the depth of this {@code Node}
		 * @param id the ID of this {@code Node}
		 */
		protected Node(final int depth, final int id) {
			this.depth = depth;
			this.id = id;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns a {@code List} with all {@code Node}s added in a depth-first order.
		 * <p>
		 * Calling this method is equivalent to {@code toList(new ArrayList<>())}.
		 * 
		 * @return a {@code List} with all {@code Node}s added in a depth-first order
		 */
		public final List<Node> toList() {
			return toList(new ArrayList<>());
		}
		
		/**
		 * Returns a {@code List} with all {@code Node}s added in a depth-first order.
		 * <p>
		 * If {@code nodes} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param nodes the {@code List} to add {@code Node}s to and return
		 * @return a {@code List} with all {@code Node}s added in a depth-first order
		 * @throws NullPointerException thrown if, and only if, {@code nodes} is {@code null}
		 */
		public abstract List<Node> toList(final List<Node> nodes);
		
		/**
		 * Returns a {@link Point3F} with the center of the bounds of this {@code Node} instance.
		 * 
		 * @return a {@code Point3F} with the center of the bounds of this {@code Node} instance
		 */
		public final Point3F getCenter() {
			return new Point3F(getCenterX(), getCenterY(), getCenterZ());
		}
		
		/**
		 * Returns a {@link Point3F} with the maximum bound of this {@code Node} instance.
		 * 
		 * @return a {@code Point3F} with the maximum bound of this {@code Node} instance
		 */
		public final Point3F getMaximum() {
			return new Point3F(getMaximumX(), getMaximumY(), getMaximumZ());
		}
		
		/**
		 * Returns a {@link Point3F} with the minimum bound of this {@code Node} instance.
		 * 
		 * @return a {@code Point3F} with the minimum bound of this {@code Node} instance
		 */
		public final Point3F getMinimum() {
			return new Point3F(getMinimumX(), getMinimumY(), getMinimumZ());
		}
		
		/**
		 * Returns a {@code String} representation of this {@code Node} instance.
		 * 
		 * @param indentation the number of indentations to use
		 * @return a {@code String} representation of this {@code Node} instance
		 */
		public abstract String toString(final int indentation);
		
		/**
		 * Returns {@code true} if, and only if, this {@code Node} is a leaf node, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code Node} is a leaf node, {@code false} otherwise
		 */
		public abstract boolean isLeaf();
		
		/**
		 * Returns the X-coordinate of the center of the bounds of this {@code Node} instance.
		 * 
		 * @return the X-coordinate of the center of the bounds of this {@code Node} instance
		 */
		public final float getCenterX() {
			return (this.maximumX + this.minimumX) * 0.5F;
		}
		
		/**
		 * Returns the Y-coordinate of the center of the bounds of this {@code Node} instance.
		 * 
		 * @return the Y-coordinate of the center of the bounds of this {@code Node} instance
		 */
		public final float getCenterY() {
			return (this.maximumY + this.minimumY) * 0.5F;
		}
		
		/**
		 * Returns the Z-coordinate of the center of the bounds of this {@code Node} instance.
		 * 
		 * @return the Z-coordinate of the center of the bounds of this {@code Node} instance
		 */
		public final float getCenterZ() {
			return (this.maximumZ + this.minimumZ) * 0.5F;
		}
		
		/**
		 * Returns the X-coordinate of the maximum bound of this {@code Node} instance.
		 * 
		 * @return the X-coordinate of the maximum bound of this {@code Node} instance
		 */
		public final float getMaximumX() {
			return this.maximumX;
		}
		
		/**
		 * Returns the Y-coordinate of the maximum bound of this {@code Node} instance.
		 * 
		 * @return the Y-coordinate of the maximum bound of this {@code Node} instance
		 */
		public final float getMaximumY() {
			return this.maximumY;
		}
		
		/**
		 * Returns the Z-coordinate of the maximum bound of this {@code Node} instance.
		 * 
		 * @return the Z-coordinate of the maximum bound of this {@code Node} instance
		 */
		public final float getMaximumZ() {
			return this.maximumZ;
		}
		
		/**
		 * Returns the X-coordinate of the minimum bound of this {@code Node} instance.
		 * 
		 * @return the X-coordinate of the minimum bound of this {@code Node} instance
		 */
		public final float getMinimumX() {
			return this.minimumX;
		}
		
		/**
		 * Returns the Y-coordinate of the minimum bound of this {@code Node} instance.
		 * 
		 * @return the Y-coordinate of the minimum bound of this {@code Node} instance
		 */
		public final float getMinimumY() {
			return this.minimumY;
		}
		
		/**
		 * Returns the Z-coordinate of the minimum bound of this {@code Node} instance.
		 * 
		 * @return the Z-coordinate of the minimum bound of this {@code Node} instance
		 */
		public final float getMinimumZ() {
			return this.minimumZ;
		}
		
		/**
		 * Returns the depth of this {@code Node} instance.
		 * 
		 * @return the depth of this {@code Node} instance
		 */
		public final int getDepth() {
			return this.depth;
		}
		
		/**
		 * Returns the ID of this {@code Node} instance.
		 * 
		 * @return the ID of this {@code Node} instance
		 */
		public final int getId() {
			return this.id;
		}
		
		/**
		 * Returns the size of this {@code Node} instance.
		 * 
		 * @return the size of this {@code Node} instance
		 */
		public abstract int getSize();
		
		/**
		 * Adds the maximum and minimum bounds to {@code point3Fs}.
		 * <p>
		 * If {@code point3Fs} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param point3Fs a {@code Collection} of {@link Point3F}s
		 * @throws NullPointerException thrown if, and only if, {@code point3Fs} is {@code null}
		 */
		public abstract void addBounds(final Collection<Point3F> point3Fs);
		
		/**
		 * Sets the maximum bound for this {@code Node} instance.
		 * 
		 * @param maximumX the X-coordinate of the maximum bound
		 * @param maximumY the Y-coordinate of the maximum bound
		 * @param maximumZ the Z-coordinate of the maximum bound
		 */
		public final void setMaximum(final float maximumX, final float maximumY, final float maximumZ) {
			this.maximumX = maximumX;
			this.maximumY = maximumY;
			this.maximumZ = maximumZ;
		}
		
		/**
		 * Sets the minimum bound for this {@code Node} instance.
		 * 
		 * @param minimumX the X-coordinate of the minimum bound
		 * @param minimumY the Y-coordinate of the minimum bound
		 * @param minimumZ the Z-coordinate of the minimum bound
		 */
		public final void setMinimum(final float minimumX, final float minimumY, final float minimumZ) {
			this.minimumX = minimumX;
			this.minimumY = minimumY;
			this.minimumZ = minimumZ;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code TreeNode} is a {@link Node} implementation that represents a tree node in a Bounding Volume Hierarchy (BVH).
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static final class TreeNode extends Node {
		private Node left;
		private Node right;
		private int size;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code TreeNode} instance.
		 * 
		 * @param depth the depth of this {@code TreeNode}
		 * @param id the ID of this {@code TreeNode}
		 */
		public TreeNode(final int depth, final int id) {
			super(depth, id);
			
			this.size = 5;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Returns a {@code List} with all {@code Node}s added in a depth-first order.
		 * <p>
		 * If {@code nodes} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param nodes the {@code List} to add {@code Node}s to and return
		 * @return a {@code List} with all {@code Node}s added in a depth-first order
		 * @throws NullPointerException thrown if, and only if, {@code nodes} is {@code null}
		 */
		@Override
		public List<Node> toList(final List<Node> nodes) {
			nodes.add(this);
			
			final Node left = this.left;
			final Node right = this.right;
			
			if(left != null) {
				left.toList(nodes);
			}
			
			if(right != null) {
				right.toList(nodes);
			}
			
			return nodes;
		}
		
		/**
		 * Returns an {@code Optional} with the optional left {@link Node}.
		 * 
		 * @return an {@code Optional} with the optional left {@code Node}
		 */
		public Optional<Node> getLeft() {
			return Optional.ofNullable(this.left);
		}
		
		/**
		 * Returns an {@code Optional} with the optional right {@link Node}.
		 * 
		 * @return an {@code Optional} with the optional right {@code Node}
		 */
		public Optional<Node> getRight() {
			return Optional.ofNullable(this.right);
		}
		
		/**
		 * Returns a {@code String} representation of this {@code TreeNode} instance.
		 * <p>
		 * Calling this method is equivalent to {@code toString(0)}.
		 * 
		 * @return a {@code String} representation of this {@code TreeNode} instance
		 */
		@Override
		public String toString() {
			return toString(0);
		}
		
		/**
		 * Returns a {@code String} representation of this {@code TreeNode} instance.
		 * 
		 * @param indentation the number of indentations to use
		 * @return a {@code String} representation of this {@code TreeNode} instance
		 */
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
		
		/**
		 * Compares {@code object} to this {@code TreeNode} instance for equality.
		 * <p>
		 * Returns {@code true} if, and only if, {@code object} is an instance of {@code TreeNode}, and their respective values are equal, {@code false} otherwise.
		 * 
		 * @param object the {@code Object} to compare to this {@code TreeNode} instance for equality
		 * @return {@code true} if, and only if, {@code object} is an instance of {@code TreeNode}, and their respective values are equal, {@code false} otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			} else if(!(object instanceof TreeNode)) {
				return false;
			} else if(Float.compare(getMaximumX(), TreeNode.class.cast(object).getMaximumX()) != 0) {
				return false;
			} else if(Float.compare(getMaximumY(), TreeNode.class.cast(object).getMaximumY()) != 0) {
				return false;
			} else if(Float.compare(getMaximumZ(), TreeNode.class.cast(object).getMaximumZ()) != 0) {
				return false;
			} else if(Float.compare(getMinimumX(), TreeNode.class.cast(object).getMinimumX()) != 0) {
				return false;
			} else if(Float.compare(getMinimumY(), TreeNode.class.cast(object).getMinimumY()) != 0) {
				return false;
			} else if(Float.compare(getMinimumZ(), TreeNode.class.cast(object).getMinimumZ()) != 0) {
				return false;
			} else if(!Objects.equals(this.left, TreeNode.class.cast(object).left)) {
				return false;
			} else if(!Objects.equals(this.right, TreeNode.class.cast(object).right)) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Returns {@code false}.
		 * 
		 * @return {@code false}
		 */
		@Override
		public boolean isLeaf() {
			return false;
		}
		
		/**
		 * Returns the size of this {@code TreeNode} instance.
		 * 
		 * @return the size of this {@code TreeNode} instance
		 */
		@Override
		public int getSize() {
			return this.size;
		}
		
		/**
		 * Returns a hash code for this {@code TreeNode} instance.
		 * 
		 * @return a hash code for this {@code TreeNode} instance
		 */
		@Override
		public int hashCode() {
			return Objects.hash(Float.valueOf(getMaximumX()), Float.valueOf(getMaximumY()), Float.valueOf(getMaximumZ()), Float.valueOf(getMinimumX()), Float.valueOf(getMinimumY()), Float.valueOf(getMinimumZ()), this.left, this.right);
		}
		
		/**
		 * Adds the maximum and minimum bounds to {@code point3Fs}.
		 * <p>
		 * If {@code point3Fs} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param point3Fs a {@code Collection} of {@link Point3F}s
		 * @throws NullPointerException thrown if, and only if, {@code point3Fs} is {@code null}
		 */
		@Override
		public void addBounds(final Collection<Point3F> point3Fs) {
			point3Fs.add(getMaximum());
			point3Fs.add(getMinimum());
			
			final Node left = this.left;
			final Node right = this.right;
			
			if(left != null) {
				left.addBounds(point3Fs);
			}
			
			if(right != null) {
				right.addBounds(point3Fs);
			}
		}
		
		/**
		 * Sets the left {@link Node}.
		 * 
		 * @param left the left {@code Node}, which may be {@code null}
		 */
		public void setLeft(final Node left) {
			this.size -= this.left != null ? this.left.getSize() : 0;
			this.left = left;
			this.size += this.left != null ? this.left.getSize() : 0;
		}
		
		/**
		 * Sets the right {@link Node}.
		 * 
		 * @param right the right {@code Node}, which may be {@code null}
		 */
		public void setRight(final Node right) {
			this.size -= this.right != null ? this.right.getSize() : 0;
			this.right = right;
			this.size += this.right != null ? this.right.getSize() : 0;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Node doCreateBoundingVolumeHierarchy(final AtomicInteger idGenerator, final List<LeafNode> leafNodes, final int depth, final float maximumX, final float maximumY, final float maximumZ, final float minimumX, final float minimumY, final float minimumZ) {
		final int size0 = leafNodes.size();
		final int size1 = size0 / 2;
		
		if(size0 < 4) {
			final LeafNode leafNode = new LeafNode(depth, idGenerator.getAndIncrement());
			
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
			
			if(abs(stop - start) < 1.0e-4F) {
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
			final LeafNode leafNode = new LeafNode(depth, idGenerator.getAndIncrement());
			
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
		Node nodeLeft = doCreateBoundingVolumeHierarchy(idGenerator, leafNodesLeft, depth + 1, maximumLeftX, maximumLeftY, maximumLeftZ, minimumLeftX, minimumLeftY, minimumLeftZ);
		nodeLeft.setMaximum(maximumLeftX, maximumLeftY, maximumLeftZ);
		nodeLeft.setMinimum(minimumLeftX, minimumLeftY, minimumLeftZ);
		
		final
		Node nodeRight = doCreateBoundingVolumeHierarchy(idGenerator, leafNodesRight, depth + 1, maximumRightX, maximumRightY, maximumRightZ, minimumRightX, minimumRightY, minimumRightZ);
		nodeRight.setMaximum(maximumRightX, maximumRightY, maximumRightZ);
		nodeRight.setMinimum(minimumRightX, minimumRightY, minimumRightZ);
		
		final
		TreeNode treeNode = new TreeNode(depth, idGenerator.getAndIncrement());
		treeNode.setLeft(nodeLeft);
		treeNode.setRight(nodeRight);
		
		return treeNode;
	}
}