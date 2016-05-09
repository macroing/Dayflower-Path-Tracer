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
	private final int[] shapeOffsets;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CompiledScene(final float[] boundingVolumeHierarchy, final float[] camera, final float[] shapes, final float[] textures, final int[] shapeOffsets) {
		this.boundingVolumeHierarchy = Objects.requireNonNull(boundingVolumeHierarchy, "boundingVolumeHierarchy == null");
		this.camera = Objects.requireNonNull(camera, "camera == null");
		this.shapes = Objects.requireNonNull(shapes, "shapes == null");
		this.textures = Objects.requireNonNull(textures, "textures == null");
		this.shapeOffsets = Objects.requireNonNull(shapeOffsets, "shapeOffsets == null");
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
	
//	TODO: Add Javadocs.
	public int[] getShapeOffsets() {
		return this.shapeOffsets;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static CompiledScene compile(final Camera camera, final Scene scene) {
		return new CompiledScene(doCompileBoundingVolumeHierarchy(scene), camera.getArray(), doCompileShapes(scene), doCompileTextures(scene), doCompileShapeOffsets(scene)).doReorderShapes();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private CompiledScene doReorderShapes() {
		final float[] shapes0 = this.shapes;
		final float[] shapes1 = new float[shapes0.length];
		
		int boundingVolumeHierarchyOffset = 0;
		int shapes1Offset = 0;
		
		while(boundingVolumeHierarchyOffset != -1) {
			final int type = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset]);
			
			if(type == 1) {
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
			} else if(type == 2) {
				for(int i = 0; i < (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]); i++) {
					final int index = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
					final int size = (int)(shapes0[index + Shape.RELATIVE_OFFSET_SIZE]);
					
					System.arraycopy(shapes0, index, shapes1, shapes1Offset, size);
					
					this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i] = shapes1Offset;
					
					shapes1Offset += size;
				}
				
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
			}
		}
		
		for(int i = 0, j = 0, k = 0; i < shapes0.length; i += j) {
			final int type = (int)(shapes0[i + Shape.RELATIVE_OFFSET_TYPE]);
			final int size = (int)(shapes0[i + Shape.RELATIVE_OFFSET_SIZE]);
			
			j = size;
			
			if(type != Triangle.TYPE) {
				System.arraycopy(shapes0, i, shapes1, shapes1Offset, size);
				
				this.shapeOffsets[k] = shapes1Offset;
				
				shapes1Offset += size;
				
				k++;
			}
		}
		
		System.arraycopy(shapes1, 0, shapes0, 0, shapes1.length);
		
		return this;
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
				
				boundingVolumeHierarchyArray[j + 0] = 2.0F;
				boundingVolumeHierarchyArray[j + 1] = next;
				boundingVolumeHierarchyArray[j + 2] = leafNode.getMinimum().x;
				boundingVolumeHierarchyArray[j + 3] = leafNode.getMinimum().y;
				boundingVolumeHierarchyArray[j + 4] = leafNode.getMinimum().z;
				boundingVolumeHierarchyArray[j + 5] = leafNode.getMaximum().x;
				boundingVolumeHierarchyArray[j + 6] = leafNode.getMaximum().y;
				boundingVolumeHierarchyArray[j + 7] = leafNode.getMaximum().z;
				boundingVolumeHierarchyArray[j + 8] = leafNode.getTriangles().size();
				
				for(int k = 0; k < leafNode.getTriangles().size(); k++) {
					boundingVolumeHierarchyArray[j + 9 + k] = leafNode.getTriangles().get(k).getOffset();
				}
				
				j += 9.0F + leafNode.getTriangles().size();
			} else if(node instanceof TreeNode) {
				final TreeNode treeNode = TreeNode.class.cast(node);
				
				final int depth = treeNode.getDepth();
				
				int next = -1;
				int leftIndex = -1;
//				int rightIndex = -1;
				
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
//							rightIndex = offsets[k];
						}
						
						l++;
					}
				}
				
				boundingVolumeHierarchyArray[j + 0] = 1.0F;
				boundingVolumeHierarchyArray[j + 1] = next;
				boundingVolumeHierarchyArray[j + 2] = treeNode.getMinimum().x;
				boundingVolumeHierarchyArray[j + 3] = treeNode.getMinimum().y;
				boundingVolumeHierarchyArray[j + 4] = treeNode.getMinimum().z;
				boundingVolumeHierarchyArray[j + 5] = treeNode.getMaximum().x;
				boundingVolumeHierarchyArray[j + 6] = treeNode.getMaximum().y;
				boundingVolumeHierarchyArray[j + 7] = treeNode.getMaximum().z;
				boundingVolumeHierarchyArray[j + 8] = leftIndex;
				
				j += 9;
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
			size += 9 + LeafNode.class.cast(node).getTriangles().size();
		} else if(node instanceof TreeNode) {
			final Optional<Node> left = TreeNode.class.cast(node).getLeft();
			final Optional<Node> right = TreeNode.class.cast(node).getRight();
			
			size += 9;
			
			if(left.isPresent()) {
				size += doSize(left.get());
			}
			
			if(right.isPresent()) {
				size += doSize(right.get());
			}
		}
		
		return size;
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
				shapeOffsets[index++] = shape.getOffset();
			}
		}
		
		return shapeOffsets;
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