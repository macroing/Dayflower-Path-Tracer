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

import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;

/**
 * An extension of the {@code AbstractKernel} class that performs Path Tracing.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class RendererKernel extends AbstractKernel {
	private static final float GAMMA = 2.2F;
	@SuppressWarnings("unused")
	private static final float GAMMA_RECIPROCAL = 1.0F / GAMMA;
	private static final float MAXIMUM_COLOR_COMPONENT = 255.0F;
	private static final float MAXIMUM_COLOR_COMPONENT_RECIPROCAL = 1.0F / MAXIMUM_COLOR_COMPONENT;
	private static final float PHONG_EXPONENT = 20.0F;
	private static final float PHONE_EXPONENT_PLUS_ONE_RECIPROCAL = 1.0F / (PHONG_EXPONENT + 1.0F);
	private static final float REFRACTIVE_INDEX_0 = 1.0F;
	private static final float REFRACTIVE_INDEX_1 = 1.5F;
	private static final float REFRACTIVE_INDEX_A = REFRACTIVE_INDEX_1 - REFRACTIVE_INDEX_0;
	private static final float REFRACTIVE_INDEX_B = REFRACTIVE_INDEX_1 + REFRACTIVE_INDEX_0;
	private static final float REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1 = REFRACTIVE_INDEX_0 / REFRACTIVE_INDEX_1;
	private static final float REFRACTIVE_INDEX_1_DIVIDED_BY_REFRACTIVE_INDEX_0 = REFRACTIVE_INDEX_1 / REFRACTIVE_INDEX_0;
	private static final float REFRACTIVE_INDEX_R0 = (REFRACTIVE_INDEX_A * REFRACTIVE_INDEX_A) / (REFRACTIVE_INDEX_B * REFRACTIVE_INDEX_B);
	private static final int MATERIAL_CLEAR_COAT = 0;
	private static final int MATERIAL_GLASS = 1;
	private static final int MATERIAL_LAMBERTIAN_DIFFUSE = 2;
	private static final int MATERIAL_MIRROR = 3;
	private static final int MATERIAL_PHONG_METAL = 4;
	private static final int RELATIVE_OFFSET_INTERSECTION_DISTANCE = 0;
	private static final int RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET = 1;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT = 2;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL = 5;
	private static final int RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES = 8;
	private static final int RELATIVE_OFFSET_RAY_DIRECTION = 3;
	private static final int RELATIVE_OFFSET_RAY_ORIGIN = 0;
	private static final int SIZE_INTERSECTION = 10;
	private static final int SIZE_PIXEL = 4;
	private static final int SIZE_RAY = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final boolean isResettingFully;
	private byte[] pixels;
	private final Camera camera;
	private final float orthoNormalBasisUX;
	private final float orthoNormalBasisUY;
	private final float orthoNormalBasisUZ;
	private final float orthoNormalBasisVX;
	private final float orthoNormalBasisVY;
	private final float orthoNormalBasisVZ;
	private final float orthoNormalBasisWX;
	private final float orthoNormalBasisWY;
	private final float orthoNormalBasisWZ;
	private final float sunDirectionX;
	private final float sunDirectionY;
	private final float sunDirectionZ;
	private final float theta;
	private final float zenithRelativeLuminance;
	private final float zenithX;
	private final float zenithY;
	private final float[] accumulatedPixelColors;
	@Constant
	private final float[] boundingVolumeHierarchy;
	private final float[] cameraArray;
	private final float[] currentPixelColors;
	private final float[] intersections;
	private final float[] perezRelativeLuminance;
	private final float[] perezX;
	private final float[] perezY;
	private float[] rays;
	@Constant
	private final float[] shapes;
	private final float[] temporaryColors;
	@Constant
	private final float[] textures;
	private int depthMaximum = 1;
	private int depthRussianRoulette = 5;
	private int shapeOffsetsLength;
	private final int width;
	@Constant
	private final int[] permutations0 = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
	@Constant
	private final int[] permutations1 = new int[512];
	private final int[] shapeOffsets;
	private final long[] subSamples;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If either {@code camera} or {@code scene} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param isResettingFully {@code true} if full resetting should be performed, {@code false} otherwise
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param scene the {@link Scene} to use
	 * @throws NullPointerException thrown if, and only if, either {@code camera} or {@code scene} are {@code null}
	 */
	public RendererKernel(final boolean isResettingFully, final int width, final int height, final Camera camera, final Scene scene) {
		final CompiledScene compiledScene = CompiledScene.compile(camera, scene);
		
		final Sky sky = new Sky();
		
		this.orthoNormalBasisUX = sky.getOrthoNormalBasis().u.x;
		this.orthoNormalBasisUY = sky.getOrthoNormalBasis().u.y;
		this.orthoNormalBasisUZ = sky.getOrthoNormalBasis().u.z;
		this.orthoNormalBasisVX = sky.getOrthoNormalBasis().v.x;
		this.orthoNormalBasisVY = sky.getOrthoNormalBasis().v.y;
		this.orthoNormalBasisVZ = sky.getOrthoNormalBasis().v.z;
		this.orthoNormalBasisWX = sky.getOrthoNormalBasis().w.x;
		this.orthoNormalBasisWY = sky.getOrthoNormalBasis().w.y;
		this.orthoNormalBasisWZ = sky.getOrthoNormalBasis().w.z;
		
		this.isResettingFully = isResettingFully;
		this.width = width;
		this.camera = camera;
		this.boundingVolumeHierarchy = compiledScene.getBoundingVolumeHierarchy();
		this.cameraArray = compiledScene.getCamera();
		this.shapes = compiledScene.getShapes();
		this.textures = compiledScene.getTextures();
		this.accumulatedPixelColors = new float[width * height * 3];
		this.currentPixelColors = new float[width * height * 3];
		this.intersections = new float[width * height * SIZE_INTERSECTION];
		this.rays = new float[width * height * SIZE_RAY];
		this.temporaryColors = new float[width * height * 3];
		this.shapeOffsets = compiledScene.getShapeOffsets();
		this.shapeOffsetsLength = this.shapeOffsets.length;
		this.subSamples = new long[width * height];
		this.sunDirectionX = sky.getSunDirection().x;
		this.sunDirectionY = sky.getSunDirection().y;
		this.sunDirectionZ = sky.getSunDirection().z;
		this.theta = sky.getTheta();
		this.zenithRelativeLuminance = sky.getZenithRelativeLuminance();
		this.zenithX = sky.getZenithX();
		this.zenithY = sky.getZenithY();
		this.perezRelativeLuminance = sky.getPerezRelativeLuminance();
		this.perezX = sky.getPerezX();
		this.perezY = sky.getPerezY();
		
		for(int i = 0; i < this.permutations0.length; i++) {
			this.permutations1[i] = this.permutations0[i];
			this.permutations1[i + this.permutations0.length] = this.permutations0[i];
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@code byte} array with the pixels.
	 * 
	 * @return the {@code byte} array with the pixels
	 */
	public byte[] getPixels() {
		return this.pixels;
	}
	
	/**
	 * Returns the maximum depth for path termination.
	 * 
	 * @return the maximum depth for path termination
	 */
	public int getDepthMaximum() {
		return this.depthMaximum;
	}
	
	/**
	 * Returns the depth used for Russian Roulette path termination.
	 * 
	 * @return the depth used for Russian Roulette path termination
	 */
	public int getDepthRussianRoulette() {
		return this.depthRussianRoulette;
	}
	
	/**
	 * Returns the optional {@link Camera} instance assigned to this {@code RendererKernel} instance.
	 * 
	 * @return the optional {@code Camera} instance assigned to this {@code RendererKernel} instance
	 */
	public Optional<Camera> getCamera() {
		return Optional.ofNullable(this.camera);
	}
	
	/**
	 * Compiles this {@code RendererKernel} instance.
	 * <p>
	 * Returns itself for method chaining.
	 * <p>
	 * If {@code pixels} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param pixels a {@code byte} array with the pixels
	 * @param width the width to use
	 * @param height the height to use
	 * @return itself for method chaining
	 * @throws NullPointerException thrown if, and only if, {@code pixels} is {@code null}
	 */
	public RendererKernel compile(final byte[] pixels, final int width, final int height) {
		this.pixels = Objects.requireNonNull(pixels, "pixels == null");
		
		setExecutionMode(EXECUTION_MODE.GPU);
		setExplicit(true);
		setSeed(System.nanoTime(), width * height);
		
		updateTables();
		
		put(this.pixels);
		put(this.accumulatedPixelColors);
		put(this.boundingVolumeHierarchy);
		put(this.cameraArray);
		put(this.currentPixelColors);
		put(this.intersections);
		put(this.perezRelativeLuminance);
		put(this.perezX);
		put(this.perezY);
		put(this.rays);
		put(this.shapes);
		put(this.temporaryColors);
		put(this.textures);
		put(this.permutations0);
		put(this.permutations1);
		put(this.shapeOffsets);
		put(this.subSamples);
		
		return this;
	}
	
	/**
	 * Resets this {@code RendererKernel} instance.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @return itself for method chaining
	 */
	public RendererKernel reset() {
		for(int i = 0; i < this.subSamples.length; i++) {
			if(this.isResettingFully) {
				final int pixelIndex = i * 3;
				
				this.accumulatedPixelColors[pixelIndex + 0] = 0.0F;
				this.accumulatedPixelColors[pixelIndex + 1] = 0.0F;
				this.accumulatedPixelColors[pixelIndex + 2] = 0.0F;
				this.subSamples[i] = 0L;
			} else {
				this.subSamples[i] = 1L;
			}
		}
		
		getCamera().ifPresent(camera -> {
			camera.update();
			
			put(this.cameraArray);
		});
		
		if(this.isResettingFully) {
			put(this.accumulatedPixelColors);
		}
		
		put(this.subSamples);
		
		return this;
	}
	
	/**
	 * Performs the Path Tracing.
	 */
	@Override
	public void run() {
		final int pixelIndex = getGlobalId();
		
		doCreatePrimaryRay(pixelIndex);
		doPathTracing(pixelIndex);
		doCalculateColor(pixelIndex);
	}
	
	/**
	 * Sets the maximum depth to be used for path termination.
	 * 
	 * @param depthMaximum the maximum depth
	 */
	public void setDepthMaximum(final int depthMaximum) {
		this.depthMaximum = depthMaximum;
	}
	
	/**
	 * Sets the depth to be used for Russian Roulette path termination.
	 * 
	 * @param depthRussianRoulette the depth to use
	 */
	public void setDepthRussianRoulette(final int depthRussianRoulette) {
		this.depthRussianRoulette = depthRussianRoulette;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private float doIntersect(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
		final int type = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_TYPE]);
		
		if(type == CompiledScene.PLANE_TYPE) {
			return doIntersectPlane(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		} else if(type == CompiledScene.SPHERE_TYPE) {
			return doIntersectSphere(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		} else if(type == CompiledScene.TRIANGLE_TYPE) {
			return doIntersectTriangle(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		}
		
		return INFINITY;
	}
	
	private float doIntersectPlane(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final int offsetSurfaceNormal = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL;
		
//		Retrieve the surface normal of the plane:
		final float surfaceNormalX = this.shapes[offsetSurfaceNormal];
		final float surfaceNormalY = this.shapes[offsetSurfaceNormal + 1];
		final float surfaceNormalZ = this.shapes[offsetSurfaceNormal + 2];
		
//		Calculate the dot product between the surface normal and the ray direction:
		final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
		
//		TODO: Write explanation!
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
//			TODO: Write explanation!
			final int offsetA = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A;
			
//			TODO: Write explanation!
			final float aX = this.shapes[offsetA];
			final float aY = this.shapes[offsetA + 1];
			final float aZ = this.shapes[offsetA + 2];
			
//			TODO: Write explanation!
			final float distance = ((aX - originX) * surfaceNormalX + (aY - originY) * surfaceNormalY + (aZ - originZ) * surfaceNormalZ) / dotProduct;
			
//			TODO: Write explanation!
			if(distance > EPSILON) {
				return distance;
			}
		}
		
		return INFINITY;
	}
	
	private float doIntersectSphere(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final int offsetPosition = shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION;
		
//		TODO: Write explanation!
		final float positionX = this.shapes[offsetPosition];
		final float positionY = this.shapes[offsetPosition + 1];
		final float positionZ = this.shapes[offsetPosition + 2];
		
//		TODO: Write explanation!
		final float radius = this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_RADIUS];
		
//		TODO: Write explanation!
		final float x = positionX - originX;
		final float y = positionY - originY;
		final float z = positionZ - originZ;
		
//		TODO: Write explanation!
		final float b = x * directionX + y * directionY + z * directionZ;
		
//		TODO: Write explanation!
		final float determinant0 = b * b - (x * x + y * y + z * z) + radius * radius;
		
//		TODO: Write explanation!
		if(determinant0 >= 0.0F) {
//			TODO: Write explanation!
			final float determinant1 = sqrt(determinant0);
			
//			TODO: Write explanation!
			final float distance1 = b - determinant1;
			
//			TODO: Write explanation!
			if(distance1 > EPSILON) {
				return distance1;
			}
			
//			TODO: Write explanation!
			final float distance2 = b + determinant1;
			
//			TODO: Write explanation!
			if(distance2 > EPSILON) {
				return distance2;
			}
		}
		
		return INFINITY;
	}
	
	private float doIntersectTriangle(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final int offsetA = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A;
		final int offsetB = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B;
		final int offsetC = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C;
		
//		Retrieve point A of the triangle:
		final float aX = this.shapes[offsetA];
		final float aY = this.shapes[offsetA + 1];
		final float aZ = this.shapes[offsetA + 2];
		
//		Retrieve point B of the triangle:
		final float bX = this.shapes[offsetB];
		final float bY = this.shapes[offsetB + 1];
		final float bZ = this.shapes[offsetB + 2];
		
//		Retrieve point C of the triangle:
		final float cX = this.shapes[offsetC];
		final float cY = this.shapes[offsetC + 1];
		final float cZ = this.shapes[offsetC + 2];
		
//		Calculate the first edge between the points A and B:
		final float edge0X = bX - aX;
		final float edge0Y = bY - aY;
		final float edge0Z = bZ - aZ;
		
//		Calculate the second edge between the points A and C:
		final float edge1X = cX - aX;
		final float edge1Y = cY - aY;
		final float edge1Z = cZ - aZ;
		
//		TODO: Write explanation!
		final float v0X = directionY * edge1Z - directionZ * edge1Y;
		final float v0Y = directionZ * edge1X - directionX * edge1Z;
		final float v0Z = directionX * edge1Y - directionY * edge1X;
		
//		TODO: Write explanation!
		final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;
		
//		TODO: Write explanation!
		float t = INFINITY;
		
//		TODO: Write explanation!
		if(!(determinant > -EPSILON && determinant < EPSILON)) {
//			TODO: Write explanation!
			final float determinantReciprocal = 1.0F / determinant;
			
//			TODO: Write explanation!
			final float v1X = originX - aX;
			final float v1Y = originY - aY;
			final float v1Z = originZ - aZ;
			
//			TODO: Write explanation!
			final float u = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
			
//			TODO: Write explanation!
			if(u >= 0.0F && u <= 1.0F) {
//				TODO: Write explanation!
				final float v2X = v1Y * edge0Z - v1Z * edge0Y;
				final float v2Y = v1Z * edge0X - v1X * edge0Z;
				final float v2Z = v1X * edge0Y - v1Y * edge0X;
				
//				TODO: Write explanation!
				final float v = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
				
//				TODO: Write explanation!
				t = v >= 0.0F && u + v <= 1.0F ? (edge1X * v2X + edge1Y * v2Y + edge1Z * v2Z) * determinantReciprocal : EPSILON;
				t = t > EPSILON ? t : INFINITY;
			}
		}
		
		return t;
	}
	
	private float doPerlinNoise(final float x, final float y, final float z) {
//		TODO: Write explanation!
		final int x0 = (int)(floor(x)) & 0xFF;
		final int y0 = (int)(floor(y)) & 0xFF;
		final int z0 = (int)(floor(z)) & 0xFF;
		
//		TODO: Write explanation!
		final float x1 = x - floor(x);
		final float y1 = y - floor(y);
		final float z1 = z - floor(z);
		
//		TODO: Write explanation!
		final float u = x1 * x1 * x1 * (x1 * (x1 * 6.0F - 15.0F) + 10.0F);
		final float v = y1 * y1 * y1 * (y1 * (y1 * 6.0F - 15.0F) + 10.0F);
		final float w = z1 * z1 * z1 * (z1 * (z1 * 6.0F - 15.0F) + 10.0F);
		
//		TODO: Write explanation!
		final int a0 = this.permutations1[x0] + y0;
		final int a1 = this.permutations1[a0] + z0;
		final int a2 = this.permutations1[a0 + 1] + z0;
		final int b0 = this.permutations1[x0 + 1] + y0;
		final int b1 = this.permutations1[b0] + z0;
		final int b2 = this.permutations1[b0 + 1] + z0;
		final int hash0 = this.permutations1[a1] & 15;
		final int hash1 = this.permutations1[b1] & 15;
		final int hash2 = this.permutations1[a2] & 15;
		final int hash3 = this.permutations1[b2] & 15;
		final int hash4 = this.permutations1[a1 + 1] & 15;
		final int hash5 = this.permutations1[b1 + 1] & 15;
		final int hash6 = this.permutations1[a2 + 1] & 15;
		final int hash7 = this.permutations1[b2 + 1] & 15;
		
//		TODO: Write explanation!
		final float gradient0U = hash0 < 8 || hash0 == 12 || hash0 == 13 ? x1 : y1;
		final float gradient0V = hash0 < 4 || hash0 == 12 || hash0 == 13 ? y1 : z1;
		final float gradient0 = ((hash0 & 1) == 0 ? gradient0U : -gradient0U) + ((hash0 & 2) == 0 ? gradient0V : -gradient0V);
		final float gradient1U = hash1 < 8 || hash1 == 12 || hash1 == 13 ? x1 - 1.0F : y1;
		final float gradient1V = hash1 < 4 || hash1 == 12 || hash1 == 13 ? y1 : z1;
		final float gradient1 = ((hash1 & 1) == 0 ? gradient1U : -gradient1U) + ((hash1 & 2) == 0 ? gradient1V : -gradient1V);
		final float gradient2U = hash2 < 8 || hash2 == 12 || hash2 == 13 ? x1 : y1 - 1.0F;
		final float gradient2V = hash2 < 4 || hash2 == 12 || hash2 == 13 ? y1 - 1.0F : z1;
		final float gradient2 = ((hash2 & 1) == 0 ? gradient2U : -gradient2U) + ((hash2 & 2) == 0 ? gradient2V : -gradient2V);
		final float gradient3U = hash3 < 8 || hash3 == 12 || hash3 == 13 ? x1 - 1.0F : y1 - 1.0F;
		final float gradient3V = hash3 < 4 || hash3 == 12 || hash3 == 13 ? y1 - 1.0F : z1;
		final float gradient3 = ((hash3 & 1) == 0 ? gradient3U : -gradient3U) + ((hash3 & 2) == 0 ? gradient3V : -gradient3V);
		final float gradient4U = hash4 < 8 || hash4 == 12 || hash4 == 13 ? x1 : y1;
		final float gradient4V = hash4 < 4 || hash4 == 12 || hash4 == 13 ? y1 : z1 - 1.0F;
		final float gradient4 = ((hash4 & 1) == 0 ? gradient4U : -gradient4U) + ((hash4 & 2) == 0 ? gradient4V : -gradient4V);
		final float gradient5U = hash5 < 8 || hash5 == 12 || hash5 == 13 ? x1 - 1.0F : y1;
		final float gradient5V = hash5 < 4 || hash5 == 12 || hash5 == 13 ? y1 : z1 - 1.0F;
		final float gradient5 = ((hash5 & 1) == 0 ? gradient5U : -gradient5U) + ((hash5 & 2) == 0 ? gradient5V : -gradient5V);
		final float gradient6U = hash6 < 8 || hash6 == 12 || hash6 == 13 ? x1 : y1 - 1.0F;
		final float gradient6V = hash6 < 4 || hash6 == 12 || hash6 == 13 ? y1 - 1.0F : z1 - 1.0F;
		final float gradient6 = ((hash6 & 1) == 0 ? gradient6U : -gradient6U) + ((hash6 & 2) == 0 ? gradient6V : -gradient6V);
		final float gradient7U = hash7 < 8 || hash7 == 12 || hash7 == 13 ? x1 - 1.0F : y1 - 1.0F;
		final float gradient7V = hash7 < 4 || hash7 == 12 || hash7 == 13 ? y1 - 1.0F : z1 - 1.0F;
		final float gradient7 = ((hash7 & 1) == 0 ? gradient7U : -gradient7U) + ((hash7 & 2) == 0 ? gradient7V : -gradient7V);
		
//		TODO: Write explanation!
		final float lerp0 = gradient0 + u * (gradient1 - gradient0);
		final float lerp1 = gradient2 + u * (gradient3 - gradient2);
		final float lerp2 = gradient4 + u * (gradient5 - gradient4);
		final float lerp3 = gradient6 + u * (gradient7 - gradient6);
		final float lerp4 = lerp0 + v * (lerp1 - lerp0);
		final float lerp5 = lerp2 + v * (lerp3 - lerp2);
		final float lerp6 = lerp4 + w * (lerp5 - lerp4);
		
		return lerp6;
	}
	
	private void doCalculateColor(final int pixelIndex) {
//		Retrieve the offset to the pixels array:
		final int pixelsOffset = pixelIndex * SIZE_PIXEL;
		
//		TODO: Write explanation!
		final int pixelIndex0 = pixelIndex * 3;
		
//		TODO: Write explanation!
		final float subSample = this.subSamples[pixelIndex];
		
//		Multiply the 'normalized' accumulated pixel color component values with the current sub-sample count:
		this.accumulatedPixelColors[pixelIndex0] *= subSample;
		this.accumulatedPixelColors[pixelIndex0 + 1] *= subSample;
		this.accumulatedPixelColors[pixelIndex0 + 2] *= subSample;
		
//		Add the current pixel color component values to the accumulated pixel color component values:
		this.accumulatedPixelColors[pixelIndex0] += this.currentPixelColors[pixelIndex0];
		this.accumulatedPixelColors[pixelIndex0 + 1] += this.currentPixelColors[pixelIndex0 + 1];
		this.accumulatedPixelColors[pixelIndex0 + 2] += this.currentPixelColors[pixelIndex0 + 2];
		
//		Increment the current sub-sample count by one:
		this.subSamples[pixelIndex] += 1;
		
//		Retrieve the current sub-sample count and calculate its reciprocal (inverse), such that no division is needed further on:
		final float currentSubSamples = subSample + 1.0F;
		final float currentSubSamplesReciprocal = 1.0F / currentSubSamples;
		
//		Multiply the accumulated pixel color component values with the reciprocal of the current sub-sample count to 'normalize' it:
		this.accumulatedPixelColors[pixelIndex0] *= currentSubSamplesReciprocal;
		this.accumulatedPixelColors[pixelIndex0 + 1] *= currentSubSamplesReciprocal;
		this.accumulatedPixelColors[pixelIndex0 + 2] *= currentSubSamplesReciprocal;
		
//		Retrieve the 'normalized' accumulated pixel color component values again:
		float r = this.accumulatedPixelColors[pixelIndex0];
		float g = this.accumulatedPixelColors[pixelIndex0 + 1];
		float b = this.accumulatedPixelColors[pixelIndex0 + 2];
		
//		====================================================================================================
		
//		Exposure Adjustment:
//		--------------------
		
//-		r *= 2.0F;
//-		g *= 2.0F;
//-		b *= 2.0F;
		
//		====================================================================================================
		
//		Reinhard Tone Mapping and Gamma Correction #1:
//		----------------------------------------------
		
//		Perform Tone Mapping on the 'normalized' accumulated pixel color components:
//-		r = r / (r + 1.0F);
//-		g = g / (g + 1.0F);
//-		b = b / (b + 1.0F);
		
//		Perform Gamma Correction on the 'normalized' accumulated pixel color components:
//-		r = pow(r, GAMMA_RECIPROCAL);
//-		g = pow(g, GAMMA_RECIPROCAL);
//-		b = pow(b, GAMMA_RECIPROCAL);
		
//		====================================================================================================
		
//		Reinhard Tone Mapping and Gamma Correction #2:
//		----------------------------------------------
		
//		Set the exposure:
//-		final float exposure = 1.5F;
		
//		Perform Tone Mapping on the 'normalized' accumulated pixel color components:
//-		r *= exposure / (1.0F + r / exposure);
//-		g *= exposure / (1.0F + g / exposure);
//-		b *= exposure / (1.0F + b / exposure);
		
//		Perform Gamma Correction on the 'normalized' accumulated pixel color components:
//-		r = pow(r, GAMMA_RECIPROCAL);
//-		g = pow(g, GAMMA_RECIPROCAL);
//-		b = pow(b, GAMMA_RECIPROCAL);
		
//		====================================================================================================
		
//		Filmic Curve Tone Mapping and Gamma Correction:
//		-----------------------------------------------
		
//		Calculate the maximum pixel color component values:
		final float rMaximum = max(r - 0.004F, 0.0F);
		final float gMaximum = max(g - 0.004F, 0.0F);
		final float bMaximum = max(b - 0.004F, 0.0F);
		
//		Perform Tone Mapping and Gamma Correction:
		r = (rMaximum * (6.2F * rMaximum + 0.5F)) / (rMaximum * (6.2F * rMaximum + 1.7F) + 0.06F);
		g = (gMaximum * (6.2F * gMaximum + 0.5F)) / (gMaximum * (6.2F * gMaximum + 1.7F) + 0.06F);
		b = (bMaximum * (6.2F * bMaximum + 0.5F)) / (bMaximum * (6.2F * bMaximum + 1.7F) + 0.06F);
		
//		====================================================================================================
		
//		Linear Tone Mapping and Gamma Correction:
//		-----------------------------------------
		
//		Calculate the maximum component value of the 'normalized' accumulated pixel color component values:
//-		final float maximumComponentValue = max(r, max(g, b));
		
//		Check if the maximum component value is greater than 1.0:
//-		if(maximumComponentValue > 1.0F) {
//			Calculate the reciprocal of the maximum component value, such that no division is needed further on:
//-			final float maximumComponentValueReciprocal = 1.0F / maximumComponentValue;
			
//			Multiply the 'normalized' accumulated pixel color component values with the reciprocal of the maximum component value for Tone Mapping:
//-			r *= maximumComponentValueReciprocal;
//-			g *= maximumComponentValueReciprocal;
//-			b *= maximumComponentValueReciprocal;
//-		}
		
//		Perform Gamma Correction on the 'normalized' accumulated pixel color components:
//-		r = pow(r, GAMMA_RECIPROCAL);
//-		g = pow(g, GAMMA_RECIPROCAL);
//-		b = pow(b, GAMMA_RECIPROCAL);
		
//		====================================================================================================
		
//		Clamp the 'normalized' accumulated pixel color components to the range [0.0, 1.0]:
		r = min(max(r, 0.0F), 1.0F);
		g = min(max(g, 0.0F), 1.0F);
		b = min(max(b, 0.0F), 1.0F);
		
//		Multiply the 'normalized' accumulated pixel color components with 255.0, to lie in the range [0.0, 255.0], so they can be displayed:
		r *= 255.0F;
		g *= 255.0F;
		b *= 255.0F;
		
//		Update the pixels array with the actual color to display it:
		this.pixels[pixelsOffset + 0] = (byte)(b);
		this.pixels[pixelsOffset + 1] = (byte)(g);
		this.pixels[pixelsOffset + 2] = (byte)(r);
		this.pixels[pixelsOffset + 3] = (byte)(255);
	}
	
	private void doCalculateColorForSky(final int pixelIndex, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final float direction0X = directionX * this.orthoNormalBasisUX + directionY * this.orthoNormalBasisUY + directionZ * this.orthoNormalBasisUZ;
		final float direction0Y = directionX * this.orthoNormalBasisVX + directionY * this.orthoNormalBasisVY + directionZ * this.orthoNormalBasisVZ;
		final float direction0Z = directionX * this.orthoNormalBasisWX + directionY * this.orthoNormalBasisWY + directionZ * this.orthoNormalBasisWZ;
		
		if(direction0Z < 0.0F) {
//			TODO: Write explanation!
			final int pixelIndex0 = pixelIndex * 3;
			
//			TODO: Write explanation!
			this.temporaryColors[pixelIndex0] = 0.0F;
			this.temporaryColors[pixelIndex0 + 1] = 0.0F;
			this.temporaryColors[pixelIndex0 + 2] = 0.0F;
			
			return;
		}
		
		final float direction1X = direction0X;
		final float direction1Y = direction0Y;
		final float direction1Z = direction0Z < 0.0F ? -direction0Z : direction0Z;//direction0Z < 0.001F ? 0.001F : direction0Z;
		final float direction1LengthReciprocal = rsqrt(direction1X * direction1X + direction1Y * direction1Y + direction1Z * direction1Z);
		
		final float direction2X = direction1X * direction1LengthReciprocal;
		final float direction2Y = direction1Y * direction1LengthReciprocal;
		final float direction2Z = direction1Z * direction1LengthReciprocal;
		
//		TODO: Write explanation!
		final float x0 = direction2X;
		final float y0 = direction2Y;
		final float z0 = direction2Z;// < 0.0F ? -direction2Z : direction2Z;
		
//		TODO: Write explanation!
		final float x1 = x0;
		final float y1 = y0;
		final float z1 = max(z0, 0.001F);
		
//		TODO: Write explanation!
		final float lengthReciprocal = rsqrt(x1 * x1 + y1 * y1 + z1 * z1);
		
//		TODO: Write explanation!
		final float x2 = x1 * lengthReciprocal;
		final float y2 = y1 * lengthReciprocal;
		final float z2 = z1 * lengthReciprocal;
		
//		TODO: Write explanation!
		final float dotProduct = x2 * this.sunDirectionX + y2 * this.sunDirectionY + z2 * this.sunDirectionZ;
		
//		TODO: Write explanation!
		final float theta0 = this.theta;
		final float theta1 = acos(max(min(z2, 1.0F), -1.0F));
		
//		TODO: Write explanation!
		final float cosTheta0 = cos(theta0);
		final float cosTheta1 = cos(theta1);
		final float cosTheta1Reciprocal = 1.0F / cosTheta1;
		
//		TODO: Write explanation!
		final float gamma = acos(max(min(dotProduct, 1.0F), -1.0F));
		
//		TODO: Write explanation!
		final float cosGamma = cos(gamma);
		
//		TODO: Write explanation!
		final float perezRelativeLuminance0 = this.perezRelativeLuminance[0];
		final float perezRelativeLuminance1 = this.perezRelativeLuminance[1];
		final float perezRelativeLuminance2 = this.perezRelativeLuminance[2];
		final float perezRelativeLuminance3 = this.perezRelativeLuminance[3];
		final float perezRelativeLuminance4 = this.perezRelativeLuminance[4];
		
//		TODO: Write explanation!
		final float zenithRelativeLuminance = this.zenithRelativeLuminance;
		
//		TODO: Write explanation!
		final float perezX0 = this.perezX[0];
		final float perezX1 = this.perezX[1];
		final float perezX2 = this.perezX[2];
		final float perezX3 = this.perezX[3];
		final float perezX4 = this.perezX[4];
		
//		TODO: Write explanation!
		final float perezY0 = this.perezY[0];
		final float perezY1 = this.perezY[1];
		final float perezY2 = this.perezY[2];
		final float perezY3 = this.perezY[3];
		final float perezY4 = this.perezY[4];
		
//		TODO: Write explanation!
		final float zenithX = this.zenithX;
		final float zenithY = this.zenithY;
		
//		TODO: Write explanation!
		final float den0 = ((1.0F + perezRelativeLuminance0 * exp(perezRelativeLuminance1)) * (1.0F + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * theta0) + perezRelativeLuminance4 * cosTheta0 * cosTheta0));
		final float num0 = ((1.0F + perezRelativeLuminance0 * exp(perezRelativeLuminance1 * cosTheta1Reciprocal)) * (1.0F + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * gamma) + perezRelativeLuminance4 * cosGamma * cosGamma));
		final float relativeLuminance = zenithRelativeLuminance * num0 / den0 * 1.0e-4F;
		
//		TODO: Write explanation!
		final float den1 = ((1.0F + perezX0 * exp(perezX1)) * (1.0F + perezX2 * exp(perezX3 * theta1) + perezX4 * cosTheta0 * cosTheta0));
		final float num1 = ((1.0F + perezX0 * exp(perezX1 * cosTheta1Reciprocal)) * (1.0F + perezX2 * exp(perezX3 * gamma) + perezX4 * cosGamma * cosGamma));
		final float x = zenithX * num1 / den1;
		
//		TODO: Write explanation!
		final float den2 = ((1.0F + perezY0 * exp(perezY1)) * (1.0F + perezY2 * exp(perezY3 * theta1) + perezY4 * cosTheta0 * cosTheta0));
		final float num2 = ((1.0F + perezY0 * exp(perezY1 * cosTheta1Reciprocal)) * (1.0F + perezY2 * exp(perezY3 * gamma) + perezY4 * cosGamma * cosGamma));
		final float y = zenithY * num2 / den2;
		
//		TODO: Write explanation!
		final float v0 = 1.0F / (0.0241F + 0.2562F * x - 0.7341F * y);
		final float v1 = (-1.3515F - 1.7703F * x + 5.9114F * y) * v0;
		final float v2 = (0.03F - 31.4424F * x + 30.0717F * y) * v0;
		
//		TODO: Write explanation!
		final float x3 = 10246.121F + v1 * 187.75537F + v2 * 213.14803F;
		final float y3 = 10676.695F + v1 * 192.59653F + v2 * 76.29494F;
		final float z3 = 12372.504F + v1 * 3482.8765F + v2 * -235.71611F;
		
//		TODO: Write explanation!
		final float y3Reciprocal = 1.0F / y3;
		
//		TODO: Write explanation!
		final float x4 = x3 * relativeLuminance * y3Reciprocal;
		final float y4 = relativeLuminance;
		final float z4 = z3 * relativeLuminance * y3Reciprocal;
		
//		TODO: Write explanation!
		float r = 3.2410042F * x4 + -1.5373994F * y4 + -0.49861607F * z4;
		float g = -0.9692241F * x4 + 1.8759298F * y4 + 0.041554242F * z4;
		float b = 0.05563942F * x4 + -0.20401107F * y4 + 1.0571486F * z4;
		
		final float w = -min(0.0F, min(r, min(g, b)));
		
		if(w > 0.0F) {
			r += w;
			g += w;
			b += w;
		}
		
//		TODO: Write explanation!
		final int pixelIndex0 = pixelIndex * 3;
		
//		TODO: Write explanation!
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	private void doCalculateSurfaceProperties(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
		final int type = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_TYPE]);
		
		if(type == CompiledScene.PLANE_TYPE) {
			doCalculateSurfacePropertiesForPlane(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
		
		if(type == CompiledScene.SPHERE_TYPE) {
			doCalculateSurfacePropertiesForSphere(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
		
		if(type == CompiledScene.TRIANGLE_TYPE) {
			doCalculateSurfacePropertiesForTriangle(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
	}
	
	private void doCalculateSurfacePropertiesForPlane(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final int offsetA = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A;
		final int offsetB = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_B;
		final int offsetC = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_C;
		final int offsetSurfaceNormal = shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL;
		
//		Retrieve the point A of the plane:
		final float a0X = this.shapes[offsetA];
		final float a0Y = this.shapes[offsetA + 1];
		final float a0Z = this.shapes[offsetA + 2];
		
//		Retrieve the point B of the plane:
		final float b0X = this.shapes[offsetB];
		final float b0Y = this.shapes[offsetB + 1];
		final float b0Z = this.shapes[offsetB + 2];
		
//		Retrieve the point C of the plane:
		final float c0X = this.shapes[offsetC];
		final float c0Y = this.shapes[offsetC + 1];
		final float c0Z = this.shapes[offsetC + 2];
		
//		Retrieve the surface normal:
		final float surfaceNormalX = this.shapes[offsetSurfaceNormal];
		final float surfaceNormalY = this.shapes[offsetSurfaceNormal + 1];
		final float surfaceNormalZ = this.shapes[offsetSurfaceNormal + 2];
		
//		TODO: Write explanation!
		final float absSurfaceNormalX = abs(surfaceNormalX);
		final float absSurfaceNormalY = abs(surfaceNormalY);
		final float absSurfaceNormalZ = abs(surfaceNormalZ);
		
//		TODO: Write explanation!
		final boolean isX = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ;
		final boolean isY = absSurfaceNormalY > absSurfaceNormalZ;
		
//		TODO: Write explanation!
		final float a1X = isX ? a0Y : isY ? a0Z : a0X;
		final float a1Y = isX ? a0Z : isY ? a0X : a0Y;
		
//		TODO: Write explanation!
		final float b1X = isX ? c0Y - a0X : isY ? c0Z - a0X : c0X - a0X;
		final float b1Y = isX ? c0Z - a0Y : isY ? c0X - a0Y : c0Y - a0Y;
		
//		TODO: Write explanation!
		final float c1X = isX ? b0Y - a0X : isY ? b0Z - a0X : b0X - a0X;
		final float c1Y = isX ? b0Z - a0Y : isY ? b0X - a0Y : b0Y - a0Y;
		
//		TODO: Write explanation!
		final float determinant = b1X * c1Y - b1Y * c1X;
		final float determinantReciprocal = 1.0F / determinant;
		
//		TODO: Write explanation!
		final float bNU = -b1Y * determinantReciprocal;
		final float bNV = b1X * determinantReciprocal;
		final float bND = (b1Y * a1X - b1X * a1Y) * determinantReciprocal;
		
//		TODO: Write explanation!
		final float cNU = c1Y * determinantReciprocal;
		final float cNV = -c1X * determinantReciprocal;
		final float cND = (c1X * a1Y - c1Y * a1X) * determinantReciprocal;
		
//		TODO: Write explanation!
		final float hU = isX ? surfaceIntersectionPointY : isY ? surfaceIntersectionPointZ : surfaceIntersectionPointX;
		final float hV = isX ? surfaceIntersectionPointZ : isY ? surfaceIntersectionPointX : surfaceIntersectionPointY;
		
//		Calculate the UV-coordinates:
		final float u = hU * bNU + hV * bNV + bND;
		final float v = hU * cNU + hV * cNV + cND;
		
//		TODO: Write explanation!
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormalX;
		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormalY;
		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZ;
		this.intersections[offsetIntersectionUVCoordinates] = u;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForSphere(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final int offsetPosition = shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION;
		
//		TODO: Write explanation!
		final float x = this.shapes[offsetPosition];
		final float y = this.shapes[offsetPosition + 1];
		final float z = this.shapes[offsetPosition + 2];
		
//		Calculate the surface normal:
		final float surfaceNormal0X = surfaceIntersectionPointX - x;
		final float surfaceNormal0Y = surfaceIntersectionPointY - y;
		final float surfaceNormal0Z = surfaceIntersectionPointZ - z;
		final float lengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		final float surfaceNormal1X = surfaceNormal0X * lengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * lengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * lengthReciprocal;
		
//		Calculate the UV-coordinates:
		final float direction0X = x - surfaceIntersectionPointX;
		final float direction0Y = y - surfaceIntersectionPointY;
		final float direction0Z = z - surfaceIntersectionPointZ;
		final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
		final float direction1X = direction0X * direction0LengthReciprocal;
		final float direction1Y = direction0Y * direction0LengthReciprocal;
		final float direction1Z = direction0Z * direction0LengthReciprocal;
		final float u = 0.5F + atan2(direction1Z, direction1X) * PI_MULTIPLIED_BY_TWO_RECIPROCAL;
		final float v = 0.5F - asin(direction1Y) * PI_RECIPROCAL;
		
//		TODO: Write explanation!
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;
		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		this.intersections[offsetIntersectionUVCoordinates] = u;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForTriangle(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final int offsetA = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A;
		final int offsetB = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B;
		final int offsetC = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C;
		final int offsetUVA = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_A;
		final int offsetUVB = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_B;
		final int offsetUVC = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_C;
		final int offsetSurfaceNormalA = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_A;
		final int offsetSurfaceNormalB = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_B;
		final int offsetSurfaceNormalC = shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_C;
		
//		Calculate the Barycentric-coordinates:
		final float aX = this.shapes[offsetA];
		final float aY = this.shapes[offsetA + 1];
		final float aZ = this.shapes[offsetA + 2];
		final float bX = this.shapes[offsetB];
		final float bY = this.shapes[offsetB + 1];
		final float bZ = this.shapes[offsetB + 2];
		final float cX = this.shapes[offsetC];
		final float cY = this.shapes[offsetC + 1];
		final float cZ = this.shapes[offsetC + 2];
		final float edge0X = bX - aX;
		final float edge0Y = bY - aY;
		final float edge0Z = bZ - aZ;
		final float edge1X = cX - aX;
		final float edge1Y = cY - aY;
		final float edge1Z = cZ - aZ;
		final float v0X = directionY * edge1Z - directionZ * edge1Y;
		final float v0Y = directionZ * edge1X - directionX * edge1Z;
		final float v0Z = directionX * edge1Y - directionY * edge1X;
		final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;
		final float determinantReciprocal = 1.0F / determinant;
		final float v1X = originX - aX;
		final float v1Y = originY - aY;
		final float v1Z = originZ - aZ;
		final float u0 = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
		final float v2X = v1Y * edge0Z - v1Z * edge0Y;
		final float v2Y = v1Z * edge0X - v1X * edge0Z;
		final float v2Z = v1X * edge0Y - v1Y * edge0X;
		final float v0 = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
		final float w = 1.0F - u0 - v0;
		
//		Calculate the UV-coordinates for Flat Shading:
//		final float aU = this.shapes[offsetUVA];
//		final float aV = this.shapes[offsetUVA + 1];
//		final float bU = this.shapes[offsetUVB];
//		final float bV = this.shapes[offsetUVB + 1];
//		final float cU = this.shapes[offsetUVC];
//		final float cV = this.shapes[offsetUVC + 1];
//		final float u1 = w * aU + u0 * bU + v0 * cU;
//		final float v1 = w * aV + u0 * bV + v0 * cV;
		
//		Calculate the surface normal for Flat Shading:
//		final float surfaceNormalAX = this.shapes[offsetSurfaceNormalA];
//		final float surfaceNormalAY = this.shapes[offsetSurfaceNormalA + 1];
//		final float surfaceNormalAZ = this.shapes[offsetSurfaceNormalA + 2];
//		final float surfaceNormal0X = edge0Y * edge1Z - edge0Z * edge1Y;
//		final float surfaceNormal0Y = edge0Z * edge1X - edge0X * edge1Z;
//		final float surfaceNormal0Z = edge0X * edge1Y - edge0Y * edge1X;
//		final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
//		final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
//		final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
//		final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
//		final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal1X * surfaceNormalAX + surfaceNormal1Y * surfaceNormalAY + surfaceNormal1Z * surfaceNormalAZ : 0.0F;
//		final float surfaceNormal2X = dotProduct < 0.0F ? -surfaceNormal1X : surfaceNormal1X;
//		final float surfaceNormal2Y = dotProduct < 0.0F ? -surfaceNormal1Y : surfaceNormal1Y;
//		final float surfaceNormal2Z = dotProduct < 0.0F ? -surfaceNormal1Z : surfaceNormal1Z;
		
//		Calculate the UV-coordinates for Gouraud Shading:
		final float aU = this.shapes[offsetUVA];
		final float aV = this.shapes[offsetUVA + 1];
		final float bU = this.shapes[offsetUVB];
		final float bV = this.shapes[offsetUVB + 1];
		final float cU = this.shapes[offsetUVC];
		final float cV = this.shapes[offsetUVC + 1];
		final float u2 = aU * w + bU * u0 + cU * v0;
		final float v2 = aV * w + bV * u0 + cV * v0;
		
//		Calculate the surface normal for Gouraud Shading:
		final float surfaceNormalAX = this.shapes[offsetSurfaceNormalA];
		final float surfaceNormalAY = this.shapes[offsetSurfaceNormalA + 1];
		final float surfaceNormalAZ = this.shapes[offsetSurfaceNormalA + 2];
		final float surfaceNormalBX = this.shapes[offsetSurfaceNormalB];
		final float surfaceNormalBY = this.shapes[offsetSurfaceNormalB + 1];
		final float surfaceNormalBZ = this.shapes[offsetSurfaceNormalB + 2];
		final float surfaceNormalCX = this.shapes[offsetSurfaceNormalC];
		final float surfaceNormalCY = this.shapes[offsetSurfaceNormalC + 1];
		final float surfaceNormalCZ = this.shapes[offsetSurfaceNormalC + 2];
		final float surfaceNormal3X = surfaceNormalAX * w + surfaceNormalBX * u0 + surfaceNormalCX * v0;
		final float surfaceNormal3Y = surfaceNormalAY * w + surfaceNormalBY * u0 + surfaceNormalCY * v0;
		final float surfaceNormal3Z = surfaceNormalAZ * w + surfaceNormalBZ * u0 + surfaceNormalCZ * v0;
		final float surfaceNormal3LengthReciprocal = rsqrt(surfaceNormal3X * surfaceNormal3X + surfaceNormal3Y * surfaceNormal3Y + surfaceNormal3Z * surfaceNormal3Z);
		final float surfaceNormal4X = surfaceNormal3X * surfaceNormal3LengthReciprocal;
		final float surfaceNormal4Y = surfaceNormal3Y * surfaceNormal3LengthReciprocal;
		final float surfaceNormal4Z = surfaceNormal3Z * surfaceNormal3LengthReciprocal;
		final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal4X * surfaceNormalAX + surfaceNormal4Y * surfaceNormalAY + surfaceNormal4Z * surfaceNormalAZ : 0.0F;
		final float surfaceNormal5X = dotProduct < 0.0F ? -surfaceNormal4X : surfaceNormal4X;
		final float surfaceNormal5Y = dotProduct < 0.0F ? -surfaceNormal4Y : surfaceNormal4Y;
		final float surfaceNormal5Z = dotProduct < 0.0F ? -surfaceNormal4Z : surfaceNormal4Z;
		
//		TODO: Write explanation!
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		
//		Update the intersections array based on Flat Shading:
//		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;
//		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;
//		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;
//		this.intersections[offsetIntersectionUVCoordinates] = u1;
//		this.intersections[offsetIntersectionUVCoordinates + 1] = v1;
		
//		Update the intersections array based on Gouraud Shading:
		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal5X;
		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal5Y;
		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal5Z;
		this.intersections[offsetIntersectionUVCoordinates] = u2;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v2;
	}
	
	private void doCalculateTextureColor(final int intersectionsOffset, final int pixelIndex, final int relativeOffsetTextures, final int shapesOffset) {
		final int texturesOffset = (int)(this.shapes[shapesOffset + relativeOffsetTextures]);
		final int textureType = (int)(this.textures[texturesOffset + CompiledScene.TEXTURE_RELATIVE_OFFSET_TYPE]);
		
		if(textureType == CompiledScene.CHECKERBOARD_TEXTURE_TYPE) {
			doCalculateTextureColorForCheckerboardTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
		
		if(textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {
			doCalculateTextureColorForImageTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
		
		if(textureType == CompiledScene.SOLID_TEXTURE_TYPE) {
			doCalculateTextureColorForSolidTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForCheckerboardTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		final int offsetColor0 = texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_0;
		final int offsetColor1 = texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_1;
		
//		TODO: Write explanation!
		final float u = this.intersections[offsetUVCoordinates];
		final float v = this.intersections[offsetUVCoordinates + 1];
		
//		TODO: Write explanation!
		final float color0R = this.textures[offsetColor0];
		final float color0G = this.textures[offsetColor0 + 1];
		final float color0B = this.textures[offsetColor0 + 2];
		
//		TODO: Write explanation!
		final float color1R = this.textures[offsetColor1];
		final float color1G = this.textures[offsetColor1 + 1];
		final float color1B = this.textures[offsetColor1 + 2];
		
//		TODO: Write explanation!
		final float degrees = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_DEGREES];
		
//		TODO: Write explanation!
		final float sU = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_U];
		final float sV = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float angle0 = toRadians(degrees);
		
//		TODO: Write explanation!
		final float cosAngle = cos(angle0);
		final float sinAngle = sin(angle0);
		
//		TODO: Write explanation!
		final float textureU = modulo((u * cosAngle - v * sinAngle) * sU);
		final float textureV = modulo((v * cosAngle + u * sinAngle) * sV);
		
//		TODO: Write explanation!
		final boolean isDarkU = textureU > 0.5F;
		final boolean isDarkV = textureV > 0.5F;
		final boolean isDark = isDarkU ^ isDarkV;
		
//		TODO: Write explanation!
		final int pixelIndex0 = pixelIndex * 3;
		
		if(color0R == color1R && color0G == color1G && color0B == color1B) {
//			TODO: Write explanation!
			final float textureMultiplier = isDark ? 0.8F : 1.2F;
			
//			TODO: Write explanation!
			final float r = color0R * textureMultiplier;
			final float g = color0G * textureMultiplier;
			final float b = color0B * textureMultiplier;
			
//			TODO: Write explanation!
			this.temporaryColors[pixelIndex0] = r;
			this.temporaryColors[pixelIndex0 + 1] = g;
			this.temporaryColors[pixelIndex0 + 2] = b;
		} else {
//			TODO: Write explanation!
			final float r = isDark ? color0R : color1R;
			final float g = isDark ? color0G : color1G;
			final float b = isDark ? color0B : color1B;
			
//			TODO: Write explanation!
			this.temporaryColors[pixelIndex0] = r;
			this.temporaryColors[pixelIndex0 + 1] = g;
			this.temporaryColors[pixelIndex0 + 2] = b;
		}
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForImageTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		TODO: Write explanation!
		final float u = this.intersections[offsetUVCoordinates];
		final float v = this.intersections[offsetUVCoordinates + 1];
		
//		TODO: Write explanation!
		final float degrees = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DEGREES];
		
//		TODO: Write explanation!
		final float width = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_WIDTH];
		final float height = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_HEIGHT];
		
//		TODO: Write explanation!
		final float scaleU = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float angle0 = toRadians(degrees);
		
//		TODO: Write explanation!
		final float cosAngle = cos(angle0);
		final float sinAngle = sin(angle0);
		
//		TODO: Write explanation!
		final float x0 = (int)((u * cosAngle - v * sinAngle) * (width * scaleU));
		final float y0 = (int)((v * cosAngle + u * sinAngle) * (height * scaleV));
		
//		TODO: Write explanation!
		final float x1 = abs(x0);
		final float y1 = abs(y0);
		
//		TODO: Write explanation!
		final float x2 = remainder(x1, width);
		final float y2 = remainder(y1, height);
		
//		TODO: Write explanation!
		final int index = (int)(y2 * width + x2);
		final int rGB = (int)(this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index]);
		
//		TODO: Write explanation!
		final float r = ((rGB >> 16) & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		final float g = ((rGB >> 8) & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		final float b = (rGB & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		
//		TODO: Write explanation!
		final int pixelIndex0 = pixelIndex * 3;
		
//		TODO: Write explanation!
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForSolidTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final int offsetColor0 = texturesOffset + CompiledScene.SOLID_TEXTURE_RELATIVE_OFFSET_COLOR;
		
//		TODO: Write explanation!
		final float r = this.textures[offsetColor0];
		final float g = this.textures[offsetColor0 + 1];
		final float b = this.textures[offsetColor0 + 2];
		
//		TODO: Write explanation!
		final int pixelIndex0 = pixelIndex * 3;
		
//		TODO: Write explanation!
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	private void doCreatePrimaryRay(final int pixelIndex) {
//		Calculate the X- and Y-coordinates on the screen:
		final int y = pixelIndex / this.width;
		final int x = pixelIndex - y * this.width;
		
//		Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:
		final float eyeX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE];
		final float eyeY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE + 1];
		final float eyeZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE + 2];
		
//		Retrieve the current X-, Y- and Z-coordinates defining the up-vector of the camera lens (eye) in the scene and normalize it:
		final float up0X = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP];
		final float up0Y = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP + 1];
		final float up0Z = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP + 2];
		final float up0LengthReciprocal = rsqrt(up0X * up0X + up0Y * up0Y + up0Z * up0Z);
		final float up1X = up0X * up0LengthReciprocal;
		final float up1Y = up0Y * up0LengthReciprocal;
		final float up1Z = up0Z * up0LengthReciprocal;
		
//		Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float w0X = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W];
		final float w0Y = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1];
		final float w0Z = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2];
		final float w0LengthReciprocal = rsqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
		final float w1X = w0X * w0LengthReciprocal;
		final float w1Y = w0Y * w0LengthReciprocal;
		final float w1Z = w0Z * w0LengthReciprocal;
		
//		Calculate the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float u0X = w1Y * up1Z - w1Z * up1Y;
		final float u0Y = w1Z * up1X - w1X * up1Z;
		final float u0Z = w1X * up1Y - w1Y * up1X;
		final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
		final float u1X = u0X * u0LengthReciprocal;
		final float u1Y = u0Y * u0LengthReciprocal;
		final float u1Z = u0Z * u0LengthReciprocal;
		
//		Calculate the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float v0X = u1Y * w1Z - u1Z * w1Y;
		final float v0Y = u1Z * w1X - u1X * w1Z;
		final float v0Z = u1X * w1Y - u1Y * w1X;
		final float u1LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
		final float v1X = v0X * u1LengthReciprocal;
		final float v1Y = v0Y * u1LengthReciprocal;
		final float v1Z = v0Z * u1LengthReciprocal;
		
//		TODO: Write explanation!
		final float middleX = eyeX + w1X;
		final float middleY = eyeY + w1Y;
		final float middleZ = eyeZ + w1Z;
		
//		Calculate the Field of View:
		final float fieldOfViewX0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW];
		final float fieldOfViewX1 = tan(fieldOfViewX0 * PI_DIVIDED_BY_360);
		final float fieldOfViewY0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 1];
		final float fieldOfViewY1 = tan(-fieldOfViewY0 * PI_DIVIDED_BY_360);
		
//		TODO: Write explanation!
		final float horizontalX = u1X * fieldOfViewX1;
		final float horizontalY = u1Y * fieldOfViewX1;
		final float horizontalZ = u1Z * fieldOfViewX1;
		
//		TODO: Write explanation!
		final float verticalX = v1X * fieldOfViewY1;
		final float verticalY = v1Y * fieldOfViewY1;
		final float verticalZ = v1Z * fieldOfViewY1;
		
//		TODO: Write explanation!
		final float jitterX = nextFloat() - 0.5F;
		final float jitterY = nextFloat() - 0.5F;
		
//		TODO: Write explanation!
		final float sx = (jitterX + x) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION] - 1.0F);
		final float sy = (jitterY + y) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION + 1] - 1.0F);
		
//		TODO: Write explanation!
		final float sx0 = (2.0F * sx - 1.0F);
		final float sy0 = (2.0F * sy - 1.0F);
		
//		TODO: Write explanation!
		final float pointOnPlaneOneUnitAwayFromEyeX = middleX + (horizontalX * sx0) + (verticalX * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeY = middleY + (horizontalY * sx0) + (verticalY * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeZ = middleZ + (horizontalZ * sx0) + (verticalZ * sy0);
		
//		TODO: Write explanation!
		final float focalDistance = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE];
		
//		TODO: Write explanation!
		final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;
		final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;
		final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;
		
//		TODO: Write explanation!
		final float apertureRadius = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_APERTURE_RADIUS];
		
//		Initialize the X-, Y- and Z-coordinates of the aperture point:
		float aperturePointX = eyeX;
		float aperturePointY = eyeY;
		float aperturePointZ = eyeZ;
		
//		TODO: Write explanation!
		if(apertureRadius > 0.00001F) {
//			TODO: Write explanation!
			final float random1 = nextFloat();
			final float random2 = nextFloat();
			
//			TODO: Write explanation!
			final float angle = PI_MULTIPLIED_BY_TWO * random1;
			
//			TODO: Write explanation!
			final float distance = apertureRadius * sqrt(random2);
			
//			TODO: Write explanation!
			final float apertureX = cos(angle) * distance;
			final float apertureY = sin(angle) * distance;
			
//			TODO: Write explanation!
			aperturePointX = eyeX + u1X * apertureX + v1X * apertureY;
			aperturePointY = eyeY + u1Y * apertureX + v1Y * apertureY;
			aperturePointZ = eyeZ + u1Z * apertureX + v1Z * apertureY;
		}
		
//		TODO: Write explanation!
		final float apertureToImagePlane0X = pointOnImagePlaneX - aperturePointX;
		final float apertureToImagePlane0Y = pointOnImagePlaneY - aperturePointY;
		final float apertureToImagePlane0Z = pointOnImagePlaneZ - aperturePointZ;
		
//		TODO: Write explanation!
		final float apertureToImagePlane0LengthReciprocal = rsqrt(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);
		
//		TODO: Write explanation!
		final float apertureToImagePlane1X = apertureToImagePlane0X * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Y = apertureToImagePlane0Y * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Z = apertureToImagePlane0Z * apertureToImagePlane0LengthReciprocal;
		
//		TODO: Write explanation!
		final int raysOffset = pixelIndex * SIZE_RAY;
		
//		TODO: Write explanation!
		this.rays[raysOffset + 0] = aperturePointX;
		this.rays[raysOffset + 1] = aperturePointY;
		this.rays[raysOffset + 2] = aperturePointZ;
		this.rays[raysOffset + 3] = apertureToImagePlane1X;
		this.rays[raysOffset + 4] = apertureToImagePlane1Y;
		this.rays[raysOffset + 5] = apertureToImagePlane1Z;
	}
	
	private void doPathTracing(final int pixelIndex) {
//		Retrieve the intersections and rays arrays:
		final float[] intersections = this.intersections;
		final float[] rays = this.rays;
		
//		Retrieve the maximum depth allowed and the depth at which to use Russian Roulette to test for path termination:
		final int depthMaximum = this.depthMaximum;
		final int depthRussianRoulette = this.depthRussianRoulette;
		
//		Calculate the current offsets to the intersections and rays arrays:
		final int intersectionsOffset = pixelIndex * SIZE_INTERSECTION;
		final int raysOffset = pixelIndex * SIZE_RAY;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Retrieve the offsets of the ray origin and the ray direction:
		final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;
		final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;
		
//		Initialize the origin from the primary ray:
		float originX = rays[offsetOrigin];
		float originY = rays[offsetOrigin + 1];
		float originZ = rays[offsetOrigin + 2];
		
//		Initialize the direction from the primary ray:
		float directionX = rays[offsetDirection];
		float directionY = rays[offsetDirection + 1];
		float directionZ = rays[offsetDirection + 2];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the radiance multiplier to white:
		float radianceMultiplierR = 1.0F;
		float radianceMultiplierG = 1.0F;
		float radianceMultiplierB = 1.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex0 = pixelIndex * 3;
		
//		Initialize the offset of the shape to skip to -1:
		int shapesOffsetToSkip = -1;
		
//		Run the following do-while-loop as long as the current depth is less than the maximum depth:
		do {
//			Perform an intersection test:
			doPerformIntersectionTest(pixelIndex, shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
			final int shapesOffset = (int)(intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || shapesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
				pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
				pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
				
//				Update the current pixel color:
				this.currentPixelColors[pixelIndex0] = max(min(pixelColorR, 255.0F), 0.0F);
				this.currentPixelColors[pixelIndex0 + 1] = max(min(pixelColorG, 255.0F), 0.0F);
				this.currentPixelColors[pixelIndex0 + 2] = max(min(pixelColorB, 255.0F), 0.0F);
				
				return;
			}
			
//			Update the offset of the shape to skip to the current offset:
			shapesOffsetToSkip = shapesOffset;
			
//			Retrieve the offsets of the surface intersection point and the surface normal:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			Retrieve the surface intersection point:
			final float surfaceIntersectionPointX = intersections[offsetIntersectionSurfaceIntersectionPoint];
			final float surfaceIntersectionPointY = intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal:
			final float surfaceNormalX = intersections[offsetIntersectionSurfaceNormal];
			final float surfaceNormalY = intersections[offsetIntersectionSurfaceNormal + 1];
			final float surfaceNormalZ = intersections[offsetIntersectionSurfaceNormal + 2];
			
//			Calculate the albedo texture color for the intersected shape:
			doCalculateTextureColor(intersectionsOffset, pixelIndex, CompiledScene.SHAPE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);
			
//			Get the color of the shape from the albedo texture color that was looked up:
			float albedoColorR = this.temporaryColors[pixelIndex0];
			float albedoColorG = this.temporaryColors[pixelIndex0 + 1];
			float albedoColorB = this.temporaryColors[pixelIndex0 + 2];
			
//			Retrieve the offset of the emission:
			final int offsetEmission = shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_EMISSION;
			
//			Retrieve the emission from the intersected shape:
			final float emissionR = this.shapes[offsetEmission];
			final float emissionG = this.shapes[offsetEmission + 1];
			final float emissionB = this.shapes[offsetEmission + 2];
			
//			Add the current radiance multiplied by the emission of the intersected shape to the current pixel color:
			pixelColorR += radianceMultiplierR * emissionR;
			pixelColorG += radianceMultiplierG * emissionG;
			pixelColorB += radianceMultiplierB * emissionB;
			
//			Increment the current depth:
			depthCurrent++;
			
//			Check if the current depth is great enough to perform Russian Roulette to probabilistically terminate the path:
			if(depthCurrent >= depthRussianRoulette) {
//				Calculate the Russian Roulette Probability Density Function (PDF) using the maximum color component of the albedo of the intersected shape:
				final float probabilityDensityFunction = max(albedoColorR, max(albedoColorG, albedoColorB));
				
//				Calculate a random number that will be used when determinating whether or not the path should be terminated:
				final float random = nextFloat();
				
//				If the random number is greater than or equal to the Russian Roulette PDF, then terminate the path:
				if(random >= probabilityDensityFunction) {
//					Perform an intersection test:
					doPerformIntersectionTest(pixelIndex, shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
					
//					Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
					final float distance0 = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
					
//					Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
					final int shapesOffset0 = (int)(intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
					
//					Test that an intersection was actually made, and if not, return black color (or possibly the background color):
					if(distance0 == INFINITY || shapesOffset0 == -1) {
//						Calculate the color for the sky in the current direction:
						doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
						
//						Add the color for the sky to the current pixel color:
						pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
						pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
						pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
					}
					
//					Update the current pixel color:
					this.currentPixelColors[pixelIndex0] = max(min(pixelColorR, 255.0F), 0.0F);
					this.currentPixelColors[pixelIndex0 + 1] = max(min(pixelColorG, 255.0F), 0.0F);
					this.currentPixelColors[pixelIndex0 + 2] = max(min(pixelColorB, 255.0F), 0.0F);
					
					return;
				}
				
//				Calculate the reciprocal of the Russian Roulette PDF, so no divisions are needed next:
				final float probabilityDensityFunctionReciprocal = 1.0F / probabilityDensityFunction;
				
//				Because the path was not terminated this time, the albedo color has to be multiplied with the reciprocal of the Russian Roulette PDF:
				albedoColorR *= probabilityDensityFunctionReciprocal;
				albedoColorG *= probabilityDensityFunctionReciprocal;
				albedoColorB *= probabilityDensityFunctionReciprocal;
			}
			
//			Retrieve the material type of the intersected shape:
			final int material = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_MATERIAL]);
			
//			Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
			final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
			final float dotProductMultipliedByTwo = dotProduct * 2.0F;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float w0X = isCorrectlyOriented ? surfaceNormalX : -surfaceNormalX;
			final float w0Y = isCorrectlyOriented ? surfaceNormalY : -surfaceNormalY;
			final float w0Z = isCorrectlyOriented ? surfaceNormalZ : -surfaceNormalZ;
			
//			Pre-compute the random values that will be used later:
			final float randomA = nextFloat();
			final float randomB = nextFloat();
			final float randomC = nextFloat();
//			final float randomD = nextFloat();
//			final float randomE = nextFloat();
			
			if(material == MATERIAL_CLEAR_COAT) {
//				TODO: Write explanation!
				final float nnt = REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1;
				
//				Calculate the dot product between the W direction and the current ray direction:
				final float dotProductOfW0AndDirection = w0X * directionX + w0Y * directionY + w0Z * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;
				
//				Initialize the specular color component values to be used:
				final float specularColorR = 1.0F;
				final float specularColorG = 1.0F;
				final float specularColorB = 1.0F;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + w0X * 0.02F;
					originY = surfaceIntersectionPointY + w0Y * 0.02F;
					originZ = surfaceIntersectionPointZ + w0Z * 0.02F;
					
//					Update the ray direction for the next iteration:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
					
//					Multiply the current radiance multiplier with the specular color:
					radianceMultiplierR *= specularColorR;
					radianceMultiplierG *= specularColorG;
					radianceMultiplierB *= specularColorB;
				} else {
//					TODO: Write explanation!
					final float angle1 = -dotProductOfW0AndDirection;
					final float angle2 = 1.0F - angle1;
					
//					TODO: Write explanation!
					final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					TODO: Write explanation!
					final float transmittance = 1.0F - reflectance;
					
//					TODO: Write explanation!
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = randomA < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
					if(isReflectionDirection) {
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + w0X * 0.02F;
						originY = surfaceIntersectionPointY + w0Y * 0.02F;
						originZ = surfaceIntersectionPointZ + w0Z * 0.02F;
						
//						Update the ray direction for the next iteration:
						directionX = reflectionDirectionX;
						directionY = reflectionDirectionY;
						directionZ = reflectionDirectionZ;
						
//						Multiply the current radiance multiplier with the specular color:
						radianceMultiplierR *= specularColorR;
						radianceMultiplierG *= specularColorG;
						radianceMultiplierB *= specularColorB;
					} else {
//						TODO: Write explanation!
						final float random0 = PI_MULTIPLIED_BY_TWO * randomB;
						final float random0Cos = cos(random0);
						final float random0Sin = sin(random0);
						final float random1 = randomC;
						final float random1Squared0 = sqrt(random1);
						final float random1Squared1 = sqrt(1.0F - random1);
						
//						Calculate the orthonormal basis W vector:
						final float w0LengthReciprocal = rsqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
						final float w1X = w0X * w0LengthReciprocal;
						final float w1Y = w0Y * w0LengthReciprocal;
						final float w1Z = w0Z * w0LengthReciprocal;
						
//						TODO: Write explanation!
						final boolean isY = abs(w1X) > 0.1F;
						
//						Calculate the orthonormal basis U vector:
						final float u0X = isY ? 0.0F : 1.0F;
						final float u0Y = isY ? 1.0F : 0.0F;
						final float u1X = u0Y * w1Z;
						final float u1Y = -(u0X * w1Z);
						final float u1Z = u0X * w1Y - u0Y * w1X;
						final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
						final float u2X = u1X * u1LengthReciprocal;
						final float u2Y = u1Y * u1LengthReciprocal;
						final float u2Z = u1Z * u1LengthReciprocal;
						
//						Calculate the orthonormal basis V vector:
						final float v0X = w0Y * u2Z - w0Z * u2Y;
						final float v0Y = w0Z * u2X - w0X * u2Z;
						final float v0Z = w0X * u2Y - w0Y * u2X;
						
//						TODO: Write explanation!
						final float direction0X = u2X * random0Cos * random1Squared0 + v0X * random0Sin * random1Squared0 + w0X * random1Squared1;
						final float direction0Y = u2Y * random0Cos * random1Squared0 + v0Y * random0Sin * random1Squared0 + w0Y * random1Squared1;
						final float direction0Z = u2Z * random0Cos * random1Squared0 + v0Z * random0Sin * random1Squared0 + w0Z * random1Squared1;
						final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
						
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + w0X * 0.01F;
						originY = surfaceIntersectionPointY + w0Y * 0.01F;
						originZ = surfaceIntersectionPointZ + w0Z * 0.01F;
						
//						Update the ray direction for the next iteration:
						directionX = direction0X * direction0LengthReciprocal;
						directionY = direction0Y * direction0LengthReciprocal;
						directionZ = direction0Z * direction0LengthReciprocal;
						
//						Multiply the current radiance multiplier with the albedo:
						radianceMultiplierR *= albedoColorR;
						radianceMultiplierG *= albedoColorG;
						radianceMultiplierB *= albedoColorB;
					}
				}
				
//				TODO: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent;
				
//				TODO: Write explanation!
//-				final float normalX = directionX;
//-				final float normalY = directionY;
//-				final float normalZ = directionZ;
				
//				TODO: Write explanation!
//-				final float scale = 0.5F;
				
//				TODO: Write explanation!
//-				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
//-				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				
//				TODO: Write explanation!
//-				final float vector1X = 0.0F - normalZ * vector0Y;
//-				final float vector1Y = normalZ * vector0X - 0.0F;
//-				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
//-				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
//-				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
//-				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
//-				final float phi = randomD * PI_MULTIPLIED_BY_TWO;
//-				final float theta = randomE;
				
//				TODO: Write explanation!
//-				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
//-				final float vector3X = cos(phi) * radius;
//-				final float vector3Y = sin(phi) * radius;
//-				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
//-				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
//-				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
//-				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
//-				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
//-				radianceMultiplierR *= this.temporaryColors[pixelIndex0];
//-				radianceMultiplierG *= this.temporaryColors[pixelIndex0 + 1];
//-				radianceMultiplierB *= this.temporaryColors[pixelIndex0 + 2];
			} else if(material == MATERIAL_LAMBERTIAN_DIFFUSE) {
//				TODO: Write explanation!
				final float random0 = PI_MULTIPLIED_BY_TWO * randomA;
				final float random0Cos = cos(random0);
				final float random0Sin = sin(random0);
				final float random1 = randomB;
				final float random1Squared0 = sqrt(random1);
				final float random1Squared1 = sqrt(1.0F - random1);
				
//				TODO: Write explanation!
				final boolean isY = abs(w0X) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float u0X = isY ? 0.0F : 1.0F;
				final float u0Y = isY ? 1.0F : 0.0F;
				final float u1X = u0Y * w0Z;
				final float u1Y = -(u0X * w0Z);
				final float u1Z = u0X * w0Y - u0Y * w0X;
				final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
				final float u2X = u1X * u1LengthReciprocal;
				final float u2Y = u1Y * u1LengthReciprocal;
				final float u2Z = u1Z * u1LengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float v0X = w0Y * u2Z - w0Z * u2Y;
				final float v0Y = w0Z * u2X - w0X * u2Z;
				final float v0Z = w0X * u2Y - w0Y * u2X;
				
//				TODO: Write explanation!
				final float direction0X = u2X * random0Cos * random1Squared0 + v0X * random0Sin * random1Squared0 + w0X * random1Squared1;
				final float direction0Y = u2Y * random0Cos * random1Squared0 + v0Y * random0Sin * random1Squared0 + w0Y * random1Squared1;
				final float direction0Z = u2Z * random0Cos * random1Squared0 + v0Z * random0Sin * random1Squared0 + w0Z * random1Squared1;
				final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + w0X * 0.01F;
				originY = surfaceIntersectionPointY + w0Y * 0.01F;
				originZ = surfaceIntersectionPointZ + w0Z * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = direction0X * direction0LengthReciprocal;
				directionY = direction0Y * direction0LengthReciprocal;
				directionZ = direction0Z * direction0LengthReciprocal;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
				
//				TODO: Write explanation!
//-				final float normalX = directionX;
//-				final float normalY = directionY;
//-				final float normalZ = directionZ;
				
//				TODO: Write explanation!
//-				final float scale = 0.5F;
				
//				TODO: Write explanation!
//-				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
//-				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				
//				TODO: Write explanation!
//-				final float vector1X = -(normalZ * vector0Y);
//-				final float vector1Y = normalZ * vector0X;
//-				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
//-				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
//-				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
//-				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
//-				final float phi = PI_MULTIPLIED_BY_TWO * randomC;
//-				final float theta = randomD;
				
//				TODO: Write explanation!
//-				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
//-				final float vector3X = cos(phi) * radius;
//-				final float vector3Y = sin(phi) * radius;
//-				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
//-				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
//-				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
//-				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
//-				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
//-				radianceMultiplierR *= this.temporaryColors[pixelIndex0];
//-				radianceMultiplierG *= this.temporaryColors[pixelIndex0 + 1];
//-				radianceMultiplierB *= this.temporaryColors[pixelIndex0 + 2];
			} else if(material == MATERIAL_PHONG_METAL) {
//				TODO: Write explanation!
				final float random0 = PI_MULTIPLIED_BY_TWO * randomA;
				final float random0Cos = cos(random0);
				final float random0Sin = sin(random0);
				final float random1 = randomB;
				
//				TODO: Write explanation!
				final float cosTheta = pow(1.0F - random1, PHONE_EXPONENT_PLUS_ONE_RECIPROCAL);
				final float sinTheta = sqrt(1.0F - cosTheta * cosTheta);
				
//				Calculate the orthonormal basis W vector:
				final float w1X = directionX - surfaceNormalX * dotProductMultipliedByTwo;
				final float w1Y = directionY - surfaceNormalY * dotProductMultipliedByTwo;
				final float w1Z = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;
				final float w1LengthReciprocal = rsqrt(w1X * w1X + w1Y * w1Y + w1Z * w1Z);
				final float w2X = w1X * w1LengthReciprocal;
				final float w2Y = w1Y * w1LengthReciprocal;
				final float w2Z = w1Z * w1LengthReciprocal;
				
//				TODO: Write explanation!
				final boolean isY = abs(w2X) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float u0X = isY ? 0.0F : 1.0F;
				final float u0Y = isY ? 1.0F : 0.0F;
				final float u1X = u0Y * w2Z;
				final float u1Y = -(u0X * w2Z);
				final float u1Z = u0X * w2Y - u0Y * w2X;
				final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
				final float u2X = u1X * u1LengthReciprocal;
				final float u2Y = u1Y * u1LengthReciprocal;
				final float u2Z = u1Z * u1LengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float v0X = w2Y * u2Z - w2Z * u2Y;
				final float v0Y = w2Z * u2X - w2X * u2Z;
				final float v0Z = w2X * u2Y - w2Y * u2X;
				
//				TODO: Write explanation!
				final float direction0X = u2X * random0Cos * sinTheta + v0X * random0Sin * sinTheta + w1X * cosTheta;
				final float direction0Y = u2Y * random0Cos * sinTheta + v0Y * random0Sin * sinTheta + w1Y * cosTheta;
				final float direction0Z = u2Z * random0Cos * sinTheta + v0Z * random0Sin * sinTheta + w1Z * cosTheta;
				final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + w2X * 0.01F;
				originY = surfaceIntersectionPointY + w2Y * 0.01F;
				originZ = surfaceIntersectionPointZ + w2Z * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = direction0X * direction0LengthReciprocal;
				directionY = direction0Y * direction0LengthReciprocal;
				directionZ = direction0Z * direction0LengthReciprocal;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
				
//				TODO: Write explanation!
//-				final float normalX = directionX;
//-				final float normalY = directionY;
//-				final float normalZ = directionZ;
				
//				TODO: Write explanation!
//-				final float scale = 0.5F;
				
//				TODO: Write explanation!
//-				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
//-				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				
//				TODO: Write explanation!
//-				final float vector1X = -(normalZ * vector0Y);
//-				final float vector1Y = normalZ * vector0X;
//-				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
//-				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
//-				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
//-				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
//-				final float phi = PI_MULTIPLIED_BY_TWO * randomC;
//-				final float theta = randomD;
				
//				TODO: Write explanation!
//-				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
//-				final float vector3X = cos(phi) * radius;
//-				final float vector3Y = sin(phi) * radius;
//-				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
//-				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
//-				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
//-				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
//-				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
//-				radianceMultiplierR *= this.temporaryColors[pixelIndex0];
//-				radianceMultiplierG *= this.temporaryColors[pixelIndex0 + 1];
//-				radianceMultiplierB *= this.temporaryColors[pixelIndex0 + 2];
			} else if(material == MATERIAL_GLASS) {
//				TODO: Write explanation!
				final boolean isGoingIn = surfaceNormalX * w0X + surfaceNormalY * w0Y + surfaceNormalZ * w0Z > 0.0F;
				
//				TODO: Write explanation!
				final float nnt = isGoingIn ? REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1 : REFRACTIVE_INDEX_1_DIVIDED_BY_REFRACTIVE_INDEX_0;
				
//				TODO: Write explanation!
				final float dotProductOfW0AndDirection = w0X * directionX + w0Y * directionY + w0Z * directionZ;
				
//				TODO: Write explanation!
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);
				
//				TODO: Write explanation!
				final float reflectionDirectionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + w0X * 0.02F;
					originY = surfaceIntersectionPointY + w0Y * 0.02F;
					originZ = surfaceIntersectionPointZ + w0Z * 0.02F;
					
//					Update the ray direction for the next iteration:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
				} else {
//					TODO: Write explanation!
					final float sqrtTotalInternalReflection = sqrt(totalInternalReflection);
					final float scalar = isGoingIn ? dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection : -(dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection);
					
//					TODO: Write explanation!
					final float direction0X = directionX * nnt - surfaceNormalX * scalar;
					final float direction0Y = directionY * nnt - surfaceNormalY * scalar;
					final float direction0Z = directionZ * nnt - surfaceNormalZ * scalar;
					final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
					
//					TODO: Write explanation!
					final float transmissionDirectionX = direction0X * direction0LengthReciprocal;
					final float transmissionDirectionY = direction0Y * direction0LengthReciprocal;
					final float transmissionDirectionZ = direction0Z * direction0LengthReciprocal;
					
//					TODO: Write explanation!
					final float angle1 = (isGoingIn ? -dotProductOfW0AndDirection : transmissionDirectionX * surfaceNormalX + transmissionDirectionY * surfaceNormalY + transmissionDirectionZ * surfaceNormalZ);
					final float angle2 = 1.0F - angle1;
					
//					TODO: Write explanation!
					final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					TODO: Write explanation!
					final float transmittance = 1.0F - reflectance;
					
//					TODO: Write explanation!
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = randomA < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
//					Retrieve the epsilon value that offsets the ray origin to mitigate self intersections:
					final float epsilon = isReflectionDirection ? 0.01F : 0.000001F;
					
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + w0X * epsilon;
					originY = surfaceIntersectionPointY + w0Y * epsilon;
					originZ = surfaceIntersectionPointZ + w0Z * epsilon;
					
//					Update the ray direction for the next iteration:
					directionX = isReflectionDirection ? reflectionDirectionX : transmissionDirectionX;
					directionY = isReflectionDirection ? reflectionDirectionY : transmissionDirectionY;
					directionZ = isReflectionDirection ? reflectionDirectionZ : transmissionDirectionZ;
				}
				
//				TODO: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent;
			} else if(material == MATERIAL_MIRROR) {
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalX * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormalY * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormalZ * 0.000001F;
				
//				Update the ray direction for the next iteration:
				directionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;
				directionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;
				directionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			}
		} while(depthCurrent < depthMaximum);
		
//		Perform an intersection test:
		doPerformIntersectionTest(pixelIndex, shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance0 = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int shapesOffset0 = (int)(intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance0 == INFINITY || shapesOffset0 == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
			pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
			pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
		}
		
//		Update the current pixel color:
		this.currentPixelColors[pixelIndex0] = max(min(pixelColorR, 255.0F), 0.0F);
		this.currentPixelColors[pixelIndex0 + 1] = max(min(pixelColorG, 255.0F), 0.0F);
		this.currentPixelColors[pixelIndex0 + 2] = max(min(pixelColorB, 255.0F), 0.0F);
	}
	
	private void doPerformIntersectionTest(final int pixelIndex, final int shapesOffsetToSkip, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offset to the intersections array:
		final int intersectionsOffset = pixelIndex * SIZE_INTERSECTION;
		
//		Initialize the distance to the closest shape to INFINITY:
		float minimumDistance = INFINITY;
		
//		Initialize the offset to the closest shape to -1:
		int shapesOffset = -1;
		
//		Calculate the reciprocal of the ray direction vector:
		final float directionReciprocalX = 1.0F / directionX;
		final float directionReciprocalY = 1.0F / directionY;
		final float directionReciprocalZ = 1.0F / directionZ;
		
//		Initialize the offset to the root of the BVH structure (which is 0):
		int boundingVolumeHierarchyOffset = 0;
		
//		Loop through the BVH structure as long as the offset to the next node is not -1:
		do {
//			Retrieve the minimum point location of the current bounding box:
			final float minimumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 2];
			final float minimumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3];
			final float minimumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 4];
			
//			Retrieve the maximum point location of the current bounding box:
			final float maximumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 5];
			final float maximumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 6];
			final float maximumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 7];
			
//			Calculate the distance to the minimum point location of the bounding box:
			final float t0X = (minimumX - originX) * directionReciprocalX;
			final float t0Y = (minimumY - originY) * directionReciprocalY;
			final float t0Z = (minimumZ - originZ) * directionReciprocalZ;
			
//			Calculate the distance to the maximum point location of the bounding box:
			final float t1X = (maximumX - originX) * directionReciprocalX;
			final float t1Y = (maximumY - originY) * directionReciprocalY;
			final float t1Z = (maximumZ - originZ) * directionReciprocalZ;
			
//			Calculate the minimum and maximum X-components:
			final float tMaximumX = max(t0X, t1X);
			final float tMinimumX = min(t0X, t1X);
			
//			Calculate the minimum and maximum Y-components:
			final float tMaximumY = max(t0Y, t1Y);
			final float tMinimumY = min(t0Y, t1Y);
			
//			Calculate the minimum and maximum Z-components:
			final float tMaximumZ = max(t0Z, t1Z);
			final float tMinimumZ = min(t0Z, t1Z);
			
//			Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:
			final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));
			final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));
			
//			Check if the maximum distance is greater than or equal to the minimum distance:
			if(tMaximum < tMinimum) {
//				Retrieve the offset to the next node in the BVH structure, relative to the current one:
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
			} else {
//				Retrieve the type of the current BVH node:
				final float type = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset];
				
				if(type == 1.0F) {
//					This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
				} else {
//					Retrieve the triangle count in the current BVH node:
					final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
					
					int i = 0;
					
//					Loop through all triangles in the current BVH node:
					do {
//						Retrieve the offset to the current triangle:
						final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
						
						if(offset != shapesOffsetToSkip) {
//							Perform an intersection test with the current triangle:
							final float currentDistance = doIntersect(offset, originX, originY, originZ, directionX, directionY, directionZ);
							
//							Check if the current distance is less than the distance to the closest shape so far:
							if(currentDistance < minimumDistance) {
//								Update the distance to the closest shape with the current one:
								minimumDistance = currentDistance;
								
//								Update the offset to the closest shape with the current one:
								shapesOffset = offset;
							}
						}
						
						i++;
					} while(i < triangleCount);
					
//					Retrieve the offset to the next node in the BVH structure, relative to the current one:
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
				}
				
//				TODO: Find out why the "child list broken" Exception occurs if the following line is not present!
				boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset;
			}
		} while(boundingVolumeHierarchyOffset != -1);
		
//		Loop through any other shapes, that are not triangles:
		for(int i = 0; i < this.shapeOffsetsLength; i++) {
//			Retrieve the offset to the shape:
			final int currentShapesOffset = this.shapeOffsets[i];
			
//			Perform an intersection test with the current shape:
			final float currentDistance = doIntersect(currentShapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
			
//			Check if the current distance is less than the distance to the closest shape so far:
			if(currentDistance < minimumDistance) {
//				Update the distance to the closest shape with the current one:
				minimumDistance = currentDistance;
				
//				Update the offset to the closest shape with the current one:
				shapesOffset = currentShapesOffset;
			}
		}
		
		if(minimumDistance < INFINITY && shapesOffset > -1) {
//			Calculate the surface properties for the intersected shape:
			doCalculateSurfaceProperties(minimumDistance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
			
//			Perform standard Normal Mapping:
			doPerformNormalMapping(intersectionsOffset, pixelIndex, shapesOffset);
			
//			Perform Perlin Noise Normal Mapping:
			doPerformPerlinNoiseNormalMapping(intersectionsOffset, shapesOffset);
		} else {
//			Retrieve the intersections array:
			final float[] intersections = this.intersections;
			
//			Reset the information in the intersections array:
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;
		}
	}
	
	private void doPerformNormalMapping(final int intersectionsOffset, final int pixelIndex, final int shapesOffset) {
//		TODO: Write explanation!
		final int texturesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL]);
		final int textureType = (int)(this.textures[texturesOffset + CompiledScene.TEXTURE_RELATIVE_OFFSET_TYPE]);
		
		if(textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {
//			TODO: Write explanation!
			doCalculateTextureColorForImageTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
			
//			TODO: Write explanation!
			final float[] intersections = this.intersections;
			
//			TODO: Write explanation!
			final int pixelIndex0 = pixelIndex * 3;
			
//			TODO: Write explanation!
			final float r = 2.0F * this.temporaryColors[pixelIndex0] - 1.0F;
			final float g = 2.0F * this.temporaryColors[pixelIndex0 + 1] - 1.0F;
			final float b = 2.0F * this.temporaryColors[pixelIndex0 + 2] - 1.0F;
			
//			TODO: Write explanation!
			final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			TODO: Write explanation!
			final float wX = intersections[offsetIntersectionSurfaceNormal];
			final float wY = intersections[offsetIntersectionSurfaceNormal + 1];
			final float wZ = intersections[offsetIntersectionSurfaceNormal + 2];
			
//			TODO: Write explanation!
			final float absWX = abs(wX);
			final float absWY = abs(wY);
			final float absWZ = abs(wZ);
			
//			TODO: Write explanation!
			final boolean isWX = absWX < absWY && absWX < absWZ;
			final boolean isWY = absWY < absWZ;
			
//			TODO: Write explanation!
			final float v0X = isWX ? 0.0F : isWY ? wZ : wY;
			final float v0Y = isWX ? wZ : isWY ? 0.0F : -wX;
			final float v0Z = isWX ? -wY : isWY ? -wX : 0.0F;
			final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
			final float v1X = v0X * v0LengthReciprocal;
			final float v1Y = v0Y * v0LengthReciprocal;
			final float v1Z = v0Z * v0LengthReciprocal;
			
//			TODO: Write explanation!
			final float u0X = v1Y * wZ - v1Z * wY;
			final float u0Y = v1Z * wX - v1X * wZ;
			final float u0Z = v1X * wY - v1Y * wX;
			final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
			final float u1X = u0X * u0LengthReciprocal;
			final float u1Y = u0Y * u0LengthReciprocal;
			final float u1Z = u0Z * u0LengthReciprocal;
			
//			Calculate the surface normal:
			final float surfaceNormal0X = r * u1X + g * v1X + b * wX;
			final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;
			final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;
			final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
			final float surfaceNormal0Sum = surfaceNormal0X + surfaceNormal0Y + surfaceNormal0Z;
			final float surfaceNormal1X = surfaceNormal0Sum == 0.0F ? wX : surfaceNormal0X * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Y = surfaceNormal0Sum == 0.0F ? wY : surfaceNormal0Y * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Z = surfaceNormal0Sum == 0.0F ? wZ : surfaceNormal0Z * surfaceNormal0LengthReciprocal;
			
//			TODO: Write explanation!
			intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;
			intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
			intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
			
			/*
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			
			final float origin0X = intersections[offsetIntersectionSurfaceIntersectionPoint];
			final float origin0Y = intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float origin0Z = intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
			
			final float dotProduct = surfaceNormal1X * wX + surfaceNormal1Y * wY + surfaceNormal1Z * wZ;
			
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
			final float multiplier = isCorrectlyOriented ? 1.1F : -1.1F;
			
			final float origin1X = origin0X + wX * multiplier;
			final float origin1Y = origin0Y + wY * multiplier;
			final float origin1Z = origin0Z + wZ * multiplier;
			
			intersections[offsetIntersectionSurfaceIntersectionPoint] = origin1X;
			intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = origin1Y;
			intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = origin1Z;
			*/
		}
	}
	
	private void doPerformPerlinNoiseNormalMapping(final int intersectionsOffset, final int shapesOffset) {
//		TODO: Write explanation!
		final float[] intersections = this.intersections;
		
//		TODO: Write explanation!
		final float amount = this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT];
		
//		TODO: Write explanation!
		final float scale = this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE];
		
//		TODO: Write explanation!
		if(amount > 0.0F && scale > 0.0F) {
//			TODO: Write explanation!
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			TODO: Write explanation!
			final float x0 = intersections[offsetIntersectionSurfaceIntersectionPoint];
			final float y0 = intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float z0 = intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			TODO: Write explanation!
			final float scaleReciprocal = 1.0F / scale;
			
//			TODO: Write explanation!
			final float x1 = x0 * scaleReciprocal;
			final float y1 = y0 * scaleReciprocal;
			final float z1 = z0 * scaleReciprocal;
			
//			TODO: Write explanation!
			final float noiseX = doPerlinNoise(x1, y1, z1);
			final float noiseY = doPerlinNoise(y1, z1, x1);
			final float noiseZ = doPerlinNoise(z1, x1, y1);
			
//			Calculate the surface normal:
			final float surfaceNormal0X = intersections[offsetIntersectionSurfaceNormal];
			final float surfaceNormal0Y = intersections[offsetIntersectionSurfaceNormal + 1];
			final float surfaceNormal0Z = intersections[offsetIntersectionSurfaceNormal + 2];
			final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;
			final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;
			final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;
			final float surfaceNormal1LengthReciprocal = rsqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);
			final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;
			
//			Update the intersections array with the new surface normal:
			intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;
			intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;
			intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;
		}
	}
}