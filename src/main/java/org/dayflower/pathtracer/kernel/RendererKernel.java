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

import static org.dayflower.pathtracer.math.Math2.saturate;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Optional;

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.color.ChromaticSpectralCurve;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;

//TODO: Add Javadocs.
public final class RendererKernel extends AbstractKernel {
//	TODO: Add Javadocs.
	public static final int DEPTH_MAXIMUM = 10;
	
//	TODO: Add Javadocs.
	public static final int DEPTH_MINIMUM = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final float GAMMA = 2.2F;
	private static final float GAMMA_RECIPROCAL = 1.0F / GAMMA;
	private static final float MAXIMUM_COLOR_COMPONENT = 255.0F;
	private static final float MAXIMUM_COLOR_COMPONENT_RECIPROCAL = 1.0F / MAXIMUM_COLOR_COMPONENT;
	private static final int DEPTH_RUSSIAN_ROULETTE = 5;
	private static final int MATERIAL_CLEAR_COAT = 0;
	private static final int MATERIAL_DIFFUSE = 1;
	private static final int MATERIAL_METAL = 2;
	private static final int MATERIAL_REFRACTIVE = 3;
	private static final int MATERIAL_SPECULAR = 4;
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
	
	@Local
	private final boolean isResettingFully;
	@Local
	private final boolean isUsingBoundingVolumeHierarchy;
	private byte[] pixels;
	private final Camera camera;
	@Local
	private final float breakPoint;
	@Local
	private final float gamma;
	@Local
	private final float segmentOffset;
	@Local
	private final float slope;
	@Local
	private final float slopeMatch;
	@Local
	private final float sunDirectionX;
	@Local
	private final float sunDirectionY;
	@Local
	private final float sunDirectionZ;
	@Local
	private final float theta;
	@Local
	private final float zenithRelativeLuminance;
	@Local
	private final float zenithX;
	@Local
	private final float zenithY;
	private final float[] accumulatedPixelColorBs;
	private final float[] accumulatedPixelColorGs;
	private final float[] accumulatedPixelColorRs;
	@Constant
	private final float[] boundingVolumeHierarchy;
	private final float[] cameraArray;
	private final float[] currentPixelColorBs;
	private final float[] currentPixelColorGs;
	private final float[] currentPixelColorRs;
	private final float[] intersections;
	@Constant
	private final float[] matrixRGBToXYZ = new float[12];
	@Constant
	private final float[] matrixXYZToRGB = new float[12];
	@Constant
	private final float[] perezRelativeLuminance;
	@Constant
	private final float[] perezX;
	@Constant
	private final float[] perezY;
	private float[] rays;
	@Constant
	private final float[] s0XYZ = ChromaticSpectralCurve.getS0XYZ();
	@Constant
	private final float[] s1XYZ = ChromaticSpectralCurve.getS1XYZ();
	@Constant
	private final float[] s2XYZ = ChromaticSpectralCurve.getS2XYZ();
	@Constant
	private final float[] shapes;
	private final float[] temporaryColorBs;
	private final float[] temporaryColorGs;
	private final float[] temporaryColorRs;
	@Constant
	private final float[] textures;
	private int depthMaximum = DEPTH_MAXIMUM;
	private int depthMinimum = DEPTH_MAXIMUM;
	@Local
	private int shapesLength;
	@Local
	private final int width;
	@Constant
	private final int[] gammaCurve = new int[256];
	@Constant
	private final int[] gammaCurveReciprocal = new int[256];
	@Constant
	private final int[] permutations0 = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
	@Constant
	private final int[] permutations1 = new int[512];
	private final long[] subSamples;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public RendererKernel(final boolean isResettingFully, final boolean isUsingBoundingVolumeHierarchy, final int width, final int height, final Camera camera, final Scene scene) {
		final CompiledScene compiledScene = CompiledScene.compile(camera, scene);
		
		this.isResettingFully = isResettingFully;
		this.isUsingBoundingVolumeHierarchy = isUsingBoundingVolumeHierarchy;
		this.width = width;
		this.camera = camera;
		this.boundingVolumeHierarchy = compiledScene.getBoundingVolumeHierarchy();
		this.cameraArray = compiledScene.getCamera();
		this.shapes = compiledScene.getShapes();
		this.textures = compiledScene.getTextures();
		this.accumulatedPixelColorBs = new float[width * height];
		this.accumulatedPixelColorGs = new float[width * height];
		this.accumulatedPixelColorRs = new float[width * height];
		this.currentPixelColorBs = new float[width * height];
		this.currentPixelColorGs = new float[width * height];
		this.currentPixelColorRs = new float[width * height];
		this.intersections = new float[width * height * SIZE_INTERSECTION];
		this.rays = new float[width * height * SIZE_RAY];
		this.temporaryColorBs = new float[width * height];
		this.temporaryColorGs = new float[width * height];
		this.temporaryColorRs = new float[width * height];
		this.shapesLength = this.shapes.length;
		this.subSamples = new long[width * height];
		this.breakPoint = 0.00304F;
		this.gamma = 2.4F;
		this.slope = this.breakPoint > 0.0F ? 1.0F / (this.gamma / pow(this.breakPoint, 1.0F / this.gamma - 1.0F) - this.gamma * this.breakPoint + this.breakPoint) : 1.0F;
		this.slopeMatch = this.breakPoint > 0.0F ? this.gamma * this.slope / pow(this.breakPoint, 1.0F / this.gamma - 1.0F) : 1.0F;
		this.segmentOffset = this.breakPoint > 0.0F ? this.slopeMatch * pow(this.breakPoint, 1.0F / this.gamma) - this.slope * this.breakPoint : 0.0F;
		
		final Sky sky = new Sky();
		
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
		
		for(int i = 0; i < 256; i++) {
			final float value = i / 255.0F;
			
			this.gammaCurve[i] = saturate((int)(doRedoGammaCorrection(value) * 255.0F + 0.5F));
			this.gammaCurveReciprocal[i] = saturate((int)(doUndoGammaCorrection(value) * 255.0F + 0.5F));
		}
		
		final float xR = 0.6400F;
		final float yR = 0.3300F;
		final float xG = 0.3000F;
		final float yG = 0.6000F;
		final float xB = 0.1500F;
		final float yB = 0.0600F;
		final float xW = 0.31271F;
		final float yW = 0.32902F;
		final float zR = 1.0F - (xR + yR);
		final float zG = 1.0F - (xG + yG);
		final float zB = 1.0F - (xB + yB);
		final float zW = 1.0F - (xW + yW);
		final float rX = (yG * zB) - (yB * zG);
		final float rY = (xB * zG) - (xG * zB);
		final float rZ = (xG * yB) - (xB * yG);
		final float rW = ((rX * xW) + (rY * yW) + (rZ * zW)) / yW;
		final float gX = (yB * zR) - (yR * zB);
		final float gY = (xR * zB) - (xB * zR);
		final float gZ = (xB * yR) - (xR * yB);
		final float gW = ((gX * xW) + (gY * yW) + (gZ * zW)) / yW;
		final float bX = (yR * zG) - (yG * zR);
		final float bY = (xG * zR) - (xR * zG);
		final float bZ = (xR * yG) - (xG * yR);
		final float bW = ((bX * xW) + (bY * yW) + (bZ * zW)) / yW;
		
		this.matrixRGBToXYZ[ 0] = rX / rW;
		this.matrixRGBToXYZ[ 1] = rY / rW;
		this.matrixRGBToXYZ[ 2] = rZ / rW;
		this.matrixRGBToXYZ[ 3] = gX / gW;
		this.matrixRGBToXYZ[ 4] = gY / gW;
		this.matrixRGBToXYZ[ 5] = gZ / gW;
		this.matrixRGBToXYZ[ 6] = bX / bW;
		this.matrixRGBToXYZ[ 7] = bY / bW;
		this.matrixRGBToXYZ[ 8] = bZ / bW;
		this.matrixRGBToXYZ[ 9] = rW;
		this.matrixRGBToXYZ[10] = gW;
		this.matrixRGBToXYZ[11] = bW;
		
		final float s = 1.0F / (this.matrixRGBToXYZ[0] * (this.matrixRGBToXYZ[4] * this.matrixRGBToXYZ[8] - this.matrixRGBToXYZ[7] * this.matrixRGBToXYZ[5]) - this.matrixRGBToXYZ[1] * (this.matrixRGBToXYZ[3] * this.matrixRGBToXYZ[8] - this.matrixRGBToXYZ[6] * this.matrixRGBToXYZ[5]) + this.matrixRGBToXYZ[2] * (this.matrixRGBToXYZ[3] * this.matrixRGBToXYZ[7] - this.matrixRGBToXYZ[6] * this.matrixRGBToXYZ[4]));
		
		this.matrixXYZToRGB[ 0] = s * (this.matrixRGBToXYZ[4] * this.matrixRGBToXYZ[8] - this.matrixRGBToXYZ[5] * this.matrixRGBToXYZ[7]);
		this.matrixXYZToRGB[ 1] = s * (this.matrixRGBToXYZ[5] * this.matrixRGBToXYZ[6] - this.matrixRGBToXYZ[3] * this.matrixRGBToXYZ[8]);
		this.matrixXYZToRGB[ 2] = s * (this.matrixRGBToXYZ[3] * this.matrixRGBToXYZ[7] - this.matrixRGBToXYZ[4] * this.matrixRGBToXYZ[6]);
		this.matrixXYZToRGB[ 3] = s * (this.matrixRGBToXYZ[2] * this.matrixRGBToXYZ[7] - this.matrixRGBToXYZ[1] * this.matrixRGBToXYZ[8]);
		this.matrixXYZToRGB[ 4] = s * (this.matrixRGBToXYZ[0] * this.matrixRGBToXYZ[8] - this.matrixRGBToXYZ[2] * this.matrixRGBToXYZ[6]);
		this.matrixXYZToRGB[ 5] = s * (this.matrixRGBToXYZ[1] * this.matrixRGBToXYZ[6] - this.matrixRGBToXYZ[0] * this.matrixRGBToXYZ[7]);
		this.matrixXYZToRGB[ 6] = s * (this.matrixRGBToXYZ[1] * this.matrixRGBToXYZ[5] - this.matrixRGBToXYZ[2] * this.matrixRGBToXYZ[4]);
		this.matrixXYZToRGB[ 7] = s * (this.matrixRGBToXYZ[2] * this.matrixRGBToXYZ[3] - this.matrixRGBToXYZ[0] * this.matrixRGBToXYZ[5]);
		this.matrixXYZToRGB[ 8] = s * (this.matrixRGBToXYZ[0] * this.matrixRGBToXYZ[4] - this.matrixRGBToXYZ[1] * this.matrixRGBToXYZ[3]);
		this.matrixXYZToRGB[ 9] = xW;
		this.matrixXYZToRGB[10] = yW;
		this.matrixXYZToRGB[11] = zW;
		
		for(int i = 0; i < this.permutations0.length; i++) {
			this.permutations1[i] = this.permutations0[i];
			this.permutations1[i + this.permutations0.length] = this.permutations0[i];
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public byte[] getPixels() {
		return this.pixels;
	}
	
//	TODO: Add Javadocs.
	public int getDepthMaximum() {
		return this.depthMaximum;
	}
	
//	TODO: Add Javadocs.
	public int getDepthMinimum() {
		return this.depthMinimum;
	}
	
//	TODO: Add Javadocs.
	public Optional<Camera> getCamera() {
		return Optional.ofNullable(this.camera);
	}
	
//	TODO: Add Javadocs.
	public RendererKernel compile(final byte[] pixels, final int width, final int height) {
		this.pixels = pixels;
		
		setExecutionMode(EXECUTION_MODE.GPU);
		setExplicit(true);
		setSeed(System.nanoTime(), width * height);
		
		updateTables();
		
		put(this.pixels);
		put(this.accumulatedPixelColorBs);
		put(this.accumulatedPixelColorGs);
		put(this.accumulatedPixelColorRs);
		put(this.boundingVolumeHierarchy);
		put(this.cameraArray);
		put(this.currentPixelColorBs);
		put(this.currentPixelColorGs);
		put(this.currentPixelColorRs);
		put(this.intersections);
		put(this.matrixRGBToXYZ);
		put(this.matrixXYZToRGB);
		put(this.perezRelativeLuminance);
		put(this.perezX);
		put(this.perezY);
		put(this.rays);
		put(this.s0XYZ);
		put(this.s1XYZ);
		put(this.s2XYZ);
		put(this.shapes);
		put(this.temporaryColorBs);
		put(this.temporaryColorGs);
		put(this.temporaryColorRs);
		put(this.textures);
		put(this.gammaCurve);
		put(this.gammaCurveReciprocal);
		put(this.permutations0);
		put(this.permutations1);
		put(this.subSamples);
		
		return this;
	}
	
//	TODO: Add Javadocs.
	public RendererKernel reset() {
		setDepthMaximum(getDepthMinimum());
		
		for(int i = 0; i < this.subSamples.length; i++) {
			if(this.isResettingFully) {
				this.accumulatedPixelColorRs[i] = 0.0F;
				this.accumulatedPixelColorGs[i] = 0.0F;
				this.accumulatedPixelColorBs[i] = 0.0F;
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
			put(this.accumulatedPixelColorBs);
			put(this.accumulatedPixelColorGs);
			put(this.accumulatedPixelColorRs);
		}
		
		put(this.subSamples);
		
		return this;
	}
	
//	TODO: Add Javadocs.
	@Override
	public void run() {
		final int pixelIndex = getGlobalId();
		
		doCreatePrimaryRay(pixelIndex);
		doPathTracing(pixelIndex);
		doCalculateColor(pixelIndex);
	}
	
//	TODO: Add Javadocs.
	public void setDepthMaximum(final int depthMaximum) {
		this.depthMaximum = depthMaximum;
	}
	
//	TODO: Add Javadocs.
	public void setDepthMinimum(final int depthMinimum) {
		this.depthMinimum = depthMinimum;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private float doIntersect(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
		final int type = (int)(this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_TYPE]);
		
		if(type == Plane.TYPE) {
			return doIntersectPlane(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		}
		
		if(type == Sphere.TYPE) {
			return doIntersectSphere(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		}
		
		if(type == Triangle.TYPE) {
			return doIntersectTriangle(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		}
		
		return INFINITY;
	}
	
	private float doIntersectPlane(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Retrieve the surface normal of the plane:
		final float surfaceNormalX = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL];
		final float surfaceNormalY = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL + 1];
		final float surfaceNormalZ = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL + 2];
		
//		Calculate the dot product between the surface normal and the ray direction:
		final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
		
//		TODO: Write explanation!
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
//			TODO: Write explanation!
			final float aX = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A];
			final float aY = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A + 1];
			final float aZ = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A + 2];
			
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
		final float positionX = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION];
		final float positionY = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 1];
		final float positionZ = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 2];
		
//		TODO: Write explanation!
		final float radius = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_RADIUS];
		
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
//		Retrieve point A of the triangle:
		final float aX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A];
		final float aY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A + 1];
		final float aZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A + 2];
		
//		Retrieve point B of the triangle:
		final float bX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B];
		final float bY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B + 1];
		final float bZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B + 2];
		
//		Retrieve point C of the triangle:
		final float cX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C];
		final float cY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C + 1];
		final float cZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C + 2];
		
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
	
//	TODO: Add Javadocs!
	private float doRedoGammaCorrection(final float value) {
		if(value <= 0.0F) {
			return 0.0F;
		} else if(value >= 1.0F) {
			return 1.0F;
		} else if(value <= this.breakPoint) {
			return value * this.slope;
		} else {
			return this.slopeMatch * pow(value, 1.0F / this.gamma) - this.segmentOffset;
		}
	}
	
//	TODO: Add Javadocs!
	private float doUndoGammaCorrection(final float value) {
		if(value <= 0.0F) {
			return 0.0F;
		} else if(value >= 1.0F) {
			return 1.0F;
		} else if(value <= this.breakPoint * this.slope) {
			return value / this.slope;
		} else {
			return pow((value + this.segmentOffset) / this.slopeMatch, this.gamma);
		}
	}
	
	private void doCalculateColor(final int pixelIndex) {
//		Retrieve the offset to the pixels array:
		final int pixelsOffset = pixelIndex * SIZE_PIXEL;
		
		final float subSample = this.subSamples[pixelIndex];
		
//		Multiply the 'normalized' accumulated pixel color component values with the current sub-sample count:
		this.accumulatedPixelColorRs[pixelIndex] *= subSample;
		this.accumulatedPixelColorGs[pixelIndex] *= subSample;
		this.accumulatedPixelColorBs[pixelIndex] *= subSample;
		
//		Add the current pixel color component values to the accumulated pixel color component values:
		this.accumulatedPixelColorRs[pixelIndex] += this.currentPixelColorRs[pixelIndex];
		this.accumulatedPixelColorGs[pixelIndex] += this.currentPixelColorGs[pixelIndex];
		this.accumulatedPixelColorBs[pixelIndex] += this.currentPixelColorBs[pixelIndex];
		
//		Increment the current sub-sample count by one:
		this.subSamples[pixelIndex] += 1;
		
//		Retrieve the current sub-sample count and calculate its reciprocal (inverse), such that no division is needed further on:
		final float currentSubSamples = subSample + 1.0F;
		final float currentSubSamplesReciprocal = 1.0F / currentSubSamples;
		
//		Multiply the accumulated pixel color component values with the reciprocal of the current sub-sample count to 'normalize' it:
		this.accumulatedPixelColorRs[pixelIndex] *= currentSubSamplesReciprocal;
		this.accumulatedPixelColorGs[pixelIndex] *= currentSubSamplesReciprocal;
		this.accumulatedPixelColorBs[pixelIndex] *= currentSubSamplesReciprocal;
		
//		Retrieve the 'normalized' accumulated pixel color component values again:
		float r = this.accumulatedPixelColorRs[pixelIndex];
		float g = this.accumulatedPixelColorGs[pixelIndex];
		float b = this.accumulatedPixelColorBs[pixelIndex];
		
//		Calculate the maximum component value of the 'normalized' accumulated pixel color component values:
		final float maximumComponentValue = max(r, max(g, b));
		
//		Check if the maximum component value is greater than 1.0:
		if(maximumComponentValue > 1.0F) {
//			Calculate the reciprocal of the maximum component value, such that no division is needed further on:
			final float maximumComponentValueReciprocal = 1.0F / maximumComponentValue;
			
//			Multiply the 'normalized' accumulated pixel color component values with the reciprocal of the maximum component value for Tone Mapping:
			r *= maximumComponentValueReciprocal;
			g *= maximumComponentValueReciprocal;
			b *= maximumComponentValueReciprocal;
		}
		
//		Perform Gamma Correction on the 'normalized' accumulated pixel color components:
		r = pow(r, GAMMA_RECIPROCAL);
		g = pow(g, GAMMA_RECIPROCAL);
		b = pow(b, GAMMA_RECIPROCAL);
		
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
		final float x0 = directionX;
		final float y0 = directionY;
		final float z0 = directionZ < 0.0F ? -directionZ : directionZ;
		
//		TODO: Write explanation!
		final float x1 = x0;
		final float y1 = y0;
		final float z1 = max(z0, 0.001F);
		
//		TODO: Write explanation!
		final float lengthReciprocal = sqrtReciprocal(x1 * x1 + y1 * y1 + z1 * z1);
		
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
		final float x3 = this.s0XYZ[0] + v1 * this.s1XYZ[0] + v2 * this.s2XYZ[0];
		final float y3 = this.s0XYZ[1] + v1 * this.s1XYZ[1] + v2 * this.s2XYZ[1];
		final float z3 = this.s0XYZ[2] + v1 * this.s1XYZ[2] + v2 * this.s2XYZ[2];
		
//		TODO: Write explanation!
		final float y3Reciprocal = 1.0F / y3;
		
//		TODO: Write explanation!
		final float x4 = x3 * relativeLuminance * y3Reciprocal;
		final float y4 = relativeLuminance;
		final float z4 = z3 * relativeLuminance * y3Reciprocal;
		
//		TODO: Write explanation!
		final float r = this.matrixRGBToXYZ[0] * x4 + this.matrixRGBToXYZ[1] * y4 + this.matrixRGBToXYZ[2] * z4;
		final float g = this.matrixRGBToXYZ[3] * x4 + this.matrixRGBToXYZ[4] * y4 + this.matrixRGBToXYZ[5] * z4;
		final float b = this.matrixRGBToXYZ[6] * x4 + this.matrixRGBToXYZ[7] * y4 + this.matrixRGBToXYZ[8] * z4;
		
//		TODO: Write explanation!
		this.temporaryColorRs[pixelIndex] = r;
		this.temporaryColorGs[pixelIndex] = g;
		this.temporaryColorBs[pixelIndex] = b;
	}
	
	private void doCalculateSurfaceProperties(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
		final int type = (int)(this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_TYPE]);
		
		if(type == Plane.TYPE) {
			doCalculateSurfacePropertiesForPlane(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
		
		if(type == Sphere.TYPE) {
			doCalculateSurfacePropertiesForSphere(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
		
		if(type == Triangle.TYPE) {
			doCalculateSurfacePropertiesForTriangle(distance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
		}
	}
	
	private void doCalculateSurfacePropertiesForPlane(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Retrieve the point A of the plane:
		final float a0X = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A];
		final float a0Y = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A + 1];
		final float a0Z = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_A + 2];
		
//		Retrieve the point B of the plane:
		final float b0X = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_B];
		final float b0Y = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_B + 1];
		final float b0Z = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_B + 2];
		
//		Retrieve the point C of the plane:
		final float c0X = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_C];
		final float c0Y = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_C + 1];
		final float c0Z = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_C + 2];
		
//		Retrieve the surface normal:
		final float surfaceNormalX = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL];
		final float surfaceNormalY = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL + 1];
		final float surfaceNormalZ = this.shapes[shapesOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL + 2];
		
//		TODO: Write explanation!
		final float absSurfaceNormalX = abs(surfaceNormalX);
		final float absSurfaceNormalY = abs(surfaceNormalY);
		final float absSurfaceNormalZ = abs(surfaceNormalZ);
		
//		TODO: Write explanation!
		final float a1X = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? a0Y : absSurfaceNormalY > absSurfaceNormalZ ? a0Z : a0X;
		final float a1Y = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? a0Z : absSurfaceNormalY > absSurfaceNormalZ ? a0X : a0Y;
		
//		TODO: Write explanation!
		final float b1X = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? c0Y - a0X : absSurfaceNormalY > absSurfaceNormalZ ? c0Z - a0X : c0X - a0X;
		final float b1Y = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? c0Z - a0Y : absSurfaceNormalY > absSurfaceNormalZ ? c0X - a0Y : c0Y - a0Y;
		
//		TODO: Write explanation!
		final float c1X = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? b0Y - a0X : absSurfaceNormalY > absSurfaceNormalZ ? b0Z - a0X : b0X - a0X;
		final float c1Y = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? b0Z - a0Y : absSurfaceNormalY > absSurfaceNormalZ ? b0X - a0Y : b0Y - a0Y;
		
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
		final float hU = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? surfaceIntersectionPointY : absSurfaceNormalY > absSurfaceNormalZ ? surfaceIntersectionPointZ : surfaceIntersectionPointX;
		final float hV = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ ? surfaceIntersectionPointZ : absSurfaceNormalY > absSurfaceNormalZ ? surfaceIntersectionPointX : surfaceIntersectionPointY;
		
//		Calculate the UV-coordinates:
		final float u = hU * bNU + hV * bNV + bND;
		final float v = hU * cNU + hV * cNV + cND;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT] = surfaceIntersectionPointX;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1] = surfaceIntersectionPointY;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2] = surfaceIntersectionPointZ;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL] = surfaceNormalX;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1] = surfaceNormalY;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2] = surfaceNormalZ;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES] = u;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForSphere(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the surface normal:
		final float surfaceNormal0X = surfaceIntersectionPointX - this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION];
		final float surfaceNormal0Y = surfaceIntersectionPointY - this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 1];
		final float surfaceNormal0Z = surfaceIntersectionPointZ - this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 2];
		final float lengthReciprocal = 1.0F / sqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		final float surfaceNormal1X = surfaceNormal0X * lengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * lengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * lengthReciprocal;
		
//		Calculate the UV-coordinates:
		final float direction0X = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION] - surfaceIntersectionPointX;
		final float direction0Y = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 1] - surfaceIntersectionPointY;
		final float direction0Z = this.shapes[shapesOffset + Sphere.RELATIVE_OFFSET_POSITION + 2] - surfaceIntersectionPointZ;
		final float direction0LengthReciprocal = 1.0F / sqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
		final float direction1X = direction0X * direction0LengthReciprocal;
		final float direction1Y = direction0Y * direction0LengthReciprocal;
		final float direction1Z = direction0Z * direction0LengthReciprocal;
		final float u = 0.5F + atan2(direction1Z, direction1X) * PI_MULTIPLIED_BY_TWO_RECIPROCAL;
		final float v = 0.5F - asin(direction1Y) * PI_RECIPROCAL;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT] = surfaceIntersectionPointX;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1] = surfaceIntersectionPointY;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2] = surfaceIntersectionPointZ;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL] = surfaceNormal1X;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1] = surfaceNormal1Y;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2] = surfaceNormal1Z;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES] = u;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForTriangle(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int intersectionsOffset, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the Barycentric-coordinates:
		final float aX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A];
		final float aY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A + 1];
		final float aZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_A + 2];
		final float bX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B];
		final float bY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B + 1];
		final float bZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_B + 2];
		final float cX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C];
		final float cY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C + 1];
		final float cZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_POINT_C + 2];
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
		
//		Calculate the UV-coordinates:
		final float aU = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_A];
		final float aV = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_A + 1];
		final float bU = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_B];
		final float bV = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_B + 1];
		final float cU = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_C];
		final float cV = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_UV_C + 1];
		final float u1 = w * aU + u0 * bU + v0 * cU;
		final float v1 = w * aV + u0 * bV + v0 * cV;
		
//		Calculate the surface normal:
		final float surfaceNormalAX = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_SURFACE_NORMAL_A];
		final float surfaceNormalAY = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_SURFACE_NORMAL_A + 1];
		final float surfaceNormalAZ = this.shapes[shapesOffset + Triangle.RELATIVE_OFFSET_SURFACE_NORMAL_A + 2];
		final float surfaceNormal0X = edge0Y * edge1Z - edge0Z * edge1Y;
		final float surfaceNormal0Y = edge0Z * edge1X - edge0X * edge1Z;
		final float surfaceNormal0Z = edge0X * edge1Y - edge0Y * edge1X;
		final float surfaceNormal0LengthReciprocal = sqrtReciprocal(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);//1.0F / sqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
		final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal1X * surfaceNormalAX + surfaceNormal1Y * surfaceNormalAY + surfaceNormal1Z * surfaceNormalAZ : 0.0F;
		final float surfaceNormal2X = dotProduct < 0.0F ? -surfaceNormal1X : surfaceNormal1X;
		final float surfaceNormal2Y = dotProduct < 0.0F ? -surfaceNormal1Y : surfaceNormal1Y;
		final float surfaceNormal2Z = dotProduct < 0.0F ? -surfaceNormal1Z : surfaceNormal1Z;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = distance;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT] = surfaceIntersectionPointX;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1] = surfaceIntersectionPointY;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2] = surfaceIntersectionPointZ;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL] = surfaceNormal2X;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1] = surfaceNormal2Y;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2] = surfaceNormal2Z;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES] = u1;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES + 1] = v1;
	}
	
	private void doCalculateTextureColor(final int intersectionsOffset, final int pixelIndex, final int relativeOffsetTextures, final int shapesOffset) {
		final int texturesOffset = (int)(this.shapes[shapesOffset + relativeOffsetTextures]);
		final int textureType = (int)(this.textures[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == CheckerboardTexture.TYPE) {
			doCalculateTextureColorForCheckerboardTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
		
		if(textureType == ImageTexture.TYPE) {
			doCalculateTextureColorForImageTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
		
		if(textureType == SolidTexture.TYPE) {
			doCalculateTextureColorForSolidTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
		}
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForCheckerboardTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final float u = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES];
		final float v = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES + 1];
		
//		TODO: Write explanation!
		final float color0R = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_0];
		final float color0G = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_0 + 1];
		final float color0B = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_0 + 2];
		
//		TODO: Write explanation!
		final float color1R = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_1];
		final float color1G = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_1 + 1];
		final float color1B = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_1 + 2];
		
//		TODO: Write explanation!
		final float degrees = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_DEGREES];
		
//		TODO: Write explanation!
		final float sU = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_U];
		final float sV = this.textures[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_V];
		
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
		
		if(color0R == color1R && color0G == color1G && color0B == color1B) {
//			TODO: Write explanation!
			final float textureMultiplier = isDark ? 0.8F : 1.2F;
			
//			TODO: Write explanation!
			final float r = color0R * textureMultiplier;
			final float g = color0G * textureMultiplier;
			final float b = color0B * textureMultiplier;
			
//			TODO: Write explanation!
			this.temporaryColorRs[pixelIndex] = r;
			this.temporaryColorGs[pixelIndex] = g;
			this.temporaryColorBs[pixelIndex] = b;
		} else {
//			TODO: Write explanation!
			final float r = isDark ? color0R : color1R;
			final float g = isDark ? color0G : color1G;
			final float b = isDark ? color0B : color1B;
			
//			TODO: Write explanation!
			this.temporaryColorRs[pixelIndex] = r;
			this.temporaryColorGs[pixelIndex] = g;
			this.temporaryColorBs[pixelIndex] = b;
		}
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForImageTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final float u = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES];
		final float v = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES + 1];
		
//		TODO: Write explanation!
		final float degrees = this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_DEGREES];
		
//		TODO: Write explanation!
		final float width = this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_WIDTH];
		final float height = this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_HEIGHT];
		
//		TODO: Write explanation!
		final float scaleU = this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_V];
		
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
		final int rGB = (int)(this.textures[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index]);
		
//		TODO: Write explanation!
		final float r = ((rGB >> 16) & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		final float g = ((rGB >> 8) & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		final float b = (rGB & 0xFF) * MAXIMUM_COLOR_COMPONENT_RECIPROCAL;
		
//		TODO: Write explanation!
		this.temporaryColorRs[pixelIndex] = r;
		this.temporaryColorGs[pixelIndex] = g;
		this.temporaryColorBs[pixelIndex] = b;
	}
	
	@SuppressWarnings("unused")
	private void doCalculateTextureColorForSolidTexture(final int intersectionsOffset, final int pixelIndex, final int shapesOffset, final int texturesOffset) {
//		TODO: Write explanation!
		final float r = this.textures[texturesOffset + SolidTexture.RELATIVE_OFFSET_COLOR];
		final float g = this.textures[texturesOffset + SolidTexture.RELATIVE_OFFSET_COLOR + 1];
		final float b = this.textures[texturesOffset + SolidTexture.RELATIVE_OFFSET_COLOR + 2];
		
//		TODO: Write explanation!
		this.temporaryColorRs[pixelIndex] = r;
		this.temporaryColorGs[pixelIndex] = g;
		this.temporaryColorBs[pixelIndex] = b;
	}
	
	private void doCreatePrimaryRay(final int pixelIndex) {
//		Calculate the X- and Y-coordinates on the screen:
		final int x = pixelIndex % this.width;
		final int y = pixelIndex / this.width;
		
//		Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:
		final float eyeX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE];
		final float eyeY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE + 1];
		final float eyeZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE + 2];
		
//		Retrieve the current X-, Y- and Z-coordinates defining the up-vector of the camera lens (eye) in the scene and normalize it:
		final float up0X = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP];
		final float up0Y = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP + 1];
		final float up0Z = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_UP + 2];
		final float up0LengthReciprocal = sqrtReciprocal(up0X * up0X + up0Y * up0Y + up0Z * up0Z);//1.0F / sqrt(up0X * up0X + up0Y * up0Y + up0Z * up0Z);
		final float up1X = up0X * up0LengthReciprocal;
		final float up1Y = up0Y * up0LengthReciprocal;
		final float up1Z = up0Z * up0LengthReciprocal;
		
//		Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float w0X = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W];
		final float w0Y = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1];
		final float w0Z = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2];
		final float w0LengthReciprocal = sqrtReciprocal(w0X * w0X + w0Y * w0Y + w0Z * w0Z);//1.0F / sqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
		final float w1X = w0X * w0LengthReciprocal;
		final float w1Y = w0Y * w0LengthReciprocal;
		final float w1Z = w0Z * w0LengthReciprocal;
		
//		Calculate the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float u0X = w1Y * up1Z - w1Z * up1Y;
		final float u0Y = w1Z * up1X - w1X * up1Z;
		final float u0Z = w1X * up1Y - w1Y * up1X;
		final float u0LengthReciprocal = sqrtReciprocal(u0X * u0X + u0Y * u0Y + u0Z * u0Z);//1.0F / sqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
		final float u1X = u0X * u0LengthReciprocal;
		final float u1Y = u0Y * u0LengthReciprocal;
		final float u1Z = u0Z * u0LengthReciprocal;
		
//		Calculate the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene and normalize it:
		final float v0X = u1Y * w1Z - u1Z * w1Y;
		final float v0Y = u1Z * w1X - u1X * w1Z;
		final float v0Z = u1X * w1Y - u1Y * w1X;
		final float u1LengthReciprocal = sqrtReciprocal(v0X * v0X + v0Y * v0Y + v0Z * v0Z);//1.0F / sqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
		final float v1X = v0X * u1LengthReciprocal;
		final float v1Y = v0Y * u1LengthReciprocal;
		final float v1Z = v0Z * u1LengthReciprocal;
		
//		TODO: Write explanation!
		final float middleX = eyeX + w1X;
		final float middleY = eyeY + w1Y;
		final float middleZ = eyeZ + w1Z;
		
//		Calculate the Field of View:
		final float fieldOfViewX0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW];
		final float fieldOfViewX1 = tan(fieldOfViewX0 * 0.5F * PI_DIVIDED_BY_180);
		final float fieldOfViewY0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 1];
		final float fieldOfViewY1 = tan(-fieldOfViewY0 * 0.5F * PI_DIVIDED_BY_180);
		
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
		final float pointOnPlaneOneUnitAwayFromEyeX = middleX + (horizontalX * (2.0F * sx - 1.0F)) + (verticalX * (2.0F * sy - 1.0F));
		final float pointOnPlaneOneUnitAwayFromEyeY = middleY + (horizontalY * (2.0F * sx - 1.0F)) + (verticalY * (2.0F * sy - 1.0F));
		final float pointOnPlaneOneUnitAwayFromEyeZ = middleZ + (horizontalZ * (2.0F * sx - 1.0F)) + (verticalZ * (2.0F * sy - 1.0F));
		
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
		final float apertureToImagePlane0LengthReciprocal = sqrtReciprocal(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);//1.0F / sqrt(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);
		
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
		final float[] intersections = this.intersections;
		final float[] rays = this.rays;
		
//		Retrieve the maximum depth allowed:
		final int depthMaximum = this.depthMaximum;
		
//		Calculate the current offsets to the intersections and rays arrays:
		final int intersectionsOffset = pixelIndex * SIZE_INTERSECTION;
		final int raysOffset = pixelIndex * SIZE_RAY;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Initialize the origin from the primary ray:
		float originX = rays[raysOffset + RELATIVE_OFFSET_RAY_ORIGIN];
		float originY = rays[raysOffset + RELATIVE_OFFSET_RAY_ORIGIN + 1];
		float originZ = rays[raysOffset + RELATIVE_OFFSET_RAY_ORIGIN + 2];
		
//		Initialize the direction from the primary ray:
		float directionX = rays[raysOffset + RELATIVE_OFFSET_RAY_DIRECTION];
		float directionY = rays[raysOffset + RELATIVE_OFFSET_RAY_DIRECTION + 1];
		float directionZ = rays[raysOffset + RELATIVE_OFFSET_RAY_DIRECTION + 2];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the radiance multiplier to white:
		float radianceMultiplierR = 1.0F;
		float radianceMultiplierG = 1.0F;
		float radianceMultiplierB = 1.0F;
		
//		Run the following while-loop as long as the current depth is less than the maximum depth:
		while(depthCurrent < depthMaximum) {
//			Perform an intersection test:
			doPerformIntersectionTest(pixelIndex, originX, originY, originZ, directionX, directionY, directionZ);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
			final int shapesOffset = (int)(intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || shapesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += radianceMultiplierR * this.temporaryColorRs[pixelIndex];
				pixelColorG += radianceMultiplierG * this.temporaryColorGs[pixelIndex];
				pixelColorB += radianceMultiplierB * this.temporaryColorBs[pixelIndex];
				
//				Update the current pixel color:
				this.currentPixelColorRs[pixelIndex] = max(min(pixelColorR, 255.0F), 0.0F);
				this.currentPixelColorGs[pixelIndex] = max(min(pixelColorG, 255.0F), 0.0F);
				this.currentPixelColorBs[pixelIndex] = max(min(pixelColorB, 255.0F), 0.0F);
				
				return;
			}
			
//			Retrieve the surface intersection point:
			final float surfaceIntersectionPointX = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT];
			final float surfaceIntersectionPointY = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1];
			final float surfaceIntersectionPointZ = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2];
			
//			Retrieve the surface normal:
			final float surfaceNormal0X = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL];
			final float surfaceNormal0Y = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1];
			final float surfaceNormal0Z = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2];
			
//			Calculate the albedo texture color for the intersected shape:
			doCalculateTextureColor(intersectionsOffset, pixelIndex, Shape.RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);
			
//			Get the color of the shape from the albedo texture color that was looked up:
			float albedoColorR = this.temporaryColorRs[pixelIndex];
			float albedoColorG = this.temporaryColorGs[pixelIndex];
			float albedoColorB = this.temporaryColorBs[pixelIndex];
			
//			Retrieve the emission from the intersected shape:
			final float emissionR = this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_EMISSION];
			final float emissionG = this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_EMISSION + 1];
			final float emissionB = this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_EMISSION + 2];
			
//			Add the current radiance multiplied by the emission of the intersected shape to the current pixel color:
			pixelColorR += radianceMultiplierR * emissionR;
			pixelColorG += radianceMultiplierG * emissionG;
			pixelColorB += radianceMultiplierB * emissionB;
			
//			Increment the current depth:
			depthCurrent++;
			
//			Check if the current depth is great enough to perform Russian Roulette to probabilistically terminate the path:
			if(depthCurrent > DEPTH_RUSSIAN_ROULETTE) {
//				Calculate the Russian Roulette Probability Density Function (PDF) using the maximum color component of the albedo of the intersected shape:
				final float probabilityDensityFunction = max(albedoColorR, max(albedoColorG, albedoColorB));
				
//				Calculate a random number that will be used when determinating whether or not the path should be terminated:
				final float random = nextFloat();
				
//				If the random number is greater than or equal to the Russian Roulette PDF, then terminate the path:
				if(random >= probabilityDensityFunction) {
//					Calculate the color for the sky in the current direction:
					doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
					
//					Add the color for the sky to the current pixel color:
					pixelColorR += radianceMultiplierR * this.temporaryColorRs[pixelIndex];
					pixelColorG += radianceMultiplierG * this.temporaryColorGs[pixelIndex];
					pixelColorB += radianceMultiplierB * this.temporaryColorBs[pixelIndex];
					
//					Update the current pixel color:
					this.currentPixelColorRs[pixelIndex] = max(min(pixelColorR, 255.0F), 0.0F);
					this.currentPixelColorGs[pixelIndex] = max(min(pixelColorG, 255.0F), 0.0F);
					this.currentPixelColorBs[pixelIndex] = max(min(pixelColorB, 255.0F), 0.0F);
					
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
			final int material = (int)(this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_MATERIAL]);
			
			if(material == MATERIAL_CLEAR_COAT) {
//				Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
				final float dotProduct = surfaceNormal0X * directionX + surfaceNormal0Y * directionY + surfaceNormal0Z * directionZ;
				
//				Check if the surface normal is correctly oriented:
				final boolean isCorrectlyOriented = dotProduct < 0.0F;
				
//				Retrieve the correctly oriented surface normal:
				final float surfaceNormal1X = isCorrectlyOriented ? surfaceNormal0X : -surfaceNormal0X;
				final float surfaceNormal1Y = isCorrectlyOriented ? surfaceNormal0Y : -surfaceNormal0Y;
				final float surfaceNormal1Z = isCorrectlyOriented ? surfaceNormal0Z : -surfaceNormal0Z;
				
//				Initialize the two hard-coded refractive indices that will be used:
				final float refractiveIndex0 = 1.0F;
				final float refractiveIndex1 = 1.5F;
				
//				TODO: Write explanation!
				final float nnt = refractiveIndex0 / refractiveIndex1;
				
//				TODO: Write explanation!
				final float ddn = surfaceNormal1X * directionX + surfaceNormal1Y * directionY + surfaceNormal1Z * directionZ;
				
//				TODO: Write explanation!
				final float cos2t = 1.0F - nnt * nnt * (1.0F - ddn * ddn);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormal0X * 2.0F * dotProduct;
				final float reflectionDirectionY = directionY - surfaceNormal0Y * 2.0F * dotProduct;
				final float reflectionDirectionZ = directionZ - surfaceNormal0Z * 2.0F * dotProduct;
				
//				Initialize the specular color component values to be used:
				final float specularColorR = 1.0F;
				final float specularColorG = 1.0F;
				final float specularColorB = 1.0F;
				
				if(cos2t < 0.0F) {
//					Multiply the radiance multiplier with the specular color:
					radianceMultiplierR *= specularColorR;
					radianceMultiplierG *= specularColorG;
					radianceMultiplierB *= specularColorB;
					
//					Calculate the new ray origin:
					originX = surfaceIntersectionPointX + surfaceNormal1X * 0.02F;
					originY = surfaceIntersectionPointY + surfaceNormal1Y * 0.02F;
					originZ = surfaceIntersectionPointZ + surfaceNormal1Z * 0.02F;
					
//					Set the new ray direction to the reflection direction:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
				} else {
//					TODO: Write explanation!
					final float a = refractiveIndex1 - refractiveIndex0;
					final float b = refractiveIndex1 + refractiveIndex0;
					
//					TODO: Write explanation!
					final float r0 = (a * a) / (b * b);
					
//					TODO: Write explanation!
					final float angle1 = -ddn;
					final float angle2 = 1.0F - angle1;
					
//					TODO: Write explanation!
					final float reflectance = r0 + (1.0F - r0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					TODO: Write explanation!
					final float transmittance = 1.0F - reflectance;
					
//					TODO: Write explanation!
					final float probability = 0.25F + 0.5F * reflectance;
					
//					TODO: Write explanation!
					final float reflectanceProbability = reflectance / probability;
					
//					TODO: Write explanation!
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					TODO: Write explanation!
					final float random = nextFloat();
					
//					TODO: Write explanation!
					final boolean isReflectionDirection = random < probability;
					
//					TODO: Write explanation!
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					TODO: Write explanation!
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
					if(isReflectionDirection) {
//						TODO: Write explanation!
						radianceMultiplierR *= specularColorR;
						radianceMultiplierG *= specularColorG;
						radianceMultiplierB *= specularColorB;
						
//						TODO: Write explanation!
						originX = surfaceIntersectionPointX + surfaceNormal1X * 0.02F;
						originY = surfaceIntersectionPointY + surfaceNormal1Y * 0.02F;
						originZ = surfaceIntersectionPointZ + surfaceNormal1Z * 0.02F;
						
//						TODO: Write explanation!
						directionX = reflectionDirectionX;
						directionY = reflectionDirectionY;
						directionZ = reflectionDirectionZ;
					} else {
//						TODO: Write explanation!
						final float wX = isCorrectlyOriented ? surfaceNormal0X : surfaceNormal0X * -1.0F;
						final float wY = isCorrectlyOriented ? surfaceNormal0Y : surfaceNormal0Y * -1.0F;
						final float wZ = isCorrectlyOriented ? surfaceNormal0Z : surfaceNormal0Z * -1.0F;
						
//						TODO: Write explanation!
						final float wLengthReciprocal = 1.0F / sqrt(wX * wX + wY * wY + wZ * wZ);
						
//						TODO: Write explanation!
						final float w0X = wX * wLengthReciprocal;
						final float w0Y = wY * wLengthReciprocal;
						final float w0Z = wZ * wLengthReciprocal;
						
//						TODO: Write explanation!
						final boolean isY = abs(w0X) > 0.1F;
						
//						TODO: Write explanation!
						final float x2 = isY ? 0.0F : 1.0F;
						final float y2 = isY ? 1.0F : 0.0F;
						final float z2 = 0.0F;
						
//						TODO: Write explanation!
						final float x3 = y2 * w0Z - z2 * w0Y;
						final float y3 = z2 * w0X - x2 * w0Z;
						final float z3 = x2 * w0Y - y2 * w0X;
						
//						TODO: Write explanation!
						final float lengthReciprocal3 = 1.0F / sqrt(x3 * x3 + y3 * y3 + z3 * z3);
						
//						TODO: Write explanation!
						final float uX = x3 * lengthReciprocal3;
						final float uY = y3 * lengthReciprocal3;
						final float uZ = z3 * lengthReciprocal3;
						
//						TODO: Write explanation!
						final float vX = wY * uZ - wZ * uY;
						final float vY = wZ * uX - wX * uZ;
						final float vZ = wX * uY - wY * uX;
						
//						TODO: Write explanation!
						final float random0 = 2.0F * PI * nextFloat();
						final float random1 = nextFloat();
						
//						TODO: Write explanation!
						final float random1Squared = sqrt(random1);
						
//						TODO: Write explanation!
						final float cos0 = cos(random0);
						final float sin0 = sin(random0);
						
//						TODO: Write explanation!
						final float sqrt0 = sqrt(1.0F - random1);
						
//						TODO: Write explanation!
						final float x4 = uX * cos0 * random1Squared + vX * sin0 * random1Squared + wX * sqrt0;
						final float y4 = uY * cos0 * random1Squared + vY * sin0 * random1Squared + wY * sqrt0;
						final float z4 = uZ * cos0 * random1Squared + vZ * sin0 * random1Squared + wZ * sqrt0;
						
//						TODO: Write explanation!
						final float lengthReciprocal4 = 1.0F / sqrt(x4 * x4 + y4 * y4 + z4 * z4);
						
//						TODO: Write explanation!
						originX = surfaceIntersectionPointX + wX * 0.01F;
						originY = surfaceIntersectionPointY + wY * 0.01F;
						originZ = surfaceIntersectionPointZ + wZ * 0.01F;
						
//						TODO: Write explanation!
						directionX = x4 * lengthReciprocal4;
						directionY = y4 * lengthReciprocal4;
						directionZ = z4 * lengthReciprocal4;
						
//						TODO: Write explanation!
						radianceMultiplierR *= albedoColorR;
						radianceMultiplierG *= albedoColorG;
						radianceMultiplierB *= albedoColorB;
					}
				}
				
//				TODO: Write explanation!
				final float normalX = directionX;
				final float normalY = directionY;
				final float normalZ = directionZ;
				
//				TODO: Write explanation!
				final float scale = 0.5F;
				
//				TODO: Write explanation!
				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				final float vector0Z = 0.0F;
				
//				TODO: Write explanation!
				final float vector1X = normalY * vector0Z - normalZ * vector0Y;
				final float vector1Y = normalZ * vector0X - normalX * vector0Z;
				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
				final float phi = nextFloat() * PI_MULTIPLIED_BY_TWO;
				final float theta = nextFloat();
				
//				TODO: Write explanation!
				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
				final float vector3X = cos(phi) * radius;
				final float vector3Y = sin(phi) * radius;
				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
				radianceMultiplierR *= this.temporaryColorRs[pixelIndex];
				radianceMultiplierG *= this.temporaryColorGs[pixelIndex];
				radianceMultiplierB *= this.temporaryColorBs[pixelIndex];
			} else if(material == MATERIAL_DIFFUSE) {
//				TODO: Write explanation!
				final float random0 = 2.0F * PI * nextFloat();
				final float random1 = nextFloat();
				
//				TODO: Write explanation!
				final float random1Squared = sqrt(random1);
				
//				TODO: Write explanation!
				final float dotProduct = surfaceNormal0X * directionX + surfaceNormal0Y * directionY + surfaceNormal0Z * directionZ;
				
//				TODO: Write explanation!
				final boolean isCorrectlyOriented = dotProduct < 0.0F;
				
//				TODO: Write explanation!
				final float wX = isCorrectlyOriented ? surfaceNormal0X : surfaceNormal0X * -1.0F;
				final float wY = isCorrectlyOriented ? surfaceNormal0Y : surfaceNormal0Y * -1.0F;
				final float wZ = isCorrectlyOriented ? surfaceNormal0Z : surfaceNormal0Z * -1.0F;
				
//				TODO: Write explanation!
				final boolean isY = abs(wX) > 0.1F;
				
//				TODO: Write explanation!
				final float x1 = isY ? 0.0F : 1.0F;
				final float y1 = isY ? 1.0F : 0.0F;
				final float z1 = 0.0F;
				
//				TODO: Write explanation!
				final float x2 = y1 * wZ - z1 * wY;
				final float y2 = z1 * wX - x1 * wZ;
				final float z2 = x1 * wY - y1 * wX;
				
//				TODO: Write explanation!
				final float lengthReciprocal2 = 1.0F / sqrt(x2 * x2 + y2 * y2 + z2 * z2);
				
//				TODO: Write explanation!
				final float uX = x2 * lengthReciprocal2;
				final float uY = y2 * lengthReciprocal2;
				final float uZ = z2 * lengthReciprocal2;
				
//				TODO: Write explanation!
				final float vX = wY * uZ - wZ * uY;
				final float vY = wZ * uX - wX * uZ;
				final float vZ = wX * uY - wY * uX;
				
//				TODO: Write explanation!
				final float cos0 = cos(random0);
				final float sin0 = sin(random0);
				
//				TODO: Write explanation!
				final float sqrt0 = sqrt(1.0F - random1);
				
//				TODO: Write explanation!
				final float x3 = uX * cos0 * random1Squared + vX * sin0 * random1Squared + wX * sqrt0;
				final float y3 = uY * cos0 * random1Squared + vY * sin0 * random1Squared + wY * sqrt0;
				final float z3 = uZ * cos0 * random1Squared + vZ * sin0 * random1Squared + wZ * sqrt0;
				
//				TODO: Write explanation!
				final float lengthReciprocal3 = 1.0F / sqrt(x3 * x3 + y3 * y3 + z3 * z3);
				
//				TODO: Write explanation!
				originX = surfaceIntersectionPointX + wX * 0.01F;
				originY = surfaceIntersectionPointY + wY * 0.01F;
				originZ = surfaceIntersectionPointZ + wZ * 0.01F;
				
//				TODO: Write explanation!
				directionX = x3 * lengthReciprocal3;
				directionY = y3 * lengthReciprocal3;
				directionZ = z3 * lengthReciprocal3;
				
//				TODO: Write explanation!
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
				
//				TODO: Write explanation!
				final float normalX = directionX;
				final float normalY = directionY;
				final float normalZ = directionZ;
				
//				TODO: Write explanation!
				final float scale = 0.5F;
				
//				TODO: Write explanation!
				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				final float vector0Z = 0.0F;
				
//				TODO: Write explanation!
				final float vector1X = normalY * vector0Z - normalZ * vector0Y;
				final float vector1Y = normalZ * vector0X - normalX * vector0Z;
				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
				final float phi = nextFloat() * PI_MULTIPLIED_BY_TWO;
				final float theta = nextFloat();
				
//				TODO: Write explanation!
				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
				final float vector3X = cos(phi) * radius;
				final float vector3Y = sin(phi) * radius;
				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
				radianceMultiplierR *= this.temporaryColorRs[pixelIndex];
				radianceMultiplierG *= this.temporaryColorGs[pixelIndex];
				radianceMultiplierB *= this.temporaryColorBs[pixelIndex];
			} else if(material == MATERIAL_METAL) {
//				TODO: Write explanation!
				final float random0 = 2.0F * PI * nextFloat();
				final float random1 = nextFloat();
				
//				TODO: Write explanation!
				final float phongExponent = 20.0F;
				
//				TODO: Write explanation!
				final float cosTheta = pow(1.0F - random1, 1.0F / (phongExponent + 1.0F));
				final float sinTheta = sqrt(1.0F - cosTheta * cosTheta);
				
//				TODO: Write explanation!
				final float dotProduct = surfaceNormal0X * directionX + surfaceNormal0Y * directionY + surfaceNormal0Z * directionZ;
				
//				TODO: Write explanation!
				final float w0X = directionX - surfaceNormal0X * 2.0F * dotProduct;
				final float w0Y = directionY - surfaceNormal0Y * 2.0F * dotProduct;
				final float w0Z = directionZ - surfaceNormal0Z * 2.0F * dotProduct;
				
//				TODO: Write explanation!
				final float lengthReciprocal2 = 1.0F / sqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
				
//				TODO: Write explanation!
				final float w1X = w0X * lengthReciprocal2;
				final float w1Y = w0Y * lengthReciprocal2;
				final float w1Z = w0Z * lengthReciprocal2;
				
//				TODO: Write explanation!
				final boolean isY = abs(w1X) > 0.1F;
				
//				TODO: Write explanation!
				final float u0X = isY ? 0.0F : 1.0F;
				final float u0Y = isY ? 1.0F : 0.0F;
				final float u0Z = 0.0F;
				
//				TODO: Write explanation!
				final float u1X = u0Y * w1Z - u0Z * w1Y;
				final float u1Y = u0Z * w1X - u0X * w1Z;
				final float u1Z = u0X * w1Y - u0Y * w1X;
				
//				TODO: Write explanation!
				final float lengthReciprocal3 = 1.0F / sqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
				
//				TODO: Write explanation!
				final float u2X = u1X * lengthReciprocal3;
				final float u2Y = u1Y * lengthReciprocal3;
				final float u2Z = u1Z * lengthReciprocal3;
				
//				TODO: Write explanation!
				final float v0X = w1Y * u2Z - w1Z * u2Y;
				final float v0Y = w1Z * u2X - w1X * u2Z;
				final float v0Z = w1X * u2Y - w1Y * u2X;
				
//				TODO: Write explanation!
				final float cosRandom0 = cos(random0);
				final float sinRandom0 = sin(random0);
				
//				TODO: Write explanation!
				final float direction0X = u2X * cosRandom0 * sinTheta + v0X * sinRandom0 * sinTheta + w1X * cosTheta;
				final float direction0Y = u2Y * cosRandom0 * sinTheta + v0Y * sinRandom0 * sinTheta + w1Y * cosTheta;
				final float direction0Z = u2Z * cosRandom0 * sinTheta + v0Z * sinRandom0 * sinTheta + w1Z * cosTheta;
				
//				TODO: Write explanation!
				final float lengthReciprocal4 = 1.0F / sqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
				
//				TODO: Write explanation!
				originX = surfaceIntersectionPointX + w1X * 0.01F;
				originY = surfaceIntersectionPointY + w1Y * 0.01F;
				originZ = surfaceIntersectionPointZ + w1Z * 0.01F;
				
//				TODO: Write explanation!
				directionX = direction0X * lengthReciprocal4;
				directionY = direction0Y * lengthReciprocal4;
				directionZ = direction0Z * lengthReciprocal4;
				
//				TODO: Write explanation!
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
				
//				TODO: Write explanation!
				final float normalX = directionX;
				final float normalY = directionY;
				final float normalZ = directionZ;
				
//				TODO: Write explanation!
				final float scale = 0.5F;
				
//				TODO: Write explanation!
				final float vector0X = normalX < 0.5F ? 1.0F : 0.0F;
				final float vector0Y = normalX < 0.5F ? 0.0F : 1.0F;
				final float vector0Z = 0.0F;
				
//				TODO: Write explanation!
				final float vector1X = normalY * vector0Z - normalZ * vector0Y;
				final float vector1Y = normalZ * vector0X - normalX * vector0Z;
				final float vector1Z = normalX * vector0Y - normalY * vector0X;
				
//				TODO: Write explanation!
				final float vector2X = normalY * vector1Z - normalZ * vector1Y;
				final float vector2Y = normalZ * vector1X - normalX * vector1Z;
				final float vector2Z = normalX * vector1Y - normalY * vector1X;
				
//				TODO: Write explanation!
				final float phi = nextFloat() * PI_MULTIPLIED_BY_TWO;
				final float theta = nextFloat();
				
//				TODO: Write explanation!
				final float radius = sqrt(1.0F - theta) * scale;
				
//				TODO: Write explanation!
				final float vector3X = cos(phi) * radius;
				final float vector3Y = sin(phi) * radius;
				final float vector3Z = sqrt(radius);
				
//				TODO: Write explanation!
				final float vector4X = vector1X * vector3X + vector2X * vector3Y + normalX * vector3Z;
				final float vector4Y = vector1Y * vector3X + vector2Y * vector3Y + normalY * vector3Z;
				final float vector4Z = vector1Z * vector3X + vector2Z * vector3Y + normalZ * vector3Z;
				
//				TODO: Write explanation!
				doCalculateColorForSky(pixelIndex, vector4X, vector4Y, vector4Z);
				
//				TODO: Write explanation!
				radianceMultiplierR *= this.temporaryColorRs[pixelIndex];
				radianceMultiplierG *= this.temporaryColorGs[pixelIndex];
				radianceMultiplierB *= this.temporaryColorBs[pixelIndex];
			} else if(material == MATERIAL_REFRACTIVE) {
//				TODO: Write explanation!
				final float dotProduct = surfaceNormal0X * directionX + surfaceNormal0Y * directionY + surfaceNormal0Z * directionZ;
				
//				TODO: Write explanation!
				final boolean isCorrectlyOriented = dotProduct < 0.0F;
				
//				TODO: Write explanation!
				final float surfaceNormal1X = isCorrectlyOriented ? surfaceNormal0X : -surfaceNormal0X;
				final float surfaceNormal1Y = isCorrectlyOriented ? surfaceNormal0Y : -surfaceNormal0Y;
				final float surfaceNormal1Z = isCorrectlyOriented ? surfaceNormal0Z : -surfaceNormal0Z;
				
//				TODO: Write explanation!
				final boolean isGoingIn = surfaceNormal0X * surfaceNormal1X + surfaceNormal0Y * surfaceNormal1Y + surfaceNormal0Z * surfaceNormal1Z > 0.0F;
				
//				TODO: Write explanation!
				final float refractiveIndex0 = 1.0F;
				final float refractiveIndex1 = 1.5F;
				
//				TODO: Write explanation!
				final float nnt = isGoingIn ? refractiveIndex0 / refractiveIndex1 : refractiveIndex1 / refractiveIndex0;
				
//				TODO: Write explanation!
				final float ddn = surfaceNormal1X * directionX + surfaceNormal1Y * directionY + surfaceNormal1Z * directionZ;
				
//				TODO: Write explanation!
				final float cos2t = 1.0F - nnt * nnt * (1.0F - ddn * ddn);
				
//				TODO: Write explanation!
				final float reflectionDirectionX = directionX - surfaceNormal0X * 2.0F * dotProduct;
				final float reflectionDirectionY = directionY - surfaceNormal0Y * 2.0F * dotProduct;
				final float reflectionDirectionZ = directionZ - surfaceNormal0Z * 2.0F * dotProduct;
				
				if(cos2t < 0.0F) {
//					TODO: Write explanation!
					originX = surfaceIntersectionPointX + surfaceNormal1X * 0.02F;
					originY = surfaceIntersectionPointY + surfaceNormal1Y * 0.02F;
					originZ = surfaceIntersectionPointZ + surfaceNormal1Z * 0.02F;
					
//					TODO: Write explanation!
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
				} else {
//					TODO: Write explanation!
					final float scalar = isGoingIn ? ddn * nnt + sqrt(cos2t) : -(ddn * nnt + sqrt(cos2t));
					
//					TODO: Write explanation!
					final float x1 = directionX * nnt - surfaceNormal0X * scalar;
					final float y1 = directionY * nnt - surfaceNormal0Y * scalar;
					final float z1 = directionZ * nnt - surfaceNormal0Z * scalar;
					
//					TODO: Write explanation!
					final float lengthReciprocal1 = 1.0F / sqrt(x1 * x1 + y1 * y1 + z1 * z1);
					
//					TODO: Write explanation!
					final float transmissionDirectionX = x1 * lengthReciprocal1;
					final float transmissionDirectionY = y1 * lengthReciprocal1;
					final float transmissionDirectionZ = z1 * lengthReciprocal1;
					
//					TODO: Write explanation!
					final float a = refractiveIndex1 - refractiveIndex0;
					final float b = refractiveIndex1 + refractiveIndex0;
					
//					TODO: Write explanation!
					final float r0 = (a * a) / (b * b);
					
//					TODO: Write explanation!
					final float angle1 = (isGoingIn ? -ddn : transmissionDirectionX * surfaceNormal0X + transmissionDirectionY * surfaceNormal0Y + transmissionDirectionZ * surfaceNormal0Z);
					final float angle2 = 1.0F - angle1;
					
//					TODO: Write explanation!
					final float reflectance = r0 + (1.0F - r0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					TODO: Write explanation!
					final float transmittance = 1.0F - reflectance;
					
//					TODO: Write explanation!
					final float probability = 0.25F + 0.5F * reflectance;
					
//					TODO: Write explanation!
					final float reflectanceProbability = reflectance / probability;
					
//					TODO: Write explanation!
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					TODO: Write explanation!
					final float random = nextFloat();
					
//					TODO: Write explanation!
					final boolean isReflectionDirection = random < probability;
					
//					TODO: Write explanation!
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					TODO: Write explanation!
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
					if(isReflectionDirection) {
//						TODO: Write explanation!
						originX = surfaceIntersectionPointX + surfaceNormal1X * 0.01F;
						originY = surfaceIntersectionPointY + surfaceNormal1Y * 0.01F;
						originZ = surfaceIntersectionPointZ + surfaceNormal1Z * 0.01F;
						
//						TODO: Write explanation!
						directionX = reflectionDirectionX;
						directionY = reflectionDirectionY;
						directionZ = reflectionDirectionZ;
					} else {
//						TODO: Write explanation!
						originX = surfaceIntersectionPointX + surfaceNormal1X * 0.000001F;
						originY = surfaceIntersectionPointY + surfaceNormal1Y * 0.000001F;
						originZ = surfaceIntersectionPointZ + surfaceNormal1Z * 0.000001F;
						
//						TODO: Write explanation!
						directionX = transmissionDirectionX;
						directionY = transmissionDirectionY;
						directionZ = transmissionDirectionZ;
					}
				}
				
//				TODO: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent;
			} else if(material == MATERIAL_SPECULAR) {
//				TODO: Write explanation!
				final float dotProduct = surfaceNormal0X * directionX + surfaceNormal0Y * directionY + surfaceNormal0Z * directionZ;
				
//				TODO: Write explanation!
				originX = surfaceIntersectionPointX + surfaceNormal0X * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormal0Y * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormal0Z * 0.000001F;
				
//				TODO: Write explanation!
				directionX = directionX - surfaceNormal0X * 2.0F * dotProduct;
				directionY = directionY - surfaceNormal0Y * 2.0F * dotProduct;
				directionZ = directionZ - surfaceNormal0Z * 2.0F * dotProduct;
				
//				TODO: Write explanation!
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			}
		}
		
//		Calculate the color for the sky in the current direction:
		doCalculateColorForSky(pixelIndex, directionX, directionY, directionZ);
		
//		Add the color for the sky to the current pixel color:
		pixelColorR += radianceMultiplierR * this.temporaryColorRs[pixelIndex];
		pixelColorG += radianceMultiplierG * this.temporaryColorGs[pixelIndex];
		pixelColorB += radianceMultiplierB * this.temporaryColorBs[pixelIndex];
		
//		Update the current pixel color:
		this.currentPixelColorRs[pixelIndex] = max(min(pixelColorR, 255.0F), 0.0F);
		this.currentPixelColorGs[pixelIndex] = max(min(pixelColorG, 255.0F), 0.0F);
		this.currentPixelColorBs[pixelIndex] = max(min(pixelColorB, 255.0F), 0.0F);
	}
	
	private void doPerformIntersectionTest(final int pixelIndex, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
		if(this.isUsingBoundingVolumeHierarchy) {
			doPerformIntersectionTestUsingBoundingVolumeHierarchy(pixelIndex, originX, originY, originZ, directionX, directionY, directionZ);
		} else {
			doPerformIntersectionTestWithoutAccelerationStructure(pixelIndex, originX, originY, originZ, directionX, directionY, directionZ);
		}
	}
	
	private void doPerformIntersectionTestUsingBoundingVolumeHierarchy(final int pixelIndex, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final int intersectionsOffset = pixelIndex * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		float minimumDistance = INFINITY;
		
//		TODO: Write explanation!
		int shapesOffset = -1;
		
		final float directionReciprocalX = 1.0F / directionX;
		final float directionReciprocalY = 1.0F / directionY;
		final float directionReciprocalZ = 1.0F / directionZ;
		
		int boundingVolumeHierarchyOffset = 0;
		
		while(boundingVolumeHierarchyOffset != -1) {
			final float minimumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 4];
			final float minimumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 5];
			final float minimumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 6];
			
			final float maximumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 7];
			final float maximumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8];
			final float maximumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9];
			
			final float t0X = (minimumX - originX) * directionReciprocalX;
			final float t0Y = (minimumY - originY) * directionReciprocalY;
			final float t0Z = (minimumZ - originZ) * directionReciprocalZ;
			
			final float t1X = (maximumX - originX) * directionReciprocalX;
			final float t1Y = (maximumY - originY) * directionReciprocalY;
			final float t1Z = (maximumZ - originZ) * directionReciprocalZ;
			
			final float tMaximumX = max(t0X, t1X);
			final float tMinimumX = min(t0X, t1X);
			
			final float tMaximumY = max(t0Y, t1Y);
			final float tMinimumY = min(t0Y, t1Y);
			
			final float tMaximumZ = max(t0Z, t1Z);
			final float tMinimumZ = min(t0Z, t1Z);
			
			final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));
			final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));
			
			if(tMaximum >= tMinimum) {
				final int type = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset]);
				
				if(type == 1) {
					final int offsetLeft = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 10]);
					final int offsetRight = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 11]);
					
					if(offsetLeft != -1) {
						boundingVolumeHierarchyOffset = offsetLeft;
					} else if(offsetRight != -1) {
						boundingVolumeHierarchyOffset = offsetRight;
					} else {
						boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3]);
					}
				}
				
				if(type == 2) {
					final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 10]);
					
					for(int i = 0; i < triangleCount; i++) {
						final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 11 + i]);
						
						final float currentDistance = doIntersect(offset, originX, originY, originZ, directionX, directionY, directionZ);
						
//						TODO: Write explanation!
						if(currentDistance < minimumDistance) {
//							TODO: Write explanation!
							minimumDistance = currentDistance;
							
//							TODO: Write explanation!
							shapesOffset = offset;
						}
					}
					
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3]);
				}
				
				if(type != 1 && type != 2) {
					boundingVolumeHierarchyOffset = -1;
				}
				
//				TODO: Find out why the "child list broken" Exception occurs if the following line is not present!
				boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset;
			} else {
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3]);
			}
		}
		
		if(minimumDistance < INFINITY && shapesOffset > -1) {
			doCalculateSurfaceProperties(minimumDistance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
			doPerformNormalMapping(intersectionsOffset, pixelIndex, shapesOffset);
			doPerformPerlinNoiseNormalMapping(intersectionsOffset, shapesOffset);
		} else {
			final float[] intersections = this.intersections;
			
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;
		}
	}
	
	private void doPerformIntersectionTestWithoutAccelerationStructure(final int pixelIndex, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		TODO: Write explanation!
		final int intersectionsOffset = pixelIndex * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		float minimumDistance = INFINITY;
		
//		TODO: Write explanation!
		int shapesOffset = -1;
		
//		TODO: Write explanation!
		for(int i = 0, j = 0; i < this.shapesLength; i += j) {
//			TODO: Write explanation!
			j = (int)(this.shapes[i + Shape.RELATIVE_OFFSET_SIZE]);
			
//			TODO: Write explanation!
			final float currentDistance = doIntersect(i, originX, originY, originZ, directionX, directionY, directionZ);
			
//			TODO: Write explanation!
			if(currentDistance < minimumDistance) {
//				TODO: Write explanation!
				minimumDistance = currentDistance;
				
//				TODO: Write explanation!
				shapesOffset = i;
			}
		}
		
		if(minimumDistance < INFINITY && shapesOffset > -1) {
			doCalculateSurfaceProperties(minimumDistance, originX, originY, originZ, directionX, directionY, directionZ, intersectionsOffset, shapesOffset);
			doPerformNormalMapping(intersectionsOffset, pixelIndex, shapesOffset);
			doPerformPerlinNoiseNormalMapping(intersectionsOffset, shapesOffset);
		} else {
			this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
			this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;
		}
	}
	
	private void doPerformNormalMapping(final int intersectionsOffset, final int pixelIndex, final int shapesOffset) {
//		TODO: Write explanation!
		final int texturesOffset = (int)(this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL]);
		final int textureType = (int)(this.textures[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == CheckerboardTexture.TYPE || textureType == ImageTexture.TYPE) {
			if(textureType == CheckerboardTexture.TYPE) {
				doCalculateTextureColorForCheckerboardTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
			}
			
			if(textureType == ImageTexture.TYPE) {
				doCalculateTextureColorForImageTexture(intersectionsOffset, pixelIndex, shapesOffset, texturesOffset);
			}
			
			final float[] intersections = this.intersections;
			
//			TODO: Write explanation!
			final float r = 2.0F * this.temporaryColorRs[pixelIndex] - 1.0F;
			final float g = 2.0F * this.temporaryColorGs[pixelIndex] - 1.0F;
			final float b = 2.0F * this.temporaryColorBs[pixelIndex] - 1.0F;
			
//			TODO: Write explanation!
			final float wX = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL];
			final float wY = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1];
			final float wZ = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2];
			
//			TODO: Write explanation!
			final float absWX = abs(wX);
			final float absWY = abs(wY);
			final float absWZ = abs(wZ);
			
//			TODO: Write explanation!
			final float v0X = absWX < absWY && absWX < absWZ ? 0.0F : absWY < absWZ ? wZ : wY;
			final float v0Y = absWX < absWY && absWX < absWZ ? wZ : absWY < absWZ ? 0.0F : -wX;
			final float v0Z = absWX < absWY && absWX < absWZ ? -wY : absWY < absWZ ? -wX : 0.0F;
			
//			TODO: Write explanation!
			final float v0LengthReciprocal = 1.0F / sqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
			
//			TODO: Write explanation!
			final float v1X = v0X * v0LengthReciprocal;
			final float v1Y = v0Y * v0LengthReciprocal;
			final float v1Z = v0Z * v0LengthReciprocal;
			
//			TODO: Write explanation!
			final float u0X = v1Y * wZ - v1Z * wY;
			final float u0Y = v1Z * wX - v1X * wZ;
			final float u0Z = v1X * wY - v1Y * wX;
			
//			TODO: Write explanation!
			final float u0LengthReciprocal = 1.0F / sqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
			
//			TODO: Write explanation!
			final float u1X = u0X * u0LengthReciprocal;
			final float u1Y = u0Y * u0LengthReciprocal;
			final float u1Z = u0Z * u0LengthReciprocal;
			
//			TODO: Write explanation!
			final float surfaceNormal0X = r * u1X + g * v1X + b * wX;
			final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;
			final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;
			
//			TODO: Write explanation!
			final float surfaceNormal0LengthReciprocal = 1.0F / sqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
			
//			TODO: Write explanation!
			final float surfaceNormal1X = surfaceNormal0X == 0.0F && surfaceNormal0Y == 0.0F && surfaceNormal0Z == 0.0F ? wX : surfaceNormal0X * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Y = surfaceNormal0X == 0.0F && surfaceNormal0Y == 0.0F && surfaceNormal0Z == 0.0F ? wY : surfaceNormal0Y * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Z = surfaceNormal0X == 0.0F && surfaceNormal0Y == 0.0F && surfaceNormal0Z == 0.0F ? wZ : surfaceNormal0Z * surfaceNormal0LengthReciprocal;
			
//			TODO: Write explanation!
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT] = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT] + surfaceNormal1X * 3.0F;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1] = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1] + surfaceNormal1Y * 3.0F;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2] = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2] + surfaceNormal1Z * 3.0F;
			
//			TODO: Write explanation!
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL] = surfaceNormal1X;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1] = surfaceNormal1Y;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2] = surfaceNormal1Z;
		}
	}
	
	private void doPerformPerlinNoiseNormalMapping(final int intersectionsOffset, final int shapesOffset) {
		final float[] intersections = this.intersections;
		
//		TODO: Write explanation!
		final float amount = this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT];
		
//		TODO: Write explanation!
		final float scale = this.shapes[shapesOffset + Shape.RELATIVE_OFFSET_PERLIN_NOISE_SCALE];
		
//		TODO: Write explanation!
		if(amount > 0.0F && scale > 0.0F) {
//			TODO: Write explanation!
			final float x0 = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT];
			final float y0 = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 1];
			final float z0 = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT + 2];
			
			final float scaleReciprocal = 1.0F / scale;
			
//			TODO: Write explanation!
			final float x1 = x0 * scaleReciprocal;
			final float y1 = y0 * scaleReciprocal;
			final float z1 = z0 * scaleReciprocal;
			
//			TODO: Write explanation!
			final float noiseX = doPerlinNoise(x1, y1, z1);
			final float noiseY = doPerlinNoise(y1, z1, x1);
			final float noiseZ = doPerlinNoise(z1, x1, y1);
			
//			TODO: Write explanation!
			final float surfaceNormal0X = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL];
			final float surfaceNormal0Y = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1];
			final float surfaceNormal0Z = intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2];
			
//			TODO: Write explanation!
			final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;
			final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;
			final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;
			
//			TODO: Write explanation!
			final float surfaceNormal1LengthReciprocal = 1.0F / sqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);
			
//			TODO: Write explanation!
			final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;
			
//			TODO: Write explanation!
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL] = surfaceNormal2X;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 1] = surfaceNormal2Y;
			intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL + 2] = surfaceNormal2Z;
		}
	}
}