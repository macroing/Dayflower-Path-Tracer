/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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

import java.io.File;
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.RGBColorSpace;
import org.dayflower.pathtracer.math.Math2;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;

/**
 * An extension of the {@code AbstractKernel} class that performs Path Tracing, Ray Casting and Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class RendererKernel extends AbstractRendererKernel {
//	private static final float MAXIMUM_COLOR_COMPONENT = 255.0F;
//	private static final float MAXIMUM_COLOR_COMPONENT_RECIPROCAL = 1.0F / MAXIMUM_COLOR_COMPONENT;
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
	private static final int RENDERER_PATH_TRACER = 1;
	private static final int RENDERER_RAY_CASTER = 2;
	private static final int RENDERER_RAY_MARCHER = 3;
	private static final int SHADING_FLAT = 1;
	private static final int SHADING_GOURAUD = 2;
	private static final int SIZE_INTERSECTION = 10;
	private static final int SIZE_PIXEL = 4;
	private static final int SIZE_RAY = 6;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE = 1;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_LINEAR = 2;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_1 = 3;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2 = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final boolean isResettingFully;
	private byte[] pixels;
	private final CompiledScene compiledScene;
	private float amplitude;
	private final float breakPoint;
	private float frequency;
	private float gain;
	private final float gamma;
	private float lacunarity;
	private float orthoNormalBasisUX;
	private float orthoNormalBasisUY;
	private float orthoNormalBasisUZ;
	private float orthoNormalBasisVX;
	private float orthoNormalBasisVY;
	private float orthoNormalBasisVZ;
	private float orthoNormalBasisWX;
	private float orthoNormalBasisWY;
	private float orthoNormalBasisWZ;
	private final float segmentOffset;
	private final float slope;
	private final float slopeMatch;
	private float sunDirectionX;
	private float sunDirectionY;
	private float sunDirectionZ;
	private float theta;
	private float zenithRelativeLuminance;
	private float zenithX;
	private float zenithY;
	private final float[] accumulatedPixelColors;
	@Constant
	private final float[] boundingVolumeHierarchy;
	@Constant
	private final float[] cameraArray;
	@Local
	private float[] currentPixelColors;
	@Local
	private float[] intersections;
	@Constant
	private final float[] perezRelativeLuminance;
	@Constant
	private final float[] perezX;
	@Constant
	private final float[] perezY;
	@Constant
	private final float[] point2s;
	@Constant
	private final float[] point3s;
	@Local
	private float[] rays;
	@Constant
	private final float[] shapes;
	@Constant
	private final float[] surfaces;
	@Local
	private float[] temporaryColors;
	@Constant
	private final float[] textures;
	@Constant
	private final float[] vector3s;
	private int depthMaximum = 1;
	private int depthRussianRoulette = 5;
	private int effectGrayScale;
	private int effectSepiaTone;
	private int isNormalMapping = 1;
	private int octaves;
	private int renderer = RENDERER_PATH_TRACER;
	private int shading = SHADING_GOURAUD;
	private int shapeOffsetsLength;
	private int toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2;
	private final int width;
	@Constant
	private final int[] permutations0 = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};
	@Constant
	private final int[] permutations1 = new int[512];
	@Constant
	private final int[] shapeOffsets;
	private final long[] subSamples;
	private final Sky sky;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If either {@code camera}, {@code sky} or {@code compiledScene} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param isResettingFully {@code true} if full resetting should be performed, {@code false} otherwise
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param sky the {@link Sky} to use
	 * @param compiledScene the {@link CompiledScene} to use
	 * @throws NullPointerException thrown if, and only if, either {@code camera}, {@code sky} or {@code compiledScene} are {@code null}
	 */
	public RendererKernel(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final CompiledScene compiledScene) {
		super(width, height, camera, compiledScene);
		
		final RGBColorSpace rGBColorSpace = RGBColorSpace.SRGB;
		
		this.breakPoint = rGBColorSpace.getBreakPoint();
		this.gamma = rGBColorSpace.getGamma();
		this.segmentOffset = rGBColorSpace.getSegmentOffset();
		this.slope = rGBColorSpace.getSlope();
		this.slopeMatch = rGBColorSpace.getSlopeMatch();
		this.sky = Objects.requireNonNull(sky, "sky == null");
		this.compiledScene = Objects.requireNonNull(compiledScene, "compiledScene == null");
		this.amplitude = 0.5F;
		this.frequency = 0.2F;
		this.lacunarity = 2.0F;
		this.gain = 1.0F / this.lacunarity;
		this.octaves = 2;
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
		this.boundingVolumeHierarchy = this.compiledScene.getBoundingVolumeHierarchy();
		this.cameraArray = this.compiledScene.getCamera();
		this.point2s = this.compiledScene.getPoint2s().length == 0 ? new float[2] : this.compiledScene.getPoint2s();
		this.point3s = this.compiledScene.getPoint3s().length == 0 ? new float[3] : this.compiledScene.getPoint3s();
		this.shapes = this.compiledScene.getShapes();
		this.surfaces = this.compiledScene.getSurfaces();
		this.textures = this.compiledScene.getTextures();
		this.vector3s = this.compiledScene.getVector3s().length == 0 ? new float[3] : this.compiledScene.getVector3s();
		this.accumulatedPixelColors = new float[width * height * 3];
		this.shapeOffsets = this.compiledScene.getShapeOffsets();
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
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If either {@code camera}, {@code sky} or {@code scene} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param isResettingFully {@code true} if full resetting should be performed, {@code false} otherwise
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param sky the {@link Sky} to use
	 * @param scene the {@link Scene} to use
	 * @throws NullPointerException thrown if, and only if, either {@code camera}, {@code sky} or {@code scene} are {@code null}
	 */
	public RendererKernel(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final Scene scene) {
		this(isResettingFully, width, height, camera, sky, CompiledScene.compile(camera, scene));
	}
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If either {@code camera}, {@code sky} or {@code filename} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param isResettingFully {@code true} if full resetting should be performed, {@code false} otherwise
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param sky the {@link Sky} to use
	 * @param filename the filename of the file to read from
	 * @throws NullPointerException thrown if, and only if, either {@code camera}, {@code sky} or {@code filename} are {@code null}
	 */
	public RendererKernel(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final String filename) {
		this(isResettingFully, width, height, camera, sky, filename, 1.0F);
	}
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If either {@code camera}, {@code sky} or {@code filename} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param isResettingFully {@code true} if full resetting should be performed, {@code false} otherwise
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param sky the {@link Sky} to use
	 * @param filename the filename of the file to read from
	 * @param scale the scale to use in the scene
	 * @throws NullPointerException thrown if, and only if, either {@code camera}, {@code sky} or {@code filename} are {@code null}
	 */
	public RendererKernel(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final String filename, final float scale) {
		this(isResettingFully, width, height, camera, sky, CompiledScene.read(camera, new File(Objects.requireNonNull(filename, "filename == null"))).scale(scale));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise
	 */
	@Override
	public boolean isEffectGrayScale() {
		return this.effectGrayScale == 1;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	@Override
	public boolean isEffectSepiaTone() {
		return this.effectSepiaTone == 1;
	}
	
	/**
	 * Returns {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise
	 */
	@Override
	public boolean isNormalMapping() {
		return this.isNormalMapping == 1;
	}
	
	/**
	 * Returns {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise
	 */
	@Override
	public boolean isPathTracing() {
		return this.renderer == RENDERER_PATH_TRACER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise
	 */
	@Override
	public boolean isRayCasting() {
		return this.renderer == RENDERER_RAY_CASTER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise
	 */
	@Override
	public boolean isRayMarching() {
		return this.renderer == RENDERER_RAY_MARCHER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Flat Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Flat Shading is used, {@code false} otherwise
	 */
	@Override
	public boolean isShadingFlat() {
		return this.shading == SHADING_FLAT;
	}
	
	/**
	 * Returns {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise
	 */
	@Override
	public boolean isShadingGouraud() {
		return this.shading == SHADING_GOURAUD;
	}
	
	/**
	 * Returns the {@code byte} array with the pixels.
	 * 
	 * @return the {@code byte} array with the pixels
	 */
	@Override
	public byte[] getPixels() {
		return this.pixels;
	}
	
	@Override
	public float getAmplitude() {
		return this.amplitude;
	}
	
	@Override
	public float getFrequency() {
		return this.frequency;
	}
	
	@Override
	public float getGain() {
		return this.gain;
	}
	
	@Override
	public float getLacunarity() {
		return this.lacunarity;
	}
	
	/**
	 * Returns the maximum depth for path termination.
	 * 
	 * @return the maximum depth for path termination
	 */
	@Override
	public int getDepthMaximum() {
		return this.depthMaximum;
	}
	
	/**
	 * Returns the depth used for Russian Roulette path termination.
	 * 
	 * @return the depth used for Russian Roulette path termination
	 */
	@Override
	public int getDepthRussianRoulette() {
		return this.depthRussianRoulette;
	}
	
	@Override
	public int getOctaves() {
		return this.octaves;
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
	@Override
	public RendererKernel compile(final byte[] pixels, final int width, final int height) {
		this.pixels = Objects.requireNonNull(pixels, "pixels == null");
		
//		setExecutionMode(EXECUTION_MODE.JTP);
		setExplicit(true);
		setSeed(System.nanoTime(), width * height);
		
		updateTables();
		
		put(this.pixels);
		put(this.accumulatedPixelColors);
		put(this.boundingVolumeHierarchy);
		put(this.cameraArray);
		put(this.point2s);
		put(this.point3s);
		put(this.perezRelativeLuminance);
		put(this.perezX);
		put(this.perezY);
		put(this.shapes);
		put(this.surfaces);
		put(this.textures);
		put(this.vector3s);
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
	@Override
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
	 * Updates the local variables.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @param localSize the local size
	 * @return itself for method chaining
	 */
	@Override
	public RendererKernel updateLocalVariables(final int localSize) {
		this.currentPixelColors = new float[localSize * 3];
		this.intersections = new float[localSize * SIZE_INTERSECTION];
		this.rays = new float[localSize * SIZE_RAY];
		this.temporaryColors = new float[localSize * 3];
		
		return this;
	}
	
	/**
	 * Updates the variables related to the {@link Sky}.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @return itself for method chaining
	 */
	@Override
	public RendererKernel updateSky() {
		this.orthoNormalBasisUX = this.sky.getOrthoNormalBasis().u.x;
		this.orthoNormalBasisUY = this.sky.getOrthoNormalBasis().u.y;
		this.orthoNormalBasisUZ = this.sky.getOrthoNormalBasis().u.z;
		this.orthoNormalBasisVX = this.sky.getOrthoNormalBasis().v.x;
		this.orthoNormalBasisVY = this.sky.getOrthoNormalBasis().v.y;
		this.orthoNormalBasisVZ = this.sky.getOrthoNormalBasis().v.z;
		this.orthoNormalBasisWX = this.sky.getOrthoNormalBasis().w.x;
		this.orthoNormalBasisWY = this.sky.getOrthoNormalBasis().w.y;
		this.orthoNormalBasisWZ = this.sky.getOrthoNormalBasis().w.z;
		this.sunDirectionX = this.sky.getSunDirection().x;
		this.sunDirectionY = this.sky.getSunDirection().y;
		this.sunDirectionZ = this.sky.getSunDirection().z;
		this.theta = this.sky.getTheta();
		this.zenithRelativeLuminance = this.sky.getZenithRelativeLuminance();
		this.zenithX = this.sky.getZenithX();
		this.zenithY = this.sky.getZenithY();
		
		System.arraycopy(this.sky.getPerezRelativeLuminance(), 0, this.perezRelativeLuminance, 0, this.perezRelativeLuminance.length);
		System.arraycopy(this.sky.getPerezX(), 0, this.perezX, 0, this.perezX.length);
		System.arraycopy(this.sky.getPerezY(), 0, this.perezY, 0, this.perezY.length);
		
		put(this.perezRelativeLuminance);
		put(this.perezX);
		put(this.perezY);
		
		return this;
	}
	
	/**
	 * Draws a line from {@code x0} and {@code y0} to {@code x1} and {@code y1} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x0 the X-coordinate of the first end of the line
	 * @param y0 the Y-coordinate of the first end of the line
	 * @param x1 the X-coordinate of the second end of the line
	 * @param y1 the Y-coordinate of the second end of the line
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	@Override
	public void drawLine(final int x0, final int y0, final int x1, final int y1, final Color color) {
		final int width = x1 - x0;
		final int height = y1 - y0;
		
		int delta0X = width < 0 ? -1 : width > 0 ? 1 : 0;
		int delta0Y = height < 0 ? -1 : height > 0 ? 1 : 0;
		int delta1X = width < 0 ? -1 : width > 0 ? 1 : 0;
		int delta1Y = 0;
		
		int longest = Math2.abs(width);
		int shortest = Math2.abs(height);
		
		if(longest <= shortest) {
			longest = Math2.abs(height);
			shortest = Math2.abs(width);
			
			delta1X = 0;
			delta1Y = height < 0 ? -1 : height > 0 ? 1 : 0;
		}
		
		int numerator = longest >> 1;
		
		int x = x0;
		int y = y0;
		
		for(int i = 0; i <= longest; i++) {
			doFill(x, y, color);
			
			numerator += shortest;
			
			if(numerator >= longest) {
				numerator -= longest;
				
				x += delta0X;
				y += delta0Y;
			} else {
				x += delta1X;
				y += delta1Y;
			}
		}
	}
	
	/**
	 * Draws a rectangle from {@code x} and {@code y} to {@code x + width} and {@code y + height} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate to start from
	 * @param y the Y-coordinate to start from
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	@Override
	public void drawRectangle(final int x, final int y, final int width, final int height, final Color color) {
		for(int i = y; i < y + height; i++) {
			for(int j = x; j < x + width; j++) {
				if(i == y || i + 1 == y + height || j == x || j + 1 == x + width) {
					doFill(j, i, color);
				}
			}
		}
	}
	
	/**
	 * Draws a triangle with a color of {@code color}.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * drawLine(x0, y0, x1, y1, color);
	 * drawLine(x1, y1, x2, y2, color);
	 * drawLine(x2, y2, x0, y0, color);
	 * }
	 * </pre>
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x0 the X-coordinate of the first point
	 * @param y0 the Y-coordinate of the first point
	 * @param x1 the X-coordinate of the second point
	 * @param y1 the Y-coordinate of the second point
	 * @param x2 the X-coordinate of the third point
	 * @param y2 the Y-coordinate of the third point
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	@Override
	public void drawTriangle(final int x0, final int y0, final int x1, final int y1, final int x2, final int y2, final Color color) {
		drawLine(x0, y0, x1, y1, color);
		drawLine(x1, y1, x2, y2, color);
		drawLine(x2, y2, x0, y0, color);
	}
	
	/**
	 * Fills a circle with a center of {@code x} and {@code y}, a radius of {@code radius} and a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate of the center
	 * @param y the Y-coordinate of the center
	 * @param radius the radius
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	@Override
	public void fillCircle(final int x, final int y, final int radius, final Color color) {
		for(int i = -radius; i <= radius; i++) {
			for(int j = -radius; j <= radius; j++) {
				if(j * j + i * i <= radius * radius) {
					doFill(x + j, y + i, color);
				}
			}
		}
	}
	
	/**
	 * Fills a rectangle from {@code x} and {@code y} to {@code x + width} and {@code y + height} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate to start from
	 * @param y the Y-coordinate to start from
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	@Override
	public void fillRectangle(final int x, final int y, final int width, final int height, final Color color) {
		for(int i = y; i < y + height; i++) {
			for(int j = x; j < x + width; j++) {
				doFill(j, i, color);
			}
		}
	}
	
	/**
	 * Performs the Path Tracing.
	 */
	@Override
	public void run() {
		final int pixelIndex = getGlobalId();
		
		if(doCreatePrimaryRay(pixelIndex)) {
			if(this.renderer == RENDERER_PATH_TRACER) {
				doPathTracing();
			} else if(this.renderer == RENDERER_RAY_CASTER) {
				doRayCasting();
			} else {
				doRayMarching();
			}
		} else {
			final int pixelIndex0 = pixelIndex * 3;
			final int pixelIndex1 = getLocalId() * 3;
			
			this.accumulatedPixelColors[pixelIndex0] = 0.0F;
			this.accumulatedPixelColors[pixelIndex0 + 1] = 0.0F;
			this.accumulatedPixelColors[pixelIndex0 + 2] = 0.0F;
			
			this.currentPixelColors[pixelIndex1] = 0.0F;
			this.currentPixelColors[pixelIndex1 + 1] = 0.0F;
			this.currentPixelColors[pixelIndex1 + 2] = 0.0F;
			
			this.subSamples[pixelIndex] = 1L;
		}
		
		doCalculateColor(pixelIndex);
	}
	
	@Override
	public void setAmplitude(final float amplitude) {
		this.amplitude = Math2.max(amplitude, 0.0F);
	}
	
	/**
	 * Sets the maximum depth to be used for path termination.
	 * 
	 * @param depthMaximum the maximum depth
	 */
	@Override
	public void setDepthMaximum(final int depthMaximum) {
		this.depthMaximum = depthMaximum;
	}
	
	/**
	 * Sets the depth to be used for Russian Roulette path termination.
	 * 
	 * @param depthRussianRoulette the depth to use
	 */
	@Override
	public void setDepthRussianRoulette(final int depthRussianRoulette) {
		this.depthRussianRoulette = depthRussianRoulette;
	}
	
	/**
	 * Enables or disables the Grayscale effect.
	 * 
	 * @param isEffectGrayScale {@code true} if the Grayscale effect is enabled, {@code false} otherwise
	 */
	@Override
	public void setEffectGrayScale(final boolean isEffectGrayScale) {
		this.effectGrayScale = isEffectGrayScale ? 1 : 0;
	}
	
	/**
	 * Enables or disables the Sepia Tone effect.
	 * 
	 * @param isEffectSepiaTone {@code true} if the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	@Override
	public void setEffectSepiaTone(final boolean isEffectSepiaTone) {
		this.effectSepiaTone = isEffectSepiaTone ? 1 : 0;
	}
	
	@Override
	public void setFrequency(final float frequency) {
		this.frequency = Math2.max(frequency, 0.0F);
	}
	
	@Override
	public void setGain(final float gain) {
		this.gain = Math2.max(gain, 0.0F);
	}
	
	@Override
	public void setLacunarity(final float lacunarity) {
		this.lacunarity = Math2.max(lacunarity, 0.0F);
	}
	
	/**
	 * Sets the Normal Mapping state.
	 * 
	 * @param isNormalMapping {@code true} if, and only if, Normal Mapping is activated, {@code false} otherwise
	 */
	@Override
	public void setNormalMapping(final boolean isNormalMapping) {
		this.isNormalMapping = isNormalMapping ? 1 : 0;
	}
	
	@Override
	public void setOctaves(final int octaves) {
		this.octaves = Math2.max(octaves, 1);
	}
	
	/**
	 * Sets whether Path Tracing should be enabled or disabled.
	 * <p>
	 * If {@code isPathTracing} is {@code false}, the renderer will be a Ray Caster.
	 * 
	 * @param isPathTracing the Path Tracing state to set
	 */
	@Override
	public void setPathTracing(final boolean isPathTracing) {
		this.renderer = isPathTracing ? RENDERER_PATH_TRACER : RENDERER_RAY_CASTER;
	}
	
	/**
	 * Sets whether Ray Casting should be enabled or disabled.
	 * <p>
	 * If {@code isRayCasting} is {@code false}, the renderer will be a Ray Marcher.
	 * 
	 * @param isRayCasting the Ray Casting state to set
	 */
	@Override
	public void setRayCasting(final boolean isRayCasting) {
		this.renderer = isRayCasting ? RENDERER_RAY_CASTER : RENDERER_RAY_MARCHER;
	}
	
	/**
	 * Sets whether Ray Marching should be enabled or disabled.
	 * <p>
	 * If {@code isRayMarching} is {@code false}, the renderer will be a Path Tracer.
	 * 
	 * @param isRayMarching the Ray Marching state to set
	 */
	@Override
	public void setRayMarching(final boolean isRayMarching) {
		this.renderer = isRayMarching ? RENDERER_RAY_MARCHER : RENDERER_PATH_TRACER;
	}
	
	/**
	 * Sets Flat Shading.
	 */
	@Override
	public void setShadingFlat() {
		this.shading = SHADING_FLAT;
	}
	
	/**
	 * Sets Gouraud Shading.
	 */
	@Override
	public void setShadingGouraud() {
		this.shading = SHADING_GOURAUD;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Filmic Curve.
	 */
	@Override
	public void setToneMappingAndGammaCorrectionFilmicCurve() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Linear.
	 */
	@Override
	public void setToneMappingAndGammaCorrectionLinear() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_LINEAR;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 1.
	 */
	@Override
	public void setToneMappingAndGammaCorrectionReinhard1() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_1;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 2.
	 */
	@Override
	public void setToneMappingAndGammaCorrectionReinhard2() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2;
	}
	
	/**
	 * Toggles to the next renderer.
	 */
	@Override
	public void toggleRenderer() {
		if(isPathTracing()) {
			setRayCasting(true);
		} else if(isRayCasting()) {
			setRayMarching(true);
		} else if(isRayMarching()) {
			setPathTracing(true);
		}
	}
	
	/**
	 * Toggles to the next shading.
	 */
	@Override
	public void toggleShading() {
		if(isShadingFlat()) {
			setShadingGouraud();
		} else if(isShadingGouraud()) {
			setShadingFlat();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doCreatePrimaryRay(final int pixelIndex) {
//		Calculate the X- and Y-coordinates on the screen:
		final int y = pixelIndex / this.width;
		final int x = pixelIndex - y * this.width;
		
//		Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:
		final float eyeX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_X];
		final float eyeY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_Y];
		final float eyeZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_Z];
		
//		Retrieve the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float uX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_X];
		final float uY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Y];
		final float uZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Z];
		
//		Retrieve the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float vX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_X];
		final float vY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Y];
		final float vZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Z];
		
//		Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float wX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_X];
		final float wY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Y];
		final float wZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Z];
		
//		Calculate the Field of View:
		final float fieldOfViewX0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_X];
		final float fieldOfViewX1 = tan(fieldOfViewX0 * PI_DIVIDED_BY_360);
		final float fieldOfViewY0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_Y];
		final float fieldOfViewY1 = tan(-fieldOfViewY0 * PI_DIVIDED_BY_360);
		
//		Calculate the horizontal direction:
		final float horizontalX = uX * fieldOfViewX1;
		final float horizontalY = uY * fieldOfViewX1;
		final float horizontalZ = uZ * fieldOfViewX1;
		
//		Calculate the vertical direction:
		final float verticalX = vX * fieldOfViewY1;
		final float verticalY = vY * fieldOfViewY1;
		final float verticalZ = vZ * fieldOfViewY1;
		
//		Calculate the pixel jitter:
		final float jitterX = this.renderer == RENDERER_PATH_TRACER ? nextFloat() - 0.5F : 0.5F;
		final float jitterY = this.renderer == RENDERER_PATH_TRACER ? nextFloat() - 0.5F : 0.5F;
		
//		Calculate the pixel sample point:
		final float sx = (jitterX + x) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION_X] - 1.0F);
		final float sy = (jitterY + y) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION_Y] - 1.0F);
		final float sx0 = 2.0F * sx - 1.0F;
		final float sy0 = 2.0F * sy - 1.0F;
		
//		Initialize w to 1.0F:
		float w = 1.0F;
		
//		Retrieve whether or not this camera uses a Fisheye camera lens:
		final boolean isFisheyeCameraLens = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_CAMERA_LENS] == Camera.CAMERA_LENS_FISHEYE;
		
		if(isFisheyeCameraLens) {
//			Calculate the dot product that will be used in determining if the current pixel is outside the camera lens:
			final float dotProduct = sx0 * sx0 + sy0 * sy0;
			
			if(dotProduct > 1.0F) {
				return false;
			}
			
//			Update the w variable:
			w = sqrt(1.0F - dotProduct);
		}
		
//		Calculate the middle point:
		final float middleX = eyeX + wX * w;
		final float middleY = eyeY + wY * w;
		final float middleZ = eyeZ + wZ * w;
		
//		Calculate the point on the plane one unit away from the eye:
		final float pointOnPlaneOneUnitAwayFromEyeX = middleX + (horizontalX * sx0) + (verticalX * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeY = middleY + (horizontalY * sx0) + (verticalY * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeZ = middleZ + (horizontalZ * sx0) + (verticalZ * sy0);
		
//		Retrieve the focal distance:
		final float focalDistance = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE];
		
//		Calculate the point on the image plane:
		final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;
		final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;
		final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;
		
//		Retrieve the aperture radius:
		final float apertureRadius = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_APERTURE_RADIUS];
		
//		Initialize the X-, Y- and Z-coordinates of the aperture point:
		float aperturePointX = eyeX;
		float aperturePointY = eyeY;
		float aperturePointZ = eyeZ;
		
//		Check if Depth of Field (DoF) is enabled:
		if(apertureRadius > 0.00001F) {
//			Calculate two random values:
			final float random1 = nextFloat();
			final float random2 = nextFloat();
			
//			Calculate the angle:
			final float angle = PI_MULTIPLIED_BY_TWO * random1;
			
//			Calculate the distance:
			final float distance = apertureRadius * sqrt(random2);
			
//			Calculate the aperture:
			final float apertureX = cos(angle) * distance;
			final float apertureY = sin(angle) * distance;
			
//			Update the aperture point:
			aperturePointX = eyeX + uX * apertureX + vX * apertureY;
			aperturePointY = eyeY + uY * apertureX + vY * apertureY;
			aperturePointZ = eyeZ + uZ * apertureX + vZ * apertureY;
		}
		
//		Calculate the aperture to image plane:
		final float apertureToImagePlane0X = pointOnImagePlaneX - aperturePointX;
		final float apertureToImagePlane0Y = pointOnImagePlaneY - aperturePointY;
		final float apertureToImagePlane0Z = pointOnImagePlaneZ - aperturePointZ;
		final float apertureToImagePlane0LengthReciprocal = rsqrt(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);
		final float apertureToImagePlane1X = apertureToImagePlane0X * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Y = apertureToImagePlane0Y * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Z = apertureToImagePlane0Z * apertureToImagePlane0LengthReciprocal;
		
//		Calculate the offset in the rays array:
		final int raysOffset = getLocalId() * SIZE_RAY;
		
//		Update the rays array with information:
		this.rays[raysOffset + 0] = aperturePointX;
		this.rays[raysOffset + 1] = aperturePointY;
		this.rays[raysOffset + 2] = aperturePointZ;
		this.rays[raysOffset + 3] = apertureToImagePlane1X;
		this.rays[raysOffset + 4] = apertureToImagePlane1Y;
		this.rays[raysOffset + 5] = apertureToImagePlane1Z;
		
		return true;
	}
	
	private float doGetY(final float x, final float z) {
		return doSimplexFractalXY(2, x, z);
	}
	
	private float doIntersect(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Retrieve the type of the shape:
		final int type = (int)(this.shapes[shapesOffset]);
		
		if(type == CompiledScene.PLANE_TYPE) {
			return doIntersectPlane(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		} else if(type == CompiledScene.SPHERE_TYPE) {
			return doIntersectSphere(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
		}
		
//		Return no hit:
		return INFINITY;
	}
	
	private float doIntersectPlane(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offset to the surface normal of the plane:
		final int offsetSurfaceNormal = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL_VECTOR3S_OFFSET]);
		
//		Retrieve the surface normal of the plane:
		final float surfaceNormalX = this.vector3s[offsetSurfaceNormal];
		final float surfaceNormalY = this.vector3s[offsetSurfaceNormal + 1];
		final float surfaceNormalZ = this.vector3s[offsetSurfaceNormal + 2];
		
//		Calculate the dot product between the surface normal and the ray direction:
		final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
		
//		Check that the dot product is not 0.0:
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
//			Calculate the offset to the point denoted as A of the plane:
			final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A_POINT3S_OFFSET]);
			
//			Retrieve the X-, Y- and Z-coordinates of the point A:
			final float aX = this.point3s[offsetA];
			final float aY = this.point3s[offsetA + 1];
			final float aZ = this.point3s[offsetA + 2];
			
//			Calculate the distance:
			final float distance = ((aX - originX) * surfaceNormalX + (aY - originY) * surfaceNormalY + (aZ - originZ) * surfaceNormalZ) / dotProduct;
			
//			Check that the distance is greater than an epsilon value and return it if so:
			if(distance > EPSILON) {
				return distance;
			}
		}
		
//		Return no hit:
		return INFINITY;
	}
	
	private float doIntersectSphere(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offset to the center position of the sphere:
		final int offsetPosition = (int)(this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION_POINT3S_OFFSET]);
		
//		Retrieve the center position of the sphere:
		final float positionX = this.point3s[offsetPosition];
		final float positionY = this.point3s[offsetPosition + 1];
		final float positionZ = this.point3s[offsetPosition + 2];
		
//		Retrieve the radius of the sphere:
		final float radius = this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_RADIUS];
		
//		Calculate the direction to the sphere center:
		final float x = positionX - originX;
		final float y = positionY - originY;
		final float z = positionZ - originZ;
		
//		Calculate the dot product between the ray direction and the direction to the sphere center position:
		final float b = x * directionX + y * directionY + z * directionZ;
		
//		Calculate the determinant:
		final float determinant0 = b * b - (x * x + y * y + z * z) + radius * radius;
		
//		Check that the determinant is positive:
		if(determinant0 >= 0.0F) {
//			Calculate the square root of the determinant:
			final float determinant1 = sqrt(determinant0);
			
//			Calculate the first distance:
			final float distance1 = b - determinant1;
			
//			Check that the first distance is greater than an epsilon value and return it if so:
			if(distance1 > EPSILON) {
				return distance1;
			}
			
//			Calculate the second distance:
			final float distance2 = b + determinant1;
			
//			Check that the second distance is greater than an epsilon value and return it if so:
			if(distance2 > EPSILON) {
				return distance2;
			}
		}
		
//		Return no hit:
		return INFINITY;
	}
	
	private float doIntersectTriangle(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offsets to the points A, B and C of the triangle:
		final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A_POINT3S_OFFSET]);
		final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B_POINT3S_OFFSET]);
		final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C_POINT3S_OFFSET]);
		
//		Retrieve point A of the triangle:
		final float aX = this.point3s[offsetA];
		final float aY = this.point3s[offsetA + 1];
		final float aZ = this.point3s[offsetA + 2];
		
//		Retrieve point B of the triangle:
		final float bX = this.point3s[offsetB];
		final float bY = this.point3s[offsetB + 1];
		final float bZ = this.point3s[offsetB + 2];
		
//		Retrieve point C of the triangle:
		final float cX = this.point3s[offsetC];
		final float cY = this.point3s[offsetC + 1];
		final float cZ = this.point3s[offsetC + 2];
		
//		Calculate the first edge between the points A and B:
		final float edge0X = bX - aX;
		final float edge0Y = bY - aY;
		final float edge0Z = bZ - aZ;
		
//		Calculate the second edge between the points A and C:
		final float edge1X = cX - aX;
		final float edge1Y = cY - aY;
		final float edge1Z = cZ - aZ;
		
//		Calculate the cross product:
		final float v0X = directionY * edge1Z - directionZ * edge1Y;
		final float v0Y = directionZ * edge1X - directionX * edge1Z;
		final float v0Z = directionX * edge1Y - directionY * edge1X;
		
//		Calculate the determinant:
		final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;
		
//		Initialize the distance to a value denoting no hit:
		float t = INFINITY;
		
//		Check that the determinant is anything other than in the range of negative epsilon and posive epsilon:
		if(determinant < -EPSILON || determinant > EPSILON) {
//			Calculate the reciprocal of the determinant:
			final float determinantReciprocal = 1.0F / determinant;
			
//			Calculate the direction to the point A:
			final float v1X = originX - aX;
			final float v1Y = originY - aY;
			final float v1Z = originZ - aZ;
			
//			Calculate the U value:
			final float u = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
			
//			Check that the U value is between 0.0 and 1.0:
			if(u >= 0.0F && u <= 1.0F) {
//				Calculate the cross product:
				final float v2X = v1Y * edge0Z - v1Z * edge0Y;
				final float v2Y = v1Z * edge0X - v1X * edge0Z;
				final float v2Z = v1X * edge0Y - v1Y * edge0X;
				
//				Calculate the V value:
				final float v = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
				
//				Update the distance value:
				t = v >= 0.0F && u + v <= 1.0F ? (edge1X * v2X + edge1Y * v2Y + edge1Z * v2Z) * determinantReciprocal : EPSILON;
				t = t > EPSILON ? t : INFINITY;
			}
		}
		
//		Return the distance:
		return t;
	}
	
	private float doPerlinNoise(final float x, final float y, final float z) {
//		Calculate the floor of the X-, Y- and Z-coordinates:
		final float floorX = floor(x);
		final float floorY = floor(y);
		final float floorZ = floor(z);
		
//		Cast the previously calculated floors of the X-, Y- and Z-coordinates to ints:
		final int x0 = (int)(floorX) & 0xFF;
		final int y0 = (int)(floorY) & 0xFF;
		final int z0 = (int)(floorZ) & 0xFF;
		
//		Calculate the fractional parts of the X-, Y- and Z-coordinates by subtracting their respective floor values:
		final float x1 = x - floorX;
		final float y1 = y - floorY;
		final float z1 = z - floorZ;
		
//		Calculate the U-, V- and W-coordinates:
		final float u = x1 * x1 * x1 * (x1 * (x1 * 6.0F - 15.0F) + 10.0F);
		final float v = y1 * y1 * y1 * (y1 * (y1 * 6.0F - 15.0F) + 10.0F);
		final float w = z1 * z1 * z1 * (z1 * (z1 * 6.0F - 15.0F) + 10.0F);
		
//		Calculate some hash values:
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
		
//		Calculate the gradients:
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
		
//		Perform linear interpolation:
		final float lerp0 = gradient0 + u * (gradient1 - gradient0);
		final float lerp1 = gradient2 + u * (gradient3 - gradient2);
		final float lerp2 = gradient4 + u * (gradient5 - gradient4);
		final float lerp3 = gradient6 + u * (gradient7 - gradient6);
		final float lerp4 = lerp0 + v * (lerp1 - lerp0);
		final float lerp5 = lerp2 + v * (lerp3 - lerp2);
		final float lerp6 = lerp4 + w * (lerp5 - lerp4);
		
		return lerp6;
	}
	
	/*
	private float doSimplexFractalX(final int octaves, final float x) {
		float result = 0.0F;
		
		float amplitude = this.amplitude;
		float frequency = this.frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += amplitude * doSimplexNoiseX(x * frequency);
			
			amplitude *= this.gain;
			frequency *= this.lacunarity;
		}
		
		return result;
	}
	*/
	
	private float doSimplexFractalXY(final int octaves, final float x, final float y) {
		float result = 0.0F;
		
		float amplitude = this.amplitude;
		float frequency = this.frequency;
		
		for(int i = 0; i < octaves; i++) {
			result += amplitude * doSimplexNoiseXY(x * frequency, y * frequency);
			
			amplitude *= this.gain;
			frequency *= this.lacunarity;
		}
		
		return result;
	}
	
	/*
	private float doSimplexNoiseX(final float x) {
		final int i0 = doFastFloor(x);
		final int i1 = i0 + 1;
		
		final float x0 = x - i0;
		final float x1 = x0 - 1.0F;
		
		final float t00 = 1.0F - x0 * x0;
		final float t01 = t00 * t00;
		
		final float t10 = 1.0F - x1 * x1;
		final float t11 = t10 * t10;
		
		final float n0 = t01 * t01 * doGradientX(doHash(i0), x0);
		final float n1 = t11 * t11 * doGradientX(doHash(i1), x1);
		
		return 0.395F * (n0 + n1);
	}
	*/
	
	private float doSimplexNoiseXY(final float x, final float y) {
		final float a = 0.366025403F;
		final float b = 0.211324865F;
		
		final float s = (x + y) * a;
		final float sx = s + x;
		final float sy = s + y;
		
		final int i0 = doFastFloor(sx);
		final int j0 = doFastFloor(sy);
		
		final float t = (i0 + j0) * b;
		
		final float x00 = i0 - t;
		final float y00 = j0 - t;
		final float x01 = x - x00;
		final float y01 = y - y00;
		
		final int i1 = x01 > y01 ? 1 : 0;
		final int j1 = x01 > y01 ? 0 : 1;
		
		final float x1 = x01 - i1 + b;
		final float y1 = y01 - j1 + b;
		final float x2 = x01 - 1.0F + 2.0F * b;
		final float y2 = y01 - 1.0F + 2.0F * b;
		
		final float t00 = 0.5F - x01 * x01 - y01 * y01;
		final float t01 = t00 < 0.0F ? t00 : t00 * t00;
		
		final float n0 = t00 < 0.0F ? 0.0F : t01 * t01 * doGradientXY(doHash(i0 + doHash(j0)), x01, y01);
		
		final float t10 = 0.5F - x1 * x1 - y1 * y1;
		final float t11 = t10 < 0.0F ? t10 : t10 * t10;
		
		final float n1 = t10 < 0.0F ? 0.0F : t11 * t11 * doGradientXY(doHash(i0 + i1 + doHash(j0 + j1)), x1, y1);
		
		final float t20 = 0.5F - x2 * x2 - y2 * y2;
		final float t21 = t20 < 0.0F ? t20 : t20 * t20;
		
		final float n2 = t20 < 0.0F ? 0.0F : t21 * t21 * doGradientXY(doHash(i0 + 1 + doHash(j0 + 1)), x2, y2);
		
		return 45.23065F * (n0 + n1 + n2);
	}
	
	private int doHash(final int index) {
		return this.permutations1[index % this.permutations1.length];
	}
	
	private void doCalculateColor(final int pixelIndex) {
//		Retrieve the offset to the pixels array:
		final int pixelsOffset = pixelIndex * SIZE_PIXEL;
		
//		Calculate the pixel index:
		final int pixelIndex0 = pixelIndex * 3;
		final int pixelIndex1 = getLocalId() * 3;
		
		if(this.renderer == RENDERER_PATH_TRACER) {
//			Retrieve the current sub-sample:
			final float subSample = this.subSamples[pixelIndex];
			
//			Multiply the 'normalized' accumulated pixel color component values with the current sub-sample count:
			this.accumulatedPixelColors[pixelIndex0] *= subSample;
			this.accumulatedPixelColors[pixelIndex0 + 1] *= subSample;
			this.accumulatedPixelColors[pixelIndex0 + 2] *= subSample;
			
//			Add the current pixel color component values to the accumulated pixel color component values:
			this.accumulatedPixelColors[pixelIndex0] += this.currentPixelColors[pixelIndex1];
			this.accumulatedPixelColors[pixelIndex0 + 1] += this.currentPixelColors[pixelIndex1 + 1];
			this.accumulatedPixelColors[pixelIndex0 + 2] += this.currentPixelColors[pixelIndex1 + 2];
			
//			Increment the current sub-sample count by one:
			this.subSamples[pixelIndex] += 1;
			
//			Retrieve the current sub-sample count and calculate its reciprocal (inverse), such that no division is needed further on:
			final float currentSubSamples = subSample + 1.0F;
			final float currentSubSamplesReciprocal = 1.0F / currentSubSamples;
			
//			Multiply the accumulated pixel color component values with the reciprocal of the current sub-sample count to 'normalize' it:
			this.accumulatedPixelColors[pixelIndex0] *= currentSubSamplesReciprocal;
			this.accumulatedPixelColors[pixelIndex0 + 1] *= currentSubSamplesReciprocal;
			this.accumulatedPixelColors[pixelIndex0 + 2] *= currentSubSamplesReciprocal;
		} else {
			this.accumulatedPixelColors[pixelIndex0] = this.currentPixelColors[pixelIndex1];
			this.accumulatedPixelColors[pixelIndex0 + 1] = this.currentPixelColors[pixelIndex1 + 1];
			this.accumulatedPixelColors[pixelIndex0 + 2] = this.currentPixelColors[pixelIndex1 + 2];
		}
		
//		Retrieve the 'normalized' accumulated pixel color component values again:
		float r = this.accumulatedPixelColors[pixelIndex0];
		float g = this.accumulatedPixelColors[pixelIndex0 + 1];
		float b = this.accumulatedPixelColors[pixelIndex0 + 2];
		
		if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_1) {
//			Perform Tone Mapping on the 'normalized' accumulated pixel color components:
			r = r / (r + 1.0F);
			g = g / (g + 1.0F);
			b = b / (b + 1.0F);
		} else if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2) {
//			Set the exposure:
			final float exposure = 1.0F;
			
//			Perform Tone Mapping on the 'normalized' accumulated pixel color components:
			r = 1.0F - exp(-r * exposure);
			g = 1.0F - exp(-g * exposure);
			b = 1.0F - exp(-b * exposure);
		} else if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE) {
//			Calculate the maximum pixel color component values:
			final float rMaximum = max(r - 0.004F, 0.0F);
			final float gMaximum = max(g - 0.004F, 0.0F);
			final float bMaximum = max(b - 0.004F, 0.0F);
			
//			Perform Tone Mapping and Gamma Correction:
			r = (rMaximum * (6.2F * rMaximum + 0.5F)) / (rMaximum * (6.2F * rMaximum + 1.7F) + 0.06F);
			g = (gMaximum * (6.2F * gMaximum + 0.5F)) / (gMaximum * (6.2F * gMaximum + 1.7F) + 0.06F);
			b = (bMaximum * (6.2F * bMaximum + 0.5F)) / (bMaximum * (6.2F * bMaximum + 1.7F) + 0.06F);
		} else if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_LINEAR) {
//			Calculate the maximum component value of the 'normalized' accumulated pixel color component values:
			final float maximumComponentValue = max(r, max(g, b));
			
//			Check if the maximum component value is greater than 1.0:
			if(maximumComponentValue > 1.0F) {
//				Calculate the reciprocal of the maximum component value, such that no division is needed further on:
				final float maximumComponentValueReciprocal = 1.0F / maximumComponentValue;
				
//				Multiply the 'normalized' accumulated pixel color component values with the reciprocal of the maximum component value for Tone Mapping:
				r *= maximumComponentValueReciprocal;
				g *= maximumComponentValueReciprocal;
				b *= maximumComponentValueReciprocal;
			}
		}
		
		if(this.effectGrayScale == 1) {
//			Perform a Grayscale effect based on Luminosity:
			r = r * 0.21F + g * 0.72F + b * 0.07F;
			g = r;
			b = r;
		}
		
		if(this.effectSepiaTone == 1) {
//			Perform a Sepia effect:
			final float r1 = r * 0.393F + g * 0.769F + b * 0.189F;
			final float g1 = r * 0.349F + g * 0.686F + b * 0.168F;
			final float b1 = r * 0.272F + g * 0.534F + b * 0.131F;
			
			r = r1;
			g = g1;
			b = b1;
		}
		
		if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE) {
//			Clamp the 'normalized' accumulated pixel color components to the range [0.0, 1.0]:
			r = min(max(r, 0.0F), 1.0F);
			g = min(max(g, 0.0F), 1.0F);
			b = min(max(b, 0.0F), 1.0F);
			
//			Multiply the 'normalized' accumulated pixel color components with 255.0, to lie in the range [0.0, 255.0], so they can be displayed:
			r *= 255.0F;
			g *= 255.0F;
			b *= 255.0F;
		} else {
//			TODO: Write explanation!
			r = r <= 0.0F ? 0.0F : r >= 1.0F ? 1.0F : r <= this.breakPoint ? r * this.slope : this.slopeMatch * pow(r, 1.0F / this.gamma) - this.segmentOffset;
			g = g <= 0.0F ? 0.0F : g >= 1.0F ? 1.0F : g <= this.breakPoint ? g * this.slope : this.slopeMatch * pow(g, 1.0F / this.gamma) - this.segmentOffset;
			b = b <= 0.0F ? 0.0F : b >= 1.0F ? 1.0F : b <= this.breakPoint ? b * this.slope : this.slopeMatch * pow(b, 1.0F / this.gamma) - this.segmentOffset;
			
//			TODO: Write explanation!
			r = min(max(r * 255.0F + 0.5F, 0.0F), 255.0F);
			g = min(max(g * 255.0F + 0.5F, 0.0F), 255.0F);
			b = min(max(b * 255.0F + 0.5F, 0.0F), 255.0F);
		}
		
//		Update the pixels array with the actual color to display it:
		this.pixels[pixelsOffset + 0] = (byte)(b);
		this.pixels[pixelsOffset + 1] = (byte)(g);
		this.pixels[pixelsOffset + 2] = (byte)(r);
		this.pixels[pixelsOffset + 3] = (byte)(255);
	}
	
	private void doCalculateColorForSky(final float directionX, final float directionY, final float directionZ) {
//		Calculate the direction vector:
		final float direction0X = directionX * this.orthoNormalBasisUX + directionY * this.orthoNormalBasisUY + directionZ * this.orthoNormalBasisUZ;
		final float direction0Y = directionX * this.orthoNormalBasisVX + directionY * this.orthoNormalBasisVY + directionZ * this.orthoNormalBasisVZ;
		final float direction0Z = directionX * this.orthoNormalBasisWX + directionY * this.orthoNormalBasisWY + directionZ * this.orthoNormalBasisWZ;
		
//		if(direction0Z < 0.0F) {
//			Calculate the pixel index:
//			final int pixelIndex0 = pixelIndex * 3;
			
//			Update the temporaryColors array with black:
//			this.temporaryColors[pixelIndex0] = 0.0F;
//			this.temporaryColors[pixelIndex0 + 1] = 0.0F;
//			this.temporaryColors[pixelIndex0 + 2] = 0.0F;
			
//			return;
//		}
		
//		Recalculate the direction vector:
		final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
		final float direction1X = direction0X * direction0LengthReciprocal;
		final float direction1Y = direction0Y * direction0LengthReciprocal;
		final float direction1Z = max(direction0Z * direction0LengthReciprocal, 0.001F);
		final float direction1LengthReciprocal = rsqrt(direction1X * direction1X + direction1Y * direction1Y + direction1Z * direction1Z);
		final float direction2X = direction1X * direction1LengthReciprocal;
		final float direction2Y = direction1Y * direction1LengthReciprocal;
		final float direction2Z = direction1Z * direction1LengthReciprocal;
		
//		Calculate the dot product between the direction vector and the sun direction vector:
		final float dotProduct = direction2X * this.sunDirectionX + direction2Y * this.sunDirectionY + direction2Z * this.sunDirectionZ;
		
//		Calculate some theta angles:
		final float theta0 = this.theta;
		final float theta1 = acos(max(min(direction2Z, 1.0F), -1.0F));
		
//		Calculate the cosines of the theta angles:
		final float cosTheta0 = cos(theta0);
		final float cosTheta1 = cos(theta1);
		final float cosTheta1Reciprocal = 1.0F / (cosTheta1 + 0.01F);
		
//		Calculate the gamma:
		final float gamma = acos(max(min(dotProduct, 1.0F), -1.0F));
		
//		Calculate the cosine of the gamma:
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
		
//		TODO: Write explanation!
		final float w0 = -min(0.0F, min(r, min(g, b)));
		final float w1 = max(0.0F, w0);
		
//		TODO: Write explanation!
		r += w1;
		g += w1;
		b += w1;
		
//		TODO: Write explanation!
		final int pixelIndex0 = getLocalId() * 3;
		
//		TODO: Write explanation!
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	private void doCalculateSurfaceProperties(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {
		final int type = (int)(this.shapes[shapesOffset]);
		
		if(type == CompiledScene.TRIANGLE_TYPE) {
			doCalculateSurfacePropertiesForTriangle(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);
		} else if(type == CompiledScene.PLANE_TYPE) {
			doCalculateSurfacePropertiesForPlane(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);
		} else if(type == CompiledScene.SPHERE_TYPE) {
			doCalculateSurfacePropertiesForSphere(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);
		}
	}
	
	private void doCalculateSurfacePropertiesForPlane(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A_POINT3S_OFFSET]);
		final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_B_POINT3S_OFFSET]);
		final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_C_POINT3S_OFFSET]);
		final int offsetSurfaceNormal = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL_VECTOR3S_OFFSET]);
		
//		Retrieve the point A of the plane:
		final float a0X = this.point3s[offsetA];
		final float a0Y = this.point3s[offsetA + 1];
		final float a0Z = this.point3s[offsetA + 2];
		
//		Retrieve the point B of the plane:
		final float b0X = this.point3s[offsetB];
		final float b0Y = this.point3s[offsetB + 1];
		final float b0Z = this.point3s[offsetB + 2];
		
//		Retrieve the point C of the plane:
		final float c0X = this.point3s[offsetC];
		final float c0Y = this.point3s[offsetC + 1];
		final float c0Z = this.point3s[offsetC + 2];
		
//		Retrieve the surface normal:
		final float surfaceNormalX = this.vector3s[offsetSurfaceNormal];
		final float surfaceNormalY = this.vector3s[offsetSurfaceNormal + 1];
		final float surfaceNormalZ = this.vector3s[offsetSurfaceNormal + 2];
		
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
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate some offsets:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset0] = distance;
		this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormalX;
		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormalY;
		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZ;
		this.intersections[offsetIntersectionUVCoordinates] = u;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForSphere(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Retrieve the offset of the position:
		final int offsetPosition = (int)(this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION_POINT3S_OFFSET]);
		
//		Retrieve the X-, Y- and Z-components of the position:
		final float x = this.point3s[offsetPosition];
		final float y = this.point3s[offsetPosition + 1];
		final float z = this.point3s[offsetPosition + 2];
		
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
		final float v = 0.5F - asinpi(direction1Y);
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Retrieve offsets for the surface intersection point, surface normal and UV-coordinates:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset0] = distance;
		this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;
		this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
		this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		this.intersections[offsetIntersectionUVCoordinates] = u;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForTriangle(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Retrieve the offsets for the positions, UV-coordinates and surface normals:
		final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A_POINT3S_OFFSET]);
		final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B_POINT3S_OFFSET]);
		final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C_POINT3S_OFFSET]);
		final int offsetUVA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_A_POINT2S_OFFSET]);
		final int offsetUVB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_B_POINT2S_OFFSET]);
		final int offsetUVC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_C_POINT2S_OFFSET]);
		final int offsetSurfaceNormalA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_A_VECTOR3S_OFFSET]);
		final int offsetSurfaceNormalB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_B_VECTOR3S_OFFSET]);
		final int offsetSurfaceNormalC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_C_VECTOR3S_OFFSET]);
		
//		Calculate the Barycentric-coordinates:
		final float aX = this.point3s[offsetA];
		final float aY = this.point3s[offsetA + 1];
		final float aZ = this.point3s[offsetA + 2];
		final float bX = this.point3s[offsetB];
		final float bY = this.point3s[offsetB + 1];
		final float bZ = this.point3s[offsetB + 2];
		final float cX = this.point3s[offsetC];
		final float cY = this.point3s[offsetC + 1];
		final float cZ = this.point3s[offsetC + 2];
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
		final float aU = this.point2s[offsetUVA];
		final float aV = this.point2s[offsetUVA + 1];
		final float bU = this.point2s[offsetUVB];
		final float bV = this.point2s[offsetUVB + 1];
		final float cU = this.point2s[offsetUVC];
		final float cV = this.point2s[offsetUVC + 1];
		final float u1 = w * aU + u0 * bU + v0 * cU;
		final float v1 = w * aV + u0 * bV + v0 * cV;
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate some offsets:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		Update the intersections array:
		this.intersections[intersectionsOffset0] = distance;
		this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections[offsetIntersectionUVCoordinates] = u1;
		this.intersections[offsetIntersectionUVCoordinates + 1] = v1;
		
		if(this.shading == SHADING_FLAT) {
//			Calculate the surface normal for Flat Shading:
			final float surfaceNormalAX = this.vector3s[offsetSurfaceNormalA];
			final float surfaceNormalAY = this.vector3s[offsetSurfaceNormalA + 1];
			final float surfaceNormalAZ = this.vector3s[offsetSurfaceNormalA + 2];
			final float surfaceNormal0X = edge0Y * edge1Z - edge0Z * edge1Y;
			final float surfaceNormal0Y = edge0Z * edge1X - edge0X * edge1Z;
			final float surfaceNormal0Z = edge0X * edge1Y - edge0Y * edge1X;
			final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
			final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
			final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal1X * surfaceNormalAX + surfaceNormal1Y * surfaceNormalAY + surfaceNormal1Z * surfaceNormalAZ : 0.0F;
			final float surfaceNormal2X = dotProduct < 0.0F ? -surfaceNormal1X : surfaceNormal1X;
			final float surfaceNormal2Y = dotProduct < 0.0F ? -surfaceNormal1Y : surfaceNormal1Y;
			final float surfaceNormal2Z = dotProduct < 0.0F ? -surfaceNormal1Z : surfaceNormal1Z;
			
//			Update the intersections array based on Flat Shading:
			this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;
			this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;
			this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;
		} else if(this.shading == SHADING_GOURAUD) {
//			Calculate the surface normal for Gouraud Shading:
			final float surfaceNormalAX = this.vector3s[offsetSurfaceNormalA];
			final float surfaceNormalAY = this.vector3s[offsetSurfaceNormalA + 1];
			final float surfaceNormalAZ = this.vector3s[offsetSurfaceNormalA + 2];
			final float surfaceNormalBX = this.vector3s[offsetSurfaceNormalB];
			final float surfaceNormalBY = this.vector3s[offsetSurfaceNormalB + 1];
			final float surfaceNormalBZ = this.vector3s[offsetSurfaceNormalB + 2];
			final float surfaceNormalCX = this.vector3s[offsetSurfaceNormalC];
			final float surfaceNormalCY = this.vector3s[offsetSurfaceNormalC + 1];
			final float surfaceNormalCZ = this.vector3s[offsetSurfaceNormalC + 2];
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
			
//			Update the intersections array based on Gouraud Shading:
			this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal5X;
			this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal5Y;
			this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal5Z;
		}
	}
	
	private void doCalculateTextureColor(final int relativeOffsetTextures, final int shapesOffset) {
		final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);
		final int texturesOffset = (int)(this.surfaces[surfacesOffset + relativeOffsetTextures]);
		final int textureType = (int)(this.textures[texturesOffset]);
		
		if(textureType == CompiledScene.CHECKERBOARD_TEXTURE_TYPE) {
			doCalculateTextureColorForCheckerboardTexture(texturesOffset);
		} else if(textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {
			doCalculateTextureColorForImageTexture(texturesOffset);
		} else if(textureType == CompiledScene.SOLID_TEXTURE_TYPE) {
			doCalculateTextureColorForSolidTexture(texturesOffset);
		}
	}
	
	private void doCalculateTextureColorForCheckerboardTexture(final int texturesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		final int offsetUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
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
		final float sU = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_U];
		final float sV = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float cosAngle = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN];
		
//		TODO: Write explanation!
		final float textureU = modulo((u * cosAngle - v * sinAngle) * sU);
		final float textureV = modulo((v * cosAngle + u * sinAngle) * sV);
		
//		TODO: Write explanation!
		final boolean isDarkU = textureU > 0.5F;
		final boolean isDarkV = textureV > 0.5F;
		final boolean isDark = isDarkU ^ isDarkV;
		
//		TODO: Write explanation!
		final int pixelIndex0 = getLocalId() * 3;
		
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
	
	private void doCalculateTextureColorForImageTexture(final int texturesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		final int offsetUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		
//		TODO: Write explanation!
		final float u = this.intersections[offsetUVCoordinates];
		final float v = this.intersections[offsetUVCoordinates + 1];
		
//		TODO: Write explanation!
		final float width = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_WIDTH];
		final float height = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_HEIGHT];
		
//		TODO: Write explanation!
		final float scaleU = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float cosAngle = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN];
		
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
		final int index = (int)((y2 * width + x2) * 3);
		
//		TODO: Write explanation!
		final float r = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index];
		final float g = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index + 1];
		final float b = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index + 2];
		
//		TODO: Write explanation!
		final int pixelIndex0 = getLocalId() * 3;
		
//		TODO: Write explanation!
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	private void doCalculateTextureColorForSolidTexture(final int texturesOffset) {
//		Calculate the color offset:
		final int offsetColor0 = texturesOffset + CompiledScene.SOLID_TEXTURE_RELATIVE_OFFSET_COLOR;
		
//		Retrieve the R-, G- and B-component values of the texture:
		final float r = this.textures[offsetColor0];
		final float g = this.textures[offsetColor0 + 1];
		final float b = this.textures[offsetColor0 + 2];
		
//		Calculate the pixel index:
		final int pixelIndex0 = getLocalId() * 3;
		
//		Update the temporaryColors array with the color of the texture:
		this.temporaryColors[pixelIndex0] = r;
		this.temporaryColors[pixelIndex0 + 1] = g;
		this.temporaryColors[pixelIndex0 + 2] = b;
	}
	
	private void doFill(final int x, final int y, final Color color) {
//		Calculate the pixel index:
		final int index = (y * this.width + x) * SIZE_PIXEL;
		
//		Retrieve the RGB-component values:
		final int r = (int)(color.getR() * 255.0F);
		final int g = (int)(color.getG() * 255.0F);
		final int b = (int)(color.getB() * 255.0F);
		
		if(index >= 0 && index + SIZE_PIXEL - 1 < this.pixels.length) {
//			Update the pixel:
			this.pixels[index + 0] = (byte)(b);
			this.pixels[index + 1] = (byte)(g);
			this.pixels[index + 2] = (byte)(r);
			this.pixels[index + 3] = (byte)(255);
		}
	}
	
	private void doPathTracing() {
//		Retrieve the maximum depth allowed and the depth at which to use Russian Roulette to test for path termination:
		final int depthMaximum = this.depthMaximum;
		final int depthRussianRoulette = this.depthRussianRoulette;
		
//		Calculate the current offsets to the intersections and rays arrays:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int raysOffset = getLocalId() * SIZE_RAY;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Retrieve the offsets of the ray origin and the ray direction:
		final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;
		final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays[offsetOrigin];
		float originY = this.rays[offsetOrigin + 1];
		float originZ = this.rays[offsetOrigin + 2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays[offsetDirection];
		float directionY = this.rays[offsetDirection + 1];
		float directionZ = this.rays[offsetDirection + 2];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the radiance multiplier to white:
		float radianceMultiplierR = 1.0F;
		float radianceMultiplierG = 1.0F;
		float radianceMultiplierB = 1.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex0 = getLocalId() * 3;
		
//		Initialize the offset of the shape to skip to -1:
		int shapesOffsetToSkip = -1;
		
//		Run the following do-while-loop as long as the current depth is less than the maximum depth and Russian Roulette does not terminate:
		do {
//			Perform an intersection test:
			doPerformIntersectionTest(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
			final int shapesOffset = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || shapesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
				pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
				pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
				
//				Update the current pixel color:
				this.currentPixelColors[pixelIndex0] = pixelColorR;
				this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
				this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
				
				return;
			}
			
//			Retrieve the offset to the surfaces array for the given shape:
			final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);
			
//			Update the offset of the shape to skip to the current offset:
			shapesOffsetToSkip = shapesOffset;
			
//			Retrieve the offsets of the surface intersection point and the surface normal:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			Retrieve the surface intersection point:
			final float surfaceIntersectionPointX = this.intersections[offsetIntersectionSurfaceIntersectionPoint];
			final float surfaceIntersectionPointY = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal:
			final float surfaceNormalX = this.intersections[offsetIntersectionSurfaceNormal];
			final float surfaceNormalY = this.intersections[offsetIntersectionSurfaceNormal + 1];
			final float surfaceNormalZ = this.intersections[offsetIntersectionSurfaceNormal + 2];
			
//			Calculate the albedo texture color for the intersected shape:
			doCalculateTextureColor(CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);
			
//			Get the color of the shape from the albedo texture color that was looked up:
			float albedoColorR = this.temporaryColors[pixelIndex0];
			float albedoColorG = this.temporaryColors[pixelIndex0 + 1];
			float albedoColorB = this.temporaryColors[pixelIndex0 + 2];
			
//			Retrieve the offset of the emission:
			final int offsetEmission = surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_EMISSION;
			
//			Retrieve the emission from the intersected shape:
			final float emissionR = this.surfaces[offsetEmission];
			final float emissionG = this.surfaces[offsetEmission + 1];
			final float emissionB = this.surfaces[offsetEmission + 2];
			
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
				
//				Calculate a random number that will be used when determining whether or not the path should be terminated:
				final float random = nextFloat();
				
//				If the random number is greater than or equal to the Russian Roulette PDF, then terminate the path:
				if(random >= probabilityDensityFunction) {
//					Perform an intersection test:
					doPerformIntersectionTestOnly(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
					
//					Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
					final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
					
//					Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
					final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
					
//					Test that an intersection was actually made, and if not, return black color (or possibly the background color):
					if(distance0 == INFINITY || shapesOffset0 == -1) {
//						Calculate the color for the sky in the current direction:
						doCalculateColorForSky(directionX, directionY, directionZ);
						
//						Add the color for the sky to the current pixel color:
						pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
						pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
						pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
					}
					
//					Update the current pixel color:
					this.currentPixelColors[pixelIndex0] = pixelColorR;
					this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
					this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
					
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
			final int material = (int)(this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_MATERIAL]);
			
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
//					Calculate some angles:
					final float angle1 = -dotProductOfW0AndDirection;
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
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
//						Compute some random values:
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
						
//						Check if the direction is the Y-direction:
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
						
//						Calculate the direction for the next iteration:
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
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
			} else if(material == MATERIAL_LAMBERTIAN_DIFFUSE) {
//				Compute some random values:
				final float theta = PI_MULTIPLIED_BY_TWO * randomA;
				final float cosTheta = cos(theta);
				final float sinTheta = sin(theta);
				final float sqrtR0 = sqrt(randomB);
				final float sqrtR1 = sqrt(1.0F - randomB);
				
//				Check if the direction is the Y-direction:
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
				
//				Calculate the direction for the next iteration:
				final float direction0X = u2X * cosTheta * sqrtR0 + v0X * sinTheta * sqrtR0 + w0X * sqrtR1;
				final float direction0Y = u2Y * cosTheta * sqrtR0 + v0Y * sinTheta * sqrtR0 + w0Y * sqrtR1;
				final float direction0Z = u2Z * cosTheta * sqrtR0 + v0Z * sinTheta * sqrtR0 + w0Z * sqrtR1;
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
			} else if(material == MATERIAL_PHONG_METAL) {
//				Compute some random values:
				final float random0 = PI_MULTIPLIED_BY_TWO * randomA;
				final float random0Cos = cos(random0);
				final float random0Sin = sin(random0);
				final float random1 = randomB;
				
//				Calculate the cos and sin values of theta:
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
				
//				Check if the direction is the Y-direction:
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
				
//				Calculate the direction for the next iteration:
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
			} else if(material == MATERIAL_GLASS) {
//				Check if the current ray is going in towards the same shape or out of it:
				final boolean isGoingIn = surfaceNormalX * w0X + surfaceNormalY * w0Y + surfaceNormalZ * w0Z > 0.0F;
				
//				TODO: Write explanation!
				final float nnt = isGoingIn ? REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1 : REFRACTIVE_INDEX_1_DIVIDED_BY_REFRACTIVE_INDEX_0;
				
//				Calculate the dot product between the orthonormal basis W vector and the current direction vector:
				final float dotProductOfW0AndDirection = w0X * directionX + w0Y * directionY + w0Z * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);
				
//				Calculate the reflection direction:
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
//					Calculate the square root of the total internal reflection:
					final float sqrtTotalInternalReflection = sqrt(totalInternalReflection);
					
//					Calculate the transmission direction:
					final float scalar = isGoingIn ? dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection : -(dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection);
					final float direction0X = directionX * nnt - surfaceNormalX * scalar;
					final float direction0Y = directionY * nnt - surfaceNormalY * scalar;
					final float direction0Z = directionZ * nnt - surfaceNormalZ * scalar;
					final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
					final float transmissionDirectionX = direction0X * direction0LengthReciprocal;
					final float transmissionDirectionY = direction0Y * direction0LengthReciprocal;
					final float transmissionDirectionZ = direction0Z * direction0LengthReciprocal;
					
//					Calculate some angles:
					final float angle1 = (isGoingIn ? -dotProductOfW0AndDirection : transmissionDirectionX * surfaceNormalX + transmissionDirectionY * surfaceNormalY + transmissionDirectionZ * surfaceNormalZ);
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
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
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
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
		doPerformIntersectionTestOnly(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance0 == INFINITY || shapesOffset0 == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];
			pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];
			pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];
		}
		
//		Update the current pixel color:
		this.currentPixelColors[pixelIndex0] = pixelColorR;
		this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
		this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
	}
	
	private void doPerformIntersectionTest(final int shapesOffsetToSkip, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
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
			if(tMaximum < tMinimum || minimumDistance < tMinimum) {
//				Retrieve the offset to the next node in the BVH structure, relative to the current one:
				boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
			} else {
//				Retrieve the type of the current BVH node:
				final float type = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset];
				
				if(type == CompiledScene.BVH_NODE_TYPE_TREE) {
//					This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
				} else {
//					Retrieve the triangle count in the current BVH node:
					final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
					
					int i = 0;
					
//					Loop through all triangles in the current BVH node:
					while(i < triangleCount) {
//						Retrieve the offset to the current triangle:
						final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
						
						if(offset != shapesOffsetToSkip) {
//							Perform an intersection test with the current triangle:
							final float currentDistance = doIntersectTriangle(offset, originX, originY, originZ, directionX, directionY, directionZ);
							
//							Check if the current distance is less than the distance to the closest shape so far:
							if(currentDistance < minimumDistance) {
//								Update the distance to the closest shape with the current one:
								minimumDistance = currentDistance;
								
//								Update the offset to the closest shape with the current one:
								shapesOffset = offset;
							}
						}
						
						i++;
					}
					
//					Retrieve the offset to the next node in the BVH structure, relative to the current one:
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset + 0;
			}
		} while(boundingVolumeHierarchyOffset != -1);
		
//		Loop through any other shapes, that are not triangles:
		for(int i = 0; i < this.shapeOffsetsLength; i++) {
//			Retrieve the offset to the shape:
			final int currentShapesOffset = this.shapeOffsets[i];
			
			if(currentShapesOffset != shapesOffsetToSkip) {
//				Perform an intersection test with the current shape:
				final float currentDistance = doIntersect(currentShapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
				
//				Check if the current distance is less than the distance to the closest shape so far:
				if(currentDistance < minimumDistance) {
//					Update the distance to the closest shape with the current one:
					minimumDistance = currentDistance;
					
//					Update the offset to the closest shape with the current one:
					shapesOffset = currentShapesOffset;
				}
			}
		}
		
		if(minimumDistance < INFINITY && shapesOffset > -1) {
//			Calculate the surface properties for the intersected shape:
			doCalculateSurfaceProperties(minimumDistance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);
			
//			Perform standard Normal Mapping:
			doPerformNormalMapping(shapesOffset);
			
//			Perform Perlin Noise Normal Mapping:
			doPerformPerlinNoiseNormalMapping(shapesOffset);
		} else {
//			Reset the information in the intersections array:
			this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
			this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;
		}
	}
	
	private void doPerformIntersectionTestOnly(final int shapesOffsetToSkip, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {
//		Calculate the offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate the reciprocal of the ray direction vector:
		final float directionReciprocalX = 1.0F / directionX;
		final float directionReciprocalY = 1.0F / directionY;
		final float directionReciprocalZ = 1.0F / directionZ;
		
//		Initialize the offset to the root of the BVH structure (which is 0):
		int boundingVolumeHierarchyOffset = 0;
		
//		Initialize a predicate:
		boolean hasFoundIntersection = false;
		
//		Reset the information in the intersections array:
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
		this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;
		
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
				
				if(type == CompiledScene.BVH_NODE_TYPE_TREE) {
//					This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
					boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
				} else {
//					Retrieve the triangle count in the current BVH node:
					final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
					
					int i = 0;
					
//					Loop through all triangles in the current BVH node:
					while(i < triangleCount) {
//						Retrieve the offset to the current triangle:
						final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);
						
						if(offset != shapesOffsetToSkip) {
//							Perform an intersection test with the current triangle:
							final float currentDistance = doIntersectTriangle(offset, originX, originY, originZ, directionX, directionY, directionZ);
							
//							Check if the current distance is less than the distance to the closest shape so far:
							if(currentDistance < INFINITY) {
//								Update the predicate:
								hasFoundIntersection = true;
								
//								Set the information in the intersections array:
								this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = currentDistance;
								this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = offset;
								
								i = triangleCount;
							}
						}
						
						i++;
					}
					
					if(hasFoundIntersection) {
						boundingVolumeHierarchyOffset = -1;
					} else {
//						Retrieve the offset to the next node in the BVH structure, relative to the current one:
						boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
					}
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset + 0;
			}
		} while(boundingVolumeHierarchyOffset != -1);
		
		if(!hasFoundIntersection) {
//			Loop through any other shapes, that are not triangles:
			for(int i = 0; i < this.shapeOffsetsLength; i++) {
//				Retrieve the offset to the shape:
				final int currentShapesOffset = this.shapeOffsets[i];
				
				if(currentShapesOffset != shapesOffsetToSkip) {
//					Perform an intersection test with the current shape:
					final float currentDistance = doIntersect(currentShapesOffset, originX, originY, originZ, directionX, directionY, directionZ);
					
//					Check if the current distance is less than the distance to the closest shape so far:
					if(currentDistance < INFINITY) {
//						Update the predicate:
						hasFoundIntersection = true;
						
//						Set the information in the intersections array:
						this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = currentDistance;
						this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = currentShapesOffset;
						
						i = this.shapeOffsetsLength;
					}
				}
			}
		}
	}
	
	private void doPerformNormalMapping(final int shapesOffset) {
//		Retrieve the offset in the textures array and the type of the texture:
		final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);
		final int texturesOffset = (int)(this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL]);
		final int textureType = (int)(this.textures[texturesOffset]);
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
		if(this.isNormalMapping == 1 && textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {
//			Calculate the texture color:
			doCalculateTextureColorForImageTexture(texturesOffset);
			
//			Calculate the index into the temporaryColors array:
			final int pixelIndex0 = getLocalId() * 3;
			
//			Retrieve the R-, G- and B-component values:
			final float r = 2.0F * this.temporaryColors[pixelIndex0] - 1.0F;
			final float g = 2.0F * this.temporaryColors[pixelIndex0 + 1] - 1.0F;
			final float b = 2.0F * this.temporaryColors[pixelIndex0 + 2] - 1.0F;
			
//			Retrieve the offset of the surface normal in the intersections array:
			final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			Retrieve the orthonormal basis W-vector:
			final float wX = this.intersections[offsetIntersectionSurfaceNormal];
			final float wY = this.intersections[offsetIntersectionSurfaceNormal + 1];
			final float wZ = this.intersections[offsetIntersectionSurfaceNormal + 2];
			
//			Calculate the absolute values of the orthonormal basis W-vector:
			final float absWX = abs(wX);
			final float absWY = abs(wY);
			final float absWZ = abs(wZ);
			
//			Check the direction of the orthonormal basis:
			final boolean isWX = absWX < absWY && absWX < absWZ;
			final boolean isWY = absWY < absWZ;
			
//			Calculate the orthonormal basis V-vector:
			final float v0X = isWX ? 0.0F : isWY ? wZ : wY;
			final float v0Y = isWX ? wZ : isWY ? 0.0F : -wX;
			final float v0Z = isWX ? -wY : isWY ? -wX : 0.0F;
			final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
			final float v1X = v0X * v0LengthReciprocal;
			final float v1Y = v0Y * v0LengthReciprocal;
			final float v1Z = v0Z * v0LengthReciprocal;
			
//			Calculate the orthonormal basis U-vector:
			final float u0X = v1Y * wZ - v1Z * wY;
			final float u0Y = v1Z * wX - v1X * wZ;
			final float u0Z = v1X * wY - v1Y * wX;
			final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
			final float u1X = u0X * u0LengthReciprocal;
			final float u1Y = u0Y * u0LengthReciprocal;
			final float u1Z = u0Z * u0LengthReciprocal;
			
//			Calculate the new surface normal:
			final float surfaceNormal0X = r * u1X + g * v1X + b * wX;
			final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;
			final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;
			final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
			final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
			
//			Update the intersections array:
			this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;
			this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
			this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		}
	}
	
	private void doPerformPerlinNoiseNormalMapping(final int shapesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Retrieve the offset to the surfaces array:
		final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);
		
//		Retrieve the Perlin noise amount from the current shape:
		final float amount = this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT];
		
//		Retrieve the Perlin noise scale from the current shape:
		final float scale = this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE];
		
//		Check that the Perlin noise amount and Perlin noise scale are greater than 0.0:
		if(this.isNormalMapping == 1 && amount > 0.0F && scale > 0.0F) {
//			Retrieve the surface intersection point and the surface normal from the current shape:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
			
//			Retrieve the X-, Y- and Z-component values from the surface intersection point:
			final float x0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint];
			final float y0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float z0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Compute the reciprocal of the Perlin noise scale:
			final float scaleReciprocal = 1.0F / scale;
			
//			Scale the X-, Y- and Z-component values:
			final float x1 = x0 * scaleReciprocal;
			final float y1 = y0 * scaleReciprocal;
			final float z1 = z0 * scaleReciprocal;
			
//			Compute the Perlin noise given the X-, Y- and Z-component values:
			final float noiseX = doPerlinNoise(x1, y1, z1);
			final float noiseY = doPerlinNoise(y1, z1, x1);
			final float noiseZ = doPerlinNoise(z1, x1, y1);
			
//			Calculate the surface normal:
			final float surfaceNormal0X = this.intersections[offsetIntersectionSurfaceNormal];
			final float surfaceNormal0Y = this.intersections[offsetIntersectionSurfaceNormal + 1];
			final float surfaceNormal0Z = this.intersections[offsetIntersectionSurfaceNormal + 2];
			final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;
			final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;
			final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;
			final float surfaceNormal1LengthReciprocal = rsqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);
			final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;
			
//			Update the intersections array with the new surface normal:
			this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;
			this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;
			this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;
		}
	}
	
	private void doRayCasting() {
//		Calculate the current offsets to the intersections and rays arrays:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int raysOffset = getLocalId() * SIZE_RAY;
		
//		Retrieve the offsets of the ray origin and the ray direction:
		final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;
		final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays[offsetOrigin];
		float originY = this.rays[offsetOrigin + 1];
		float originZ = this.rays[offsetOrigin + 2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays[offsetDirection];
		float directionY = this.rays[offsetDirection + 1];
		float directionZ = this.rays[offsetDirection + 2];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex0 = getLocalId() * 3;
		
//		Perform an intersection test:
		doPerformIntersectionTest(-1, originX, originY, originZ, directionX, directionY, directionZ);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int shapesOffset = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance == INFINITY || shapesOffset == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR = this.temporaryColors[pixelIndex0];
			pixelColorG = this.temporaryColors[pixelIndex0 + 1];
			pixelColorB = this.temporaryColors[pixelIndex0 + 2];
			
//			Update the current pixel color:
			this.currentPixelColors[pixelIndex0] = pixelColorR;
			this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
			this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
			
			return;
		}
		
//		Calculate the albedo texture color for the intersected shape:
		doCalculateTextureColor(CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);
		
//		Get the color of the shape from the albedo texture color that was looked up:
		float albedoColorR = this.temporaryColors[pixelIndex0];
		float albedoColorG = this.temporaryColors[pixelIndex0 + 1];
		float albedoColorB = this.temporaryColors[pixelIndex0 + 2];
		
//		Retrieve the sun direction:
		final float lightDirectionX = -this.sunDirectionX;
		final float lightDirectionY = -this.sunDirectionY;
		final float lightDirectionZ = -this.sunDirectionZ;
		
//		Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		
//		Retrieve the surface intersection point from the intersections array:
		final float surfaceIntersectionPointX = this.intersections[offsetIntersectionSurfaceIntersectionPoint];
		final float surfaceIntersectionPointY = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];
		final float surfaceIntersectionPointZ = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];
		
//		Retrieve the surface normal from the intersections array:
		final float surfaceNormalX = this.intersections[offsetIntersectionSurfaceNormal];
		final float surfaceNormalY = this.intersections[offsetIntersectionSurfaceNormal + 1];
		final float surfaceNormalZ = this.intersections[offsetIntersectionSurfaceNormal + 2];
		
//		Initialize the pixel color components with ambient lighting:
		pixelColorR = albedoColorR * 0.5F;
		pixelColorG = albedoColorG * 0.5F;
		pixelColorB = albedoColorB * 0.5F;
		
//		Perform an intersection test:
		doPerformIntersectionTestOnly(shapesOffset, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, lightDirectionX, lightDirectionY, lightDirectionZ);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);
		
		if(distance0 != INFINITY && shapesOffset0 != -1) {
//			Initialize the intensity of the light:
			final float lightIntensity = 1.0F;
			
//			Calculate the dot product between the surface normal and the sun direction:
			final float dotProduct = surfaceNormalX * lightDirectionX + surfaceNormalY * lightDirectionY + surfaceNormalZ * lightDirectionZ;
			final float dotProductOrZero = max(dotProduct, 0.0F);
			
			final float diffuseColorR = albedoColorR * dotProductOrZero * lightIntensity;
			final float diffuseColorG = albedoColorG * dotProductOrZero * lightIntensity;
			final float diffuseColorB = albedoColorB * dotProductOrZero * lightIntensity;
			
			final float reflectionX = surfaceNormalX * 2.0F * dotProduct - lightDirectionX;
			final float reflectionY = surfaceNormalY * 2.0F * dotProduct - lightDirectionY;
			final float reflectionZ = surfaceNormalZ * 2.0F * dotProduct - lightDirectionZ;
			
			final float dotProduct0 = -directionX * reflectionX + -directionY * reflectionY + -directionZ * reflectionZ;
			
			final float specularPower = 25.0F;
			final float specularAmount = dotProduct0 < 0.0F ? 0.0F : pow(dotProduct0, specularPower) * lightIntensity;
			final float specularColorR = specularAmount;
			final float specularColorG = specularAmount;
			final float specularColorB = specularAmount;
			
//			Calculate the lighting and add to the pixel color:
			pixelColorR += diffuseColorR + specularColorR;
			pixelColorG += diffuseColorG + specularColorG;
			pixelColorB += diffuseColorB + specularColorB;
		}
		
//		Update the current pixel color:
		this.currentPixelColors[pixelIndex0] = pixelColorR;
		this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
		this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
	}
	
	private void doRayMarching() {
		final int pixelIndex0 = getLocalId() * 3;
		final int raysOffset = getLocalId() * SIZE_RAY;
		final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;
		final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;
		
		float originX = this.rays[offsetOrigin];
		float originY = this.rays[offsetOrigin + 1];
		float originZ = this.rays[offsetOrigin + 2];
		
		originY = doGetY(originX, originZ) + 0.1F;//max(doGetY(originX, originZ) + 0.1F, 0.022F);
		
		float directionX = this.rays[offsetDirection];
		float directionY = this.rays[offsetDirection + 1];
		float directionZ = this.rays[offsetDirection + 2];
		
		final float delTMultiplier = 0.01F;
		
		float delT = delTMultiplier;
		
		final float minT = 0.001F;
		final float maxT = max(originY, 1.0F) * 20.0F;
		
//		float lh = 0.0F;
//		float ly = 0.0F;
		
//		float hitT = -1.0F;
		
		doCalculateColorForSky(directionX, directionY, directionZ);
		
		float pixelColorR = this.temporaryColors[pixelIndex0];
		float pixelColorG = this.temporaryColors[pixelIndex0 + 1];
		float pixelColorB = this.temporaryColors[pixelIndex0 + 2];
		
		final float sunDirectionX = this.sunDirectionX;
		final float sunDirectionY = this.sunDirectionY;
		final float sunDirectionZ = this.sunDirectionZ;
		
		final float sunDistance = 1000.0F;
		
		final float sunPositionX = sunDirectionX * sunDistance;
		final float sunPositionY = sunDirectionY * sunDistance;
		final float sunPositionZ = sunDirectionZ * sunDistance;
		
		final float sunAmbientCoefficient = 0.2F;
		
//		final float sunLightIntensity = 3.0F;
		
		for(float t = minT; t < maxT; t += delT) {
			final float surfaceIntersectionPointX = originX + directionX * t;
			final float surfaceIntersectionPointY = originY + directionY * t;
			final float surfaceIntersectionPointZ = originZ + directionZ * t;
			
			final float height = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ);
			
			if(surfaceIntersectionPointY < height) {
//				hitT = t - delT + delT * (lh - ly) / (surfaceIntersectionPointY - ly - height + lh);
				
				final float gray = 0.5F;
				
//				Calculate the albedo color of the surface intersection point:
				float albedoColorR = gray;
				float albedoColorG = gray;
				float albedoColorB = gray;
				
				/*
				if(height > 0.1F) {
					albedoColorR = gray;
					albedoColorG = gray;
					albedoColorB = gray;
				} else if(height > 0.02F) {
					albedoColorR = gray;
					albedoColorG = gray;
					albedoColorB = gray;
				} else {
					albedoColorR = gray;
					albedoColorG = gray;
					albedoColorB = gray;
				}
				*/
				
//				Calculate the surface normal at the surface intersection point:
				final float surfaceNormal0X = doGetY(surfaceIntersectionPointX - EPSILON, surfaceIntersectionPointZ) - doGetY(surfaceIntersectionPointX + EPSILON, surfaceIntersectionPointZ);
				final float surfaceNormal0Y = 2.0F * EPSILON;
				final float surfaceNormal0Z = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ - EPSILON) - doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ + EPSILON);
				final float surfaceNormal0LengthReciprocal = 1.0F / sqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
//				Calculate the sun and sky color given the surface normal at the surface intersection point:
//				doCalculateColorForSky(pixelIndex, surfaceNormal1X, surfaceNormal1Y, surfaceNormal1Z);
				
//				final float sunAndSkyColorR = this.temporaryColors[pixelIndex0];
//				final float sunAndSkyColorG = this.temporaryColors[pixelIndex0 + 1];
//				final float sunAndSkyColorB = this.temporaryColors[pixelIndex0 + 2];
				
//				Calculate the direction from the surface intersection point to the sun:
				final float surfaceToSun0X = sunPositionX - surfaceIntersectionPointX;
				final float surfaceToSun0Y = sunPositionY - surfaceIntersectionPointY;
				final float surfaceToSun0Z = sunPositionZ - surfaceIntersectionPointZ;
				final float surfaceToSun0LengthReciprocal = 1.0F / sqrt(surfaceToSun0X * surfaceToSun0X + surfaceToSun0Y * surfaceToSun0Y + surfaceToSun0Z * surfaceToSun0Z);
				final float surfaceToSun1X = surfaceToSun0X * surfaceToSun0LengthReciprocal;
				final float surfaceToSun1Y = surfaceToSun0Y * surfaceToSun0LengthReciprocal;
				final float surfaceToSun1Z = surfaceToSun0Z * surfaceToSun0LengthReciprocal;
				
//				Calculate the direction from the surface intersection point to the camera:
				final float surfaceToCameraX = -directionX;
				final float surfaceToCameraY = -directionY;
				final float surfaceToCameraZ = -directionZ;
				
//				Calculate the ambient color:
				final float ambientColorR = sunAmbientCoefficient * albedoColorR * 1.0F;//sunAndSkyColorR;
				final float ambientColorG = sunAmbientCoefficient * albedoColorG * 1.0F;//sunAndSkyColorG;
				final float ambientColorB = sunAmbientCoefficient * albedoColorB * 1.0F;//sunAndSkyColorB;
				
//				Calculate the diffuse color:
				final float dotProductSurfaceNormalAndSurfaceToSun = surfaceNormal1X * surfaceToSun1X + surfaceNormal1Y * surfaceToSun1Y + surfaceNormal1Z * surfaceToSun1Z;
				final float diffuseCoefficient = max(dotProductSurfaceNormalAndSurfaceToSun, 0.0F);
				final float diffuseColorR = diffuseCoefficient * albedoColorR * 1.0F;//sunAndSkyColorR;
				final float diffuseColorG = diffuseCoefficient * albedoColorG * 1.0F;//sunAndSkyColorG;
				final float diffuseColorB = diffuseCoefficient * albedoColorB * 1.0F;//sunAndSkyColorB;
				
//				Calculate the specular color:
				float specularCoefficient = 0.0F;
				
				if(diffuseCoefficient > 0.0F) {
					final float incidentX = -surfaceToSun1X;
					final float incidentY = -surfaceToSun1Y;
					final float incidentZ = -surfaceToSun1Z;
					
					final float dotProductSurfaceNormalAndIncident = surfaceNormal1X * incidentX + surfaceNormal1Y * incidentY + surfaceNormal1Z * incidentZ;
					
					final float reflectionX = incidentX - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1X;
					final float reflectionY = incidentY - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1Y;
					final float reflectionZ = incidentZ - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1Z;
					
					final float dotProductSurfaceToCameraAndReflection = surfaceToCameraX * reflectionX + surfaceToCameraY * reflectionY + surfaceToCameraZ * reflectionZ;
					
					final float specularPower = 50.0F;
					
					specularCoefficient = pow(max(dotProductSurfaceToCameraAndReflection, 0.0F), specularPower);
				}
				
				final float specularColorR = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorR;
				final float specularColorG = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorG;
				final float specularColorB = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorB;
				
//				Calculate the final color in linear color space:
				pixelColorR = ambientColorR + diffuseColorR + specularColorR;
				pixelColorG = ambientColorG + diffuseColorG + specularColorG;
				pixelColorB = ambientColorB + diffuseColorB + specularColorB;
				
				/*
				final float dotProduct = surfaceNormal1X * sunDirectionX + surfaceNormal1Y * sunDirectionY + surfaceNormal1Z * sunDirectionZ;
				final float dotProductOrZero = max(dotProduct, 0.0F);
				
				final float ambientColorR = albedoColorR * this.temporaryColors[pixelIndex0];
				final float ambientColorG = albedoColorG * this.temporaryColors[pixelIndex0 + 1];
				final float ambientColorB = albedoColorB * this.temporaryColors[pixelIndex0 + 2];
				
				final float diffuseColorR = albedoColorR * dotProductOrZero * sunLightIntensity;
				final float diffuseColorG = albedoColorG * dotProductOrZero * sunLightIntensity;
				final float diffuseColorB = albedoColorB * dotProductOrZero * sunLightIntensity;
				
				final float reflectionX = surfaceNormal1X * 2.0F * dotProduct - sunDirectionX;
				final float reflectionY = surfaceNormal1Y * 2.0F * dotProduct - sunDirectionY;
				final float reflectionZ = surfaceNormal1Z * 2.0F * dotProduct - sunDirectionZ;
				
				final float dotProduct0 = -directionX * reflectionX + -directionY * reflectionY + -directionZ * reflectionZ;
				
				final float specularPower = 128.0F;
				final float specularAmount = dotProduct0 < 0.0F ? 0.0F : pow(dotProduct0, specularPower) * sunLightIntensity;
				final float specularColorR = specularAmount;
				final float specularColorG = specularAmount;
				final float specularColorB = specularAmount;
				
				pixelColorR = ambientColorR + diffuseColorR + specularColorR;
				pixelColorG = ambientColorG + diffuseColorG + specularColorG;
				pixelColorB = ambientColorB + diffuseColorB + specularColorB;
				*/
				
//				final float fogColorR = 0.5F;
//				final float fogColorG = 0.6F;
//				final float fogColorB = 0.7F;
				
//				final float distance = 20.0F;//hitT;
//				final float falloff = 0.2F;
//				final float exponent = exp(-distance * falloff);
				
//				pixelColorR = pixelColorR * (1.0F - exponent) + fogColorR * exponent;
//				pixelColorG = pixelColorG * (1.0F - exponent) + fogColorG * exponent;
//				pixelColorB = pixelColorB * (1.0F - exponent) + fogColorB * exponent;
				
				t = maxT;
			}
			
			delT = delTMultiplier * t;
			
//			lh = height;
//			ly = surfaceIntersectionPointY;
		}
		
//		if(originY <= 0.02F) {
//			pixelColorB += 0.4F;
//		}
		
		this.currentPixelColors[pixelIndex0] = pixelColorR;
		this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;
		this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	private static float doGradientX(final int hash, final float x) {
		final int hash0 = hash & 0x0F;
		
		float gradient = 1.0F + (hash0 & 7);
		
		if((hash0 & 8) != 0) {
			gradient = -gradient;
		}
		
		return gradient * x;
	}
	*/
	
	private static float doGradientXY(final int hash, final float x, final float y) {
		final int hash0 = hash & 0x3F;
		
		final float u = hash0 < 4 ? x : y;
		final float v = hash0 < 4 ? y : x;
		
		return ((hash0 & 1) == 1 ? -u : u) + ((hash0 & 2) == 1 ? -2.0F * v : 2.0F * v);
	}
	
	private static int doFastFloor(final float value) {
		final int i = (int)(value);
		
		return value < i ? i - 1 : i;
	}
}