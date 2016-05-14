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
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;

//TODO: Add Javadocs.
public final class CompiledScene {
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_0 = 2;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_1 = 5;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_DEGREES = 8;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_U = 9;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_V = 10;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_SIZE = 11;
	
//	TODO: Add Javadocs.
	public static final int CHECKERBOARD_TEXTURE_TYPE = 1;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_DATA = 7;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_DEGREES = 2;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_HEIGHT = 4;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_U = 5;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_V = 6;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_RELATIVE_OFFSET_WIDTH = 3;
	
//	TODO: Add Javadocs.
	public static final int IMAGE_TEXTURE_TYPE = 3;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_A = 10;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_B = 13;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_C = 16;
	
//	TODO: Add Javadocs.
	public static final int PLANE_RELATIVE_OFFSET_SURFACE_NORMAL = 19;
	
//	TODO: Add Javadocs.
	public static final int PLANE_SIZE = 22;
	
//	TODO: Add Javadocs.
	public static final int PLANE_TYPE = 3;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_EMISSION = 2;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_MATERIAL = 5;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT = 8;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE = 9;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO = 6;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL = 7;
	
//	TODO: Add Javadocs.
	public static final int SHAPE_RELATIVE_OFFSET_TYPE = 0;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_RELATIVE_OFFSET_COLOR = 2;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_SIZE = 5;
	
//	TODO: Add Javadocs.
	public static final int SOLID_TEXTURE_TYPE = 2;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_RELATIVE_OFFSET_POSITION = 11;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_RELATIVE_OFFSET_RADIUS = 10;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_SIZE = 14;
	
//	TODO: Add Javadocs.
	public static final int SPHERE_TYPE = 1;
	
//	TODO: Add Javadocs.
	public static final int TEXTURE_RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	public static final int TEXTURE_RELATIVE_OFFSET_TYPE = 0;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_A = 10;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_B = 13;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_POINT_C = 16;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_A = 19;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_B = 22;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_C = 25;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_A = 28;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_B = 30;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_RELATIVE_OFFSET_UV_C = 32;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_SIZE = 34;
	
//	TODO: Add Javadocs.
	public static final int TRIANGLE_TYPE = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
					final int size = (int)(shapes0[index + SHAPE_RELATIVE_OFFSET_SIZE]);
					
					System.arraycopy(shapes0, index, shapes1, shapes1Offset, size);
					
					this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i] = shapes1Offset;
					
					shapes1Offset += size;
				}
				
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
			}
		}
		
		for(int i = 0, j = 0, k = 0; i < shapes0.length; i += j) {
			final int type = (int)(shapes0[i + SHAPE_RELATIVE_OFFSET_TYPE]);
			final int size = (int)(shapes0[i + SHAPE_RELATIVE_OFFSET_SIZE]);
			
			j = size;
			
			if(type != TRIANGLE_TYPE) {
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
					boundingVolumeHierarchyArray[j + 9 + k] = doGetOffset(leafNode.getTriangles().get(k), shapes);
				}
				
				j += 9.0F + leafNode.getTriangles().size();
			} else if(node instanceof TreeNode) {
				final TreeNode treeNode = TreeNode.class.cast(node);
				
				final int depth = treeNode.getDepth();
				
				int next = -1;
				int leftIndex = -1;
				
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
		final List<Texture> textures = scene.getTextures();
		
		for(final Shape shape : scene.getShapes()) {
			final float[] floatArray = doToFloatArray(shape, textures);
			
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
			final float[] floatArray = doToFloatArray(texture);
			
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
	
	private static float[] doToFloatArray(final Shape shape, final List<Texture> textures) {
		if(shape instanceof Plane) {
			return doToFloatArrayPlane(Plane.class.cast(shape), textures);
		} else if(shape instanceof Sphere) {
			return doToFloatArraySphere(Sphere.class.cast(shape), textures);
		} else if(shape instanceof Triangle) {
			return doToFloatArrayTriangle(Triangle.class.cast(shape), textures);
		} else {
			throw new IllegalArgumentException(String.format("The Shape provided is not supported: %s", shape));
		}
	}
	
	private static float[] doToFloatArray(final Texture texture) {
		if(texture instanceof CheckerboardTexture) {
			return doToFloatArrayCheckerboardTexture(CheckerboardTexture.class.cast(texture));
		} else if(texture instanceof ImageTexture) {
			return doToFloatArrayImageTexture(ImageTexture.class.cast(texture));
		} else if(texture instanceof SolidTexture) {
			return doToFloatArraySolidTexture(SolidTexture.class.cast(texture));
		} else {
			throw new IllegalArgumentException(String.format("The Texture provided is not supported: %s", texture));
		}
	}
	
	private static float[] doToFloatArrayCheckerboardTexture(final CheckerboardTexture checkerboardTexture) {
		return new float[] {
			CHECKERBOARD_TEXTURE_TYPE,
			CHECKERBOARD_TEXTURE_SIZE,
			checkerboardTexture.getColor0().r,
			checkerboardTexture.getColor0().g,
			checkerboardTexture.getColor0().b,
			checkerboardTexture.getColor1().r,
			checkerboardTexture.getColor1().g,
			checkerboardTexture.getColor1().b,
			checkerboardTexture.getDegrees(),
			checkerboardTexture.getScaleU(),
			checkerboardTexture.getScaleV()
		};
	}
	
	private static float[] doToFloatArrayImageTexture(final ImageTexture imageTexture) {
		final int size = 7 + imageTexture.getData().length;
		
		final float[] floatArray = new float[size];
		
		floatArray[0] = IMAGE_TEXTURE_TYPE;
		floatArray[1] = size;
		floatArray[2] = imageTexture.getDegrees();
		floatArray[3] = imageTexture.getWidth();
		floatArray[4] = imageTexture.getHeight();
		floatArray[5] = imageTexture.getScaleU();
		floatArray[6] = imageTexture.getScaleV();
		
		final float[] data = imageTexture.getData();
		
		for(int i = 0; i < data.length; i++) {
			floatArray[i + 7] = data[i];
		}
		
		return floatArray;
	}
	
	private static float[] doToFloatArrayPlane(final Plane plane, final List<Texture> textures) {
		return new float[] {
			PLANE_TYPE,
			PLANE_SIZE,
			plane.getEmission().r,
			plane.getEmission().g,
			plane.getEmission().b,
			plane.getMaterial().ordinal(),
			doGetOffset(plane.getTextureAlbedo(), textures),
			doGetOffset(plane.getTextureNormal(), textures),
			plane.getPerlinNoiseAmount(),
			plane.getPerlinNoiseScale(),
			plane.getA().x,
			plane.getA().y,
			plane.getA().z,
			plane.getB().x,
			plane.getB().y,
			plane.getB().z,
			plane.getC().x,
			plane.getC().y,
			plane.getC().z,
			plane.getSurfaceNormal().x,
			plane.getSurfaceNormal().y,
			plane.getSurfaceNormal().z
		};
	}
	
	private static float[] doToFloatArraySolidTexture(final SolidTexture solidTexture) {
		return new float[] {
			SOLID_TEXTURE_TYPE,
			SOLID_TEXTURE_SIZE,
			solidTexture.getColor().r,
			solidTexture.getColor().g,
			solidTexture.getColor().b
		};
	}
	
	private static float[] doToFloatArraySphere(final Sphere sphere, final List<Texture> textures) {
		return new float[] {
			SPHERE_TYPE,
			SPHERE_SIZE,
			sphere.getEmission().r,
			sphere.getEmission().g,
			sphere.getEmission().b,
			sphere.getMaterial().ordinal(),
			doGetOffset(sphere.getTextureAlbedo(), textures),
			doGetOffset(sphere.getTextureNormal(), textures),
			sphere.getPerlinNoiseAmount(),
			sphere.getPerlinNoiseScale(),
			sphere.getRadius(),
			sphere.getPosition().x,
			sphere.getPosition().y,
			sphere.getPosition().z
		};
	}
	
	private static float[] doToFloatArrayTriangle(final Triangle triangle, final List<Texture> textures) {
		return new float[] {
			TRIANGLE_TYPE,
			TRIANGLE_SIZE,
			triangle.getEmission().r,
			triangle.getEmission().g,
			triangle.getEmission().b,
			triangle.getMaterial().ordinal(),
			doGetOffset(triangle.getTextureAlbedo(), textures),
			doGetOffset(triangle.getTextureNormal(), textures),
			triangle.getPerlinNoiseAmount(),
			triangle.getPerlinNoiseScale(),
			triangle.getA().position.x,
			triangle.getA().position.y,
			triangle.getA().position.z,
			triangle.getB().position.x,
			triangle.getB().position.y,
			triangle.getB().position.z,
			triangle.getC().position.x,
			triangle.getC().position.y,
			triangle.getC().position.z,
			triangle.getA().normal.x,
			triangle.getA().normal.y,
			triangle.getA().normal.z,
			triangle.getB().normal.x,
			triangle.getB().normal.y,
			triangle.getB().normal.z,
			triangle.getC().normal.x,
			triangle.getC().normal.y,
			triangle.getC().normal.z,
			triangle.getA().textureCoordinates.x,
			triangle.getA().textureCoordinates.y,
			triangle.getB().textureCoordinates.x,
			triangle.getB().textureCoordinates.y,
			triangle.getC().textureCoordinates.x,
			triangle.getC().textureCoordinates.y
		};
	}
	
	private static int doGetOffset(final Shape shape, final List<Shape> shapes) {
		for(int i = 0, j = 0; i < shapes.size(); i++) {
			final Shape shape0 = shapes.get(i);
			
			if(shape.equals(shape0)) {
				return j;
			}
			
			j += doSize(shape0);
		}
		
		throw new IllegalArgumentException(String.format("No such Shape found: %s", shape));
	}
	
	private static int doGetOffset(final Texture texture, final List<Texture> textures) {
		for(int i = 0, j = 0; i < textures.size(); i++) {
			final Texture texture0 = textures.get(i);
			
			if(texture.equals(texture0)) {
				return j;
			}
			
			j += doSize(texture0);
		}
		
		throw new IllegalArgumentException(String.format("No such Texture found: %s", texture));
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
	
	private static int doSize(final Shape shape) {
		if(shape instanceof Plane) {
			return PLANE_SIZE;
		} else if(shape instanceof Sphere) {
			return SPHERE_SIZE;
		} else if(shape instanceof Triangle) {
			return TRIANGLE_SIZE;
		} else {
			throw new IllegalArgumentException(String.format("The Shape provided is not supported: %s", shape));
		}
	}
	
	private static int doSize(final Texture texture) {
		if(texture instanceof CheckerboardTexture) {
			return CHECKERBOARD_TEXTURE_SIZE;
		} else if(texture instanceof ImageTexture) {
			return 7 + ImageTexture.class.cast(texture).getData().length;
		} else if(texture instanceof SolidTexture) {
			return SOLID_TEXTURE_SIZE;
		} else {
			throw new IllegalArgumentException(String.format("The Texture provided is not supported: %s", texture));
		}
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
				shapeOffsets[index++] = doGetOffset(shape, shapes);
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