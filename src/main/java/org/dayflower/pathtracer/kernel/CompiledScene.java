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
package org.dayflower.pathtracer.kernel;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.LeafNode;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.Node;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy.TreeNode;
import org.dayflower.pathtracer.scene.shape.Triangle;

//TODO: Add Javadocs.
public final class CompiledScene {
	private final float[] boundingVolumeHierarchy;
	private final float[] camera;
	private final float[] shapes;
	private final float[] textures;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CompiledScene(final float[] boundingVolumeHierarchy, final float[] camera, final float[] shapes, final float[] textures) {
		this.boundingVolumeHierarchy = Objects.requireNonNull(boundingVolumeHierarchy, "boundingVolumeHierarchy == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.shapes = Objects.requireNonNull(shapes, "shapes == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public float[] getBoundingVolumeHierarchy() {
		return this.boundingVolumeHierarchy;
	}
	
//	TODO: Add Javadocs.
	public float[] getCamera() {
		return this.camera;
	}
	
//	TODO: Add Javadocs.
	public float[] getShapes() {
		return this.shapes;
	}
	
//	TODO: Add Javadocs.
	public float[] getTextures() {
		return this.textures;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene compile(final Camera camera, final Scene scene) {
		return new CompiledScene(doCompileBoundingVolumeHierarchy(scene), camera.getArray(), doCompileShapes(scene), doCompileTextures(scene));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static float[] doCompileBoundingVolumeHierarchy(final Scene scene) {
		final List<Shape> shapes = scene.getShapes();
		final List<Triangle> triangles = new ArrayList<>();
		
		for(final Shape shape : shapes) {
			if(shape instanceof Triangle) {
				triangles.add(Triangle.class.cast(shape));
			}
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
				j += 16;
			} else if(node instanceof TreeNode) {
				j += 16;
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
				
				boundingVolumeHierarchyArray[j +  0] = 2.0F;
				boundingVolumeHierarchyArray[j +  1] = 16.0F;
				boundingVolumeHierarchyArray[j +  2] = depth;
				boundingVolumeHierarchyArray[j +  3] = next;
				boundingVolumeHierarchyArray[j +  4] = leafNode.getMinimum().x;
				boundingVolumeHierarchyArray[j +  5] = leafNode.getMinimum().y;
				boundingVolumeHierarchyArray[j +  6] = leafNode.getMinimum().z;
				boundingVolumeHierarchyArray[j +  7] = leafNode.getMaximum().x;
				boundingVolumeHierarchyArray[j +  8] = leafNode.getMaximum().y;
				boundingVolumeHierarchyArray[j +  9] = leafNode.getMaximum().z;
				boundingVolumeHierarchyArray[j + 10] = leafNode.getTriangles().size();
				
				for(int k = 0; k < leafNode.getTriangles().size(); k++) {
					boundingVolumeHierarchyArray[j + 11 + k] = leafNode.getTriangles().get(k).getOffset();
				}
				
				j += 16;
			} else if(node instanceof TreeNode) {
				final TreeNode treeNode = TreeNode.class.cast(node);
				
				final int depth = treeNode.getDepth();
				
				int next = -1;
				int leftIndex = -1;
				int rightIndex = -1;
				
				for(int k = i + 1; k < nodes.size(); k++) {
					final Node node0 = nodes.get(k);
					
					if(node0.getDepth() <= depth) {
						next = offsets[k];
						
						break;
					}
				}
				
				for(int k = i + 1, l = 0; k < nodes.size() && l < 2; k++) {
					final Node node0 = nodes.get(k);
					
					if(node0.getDepth() == depth + 1) {
						if(l == 0) {
							leftIndex = offsets[k];
						} else if(l == 1) {
							rightIndex = offsets[k];
						}
						
						l++;
					}
				}
				
				boundingVolumeHierarchyArray[j +  0] = 1.0F;
				boundingVolumeHierarchyArray[j +  1] = 16.0F;
				boundingVolumeHierarchyArray[j +  2] = depth;
				boundingVolumeHierarchyArray[j +  3] = next;
				boundingVolumeHierarchyArray[j +  4] = treeNode.getMinimum().x;
				boundingVolumeHierarchyArray[j +  5] = treeNode.getMinimum().y;
				boundingVolumeHierarchyArray[j +  6] = treeNode.getMinimum().z;
				boundingVolumeHierarchyArray[j +  7] = treeNode.getMaximum().x;
				boundingVolumeHierarchyArray[j +  8] = treeNode.getMaximum().y;
				boundingVolumeHierarchyArray[j +  9] = treeNode.getMaximum().z;
				boundingVolumeHierarchyArray[j + 10] = leftIndex;
				boundingVolumeHierarchyArray[j + 11] = rightIndex;
				
				j += 16;//11;
			}
		}
		
		return boundingVolumeHierarchyArray;
	}
	
	private static float[] doCompileShapes(final Scene scene) {
		final List<Float> floats = new ArrayList<>();
		
		for(final Shape shape : scene.getShapes()) {
			final float[] floatArray = shape.toFloatArray();
			
			for(final float value : floatArray) {
				floats.add(Float.valueOf(value));
			}
		}
		
		final float[] floatArray = new float[floats.size()];
		
		for(int i = 0; i < floats.size(); i++) {
			floatArray[i] = floats.get(i).floatValue();
		}
		
		return floatArray;
	}
	
	private static float[] doCompileTextures(final Scene scene) {
		final List<Float> floats = new ArrayList<>();
		
		for(final Texture texture : scene.getTextures()) {
			final float[] floatArray = texture.toFloatArray();
			
			for(final float value : floatArray) {
				floats.add(Float.valueOf(value));
			}
		}
		
		final float[] floatArray = new float[floats.size()];
		
		for(int i = 0; i < floats.size(); i++) {
			floatArray[i] = floats.get(i).floatValue();
		}
		
		return floatArray;
	}
	
	private static int doSize(final Node node) {
		int size = 0;
		
		if(node instanceof LeafNode) {
			size += 16;
		} else if(node instanceof TreeNode) {
			final Optional<Node> left = TreeNode.class.cast(node).getLeft();
			final Optional<Node> right = TreeNode.class.cast(node).getRight();
			
			size += 16;
			
			if(left.isPresent()) {
				size += doSize(left.get());
			}
			
			if(right.isPresent()) {
				size += doSize(right.get());
			}
		}
		
		return size;
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
}