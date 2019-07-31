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
package org.dayflower.pathtracer.kernel;

import java.util.Arrays;
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.color.colorspace.RGBColorSpace;
import org.dayflower.pathtracer.math.MathF;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.loader.SceneLoader;
import org.dayflower.pathtracer.scene.material.ClearCoatMaterial;
import org.dayflower.pathtracer.scene.material.GlassMaterial;
import org.dayflower.pathtracer.scene.material.LambertianMaterial;
import org.dayflower.pathtracer.scene.material.PhongMaterial;
import org.dayflower.pathtracer.scene.material.ReflectionMaterial;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.TriangleMesh;
import org.dayflower.pathtracer.scene.texture.BlendTexture;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ConstantTexture;
import org.dayflower.pathtracer.scene.texture.FractionalBrownianMotionTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SurfaceNormalTexture;
import org.dayflower.pathtracer.util.FloatArrayThreadLocal;

/**
 * An extension of the {@code AbstractKernel} class that performs Path Tracing, Ray Casting and Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class RendererKernel extends AbstractKernel {
	private static final float COLOR_RECIPROCAL = 1.0F / 255.0F;
	private static final float REFRACTIVE_INDEX_AIR = 1.0F;
	private static final float REFRACTIVE_INDEX_GLASS = 1.5F;
	private static final int BOOLEAN_FALSE = 0;
	private static final int BOOLEAN_TRUE = 1;
	private static final int RELATIVE_OFFSET_INTERSECTION_DISTANCE = 0;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_U = 10;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_V = 13;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_W = 16;
	private static final int RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET = 1;
	private static final int RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET = 23;
	private static final int RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE = 22;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT = 2;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL = 5;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING = 19;
	private static final int RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES = 8;
	private static final int RENDERER_AMBIENT_OCCLUSION = 0;
	private static final int RENDERER_PATH_TRACER = 1;
	private static final int RENDERER_RAY_CASTER = 2;
	private static final int RENDERER_RAY_MARCHER = 3;
	private static final int RENDERER_RAY_TRACER = 4;
	private static final int SHADING_FLAT = 1;
	private static final int SHADING_GOURAUD = 2;
	private static final int SIZE_COLOR_RGB = 3;
	private static final int SIZE_INTERSECTION = 24;
	private static final int SIZE_PIXEL = 4;
	private static final int SIZE_RAY = 6;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_1 = 1;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_2 = 2;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_LINEAR = 3;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_1 = 4;
	private static final int TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2 = 5;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Camera camera;
	private final CompiledScene compiledScene;
	private final Scene scene;
	private final Sky sky;
	private final ThreadLocal<float[]> colorTemporarySamplesThreadLocal;
	private final ThreadLocal<float[]> raysThreadLocal;
	private boolean isResetRequired;
	private byte[] pixels;
	private double sunAndSkyZenithRelativeLuminance;
	private double sunAndSkyZenithX;
	private double sunAndSkyZenithY;
	private final double[] sunAndSkyPerezRelativeLuminance_$constant$;
	private final double[] sunAndSkyPerezX_$constant$;
	private final double[] sunAndSkyPerezY_$constant$;
	private final float colorSpaceBreakPoint;
	private final float colorSpaceGamma;
	private final float colorSpaceSegmentOffset;
	private final float colorSpaceSlope;
	private final float colorSpaceSlopeMatch;
	private float rendererAOMaximumDistance;
	private float sunAndSkyJacobian;
	private float sunAndSkyOrthoNormalBasisUX;
	private float sunAndSkyOrthoNormalBasisUY;
	private float sunAndSkyOrthoNormalBasisUZ;
	private float sunAndSkyOrthoNormalBasisVX;
	private float sunAndSkyOrthoNormalBasisVY;
	private float sunAndSkyOrthoNormalBasisVZ;
	private float sunAndSkyOrthoNormalBasisWX;
	private float sunAndSkyOrthoNormalBasisWY;
	private float sunAndSkyOrthoNormalBasisWZ;
	private float sunAndSkySunColorB;
	private float sunAndSkySunColorG;
	private float sunAndSkySunColorR;
	private float sunAndSkySunDirectionWorldX;
	private float sunAndSkySunDirectionWorldY;
	private float sunAndSkySunDirectionWorldZ;
	private float sunAndSkySunDirectionX;
	private float sunAndSkySunDirectionY;
	private float sunAndSkySunDirectionZ;
	private float sunAndSkySunOriginX;
	private float sunAndSkySunOriginY;
	private float sunAndSkySunOriginZ;
	private float sunAndSkyTheta;
	private float sunAndSkyTurbidity;
	private final float[] colorAverageSamples;
	private float[] colorCurrentSamples_$local$;
	private float[] colorTemporarySamples_$private$3;
	private final float[] sceneCamera_$constant$;
	private final float[] scenePoint2Fs_$constant$;
	private final float[] scenePoint3Fs_$constant$;
	private final float[] sceneSpheres_$constant$;
	private final float[] sceneSurfaces_$constant$;
	private final float[] sceneTerrains_$constant$;
	private final float[] sceneTextures_$constant$;
	private final float[] sceneVector3Fs_$constant$;
	private final float[] sunAndSkyColHistogram_$constant$;
	private final float[] sunAndSkyImageHistogram_$constant$;
	private float[] intersections_$local$;
	private float[] rays_$private$6;
	private int effectGrayScale;
	private int effectSepiaTone;
	private final int height;
	private int isNormalMapping = BOOLEAN_TRUE;
	private int isRenderingWireframes = BOOLEAN_FALSE;
	private int renderer;
	private int rendererPTRayDepthMaximum;
	private int rendererPTRayDepthRussianRoulette;
	private final int scenePrimitivesCount;
//	private final int scenePrimitivesEmittingLightCount;
	private int selectedPrimitiveOffset = -1;
	private int shading = SHADING_GOURAUD;
	private int sunAndSkyColHistogramLength;
	private int sunAndSkyImageHistogramHeight;
	private int sunAndSkyIsActive;
	private int sunAndSkyIsShowingClouds;
	private int sunAndSkySamples;
	private int toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_2;
	private final int width;
	private int[] primitiveOffsetsForPrimaryRay;
	private final int[] sceneBoundingVolumeHierarchies_$constant$;
	private final int[] scenePlanes_$constant$;
	private final int[] scenePrimitives_$constant$;
	private final int[] scenePrimitivesEmittingLight_$constant$;
	private final int[] sceneTriangles_$constant$;
	private final long[] subSamples;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code RendererKernel} instance.
	 * <p>
	 * If {@code sceneLoader} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param width the width to use
	 * @param height the height to use
	 * @param sceneLoader the {@link SceneLoader} to use
	 * @throws NullPointerException thrown if, and only if, {@code sceneLoader} is {@code null}
	 */
	public RendererKernel(final int width, final int height, final SceneLoader sceneLoader) {
		final RGBColorSpace rGBColorSpace = RGBColorSpace.SRGB;
		
		final Scene scene = sceneLoader.loadScene();
		
		final CompiledScene compiledScene = sceneLoader.loadCompiledScene();
		
		this.camera = scene.getCamera();
		this.camera.setArray(compiledScene.getCamera());
		this.compiledScene = compiledScene;
		this.scene = scene;
		this.sky = scene.getSky();
		this.colorTemporarySamplesThreadLocal = new FloatArrayThreadLocal(SIZE_COLOR_RGB);
		this.raysThreadLocal = new FloatArrayThreadLocal(SIZE_RAY);
		
//		Initialize the color variables:
		this.colorAverageSamples = new float[width * height * SIZE_COLOR_RGB];
		this.colorTemporarySamples_$private$3 = new float[SIZE_COLOR_RGB];
		
//		Initialize the color space variables:
		this.colorSpaceBreakPoint = rGBColorSpace.getBreakPoint();
		this.colorSpaceGamma = rGBColorSpace.getGamma();
		this.colorSpaceSegmentOffset = rGBColorSpace.getSegmentOffset();
		this.colorSpaceSlope = rGBColorSpace.getSlope();
		this.colorSpaceSlopeMatch = rGBColorSpace.getSlopeMatch();
		
//		Initialize the scene variables:
		this.sceneCamera_$constant$ = compiledScene.getCamera();
		this.scenePoint2Fs_$constant$ = compiledScene.getPoint2Fs();
		this.scenePoint3Fs_$constant$ = compiledScene.getPoint3Fs();
		this.sceneSpheres_$constant$ = compiledScene.getSpheres();
		this.sceneSurfaces_$constant$ = compiledScene.getSurfaces();
		this.sceneTerrains_$constant$ = compiledScene.getTerrains();
		this.sceneTextures_$constant$ = compiledScene.getTextures();
		this.sceneVector3Fs_$constant$ = compiledScene.getVector3Fs();
		this.sceneBoundingVolumeHierarchies_$constant$ = compiledScene.getBoundingVolumeHierarchies();
		this.scenePlanes_$constant$ = compiledScene.getPlanes();
		this.scenePrimitives_$constant$ = compiledScene.getPrimitives();
		this.scenePrimitivesCount = this.scenePrimitives_$constant$.length / Primitive.SIZE;
		this.scenePrimitivesEmittingLight_$constant$ = compiledScene.getPrimitivesEmittingLight();
//		this.scenePrimitivesEmittingLightCount = this.scenePrimitivesEmittingLight_$constant$[0];
		this.sceneTriangles_$constant$ = compiledScene.getTriangles();
		
//		Initialize the renderer parameters:
		this.renderer = RENDERER_PATH_TRACER;
		this.rendererAOMaximumDistance = 200.0F;
		this.rendererPTRayDepthMaximum = 5;
		this.rendererPTRayDepthRussianRoulette = 5;
		
//		Initialize the sun and sky variables:
		this.sunAndSkyColHistogram_$constant$ = this.sky.getColHistogram();
		this.sunAndSkyColHistogramLength = this.sunAndSkyColHistogram_$constant$.length;
		this.sunAndSkyImageHistogram_$constant$ = this.sky.getImageHistogram();
		this.sunAndSkyImageHistogramHeight = this.sky.getImageHistogramHeight();
		this.sunAndSkyIsActive = BOOLEAN_TRUE;
		this.sunAndSkyIsShowingClouds = BOOLEAN_FALSE;
		this.sunAndSkyJacobian = this.sky.getJacobian();
		this.sunAndSkyOrthoNormalBasisUX = this.sky.getOrthoNormalBasis().u.x;
		this.sunAndSkyOrthoNormalBasisUY = this.sky.getOrthoNormalBasis().u.y;
		this.sunAndSkyOrthoNormalBasisUZ = this.sky.getOrthoNormalBasis().u.z;
		this.sunAndSkyOrthoNormalBasisVX = this.sky.getOrthoNormalBasis().v.x;
		this.sunAndSkyOrthoNormalBasisVY = this.sky.getOrthoNormalBasis().v.y;
		this.sunAndSkyOrthoNormalBasisVZ = this.sky.getOrthoNormalBasis().v.z;
		this.sunAndSkyOrthoNormalBasisWX = this.sky.getOrthoNormalBasis().w.x;
		this.sunAndSkyOrthoNormalBasisWY = this.sky.getOrthoNormalBasis().w.y;
		this.sunAndSkyOrthoNormalBasisWZ = this.sky.getOrthoNormalBasis().w.z;
		this.sunAndSkyPerezRelativeLuminance_$constant$ = this.sky.getPerezRelativeLuminance();
		this.sunAndSkyPerezX_$constant$ = this.sky.getPerezX();
		this.sunAndSkyPerezY_$constant$ = this.sky.getPerezY();
		this.sunAndSkySamples = this.sky.getSamples();
		this.sunAndSkySunColorR = this.sky.getSunColor().r;
		this.sunAndSkySunColorG = this.sky.getSunColor().g;
		this.sunAndSkySunColorB = this.sky.getSunColor().b;
		this.sunAndSkySunDirectionWorldX = this.sky.getSunDirectionWorld().x;
		this.sunAndSkySunDirectionWorldY = this.sky.getSunDirectionWorld().y;
		this.sunAndSkySunDirectionWorldZ = this.sky.getSunDirectionWorld().z;
		this.sunAndSkySunDirectionX = this.sky.getSunDirection().x;
		this.sunAndSkySunDirectionY = this.sky.getSunDirection().y;
		this.sunAndSkySunDirectionZ = this.sky.getSunDirection().z;
		this.sunAndSkySunOriginX = this.sky.getSunOrigin().x;
		this.sunAndSkySunOriginY = this.sky.getSunOrigin().y;
		this.sunAndSkySunOriginZ = this.sky.getSunOrigin().z;
		this.sunAndSkyTheta = this.sky.getTheta();
		this.sunAndSkyTurbidity = this.sky.getTurbidity();
		this.sunAndSkyZenithRelativeLuminance = this.sky.getZenithRelativeLuminance();
		this.sunAndSkyZenithX = this.sky.getZenithX();
		this.sunAndSkyZenithY = this.sky.getZenithY();
		
		this.rays_$private$6 = new float[SIZE_RAY];
		this.width = width;
		this.height = height;
		this.subSamples = new long[width * height];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Camera} instance assigned to this {@code RendererKernel} instance.
	 * 
	 * @return the {@code Camera} instance assigned to this {@code RendererKernel} instance
	 */
	public Camera getCamera() {
		return this.camera;
	}
	
	/**
	 * Returns the {@link CompiledScene}.
	 * 
	 * @return the {@code CompiledScene}
	 */
	public CompiledScene getCompiledScene() {
		return this.compiledScene;
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
		this.primitiveOffsetsForPrimaryRay = new int[width * height];
		
		Arrays.fill(this.primitiveOffsetsForPrimaryRay, -1);
		
		setExplicit(true);
//		setExecutionMode(EXECUTION_MODE.JTP);
		
		update(width, height);
		
		put(this.sceneBoundingVolumeHierarchies_$constant$);
		put(this.sceneCamera_$constant$);
		put(this.scenePlanes_$constant$);
		put(this.scenePoint2Fs_$constant$);
		put(this.scenePoint3Fs_$constant$);
		put(this.scenePrimitives_$constant$);
		put(this.scenePrimitivesEmittingLight_$constant$);
		put(this.sceneSpheres_$constant$);
		put(this.sceneSurfaces_$constant$);
		put(this.sceneTerrains_$constant$);
		put(this.sceneTextures_$constant$);
		put(this.sceneTriangles_$constant$);
		put(this.sceneVector3Fs_$constant$);
		
		put(this.pixels);
		put(this.colorAverageSamples);
		put(this.sunAndSkyPerezRelativeLuminance_$constant$);
		put(this.sunAndSkyPerezX_$constant$);
		put(this.sunAndSkyPerezY_$constant$);
		put(this.primitiveOffsetsForPrimaryRay);
		put(this.subSamples);
		put(this.sunAndSkyImageHistogram_$constant$);
		put(this.sunAndSkyColHistogram_$constant$);
		
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
		final boolean isResettingFully = this.renderer != RENDERER_AMBIENT_OCCLUSION && this.renderer != RENDERER_PATH_TRACER;
		
		for(int i = 0; i < this.subSamples.length; i++) {
			if(isResettingFully) {
				final int pixelIndex = i * SIZE_COLOR_RGB;
				
				this.colorAverageSamples[pixelIndex + 0] = 0.0F;
				this.colorAverageSamples[pixelIndex + 1] = 0.0F;
				this.colorAverageSamples[pixelIndex + 2] = 0.0F;
				this.subSamples[i] = 0L;
			} else {
				this.subSamples[i] = 1L;
			}
		}
		
		final
		Camera camera = getCamera();
		camera.update();
		
		put(this.sceneCamera_$constant$);
		
		if(isResettingFully) {
			put(this.colorAverageSamples);
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
	public RendererKernel updateLocalVariables(final int localSize) {
		this.colorCurrentSamples_$local$ = new float[localSize * SIZE_COLOR_RGB];
		this.intersections_$local$ = new float[localSize * SIZE_INTERSECTION];
		
		return this;
	}
	
	/**
	 * Updates the variables related to the {@link Sky}.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @return itself for method chaining
	 */
	public RendererKernel updateSky() {
		this.sunAndSkyJacobian = this.sky.getJacobian();
		this.sunAndSkyOrthoNormalBasisUX = this.sky.getOrthoNormalBasis().u.x;
		this.sunAndSkyOrthoNormalBasisUY = this.sky.getOrthoNormalBasis().u.y;
		this.sunAndSkyOrthoNormalBasisUZ = this.sky.getOrthoNormalBasis().u.z;
		this.sunAndSkyOrthoNormalBasisVX = this.sky.getOrthoNormalBasis().v.x;
		this.sunAndSkyOrthoNormalBasisVY = this.sky.getOrthoNormalBasis().v.y;
		this.sunAndSkyOrthoNormalBasisVZ = this.sky.getOrthoNormalBasis().v.z;
		this.sunAndSkyOrthoNormalBasisWX = this.sky.getOrthoNormalBasis().w.x;
		this.sunAndSkyOrthoNormalBasisWY = this.sky.getOrthoNormalBasis().w.y;
		this.sunAndSkyOrthoNormalBasisWZ = this.sky.getOrthoNormalBasis().w.z;
		this.sunAndSkySamples = this.sky.getSamples();
		this.sunAndSkySunColorB = this.sky.getSunColor().b;
		this.sunAndSkySunColorG = this.sky.getSunColor().g;
		this.sunAndSkySunColorR = this.sky.getSunColor().r;
		this.sunAndSkySunDirectionWorldX = this.sky.getSunDirectionWorld().x;
		this.sunAndSkySunDirectionWorldY = this.sky.getSunDirectionWorld().y;
		this.sunAndSkySunDirectionWorldZ = this.sky.getSunDirectionWorld().z;
		this.sunAndSkySunDirectionX = this.sky.getSunDirection().x;
		this.sunAndSkySunDirectionY = this.sky.getSunDirection().y;
		this.sunAndSkySunDirectionZ = this.sky.getSunDirection().z;
		this.sunAndSkySunOriginX = this.sky.getSunOrigin().x;
		this.sunAndSkySunOriginY = this.sky.getSunOrigin().y;
		this.sunAndSkySunOriginZ = this.sky.getSunOrigin().z;
		this.sunAndSkyTheta = this.sky.getTheta();
		this.sunAndSkyTurbidity = this.sky.getTurbidity();
		this.sunAndSkyZenithRelativeLuminance = this.sky.getZenithRelativeLuminance();
		this.sunAndSkyZenithX = this.sky.getZenithX();
		this.sunAndSkyZenithY = this.sky.getZenithY();
		
		System.arraycopy(this.sky.getColHistogram(), 0, this.sunAndSkyColHistogram_$constant$, 0, this.sunAndSkyColHistogram_$constant$.length);
		System.arraycopy(this.sky.getImageHistogram(), 0, this.sunAndSkyImageHistogram_$constant$, 0, this.sunAndSkyImageHistogram_$constant$.length);
		System.arraycopy(this.sky.getPerezRelativeLuminance(), 0, this.sunAndSkyPerezRelativeLuminance_$constant$, 0, this.sunAndSkyPerezRelativeLuminance_$constant$.length);
		System.arraycopy(this.sky.getPerezX(), 0, this.sunAndSkyPerezX_$constant$, 0, this.sunAndSkyPerezX_$constant$.length);
		System.arraycopy(this.sky.getPerezY(), 0, this.sunAndSkyPerezY_$constant$, 0, this.sunAndSkyPerezY_$constant$.length);
		
		put(this.sunAndSkyColHistogram_$constant$);
		put(this.sunAndSkyImageHistogram_$constant$);
		put(this.sunAndSkyPerezRelativeLuminance_$constant$);
		put(this.sunAndSkyPerezX_$constant$);
		put(this.sunAndSkyPerezY_$constant$);
		
		this.isResetRequired = true;
		
		return this;
	}
	
	/**
	 * Returns the {@link Scene} instance assigned to this {@code RendererKernel} instance.
	 * 
	 * @return the {@code Scene} instance assigned to this {@code RendererKernel} instance
	 */
	public Scene getScene() {
		return this.scene;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ambient Occlusion is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ambient Occlusion is enabled, {@code false} otherwise
	 */
	public boolean isAmbientOcclusion() {
		return this.renderer == RENDERER_AMBIENT_OCCLUSION;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise
	 */
	public boolean isEffectGrayScale() {
		return this.effectGrayScale == BOOLEAN_TRUE;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	public boolean isEffectSepiaTone() {
		return this.effectSepiaTone == BOOLEAN_TRUE;
	}
	
	/**
	 * Returns {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise
	 */
	public boolean isNormalMapping() {
		return this.isNormalMapping == BOOLEAN_TRUE;
	}
	
	/**
	 * Returns {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise
	 */
	public boolean isPathTracing() {
		return this.renderer == RENDERER_PATH_TRACER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise
	 */
	public boolean isRayCasting() {
		return this.renderer == RENDERER_RAY_CASTER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise
	 */
	public boolean isRayMarching() {
		return this.renderer == RENDERER_RAY_MARCHER;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Tracing is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Tracing is enabled, {@code false} otherwise
	 */
	public boolean isRayTracing() {
		return this.renderer == RENDERER_RAY_TRACER;
	}
	
	/**
	 * Returns {@code true} if, and only if, wireframe rendering is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, wireframe rendering is enabled, {@code false} otherwise
	 */
	public boolean isRenderingWireframes() {
		return this.isRenderingWireframes != BOOLEAN_FALSE;
	}
	
	/**
	 * Returns {@code true} if, and only if, reset is required, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, reset is required, {@code false} otherwise
	 */
	public boolean isResetRequired() {
		return this.isResetRequired;
	}
	
	/**
	 * Returns {@code true} if, and only if, Flat Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Flat Shading is used, {@code false} otherwise
	 */
	public boolean isShadingFlat() {
		return this.shading == SHADING_FLAT;
	}
	
	/**
	 * Returns {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise
	 */
	public boolean isShadingGouraud() {
		return this.shading == SHADING_GOURAUD;
	}
	
	/**
	 * Returns {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise
	 */
	public boolean isShowingClouds() {
		return this.sunAndSkyIsShowingClouds == BOOLEAN_TRUE;
	}
	
	/**
	 * Returns the {@code byte} array with the pixels.
	 * 
	 * @return the {@code byte} array with the pixels
	 */
	public byte[] getPixels() {
		return this.pixels;
	}
	
	/**
	 * Returns the maximum distance for Ambient Occlusion.
	 * 
	 * @return the maximum distance for Ambient Occlusion
	 */
	public float getRendererAOMaximumDistance() {
		return this.rendererAOMaximumDistance;
	}
	
	/**
	 * Returns the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the maximum ray depth for Path Tracing.
	 * 
	 * @return the maximum ray depth for Path Tracing
	 */
	public int getRendererPTRayDepthMaximum() {
		return this.rendererPTRayDepthMaximum;
	}
	
	/**
	 * Returns the ray depth to begin Russian Roulette path termination for Path Tracing.
	 * 
	 * @return the ray depth to begin Russian Roulette path termination for Path Tracing
	 */
	public int getRendererPTRayDepthRussianRoulette() {
		return this.rendererPTRayDepthRussianRoulette;
	}
	
	/**
	 * Returns the selected primitive offset.
	 * 
	 * @return the selected primitive offset
	 */
	public int getSelectedPrimitiveOffset() {
		return this.selectedPrimitiveOffset;
	}
	
	/**
	 * Returns the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the primitive offsets for all primary rays.
	 * 
	 * @return the primitive offsets for all primary rays
	 */
	public int[] getPrimitiveOffsetsForPrimaryRay() {
		get(this.primitiveOffsetsForPrimaryRay);
		
		return this.primitiveOffsetsForPrimaryRay;
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
	public void drawLine(final int x0, final int y0, final int x1, final int y1, final Color color) {
		final int width = x1 - x0;
		final int height = y1 - y0;
		
		int delta0X = width < 0 ? -1 : width > 0 ? 1 : 0;
		int delta0Y = height < 0 ? -1 : height > 0 ? 1 : 0;
		int delta1X = width < 0 ? -1 : width > 0 ? 1 : 0;
		int delta1Y = 0;
		
		int longest = MathF.abs(width);
		int shortest = MathF.abs(height);
		
		if(longest <= shortest) {
			longest = MathF.abs(height);
			shortest = MathF.abs(width);
			
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
	public void fillRectangle(final int x, final int y, final int width, final int height, final Color color) {
		for(int i = y; i < y + height; i++) {
			for(int j = x; j < x + width; j++) {
				doFill(j, i, color);
			}
		}
	}
	
	/**
	 * Called when this {@code RendererKernel} is run in JTP mode.
	 */
	@NoCL
	public void noCL() {
		this.colorTemporarySamples_$private$3 = this.colorTemporarySamplesThreadLocal.get();
		this.rays_$private$6 = this.raysThreadLocal.get();
	}
	
	/**
	 * Performs the Path Tracing.
	 */
	@Override
	public void run() {
		noCL();
		
		final int pixelIndex = getGlobalId();
		
		if(doCreatePrimaryRay(pixelIndex)) {
			if(this.renderer == RENDERER_AMBIENT_OCCLUSION) {
				doAmbientOcclusion(1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
			} else if(this.renderer == RENDERER_PATH_TRACER) {
				doPathTracing();
			} else if(this.renderer == RENDERER_RAY_CASTER) {
				doRayCasting();
			} else if(this.renderer == RENDERER_RAY_MARCHER) {
				doRayMarching();
			} else if(this.renderer == RENDERER_RAY_TRACER) {
				doRayTracing();
			} else {
				doPathTracing();
			}
			
			if(this.isRenderingWireframes != 0) {
				doWireframeRendering();
			}
		} else {
			final int pixelIndex0 = pixelIndex * SIZE_COLOR_RGB;
			final int pixelIndex1 = getLocalId() * SIZE_COLOR_RGB;
			
			this.colorAverageSamples[pixelIndex0] = 0.0F;
			this.colorAverageSamples[pixelIndex0 + 1] = 0.0F;
			this.colorAverageSamples[pixelIndex0 + 2] = 0.0F;
			
			this.colorCurrentSamples_$local$[pixelIndex1 + 0] = 0.0F;
			this.colorCurrentSamples_$local$[pixelIndex1 + 1] = 0.0F;
			this.colorCurrentSamples_$local$[pixelIndex1 + 2] = 0.0F;
			
			this.subSamples[pixelIndex] = 1L;
		}
		
		doCalculateColor(pixelIndex);
	}
	
	/**
	 * Sets whether Ambient Occlusion should be enabled or disabled.
	 * <p>
	 * If {@code isAmbientOcclusion} is {@code false}, the renderer will be a Path Tracer.
	 * 
	 * @param isAmbientOcclusion the Ambient Occlusion state to set
	 */
	public void setAmbientOcclusion(final boolean isAmbientOcclusion) {
		this.renderer = isAmbientOcclusion ? RENDERER_AMBIENT_OCCLUSION : RENDERER_PATH_TRACER;
		this.isResetRequired = true;
	}
	
	/**
	 * Enables or disables the Grayscale effect.
	 * 
	 * @param isEffectGrayScale {@code true} if the Grayscale effect is enabled, {@code false} otherwise
	 */
	public void setEffectGrayScale(final boolean isEffectGrayScale) {
		this.effectGrayScale = isEffectGrayScale ? 1 : 0;
	}
	
	/**
	 * Enables or disables the Sepia Tone effect.
	 * 
	 * @param isEffectSepiaTone {@code true} if the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	public void setEffectSepiaTone(final boolean isEffectSepiaTone) {
		this.effectSepiaTone = isEffectSepiaTone ? 1 : 0;
	}
	
	/**
	 * Sets the Normal Mapping state.
	 * 
	 * @param isNormalMapping {@code true} if, and only if, Normal Mapping is activated, {@code false} otherwise
	 */
	public void setNormalMapping(final boolean isNormalMapping) {
		this.isNormalMapping = isNormalMapping ? 1 : 0;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets whether Path Tracing should be enabled or disabled.
	 * <p>
	 * If {@code isPathTracing} is {@code false}, the renderer will be a Ray Caster.
	 * 
	 * @param isPathTracing the Path Tracing state to set
	 */
	public void setPathTracing(final boolean isPathTracing) {
		this.renderer = isPathTracing ? RENDERER_PATH_TRACER : RENDERER_RAY_CASTER;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets whether Ray Casting should be enabled or disabled.
	 * <p>
	 * If {@code isRayCasting} is {@code false}, the renderer will be a Ray Marcher.
	 * 
	 * @param isRayCasting the Ray Casting state to set
	 */
	public void setRayCasting(final boolean isRayCasting) {
		this.renderer = isRayCasting ? RENDERER_RAY_CASTER : RENDERER_RAY_MARCHER;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets whether Ray Marching should be enabled or disabled.
	 * <p>
	 * If {@code isRayMarching} is {@code false}, the renderer will be a Ray Tracer.
	 * 
	 * @param isRayMarching the Ray Marching state to set
	 */
	public void setRayMarching(final boolean isRayMarching) {
		this.renderer = isRayMarching ? RENDERER_RAY_MARCHER : RENDERER_RAY_TRACER;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets whether Ray Tracing should be enabled or disabled.
	 * <p>
	 * If {@code isRayTracing} is {@code false}, the renderer will be Ambient Occlusion.
	 * 
	 * @param isRayTracing the Ray Tracing state to set
	 */
	public void setRayTracing(final boolean isRayTracing) {
		this.renderer = isRayTracing ? RENDERER_RAY_TRACER : RENDERER_AMBIENT_OCCLUSION;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the maximum distance for Ambient Occlusion.
	 * 
	 * @param rendererAOMaximumDistance the maximum distance for Ambient Occlusion
	 */
	public void setRendererAOMaximumDistance(final float rendererAOMaximumDistance) {
		this.rendererAOMaximumDistance = rendererAOMaximumDistance;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the maximum ray depth for Path Tracing.
	 * 
	 * @param rendererPTRayDepthMaximum the maximum ray depth for Path Tracing
	 */
	public void setRendererPTRayDepthMaximum(final int rendererPTRayDepthMaximum) {
		this.rendererPTRayDepthMaximum = rendererPTRayDepthMaximum;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the ray depth to begin Russian Roulette path termination for Path Tracing.
	 * 
	 * @param rendererPTRayDepthRussianRoulette the ray depth to begin Russian Roulette path termination for Path Tracing
	 */
	public void setRendererPTRayDepthRussianRoulette(final int rendererPTRayDepthRussianRoulette) {
		this.rendererPTRayDepthRussianRoulette = rendererPTRayDepthRussianRoulette;
	}
	
	/**
	 * Sets whether wireframe rendering should be enabled or disabled.
	 * 
	 * @param isRenderingWireframes the wireframe rendering state to set
	 */
	public void setRenderingWireframes(final boolean isRenderingWireframes) {
		this.isRenderingWireframes = isRenderingWireframes ? 1 : 0;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the selected primitive offset.
	 * 
	 * @param selectedPrimitiveOffset the selected primitive offset
	 */
	public void setSelectedPrimitiveOffset(final int selectedPrimitiveOffset) {
		this.selectedPrimitiveOffset = selectedPrimitiveOffset;
	}
	
	/**
	 * Sets Flat Shading.
	 */
	public void setShadingFlat() {
		this.shading = SHADING_FLAT;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets Gouraud Shading.
	 */
	public void setShadingGouraud() {
		this.shading = SHADING_GOURAUD;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets whether the sky is showing clouds or not.
	 * 
	 * @param isShowingClouds {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise
	 */
	public void setShowingClouds(final boolean isShowingClouds) {
		this.sunAndSkyIsShowingClouds = isShowingClouds ? BOOLEAN_TRUE : BOOLEAN_FALSE;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Filmic Curve version 1.
	 */
	public void setToneMappingAndGammaCorrectionFilmicCurve1() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_1;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Filmic Curve version 2.
	 */
	public void setToneMappingAndGammaCorrectionFilmicCurve2() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_2;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Linear.
	 */
	public void setToneMappingAndGammaCorrectionLinear() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_LINEAR;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 1.
	 */
	public void setToneMappingAndGammaCorrectionReinhard1() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_1;
		this.isResetRequired = true;
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 2.
	 */
	public void setToneMappingAndGammaCorrectionReinhard2() {
		this.toneMappingAndGammaCorrection = TONE_MAPPING_AND_GAMMA_CORRECTION_REINHARD_2;
		this.isResetRequired = true;
	}
	
	/**
	 * Toggles the visibility for the clouds in the sky.
	 */
	public void toggleClouds() {
		if(this.sunAndSkyIsShowingClouds == BOOLEAN_FALSE) {
			this.sunAndSkyIsShowingClouds = BOOLEAN_TRUE;
		} else {
			this.sunAndSkyIsShowingClouds = BOOLEAN_FALSE;
		}
	}
	
	/**
	 * Toggles the material for the selected shape.
	 */
	public void toggleMaterial() {
		final int selectedPrimitiveOffset = getSelectedPrimitiveOffset();
		
		if(selectedPrimitiveOffset != -1) {
			final int surfacesOffset = this.scenePrimitives_$constant$[selectedPrimitiveOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
			final int oldMaterialType = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			final int oldMaterialOrdinal = oldMaterialType - 1;
			
			final int[] materials = new int[] {
				ClearCoatMaterial.TYPE,
				GlassMaterial.TYPE,
				LambertianMaterial.TYPE,
				PhongMaterial.TYPE,
				ReflectionMaterial.TYPE
			};
			
			if(oldMaterialOrdinal >= 0 && oldMaterialOrdinal < materials.length) {
				final int newMaterialOrdinal = (oldMaterialOrdinal + 1) % materials.length;
				final int newMaterialType = materials[newMaterialOrdinal];
				
				this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL] = newMaterialType;
				
				put(this.sceneSurfaces_$constant$);
			}
		}
	}
	
	/**
	 * Toggles to the next renderer.
	 */
	public void toggleRenderer() {
		if(isAmbientOcclusion()) {
			setPathTracing(true);
		} else if(isPathTracing()) {
			setRayCasting(true);
		} else if(isRayCasting()) {
			setRayMarching(true);
		} else if(isRayMarching()) {
			setRayTracing(true);
		} else if(isRayTracing()) {
			setAmbientOcclusion(true);
		}
	}
	
	/**
	 * Toggles to the next shading.
	 */
	public void toggleShading() {
		if(isShadingFlat()) {
			setShadingGouraud();
		} else if(isShadingGouraud()) {
			setShadingFlat();
		}
	}
	
	/**
	 * Toggles the sun and sky.
	 */
	public void toggleSunAndSky() {
		if(this.sunAndSkyIsActive == BOOLEAN_FALSE) {
			this.sunAndSkyIsActive = BOOLEAN_TRUE;
		} else {
			this.sunAndSkyIsActive = BOOLEAN_FALSE;
		}
	}
	
	/**
	 * Updates the reset status.
	 * <p>
	 * This essentially means that {@code isResetRequired()} will return {@code false} immediately after this method has been called.
	 */
	public void updateResetStatus() {
		this.isResetRequired = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doCreatePrimaryRay(final int pixelIndex) {
//		Calculate the X- and Y-coordinates on the screen:
		final int y = pixelIndex / this.width;
		final int x = pixelIndex - y * this.width;
		
//		Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:
		final float eyeX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_X];
		final float eyeY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_Y];
		final float eyeZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_Z];
		
//		Retrieve the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float uX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X];
		final float uY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y];
		final float uZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z];
		
//		Retrieve the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float vX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X];
		final float vY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y];
		final float vZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z];
		
//		Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float wX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X];
		final float wY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y];
		final float wZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z];
		
//		Calculate the Field of View:
		final float fieldOfViewX0 = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FIELD_OF_VIEW_X];
		final float fieldOfViewX1 = tan(fieldOfViewX0 * PI_DIVIDED_BY_360);
		final float fieldOfViewY0 = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y];
		final float fieldOfViewY1 = tan(-fieldOfViewY0 * PI_DIVIDED_BY_360);
		
//		Calculate the horizontal direction:
		final float horizontalX = uX * fieldOfViewX1;
		final float horizontalY = uY * fieldOfViewX1;
		final float horizontalZ = uZ * fieldOfViewX1;
		
//		Calculate the vertical direction:
		final float verticalX = vX * fieldOfViewY1;
		final float verticalY = vY * fieldOfViewY1;
		final float verticalZ = vZ * fieldOfViewY1;
		
//		Calculate the pixel sample:
		float sampleX = 0.5F;
		float sampleY = 0.5F;
		
		if(this.renderer == RENDERER_PATH_TRACER) {
			sampleX = nextFloat();
			sampleY = nextFloat();
			
//			Box Filter:
//			sampleX -= 0.5F;
//			sampleY -= 0.5F;
			
//			Triangle Filter (Tent Filter):
			sampleX = sampleX < 0.5F ? sqrt(2.0F * sampleX) - 1.0F : 1.0F - sqrt(2.0F - 2.0F * sampleX);
			sampleY = sampleY < 0.5F ? sqrt(2.0F * sampleY) - 1.0F : 1.0F - sqrt(2.0F - 2.0F * sampleY);
		}
		
//		Calculate the pixel sample point:
		final float sx = (sampleX + x) / (this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_RESOLUTION_X] - 1.0F);
		final float sy = (sampleY + y) / (this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_RESOLUTION_Y] - 1.0F);
		final float sx0 = 2.0F * sx - 1.0F;
		final float sy0 = 2.0F * sy - 1.0F;
		
//		Initialize w to 1.0F:
		float w = 1.0F;
		
//		Retrieve whether or not this camera uses a Fisheye camera lens:
		final boolean isFisheyeCameraLens = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_CAMERA_LENS] == Camera.CAMERA_LENS_FISHEYE;
		
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
		final float focalDistance = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FOCAL_DISTANCE];
		
//		Calculate the point on the image plane:
		final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;
		final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;
		final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;
		
//		Retrieve the aperture radius:
		final float apertureRadius = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_APERTURE_RADIUS];
		
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
		
//		Update the rays_$private$6 array with information:
		this.rays_$private$6[0] = aperturePointX;
		this.rays_$private$6[1] = aperturePointY;
		this.rays_$private$6[2] = aperturePointZ;
		this.rays_$private$6[3] = apertureToImagePlane1X;
		this.rays_$private$6[4] = apertureToImagePlane1Y;
		this.rays_$private$6[5] = apertureToImagePlane1Z;
		
		return true;
	}
	
	private float doGetY(final float x, final float z) {
		return simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), x, z);
	}
	
	private float doIntersectPrimitives(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final boolean isTesting) {
//		Compute the offset for the array containing intersection data:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the distance to the closest primitive to INFINITY:
		float closestDistance = INFINITY;
		
//		Initialize the offset to the closest primitive, the shape type of the closest primitive and the shape offset of the closest primitive to -1:
		int closestPrimitiveOffset = -1;
		int closestShapeType = -1;
		int closestShapeOffset = -1;
		
//		Calculate the reciprocal of the ray direction vector:
		final float directionReciprocalX = 1.0F / directionX;
		final float directionReciprocalY = 1.0F / directionY;
		final float directionReciprocalZ = 1.0F / directionZ;
		
		for(int i = 0; i < this.scenePrimitivesCount; i++) {
			float currentDistance = INFINITY;
			
			final int currentPrimitiveOffset = i * Primitive.SIZE;
			
			int currentShapeType = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_TYPE];
			int currentShapeOffset = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_OFFSET];
			
			if(currentShapeType == TriangleMesh.TYPE) {
//				Initialize the offset to the root of the BVH structure:
				int boundingVolumeHierarchyAbsoluteOffset = currentShapeOffset;
				int boundingVolumeHierarchyRelativeOffset = 0;
				
//				Loop through the BVH structure as long as the offset to the next node is not -1:
				while(boundingVolumeHierarchyRelativeOffset != -1) {
//					Calculate the current offset in the BVH structure:
					final int boundingVolumeHierarchyOffset = boundingVolumeHierarchyAbsoluteOffset + boundingVolumeHierarchyRelativeOffset;
					
//					Retrieve the offsets to the points defining the minimum and maximum locations of the current bounding box:
					final int minimumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 2];
					final int maximumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 3];
					
//					Retrieve the minimum point location of the current bounding box:
					final float minimumX = this.scenePoint3Fs_$constant$[minimumOffset + 0];
					final float minimumY = this.scenePoint3Fs_$constant$[minimumOffset + 1];
					final float minimumZ = this.scenePoint3Fs_$constant$[minimumOffset + 2];
					
//					Retrieve the maximum point location of the current bounding box:
					final float maximumX = this.scenePoint3Fs_$constant$[maximumOffset + 0];
					final float maximumY = this.scenePoint3Fs_$constant$[maximumOffset + 1];
					final float maximumZ = this.scenePoint3Fs_$constant$[maximumOffset + 2];
					
//					Calculate the distance to the minimum point location of the bounding box:
					final float t0X = (minimumX - originX) * directionReciprocalX;
					final float t0Y = (minimumY - originY) * directionReciprocalY;
					final float t0Z = (minimumZ - originZ) * directionReciprocalZ;
					
//					Calculate the distance to the maximum point location of the bounding box:
					final float t1X = (maximumX - originX) * directionReciprocalX;
					final float t1Y = (maximumY - originY) * directionReciprocalY;
					final float t1Z = (maximumZ - originZ) * directionReciprocalZ;
					
//					Calculate the minimum and maximum X-components:
					final float tMaximumX = max(t0X, t1X);
					final float tMinimumX = min(t0X, t1X);
					
//					Calculate the minimum and maximum Y-components:
					final float tMaximumY = max(t0Y, t1Y);
					final float tMinimumY = min(t0Y, t1Y);
					
//					Calculate the minimum and maximum Z-components:
					final float tMaximumZ = max(t0Z, t1Z);
					final float tMinimumZ = min(t0Z, t1Z);
					
//					Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:
					final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));
					final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));
					
//					Check if the maximum distance is greater than or equal to the minimum distance:
					if(tMaximum < 0.0F || tMinimum > tMaximum || closestDistance < tMinimum) {
//						Retrieve the offset to the next node in the BVH structure, relative to the current one:
						boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
					} else {
//						Retrieve the type of the current BVH node:
						final int type = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset];
						
						if(type == BoundingVolumeHierarchy.NODE_TYPE_TREE) {
//							This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
						} else {
//							Retrieve the triangle count in the current BVH node:
							final int triangleCount = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
							
							int j = 0;
							
//							Loop through all triangles in the current BVH node:
							while(j < triangleCount) {
//								Retrieve the offset to the current triangle:
								final int currentTriangleOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 5 + j];
								
								final int offsetAPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
								final int offsetBPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
								final int offsetCPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
								
								final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
								final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
								final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
								final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
								final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
								final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
								final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
								final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
								final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
								
//								Perform an intersection test with the current triangle:
								currentDistance = doIntersectTriangle(originX, originY, originZ, directionX, directionY, directionZ, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ);
								
//								Check if the current distance is less than the distance to the closest primitive so far:
								if(currentDistance < closestDistance) {
									closestDistance = currentDistance;
									closestPrimitiveOffset = currentPrimitiveOffset;
									closestShapeType = Triangle.TYPE;
									closestShapeOffset = currentTriangleOffset;
								}
								
								if(isTesting && closestPrimitiveOffset != -1) {
									return closestDistance;
								}
								
								j++;
							}
							
//							Retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
						}
					}
				}
			} else if(currentShapeType == Plane.TYPE) {
				final int offsetA = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
				final int offsetSurfaceNormal = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
				
				final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
				final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
				final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
				
				final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
				final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
				final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
				
				currentDistance = doIntersectPlane(originX, originY, originZ, directionX, directionY, directionZ, aX, aY, aZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ);
			} else if(currentShapeType == Sphere.TYPE) {
				final int offsetPosition = (int)(this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
				
				final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
				final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
				final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
				
				final float radius = this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_RADIUS];
				
				currentDistance = doIntersectSphere(originX, originY, originZ, directionX, directionY, directionZ, positionX, positionY, positionZ, radius);
			} else if(currentShapeType == Terrain.TYPE) {
				final float frequency = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
				final float gain = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
				final float minimum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
				final float maximum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
				
				final int octaves = (int)(this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
				
				currentDistance = doIntersectTerrain(originX, originY, originZ, directionX, directionY, directionZ, frequency, gain, minimum, maximum, octaves);
			} else if(currentShapeType == Triangle.TYPE) {
				final int offsetAPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
				final int offsetBPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
				final int offsetCPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
				
				final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
				final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
				final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
				final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
				final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
				final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
				final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
				final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
				final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
				
				currentDistance = doIntersectTriangle(originX, originY, originZ, directionX, directionY, directionZ, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ);
			}
			
			if(currentDistance < closestDistance) {
				closestDistance = currentDistance;
				closestPrimitiveOffset = currentPrimitiveOffset;
				closestShapeType = currentShapeType;
				closestShapeOffset = currentShapeOffset;
			}
			
			if(isTesting && closestPrimitiveOffset != -1) {
				return closestDistance;
			}
		}
		
		if(!isTesting) {
			if(closestPrimitiveOffset != -1) {
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = closestDistance;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = closestPrimitiveOffset;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = closestShapeType;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = closestShapeOffset;
				
				if(closestShapeType == Plane.TYPE) {
					final int offsetA = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
					final int offsetB = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_B_OFFSET];
					final int offsetC = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_C_OFFSET];
					final int offsetSurfaceNormal = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
					
					final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
					final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
					final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
					final float bX = this.scenePoint3Fs_$constant$[offsetB + 0];
					final float bY = this.scenePoint3Fs_$constant$[offsetB + 1];
					final float bZ = this.scenePoint3Fs_$constant$[offsetB + 2];
					final float cX = this.scenePoint3Fs_$constant$[offsetC + 0];
					final float cY = this.scenePoint3Fs_$constant$[offsetC + 1];
					final float cZ = this.scenePoint3Fs_$constant$[offsetC + 2];
					
					final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
					final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
					final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
					
					doCalculateSurfacePropertiesForPlane(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, aX, aY, aZ, bX, bY, bZ, cX, cY, cZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ);
				} else if(closestShapeType == Sphere.TYPE) {
					final int offsetPosition = (int)(this.sceneSpheres_$constant$[closestShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
					
					final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
					final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
					final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
					
					doCalculateSurfacePropertiesForSphere(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, positionX, positionY, positionZ);
				} else if(closestShapeType == Terrain.TYPE) {
					final float frequency = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
					final float gain = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
					final float minimum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
					final float maximum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
					
					final int octaves = (int)(this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
					
					doCalculateSurfacePropertiesForTerrain(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, frequency, gain, minimum, maximum, octaves);
				} else if(closestShapeType == Triangle.TYPE) {
					final int offsetAPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
					final int offsetBPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
					final int offsetCPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
					
					final int offsetASurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_SURFACE_NORMAL_OFFSET];
					final int offsetBSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_SURFACE_NORMAL_OFFSET];
					final int offsetCSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_SURFACE_NORMAL_OFFSET];
					
					final int offsetATextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_TEXTURE_COORDINATES_OFFSET];
					final int offsetBTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_TEXTURE_COORDINATES_OFFSET];
					final int offsetCTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_TEXTURE_COORDINATES_OFFSET];
					
					final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
					final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
					final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
					final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
					final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
					final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
					final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
					final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
					final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
					
					final float aSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 0];
					final float aSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 1];
					final float aSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 2];
					final float bSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 0];
					final float bSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 1];
					final float bSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 2];
					final float cSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 0];
					final float cSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 1];
					final float cSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 2];
					
					final float aTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 0];
					final float aTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 1];
					final float bTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 0];
					final float bTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 1];
					final float cTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 0];
					final float cTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 1];
					
					doCalculateSurfacePropertiesForTriangle(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, aSurfaceNormalX, aSurfaceNormalY, aSurfaceNormalZ, bSurfaceNormalX, bSurfaceNormalY, bSurfaceNormalZ, cSurfaceNormalX, cSurfaceNormalY, cSurfaceNormalZ, aTextureCoordinatesU, aTextureCoordinatesV, bTextureCoordinatesU, bTextureCoordinatesV, cTextureCoordinatesU, cTextureCoordinatesV);
				}
				
//				Perform Normal Mapping via Image Texture:
				doPerformNormalMappingViaImageTexture(closestPrimitiveOffset);
				
//				Perform Normal Mapping via Noise:
				doPerformNormalMappingViaNoise(closestPrimitiveOffset);
			} else {
//				Reset the information in the intersections array:
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = -1;
			}
		}
		
		return closestDistance;
	}
	
	@SuppressWarnings("static-method")
	private float doIntersectPlane(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float aX, final float aY, final float aZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ) {
//		Calculate the dot product between the surface normal and the ray direction:
		final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
		
//		Check that the dot product is not 0.0:
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
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
	
	private float doIntersectSphere(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float positionX, final float positionY, final float positionZ, final float radius) {
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
	
	private float doIntersectTerrain(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		float t = 0.0F;
		
		final float tMinimum = 0.001F;
		final float tMaximum = 2000.0F;
		final float tMultiplier = 0.1F;
		
		float tDelta = tMultiplier;
		
		for(float tCurrent = tMinimum; tCurrent < tMaximum; tCurrent += tDelta) {
			final float surfaceIntersectionPointX = originX + directionX * tCurrent;
			final float surfaceIntersectionPointY = originY + directionY * tCurrent;
			final float surfaceIntersectionPointZ = originZ + directionZ * tCurrent;
			
			final float y = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ);
			
			if(surfaceIntersectionPointY < y) {
				t = tCurrent;
				
				tCurrent = tMaximum;
			}
			
			tDelta = tMultiplier * tCurrent;
		}
		
		t = t > EPSILON ? t : INFINITY;
		
		return t;
	}
	
	@SuppressWarnings("static-method")
	private float doIntersectTriangle(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float aX, final float aY, final float aZ, final float bX, final float bY, final float bZ, final float cX, final float cY, final float cZ) {
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
		
//		Check that the determinant is anything other than in the range of negative epsilon and positive epsilon:
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
	
	private int doGetTextureColor(final int texturesOffset) {
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == BlendTexture.TYPE) {
			return doGetTextureColorFromBlendTexture(texturesOffset);
		} else if(textureType == CheckerboardTexture.TYPE) {
			return doGetTextureColorFromCheckerboardTexture(texturesOffset);
		} else if(textureType == ConstantTexture.TYPE) {
			return doGetTextureColorFromConstantTexture(texturesOffset);
		} else if(textureType == FractionalBrownianMotionTexture.TYPE) {
			return doGetTextureColorFromFractionalBrownianMotionTexture(texturesOffset);
		} else if(textureType == ImageTexture.TYPE) {
//			return doGetTextureColorFromImageTexture(texturesOffset);
			return doGetTextureColorFromImageTextureBilinearInterpolation(texturesOffset);
		} else if(textureType == SurfaceNormalTexture.TYPE) {
			return doGetTextureColorFromSurfaceNormalTexture();
		} else {
			return 0;
		}
	}
	
	private int doGetTextureColor2(final int texturesOffset) {
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == CheckerboardTexture.TYPE) {
			return doGetTextureColorFromCheckerboardTexture(texturesOffset);
		} else if(textureType == ConstantTexture.TYPE) {
			return doGetTextureColorFromConstantTexture(texturesOffset);
		} else if(textureType == FractionalBrownianMotionTexture.TYPE) {
			return doGetTextureColorFromFractionalBrownianMotionTexture(texturesOffset);
		} else if(textureType == ImageTexture.TYPE) {
//			return doGetTextureColorFromImageTexture(texturesOffset);
			return doGetTextureColorFromImageTextureBilinearInterpolation(texturesOffset);
		} else if(textureType == SurfaceNormalTexture.TYPE) {
			return doGetTextureColorFromSurfaceNormalTexture();
		} else {
			return 0;
		}
	}
	
	private int doGetTextureColorFromBlendTexture(final int texturesOffset) {
		final int textureAOffset = (int)(this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_TEXTURE_A_OFFSET]);
		final int textureBOffset = (int)(this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_TEXTURE_B_OFFSET]);
		
		final float factor = this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_FACTOR];
		
		final int textureAColorRGB = doGetTextureColor2(textureAOffset);
		final int textureBColorRGB = doGetTextureColor2(textureBOffset);
		
		final float textureAColorR = ((textureAColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float textureAColorG = ((textureAColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float textureAColorB = ((textureAColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float textureBColorR = ((textureBColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float textureBColorG = ((textureBColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float textureBColorB = ((textureBColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final int colorR = (int)(saturate((1.0F - factor) * textureAColorR + factor * textureBColorR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorG = (int)(saturate((1.0F - factor) * textureAColorG + factor * textureBColorG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorB = (int)(saturate((1.0F - factor) * textureAColorB + factor * textureBColorB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int colorRGB = ((colorR & 0xFF) << 16) | ((colorG & 0xFF) << 8) | (colorB & 0xFF);
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromCheckerboardTexture(final int texturesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		final int offsetColor0 = texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_0;
		final int offsetColor1 = texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_1;
		
//		TODO: Write explanation!
		final float u = this.intersections_$local$[offsetUVCoordinates];
		final float v = this.intersections_$local$[offsetUVCoordinates + 1];
		
//		TODO: Write explanation!
		final int color0RGB = (int)(this.sceneTextures_$constant$[offsetColor0]);
		final int color1RGB = (int)(this.sceneTextures_$constant$[offsetColor1]);
		
		final float color0R = ((color0RGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float color0G = ((color0RGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float color0B = ((color0RGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float color1R = ((color1RGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float color1G = ((color1RGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float color1B = ((color1RGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
//		TODO: Write explanation!
		final float sU = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_U];
		final float sV = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
//		TODO: Write explanation!
		final float textureU = modulo((u * cosAngle - v * sinAngle) * sU);
		final float textureV = modulo((v * cosAngle + u * sinAngle) * sV);
		
//		TODO: Write explanation!
		final boolean isDarkU = textureU > 0.5F;
		final boolean isDarkV = textureV > 0.5F;
		final boolean isDark = isDarkU ^ isDarkV;
		
//		TODO: Write explanation!
//		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
		if(color0R == color1R && color0G == color1G && color0B == color1B) {
//			TODO: Write explanation!
			final float textureMultiplier = isDark ? 0.8F : 1.2F;
			
//			TODO: Write explanation!
			final int r = (int)(saturate(color0R * textureMultiplier, 0.0F, 1.0F) * 255.0F + 0.5F);
			final int g = (int)(saturate(color0G * textureMultiplier, 0.0F, 1.0F) * 255.0F + 0.5F);
			final int b = (int)(saturate(color0B * textureMultiplier, 0.0F, 1.0F) * 255.0F + 0.5F);
			
			final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
			
			return rGB;
		}
		
//		TODO: Write explanation!
		final int r = (int)(saturate(isDark ? color0R : color1R, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(isDark ? color0G : color1G, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(isDark ? color0B : color1B, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromConstantTexture(final int texturesOffset) {
//		Retrieve the R-, G- and B-component values of the texture:
		final int colorRGB = (int)(this.sceneTextures_$constant$[texturesOffset + ConstantTexture.RELATIVE_OFFSET_COLOR]);
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromFractionalBrownianMotionTexture(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
//		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;
		final int offsetAddend = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_ADDEND;
		final int offsetMultiplier = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_MULTIPLIER;
		final int offsetFrequency = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_FREQUENCY;
		final int offsetGain = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_GAIN;
		final int offsetOctaves = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_OCTAVES;
		
		final float x = this.intersections_$local$[offsetSurfaceIntersectionPoint];
		final float y = this.intersections_$local$[offsetSurfaceIntersectionPoint + 1];
		final float z = this.intersections_$local$[offsetSurfaceIntersectionPoint + 2];
//		final float u = this.intersections_$local$[offsetUVCoordinates];
//		final float v = this.intersections_$local$[offsetUVCoordinates + 1];
		
		final int addendRGB = (int)(this.sceneTextures_$constant$[offsetAddend]);
		final int multiplierRGB = (int)(this.sceneTextures_$constant$[offsetMultiplier]);
		
		final float addendR = ((addendRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float addendG = ((addendRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float addendB = ((addendRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float multiplierR = ((multiplierRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float multiplierG = ((multiplierRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float multiplierB = ((multiplierRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float frequency = this.sceneTextures_$constant$[offsetFrequency];
		final float gain = this.sceneTextures_$constant$[offsetGain];
		
		final int octaves = (int)(this.sceneTextures_$constant$[offsetOctaves]);
		
		final float noise = simplexFractionalBrownianMotionXYZ(frequency, gain, 0.0F, 1.0F, octaves, x, y, z);
//		final float noise = simplexFractionalBrownianMotionXY(frequency, gain, 0.0F, 1.0F, octaves, u, v);
		
		final int r = (int)(saturate(noise * multiplierR + addendR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(noise * multiplierG + addendG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(noise * multiplierB + addendB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromImageTexture(final int texturesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		TODO: Write explanation!
		final int offsetTextureCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		TODO: Write explanation!
		final float u = this.intersections_$local$[offsetTextureCoordinates + 0];
		final float v = this.intersections_$local$[offsetTextureCoordinates + 1];
		
//		TODO: Write explanation!
		final float width = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_WIDTH];
		final float height = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_HEIGHT];
		
//		TODO: Write explanation!
		final float scaleU = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_V];
		
//		TODO: Write explanation!
		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
//		TODO: Write explanation!
		final float x = remainder(abs((int)((u * cosAngle - v * sinAngle) * (width * scaleU))), width);
		final float y = remainder(abs((int)((v * cosAngle + u * sinAngle) * (height * scaleV))), height);
		
//		TODO: Write explanation!
		final int index = (int)((y * width + x));
		
		final int rGB = (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index]);
		
		return rGB;
	}
	
	private int doGetTextureColorFromImageTextureBilinearInterpolation(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
		final float u = this.intersections_$local$[offsetUVCoordinates];
		final float v = this.intersections_$local$[offsetUVCoordinates + 1];
		final float width = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_WIDTH];
		final float height = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_HEIGHT];
		final float scaleU = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_V];
//		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_COS];
//		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
//		float x = remainder(abs((int)((u * cosAngle - v * sinAngle) * (width * scaleU))), width);
//		float y = remainder(abs((int)((v * cosAngle + u * sinAngle) * (height * scaleV))), height);
		float x = u * scaleU;
		float y = v * scaleV;
		
		x = x - (int)(x);
		y = y - (int)(y);
		
		if(x < 0.0F) {
			x++;
		}
		
		if(y < 0.0F) {
			y++;
		}
		
		final float dx = x * (width - 1.0F);
		final float dy = y * (height - 1.0F);
		
		final int ix0 = (int)(dx);
		final int iy0 = (int)(dy);
		final int ix1 = (int)(remainder(ix0 + 1, width));
		final int iy1 = (int)(remainder(iy0 + 1, height));
		
		float u0 = dx - ix0;
		float v0 = dy - iy0;
		
		u0 = u0 * u0 * (3.0F - (2.0F * u0));
		v0 = v0 * v0 * (3.0F - (2.0F * v0));
		
		final float k00 = (1.0F - u0) * (1.0F - v0);
		final float k01 = (1.0F - u0) * v0;
		final float k10 = u0 * (1.0F - v0);
		final float k11 = u0 * v0;
		
		final int index00 = iy0 * (int)(width) + ix0;
		final int index01 = iy1 * (int)(width) + ix0;
		final int index10 = iy0 * (int)(width) + ix1;
		final int index11 = iy1 * (int)(width) + ix1;
		
		final int rGB00 = (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index00]);
		final int rGB01 = (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index01]);
		final int rGB10 = (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index10]);
		final int rGB11 = (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index11]);
		
		final float r00 = ((rGB00 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float g00 = ((rGB00 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float b00 = ((rGB00 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float r01 = ((rGB01 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float g01 = ((rGB01 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float b01 = ((rGB01 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float r10 = ((rGB10 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float g10 = ((rGB10 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float b10 = ((rGB10 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float r11 = ((rGB11 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float g11 = ((rGB11 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float b11 = ((rGB11 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final int r = (int)(saturate(r00 * k00 + r01 * k01 + r10 * k10 + r11 * k11, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(g00 * k00 + g01 * k01 + g10 * k10 + g11 * k11, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(b00 * k00 + b01 * k01 + b10 * k10 + b11 * k11, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromSurfaceNormalTexture() {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final float surfaceNormalShadingX = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING];
		final float surfaceNormalShadingY = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING + 2];
		
		final int r = (int)(saturate((surfaceNormalShadingX + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate((surfaceNormalShadingY + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate((surfaceNormalShadingZ + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doShaderPhongReflectionModel0(final boolean isCheckingForIntersections, final float pX, final float pY, final float pZ, final float nX, final float nY, final float nZ, final float vX, final float vY, final float vZ, final float albedoR, final float albedoG, final float albedoB, final float kaR, final float kaG, final float kaB, final float kdR, final float kdG, final float kdB, final float ksR, final float ksG, final float ksB, final float ns) {
//		Initialize the color:
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		
//		Retrieve Ia, the ambient intensity:
		final float ia = 1.0F;
		
//		Compute and add the ambient light to the current color:
		r += albedoR * kaR * ia;
		g += albedoG * kaG * ia;
		b += albedoB * kaB * ia;
		
		final float lPositionX = this.sunAndSkySunOriginX;
		final float lPositionY = this.sunAndSkySunOriginY;
		final float lPositionZ = this.sunAndSkySunOriginZ;
		
//		Compute L, the normalized direction vector from the intersection point to the point light:
		final float lX = lPositionX - pX;
		final float lY = lPositionY - pY;
		final float lZ = lPositionZ - pZ;
		final float lLengthSquared = lX * lX + lY * lY + lZ * lZ;
		final float lLength = sqrt(lLengthSquared);
		final float lLengthReciprocal = 1.0F / lLength;
		final float lNormalizedX = lX * lLengthReciprocal;
		final float lNormalizedY = lY * lLengthReciprocal;
		final float lNormalizedZ = lZ * lLengthReciprocal;
		
		if(isCheckingForIntersections) {
//			Perform an intersection test:
			doIntersectPrimitives(pX, pY, pZ, lNormalizedX, lNormalizedY, lNormalizedZ, false);
		}
		
//		Compute t, the closest intersection from the intersection point in the direction of L:
		final float t = isCheckingForIntersections ? this.intersections_$local$[getLocalId() * SIZE_INTERSECTION + RELATIVE_OFFSET_INTERSECTION_DISTANCE] : INFINITY;
		
		if(t >= INFINITY || t > lLengthSquared) {
//			Compute the dot product between L and N:
			final float lDotN = lNormalizedX * nX + lNormalizedY * nY + lNormalizedZ * nZ;
			
//			Retrieve Id, the diffuse intensity:
			final float id = 1.0F;
			
//			Compute and add the diffuse light to the current color:
			r += albedoR * kdR * max(lDotN, 0.0F) * id;
			g += albedoG * kdG * max(lDotN, 0.0F) * id;
			b += albedoB * kdB * max(lDotN, 0.0F) * id;
			
			if(lDotN > 0.0F) {
				final float rX = 2.0F * lDotN * nX - lNormalizedX;
				final float rY = 2.0F * lDotN * nY - lNormalizedY;
				final float rZ = 2.0F * lDotN * nZ - lNormalizedZ;
				final float rLengthReciprocal = rsqrt(rX * rX + rY * rY + rZ * rZ);
				final float rNormalizedX = rX * rLengthReciprocal;
				final float rNormalizedY = rY * rLengthReciprocal;
				final float rNormalizedZ = rZ * rLengthReciprocal;
				
				final float rDotV = rNormalizedX * vX + rNormalizedY * vY + rNormalizedZ * vZ;
				
				final float shininess = pow(max(rDotV, 0.0F), ns);
				
//				Retrieve Is, the specular intensity:
				final float is = 1.0F;
				
//				Compute and add the specular light to the color:
				r += ksR * shininess * is;
				g += ksG * shininess * is;
				b += ksB * shininess * is;
			}
		}
		
		r = saturate(r, 0.0F, 1.0F);
		g = saturate(g, 0.0F, 1.0F);
		b = saturate(b, 0.0F, 1.0F);
		
		return (((int)(r * 255.0F + 0.5F) & 0xFF) << 16) | (((int)(g * 255.0F + 0.5F) & 0xFF) << 8) | (((int)(b * 255.0F + 0.5F) & 0xFF));
	}
	
	@SuppressWarnings("unused")
	private int doShaderPhongReflectionModel1(final boolean isCheckingForIntersections, final float surfaceIntersectionPointX, final float surfaceIntersectionPointY, final float surfaceIntersectionPointZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ, final float rayDirectionX, final float rayDirectionY, final float rayDirectionZ, final float albedoR, final float albedoG, final float albedoB) {
		float r = albedoR * 0.5F;
		float g = albedoG * 0.5F;
		float b = albedoB * 0.5F;
		
		final float woX = -rayDirectionX;
		final float woY = -rayDirectionY;
		final float woZ = -rayDirectionZ;
		final float woLengthReciprocal = rsqrt(woX * woX + woY * woY + woZ * woZ);
		final float woNormalizedX = woX * woLengthReciprocal;
		final float woNormalizedY = woY * woLengthReciprocal;
		final float woNormalizedZ = woZ * woLengthReciprocal;
		
		final float lPositionX = this.sunAndSkySunOriginX;
		final float lPositionY = this.sunAndSkySunOriginY;
		final float lPositionZ = this.sunAndSkySunOriginZ;
		
//		Compute L, the normalized direction vector from the intersection point to the point light:
		final float lX = lPositionX - surfaceIntersectionPointX;
		final float lY = lPositionY - surfaceIntersectionPointY;
		final float lZ = lPositionZ - surfaceIntersectionPointZ;
		final float lLengthSquared = lX * lX + lY * lY + lZ * lZ;
		final float lLength = sqrt(lLengthSquared);
		final float lLengthReciprocal = 1.0F / lLength;
		final float lNormalizedX = lX * lLengthReciprocal;
		final float lNormalizedY = lY * lLengthReciprocal;
		final float lNormalizedZ = lZ * lLengthReciprocal;
		
		if(isCheckingForIntersections) {
//			Perform an intersection test:
			doIntersectPrimitives(surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, lNormalizedX, lNormalizedY, lNormalizedZ, false);
		}
		
//		Compute t, the closest intersection from the intersection point in the direction of L:
		final float t = isCheckingForIntersections ? this.intersections_$local$[getLocalId() * SIZE_INTERSECTION + RELATIVE_OFFSET_INTERSECTION_DISTANCE] : INFINITY;
		
		if(t >= INFINITY || t > lLengthSquared) {
			final float wiX = lPositionX - surfaceIntersectionPointX;//surfaceNormalX;
			final float wiY = lPositionY - surfaceIntersectionPointY;//surfaceNormalY;
			final float wiZ = lPositionZ - surfaceIntersectionPointZ;//surfaceNormalZ;
			final float wiLengthReciprocal = rsqrt(wiX * wiX + wiY * wiY + wiZ * wiZ);
			final float wiNormalizedX = wiX * wiLengthReciprocal;
			final float wiNormalizedY = wiY * wiLengthReciprocal;
			final float wiNormalizedZ = wiZ * wiLengthReciprocal;
			
			final float surfaceNormalDotWi = surfaceNormalX * wiNormalizedX + surfaceNormalY * wiNormalizedY + surfaceNormalZ * wiNormalizedZ;
			
			if(surfaceNormalDotWi > 0.0F) {
				final float reflectionX = -wiNormalizedX + (2.0F * surfaceNormalX * surfaceNormalDotWi);
				final float reflectionY = -wiNormalizedY + (2.0F * surfaceNormalY * surfaceNormalDotWi);
				final float reflectionZ = -wiNormalizedZ + (2.0F * surfaceNormalZ * surfaceNormalDotWi);
				
				final float reflectionDotWo = reflectionX * woNormalizedX + reflectionY * woNormalizedY + reflectionZ * woNormalizedZ;
				
				final float diffuseIntensity = 1.0F;
				final float diffuseColorR = albedoR * diffuseIntensity * PI_RECIPROCAL;
				final float diffuseColorG = albedoG * diffuseIntensity * PI_RECIPROCAL;
				final float diffuseColorB = albedoB * diffuseIntensity * PI_RECIPROCAL;
				
				if(reflectionDotWo > 0.0F) {
					final float specularIntensity = 1.0F;
					final float specularPower = 50.0F;
					final float specularComponent = pow(reflectionDotWo, specularPower) * specularIntensity;
					final float specularColorR = 1.0F * specularComponent;
					final float specularColorG = 1.0F * specularComponent;
					final float specularColorB = 1.0F * specularComponent;
					
					r += (diffuseColorR + specularColorR) * surfaceNormalDotWi;
					g += (diffuseColorG + specularColorG) * surfaceNormalDotWi;
					b += (diffuseColorB + specularColorB) * surfaceNormalDotWi;
				} else {
					r += diffuseColorR * surfaceNormalDotWi;
					g += diffuseColorG * surfaceNormalDotWi;
					b += diffuseColorB * surfaceNormalDotWi;
				}
			}
		}
		
		r = saturate(r, 0.0F, 1.0F);
		g = saturate(g, 0.0F, 1.0F);
		b = saturate(b, 0.0F, 1.0F);
		
		return (((int)(r * 255.0F + 0.5F) & 0xFF) << 16) | (((int)(g * 255.0F + 0.5F) & 0xFF) << 8) | (((int)(b * 255.0F + 0.5F) & 0xFF));
	}
	
	private void doAmbientOcclusion(final float brightR, final float brightG, final float brightB, final float darkR, final float darkG, final float darkB) {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
//		this.shapeOffsetsForPrimaryRay[getGlobalId()] = shapesOffset;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance != INFINITY && primitivesOffset != -1) {
//			Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point from the intersections array:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal from the intersections array:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float w0X = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
			final float w0Y = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
			final float w0Z = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
			
//			Calculate the orthonormal basis W vector:
			final float w0LengthReciprocal = rsqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
			final float w1X = w0X * w0LengthReciprocal;
			final float w1Y = w0Y * w0LengthReciprocal;
			final float w1Z = w0Z * w0LengthReciprocal;
			
//			Check if the direction is the Y-direction:
			final boolean isY = abs(w1X) > 0.1F;
			
//			Calculate the orthonormal basis U vector:
			final float u0X = isY ? 0.0F : 1.0F;
			final float u0Y = isY ? 1.0F : 0.0F;
			final float u1X = u0Y * w1Z;
			final float u1Y = -(u0X * w1Z);
			final float u1Z = u0X * w1Y - u0Y * w1X;
			final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
			final float u2X = u1X * u1LengthReciprocal;
			final float u2Y = u1Y * u1LengthReciprocal;
			final float u2Z = u1Z * u1LengthReciprocal;
			
//			Calculate the orthonormal basis V vector:
			final float v0X = w1Y * u2Z - w1Z * u2Y;
			final float v0Y = w1Z * u2X - w1X * u2Z;
			final float v0Z = w1X * u2Y - w1Y * u2X;
			
			final float xi = nextFloat();
			final float xj = nextFloat();
			final float phi = PI_MULTIPLIED_BY_TWO * xi;
			final float cosPhi = cos(phi);
			final float sinPhi = sin(phi);
			final float sinTheta = sqrt(xj);
			final float cosTheta = sqrt(1.0F - xj);
			
			final float direction0X = cosPhi * sinTheta;
			final float direction0Y = sinPhi * sinTheta;
			final float direction0Z = cosTheta;
			final float direction1X = direction0X * u2X + direction0Y * v0X + direction0Z * w1X;
			final float direction1Y = direction0X * u2Y + direction0Y * v0Y + direction0Z * w1Y;
			final float direction1Z = direction0X * u2Z + direction0Y * v0Z + direction0Z * w1Z;
			final float direction1LengthReciprocal = rsqrt(direction1X * direction1X + direction1Y * direction1Y + direction1Z * direction1Z);
			
			originX = surfaceIntersectionPointX + w1X * 0.01F;
			originY = surfaceIntersectionPointY + w1Y * 0.01F;
			originZ = surfaceIntersectionPointZ + w1Z * 0.01F;
			
			directionX = direction1X * direction1LengthReciprocal;
			directionY = direction1Y * direction1LengthReciprocal;
			directionZ = direction1Z * direction1LengthReciprocal;
			
			final float t = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
			final boolean isHit = t < this.rendererAOMaximumDistance;
			
			final float r = isHit ? brightR : darkR;
			final float g = isHit ? brightG : darkG;
			final float b = isHit ? brightB : darkB;
			
			pixelColorR += (1.0F - r) * brightR + r * darkR;
			pixelColorG += (1.0F - g) * brightG + g * darkG;
			pixelColorB += (1.0F - b) * brightB + b * darkB;
		}
		
//		Update the current pixel color:
		this.colorCurrentSamples_$local$[pixelIndex + 0] = pixelColorR;
		this.colorCurrentSamples_$local$[pixelIndex + 1] = pixelColorG;
		this.colorCurrentSamples_$local$[pixelIndex + 2] = pixelColorB;
	}
	
	private void doCalculateColor(final int pixelIndex) {
//		Retrieve the offset to the pixels array:
		final int pixelsOffset = pixelIndex * SIZE_PIXEL;
		
//		Calculate the pixel index:
		final int pixelIndex0 = pixelIndex * SIZE_COLOR_RGB;
		final int pixelIndex1 = getLocalId() * SIZE_COLOR_RGB;
		
//		Perform the moving average algorithm to calculate the average pixel color:
		final long oldSubSample = this.subSamples[pixelIndex];
		final long newSubSample = oldSubSample + 1L;
		
		final float currentPixelR = this.colorCurrentSamples_$local$[pixelIndex1 + 0];
		final float currentPixelG = this.colorCurrentSamples_$local$[pixelIndex1 + 1];
		final float currentPixelB = this.colorCurrentSamples_$local$[pixelIndex1 + 2];
		
		if(currentPixelR >= 0.0F && currentPixelG >= 0.0F && currentPixelB >= 0.0F) {
			final float oldAverageR = this.colorAverageSamples[pixelIndex0 + 0];
			final float oldAverageG = this.colorAverageSamples[pixelIndex0 + 1];
			final float oldAverageB = this.colorAverageSamples[pixelIndex0 + 2];
			
			final float newAverageR = oldAverageR + ((currentPixelR - oldAverageR) / newSubSample);
			final float newAverageG = oldAverageG + ((currentPixelG - oldAverageG) / newSubSample);
			final float newAverageB = oldAverageB + ((currentPixelB - oldAverageB) / newSubSample);
			
			this.subSamples[pixelIndex] = newSubSample;
			this.colorAverageSamples[pixelIndex0 + 0] = newAverageR;
			this.colorAverageSamples[pixelIndex0 + 1] = newAverageG;
			this.colorAverageSamples[pixelIndex0 + 2] = newAverageB;
		}
		
//		Retrieve the 'normalized' accumulated pixel color component values again:
		float r = this.colorAverageSamples[pixelIndex0];
		float g = this.colorAverageSamples[pixelIndex0 + 1];
		float b = this.colorAverageSamples[pixelIndex0 + 2];
		
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
		} else if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_1) {
//			Calculate the maximum pixel color component values:
			final float rMaximum = max(r - 0.004F, 0.0F);
			final float gMaximum = max(g - 0.004F, 0.0F);
			final float bMaximum = max(b - 0.004F, 0.0F);
			
//			Perform Tone Mapping and Gamma Correction:
			r = (rMaximum * (6.2F * rMaximum + 0.5F)) / (rMaximum * (6.2F * rMaximum + 1.7F) + 0.06F);
			g = (gMaximum * (6.2F * gMaximum + 0.5F)) / (gMaximum * (6.2F * gMaximum + 1.7F) + 0.06F);
			b = (bMaximum * (6.2F * bMaximum + 0.5F)) / (bMaximum * (6.2F * bMaximum + 1.7F) + 0.06F);
		} else if(this.toneMappingAndGammaCorrection == TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_2) {
			final float exposure = 0.5F;
			
			r *= exposure;
			g *= exposure;
			b *= exposure;
			
//			Perform Tone Mapping:
			r = saturate((r * (2.51F * r + 0.03F)) / (r * (2.43F * r + 0.59F) + 0.14F), 0.0F, 1.0F);
			g = saturate((g * (2.51F * g + 0.03F)) / (g * (2.43F * g + 0.59F) + 0.14F), 0.0F, 1.0F);
			b = saturate((b * (2.51F * b + 0.03F)) / (b * (2.43F * b + 0.59F) + 0.14F), 0.0F, 1.0F);
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
		
		final int primitiveOffsetFromPrimaryRay = this.primitiveOffsetsForPrimaryRay[pixelIndex];
		
		if(primitiveOffsetFromPrimaryRay > -1 && primitiveOffsetFromPrimaryRay == this.selectedPrimitiveOffset) {
			g += 1.0F;
		}
		
		if(this.toneMappingAndGammaCorrection != TONE_MAPPING_AND_GAMMA_CORRECTION_FILMIC_CURVE_1) {
			final float breakPoint = this.colorSpaceBreakPoint;
			final float gamma = this.colorSpaceGamma;
			final float gammaReciprocal = 1.0F / gamma;
			final float segmentOffset = this.colorSpaceSegmentOffset;
			final float slope = this.colorSpaceSlope;
			final float slopeMatch = this.colorSpaceSlopeMatch;
			
//			Gamma correct the 'normalized' accumulated pixel color components using sRGB as color space:
			r = r <= 0.0F ? 0.0F : r >= 1.0F ? 1.0F : r <= breakPoint ? r * slope : slopeMatch * pow(r, gammaReciprocal) - segmentOffset;
			g = g <= 0.0F ? 0.0F : g >= 1.0F ? 1.0F : g <= breakPoint ? g * slope : slopeMatch * pow(g, gammaReciprocal) - segmentOffset;
			b = b <= 0.0F ? 0.0F : b >= 1.0F ? 1.0F : b <= breakPoint ? b * slope : slopeMatch * pow(b, gammaReciprocal) - segmentOffset;
		}
		
//		Multiply the 'normalized' accumulated pixel color components with 255.0 and clamp them to the range [0.0, 255.0], so they can be displayed:
		r = min(max(r * 255.0F + 0.5F, 0.0F), 255.0F);
		g = min(max(g * 255.0F + 0.5F, 0.0F), 255.0F);
		b = min(max(b * 255.0F + 0.5F, 0.0F), 255.0F);
		
//		Update the pixels array with the actual color to display it:
		this.pixels[pixelsOffset + 0] = (byte)(b);
		this.pixels[pixelsOffset + 1] = (byte)(g);
		this.pixels[pixelsOffset + 2] = (byte)(r);
		this.pixels[pixelsOffset + 3] = (byte)(255);
	}
	
	private void doCalculateColorForSky(final float directionX, final float directionY, final float directionZ) {
//		Calculate the direction vector:
		float direction0X = directionX * this.sunAndSkyOrthoNormalBasisUX + directionY * this.sunAndSkyOrthoNormalBasisUY + directionZ * this.sunAndSkyOrthoNormalBasisUZ;
		float direction0Y = directionX * this.sunAndSkyOrthoNormalBasisVX + directionY * this.sunAndSkyOrthoNormalBasisVY + directionZ * this.sunAndSkyOrthoNormalBasisVZ;
		float direction0Z = directionX * this.sunAndSkyOrthoNormalBasisWX + directionY * this.sunAndSkyOrthoNormalBasisWY + directionZ * this.sunAndSkyOrthoNormalBasisWZ;
		
		if(direction0Z < 0.0F || this.sunAndSkyIsActive == BOOLEAN_FALSE) {
//			Update the colorTemporarySamples_$local$ array with black:
			this.colorTemporarySamples_$private$3[0] = 0.01F;
			this.colorTemporarySamples_$private$3[1] = 0.01F;
			this.colorTemporarySamples_$private$3[2] = 0.01F;
			
			return;
		}
		
		if(direction0Z < 0.001F) {
			direction0Z = 0.001F;
		}
		
//		Recalculate the direction vector:
		final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
		final float direction1X = direction0X * direction0LengthReciprocal;
		final float direction1Y = direction0Y * direction0LengthReciprocal;
		final float direction1Z = direction0Z * direction0LengthReciprocal;
		
//		Calculate the dot product between the direction vector and the sun direction vector:
		final float dotProduct = direction1X * this.sunAndSkySunDirectionX + direction1Y * this.sunAndSkySunDirectionY + direction1Z * this.sunAndSkySunDirectionZ;
		
//		Calculate some theta angles:
		final double theta0 = this.sunAndSkyTheta;
		final double theta1 = acos(max(min(direction1Z, 1.0D), -1.0D));
		
//		Calculate the cosines of the theta angles:
		final double cosTheta0 = cos(theta0);
		final double cosTheta1 = cos(theta1);
		final double cosTheta1Reciprocal = 1.0D / (cosTheta1 + 0.01D);
		
//		Calculate the gamma:
		final double gamma = acos(max(min(dotProduct, 1.0D), -1.0D));
		
//		Calculate the cosine of the gamma:
		final double cosGamma = cos(gamma);
		
//		TODO: Write explanation!
		final double perezRelativeLuminance0 = this.sunAndSkyPerezRelativeLuminance_$constant$[0];
		final double perezRelativeLuminance1 = this.sunAndSkyPerezRelativeLuminance_$constant$[1];
		final double perezRelativeLuminance2 = this.sunAndSkyPerezRelativeLuminance_$constant$[2];
		final double perezRelativeLuminance3 = this.sunAndSkyPerezRelativeLuminance_$constant$[3];
		final double perezRelativeLuminance4 = this.sunAndSkyPerezRelativeLuminance_$constant$[4];
		
//		TODO: Write explanation!
		final double zenithRelativeLuminance = this.sunAndSkyZenithRelativeLuminance;
		
//		TODO: Write explanation!
		final double perezX0 = this.sunAndSkyPerezX_$constant$[0];
		final double perezX1 = this.sunAndSkyPerezX_$constant$[1];
		final double perezX2 = this.sunAndSkyPerezX_$constant$[2];
		final double perezX3 = this.sunAndSkyPerezX_$constant$[3];
		final double perezX4 = this.sunAndSkyPerezX_$constant$[4];
		
//		TODO: Write explanation!
		final double perezY0 = this.sunAndSkyPerezY_$constant$[0];
		final double perezY1 = this.sunAndSkyPerezY_$constant$[1];
		final double perezY2 = this.sunAndSkyPerezY_$constant$[2];
		final double perezY3 = this.sunAndSkyPerezY_$constant$[3];
		final double perezY4 = this.sunAndSkyPerezY_$constant$[4];
		
//		TODO: Write explanation!
		final double zenithX = this.sunAndSkyZenithX;
		final double zenithY = this.sunAndSkyZenithY;
		
//		TODO: Write explanation!
		final double relativeLuminanceDenominator = ((1.0D + perezRelativeLuminance0 * exp(perezRelativeLuminance1)) * (1.0D + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * theta0) + perezRelativeLuminance4 * cosTheta0 * cosTheta0));
		final double relativeLuminanceNumerator = ((1.0D + perezRelativeLuminance0 * exp(perezRelativeLuminance1 * cosTheta1Reciprocal)) * (1.0D + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * gamma) + perezRelativeLuminance4 * cosGamma * cosGamma));
		final double relativeLuminance = zenithRelativeLuminance * relativeLuminanceNumerator / relativeLuminanceDenominator * 1.0e-4D;
		
//		TODO: Write explanation!
		final double xDenominator = ((1.0D + perezX0 * exp(perezX1)) * (1.0D + perezX2 * exp(perezX3 * theta1) + perezX4 * cosTheta0 * cosTheta0));
		final double xNumerator = ((1.0D + perezX0 * exp(perezX1 * cosTheta1Reciprocal)) * (1.0D + perezX2 * exp(perezX3 * gamma) + perezX4 * cosGamma * cosGamma));
		final double x = zenithX * xNumerator / xDenominator;
		
//		TODO: Write explanation!
		final double yDenominator = ((1.0D + perezY0 * exp(perezY1)) * (1.0D + perezY2 * exp(perezY3 * theta1) + perezY4 * cosTheta0 * cosTheta0));
		final double yNumerator = ((1.0D + perezY0 * exp(perezY1 * cosTheta1Reciprocal)) * (1.0D + perezY2 * exp(perezY3 * gamma) + perezY4 * cosGamma * cosGamma));
		final double y = zenithY * yNumerator / yDenominator;
		
//		Calculates a CIE XYZ color:
		final float colorCIE0 = 1.0F / (0.0241F + 0.2562F * (float)(x) - 0.7341F * (float)(y));
		final float colorCIE1 = (-1.3515F - 1.7703F * (float)(x) + 5.9114F * (float)(y)) * colorCIE0;
		final float colorCIE2 = (0.03F - 31.4424F * (float)(x) + 30.0717F * (float)(y)) * colorCIE0;
		final float colorCIEX = 10246.121F + colorCIE1 * 187.75537F + colorCIE2 * 213.14803F;
		final float colorCIEY = 10676.695F + colorCIE1 * 192.59653F + colorCIE2 * 76.29494F;
		final float colorCIEZ = 12372.504F + colorCIE1 * 3482.8765F + colorCIE2 * -235.71611F;
		final float colorCIEYReciprocal = 1.0F / colorCIEY;
		final float colorCIER = (float)(colorCIEX * relativeLuminance * colorCIEYReciprocal);
		final float colorCIEG = (float)(relativeLuminance);
		final float colorCIEB = (float)(colorCIEZ * relativeLuminance * colorCIEYReciprocal);
		
//		Converts the CIE XYZ color to an sRGB color:
		float r = 3.2410042F * colorCIER + -1.5373994F * colorCIEG + -0.49861607F * colorCIEB;
		float g = -0.9692241F * colorCIER + 1.8759298F * colorCIEG + 0.041554242F * colorCIEB;
		float b = 0.05563942F * colorCIER + -0.20401107F * colorCIEG + 1.0571486F * colorCIEB;
		
//		TODO: Write explanation!
		final float w = max(0.0F, -min(0.0F, min(r, min(g, b))));
		
//		TODO: Write explanation!
		r += w;
		g += w;
		b += w;
		
		if(this.sunAndSkyIsShowingClouds == BOOLEAN_TRUE) {
//			final float phi0 = atan2(directionX, directionZ);
//			final float phi1 = phi0 < 0.0F ? phi0 + PI_MULTIPLIED_BY_TWO : phi0;
			
//			final float theta = acos(directionY);
			
//			final float u = phi1 / PI_MULTIPLIED_BY_TWO;
//			final float v = theta / PI;
			
			final float frequency = (this.sunAndSkyTurbidity - 2.0F) * 8.0F;
			final float rMultiplier = 1.0F;
			final float gMultiplier = 1.0F;
			final float bMultiplier = 1.0F;
			final float rAddend = 135.0F / 2550.0F;
			final float gAddend = 206.0F / 2550.0F;
			final float bAddend = 235.0F / 2550.0F;
			
			final float noise = simplexFractionalBrownianMotionXYZ(frequency, 0.5F, 0.0F, 1.0F, 16, directionX, directionY, directionZ);
			
			r *= saturate(noise * rMultiplier + rAddend, 0.0F, 1.0F);
			g *= saturate(noise * gMultiplier + gAddend, 0.0F, 1.0F);
			b *= saturate(noise * bMultiplier + bAddend, 0.0F, 1.0F);
		}
		
		this.colorTemporarySamples_$private$3[0] = r;
		this.colorTemporarySamples_$private$3[1] = g;
		this.colorTemporarySamples_$private$3[2] = b;
	}
	
	@SuppressWarnings("unused")
	private void doCalculateColorForSkyBySampling(final float directionX, final float directionY, final float directionZ) {
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		
		final float sunDirectionWorldX = this.sunAndSkySunDirectionWorldX;
		final float sunDirectionWorldY = this.sunAndSkySunDirectionWorldY;
		final float sunDirectionWorldZ = this.sunAndSkySunDirectionWorldZ;
		
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		
		final float surfaceNormalX = this.intersections_$local$[offsetIntersectionSurfaceNormal];
		final float surfaceNormalY = this.intersections_$local$[offsetIntersectionSurfaceNormal + 1];
		final float surfaceNormalZ = this.intersections_$local$[offsetIntersectionSurfaceNormal + 2];
		final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
		final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
		
		final float dotProductSunDirectionWorldSurfaceNormal = sunDirectionWorldX * surfaceNormalX + sunDirectionWorldY * surfaceNormalY + sunDirectionWorldZ * surfaceNormalZ;
		final float dotProductSunDirectionWorldSurfaceNormalShading = sunDirectionWorldX * surfaceNormalShadingX + sunDirectionWorldY * surfaceNormalShadingY + sunDirectionWorldZ * surfaceNormalShadingZ;
		
		if(dotProductSunDirectionWorldSurfaceNormal > 0.0F && dotProductSunDirectionWorldSurfaceNormalShading > 0.0F) {
			r += this.sunAndSkySunColorR;
			g += this.sunAndSkySunColorG;
			b += this.sunAndSkySunColorB;
		}
		
		final int samples = this.sunAndSkySamples;
		final int colHistogramLength = this.sunAndSkyColHistogramLength;
		final int imageHistogramHeight = this.sunAndSkyImageHistogramHeight;
		
//		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
		for(int i = 0; i < samples; i++) {
			final float randomX = nextFloat();
			final float randomY = nextFloat();
			
			int x = 0;
			
			while(randomX >= this.sunAndSkyColHistogram_$constant$[x] && x < colHistogramLength - 1) {
				x++;
			}
			
			final int rowHistogramStart = x * imageHistogramHeight;
			final int rowHistogramEnd = rowHistogramStart + imageHistogramHeight - 1;
			
			int y = rowHistogramStart;
			
			while(randomY >= this.sunAndSkyImageHistogram_$constant$[y] && y < rowHistogramEnd) {
				y++;
			}
			
			final float u = x == 0 ? randomX / this.sunAndSkyColHistogram_$constant$[0] : (randomX - this.sunAndSkyColHistogram_$constant$[x - 1]) / (this.sunAndSkyColHistogram_$constant$[x] - this.sunAndSkyColHistogram_$constant$[x - 1]);
			final float v = y == 0 ? randomY / this.sunAndSkyImageHistogram_$constant$[rowHistogramStart] : (randomY - this.sunAndSkyImageHistogram_$constant$[y - 1]) / (this.sunAndSkyImageHistogram_$constant$[y] - this.sunAndSkyImageHistogram_$constant$[y - 1]);
			
			final float px = x == 0 ? this.sunAndSkyColHistogram_$constant$[0] : this.sunAndSkyColHistogram_$constant$[x] - this.sunAndSkyColHistogram_$constant$[x - 1];
			final float py = y == 0 ? this.sunAndSkyImageHistogram_$constant$[rowHistogramStart] : this.sunAndSkyImageHistogram_$constant$[y] - this.sunAndSkyImageHistogram_$constant$[y - 1];
			
			final float su = (x + u) / colHistogramLength;
			final float sv = (y + v) / imageHistogramHeight;
			
			final float invP = sin(sv * PI) * this.sunAndSkyJacobian / (samples * px * py);
			
			final float theta = u * PI_MULTIPLIED_BY_TWO;
			final float phi = v * PI;
			final float sinPhi = sin(phi);
			
			final float localX = -sinPhi * cos(theta);
			final float localY = cos(phi);
			final float localZ = sinPhi * sin(theta);
			
			doCalculateColorForSky(localX, localY, localZ);
			
			final float sampleR = this.colorTemporarySamples_$private$3[0];
			final float sampleG = this.colorTemporarySamples_$private$3[1];
			final float sampleB = this.colorTemporarySamples_$private$3[2];
			
			r += sampleR * invP;
			g += sampleG * invP;
			b += sampleB * invP;
		}
		
		this.colorTemporarySamples_$private$3[0] = r;
		this.colorTemporarySamples_$private$3[1] = g;
		this.colorTemporarySamples_$private$3[2] = b;
	}
	
	private void doCalculateSurfacePropertiesForPlane(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float aX, final float aY, final float aZ, final float bX, final float bY, final float bZ, final float cX, final float cY, final float cZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final float absSurfaceNormalX = abs(surfaceNormalX);
		final float absSurfaceNormalY = abs(surfaceNormalY);
		final float absSurfaceNormalZ = abs(surfaceNormalZ);
		
//		TODO: Write explanation!
		final boolean isX = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ;
		final boolean isY = absSurfaceNormalY > absSurfaceNormalZ;
		
//		TODO: Write explanation!
		final float a1X = isX ? aY : isY ? aZ : aX;
		final float a1Y = isX ? aZ : isY ? aX : aY;
		
//		TODO: Write explanation!
		final float b1X = isX ? cY - aX : isY ? cZ - aX : cX - aX;
		final float b1Y = isX ? cZ - aY : isY ? cX - aY : cY - aY;
		
//		TODO: Write explanation!
		final float c1X = isX ? bY - aX : isY ? bZ - aX : bX - aX;
		final float c1Y = isX ? bZ - aY : isY ? bX - aY : bY - aY;
		
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
		final int offsetIntersectionOrthoNormalBasisU = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_U;
		final int offsetIntersectionOrthoNormalBasisV = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_V;
		final int offsetIntersectionOrthoNormalBasisW = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_W;
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Calculate Ortho Normal Basis W:
		final float orthoNormalBasisWX = surfaceNormalX;
		final float orthoNormalBasisWY = surfaceNormalY;
		final float orthoNormalBasisWZ = surfaceNormalZ;
		
//		Calculate Ortho Normal Basis V:
		final float orthoNormalBasisV0X = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? 0.0F : absSurfaceNormalY < absSurfaceNormalZ ? orthoNormalBasisWZ : orthoNormalBasisWY;
		final float orthoNormalBasisV0Y = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? orthoNormalBasisWZ : absSurfaceNormalY < absSurfaceNormalZ ? 0.0F : -surfaceNormalX;
		final float orthoNormalBasisV0Z = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? -orthoNormalBasisWY : absSurfaceNormalY < absSurfaceNormalZ ? -surfaceNormalX : 0.0F;
		final float orthoNormalBasisV0LengthReciprocal = rsqrt(orthoNormalBasisV0X * orthoNormalBasisV0X + orthoNormalBasisV0Y * orthoNormalBasisV0Y + orthoNormalBasisV0Z * orthoNormalBasisV0Z);
		final float orthoNormalBasisV1X = orthoNormalBasisV0X * orthoNormalBasisV0LengthReciprocal;
		final float orthoNormalBasisV1Y = orthoNormalBasisV0Y * orthoNormalBasisV0LengthReciprocal;
		final float orthoNormalBasisV1Z = orthoNormalBasisV0Z * orthoNormalBasisV0LengthReciprocal;
		
//		Calculate Ortho Normal Basis U:
		final float orthoNormalBasisUX = orthoNormalBasisV1Y * orthoNormalBasisWZ - orthoNormalBasisV1Z * orthoNormalBasisWY;
		final float orthoNormalBasisUY = orthoNormalBasisV1Z * orthoNormalBasisWX - orthoNormalBasisV1X * orthoNormalBasisWZ;
		final float orthoNormalBasisUZ = orthoNormalBasisV1X * orthoNormalBasisWY - orthoNormalBasisV1Y * orthoNormalBasisWX;
		
//		Update the intersections array:
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU] = orthoNormalBasisUX;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 1] = orthoNormalBasisUY;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 2] = orthoNormalBasisUZ;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV] = orthoNormalBasisV1X;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 1] = orthoNormalBasisV1Y;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 2] = orthoNormalBasisV1Z;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW] = orthoNormalBasisWX;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 1] = orthoNormalBasisWY;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 2] = orthoNormalBasisWZ;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormal] = surfaceNormalX;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormalY;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormalX;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormalY;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormalZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForSphere(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float positionX, final float positionY, final float positionZ) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the surface normal:
		final float surfaceNormal0X = surfaceIntersectionPointX - positionX;
		final float surfaceNormal0Y = surfaceIntersectionPointY - positionY;
		final float surfaceNormal0Z = surfaceIntersectionPointZ - positionZ;
		final float lengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		final float surfaceNormal1X = surfaceNormal0X * lengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * lengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * lengthReciprocal;
		
//		Calculate the UV-coordinates:
		final float direction0X = positionX - surfaceIntersectionPointX;
		final float direction0Y = positionY - surfaceIntersectionPointY;
		final float direction0Z = positionZ - surfaceIntersectionPointZ;
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
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Update the intersections array:
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForTerrain(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
		final float epsilon = 0.02F;
		
		final float surfaceNormalX = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX - epsilon, surfaceIntersectionPointZ) - simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX + epsilon, surfaceIntersectionPointZ);
		final float surfaceNormalY = -2.0F * epsilon;
		final float surfaceNormalZ = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ - epsilon) - simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ + epsilon);
		final float surfaceNormalLengthReciprocal = rsqrt(surfaceNormalX * surfaceNormalX + surfaceNormalY * surfaceNormalY + surfaceNormalZ * surfaceNormalZ);
		final float surfaceNormalNormalizedX = surfaceNormalX * surfaceNormalLengthReciprocal;
		final float surfaceNormalNormalizedY = surfaceNormalY * surfaceNormalLengthReciprocal;
		final float surfaceNormalNormalizedZ = surfaceNormalZ * surfaceNormalLengthReciprocal;
		
		final float u = surfaceIntersectionPointX;
		final float v = surfaceIntersectionPointZ;
		
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormalNormalizedX;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormalNormalizedY;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormalNormalizedZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormalNormalizedX;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormalNormalizedY;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormalNormalizedZ;
	}
	
	private void doCalculateSurfacePropertiesForTriangle(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float aPositionX, final float aPositionY, final float aPositionZ, final float bPositionX, final float bPositionY, final float bPositionZ, final float cPositionX, final float cPositionY, final float cPositionZ, final float aSurfaceNormalX, final float aSurfaceNormalY, final float aSurfaceNormalZ, final float bSurfaceNormalX, final float bSurfaceNormalY, final float bSurfaceNormalZ, final float cSurfaceNormalX, final float cSurfaceNormalY, final float cSurfaceNormalZ, final float aTextureCoordinatesU, final float aTextureCoordinatesV, final float bTextureCoordinatesU, final float bTextureCoordinatesV, final float cTextureCoordinatesU, final float cTextureCoordinatesV) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the Barycentric-coordinates:
		final float edge0X = bPositionX - aPositionX;
		final float edge0Y = bPositionY - aPositionY;
		final float edge0Z = bPositionZ - aPositionZ;
		final float edge1X = cPositionX - aPositionX;
		final float edge1Y = cPositionY - aPositionY;
		final float edge1Z = cPositionZ - aPositionZ;
		final float v0X = directionY * edge1Z - directionZ * edge1Y;
		final float v0Y = directionZ * edge1X - directionX * edge1Z;
		final float v0Z = directionX * edge1Y - directionY * edge1X;
		final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;
		final float determinantReciprocal = 1.0F / determinant;
		final float v1X = originX - aPositionX;
		final float v1Y = originY - aPositionY;
		final float v1Z = originZ - aPositionZ;
		final float u0 = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
		final float v2X = v1Y * edge0Z - v1Z * edge0Y;
		final float v2Y = v1Z * edge0X - v1X * edge0Z;
		final float v2Z = v1X * edge0Y - v1Y * edge0X;
		final float v0 = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
		final float w = 1.0F - u0 - v0;
		
//		Calculate the UV-coordinates:
		final float u1 = w * aTextureCoordinatesU + u0 * bTextureCoordinatesU + v0 * cTextureCoordinatesU;
		final float v1 = w * aTextureCoordinatesV + u0 * bTextureCoordinatesV + v0 * cTextureCoordinatesV;
		
//		Get the intersections offset:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate some offsets:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Update the intersections array:
		this.intersections_$local$[intersectionsOffset] = distance;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates] = u1;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v1;
		
		if(this.shading == SHADING_FLAT) {
//			Calculate the surface normal for Flat Shading:
			final float surfaceNormal0X = edge0Y * edge1Z - edge0Z * edge1Y;
			final float surfaceNormal0Y = edge0Z * edge1X - edge0X * edge1Z;
			final float surfaceNormal0Z = edge0X * edge1Y - edge0Y * edge1X;
			final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
			final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
			final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
			final float dotProduct = aSurfaceNormalX != 0.0F && aSurfaceNormalY != 0.0F && aSurfaceNormalZ != 0.0F ? surfaceNormal1X * aSurfaceNormalX + surfaceNormal1Y * aSurfaceNormalY + surfaceNormal1Z * aSurfaceNormalZ : 0.0F;
			final float surfaceNormal2X = dotProduct < 0.0F ? -surfaceNormal1X : surfaceNormal1X;
			final float surfaceNormal2Y = dotProduct < 0.0F ? -surfaceNormal1Y : surfaceNormal1Y;
			final float surfaceNormal2Z = dotProduct < 0.0F ? -surfaceNormal1Z : surfaceNormal1Z;
			
//			Update the intersections array based on Flat Shading:
			this.intersections_$local$[offsetIntersectionSurfaceNormal] = surfaceNormal2X;
			this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal2X;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal2Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal2Z;
		} else if(this.shading == SHADING_GOURAUD) {
//			Calculate the surface normal for Gouraud Shading:
			final float surfaceNormal3X = aSurfaceNormalX * w + bSurfaceNormalX * u0 + cSurfaceNormalX * v0;
			final float surfaceNormal3Y = aSurfaceNormalY * w + bSurfaceNormalY * u0 + cSurfaceNormalY * v0;
			final float surfaceNormal3Z = aSurfaceNormalZ * w + bSurfaceNormalZ * u0 + cSurfaceNormalZ * v0;
			final float surfaceNormal3LengthReciprocal = rsqrt(surfaceNormal3X * surfaceNormal3X + surfaceNormal3Y * surfaceNormal3Y + surfaceNormal3Z * surfaceNormal3Z);
			final float surfaceNormal4X = surfaceNormal3X * surfaceNormal3LengthReciprocal;
			final float surfaceNormal4Y = surfaceNormal3Y * surfaceNormal3LengthReciprocal;
			final float surfaceNormal4Z = surfaceNormal3Z * surfaceNormal3LengthReciprocal;
			final float dotProduct = aSurfaceNormalX != 0.0F && aSurfaceNormalY != 0.0F && aSurfaceNormalZ != 0.0F ? surfaceNormal4X * aSurfaceNormalX + surfaceNormal4Y * aSurfaceNormalY + surfaceNormal4Z * aSurfaceNormalZ : 0.0F;
			final float surfaceNormal5X = dotProduct < 0.0F ? -surfaceNormal4X : surfaceNormal4X;
			final float surfaceNormal5Y = dotProduct < 0.0F ? -surfaceNormal4Y : surfaceNormal4Y;
			final float surfaceNormal5Z = dotProduct < 0.0F ? -surfaceNormal4Z : surfaceNormal4Z;
			
//			Update the intersections array based on Gouraud Shading:
			this.intersections_$local$[offsetIntersectionSurfaceNormal] = surfaceNormal5X;
			this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormal5Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormal5Z;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal5X;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal5Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal5Z;
		}
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
		final int depthMaximum = this.rendererPTRayDepthMaximum;
		final int depthRussianRoulette = this.rendererPTRayDepthRussianRoulette;
		
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the radiance multiplier to white:
		float radianceMultiplierR = 1.0F;
		float radianceMultiplierG = 1.0F;
		float radianceMultiplierB = 1.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex0 = getLocalId() * SIZE_COLOR_RGB;
		
//		Run the following do-while-loop as long as the current depth is less than the maximum depth and Russian Roulette does not terminate:
		do {
//			Perform an intersection test:
			doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
			final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
			
			if(depthCurrent == 0) {
				this.primitiveOffsetsForPrimaryRay[getGlobalId()] = primitivesOffset;
			}
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || primitivesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0];
				pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1];
				pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2];
				
//				Update the current pixel color:
				this.colorCurrentSamples_$local$[pixelIndex0 + 0] = pixelColorR;
				this.colorCurrentSamples_$local$[pixelIndex0 + 1] = pixelColorG;
				this.colorCurrentSamples_$local$[pixelIndex0 + 2] = pixelColorB;
				
				return;
			}
			
//			Retrieve the offset to the surfaces array for the given shape:
			final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
//			Retrieve the offsets of the surface intersection point and the surface normal:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal for shading:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
			final int textureOffsetEmission = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_EMISSION_OFFSET]);
			
//			Calculate the albedo texture color for the intersected primitive:
			final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
			
//			Get the color of the primitive from the albedo texture color that was looked up:
			float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
			float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
			float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
			
//			Calculate the emission texture color for the intersected primitive:
			final int emissionColorRGB = doGetTextureColor(textureOffsetEmission);
			
//			Get the color of the primitive from the emission texture color that was looked up:
			float emissionColorR = ((emissionColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
			float emissionColorG = ((emissionColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
			float emissionColorB = ((emissionColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
			
//			Add the current radiance multiplied by the emission of the intersected primitive to the current pixel color:
			pixelColorR += radianceMultiplierR * emissionColorR;
			pixelColorG += radianceMultiplierG * emissionColorG;
			pixelColorB += radianceMultiplierB * emissionColorB;
			
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
					final boolean isIntersecting = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, true) < INFINITY;
					
//					Test that an intersection was actually made, and if not, return black color (or possibly the background color):
					if(!isIntersecting) {
//						Calculate the color for the sky in the current direction:
						doCalculateColorForSky(directionX, directionY, directionZ);
						
//						Add the color for the sky to the current pixel color:
						pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0];
						pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1];
						pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2];
					}
					
//					Update the current pixel color:
					this.colorCurrentSamples_$local$[pixelIndex0 + 0] = pixelColorR;
					this.colorCurrentSamples_$local$[pixelIndex0 + 1] = pixelColorG;
					this.colorCurrentSamples_$local$[pixelIndex0 + 2] = pixelColorB;
					
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
			final int material = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			
//			Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
			final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
			final float dotProductMultipliedByTwo = dotProduct * 2.0F;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float surfaceNormalWNormalizedX = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
			final float surfaceNormalWNormalizedY = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
			final float surfaceNormalWNormalizedZ = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
			
			if(material == ClearCoatMaterial.TYPE) {
				final float a = REFRACTIVE_INDEX_GLASS - REFRACTIVE_INDEX_AIR;
				final float b = REFRACTIVE_INDEX_GLASS + REFRACTIVE_INDEX_AIR;
				final float c = REFRACTIVE_INDEX_AIR / REFRACTIVE_INDEX_GLASS;
				final float e = (a * a) / (b * b);
				
//				TODO: Write explanation!
				final float nnt = c;
				
//				Calculate the dot product between the W direction and the current ray direction:
				final float dotProduct0 = surfaceNormalWNormalizedX * directionX + surfaceNormalWNormalizedY * directionY + surfaceNormalWNormalizedZ * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProduct0 * dotProduct0);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
//				Initialize the specular color component values to be used:
				final float specularColorR = 1.0F;
				final float specularColorG = 1.0F;
				final float specularColorB = 1.0F;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.02F;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.02F;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.02F;
					
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
					final float angle1 = -dotProduct0;
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = e + (1.0F - e) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = nextFloat() < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
					if(isReflectionDirection) {
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.02F;
						originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.02F;
						originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.02F;
						
//						Update the ray direction for the next iteration:
						directionX = reflectionDirectionX;
						directionY = reflectionDirectionY;
						directionZ = reflectionDirectionZ;
						
//						Multiply the current radiance multiplier with the specular color:
						radianceMultiplierR *= specularColorR;
						radianceMultiplierG *= specularColorG;
						radianceMultiplierB *= specularColorB;
					} else {
//						Compute cosine weighted hemisphere sample:
						final float u = nextFloat();
						final float v = nextFloat();
						final float phi = PI_MULTIPLIED_BY_TWO * u;
						final float cosTheta = sqrt(v);
						final float sinTheta = sqrt(1.0F - v);
						final float x = cos(phi) * sinTheta;
						final float y = sin(phi) * sinTheta;
						final float z = cosTheta;
						
//						Check if the direction is the Y-direction:
						final boolean isY = abs(surfaceNormalWNormalizedX) > 0.1F;
						
//						Calculate the orthonormal basis U vector:
						final float surfaceNormalUX = (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedZ;
						final float surfaceNormalUY = -((isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedZ);
						final float surfaceNormalUZ = (isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedY - (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedX;
						final float surfaceNormalULengthReciprocal = rsqrt(surfaceNormalUX * surfaceNormalUX + surfaceNormalUY * surfaceNormalUY + surfaceNormalUZ * surfaceNormalUZ);
						final float surfaceNormalUNormalizedX = surfaceNormalUX * surfaceNormalULengthReciprocal;
						final float surfaceNormalUNormalizedY = surfaceNormalUY * surfaceNormalULengthReciprocal;
						final float surfaceNormalUNormalizedZ = surfaceNormalUZ * surfaceNormalULengthReciprocal;
						
//						Calculate the orthonormal basis V vector:
						final float surfaceNormalVNormalizedX = surfaceNormalWNormalizedY * surfaceNormalUNormalizedZ - surfaceNormalWNormalizedZ * surfaceNormalUNormalizedY;
						final float surfaceNormalVNormalizedY = surfaceNormalWNormalizedZ * surfaceNormalUNormalizedX - surfaceNormalWNormalizedX * surfaceNormalUNormalizedZ;
						final float surfaceNormalVNormalizedZ = surfaceNormalWNormalizedX * surfaceNormalUNormalizedY - surfaceNormalWNormalizedY * surfaceNormalUNormalizedX;
						
//						Calculate the direction for the next iteration:
						final float lambertianDirectionX = surfaceNormalUNormalizedX * x + surfaceNormalVNormalizedX * y + surfaceNormalWNormalizedX * z;
						final float lambertianDirectionY = surfaceNormalUNormalizedY * x + surfaceNormalVNormalizedY * y + surfaceNormalWNormalizedY * z;
						final float lambertianDirectionZ = surfaceNormalUNormalizedZ * x + surfaceNormalVNormalizedZ * y + surfaceNormalWNormalizedZ * z;
						final float lambertianDirectionLengthReciprocal = rsqrt(lambertianDirectionX * lambertianDirectionX + lambertianDirectionY * lambertianDirectionY + lambertianDirectionZ * lambertianDirectionZ);
						final float lambertianDirectionNormalizedX = lambertianDirectionX * lambertianDirectionLengthReciprocal;
						final float lambertianDirectionNormalizedY = lambertianDirectionY * lambertianDirectionLengthReciprocal;
						final float lambertianDirectionNormalizedZ = lambertianDirectionZ * lambertianDirectionLengthReciprocal;
						
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.01F;
						originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.01F;
						originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.01F;
						
//						Update the ray direction for the next iteration:
						directionX = lambertianDirectionNormalizedX;
						directionY = lambertianDirectionNormalizedY;
						directionZ = lambertianDirectionNormalizedZ;
						
//						Multiply the current radiance multiplier with the albedo:
						radianceMultiplierR *= albedoColorR;
						radianceMultiplierG *= albedoColorG;
						radianceMultiplierB *= albedoColorB;
					}
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
			} else if(material == LambertianMaterial.TYPE) {
//				Compute cosine weighted hemisphere sample:
				final float u = nextFloat();
				final float v = nextFloat();
				final float phi = PI_MULTIPLIED_BY_TWO * u;
				final float cosTheta = sqrt(v);
				final float sinTheta = sqrt(1.0F - v);
				final float x = cos(phi) * sinTheta;
				final float y = sin(phi) * sinTheta;
				final float z = cosTheta;
				
//				Check if the direction is the Y-direction:
				final boolean isY = abs(surfaceNormalWNormalizedX) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float surfaceNormalUX = (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedZ;
				final float surfaceNormalUY = -((isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedZ);
				final float surfaceNormalUZ = (isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedY - (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedX;
				final float surfaceNormalULengthReciprocal = rsqrt(surfaceNormalUX * surfaceNormalUX + surfaceNormalUY * surfaceNormalUY + surfaceNormalUZ * surfaceNormalUZ);
				final float surfaceNormalUNormalizedX = surfaceNormalUX * surfaceNormalULengthReciprocal;
				final float surfaceNormalUNormalizedY = surfaceNormalUY * surfaceNormalULengthReciprocal;
				final float surfaceNormalUNormalizedZ = surfaceNormalUZ * surfaceNormalULengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float surfaceNormalVNormalizedX = surfaceNormalWNormalizedY * surfaceNormalUNormalizedZ - surfaceNormalWNormalizedZ * surfaceNormalUNormalizedY;
				final float surfaceNormalVNormalizedY = surfaceNormalWNormalizedZ * surfaceNormalUNormalizedX - surfaceNormalWNormalizedX * surfaceNormalUNormalizedZ;
				final float surfaceNormalVNormalizedZ = surfaceNormalWNormalizedX * surfaceNormalUNormalizedY - surfaceNormalWNormalizedY * surfaceNormalUNormalizedX;
				
//				Calculate the direction for the next iteration:
				final float lambertianDirectionX = surfaceNormalUNormalizedX * x + surfaceNormalVNormalizedX * y + surfaceNormalWNormalizedX * z;
				final float lambertianDirectionY = surfaceNormalUNormalizedY * x + surfaceNormalVNormalizedY * y + surfaceNormalWNormalizedY * z;
				final float lambertianDirectionZ = surfaceNormalUNormalizedZ * x + surfaceNormalVNormalizedZ * y + surfaceNormalWNormalizedZ * z;
				final float lambertianDirectionLengthReciprocal = rsqrt(lambertianDirectionX * lambertianDirectionX + lambertianDirectionY * lambertianDirectionY + lambertianDirectionZ * lambertianDirectionZ);
				final float lambertianDirectionNormalizedX = lambertianDirectionX * lambertianDirectionLengthReciprocal;
				final float lambertianDirectionNormalizedY = lambertianDirectionY * lambertianDirectionLengthReciprocal;
				final float lambertianDirectionNormalizedZ = lambertianDirectionZ * lambertianDirectionLengthReciprocal;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.01F;
				originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.01F;
				originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = lambertianDirectionNormalizedX;
				directionY = lambertianDirectionNormalizedY;
				directionZ = lambertianDirectionNormalizedZ;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			} else if(material == PhongMaterial.TYPE) {
//				Compute power cosine weighted hemisphere sample:
				final float exponent = 20.0F;
				final float u = nextFloat();
				final float v = nextFloat();
				final float phi = PI_MULTIPLIED_BY_TWO * u;
				final float cosTheta = pow(1.0F - v, 1.0F / (exponent + 1.0F));
				final float sinTheta = sqrt(1.0F - cosTheta * cosTheta);
				final float x = cos(phi) * sinTheta;
				final float y = sin(phi) * sinTheta;
				final float z = cosTheta;
				
//				Calculate the orthonormal basis W vector:
				final float reflectionDirectionWX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionWY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionWZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				final float reflectionDirectionWLengthReciprocal = rsqrt(reflectionDirectionWX * reflectionDirectionWX + reflectionDirectionWY * reflectionDirectionWY + reflectionDirectionWZ * reflectionDirectionWZ);
				final float reflectionDirectionWNormalizedX = reflectionDirectionWX * reflectionDirectionWLengthReciprocal;
				final float reflectionDirectionWNormalizedY = reflectionDirectionWY * reflectionDirectionWLengthReciprocal;
				final float reflectionDirectionWNormalizedZ = reflectionDirectionWZ * reflectionDirectionWLengthReciprocal;
				
//				Check if the direction is the Y-direction:
				final boolean isY = abs(reflectionDirectionWNormalizedX) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float reflectionDirectionUX = (isY ? 1.0F : 0.0F) * reflectionDirectionWNormalizedZ;
				final float reflectionDirectionUY = -((isY ? 0.0F : 1.0F) * reflectionDirectionWNormalizedZ);
				final float reflectionDirectionUZ = (isY ? 0.0F : 1.0F) * reflectionDirectionWNormalizedY - (isY ? 1.0F : 0.0F) * reflectionDirectionWNormalizedX;
				final float reflectionDirectionULengthReciprocal = rsqrt(reflectionDirectionUX * reflectionDirectionUX + reflectionDirectionUY * reflectionDirectionUY + reflectionDirectionUZ * reflectionDirectionUZ);
				final float reflectionDirectionUNormalizedX = reflectionDirectionUX * reflectionDirectionULengthReciprocal;
				final float reflectionDirectionUNormalizedY = reflectionDirectionUY * reflectionDirectionULengthReciprocal;
				final float reflectionDirectionUNormalizedZ = reflectionDirectionUZ * reflectionDirectionULengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float reflectionDirectionVNormalizedX = reflectionDirectionWNormalizedY * reflectionDirectionUNormalizedZ - reflectionDirectionWNormalizedZ * reflectionDirectionUNormalizedY;
				final float reflectionDirectionVNormalizedY = reflectionDirectionWNormalizedZ * reflectionDirectionUNormalizedX - reflectionDirectionWNormalizedX * reflectionDirectionUNormalizedZ;
				final float reflectionDirectionVNormalizedZ = reflectionDirectionWNormalizedX * reflectionDirectionUNormalizedY - reflectionDirectionWNormalizedY * reflectionDirectionUNormalizedX;
				
//				Calculate the direction for the next iteration:
				final float phongDirectionX = reflectionDirectionUNormalizedX * x + reflectionDirectionVNormalizedX * y + reflectionDirectionWX * z;
				final float phongDirectionY = reflectionDirectionUNormalizedY * x + reflectionDirectionVNormalizedY * y + reflectionDirectionWY * z;
				final float phongDirectionZ = reflectionDirectionUNormalizedZ * x + reflectionDirectionVNormalizedZ * y + reflectionDirectionWZ * z;
				final float phongDirectionLengthReciprocal = rsqrt(phongDirectionX * phongDirectionX + phongDirectionY * phongDirectionY + phongDirectionZ * phongDirectionZ);
				final float phongDirectionNormalizedX = phongDirectionX * phongDirectionLengthReciprocal;
				final float phongDirectionNormalizedY = phongDirectionY * phongDirectionLengthReciprocal;
				final float phongDirectionNormalizedZ = phongDirectionZ * phongDirectionLengthReciprocal;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + reflectionDirectionWNormalizedX * 0.01F;
				originY = surfaceIntersectionPointY + reflectionDirectionWNormalizedY * 0.01F;
				originZ = surfaceIntersectionPointZ + reflectionDirectionWNormalizedZ * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = phongDirectionNormalizedX;
				directionY = phongDirectionNormalizedY;
				directionZ = phongDirectionNormalizedZ;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			} else if(material == GlassMaterial.TYPE) {
				final float a = REFRACTIVE_INDEX_GLASS - REFRACTIVE_INDEX_AIR;
				final float b = REFRACTIVE_INDEX_GLASS + REFRACTIVE_INDEX_AIR;
				final float c = REFRACTIVE_INDEX_AIR / REFRACTIVE_INDEX_GLASS;
				final float d = REFRACTIVE_INDEX_GLASS / REFRACTIVE_INDEX_AIR;
				final float e = (a * a) / (b * b);
				
//				Check if the current ray is going in towards the same shape or out of it:
				final boolean isGoingIn = surfaceNormalShadingX * surfaceNormalWNormalizedX + surfaceNormalShadingY * surfaceNormalWNormalizedY + surfaceNormalShadingZ * surfaceNormalWNormalizedZ > 0.0F;
				
//				TODO: Write explanation!
				final float nnt = isGoingIn ? c : d;
				
//				Calculate the dot product between the orthonormal basis W vector and the current direction vector:
				final float dotProductOfW0AndDirection = surfaceNormalWNormalizedX * directionX + surfaceNormalWNormalizedY * directionY + surfaceNormalWNormalizedZ * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.02F;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.02F;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.02F;
					
//					Update the ray direction for the next iteration:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
				} else {
//					Calculate the square root of the total internal reflection:
					final float sqrtTotalInternalReflection = sqrt(totalInternalReflection);
					
//					Calculate the transmission direction:
					final float scalar = isGoingIn ? dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection : -(dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection);
					final float direction0X = directionX * nnt - surfaceNormalShadingX * scalar;
					final float direction0Y = directionY * nnt - surfaceNormalShadingY * scalar;
					final float direction0Z = directionZ * nnt - surfaceNormalShadingZ * scalar;
					final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
					final float transmissionDirectionX = direction0X * direction0LengthReciprocal;
					final float transmissionDirectionY = direction0Y * direction0LengthReciprocal;
					final float transmissionDirectionZ = direction0Z * direction0LengthReciprocal;
					
//					Calculate some angles:
					final float angle1 = (isGoingIn ? -dotProductOfW0AndDirection : transmissionDirectionX * surfaceNormalShadingX + transmissionDirectionY * surfaceNormalShadingY + transmissionDirectionZ * surfaceNormalShadingZ);
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = e + (1.0F - e) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = nextFloat() < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
//					Retrieve the epsilon value that offsets the ray origin to mitigate self intersections:
					final float epsilon = isReflectionDirection ? 0.01F : 0.000001F;
					
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * epsilon;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * epsilon;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * epsilon;
					
//					Update the ray direction for the next iteration:
					directionX = isReflectionDirection ? reflectionDirectionX : transmissionDirectionX;
					directionY = isReflectionDirection ? reflectionDirectionY : transmissionDirectionY;
					directionZ = isReflectionDirection ? reflectionDirectionZ : transmissionDirectionZ;
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
			} else if(material == ReflectionMaterial.TYPE) {
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalShadingX * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormalShadingY * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormalShadingZ * 0.000001F;
				
//				Update the ray direction for the next iteration:
				directionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				directionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				directionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			}
		} while(depthCurrent < depthMaximum);
		
//		Perform an intersection test:
		final boolean isIntersecting = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, true) < INFINITY;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(!isIntersecting) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0];
			pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1];
			pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2];
		}
		
//		Update the current pixel color:
		this.colorCurrentSamples_$local$[pixelIndex0 + 0] = pixelColorR;
		this.colorCurrentSamples_$local$[pixelIndex0 + 1] = pixelColorG;
		this.colorCurrentSamples_$local$[pixelIndex0 + 2] = pixelColorB;
	}
	
	private void doPerformNormalMappingViaNoise(final int primitivesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Retrieve the offset to the surfaces array:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		
//		Retrieve the noise amount from the current shape:
		final float amount = this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_NOISE_AMOUNT];
		
//		Retrieve the noise scale from the current shape:
		final float scale = this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_NOISE_SCALE];
		
//		Check that the noise amount and noise scale are greater than 0.0:
		if(this.isNormalMapping == 1 && amount > 0.0F && scale > 0.0F) {
//			Retrieve the surface intersection point and the surface normal from the current shape:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the X-, Y- and Z-component values from the surface intersection point:
			final float x0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint];
			final float y0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float z0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Compute the reciprocal of the noise scale:
			final float scaleReciprocal = 1.0F / scale;
			
//			Scale the X-, Y- and Z-component values:
			final float x1 = x0 * scaleReciprocal;
			final float y1 = y0 * scaleReciprocal;
			final float z1 = z0 * scaleReciprocal;
			
//			Compute the noise given the X-, Y- and Z-component values:
			final float noiseX = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, x1, y1, z1);
			final float noiseY = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, y1, z1, x1);
			final float noiseZ = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, z1, x1, y1);
			
//			Calculate the surface normal:
			final float surfaceNormal0X = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float surfaceNormal0Y = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormal0Z = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;
			final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;
			final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;
			final float surfaceNormal1LengthReciprocal = rsqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);
			final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;
			
//			Update the intersections array with the new surface normal:
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal2X;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal2Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal2Z;
		}
	}
	
	private void doPerformNormalMappingViaImageTexture(final int primitivesOffset) {
//		Retrieve the offset in the textures array and the type of the texture:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		final int texturesOffset = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_NORMAL_OFFSET]);
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset]);
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
		if(this.isNormalMapping == 1 && textureType == ImageTexture.TYPE) {
//			Calculate the texture color:
			final int rGB = doGetTextureColorFromImageTexture(texturesOffset);
			
//			Retrieve the R-, G- and B-component values:
			final float r = 2.0F * (((rGB >> 16) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
			final float g = 2.0F * (((rGB >>  8) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
			final float b = 2.0F * (((rGB >>  0) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
			
//			Retrieve the offset of the surface normal in the intersections array:
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the orthonormal basis W-vector:
			final float wX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float wY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float wZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final int type = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SHAPE_TYPE];
			
			if(type == Sphere.TYPE) {
				final float v0X = -2.0F * PI * wY;//wZ?
				final float v0Y = 2.0F * PI * wX;
				final float v0Z = 0.0F;
				
				final float u0X = v0Y * wZ - v0Z * wY;
				final float u0Y = v0Z * wX - v0X * wZ;
				final float u0Z = v0X * wY - v0Y * wX;
				final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
				final float u1X = u0X * u0LengthReciprocal;
				final float u1Y = u0Y * u0LengthReciprocal;
				final float u1Z = u0Z * u0LengthReciprocal;
				
				final float v1X = wY * u1Z - wZ * u1Y;
				final float v1Y = wZ * u1X - wX * u1Z;
				final float v1Z = wX * u1Y - wY * u1X;
				final float v1LengthReciprocal = rsqrt(v1X * v1X + v1Y * v1Y + v1Z * v1Z);
				final float v2X = v1X * v1LengthReciprocal;
				final float v2Y = v1Y * v1LengthReciprocal;
				final float v2Z = v1Z * v1LengthReciprocal;
				
				final float surfaceNormal0X = r * u1X + g * v2X + b * wX;
				final float surfaceNormal0Y = r * u1Y + g * v2Y + b * wY;
				final float surfaceNormal0Z = r * u1Z + g * v2Z + b * wZ;
				final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal1X;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
			} else {
//				Calculate the absolute values of the orthonormal basis W-vector:
				final float absWX = abs(wX);
				final float absWY = abs(wY);
				final float absWZ = abs(wZ);
				
//				Check the direction of the orthonormal basis:
				final boolean isWX = absWX < absWY && absWX < absWZ;
				final boolean isWY = absWY < absWZ;
				
//				Calculate the orthonormal basis V-vector:
				final float v0X = isWX ? 0.0F : isWY ? wZ : wY;
				final float v0Y = isWX ? wZ : isWY ? 0.0F : -wX;
				final float v0Z = isWX ? -wY : isWY ? -wX : 0.0F;
				final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
				final float v1X = v0X * v0LengthReciprocal;
				final float v1Y = v0Y * v0LengthReciprocal;
				final float v1Z = v0Z * v0LengthReciprocal;
				
//				Calculate the orthonormal basis U-vector:
				final float u0X = v1Y * wZ - v1Z * wY;
				final float u0Y = v1Z * wX - v1X * wZ;
				final float u0Z = v1X * wY - v1Y * wX;
				final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
				final float u1X = u0X * u0LengthReciprocal;
				final float u1Y = u0Y * u0LengthReciprocal;
				final float u1Z = u0Z * u0LengthReciprocal;
				
//				Calculate the new surface normal:
				final float surfaceNormal0X = r * u1X + g * v1X + b * wX;
				final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;
				final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;
				final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
//				Update the intersections array:
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal1X;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
			}
		}
	}
	
	private void doRayCasting() {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
//		Retrieve the offset to the surfaces array for the given shape:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		
		this.primitiveOffsetsForPrimaryRay[getGlobalId()] = primitivesOffset;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance == INFINITY || primitivesOffset == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR = this.colorTemporarySamples_$private$3[0];
			pixelColorG = this.colorTemporarySamples_$private$3[1];
			pixelColorB = this.colorTemporarySamples_$private$3[2];
			
//			Update the current pixel color:
			this.colorCurrentSamples_$local$[pixelIndex + 0] = pixelColorR;
			this.colorCurrentSamples_$local$[pixelIndex + 1] = pixelColorG;
			this.colorCurrentSamples_$local$[pixelIndex + 2] = pixelColorB;
			
			return;
		}
		
		final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
		
//		Calculate the albedo texture color for the intersected shape:
		final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
		
//		Get the color of the shape from the albedo texture color that was looked up:
		float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
//		Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		
//		Retrieve the surface intersection point from the intersections array:
		final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
		final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
		final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
		
//		Retrieve the surface normal from the intersections array:
		final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
		final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
		
		final int colorRGB = doShaderPhongReflectionModel0(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalShadingX, surfaceNormalShadingY, surfaceNormalShadingZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//		final int colorRGB = doShaderPhongReflectionModel1(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalShadingX, surfaceNormalShadingY, surfaceNormalShadingZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
		
		pixelColorR = ((colorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		pixelColorG = ((colorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		pixelColorB = ((colorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
//		Update the current pixel color:
		this.colorCurrentSamples_$local$[pixelIndex + 0] = pixelColorR;
		this.colorCurrentSamples_$local$[pixelIndex + 1] = pixelColorG;
		this.colorCurrentSamples_$local$[pixelIndex + 2] = pixelColorB;
	}
	
	private void doRayMarching() {
		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
		originY = doGetY(originX, originZ) + 0.1F;
		
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
		final float delTMultiplier = 0.01F;
		
		float delT = delTMultiplier;
		
		final float minT = 0.001F;
		final float maxT = max(originY, 1.0F) * 20.0F;
		
		doCalculateColorForSky(directionX, directionY, directionZ);
		
		float pixelColorR = this.colorTemporarySamples_$private$3[0];
		float pixelColorG = this.colorTemporarySamples_$private$3[1];
		float pixelColorB = this.colorTemporarySamples_$private$3[2];
		
		for(float t = minT; t < maxT; t += delT) {
			final float surfaceIntersectionPointX = originX + directionX * t;
			final float surfaceIntersectionPointY = originY + directionY * t;
			final float surfaceIntersectionPointZ = originZ + directionZ * t;
			
			final float height = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ);
			
			if(surfaceIntersectionPointY < height) {
//				Calculate the surface normal at the surface intersection point:
				final float surfaceNormalX = doGetY(surfaceIntersectionPointX - EPSILON, surfaceIntersectionPointZ) - doGetY(surfaceIntersectionPointX + EPSILON, surfaceIntersectionPointZ);
				final float surfaceNormalY = 2.0F * EPSILON;
				final float surfaceNormalZ = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ - EPSILON) - doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ + EPSILON);
				final float surfaceNormalLengthReciprocal = 1.0F / sqrt(surfaceNormalX * surfaceNormalX + surfaceNormalY * surfaceNormalY + surfaceNormalZ * surfaceNormalZ);
				final float surfaceNormalNormalizedX = surfaceNormalX * surfaceNormalLengthReciprocal;
				final float surfaceNormalNormalizedY = surfaceNormalY * surfaceNormalLengthReciprocal;
				final float surfaceNormalNormalizedZ = surfaceNormalZ * surfaceNormalLengthReciprocal;
				
				final float noise = simplexFractionalBrownianMotionXYZ(8.0F, 0.5F, 0.0F, 1.0F, 16, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ);
				
//				Calculate the albedo color of the surface intersection point:
				float albedoColorR = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				float albedoColorG = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				float albedoColorB = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				
				final int colorRGB = doShaderPhongReflectionModel0(false, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalNormalizedX, surfaceNormalNormalizedY, surfaceNormalNormalizedZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//				final int colorRGB = doShaderPhongReflectionModel1(false, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalNormalizedX, surfaceNormalNormalizedY, surfaceNormalNormalizedZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
				
				pixelColorR = ((colorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
				pixelColorG = ((colorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
				pixelColorB = ((colorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
				
				t = maxT;
			}
			
			delT = delTMultiplier * t;
		}
		
		this.colorCurrentSamples_$local$[pixelIndex + 0] = pixelColorR;
		this.colorCurrentSamples_$local$[pixelIndex + 1] = pixelColorG;
		this.colorCurrentSamples_$local$[pixelIndex + 2] = pixelColorB;
	}
	
	private void doRayTracing() {
//		Retrieve the maximum depth allowed:
		final int depthMaximum = 5;
		
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Retrieve the pixel index:
		final int pixelIndex0 = getLocalId() * SIZE_COLOR_RGB;
		
//		Initialize the offset to the primitive to -1:
		int primitivesOffset = -1;
		
		do {
//			Perform an intersection test:
			doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
			primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
			
			if(depthCurrent == 0) {
				this.primitiveOffsetsForPrimaryRay[getGlobalId()] = primitivesOffset;
			}
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || primitivesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += this.colorTemporarySamples_$private$3[0];
				pixelColorG += this.colorTemporarySamples_$private$3[1];
				pixelColorB += this.colorTemporarySamples_$private$3[2];
				
//				Update the current pixel color:
				this.colorCurrentSamples_$local$[pixelIndex0 + 0] = pixelColorR;
				this.colorCurrentSamples_$local$[pixelIndex0 + 1] = pixelColorG;
				this.colorCurrentSamples_$local$[pixelIndex0 + 2] = pixelColorB;
				
				return;
			}
			
//			Increment the current depth:
			depthCurrent++;
			
//			Retrieve the offset to the surfaces array for the given primitive:
			final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
//			Retrieve the material type of the intersected primitive:
			final int material = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			
			final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
			
//			Calculate the albedo texture color for the intersected primitive:
			final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
			
//			Get the color of the shape from the albedo texture color that was looked up:
			float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) / 255.0F;
			float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) / 255.0F;
			float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) / 255.0F;
			
//			Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point from the intersections array:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal from the intersections array:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final int colorRGB = doShaderPhongReflectionModel0(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalShadingX, surfaceNormalShadingY, surfaceNormalShadingZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//			final int colorRGB = doShaderPhongReflectionModel1(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalShadingX, surfaceNormalShadingY, surfaceNormalShadingZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
			
			pixelColorR += ((colorRGB >> 16) & 0xFF) / 255.0F;
			pixelColorG += ((colorRGB >> 8) & 0xFF) / 255.0F;
			pixelColorB += (colorRGB & 0xFF) / 255.0F;
			
			if(material == ReflectionMaterial.TYPE) {
//				Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
				final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
				final float dotProductMultipliedByTwo = dotProduct * 2.0F;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalShadingX * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormalShadingY * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormalShadingZ * 0.000001F;
				
//				Update the ray direction for the next iteration:
				directionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				directionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				directionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
			} else {
				depthCurrent = depthMaximum;
			}
		} while(depthCurrent < depthMaximum);
		
//		Update the current pixel color:
		this.colorCurrentSamples_$local$[pixelIndex0 + 0] = pixelColorR;
		this.colorCurrentSamples_$local$[pixelIndex0 + 1] = pixelColorG;
		this.colorCurrentSamples_$local$[pixelIndex0 + 2] = pixelColorB;
	}
	
	private void doWireframeRendering() {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Retrieve the pixel index:
		final int pixelIndex = getLocalId() * SIZE_COLOR_RGB;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
		final float lineWidth = PI * 0.5F / 4096.0F;
		final float lineWidthCos = cos(lineWidth);
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance != INFINITY && primitivesOffset != -1) {
//			Retrieve the type and offset of the shape:
			final int shapeType = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE]);
			final int shapeOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET]);
			
			if(shapeType == Triangle.TYPE) {
//				Retrieve the offsets of the surface intersection point and the points A, B and C in the intersections array:
				final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
				final int offsetA = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
				final int offsetB = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
				final int offsetC = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
				
//				Retrieve the camera orthonormal basis:
				final float cameraOrthoNormalBasisUX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X];
				final float cameraOrthoNormalBasisUY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y];
				final float cameraOrthoNormalBasisUZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z];
				final float cameraOrthoNormalBasisVX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X];
				final float cameraOrthoNormalBasisVY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y];
				final float cameraOrthoNormalBasisVZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z];
				final float cameraOrthoNormalBasisWX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X];
				final float cameraOrthoNormalBasisWY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y];
				final float cameraOrthoNormalBasisWZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z];
				
//				Retrieve the surface intersection point from the intersections array:
				final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
				final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
				final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
				final float surfaceIntersectionPointCameraX = surfaceIntersectionPointX * cameraOrthoNormalBasisUX + surfaceIntersectionPointY * cameraOrthoNormalBasisUY + surfaceIntersectionPointZ * cameraOrthoNormalBasisUZ;
				final float surfaceIntersectionPointCameraY = surfaceIntersectionPointX * cameraOrthoNormalBasisVX + surfaceIntersectionPointY * cameraOrthoNormalBasisVY + surfaceIntersectionPointZ * cameraOrthoNormalBasisVZ;
				final float surfaceIntersectionPointCameraZ = surfaceIntersectionPointX * cameraOrthoNormalBasisWX + surfaceIntersectionPointY * cameraOrthoNormalBasisWY + surfaceIntersectionPointZ * cameraOrthoNormalBasisWZ;
				final float surfaceIntersectionPointCameraN = rsqrt(surfaceIntersectionPointCameraX * surfaceIntersectionPointCameraX + surfaceIntersectionPointCameraY * surfaceIntersectionPointCameraY + surfaceIntersectionPointCameraZ * surfaceIntersectionPointCameraZ);
				
//				Retrieve point A of the triangle:
				final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
				final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
				final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
				final float aCameraX = aX * cameraOrthoNormalBasisUX + aY * cameraOrthoNormalBasisUY + aZ * cameraOrthoNormalBasisUZ;
				final float aCameraY = aX * cameraOrthoNormalBasisVX + aY * cameraOrthoNormalBasisVY + aZ * cameraOrthoNormalBasisVZ;
				final float aCameraZ = aX * cameraOrthoNormalBasisWX + aY * cameraOrthoNormalBasisWY + aZ * cameraOrthoNormalBasisWZ;
				
//				Retrieve point B of the triangle:
				final float bX = this.scenePoint3Fs_$constant$[offsetB + 0];
				final float bY = this.scenePoint3Fs_$constant$[offsetB + 1];
				final float bZ = this.scenePoint3Fs_$constant$[offsetB + 2];
				final float bCameraX = bX * cameraOrthoNormalBasisUX + bY * cameraOrthoNormalBasisUY + bZ * cameraOrthoNormalBasisUZ;
				final float bCameraY = bX * cameraOrthoNormalBasisVX + bY * cameraOrthoNormalBasisVY + bZ * cameraOrthoNormalBasisVZ;
				final float bCameraZ = bX * cameraOrthoNormalBasisWX + bY * cameraOrthoNormalBasisWY + bZ * cameraOrthoNormalBasisWZ;
				
//				Retrieve point C of the triangle:
				final float cX = this.scenePoint3Fs_$constant$[offsetC + 0];
				final float cY = this.scenePoint3Fs_$constant$[offsetC + 1];
				final float cZ = this.scenePoint3Fs_$constant$[offsetC + 2];
				final float cCameraX = cX * cameraOrthoNormalBasisUX + cY * cameraOrthoNormalBasisUY + cZ * cameraOrthoNormalBasisUZ;
				final float cCameraY = cX * cameraOrthoNormalBasisVX + cY * cameraOrthoNormalBasisVY + cZ * cameraOrthoNormalBasisVZ;
				final float cCameraZ = cX * cameraOrthoNormalBasisWX + cY * cameraOrthoNormalBasisWY + cZ * cameraOrthoNormalBasisWZ;
				
				final float distanceSquaredAC = ((aCameraX - cCameraX) * (aCameraX - cCameraX)) + ((aCameraY - cCameraY) * (aCameraY - cCameraY)) + ((aCameraZ - cCameraZ) * (aCameraZ - cCameraZ));
				final float distanceSquaredBA = ((bCameraX - aCameraX) * (bCameraX - aCameraX)) + ((bCameraY - aCameraY) * (bCameraY - aCameraY)) + ((bCameraZ - aCameraZ) * (bCameraZ - aCameraZ));
				final float distanceSquaredCB = ((cCameraX - bCameraX) * (cCameraX - bCameraX)) + ((cCameraY - bCameraY) * (cCameraY - bCameraY)) + ((cCameraZ - bCameraZ) * (cCameraZ - bCameraZ));
				
				final float tAC0 = ((surfaceIntersectionPointCameraX - aCameraX) * (cCameraX - aCameraX)) + ((surfaceIntersectionPointCameraY - aCameraY) * (cCameraY - aCameraY)) + ((surfaceIntersectionPointCameraZ - aCameraZ) * (cCameraZ - aCameraZ));
				final float tAC1 = tAC0 / distanceSquaredAC;
				final float tBA0 = ((surfaceIntersectionPointCameraX - bCameraX) * (aCameraX - bCameraX)) + ((surfaceIntersectionPointCameraY - bCameraY) * (aCameraY - bCameraY)) + ((surfaceIntersectionPointCameraZ - bCameraZ) * (aCameraZ - bCameraZ));
				final float tBA1 = tBA0 / distanceSquaredBA;
				final float tCB0 = ((surfaceIntersectionPointCameraX - cCameraX) * (bCameraX - cCameraX)) + ((surfaceIntersectionPointCameraY - cCameraY) * (bCameraY - cCameraY)) + ((surfaceIntersectionPointCameraZ - cCameraZ) * (bCameraZ - cCameraZ));
				final float tCB1 = tCB0 / distanceSquaredCB;
				
				final float projectionACX = (1.0F - tAC1) * aCameraX + tAC1 * cCameraX;
				final float projectionACY = (1.0F - tAC1) * aCameraY + tAC1 * cCameraY;
				final float projectionACZ = (1.0F - tAC1) * aCameraZ + tAC1 * cCameraZ;
				final float projectionACN = rsqrt(projectionACX * projectionACX + projectionACY * projectionACY + projectionACZ * projectionACZ);
				final float projectionBAX = (1.0F - tBA1) * bCameraX + tBA1 * aCameraX;
				final float projectionBAY = (1.0F - tBA1) * bCameraY + tBA1 * aCameraY;
				final float projectionBAZ = (1.0F - tBA1) * bCameraZ + tBA1 * aCameraZ;
				final float projectionBAN = rsqrt(projectionBAX * projectionBAX + projectionBAY * projectionBAY + projectionBAZ * projectionBAZ);
				final float projectionCBX = (1.0F - tCB1) * cCameraX + tCB1 * bCameraX;
				final float projectionCBY = (1.0F - tCB1) * cCameraY + tCB1 * bCameraY;
				final float projectionCBZ = (1.0F - tCB1) * cCameraZ + tCB1 * bCameraZ;
				final float projectionCBN = rsqrt(projectionCBX * projectionCBX + projectionCBY * projectionCBY + projectionCBZ * projectionCBZ);
				
				final float dotProductAC = projectionACX * surfaceIntersectionPointCameraX + projectionACY * surfaceIntersectionPointCameraY + projectionACZ * surfaceIntersectionPointCameraZ;
				final float dotProductBA = projectionBAX * surfaceIntersectionPointCameraX + projectionBAY * surfaceIntersectionPointCameraY + projectionBAZ * surfaceIntersectionPointCameraZ;
				final float dotProductCB = projectionCBX * surfaceIntersectionPointCameraX + projectionCBY * surfaceIntersectionPointCameraY + projectionCBZ * surfaceIntersectionPointCameraZ;
				
				if(dotProductAC * projectionACN * surfaceIntersectionPointCameraN >= lineWidthCos || dotProductBA * projectionBAN * surfaceIntersectionPointCameraN >= lineWidthCos || dotProductCB * projectionCBN * surfaceIntersectionPointCameraN >= lineWidthCos) {
//					Update the current pixel color:
					this.colorCurrentSamples_$local$[pixelIndex + 0] = 0.0F;
					this.colorCurrentSamples_$local$[pixelIndex + 1] = 0.0F;
					this.colorCurrentSamples_$local$[pixelIndex + 2] = 0.0F;
				}
			}
		}
	}
}